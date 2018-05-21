package com.iexceed.appzillon.domain.repository.meta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiAppOsVersion;
import com.iexceed.appzillon.domain.entity.TbAsmiAppOsVersionPK;

@Repository
public interface TbAsmiAppOsVersionRepository extends JpaRepository<TbAsmiAppOsVersion, TbAsmiAppOsVersionPK>{

	@Query("select tb from TbAsmiAppOsVersion tb")
	List<TbAsmiAppOsVersion> findAllApp();

	@Query("select tb from TbAsmiAppOsVersion tb where  tb.id.appId =:appId")
	List<TbAsmiAppOsVersion> findAppVersionByAppId(@Param("appId") String appid);
	
	//@Query("select tb from TbAsmiAppOsVersion tb where  tb.id.appId =:appId AND tb.id.os =:os")
	//TbAsmiAppOsVersion findAppVersionByAppIdAndOS(@Param("appId") String appid, @Param("os") String lOS);
	
	@Query("select MAX(tb.id.appVersion) from TbAsmiAppOsVersion tb where  tb.id.appId =:appId AND tb.id.os =:os")
	String findMaxAppVersionByAppIdAndOS(@Param("appId") String appid, @Param("os") String lOS);
	
}
