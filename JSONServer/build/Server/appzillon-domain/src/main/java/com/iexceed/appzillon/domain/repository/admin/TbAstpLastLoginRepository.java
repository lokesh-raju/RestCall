/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.admin;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAstpLastLogin;
import com.iexceed.appzillon.domain.entity.TbAstpLastLoginPK;
import com.iexceed.appzillon.domain.entity.VwActiveSession;
import com.iexceed.appzillon.domain.entity.VwLocationDetail;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAstpLastLoginRepository extends JpaRepository<TbAstpLastLogin, TbAstpLastLoginPK>,JpaSpecificationExecutor<TbAstpLastLogin>{
	@Query("select tb from TbAstpLastLogin tb where tb.id.userId =:userId and tb.id.appId =:appId order by tb.loginTime desc")
	List<TbAstpLastLogin> findByUserIdAndAppIdOrderByLoginTime(@Param("userId")String string, @Param("appId")String appId);
	
	@Query("select tb from TbAstpLastLogin tb where tb.id.userId =:userId and tb.id.appId =:appId and tb.id.deviceId !=:deviceId")
	List<TbAstpLastLogin> findByUserIdAndAppIdAndNotByDeviceId(@Param("userId")String pUserId, @Param("appId")String pAppId, @Param("deviceId")String pDeviceId);
	
	@Query("select count(*) from TbAstpLastLogin tb where tb.id.appId =:appId AND tb.id.deviceId = 'WEB' AND tb.sessionId !=null OR tb.sessionId = ''")
	int getCurrentSessionCountByOsWeb(@Param("appId")String pAppId);
	
	@Query("select count(*) from TbAstpLastLogin tb where tb.id.appId =:appId AND tb.id.deviceId = 'WEB' AND (tb.sessionId !=null OR tb.sessionId = '') AND tb.createTs>=:fromDate AND tb.createTs <=:toDate")
	int getCurrentSessionCountByOsWebAndDate(@Param("appId")String pAppId, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	@Query("select tb from VwLocationDetail tb where tb.id.appId =:appId")
	List<VwLocationDetail> getLocationDetailsByAppId(@Param("appId")String pAppId);
	
	@Query("select vs from VwActiveSession vs where vs.appId=:appId")
	List<VwActiveSession> getCurrentSessionCountByOs(@Param("appId")String pAppId);
}
