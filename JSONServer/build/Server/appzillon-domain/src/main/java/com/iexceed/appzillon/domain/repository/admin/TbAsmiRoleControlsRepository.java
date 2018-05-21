/**
 * 
 */
package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiRoleControls;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleControlsPK;

/**
 * @author ripu.pandey
 *
 */
@Repository
public interface TbAsmiRoleControlsRepository extends JpaRepository<TbAsmiRoleControls, TbAsmiRoleControlsPK> {

	@Query("select tb from TbAsmiRoleControls tb")
	List<TbAsmiRoleControls> findTbAsmiRoleControls();
	
	//@Query("SELECT tbControlMaster.id.controlId FROM TbAsmiControlsMaster as tbControlMaster WHERE tbControlMaster.id.appId =:AppId AND tbControlMaster.id.controlId IN (SELECT tbRoleControl.id.controlId FROM TbAsmiRoleControls as tbRoleControl WHERE tbRoleControl.id.appId =:AppId AND tbRoleControl.id.roleId IN (SELECT tbUserRole.id.roleId FROM TbAsmiUserRole as tbUserRole WHERE tbUserRole.id.userId =:UserId AND tbUserRole.id.appId =:AppId))")
	//List<String> findListOfAuthorizedControlsId(@Param("AppId")String pAppId, @Param("UserId")String pUserId);

	//@Query("SELECT tbControlMaster.id.controlId FROM TbAsmiControlsMaster as tbControlMaster WHERE tbControlMaster.id.appId =:AppId AND tbControlMaster.id.controlId NOT IN (SELECT tbRoleControl.id.controlId FROM TbAsmiRoleControls as tbRoleControl WHERE tbRoleControl.id.appId =:AppId AND tbRoleControl.id.roleId IN (SELECT tbUserRole.id.roleId FROM TbAsmiUserRole as tbUserRole WHERE tbUserRole.id.userId =:UserId AND tbUserRole.id.appId =:AppId))")
	//List<String> findListOfNotAuthorizedControlsId(@Param("AppId")String pAppId, @Param("UserId")String pUserId);
	
	@Query("SELECT tbControlMaster.id.controlId FROM TbAsmiControlsMaster as tbControlMaster WHERE tbControlMaster.id.appId =:AppId AND tbControlMaster.id.controlId IN (SELECT tbRoleControl.id.controlId FROM TbAsmiRoleControls as tbRoleControl WHERE tbRoleControl.id.appId =:AppId AND tbRoleControl.id.roleId IN:RoleList)")
	List<String> findListOfAuthorizedControlsId(@Param("AppId")String pAppId, @Param("RoleList")List<String> pRoleList);

	@Query("SELECT tbControlMaster.id.controlId FROM TbAsmiControlsMaster as tbControlMaster WHERE tbControlMaster.id.appId =:AppId AND tbControlMaster.id.controlId NOT IN (SELECT tbRoleControl.id.controlId FROM TbAsmiRoleControls as tbRoleControl WHERE tbRoleControl.id.appId =:AppId AND tbRoleControl.id.roleId IN:RoleList)")
	List<String> findListOfNotAuthorizedControlsId(@Param("AppId")String pAppId, @Param("RoleList")List<String> pRoleList);
	
	@Query("select tbRolCntrl.id.controlId from TbAsmiRoleControls as tbRolCntrl where tbRolCntrl.id.roleId IN (select tbUsrRole.id.roleId from TbAsmiUserRole as tbUsrRole where tbUsrRole.id.userId =:UserId and tbUsrRole.id.appId =:AppId) and tbRolCntrl.id.controlId in (select tbCntrlMastr.id.controlId from TbAsmiControlsMaster as tbCntrlMastr where tbCntrlMastr.id.appId = :AppId)")
	List<String> getListOfAuthorizedControlId(@Param("UserId")String pUserId,@Param("AppId")String pAppId);
	
	//@Query("select tbCntrlMastr.id.controlId from TbAsmiControlsMaster as tbCntrlMastr where tbCntrlMastr.id.appId =:AppId and tbCntrlMastr.id.controlId NOT IN (select tbRolCntrl.id.controlId from TbAsmiRoleControls as tbRolCntrl where tbRolCntrl.id.roleId IN (select tbUsrRole.id.roleId from TbAsmiUserRole as tbUsrRole where tbUsrRole.id.userId =:UserId and tbUsrRole.id.appId =:AppId and tbUsrRole.accessType ='D'))")
	//List<String> getListOfMasterControlMinusUnauthorizedControl(@Param("UserId")String pUserId, @Param("AppId")String pAppId);
	
	@Query("select tbCntrlMastr.id.controlId from TbAsmiControlsMaster as tbCntrlMastr where tbCntrlMastr.id.appId =:AppId and tbCntrlMastr.id.controlId NOT IN:controlList")
	List<String> getListOfMasterControlMinusGivenControl(@Param("AppId")String pAppId, @Param("controlList")List<String> pControlList);
	
	//@Query("select tbCntrlMastr.id.controlId from TbAsmiControlsMaster as tbCntrlMastr where tbCntrlMastr.id.appId =:AppId and tbCntrlMastr.id.controlId NOT IN (select tbRolCntrl.id.controlId from TbAsmiRoleControls as tbRolCntrl where tbRolCntrl.id.roleId IN (select tbUsrRole.id.roleId from TbAsmiUserRole as tbUsrRole where tbUsrRole.id.userId =:UserId and tbUsrRole.id.appId =:AppId and tbUsrRole.accessType ='A'))")
	//List<String> getListOfMasterControlMinusAuthorizedControl(@Param("UserId")String pUserId, @Param("AppId")String pAppId);
	
	@Query("SELECT c.id.controlId FROM TbAsmiRoleControls as c WHERE c.id.roleId IN:roles and c.id.appId =:appId")
	List<String> findControlsForRoleIds(@Param("roles")List<String> lRoleslist,@Param("appId") String pappid);
	
	@Query("SELECT c FROM TbAsmiRoleControls as c WHERE c.id.roleId =:roleId and c.id.appId =:appId")
	List<TbAsmiRoleControls> findControlsForRoleIdAndAppId(@Param("roleId")String roleId,@Param("appId") String appId);
}
