package com.iexceed.appzillon.securityutils;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utilsexception.UtilsException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by diganta.kumar@i-exceed.com on 6/2/18 3:10 PM 
 */
public class RSACryptoUtils {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, RSACryptoUtils.class.toString());

    private static final String ALGORITHM = "RSA";
    private static final String ALGORITHM_WITH_PADDING = "RSA/ECB/PKCS1Padding";//RSA/ECB/PKCS1Padding
    public static String rsaEncryptionRequired = ServerConstants.NO;
    public static String safeToken = "";
    static PrivateKey privateKey = null;

    public static String decryptRequestPayLoad(String inputString, HttpServletRequest request){
        String encryptionFlag= PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,ServerConstants.ENCRYPTION_FLAG);
        LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Encryption Required :"+encryptionFlag +" Encryption flag in http request object: "+request.getAttribute(ServerConstants.ENCRYPTION_FLAG));
        try {
            JSONObject jsonObject = new JSONObject(inputString);
            jsonObject= jsonObject.getJSONObject(ServerConstants.MESSAGE_HEADER);
            String interfaceId=jsonObject.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
            if(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE.equalsIgnoreCase(interfaceId)
                    || ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH.equalsIgnoreCase(interfaceId)
                    || ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS.equalsIgnoreCase(interfaceId) ){
                request.setAttribute(ServerConstants.ENCRYPTION_FLAG,ServerConstants.NO);
            }else {
                String appId= jsonObject.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
                String replayAttackFlag=PropertyUtils.getPropValue(appId,ServerConstants.REPLAY_REQUEST_REQUIRED);
                LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "replayAttackFlag for request:"+replayAttackFlag);
                if(Utils.isNotNullOrEmpty(replayAttackFlag) && replayAttackFlag.equalsIgnoreCase(ServerConstants.NO)){
                    encryptionFlag=ServerConstants.NO;
                }

            }
        }catch (JSONException e){
            LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Request may be encrypted :"+e);
        }
        try {
            if (ServerConstants.YES.equalsIgnoreCase(encryptionFlag)
                    && (request.getAttribute(ServerConstants.ENCRYPTION_FLAG) == null || ServerConstants.YES.equalsIgnoreCase((String) request.getAttribute(ServerConstants.ENCRYPTION_FLAG)))
                    && (request.getHeader(ServerConstants.APZCNTR) == null || !ServerConstants.APZRICT.equalsIgnoreCase(request.getHeader(ServerConstants.APZCNTR)))) {
                LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Encrypted Request PayLoad:" + inputString);
                rsaEncryptionRequired = ServerConstants.YES;
                privateKey = readPrivateKeyFromFile();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(inputString);
                } catch (JSONException e) {
                    LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "Exception in Encrypted input:", e);
                }
                String encryptedKey = jsonObject.getString(ServerConstants.MESSAGE_SAFE);

                String decryptedKey = decryptData(encryptedKey);
                safeToken = decryptedKey;
                String headerDecrypted = getDecryptedPayload(decryptedKey, jsonObject.getString(ServerConstants.MESSAGE_HEADER));
                String bodyDecrypted = getDecryptedPayload(decryptedKey, jsonObject.getString(ServerConstants.MESSAGE_BODY));
                String qop = jsonObject.has(ServerConstants.QOP) ? jsonObject.getString(ServerConstants.QOP) : null;
                inputString = getAppzillonPayload(qop, headerDecrypted, bodyDecrypted);


        } else if(ServerConstants.YES.equalsIgnoreCase(encryptionFlag) && request.getHeader(ServerConstants.APZCNTR) != null && ServerConstants.APZRICT.equalsIgnoreCase(request.getHeader(ServerConstants.APZCNTR)) ){
         // decryption for rict
            rsaEncryptionRequired = ServerConstants.YES;
            JSONObject jsonObject = new JSONObject(inputString);
            String appzillonHeader = jsonObject.getString(ServerConstants.MESSAGE_HEADER);
            String appzillonBody = jsonObject.getString(ServerConstants.MESSAGE_BODY);
            String appzillonSafe = jsonObject.getString(ServerConstants.MESSAGE_SAFE);

            appzillonHeader = decryptRequest(appzillonSafe,appzillonHeader);
            appzillonBody = decryptRequest(appzillonSafe,appzillonBody);

                inputString = getAppzillonPayload(null, appzillonHeader, appzillonBody);

            }
            LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Decrypted inputString:" + inputString);
        }catch (JSONException e){
            LOG.error("Error in json request :",e);
            UtilsException utilsException = UtilsException.getUtilsExceptionInstance();
            utilsException.setCode("APZ_RS_001");
            utilsException.setMessage("Invalid Appzillon Request");
            throw utilsException;
        }
        return inputString;

    }

    public static String getEncryptedString(Header header, String toBeEncrypted){
        String encryptData;
        LOG.debug("rsaEncryptionRequired :" + rsaEncryptionRequired);
        String secreteKey = header.getServerToken();
        if (Utils.isNullOrEmpty(secreteKey)) {
            secreteKey = safeToken;
        }
        encryptData = getEncryptePayload(secreteKey, toBeEncrypted);
        return encryptData;
    }
    public static String getEncryptePayload(String key, String pResponseBody) {
        String encryptedAppzillonBody = null;

        LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
                + " getEncrypteBody Encryption key  -:"
                + key);
        LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
                + " getEncrypteBody pResponseBody -:" + pResponseBody);

        String paddingMask = "$$$$$$$$$$$$$$$$";
        if (key.length() <= 16) {
            key += paddingMask.substring(0, 16 - key.length());
        }
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
                + " getEncrypteBody Encryption key after checking the length -:"
                + key);
        byte[] iv = AppzillonAESUtils.getIV(key);
        String finalSalt = AppzillonAESUtils.getSalt(key);
        encryptedAppzillonBody = AppzillonAESUtils.encryptString(ServerConstants.PBS_PADDING,
                key, pResponseBody, finalSalt, iv, ServerConstants.WEB);
        LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
                + " getEncrypteBody Encrypted Body -:" + encryptedAppzillonBody);
        return encryptedAppzillonBody;
    }


    public static String getDecryptedPayload(String key, String pRequestBody) {
        String decryptedAppzillonBody = null;
        LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
                + " getDecryptedBody Encryption key from properties file -:"
                + key);
        LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
                + " getDecryptedBody pRequestBody -:" + pRequestBody);

        String paddingMask = "$$$$$$$$$$$$$$$$";
        if (key.length() <= 16) {
            key += paddingMask.substring(0, 16 - key.length());
        }
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
                + " getDecryptedBody Encryption key after checking the length -:"
                + key);
        byte[] iv = AppzillonAESUtils.getIV(key);
        String finalSalt = AppzillonAESUtils.getSalt(key);
        decryptedAppzillonBody = AppzillonAESUtils.decryptString(ServerConstants.PBS_PADDING, key, pRequestBody,finalSalt, iv, ServerConstants.WEB);
        LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
                + " getDecryptedBody Decrypted AppzillonBody -:"
                + decryptedAppzillonBody);

        return decryptedAppzillonBody;
    }

    public static String decryptData(String p_dataToDecrypt) {
        String res = "";
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM_WITH_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] ciphertextBytes = org.apache.commons.codec.binary.Base64.decodeBase64(p_dataToDecrypt);
            byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
            res = new String(decryptedBytes);
        } catch (Exception e) {
           LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL+" Error in Decrypting Data",e);
        }
        return res;
    }
    public static String encryptData(final String p_dataToEncrypt) {
        String res = "";
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM_WITH_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] encryptedBytes = cipher.doFinal(p_dataToEncrypt.getBytes(Charset.forName("UTF-8")));
            res =  new String(org.apache.commons.codec.binary.Base64.encodeBase64(encryptedBytes));

        } catch (Exception exe) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL+" Error in Encrypting Data",exe);
        }
        return res;
    }
    public static PrivateKey readPrivateKeyFromFile() {
        String p_keyFileName= PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,ServerConstants.ENCRYPTION_KEY_FILENAME);
        KeyFactory keyFactory;
        PrivateKey privateKey=null ;
        try {
            if(p_keyFileName.toUpperCase().endsWith(".PEM")){
                LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Reading PEM file.");
                // Read PEM file
                Security.addProvider(new BouncyCastleProvider());
                KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
                PemFile pemFile = new PemFile(p_keyFileName);
                byte[] content = pemFile.getPemObject().getContent();
                PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
                privateKey =  factory.generatePrivate(privKeySpec);
            } else if(p_keyFileName.toUpperCase().endsWith(".DER")){
                // Read DER file
                LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Reading DER file.");
                byte [] privateKeyContents = readFileContents(p_keyFileName);
                PKCS8EncodedKeySpec privateSpec1 = new PKCS8EncodedKeySpec(privateKeyContents);
                keyFactory = KeyFactory.getInstance(ALGORITHM);
                privateKey = keyFactory.generatePrivate(privateSpec1);
            }

        } catch (IOException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL+" IOException",e);
        } catch (NoSuchAlgorithmException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL+" NoSuchAlgorithmException",e);
        } catch (InvalidKeySpecException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL+" InvalidKeySpecException",e);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return privateKey;
    }
    public static byte[] readFileContents(String file) throws IOException {

        DataInputStream dis ;
        byte[] privKeyBytes;
        if(Utils.isNullOrEmpty(Logger.propertiesPath)) {
            dis = new DataInputStream(RSACryptoUtils.class.getClassLoader().getResourceAsStream(file));
        }else {
            dis = new DataInputStream(RSACryptoUtils.class.getClassLoader().getResourceAsStream(Logger.propertiesPath+"/"+file));
        }
        privKeyBytes = new byte[dis.available()];
        dis.readFully(privKeyBytes);
        dis.close();
        return privKeyBytes;
    }
    public static String removeSpaceFromElements(String payloadJson){
        Pattern p = Pattern.compile("\\s*[\"\"]");
        Matcher m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("\"");

        p = Pattern.compile("[\"\"]\\s*");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("\"");

        p =  Pattern.compile("[}]\\s*");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("}");

        p = Pattern.compile("\\s*[{]");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("{");

        p =  Pattern.compile("[]]\\s*");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("]");

        p =  Pattern.compile("[\\[]\\s*");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("[");

        p = Pattern.compile("[:]\\s*");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll(":");
        return payloadJson;
    }
