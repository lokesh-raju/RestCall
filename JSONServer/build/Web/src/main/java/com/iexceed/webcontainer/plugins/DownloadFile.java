package com.iexceed.webcontainer.plugins;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;


import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.WebProperties;
import com.iexceed.webcontainer.utils.hash.Utility;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;


public class DownloadFile {

	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(DownloadFile.class.getName());

	public DownloadFile() { }

	public static void download(HttpServletRequest request, HttpServletResponse response, 
			JSONObject content, ServletContext servletContext)throws ServletException, java.io.IOException {
		LOG.debug("Inside download");
		try {
			String requestHeader = content.getString(APPZILLON_HEADER);
			JSONObject header = new JSONObject(requestHeader);
			String interfaceId = header.getString(APPZILLON_INTERFACEID);
			header.put(APPZILLON_SESSION_ID, (String) request.getSession(false).getAttribute(SESSIONID));
			header.put(APPZILLON_REQUEST_KEY, (String) request.getSession(false).getAttribute(REQUESTKEY));
			header.put(HEADER_ORIGINATION, request.getRemoteAddr());
			
			String requestContent = content.getString(APPZILLON_BODY);
			JSONObject obj = new JSONObject(requestContent);
			LOG.debug("request Content :: "+ obj.toString());
			String reqFileName = obj.getString(FILENAME);
			String reqFilePath = obj.getString(FILEPATH);
			String base64Status = obj.getString(BASE64);
			LOG.debug("Requested File Name :: "+ reqFileName);
			LOG.debug("base64 Status :: " + base64Status);
			JSONObject jRequest = new JSONObject();
			jRequest.put(APPZILLON_HEADER, header);

			String appId = WebProperties.getAppId();
			String serverURL = WebProperties.getServerURL();
			String internalServerURL = serverURL + APPZILLON_DOWNLOAD_URL;
			String responseJSON = null;
			JSONObject pushRequest = new JSONObject();
			pushRequest.put(FILENAME, reqFileName);
			pushRequest.put(FILEPATH, reqFilePath);
			pushRequest.put(BASE64, base64Status);
			 JSONObject filePushRequest = new JSONObject();
			if(!APPZILLON_INTERFACE_FILEPUSH_WS.equalsIgnoreCase(interfaceId)){
				filePushRequest.put(APPZILLON_FILEPUSH_REQ, pushRequest);
			}else{
				filePushRequest.put(APPZILLON_FILEPUSH_WS_REQ, pushRequest);
			}
			jRequest.put(APPZILLON_BODY, filePushRequest);
			String jsonRequest = jRequest.toString();
			LOG.debug("Request JSON for File Download :" + jsonRequest);

			HttpURLConnection urlConn = null;
			String requestString = jsonRequest.toString();
			int responseCode = 0;
			ClientResponse httpresponse = null;
			if("N".equalsIgnoreCase(base64Status)){
				LOG.debug("Download Rest Server URL :: "+internalServerURL);
				Client client = Client.create();
				WebResource webResource = client.resource(internalServerURL);
				httpresponse = webResource.accept(MediaType.MULTIPART_FORM_DATA)
						.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, requestString);
				responseCode = httpresponse.getStatus();
			} else if(YES.equalsIgnoreCase(base64Status)) {
				URL url = new URL(serverURL);
				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setRequestMethod(POST);
				urlConn.setRequestProperty("Content-Type", "application/json");
				urlConn.setDoOutput(true);
				 DataOutputStream dOpStr = new DataOutputStream(urlConn.getOutputStream());
				dOpStr.write(jsonRequest.getBytes());
				dOpStr.flush();
				dOpStr.close();
				responseCode = urlConn.getResponseCode();
			}
			LOG.debug("Server Response code is: " + responseCode);
			  			
			if (responseCode == 200) {
				Map<String, String> result = null;
				JSONObject resultJSON = null;
				LOG.debug("downloading file as multipart");
				if(NO.equalsIgnoreCase(base64Status)){
					FormDataMultiPart multipartResponse = httpresponse.getEntity(FormDataMultiPart.class);
					responseJSON = multipartResponse.getField("responseJSON").getEntityAs(String.class);
					if(new JSONObject(responseJSON).has(APPZILLON_ERRORS)) {
						LOG.info("Download failed sending error details");
						String errorString = new JSONObject(responseJSON).get(APPZILLON_ERRORS).toString();
						JSONArray errorArray = new JSONArray(errorString);
						resultJSON = new JSONObject(errorArray.getString(0));
					} else {
						InputStream fileContents = multipartResponse.getField("fileContents")
								.getEntityAs(InputStream.class);
						ByteArrayOutputStream os = new ByteArrayOutputStream();
					    int b;
					    while ((b = fileContents.read()) != -1)
					        os.write(b);
					    response.setContentLength(os.size());
					    response.setContentType("application/octet-stream");
						response.setContentLength((int) os.size());
						response.setHeader("Content-Disposition","attachment; fileName=\"" + reqFileName + "\"");
					    ServletOutputStream output = response.getOutputStream();
						output.write(os.toByteArray());
						output.flush();
						output.close();
						os.close();
					}
				} else if (YES.equalsIgnoreCase(base64Status)){
					InputStream inpStr = urlConn.getInputStream();
					responseJSON = Utility.convertToString(inpStr);
					JSONObject respJSON = new JSONObject(responseJSON);
					JSONObject jObjDownloadResp = null;
					if(!respJSON.has(APPZILLON_ERRORS)) {
						JSONObject respJSONBody = respJSON.getJSONObject(APPZILLON_BODY);
						jObjDownloadResp = respJSONBody.getJSONObject(APPZILLON_FILEPUSH_RES);
						String file = jObjDownloadResp.getString("file");
						result = new HashMap<String, String>();
						result.put(RESULT, SUCCESS);
						result.put(FILELINK, file);
						resultJSON = new JSONObject(result);
						LOG.debug("Success Response: " + result.toString());
					} else{
						LOG.info("Download failed sending error details");
						String errorString = new JSONObject(responseJSON).get(APPZILLON_ERRORS).toString();
						JSONArray errorArray = new JSONArray(errorString);
						resultJSON = new JSONObject(errorArray.getString(0));
					}
				}
				JSONObject respJSON = new JSONObject(responseJSON);
				JSONObject respJSONHeader = respJSON.getJSONObject(APPZILLON_HEADER);
				if (respJSONHeader.getBoolean(STATUS)) {
					String requestKey = respJSONHeader.getString( APPZILLON_REQUEST_KEY);
					request.getSession(false).setAttribute(REQUESTKEY, requestKey);
					LOG.debug("updated request key");
				}
				Utility.sendResponse(response, resultJSON.toString());
			} else {
				sendFailureResponse(response, DOWNLOAD_FILE_ERROR);
			}
		} catch (JSONException e) {
			LOG.error("JSONException", e);
			callFailure(response, e);
		} catch (MalformedURLException e) {
			LOG.error("MalformedURLException", e);
			callFailure(response, e);
		} catch (IOException e) {
			LOG.error("IOException", e);
			callFailure(response, e);
		}
	}

	private static void callFailure(HttpServletResponse response, Exception ex) {
		try {
			sendFailureResponse(response, DOWNLOAD_FILE_ERROR);
		} catch (IOException ioex) {
			LOG.error("IOException", ioex);
		}
	}

	private static void sendFailureResponse(HttpServletResponse response, String errorMessage)throws IOException {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(RESULT, errorMessage);
		JSONObject jObj = new JSONObject(resultMap);
		Utility.sendResponse(response, jObj.toString());
	}
}