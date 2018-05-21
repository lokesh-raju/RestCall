package com.iexceed.appzillon.domain.repository.meta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.iexceed.appzillon.domain.entity.TbAstpBeacon;



public interface TbAstpBeaconRepository  extends JpaRepository<TbAstpBeacon, Integer>,JpaSpecificationExecutor<TbAstpBeacon>{
	
	@Query("SELECT t FROM TbAstpBeacon t where t.status ='P'")
	List<TbAstpBeacon> getBeaconDetails();
}
