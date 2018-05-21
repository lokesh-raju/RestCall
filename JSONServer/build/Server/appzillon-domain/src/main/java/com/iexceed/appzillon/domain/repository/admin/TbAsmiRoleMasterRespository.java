/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiRoleMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleMasterPK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiRoleMasterRespository extends JpaRepository<TbAsmiRoleMaster, TbAsmiRoleMasterPK>,JpaSpecificationExecutor<TbAsmiRoleMaster>{
	@Query("select tb from TbAsmiRoleMaster tb")
	List<TbAsmiRoleMaster> findAllCssdRoleMaster();

	@Query("select tb from TbAsmiRoleMaster tb where tb.id.roleId =:roleId AND tb.id.appId =:appId")
	TbAsmiRoleMaster findRolesByRoleIdAppId(
			@Param("roleId") String roleid, @Param("appId") String appid);

	@Query("select tb from TbAsmiRoleMaster tb where  tb.id.appId =:appId")
	List<TbAsmiRoleMaster> findRolesByAppId(@Param("appId") String appid);
}