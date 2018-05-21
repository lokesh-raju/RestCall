package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TB_ASMI_CS_NONCEDETAILS database table.
 * 
 */
@Embeddable
public class TbAsmiCsNonceDetailPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="DEVICE_ID")
	private String deviceId;
	
	@Column(name="REQUEST_ID")
	private String requestId;
	
	@Column(name="CLIENT_NONCE")
	private String clientNonce;
	
	@Column(name="SERVER_NONCE")
	private String serverNonce;

	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getServerNonce() {
		return serverNonce;
	}
	public void setServerNonce(String serverNonce) {
		this.serverNonce = serverNonce;
	}
	public TbAsmiCsNonceDetailPK() {
	}
	public String getAppId() {
		return this.appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getDeviceId() {
		return this.deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getClientNonce() {
		return clientNonce;
	}
	public void setClientNonce(String clientNonce) {
		this.clientNonce = clientNonce;
	}
	@Override
	public String toString() {
		return "TbAsmiCsNonceDetailPK [appId=" + appId + ", deviceId=" + deviceId + ", requestId=" + requestId
				+ ", clientNonce=" + clientNonce + ", serverNonce=" + serverNonce + "]";
	}
}
	
