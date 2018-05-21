package com.iexceed.appzillon.domain.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiCsNonceDetail;
import com.iexceed.appzillon.domain.entity.TbAsmiCsNonceDetailPK;


@Repository
public interface TbAsmiValidateNonceDetailRepository extends JpaRepository<TbAsmiCsNonceDetail, TbAsmiCsNonceDetailPK> {

	@Query("Select tb from TbAsmiCsNonceDetail tb where tb.id.appId=:appId AND tb.id.requestId=:requestId AND tb.id.clientNonce=:clientNonce AND tb.id.serverNonce=:serverNonce")
	TbAsmiCsNonceDetail findRecWithAppIdReqIdCNonceSNonce(@Param("appId") String appId,@Param("requestId") String requestId,@Param("clientNonce") String clientNonce,@Param("serverNonce") String serverNonce);

	@Modifying
	@Query("delete from TbAsmiCsNonceDetail tb where tb.id.serverNonce=:serverNonce")
	void deleteRecWithsNonce(@Param("serverNonce") String serverNonce);
	
	@Query("Select tb from TbAsmiCsNonceDetail tb where tb.id.appId =:appId AND tb.id.requestId=:requestId AND tb.id.serverNonce=:serverNonce AND tb.id.clientNonce='N'")
	TbAsmiCsNonceDetail findRecWithAppIdReqIdSNonceDefaultcNonce(@Param("appId") String appId,@Param("requestId") String requestId,@Param("serverNonce") String serverNonce);

	@Modifying
	@Query("delete from TbAsmiCsNonceDetail tb where tb.id.deviceId=:deviceId")
	void deleteRecsWithDeviceId(@Param("deviceId") String deviceId);
	
}