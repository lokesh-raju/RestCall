package com.iexceed.appzillon.sms.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.codec.binary.Base64;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

public class HashXor {
	
	  private static final Logger LOG = LoggerFactory.getLoggerFactory().getLogger(
			  ServerConstants.LOGGER_SMS);
	 
	public static void main(String[] args) throws IOException {
		FileWriter fw = new FileWriter("25KOTP_MultipleUname.txt", true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);		
		
		String uname = "1114";
		String inpin = "1114";

		for (int k = 1; k <= 1; k++) {

			String imie = "40:6C:8F:39:C2:40";
			String imsi = "";			

			String inconcatstr;

			String dtstr = "Mon, 08-04-2013 17:26:05";

			String day = dtstr.substring(0, 3);
			String hr = dtstr.substring(16, 18);
			String min = dtstr.substring(19, 21);
			String sec = dtstr.substring(22, 24);
			String yr = dtstr.substring(13, 15);
			String dd = dtstr.substring(5, 7);
			String mm = dtstr.substring(8, 10);
			inconcatstr = hr + min + day + yr + dd + mm + sec + uname + imie
					+ imsi;
			// Get the concatenated value
			try {
				// Sha-256 value of concatenated String
				// Sha-256 value of 4-digit pin repeated twice to get 8-digits
				HashXor xx = new HashXor();
				// XOR Sha-256 values of concatenated String and PIN
				String hshxor = xx.xorHex(ShaUtil.toSha256String(inconcatstr),
						ShaUtil.toSha256String(inpin ));
				// First Byte of XOR'ed Sha-256 value of concatenated String and
				// PIN
				String fbyte = hshxor.substring(0, 16);
				// Last Byte of XOR'ed Sha-256 value of concatenated String and
				// PIN
				String lbyte = hshxor.substring(48);
				// XOR First And Last Bytes
				String fblbxor = xx.xorHex(fbyte, lbyte);
				// XOR Above with Pin Hash
				String hshxor2 = xx.xorHex(fblbxor,
						ShaUtil.toSha256String(inpin));
				// Round 2 -> First Byte of XOR'ed Sha-256 value of concatenated
				// String and PIN
				String fbyte2 = hshxor2.substring(0, 8);
				// Round 2 -> Last Byte of XOR'ed Sha-256 value of concatenated
				// String and PIN

				String lbyte2 = hshxor2.substring(8);
				// Round 2 -> XOR First And Last Bytes
				String fblbxor2 = xx.xorHex(fbyte2, lbyte2);

				// Round 2 -> XOR Above with Pin Hash
				//
				String finalstr = xx.xorHex(fblbxor2,
						ShaUtil.toSha256String(inpin ));

				

				// Finally, the base64 encoded value
				// BASE64Encoder encoder = new BASE64Encoder()
				byte[] encodedBytes = Base64.encodeBase64(finalstr.getBytes());								
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " Encoded Bytes " + new String(encodedBytes));
				
				pw.print(encodedBytes);				

			} catch (Exception e) {
				
				LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "Usage: Sha256 <text> or Sha256 -f<filename>", e);
			}

		}

		bw.close();
		fw.close();
		pw.close();
	}

	
	public String hashValue(String pimie, String pimsi, String puid,
			String puname, String pInpin, String pDate) {
		byte[] encodedBytes = null;		
		String uname = puname;
		String inpin = pInpin;
		String imie = pimie;
		String imsi = pimsi;

		String inconcatstr;
		String dtstr = pDate;
		
		String day = dtstr.substring(0, 3);
		String hr = dtstr.substring(16, 18);
		String min = dtstr.substring(19, 21);
		String sec = dtstr.substring(22, 24);
		String yr = dtstr.substring(13, 15);
		String dd = dtstr.substring(5, 7);
		String mm = dtstr.substring(8, 10);
		if (puid.isEmpty()){
			inconcatstr = hr + min + day + yr + dd + mm + sec + uname + imie
					+ imsi;
		}else{
			inconcatstr = hr + min + day + yr + dd + mm + sec + uname + puid;	
		}
		try {

			HashXor xx = new HashXor();
			// XOR Sha-256 values of concatenated String and PIN
			String hshxor = xx.xorHex(ShaUtil.toSha256String(inconcatstr),
					ShaUtil.toSha256String(inpin));
			// First Byte of XOR'ed Sha-256 value of concatenated String and PIN
			String fbyte = hshxor.substring(0, 16);
			// Last Byte of XOR'ed Sha-256 value of concatenated String and PIN
			String lbyte = hshxor.substring(48);
			// XOR First And Last Bytes
			String fblbxor = xx.xorHex(fbyte, lbyte);
			// XOR Above with Pin Hash
			String hshxor2 = xx.xorHex(fblbxor,
					ShaUtil.toSha256String(inpin));

			// Round 2 -> First Byte of XOR'ed Sha-256 value of concatenated
			// String and PIN
			String fbyte2 = hshxor2.substring(0, 8);
			// Round 2 -> Last Byte of XOR'ed Sha-256 value of concatenated
			// String and PIN
			String lbyte2 = hshxor2.substring(8);
			// Round 2 -> XOR First And Last Bytes
			String fblbxor2 = xx.xorHex(fbyte2, lbyte2);
			// Round 2 -> XOR Above with Pin Hash//
			String finalstr = xx.xorHex(fblbxor2,
					ShaUtil.toSha256String(inpin));
			
			// Finally, the base64 encoded value
			// BASE64Encoder encoder = new BASE64Encoder()
			encodedBytes = Base64.encodeBase64(finalstr.getBytes());
			
			

		} catch (Exception e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "Exception in hashXor", e);	
		}
		return new String(encodedBytes);

	}

	public static String toHexString(byte[] array) {
		return DatatypeConverter.printHexBinary(array);
	}

	public static byte[] toByteArray(String s) {
		return DatatypeConverter.parseHexBinary(s);
	}
	
	public static byte[] hexToBytes(String hexString) {
		HexBinaryAdapter adapter = new HexBinaryAdapter();
		return adapter.unmarshal(hexString);
	}

	
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	// XOR Truth Table from two Hex Strings
	public String xorHex(String a, String b) {
		char[] chars = new char[a.length()];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = toHex(fromHex(a.charAt(i)) ^ fromHex(b.charAt(i)));
		}
		return new String(chars);
	}

	// char wise Hex to int
	private static int fromHex(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		} else if (c >= 'A' && c <= 'F') {
			return c - 'A' + 10;
		} else if (c >= 'a' && c <= 'f') {
			return c - 'a' + 10;
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