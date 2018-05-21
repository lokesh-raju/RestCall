package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiAppMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiAppMasterPK;

@Repository
public interface TbAsmiAppMasterRepository extends JpaRepository<TbAsmiAppMaster, TbAsmiAppMasterPK>{

	@Query("select tb from TbAsmiAppMaster tb")
	List<TbAsmiAppMaster> findAllOTAFile();

	@Query("select tb from TbAsmiAppMaster tb where  tb.id.appId =:appId")
	List<TbAsmiAppMaster> findAppMasterByAppIdinList(@Param("appId") String appid);
	
	@Query("select tb from TbAsmiAppMaster tb where  tb.id.appId =:appId")
	TbAsmiAppMaster findAppMasterByAppId(@Param("appId") String appid);
}
