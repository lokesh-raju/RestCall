/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.admin;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.TbAsmiUserPK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiUserRepository extends JpaRepository<TbAsmiUser, TbAsmiUserPK>, JpaSpecificationExecutor<TbAsmiUser>{

	@Query("select c from TbAsmiUser as c where c.id.userId=:userId AND c.id.appId=:appId")
	TbAsmiUser findUsersByAppIdUserId(
			@Param("userId") String userid, @Param("appId") String appid);

	@Query("select c from TbAsmiUser as c where c.id.userId=:userId AND c.id.appId=:appId AND c.userActive='Y'")
	TbAsmiUser findUsersByAppIdUserIdUserActive(
			@Param("userId") String userid, @Param("appId") String appid);

	@Query("select c from TbAsmiUser as c where c.id.userId=:userId AND c.id.appId=:appId AND  c.pin=:pin")
	List<TbAsmiUser> findUsersByAppIdUserIdPin(
			@Param("userId") String userid, @Param("appId") String appid,
			@Param("pin") String pin);
	
	@Query("select c from TbAsmiUser as c where c.id.userId like :userId AND c.id.appId like :appId AND c.createTs>=:fromDate AND c.createTs <=:toDate")
	List<TbAsmiUser> findUsersByAppIdDate(
			@Param("userId") String userid, @Param("appId") String appid, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	
	/*@Query("select c from TbAsmiUser as c where c.userPhno1=:userPhno1")
	TbAsmiUser findUsersByMobileNumber(@Param("userPhno1") String pMobileNum);*/
	//List<TbAsmiUser> findUsersByMobileNumber(@Param("userPhno1") String pMobileNum);

	/**
	 * Author : Ripu
	 * Aim : Below changes done for merchant consumer app
	 * Date : 15-06-2015
	 * @param pMobileNum
	 * @param pAppId
	 * @return
	 */
	@Query("select c from TbAsmiUser as c where c.userPhno1=:userPhno1 AND c.id.appId=:appId")
	TbAsmiUser findUsersByMobileNumberAppId(@Param("userPhno1") String pMobileNum, @Param("appId") String pAppId);
	
	//added by sasidhar on 03/04/17
	@Modifying
	@Query("Update TbAsmiUser u set u.pin=:pin,u.checkerTs=:checkerTs,u.authStatus=:authStatus,u.checkerId=:checkerId Where u.id.userId=:userId AND u.id.appId=:appId")
	int updatePinAuthStatusCheckerTSandId(@Param("pin")String pin,@Param("checkerTs")Timestamp checkerTs,@Param("authStatus")String authStatus,@Param("checkerId")String checkerId,@Param("userId")String userId,@Param("appId")String appId);
    
	@Modifying  
	@Query("Update TbAsmiUser u set u.checkerTs=:checkerTs, u.authStatus=:authStatus,u.checkerId=:checkerId where u.id.userId=:userId AND u.id.appId=:appId")
	int updateAuthStatusCheckerTsandId( @Param("checkerTs") Timestamp checkerTs,@Param("authStatus") String authStatus,@Param("checkerId") String checkerId,@Param("userId") String userId,@Param("appId") String appId);
   
	@Query("select sum(vs.active) from VwActiveInactiveUser vs where vs.appId=:appId")
	int getActiveUsers(@Param("appId") String appid);
	
	@Query("select sum(vs.inactive) from VwActiveInactiveUser vs where vs.appId=:appId")
	int getInActiveUsers(@Param("appId") String appid);
	
	@Query("select vs.createTs, sum(vs.active), sum(vs.inactive) from VwActiveInactiveUser vs where vs.appId=:appId AND vs.createTs>=:fromDate AND vs.createTs <=:toDate group by vs.createTs")
	List<Object[]> getActiveInActiveUsersByDate(@Param("appId")String pAppId, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	@Query("select vs.createTs, sum(vs.active), sum(vs.inactive) from VwActiveInactiveUser vs where vs.appId=:appId AND vs.createTs <=:toDate group by vs.createTs")
	List<Object[]> getActiveInActiveUsersBeforeDate(@Param("appId")String pAppId,@Param("toDate") Date toDate);
	
	
	@Query("select vs.createTs, sum(vs.active), sum(vs.inactive) from VwActiveInactiveUser vs where vs.appId=:appId AND vs.createTs>=:fromDate group by vs.createTs")
	List<Object[]> getActiveInActiveUsersAfterDate(@Param("appId")String pAppId, @Param("fromDate") Date fromDate);
	
	
	@Query("select vs.createTs, sum(vs.active), sum(vs.inactive) from VwActiveInactiveUser vs where vs.appId=:appId group by vs.createTs")
	List<Object[]> getActiveInActiveUsers(@Param("appId")String pAppId);
	
}
