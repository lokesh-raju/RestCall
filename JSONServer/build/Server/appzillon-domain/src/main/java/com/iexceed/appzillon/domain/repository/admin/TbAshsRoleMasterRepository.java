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

import com.iexceed.appzillon.domain.entity.history.TbAshsRoleMaster;
import com.iexceed.appzillon.domain.entity.history.TbAshsRoleMasterPK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAshsRoleMasterRepository extends JpaRepository<TbAshsRoleMaster, TbAshsRoleMasterPK>,JpaSpecificationExecutor<TbAshsRoleMaster>{
	@Query("select max(c.id.versionNo) from TbAshsRoleMaster as c where c.id.appId=:appId and c.id.roleId=:roleId")
	Integer findMaxVersionNo(@Param("appId") String appid,@Param("roleId") String roleId);
}