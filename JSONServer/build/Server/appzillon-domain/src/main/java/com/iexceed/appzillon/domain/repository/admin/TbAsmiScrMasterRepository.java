/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiScrMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiScrMasterPK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiScrMasterRepository extends CrudRepository<TbAsmiScrMaster, TbAsmiScrMasterPK>,JpaSpecificationExecutor<TbAsmiScrMaster>{

	@Query("SELECT c FROM TbAsmiScrMaster as c WHERE c.id.appId=:appId")
	List<TbAsmiScrMaster> findscreenbyappid(@Param("appId") String appId);
	
	@Query("SELECT c FROM TbAsmiScrMaster as c WHERE c.id.appId=:appId and c.id.screenId=:screenId")
	TbAsmiScrMaster findscreenbyappidscrid(@Param("appId") String appId,
			@Param("screenId") String screenId);
	
	@Query("SELECT c.id.screenId FROM TbAsmiScrMaster as c WHERE c.id.appId=:appId")
	List<String> findScreenIdBasedOnAppId(@Param("appId") String appId);
}