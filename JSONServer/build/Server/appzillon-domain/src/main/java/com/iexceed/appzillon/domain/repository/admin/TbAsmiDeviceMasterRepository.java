/**
 * 
 */
package com.iexceed.appzillon.domain.repository.admin;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiDeviceMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiDeviceMasterPK;

/**
 * @author Ripu
 *
 */
@Repository
public interface TbAsmiDeviceMasterRepository extends JpaRepository<TbAsmiDeviceMaster, TbAsmiDeviceMasterPK> {
	@Query("select tb from TbAsmiDeviceMaster tb where tb.id.deviceId =:DeviceID")
	List<TbAsmiDeviceMaster> findDeviceMasterByDeviceID(@Param("DeviceID") String deviceID);
	
	@Query("select tb from TbAsmiDeviceMaster tb where tb.id.appId =:AppId")
	List<TbAsmiDeviceMaster> findDeviceMasterByAppID(@Param("AppId") String appid);
	
	@Query("select tb from TbAsmiDeviceMaster tb where tb.id.appId =:AppId AND tb.id.deviceId =:DeviceID")
	List<TbAsmiDeviceMaster> findDeviceMasterByAppIdDeviceId(@Param("AppId") String appid, @Param("DeviceID") String deviceID);
	
	@Query("select tb from TbAsmiDeviceMaster tb where tb.os =:os")
	List<TbAsmiDeviceMaster> findDeviceMasterByOS(@Param("os") String lOs);
	
	@Query("select tb from TbAsmiDeviceMaster tb where tb.id.appId =:AppId AND tb.os =:OS")
	List<TbAsmiDeviceMaster> findDeviceMasterByAppIdAndOs(@Param("AppId") String appid, @Param("OS") String OS);
	
	@Query("select tb from TbAsmiDeviceMaster tb where tb.id.deviceId =:DeviceID AND tb.os =:OS")
	List<TbAsmiDeviceMaster> findDeviceMasterByDeviceIdAndOs(@Param("DeviceID") String appid, @Param("OS") String OS);
	
	@Query("select tb from TbAsmiDeviceMaster tb where tb.id.appId =:AppId AND tb.id.deviceId =:DeviceID AND tb.os =:OS")
	List<TbAsmiDeviceMaster> findDeviceMasterByAppIdDeviceIdAndOs(@Param("AppId")String lAppId, @Param("DeviceID")String lDeviceId, @Param("OS")String lOs);

	@Query("select tb.os, count(*) from TbAsmiDeviceMaster tb where tb.id.appId =:appId group by tb.os")
	List<Object[]> getInstallsByOs(@Param("appId")String lAppId);
	
	@Query("select tb.os,count(*),YEAR(tb.createTs),MONTH(tb.createTs),DAY(tb.createTs) from TbAsmiDeviceMaster tb where tb.id.appId =:appId AND tb.createTs>=:fromDate AND tb.createTs <=:toDate group by tb.os,YEAR(tb.createTs),MONTH(tb.createTs),DAY(tb.createTs)")
	List<Object[]> getInstallsByOsAndDate(@Param("appId")String lAppId, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	@Query("select tb.os,count(*),YEAR(tb.createTs),MONTH(tb.createTs),DAY(tb.createTs) from TbAsmiDeviceMaster tb where tb.id.appId =:appId AND tb.createTs <=:toDate group by tb.os,YEAR(tb.createTs),MONTH(tb.createTs),DAY(tb.createTs)")
	List<Object[]> getInstallsByOsAndBeforeDate(@Param("appId")String lAppId,@Param("toDate") Date toDate);

	@Query("select tb.os,count(*),YEAR(tb.createTs),MONTH(tb.createTs),DAY(tb.createTs) from TbAsmiDeviceMaster tb where tb.id.appId =:appId AND tb.createTs>=:fromDate group by tb.os,YEAR(tb.createTs),MONTH(tb.createTs),DAY(tb.createTs)")
	List<Object[]> getInstallsByOsAndAfterDate(@Param("appId")String lAppId, @Param("fromDate") Date fromDate);

}
