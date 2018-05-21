package com.iexceed.appzillon.sms.processor;



import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author arthanarisamy
 */
public class SMS {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
			ServerConstants.LOGGER_RESTFULL_SERVICES, SMS.class.toString());

	/**
	 * @param args the command line arguments
	 */
	public SMS() {
		super();
	}

	//private static Service lsmsserv = null;

	/*public void sendSMS() {
		////Get All Ports..
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Getting all ports....");
		try {
			Enumeration lportsenum = CommPortIdentifier.getPortIdentifiers();
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL+"lportsenum.hasMoreElements()"+lportsenum.hasMoreElements());
			while (lportsenum.hasMoreElements()) {
				CommPortIdentifier lcpi = (CommPortIdentifier) lportsenum.nextElement();
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Com Ports::" + lcpi.getName() + ":" + lcpi.getPortType());
			}
			String commPort = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.SMS_COMM_PORT);
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + " Appzillon Comm Port -:" + commPort);

			SerialModemGateway lgateway = new SerialModemGateway("iexceed", commPort, 921600, "", "");
			lgateway.setProtocol(AGateway.Protocols.PDU);
			lgateway.setInbound(true);
			lgateway.setOutbound(true);
			lgateway.setSimPin("0000");
			lgateway.getATHandler().setStorageLocations("SMME");

			lsmsserv = Service.getInstance();
			lsmsserv.S.SERIAL_TIMEOUT = 5000;



			lsmsserv.addGateway(lgateway);
			lsmsserv.setInboundMessageNotification(new Receiver());
			int msgCount = lsmsserv.getInboundMessageCount();
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "inbound message count -:" + msgCount);
			lsmsserv.startService();
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "lsmsserv initialised with "+lsmsserv);

		}catch(UnsatisfiedLinkError ule){
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "UnsatisfiedLinkError");
		}catch(UnsupportedClassVersionError use){
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "UnsupportedClassVersionError");
		}
		catch (Exception e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "Exception", e);
		}
	}*/

	/*public void sendMessage(String no, String messageTxt) {
		try {
			if (Utils.isNotNullOrEmpty(messageTxt)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "msg "+messageTxt+" to number "+no);
				OutboundMessage lmsg = new OutboundMessage("+"+no, messageTxt);
				String SMSPort = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.SMS_DEVICE_DESTINATION_PORT);
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + " Appzillon SMS Port -:" + SMSPort);
				if(SMSPort == null || SMSPort.equals("")){
					LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + " Appzillon SMS Port is not set or found to be null, hence setting it to -1");
					SMSPort = "" + "-1";
				}
				lmsg.setSrcPort(Integer.parseInt(SMSPort));
				lmsg.setDstPort(Integer.parseInt(SMSPort));                               
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "lsmsserv "+lsmsserv);
				lsmsserv.sendMessage(lmsg);
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Message sent to "+no+"  Response is "+messageTxt);
			}

		} catch (TimeoutException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "TimeoutException", e);
		} catch (GatewayException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "GatewayException", e);
		} catch (IOException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "IOException", e);
		} catch (InterruptedException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "InterruptedException", e);
		} 


	}*/


}
