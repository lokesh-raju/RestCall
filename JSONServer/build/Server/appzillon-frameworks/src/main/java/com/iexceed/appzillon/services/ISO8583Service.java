package com.iexceed.appzillon.services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;





import org.apache.camel.spring.SpringCamelContext;

import com.iexceed.appzillon.dao.ISO8583Details;
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServicesUtil;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

/**
 *
 * @author arthanarisamy
 */
public class ISO8583Service implements IServicesBean{

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					ISO8583Service.class.toString());
	protected ISO8583Details lisoSocketDetails = null;

    private String responseExceptionPayload=null;
    private Object requestPayload;

	public enum ISO_MSG_TYPE {

		ISO_0100("0100"), ISO_0110("0110"), ISO_0120("0120"), ISO_0121("0121"), ISO_0130(
				"0130"), ISO_0200("0200"), ISO_0210("0210"), ISO_0220("0220"), ISO_0221(
						"0221"), ISO_0230("0230"), ISO_0400("0400"), ISO_0420("0420"), ISO_0421(
								"0421"), ISO_0430("0430"), ISO_0800("0800"), ISO_0810("0810"), ISO_0820(
										"0820"), ISO_1200("1200"), ISO_1210("1210"), ISO_1304("1304"), ISO_1314("1314");
		String isomsgtype;

		private ISO_MSG_TYPE(String type) {
			isomsgtype = type;
		}

		@Override
		public String toString() {
			return isomsgtype;
		}
	}

	public byte[] buildRequest(Message pMessage,
			Object pRequestPayLoad, SpringCamelContext pContext) {
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Building request for external ISO8583 service....");
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Request Payload for ISO 8582 service is -:" + pRequestPayLoad);
		String lpayLoad = (String) pRequestPayLoad;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Sending request payload to build ISO8583 Message...");
		//		String iSOReqMsg = iSOMessageBuilder(lpayLoad, pMessage.getHeader().getInterfaceId(), pMessage.getHeader().getAppId());
		byte[] iSOReqMsg = iSOMessageBuilder(lpayLoad, pMessage.getHeader().getInterfaceId(), pMessage.getHeader().getAppId(), pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After building ISO 8583 message -:" + new String(iSOReqMsg));
		return iSOReqMsg;
	}

	public void getSocketDetails(Message pMessage,
			SpringCamelContext pContext) {
		String interfaceId = pMessage.getHeader().getInterfaceId();
		String appId = pMessage.getHeader().getAppId();
		lisoSocketDetails = (ISO8583Details)ExternalServicesRouter.injectBeanFromSpringContext(appId + "_" + interfaceId , pContext);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details bean injected ." + lisoSocketDetails);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getEndPointURL-:" + lisoSocketDetails.getEndPointURL());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getEndPointURL -:" + lisoSocketDetails.getEndPointURL());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getPortNo - :" + lisoSocketDetails.getPortNo());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getAutoGenerateField11 -:" + lisoSocketDetails.getAutoGenerateField11());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getIsoReqBinaryBitmap -:" + lisoSocketDetails.getIsoReqBinaryBitmap());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getIsoReqFormat -:" + lisoSocketDetails.getIsoReqFormat());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getIsoReqHeaderLength -:" + lisoSocketDetails.getIsoReqHeaderLength());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getIsoReqHeaderMask -:" + lisoSocketDetails.getIsoReqHeaderMask());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getIsoRespBinaryBitmap -:" + lisoSocketDetails.getIsoRespBinaryBitmap());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getIsoRespFormat - :" + lisoSocketDetails.getIsoRespFormat());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getIsoRespHeaderLength -:" + lisoSocketDetails.getIsoRespHeaderLength());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getNewLineReq -:" + lisoSocketDetails.getNewLineReq());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getTimeOut -:" + lisoSocketDetails.getTimeOut());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ISO8583  service details getKeepAlive -:" + lisoSocketDetails.getKeepAlive());

		int timeOut = lisoSocketDetails.getTimeOut();
		/**
		 * Below changes are made by Vinod as part of
		 * At app level, service time out should be configurable.
		 * Appzillon 3.1 - 63 -- Start
		 */
		if(timeOut == 0){

			LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Timeout value not configured will use default timeOut");
			timeOut =Integer.parseInt(PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.DEFAULT_TIMEOUT).trim()) ;
		}
		/** Appzillon 3.1 - 63 -- END */
		lisoSocketDetails.setTimeOut(timeOut);
	}

	public Object callService(Message pMessage,
			Object pRequestPayLoad, SpringCamelContext pContext) {
		Socket socket = null;
		OutputStream lOutPutStream = null;
		InputStream lInputStream = null;
		JSONObject lResponseJson = null;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Getting Socket Details ....");
		getSocketDetails(pMessage, pContext);

		pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad, lisoSocketDetails.getAutoGenElementMap(), lisoSocketDetails.getTranslationElementMap());
		pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad+""));
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Appending Request Json With MaskedId : " + pMessage.getRequestObject().getRequestJson());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Got Socket Details ....");
		byte[] payload = buildRequest(pMessage, pRequestPayLoad.toString(), pContext);
        requestPayload=new String(payload);

		try{
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Establishing socket connection....");
            Utils.setExtTime(pMessage,"S");
            socket = establishSocketConnection(lisoSocketDetails.getEndPointURL(), lisoSocketDetails.getPortNo(), lisoSocketDetails.getKeepAlive());
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Socket Connection Established....");
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Setting time out for the socket connection....");
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Setting Time out:" + lisoSocketDetails.getTimeOut() + ", to millisecond:"
                    + lisoSocketDetails.getTimeOut() * 1000);
			socket.setSoTimeout(lisoSocketDetails.getTimeOut() * 1000);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Getting socket's output stream....");
			lOutPutStream = socket.getOutputStream();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Got socket's output stream....");
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Getting socket's intput stream....");
			lInputStream = socket.getInputStream();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Got socket's intput stream....");
			boolean signOnStatus = true;
			if (ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getSignonRequired())) {
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Signon is reguired before sending the service request payload...");
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Building Signon request....");
				String signonRequest = buildSignOnRequest(lisoSocketDetails);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Built Signon request is -:" + signonRequest);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Building Sign on ISO 8583 Request....");
				//    			String iSOSignOnReqMsg = iSOMessageBuilder(signonRequest, ServerConstants.ISO_SIGN_ON,pMessage.getHeader().getAppId());
				byte[] iSOSignOnReqMsg = iSOMessageBuilder(signonRequest, ServerConstants.ISO_SIGN_ON,pMessage.getHeader().getAppId(), pMessage);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Sign on ISO8583 request is built -:" + iSOSignOnReqMsg);
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Sending Signon Request....");
				sendRequest(socket, iSOSignOnReqMsg, lOutPutStream);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Sign on Request is Sent....");
				byte[] signOnResp = getResponse(socket, lInputStream);
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response Received for Signon Request is -:" + signOnResp);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Parsing Signon Response....");
				String signOnRespJson = parseISOMessage(lisoSocketDetails, signOnResp, ServerConstants.ISO_SIGN_ON,pMessage.getHeader().getAppId());
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After parsing Signon Response -:" + signOnRespJson);
				signOnStatus = processSignOnResponse(signOnRespJson);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Signon status from the response -:" + signOnStatus);

			}
			if(signOnStatus){
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Signon sucess hence proceeding to send the actual request -:" + signOnStatus);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Building actual ISO 8583 request....");
				byte[] iSOReqMsg = payload;
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Built actual ISO 8583 request is-:" + new String(iSOReqMsg));
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Sending actual service request....");
				Utils.setExtTime(pMessage,"S");
				sendRequest(socket, iSOReqMsg, lOutPutStream);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Sent actual service request....");
				byte[] iSOResp = getResponse(socket, lInputStream);
                Utils.setExtTime(pMessage,"E");
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response Received for actual Request is -:" + iSOResp);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Parsing actual service Response....");
				String lresponse = parseISOMessage(lisoSocketDetails,iSOResp, pMessage.getHeader().getInterfaceId(),pMessage.getHeader().getAppId());
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Parsing actual service response -:" + lresponse);
				lResponseJson = (JSONObject) processResponse(pMessage, lresponse, pContext);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Building actual service response -:" + lresponse);
				ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.SUCCESS,new String(iSOResp),new String(payload));

			}

		} catch (IOException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException - ex:" ,ex);
            responseExceptionPayload=ex.getMessage();
		} finally {

            try {


                if(lOutPutStream!=null) {
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " - Closing OutPutStream.....");
                    lOutPutStream.close();
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " - Closed OutPutStream.....");
                }

                if(lInputStream!=null) {
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " - Closing InPutStream.....");
                    lInputStream.close();
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " - Closed InPutStream.....");
                }

                if(!lisoSocketDetails.getKeepAlive().equalsIgnoreCase(ServerConstants.YES)) {
                    if(socket!=null) {
                        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " - Closing Socket Connection .....");
                        socket.close();
                        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " - Closed Socket Connection .....");
                    }

                }
            } catch (IOException e) {
                LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException:" , e);

            }
            if(responseExceptionPayload!=null) {
                Utils.setExtTime(pMessage, "E");
                ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, responseExceptionPayload,requestPayload);
            }

        }
        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Returning response -:" + lResponseJson);
        return lResponseJson;

	}

	public boolean processSignOnResponse(String pSignOnResponse){
		boolean signedOn = false;
		try{
			JSONObject signOnJson = new JSONObject(pSignOnResponse);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " After converting Sigon response to a JSON -:" + signOnJson);
			String json0810Resp = signOnJson.getString(ServerConstants.TYPE);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " SignOn Response is -:" + json0810Resp);
			if(json0810Resp!= null && ISO_MSG_TYPE.ISO_0810.equals(json0810Resp)){
				signedOn = true;
			}
		}catch (JSONException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSONException",e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_019
					.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_019));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		}
		return signedOn;
	}

	public Object processResponse(Message pMessage, Object pResponse, SpringCamelContext pContext) {

		JSONObject lResponseJson = new JSONObject((String)pResponse);
//		lResponseJson.put(ServerConstants.ISO8583_RESPONSE_NODE, new JSONObject((String)pResponse));
		return lResponseJson;
	}

	/**
	 *
	 * @param pIpAddress
	 * @param pPortNo
	 * @return
	 */
	public Socket establishSocketConnection(String pIpAddress, int pPortNo, String keepAlive) {
		Socket socket = null;
		try{
			socket = new Socket(pIpAddress, pPortNo);
			if(keepAlive.equalsIgnoreCase(ServerConstants.YES)) {
				socket.setKeepAlive(true);
			}

		} catch (NumberFormatException e) {
		    responseExceptionPayload=e.getMessage();
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "NumberFormatException:" , e);
		} catch (UnknownHostException e) {
		    responseExceptionPayload=e.getMessage();
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "UnknownHostException:" , e);
		} catch (IOException e) {
		    responseExceptionPayload=e.getMessage();
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "callExternalService - IOException:" , e);
		}
		return socket;
	}

	public byte[] iSOMessageBuilder(String inputJSon, String interfaceId,String appId, Message pMessage) {
		byte[] lmsgbytes = null;
//		String lheader = "";

		try {
			//JSONObject json = new JSONObject(inputJSon);
			//LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Input JSON:" + json);
/*			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "iSOMessageBuilder - getInputJson:"
					+ getInputJson(inputJSon));*/
/*			JSONObject reqBody = new JSONObject(json.get(ServerConstants.ISO8583_REQUEST_NODE)
					.toString());*/
			JSONObject reqBody = new JSONObject(inputJSon);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "request Body JSON:" + reqBody);
			String type = reqBody.getString("type");
			String lheader = reqBody.getString("header");
			Iterator<String> fields;
			fields = reqBody.keys();
			StringBuilder bitMap = new StringBuilder();
			Map<String, String> elementMap = new TreeMap<String, String>();

			fields = sortedIterator(fields, getStrComparator());
			while (fields.hasNext()) {

				String field = (String) fields.next();
				if (!"type".equalsIgnoreCase(field)
						&& !ServerConstants.HEADER.equalsIgnoreCase(field)) {
					bitMap.append(reqBody.getString(field));
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Position:" + field + ", value:"
							+ reqBody.getString(field));
					elementMap.put(field, reqBody.getString(field));
				}

			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "bitMap:" + bitMap);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "elementMap:" + elementMap);

			IsoMessage message = getISOMessage(elementMap, type,interfaceId,appId, pMessage);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			message.write(byteArrayOutputStream, 0);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Before Setting Header -:" + byteArrayOutputStream.toString());
			/*
			 * Below changes are made by Samy on 31/08/2015
			 * To address header length and static string
			 */
			lmsgbytes = byteArrayOutputStream.toByteArray();
			int lmsglen = lmsgbytes.length;
			String lnewln = "\n";
			byte[] lnewlnbytes = lnewln.getBytes();
			int lnewlinebytelen = lnewlnbytes.length;
			if (ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getNewLineReq())) {
				lmsglen = lmsglen + lnewlinebytelen;
			}
			if (lisoSocketDetails.getIsoReqHeaderMask()!= null) {
				int lidx = lisoSocketDetails.getIsoReqHeaderMask().indexOf(ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH);
				if (lidx > -1) {
					////Length is Required in the Header..
					int lmasklen = lisoSocketDetails.getIsoReqHeaderMask().length();
					int lheaderlen = Integer.parseInt(lisoSocketDetails.getIsoReqHeaderLength());
					int ldesiredlenoflen = lheaderlen - (lmasklen - (ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH.length()));
					String lmsglenstr = Integer.toString(lmsglen);
					lmsglenstr = padString(lmsglenstr, "0", ldesiredlenoflen);
					lheader = replaceTag(lisoSocketDetails.getIsoReqHeaderMask(), ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH, lmsglenstr);
				}
			}
			message.setIsoHeader(lheader);
			/////Convert the Message into Bytes
			byteArrayOutputStream = new ByteArrayOutputStream();
			message.write(byteArrayOutputStream, 0);
			lmsgbytes = byteArrayOutputStream.toByteArray();
			lmsglen = lmsgbytes.length;
			if (ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getNewLineReq())) {
				lmsglen = lmsglen + lnewlinebytelen;
				byte[] lnewmsgbytes = new byte[lmsglen];
				////Copy Message -
				/*
				 * Use copyBytes(byte1[], byte2[]);
				 * Only if JDK/JRE used is 1.6
				 */
				System.arraycopy(lmsgbytes, 0, lnewmsgbytes, 0, lmsgbytes.length);
				////Copy Newline bytes as well..
				System.arraycopy(lnewlnbytes, 0, lnewmsgbytes, lmsgbytes.length, lnewlnbytes.length);
				lmsgbytes = lnewmsgbytes;
			}

			/*byte[] lmsgbytes = byteArrayOutputStream.toByteArray();
			int lmsglen = lmsgbytes.length;
			String isoHeader = getISOHeader(byteArrayOutputStream);
			message.setIsoHeader(isoHeader);

			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO Message Length:" + lmsglen);
			byte[] llenbytes = isoHeader.getBytes();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO Message LenByte:" + llenbytes.length);
			int ltotalbytes = lmsgbytes.length + llenbytes.length;
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO Message Length including header :" + ltotalbytes);
			lpayloadbytes = new byte[ltotalbytes];

			for (int i = 0; i < llenbytes.length; i++) {
				lpayloadbytes[i] = llenbytes[i];
			}
			for (int i = llenbytes.length; i < ltotalbytes; i++) {
				lpayloadbytes[i] = lmsgbytes[i - llenbytes.length];
			}
			if(ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getNewLineReq())){
				byte[] lnewlinebytes = "\n".getBytes();
				lpayloadbytes[ltotalbytes-1] = lnewlinebytes[0];
			}*/

			/*if(ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getNewLineReq())){
				String lnewln = "\n";
				byte[] lnewlnbytes = lnewln.getBytes();
				int lnewlinebytelen = lnewlnbytes.length;
				ltotalbytes = ltotalbytes + lnewlinebytelen;
				byte[] lnewmsgbytes = new byte[lmsglen];
				////Copy Message
				System.arraycopy(lmsgbytes, 0, ltotalbytes, 0, lmsgbytes.length);
				////Copy Ne wline bytes as well..
				System.arraycopy(lnewlnbytes, 0, ltotalbytes, lmsgbytes.length, lnewlnbytes.length);
				lmsgbytes = lnewmsgbytes;
			}*/

			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO Message To String -:" + new String(lmsgbytes));
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO Message Length -:" + lmsgbytes.length);

			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Header :" + message.getIsoHeader());
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSONException:" , ex);
		} catch (IOException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException:" , ex);
		}
		return lmsgbytes;
	}

	public static Iterator<String> sortedIterator(Iterator<String> it,
			Comparator<String> comparator) {
		List<String> list = new ArrayList<String>();
		while (it.hasNext()) {
			list.add(it.next());
		}

		Collections.sort(list, comparator);
		return list.iterator();
	}

	public static Comparator<String> getStrComparator() {
		Comparator<String> comparator = new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		};
		return comparator;
	}

	public IsoMessage getISOMessage(Map<String, String> elementMap,
			String type, String interfaceId,String appId, Message pMessage) {
		IsoMessage m = null;
		MessageFactory<IsoMessage> mfact = null;
		try {
			String configFile = ServerConstants.META_INF_SPRING + appId+"/" + interfaceId
					+ ServerConstants.ISO_CONFIG_XML;
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getISOMessage - configFile name:" + configFile);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Config File Loaded..Before");
			/**
			 * Below changes are made to read the iso config file from class
			 * path instead of a location in c drive. Changes made by Samy on
			 * 14-04-2014
			 */
			URL lconfigurl = ISO8583Service.class.getClassLoader().getResource(
					configFile);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getISOMessage - lconfigurl:" + lconfigurl);
			mfact = ConfigParser.createFromUrl(lconfigurl);


			/*
			 * Commenting the below block of code to generate field 7 & 11
			 * as the same is handled based on condition and configuration
			 * Changes made by Samy on 31/08/2015
			 */
			/*
			 *//**
			 * Commenting the trace number generation in memory and using the
			 * sequence from DB. Changed made by Samy on 18-12-2013
			 *//*
            mfact.setTraceNumberGenerator(new SimpleTraceGenerator(
                    (int) (System.currentTimeMillis() % 1000000)));

			  *//**
			  * Setting field 7 with the date Changes made by samy on 19-12-2013
			  *//*
            mfact.setAssignDate(true);*/
			/*
			 * Below changes are made by
			 * Samy on 09/09/2015
			 * to set field 7 to the message factory
			 */
			if(elementMap.containsKey("7")){
				mfact.setAssignDate(true);
			}

			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Config File Loaded..");

			int msgType = Integer.valueOf(String.valueOf(type), 16);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getISOMessage - MsgType:" + msgType);
			m = mfact.newMessage(msgType);
			m.setIsoHeader("");

			////Field 11
			if (ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getAutoGenerateField11())) {
				m.setValue(11, getField11(pMessage), m.getField(11).getType(), m.getField(11).getLength());
			}
			/*
			 * Below changes to set binary bitmap
			 * done by Samy on 31/08/2015
			 */
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Reuqest Binary Bitmap -:" + lisoSocketDetails.getIsoReqBinaryBitmap());
			if(ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getIsoReqBinaryBitmap())){
				mfact.setUseBinaryBitmap(true);
				m.setBinaryBitmap(true);
			}
			/*
			 * Below changes to set message as binary
			 * done by Samy on 31/08/2015
			 */
			if(ServerConstants.ISO8583_REQUEST_TYPE_BINARY.equalsIgnoreCase(lisoSocketDetails.getIsoReqFormat())){
				m.setBinary(true);
			}

			Set<String> elementKeys = elementMap.keySet();
			Iterator<String> position = elementKeys.iterator();
			position = sortedIterator(position, getStrComparator());
			while (position.hasNext()) {
				String lKey = (String) position.next();
				int field = 0;
				if ((!"type".equals(lKey)) && (!"header".equals(lKey))
						&& (!"11".equals(lKey)) && (!"37".equals(lKey))
						&& (!"7".equals(lKey))) {
					field = Integer.parseInt(lKey);

					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Adding Field :" + field + ":"
							+ elementMap.get(lKey));
					m.setValue(field, elementMap.get(lKey), m.getField(field)
							.getType(), m.getField(field).getLength());
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO Message - After : m.getField(" + field
							+ "):" + m.getField(field) + ", Lenght:"
							+ m.getField(field).getLength() + ", value:"
							+ m.getField(field).getValue() + ", Type:"
							+ m.getField(field).getType());
				} else {
					if ("11".equals(lKey)) {
						if(!ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getAutoGenerateField11())){
							m.setValue(11, elementMap.get(lKey), m.getField(11).getType(), m
									.getField(11).getLength());
						}
					} else if ("37".equals(lKey)) {
						if (elementMap.get(lKey) != null
								&& elementMap.get(lKey).length() > 0) {
							m.setValue(37, elementMap.get(lKey), m.getField(37)
									.getType(), m.getField(37).getLength());
						} else {
							String lref = getRef37();
							LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Field 37 - lref:" + lref);
							m.setValue(37, lref, m.getField(37).getType(), m
									.getField(37).getLength());
						}

					}

				}

			}

		} catch (IOException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException:" , ex);
		}
		return m;
	}

	public String getRef37() {
		String lref = "";
		try {
			Calendar cal = Calendar.getInstance();
			lref = new SimpleDateFormat("ddMMhhmmssSSS").format(cal.getTime());
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Reference Time  :" + lref);

			if (lref.length() > 12) {
				lref = lref.substring(0, 12);
			}
		} catch (Exception ex) {
			lref = "000000000000";
		}
		return lref;
	}

	public static int converttoHex(int n) {
		return Integer.valueOf(String.valueOf(n), 16);
	}

	public String parseISOMessage(ISO8583Details pISODet, byte[] isoResponse,
			String pintfaceid,String appId) {
		String response = null;

		String configFile = ServerConstants.META_INF_SPRING+appId+"/"+ pintfaceid + ServerConstants.ISO_CONFIG_XML;
		try {
			IsoMessage message = null;
			MessageFactory<IsoMessage> mfact = null;

			/**
			 * Below changes are made to read the iso config file from class
			 * path instead of a location in c drive. Changes made by Samy on
			 * 16-04-2014
			 */
			URL lconfigurl = ISO8583Service.class.getClassLoader().getResource(
					configFile);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getISOMessage - lconfigurl:" + lconfigurl);
			mfact = ConfigParser.createFromUrl(lconfigurl);

			/*
			 * Below changes are to set binary bitmap
			 * done by Samy on 31/08/2015
			 */
			if(ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getIsoRespBinaryBitmap())){
				mfact.setUseBinaryBitmap(true);
			}

			//            mfact.setAssignDate(true);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "parseISOMessage - isoRespHeaderLength:"
					+ pISODet.getIsoRespHeaderLength());
			message = mfact.parseMessage(isoResponse,
					pISODet.getIsoRespHeaderLength());
			/*
			 * Below changes are to set message as binary
			 * done by Samy on 31/08/2015
			 */
			if(ServerConstants.ISO8583_REQUEST_TYPE_BINARY.equalsIgnoreCase(lisoSocketDetails.getIsoRespFormat())){
				message.setBinary(true);
			}
			response = buildISORespJson(message);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "parseISOMessage - response:" + response);
		} catch (IOException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException:" , ex);
		} catch (ParseException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ParseException:" , ex);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		}
		return response;

	}

	public static void print(IsoMessage m) {
		LOG.debug(ServerConstants.MESSAGE+" : " + m);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "TYPE: %04x\n" + m.getType());
		for (int i = 2; i < 128; i++) {
			if (m.hasField(i)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "F %3d(%s): %s -> '%s'\n" + i
						+ m.getField(i).getType() + m.getObjectValue(i)
						+ m.getField(i).toString());
			}
		}
	}

	public String buildISORespJson(IsoMessage m) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS  + " Response Message : " + m);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS +"TYPE: %04x\n"+ m.getType());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Debug String:"+ m.debugString());
		JSONObject isoResp = null;
		try {
			isoResp = new JSONObject();
			isoResp.put("header", m.getIsoHeader());
			for (int i = 2; i < 128; i++) {
				if (m.hasField(i)) {
					LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Field:" + i + ", Value:" + m.getObjectValue(i));
					isoResp.put(Integer.toString(i), m.getObjectValue(i));
				}

			}

			Formatter formatter = getFormatter().format("%04x", m.getType());
			String respType = formatter.toString();
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "strRespType.toString():" + respType);
			isoResp.put("type", respType);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSON Response:" + isoResp);
		} catch (JSONException jsonex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSONException:" , jsonex);
		}
		return isoResp.toString();
	}

	public Formatter getFormatter() {
		return new Formatter();
	}

	private String getInputJson(String inputJson) {
		String jsonBody = null;
		try {
			JSONObject jSONObject = new JSONObject(inputJson);
			Iterator<?> keyIterator = jSONObject.keys();
			while (keyIterator.hasNext()) {
				String key = (String) keyIterator.next();
				for (ISO_MSG_TYPE types : ISO_MSG_TYPE.values()) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getMsgType - types:" + types.toString());
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Request MsgType:" + key);
					if (key.equals(types.toString())) {
						jsonBody = jSONObject.getString(types.toString());
						break;
					}
				}
			}
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSONException:" , ex);
		}
		return jsonBody;

	}

	public String formatDate() {
		String lFormatteDate = null;
		SimpleDateFormat sf = new SimpleDateFormat("ddmmHHmmss");
		Date date = new Date();
		lFormatteDate = sf.format(date);
		return lFormatteDate;

	}

	public String buildSignOnRequest(ISO8583Details isoSocketDetails) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "****Inside buildSignOnRequest....");
		JSONObject lSignOnReq = new JSONObject();
		JSONObject lSignOnReqJson = new JSONObject();
		try {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "buildSignOnRequest - sigonRequest:"
					+ isoSocketDetails.getSigonRequest());
			lSignOnReq = new JSONObject(isoSocketDetails.getSigonRequest());
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "sendSignOnRequest - lSignOnReq" + lSignOnReq.toString());


			lSignOnReqJson = new JSONObject(lSignOnReq);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "sendSignOnRequest - lSignOnReqJson" + lSignOnReqJson);

		} catch (JSONException jsex) {
			jsex.getStackTrace();
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "buildSignOnRequest - JSONException:"
					,jsex);
		}
		return lSignOnReqJson.toString();
	}

	public void sendRequest(Socket socket, String iSORequest, OutputStream dout) {
		try {
			dout.write(iSORequest.getBytes(), 0, iSORequest.length());
			dout.flush();

		} catch (IOException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException",e);
		}
	}

	public void sendRequest(Socket socket, byte[] iSORequest, OutputStream dout) {
		try {
			DataOutputStream out = new DataOutputStream(dout);
			out.write(iSORequest, 0, iSORequest.length);
			out.flush();
			dout.flush();

		} catch (IOException e) {
		    responseExceptionPayload=e.getMessage();
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException",e);
		}
	}

	/**
	 *
	 * @param socket
	 * @param in
	 * @return
	 */
	public byte[] getResponse(Socket socket, InputStream in) {
		String str = null;
		byte[] resbytes = null;
		/*
		Changes made by Samy on 06/11/2017
		To read response bytes from Socket.
		 */
		DataInputStream din = null;
		try {
			din = new DataInputStream(in);
			resbytes = new byte[16 * 1024];
			byte[] lreadbytes = new byte[16 * 1024];
			boolean read = true;
			int lresplen = 0;
			while(read){
				int lreadlen = 0;
				try {
					if (socket.isClosed()) {
						lreadlen = 0;
						LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Socket is closed....");
					} else {
						lreadlen = din.read(lreadbytes);
						LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Reading Bytes and read bytes length -:" + lreadlen);
					}
				} catch (SocketTimeoutException stoex) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Timeout Exception..");
                    responseExceptionPayload=stoex.getMessage();
					lreadlen = 0;
				}


				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Bytes Recevied lreadlen-:"+ lreadlen);
				if (lreadlen <= 0){
					read = false;
				}else{
					//                	  		Arrays.copyOf(lreadbytes, lreadbytes.length);
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Total Bytes Recevied lreadbytes-:"+ lreadbytes
							+ " Total Bytes Recevied resbytes-:"+ resbytes
							+ " Total Bytes Recevied lresplen-:"+ lresplen
							+ " Total Bytes Recevied lreadbytes length-:"+ lreadbytes.length);
					System.arraycopy(lreadbytes, 0, resbytes, lresplen, lreadlen);
					lresplen = lresplen  + lreadlen;
					/*
					Changes made by Samy on 06/11/2017
					To read response bytes from Socket.
		 			*/
					read = false;
				}
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Total Bytes Recevied -:"+ lresplen);
			str = new String(resbytes, 0, lresplen);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getResponse - Response ISO from external Socket server"
					+ str);

			if (Utils.isNotNullOrEmpty(str) && str.length() > 1) {
				String lleadingspace = str.substring(0, 1);
				if (" ".equals(lleadingspace)) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getResponse - Space Removed..");
					str = str.substring(1);
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getResponse - Message After Removing Space :"
							+ str);
				}
			}
			/*
			Changes made by Samy on 06/11/2017
			To read response bytes from Socket.
		 	*/
