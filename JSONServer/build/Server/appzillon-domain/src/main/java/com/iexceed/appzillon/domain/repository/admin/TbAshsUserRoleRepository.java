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

import com.iexceed.appzillon.domain.entity.history.TbAshsUserRole;
import com.iexceed.appzillon.domain.entity.history.TbAshsUserRolePK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAshsUserRoleRepository extends JpaRepository<TbAshsUserRole, TbAshsUserRolePK>,JpaSpecificationExecutor<TbAshsUserRole>{

	@Query("SELECT max(c.id.versionNo) FROM TbAshsUserRole as c WHERE c.id.roleId=:roleId and c.id.appId=:appId and c.id.userId=:userId")
	Integer findMaxVersionbyRoleidAppidUserId(@Param("roleId") String roleId,	@Param("appId") String appId, @Param("userId") String userId);

}
