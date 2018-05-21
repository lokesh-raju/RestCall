package com.iexceed.appzillon.sms.processor;


import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;


/**
 *
 * @author arthanarisamy
 */
public class Receiver {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, Receiver.class.toString());
    
    /*public void process(AGateway ag, Message.MessageTypes mt, InboundMessage im) {
//    	ThreadContext.put("USER", "SMSSupport");
    	ThreadContext.put("logRouter","SMSSupport");
    	LOG.debug("SMS Received -:" + im.getText());
    	LOG.debug("im.getMpMaxNo() "+im.getMpMaxNo()+ ",im mem Index "+im.getMemIndex()+",im.msId "+im.getMessageId()+",Sender -:" + im.getOriginator());    	
    	LOG.debug("Sender ID -:" + im.getSmscNumber());
    	LOG.debug("Sender's UUDID -:" + im.getUuid());
    	LOG.debug("Calling processRequest....");
        SMSRequestProcessorImpl smsProcessor = new SMSRequestProcessorImpl();
//        smsProcessor.processRequest(im);
    }*/
    
}
