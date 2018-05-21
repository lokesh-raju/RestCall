package com.iexceed.appzillon.domain.repository.meta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAstpARMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

@Repository
public interface TbAstpARMasterRepository extends JpaRepository<TbAstpARMaster,Integer>, JpaSpecificationExecutor<TbAstpARMaster>{
	
	@Query("SELECT tb from TbAstpARMaster tb where tb.appId = :appId and tb.regionCode = :regionCode")
	List<TbAstpARMaster> findByAppIdRegionCode(@Param("appId")String appId, @Param("regionCode") String regionCode);

	@Query("SELECT tb from TbAstpARMaster tb where tb.appId = :appId and tb.regionCode = :regionCode and tb.category = :category")
	List<TbAstpARMaster> findByAppIdRegionCodeCategory(@Param("appId")String appId, @Param("regionCode") String regionCode, @Param("category") String category);
	
	@Query("SELECT tb from TbAstpARMaster tb where tb.appId = :appId and tb.latitude = :latitude and tb.longitude =:longitude")
	List<TbAstpARMaster> findByAppIdLatLong(@Param("appId")String appId, @Param("latitude") String latitude,@Param("longitude") String longitude);
	
	@Query("SELECT tb from TbAstpARMaster tb where tb.appId = :appId and tb.latitude = :latitude and tb.longitude =:longitude and tb.category = :category")
	List<TbAstpARMaster> findByAppIdLatLongCategory(@Param("appId")String appId, @Param("category") String category, @Param("latitude") String latitude,@Param("longitude") String longitude);
	
	@Query("SELECT tb from TbAstpARMaster tb where tb.appId = :appId and tb.regionCode = :regionCode and tb.category = :category and tb.latitude = :latitude and tb.longitude = :longitude and tb.title = :title and tb.additionalInfo = :additionalInfo")
	List<TbAstpARMaster> findByAll(@Param("appId")String appId, @Param("regionCode") String regionCode, @Param("category") String category, @Param("latitude") String latitude, @Param("longitude") String longitude, @Param("title") String title, @Param("additionalInfo") String additionalInfo);
	
	@Query("SELECT tb from TbAstpARMaster tb where tb.appId like :appId and tb.regionCode like :regionCode and tb.category like :category and tb.latitude like :latitude and tb.longitude like :longitude and tb.title like :title and tb.additionalInfo like :additionalInfo")
	List<TbAstpARMaster> findByLikeAll(@Param("appId")String appId, @Param("regionCode") String regionCode, @Param("category") String category, @Param("latitude") String latitude, @Param("longitude") String longitude, @Param("title") String title, @Param("additionalInfo") String additionalInfo);
}
