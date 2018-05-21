package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TB_ASTP_SESSION_STORAGE database table.
 * 
 */
@Embeddable
public class TbAstpSessionStoragePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="USER_ID")
	private String userId;

	@Column(name="SESSION_ID")
	private String sessionId;

	@Column(name="SESSION_KEY")
	private String sessionKey;
	
	public TbAstpSessionStoragePK() {
	}
	public String getAppId() {
		return this.appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSessionId() {
		return this.sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSessionKey() {
		return this.sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAstpSessionStoragePK)) {
			return false;
		}
		TbAstpSessionStoragePK castOther = (TbAstpSessionStoragePK)other;
		return 
			this.appId.equals(castOther.appId)
			&& this.userId.equals(castOther.userId)
			&& this.sessionId.equals(castOther.sessionId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.userId.hashCode();
		hash = hash * prime + this.sessionId.hashCode();
		
		return hash;
	}
}