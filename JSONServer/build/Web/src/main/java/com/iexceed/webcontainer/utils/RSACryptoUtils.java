package com.iexceed.webcontainer.utils;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import com.iexceed.webcontainer.startup.WebContextListener;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.hash.Utility;

public class RSACryptoUtils {

	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(RSACryptoUtils.class.getName());

	public static PublicKey publicKey = null ;

	public static void readPublicKeyFromFile() {
		String p_keyFileName = WebProperties.getEncryptionKeyFileName();
		KeyFactory keyFactory;
		LOG.debug("KeyFileName: " + p_keyFileName);
		try {
			if (p_keyFileName.toUpperCase().endsWith(".PEM")) {
				LOG.debug("Reading PEM file");
				// Read PEM file
				Security.addProvider(new BouncyCastleProvider());
				KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
				PemFile pemFile = new PemFile(p_keyFileName);
				byte[] content = pemFile.getPemObject().getContent();
				X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
				publicKey = factory.generatePublic(pubKeySpec);
			} else if (p_keyFileName.toUpperCase().endsWith(".DER")) {
				// Read DER file
				LOG.debug("Reading DER file");
				byte[] publicKeyContents = readFileContents(p_keyFileName);
				X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyContents);
				keyFactory = KeyFactory.getInstance("RSA");
				publicKey = keyFactory.generatePublic(pubKeySpec);
			}
		} catch (IOException e) {
			LOG.error("IOException ", e);
		} catch (NoSuchAlgorithmException e) {
			LOG.error("NoSuchAlgorithmException ", e);
		} catch (InvalidKeySpecException e) {
			LOG.error("InvalidKeySpecException ", e);
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] readFileContents(String file) throws IOException {
		DataInputStream dis;
		byte[] pubKeyBytes;
		if(WebContextListener.propertiesPath !=null && !"".equals(WebContextListener.propertiesPath)) {
			dis = new DataInputStream(RSACryptoUtils.class.getClassLoader().getResourceAsStream(WebContextListener.propertiesPath+"/"+file));
		}else {
			dis = new DataInputStream(RSACryptoUtils.class.getClassLoader().getResourceAsStream(file));
		}
		pubKeyBytes = new byte[dis.available()];
		dis.readFully(pubKeyBytes);
		dis.close();
		return pubKeyBytes;
	}
	
	// encryption using appzillonSafe
	public static String encryptPayloadWithKey(String dataToEncript, String secretKey, String requestType)
			throws IOException, JSONException {
		JSONObject jsonObject = new JSONObject(dataToEncript);	
		String encryptedheader = AppzillonAESUtils.getEncryptePayload(secretKey,jsonObject.getJSONObject(APPZILLON_HEADER).toString());
		String encryptedbody = AppzillonAESUtils.getEncryptePayload(secretKey,jsonObject.getJSONObject(APPZILLON_BODY).toString());
		String encryptedSafeToken = encryptData(secretKey);
		
		StringBuilder lEncryptedReq = new StringBuilder();
		if (YES.equals(WebProperties.getDataIntegrity())
				&& (!APPZILLON_GET_APP_SEC_TOKENS_REQUEST.equals(requestType))) {
			String hashedQop = jsonObject.getString(QOP);
			lEncryptedReq.append("{\"").append(QOP).append("\":\"").append(hashedQop).append("\",").append("\"")
					.append(APPZILLON_BODY).append("\":\"").append(encryptedbody).append("\",").append("\"")
					.append(APPZILLON_HEADER).append("\":\"").append(encryptedheader).append("\",").append("\"")
					.append(APPZILLON_SAFE).append("\":\"").append(encryptedSafeToken).append("\"").append("}");
		} else {
			lEncryptedReq.append("{\"").append(APPZILLON_BODY).append("\":\"").append(encryptedbody).append("\",")
					.append("\"").append(APPZILLON_HEADER).append("\":\"").append(encryptedheader).append("\",")
					.append("\"").append(APPZILLON_SAFE).append("\":\"").append(encryptedSafeToken).append("\"")
					.append("}");
		}

		return lEncryptedReq.toString();

	}

