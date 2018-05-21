package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiSmsUser;
import com.iexceed.appzillon.domain.entity.TbAsmiSmsUserPK;

/**
 * 
 * @author Ripu
 * This Repository is written for doing all required operation with DB for sending SMS to user
 */
@Repository
public interface TbAsmiSmsUserRepository extends JpaRepository<TbAsmiSmsUser, TbAsmiSmsUserPK>{

	@Query("select tb from TbAsmiSmsUser tb where tb.id.appId =:appId")
	List<TbAsmiSmsUser> findMobileNumByAppId(@Param("appId") String lAppId);
	
	@Query("select tb from TbAsmiSmsUser tb where tb.mobileNumber =:mobileNumber")
	TbAsmiSmsUser findAppIdUserIdByMobileNum(@Param("mobileNumber") String lMobileNumber);
	
	/*@Query("select tb from TbAsmiSmsUser tb where tb.id.appId =:appId AND tb.mobileNumber =:mobileNumber")
	TbAsmiSmsUser findUserIdByAppIdAndMobileNum(@Param("appId") String lAppId, @Param("mobileNumber") String lMobileNumber);*/
	
	@Query("select tb from TbAsmiSmsUser tb where tb.id.appId =:appId AND tb.mobileNumber =:mobileNumber ORDER BY tb.createTs DESC")
	TbAsmiSmsUser findUserIdByAppIdAndMobileNum(@Param("appId") String lAppId, @Param("mobileNumber") String lMobileNumber);
	
	@Query("select tb from TbAsmiSmsUser tb where tb.id.appId =:appId AND tb.id.userId =:userId")
	TbAsmiSmsUser findMobileNumberByAppIdAndUserId(@Param("appId") String lAppId, @Param("userId") String lUserId);
}
