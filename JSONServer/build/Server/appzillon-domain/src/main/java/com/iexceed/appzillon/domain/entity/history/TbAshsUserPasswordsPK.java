package com.iexceed.appzillon.domain.entity.history;
// Generated Aug 6, 2013 11:23:55 AM by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * TbAshsUserPasswordsPK generated by hbm2java
 */
@Embeddable
public class TbAshsUserPasswordsPK  implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String userId;
    private String appId;
    private String pin;

    public TbAshsUserPasswordsPK() {
    }

    public TbAshsUserPasswordsPK(String userId, String appId, String pin) {
       this.userId = userId;
       this.appId = appId;
       this.pin = pin;
    }
   

    @Column(name="USER_ID", nullable=false, length=100)
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name="APP_ID", nullable=false, length=100)
    public String getAppId() {
        return this.appId;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name="PIN", nullable=false)
    public String getPin() {
        return this.pin;
    }
    
    public void setPin(String pin) {
        this.pin = pin;
    }


   public boolean equals(Object other) {
	   if (this == other){
      	 return true;
       }
		 if (other == null){
			 return false;
		 }
		 if ( !(other instanceof TbAshsUserPasswordsPK) ){
			 return false;
		 }
		 TbAshsUserPasswordsPK castOther = ( TbAshsUserPasswordsPK ) other; 
         
		 return ( (this.getUserId()==castOther.getUserId()) || ( this.getUserId()!=null && castOther.getUserId()!=null && this.getUserId().equals(castOther.getUserId()) ) )
 && ( (this.getAppId()==castOther.getAppId()) || ( this.getAppId()!=null && castOther.getAppId()!=null && this.getAppId().equals(castOther.getAppId()) ) )
 && ( (this.getPin()==castOther.getPin()) || ( this.getPin()!=null && castOther.getPin()!=null && this.getPin().equals(castOther.getPin()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getUserId() == null ? 0 : this.getUserId().hashCode() );
         result = 37 * result + ( getAppId() == null ? 0 : this.getAppId().hashCode() );
         result = 37 * result + ( getPin() == null ? 0 : this.getPin().hashCode() );
         return result;
   }   


}


