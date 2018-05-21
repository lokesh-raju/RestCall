package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillon.domain.entity.history.TbAshsUserPasswords;
import com.iexceed.appzillon.domain.entity.history.TbAshsUserPasswordsPK;

public interface TbAshsUserPasswordsRepository extends
		CrudRepository<TbAshsUserPasswords, TbAshsUserPasswordsPK> {
	@Query("select tb from TbAshsUserPasswords tb")
	TbAshsUserPasswords findAllCssdPasswords();

	@Query("select tb from TbAshsUserPasswords as tb where tb.id.userId =:puserId and tb.id.appId=:pappId")
	List<TbAshsUserPasswords> findPasswordsByUserIdAppId(@Param("puserId") String puserId,
			@Param("pappId") String pappId);

	@Query("select tb from TbAshsUserPasswords as tb where tb.id.userId =:puserId and tb.id.appId=:pappId order by tb.createTs ")
	List<TbAshsUserPasswords> findrowsByUserIdAppIdorderbytime(@Param("puserId") String puserId,
			@Param("pappId") String pappId);
	
	@Query("select tb from TbAshsUserPasswords as tb where tb.id.userId =:puserId and tb.id.appId=:pappId order by tb.createTs Desc")
	List<TbAshsUserPasswords> findrowsByUserIdAppIdorderbytimeDesc(@Param("puserId") String puserId,
			@Param("pappId") String pappId);

	@Query("select tb from TbAshsUserPasswords as tb where tb.id.userId =:puserId and tb.id.appId=:pappId and tb.id.pin=:ppin")
	TbAshsUserPasswords findrowByUserIdAppIdPin(@Param("puserId") String puserId,@Param("pappId") String pappId,
			@Param("ppin") String ppin);
}
