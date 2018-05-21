package com.iexceed.appzillon.domain.repository.meta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiAppUser;
import com.iexceed.appzillon.domain.entity.TbAsmiAppUserPK;

@Repository
public interface TbAsmiAppUserRepository extends JpaRepository<TbAsmiAppUser, TbAsmiAppUserPK>{

	@Query("select tb from TbAsmiAppUser tb")
	List<TbAsmiAppUser> findAllAppUser();

	@Query("select tb from TbAsmiAppUser tb where tb.id.userId =:userId AND tb.id.parentAppId =:parentAppId")
	List<TbAsmiAppUser> findChildAppIdByUserIdandMasterAppId(@Param("userId") String lUserId, @Param("parentAppId") String lParentAppId);

	@Query("select tb from TbAsmiAppUser tb where tb.id.parentAppId =:parentAppId")
	List<TbAsmiAppUser> findChildAppIdByMasterAppId(@Param("parentAppId") String lParentAppId);
	
	@Query("select tb from TbAsmiAppUser tb where tb.id.userId =:userId")
	List<TbAsmiAppUser> findChildAppIdByUserId(@Param("userId") String luserId);
	
}
