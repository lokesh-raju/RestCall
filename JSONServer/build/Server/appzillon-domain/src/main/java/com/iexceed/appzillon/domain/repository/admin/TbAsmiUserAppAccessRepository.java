/**
 * 
 */
package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiUserAppAccess;
import com.iexceed.appzillon.domain.entity.TbAsmiUserAppAccessPK;

/**
 * @author ripu.pandey
 *
 */
@Repository
public interface TbAsmiUserAppAccessRepository extends JpaRepository<TbAsmiUserAppAccess, TbAsmiUserAppAccessPK>,
JpaSpecificationExecutor<TbAsmiUserAppAccess>{
	
	@Query("select tb from TbAsmiUserAppAccess as tb where tb.id.userId =:UserId")
	List<TbAsmiUserAppAccess> getAllwedAppIdByUserId(@Param("UserId") String pUserId);
	
	@Query("select tb from TbAsmiUserAppAccess as tb where tb.id.appId =:appId AND tb.id.userId =:userId")
	List<TbAsmiUserAppAccess> getUserAppAccessdByAppIdUserId(@Param("appId") String appId,@Param("userId") String userId);
	
	@Query("select tb.id.allowedAppId from TbAsmiUserAppAccess as tb where tb.id.userId =:UserId AND tb.appAccess =:AppAccess AND tb.id.appId =:appId ")
	List<String> getAllwedAppIdByUserIdAndAppAllowed(@Param("UserId") String pUserId, @Param("AppAccess") String pAppAccess, @Param("appId") String pAppId);
}
