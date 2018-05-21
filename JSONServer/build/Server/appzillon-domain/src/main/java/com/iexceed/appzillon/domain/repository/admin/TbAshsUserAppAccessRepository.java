/**
 * 
 */
package com.iexceed.appzillon.domain.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.history.TbAshsUserAppAccess;
import com.iexceed.appzillon.domain.entity.history.TbAshsUserAppAccessPK;

/**
 * @author ripu.pandey
 *
 */
@Repository
public interface TbAshsUserAppAccessRepository extends JpaRepository<TbAshsUserAppAccess, TbAshsUserAppAccessPK>,
JpaSpecificationExecutor<TbAshsUserAppAccess>{
	@Query("select max(c.id.versionNo) from TbAshsUserAppAccess as c where c.id.appId=:appId and c.id.userId=:userId")
	Integer findMaxVersionNo(@Param("appId") String appid,@Param("userId") String userId);
}
