/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.history.TbAshsRoleScr;
import com.iexceed.appzillon.domain.entity.history.TbAshsRoleScrPK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAshsRoleScrRepository extends JpaRepository<TbAshsRoleScr, TbAshsRoleScrPK>{
	@Query("select max(c.id.versionNo) from TbAshsRoleScr as c where c.id.appId=:appId and c.id.roleId=:roleId and c.id.screenId=:screenId")
	Integer findMaxVersionNo(@Param("appId") String appid,@Param("roleId") String roleId,@Param("screenId") String screenId);
}
