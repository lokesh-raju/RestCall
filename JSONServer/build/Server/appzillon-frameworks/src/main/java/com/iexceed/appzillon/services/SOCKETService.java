package com.iexceed.appzillon.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;

import com.iexceed.appzillon.exception.Utils;
import org.apache.camel.spring.SpringCamelContext;

import com.iexceed.appzillon.dao.SocketDetails;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;

public class SOCKETService implements IServicesBean {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					SOCKETService.class.toString());
	protected SocketDetails socketDet = null;
	
	@Override
	public Object callService(Message pMessage, Object pRequestPayLoad, SpringCamelContext pContext) {
		OutputStream out = null;
		InputStream in = null;
		String dSource = null;
		int timeOut = 120;
		Socket s = null;
		String appId = pMessage.getHeader().getAppId();
		String interfaceId = pMessage.getHeader().getInterfaceId();
		String payLoad=null;
		try {
			socketDet = getSocketDetails(interfaceId, appId, pContext);
			timeOut = socketDet.getTimeOut();

			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "connecting to " + socketDet.getEndPointURL()
					+ " on port " + socketDet.getPortNo());
			s = new Socket(socketDet.getEndPointURL(), Integer.parseInt(socketDet.getPortNo()));
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Setting Time out:" + timeOut + ", to millisecond:"
					+ timeOut * 1000);
			s.setSoTimeout(timeOut * 1000);
			pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad,
					socketDet.getAutoGenElementMap(), socketDet.getTranslationElementMap());
			pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad + ""));
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Appending Request Json With MaskedId : "
					+ pMessage.getRequestObject().getRequestJson());

			payLoad = (String) buildRequest(pMessage, pRequestPayLoad.toString(), pContext);

			out = s.getOutputStream();
			String lPayLoad = payLoad;
			Utils.setExtTime(pMessage, "S");
			out.write(lPayLoad.getBytes());
			Utils.setExtTime(pMessage, "E");
			out.flush();

			String str = null;

			in = s.getInputStream();
			int size = s.getReceiveBufferSize();
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "buffer size" + size);
			byte[] resbytes = new byte[size];
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "response byte array" + resbytes);
			int length = in.read(resbytes);
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "length" + length);
			str = new String(resbytes, 0, length);
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.SUCCESS,str,payLoad);
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "xmlResponse from external server " + str);
			dSource = str;
		} catch (NumberFormatException e) {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Exception in port" + e);
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "NumberFormatException ", e);		
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_054.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_054));
			exsrvcallexp.setPriority("1");
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,e.getMessage(),payLoad);
			throw exsrvcallexp;
		} catch (UnknownHostException e) {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Unknown Host Exception " + e);
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "UnknownHostException ", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,e.getMessage(),payLoad);
			throw exsrvcallexp;
		}catch (ConnectException e){
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ConnectException " + e);
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ConnectException ", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,e.getMessage(),payLoad);
			throw exsrvcallexp;
		}catch (SocketTimeoutException e){
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Socket Timedout Exception " + e);
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "SocketTimeoutException ", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,e.getMessage(),payLoad);
			throw exsrvcallexp;
		}catch (SocketException e){
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Socket Exception " + e);
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "SocketException ", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,e.getMessage(),payLoad);
			throw exsrvcallexp;
		}catch (IOException e) {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IO Exception" + e);
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_055.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_055));
			exsrvcallexp.setPriority("1");
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,e.getMessage(),payLoad);
			throw exsrvcallexp;
		}
		finally {
			// closing resources
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (s != null) {
					s.close();
				}
			} catch (IOException e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException", e);
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException" + e);
			}
		}
		dSource = (String) processResponse(pMessage, dSource, pContext);
		return new JSONObject(dSource);
	}

	public SocketDetails getSocketDetails(String pInterfaceID, String pAppId,
			SpringCamelContext pContext) {
		SocketDetails socketDetails = (SocketDetails) ExternalServicesRouter
				.injectBeanFromSpringContext(pAppId + "_" + pInterfaceID,
						pContext);
		int timeOut = socketDetails.getTimeOut();
        /**
         * Below changes are made by Vinod as part of 
         * At app level, service time out should be configurable.
         * Appzillon 3.1 - 63 -- Start
         */
		if (timeOut == 0 ) {
				LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Timeout value not configured will use default timeOut");
				timeOut =Integer.parseInt(PropertyUtils.getPropValue(pAppId, ServerConstants.DEFAULT_TIMEOUT).trim()) ;
				}
		/** Appzillon 3.1 - 63 -- END */
		socketDetails.setTimeOut(timeOut);
		return socketDetails;

	}

	@Override
	public Object buildRequest(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Inside SuperClass buildRequest with payload "
				+ pRequestPayLoad);
		
		return pRequestPayLoad;
	}

	@Override
	public Object processResponse(Message pMessage, Object pResponse,
			SpringCamelContext pContext) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Inside SuperClass processResponse with payload " + pResponse);
		return pResponse;
	}
}
