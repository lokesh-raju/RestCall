/**
 * 
 */
package com.iexceed.appzillon.exception;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 * @author arthanarisamy Created on 10-07-2013
 * 
 */
public class Utils {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, Utils.class.toString());

	private static String cStackTrace = null;

	/*
	 * @author arthanarisamy
	 * 
	 * @param pEx
	 * 
	 * @return String
	 * 
	 * getStackTrace() method takes Exception as its parameter and returns the
	 * stack trace of the exception as a string which helps in logging and
	 * better debugging Created on 10-07-2013
	 */
	// Server Appzillon �RS Ref� Changes (Server Appzillon 2.1 ) - Start
	// New utility method added
	public static String getStackTrace(Exception pEx) {
		StringWriter lSw = null;
		PrintWriter lPw = null;
		try {
			// Creating String writer Object
			lSw = new StringWriter();
			// Creating print writer object
			lPw = new PrintWriter(lSw);
			// Getting stack trace and storing it in print writer obj
			pEx.printStackTrace(lPw);
			// Storing the stack trace string to the string object
			cStackTrace = lSw.toString(); // StackTrace as a string
		} catch (Exception ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, ex);

		}
		// returning stack trace string
		return cStackTrace;
	}

	/*
	 * @author arthanarisamy
	 * 
	 * @param pInputXMLStr
	 * 
	 * @param pQualifiedClassName
	 * 
	 * @return object
	 * 
	 * getUnMarshalled() returns Java Object after unmarshalling the input
	 * request XML string.
	 * 
	 * Firstly loads the Class at run time from the fully qualified class name.
	 * Create JAXBContext taking dynamically created class. Create JAXB
	 * UnMarshaller from the JAXB Context Create StringReader passing the input
	 * xml string. UnMarshall to Java object passing the String reader. Returns
	 * UnMarshalled Object
	 */
	// Converting XML to Object using JAXB
	public static Object getUnMarshalled(String pInputXMLStr, String pQualifiedClassName) {
		Object lRespObject = null;

		try {
			// Loading Class at Run time from the fully qualified class name
			@SuppressWarnings("rawtypes")
			Class lC = Class.forName(pQualifiedClassName);

			// Creating JAXBContext instance
			JAXBContext lJaxbContext = JAXBContext.newInstance(lC);

			// Creating JAXB UnMarshaller Instance
			Unmarshaller lJaxbUnmarshaller = lJaxbContext.createUnmarshaller();
			// Creating/Converting input XML string to String Reader object
			StringReader lInStringReader = new StringReader(pInputXMLStr);
			// Passing String reader Object for UnMarshalling
			lRespObject = lJaxbUnmarshaller.unmarshal(lInStringReader);
		} catch (ClassNotFoundException pCnfex) {
			// Class not found Exception
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, pCnfex);
		} catch (JAXBException pJaxex) {
			// JAXB Exception
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, pJaxex);
		}
		// Returning UnMarshalled Object
		return lRespObject;
	}

	/*
	 * @author arthanarisamy
	 * 
	 * @param pInputXMLStr
	 * 
	 * @param pQualifiedClassName
	 * 
	 * @return object
	 * 
	 * getMarshalled() returns String after Marshalling the input response
	 * Object.
	 * 
	 * Firstly loads the Class at run time from the fully qualified class name.
	 * Create JAXBContext taking dynamically created class. Create JAXB
	 * Marshaller from the JAXB Context Create StringWriter Marshall the Java
	 * object to XML String Returns Marshalled XML String
	 */
	// Converting Object to XML using JAXB

	public static String getMarshalled(Object pResponseObj, String pQualifiedClassName) {

		String lResponse = null;
		try {

			// Loading Class at Run time from the fully qualified class name
			@SuppressWarnings("rawtypes")
			Class lC = Class.forName(pQualifiedClassName);

			// Creating JAXBContext instance
			JAXBContext lJaxbContext = JAXBContext.newInstance(lC);

			StringWriter stringWriter = new StringWriter();

			// Creating JAXB Marshaller Instance
			Marshaller lJaxbmarshaller = lJaxbContext.createMarshaller();
			// Setting property to format the xml
			lJaxbmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// Passing the response object and Stringwriter to marshall
			lJaxbmarshaller.marshal(pResponseObj, stringWriter);
			// assigning the marshelled xml string to string object
			lResponse = stringWriter.toString();
		} catch (JAXBException pJaxex) {
			// JAXB Exception
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, pJaxex);
		} catch (ClassNotFoundException pCnfex) {
			// Class not found Exception
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, pCnfex);
		}
		// Returning response XML
		return lResponse;
	}

	public static String[] split(String original, String separator) {
		Vector<String> nodes = new Vector<String>();
		String[] result = null;
		String lOriginal = original;
		try {
			int index = lOriginal.indexOf(separator);
			while (index >= 0) {
				nodes.addElement(lOriginal.substring(0, index));
				lOriginal = lOriginal.substring(index + separator.length());
				index = lOriginal.indexOf(separator);
			}
			if (isNotNullOrEmpty(lOriginal.trim())) {
				nodes.addElement(lOriginal);
			}
			result = new String[nodes.size()];
			if (!nodes.isEmpty()) {
				for (int loop = 0; loop < nodes.size(); loop++) {
					result[loop] = (String) nodes.elementAt(loop);
				}
			}
		} catch (Exception e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, e);
		}
		return result;
	}

	public static Object[] splitToObj(String original, String separator) {
		Vector<String> nodes = new Vector<String>();
		Object[] result = null;
		try {
			int index = original.indexOf(separator);
			while (index >= 0) {
				nodes.addElement(original.substring(0, index));
				original = original.substring(index + separator.length());
				index = original.indexOf(separator);
			}
			if (isNotNullOrEmpty(original.trim())) {
				nodes.addElement(original);
			}
			result = new String[nodes.size()];
			if (!nodes.isEmpty()) {
				for (int loop = 0; loop < nodes.size(); loop++) {
					result[loop] = (String) nodes.elementAt(loop);
				}
			}
		} catch (Exception e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, e);
		}
		return result;
	}
	// Server Appzillon �RS Ref� Changes (Server Appzillon 2.1 ) - End

	public static String getPaddedString(String data, int len, char padder, boolean isPrefix) {
		String toReturn = "";
		try {
			if(data.length() < len){
				while (data != null && data.length() < len) {
					if (isPrefix) {
						data = "" + padder + data;
					} else {
						data = data + padder;
					}
				}
			} else if(data.length() > len) {
				data = data.substring(data.length() - len, data.length());
			}
			toReturn = data;
		} catch (Exception e) {
			toReturn = data;
		}
		return toReturn;
	}

	public static String generateRandomofLength(int length) {
		SecureRandom random = new SecureRandom();
		char[] digits = new char[length];
		digits[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < length; i++) {
			digits[i] = (char) (random.nextInt(10) + '0');
		}
		return new String(digits);
	}
	
	public static String generateRandomNo() {
		SecureRandom random = new SecureRandom();
		StringBuilder lreqRef = new StringBuilder();
		lreqRef.append(System.currentTimeMillis());
		lreqRef.append("");
		lreqRef.append(random.nextInt(1000000000));
		return lreqRef.toString();
	}

    /**
     *
     * @param length
     * @param type
     * @return
     */

	 public static String generateRandomofLength(int length,String type) {
		 
		 String result = new String();
		 SecureRandom secureRandom = new SecureRandom();
		 
		 if(type.equalsIgnoreCase(ServerConstants.OTP_ALPHA_NUMERIC)){
		 String alphanumeric = new String(ServerConstants.A_TO_Z_a_TO_z_0_9);
			int size = alphanumeric.length();			

			for (int i=0; i<length; i++){
			 result = result + alphanumeric.charAt(secureRandom.nextInt(size));
			}	
	     }else if(type.equalsIgnoreCase(ServerConstants.OTP_ALPHA)){
			 String alphanumeric = new String(ServerConstants.A_TO_Z_a_TO_z);
				int size = alphanumeric.length();	
				Random random = new Random();
				for (int i=0; i<length; i++){
				 result = result + alphanumeric.charAt(secureRandom.nextInt(size));
			}	
		 }else if(type.equalsIgnoreCase(ServerConstants.OTP_NUMERIC)){
			 String alphanumeric = new String(ServerConstants.ZERO_TO_NINE);
				int size = alphanumeric.length();				
				for (int i=0; i<length; i++){
				 result = result + alphanumeric.charAt(secureRandom.nextInt(size));
				}	
			 }	
	     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" OTP generated is :: " + result);
		 return result;
	 }

    /**
     * returns true if the string is not null and not empty
     * otherwise returns false
     * @param pValue
     * @return
     */

	public static boolean isNotNullOrEmpty(String pValue) {
		return pValue != null && !pValue.isEmpty() ? true : false;
	}

    /**
     *returns true if the string is null or empty
     * otherwise returns false
     * @param pValue
     * @return
     */

	public static boolean isNullOrEmpty(String pValue) {

		return pValue == null || pValue.isEmpty() ? true : false;
	}
	
	
	public static void setExtTime(Message message,String flag){
		if(flag.equalsIgnoreCase("S")){
			message.getHeader().setExtStartTime(new Timestamp(new Date().getTime()));
		}else if(flag.equalsIgnoreCase("E")){
		    message.getHeader().setExtEndTime(new Timestamp(new Date().getTime()));
        }
	}
	
	public static String getTxnRefNum(String pUserId) {
		SecureRandom random = new SecureRandom();
		int randomno = random.nextInt(1000000);
		if(isNullOrEmpty(pUserId))
			return System.currentTimeMillis() + "" + randomno;
		else
		  return (pUserId + System.currentTimeMillis() + "" + randomno);
	}
	
	public static boolean checkQualityOfPayload(Message pMessage) {
		String inputString = pMessage.getHeader().getInputString();
		LOG.debug("Raw Request payload to be hashed :" + inputString);
		int i = inputString.indexOf(ServerConstants.QOP);
		String qop = inputString.substring(i+15,i+79);
        LOG.debug("QOP from Request Payload:"+qop);
		inputString = inputString.replaceAll(inputString.substring(i-1,i+81), "");
		String lHashedCnonce = HashUtils.hashSHA256(pMessage.getHeader().getClientNonce(),
				pMessage.getHeader().getServerNonce() + pMessage.getSecurityParams().getServerToken());
        LOG.debug("Request payload After removing QOP:"+inputString);
        LOG.debug("Request payload After removing QOP length:"+inputString.length());
		inputString =   Base64.encodeBase64String(inputString.getBytes());
		LOG.debug("Request payload Base64 encoded:"+inputString);
		String hashedPayLoad = HashUtils.hashSHA256(inputString, lHashedCnonce);
        LOG.debug("Request payload hashed value:"+hashedPayLoad);
		if (qop.equals(hashedPayLoad)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getFileNameForMailSMSTemplate(String appId, String pFileName) {
		if(PropertyUtils.class.getClassLoader().getResourceAsStream(appId + "_" + pFileName) == null) {
			return pFileName;
		}
		return appId + "_" + pFileName;
	}

    public static String getConstructedBody(String template, JSONObject fillersNValue) {

		Iterator fillers = fillersNValue.keys();
		while (fillers.hasNext()) {
			String key = (String) fillers.next();
			template = template.replace(key, fillersNValue.getString(key));
		}

        return template;

    }


}