//			din.close();
		} catch (IOException e) {
            responseExceptionPayload=e.getMessage();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getResponse - e:" + Utils.getStackTrace(e));
		}finally {
			/*
			Changes made by Samy on 06/11/2017
			To read response bytes from Socket.
		 	*/
			if(din != null){
				try {
					din.close();
				} catch (IOException e) {
					LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " IOException -:", e);
				}
			}
		}
		return resbytes;
	}

	/**
	 *
	 * @param socket
	 * @param in
	 * @return
	 */
	public byte[] getResponseBufferedReader(Socket socket, InputStream in) {
		String lrespstr = "";
		try {
               BufferedReader lbufferedreader = null;
               InputStream linputstream = null;
               linputstream = socket.getInputStream();
               lbufferedreader = new BufferedReader(new InputStreamReader(linputstream));
               long lwaittill =  System.currentTimeMillis() + (lisoSocketDetails.getTimeOut() * 1000) ;
               LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Buffer will wait till -:" + lwaittill);
               boolean lbufferedreaderready = false;
               while (System.currentTimeMillis() <= lwaittill) {
                     if (lbufferedreader.ready()) {
                            lbufferedreaderready = true;
                            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Breaking at ...:" + System.currentTimeMillis());
                            break;
                     }
               }
               if (lbufferedreaderready) {
                     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Ready...");
                     lrespstr = "";
                     int avail = linputstream.available();
                     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Availability :" + avail);
                     if (linputstream.available() > 1) {
                            for (int i = 0; i < avail; i++) {
                                   int lchar = lbufferedreader.read();
                                   lrespstr = lrespstr + (char) lchar;
                            }
                     }
               }
               LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response Received :" + lrespstr);
        } catch (Exception ex) {
               LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Receive Error :", ex);
               ex.printStackTrace();
        }
		return lrespstr.getBytes();
 }

	private String getField7() {
		String lFormatteDate = null;
		SimpleDateFormat sf = new SimpleDateFormat("MMddhhmmss");
		Date date = new Date();
		lFormatteDate = sf.format(date);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lFormatteDate:" + lFormatteDate);
		return lFormatteDate;
	}

	synchronized private String getField11(Message pMessage) {
		Header lHeader  = pMessage.getHeader();
		JSONObject lRequetJSON = pMessage.getRequestObject().getRequestJson();
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_FETCH_SEQUENCE_VALUE);
		JSONObject sequenceValueJSON = new JSONObject();
		sequenceValueJSON.put("sequenceName", "ISO8583_F11");
		pMessage.getRequestObject().setRequestJson(new JSONObject().put("sequenceDtlsRequest", sequenceValueJSON));
		DomainStartup.getInstance().processRequest(pMessage);
		JSONObject lSequenceDetailsJSON = pMessage.getResponseObject().getResponseJson();
		JSONObject lSequenceRespJson = lSequenceDetailsJSON.getJSONObject("sequenceDtlsResponse");
		String lSequenceValue = lSequenceRespJson.getString("sequenceValue");
		int field11Length = Integer.parseInt(lisoSocketDetails.getField11Length());
		String lpaddedField11 = Utils.getPaddedString("" + lSequenceValue, field11Length , '0', true);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Generated field 11 after padding -:" + lpaddedField11);
		pMessage.setHeader(lHeader);
		pMessage.getRequestObject().setRequestJson(lRequetJSON);

		return lpaddedField11;
	}

	private String getISOHeader(ByteArrayOutputStream pisoMessageBytes){
		String lHeader = "";

		if(lisoSocketDetails.getIsoReqHeaderMask()!= null
				&& lisoSocketDetails.getIsoReqHeaderMask()!=  ""
				&& lisoSocketDetails.getIsoReqHeaderMask().contains(ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH)){
			byte[] lmsgbytes = pisoMessageBytes.toByteArray();
			int lmsglen = lmsgbytes.length;
			/*int toPadLength = Integer.parseInt(lisoSocketDetails.getIsoReqHeaderLength());
    			String paddedMessageLength = Utils.getPaddedString(""+ lmsglen, 4 , '0', true);
    			lHeader = lisoSocketDetails.getIsoReqHeaderMask().replace(ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH, paddedMessageLength);*/

			////Length is Required in the Header..
			int lmasklen = lisoSocketDetails.getIsoReqHeaderMask().length();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Header Mask Length -:" + lmasklen);
			int lHeaderLength = Integer.parseInt(lisoSocketDetails.getIsoReqHeaderLength());
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Header Length -:" + lHeaderLength);
			int ldesiredlenoflen = lHeaderLength - (lmasklen - (ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH.length()));
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Header Desired Length -:" + ldesiredlenoflen);
			String lmsglenstr = Integer.toString(lmsglen);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Message Length Before Padding -:" + lmsglenstr);
			lmsglenstr = Utils.getPaddedString(lmsglenstr, ldesiredlenoflen, '0', true);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Message Length After Padding -:" + lmsglenstr);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Request Header Mask -:" + lisoSocketDetails.getIsoReqHeaderMask());
			lHeader = replaceTag(lisoSocketDetails.getIsoReqHeaderMask(), ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH, lmsglenstr);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Message Header -:" + lHeader);
		}
		return lHeader;
	}

	private static void replaceTag(StringBuffer pcontent, String ptag, String pval) {
		if (pval == null) {
			pval = "";
		}
		int lindex = pval.indexOf(ptag);
		if (lindex >= 0) {
			// //Infinite Loop Case..
		} else {
			int ltagindex = -1;
			int ltaglen = 0;
			ltaglen = ptag.length();
			if (pcontent != null) {
				ltagindex = pcontent.indexOf(ptag);
				while (ltagindex >= 0) {
					pcontent.replace(ltagindex, ltagindex + ltaglen, pval);
					ltagindex = pcontent.indexOf(ptag);
				}
			}
		}
	}
	private static String replaceTag(String pcontent, String ptag, String pval) {
		String lcontent = pcontent;
		if (pcontent != null) {
			StringBuffer lbuf = new StringBuffer(pcontent);
			replaceTag(lbuf, ptag, pval);
			lcontent = lbuf.toString();
		}
		return lcontent;
	}

	private static String padString(String pstr, String ppadchar, int plen) {
		String lstr = pstr;
		int lactlen = pstr.length();
		int ldesiredlen = plen;
		if ((ldesiredlen - lactlen) > 0) {
			for (int i = 0; i < (ldesiredlen - lactlen); i++) {
				lstr = ppadchar + lstr;
			}
		}
		return lstr;
	}

	private static byte[] copyBytes(byte[] src, byte[] copy){
		copy = Arrays.copyOf(src, src.length);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Copying bytes to a new bytes array -:" + Arrays.toString(copy));
		return copy;
	}
}