	// decryption using appzillonSafe
	public static String decryptPayloadWithKey(String encryptedString) throws JSONException, IOException {
		JSONObject jsonObject = new JSONObject(encryptedString);
		String secretKey = decryptData(jsonObject.getString(APPZILLON_SAFE));
		jsonObject.remove(APPZILLON_SAFE);
		String decryptederror = "";
		String qop = "";

		String decryptedheader = AppzillonAESUtils.getDecryptedPayload(secretKey,
				jsonObject.getString(APPZILLON_HEADER));
		String decryptedbody = AppzillonAESUtils.getDecryptedPayload(secretKey, jsonObject.getString(APPZILLON_BODY));

		if (jsonObject.has(QOP)) {
			qop = jsonObject.getString(QOP);
		}
		if (jsonObject.has(APPZILLON_ERRORS)) {
			decryptederror = AppzillonAESUtils.getDecryptedPayload(secretKey, jsonObject.getString(APPZILLON_ERRORS));
		}

		StringBuilder lReqString = new StringBuilder();

		if (jsonObject.has(QOP) && jsonObject.has(APPZILLON_ERRORS)) {
			lReqString.append("{\"").append(QOP).append("\":\"").append(qop).append("\",").append("\"")
					.append(APPZILLON_ERRORS).append("\":").append(decryptederror).append(",").append("\"");
		} else if (jsonObject.has(QOP) && !jsonObject.has(APPZILLON_ERRORS)) {
			lReqString.append("{\"").append(QOP).append("\":\"").append(qop).append("\",").append("\"");
		} else if (!jsonObject.has(QOP) && jsonObject.has(APPZILLON_ERRORS)) {
			lReqString.append("{\"").append(APPZILLON_ERRORS).append("\":").append(decryptederror).append(",")
					.append("\"");
		} else {
			lReqString.append("{\"");
		}

		lReqString.append(APPZILLON_HEADER).append("\":").append(decryptedheader).append(",\"").append(APPZILLON_BODY)
				.append("\":").append(decryptedbody).append("}");
		
		return lReqString.toString().trim();
	}

	// encryption using public key
	public static String encryptData(String plainText) throws JSONException, IOException {
		String ecKey = "";
		try {
			Cipher cipher2 = Cipher.getInstance(ALGORITHM_WITH_PADDING);
			cipher2.init(Cipher.ENCRYPT_MODE, publicKey);
			ecKey = new String(org.apache.commons.codec.binary.Base64
					.encodeBase64(cipher2.doFinal(plainText.getBytes(Charset.forName("UTF-8")))));

		} catch (Exception exe) {
			LOG.error("Exception", exe);
		}
		return ecKey;
	}

	// decryption using public key
	public static String decryptData(String encryptedString) throws IOException {
		String dcKey = "";
		try {
			Cipher cipher3 = Cipher.getInstance(ALGORITHM_WITH_PADDING);
			cipher3.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] bytNewData = cipher3.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(encryptedString));
			dcKey = new String(bytNewData);
		} catch (Exception e) {
			LOG.error("Exception", e);
		}
		return dcKey;

	}

	public static String removeSpaceFromElements(String payloadJson) {
		Pattern p = Pattern.compile("\\s*[\"\"]");
		Matcher m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("\"");

		p = Pattern.compile("[\"\"]\\s*");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("\"");

		p = Pattern.compile("[}]\\s*");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("}");

		p = Pattern.compile("\\s*[{]");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("{");

		p = Pattern.compile("[]]\\s*");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("]");

		p = Pattern.compile("[\\[]\\s*");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("[");

		p = Pattern.compile("[:]\\s*");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll(":");

		p = Pattern.compile("[\n]");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("");

		return payloadJson;
	}
	
	public static String generatePayloadWithQOP(JSONObject payloadJson) throws JSONException {
		LOG.debug("Data integration is in process...");
		JSONObject requestHeader = payloadJson.getJSONObject(APPZILLON_HEADER);
		String cNonce = requestHeader.getString(CLIENT_NONCE);
		String serverNonce = requestHeader.getString(SERVER_NONCE);
		String lServerToken = WebProperties.getServerToken();
		String payload = payloadJson.toString();
		String encodedPayload = Base64.encodeBase64String(payload.getBytes());
		String lHashedCnonce = Utility.hashSHA256(cNonce, serverNonce + lServerToken);
		String hashedPayLoad = Utility.hashSHA256(encodedPayload,lHashedCnonce);
		StringBuilder lReqString = new StringBuilder();
		lReqString.append("{\"").append(QOP).append("\":\"").append(hashedPayLoad).append("\",")
				.append(payload.substring(1));
		return lReqString.toString();
	}

}
