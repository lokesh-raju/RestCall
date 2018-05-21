package com.iexceed.appzillon.domain.entity;

import javax.persistence.Column;

public class TbAslgAuditLogPK implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String appId;
    private String interfaceId;
    private String deviceId;
    private String requestKey;

   public TbAslgAuditLogPK() {
   }

   public TbAslgAuditLogPK(String appId, String interfaceId, String deviceId, String requestKey) {
      this.appId = appId;
      this.interfaceId = interfaceId;
      this.deviceId = deviceId;
      this.requestKey = requestKey;
   }
  

   @Column(name="APP_ID", nullable=false, length=100)
   public String getAppId() {
       return this.appId;
   }
   
   public void setAppId(String appId) {
       this.appId = appId;
   }

   @Column(name="INTERFACE_ID", nullable=false, length=100)
   public String getInterfaceId() {
       return this.interfaceId;
   }
   
   public void setInterfaceId(String interfaceId) {
       this.interfaceId = interfaceId;
   }

   @Column(name="DEVICE_ID", nullable=false, length=100)
   public String getDeviceId() {
       return this.deviceId;
   }
   
   public void setDeviceId(String deviceId) {
       this.deviceId = deviceId;
   }

   @Column(name="REQUEST_KEY", nullable=false, length=100)
   public String getRequestKey() {
       return this.requestKey;
   }
   
   public void setRequestKey(String requestKey) {
       this.requestKey = requestKey;
   }


  public boolean equals(Object other) {
        if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof TbAslgAuditLogPK) ) return false;
		 TbAslgAuditLogPK castOther = ( TbAslgAuditLogPK ) other; 
        
		 return ( (this.getAppId()==castOther.getAppId()) || ( this.getAppId()!=null && castOther.getAppId()!=null && this.getAppId().equals(castOther.getAppId()) ) )
&& ( (this.getInterfaceId()==castOther.getInterfaceId()) || ( this.getInterfaceId()!=null && castOther.getInterfaceId()!=null && this.getInterfaceId().equals(castOther.getInterfaceId()) ) )
&& ( (this.getDeviceId()==castOther.getDeviceId()) || ( this.getDeviceId()!=null && castOther.getDeviceId()!=null && this.getDeviceId().equals(castOther.getDeviceId()) ) )
&& ( (this.getRequestKey()==castOther.getRequestKey()) || ( this.getRequestKey()!=null && castOther.getRequestKey()!=null && this.getRequestKey().equals(castOther.getRequestKey()) ) );
  }
  
  public int hashCode() {
        int result = 17;
        
        result = 37 * result + ( getAppId() == null ? 0 : this.getAppId().hashCode() );
        result = 37 * result + ( getInterfaceId() == null ? 0 : this.getInterfaceId().hashCode() );
        result = 37 * result + ( getDeviceId() == null ? 0 : this.getDeviceId().hashCode() );
        result = 37 * result + ( getRequestKey() == null ? 0 : this.getRequestKey().hashCode() );
        return result;
  }   


}
