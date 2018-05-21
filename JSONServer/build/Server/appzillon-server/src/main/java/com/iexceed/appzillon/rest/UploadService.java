package com.iexceed.appzillon.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

public class UploadService {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, UploadService.class.toString());

	static JSONArray createServerRequest(JSONObject appzillonHeader, JSONArray filedetailsarray, String destination,
			String overrideFlag) {
		JSONArray jsonfiles = new JSONArray();
		JSONObject jsonfile = null;

		String appId = null;
		String screenId = "";
		String userId = "";

		appId = appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		screenId = appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
		userId = appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_USER_ID);

		for (int i = 0; i < filedetailsarray.length(); i++) {
			jsonfile = new JSONObject();
			JSONObject filedetails = filedetailsarray.getJSONObject(i);

			String desfilePath = destination + "/" + filedetails.get(ServerConstants.REPORT_FILENAME).toString();
			String fileType = "";
			if (desfilePath.contains(".")) {
				fileType = desfilePath.substring(desfilePath.lastIndexOf("."));
			}
			try {
				jsonfile.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
				jsonfile.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, screenId);
				jsonfile.put(ServerConstants.MESSAGE_HEADER_USER_ID, userId);
				jsonfile.put(ServerConstants.REPORT_FILENAME, desfilePath);
				jsonfile.put(ServerConstants.REPORT_FILE_TYPE, fileType);
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "overWrite Flag  found to be " + overrideFlag);
				jsonfile.put(ServerConstants.OVERRIDEFLAG, overrideFlag);
			} catch (JSONException e1) {
				LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, e1);
			}
			jsonfiles.put(jsonfile);
		}
		return jsonfiles;
	}

	static boolean writeToFile(FormDataMultiPart multiPart, String filePath, JSONObject appzillonBody,
			JSONArray filedetailsarray, String overrideFlag, String ifaceId, int maxFileSize) throws Exception {
		boolean uploadstatus = false;

		ArrayList<String> filenames = new ArrayList<String>();
		ArrayList<FormDataBodyPart> contentslist = new ArrayList<FormDataBodyPart>();
		LOG.debug("filedetailsarray.length() : " + filedetailsarray.length());
		for (int i = 1; i <= filedetailsarray.length(); i++) {
			JSONObject filedetails = filedetailsarray.getJSONObject(i - 1);
			filenames.add(filedetails.get(ServerConstants.REPORT_FILENAME).toString());
			FormDataBodyPart contentsform = multiPart.getField(filenames.get(i - 1).toString());
			contentslist.add(contentsform);
			LOG.debug("contentslist : " + contentslist.size());
		}

		Iterator<String> i = filenames.iterator();
		Iterator<FormDataBodyPart> j = contentslist.iterator();

		while (i.hasNext() && j.hasNext()) {
			String filename = (String) i.next();

			LOG.debug(
					ServerConstants.LOGGER_PREFIX_RESTFULL + " File Path from client :: " + filePath + "/" + filename);
			if (!filename.isEmpty()) {
				File file = new File(filePath + "/" + filename);
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "directory created..");
				}
				if (!(file.isFile() && file.exists()) || ServerConstants.YES.equalsIgnoreCase(overrideFlag)) {
					// LOG.debug(" j.next() ::"+j.next());

					FormDataBodyPart tempfile = (FormDataBodyPart) j.next();
					// LOG.debug("tempfile formdatabodypart value :: "+
					// tempfile);
					File tempfilecontent = tempfile.getEntityAs(File.class);
					LOG.debug("temp file content :: " + tempfilecontent);

					InputStream uploadinputstream = new FileInputStream(tempfilecontent);
					int fileSize = uploadinputstream.available();
					if (fileSize <= maxFileSize) {
						OutputStream out = new FileOutputStream(file);
						byte[] buffer = new byte[1024];
						int c = 0;
						while ((c = uploadinputstream.read(buffer)) != -1) {
							out.write(buffer, 0, c);
						}

						out.flush();
						out.close();
						uploadinputstream.close();
					}
				}
			}
			uploadstatus = true;
		}
		return uploadstatus;
	}

	static String getFileUploadLocation(String pAppId) {
		String filePath = (String) PropertyUtils.getPropValue(pAppId, ServerConstants.FILE_UPLOAD_LOCATION);
		LOG.debug("filePath : " + filePath);
		if (Utils.isNullOrEmpty(filePath)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL
					+ " file Upload location not set ,File will be uploaded in root dir");
		}

		if (filePath.endsWith("\\")) {
			filePath = filePath.substring(0, filePath.lastIndexOf('\\'));
		}

		if (filePath.endsWith("/")) {
			filePath = filePath.substring(0, filePath.lastIndexOf('/'));
		}

		return filePath;
	}

	static int getMaxFileSize(String appId) {
		int maxFileSize;
		try {
			maxFileSize = Integer.parseInt(
					PropertyUtils.getPropValue(appId, ServerConstants.MAX_UPLOAD_FILE_SIZE).toString().trim());
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Maximum file size configured in MB : "
					+ (maxFileSize / (1024 * 1024)));
		} catch (Exception e) {
			maxFileSize = 2097152;
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL
					+ "Max file size not found in property file so setting to 2MB", e);

		}
		return maxFileSize;
	}

	static String createAndSendRequestJSON(String filePath, boolean uploadstatus, JSONObject jsonHeader,
										   JSONObject jsonBody, String pIfaceId, JSONArray jsonfiles,HttpServletRequest request,HttpServletResponse response) {
		String outputString = "";
		LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "appzillonHeader found in upload REST service " + jsonHeader);
		String content = null;
		try {
			if ((!ServerConstants.INTERFACE_ID_UPLOAD_FILE
					.equals(jsonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID))
					&& !ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS
							.equals(jsonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID))
					&& !ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH
							.equals(jsonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)))) {
				LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Creating content for External Service");
				content = "{\"" + ServerConstants.MESSAGE_HEADER + "\":" + jsonHeader.toString() + "            ,\""
						+ ServerConstants.MESSAGE_BODY + "\": " + "" + "}";
			} else {
				content = "{\"" + ServerConstants.MESSAGE_HEADER + "\":" + jsonHeader.toString() + ",\""
						+ ServerConstants.MESSAGE_BODY + "\": {\"" + ServerConstants.INTERFACE_ID_UPLOAD_FILE
						+ "Request\":{\"Files\":" + jsonfiles.toString() + "}}}";
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Final Body Content Built : " + content);
			request.setAttribute(ServerConstants.ENCRYPTION_FLAG,ServerConstants.NO);
			outputString = (String) new AppzillonRestWS().processRequest(content,request,response);
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Output from Internal Server" + outputString);

			JSONObject internalResponseobj = new JSONObject(outputString);
			JSONObject lappHeader = internalResponseobj.getJSONObject(ServerConstants.MESSAGE_HEADER);
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "appzillonHeader in Response from internal server "
					+ lappHeader.toString());

		} catch (Exception e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "Exception", e);
		}
		return outputString;
	}
}
