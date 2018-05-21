package com.iexceed.appzillon.domain.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbDbtpTxnMaster;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbDbtpTxnMasterRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

@Named(ServerConstants.SERVICE_MICROAPP)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class MicroAppService {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
			MicroAppService.class.toString());

	@Inject
	TbDbtpTxnMasterRepository masterRepo;

	public void saveMicroAppTxnDetails(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Saving MicroAppTxnDetails ...");
		JSONObject request = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.SAVE_MICROAPP_TXN_DETAILS);
		
		String txnStatus = "";
		String appId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String userId = request.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
		String custId = request.getString(ServerConstants.CUSTOMER_ID);
		String amount = request.getString(ServerConstants.AMOUNT);
		String txnType = request.getString(ServerConstants.TXN_TYPE);
		if(request.has(ServerConstants.TXN_STATUS) && 
				Utils.isNotNullOrEmpty(request.getString(ServerConstants.TXN_STATUS))){
		 txnStatus = request.getString(ServerConstants.TXN_STATUS);	
		}
		Timestamp tm = new Timestamp(new Date().getTime());

		TbDbtpTxnMaster txnMaster = new TbDbtpTxnMaster();
		txnMaster.setTxnRefNo(Utils.getTxnRefNum(userId));
		txnMaster.setUserId(userId);
		txnMaster.setAppId(appId);
		txnMaster.setTxnType(txnType);
		txnMaster.setCustomerId(custId);
		txnMaster.setAmount(new BigDecimal(amount));
		txnMaster.setCreationTs(tm);
		if(txnStatus.isEmpty()){
		    txnMaster.setTxnStatus("U");
		}else{
			txnMaster.setTxnStatus(txnStatus);
		}
		masterRepo.save(txnMaster);

		JSONObject response = new JSONObject();
		response.put(ServerConstants.TXN_REF, txnMaster.getTxnRefNo());
		response.put(ServerConstants.MESSAGE_HEADER_USER_ID, txnMaster.getUserId());
		response.put(ServerConstants.MESSAGE_HEADER_APP_ID, txnMaster.getAppId());
		response.put(ServerConstants.TXN_TYPE, txnMaster.getTxnType());
		response.put(ServerConstants.CUSTOMER_ID, txnMaster.getCustomerId());
		response.put(ServerConstants.AMOUNT, txnMaster.getAmount().toString());
		response.put(ServerConstants.CREATION_TS,txnMaster.getCreationTs());
		response.put(ServerConstants.TXN_STATUS, txnMaster.getTxnStatus());
		pMessage.getResponseObject().setResponseJson(response);
	}

	public void updateMicroAppTxnDetails(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Update MicroAppTxnDetails ...");
		JSONObject request = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.UPDATE_MICROAPP_TXN_DETAILS);
		//String appId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		//String userId = request.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
		//String custId = request.getString(ServerConstants.CUSTOMER_ID);
		//String amount = request.getString(ServerConstants.AMOUNT);
		//String txnType = request.getString(ServerConstants.TXN_TYPE);
		String txnRef = request.getString(ServerConstants.TXN_REF);
		String txnStatus = request.getString(ServerConstants.TXN_STATUS);
        String updateTsflag = "";
        if(request.has(ServerConstants.UPDATE_TS)&& 
        		Utils.isNotNullOrEmpty(request.getString(ServerConstants.UPDATE_TS))){
        	updateTsflag = request.getString(ServerConstants.UPDATE_TS);
        }
        
		TbDbtpTxnMaster txnMaster = masterRepo.findOne(txnRef);
		if (txnMaster != null) {
			//txnMaster.setUserId(userId);
			//txnMaster.setAppId(appId);
			//txnMaster.setTxnType(txnType);
			//txnMaster.setCustomerId(custId);
			//txnMaster.setAmount(new BigDecimal(amount));
			txnMaster.setTxnStatus(txnStatus);
			if (ServerConstants.YES.equals(updateTsflag)) {
				Timestamp tm = new Timestamp(new Date().getTime());
				txnMaster.setCompletionTs(tm);
			}
			masterRepo.save(txnMaster);
			//response.put(ServerConstants.STATUS,ServerConstants.SUCCESS);
			JSONObject response = new JSONObject();
			response.put(ServerConstants.TXN_REF, txnMaster.getTxnRefNo());
			response.put(ServerConstants.MESSAGE_HEADER_USER_ID, txnMaster.getUserId());
			response.put(ServerConstants.MESSAGE_HEADER_APP_ID, txnMaster.getAppId());
			response.put(ServerConstants.TXN_TYPE, txnMaster.getTxnType());
			response.put(ServerConstants.CUSTOMER_ID, txnMaster.getCustomerId());
			response.put(ServerConstants.AMOUNT, txnMaster.getAmount().toString());
			response.put(ServerConstants.CREATION_TS,txnMaster.getCreationTs());
			response.put(ServerConstants.TXN_STATUS,txnMaster.getTxnStatus());
			if (ServerConstants.YES.equals(updateTsflag)) {
				response.put(ServerConstants.COMPLETION_TS,txnMaster.getCompletionTs());
			}
			pMessage.getResponseObject().setResponseJson(response);
		} else {
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_068);
			lDomainException.setMessage(emsg);
			lDomainException.setCode(DomainException.Code.APZ_DM_068.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
	}

	public void fetchMicroAppTxnDetails(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetching MicroAppTxnDetails ...");
		JSONObject request = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.FETCH_MICROAPP_TXN_DETAILS);
		String txnRef = request.getString(ServerConstants.TXN_REF);
		TbDbtpTxnMaster txnMaster = masterRepo.findOne(txnRef);
		if (txnMaster != null) {
			JSONObject response = new JSONObject();
			response.put(ServerConstants.MESSAGE_HEADER_USER_ID, txnMaster.getUserId());
			response.put(ServerConstants.MESSAGE_HEADER_APP_ID, txnMaster.getAppId());
			response.put(ServerConstants.CUSTOMER_ID, txnMaster.getCustomerId());
			response.put(ServerConstants.AMOUNT, txnMaster.getAmount());
			response.put(ServerConstants.TXN_TYPE, txnMaster.getTxnType());
			response.put(ServerConstants.TXN_STATUS, txnMaster.getTxnStatus());
			response.put(ServerConstants.CREATION_TS, txnMaster.getCreationTs());
			response.put(ServerConstants.COMPLETION_TS, txnMaster.getCompletionTs());
			pMessage.getResponseObject().setResponseJson(response);
		} else {
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_068);
			lDomainException.setMessage(emsg);
			lDomainException.setCode(DomainException.Code.APZ_DM_068.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
	}
	

}
