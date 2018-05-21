package com.iexceed.appzillon.domain.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.exception.Utils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsfsFileDetails;
import com.iexceed.appzillon.domain.entity.TbAsfsFileDetailsId;
import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.TbAsmiUserRole;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiRoleIntfRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRoleRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsfsFileDetailsRepository;
import com.iexceed.appzillon.domain.spec.FileSpecification;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.utils.ServerConstants;

@Named("appzillonFileService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class FileService {
	private Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
			FileService.class.toString());
	@Inject
	private TbAsfsFileDetailsRepository fileDetailsRepo;

	public void createFile(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside createFile().");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "interfaceId : " + pMessage.getHeader().getInterfaceId());
		boolean userAuthStatus;
		if (ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH.equals(pMessage.getHeader().getInterfaceId())) {
			userAuthStatus = checkForAuthorization(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "User Auth Status : " + userAuthStatus);
		}
		JSONObject mRequest = null;
		JSONObject mResponse = null;
		try {
			LOG.info("Creating file Entry in database");
			String path = PropertyUtils
					.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.FILE_UPLOAD_LOCATION).toString()
					.trim();
			if (path.endsWith("\\"))
				path = path.substring(0, path.lastIndexOf('\\'));
			if (path.endsWith("/"))
				path = path.substring(0, path.lastIndexOf('/'));
			JSONObject p_body = pMessage.getRequestObject().getRequestJson();
			mRequest = p_body.getJSONObject(ServerConstants.UPLOADFILEREQUEST);
			JSONArray fileArray = mRequest.getJSONArray("Files");
			String createUserId = pMessage.getHeader().getUserId();
			JSONObject response = new JSONObject();
			String iface = pMessage.getHeader().getInterfaceId();
			for (int i = 0; i < fileArray.length(); i++) {

				String appId = fileArray.getJSONObject(i).get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();
				String screenId = fileArray.getJSONObject(i).get(ServerConstants.MESSAGE_HEADER_SCREEN_ID).toString();
				String userId = fileArray.getJSONObject(i).get(ServerConstants.MESSAGE_HEADER_USER_ID).toString();
				String fileName = fileArray.getJSONObject(i).get(ServerConstants.REPORT_FILENAME).toString();
				String fileExtn = fileArray.getJSONObject(i).get(ServerConstants.REPORT_FILE_TYPE).toString();
				String overrideFlag = fileArray.getJSONObject(i).get(ServerConstants.OVERRIDEFLAG).toString();
				File f = new File(path + "/" + fileName);
				LOG.info("Looking file at loc:" + path + "/" + fileName);
				if(fileName.contains("/")) {
					fileName = fileName.substring(fileName.lastIndexOf("/")+1, fileName.length());
				} else if(fileName.contains("\\")) {
					fileName = fileName.substring(fileName.lastIndexOf("\\")+1, fileName.length());
				}
				if (f.exists()) {
					LOG.info("File exist at location");

					if (screenId.isEmpty())
						screenId = ServerConstants.NULL;

					if (userId.isEmpty())
						userId = ServerConstants.NULL;
					LOG.debug("screenId after removing empty" + screenId);
					LOG.debug("after removing empty userId" + userId);
					TbAsfsFileDetails filedetail = this.getFile(appId, fileName);
					TbAsfsFileDetailsId recordId = new TbAsfsFileDetailsId();
					TbAsfsFileDetails record = null;
					Timestamp t = new Timestamp(new Date().getTime());
					if (filedetail == null) {
						LOG.info("file details not found so creating new record");
						recordId = new TbAsfsFileDetailsId();
						record = new TbAsfsFileDetails();
						recordId.setAppId(appId);
						recordId.setFileName(fileName);
						record.setId(recordId);
						record.setUserId(userId);
						record.setScreenId(screenId);
						record.setFileType(fileExtn);
						record.setInterfaceId(iface);
						record.setLastUploadedDate(t);
						record.setRecentUploadedDate(t);
						record.setCreateUserId(createUserId);

						fileDetailsRepo.save(record);
						response.put(fileName, ServerConstants.SUCCESS);
					} else if (ServerConstants.NO.equalsIgnoreCase(overrideFlag)) {
						LOG.info("file details found");
						response.put(fileName, "Already Exists");
					} else {
						LOG.info("file details found will update in db");
						LOG.info("file details found");
						filedetail.setUserId(userId);
						filedetail.setScreenId(screenId);
						filedetail.setFileType(fileExtn);
						filedetail.setInterfaceId(iface);
						filedetail.setLastUploadedDate(t);
						filedetail.setRecentUploadedDate(t);
						filedetail.setCreateUserId(createUserId);
						fileDetailsRepo.save(filedetail);
						response.put(fileName, ServerConstants.SUCCESS);
					}
				} else {

					if (!fileArray.getJSONObject(i).has(ServerConstants.MESSAGE_HEADER_STATUS))
						response.put(fileName, ServerConstants.FAILURE);
					else
						response.put(fileName,
								fileArray.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_STATUS));
				}
			}
			mResponse = new JSONObject();
			if (!iface.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS)) {
				mResponse.put(ServerConstants.UPLOADFILERESPONSE, response);
			} else {
				mResponse.put(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS + "Response", response);
			}
			pMessage.getResponseObject().setResponseJson(mResponse);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}

	}

	public String deleteFile(Message pMessage) {
		JSONObject response = null;
		try {
			LOG.info("Deleting file");
			JSONObject body = pMessage.getRequestObject().getRequestJson();
			response = new JSONObject();

			String path = PropertyUtils
					.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.FILE_UPLOAD_LOCATION).toString()
					.trim();
			if (path.endsWith("/"))
				path = path.substring(0, path.lastIndexOf('/'));
			if (path.endsWith("\\"))
				path = path.substring(0, path.lastIndexOf('\\'));
			LOG.info("Deleting file at location " + path);
			String userId = body.get(ServerConstants.MESSAGE_HEADER_USER_ID).toString();
			String appId = body.get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();
			String screenId = body.get(ServerConstants.MESSAGE_HEADER_SCREEN_ID).toString();
			String fileName = body.get(ServerConstants.REPORT_FILENAME).toString();

			String appendPath = "";
			String pathappID = appId.replace('.', '_');
			if (Utils.isNotNullOrEmpty(pathappID))
				appendPath += pathappID + "/";
			String pathscreenID = screenId.replace('.', '_');
			if (Utils.isNotNullOrEmpty(pathscreenID))
				appendPath += pathscreenID + "/";
			String pathuserID = userId.replace('.', '_');
			if (Utils.isNotNullOrEmpty(pathuserID))
				appendPath += pathuserID + "/";

			if (Utils.isNullOrEmpty (screenId))
				screenId = ServerConstants.NULL;

			if (Utils.isNullOrEmpty (userId))
				userId = ServerConstants.NULL;
			TbAsfsFileDetails filedetail = this.getFile(appId, fileName);
			if (filedetail != null) {
				LOG.info("Deleting  file at root location :" + appendPath + fileName);
				fileDetailsRepo.delete(filedetail);
				File folder = new File(path + "/" + appendPath + fileName);
				try {

					this.delete(folder);
				} catch (IOException e) {
					LOG.error("IOException", e);
					e.printStackTrace();
				}
				response.put(appendPath + fileName, ServerConstants.SUCCESS);
			} else {
				response.put(appendPath + fileName, "file record not found");
			}
			return response.toString();
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	public void searchFile(Message pMessage) {
		JSONObject mResponse = null;
		JSONObject mRequest = null;
		try {
			LOG.info("Searching file");
			JSONObject body = pMessage.getRequestObject().getRequestJson();
			mRequest = body.getJSONObject(ServerConstants.SEARCHFILEREQUEST);
			String appId = mRequest.get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();
			if (Utils.isNullOrEmpty(appId) ) {
				LOG.warn("Search columns values not found");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("Column value can not be null or blank");
				dexp.setCode(DomainException.Code.APZ_DM_009.toString());
				dexp.setPriority("1");
				throw dexp;
			}
			final String fappId;
			if (Utils.isNullOrEmpty(appId))
				fappId = "%";
			else
				fappId = appId;

			List<TbAsfsFileDetails> reslist = new ArrayList<TbAsfsFileDetails>();
			reslist = fileDetailsRepo.findAll(FileSpecification
					.likeAppId(fappId), new Sort(Direction.DESC,
							"recentUploadedDate"));
			if (reslist.isEmpty()) {
				LOG.warn("No record found ");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("No record Found");
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				throw dexp;
			}
			JSONArray ARR = new JSONArray();
			int i = 0;
			while (i < reslist.size()) {
				JSONObject obj = new JSONObject();
				obj.put(ServerConstants.MESSAGE_HEADER_APP_ID, reslist.get(i).getId().getAppId());
				String screenId = reslist.get(i).getScreenId();
				if (ServerConstants.NULL.equals(screenId))
					screenId = "";
				obj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, screenId);
				String userId = reslist.get(i).getUserId();
				if (ServerConstants.NULL.equals(userId))
					userId = "";
				obj.put(ServerConstants.MESSAGE_HEADER_USER_ID, userId);

				obj.put(ServerConstants.REPORT_FILENAME, reslist.get(i)
						.getId().getFileName());
				obj.put(ServerConstants.REPORT_FILE_TYPE, reslist.get(i)
						.getFileType());
				obj.put("lastUpload", reslist.get(i).getLastUploadedDate());
				obj.put("recentUpload", reslist.get(i).getRecentUploadedDate());
				ARR.put(obj);
				i++;
			}
			JSONObject res = new JSONObject();
			res.put("Files", ARR);
			mResponse = new JSONObject();
			mResponse.put(ServerConstants.SEARCHFILERESPONSE, res);
			pMessage.getResponseObject().setResponseJson(mResponse);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	public TbAsfsFileDetails getFile(String appId, String fileName) {
		TbAsfsFileDetailsId id = new TbAsfsFileDetailsId();
		id.setAppId(appId);
		id.setFileName(fileName);
		TbAsfsFileDetails record = fileDetailsRepo.findOne(id);
		return record;
	}

	public void deleteFileRequest(Message pMessage) {
		JSONObject mRequest = null;
		JSONObject mResponse = null;
		try {
			LOG.info("Files Delete Request recieved");
			JSONObject body = pMessage.getRequestObject().getRequestJson();
			mRequest = body.getJSONObject(ServerConstants.DELETEFILEREQUEST);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		String colstring = JSONUtils.getColStringFromBody(pMessage.getHeader(), mRequest.toString(), "Files");
		LOG.info("colstring to delete" + colstring);
		JSONArray finalStatus = null;
		String status = "";
		JSONObject jsonobj = null;
		JSONArray jsonarr = null;
		try {
			finalStatus = new JSONArray();
			if (colstring.substring(0, 1).equals("{")) {
				jsonobj = new JSONObject(colstring);
			} else if (colstring.substring(0, 1).equals("[")) {
				jsonarr = new JSONArray(colstring);
			}
			if (jsonobj != null) {
				status = this.deleteFile(pMessage);
				finalStatus.put(new JSONObject(status));
			} else if (jsonarr != null) {
				LOG.info("Array of files found");
				for (int i = 0; i < jsonarr.length(); i++) {
					pMessage.getRequestObject().setRequestJson(jsonarr.getJSONObject(i));
					//status = this.deleteFile(headerMap, jsonarr.getJSONObject(i).toString());
					status = this.deleteFile(pMessage);
					finalStatus.put(new JSONObject(status));
				}
			}
			mResponse = new JSONObject();
			mResponse.put(ServerConstants.DELETEFILERESPONSE, finalStatus);
			pMessage.getResponseObject().setResponseJson(mResponse);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	public void delete(File file) throws IOException {
		if (file.isDirectory()) {
			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
			} else {
				// list all the directory contents
				String files[] = file.list();
				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);
					// recursive delete
					this.delete(fileDelete);
				}
				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
				}
			}
		} else {
			// if file, then delete it
			file.delete();
		}
	}

	public void pushFile(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside pushFile");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "interfaceId : " + pMessage.getHeader().getInterfaceId());
		boolean userAuthStatus;
		if (ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH.equals(pMessage.getHeader().getInterfaceId())) {
			userAuthStatus = checkForAuthorization(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "User Auth Status : " + userAuthStatus);
		}
		JSONObject mRequest = null;
		JSONObject mResponse = null;
		String base64 = null;
		JSONObject body = pMessage.getRequestObject().getRequestJson();
		if (body.has(ServerConstants.FILEPUSHSERVICEREQUEST)) {
			mRequest = body.getJSONObject(ServerConstants.FILEPUSHSERVICEREQUEST);
		} else if (body.has(ServerConstants.FILEPUSHSERVICEWSREQUEST)) {
			mRequest = body.getJSONObject(ServerConstants.FILEPUSHSERVICEWSREQUEST);
		}
		String fileName = mRequest.get(ServerConstants.REPORT_FILENAME).toString();
		String filePath = mRequest.get(ServerConstants.FILEPATH).toString();
		if (mRequest.getString(ServerConstants.BASE64STATUS).equalsIgnoreCase(ServerConstants.NO)) {
			LOG.debug("Multipart file download");
			LOG.debug("Filename in request body :: " + fileName);

			String lfileName = "";
			if (fileName.contains("/")) {
				lfileName = fileName.substring(fileName.lastIndexOf("/"));
			} else {
				lfileName = fileName;
			} 
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " File Download Request File Name -:" + lfileName);
			String fileType = fileName.substring(fileName.lastIndexOf("."));
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " File Download Request File Type -:" + fileType);
			mResponse = new JSONObject();
			JSONObject obj = new JSONObject();
			obj.put(ServerConstants.REPORT_FILENAME, fileName);
			obj.put(ServerConstants.FILEPATH, filePath);
			obj.put(ServerConstants.REPORT_FILE_TYPE, fileType);
			if (body.has(ServerConstants.FILEPUSHSERVICEREQUEST)) {
				mResponse.put(ServerConstants.FILEPUSHSERVICERESPONSE, obj);
			} else {
				mResponse.put(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS + "Response", obj);
			}
			pMessage.getResponseObject().setResponseJson(mResponse);
		} else {
			try {
				LOG.info("Request recieved to push file as base 64 String");
				String rootpath = PropertyUtils
						.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.FILE_UPLOAD_LOCATION).toString()
						.trim();
				if (rootpath.endsWith("/"))
					rootpath = rootpath.substring(0, rootpath.lastIndexOf('/'));
				if (rootpath.endsWith("\\"))
					rootpath = rootpath.substring(0, rootpath.lastIndexOf('\\'));
				String iface = pMessage.getHeader().getInterfaceId();
				/*
				 * Changes made by Samy on 10/09/2015 Not to check for database
				 * entry for file download request.
				 */
				/*
				 * TbAsfsFileDetails record = this.getFile(appId,fileName); if
				 * (record != null) {
				 */

				File file = new File(rootpath + "/" + filePath + "/" + fileName);
				/*
				 * changes done by Abhishek on 29-09-2015 to handle when only
				 * fileName comes without "/"
				 */
				String lfileName = "";
				if (fileName.contains("/")) {
					lfileName = fileName.substring(fileName.lastIndexOf("/"));
				} else {
					lfileName = fileName;
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " File Download Request File Name -:" + lfileName);
				String fileType = fileName.substring(fileName.lastIndexOf("."));
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " File Download Request File Type -:" + fileType);
				LOG.info("Path Search for file " + rootpath + "/" + fileName);
				byte[] data = new byte[(int) file.length()];

				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					fileInputStream.read(data);
					fileInputStream.close();
				} catch (IOException e) {
					LOG.error("File  not Found at Uploaded Location", e);
					DomainException dexp = DomainException.getDomainExceptionInstance();
					dexp.setMessage("File  not Found at Uploaded Location");
					dexp.setCode(DomainException.Code.APZ_DM_028.toString());
					dexp.setPriority("1");
					throw dexp;
				}
				JSONObject obj = null;
				base64 = Base64.encodeBase64String(data);
				mResponse = new JSONObject();
				obj = new JSONObject();
				obj.put(ServerConstants.REPORT_FILENAME, lfileName);
				obj.put(ServerConstants.REPORT_FILE_TYPE, fileType);
				obj.put("file", base64);
				
				if (!iface.equals(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS)) {
					mResponse.put(ServerConstants.FILEPUSHSERVICERESPONSE, obj);
				} else {
					mResponse.put(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS + "Response", obj);
				}
				/*
				 * } else { LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN +
				 * "File Entry not Found in DataBase"); DomainException dexp =
				 * DomainException.getDomainExceptionInstance();
				 * dexp.setMessage("File Entry not Found in DataBase");
				 * dexp.setCode(DomainException.Code.APZ_DM_003.toString());
				 * dexp.setPriority("1"); throw dexp; }
				 */
				pMessage.getResponseObject().setResponseJson(mResponse);
			} catch (JSONException jsone) {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(jsone.getMessage());
				dexp.setCode(DomainException.Code.APZ_DM_000.toString());
				dexp.setPriority("1");
				throw dexp;
			}
		}

	}
	
	@Inject
	TbAsmiUserRepository cAsmiUserDetRepository;
	@Inject
	TbAsmiUserRoleRepository cAsmiUserRoleRepository;
	@Inject
	TbAsmiRoleIntfRepository cAsmiRoleIntfRepository;
	private boolean checkForAuthorization(Message pMessage){
		boolean status = false;
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside checkForAuthorization..");
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.UPLOADFILEREQUEST);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request Json : "+ requestJson);
		/*JSONObject jsonObj = null;
		if(requestJson.has("Files") && requestJson.get("Files") instanceof JSONArray){
			JSONArray jsonArray = requestJson.getJSONArray("Files");
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObj = jsonArray.getJSONObject(i);
			}
		} else if(requestJson.has("Files") && requestJson.get("Files") instanceof JSONObject){
			jsonObj = requestJson.getJSONObject("Files");
		} else {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Invalid Request Format!");
		}*/
		boolean userExists = getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId());
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "User Exist Status : "+ userExists);
		if(userExists){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetching List of Roles for the UserId : "+pMessage.getHeader().getUserId()+ " and AppId : "+ pMessage.getHeader().getAppId());
			List<TbAsmiUserRole> result = cAsmiUserRoleRepository.findRolesByAppIdUserId(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId());
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Of Roles : " + result.size() + " for userId : " + pMessage.getHeader().getUserId() + " and appId : " + pMessage.getHeader().getAppId());
			//List listOfRole = null;
			List<String> lRoleslist = null;
			if (!result.isEmpty()) {
				lRoleslist = new ArrayList<String>();
				for (TbAsmiUserRole tbAsmiUserRole : result) {
					lRoleslist.add(tbAsmiUserRole.getTbAsmiUserRolePK().getRoleId());
				}
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Roles Exist For the User : " + lRoleslist);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetching List of interfaces authorized for the roleId");
				String[] interfaceids = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIds(lRoleslist, pMessage.getHeader().getAppId());
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "List of interfaces authorized : "+ interfaceids);
				List<String> responseInterfaceIdList = null;
				if(interfaceids != null && interfaceids.length > 0){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "List of interfaces authorized Not Null");
					responseInterfaceIdList = new ArrayList<String>();
					for (int i = 0; i < interfaceids.length; i++) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +interfaceids[i]);
						responseInterfaceIdList.add(interfaceids[i]);
					}
					
					if(responseInterfaceIdList.contains(pMessage.getHeader().getInterfaceId())){
						LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "InterfaceId Exists.");
						status = true;
					} else {
						LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "This InterfaceId Not Authorized.");
						DomainException dexp = DomainException.getDomainExceptionInstance();
						dexp.setMessage("This InterfaceId Not Authorized.");
						dexp.setCode(DomainException.Code.APZ_DM_025.toString());
						dexp.setPriority("1");
						throw dexp;
					}
				} else {
					LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No InterfaceIds Exists in TbAsmiRoleIntf to Validate with request interfaceId.");
					DomainException dexp = DomainException.getDomainExceptionInstance();
					dexp.setMessage("No InterfaceIds Exists");
					dexp.setCode(DomainException.Code.APZ_DM_025.toString());
					dexp.setPriority("1");
					throw dexp;
				}
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("No role exists for this userId : "+pMessage.getHeader().getUserId());
				dexp.setCode(DomainException.Code.APZ_DM_018.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No role exists for this userId : "+pMessage.getHeader().getUserId() , dexp);
				throw dexp;
			}
		}
		return status;
	}
	
	private boolean getUserByAppIdMatchWithReqUserId(String puserid, String pappid) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside getUserByAppIdMatchWithReqUserId() - AppID - "+pappid+ ", UserId - "+puserid);
		TbAsmiUser result = cAsmiUserDetRepository.findUsersByAppIdUserId(puserid, pappid);
		if (result == null || ServerConstants.NO.equalsIgnoreCase(result.getUserActive())) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("This user does not exist so no role exists for this user");
			dexp.setCode(DomainException.Code.APZ_DM_012.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " This user does not exist so no role exists for this user", dexp);
			throw dexp;
		}else{
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "User Exits in TB_ASMI_USER tbale and Active status of user : " + result.getUserActive());
			return true;
		}
	}
}
