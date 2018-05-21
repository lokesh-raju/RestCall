package com.iexceed.appzillon.domain.repository.meta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsnfDevicesMaster;
import com.iexceed.appzillon.domain.entity.TbAsnfDevicesMasterPK;

@Repository
public interface TbAsnfDevicesMasterRepository extends
		JpaRepository<TbAsnfDevicesMaster, TbAsnfDevicesMasterPK>,
		JpaSpecificationExecutor<TbAsnfDevicesMaster> {
	@Query("SELECT tb FROM TbAsnfDevicesMaster tb where tb.id.notifRegId IN:regIdsUnderGroup AND tb.id.appId=:appId AND tb.status='Y'")
	List<TbAsnfDevicesMaster> findByInRegIdListNAppId(
			@Param("regIdsUnderGroup") List<String> regIdsUnderGroup,
			@Param("appId") String appId);

	@Query("SELECT tb FROM TbAsnfDevicesMaster tb where tb.id.notifRegId NOT IN:regIdsUnderGroup AND tb.id.appId=:appId AND tb.status='Y'")
	List<TbAsnfDevicesMaster> findByNotInRegIdListNAppId(
			@Param("regIdsUnderGroup") List<String> regIdsUnderGroup,
			@Param("appId") String appId);

	@Query("SELECT tb FROM TbAsnfDevicesMaster tb where tb.id.appId=:appId AND tb.status='Y' ")
	List<TbAsnfDevicesMaster> findByAppIdNStatus(@Param("appId") String appId);

	@Query("SELECT tb FROM TbAsnfDevicesMaster tb where tb.deviceId  IN:list AND tb.id.appId=:appId")
	List<TbAsnfDevicesMaster> findByDeviceIdsListNAppId(
			@Param("list") List<String> devices, @Param("appId") String pappId);

	@Query("SELECT tb.id.notifRegId FROM TbAsnfDevicesMaster tb where tb.deviceId  IN:list AND tb.id.appId=:appId")
	List<String> findRegIdsByDeviceIdsListNAppId(
			@Param("list") List<String> devices, @Param("appId") String pappId);

	@Query("SELECT tb FROM TbAsnfDevicesMaster tb where tb.deviceName LIKE:deviceName AND tb.osId LIKE:osId")
	List<TbAsnfDevicesMaster> findByLikeDeviceNameNOsId(
			@Param("deviceName") String deviceName, @Param("osId") String osId);
	
	
	@Query("SELECT tb FROM TbAsnfDevicesMaster tb where tb.id.notifRegId NOT IN:regIdsUnderGroup AND tb.id.appId=:appId AND tb.osId IN:osIds AND tb.status='Y'")
	List<TbAsnfDevicesMaster> findByInOsIdNotInRegIdListNAppId(
			@Param("regIdsUnderGroup") List<String> regIdsUnderGroup,
			@Param("osIds") List<String> regOsIds,
			@Param("appId") String appId);

	//below method added by ripu on 14-Jan-2016
	@Query("SELECT tb FROM TbAsnfDevicesMaster tb where tb.id.appId =:appId AND tb.deviceId =:deviceId AND tb.status='Y'")
	List<TbAsnfDevicesMaster> findByAppIdAndDeviceId(@Param("appId") String appId, @Param("deviceId") String deviceId);

	@Query("SELECT tb FROM TbAsnfDevicesMaster tb where tb.id.appId=:appId AND tb.osId IN:osIds AND tb.status='Y'")
	List<TbAsnfDevicesMaster> findByInOsIdListAndAppId(@Param("osIds") List<String> OsIdList, @Param("appId") String appId );
	

}
