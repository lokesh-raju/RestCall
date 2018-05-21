package com.iexceed.appzillon.securityutils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

public class HashUtils {
	/* Function to convert SHA256 to Hexadecimal */
	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, HashUtils.class.toString());

	public static String toHexString(byte[] b) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "converting to HexString");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			int c = ((b[i]) >>> 4) & 0xf;
			sb.append(HEX[c]);
			c = (b[i] & 0xf);
			sb.append(HEX[c]);
		}
		return sb.toString();
	}

	/* To encrypt ptext with salt using SHA-256 hash Algo */
	public static String hashSHA256(String pText, String pSalt) {
		String pTextSalt = pText + pSalt;
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
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "EncryptHash@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
					+ pHashedText);

		} catch (NoSuchAlgorithmException n) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "NoSuchAlgorithmException...", n);
		} catch (UnsupportedEncodingException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "UnsupportedEncodingException...", e);
		}
		return pHashedText;
	}
}
