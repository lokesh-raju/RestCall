package com.iexceed.appzillon.domain.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.exception.Utils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiAppFiles;
import com.iexceed.appzillon.domain.entity.TbAsmiAppFilesPK;
import com.iexceed.appzillon.domain.entity.TbAsmiAppMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiAppMasterPK;
import com.iexceed.appzillon.domain.entity.TbAsmiAppUser;

import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiAppMasterRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiAppFileRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiAppOsVersionRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiAppUserRepository;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
/**
 * @author Ripu
 * This class is written for handling all the operation for OTA
 */
@Named(ServerConstants.SERVICE_OTA)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class OTAService {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, OTAService.class.getName());
	@Inject
	private TbAsmiAppUserRepository appUserRepo;
	@Inject
	private TbAsmiAppFileRepository appFileRepo;
	@Inject
	private TbAsmiAppMasterRepository appMasterRepo;
	@Inject
	private TbAsmiAppOsVersionRepository appOsVersionRepo;
	
	/**
	 * Below method written by ripu for fetching child app details, which are assigned to the particular user and master app
	 * @param pMessage
	 */
	public void getChildAppDetails(Message pMessage){
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside getChildAppDetails()- requestJson : "+ requestJson);
		JSONObject request = requestJson.getJSONObject("appzillonChildAppRequest");
		String lUserId = request.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
		String lParentAppId = request.getString(ServerConstants.PARENT_APPID);

		List<TbAsmiAppUser> appUserChildList = appUserRepo.findChildAppIdByUserIdandMasterAppId(lUserId, lParentAppId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "TbAsmiAppUser : "+ appUserChildList);
		
		if(appUserChildList.isEmpty()){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Child App Exist for user-id : "+ lUserId+", and parent app-id : "+lParentAppId);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No Child App Exist for user-id : "+ lUserId+", and parent app-id : "+lParentAppId);
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}else{
			JSONObject json = null;
			JSONArray mResponse = new JSONArray();
			for (TbAsmiAppUser tbAsmiAppUser : appUserChildList) {
				json = new JSONObject();
				json.put(ServerConstants.MESSAGE_HEADER_USER_ID, tbAsmiAppUser.getTbAsmiAppUserPK().getUserId());
				json.put(ServerConstants.PARENT_APPID, tbAsmiAppUser.getTbAsmiAppUserPK().getParentAppId());
				json.put(ServerConstants.CHILD_APP_ID, tbAsmiAppUser.getTbAsmiAppUserPK().getChildAppId());
				mResponse.put(json);
			}
			pMessage.getResponseObject().setResponseJson(new JSONObject().put("appzillonChildAppResponse", mResponse));
		}
	}
	
	public void getAppFileDetails(Message pMessage){
		JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside getAppFileDetails() - Request :: "+ mRequest);
		JSONObject jsonRequest = mRequest.getJSONObject("appzillonAppFilesRequest");
		String lAppId = jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String lAppVersion = jsonRequest.getString(ServerConstants.APPVERSION);
		String lOs = jsonRequest.getString(ServerConstants.OS);
		//TbAsmiAppOsVersion appOsVersion = appOsVersionRepo.findAppVersionByAppIdAndOS(lAppId, lOs);
		String appOsMaxVersion = appOsVersionRepo.findMaxAppVersionByAppIdAndOS(lAppId, lOs);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "TbAsmiAppOsVersion : "+appOsMaxVersion);
		if(appOsMaxVersion != null && !appOsMaxVersion.isEmpty()){
			List<TbAsmiAppFiles> otafileList = appFileRepo.findOTAFileBetweenVersion(lAppVersion, appOsMaxVersion, lAppId, lOs);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "otafileList : "+otafileList );
			List<TbAsmiAppFiles> otafileAllList = appFileRepo.findOTAByAppIdAndOS(lAppId, "ALL");
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "otafileAll : "+otafileAllList );
			JSONArray mResponse = new JSONArray();
			if(otafileList.isEmpty() && otafileAllList.isEmpty()){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No OTA file details found.");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("No OTA file details found.");
				dexp.setCode("APZ-DM-048");
				dexp.setPriority("1");
				throw dexp;
			}else{
				JSONObject json = new JSONObject();
				if(!otafileAllList.isEmpty()){
					for (TbAsmiAppFiles tbAsmiAppFiles : otafileAllList) {
						json = new JSONObject();
						json.put(ServerConstants.APPVERSION, tbAsmiAppFiles.getTbAsmiAppFilesPK().getAppVersion());
						json.put("filename", tbAsmiAppFiles.getTbAsmiAppFilesPK().getFileName());
						json.put(ServerConstants.FILE_PATH, tbAsmiAppFiles.getFilePath());
						json.put(ServerConstants.OS, tbAsmiAppFiles.getTbAsmiAppFilesPK().getOs());
						json.put(ServerConstants.ACTION, tbAsmiAppFiles.getAction());
						mResponse.put(json);
					}
				}
				if(!otafileList.isEmpty()){
					for (TbAsmiAppFiles obj : otafileList) {
						json = new JSONObject();
						json.put(ServerConstants.APPVERSION, obj.getTbAsmiAppFilesPK().getAppVersion());
						json.put("filename", obj.getTbAsmiAppFilesPK().getFileName());
						json.put(ServerConstants.FILE_PATH, obj.getFilePath());
						json.put(ServerConstants.OS, obj.getTbAsmiAppFilesPK().getOs());
						json.put(ServerConstants.ACTION, obj.getAction());
						mResponse.put(json);
					}
				}
			}
			JSONObject finalResponse = new JSONObject();
			finalResponse.put(lAppId, mResponse);
			finalResponse.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
			finalResponse.put(ServerConstants.APPVERSION, appOsMaxVersion);
			
			pMessage.getResponseObject().setResponseJson(finalResponse);
		} else {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Record found in TbAsmiAppOsVersion table for appId - "+lAppId+", and OS - "+lOs);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No Record found in TbAsmiAppOsVersion table for appId - "+lAppId+", and OS - "+lOs);
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	public void downloadFile(Message pMessage) {
		try {
			JSONObject body = pMessage.getRequestObject().getRequestJson();
			JSONObject mRequest = body.getJSONObject(ServerConstants.INTERFACE_ID_OTAFILE_DOWNLOADREQ);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request Received to Download the File for OTA : "+mRequest);
			//otaSourceLocation
			String rootpath = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.OTA_SOURCE_LOCATION).toString().trim();
			if (rootpath.endsWith("/")){
				rootpath = rootpath.substring(0, rootpath.lastIndexOf('/'));
			}else if (rootpath.endsWith("\\")){
				rootpath = rootpath.substring(0, rootpath.lastIndexOf('\\'));
			}
			String appId = mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String fileName = mRequest.getString(ServerConstants.REPORT_FILENAME);
			String osId = mRequest.getString(ServerConstants.OS);
			String appVersion = mRequest.getString(ServerConstants.APPVERSION);
			String lFilePath = mRequest.getString(ServerConstants.FILE_PATH);

			File file = new File(rootpath + "/" +osId+ "/" +appId+ "/" +appVersion+ "/" + lFilePath);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "File will be downloaded from the Location : " + rootpath + "/" +osId+ "/" +appId+ "/" +appVersion+ "/" + lFilePath);

			byte[] data = new byte[(int) file.length()];
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				fileInputStream.read(data);
				fileInputStream.close();
			} catch (IOException e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "File  not Found at Uploaded Location" , e);
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("File  not Found at Uploaded Location");
				dexp.setCode(DomainException.Code.APZ_DM_028.toString());
				dexp.setPriority("1");
				throw dexp;
			}
			String base64 = Base64.encodeBase64String(data);
			JSONObject mResponse = new JSONObject();
			JSONObject obj = new JSONObject();
			obj.put(ServerConstants.REPORT_FILENAME, fileName);
			obj.put("file", base64);
			obj.put("filePath",lFilePath);
			mResponse.put(ServerConstants.OTAFILEDOWNLOAD_RESPONSE, obj);

			pMessage.getResponseObject().setResponseJson(mResponse);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION , jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}

	}
	
	public void createAppFiles(Message pMessage){
		JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside createAppFiles() - Request :: "+ mRequest);
		JSONObject jsonRequest = mRequest.getJSONObject("createAppFileRequest");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "jsonRequest :: "+ jsonRequest);
		String status = null;
		TbAsmiAppFilesPK id = new TbAsmiAppFilesPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), jsonRequest.getString(ServerConstants.APPVERSION), jsonRequest.getString(ServerConstants.OS)
				, jsonRequest.getString(ServerConstants.REPORT_FILENAME));
		if(!appFileRepo.exists(id)){
			TbAsmiAppFiles asmiOTAfile = new TbAsmiAppFiles(); 
			asmiOTAfile.setTbAsmiAppFilesPK(id);
			asmiOTAfile.setFilePath(jsonRequest.getString(ServerConstants.FILE_PATH));
			asmiOTAfile.setAction(jsonRequest.getString(ServerConstants.ACTION));
			asmiOTAfile.setCreateUserId(pMessage.getHeader().getUserId());
			asmiOTAfile.setCreateTs(new Date());
			asmiOTAfile.setVersionNo(0);

			appFileRepo.save(asmiOTAfile);
			status = ServerConstants.SUCCESS;
		}else{
			status = ServerConstants.FAILURE;
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record already exists");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_015));
			dexp.setCode(DomainException.Code.APZ_DM_015.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		JSONObject response = new JSONObject();
		response.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
		pMessage.getResponseObject().setResponseJson(new JSONObject().put("createAppFileResponse", response));
	}
	/**
	 * Below method written by ripu on 12-12-2014 for updating files
	 * @param pMessage
	 */
	public void updateAppFiles(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside updateAppFiles()..");
		JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request :: "+ mRequest);
		JSONObject jsonRequest = mRequest.getJSONObject("updateAppFileRequest");
		String status = null;
		TbAsmiAppFilesPK id = new TbAsmiAppFilesPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), jsonRequest.getString(ServerConstants.APPVERSION), jsonRequest.getString(ServerConstants.OS)
				, jsonRequest.getString("fileName"));
		TbAsmiAppFiles asmiOTAfile = appFileRepo.findOne(id);

		if(asmiOTAfile != null){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "File Exists..");
			appFileRepo.delete(id);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "File Deleted");
			TbAsmiAppFilesPK pk = new TbAsmiAppFilesPK();
			pk.setAppId(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			pk.setAppVersion(jsonRequest.getString(ServerConstants.APPVERSION));
			pk.setOs(jsonRequest.getString(ServerConstants.OS));
			pk.setFileName(jsonRequest.getString("newFileName"));
			
			TbAsmiAppFiles obj = new TbAsmiAppFiles(); 
			obj.setTbAsmiAppFilesPK(pk);
			obj.setFilePath(jsonRequest.getString(ServerConstants.FILE_PATH));
			obj.setAction(jsonRequest.getString(ServerConstants.ACTION));
			obj.setCreateUserId(pMessage.getHeader().getUserId());
			obj.setCreateTs(new Date());
			obj.setVersionNo(asmiOTAfile.getVersionNo() + 1);

			appFileRepo.save(obj);
			status = ServerConstants.SUCCESS;
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record Updated in TbAsmiAppFiles table..");
		}else{
			status = ServerConstants.FAILURE;
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record does not exist");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		JSONObject response = new JSONObject();
		response.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
		pMessage.getResponseObject().setResponseJson(new JSONObject().put("updateAppFileResponse", response));
	
	}
	
	/**
	 * Below method written by ripu on 12-12-2014 for searching files
	 * @param pMessage
	 */
	public void searchAppFiles(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside searchAppFiles()..");
		JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request :: "+ mRequest);
		JSONObject jsonRequest = mRequest.getJSONObject("searchAppFileRequest");
		String lAppId = jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String lOs = jsonRequest.getString(ServerConstants.OS);
		
		List<TbAsmiAppFiles> appFileList = null;
		if(Utils.isNullOrEmpty (lAppId) && (Utils.isNullOrEmpty (lOs) || "ALL".equalsIgnoreCase(lOs))){
			appFileList = appFileRepo.findAll();
		}else if (Utils.isNullOrEmpty (lAppId) && Utils.isNotNullOrEmpty (lOs)){
			appFileList = appFileRepo.findOTAByOS(lOs);
		}else if(Utils.isNotNullOrEmpty (lAppId) && Utils.isNotNullOrEmpty (lOs)){
			appFileList = appFileRepo.findOTAByAppIdAndOS(lAppId, lOs);
		}
		if(appFileList.isEmpty() ){
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No OTA file details found.");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No OTA file details found. ");
			dexp.setCode("APZ-DM-048");
			dexp.setPriority("1");
			throw dexp;
		}else{
			JSONArray mResponse = new JSONArray();
			JSONObject json = new JSONObject();
			for(int a=0; a < appFileList.size(); a++){
				TbAsmiAppFiles otafileAll = (TbAsmiAppFiles)appFileList.get(a);
				json = new JSONObject();
				json.put(ServerConstants.MESSAGE_HEADER_APP_ID, otafileAll.getTbAsmiAppFilesPK().getAppId());
				json.put(ServerConstants.APPVERSION, otafileAll.getTbAsmiAppFilesPK().getAppVersion());
				json.put(ServerConstants.OS, otafileAll.getTbAsmiAppFilesPK().getOs());
				json.put("filename", otafileAll.getTbAsmiAppFilesPK().getFileName());
				json.put(ServerConstants.FILE_PATH, otafileAll.getFilePath());
				json.put(ServerConstants.ACTION, otafileAll.getAction());
				
				mResponse.put(json);
			}
			JSONObject finalResponse = new JSONObject();
			finalResponse.put("searchAppFileResponse", mResponse);
			finalResponse.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
			pMessage.getResponseObject().setResponseJson(finalResponse);
		}
	}
	
	/**
	 * Below method written by ripu on 12-12-2014 for deleting files
	 * @param pMessage
	 */
	public void deleteAppFiles(Message pMessage){
		JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside deleteAppFiles() - Request :: "+ mRequest);
		JSONObject jsonRequest = null;
		try{
			if(mRequest.get("deleteAppFileRequest") instanceof JSONArray){
				JSONArray jsonArray = mRequest.getJSONArray("deleteAppFileRequest");
				for(int i = 0; i<jsonArray.length(); i++){
					jsonRequest = jsonArray.getJSONObject(i);
					deleteAppFilesRequest(jsonRequest);
				}
			}else if(mRequest.get("deleteAppFileRequest") instanceof JSONObject){
				jsonRequest = mRequest.getJSONObject("deleteAppFileRequest");
				deleteAppFilesRequest(jsonRequest);
			}
			JSONObject response = new JSONObject();
			response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			pMessage.getResponseObject().setResponseJson(new JSONObject().put("deleteAppFileResponse", response));
		}catch(JSONException json){
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}
		
	private boolean deleteAppFilesRequest(JSONObject pRequest){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside deleteAppFilesRequest() - Request :: "+ pRequest);
		boolean status = false;
		TbAsmiAppFilesPK id = new TbAsmiAppFilesPK(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), pRequest.getString(ServerConstants.APPVERSION), pRequest.getString(ServerConstants.OS)
				, pRequest.getString(ServerConstants.REPORT_FILENAME));
		if(appFileRepo.exists(id)){
			appFileRepo.delete(id);
			status = true;
		}else{
			status = false;
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record does not exist");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		return status;
	}
	
	
	/**
	 * Below service written by ripu on 11-12-2014 for creating app-master
	 * @param pMessage
	 */
	@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
	public void createAppMaster(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside createAppMaster()..");
		JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request :: "+ mRequest);
		JSONObject jsonRequest = mRequest.getJSONObject("createAppMasterRequest");
		String status = ServerConstants.FAILURE;
		String appName = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.APP_NAME);
		String appVersion = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.APPVERSION);
		String ideVersion = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.IDE_VERSION);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");//("yyyy-MM-dd HH:mm:ss"); this code is commented by ripu because date was coming in (2015-12-30) format
		Date expiryDate = null;
		try{
			expiryDate = formatter.parse(jsonRequest.getString(ServerConstants.EXPIRY_DATE));
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Expiry Date : "+ expiryDate);
		}catch(Exception e){
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Date Formating Error", e);
		}
		/**
		 * Below changes done by ripu
		 * parentId is made nullable and not primary key on 12-03-2015. Since the Appzillon IDE will send parentId value null if
		 * parentId is not available.
		 * 
		 * previous code was "TbAsmiAppMasterPK id = new TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), jsonRequest.getString(ServerConstants.PARENT_APPID));"
		 * when parentId was primaryKey
		 * 
		 * Now it is changed to "TbAsmiAppMasterPK id = new TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));"
		 */
		TbAsmiAppMasterPK id = new TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
		appName = (appName != null && !appName.isEmpty()) ? appName : jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		appVersion = (appVersion != null && !appVersion.isEmpty()) ? appVersion : appName;
		ideVersion = (ideVersion != null && !ideVersion.isEmpty()) ? ideVersion : appName;
		if(! appMasterRepo.exists(id)){
			TbAsmiAppMaster obj = new TbAsmiAppMaster();
			obj.setTbAsmiAppMasterPK(id);
			obj.setParentAppId(jsonRequest.getString(ServerConstants.PARENT_APPID));
			obj.setAppName(appName);
			obj.setContainerApp(jsonRequest.getString(ServerConstants.CONTAINER_APP));
			obj.setExpiryDate(expiryDate);
			obj.setOtaReq(jsonRequest.getString(ServerConstants.OTA_REQUIRED));
			obj.setRemoteDebug(jsonRequest.getString(ServerConstants.REMOTE_DEBUG));
			obj.setDefaultLanguage(jsonRequest.getString(ServerConstants.DEFAULTLANGUAGE));
			obj.setMicroAppType(JSONUtils.getJsonValueFromObject(jsonRequest,ServerConstants.MICRO_APP_TYPE));
			obj.setAppVersion(appVersion);
			obj.setIdeVersion(ideVersion);
			obj.setCreateTs(new Date());
			obj.setCreateUserId(pMessage.getHeader().getUserId());
			appMasterRepo.save(obj);
			status = ServerConstants.SUCCESS;
		}else{
			status = ServerConstants.FAILURE;
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record already exists");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_015));
			dexp.setCode(DomainException.Code.APZ_DM_015.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		JSONObject response = new JSONObject();
		response.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
		pMessage.getResponseObject().setResponseJson(new JSONObject().put("createAppMasterResponse", response));
	}
	
	/**
	 * Below service written by ripu on 15-12-2014 for updating app-master
	 * @param pMessage
	 */
	@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
	public void updateAppMaster(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside updateAppMaster()..");
		JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request :: "+ mRequest);
		JSONObject jsonRequest = mRequest.getJSONObject("updateAppMasterRequest");
		String status = ServerConstants.FAILURE;
		String appName = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.APP_NAME);
		String appVersion = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.APPVERSION);
		String ideVersion = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.IDE_VERSION);
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date expiryDate = null;
		try{
			expiryDate = formatter.parse(jsonRequest.getString(ServerConstants.EXPIRY_DATE));
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Expiry Date : "+ expiryDate);
		}catch(Exception e){
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Date Formating Error", e);
		}
		/**
		 * Below changes done by ripu
		 * parentId is made nullable and not primary key on 12-03-2015. Since the Appzillon IDE will send parentId value null if
		 * parentId is not available.
		 * 
		 * previous code was "TbAsmiAppMasterPK id = new TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), jsonRequest.getString(ServerConstants.PARENT_APPID));"
		 * when parentId was primaryKey
		 * 
		 * Now it is changed to "TbAsmiAppMasterPK id = new TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));"
		 */
		TbAsmiAppMasterPK id = new TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
		TbAsmiAppMaster obj = appMasterRepo.findOne(id);
		appName = (appName != null && !appName.isEmpty()) ? appName : jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		appVersion = (appVersion != null && !appVersion.isEmpty()) ? appVersion : appName;
		ideVersion = (ideVersion != null && !ideVersion.isEmpty()) ? ideVersion : appName;
		if(obj != null){
			obj = new TbAsmiAppMaster();
			obj.setTbAsmiAppMasterPK(id);
			obj.setParentAppId(jsonRequest.getString(ServerConstants.PARENT_APPID));
			obj.setContainerApp(jsonRequest.getString(ServerConstants.CONTAINER_APP));
			obj.setAppName(appName);
			obj.setExpiryDate(expiryDate);
			obj.setOtaReq(jsonRequest.getString(ServerConstants.OTA_REQUIRED));
			obj.setRemoteDebug(jsonRequest.getString(ServerConstants.REMOTE_DEBUG));
			obj.setDefaultLanguage(jsonRequest.getString(ServerConstants.DEFAULTLANGUAGE));
			obj.setVersionNo(obj.getVersionNo() + 1);
			obj.setMicroAppType(JSONUtils.getJsonValueFromObject(jsonRequest,ServerConstants.MICRO_APP_TYPE));
			obj.setAppVersion(appVersion);
			obj.setIdeVersion(ideVersion);
			obj.setCreateTs(new Date());
			obj.setCreateUserId(pMessage.getHeader().getUserId());
			appMasterRepo.save(obj);
			status = ServerConstants.SUCCESS;
		}else{
			status = ServerConstants.FAILURE;
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record does not exist");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		JSONObject response = new JSONObject();
		response.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
		pMessage.getResponseObject().setResponseJson(new JSONObject().put("updateAppMasterResponse", response));
	}
	
	/**
	 * Below service written by ripu on 08-12-2014 for searching app-master
	 * @param pMessage
	 */
	public void searchAppMaster(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside searchAppMaster()..");
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "requestJson : "+ requestJson);
		JSONObject request = requestJson.getJSONObject("appzillonSearchAppMasterRequest");
		String lAppId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID); 
		List<TbAsmiAppMaster> objList = null;
		if(Utils.isNullOrEmpty (lAppId)){
			objList = appMasterRepo.findAll();
		}else{
			objList = appMasterRepo.findAppMasterByAppIdinList(lAppId);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "TbAsmiAppMaster : "+ objList);
		JSONArray mResponse = new JSONArray();
		if(!objList.isEmpty()){
			for(int i=0; i < objList.size(); i++){
				TbAsmiAppMaster appMaster = objList.get(i);
				JSONObject json = new JSONObject();
				json.put(ServerConstants.MESSAGE_HEADER_APP_ID, appMaster.getTbAsmiAppMasterPK().getAppId());
				json.put(ServerConstants.CONTAINER_APP, appMaster.getContainerApp());
				json.put(ServerConstants.OTA_REQUIRED, appMaster.getOtaReq());
				json.put(ServerConstants.REMOTE_DEBUG, appMaster.getRemoteDebug());
				json.put(ServerConstants.EXPIRY_DATE, appMaster.getExpiryDate());
				json.put(ServerConstants.PARENT_APPID, appMaster.getParentAppId());
				json.put(ServerConstants.DEFAULTLANGUAGE, appMaster.getDefaultLanguage());
				mResponse.put(json);
			}
			pMessage.getResponseObject().setResponseJson(new JSONObject().put("appzillonSearchAppMasterResponse", mResponse));
		}else{
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Record found for this appId in TbAsmiAppMaster table - "+lAppId);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No Record found for this appId in TbAsmiAppMaster table - "+lAppId);
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}
	
	/**
	 * Below service written by ripu on 15-12-2014 for updating app-master
	 * @param pMessage
	 */
	@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
	public void deleteAppMaster(Message pMessage){
		JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside deleteAppMaster() - Request :: "+ mRequest);
		JSONObject jsonRequest = null;
		try{
		if(mRequest.get("deleteAppMasterRequest") instanceof JSONArray){
			JSONArray jsonArray = mRequest.getJSONArray("deleteAppMasterRequest");
			for(int i = 0; i<jsonArray.length(); i++){
				jsonRequest = jsonArray.getJSONObject(i);
				deleteAppMasterRequest(jsonRequest);
			}
		}else if(mRequest.get("deleteAppMasterRequest") instanceof JSONObject){
			jsonRequest = mRequest.getJSONObject("deleteAppMasterRequest");
			deleteAppMasterRequest(jsonRequest);
		}
		JSONObject response = new JSONObject();
		response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
		pMessage.getResponseObject().setResponseJson(new JSONObject().put("deleteAppMasterResponse", response));
		}catch(JSONException json){
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	private boolean deleteAppMasterRequest(JSONObject pJsonRequest){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside deleteAppMasterRequest() - Request :: "+ pJsonRequest);
		boolean status = false;
		TbAsmiAppMasterPK id = new TbAsmiAppMasterPK(pJsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
		if(appMasterRepo.exists(id)){
			appMasterRepo.delete(id);
			status = true;
		}else{
			status = false;
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record does not exist");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		return status;
	}
	
	public void fetchWelcomeMsg(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Inside fetchWelcomeMsg()");
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_CNVUI_WELCOME_MSG_REQUEST);
		String appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		TbAsmiAppMaster tbAsmiAppMaster = appMasterRepo.findAppMasterByAppId(appId);
		String message = tbAsmiAppMaster.getWelcomeMsg();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Welcome message : " + message);
		JSONObject responseJson = new JSONObject();
		responseJson.put(ServerConstants.MESSAGE, message);
		pMessage.getResponseObject().getResponseJson().put(ServerConstants.APPZILLON_ROOT_GET_CNVUI_WELCOME_MSG_RESPONSE,
				responseJson);
	}
	
}
