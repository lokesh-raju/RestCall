package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 * 
 * @author arthanarisamy
 */
public interface IAppzillonReport {

	/**
	 * 
	 * @param pMessage
	 */
	void getLoginReoport(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void getAppUsageReport(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void searchTxnLogging(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void getReqResp(Message pMessage);

	void getMsgStatDetails(Message pMessage);

	void getCustomerOverview(Message pMessage);

	void getCustomerLocationDetail(Message pMessage);

	void getCustomerDetailsReport(Message pMessage);

}
