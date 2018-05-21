package com.iexceed.appzillon.domain.repository.meta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiAppFiles;
import com.iexceed.appzillon.domain.entity.TbAsmiAppFilesPK;

@Repository
public interface TbAsmiAppFileRepository extends JpaRepository<TbAsmiAppFiles, TbAsmiAppFilesPK>{

	@Query("select tb from TbAsmiAppFiles tb")
	List<TbAsmiAppFiles> findAllOTAFile();

	@Query("select tb from TbAsmiAppFiles tb where tb.id.appVersion =:appVersion AND tb.id.appId =:appId")
	List<TbAsmiAppFiles> findOTAByAppIdAppVersion(
			@Param("appVersion") String appVersion, @Param("appId") String appid);

	@Query("select tb from TbAsmiAppFiles tb where tb.id.appVersion =:appVersion AND tb.id.appId =:appId AND tb.id.os =:os")
	List<TbAsmiAppFiles> findOTAByAppIdAppVersionAndOS(
			@Param("appVersion") String appVersion, @Param("appId") String appid, @Param("os") String os);

	@Query("select tb from TbAsmiAppFiles tb where tb.id.appId =:appId")
	List<TbAsmiAppFiles> findOTAByAppId(@Param("appId") String appid);
	
	@Query("select tb from TbAsmiAppFiles tb where tb.id.appId =:appId AND tb.id.os =:os")
	List<TbAsmiAppFiles> findOTAByAppIdAndOS(@Param("appId") String appid, @Param("os") String os);
	
	//@Query("select tb from TbAsmiAppFiles tb where tb.id.appVersion >=  :startVersion AND tb.id.appVersion <=  :endVersion  AND tb.id.appId LIKE :appId  AND tb.id.os LIKE :OS")
	@Query("select tb from TbAsmiAppFiles tb where tb.id.appVersion >  :startVersion AND tb.id.appVersion <=  :endVersion  AND tb.id.appId =:appId  AND tb.id.os =:OS")
	List<TbAsmiAppFiles> findOTAFileBetweenVersion(@Param("startVersion") String startVersion, @Param("endVersion") String endVersion, @Param("appId") String appid, @Param("OS") String os);
	
	@Query("select tb from TbAsmiAppFiles tb where tb.id.os =:os")
	List<TbAsmiAppFiles> findOTAByOS(@Param("os") String lOS);
}
