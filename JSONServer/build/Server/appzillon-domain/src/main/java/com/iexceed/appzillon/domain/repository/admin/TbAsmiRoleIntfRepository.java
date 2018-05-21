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

import com.iexceed.appzillon.domain.entity.TbAsmiRoleIntf;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleIntfPK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiRoleIntfRepository extends JpaRepository<TbAsmiRoleIntf, TbAsmiRoleIntfPK>{

	@Query("select tb from TbAsmiRoleIntf tb")
	List<TbAsmiRoleIntf> findAllCssdRoleInterface();
	
@Query("SELECT c FROM TbAsmiRoleIntf as c WHERE c.id.roleId =:roleId and c.id.appId =:appId")
	List<TbAsmiRoleIntf> findIntfByAppIdRoleId(@Param("roleId")String lRoleId , @Param("appId")String lAppId);

@Query("SELECT c.id.interfaceId FROM TbAsmiRoleIntf as c WHERE c.id.roleId IN:roles and c.id.appId =:appId")
String[] findInterfaceIdsForRoleIds(@Param("roles")List<String> lRoleslist,@Param("appId") String pappid);

	//@Query("SELECT tbintfMaster.id.interfaceId FROM TbAsmiIntfMaster as tbintfMaster WHERE tbintfMaster.id.appId =:AppId AND tbintfMaster.id.interfaceId IN (SELECT tbRoleIntf.id.interfaceId FROM TbAsmiRoleIntf as tbRoleIntf WHERE tbRoleIntf.id.appId =:AppId AND tbRoleIntf.id.roleId IN (SELECT tbUserRole.id.roleId FROM TbAsmiUserRole as tbUserRole WHERE tbUserRole.id.userId =:UserId AND tbUserRole.id.appId =:AppId))")
	//List<String> findListOfAuthorizedInterfaceId(@Param("AppId") String pAppId, @Param("UserId") String pUserId);
	
	//@Query("SELECT tbintfMaster.id.interfaceId FROM TbAsmiIntfMaster as tbintfMaster WHERE tbintfMaster.id.appId =:AppId AND tbintfMaster.id.interfaceId NOT IN (SELECT tbRoleIntf.id.interfaceId FROM TbAsmiRoleIntf as tbRoleIntf WHERE tbRoleIntf.id.appId =:AppId AND tbRoleIntf.id.roleId IN (SELECT tbUserRole.id.roleId FROM TbAsmiUserRole as tbUserRole WHERE tbUserRole.id.userId =:UserId AND tbUserRole.id.appId =:AppId))")
	//List<String> findListOfNotAuthorizedInterfaceId(@Param("AppId") String pAppId, @Param("UserId") String pUserId);
	
	@Query("SELECT tbintfMaster.id.interfaceId FROM TbAsmiIntfMaster as tbintfMaster WHERE tbintfMaster.id.appId =:AppId AND tbintfMaster.id.interfaceId IN (SELECT tbRoleIntf.id.interfaceId FROM TbAsmiRoleIntf as tbRoleIntf WHERE tbRoleIntf.id.appId =:AppId AND tbRoleIntf.id.roleId IN:RoleList)")
	List<String> findListOfAuthorizedInterfaceId(@Param("AppId") String pAppId, @Param("RoleList")List<String> pRoleList);
	
	@Query("SELECT tbintfMaster.id.interfaceId FROM TbAsmiIntfMaster as tbintfMaster WHERE tbintfMaster.id.appId =:AppId AND tbintfMaster.id.interfaceId NOT IN (SELECT tbRoleIntf.id.interfaceId FROM TbAsmiRoleIntf as tbRoleIntf WHERE tbRoleIntf.id.appId =:AppId AND tbRoleIntf.id.roleId IN:RoleList)")
	List<String> findListOfNotAuthorizedInterfaceId(@Param("AppId") String pAppId, @Param("RoleList")List<String> pRoleList);

	//@Query("SELECT tbintfMaster.id.interfaceId FROM TbAsmiIntfMaster as tbintfMaster WHERE tbintfMaster.id.interfaceId =:InterfaceId AND tbintfMaster.id.appId =:AppId AND tbintfMaster.id.interfaceId IN (SELECT tbRoleIntf.id.interfaceId FROM TbAsmiRoleIntf as tbRoleIntf WHERE tbRoleIntf.id.appId =:AppId AND tbRoleIntf.id.roleId IN:RoleList)")
	//String checkInterfaceIdExistForRoles(@Param("InterfaceId") String pInterfaceId, @Param("AppId") String pAppId, @Param("RoleList")List<String> pRoleList);
	
	@Query("SELECT tbintfMaster.id.interfaceId FROM TbAsmiIntfMaster as tbintfMaster WHERE tbintfMaster.id.interfaceId =:InterfaceId AND tbintfMaster.id.appId =:AppId AND tbintfMaster.id.interfaceId IN (SELECT tbRoleIntf.id.interfaceId FROM TbAsmiRoleIntf as tbRoleIntf WHERE tbRoleIntf.id.appId =:AppId AND tbRoleIntf.id.roleId IN (SELECT c.id.roleId FROM TbAsmiUserRole as c WHERE c.id.userId =:UserId and c.id.appId =:AppId))")
	String checkInterfaceIdExistForRoles(@Param("InterfaceId") String pInterfaceId, @Param("AppId") String pAppId, @Param("UserId")String pUserId);
	
	@Query("select tbRoleIntf.id.interfaceId from TbAsmiRoleIntf as tbRoleIntf where tbRoleIntf.id.roleId IN (select tbUsrRole.id.roleId from TbAsmiUserRole as tbUsrRole where tbUsrRole.id.userId =:UserId and tbUsrRole.id.appId =:AppId) and tbRoleIntf.id.interfaceId in (select tbIntfMaster.id.interfaceId from TbAsmiIntfMaster as tbIntfMaster where tbIntfMaster.id.appId = :AppId)")
	List<String> getListOfAuthorizedInterfaceId(@Param("UserId")String pUserId,@Param("AppId")String pAppId);
	
	//@Query("select tbintfMaster.id.interfaceId from TbAsmiIntfMaster as tbintfMaster where tbintfMaster.id.appId =:AppId and tbintfMaster.id.interfaceId NOT IN (select tbRoleIntf.id.interfaceId from TbAsmiRoleIntf as tbRoleIntf where tbRoleIntf.id.roleId IN (select tbUsrRole.id.roleId from TbAsmiUserRole as tbUsrRole where tbUsrRole.id.userId =:UserId and tbUsrRole.id.appId =:AppId and tbUsrRole.accessType ='D'))")
	//List<String> getListOfMasterInterfaceMinusUnauthorizedInterface(@Param("UserId")String pUserId, @Param("AppId")String pAppId);
	
	@Query("select tbintfMaster.id.interfaceId from TbAsmiIntfMaster as tbintfMaster where tbintfMaster.id.appId =:AppId and tbintfMaster.id.interfaceId NOT IN:interfaceList")
	List<String> getListOfMasterInterfaceMinusGivenInterface(@Param("AppId")String pAppId, @Param("interfaceList")List<String> pInterfaceList);
	
	//@Query("select tbintfMaster.id.interfaceId from TbAsmiIntfMaster as tbintfMaster where tbintfMaster.id.appId =:AppId and tbintfMaster.id.interfaceId NOT IN (select tbRoleIntf.id.interfaceId from TbAsmiRoleIntf as tbRoleIntf where tbRoleIntf.id.roleId IN (select tbUsrRole.id.roleId from TbAsmiUserRole as tbUsrRole where tbUsrRole.id.userId =:UserId and tbUsrRole.id.appId =:AppId and tbUsrRole.accessType ='A'))")
	//List<String> getListOfMasterInterfaceMinusAuthorizedInterface(@Param("UserId")String pUserId, @Param("AppId")String pAppId);
	
	@Query("SELECT c.id.interfaceId FROM TbAsmiRoleIntf as c WHERE c.id.roleId IN:roles and c.id.appId =:appId")
	List<String> findInterfaceIdsForRoleIdAndAppId(@Param("roles")List<String> lRoleslist,@Param("appId") String pappid);
	
	@Query("SELECT c.id.interfaceId FROM TbAsmiIntfMaster as c WHERE c.id.appId =:appId")
	List<String> findInterfaceIdsForAppIdFromInterfaceMaster(@Param("appId") String pappid);
	
}
