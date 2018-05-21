package com.iexceed.appzillon.services;

import com.iexceed.appzillon.dao.JMSDetails;
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServicesUtil;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;

import org.springframework.jndi.JndiTemplate;

import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.jms.CamelJMSListener;
import com.iexceed.appzillon.utils.jms.CamelJMSQueueResolver;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.*;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;

public class JMSService implements IServicesBean {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					JMSService.class.toString());
	public static Map<String, String> isConnected = new HashMap<String, String>();
	protected JMSDetails cJMSDetails = null;

	public void getJMSDetails(com.iexceed.appzillon.message.Message pMessage,
			SpringCamelContext pContext) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ApplicationId -:"
				+ pMessage.getHeader().getAppId() + ", InterfaceId -:"
				+ pMessage.getHeader().getInterfaceId()
				+ ", and Service Details BeanId -:"
				+ pMessage.getHeader().getAppId() + "_"
				+ pMessage.getHeader().getInterfaceId());
		cJMSDetails = (JMSDetails) ExternalServicesRouter
				.injectBeanFromSpringContext(pMessage.getHeader().getAppId()
						+ "_" + pMessage.getHeader().getInterfaceId(), pContext);
		int timeOut = cJMSDetails.getTimeOut();
        /**
         * Below changes are made by Vinod as part of 
         * At app level, service time out should be configurable.
         * Appzillon 3.1 - 63 -- Start
         */
		if (timeOut == 0) {
		   	LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Timeout value not configured will use default timeOut");
			timeOut =Integer.parseInt(PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.DEFAULT_TIMEOUT).trim()) ;
			}
		/** Appzillon 3.1 - 63 -- END */
		  cJMSDetails.setTimeOut(timeOut);  
	}

	@Override
	public Object buildRequest(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		Object reqpayload = null;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Building Request Usiing Superclass implementation ");
		String lpayLoad = getPayLoadtoDeliver((String) pRequestPayLoad);

		if (ServerConstants.JMS_MSG_TYPE_OBJECT.equals(cJMSDetails
				.getJmsMessageType())) {
			String lInputXMLStr = ExternalServicesRouter.getJSONtoXML(lpayLoad);
			String lReqQualifiedClassName = cJMSDetails
					.getRequestQualifiedClassName();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Qualified class name " + lReqQualifiedClassName);
			reqpayload = ExternalServicesRouter.getUnMarshalled(lInputXMLStr,
					lReqQualifiedClassName);
		} else if (ServerConstants.JMS_MSG_TYPE_MAP.equals(cJMSDetails
				.getJmsMessageType())) {
			reqpayload = JSONUtils.getJsonHashMap(lpayLoad);
		} else if (ServerConstants.JMS_MSG_TYPE_STREAM.equals(cJMSDetails
				.getJmsMessageType())) {
			StringWriter s = new StringWriter();
			s.write(lpayLoad);
			reqpayload = s;
			try {
				s.close();
			} catch (IOException e) {
				LOG.error(ServerConstants.IOEXCEPTION, e);
			}
		} else if (ServerConstants.JMS_MSG_TYPE_BYTES.equals(cJMSDetails
				.getJmsMessageType())) {

			try {
				reqpayload = lpayLoad.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOG.error("UnsupportedEncodingException",e);
			}
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " JMSmessage Type Text found");
			if (ServerConstants.XML.equalsIgnoreCase(cJMSDetails
					.getRequestContentType())) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " requestContentType XML");
				reqpayload = ExternalServicesRouter.getJSONtoXML(lpayLoad);

			} else {
				LOG.debug("requestcontentType NON-XML");
				reqpayload = lpayLoad;
			}

		}
		return reqpayload;
	}

	@Override
	public Object processResponse(Message pMessage, Object pResponse,
			SpringCamelContext pContext) {
		return pResponse;
	}

	@Override
	public Object callService(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		String responseFromQueue;
		String ouputString = null;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Getting JMSDetail bean from context");
		getJMSDetails(pMessage, pContext);
		
		pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad, cJMSDetails.getAutoGenElementMap(), cJMSDetails.getTranslationElementMap());
		pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad+""));
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Appending Request Json With MaskedId : " + pMessage.getRequestObject().getRequestJson());
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Calling Build Request method");
		final Object fObject = buildRequest(pMessage, pRequestPayLoad.toString(), pContext);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Request created or edited succesfully " + fObject);

		LOG.info("\n\n"+ ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "********Sending request to Request Queue   Start ***********");
		ProducerTemplate pProducer = ExternalServicesRouter
				.createProducerTemplate(pContext);
		CamelJMSQueueResolver ljmsQueueResolver = (CamelJMSQueueResolver) ExternalServicesRouter
				.injectBeanFromSpringContext(pMessage.getHeader().getAppId()
						+ "_" + pMessage.getHeader().getInterfaceId()+"_"
						+ ServerConstants.JMS_CAMEL_QUEUE_RESOLVER, pContext);
		String endpoint = createEndpointURIByAppendingParam(pMessage, pContext);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Endpoint URI used after appending params " + endpoint);
		final String fcorrelationId = createJMSTransactionInDomain(pMessage,
				ljmsQueueResolver,
				getPayLoadtoDeliver( pRequestPayLoad.toString()));
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Jms endpoint used after appending params" + endpoint);
		Utils.setExtTime(pMessage,"S");
		Exchange exchange = pProducer.request(endpoint, new Processor() {
			public void process(Exchange exchng) throws Exception {
				exchng.getIn().setBody(fObject);
				exchng.getIn().setHeader("JMSCorrelationID", fcorrelationId);

			}
		});

		Utils.setExtTime(pMessage,"E");
		if (exchange.getException() != null) {
			LOG.error("Exchange exception",exchange.getException());
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_011.toString());
			String emsg = exsrvcallexp
					.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_011);
			exsrvcallexp.setMessage(emsg);
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,exchange.getException().getMessage(),fObject);
			throw exsrvcallexp;
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " No Exception"
					+ "So starting listener on all response queues");
			responseFromQueue = getMessageFromResponseQueue(fcorrelationId,
					ljmsQueueResolver, pMessage, pContext, pProducer);
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.SUCCESS,responseFromQueue,fObject);

		}
		ouputString = (String) processResponse(pMessage, responseFromQueue,
				pContext);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Response from JMSService " + ouputString);
		return new JSONObject(ouputString);
	}

	public String getMessageFromResponseQueue(String fcorrelationId,
			CamelJMSQueueResolver ljmsQueueResolver, Message pMessage,
			SpringCamelContext pContext, ProducerTemplate pProducer) {

		String lInterfaceId = pMessage.getHeader().getInterfaceId();
		String lAppId = pMessage.getHeader().getAppId();
		String lresponseFromQueue = null;
		Context lnamingContext = null;
		Properties lprop = null;
		QueueSession lqueueSession = null;
		QueueConnection lconnection = null;
		QueueConnectionFactory lconnectionFactory = null;
		long lstartTime = 0;
		String lconStatus = isConnected.get(lInterfaceId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " connection status of this interfaceID " + lconStatus);
		if (!ServerConstants.CONNECTED.equals(lconStatus)) {

			try {
				lprop = ((JndiTemplate) pContext.getRegistry().lookupByName(
						lAppId + "_" + lInterfaceId + ServerConstants.JNDI_TEMPLATE))
						.getEnvironment();
				lnamingContext = new InitialContext(lprop);

				lconnectionFactory = (QueueConnectionFactory) lnamingContext
						.lookup(cJMSDetails.getConnectionFactory());

				lconnection = lconnectionFactory
						.createQueueConnection(
								(String) lprop
										.get(ServerConstants.JMS_JAVA_NAMING_SEC_PRINCIPAL),
								(String) lprop
										.get(ServerConstants.JMS_JAVA_NAMING_SEC_CREDENTIALS));
				lconnection
						.setExceptionListener(new com.iexceed.appzillon.utils.jms.JMSConExceptionListener(
								lconnection, lnamingContext, lInterfaceId));
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " connectionFactory:  " + lconnectionFactory);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " user :"
						+ (String) lprop
								.get(ServerConstants.JMS_JAVA_NAMING_SEC_PRINCIPAL));

				lqueueSession = lconnection.createQueueSession(false,
						QueueSession.AUTO_ACKNOWLEDGE);

				CamelJMSListener lcamelJMSListener = CamelJMSListener
						.getInstance();

				int i = 0;
				while (i < ljmsQueueResolver.getResponseQueues().size()) {
					Queue queue = (Queue) lnamingContext
							.lookup(ljmsQueueResolver.getResponseQueues()
									.get(i));

					QueueReceiver queueReceiver = lqueueSession
							.createReceiver(queue);
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " queueReceiver:" + queueReceiver);
					queueReceiver.setMessageListener(lcamelJMSListener);

					i++;
				}
				lconnection.start();

				isConnected.put(lInterfaceId, ServerConstants.CONNECTED);

			} catch (NamingException e) {
				LOG.error("NamingException",e);
				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_011.toString());
				String emsg = exsrvcallexp
						.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_011);
				exsrvcallexp.setMessage(emsg);
				exsrvcallexp.setPriority("1");

				throw exsrvcallexp;
			} catch (JMSException e) {
				LOG.error("JMSException",e);
				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_011.toString());
				String emsg = exsrvcallexp
						.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_011);
				exsrvcallexp.setMessage(emsg);
				exsrvcallexp.setPriority("1");

				throw exsrvcallexp;
			}
		}

		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Waiting for correlation iD :" + fcorrelationId
				+ " to get Updated in atmost:" + cJMSDetails.getTimeOut()
				+ " sec");
		int timeOut = cJMSDetails.getTimeOut() * 1000;
		boolean updatedBefTimeOut = false;
		lstartTime = new Date().getTime();
		String updateCor = CamelJMSListener.lastUpdatedId;
		while (System.currentTimeMillis() - lstartTime < timeOut) {
			if (updateCor.equals(fcorrelationId)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Updated corelation id in  "
						+ (System.currentTimeMillis() - lstartTime)
						+ "millisecs after connection");
				updatedBefTimeOut = true;
				break;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					LOG.error("InterruptedException",e);
				}
				updateCor = CamelJMSListener.lastUpdatedId;
			}
		}
		if (!updatedBefTimeOut) {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Didnot get the response within " + timeOut + " ms");
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "  response from database");
		JSONObject lobject = new JSONObject();
		try {
			lobject.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
			lobject.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID,
					lInterfaceId);
			lobject.put(ServerConstants.JMS_MSG_CORRELATION_ID, fcorrelationId);

			pMessage.getHeader().setServiceType(
					ServerConstants.APPZGETJMSSTATUS);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "setting service type to"
					+ pMessage.getHeader().getServiceType());
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " sending json to domain " + lobject);
			pMessage.getRequestObject().setRequestJson(lobject);
			DomainStartup.getInstance().processRequest(pMessage);
			JSONObject response = pMessage.getResponseObject()
					.getResponseJson();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " response from domain " + response);
			lresponseFromQueue = response.getString("JmsResponseJSON");
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " getMessageFromResponseQueue - database response:"
					+ lresponseFromQueue);
		} catch (JSONException e) {
			LOG.error("JSONException",e);
		}
		if (lresponseFromQueue == null) {
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_010.toString());
			String emsg = exsrvcallexp
					.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_010);
			exsrvcallexp.setMessage(emsg);
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;

		}
		return lresponseFromQueue;

	}

	public String createEndpointURIByAppendingParam(Message pMessage,
			SpringCamelContext pContext) {

		String camelID = (pMessage.getHeader().getAppId() + "__" + pMessage
				.getHeader().getInterfaceId()).replaceAll("[\\.]", "__");
		String endpoint = pContext.getEndpoint(camelID).getEndpointUri();
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Jms endpoint used before appending params" + endpoint);

		StringBuilder appendOption = new StringBuilder(ServerConstants.JMS_DEFAULT_APPENDER);
		int timeOut = cJMSDetails.getTimeOut();
		timeOut = timeOut * 1000;
		if (timeOut == 0 || (Integer) timeOut == null) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " TimeOut value is not set by the user hence setting it to 20 seconds....");
			timeOut = 20000;
		}
		appendOption.append(ServerConstants.JMS_APPEND_MSG_TYPE
				+ cJMSDetails.getJmsMessageType());
		Properties prop = ((JndiTemplate) pContext.getRegistry().lookupByName(
				pMessage.getHeader().getAppId() + "_"
						+ pMessage.getHeader().getInterfaceId()
						+ ServerConstants.JNDI_TEMPLATE)).getEnvironment();
		String username = (String) prop
				.get(ServerConstants.JMS_JAVA_NAMING_SEC_PRINCIPAL);
		String password = (String) prop
				.get(ServerConstants.JMS_JAVA_NAMING_SEC_CREDENTIALS);
		if (!(username == null || password == null)) {
			appendOption.append(ServerConstants.JMS_APPEND_AND_USER + username
					+ ServerConstants.JMS_APPEND_AND_PASS + password);
		}

		endpoint += appendOption;
		return endpoint;
	}

	public String createJMSTransactionInDomain(Message pMessage,
			CamelJMSQueueResolver pjmsQueueRes, String lpayLoad) {
		String lcorelationRefName = cJMSDetails.getJmsCorelationName();
		String wholepayLoad = pMessage.getRequestObject().getRequestJson()
				.toString();
		if (lcorelationRefName == null) {
			lcorelationRefName = "";
		}
		String lcorrelationId = JSONUtils.getKeyValue(wholepayLoad,
				lcorelationRefName);
		JSONObject lJSONObject = new JSONObject();
		lJSONObject.put(ServerConstants.JMS_REQ_PAYLOAD, lpayLoad);
		lJSONObject.put(ServerConstants.JMS_RESP_MSG, "");

		lJSONObject.put(ServerConstants.JMS_REQ_TYPE,
				cJMSDetails.getRequestContentType());
		lJSONObject.put(ServerConstants.JMS_REQ_QUEUE,
				pjmsQueueRes.getRequestQueue());
		lJSONObject.put(ServerConstants.JMS_RESP_TYPE,
				cJMSDetails.getResponseContentType());
		lJSONObject.put(ServerConstants.JMS_MSG_ID, lcorrelationId);
		LOG.info("reqQueue                                            "
				+ cJMSDetails);

		pMessage.getHeader().setServiceType(
				ServerConstants.APPZJMSREQINSERTREQUEST);
		pMessage.getRequestObject().setRequestJson(lJSONObject);
		DomainStartup.getInstance().processRequest(pMessage);
		String jmsTransactionId = pMessage.getResponseObject()
				.getResponseJson().getString("jmsTransactionId");
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " createJMSTransactionInDomain -jmsTranactionId:"
				+ jmsTransactionId);
		if (Utils.isNullOrEmpty(lcorrelationId)) {
			lcorrelationId = "JMS_" + jmsTransactionId;
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " createJMSTransactionInDomain -lcorrelationId:" + lcorrelationId);
		return lcorrelationId;

	}

	public String getPayLoadtoDeliver(String appzillonBodyContent) {
		String lpayLoad = appzillonBodyContent;
		if (Utils.isNotNullOrEmpty(cJMSDetails.getTextMsgNode())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Message node configured in detail bean is :"
					+ cJMSDetails.getTextMsgNode());
			String nodeValue = JSONUtils.getKeyValue(appzillonBodyContent,
					cJMSDetails.getTextMsgNode());
			if (Utils.isNullOrEmpty(nodeValue)) {
				LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " No Node found with name "
						+ cJMSDetails.getTextMsgNode() + "in appzillonBody");
				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_009.toString());
				String emsg = exsrvcallexp
						.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_009);
				exsrvcallexp.setMessage(emsg);
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;

			} else {
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Node value found in appzillonBody ");
				lpayLoad = nodeValue;
			}
		} else {
			LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " No Specific Node configured with  in appzillonBody: Will Use appzillonBody");

		}
		return lpayLoad;

	}
}