//  RICT changes
    public static String encryptResponse(String responseJson) {
        LOG.debug("-----------------------------Encrypting--------------------");
        JSONObject appzillonResponse = new JSONObject(responseJson);
        String header = appzillonResponse.getJSONObject(ServerConstants.MESSAGE_HEADER).toString(0);
        String body = appzillonResponse.getJSONObject(ServerConstants.MESSAGE_BODY).toString(0);
        String errors="";
        String secretKey =  PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,ServerConstants.APPZILLON_SAFE_KEY);
        LOG.debug("Key :" + secretKey);
        String finalSecretKey = getKey(secretKey);
        SecretKeySpec key = new SecretKeySpec(finalSecretKey.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(getIV(finalSecretKey));
        String ivtobetransfered = "";
        ivtobetransfered = new String(java.util.Base64.getEncoder().encode(iv.getIV()));
        LOG.debug("iv -:" + ivtobetransfered);
        String encryptedHeader = getAESencryptedForJS(key, iv, header);
        String encryptedBody = getAESencryptedForJS(key, iv, body);
        String encryptedError= "";
        if(appzillonResponse.has(ServerConstants.MESSAGE_ERROR)){
            errors=appzillonResponse.getJSONArray(ServerConstants.MESSAGE_ERROR).toString(0);
            encryptedError=getAESencryptedForJS(key, iv, errors);
            appzillonResponse.put(ServerConstants.MESSAGE_ERROR,encryptedError);
        }
        appzillonResponse.put(ServerConstants.MESSAGE_HEADER,encryptedHeader);
        appzillonResponse.put(ServerConstants.MESSAGE_BODY,encryptedBody);
        appzillonResponse.put(ServerConstants.MESSAGE_SAFE,ivtobetransfered);

        if (ServerConstants.FAILURE.equalsIgnoreCase(encryptedHeader) || ServerConstants.FAILURE.equalsIgnoreCase(encryptedBody)) {
            String result = "";
            JSONObject res ;
            try {
                res = new JSONObject();
                res.put(ServerConstants.STATUS, false);
                res.put(ServerConstants.APPZILLON_ERROR_CODE, ServerConstants.PAYLOAD_ENCRYPTION_ERROR_CODE);
                result = res.toString();
            } catch (JSONException e) {
                LOG.error("JSONException Occurred!!! ", e);
            }
        }
        LOG.debug("Transitted Text encrypted Data -:" + encryptedHeader);
        LOG.debug("Transitted Text encrypted Data -:" + encryptedBody);
        LOG.debug("Transitted Text encrypted Data -:" + encryptedError);
        LOG.debug("-----------------------------Encrypted--------------------");
        responseJson=appzillonResponse.toString(0);
        return responseJson;
    }

    public static String decryptRequest(String ivString,String encryptedPayload) {
        LOG.debug("-----------------------------Decrypting--------------------");
        String decryptedString = "";
        LOG.debug("Incoming CipherText :" + encryptedPayload);
        String secretKey = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,ServerConstants.APPZILLON_SAFE_KEY);
        LOG.debug("Key :" + secretKey);
        LOG.debug("iv :" + ivString);
        LOG.debug("Encrypted Text : " + encryptedPayload);
        String finalSecretKey = getKey(secretKey);
        byte[] keytobeUsed = Arrays.copyOfRange(finalSecretKey.getBytes(), 0, 16);
        SecretKeySpec key = new SecretKeySpec(keytobeUsed, "AES");
        byte[] ivtobeused = Arrays.copyOfRange(Base64.decodeBase64(ivString), 0, 16);
        IvParameterSpec iv = new IvParameterSpec(ivtobeused);
        decryptedString = getAESdecryptedFromJS(key, iv, encryptedPayload);
        if (ServerConstants.FAILURE.equalsIgnoreCase(decryptedString)) {
            JSONObject res = null;
            try {
                res = new JSONObject();
                res.put(ServerConstants.STATUS, false);
                res.put(ServerConstants.APPZILLON_ERROR_CODE, ServerConstants.PAYLOAD_ENCRYPTION_ERROR_CODE);
                decryptedString = res.toString();
            } catch (JSONException e) {
                LOG.error("JSONException Occurred!!! ", e);
            }
        }
        LOG.debug("Decrypted Text : " + decryptedString);
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
            cipherText = ServerConstants.FAILURE;
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
            decryptedText = new String(decryptedData, StandardCharsets.UTF_8);
            LOG.debug("Decrypted Text :" + decryptedText);
        } catch (Exception ex) {
            LOG.error("Exception Occurred!!! ", ex);
            decryptedText = ServerConstants.FAILURE;
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

    private static String getKey(String key) {
        LOG.debug("get Key value");
        String result = null;
        if (key.length() < 16) {
            StringBuilder temp = new StringBuilder(key);
            for (int i = key.length(); i < 16; i++) {
                temp.append(ServerConstants.DOLLAR);
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

    private static String getAppzillonPayload(String qop, String header, String body){
        StringBuilder requestPayload = new StringBuilder();
        requestPayload.append("{\"").append(ServerConstants.QOP).append("\":\"").append(qop).append("\",").append("\"")
                .append(ServerConstants.MESSAGE_HEADER).append("\":").append(header).append(",").append("\"")
                .append(ServerConstants.MESSAGE_BODY).append("\":").append(body).append("}");
        return requestPayload.toString();
    }
}
