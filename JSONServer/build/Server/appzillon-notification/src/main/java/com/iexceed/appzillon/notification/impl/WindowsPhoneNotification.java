package com.iexceed.appzillon.notification.impl;

/**
 *
 * @author Vinod Rawat
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

import java.util.Map;
import java.util.Map.Entry;

public class WindowsPhoneNotification {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					WindowsPhoneNotification.class.toString());

	public String createMessage(String messageData, JSONObject pParams) {
		String toastMessage = null;
		if(pParams != null){
			toastMessage = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<wp:Notification xmlns:wp=\"WPNotification\">"
					+ "<wp:Toast>"
					+ "<wp:Text1>"
					+ messageData
					+ "</wp:Text1>"
					+ "<wp:Param>/MainPage.xaml?NavigatedFrom=Toast Notification  message="+messageData  + getCustomPayLoad(messageData,pParams) +"</wp:Param>"
					+ "</wp:Toast> " + "</wp:Notification>";			
		}
		else{
			toastMessage = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<wp:Notification xmlns:wp=\"WPNotification\">"
					+ "<wp:Toast>"
					+ "<wp:Text1>"
					+ messageData
					+ "</wp:Text1>"
					+ "</wp:Toast> " + "</wp:Notification>";
		}
		return toastMessage;
	}

	public Map<String, String> postToMNPS(List<String> uris, String messageData, JSONObject pParams) {
		StringBuilder response = new StringBuilder("");
		Map<String, String> hash = new HashMap<String, String>();
		String xml= this.createMessage(messageData, pParams);
		LOG.debug("[NOTIFICATIONS] xml created: "+xml);
		byte[] xmlBt = null;
		try {
			xmlBt = xml.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("[NOTIFICATIONS] UnsupportedEncodingException  ", e);
		}
		int i = 0;
		LOG.info("[NOTIFICATIONS] ................WINDOWS PHONE LOGs....................");
		HttpURLConnection urlConn = null;
		while (uris.size() > i) {

			try {
				URL url = new URL((String) uris.get(i));
				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setRequestMethod("POST");
				urlConn.setRequestProperty("Content-Type", "text/xml");
				urlConn.setRequestProperty("X-WindowsPhone-Target", "toast");
				urlConn.setRequestProperty("X-NotificationClass", "2");
				urlConn.setRequestProperty("Content-Length",
						"" + Integer.toString(xmlBt.length));

				LOG.info("[NOTIFICATIONS] Length is:" + "" + Integer.toString(xmlBt.length));
				urlConn.setRequestProperty("Content-Language", "en-US");
				urlConn.setUseCaches(false);
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(
						urlConn.getOutputStream());
				wr.write(xmlBt);
				wr.flush();
				wr.close();
				BufferedReader rd = null;
				Integer code = null;
				String msg = null;
				LOG.info("[NOTIFICATIONS] Response code is: " + urlConn.getResponseCode());
				LOG.info("[NOTIFICATIONS] Response Message is: " + urlConn.getResponseMessage());
				code = urlConn.getResponseCode();
				msg = urlConn.getResponseMessage();
				if (code == 200) {
					hash.put((String) uris.get(i), "Success");
					InputStream is = urlConn.getInputStream();
					rd = new BufferedReader(new InputStreamReader(is));
					String line;

					while ((line = rd.readLine()) != null) {
						response.append(line);
					}
					rd.close();
				} else {
					hash.put((String) uris.get(i), code.toString() + " :" + msg);
				}
				urlConn.disconnect();
			} catch (Exception e) {
				hash.put((String) uris.get(i), "Failure");
				LOG.error((String) uris.get(i) + "        " + "Exception "
						, e);
			}
			i++;
		}
		LOG.info("[NOTIFICATIONS] ...............WINDOWs PHONE LOGs  ENDING......................");
		return hash;
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
