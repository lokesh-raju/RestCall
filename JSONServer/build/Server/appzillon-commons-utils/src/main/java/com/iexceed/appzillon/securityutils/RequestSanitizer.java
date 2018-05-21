package com.iexceed.appzillon.securityutils;

import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utilsexception.UtilsException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.DefaultEncoder;
import org.owasp.esapi.reference.DefaultValidator;

public class RequestSanitizer extends DefaultEncoder {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, RequestSanitizer.class.toString());
 

	public RequestSanitizer(List<String> arg0) {

		super(arg0);
	}

	public static RequestSanitizer getInstance(String pAppId) {
		 RequestSanitizer sanitizer = null;
		if (sanitizer == null) {
			List<String> codecs = new ArrayList<String>();
			if ("Y".equals(PropertyUtils.getPropValue(pAppId, ServerConstants.MYSQL_CODEC_REQUIRED).toString()
					.trim())) {
				codecs.add(ServerConstants.MYSQL_CODEC);

			}
			if ("Y".equals(PropertyUtils.getPropValue(pAppId, ServerConstants.ORACLE_CODEC_REQUIRED).toString()
					.trim())) {
				codecs.add(ServerConstants.ORACLE_CODEC);

			}
			if ("Y".equals(PropertyUtils.getPropValue(pAppId, ServerConstants.PERCENT_CODEC_REQUIRED).toString()
					.trim())) {
				codecs.add(ServerConstants.PERCENT_CODEC);

			}
			if ("Y".equals(PropertyUtils.getPropValue(pAppId, ServerConstants.PUSHBACK_STRING_REQUIRED).toString()
					.trim())) {
				codecs.add(ServerConstants.PUSHBACK_STRING);

			}
			if ("Y".equals(PropertyUtils.getPropValue(pAppId, ServerConstants.HTML_CODEC_REQUIRED)
					.toString().trim())) {
				codecs.add(ServerConstants.HTML_CODEC);
			}
			if ("Y".equals(PropertyUtils.getPropValue(pAppId, ServerConstants.XML_CODEC_REQUIRED).toString()
					.trim())) {
				codecs.add(ServerConstants.XML_CODEC);

			}
			if ("Y".equals(PropertyUtils.getPropValue(pAppId, ServerConstants.JS_CODEC_REQUIRED)
					.toString().trim())) {
				codecs.add(ServerConstants.JS_CODEC);

			}
			if (!codecs.isEmpty()) {
				sanitizer = new RequestSanitizer(codecs);
			} else {
				UtilsException u = UtilsException.getUtilsExceptionInstance();
				u.setMessage(u
						.getUtilsExceptionMessage(UtilsException.Code.APZ_UT_000));
				u.setCode(UtilsException.Code.APZ_UT_000.toString());
				u.setPriority("1");
				throw u;
			}
		}

		return sanitizer;
	}

	public String encodeRequest(String inputJson) {
		JSONObject obj = null;
		JSONObject responseObj = new JSONObject();
		try {
			obj = new JSONObject(inputJson);
			Iterator<?> it = obj.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				this.canonicalize(key, true);
				Object nodeValue = obj.get(key);

				if (nodeValue instanceof JSONObject) {
					String res = encodeRequest(nodeValue.toString());
					if (obj.has(key)) {
						responseObj.put(key, new JSONObject(res));
					}

				} else if (nodeValue instanceof JSONArray) {
					JSONArray array = (JSONArray) nodeValue;
					JSONArray resarray = new JSONArray();
					int i = 0;
					while (i < array.length()) {
						JSONObject ob = array.getJSONObject(i);
						String res = encodeRequest(ob.toString());

						resarray.put(new JSONObject(res));
						i++;
					}
					if (obj.has(key)) {
						responseObj.put(key, resarray);
					}
				} else {
					try {
						if (nodeValue instanceof Integer) {

							nodeValue = this.canonicalize("" + nodeValue, true);
							nodeValue = Integer.parseInt("" + nodeValue);
						} else if (nodeValue instanceof Double) {

							nodeValue = this.canonicalize("" + nodeValue, true);
							nodeValue = Double.parseDouble("" + nodeValue);
						} else {
							nodeValue = this.canonicalize("" + nodeValue, true);
						}
					} catch (Exception e) {
						UtilsException u = UtilsException.getUtilsExceptionInstance();
						u.setMessage(u
								.getUtilsExceptionMessage(UtilsException.Code.APZ_UT_001)
								+ "for " + key + " :" + nodeValue);
						u.setCode(UtilsException.Code.APZ_UT_001.toString());
						u.setPriority("1");
						throw u;
					}
					if (!(ServerConstants.MESSAGE_HEADER_SESSION_ID.equals(key) || ServerConstants.PIN.equals(key)
							|| ServerConstants.REPORT_FILE.equals(key) || ServerConstants.LOV_QUERY.contains(key) || "Password"
								.contains(key) || "pin".contains(key))) {

						try {
							if (nodeValue instanceof Integer) {
								nodeValue = new DefaultValidator()
										.getValidInteger("Node",
												"" + nodeValue,
												Integer.MIN_VALUE,
												Integer.MAX_VALUE, false);
							} else if (nodeValue instanceof Double) {
								nodeValue = new DefaultValidator()
										.getValidDouble("Node", "" + nodeValue,
												Double.MIN_VALUE,
												Double.MAX_VALUE, false);
							} else {
								nodeValue = new DefaultValidator()
										.getValidInput("Node",
												nodeValue.toString(),
												"SafeString", Integer.MAX_VALUE, true, false);
							}
							if (nodeValue == null) {
								nodeValue = "";
							}
						} catch (ValidationException e) {
							UtilsException u = UtilsException.getUtilsExceptionInstance();
							u.setMessage(u
									.getUtilsExceptionMessage(UtilsException.Code.APZ_UT_002)
									+ "for" + key + " :" + nodeValue);
							u.setCode(UtilsException.Code.APZ_UT_002.toString());
							u.setPriority("1");
							LOG.error("ValidationException",e);
							throw u;
						}
					}

					if (obj.has(key)) {
						responseObj.put(key, nodeValue);
					} 
				}
			}
		} catch (JSONException e) {
			LOG.error("JSONException",e);
		}
		return responseObj.toString();
	}
}
