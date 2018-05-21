/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.history.TbAshsUser;
import com.iexceed.appzillon.domain.entity.history.TbAshsUserPK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAshsUserRepository extends JpaRepository<TbAshsUser, TbAshsUserPK>, JpaSpecificationExecutor<TbAshsUser>{

	

	@Query("select max(c.id.versionNo) from TbAshsUser as c where c.id.userId=:userId AND c.id.appId=:appId")
	Integer findMaxVersionNoByAppIdUserId(@Param("userId") String userid, @Param("appId") String appid);

	/*@Query("select c from TbAshsUser as c where c.userPhno1=:userPhno1")
	TbAshsUser findUsersByMobileNumber(@Param("userPhno1") String pMobileNum);*/
	//List<TbAshsUser> findUsersByMobileNumber(@Param("userPhno1") String pMobileNum);

	/**
	 * Author : Ripu
	 * Aim : Below changes done for merchant consumer app
	 * Date : 15-06-2015
	 * @param pMobileNum
	 * @param pAppId
	 * @return
	 */
/*	@Query("select c from TbAshsUser as c where c.userPhno1=:userPhno1 AND c.id.appId=:appId")
	TbAshsUser findUsersByMobileNumberAppId(@Param("userPhno1") String pMobileNum, @Param("appId") String pAppId);*/
}
