package com.iexceed.appzillon.domain.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiSecurityParams;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiSecurityParamsRepository extends JpaRepository<TbAsmiSecurityParams, String>, JpaSpecificationExecutor<TbAsmiSecurityParams>{
	@Query("SELECT c FROM TbAsmiSecurityParams as c WHERE c.appId =:appId")
	TbAsmiSecurityParams findSecurityParamsbyAppId(@Param("appId") String appId);
}
