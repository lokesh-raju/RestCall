package com.iexceed.appzillon.domain.repository.meta;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAstpOtpEngine;

@Repository
public interface TbAstpOtpEngineRepository extends JpaRepository<TbAstpOtpEngine, Integer>,JpaSpecificationExecutor<TbAstpOtpEngine>{
	@Modifying
	@Query("update TbAstpOtpEngine u set u.status =:status, u.otpValTime =:otpValTime where u.id.serialNo =:serialNo")
	int updateOtpStatus(@Param("status") String status, @Param("serialNo") int serialNo, @Param("otpValTime") Timestamp otpValTime);
	
	/*@Modifying
	@Query("update TbAstpOtpVal u set u.payloadStatus =:payloadStatus and u.payloadProcessTime =:payloadProcessTS where u.id.serialNo =:serialNo")
	int updatePayLoadStatus(@Param("payloadStatus") String payloadStatus, @Param("payloadProcessTS") Timestamp payloadProcessTS, @Param("serialNo") int serialNo);*/
	
	@Query("select tb from TbAstpOtpEngine tb where tb.appId =:appId and tb.userId=:userId and tb.interfaceId=:interfaceId and tb.sessionId=:sessionId")
	List<TbAstpOtpEngine> findAstpOtpValByAppIdUserIdInterfaceIdSessionId(@Param("appId") String appId,@Param("userId") String userId,@Param("interfaceId") String interfaceId,@Param("sessionId") String sessionId);

	@Query("select tb from TbAstpOtpEngine tb where tb.appId =:appId and tb.userId=:userId and tb.interfaceId=:interfaceId and tb.sessionId=:sessionId and tb.status =:status")
	List<TbAstpOtpEngine> findAstpOtpValByAppIdUserIdInterfaceIdSessionIdStatus(@Param("appId") String appId,@Param("userId") String userId,@Param("interfaceId") String interfaceId,@Param("sessionId") String sessionId, @Param("status") String status);
	
	//modified on 08/12/16
	@Query("select tb from TbAstpOtpEngine tb where tb.id.serialNo =:serialNo")
	List<TbAstpOtpEngine> findAstpOtpValByRefNo(@Param("serialNo") int serialNo);

	@Query("select tb from TbAstpOtpEngine tb where tb.appId =:appId and tb.userId=:userId and tb.interfaceId=:interfaceId and tb.sessionId=:sessionId and tb.payloadStatus =:payloadStatus order by tb.id.serialNo desc ")
	List<TbAstpOtpEngine> findAstpOtpValByAppIdUserIdInterfaceIdSessionIdPayLoadStatus(@Param("appId") String appId, @Param("userId") String userId,@Param("interfaceId") String interfaceId,@Param("sessionId") String sessionId, @Param("payloadStatus") String payloadStatus);
	
	@Modifying
	@Query("update TbAstpOtpEngine u set u.status =:ustatus where u.userId =:userId and u.appId =:appId and u.sessionId=:sessionId and u.status in (:statuses)")
	int invalidateUserOTP(@Param("ustatus") String ustatus, @Param("userId") String userId, @Param("appId") String appId, @Param("sessionId") String sessionId, @Param("statuses") String statuses);

	// following are added by sasidhar
	//modified on 30/11/16
	@Modifying
	@Query("update TbAstpOtpEngine u set u.otpResentCount =:otpResentCount where u.id.serialNo =:serialNo ")
	int updateOtpResendCount(@Param("otpResentCount") int otpResentCount,@Param("serialNo") int serialNo);
		
	@Modifying
	@Query("update TbAstpOtpEngine u set u.otpResendLock =:otpResendLock, u.otpResendLockTime =:otpResendLockTime,u.status =:status where u.id.serialNo =:serialNo ")
	int updateOtpResendLock(@Param("otpResendLock") String otpResendLock,@Param("otpResendLockTime") Timestamp otpResendLockTime ,@Param("status") String status,@Param("serialNo") int serialNo);
	
	//modified
	@Modifying
	@Query("update TbAstpOtpEngine u set u.otpResendLock =:otpResendLock, u.otpResentCount =:otpResentCount,u.otpResendLockTime =:otpResendLockTime,u.status =:status where u.id.serialNo =:serialNo")
    int OtpUnlocking(@Param("otpResendLock") String otpResendLock,@Param("otpResentCount") int otpResentCount,@Param("otpResendLockTime") Timestamp otpResendLockTime,@Param("status") String status,@Param("serialNo") int serialNo);
    
	//added to update otpvalidation count
	@Modifying
	@Query("update TbAstpOtpEngine u set u.otpValidationCount =:otpValidationCount where u.id.serialNo =:serialNo ")
    int updateValidationCount(@Param("otpValidationCount") int otpValidationCount,@Param("serialNo") int serialNo);
    
	@Modifying
	@Query("update TbAstpOtpEngine u set u.status =:status where u.id.serialNo =:serialNo ")
	int updateOtpExpiryStatus(@Param("status") String status,@Param("serialNo") int serialNo);
}

