/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.admin;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiUserDevices;
import com.iexceed.appzillon.domain.entity.TbAsmiUserDevicesPK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiUserDevicesRepository extends JpaRepository<TbAsmiUserDevices, TbAsmiUserDevicesPK>, JpaSpecificationExecutor<TbAsmiUserDevices>{
	@Query("select tb from TbAsmiUserDevices tb where tb.id.userId =:userId")
	List<TbAsmiUserDevices> findByUserId(@Param("userId")String string);
	@Query("select tb from TbAsmiUserDevices tb where tb.id.userId =:userId AND tb.id.appId =:appId")
	List<TbAsmiUserDevices> findByUserIdAppId(@Param("userId")String userId, @Param("appId")String appId);
	
	@Query("select distinct tb.deviceStatus from TbAsmiUserDevices tb where tb.id.deviceId =:deviceId AND tb.id.appId =:appId")
	String getDeviceStatus(@Param("appId")String lappId, @Param("deviceId")String ldeviceId);
	
	@Query("select tb from TbAsmiUserDevices tb where tb.id.appId =:appId AND tb.id.deviceId =:deviceId")
	List<TbAsmiUserDevices> findByAppIdAndDeviceId(@Param("appId")String pAppId, @Param("deviceId")String pDeviceId);
	
	@Query("select count(*) from TbAsmiUserDevices tb where tb.id.appId =:appId AND tb.id.deviceId ='WEB'")
	int getWebInstalls(@Param("appId")String pAppId);
	
	@Query("select count(*) from TbAsmiUserDevices tb where tb.id.appId =:appId AND tb.id.deviceId ='WEB' AND tb.createTs>=:fromDate AND tb.createTs <=:toDate")
	int getWebInstallsByDate(@Param("appId")String pAppId, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

}
