package com.iexceed.appzillon.services;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
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
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.httpclient.URIException;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.iexceed.appzillon.dao.HttpDetails;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;

/**
 * 
 * @author arthanarisamy
 *
 */
public class HttpService implements IServicesBean {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS, HttpService.class.toString());

	protected HttpDetails httpDtls;

	private Object  responsePayLoad;

	private Object requestPayload;
	
	private Map<String, String> pathParameters = new HashMap<String,String>();

	public Object buildRequest(com.iexceed.appzillon.message.Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Building request for external HTTP service....");
		String lRequestPayLoad = null;
		if (ServerConstants.HTTP_CALL_TYPE_GET.equalsIgnoreCase(httpDtls.getCallType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "HTTP service call type is GET....");
			final String queryString = getQueryString(pRequestPayLoad.toString());
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Request built for HTTP Service GET request -:"
					+ queryString);

			try {
				lRequestPayLoad = org.apache.commons.httpclient.util.URIUtil.encodeQuery(queryString);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ "After encoded the request for HTTP Service GET method -:" + lRequestPayLoad);
			} catch (URIException e) {
				LOG.error("URIException", e);
			}
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "HTTP service call type is POST....");
			lRequestPayLoad = pRequestPayLoad.toString();

			//Removing Header Ref elements from actual payLoad
			lRequestPayLoad = removeHeaderRef(lRequestPayLoad);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After removing header param from payload :"+lRequestPayLoad);
			
			//Removing Query Ref elements from actual payLoad
			lRequestPayLoad = removeQueryRef(lRequestPayLoad);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After removing query param from payload :"+lRequestPayLoad);
			lRequestPayLoad = removePathRef(lRequestPayLoad);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After removing path param from payload :"+lRequestPayLoad);
			if (ServerConstants.XML.equals(httpDtls.getRequestType())) {
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ "Request type excepted is XML, hence converting JSON to XML....");
				/** checking for queryString in POST Method */
				Map<String, Object> queryparams = httpDtls.getQueryParameters();
				if(queryparams!=null && !queryparams.isEmpty()){
					if(queryparams.containsKey(ServerConstants.QUERY_STRING) && queryparams.get(ServerConstants.QUERY_STRING) != null){
						LOG.debug("Checking for Query String in POST Method.");
						String queryStringNodeElement = queryparams.get(ServerConstants.QUERY_STRING)+"";
						String valueFromPayLoad = getValueFromNodeStruc(queryStringNodeElement, new JSONObject(lRequestPayLoad));
						LOG.debug("New payLoad in POST Method : " + valueFromPayLoad);
						lRequestPayLoad = valueFromPayLoad;
					}
				}
				/** checking for queryString in POST Method END here */
				lRequestPayLoad = ExternalServicesRouter.getJSONtoXML(lRequestPayLoad);
				LOG.debug(
						ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Converting JSON to XML -:" + lRequestPayLoad);
				lRequestPayLoad = appendAttributes(lRequestPayLoad);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After appending attributes to XML -:"
						+ lRequestPayLoad);
			}
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Returning request for HTTP Service -:" + lRequestPayLoad);

		return lRequestPayLoad;
	}

	/**
	 * @param lRequestPayLoad
	 * @return
	 * @throws FactoryConfigurationError
	 */
	private String appendAttributes(String lRequestPayLoad) throws FactoryConfigurationError {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			/*
			 * Converting requestPayLoad string to document object for XML
			 * parsing
			 */
			// Document doc = docBuilder.parse(lRequestPayLoad); 1-10-2015 :
			// updated to handle java.net.MalformedURLException and
			// org.w3c.dom.DOMException: INVALID_CHARACTER_ERR
			Document doc = docBuilder.parse(new InputSource(new StringReader(lRequestPayLoad)));

			/* Obtaining childnodes of the root node */
			// Node rootNode = (Node) doc.getChildNodes();
			Node rootNode = (Node) doc.getFirstChild(); // updated on 19-10-2015
														// to handle header
														// attribute

			/* Obtaining rootnode attributes to the nodeMap */
			NamedNodeMap nodeMap = rootNode.getAttributes();
			/*
			 * Adding headerNode attribute and value to attributes
			 */

			if (Utils.isNotNullOrEmpty(httpDtls.getHeaderNode()) && Utils.isNotNullOrEmpty(httpDtls.getHeaderAttributes())) {
				Attr attributes = doc.createAttribute(httpDtls.getHeaderNode());
				attributes.setNodeValue(httpDtls.getHeaderAttributes());
				/* Adding attributes to the nodeMap */
				nodeMap.setNamedItem(attributes);
			}

			/*
			 * Converting document to DOMSource for XML conversion
			 */
			DOMSource domSource = new DOMSource(doc);
			/* Converting DOMSource to attributesAddedXML String */
			String attributesAddedXML = getStringFromDoc(domSource);

			lRequestPayLoad = attributesAddedXML;

		} catch (Exception e) {
			LOG.debug("Exception while appending attributes to root node" + Utils.getStackTrace(e));
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Returning XML after adding attributes -:"
				+ lRequestPayLoad);
		return lRequestPayLoad;
	}

	public String getQueryString(String requestPayLoad) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Building query Parameters, from request -:"
				+ requestPayLoad + " and the parameters are -:" + httpDtls.getQueryParameters());
		if (httpDtls.getQueryParameters() == null || httpDtls.getQueryParameters().size() <= 0) {
			return "";
		} else {
			Map<String, Object> dbqueryParam = new HashMap<String, Object>();
			try {
				JSONObject screenObj = new JSONObject(requestPayLoad);
				JSONObject screenObj2 = screenObj;
				Map<String, Object> queryparams = httpDtls.getQueryParameters();
				if(queryparams!=null && !queryparams.isEmpty()){
				// changes for QueryString Start
				if(queryparams.containsKey(ServerConstants.QUERY_STRING) && queryparams.get(ServerConstants.QUERY_STRING) != null){
					String queryStringNodeElement = queryparams.get(ServerConstants.QUERY_STRING)+"";
					String valueFromPayLoad = getValueFromNodeStruc(queryStringNodeElement, new JSONObject(requestPayLoad));
					return valueFromPayLoad;
				}
				// changes for QueryString End here

				for (Map.Entry<String, Object> entry : queryparams.entrySet()) {
					String parameterKey = entry.getKey();
					if(!parameterKey.equals(ServerConstants.QUERY_STRING)){
					String queryParamFields = (String) entry.getValue();
					String[] nodestructure = queryParamFields.split("[\\.]");
					screenObj2 = screenObj;
					int j = 0;
					for (; j < nodestructure.length - 1; j++) {
						LOG.debug("[FrameWorks] **** nodestructure[j]:" + nodestructure[j]);
						try {
							screenObj2 = screenObj2.getJSONObject(nodestructure[j]);
						} catch (JSONException e) {
							LOG.error("WARNING !!! Node Structure " + e);
						}
						LOG.debug("[FrameWorks] $$$$ screenObj2:" + screenObj2);
					}
					LOG.debug("[FrameWorks] **** screenObj2:" + screenObj2.toString());
					try {
						dbqueryParam.put(parameterKey, screenObj2.get(nodestructure[j]));
					} catch (JSONException e) {
						if(e.getMessage().contains("not found")){
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Json Object not found, "+e.getMessage());
							dbqueryParam.put(parameterKey,queryParamFields);
						}
						else{
							LOG.error("WARNING !!! parameterKey " + e);
						}
					}
				 }
				}
				LOG.debug("[FrameWorks] the hashmap with query parameter values" + dbqueryParam.toString());
			}
			} catch (JSONException jsonex) {
				LOG.error("JSONException", jsonex);
			}
			return converttoQueryString(dbqueryParam);
		}

	}

	public String converttoQueryString(Map<String, Object> dbqueryParamValues) {
		String queryString = "";
		if(dbqueryParamValues!=null && !dbqueryParamValues.isEmpty()){
		Iterator<Entry<String, Object>> it = dbqueryParamValues.entrySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			if (count > 0) {
				queryString = queryString + ServerConstants.AMD;
			}
			Map.Entry pairs = it.next();
			queryString = queryString + pairs.getKey().toString() + "=" + pairs.getValue();
			count++;
		}
		}
		return queryString;
	}

	public Object callService(com.iexceed.appzillon.message.Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		Exchange exchange = null;
		String appId = pMessage.getHeader().getAppId();
		String interfaceId = pMessage.getHeader().getInterfaceId();
		ProducerTemplate producer = FrameworksStartup.getInstance().getProducerTemplate();
		getHttpDetails(interfaceId, appId, pContext);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Got the producerTemplate" + producer);
		pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad,
				httpDtls.getAutoGenElementMap(), httpDtls.getTranslationElementMap());
		pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad + ""));
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Appending Request Json With MaskedId : "
				+ pMessage.getRequestObject().getRequestJson());
		String endpointURI = createEndpointURI(pMessage, pContext);

		/**
		 * Below changes are made by Samy on 04/03/2016 To enhance support for
		 * Path Parameters to consume RESTFull services using HTTPInterface.
		 */
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "endpointURI before appending Path Parameters -:"
				+ endpointURI);
		endpointURI = appendPathParameters(endpointURI, pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "endpointURI after appending Path Parameters -:"
				+ endpointURI);
		/**
		 * Changes End
		 */
		applySSL(pMessage);
		final String contentType = httpDtls.getPayLoadType();
		final Map<String, Object> lHeaderAttributesMap = buildHeaderParamMap(new JSONObject(pRequestPayLoad + ""));
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " After Building HeaderMap -:" + lHeaderAttributesMap);
		if (ServerConstants.HTTP_CALL_TYPE_GET.equalsIgnoreCase(httpDtls.getCallType())) {
			String queryString = (String) buildRequest(pMessage, pRequestPayLoad, pContext);
			final String encqueryString = queryString;
			if (Utils.isNotNullOrEmpty(queryString) && queryString.trim().length() > 0) {
				endpointURI = endpointURI + "&" + encqueryString;
			}
			Utils.setExtTime(pMessage,"S");
			exchange = producer.request(endpointURI, new Processor() {
				public void process(Exchange exchange) throws Exception {
					/*
					 * exchange.getIn().setHeader(Exchange.HTTP_QUERY,
					 * encqueryString);
					 */
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Query Parameter is set -:" + encqueryString);
					CaseInsensitiveMap caseInsensitiveMap = new CaseInsensitiveMap(lHeaderAttributesMap);
					
					if(!caseInsensitiveMap.containsKey(Exchange.HTTP_METHOD.toLowerCase())){
						caseInsensitiveMap.put(Exchange.HTTP_METHOD, httpDtls.getCallType());
					}
					if(!caseInsensitiveMap.containsKey(Exchange.CONTENT_TYPE.toLowerCase())){
						caseInsensitiveMap.put(Exchange.CONTENT_TYPE, contentType);
					}
					if(!caseInsensitiveMap.containsKey("Accept".toLowerCase())){
						caseInsensitiveMap.put("Accept", contentType);
					}
					
					
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Check Setting final HeaderMap 1 -: " + exchange.getIn().getHeaders());
					if(caseInsensitiveMap != null && !caseInsensitiveMap.isEmpty()){
						LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Setting final HeaderMap -: " + caseInsensitiveMap);
						exchange.getIn().setHeaders(caseInsensitiveMap);
					}
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Header Attributes Map is set -:"
							+ caseInsensitiveMap);
				}
			});
			Utils.setExtTime(pMessage,"E");
			requestPayload=encqueryString;
		} else if (ServerConstants.HTTP_CALL_TYPE_POST.equalsIgnoreCase(httpDtls.getCallType())
				|| ServerConstants.HTTP_CALL_TYPE_DELETE.equalsIgnoreCase(httpDtls.getCallType())
				|| ServerConstants.HTTP_CALL_TYPE_PUT.equalsIgnoreCase(httpDtls.getCallType())) {

			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IN POST/PUT/DELETE Type");
			String lPayload = (String) buildRequest(pMessage, pRequestPayLoad, pContext);
			/*
			 * Checking if the request payload built is empty JSON.
			 * if empty, no payload is set to the HTTP body.
			 * Changes made by Samy on 06/07/2017 -- Start
			 */
			if(lPayload.equalsIgnoreCase("{}")){
				lPayload = "";
			}
			final String payload = lPayload;
			/*        END			 */
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Payload:" + payload + ", ContentType:" + contentType);
			final String pQueryString = pRequestPayLoad.toString();
			String lQueryParameters = null;
			try {
				String queryString = getQueryString(pQueryString);
				LOG.debug(
						ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After building  Query Parameters -:" + queryString);
				lQueryParameters = org.apache.commons.httpclient.util.URIUtil.encodeQuery(queryString);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ "After encoded the request for HTTP Service POST/PUT/DELETE method -:" + lQueryParameters
						+ " parameters length -:" + lQueryParameters.length());

				if (Utils.isNotNullOrEmpty(lQueryParameters) && lQueryParameters.trim().length() > 0) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " setting query paramters....");
					endpointURI = endpointURI + "&" + lQueryParameters;
				}

			} catch (URIException e) {
				LOG.error("URIException", e);

			}
			Utils.setExtTime(pMessage,"S");
			exchange = producer.request(endpointURI, new Processor() {
				public void process(Exchange exchange) throws Exception {
					CaseInsensitiveMap caseInsensitiveMap = new CaseInsensitiveMap(lHeaderAttributesMap);
					
					if(!caseInsensitiveMap.containsKey(Exchange.HTTP_METHOD.toLowerCase())){
						caseInsensitiveMap.put(Exchange.HTTP_METHOD, httpDtls.getCallType());
					}
					if(!caseInsensitiveMap.containsKey(Exchange.CONTENT_TYPE.toLowerCase())){
						caseInsensitiveMap.put(Exchange.CONTENT_TYPE, contentType);
					}
					if(!caseInsensitiveMap.containsKey("Accept".toLowerCase())){
						caseInsensitiveMap.put("Accept", contentType);
					}
					
					
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Check Setting final HeaderMap 1 -: " + exchange.getIn().getHeaders());
					if(caseInsensitiveMap != null && !caseInsensitiveMap.isEmpty()){
						LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Setting final HeaderMap -: " + caseInsensitiveMap);
						exchange.getIn().setHeaders(caseInsensitiveMap);
					}
					if (!ServerConstants.HTTP_CALL_TYPE_DELETE.equalsIgnoreCase(httpDtls.getCallType())) {
						exchange.getIn().setBody(payload);
					}
					
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Check Setting final HeaderMap 2 -: " + exchange.getIn().getHeaders());
				}
			});
			Utils.setExtTime(pMessage,"E");
			requestPayload=payload;
		} else {
			LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Only POST, GET, PUT and DELETE methods are allowed :" + httpDtls.getCallType());
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException .getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_026.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_026));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		}
		JSONObject lResponse = null;
		Object lOutput = null;
		if (exchange.getException() == null) {
			Message out = exchange.getOut();
			try {

				lOutput = processResponse(pMessage, out, pContext);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " processResponse lOutput -:" + lOutput.toString());
				lResponse = new JSONObject(lOutput.toString());

				ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.SUCCESS,responsePayLoad,requestPayload);

				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " processResponse lResponse -:" + lOutput);
			} catch (Exception ex) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Class -:" + ex.getClass());
				if (ex instanceof ClassCastException || ex instanceof JSONException) {
					try {
						lResponse = new JSONObject(lOutput.toString());
						LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response put in JSONObject : " + lResponse);
					} catch (JSONException csex) {
						LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
								+ "Exception while putting in JSONObject, since external response is not a JSONObject.");
						LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Now Putting response in JSONArray");
						try {
							lResponse = new JSONArray(lOutput.toString());
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSONArray Casting -:" + lResponse);
						} catch (JSONException jsonEx) {
							LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
									+ "Exception while putting in JSONArray, since response is not an array.");
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
									+ "Now Putting response string from external service in JSONObject.");
							lResponse = new JSONObject().put("response", lOutput.toString().trim());
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Putting in the response : "
									+ lResponse);
						}
					}
				}
			}
		} else {
			LOG.debug("Exception from external system hence will be throwing an exception....");
			LOG.error("callExternalService - Exchange-exception : ", exchange.getException());
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,exchange.getException().getMessage(),requestPayload);
			if(exchange.getException() instanceof HttpOperationFailedException){
            	ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
    					.getExternalServicesRouterExceptionInstance();
                HttpOperationFailedException exp = (HttpOperationFailedException) exchange.getException(HttpOperationFailedException.class);
    			exsrvcallexp.setCode(String.valueOf(exp.getStatusCode()));
                exsrvcallexp.setMessage(exp.getStatusText());
                if(exp.getResponseBody() != null){
                	pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.HTTP_ERROR_BODY, exp.getResponseBody()));
                }
    		    exsrvcallexp.setPriority("1");
    			throw exsrvcallexp;
            }else{
				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
						.getExternalServicesRouterExceptionInstance();

				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
				exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
				/*
				 * exsrvcallexp.setCode(String.valueOf(exp.getStatusCode()));
				 * exsrvcallexp.setMessage(exp.getStatusText());
				 */
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;

			}
		}
		return lResponse;
	}

	public void applySSL(com.iexceed.appzillon.message.Message pMessage) {
		if (ServerConstants.YES.equalsIgnoreCase(httpDtls.getSSLRequired())) {
			Object keyStorePath = null;
			Object keyStorePassword = null;
			Object trustStorePath = null;
			Object trustStorePassword = null;

			String trustStrPath = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
					ServerConstants.TRUST_STORE_PATH);
			String trustStrPwd = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
					ServerConstants.TRUST_STORE_PASSWORD);
			LOG.debug("Reading trustStrPath :" + trustStrPath);
			LOG.debug("Reading trustStrPwd :" + trustStrPwd);

			if (trustStrPath != null && trustStrPath.startsWith("${env:")) {
				// reading from environment variable
				LOG.debug("Reading truststore path from env variable :" + trustStrPath);
				trustStorePath = System.getenv(trustStrPath.replace("${env:", "").replace("}", ""));
			} else if (trustStrPath != null && trustStrPath.startsWith("${sys:")) {
				// reading from system property
				LOG.debug("Reading truststore path from system property :" + trustStrPath);
				trustStorePath = System.getProperty(trustStrPath.replace("${sys:", "").replace("}", ""));
			} else {
				trustStorePath = trustStrPath;
			}
			if (trustStrPwd != null && trustStrPwd.startsWith("${env:")) {
				// reading from environment variable
				LOG.debug("Reading truststore password from env variable :" + trustStrPwd);
				trustStorePassword = System.getenv(trustStrPwd.replace("${env:", "").replace("}", ""));
			} else if (trustStrPwd != null && trustStrPwd.startsWith("${sys:")) {
				// reading from system property
				LOG.debug("Reading truststore password from system property :" + trustStrPwd);
				trustStorePassword = System.getProperty(trustStrPwd.replace("${sys:", "").replace("}", ""));
			} else {
				trustStorePassword = trustStrPwd;
			}

			String keyStrPath = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
					ServerConstants.KEY_STORE_PATH);
			String keyStrPwd = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
					ServerConstants.KEY_STORE_PASSWORD);
			LOG.debug("Reading keyStrPath :" + keyStrPath);
			LOG.debug("Reading keyStrPwd :" + keyStrPwd);
			if (keyStrPath != null && keyStrPath.startsWith("${env:")) {
				// reading from environment variable
				LOG.debug("Reading keystore path from env variable :" + keyStrPath);
				keyStorePath = System.getenv(keyStrPath.replace("${env:", "").replace("}", ""));
			} else if (keyStrPath != null && keyStrPath.startsWith("${sys:")) {
				// reading from system property
				LOG.debug("Reading keystore path from system property :" + keyStrPath);
				keyStorePath = System.getProperty(keyStrPath.replace("${sys:", "").replace("}", ""));
			} else {
				keyStorePath = keyStrPath;
			}
			if (keyStrPwd != null && keyStrPwd.startsWith("${env:")) {
				// reading from environment variable
				LOG.debug("Reading keystore password from env variable :" + keyStrPwd);
				keyStorePassword = System.getenv(keyStrPwd.replace("${env:", "").replace("}", ""));
			} else if (keyStrPwd != null && keyStrPwd.startsWith("${sys:")) {
				// reading from system property
				LOG.debug("Reading keystore password from system property :" + keyStrPwd);
				keyStorePassword = System.getProperty(keyStrPwd.replace("${sys:", "").replace("}", ""));
			} else {
				keyStorePassword = keyStrPwd;
			}
			LOG.debug("Actual Keystore Path  :" + keyStorePath);
			LOG.debug("Actual Keystore Password :" + keyStorePassword);
			if (keyStorePath != null) {
				System.setProperty(ServerConstants.SYSTEM_PROPERTY_SSL_KEY_STORE, keyStorePath.toString().trim());
				System.setProperty(ServerConstants.SYSTEM_PROPERTY_SSL_KEY_STORE_PASSWORD,
						keyStorePassword.toString().trim());

			}
			LOG.debug("Actual truststore Path  :" + trustStorePath);
			LOG.debug("Actual truststore Password :" + trustStorePassword);
			if (trustStorePath != null) {
				System.setProperty(ServerConstants.SYSTEM_PROPERTY_SSL_TRUST_STORE, trustStorePath.toString().trim());
				System.setProperty(ServerConstants.SYSTEM_PROPERTY_SSL_TRUST_STORE_PASSWORD,
						trustStorePassword.toString().trim());
			}
		}
	}

	public String createEndpointURI(com.iexceed.appzillon.message.Message pMessage, SpringCamelContext pContext) {
		String appId = pMessage.getHeader().getAppId();
		String interfaceId = pMessage.getHeader().getInterfaceId();
		String camelID = (appId + "__" + interfaceId).replaceAll("[\\.]", "__");
		LOG.info("[FrameWorks] Getting endpoint with id " + camelID);
		StringBuilder endpoint = new StringBuilder(pContext.getEndpoint(camelID).getEndpointUri());
		LOG.debug("[FrameWorks] URI enpoint id: " + camelID);
		LOG.debug("[FrameWorks] timeout value in secs" + httpDtls.getTimeOut());
		int timeOut = httpDtls.getTimeOut() * 1000;
		StringBuilder appendOption = new StringBuilder(ServerConstants.HTTP_APPEND_TIMEOUT + timeOut);
		LOG.debug("[FrameWorks] Option appended " + appendOption);
		endpoint.append(appendOption);
		LOG.debug("[FrameWorks] Endpoint used after adding params " + endpoint);
		return endpoint.toString();
	}

	public Object processResponse(com.iexceed.appzillon.message.Message pMessage, Object pResponse,
			SpringCamelContext pContext) {
		Message out = (Message) pResponse;
		String output = null;
		Integer statusHeader = (Integer) out.getHeader(Exchange.HTTP_RESPONSE_CODE);

		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "status header is " + statusHeader);
		if (statusHeader != null) {
			try {
				output = out.getMandatoryBody(String.class);;
				responsePayLoad=output;
				if (Utils.isNullOrEmpty(output)) {
					output = "{}";
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
							+ "out.getMandatoryBody(String.class) is set as empty json string");
				}

				if (ServerConstants.XML.equalsIgnoreCase(httpDtls.getResponseType())) {
					output = ExternalServicesRouter.getXMLToJSON(output);
				}
				JSONObject jsonOut = new JSONObject(output);
				output = buildJsonwithHeaderParams(out.getHeaders(), jsonOut).toString();
			} catch (InvalidPayloadException e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ "callExternalService - Error parsing external response:", e);
				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
						.getExternalServicesRouterExceptionInstance();
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
				exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;
			} catch (JSONException e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ "callExternalService - Error parsing external response:" + e);
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Super Class processResponse" + output);
		}
		return output;
	}

	public String getStringFromDoc(DOMSource dsource) {
		try {
			if (dsource.getNode().getPrefix() != null) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "prefix: " + dsource.getNode().getPrefix());
				dsource.getNode().setPrefix(null);
			}
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(dsource, result);
			writer.flush();
			return writer.toString();
		} catch (TransformerException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "TransformerException", ex);
			return null;
		}
	}

	public void getHttpDetails(String pInterfaceID, String pAppId, SpringCamelContext pContext) {
		LOG.debug("[FrameWorks] getTargetNameSpace ifID:" + pInterfaceID);

		httpDtls = (HttpDetails) ExternalServicesRouter.injectBeanFromSpringContext(pAppId + "_" + pInterfaceID,
				pContext);
		LOG.debug("[FrameWorks] QueryParameters" + httpDtls.getQueryParameters() + "call Type: "
				+ httpDtls.getCallType() + ", requestType :" + httpDtls.getRequestType());
		int timeOut = httpDtls.getTimeOut();
		/**
		 * Below changes are made by Vinod as part of At app level, service time
		 * out should be configurable. Appzillon 3.1 - 63 -- Start
		 */
		if (timeOut == 0) {

			LOG.warn(
					ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Timeout value not configured will use default timeOut");
			timeOut = Integer.parseInt(PropertyUtils.getPropValue(pAppId, ServerConstants.DEFAULT_TIMEOUT).trim());
		}
		/** Appzillon 3.1 - 63 -- END */
		httpDtls.setTimeOut(timeOut);
	}

	/*
	 * Below methods are added by Samy on 11/08/2105 to address passing
	 * Parameters in request header
	 */
	private Map<String, Object> buildHeaderParamMap(JSONObject prequestPayLoad) {
		Map<String, Object> lheaderParamMap = new HashMap<String, Object>();
		if (httpDtls.getRequestHeaderParams() != null && httpDtls.getRequestHeaderParams().size() > 0) {
			Map<String, String> lRequestParamMap = httpDtls.getRequestHeaderParams();
			Iterator<Entry<String, String>> lattributeNodeStrucIterator = lRequestParamMap.entrySet().iterator();
			while (lattributeNodeStrucIterator.hasNext()) {
				Entry<String, String> nodeStruc = lattributeNodeStrucIterator.next();
				String headerValue = getValueFromNodeStruc(nodeStruc.getValue().toString(), prequestPayLoad);
				lheaderParamMap.put(nodeStruc.getKey(), headerValue);
				// pMessage.getRequestObject().getRequestJson().remove(Utils.split(nodeStruc.getValue().toString(),".")[0]);
			}
		}
		return lheaderParamMap;
	}

	private String getValueFromNodeStruc(String pNodeStruc, JSONObject prequestPayLoad) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "getValueFromNodeStruc, Building query Parameters, from request -:" + prequestPayLoad
				+ " and the parameters are -:" + httpDtls.getQueryParameters());
		String lvalue = "";
		try {
			JSONObject lTempJSON = prequestPayLoad;
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "**** pNodeStruc:" + pNodeStruc);
			String[] nodestructure = pNodeStruc.split("[\\.]");
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "**** nodestructure.length:" + nodestructure.length);
			int j = 0;
			for (; j < nodestructure.length - 1; j++) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "**** nodestructure[j]:" + nodestructure[j]);
				lTempJSON = lTempJSON.getJSONObject(nodestructure[j]);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " $$$$ lTempJSON:" + lTempJSON);
			}
			/**
			 * Below condition put for taking JSONObject if JSONObject coming as
			 * element value, in the case of POST Method XML comes as
			 * JSONObject, and we build XML from JSONObject, added by ripu
			 * 28-03-2016
			 */
			try {
				lvalue = lTempJSON.get(nodestructure[j]) + "";
			} catch (JSONException jsonex) {

				if (jsonex.getMessage().contains("not found")) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " JSON object Not found, "
							+ jsonex.getMessage());
					lvalue = pNodeStruc;
				} else {
					LOG.error("Not String Value, So Its Fetching JSON Object ", jsonex);
					lvalue = lTempJSON.getJSONObject(nodestructure[j]) + "";
				}
				LOG.debug("lvalue : " + lvalue);
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " **** lTempJSON:" + lTempJSON.toString());
		} catch (JSONException jsonex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSONException", jsonex);
		}
		return lvalue;
	}

	private JSONObject buildJsonwithHeaderParams(Map<String, Object> lheaderParamMap, JSONObject pHeaderParamJson) {
		Map<String, String> lResponseParamMap = httpDtls.getResponseHeaderParams();
		if (httpDtls.getResponseHeaderParams() != null && httpDtls.getResponseHeaderParams().size() > 0) {
			Iterator<Entry<String, String>> lattributeNodeStrucIterator = lResponseParamMap.entrySet().iterator();
			while (lattributeNodeStrucIterator.hasNext()) {
				Entry<String, String> headerEntry = lattributeNodeStrucIterator.next();
				String key = headerEntry.getKey();
				pHeaderParamJson = buildJsonwithHeaderParams(pHeaderParamJson, headerEntry.getValue(), key,
						lheaderParamMap.get(key).toString());
			}
		}
		return pHeaderParamJson;
	}

	private static JSONObject buildJsonwithHeaderParams(JSONObject previousJSON, String nodeStruc, String key,
			String value) {
		String[] larry = Utils.split(nodeStruc, ".");
		JSONObject ljsonobject = new JSONObject();
		JSONObject lchildobj = null;
		LOG.debug("newJSON Loop starts larry.length -:" + larry.length);
		for (int i = 0; i < larry.length; i++) {
			LOG.debug("newJSON Loop starts i -: " + i + "newJSON Loop starts ljsonobject -:" + ljsonobject
					+ "checking json has element - " + i + " :" + larry[i] + "checking json has element -:"
					+ previousJSON.has(larry[i]));
			if (i == 0) {
				LOG.debug("previousJSON.has(larry[i]) -:" + previousJSON.has(larry[i]));
				if (!previousJSON.has(larry[i])) {
					if (previousJSON.toString() != "{}") {
						ljsonobject = previousJSON;
					}
					if (larry.length == 1) {

						JSONObject tmp = new JSONObject();
						tmp.put(key, value);
						LOG.debug("tmp.get(key)-:" + tmp.get(key));
						ljsonobject.accumulate(larry[i].toString(), tmp.get(key));
						LOG.debug("ljsonobject : " + ljsonobject.toString());
					} else {
						LOG.debug("Inside else");
						JSONObject tmp = new JSONObject();
						tmp.put(key, value);
						LOG.debug("tmp.get(key)-:" + tmp.get(key));
						ljsonobject.accumulate(larry[i].toString(), new JSONObject());
						LOG.debug("ljsonobject : " + ljsonobject.toString());
					}

				} else {
					LOG.debug("ELSE VAlue of i-:" + i + " and node is present previousJSON - :" + previousJSON);
					if (i == larry.length) {
						JSONObject tempJson = previousJSON.getJSONObject(larry[i].toString());
						tempJson.put(key, value);
						ljsonobject.put(larry[i].toString(), tempJson);
					} else {
						ljsonobject = previousJSON;
						if (!ljsonobject.has(key)) {
							JSONObject tempJson = previousJSON.getJSONObject(larry[i].toString());
							LOG.debug("tag.getElement():" + key);
							tempJson.put(key, value);
							ljsonobject.put(larry[i].toString(), tempJson);
						}
					}

				}
			} else {
				if (lchildobj == null) {
					LOG.debug("Child Object is null ....");
					LOG.debug("Array length -" + larry.length + ", i-:" + i);
					LOG.debug("**** Child node I length -:" + i + "node name -:" + larry[i] + " - ljsonobject -:"
							+ ljsonobject);
					if (i == larry.length - 1) {
						LOG.debug("First Child node I length -:" + i + "node name -:" + larry[i] + " - ljsonobject -:"
								+ ljsonobject);
						lchildobj = getJSONObject(larry[i].toString(),
								(JSONObject) ljsonobject.get(larry[i - 1].toString()), larry[i].toString(), value);
						LOG.debug("After adding first child -:" + lchildobj);
					} else {
						lchildobj = (JSONObject) ljsonobject.get(larry[i - 1].toString());
					}

				} else {
					if (i == larry.length - 1) {
						LOG.debug("&&&&&&&&&&&&&&&&&&& I length -:" + i + "node name -:" + larry[i]);
						lchildobj = getJSONObject(larry[i].toString(),
								(JSONObject) lchildobj.get(larry[i - 1].toString()), larry[i].toString(), value);
					} else {
						LOG.debug("lchildobj-:" + lchildobj);
						lchildobj = getNewJSONObject(larry[i].toString(), (JSONObject) lchildobj.get(larry[i - 1]),
								larry[i].toString(), value);
					}

				}
			}
		}
		return ljsonobject;
	}

	private static JSONObject getNewJSONObject(String p, JSONObject pjsonobj, String element, String value) {
		JSONObject lobj = new JSONObject();
		try {
			LOG.debug(" Checkign -" + (pjsonobj.has(p)));
			LOG.debug("pjsonobj -:" + pjsonobj);
			if (pjsonobj.has(p)) {
				JSONObject tempJSON = pjsonobj.getJSONObject(p);
				LOG.debug("Temp JSON -:" + tempJSON);
				tempJSON.put(element, value);
				pjsonobj.put(p, tempJSON);
			} else {
				pjsonobj.accumulate(p, lobj);
			}

		} catch (JSONException jx) {
		}
		return pjsonobj;

	}

	private static JSONObject getJSONObject(String p, JSONObject pjsonobj, String element, String value) {
		try {
			LOG.debug("pjsonobj -:" + pjsonobj);
			LOG.debug("getJSONObject -: " + p + " has -" + pjsonobj.has(p));
			if (pjsonobj.has(p)) {
				JSONObject tempJSON = pjsonobj.getJSONObject(p);
				LOG.debug("Temp JSON -:" + tempJSON);
				tempJSON.put(element, value);
				pjsonobj.put(p, tempJSON);
			} else {
				pjsonobj.accumulate(p, value);
			}

		} catch (JSONException jx) {
		}
		return pjsonobj;
	}

	private String removeHeaderRef(String payLoad) {
		JSONObject requestPayLoad = new JSONObject(payLoad);
		if (httpDtls.getRequestHeaderParams() != null && httpDtls.getRequestHeaderParams().size() > 0) {
			Map<String, String> lHeaderParams = httpDtls.getRequestHeaderParams();
			int i = 0;
			for (Map.Entry<String, String> entry : lHeaderParams.entrySet()) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "key-" + entry.getKey() + ", value - "
						+ entry.getValue());
				String nodes = entry.getValue().toString();
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "nodes : " + nodes);
				String trimmedNodes = Utils.split(nodes, ".")[i];
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "trimmedNode : " + trimmedNodes);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "requestPayLoad-" + requestPayLoad);
				if (requestPayLoad.has(trimmedNodes)) {
					requestPayLoad.remove(Utils.split(nodes, ".")[i]).toString();
				}
			}
		}
		return requestPayLoad.toString();
	}

	private String removeQueryRef(String payLoad) {
		JSONObject requestPayLoad = new JSONObject(payLoad);
		if (httpDtls.getQueryParameters() != null && httpDtls.getQueryParameters().size() > 0) {
			Map<String, Object> lqueryParams = httpDtls.getQueryParameters();
			int i = 0;
			for (Map.Entry<String, Object> entry : lqueryParams.entrySet()) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "key-" + entry.getKey() + ", value - "
						+ entry.getValue());
				String nodes = entry.getValue().toString();
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "nodes : " + nodes);
				String[] nodesToRemove = Utils.split(nodes, ".");
				if (nodesToRemove.length != 0){
					String trimmedNodes = nodesToRemove[i];
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "trimmedNode : " + trimmedNodes);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "requestPayLoad-" + requestPayLoad);
				if (requestPayLoad.has(trimmedNodes)) {
					requestPayLoad.remove(nodesToRemove[i]).toString();
				}
			}
			}
		}
		return requestPayLoad.toString();
	}
	
	private String removePathRef(String payLoad) {
		JSONObject requestPayLoad = new JSONObject(payLoad);
		if (pathParameters != null && pathParameters.size() > 0) {
			int i = 0;
			for (Map.Entry<String, String> entry : pathParameters.entrySet()) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "key-" + entry.getKey() + ", value - "
						+ entry.getValue());
				String nodes = entry.getValue().toString();
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "nodes : " + nodes);
				String[] nodesToRemove = Utils.split(nodes, ".");
				if (nodesToRemove.length != 0){
					String trimmedNodes = nodesToRemove[i];
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "trimmedNode : " + trimmedNodes);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "requestPayLoad-" + requestPayLoad);
				if (requestPayLoad.has(trimmedNodes)) {
					requestPayLoad.remove(nodesToRemove[i]).toString();
				}
			}
			}
		}
		return requestPayLoad.toString();
	}

	/**
	 * Below changes are made by Samy on 04/03/2016 To enhance support for Path
	 * Parameters to consume RESTFull services using HTTPInterface.
	 * 
	 * @param pURL
	 * @param pMessage
	 * @return
	 */
	private String appendPathParameters(String pURL, com.iexceed.appzillon.message.Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " appendPathParameters Encoded URL -:" + pURL);
		String lURL = "";
		try {
			lURL = URLDecoder.decode(pURL, ServerConstants.CHARACTER_ENCODING_UTF_8);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " appendPathParameters Decoded URL -:" + lURL);
			while ((lURL.contains(ServerConstants.PATH_PARAMETERS_LEFT_FILLER)
					&& lURL.contains(ServerConstants.PATH_PARAMETERS_RIGHT_FILLER))) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters  lURL -:" + lURL);
				int lfirstIndex = lURL.indexOf(ServerConstants.PATH_PARAMETERS_LEFT_FILLER);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters  lfirstIndex -:"
						+ lfirstIndex);
				int rfirstIndex = lURL.indexOf(ServerConstants.PATH_PARAMETERS_RIGHT_FILLER);
				LOG.debug(
						ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters rfirstIndex -:" + rfirstIndex);
				String nodeStructure = lURL.substring(lfirstIndex + 1, rfirstIndex);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters nodeStructure -:"
						+ nodeStructure);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters RequestPayLoad -:"
						+ pMessage.getRequestObject().getRequestJson());
				String lValueFromNodeStruc = getValueFromNodeStruc(nodeStructure,
						pMessage.getRequestObject().getRequestJson());
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters lValueFromNodeStruc -:"
						+ lValueFromNodeStruc);
				lURL = lURL.replace(ServerConstants.PATH_PARAMETERS_LEFT_FILLER + nodeStructure
						+ ServerConstants.PATH_PARAMETERS_RIGHT_FILLER, lValueFromNodeStruc);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After appending Path Parameter -:" + lURL);
				pathParameters.put(ServerConstants.PATH_PARAMETERS_LEFT_FILLER + lValueFromNodeStruc
						+ ServerConstants.PATH_PARAMETERS_RIGHT_FILLER, nodeStructure);
			}
		} catch (UnsupportedEncodingException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "UnsupportedEncodingException -:", e);
			lURL = pURL;
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Final URL after replacing path parameters -:" + lURL);
		return lURL;
	}
}
