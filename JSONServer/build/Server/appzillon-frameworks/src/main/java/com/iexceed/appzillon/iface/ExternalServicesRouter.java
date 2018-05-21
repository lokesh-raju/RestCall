package com.iexceed.appzillon.iface;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;

import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.json.XML;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;

public abstract class ExternalServicesRouter implements IExternalServiceRouter {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					ExternalServicesRouter.class.toString());

	public static Object injectBeanFromSpringContext(String beanId,
			SpringCamelContext context) throws ExternalServicesRouterException {
		Object bean = null;
		try {

			bean = context.getApplicationContext().getBean(beanId);

		} catch (Exception ex) {
			LOG.error("Exception",ex);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_004.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_004));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;

		}
		return bean;
	}

	public void injectBeanfromCamelContext(String beanId)
			throws ExternalServicesRouterException {

	}

	public static SpringCamelContext getCamelContext()
			throws ExternalServicesRouterException {
		SpringCamelContext context = null;
		try {
			context = FrameworksStartup.getInstance().getCamelContext();
		} catch (Exception ex) {
			LOG.error("Exception",ex);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_002.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_002));
			exsrvcallexp.setPriority("1");

			throw exsrvcallexp;
		}
		return context;
	}

	public static  ProducerTemplate createProducerTemplate(SpringCamelContext pContext)
			throws ExternalServicesRouterException {
		ProducerTemplate producer = null;
		try {

			producer = FrameworksStartup.getInstance().getProducerTemplate();
			LOG.debug("**** createProducerTemplate - producer:" + producer);
		} catch (Exception ex) {
			LOG.error("Exception",ex);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_003.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_003));
			exsrvcallexp.setPriority("1");

			throw exsrvcallexp;
		}
		return producer;
	}

	public static Object getUnMarshalled(String pInputXMLStr,
			String pQualifiedClassName) {
		Object lRespObject = null;

		try {

			@SuppressWarnings("rawtypes")
			Class lc = Class.forName(pQualifiedClassName);

			JAXBContext lJaxbContext = JAXBContext.newInstance(lc);

			Unmarshaller lJaxbUnmarshaller = lJaxbContext.createUnmarshaller();

			StringReader lInStringReader = new StringReader(pInputXMLStr);

			lRespObject = lJaxbUnmarshaller.unmarshal(lInStringReader);
		} catch (ClassNotFoundException pCnfex) {

			LOG.error("ClassNotFoundException",pCnfex);
		} catch (JAXBException pJaxex) {

			LOG.error("JAXBException",pJaxex);
		}

		return lRespObject;
	}

	public static String getMarshalled(Object pResponseObj, String pQualifiedClassName) {

		String lResponse = null;
		try {

			@SuppressWarnings("rawtypes")
			Class lc = Class.forName(pQualifiedClassName);

			JAXBContext lJaxbContext = JAXBContext.newInstance(lc);

			StringWriter stringWriter = new StringWriter();

			Marshaller lJaxbmarshaller = lJaxbContext.createMarshaller();

			lJaxbmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			lJaxbmarshaller.marshal(pResponseObj, stringWriter);

			lResponse = stringWriter.toString();
		} catch (JAXBException pJaxex) {

			LOG.error("JAXBException",pJaxex);
		} catch (ClassNotFoundException pCnfex) {

			LOG.error("ClassNotFoundException",pCnfex);
		}

		return lResponse;
	}

	public static String getJSONtoXML(String inputJSON) {
		String lxML = null;
		try {

			JSONObject jSONObject = new JSONObject(inputJSON);
			LOG.debug("getJSONtoXML.Input JSON:" + inputJSON);
			lxML = XML.toString(jSONObject);
			LOG.debug("getJSONtoXML.After converting JSON to XML:" + lxML);

		} catch (JSONException ex) {
			LOG.error("Exception",ex);
		}
		return lxML;
	}

	public static String getXMLToJSON(String inputXML) {
		String ljSON = null;
		try {

			com.iexceed.appzillon.json.JSONObject ljSONObj = com.iexceed.appzillon.json.XML
					.toJSONObject(inputXML);
			ljSON = ljSONObj.toString();

		} catch (com.iexceed.appzillon.json.JSONException ex) {
			LOG.error("Exception",ex);
		}
		return ljSON;
	}
}
