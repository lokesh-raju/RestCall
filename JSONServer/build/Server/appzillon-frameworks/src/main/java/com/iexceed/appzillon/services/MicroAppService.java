package com.iexceed.appzillon.services;

import java.sql.Timestamp;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.entity.TbDbtpTxnMaster;
import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

public class MicroAppService {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					MicroAppService.class.getName());
		
	public TbDbtpTxnMaster saveMicroAppTxnDetails(Message pMessage,TbDbtpTxnMaster ltbDtpTxnRec){
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS +"In MicroAppService");
		JSONObject lRequest  = pMessage.getRequestObject().getRequestJson();
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_SAVE_MICROAPPTXNDETAILS);
		
		JSONObject ltbDbpRecord = new JSONObject();
		ltbDbpRecord.put(ServerConstants.MESSAGE_HEADER_USER_ID, ltbDtpTxnRec.getUserId());
		ltbDbpRecord.put(ServerConstants.MESSAGE_HEADER_APP_ID, ltbDtpTxnRec.getAppId());
		ltbDbpRecord.put(ServerConstants.TXN_TYPE, ltbDtpTxnRec.getTxnType());
		ltbDbpRecord.put(ServerConstants.CUSTOMER_ID, ltbDtpTxnRec.getCustomerId());
		ltbDbpRecord.put(ServerConstants.AMOUNT, ltbDtpTxnRec.getAmount().toString());
		ltbDbpRecord.put(ServerConstants.TXN_STATUS, ltbDtpTxnRec.getTxnStatus());
		
		JSONObject lReqNode = new JSONObject();
		lReqNode.put(ServerConstants.SAVE_MICROAPP_TXN_DETAILS, ltbDbpRecord);
		pMessage.getRequestObject().setRequestJson(lReqNode);
		DomainStartup.getInstance().processRequest(pMessage);
		pMessage.getRequestObject().setRequestJson(lRequest);
		JSONObject lResponse = pMessage.getResponseObject().getResponseJson();
		ltbDtpTxnRec.setTxnStatus(lResponse.getString(ServerConstants.TXN_STATUS));
		ltbDtpTxnRec.setCreationTs((Timestamp)lResponse.get(ServerConstants.CREATION_TS));
		ltbDtpTxnRec.setTxnRefNo(lResponse.getString(ServerConstants.TXN_REF));
		return ltbDtpTxnRec;
	}		
	
	public TbDbtpTxnMaster updateMicroAppTxnDetails(Message pMessage,TbDbtpTxnMaster ltbDtpTxnRec,String updateTs){
		JSONObject lRequest  = pMessage.getRequestObject().getRequestJson();
		try{
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS +"In MicroAppService");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_UPDATE_MICROAPPTXNDETAILS);
		
		JSONObject ltbDbpRecord = new JSONObject();
		//ltbDbpRecord.put(ServerConstants.MESSAGE_HEADER_USER_ID, ltbDtpTxnRec.getUserId());
		//ltbDbpRecord.put(ServerConstants.MESSAGE_HEADER_APP_ID, ltbDtpTxnRec.getAppId());
		//ltbDbpRecord.put(ServerConstants.CUSTOMER_ID, ltbDtpTxnRec.getCustomerId());
		//ltbDbpRecord.put(ServerConstants.AMOUNT, ltbDtpTxnRec.getAmount().toString());
		ltbDbpRecord.put(ServerConstants.TXN_STATUS, ltbDtpTxnRec.getTxnStatus());
		ltbDbpRecord.put(ServerConstants.TXN_REF, ltbDtpTxnRec.getTxnRefNo());
		//ltbDbpRecord.put(ServerConstants.TXN_TYPE, ltbDtpTxnRec.getTxnType());
		ltbDbpRecord.put(ServerConstants.UPDATE_TS, updateTs);
		
		JSONObject lReqNode = new JSONObject();
		lReqNode.put(ServerConstants.UPDATE_MICROAPP_TXN_DETAILS,ltbDbpRecord);
		pMessage.getRequestObject().setRequestJson(lReqNode);
		DomainStartup.getInstance().processRequest(pMessage);
		pMessage.getRequestObject().setRequestJson(lRequest);
		}catch(AppzillonException ex){
		  pMessage.getRequestObject().setRequestJson(lRequest);
		  throw ex;
		}
		JSONObject lResponse = pMessage.getResponseObject().getResponseJson();
		ltbDtpTxnRec.setTxnStatus(lResponse.getString(ServerConstants.TXN_STATUS));
		ltbDtpTxnRec.setCreationTs((Timestamp) lResponse.get(ServerConstants.CREATION_TS));
		if (Utils.isNotNullOrEmpty(updateTs) && ServerConstants.YES.equals(updateTs)) {
			ltbDtpTxnRec.setCompletionTs((Timestamp)lResponse.get(ServerConstants.COMPLETION_TS));
		}
		return ltbDtpTxnRec;
	}
	
	public void fetchMicroAppTxnDetails(Message pMessage,String txnRef){
		JSONObject lRequest  = pMessage.getRequestObject().getRequestJson();
		try{
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS +"In MicroAppService");
		pMessage.getHeader().setServiceType(ServerConstants.FETCH_MICROAPP_TXN_DETAILS);
		JSONObject lReqNode = new JSONObject();
		lReqNode.put(ServerConstants.SERVICE_FETCH_MICROAPPTXNDETAILS,new JSONObject().put(ServerConstants.TXN_REF, txnRef));
		pMessage.getRequestObject().setRequestJson(lReqNode);
		DomainStartup.getInstance().processRequest(pMessage);
		pMessage.getRequestObject().setRequestJson(lRequest);
		}catch(AppzillonException ex){
			pMessage.getRequestObject().setRequestJson(lRequest);
			throw ex;
		}
	}
}
