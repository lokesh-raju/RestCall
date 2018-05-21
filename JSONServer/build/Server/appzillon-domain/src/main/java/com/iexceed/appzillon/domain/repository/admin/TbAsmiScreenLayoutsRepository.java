package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAsmiScreenLayout;
import com.iexceed.appzillon.domain.entity.TbAsmiScreenLayoutPK;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 8:58 PM
 */
@Repository
public interface TbAsmiScreenLayoutsRepository extends JpaRepository<TbAsmiScreenLayout, TbAsmiScreenLayoutPK> {

	/*@Query("select tb.defaultTemplate from TbAsmiScreenLayout tb where tb.id.appId=:appId AND tb.id.layoutId=:layOutId AND tb.id.screenId=:screenId")
	String findCurrentDesigns(@Param("appId")  String appId, @Param("layOutId")String layOutId,@Param("screenId") String screenId);
*/
	@Query("select vw.microAppId,vw.microAppName,vw.microAppIcon from VwAsmiMicroapp vw where vw.appId=:appId AND vw.screenId=:screenId AND vw.microAppType='EXT'")
	List<Object[]> findMicroApps(@Param("appId") String appId,@Param("screenId") String screenId);
	
	@Query("select vw.microAppId,vw.microAppName,vw.microAppIcon from VwAsmiMicroapp vw where vw.appId=:appId AND vw.screenId=:screenId AND vw.microAppType='NAV'")
	List<Object[]> findNavigators(@Param("appId") String appId,@Param("screenId") String screenId);

	@Query("select vw.callformId,vw.callformName,vw.callformIcon from VwAsmiNanoapp vw where vw.appId=:appId AND vw.screenId=:screenId")
	List<Object[]> findWidgets(@Param("appId") String appId,@Param("screenId") String screenId);
	
	@Query("select vw.deviceGroupId,vw.deviceGroupDescription,vw.os,vw.height,vw.width,vw.orientation from VwAsmiDeviceGrp vw where vw.appId=:appId")
	List<Object[]> findDeviceGroupsByAppId(@Param("appId") String appId);
	/*
	@Query("select vw.screenId,vw.screenName,vw.layoutid  from VwAsmiScreen vw where vw.appId=:appId AND vw.deviceGroupId=:deviceGroupId AND vw.orientation=:orientation")
	List<Object[]> findListOfScreensB(@Param("appId") String appId, @Param("deviceGroupId") String deviceGroupId,
			@Param("orientation") String orientation);*/
}
