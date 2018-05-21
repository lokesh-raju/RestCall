/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.webcontainer.utils.hash;



import java.io.IOException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.codec.binary.Base64;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;

public class HashXor {
	
	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(HashXor.class.getName());

	public static String hashValue(String puname,  String pInpin,
			 String pDate) {
		byte[] encodedBytes = null;
		 String uname = puname;		
		 String inpin = pInpin;
		 String inconcatstr;
		 String dtstr = pDate;
		 String day = dtstr.substring(0, 3);
		 String hour = dtstr.substring(16, 18);
		 String min = dtstr.substring(19, 21);
		 String sec = dtstr.substring(22, 24);
		 String year = dtstr.substring(13, 15);
		 String dDate = dtstr.substring(5, 7);
		 String mMonth = dtstr.substring(8, 10);
		inconcatstr = hour + min + day + year + dDate + mMonth + sec
					+ uname;
		try {

			 HashXor hashXor = new HashXor();
			// XOR Sha-256 values of concatenated String and PIN
			 String hshxor = hashXor.xorHex(
					ShaUtil.toSha256String(inconcatstr),
					ShaUtil.toSha256String(inpin));
			// First Byte of XOR'ed Sha-256 value of concatenated String and PIN
			 String fbyte = hshxor.substring(0, 16);
			// Last Byte of XOR'ed Sha-256 value of concatenated String and PIN
			 String lbyte = hshxor.substring(48);
			// XOR First And Last Bytes
			 String fblbxor = hashXor.xorHex(fbyte, lbyte);
			// XOR Above with Pin Hash
			 String hshxor2 = hashXor.xorHex(fblbxor,
					ShaUtil.toSha256String(inpin));

			// Round 2 -> First Byte of XOR'ed Sha-256 value of concatenated
			// String and PIN
			 String fbyte2 = hshxor2.substring(0, 8);
			// Round 2 -> Last Byte of XOR'ed Sha-256 value of concatenated
			// String and PIN
			 String lbyte2 = hshxor2.substring(8);
			// Round 2 -> XOR First And Last Bytes
			 String fblbxor2 = hashXor.xorHex(fbyte2, lbyte2);
			// Round 2 -> XOR Above with Pin Hash//
			 String finalstr = hashXor.xorHex(fblbxor2,
					ShaUtil.toSha256String(inpin));

			// Finally the base64 encoded value BASE64Encoder encoder  new BASE64Encoder()
			encodedBytes = Base64.encodeBase64(finalstr.getBytes());
		} catch (IOException e) {
			LOG.error("hashValue ", e);
		}
		return new String(encodedBytes);
	}

	public static String toHexString(byte[] array) {
		return DatatypeConverter.printHexBinary(array);
	}

	public static byte[] toByteArray(String lString) {
		return DatatypeConverter.parseHexBinary(lString);
	}

	public static byte[] hexToBytes(String hexString) {
		 HexBinaryAdapter adapter = new HexBinaryAdapter();
		return adapter.unmarshal(hexString);
	}

	public static byte[] hexStringToByteArray(String lString) {
		 int len = lString.length();
		 byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(lString.charAt(i), 16) << 4) + Character
					.digit(lString.charAt(i + 1), 16));
		}
		return data;
	}

	// XOR Truth Table from two Hex Strings
	public final String xorHex(String lString1, String lString2) {
		 char[] chars = new char[lString1.length()];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = this.toHex(fromHex(lString1.charAt(i))
					^ fromHex(lString2.charAt(i)));
		}
		return new String(chars);
	}

	// char wise Hex to int
	private static int fromHex(char lChar) {
		if (lChar >= '0' && lChar <= '9') {
			return lChar - '0';
		}
		if (lChar >= 'A' && lChar <= 'F') {
			return lChar - 'A' + 10;
		}
		if (lChar >= 'a' && lChar <= 'f') {
			return lChar - 'a' + 10;
		}
		throw new IllegalArgumentException();
	}

	// char wise int to Hex
	private char toHex(int nybble) {
		if (nybble < 0 || nybble > 15) {
			throw new IllegalArgumentException();
		}
		return "0123456789ABCDEF".charAt(nybble);
	}
}
