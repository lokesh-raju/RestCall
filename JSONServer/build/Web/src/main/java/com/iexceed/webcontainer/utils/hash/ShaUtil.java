/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.webcontainer.utils.hash;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author arthanarisamy
 */
public class ShaUtil {

	private static Sha4J sha4j = new Sha4J();
	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private ShaUtil() {}

	// SHA-1
	public static byte[] sha1(String input) throws IOException {
		sha4j.reset();
		return sha4j.sha1Digest(new ByteArrayInputStream(input.getBytes()));
	}

	public static byte[] sha1(InputStream input) throws IOException {
		sha4j.reset();
		return sha4j.sha1Digest(input);
	}

	public static byte[] sha1(File input) throws IOException {
		sha4j.reset();
		final FileInputStream fis = new FileInputStream(input);
		return sha4j.sha1Digest(fis);
	}

	public static String toSha1String(String input) throws IOException {
		return toHexString(sha1(input));
	}

	public static String toSha1String(InputStream input)
			throws IOException {
		return toHexString(sha1(input));
	}

	public static String toSha1String(File input) throws IOException {
		return toHexString(sha1(input));
	}

	// SHA-224
	public static byte[] sha224(String input) throws IOException {
		sha4j.reset();
		return sha4j.sha224Digest(new ByteArrayInputStream(input.getBytes()));
	}

	public static byte[] sha224(InputStream input) throws IOException {
		sha4j.reset();
		return sha4j.sha224Digest(input);
	}

	public static byte[] sha224(File input) throws IOException {
		sha4j.reset();
		final FileInputStream fis = new FileInputStream(input);
		return sha4j.sha224Digest(fis);
	}

	public static String toSha224String(String input) throws IOException {
		return toHexString(sha224(input));
	}

	public static String toSha224String(InputStream input)
			throws IOException {
		return toHexString(sha224(input));
	}

	public static String toSha224String(File input) throws IOException {
		return toHexString(sha224(input));
	}

	// SHA-256
	public static byte[] sha256(String input) throws IOException {
		sha4j.reset();
		return sha4j.sha256Digest(new ByteArrayInputStream(input.getBytes()));
	}

	public static byte[] sha256(InputStream input) throws IOException {
		sha4j.reset();
		return sha4j.sha256Digest(input);
	}

	public static byte[] sha256(File input) throws IOException {
		sha4j.reset();
		final FileInputStream fis = new FileInputStream(input);
		return sha4j.sha256Digest(fis);
	}

	public static String toSha256String(String input) throws IOException {
		return toHexString(sha256(input));
	}

	public static String toSha256String(InputStream input)
			throws IOException {
		return toHexString(sha256(input));
	}

	public static String toSha256String(File input) throws IOException {
		return toHexString(sha256(input));
	}

	// SHA-384
	public static byte[] sha384(String input) throws IOException {
		sha4j.reset();
		return sha4j.sha384Digest(new ByteArrayInputStream(input.getBytes()));
	}

	public static byte[] sha384(InputStream input) throws IOException {
		sha4j.reset();
		return sha4j.sha384Digest(input);
	}

	public static byte[] sha384(File input) throws IOException {
		sha4j.reset();
		final FileInputStream fis = new FileInputStream(input);
		return sha4j.sha384Digest(fis);
	}

	public static String toSha384String(String input) throws IOException {
		return toHexString(sha384(input));
	}

	public static String toSha384String(InputStream input)
			throws IOException {
		return toHexString(sha384(input));
	}

	public static String toSha384String(File input) throws IOException {
		return toHexString(sha384(input));
	}

	// SHA-512
	public static byte[] sha512(String input) throws IOException {
		sha4j.reset();
		return sha4j.sha512Digest(new ByteArrayInputStream(input.getBytes()));
	}

	public static byte[] sha512(InputStream input) throws IOException {
		sha4j.reset();
		return sha4j.sha512Digest(input);
	}

	public static byte[] sha512(File input) throws IOException {
		sha4j.reset();
		final FileInputStream fis = new FileInputStream(input);
		return sha4j.sha512Digest(fis);
	}

	public static String toSha512String(String input) throws IOException {
		return toHexString(sha512(input));
	}

	public static String toSha512String(InputStream input)
			throws IOException {
		return toHexString(sha512(input));
	}

	public static String toSha512String(File input) throws IOException {
		return toHexString(sha512(input));
	}

	public static String toHexString(byte[] lByte) {
		StringBuilder strBuff = new StringBuilder();
		for (int i = 0; i < lByte.length; i++) {
			int lChar = ((lByte[i]) >>> 4) & 0xf;
			strBuff.append(HEX[lChar]);
			lChar = (int) lByte[i] & 0xf;
			strBuff.append(HEX[lChar]);
		}
		return strBuff.toString();
	}
}