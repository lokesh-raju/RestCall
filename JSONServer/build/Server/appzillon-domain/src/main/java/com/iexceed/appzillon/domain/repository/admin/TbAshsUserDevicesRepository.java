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

import com.iexceed.appzillon.domain.entity.history.TbAshsUserDevices;
import com.iexceed.appzillon.domain.entity.history.TbAshsUserDevicesPK;


/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAshsUserDevicesRepository extends JpaRepository<TbAshsUserDevices, TbAshsUserDevicesPK>, JpaSpecificationExecutor<TbAshsUserDevices>{

	@Query("select max(tb.id.versionNo) from TbAshsUserDevices tb where tb.id.deviceId =:deviceId AND tb.id.appId =:appId AND tb.id.userId =:userId")
	Integer findMaxVersionNoByAppIdDeviceIdUserId(@Param("appId")String lappId, @Param("deviceId")String ldeviceId, @Param("userId")String luserId);
	
	
}
