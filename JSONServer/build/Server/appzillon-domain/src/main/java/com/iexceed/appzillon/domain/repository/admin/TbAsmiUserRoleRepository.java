/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiUserRole;
import com.iexceed.appzillon.domain.entity.TbAsmiUserRolePK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiUserRoleRepository extends JpaRepository<TbAsmiUserRole, TbAsmiUserRolePK>,JpaSpecificationExecutor<TbAsmiUserRole>{
	@Query("select tb from TbAsmiUserRole tb")
	List<TbAsmiUserRole> findAllCssdUserRole();

	@Query("SELECT c FROM TbAsmiUserRole as c WHERE c.id.roleId=:roleId and c.id.appId=:appId")
	List<TbAsmiUserRole> findrecbyroleidappid(@Param("roleId") String roleId,
			@Param("appId") String appId);

	@Query("SELECT c FROM TbAsmiUserRole as c WHERE c.id.userId=:userId and c.id.appId=:appId")
	List<TbAsmiUserRole> findRolesByAppIdUserId(@Param("appId") String appid,
			@Param("userId") String userid);
	
	//28-5-2014 : to deleteRolesByAppIdUserId
	@Modifying
	@Query("Delete FROM TbAsmiUserRole  c WHERE c.id.userId=:userId and c.id.appId=:appId")
	void deleteRolesByAppIdUserId(@Param("appId") String appid,
			@Param("userId") String userid);
	
	//Added by ripu for TaskRepair changes on 04-May-2015
	@Query("SELECT c FROM TbAsmiUserRole as c WHERE c.id.userId=:userId")
	List<TbAsmiUserRole> findRolesByUserId(@Param("userId") String userid);
	
	@Query("SELECT c.id.roleId FROM TbAsmiUserRole as c WHERE c.id.userId=:userId and c.id.appId=:appId")
	List<String> findRoleListByAppIdUserId(@Param("appId") String appid, @Param("userId") String userid);
}
