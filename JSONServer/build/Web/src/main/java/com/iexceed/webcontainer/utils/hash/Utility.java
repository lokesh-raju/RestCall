package com.iexceed.webcontainer.utils.hash;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.iexceed.appzillon.custom.IRequestProcessor;
import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.plugins.CryptoPlugin;
import com.iexceed.webcontainer.plugins.UploadFile;
import com.iexceed.webcontainer.utils.WebProperties;

public class Utility {
	
	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(Utility.class.getName());

	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };
	
	private static final String A_TO_Z_a_TO_z = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	private static ApplicationContext appContext = null;

	private Utility() {
	}
	
	public static void setSessionTimeOut(HttpSession session) {
		String sessionTimeOut = WebProperties.getSessionTimeOut();
		int timeOutInt = -1;
		try {
			LOG.debug("Setting SessionTimeout...");
			timeOutInt = Integer.parseInt(sessionTimeOut);
			LOG.debug("SessionTimeOut : " + timeOutInt);
		} catch (NumberFormatException e) {
			LOG.error("SessionTimeOut is not set, hence setting to be eternal....");
		}
		session.setMaxInactiveInterval(timeOutInt);
	}

	public static String hashSHA256(String ptext, String psalt) {
		String pTextSalt = ptext + psalt;
		String pHashedText = "";
		byte[] ptextSaltbyte = new byte[200];
		byte[] hashbyte = new byte[200];
		try {
			MessageDigest msgdigest = MessageDigest.getInstance("SHA-256");
			ptextSaltbyte = pTextSalt.getBytes("UTF-8");
			msgdigest.reset();
			msgdigest.update(ptextSaltbyte);
			hashbyte = msgdigest.digest();
			pHashedText = toHexString(hashbyte);
		} catch (NoSuchAlgorithmException n) {
			LOG.error("NoSuchAlgorithmException: ", n);
		} catch (UnsupportedEncodingException unse) {
			LOG.error("Unsupported character set", unse);
		}
		LOG.info("Hashed Successfully");
		return pHashedText;
	}

	public static String toHexString(byte[] param) {
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < param.length; i++) {
			int temp = ((param[i]) >>> 4) & 0xf;
			strBuilder.append(HEX[temp]);
			temp = param[i] & 0xf;
			strBuilder.append(HEX[temp]);
		}
		return strBuilder.toString();
	}

	public static byte[] getRandomByte(int len) {
		byte[] randomByte = null;
		SecureRandom secRand = new SecureRandom();
		randomByte = new byte[len];
		secRand.nextBytes(randomByte);
		return randomByte;
	}

	public static String getRandomString(int len) {
		String randomString = "";
		byte[] randomByte = getRandomByte(len);
		randomString = Base64.encodeBase64String(randomByte);
		return randomString;
	}

	public static String generateRandomofLength(int length) {
		String result = new String();
		String alphanumeric = new String(A_TO_Z_a_TO_z);
				int size = alphanumeric.length();	
				Random random = new Random();
				for (int i=0; i<length; i++){
				 result = result + alphanumeric.charAt(random.nextInt(size));
				}
		 return result;
	} 
	public static String convertToString(InputStream inpStr) throws IOException {
		BufferedReader buffRdr = new BufferedReader(new InputStreamReader(inpStr));
		String line;
		StringBuilder lContent = new StringBuilder("");
		while ((line = buffRdr.readLine()) != null) {
			lContent.append(line);
		}
		buffRdr.close();
		inpStr.close();
		return lContent.toString();
	}

	public static String getStackTrace(Exception pEx) {
		StringWriter lSw = null;
		PrintWriter lPw = null;
		try {
			// Creating String writer Object
			lSw = new StringWriter();
			// Creating print writer object
			lPw = new PrintWriter(lSw);
			// Getting stack trace and storing it in print writer obj
			pEx.printStackTrace(lPw);
			// Storing the stack trace string to the string object
		} catch (Exception ex) {
			LOG.error("Exception", ex);
		}
		// returning stack trace string
		return lSw.toString();
	}

	public static String getStringFromInputStream(InputStream inpStrm) {
		LOG.debug("Converting InputStream to String");
		BufferedReader buffRdr = null;
		StringBuilder strBuilder = new StringBuilder();
		String line;
		try {
			buffRdr = new BufferedReader(new InputStreamReader(inpStrm, "UTF-8"));
			while ((line = buffRdr.readLine()) != null) {
				strBuilder.append(line);
			}
		} catch (IOException e) {
			LOG.error("IOException", e);
		} finally {
			if (buffRdr != null) {
				try {
					buffRdr.close();
				} catch (IOException e) {
					LOG.error("IOExecption", e);
				}
			}
		}
		return strBuilder.toString();
	}

	public static String getContextDetails(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("Getting request object details");
		JSONObject reqObj = null;
		try {
			reqObj = new JSONObject();
			if (request.getParameterNames() != null) {
				JSONObject parNames = null;
				JSONArray paramArray = new JSONArray();
				Enumeration<String> parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					parNames = new JSONObject();
					String key = (String) parameterNames.nextElement();
					String val = request.getParameter(key);
					parNames.put("name", key);
					parNames.put("value", val);
					paramArray.put(parNames);
				}
				reqObj.put("RequestParameters", paramArray);
			}
			if (request.getQueryString() != null) {
				reqObj.put("QueryString", request.getQueryString());
			}
			if (request.getInputStream() != null) {
				InputStream inpStr = request.getInputStream();
				final BufferedReader buffRdr = new BufferedReader(new InputStreamReader(inpStr));
				String line;
				StringBuilder responseStream = new StringBuilder();
				while ((line = buffRdr.readLine()) != null) {
					responseStream.append(line);
				}
				reqObj.put("FormData", responseStream);
			}
			if (request.getHeaderNames() != null) {
				JSONArray hdrArray = new JSONArray();
				Enumeration<String> headerNames = request.getHeaderNames();
				JSONObject hdr = null;
				while (headerNames.hasMoreElements()) {
					hdr = new JSONObject();
					String paramName = (String) headerNames.nextElement();
					String paramValue = request.getHeader(paramName);
					hdr.put("name", paramName);
					hdr.put("value", paramValue);
					hdrArray.put(hdr);
				}
				reqObj.put("HeaderParams", hdrArray);
			}
			LOG.debug("Request Json Details : " + reqObj);
		} catch (JSONException jsone) {
			LOG.error("JSONException ", jsone);
		} catch (IOException e) {
			LOG.error("IOException ", e);
		}
		return reqObj.toString();
	}

	public static void getRequestProcessorBean(HttpServletRequest request, HttpServletResponse response, String appId) {
		try {
			IRequestProcessor bean = (IRequestProcessor) getAppContext().getBean(REQUEST_PROCESSOR_BEAN);
			bean.requestProcessor(request, response, appId);
			return;
		} catch (Exception ex) {
			LOG.error("Bean not found ", ex);
		}
	}

	public static BeanFactory getAppContext() {
		try {
			if (appContext == null) {
				LOG.debug("Application context is getting intialized");
				appContext = new ClassPathXmlApplicationContext("META-INF/appzillon-beans.xml");
			}
		} catch (Exception ex) {
			LOG.error("Bean information not found ", ex);
		}
		return appContext;
	}

	public static JSONObject getOTPdetails(JSONObject requestJson) {
		JSONObject resJson = null;
		try {
			IAppzillonHashing bean = (IAppzillonHashing) ((BeanFactory) getAppContext()).getBean(GENERATE_OTP);
			resJson = bean.generateHashedPin(requestJson, APPZILLON_LOGIN_REQ);
		} catch (JSONException jse) {
			LOG.error("JSONException", jse);
		} catch (Exception ex) {
			LOG.error("Bean not found ", ex);
		}
		return resJson;
	}

	public static boolean validateLicense() {
		LOG.info("Checking for Application's Expiry");
		boolean validitity = false;
		try {
			LOG.info("Expiry Date format is: " + EXPIRY_DATE_FORMAT);
			SimpleDateFormat sDateFormat = new SimpleDateFormat(EXPIRY_DATE_FORMAT);
			String expDate = WebProperties.getExpiryDate();
			LOG.info("Application's Expiry Date is: " + expDate);
			if ((expDate == null) || expDate.isEmpty()) {
				validitity = true;
			} else {
				Date lExpiryDate = sDateFormat.parse(expDate);
				Date date = new Date();
				Date lCurrentDate = sDateFormat.parse(sDateFormat.format(date));
				if (lExpiryDate.compareTo(lCurrentDate) > 0) {
					validitity = true;
				}
			}
		} catch (ParseException ex) {
			LOG.error("ParseException ", ex);
			validitity = false;
		}
		return validitity;
	}

	public static void callFailure(HttpServletResponse response, Exception lException, String clsMsg,
			String identifier) {
		LOG.error(clsMsg, lException);
		try {
			JSONObject result = new JSONObject();
			result.put(STATUS, FAILURE);
			if ((identifier != null) && (!"".equals(identifier)) && (!"null".equalsIgnoreCase(identifier))) {
				result.put(ID, identifier);
			}
			sendResponse(response, result.toString());
		} catch (JSONException jse) {
			LOG.error(clsMsg, jse);
		} catch (IOException ioe) {
			LOG.error(clsMsg, ioe);
		}
	}

	private static String getfinalResponse(HttpServletResponse response, String result){
		String payload = "";
		if(YES.equalsIgnoreCase(WebProperties.getPayloadEncryptionReq())){
			LOG.debug("Encrypting the response ...");
			payload = CryptoPlugin.encryptResponse(result);
			LOG.debug("Encrypted the response ...");
		} else {
			payload = result;
		}
		LOG.debug("Final Payload : " + payload);
		return payload;
	}
	
	public static void sendResponse(HttpServletResponse response, String result) throws IOException {
		LOG.debug("Sending Response :" + result);
		PrintWriter out = response.getWriter();
		String finalResponse = getfinalResponse(response, result);
		out.print(finalResponse);
		out.flush();
		out.close();
	}
	
	 /**
     * returns true if the string is not null and not empty
     * otherwise returns false
     * @param pValue
     * @return
     */

	public static boolean isNotNullOrEmpty(String pValue) {
		return pValue != null && !pValue.isEmpty() ? true : false;
	}

    /**
     *returns true if the string is null or empty
     * otherwise returns false
     * @param pValue
     * @return
     */

	public static boolean isNullOrEmpty(String pValue) {
		return pValue == null || pValue.isEmpty() ? true : false;
	}
	
	public static JSONObject getJsonRequest(HttpServletRequest request, HttpServletResponse response) {
		InputStream inpStrm = null;
		String content = "";
		JSONObject requestJson = null;
		try {
			LOG.debug("ContentType : " + request.getContentType());
			if (request.getContentType() != null && !request.getContentType().isEmpty()) {
				if (request.getContentType().contains(CONTENT_TYPE_MULTIPART)) {
					requestJson = new JSONObject();
					requestJson.put(APPZILLON_BODY, new JSONObject().put(ACTION_ID, ACTION_ID_UPLOAD));

				} else if (request.getContentType().contains("application/x-www-form-urlencoded")) {
					// Do Nothing

				} else {
					inpStrm = request.getInputStream();
					content = Utility.convertToString(inpStrm);
					if("Y".equalsIgnoreCase(WebProperties.getPayloadEncryptionReq()) 
							&& !content.contains(APPZILLON_INTERFACE_FILE_PUSH)
							&& !content.contains(APPZILLON_INTERFACE_FILEPUSH_WS)
							&& !content.contains(APPZILLON_INTERFACE_FILE_PUSH_AUTH)){
						//Decrypt Request coming from javascript 
						LOG.debug("Decrypting Request : " + content);
						content = CryptoPlugin.decryptRequest(content);
						LOG.debug("Decrypted Request: " + content);
					}
					if (!content.isEmpty()) {
						requestJson = new JSONObject(content.trim());
						LOG.debug("Request content : " + content);
					}
					if ("Y".equalsIgnoreCase(WebProperties.getPayloadEncryptionReq()) && requestJson.has(APPZILLON_ERRORS)) {
						LOG.debug("Processing for the failure Payload encryption details ...");
						requestJson.getJSONObject(APPZILLON_BODY).put(ACTION_ID, PAYLOAD_ENCRYPTION);
						LOG.debug("Sending the failure payload details : " + requestJson.toString(0));
					}
				}
			}
		} catch (JSONException e) {
			LOG.error("JSONException", e);
		} catch (Exception e) {
			LOG.error("Input Stream is not proper");
		}
		return requestJson;
	}
	
	public static boolean checkQualityOfPayload(String responseJson)
			throws JSONException, UnsupportedEncodingException {
		LOG.info("Data Integration is in process....");
		JSONObject res = new JSONObject(responseJson);
		String cNonce = res.getJSONObject(APPZILLON_HEADER).getString(CLIENT_NONCE);
		String sNonce = res.getJSONObject(APPZILLON_HEADER).getString(SERVER_NONCE);
		String hashedResponse = res.getString(QOP);
		int i = responseJson.indexOf(QOP);
		responseJson = responseJson.replaceAll(responseJson.substring(i - 1, i + 81), "");
		String encodedPayload = Base64.encodeBase64String(responseJson.trim().getBytes());
		String lHashedCnonce = Utility.hashSHA256(cNonce, sNonce + WebProperties.getServerToken());
		String hashedPayLoad = Utility.hashSHA256(encodedPayload, lHashedCnonce);
		if (hashedResponse.equals(hashedPayLoad)) {
			return true;
		} else {
			return false;
		}
	}

	public static void upload(HttpServletRequest request, HttpServletResponse response, JSONObject appzillonHeader,
			MultipartEntityBuilder builder) throws JSONException, IOException, ClientProtocolException {
		String serverURL = WebProperties.getServerURL();
		LOG.debug("Server URL : " + serverURL);
		String internalServerURL = serverURL + APPZILLON_UPLOAD_PATH;
		LOG.debug("Upload Server url : " + internalServerURL);

		String interfaceId = appzillonHeader.getString(APPZILLON_INTERFACEID);

		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost postrequest = new HttpPost(internalServerURL);

		HttpEntity entity = builder.build();
		postrequest.setEntity(entity);

		HttpResponse httpresponse = httpclient.execute(postrequest);
		int responseCode = httpresponse.getStatusLine().getStatusCode();
		String responseMessage = httpresponse.getStatusLine().getReasonPhrase();
		LOG.debug("Response code is : " + responseCode + " and Response Message is : " + responseMessage);
		if (responseCode == 200) {
			String responseJSON = EntityUtils.toString(httpresponse.getEntity());
			JSONObject jObjRes = new JSONObject(responseJSON);
			JSONObject jObjHeader = jObjRes.getJSONObject(APPZILLON_HEADER);
			JSONObject resultJson = null;
			if (jObjHeader.getBoolean(STATUS)) {
				String requestKey = jObjHeader.getString(APPZILLON_REQUEST_KEY);
				request.getSession(false).setAttribute(REQUESTKEY, requestKey);
				JSONObject jObjBody = jObjRes.getJSONObject(APPZILLON_BODY);

				JSONObject uploadRes = null;
				if (APPZILLON_INTERFACE_UPLOAD_FILE_WS.equalsIgnoreCase(interfaceId)) {
					uploadRes = jObjBody.getJSONObject(APPZILLON_UPLOADWSFILE_RES);
				} else {
					uploadRes = jObjBody.getJSONObject(APPZILLON_UPLOADFILE_RES);
				}
				resultJson = new JSONObject();
				resultJson.put(SUCCESS_MESSAGE, uploadRes);
				resultJson.put(RESULT, SUCCESS);
				Utility.sendResponse(response, resultJson.toString());

			} else {
				String errorString = new JSONObject(responseJSON).get(APPZILLON_ERRORS).toString();
				JSONArray errorArray = new JSONArray(errorString);
				resultJson = new JSONObject(errorArray.getString(0));
				Utility.sendResponse(response, resultJson.toString());
			}
		} else {
			UploadFile.sendFailureResponse(response, APPZILLON_ERROR_DESC, ULOAD_FAILURE);
		}
		postrequest.releaseConnection();
	}
}
