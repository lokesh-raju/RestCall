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

import com.iexceed.appzillon.domain.entity.TbAsmiIntfMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiIntfMasterPK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiIntfMasterRepository extends JpaRepository<TbAsmiIntfMaster, TbAsmiIntfMasterPK>,JpaSpecificationExecutor<TbAsmiIntfMaster>{

	@Query(name = "findinterfacebyappid", value = "SELECT c FROM TbAsmiIntfMaster as c WHERE c.id.appId=:appId")
	List<TbAsmiIntfMaster> findinterfacebyappid(@Param("appId") String appId);

	@Query(name = "findinterfacebyappidinterfaceid", value = "SELECT c FROM TbAsmiIntfMaster as c WHERE c.id.appId=:appId and c.id.interfaceId=:interfaceId")
	TbAsmiIntfMaster findinterfacebyappidinterfaceid(
			@Param("appId") String appId,
			@Param("interfaceId") String interfaceId);
	@Query(name = "getinterfacebyappidinterfaceid", value = "SELECT c FROM TbAsmiIntfMaster as c WHERE c.id.appId=:appId and c.id.interfaceId=:interfaceId")
	List<TbAsmiIntfMaster> getinterfacebyappidinterfaceid(
			@Param("appId") String appId,
			@Param("interfaceId") String interfaceId);
    @Query("select tb.id.interfaceId,tb.interfaceDef from TbAsmiIntfMaster tb where tb.id.appId =:appId AND tb.id.interfaceId IN:interfaceIds")
    List<Object[]> findIntfDefbyAppIdAndInterfaceId(@Param("appId") String appId, @Param("interfaceIds") List<String> interfaceIds);
}