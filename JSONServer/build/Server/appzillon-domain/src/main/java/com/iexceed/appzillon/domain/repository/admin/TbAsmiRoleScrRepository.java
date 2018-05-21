/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiRoleScr;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleScrPK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiRoleScrRepository extends JpaRepository<TbAsmiRoleScr, TbAsmiRoleScrPK>{
	@Query("select tb from TbAsmiRoleScr tb")
	List<TbAsmiRoleScr> findAllCssdRoleScreen();

	@Query("SELECT c FROM TbAsmiRoleScr as c WHERE c.id.roleId =:roleId and c.id.appId =:appId")
	List<TbAsmiRoleScr> findScreensByAppIdRoleId(@Param("roleId")String lRoleId , @Param("appId")String lAppId);

	
	@Query("SELECT c.id.screenId FROM TbAsmiRoleScr as c WHERE c.id.roleId IN:roles and c.id.appId =:appId")
	String[] findScreenIdsForRoleIds(@Param("roles")List<String> lRoleslist,@Param("appId") String pappid);	
	
	//Added by ripu for TaskRepair changes on 04-May-2015
	@Query("SELECT c FROM TbAsmiRoleScr as c WHERE c.id.roleId =:roleId and c.id.screenId =:screenId")
	TbAsmiRoleScr findScreensByRoleIdScreenId(@Param("roleId")String pRoleId, @Param("screenId")String pScreenId);

	//@Query("SELECT tbScrMaster.id.screenId FROM TbAsmiScrMaster as tbScrMaster WHERE tbScrMaster.id.appId =:AppId AND tbScrMaster.id.screenId IN (SELECT tbRoleScr.id.screenId FROM TbAsmiRoleScr as tbRoleScr WHERE tbRoleScr.id.appId =:AppId and tbRoleScr.id.roleId IN (SELECT tbUserRole.id.roleId FROM TbAsmiUserRole as tbUserRole WHERE tbUserRole.id.userId =:UserId AND tbUserRole.id.appId =:AppId))")
	//List<String> findListOfAuthorizedScreenId(@Param("AppId")String pAppId, @Param("UserId")String pUserId);

	//@Query("SELECT tbScrMaster.id.screenId FROM TbAsmiScrMaster as tbScrMaster WHERE tbScrMaster.id.appId =:AppId AND tbScrMaster.id.screenId NOT IN (SELECT tbRoleScr.id.screenId FROM TbAsmiRoleScr as tbRoleScr WHERE tbRoleScr.id.appId =:AppId and tbRoleScr.id.roleId IN (SELECT tbUserRole.id.roleId FROM TbAsmiUserRole as tbUserRole WHERE tbUserRole.id.userId =:UserId AND tbUserRole.id.appId =:AppId))")
	//List<String> findListOfNotAuthorizedScreenId(@Param("AppId")String pAppId, @Param("UserId")String pUserId);
	
	@Query("SELECT tbScrMaster.id.screenId FROM TbAsmiScrMaster as tbScrMaster WHERE tbScrMaster.id.appId =:AppId AND tbScrMaster.id.screenId IN (SELECT tbRoleScr.id.screenId FROM TbAsmiRoleScr as tbRoleScr WHERE tbRoleScr.id.appId =:AppId and tbRoleScr.id.roleId IN:RoleList)")
	List<String> findListOfAuthorizedScreenId(@Param("AppId")String pAppId, @Param("RoleList")List<String> pRoleList);

	@Query("SELECT tbScrMaster.id.screenId FROM TbAsmiScrMaster as tbScrMaster WHERE tbScrMaster.id.appId =:AppId AND tbScrMaster.id.screenId NOT IN (SELECT tbRoleScr.id.screenId FROM TbAsmiRoleScr as tbRoleScr WHERE tbRoleScr.id.appId =:AppId and tbRoleScr.id.roleId IN:RoleList)")
	List<String> findListOfNotAuthorizedScreenId(@Param("AppId")String pAppId, @Param("RoleList")List<String> pRoleList);
	
	@Query("select tbRoleScreen.id.screenId from TbAsmiRoleScr as tbRoleScreen where tbRoleScreen.id.roleId IN (select tbUsrRole.id.roleId from TbAsmiUserRole as tbUsrRole where tbUsrRole.id.userId =:UserId and tbUsrRole.id.appId =:AppId) and tbRoleScreen.id.screenId IN (select tbScrMaster.id.screenId from TbAsmiScrMaster as tbScrMaster where tbScrMaster.id.appId =:AppId)")
	List<String> getListOfAuthorizedScreens(@Param("UserId")String pUserId,@Param("AppId")String pAppId);

	//@Query("select tbScrMaster.id.screenId from TbAsmiScrMaster as tbScrMaster where tbScrMaster.id.appId =:AppId and tbScrMaster.id.screenId NOT IN (select tbRoleScr.id.screenId from TbAsmiRoleScr as tbRoleScr where tbRoleScr.id.roleId IN (select tbUsrRole.id.roleId from TbAsmiUserRole as tbUsrRole where tbUsrRole.id.userId =:UserId and tbUsrRole.id.appId =:AppId and tbUsrRole.accessType ='D'))")
	//List<String> getListOfMasterScreensMinusUnauthorizedScreens(@Param("UserId")String lUserId, @Param("AppId")String appId);
	
	@Query("select tbScrMaster.id.screenId from TbAsmiScrMaster as tbScrMaster where tbScrMaster.id.appId =:AppId and tbScrMaster.id.screenId NOT IN:screenList")
	List<String> getListOfMasterScreensMinusGivenScreens(@Param("AppId")String appId, @Param("screenList")List<String> lScreenList);
	
	//@Query("select tbScrMaster.id.screenId from TbAsmiScrMaster as tbScrMaster where tbScrMaster.id.appId =:AppId and tbScrMaster.id.screenId NOT IN (select tbRoleScr.id.screenId from TbAsmiRoleScr as tbRoleScr where tbRoleScr.id.roleId IN (select tbUsrRole.id.roleId from TbAsmiUserRole as tbUsrRole where tbUsrRole.id.userId =:UserId and tbUsrRole.id.appId =:AppId and tbUsrRole.accessType ='A'))")
	//List<String> getListOfMasterScreensMinusAuthorizedScreens(@Param("UserId")String lUserId, @Param("AppId")String appId);
	
	@Query("SELECT c.id.screenId FROM TbAsmiRoleScr as c WHERE c.id.roleId IN:roles and c.id.appId =:appId")
	List<String> findScreenIdsForRoleId(@Param("roles")List<String> lRoleslist,@Param("appId") String pappid);
}
