package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.entity.TbAsmiAppMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiDeviceMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiDeviceMasterPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.handler.IHandler;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiAppMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiDeviceMasterRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiAppOsVersionRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

@Named(ServerConstants.SERVICE_APP_MASTER)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class AppMasterService {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
			AppMasterService.class.getName());

	@Inject
	private TbAsmiAppMasterRepository appMasterRepo;
	@Inject
	private TbAsmiAppOsVersionRepository appOsVersionRepo;
	@Inject
	private TbAsmiDeviceMasterRepository deviceMasterRepo;

	public void getAppMasterDetails(Message pMessage) {
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		JSONObject request = requestJson.getJSONObject(ServerConstants.APPZILLON_APP_MASTER_REQUEST);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside getAppMasterDetails - requestJson : " + requestJson);
		JSONObject parentJson = new JSONObject();
        String lAppId = "";
		if (!pMessage.getHeader().getDeviceId().equals(ServerConstants.WEB)) {
		    lAppId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String lOs = request.getString(ServerConstants.OS);
			String lDeviceId = request.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
			TbAsmiAppMaster appMaster = appMasterRepo.findAppMasterByAppId(lAppId);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "TbAsmiAppMaster : " + appMaster);
			// TbAsmiAppOsVersion appOsVersion =
			// appOsVersionRepo.findAppVersionByAppIdAndOS(lAppId, lOs);
			String appOsVersion = appOsVersionRepo.findMaxAppVersionByAppIdAndOS(lAppId, lOs);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "TbAsmiAppOsVersion : " + appOsVersion);
			if (appMaster != null && appOsVersion != null && !appOsVersion.isEmpty()) {
				// TbAsmiDeviceMasterPK id = new TbAsmiDeviceMasterPK(lAppId,
				// lDeviceId);
				// TbAsmiDeviceMaster deviceMaster =
				// deviceMasterRepo.findOne(id);
				TbAsmiDeviceMaster deviceMaster = getDeviceMaster(lAppId, lDeviceId);
				if (deviceMaster == null) {
					LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "No Record found in TbAsmiDeviceMaster table for this AppId - " + lAppId
							+ ", and DeviceId - " + lDeviceId);
					DomainException dexp = DomainException.getDomainExceptionInstance();
					dexp.setMessage("No Record found in TbAsmiDeviceMaster table for this AppId - " + lAppId
							+ ", and DeviceId - " + lDeviceId);
					dexp.setCode(DomainException.Code.APZ_DM_008.toString());
					dexp.setPriority("1");
					throw dexp;
				} else {
					parentJson.put(ServerConstants.WIPE_OUT, deviceMaster.getWipedOut());
				}

				Timestamp currentTime = new Timestamp(new Date().getTime());
				String expiryTimeFromDb = appMaster.getExpiryDate().toString();
				try {
					String currentTm = currentTime.toString();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long diff = formatter.parse(expiryTimeFromDb).getTime() - formatter.parse(currentTm).getTime();
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "Time Difference between current time and expiry time : " + diff);
					parentJson.put(ServerConstants.EXPIRED, (diff < 0) ? ServerConstants.YES : ServerConstants.NO);
				} catch (Exception e) {
					LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.EXCEPTION, e);
				}
				parentJson.put(ServerConstants.MESSAGE_HEADER_APP_ID, appMaster.getTbAsmiAppMasterPK().getAppId());
				parentJson.put(ServerConstants.APPVERSION, appOsVersion);
				parentJson.put(ServerConstants.CONTAINER_APP, appMaster.getContainerApp());
				parentJson.put(ServerConstants.OTA_REQUIRED, appMaster.getOtaReq());
				parentJson.put(ServerConstants.REMOTE_DEBUG, appMaster.getRemoteDebug());
				parentJson.put(ServerConstants.EXPIRY_DATE, expiryTimeFromDb.substring(0, 10));
				parentJson.put(ServerConstants.PARENT_APPID, appMaster.getParentAppId());

				pMessage.getResponseObject().setResponseJson(new JSONObject().put(lAppId, parentJson));
			} else {
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "No Record found for this appId and os in TbAsmiAppMaster or TbAsmiAppOsVersion table - "
						+ lAppId + ", " + lOs);
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(
						"No Record found for this appId and os in TbAsmiAppMaster or TbAsmiAppOsVersion table - "
								+ lAppId + ", " + lOs);
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				throw dexp;
			}
		}
		/*if (requestJson.getJSONObject(ServerConstants.APPZILLON_APP_MASTER_REQUEST)
				.has(ServerConstants.NONCE_VERIFICATION)
				&& requestJson.getJSONObject(ServerConstants.APPZILLON_APP_MASTER_REQUEST)
						.getString(ServerConstants.NONCE_VERIFICATION).equals("Y")) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To Nonce Handler...");
			IHandler domainHandler = (IHandler) DomainStartup.getInstance().getSpringContext()
					.getBean(pMessage.getHeader().getAppId() + "_nonceHandler");
			domainHandler.handleRequest(pMessage);
			if(!pMessage.getHeader().getDeviceId().equals(ServerConstants.WEB)){
				JSONObject json = pMessage.getResponseObject().getResponseJson();
				parentJson.put(ServerConstants.CS_NONCE_DETAILS, json.getJSONObject(ServerConstants.CS_NONCE_DETAILS));
				pMessage.getResponseObject().setResponseJson(new JSONObject().put(lAppId, parentJson));
			}
			
		}*/
	}

	@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
	private TbAsmiDeviceMaster getDeviceMaster(String pAppId, String pDeviceId) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "inside getDeviceMaster(), Device Master Details Will be fetched from Admin Meta.");
		TbAsmiDeviceMasterPK id = new TbAsmiDeviceMasterPK(pAppId, pDeviceId);
		TbAsmiDeviceMaster deviceMaster = deviceMasterRepo.findOne(id);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Response DeviceMaster : " + deviceMaster);
		return deviceMaster;
	}

}
