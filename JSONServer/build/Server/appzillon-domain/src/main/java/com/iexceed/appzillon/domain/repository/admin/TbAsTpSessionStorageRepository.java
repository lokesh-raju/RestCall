package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAstpSessionStorage;
import com.iexceed.appzillon.domain.entity.TbAstpSessionStoragePK;

@Repository
public interface TbAsTpSessionStorageRepository extends JpaRepository<TbAstpSessionStorage, TbAstpSessionStoragePK>{
	
	@Query("select tb from TbAstpSessionStorage tb where tb.id.appId=:appId AND tb.id.userId=:userId AND tb.deviceId=:deviceId")
	List<TbAstpSessionStorage> findRecordsWithDeviceId(@Param("appId") String appId,@Param("userId") String userId,@Param("deviceId") String deviceId);
	
}