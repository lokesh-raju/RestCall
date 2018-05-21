package com.iexceed.webcontainer.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AppzillonAESUtils {
	
    private static final String SHA_1 = "SHA-1";
    private static final String UTF_8 = "UTF-8";
    private static final String AES = "AES";

    public  static String decryptString(String cypher, String key, String textToDecrypt, String salt,byte[] iv, String pOS) {
        SecretKeySpec skeySpec = null;

            skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");

        try {
            Cipher cipher = Cipher.getInstance(cypher);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParams);
            byte[] plaintext = cipher.doFinal(Base64.decodeBase64(textToDecrypt));
            String plainrStr = new String(plaintext, "UTF-8");
            return new String(plainrStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public  static String encryptString(String cypher, String key,String clearText, String salt,byte[] iv, String pOS) {
        SecretKeySpec skeySpec = null;
            skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");
        try {
            Cipher cipher = Cipher.getInstance(cypher);
            //iv = ivText.getBytes();
            // random.nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParams);
            byte[] encryptedData = cipher.doFinal(clearText.getBytes("UTF-8"));
            if (encryptedData == null)
                return null;
            return Base64.encodeBase64String(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        /**/
    }
    public static byte[] hmacSha1(String salt, String key) {
        SecretKeyFactory factory = null;
        Key keyByte = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keyspec = new PBEKeySpec(key.toCharArray(),
                    salt.getBytes("UTF-8"), 2, 128);
            keyByte = factory.generateSecret(keyspec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return keyByte.getEncoded();
    }
    public static String getDecryptedPayload(String key, String pRequestBody) {
        String paddingMask = "$$$$$$$$$$$$$$$$";
        if (key.length() <= 16) {
            key += paddingMask.substring(0, 16 - key.length());
        }
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        byte[] iv = getIV(key);
        String finalSalt = getSalt(key);
        String decryptedAppzillonBody =  decryptString("AES/CBC/PKCS5padding", key, pRequestBody,finalSalt, iv, "WEB");

        return decryptedAppzillonBody;
    }
    public static String getSalt(String key) {

        String originalString = key;

        char[]c = originalString.toCharArray();

        // Replace with a "swap" function, if desired:
        char temp = c[0];
        c[0] = c[1];
        c[1] = temp;

        temp = c[c.length - 1];
        c[c.length - 1] = c[c.length - 2];
        c[c.length - 2] = temp;
        String swappedString = new String(c);
        return swappedString;
    }
    public static byte[] getIV(String key) {
        byte[]iv = new byte[16];
        java.util.Arrays.fill(iv, (byte)0);
        StringBuffer or = new StringBuffer(key);
        String nw = or.reverse().toString();
        byte[]keyBytes = null;
        try {
            keyBytes = nw.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[]rawIV = new byte[keyBytes.length];
        for (int i = 0; i < keyBytes.length; i++) {
            rawIV[i] = (byte)(keyBytes[i] >> 1);
        }
        for (int i = 0; i < iv.length; i++) {
            iv[i] = rawIV[i];
        }
        return iv;
    }

    public static String getEncryptePayload(String key, String pResponseBody) {
        String paddingMask = "$$$$$$$$$$$$$$$$";
        if (key.length() <= 16) {
            key += paddingMask.substring(0, 16 - key.length());
        }
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        byte[] iv = getIV(key);
        String finalSalt = getSalt(key);
        String   encryptedAppzillonBody = encryptString("AES/CBC/PKCS5padding",key, pResponseBody, finalSalt, iv, "WEB");

        return encryptedAppzillonBody;
    }


}