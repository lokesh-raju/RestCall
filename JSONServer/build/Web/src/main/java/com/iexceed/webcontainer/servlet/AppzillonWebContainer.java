package com.iexceed.webcontainer.servlet;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.plugins.CryptoPlugin;
import com.iexceed.webcontainer.plugins.DownloadFile;
import com.iexceed.webcontainer.plugins.UploadFile;
import com.iexceed.webcontainer.utils.CallInternalServer;
import com.iexceed.webcontainer.utils.FileUtils;
import com.iexceed.webcontainer.utils.PropertyUtils;
import com.iexceed.webcontainer.utils.WebProperties;
import com.iexceed.webcontainer.utils.hash.GenerateHashedPin;
import com.iexceed.webcontainer.utils.hash.IAppzillonHashing;
import com.iexceed.webcontainer.utils.hash.Utility;

public class AppzillonWebContainer extends HttpServlet {

	private static final long serialVersionUID = -5726317205907600251L;

	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(AppzillonWebContainer.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcessRequest(request, response);
	}

	public enum ActionID {
		appzillonAuthenticationRequest, LOADSETTINGS, USERSETTINGS, appzillonReLoginRequest, UPLOAD, UPLOADAUTH, DOWNLOADAUTH, appzillonLogoutRequest, UPLOADWS, appzillonForgotPassword, appzillonChangePassword, DOWNLOADFILE, DOWNLOADWS, appzillonMaps, appzillonGetAppSecTokens, payloadEncryption;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcessRequest(request, response);
	}

	private void doProcessRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info("Processing a Request");
		// Setting Response Character Encoding
		response.setCharacterEncoding(UTF_8);
		// Setting Cache Control
		response.setHeader(CACHE_CONTROL, CACHE_CONTROL_VAL);
		JSONObject requestJson = Utility.getJsonRequest(request, response);
		String action = "";
		// Reading ActionID from Request JSON Payload
		if (requestJson != null) {
			action = getActionId(requestJson);
		}

