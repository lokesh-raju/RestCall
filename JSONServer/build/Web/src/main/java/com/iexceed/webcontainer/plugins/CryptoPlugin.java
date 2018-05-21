package com.iexceed.webcontainer.plugins;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.AppzillonConstants;
import com.iexceed.webcontainer.utils.WebProperties;
import com.iexceed.webcontainer.utils.hash.Utility;

public class CryptoPlugin {

	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(CryptoPlugin.class.getName());

	private CryptoPlugin() {
	}

	public static void encryptData(HttpServletRequest request, HttpServletResponse response, String lContent) {
		LOG.debug("Inside encryptData");
		String identifier = "";
		JSONObject resultJSON = new JSONObject();
		try {
			JSONObject jObj = new JSONObject(lContent);
			identifier = jObj.getString(ID);
			String key = jObj.getString(KEY);
			String stringToEncrypt = jObj.getString(STRING_TO_ENCRYPT);

			String encryptedStr = process(ENCRYPT_REQ, key, stringToEncrypt);

			resultJSON.put(AppzillonConstants.STATUS, AppzillonConstants.SUCCESS);
			resultJSON.put(AppzillonConstants.ID, identifier);
			resultJSON.put(AppzillonConstants.ENCRYPTED_STRING, encryptedStr);
			Utility.sendResponse(response, resultJSON.toString());
			LOG.debug("Encrypted successfully");
		} catch (JSONException e) {
			Utility.callFailure(response, e, CryptoPlugin.class + ".encryptData ==========>\n", identifier);
		} catch (Exception e) {
			Utility.callFailure(response, e, CryptoPlugin.class + ".encryptData ==========>\n", identifier);
		}
	}

	public static void decryptData(HttpServletRequest request, HttpServletResponse response, String lContent) {
		LOG.debug("Inside decryptData");
		String identifier = "";
		try {
			JSONObject resultJSON = new JSONObject();
			JSONObject jObj = new JSONObject(lContent);
			identifier = jObj.getString(ID);
			String password = jObj.getString(KEY);
			String stringToDecrypt = jObj.getString(STRING_TO_DECRYPT);

			String decryptedStr = process(DECRYPT_REQ, password, stringToDecrypt);

			resultJSON.put(STATUS, SUCCESS);
			resultJSON.put(ID, identifier);
			resultJSON.put(DECRYPTED_STRING, decryptedStr);
			Utility.sendResponse(response, resultJSON.toString());
			LOG.info("Decrypted successfully");
		} catch (JSONException e) {
			Utility.callFailure(response, e, CryptoPlugin.class + ".decryptData ==========>\n", identifier);
		} catch (Exception e) {
			Utility.callFailure(response, e, CryptoPlugin.class + ".decryptData ==========>\n", identifier);
		}
	}

	public static String process(int request, String key, String reqText) throws NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
			UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		LOG.debug("Inside encrypt/decrypt data process");
		String result = null;
		String keyVal = getKey(key);
		byte[] salt = getSalt(keyVal);
		// final byte[] ivBytes = getInitializationVector(keyVal);
		byte[] ivBytes = getIV(keyVal);
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

		SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY);
		KeySpec spec = new PBEKeySpec(keyVal.toCharArray(), salt, 2, 128);
		SecretKey tmp = factory.generateSecret(spec);

		SecretKeySpec secret = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);

		Cipher cipher = Cipher.getInstance(CIPHER);
		LOG.debug("checking for request sent encrypt/decrypt");

		switch (request) {
		case ENCRYPT_REQ:
			cipher.init(Cipher.ENCRYPT_MODE, secret, ivSpec);
			byte[] ciphertext = cipher.doFinal(reqText.getBytes(UTF_8));
			result = toBase64Encode(ciphertext);
			break;

		case DECRYPT_REQ:
			cipher.init(Cipher.DECRYPT_MODE, secret, ivSpec);
			byte[] encryptedText = toByteArrayDecode(reqText);
			result = new String(cipher.doFinal(encryptedText), UTF_8);
			break;
		default:
			break;
		}
		return result;
	}

	public static String encryptResponse(String responseJson) {
		LOG.debug("-----------------------------Encrypting--------------------");
		String encryptedString = "";
		String appzillonHeader = "";
		String appzillonBody = "";
		try {
			String secretKey = WebProperties.getAppzillonSafeKey();
			//LOG.debug("Key :" + secretKey);
			JSONObject payload = new JSONObject(responseJson);
			if(payload.has(APPZILLON_HEADER)) {
				appzillonHeader = payload.getString(APPZILLON_HEADER);
			}
			LOG.debug("Encrypting appzillonHeader : " + appzillonHeader);
			if(payload.has(APPZILLON_BODY)) {
				appzillonBody = payload.getString(APPZILLON_BODY);
			}
			LOG.debug("Encrypting appzillonBody : " + appzillonBody);
			String finalSecretKey = getKey(secretKey);
			SecretKeySpec key = new SecretKeySpec(finalSecretKey.getBytes(), "AES");
			IvParameterSpec iv = new IvParameterSpec(getIV(finalSecretKey));
			String ivtobetransfered = new String(toBase64Encode(iv.getIV()));
			//String ivtobetransfered = new String(java.util.Base64.getEncoder().encode(iv.getIV()));
			//LOG.debug("iv -:" + ivtobetransfered);
			appzillonHeader = getAESencryptedForJS(key, iv, appzillonHeader);
			LOG.debug("Encrypted appzillonHeader : " + appzillonHeader);
			appzillonBody = getAESencryptedForJS(key, iv, appzillonBody);
			LOG.debug("Encrypted appzillonBody : " + appzillonBody);
			payload.put(APPZILLON_HEADER, appzillonHeader);
			payload.put(APPZILLON_BODY, appzillonBody);
			payload.put(APPZILLON_SAFE, ivtobetransfered);
	        if(payload.has(APPZILLON_ERRORS)){
	        	String errors = payload.getJSONArray(APPZILLON_ERRORS).toString();
	        	LOG.debug("Encrypting appzillonErrors : " + errors);
	            String encryptedError = getAESencryptedForJS(key, iv, errors);
	            LOG.debug("Encrypted appzillonErrors : " + encryptedError);
	            payload.put(APPZILLON_ERRORS, encryptedError);
	        }
	        encryptedString = payload.toString();
	       /* if (FAILURE.equalsIgnoreCase(appzillonHeader) || FAILURE.equalsIgnoreCase(appzillonBody)) {
	        	JSONObject res = new JSONObject();
	        	res.put(STATUS, false);
	        	res.put(APPZILLON_ERROR_CODE, PAYLOAD_ENCRYPTION_ERROR_CODE);
	        	res.put(APPZILLON_SAFE, ivtobetransfered);
	        	encryptedString = getAESencryptedForJS(key, iv, res.toString());
			}*/
		} catch (JSONException e) {
			LOG.error("JSONException Occurred!!! ", e);
		}
		LOG.debug("Transitted Encrypted Data -:" + encryptedString);
		LOG.debug("-----------------------------Encrypted--------------------");
		return encryptedString;
	}

	public static String decryptRequest(String encryptedPayload) {
		LOG.debug("-----------------------------Decrypting--------------------");
		String decryptedString = "";
		try {
			LOG.debug("Incoming CipherText :" + encryptedPayload);
			String secretKey = WebProperties.getAppzillonSafeKey();
			//LOG.debug("Key :" + secretKey);
			JSONObject payload = new JSONObject(encryptedPayload);
			String appzillonHeader = payload.getString(APPZILLON_HEADER);
			LOG.debug("Decrypting appzillonHeader : " + appzillonHeader);
			String appzillonBody = payload.getString(APPZILLON_BODY);
			LOG.debug("Decrypting appzillonBody : " + appzillonBody);
			String appzillonSafe = payload.getString(APPZILLON_SAFE);
			LOG.debug("appzillonSafe : " + appzillonSafe + " as is set to iv ");
			String finalSecretKey = getKey(secretKey);
			byte[] keytobeUsed = Arrays.copyOfRange(finalSecretKey.getBytes(), 0, 16);
			SecretKeySpec key = new SecretKeySpec(keytobeUsed, "AES");
			byte[] ivtobeused = Arrays.copyOfRange(toByteArrayDecode(appzillonSafe), 0, 16);
			IvParameterSpec iv = new IvParameterSpec(ivtobeused);
			appzillonHeader = getAESdecryptedFromJS(key, iv, appzillonHeader);
			LOG.debug("Decrypted appzillonHeader : " + appzillonHeader);
			appzillonBody = getAESdecryptedFromJS(key, iv, appzillonBody);
			LOG.debug("Decrypted appzillonBody : " + appzillonBody);
			if (!FAILURE.equalsIgnoreCase(appzillonHeader) || !FAILURE.equalsIgnoreCase(appzillonBody)) {
				payload.put(APPZILLON_HEADER, new JSONObject(appzillonHeader));
				payload.put(APPZILLON_BODY, new JSONObject(appzillonBody));
			}
			LOG.debug("Removing appzillonsafe from the request ...");
			payload.remove(APPZILLON_SAFE);
			decryptedString = payload.toString();
			if (FAILURE.equalsIgnoreCase(appzillonHeader) || FAILURE.equalsIgnoreCase(appzillonBody)) {
				JSONObject res = new JSONObject();
				JSONObject appzHeader = new JSONObject();
				JSONObject error = new JSONObject();
				JSONArray appzErrors = new JSONArray();
				appzHeader.put(STATUS, false);
				error.put(APPZILLON_ERROR_CODE, PAYLOAD_ENCRYPTION_ERROR_CODE);
				error.put(APPZILLON_ERROR_MSG, "Payload encryption failed ...");
				appzErrors.put(0, error);
				res.put(APPZILLON_HEADER, appzHeader);
				res.put(APPZILLON_BODY, new JSONObject());
				res.put(APPZILLON_ERRORS, appzErrors);
				decryptedString = res.toString();
			}
		} catch (JSONException e) {
			LOG.error("JSONException Occurred!!! ", e);
		}
		LOG.debug("Transitted Decrypted data : " + decryptedString);
		LOG.debug("-----------------------------Decrypted--------------------");
		return decryptedString;
	}

	public static String getAESencryptedForJS(SecretKeySpec key, IvParameterSpec iv, String plainText) {
		LOG.debug("AESencryptedForJS PlainText :" + plainText);
		String cipherText = "";
		try {
			Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCBC.init(Cipher.ENCRYPT_MODE, key, iv);
			byte[] encryptedData = aesCBC.doFinal(plainText.getBytes());
			cipherText = Base64.encodeBase64String(encryptedData);
			LOG.debug("encrypted Text -:" + cipherText);
		} catch (Exception ex) {
			LOG.error("Exception Occurred!!! ", ex);
			cipherText = FAILURE;
		}
		return cipherText;
	}

	public static String getAESdecryptedFromJS(SecretKeySpec key, IvParameterSpec iv, String cipherText) {
		LOG.debug("AESdecryptedFromJS CipherText :" + cipherText);
		String decryptedText = "";
		try {
			byte[] cipherData = Base64.decodeBase64(cipherText);
			Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
			byte[] decryptedData = aesCBC.doFinal(cipherData);
			decryptedText = new String(decryptedData, UTF_8);
		} catch (Exception ex) {
			LOG.error("Exception Occurred!!! ", ex);
			decryptedText = FAILURE;
		}
		return decryptedText;
	}

	private static byte[] getIV(String key) {
		byte[] iv = new byte[16];
		java.util.Arrays.fill(iv, (byte) 0);
		StringBuilder or = new StringBuilder(key);
		String nw = or.reverse().toString();
		byte[] keyBytes = null;
		try {
			keyBytes = nw.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] rawIV = new byte[keyBytes.length];
		for (int i = 0; i < keyBytes.length; i++) {
			rawIV[i] = (byte) (keyBytes[i] >> 1);
		}
		for (int i = 0; i < iv.length; i++) {
			iv[i] = rawIV[i];
		}
		return iv;
	}

	private static String toBase64Encode(byte[] data) {
		return Base64.encodeBase64String(data);
	}

	private static byte[] toByteArrayDecode(String data) {
		return Base64.decodeBase64(data);
	}

	private static byte[] getSalt(String key) {
		LOG.debug("get Salt value in bytes");
		byte[] result = null;
		if (key.length() >= 2) {
			char char1 = key.charAt(0);
			char char2 = key.charAt(1);
			char[] charArr = key.toCharArray();
			charArr[0] = char2;
			charArr[1] = char1;
			if (key.length() >= 4) {
				char char3 = key.charAt(key.length() - 1);
				char char4 = key.charAt(key.length() - 2);
				charArr[key.length() - 1] = char4;
				charArr[key.length() - 2] = char3;
			}
			String temp = new String(charArr);
			LOG.debug(SALT + temp);
			result = temp.getBytes();
		} else {
			LOG.debug(SALT + key);
			result = key.getBytes();
		}
		return result;
	}

	private static String getKey(String key) {
		LOG.debug("get Key value");
		String result = null;
		if (key.length() < 16) {
			StringBuilder temp = new StringBuilder(key);
			for (int i = key.length(); i < 16; i++) {
				temp.append(DOLLAR);
			}
			LOG.debug("Key: " + temp.toString());
			result = temp.toString();
		} else if (key.length() > 16) {
			String finalKey = key.substring(0, 16);
			LOG.debug("Key: " + finalKey);
			result = finalKey;
		} else {
			LOG.debug("Key: " + key);
			result = key;
		}
		return result;
	}
}
