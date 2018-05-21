package com.iexceed.appzillon.services;

import com.iexceed.appzillon.exception.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ResolveEndpointFailedException;
import org.apache.camel.spring.SpringCamelContext;

import com.iexceed.appzillon.dao.MailDetails;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;

import java.sql.Timestamp;
import java.util.Date;

public class MailService implements IServicesBean {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					MailService.class.toString());
	protected MailDetails mailDetails = null;

	@Override
	public Object callService(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Inside mailservice... with payload "
				+ pRequestPayLoad);
		getMailDetails(pMessage, pContext);
		JSONObject json = (JSONObject) buildRequest(pMessage, pRequestPayLoad,
				pContext);
		String uri = createURIByParams(pMessage, json);
		Exchange exchange = null;

		final String lBodymsg = json
				.getString(ServerConstants.MAIL_CONSTANTS_BODY);
		ProducerTemplate producer = ExternalServicesRouter
				.createProducerTemplate(pContext);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "direct start endpoint..." + uri);
		
		try{
			Utils.setExtTime(pMessage,"S");
			exchange = producer.request(uri, new Processor() {
				public void process(Exchange exchange) throws Exception {
					exchange.setProperty(Exchange.CHARSET_NAME, "UTF-8");
					exchange.getIn().getHeaders().put("Content-Type", "text/html");
					exchange.getIn().setBody(lBodymsg);
				}
			});
		}catch(ResolveEndpointFailedException ex) {		//29-9-2015 handled if Mail details not found
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_044.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_044));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		}
		
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Exchange completed");
		Utils.setExtTime(pMessage,"E");
		if (exchange.getException() != null) {
			LOG.error("Exchange Exception",exchange.getException());
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();

			// handled if authentication is failed
			if (exchange.getException() instanceof javax.mail.AuthenticationFailedException) {
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_035.toString());
				exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_035));
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;
			}
			
			// handled if missing domain in mail address
			if (exchange.getException() instanceof javax.mail.internet.AddressException) {
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_036.toString());
				exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_036));
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;
			}
			
			
			//10-7-2014  : below change is to handle exception when user enters invalid email address
			if (exchange.getException() instanceof javax.mail.SendFailedException) {
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_025.toString());
				exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_025));
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;
				
			}else {
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_023.toString());
				exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_023));
			}
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;

		}
		JSONObject lRespJson = new JSONObject();
		lRespJson.put(ServerConstants.MESSAGE_HEADER_STATUS,
				ServerConstants.SUCCESS);
		String resobj = (String) processResponse(pMessage,
				lRespJson.toString(), pContext);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Response Object after processing response "
				+ resobj);
		return new JSONObject(resobj);
	}

	@Override
	public Object buildRequest(Message pMessage, Object pRequestPayLoad,SpringCamelContext pContext) {
		JSONObject lPayloadobj = (JSONObject) pRequestPayLoad;
		JSONObject lEmailobj = null;

		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Inside  buildRequest with payload "
				+ lPayloadobj);
		/*if (lPayloadobj.has(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside password reset..");
			lEmailobj = lPayloadobj
					.getJSONObject(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES);
			lEmailobj.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, "password");

		} else if (lPayloadobj
				.has(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside createuser...");
			lEmailobj = lPayloadobj
					.getJSONObject(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES);
		}*/ //else {
			lEmailobj = lPayloadobj
					.getJSONObject(ServerConstants.INTERFACE_ID_MAIL_REQ);
		//}
		if (lEmailobj.has(ServerConstants.MAIL_CONSTANTS_EMAIL_ID)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Mail to be sent to Email Id "
					+ lEmailobj
							.getString(ServerConstants.MAIL_CONSTANTS_EMAIL_ID));
		} else {
			LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "emailid is not found in request...");
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_012.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_012));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		}
		if (!lEmailobj.has(ServerConstants.MAIL_CONSTANTS_CC)) {
			lEmailobj.put(ServerConstants.MAIL_CONSTANTS_CC, "");
		}
		if (!lEmailobj.has(ServerConstants.MAIL_CONSTANTS_BCC)) {
			lEmailobj.put(ServerConstants.MAIL_CONSTANTS_BCC, "");
		}
		if (!lEmailobj.has(ServerConstants.MAIL_CONSTANTS_SUBJECT)) {
			lEmailobj.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, "");
		}
		if (!lEmailobj.has(ServerConstants.MAIL_CONSTANTS_BODY)) {
			lEmailobj.put(ServerConstants.MAIL_CONSTANTS_BODY, "");
		}
		String msg = "";
		if (Utils.isNullOrEmpty(lEmailobj.getString(ServerConstants.MAIL_CONSTANTS_BODY))
				&& !lPayloadobj.has(ServerConstants.INTERFACE_ID_MAIL_REQ)) {
			lEmailobj.put(ServerConstants.MAIL_CONSTANTS_BODY, msg);
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Data build from build request "
				+ lPayloadobj);
		return lEmailobj;

	}

	@Override
	public Object processResponse(Message pMessage, Object pResponse, SpringCamelContext pContext) {
		return pResponse;
	}

	public String createURIByParams(Message pMessage, JSONObject json) {
		LOG.debug("[FrameworkServices] " + " protocol used : "
				+ mailDetails.getProtocol() + " port used : "
				+ mailDetails.getPortNumber() + " from user: "
				+ mailDetails.getFrom());
		String str = mailDetails.getProtocol()
				+ ServerConstants.MAIL_URL_SEPARATOR_COLON
				+ ServerConstants.MAIL_URL_SEPARATOR_DOUBLE_SLASH
				+ mailDetails.getHostName()
				+ ServerConstants.MAIL_URL_SEPARATOR_COLON
				+ mailDetails.getPortNumber()
				+ ServerConstants.MAIL_URL_CONSTANTS_TO
				+ json.getString(ServerConstants.MAIL_CONSTANTS_EMAIL_ID)
				+ ServerConstants.MAIL_URL_CONSTANTS_SUBJECT
				+ json.getString(ServerConstants.MAIL_CONSTANTS_SUBJECT)
				+ ServerConstants.MAIL_URL_CONSTANTS_FROM
				+ mailDetails.getFrom();
		StringBuilder defaultURI = new StringBuilder(str);
		String appendUserIDPassURI = ServerConstants.MAIL_URL_CONSTANTS_USER_NAME
				+ mailDetails.getUserName()
				+ ServerConstants.MAIL_URL_CONSTANTS_PASSWORD
				+ mailDetails.getPassword();
		if (Utils.isNotNullOrEmpty(json.getString(ServerConstants.MAIL_CONSTANTS_CC))) {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "CC is not null");
			defaultURI.append(ServerConstants.MAIL_URL_CONSTANTS_CC
					+ json.getString(ServerConstants.MAIL_CONSTANTS_CC));
		}
		if (Utils.isNotNullOrEmpty(json.getString(ServerConstants.MAIL_CONSTANTS_BCC))) {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "BCC is not null");
			defaultURI.append(ServerConstants.MAIL_URL_CONSTANTS_BCC
					+ json.getString(ServerConstants.MAIL_CONSTANTS_BCC));
		}
		if (Utils.isNotNullOrEmpty(mailDetails.getUserName()) || Utils.isNotNullOrEmpty(mailDetails.getPassword())) {
			defaultURI.append(appendUserIDPassURI);
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "final URI formed : " + defaultURI);
		return defaultURI.toString();
	}

	public void getMailDetails(Message pMessage, SpringCamelContext pContext) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Mail Details Id used "
				+ pMessage.getHeader().getAppId() + "_"
				+ pMessage.getHeader().getInterfaceId());
		mailDetails = (MailDetails) ExternalServicesRouter
				.injectBeanFromSpringContext(pMessage.getHeader().getAppId()
						+ "_" + pMessage.getHeader().getInterfaceId(), pContext);

	}
}
