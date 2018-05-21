package com.iexceed.appzillon.notification.impl;

/**
 *
 * @author Vinod Rawat
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;

public class WindowsSurfaceNotification {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					WindowsSurfaceNotification.class.toString());
        static String UTF_8 = "UTF-8";
	public Map<String, String> postToWns(List<String> uris, String messageData,
			String type, String pAppId, JSONObject pParams) {
		Map<String, String> hash = new HashMap<String, String>();
		StringBuilder response = new StringBuilder("");
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "................WINDOWS   SURFACE LOGS....................");
		String accessToken = null;
		String sid = PropertyUtils.getPropValue(pAppId, "windowssid").toString().trim();
		String secret = PropertyUtils.getPropValue(pAppId, "windowssecret").toString()
				.trim();
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Windows SID used "+sid);
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Windows Secret key used "+secret);
		String xml = this.createMessage(messageData, pParams);
		String temp = getAccessToken(secret, sid);
		JSONObject jObj = null;
		try {
			LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON+"Access Token Response "+temp);
			jObj = new JSONObject(temp);
			accessToken = jObj.getString("access_token");
		} catch (JSONException e1) {
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "JSONException", e1);
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Recieved token :" + accessToken);
		byte[] xmlBt = null;
		try {
			xmlBt = xml.getBytes(UTF_8);
		} catch (UnsupportedEncodingException e1) {
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "UnsupportedEncodingException in XML", e1);
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "UnsupportedEncodingException  ", e1);
		}
		int i = 0;
		HttpURLConnection urlConn = null;
		while (uris.size() > i) {
			try {
				URL url = new URL((String) uris.get(i));
				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setRequestMethod("POST");
				urlConn.setRequestProperty("Content-Type", "text/xml");
				urlConn.setRequestProperty("X-WNS-Type", type);
				urlConn.setRequestProperty("Authorization", "Bearer "
						+ accessToken);
				urlConn.setRequestProperty("Content-Length",
						"" + Integer.toString(xmlBt.length));
				LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Length is:" + "" + Integer.toString(xmlBt.length));
				urlConn.setRequestProperty("Content-Language", "en-US");
				urlConn.setUseCaches(false);
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(
						urlConn.getOutputStream());
				wr.write(xmlBt);
				wr.flush();
				wr.close();

				Integer code = null;
				if (urlConn != null) {
					LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Response code is: " + urlConn.getResponseCode());
					LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Response Message is: "
							+ urlConn.getResponseMessage());
					InputStream is;
					code = urlConn.getResponseCode();
					if (code == 200) {
						hash.put((String) uris.get(i), "Success");
						is = urlConn.getInputStream();
						BufferedReader rd = new BufferedReader(
								new InputStreamReader(is));
						String line;
						while ((line = rd.readLine()) != null) {
							response.append(line);
						}
						rd.close();
					} else {
						hash.put((String) uris.get(i), code.toString());
					}
				}
			} catch (Exception e) {
				hash.put((String) uris.get(i), "Failure");
				LOG.error((String) uris.get(i) + "  " , e);
			}

			i++;
		}

		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "................WINDOWs SURFACE LOGs ENDING..................");
		return hash;
	}

	public String createMessage(String messageData, JSONObject pParams) {
		/*Changes are made by Samy on 02/03/2015
		 * As part of Windows 8.1 notification changes....
		 * 
		 */
		String toastMessage = null;
		if(pParams != null){
			toastMessage = "<toast launch =\"" + messageData + "\"><visual><binding template=\"ToastText01\"><text id=\"1\">"
					+ messageData+ getCustomPayLoad(messageData, pParams)
					+ "</text></binding>  </visual></toast>";
		}
		else{
			toastMessage = "<toast launch =\"" + messageData + "\"><visual><binding template=\"ToastText01\"><text id=\"1\">"
					+ messageData
					+ "</text></binding>  </visual></toast>";
		}
		return toastMessage;
	}
	static String getAccessToken(String secret, String sid) {
		StringBuffer response = new StringBuffer();
		String tokenUri = "https://login.live.com/accesstoken.srf";
		try {
			String encodedSecret = URLEncoder.encode(secret, UTF_8);
			String encodedSID = URLEncoder.encode(sid, UTF_8);
			String body = "grant_type=client_credentials&client_id="
					+ encodedSID + "&client_secret=" + encodedSecret
					+ "&scope=notify.windows.com";
			URL url;
			url = new URL(tokenUri);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("accept-charset", UTF_8);
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConn.setRequestProperty("Host", "login.live.com");
			urlConn.setRequestProperty("Content-Length",
					"" + Integer.toString(body.getBytes().length));
			urlConn.setRequestProperty("Content-Language", "en-US");
			urlConn.setUseCaches(false);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(
					urlConn.getOutputStream());
			wr.writeBytes(body);
			wr.flush();
			wr.close();
			InputStream is = urlConn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
		} catch (MalformedURLException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "", e);
		} catch (ProtocolException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "", e);
		} catch (IOException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "", e);
		}

		return response.toString();
	}
	
	private String getCustomPayLoad(String messageData, JSONObject pCustomPayload){
		Map<String, String> parametersMap = JSONUtils.getJsonHashMap(pCustomPayload.toString());
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "After converting json to map -:" + parametersMap);
		Iterator<Entry<String, String>> parameters = parametersMap.entrySet().iterator();
		String message = "";
		while(parameters.hasNext()){
			Map.Entry<String, String> parameter = parameters.next();
			message = "#APZ" + parameter.getValue();
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Custom Payload -:" + message);
		return message;
		
	}
}