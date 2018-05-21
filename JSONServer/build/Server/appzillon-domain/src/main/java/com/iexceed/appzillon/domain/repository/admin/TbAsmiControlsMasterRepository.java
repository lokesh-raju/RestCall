/**
 * 
 */
package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiControlsMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiControlsMasterPK;

/**
 * @author ripu.pandey
 *
 */
@Repository
public interface TbAsmiControlsMasterRepository extends JpaRepository<TbAsmiControlsMaster, TbAsmiControlsMasterPK> {

	@Query("select tb from TbAsmiControlsMaster tb")
	List<TbAsmiControlsMaster> findAllTbAsmiControlsMaster();
	
	@Query("SELECT c.id.controlId FROM TbAsmiControlsMaster as c WHERE c.id.appId=:appId")
	List<String> findControlIdBasedOnAppId(@Param("appId") String appId);
}
