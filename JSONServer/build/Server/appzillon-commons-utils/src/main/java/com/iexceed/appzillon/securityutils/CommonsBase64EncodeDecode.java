package com.iexceed.appzillon.securityutils;

import org.apache.commons.codec.binary.Base64;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author arthanarisamy Class helps in encoding and decoding using Base64
 * techniques It is wrapped over apache's commons Has two methods namely
 * encodeBase64String() = to encode the string decodeString() = to decode the
 * input encoded string
 */
public class CommonsBase64EncodeDecode {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, CommonsBase64EncodeDecode.class.toString());
    /*
     * @author arthanarisamy
     * encodeBase64String()
     * @param String
     * @returns String
     * 
     * Method is a wrapper for Base64 apache's commons encoding.
     * Takes input/plain string as input.
     * Returns encoded string
     */
    public static String encodeBase64String(String pInputStr) {
        String lEncodedStr = null;
        try {
            lEncodedStr = Base64.encodeBase64String(pInputStr.getBytes());

        } catch (Exception ex) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.EXCEPTION,ex);
        }
        return lEncodedStr;
    }

    /*
     * @author arthanarisamy
     * decodeString()
     * @param String
     * @returns String
     * 
     * Method is a wrapper for Base64 apache'scommons decoding.
     * Takes encoded string as input.
     * Returns decoded/plain string
     */
    public static String decodeString(String pEncodedStr) {
        String lDecodedStr = null;

        try {
            // Calling decodeBase64() to decode the encoded string
            byte[] decodedBytes = Base64.decodeBase64(pEncodedStr);
            // Converting byte array to string
            lDecodedStr = new String(decodedBytes);
        } catch (Exception ex) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.EXCEPTION,ex);
        }
        return lDecodedStr;
    }
}