		// ActionID is either not present or is found to be null hence
		// considering it to be the launch of the application
		if (action == null || action.isEmpty()) {
			// Session handling
			String fedUrl = PropertyUtils.getPropertyValue(FEDERATION_URL);
			LOG.debug("Checking for URL from the properties file : " + fedUrl);
			if(Utility.isNullOrEmpty(fedUrl)){
				createSession(request, response, action);
			}else{
				HttpSession session = request.getSession(false);
				LOG.debug("Checking for the existing Session -: " + session);
				if(session == null){
					createSession(request, response, action);
				}
			}
			// Setting Webcontainer's Default Settings and app properties in
			// request object to be set in JS layer
			request.setAttribute(DEFAULT_SETTINGS_ATTR, WebProperties.getLoadDefaultSettings());
			request.setAttribute(APP_PROP_ATTR, WebProperties.getAppProps());
			// Reading all data from context
			String contextDetails = Utility.getContextDetails(request, response);
			request.setAttribute(APZILLON_ARGS_ATTR, contextDetails);
			request.setAttribute(EXCHANGE, WebProperties.getAppzillonSafeKey());
			request.setAttribute(PAYLOAD_ENCRYPTION_FLAG, WebProperties.getPayloadEncryptionReq());

			// Looking up Request Processor Bean and invoking processor's
			// implementation
			LOG.debug("Checking for RequestProcessor Bean " + response.isCommitted());
			Utility.getRequestProcessorBean(request, response, WebProperties.getAppId());

			// Checking the application validity
			LOG.debug("Checking for the License validity");
			if (Utility.validateLicense()) {
				if (!response.isCommitted()) {
					LOG.debug("Dispatching request to AppzillonFirstPage jsp to launch first HTML page....");
					RequestDispatcher dispatch = request.getRequestDispatcher(WebProperties.getAppId() + ".jsp");
					dispatch.forward(request, response);
					return;
				} else {
					LOG.debug("Cannot forward as response has been committed");
				}
			} else {
				request.setAttribute(MESSAGE, "Application has expired");
				RequestDispatcher dispatch = request
						.getRequestDispatcher("/apps/" + WebProperties.getAppId() + "/screens/AppMessage.jsp");
				dispatch.forward(request, response);
				return;
			}
		} else {
			// ActionID is present, hence creating session and processing the
			// request based on the Action ID

			// Processing Appzillon Action
			processAppzillonAction(request, response, action, requestJson, getServletContext());
		}
	}

	public void processAppzillonAction(HttpServletRequest request, HttpServletResponse response, String action,
			JSONObject requestJson, ServletContext servletContext) throws IOException, ServletException {
		LOG.info("Processing Actions");
		HttpSession session = request.getSession(false);
		String lReqBody = null;
		try {
			lReqBody = requestJson.getString(APPZILLON_BODY);
		} catch (JSONException je) {
			LOG.error("JSONException", je);
		}
		if (session != null) {
			try {
				switch (ActionID.valueOf(action)) {
				case appzillonAuthenticationRequest:
					LOG.info("Calling Log in");
					appzillonLoginAction(request, response, requestJson, servletContext);
					break;

				case appzillonReLoginRequest:
					LOG.info("Calling ReLogin");
					appzillonLoginAction(request, response, requestJson, servletContext);
					break;

				case USERSETTINGS:
					LOG.info("Calling updating user settings");
					createUpdateUserSettings(response, session, lReqBody);
					break;

				case LOADSETTINGS:
					LOG.info("Calling load settings");
					String userId = (String) request.getSession(false).getAttribute(USERID);
					if (userId != null && !userId.isEmpty()) {
						String userSettings = FileUtils.readUserSettings(userId, servletContext).toString();
						this.sendSuccessResponse(response, userSettings);
					} else {
						this.sendSuccessResponse(response, WebProperties.getLoadDefaultSettings());
					}
					break;

				case UPLOAD:
					LOG.info("Calling Upload");
					UploadFile.upload(request, response, servletContext);
					break;

				case UPLOADAUTH:
					LOG.info("Calling Upload with Auth");
					UploadFile.upload(request, response, servletContext);
					break;

				case UPLOADWS:
					LOG.info("Calling Upload without session");
					UploadFile.upload(request, response, servletContext);
					break;

				case DOWNLOADFILE:
					LOG.info("Calling Download");
					DownloadFile.download(request, response, requestJson, servletContext);
					break;

				case DOWNLOADAUTH:
					LOG.info("Calling Download with Auth");
					DownloadFile.download(request, response, requestJson, servletContext);
					break;

				case DOWNLOADWS:
					LOG.info("Calling Download without session");
					DownloadFile.download(request, response, requestJson, servletContext);
					break;

				/*case ENCRYPT:
					LOG.info("Calling Encrypt");
					CryptoPlugin.encryptData(request, response, lReqBody);
					break;

				case DECRYPT:
					LOG.info("Calling Decrypt");
					CryptoPlugin.decryptData(request, response, lReqBody);
					break;*/

				case appzillonLogoutRequest:
					LOG.debug("Calling Logging Out");
					CallInternalServer.callServer(requestJson, request, response, APPZILLON_LOGOUT_REQ, servletContext);
					break;

				case appzillonChangePassword:
					LOG.info("Calling Change password");
					changePassword(request, response, requestJson, servletContext);
					break;

				case appzillonForgotPassword:
					LOG.info("Calling Forgot password");
					forgotPassword(request, response, requestJson, servletContext);
					break;

				case appzillonMaps:
					LOG.info("Loading Maps");
					sendSuccessResponse(response, SUCCESSCAPS);
					break;
					
				case appzillonGetAppSecTokens:
					LOG.info("Getting app server nonce");
					getAppSecTokens(request, response, requestJson, APPZILLON_GET_APP_SEC_TOKENS_REQUEST, servletContext);
					break;
					
				case payloadEncryption:
					LOG.info("payload Encryption ...");
					try {
						requestJson.getJSONObject(APPZILLON_BODY).remove(ACTION_ID);
						LOG.debug("Removed action id from the payload ... " + requestJson.toString(0));
					} catch (JSONException je) {
						LOG.error("JSONException ", je);
					}
					Utility.sendResponse(response, requestJson.toString());
					break;
				}
			} catch (IllegalArgumentException e) {
				LOG.info("Calling other Server Requests");
				CallInternalServer.callServer(requestJson, request, response, "", servletContext);
			}
		} else {
			LOG.debug("A valid session does not exit");
			JSONObject result = requestJson;
			try {
				JSONObject appzHeader = result.getJSONObject(APPZILLON_HEADER);
				JSONArray appzErrors = new JSONArray();
				JSONObject error = new JSONObject();
				appzHeader.put(STATUS, false);
				error.put(APPZILLON_ERROR_MSG, "A valid session does not exist...");
				error.put(APPZILLON_ERROR_CODE, "APZ-SMS-EX-003");
				appzErrors.put(0, error);
				result.put(APPZILLON_HEADER, appzHeader);
				result.put(APPZILLON_BODY, new JSONObject());
				result.put(APPZILLON_ERRORS, appzErrors);
				Utility.sendResponse(response, result.toString());
			} catch (JSONException je) {
				LOG.error("JSONException ", je);
			}
		}
	}
	
	private void getAppSecTokens(HttpServletRequest request, HttpServletResponse response, JSONObject requestJson,
			String requestType, ServletContext servletContext) {
		String deviceId = request.getSession(false).getId();
		try {
			requestJson.getJSONObject(APPZILLON_BODY).getJSONObject(APPZILLON_GET_APP_SEC_TOKENS_REQUEST).put(DEVICE_ID,
						deviceId);
			CallInternalServer.callServer(requestJson, request, response, requestType, servletContext);
		} catch (JSONException e) {
			LOG.error("JSONException", e);
		} catch (IOException e) {
			LOG.error("IOException", e);
		}
	}

	private void forgotPassword(HttpServletRequest request, HttpServletResponse response, JSONObject requestJson,
			ServletContext servletContext) throws IOException {
		try {
			String password = requestJson.getJSONObject(APPZILLON_BODY).getJSONObject(APPZILLON_FORGOTPSWD_REQ)
					.getString("password").trim();
			String encryptedPwd = CryptoPlugin.process(1, WebProperties.getServerToken(), password);
			LOG.debug("forgotPassword CipherString :" + encryptedPwd);
			requestJson.getJSONObject(APPZILLON_BODY).getJSONObject(APPZILLON_FORGOTPSWD_REQ).put("password",
					encryptedPwd);
		} catch (JSONException e) {
			LOG.error("JSONException", e);
		} catch (InvalidKeyException e) {
			LOG.error("InvalidKeyException ", e);
		} catch (NoSuchAlgorithmException e) {
			LOG.error("NoSuchAlgorithmException ", e);
		} catch (InvalidKeySpecException e) {
			LOG.error("InvalidKeySpecException ", e);
		} catch (NoSuchPaddingException e) {
			LOG.error("NoSuchPaddingException ", e);
		} catch (InvalidAlgorithmParameterException e) {
			LOG.error("InvalidAlgorithmParameterException ", e);
		} catch (UnsupportedEncodingException e) {
			LOG.error("UnsupportedEncodingException ", e);
		} catch (IllegalBlockSizeException e) {
			LOG.error("IllegalBlockSizeException ", e);
		} catch (BadPaddingException e) {
			LOG.error("BadPaddingException ", e);
		}
		LOG.debug("Updated Request " + requestJson.toString());
		CallInternalServer.callServer(requestJson, request, response, "", servletContext);
	}

	private void changePassword(HttpServletRequest request, HttpServletResponse response, JSONObject requestJson,
			ServletContext servletContext) throws IOException {
		LOG.debug("Is OTP generation required: " + WebProperties.getAuthenticationType());
		try {
			requestJson = generateOTP(requestJson, APPZILLON_CHANGEPASSWORD);
		} catch (JSONException je) {
			LOG.error("JSONException", je);
		}
		CallInternalServer.callServer(requestJson, request, response, "", servletContext);
	}

	private void createUpdateUserSettings(HttpServletResponse response, HttpSession session, String lReqBody)
			throws IOException {
		String userId = (String) session.getAttribute(USERID);
		try {
			if ((userId == null) || (userId.isEmpty())) {
				userId = DEFAULT_SETTINGS;
			}
			String userPrefs = new JSONObject(lReqBody).getString("userPrefs");
			LOG.debug("UserId: " + userId);
			String settingsFilePath = WebProperties.getSettingsPath() + WebProperties.getAppId() + File.separator
					+ userId + File.separator + SETTINGSDATAJSON;
			File settingsFile = new File(settingsFilePath);
			FileUtils.writeFileContent(settingsFile, userPrefs, false);
			this.sendSuccessResponse(response, SUCCESS);

		} catch (IOException ioe) {
			this.sendFailureResponse(response);
			LOG.error("Error in updating User Settings ", ioe);
		} catch (JSONException e) {
			LOG.error("userPrefs not found ", e);
		}
	}

	public void appzillonLoginAction(HttpServletRequest request, HttpServletResponse response, JSONObject requestJson,
			ServletContext servletContext) throws ServletException, IOException {
		try {
			LOG.debug(
					"Inside appzillon Login Action and Authentication type : " + WebProperties.getAuthenticationType());
			if (AUTHENTICATION_TYPE_DEVICE_ID.equals(WebProperties.getAuthenticationType())) {
				requestJson = generateOTP(requestJson, APPZILLON_LOGIN_REQ);
			}
			CallInternalServer.callServer(requestJson, request, response, APPZILLON_LOGIN_REQ, servletContext);
		} catch (JSONException jse) {
			LOG.error("Error in executing Login Request JSONException", jse);
		} catch (Exception ex) {
			LOG.error("Error in executing Login Request", ex);
		}
	}
	
	private JSONObject generateOTP(JSONObject requestJson, String action) throws JSONException {
		LOG.debug("Generating OTP");
		String overrideOTP = WebProperties.getOverrideOTP();
		if (overrideOTP != null && YES.equalsIgnoreCase(overrideOTP)) {
			LOG.debug("Override OTP : " + overrideOTP);
			requestJson = Utility.getOTPdetails(requestJson);
		} else {
			IAppzillonHashing otp = new GenerateHashedPin();
			requestJson = otp.generateHashedPin(requestJson, action);
		}
		return requestJson;
	}

	private String getActionId(JSONObject request) {
		String action = "";
		try {
			JSONObject appzillonHeader = null;
			if (request.has(APPZILLON_HEADER)) {
				appzillonHeader = request.getJSONObject(APPZILLON_HEADER);
			}
			JSONObject appzillonBody = request.getJSONObject(APPZILLON_BODY);
			if (appzillonBody.has(ACTION_ID) && !appzillonBody.getString(ACTION_ID).isEmpty()) {
				action = appzillonBody.getString(ACTION_ID);
			} else if (appzillonHeader.has(APPZILLON_INTERFACEID)) {
				action = appzillonHeader.getString(APPZILLON_INTERFACEID);
			}
			LOG.debug("Processing for action : " + action);
		} catch (JSONException e) {
			LOG.error("JSONException", e);
		} catch (Exception e) {
			LOG.error("Input Stream Exception -:", e);
		}
		return action;
	}

	private void createSession(HttpServletRequest request, HttpServletResponse response, String action) {
		HttpSession session = request.getSession(false);
		LOG.debug("Existing Session -: " + session);
		if(session != null) {
			session.invalidate();
		}
		session = request.getSession(true);
		LOG.debug("Created New Session -: " + session);
		Utility.setSessionTimeOut(session);
	}

	public void sendFailureResponse(HttpServletResponse response) throws IOException {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(RESULT, FAILURE);
		JSONObject jObj = new JSONObject(resultMap);
		Utility.sendResponse(response, jObj.toString());
	}

	public void sendSuccessResponse(HttpServletResponse response, String result) throws IOException {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(RESULT, result);
		JSONObject jObj = new JSONObject(resultMap);
		Utility.sendResponse(response, jObj.toString());
	}
}