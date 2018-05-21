/**
 * 
 */
package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IDragDrop;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 *
 */
public class DragDropHandler implements IHandler {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, DragDropHandler.class.toString());
	private IDragDrop cDragDrop;
	
	
	public IDragDrop getcDragDrop() {
		return cDragDrop;
	}

	public void setcDragDrop(IDragDrop cDragDrop) {
		this.cDragDrop = cDragDrop;
	}

	@Override
	public void handleRequest(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside handleRequest()..");
		String mRequesttype = pMessage.getHeader().getInterfaceId();
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Interface id : "+ mRequesttype);
		if(ServerConstants.INTERFACE_ID_DRAG_DROP_INSERT.equals(mRequesttype)){
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to DragDropImpl to Insert the Record");
			cDragDrop.insert(pMessage);
		} else if(ServerConstants.INTERFACE_ID_DRAG_DROP_DELETE.equals(mRequesttype)){
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to DragDropImpl to Delete The record.");
			cDragDrop.delete(pMessage);
		} else if(ServerConstants.INTERFACE_ID_DRAG_DROP_SEARCH.equals(mRequesttype)){
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to DragDropImpl to Fetch the Records");
			cDragDrop.search(pMessage);
		} else if(ServerConstants.INTERFACE_ID_DRAG_DROP_UPDATE.equals(mRequesttype)){
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to DragDropImpl to Update the record.");
			cDragDrop.update(pMessage);
		}

	}

}
