/**
 * 
 */
package com.iexceed.appzillon.domain.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.history.TbAshsRoleControls;
import com.iexceed.appzillon.domain.entity.history.TbAshsRoleControlsPK;

/**
 * @author ripu.pandey
 *
 */
@Repository
public interface TbAshsRoleControlsRepository extends JpaRepository<TbAshsRoleControls, TbAshsRoleControlsPK> {

	@Query("select max(c.id.versionNo) from TbAshsRoleControls as c where c.id.appId=:appId and c.id.roleId=:roleId and c.id.controlId=:controlId")
	Integer findMaxVersionNo(@Param("appId") String appid,@Param("roleId") String roleId,@Param("controlId") String controlId);
	
}
