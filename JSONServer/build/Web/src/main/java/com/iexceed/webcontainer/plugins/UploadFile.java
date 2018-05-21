package com.iexceed.webcontainer.plugins;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.hash.Utility;

public class UploadFile{
	
	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(UploadFile.class.getName());

	public UploadFile() { }
	    
	public static void upload(HttpServletRequest request, HttpServletResponse response,	ServletContext servletContext) {	
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try{
			List fileitems = upload.parseRequest(request);
			Iterator i = fileitems.iterator();
						
			JSONArray filedetailsarray = new JSONArray();	
			JSONObject appzillonRequest = new JSONObject();
			List<String> contentList = new ArrayList<String>();
			String destination= "";
			String fileOverride = "";
			String interfaceId = "";
			JSONObject appzillonHeader = null;
			JSONObject appzillonBody = new JSONObject();
						
			int fileno = 0;
			while(i.hasNext()){
				FileItem fi = (FileItem)i.next();
				JSONObject filedetails = new JSONObject();
				
				if(!fi.isFormField()){
					fileno++;
					String filename = fi.getName();
					filedetails.put(FILE_NAME, filename);
						
					String filetype = fi.getContentType();
					LOG.debug("File Type : "+filetype);
					filedetails.put(FILE_TYPE, filetype);
					
					String fieldname= fi.getFieldName();
					LOG.debug("FieldName : "+ fieldname);
					String contents = fi.getString();
					contentList.add(contents);
					//LOG.debug("Contents : "+ contents);
					
					long filesize = fi.getSize();
					LOG.debug("File Size : "+ filesize);
					filedetails.put(FILE_SIZE, filesize);
										
					LOG.debug("File No : "+ fileno);
					filedetails.put(FILE_NO, fileno);
					
					filedetailsarray.put(filedetails);							
				}
				appzillonBody.put(FILE_DETAILS, filedetailsarray);
				
				if(fi.getFieldName().equals(FILE_DESTINATION)){
					LOG.debug( fi.getFieldName() + " : "+ fi.getString());
					destination = fi.getString();
					appzillonBody.put(FILE_DESTINATION, destination);
				}
				
				if(fi.getFieldName().equals(APPZILLON_INTERFACEID)){
					interfaceId = fi.getString();
					appzillonBody.put(APPZILLON_INTERFACEID, interfaceId);
				}
				
				if(fi.getFieldName().equals("request")){
					String requestJson = fi.getString();
					LOG.debug(fi.getFieldName()+ " : "+ fi.getString());
					appzillonBody.put("request", requestJson);
				}
				
				if(fi.getFieldName().equals(FILE_OVERRIDE)){
					fileOverride = fi.getString();
					LOG.debug(fi.getFieldName()+ " : "+ fi.getString());
					appzillonBody.put(FILE_OVERRIDE,fileOverride);
				}
				
				if(fi.getFieldName().equals(APPZILLON_HEADER)){
					LOG.debug(fi.getFieldName()+ " :: "+ fi.getString());
					String headerstring = fi.getString();
					appzillonHeader = new JSONObject(headerstring);
					appzillonHeader.put(APPZILLON_SESSION_ID, (String) request.getSession(false).getAttribute(SESSIONID));
					if(interfaceId != null && !interfaceId.isEmpty()){
						appzillonHeader.put(APPZILLON_INTERFACEID, interfaceId);
					}
					appzillonHeader.put(APPZILLON_REQUEST_KEY, (String) request.getSession(false).getAttribute(REQUESTKEY));
					appzillonHeader.put(HEADER_ORIGINATION, request.getRemoteAddr());
					appzillonRequest.put(APPZILLON_HEADER, appzillonHeader);
				}
				appzillonRequest.put(APPZILLON_BODY, appzillonBody);
			}
			if(fileno == 0) {
				String errorCode = "APZ-CNT-009";
				String errorDecription = "No File Selected";
				UploadFile.sendFailureResponse(response, errorDecription, errorCode);
			} else {
				LOG.debug("Appzillon Request : "+ appzillonRequest.toString());
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.addTextBody(APPZILLON_REQUEST, appzillonRequest.toString());
				
				ListIterator k = contentList.listIterator();
				while(k.hasNext()){
					String contents = k.next().toString();
					k.nextIndex();
					builder.addTextBody(filedetailsarray.getJSONObject(k.nextIndex()-1).get(FILE_NAME).toString()
							, contents);
					//LOG.debug("Content "+ filecount+" :: "+ contents);
				}
				Utility.upload(request, response, appzillonHeader, builder);
			}
		} catch (IOException ex) {
			LOG.error("IOException ", ex);
			try {
				sendFailureResponse(response, ex.getMessage(), "");
			} catch (IOException iex) {
				LOG.error("IOException ", iex);
			}
		} catch (JSONException ex) {
			LOG.error("JSONException ", ex);
			try {
				sendFailureResponse(response, ex.getMessage(), "");
			} catch (IOException iex) {
				LOG.error("IOException ", iex);
			}
		} catch (FileUploadException ex) {
			LOG.error("FileUploadException ", ex);
		}
	}

	public static void sendFailureResponse(HttpServletResponse response, String errorMessage, String errorCode) throws IOException {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(APPZILLON_ERROR_DESC, errorMessage);
		resultMap.put(APPZILLON_ERROR_CODE, errorCode);
		LOG.debug("Error Response : " + resultMap.toString());
		JSONObject jObj = new JSONObject(resultMap);
		Utility.sendResponse(response, jObj.toString());
	}
}