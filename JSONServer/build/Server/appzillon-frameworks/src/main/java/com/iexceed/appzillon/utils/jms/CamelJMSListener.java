package com.iexceed.appzillon.utils.jms;

import java.util.Enumeration;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Request;
import com.iexceed.appzillon.message.Response;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.impl.JMSServicesImpl;
import com.iexceed.appzillon.json.JSONObject;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.logging.log4j.ThreadContext;

public class CamelJMSListener implements MessageListener {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					CamelJMSListener.class.toString());
	public static String lastUpdatedId = "";
	private static CamelJMSListener jmsListener = null;

	public static CamelJMSListener getInstance() {
		if (jmsListener == null) {
			jmsListener = new CamelJMSListener();
		}
		return jmsListener;
	}

	public void onMessage(Message message) {
		try {
			if (message.getJMSCorrelationID() != null) {
				String lresponseString = "";
				ThreadContext.put("logRouter", "JMSListenerThread");
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JMS Message received - correlation ID:"
						+ message.getJMSCorrelationID());
				if (message instanceof TextMessage) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "TextMessage found");
					lresponseString = ((TextMessage) message).getText();
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JMS Message received - responseString:"
							+ lresponseString);
					if (lresponseString != null) {
						if (lresponseString.charAt(0) == '<') {
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "xml found");
							lresponseString = JMSServicesImpl.getXMLToJSON(lresponseString);
						} else if (lresponseString.charAt(0) == '{') {
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "json found");
						
						} else {
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Non xml/json value found");
							lresponseString = "{\"Response\":\""
									+ lresponseString + "\"}";
						}
					}

				} else if (message instanceof ObjectMessage) {
					Object resobj = ((ObjectMessage) message).getObject();
					String responseClass = "" + resobj.getClass();
					responseClass = responseClass.replaceAll("class ", "");
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response classs name " + responseClass);

					String xmlRes = JMSServicesImpl.getMarshalled(resobj, "" + responseClass);
					lresponseString = JMSServicesImpl.getXMLToJSON(xmlRes);
				} else if (message instanceof MapMessage) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "MapMessage found");
					Enumeration<?> mapNames = ((MapMessage) message).getMapNames();
					String eachName;
					Object eachValue;
					JSONObject json = new JSONObject();
					while (mapNames.hasMoreElements()) {
						
						eachName = (String) mapNames.nextElement();
						eachValue = ((MapMessage) message).getObject(eachName);
						LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "key found "+eachName);
						json.put(eachName, "" + eachValue);

					}
					lresponseString = json.toString();

				} else if (message instanceof BytesMessage) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "BytesMessage found");
					int contentLength = (int) ((BytesMessage) message)
							.getBodyLength();
					byte[] body = new byte[contentLength];
					((BytesMessage) message).readBytes(body);
					lresponseString = new String(body);
					if (lresponseString != null) {
						if (lresponseString.charAt(0) == '<') {
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "xml found");
							lresponseString = JMSServicesImpl.getXMLToJSON(lresponseString);
						} else if (lresponseString.charAt(0) == '{') {
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "json found");
						
						} else {
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Non xml/json value found");
							lresponseString = "{\"Response\":\""
									+ lresponseString + "\"}";
						}
					}

				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "StreamMessage found");
					int contentLength = 1;
					while (((StreamMessage) message).readByte() != -1) {
						contentLength++;
					}
					byte[] body = new byte[contentLength];
					((StreamMessage) message).readBytes(body);
					lresponseString = new String(body);
					if (lresponseString != null) {
						if (lresponseString.charAt(0) == '<') {
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "xml found");
							lresponseString = JMSServicesImpl.getXMLToJSON(lresponseString);
						} else if (lresponseString.charAt(0) == '{') {
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "json found");
						
						} else {
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Non xml/json value found");
							lresponseString = "{\"Response\":\""
									+ lresponseString + "\"}";
						}
					}

				}

				JSONObject lJSONObject = new JSONObject();
				lJSONObject.put(
						ServerConstants.JMS_RESP_QUEUE,
						message.getJMSDestination().toString());
				lJSONObject
						.put(ServerConstants.JMS_MSG_CORRELATION_ID,
								message.getJMSCorrelationID());
				lJSONObject.put(
						ServerConstants.JMS_RESP_MSG,
						lresponseString);
				com.iexceed.appzillon.message.Message msg = com.iexceed.appzillon.message.Message
						.getInstance();
				msg.setHeader(Header.getInstance());
				msg.getHeader().setServiceType(
						ServerConstants.APPZJMSRESPUPDATEREQUEST);
				msg.setRequestObject(Request.getInstance());
				msg.getRequestObject().setRequestJson(lJSONObject);
				msg.setResponseObject(Response.getInstance());
				DomainStartup.getInstance().processRequest(msg);

				String jmsResponse = msg.getResponseObject().getResponseJson()
						.getString("jmsStatus");
				lastUpdatedId = message.getJMSCorrelationID();
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JMS response after updating the DB - lJMSResp:"
						+ jmsResponse);
			}
		} catch (com.iexceed.appzillon.json.JSONException ex) {
			LOG.error("[FrameworkServices]  JSONException:" ,ex);
		} catch (JMSException e) {
			LOG.error("[FrameworkServices]  JMSException:" ,e);
		}

	}

}
