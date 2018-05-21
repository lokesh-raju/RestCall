package com.iexceed.webcontainer.utils.hash;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONObject;
import org.json.JSONException;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.plugins.CryptoPlugin;
import com.iexceed.webcontainer.utils.RSACryptoUtils;
import com.iexceed.webcontainer.utils.WebProperties;

/**
 * 
 * @author arthanarisamy
 *
 */
public class GenerateHashedPin implements IAppzillonHashing {
	
	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(GenerateHashedPin.class.getName());

	public static final Charset UTF_8 = Charset.forName("UTF-8");
	
	public JSONObject generateHashedPin(JSONObject requestJSON, String paction) throws JSONException {	
		JSONObject request = null;
		JSONObject requestBody = requestJSON.getJSONObject(APPZILLON_BODY);
		if (paction.equals(APPZILLON_LOGIN_REQ)) {
			request = requestBody.getJSONObject(LOGIN_REQUEST);
			
		}else if (paction.equals(APPZILLON_CHANGEPASSWORD)) {
			request = requestBody.getJSONObject(CHANGEPASSWORD_REQ);
			String newPassword = request.getString("newPassword");
			//LOG.debug("newPassword : " + newPassword);
			try {
				String encryptedPwd = CryptoPlugin.process(1, WebProperties.getServerToken(), newPassword);
				LOG.debug("encryptedPwd : " + encryptedPwd);
				request.put("newPassword", encryptedPwd);
			} catch (InvalidKeyException e) {
				LOG.error("InvalidKeyException ", e);
			} catch (NoSuchAlgorithmException e) {
				LOG.error("NoSuchAlgorithmException ", e);
			} catch (InvalidKeySpecException e) {
				LOG.error("InvalidKeySpecException ", e);
			} catch (NoSuchPaddingException e) {
				LOG.error("NoSuchPaddingException ", e);
			} catch (InvalidAlgorithmParameterException e) {
				LOG.error("InvalidAlgorithmParameterException ",e);
			} catch (UnsupportedEncodingException e) {
				LOG.error("UnsupportedEncodingException ", e);
			} catch (IllegalBlockSizeException e) {
				LOG.error("IllegalBlockSizeException ", e);
			} catch (BadPaddingException e) {
				LOG.error("BadPaddingException ", e);
			}
		}
		if (request != null && AUTHENTICATION_TYPE_DEVICE_ID.equals(WebProperties.getAuthenticationType())) {
			String lUserID = request.getString(HEADER_USER_ID);
			String lPin = request.getString(HEADER_PIN);
			Date lDate = new Date();
			DateFormat lDtFormat = new SimpleDateFormat(SYSDATEFORMAT);
			String lSysDate = lDtFormat.format(lDate);
			String lServerToken = WebProperties.getServerToken();					
			String lHashedPin = Utility.hashSHA256(lPin, lUserID + lServerToken);
			String lPIN = HashXor.hashValue(lUserID, lHashedPin, lSysDate);
			request.put(HEADER_PIN, lPIN);
			request.put(HEADER_SYSDATE, lSysDate);
		}
		return requestJSON;
	}
}
