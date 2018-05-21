package com.iexceed.appzillon.domain.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.history.TbAshsSecurityParams;


@Repository
public interface TbAshsSecurityParamsRepository extends JpaRepository<TbAshsSecurityParams, String>, JpaSpecificationExecutor<TbAshsSecurityParams>{
	@Query("select max(c.versionNo) from TbAshsSecurityParams as c where c.appId=:appId")
	Integer findMaxVersionNoByAppId(@Param("appId") String appid);
}
