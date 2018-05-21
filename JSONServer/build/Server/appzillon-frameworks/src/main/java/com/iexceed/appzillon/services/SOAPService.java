package com.iexceed.appzillon.services;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.spring.ws.SpringWebserviceConstants;
import org.apache.camel.spring.SpringCamelContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.iexceed.appzillon.dao.SOAPDetails;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;

public class SOAPService implements IServicesBean {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					SOAPService.class.toString());
	protected SOAPDetails soapDtls = null;
	private String XMLNS_COLON = "xmlns:";
	String tempHeader = "";
	Object reqPayLoad ;

	@Override
	public Object callService(com.iexceed.appzillon.message.Message pMessage,
			Object pRequestPayLoad, SpringCamelContext pContext) {
		String output = "";
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "After getting the context" + pContext);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "Request Payload in callservice" + pRequestPayLoad.toString());
		getSOAPDetails(pMessage, pContext);
		// get soapXmlHeader from payload and remove soapXmlheader from payload
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+ "Soap Details :" + soapDtls.toString());
		String soapHeaderXmlNode = soapDtls.getHeaderXmlNode();
		
		pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad, soapDtls.getAutoGenElementMap(), soapDtls.getTranslationElementMap());
		pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad+""));
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Appending Request Json With MaskedId : " + pMessage.getRequestObject().getRequestJson());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "Soap Header Xml Node:" + soapHeaderXmlNode);
		if (Utils.isNotNullOrEmpty(soapHeaderXmlNode)) {//soapHeaderXmlNode.length() > 0
			prepareHeaderFromNodeStructure(soapHeaderXmlNode,pRequestPayLoad);
			pRequestPayLoad = reqPayLoad;
			} 

		final String payload = (String) buildRequest(pMessage,
				pRequestPayLoad.toString(), pContext);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "targetname space"
				+ soapDtls.getTargetNamespace());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "Input to callExternalService --- serviceName: "
				+ pMessage.getHeader().getInterfaceId() + " requestPayload : "
				+ payload);
		ProducerTemplate lProducerTemplate = ExternalServicesRouter
				.createProducerTemplate(pContext);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "got the producer"
				+ lProducerTemplate);
		//applySSlRequirement(pMessage);
		String endpoint = createEndpointURIbyParam(pMessage, pContext);
		final Map<String, Object> headermap = soapDtls.getHeaderAttributesMap();
		final String customHeader = tempHeader;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "Endpoint URL after appending options" + endpoint);
		Utils.setExtTime(pMessage,"S");
		Exchange exchangeRequest = lProducerTemplate.request(endpoint,
				new Processor() {
					public void process(Exchange exchange) throws Exception {

						exchange.getIn().setBody(payload);
						if (Utils.isNotNullOrEmpty(customHeader)) {
							exchange.getIn()
									.setHeader(
											SpringWebserviceConstants.SPRING_WS_SOAP_HEADER,
											customHeader);
						}
						if (headermap != null && headermap.size() > 0) {
							exchange.getIn().setHeaders(headermap);
						}
					}
				});
		Utils.setExtTime(pMessage,"E");
		if (exchangeRequest.getException() == null) {
			Message outMessage = exchangeRequest.getOut();
			Integer statusHeader = (Integer) outMessage
					.getHeader(Exchange.HTTP_RESPONSE_CODE);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ "HTTP Response code" + statusHeader);
			if (exchangeRequest.getException() != null) {
				LOG.error("[FrameworkServices]  The exception in exchange",
						exchangeRequest.getException());
			}

			try {
				output = outMessage.getMandatoryBody(String.class);
			} catch (InvalidPayloadException e1) {
				LOG.error(
						"[FrameworkServices]  callExternalService - Exchange - exception :",
						exchangeRequest.getException());
				ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,e1.getMessage(),payload);
				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
						.getExternalServicesRouterExceptionInstance();
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
				exsrvcallexp.setMessage(exchangeRequest.getException()
						.getMessage());
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;
			}

			LOG.info("[FrameworkServices]  response content recieved " + output);
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.SUCCESS,output,payload);

			output = (String) processResponse(pMessage, output, pContext);

			return new JSONObject(output);
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ "Exception from external system hence will be throwing an exception....");
			LOG.error(
					"[FrameworkServices]  callExternalService - Exchange-exception :",
					exchangeRequest.getException());
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
					.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp
					.setMessage(exsrvcallexp
							.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,exsrvcallexp.getMessage(),payload);
			throw exsrvcallexp;
		}

	}

	@Override
	public Object buildRequest(com.iexceed.appzillon.message.Message pMessage,
			Object pRequestPayLoad, SpringCamelContext pContext) {
		String lXML = null;
		LOG.debug("Inside SuperClass buildRequest with payload " + pRequestPayLoad);
		lXML = ExternalServicesRouter.getJSONtoXML((String) pRequestPayLoad);
		lXML = addNamespace(soapDtls, lXML);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "final Request Build in SuperClass -:" + lXML);
		return lXML;
	}

	@Override
	public Object processResponse(
			com.iexceed.appzillon.message.Message pMessage, Object pResponse,
			SpringCamelContext pContext) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "Processing response in superclass with payload " + pResponse);
		return ExternalServicesRouter.getXMLToJSON((String) pResponse);
	}

	public void getSOAPDetails(com.iexceed.appzillon.message.Message pMessage,
			SpringCamelContext pContext) {
		soapDtls = (SOAPDetails) ExternalServicesRouter
				.injectBeanFromSpringContext(pMessage.getHeader().getAppId()
						+ "_" + pMessage.getHeader().getInterfaceId(), pContext);
		int timeOut = soapDtls.getTimeOut();
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ " getSOAPDetails - timeOut -:" + timeOut);
		/**
		 * Below changes are made by Vinod as part of At app level, service time
		 * out should be configurable. Appzillon 3.1 - 63 -- Start
		 */
		if (timeOut == 0) {
			LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ "Timeout value not configured will use default timeOut");
			timeOut = Integer.parseInt(PropertyUtils.getPropValue(
					pMessage.getHeader().getAppId(),
					ServerConstants.DEFAULT_TIMEOUT).trim());
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ "After setting default timeout value -:" + timeOut);
		}
		/** Appzillon 3.1 - 63 -- END */
		soapDtls.setTimeOut(timeOut * 1000);
	}

	public void applySSlRequirement(
			com.iexceed.appzillon.message.Message pMessage) {
		if (ServerConstants.YES.equalsIgnoreCase(soapDtls.getSSLRequired())) {
			Object keyStorePath = null;
			Object keyStorePassword = null;
			Object trustStorePath =null;
			Object trustStorePassword = null;
			
			String trustStrPath = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,ServerConstants.TRUST_STORE_PATH);
			String trustStrPwd = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,ServerConstants.TRUST_STORE_PASSWORD);
			LOG.debug("Reading trustStrPath :"+trustStrPath);
			LOG.debug("Reading trustStrPwd :"+trustStrPwd);
			
			if(trustStrPath != null && trustStrPath.startsWith("${env:")){
				// reading from environment variable
				LOG.debug("Reading truststore path from env variable :"+trustStrPath);
				trustStorePath=System.getenv(trustStrPath.replace("${env:", "").replace("}", ""));
			} else if(trustStrPath != null && trustStrPath.startsWith("${sys:")){
				// reading from system property
				LOG.debug("Reading truststore path from system property :"+trustStrPath);
				trustStorePath=System.getProperty(trustStrPath.replace("${sys:", "").replace("}", ""));
			} else{
				trustStorePath=trustStrPath;
			}
			if(trustStrPwd != null && trustStrPwd.startsWith("${env:")){
				// reading from environment variable
				LOG.debug("Reading truststore password from env variable :"+trustStrPwd);
				trustStorePassword=System.getenv(trustStrPwd.replace("${env:", "").replace("}", ""));
			} else if(trustStrPwd != null && trustStrPwd.startsWith("${sys:")){
				// reading from system property
				LOG.debug("Reading truststore password from system property :"+trustStrPwd);
				trustStorePassword=System.getProperty(trustStrPwd.replace("${sys:", "").replace("}", ""));
			} else{
				trustStorePassword=trustStrPwd;
			}
			
			String keyStrPath = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.KEY_STORE_PATH);
			String keyStrPwd = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.KEY_STORE_PASSWORD);
			LOG.debug("Reading keyStrPath :"+keyStrPath);
			LOG.debug("Reading keyStrPwd :"+keyStrPwd);
			if(keyStrPath != null && keyStrPath.startsWith("${env:")){
				// reading from environment variable
				LOG.debug("Reading keystore path from env variable :"+keyStrPath);
				keyStorePath=System.getenv(keyStrPath.replace("${env:", "").replace("}", ""));
			} else if(keyStrPath != null && keyStrPath.startsWith("${sys:")){
				// reading from system property
				LOG.debug("Reading keystore path from system property :"+keyStrPath);
				keyStorePath=System.getProperty(keyStrPath.replace("${sys:", "").replace("}", ""));
			} else{
				keyStorePath=keyStrPath;
			}
			if(keyStrPwd != null && keyStrPwd.startsWith("${env:")){
				// reading from environment variable
				LOG.debug("Reading keystore password from env variable :"+keyStrPwd);
				keyStorePassword=System.getenv(keyStrPwd.replace("${env:", "").replace("}", ""));
			} else if(keyStrPwd != null && keyStrPwd.startsWith("${sys:")){
				// reading from system property
				LOG.debug("Reading keystore password from system property :"+keyStrPwd);
				keyStorePassword=System.getProperty(keyStrPwd.replace("${sys:", "").replace("}", ""));
			} else{
				keyStorePassword=keyStrPwd;
			}
			LOG.debug("Actual Keystore Path  :"+keyStorePath);
			LOG.debug("Actual Keystore Password :"+keyStorePassword);
			if (keyStorePath != null) {
				System.setProperty(
						ServerConstants.SYSTEM_PROPERTY_SSL_KEY_STORE,
						keyStorePath.toString().trim());
				System.setProperty(
						ServerConstants.SYSTEM_PROPERTY_SSL_KEY_STORE_PASSWORD,
						keyStorePassword.toString().trim());
				
			}
			LOG.debug("Actual truststore Path  :"+trustStorePath);
			LOG.debug("Actual truststore Password :"+trustStorePassword);
			if(trustStorePath != null){
				System.setProperty(
						ServerConstants.SYSTEM_PROPERTY_SSL_TRUST_STORE,
						trustStorePath.toString().trim());
				System.setProperty(
						ServerConstants.SYSTEM_PROPERTY_SSL_TRUST_STORE_PASSWORD,
						trustStorePassword.toString().trim());
			}
		}
	}

	public String createEndpointURIbyParam(
			com.iexceed.appzillon.message.Message pMessage,
			SpringCamelContext pContext) {
		String camelID = (pMessage.getHeader().getAppId() + "__" + pMessage
				.getHeader().getInterfaceId()).replaceAll("[\\.]", "__");
		LOG.info("[FrameworkServices]  Getting endpoint with id " + camelID);
		String endpointURI = pContext.getEndpoint(camelID).getEndpointUri();
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "uriMap id: "
				+ endpointURI);
		StringBuilder appendOption = new StringBuilder("?");
		if(ServerConstants.YES.equalsIgnoreCase(soapDtls.getSSLRequired())){
			appendOption.append(ServerConstants.SSL_CONTEXT_PARAMETERS);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "SSL Required and setting SSLContextParameters -: " + appendOption);
		}
		
		String soapAction = soapDtls.getAction();

		if (Utils.isNotNullOrEmpty(soapAction)) {
			if ("?".equals(appendOption.toString())) {
				appendOption.append(ServerConstants.SOAP_URL_SOAP_ACTION + soapAction);
			} else {
				appendOption.append(ServerConstants.AMD + ServerConstants.SOAP_URL_SOAP_ACTION + soapAction);
			}
			
		}

		if (ServerConstants.SOAP_VERSION_SOAP12.equalsIgnoreCase(soapDtls
				.getVersion())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ " SOAP Version is SOAP1.2 and appending timeout parameters....");
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ " SOAP1.2 timeout -:" + soapDtls.getTimeOut());
			if ("?".equals(appendOption.toString())) {
				appendOption
						.append(ServerConstants.SOAP_URL_MSG_FACTORY2_TIMEOUT
								+ soapDtls.getTimeOut());
			} else {
				appendOption.append(ServerConstants.AMD
						+ ServerConstants.SOAP_URL_MSG_FACTORY2_TIMEOUT
						+ soapDtls.getTimeOut());

			}

		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ " SOAP Version is SOAP1.1 and appending timeout parameters....");
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ " SOAP1.1 timeout -:" + soapDtls.getTimeOut());
			if ("?".equals(appendOption.toString())) {
				appendOption
						.append(ServerConstants.SOAP_URL_MSG_FACTORY1_TIMEOUT
								+ soapDtls.getTimeOut());
			} else {
				appendOption.append(ServerConstants.AMD
						+ ServerConstants.SOAP_URL_MSG_FACTORY1_TIMEOUT
						+ soapDtls.getTimeOut());
			}
		}
		LOG.debug("Final URI formed " + endpointURI + appendOption);
		return endpointURI + appendOption;
	}

	public String addNamespace(SOAPDetails soapDtls, String xmlData) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "XmlData:::"
				+ xmlData);
		String output = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(
					xmlData)));
			Element originalDocumentElement = document.getDocumentElement();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Root Node:::"
					+ originalDocumentElement.getNodeName());

			// uncommented below line to get child node
			Node nextNode = originalDocumentElement.getChildNodes().item(0);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Next Node:::"
					+ nextNode.getNodeName());
			/**
			 * Creating Element with Namespace is commented as the namespace
			 * will be added as attributes Changes made by Samy on 04/06/2015
			 */
			/*
			 * Element newDocumentElement = document.createElementNS(
			 * soapDtls.getNameSpaces(), originalDocumentElement.getNodeName());
			 */
			Element newDocumentElement = document
					.createElement(originalDocumentElement.getNodeName());
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "New Node:::"
					+ newDocumentElement.getNodeName());
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ "getIgnoreNamespaces-:" + soapDtls.getIgnoreNamespaces());
			if (ServerConstants.NO.equalsIgnoreCase(soapDtls
					.getIgnoreNamespaces())) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ "getNameSpaces-:" + soapDtls.getNameSpaces());
				// if(soapDtls.getNameSpaces().contains(XMLNS_COLON)){
				Map<String, String> lNameSpacesUriAttributesMap = getNameSpaceUriMap(soapDtls
						.getNameSpaces());
				for (Map.Entry<String, String> entry : lNameSpacesUriAttributesMap
						.entrySet()) {
					newDocumentElement.setAttribute(entry.getKey(),
							entry.getValue());
				}
				/*
				 * } else { LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS +
				 * "else case adding xmlns attribute and the url-:" +
				 * soapDtls.getNameSpaces()); String[] nameSpaceWithoutAlias =
				 * Utils.split(soapDtls.getNameSpaces(), "="); String
				 * nameSpaceUri = nameSpaceWithoutAlias[1];
				 * LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS +
				 * "Prefix nameSpaceWithoutAlias-:"+ nameSpaceWithoutAlias[0] +
				 * ", nameSpaceWithoutAlias TagertURL -:" + nameSpaceUri);
				 * if(nameSpaceUri.endsWith("\"")){
				 * if(nameSpaceUri.startsWith("\"")){ nameSpaceUri =
				 * nameSpaceUri.substring(1, nameSpaceUri.length()).trim(); }
				 * nameSpaceUri = nameSpaceUri.substring(0,
				 * nameSpaceUri.lastIndexOf("\"")).trim(); } else {
				 * if(nameSpaceUri.startsWith("\"")){ nameSpaceUri =
				 * nameSpaceUri.substring(1, nameSpaceUri.length()).trim(); }
				 * nameSpaceUri = nameSpaceUri.substring(0,
				 * nameSpaceUri.length()).trim(); }
				 * newDocumentElement.setAttribute(nameSpaceWithoutAlias[0],
				 * nameSpaceUri); }
				 */

			}

			NodeList list = originalDocumentElement.getChildNodes();
			while (list.getLength() != 0) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ "Element at Zeroth Position:::"
						+ list.item(0).getNodeName());
				newDocumentElement.appendChild(list.item(0));
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ "After Adding Child:::"
						+ newDocumentElement.getNodeName());
			}

			document.removeChild(originalDocumentElement);
			document.appendChild(newDocumentElement);

			Source src = new DOMSource(document);
			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();
			StringWriter writer = new StringWriter();
			Result dest = new StreamResult(writer);
			aTransformer.transform(src, dest);
			writer.flush();
			output = writer.toString();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "output:::"
					+ output);
		} catch (TransformerException ex) {
			LOG.error("TransformerException", ex);
		} catch (SAXException ex) {
			LOG.error("SAXException", ex);
		} catch (IOException ex) {
			LOG.error("IOException", ex);
		} catch (ParserConfigurationException ex) {
			LOG.error("ParserConfigurationException", ex);
		}
		return output;
	}

	private Map<String, String> getNameSpaceUriMap(String nameSpaces) {
		String[] nameSpacesSplit = Utils.split(nameSpaces, XMLNS_COLON);
		Map<String, String> attributes = new HashMap<String, String>();
		for (int i = 1; i < nameSpacesSplit.length; i++) {
			String[] nameSpace = Utils.split(nameSpacesSplit[i], "=");
			String nameSpaceUri = nameSpace[1].substring(1,
					nameSpace[1].lastIndexOf("\"")).trim();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Prefix -:"
					+ nameSpace[0] + ", TagertURL -:" + nameSpaceUri);
			if (nameSpaceUri.endsWith("\"")) {
				if (nameSpaceUri.startsWith("\"")) {
					nameSpaceUri = nameSpaceUri.substring(1,
							nameSpaceUri.length()).trim();
				}
				nameSpaceUri = nameSpaceUri.substring(0,
						nameSpaceUri.lastIndexOf("\"")).trim();
			} else {
				if (nameSpaceUri.startsWith("\"")) {
					nameSpaceUri = nameSpaceUri.substring(1,
							nameSpaceUri.length()).trim();
				}
				nameSpaceUri = nameSpaceUri.substring(0, nameSpaceUri.length())
						.trim();
			}

			attributes.put(XMLNS_COLON + nameSpace[0], nameSpaceUri);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "TargetNameSpacesURI Map -:" + attributes);
		return attributes;
	}

	private String getValueFromNodeStruc(String pNodeStruc,
			JSONObject prequestPayLoad) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "getValueFromNodeStruc, Building query Parameters, from request -:"
				+ prequestPayLoad);
		String lvalue = "";
		try {
			JSONObject lTempJSON = prequestPayLoad;
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ "**** pNodeStruc:" + pNodeStruc);
			//String[] nodestructure = pNodeStruc.split("[\\.]");
			String[] nodestructure = Utils.split(pNodeStruc, ".");
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ "**** nodestructure.length:" + nodestructure.length);
			int j = 0;
			for (; j < nodestructure.length - 1; j++) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ "**** nodestructure[j]:" + nodestructure[j]);
				lTempJSON = lTempJSON.getJSONObject(nodestructure[j]);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ " $$$$ lTempJSON:" + lTempJSON);
			}
	
		
				lvalue = new JSONObject().put(nodestructure[j], lTempJSON.getJSONObject(nodestructure[j])).toString();
				LOG.debug("lvalue : " + lvalue);
		
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ " **** lTempJSON:" + lTempJSON.toString());
		} catch (JSONException jsonex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
					+ "JSONException", jsonex);
		}
		return lvalue;
		
	}
	private void prepareHeaderFromNodeStructure(String headerNodeStructure,Object payLoadReq){
		JSONObject jsonObject = new JSONObject(payLoadReq.toString());
		String customHeaderJson = getValueFromNodeStruc(headerNodeStructure,
				jsonObject);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "Soap Header Xml value in JSON" + customHeaderJson);
		// convert customHeader JSON to XML
		tempHeader = ExternalServicesRouter
				.getJSONtoXML((String) customHeaderJson);
		tempHeader = addNamespace(soapDtls, tempHeader);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "After converting Soap Header Xml JSON to XML"
				+ tempHeader);

		// remove soapXmlHeader from payload
		//jsonObject.remove(headerNodeStructure.split("[\\.]")[0]);
		jsonObject.remove(Utils.split(headerNodeStructure, ".")[0]);
		reqPayLoad = jsonObject;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "After Removing Soap Header Xml from Payload "
				+ reqPayLoad);
	}
}
