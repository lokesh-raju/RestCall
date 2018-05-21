package com.iexceed.appzillon.securityutils;

import org.owasp.esapi.codecs.Base64;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

public class ESAPIBase64EncodeDecode {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, ESAPIBase64EncodeDecode.class.toString());
	
	public static String encode(String pInput){
		String lEncodedStr= null;
		try{
			lEncodedStr = Base64.encodeBytes(pInput.getBytes(), Base64.GZIP);
		}catch(Exception ex){
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.EXCEPTION,ex);
		}
		return lEncodedStr;
	}
	
	public static String decode(String pEncodedStr){
		String lDecodedStr = null;
		try{
			byte[] decoded = Base64.decode(pEncodedStr);
			lDecodedStr = new String(decoded);
		}catch(Exception ex){
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.EXCEPTION,ex);
		}
		return lDecodedStr;
	}

}
