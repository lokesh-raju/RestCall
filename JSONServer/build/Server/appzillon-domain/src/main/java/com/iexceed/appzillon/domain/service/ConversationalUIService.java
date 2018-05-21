package com.iexceed.appzillon.domain.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScr;
import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScrPK;
import com.iexceed.appzillon.domain.entity.TbAsmiDlgMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiDlgMasterPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiCnvUIScrRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiDlgMasterRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

@Named(ServerConstants.SERVICE_CONVERSATIONAL_UI)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class ConversationalUIService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
			ConversationalUIService.class.toString());

	@Inject
	private TbAsmiDlgMasterRepository cAsmiDlgMasterRepository;

	@Inject
	private TbAsmiCnvUIScrRepository cAsmiCnvUIScrRepository;

	public void fetchFirstCnvUIDlg(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " fetching first conversationaUI dialogue");
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_REQUEST);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " requestJson in fetchFirstCnvUIDlg : " + requestJson);
		String appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String cnvUIId = requestJson.getString(ServerConstants.APPZILLON_CNVUI_ID);
		TbAsmiDlgMaster asmiDlgMaster = cAsmiDlgMasterRepository.findFirstDlg(appId, cnvUIId);
		String scrId = asmiDlgMaster.getScreenId();
		JSONObject screenDetails = fetchScreenDet(appId, scrId);
		JSONObject responseJson = new JSONObject();
		responseJson.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
		responseJson.put(ServerConstants.APPZILLON_CNVUI_ID, cnvUIId);
		responseJson.put(ServerConstants.DLG_ID, asmiDlgMaster.getId().getDlgId());
		responseJson.put(ServerConstants.RESP_DLG_ID, asmiDlgMaster.getRespDlgId());
		responseJson.put(ServerConstants.DLG_DESC, asmiDlgMaster.getDlgDesc());
		responseJson.put(ServerConstants.SCREEN_DETAILS, screenDetails);
		pMessage.getResponseObject().getResponseJson().put(ServerConstants.APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_RESPONSE,
				responseJson);
		cnvUITxnLog(pMessage);

	}

	public void fetchNextCnvUIDlg(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " fetching next conversationaUI dialogue");
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_CNVUI_DLG_REQUEST);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " requestJson in fetchNextCnvUIDlg : " + requestJson);
		String appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String cnvUIId = requestJson.getString(ServerConstants.APPZILLON_CNVUI_ID);
		String dlgId = requestJson.getString(ServerConstants.DLG_ID);
		String txnRef = requestJson.getString(ServerConstants.TXN_REF);
		TbAsmiDlgMaster asmiDlgMaster;
		if(requestJson.has(ServerConstants.DLG_ID_FROM_RULE_BEAN)) {
			asmiDlgMaster = cAsmiDlgMasterRepository.findOne(new TbAsmiDlgMasterPK(appId, cnvUIId, 
					requestJson.getString(ServerConstants.DLG_ID_FROM_RULE_BEAN)));
			requestJson.remove(ServerConstants.DLG_ID_FROM_RULE_BEAN);
		} else {
			asmiDlgMaster = cAsmiDlgMasterRepository.findNextDlg(appId, cnvUIId, dlgId);
		}
		String scrId = asmiDlgMaster.getScreenId();
		JSONObject screenDetails = fetchScreenDet(appId, scrId);
		JSONObject responseJson = new JSONObject();
		responseJson.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
		responseJson.put(ServerConstants.APPZILLON_CNVUI_ID, cnvUIId);
		responseJson.put(ServerConstants.TXN_REF, txnRef);
		responseJson.put(ServerConstants.DLG_ID, asmiDlgMaster.getId().getDlgId());
		responseJson.put(ServerConstants.RESP_DLG_ID, asmiDlgMaster.getRespDlgId());
		responseJson.put(ServerConstants.DLG_DESC, asmiDlgMaster.getDlgDesc());
		responseJson.put(ServerConstants.SCREEN_DETAILS, screenDetails);
		pMessage.getResponseObject().getResponseJson().put(ServerConstants.APPZILLON_ROOT_GET_CNVUI_DLG_RESPONSE,
				responseJson);
		cnvUITxnLog(pMessage);
	}

	private JSONObject fetchScreenDet(String appId, String scrId) {
		TbAsmiCnvUIScr asmiCnvUIScr = cAsmiCnvUIScrRepository.findOne(new TbAsmiCnvUIScrPK(appId, scrId));
		JSONObject screenDetails = new JSONObject();
		screenDetails.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, scrId);
		screenDetails.put(ServerConstants.SCREEN_DEF, asmiCnvUIScr.getScreenDef());
		screenDetails.put(ServerConstants.SCREEN_HTML, asmiCnvUIScr.getScreenHtml());
		return screenDetails;
	}

	private void cnvUITxnLog(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + " Logging cnv ui Transaction");
        pMessage.getHeader().setServiceType(ServerConstants.CNVUI_LOG_TRANSACTION);
        try{
        	DomainStartup.getInstance().processRequest(pMessage);
        } catch(Exception e) {	
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " cnv UI txn log failed");
			DomainException lException = DomainException.getDomainExceptionInstance();
			lException.setCode(DomainException.Code.APZ_DM_070.toString());
			lException.setMessage(lException.getDomainExceptionMessage(DomainException.Code.APZ_DM_070));
			lException.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ ServerConstants.EXCEPTION, e);
			throw lException;
		}
        pMessage.getHeader().setServiceType("");
	}

}
