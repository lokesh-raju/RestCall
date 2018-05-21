package com.iexceed.appzillon.domain.repository.meta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsnfGroupDevice;
import com.iexceed.appzillon.domain.entity.TbAsnfGroupDevicePK;

@Repository
public interface TbAsnfGroupDeviceRepository extends
		JpaRepository<TbAsnfGroupDevice, TbAsnfGroupDevicePK> {
	@Query("Select tb.id.notifRegId FROM TbAsnfGroupDevice tb where tb.id.appId=:appId AND tb.id.groupId=:groupId")
	List<String> findRegIdsByAppIdAndGroupId(@Param("appId") String appId,
			@Param("groupId") String groupId);

	@Modifying
	@Query("DELETE FROM TbAsnfGroupDevice tb where tb.id.notifRegId IN:oldRegIds AND tb.id.appId=:appId AND tb.id.groupId=:groupId")
	void deleteAllRowsContaingRegIds(
			@Param("oldRegIds") List<String> oldRegIds,
			@Param("appId") String appId, @Param("groupId") String groupId);

	@Modifying
	@Query("DELETE FROM TbAsnfGroupDevice tb where tb.id.appId=:appId AND tb.id.groupId=:groupId")
	void deleteByAppIdAndGroupId(@Param("appId") String appId,
			@Param("groupId") String groupId);

	@Query("Select tb.id.notifRegId FROM TbAsnfGroupDevice tb where tb.id.appId=:appId AND tb.id.groupId IN:list")
	List<String> findRegIdsByAppIdAndGroupIdList(@Param("appId") String lappId,
			@Param("list") List<String> groups);

}
