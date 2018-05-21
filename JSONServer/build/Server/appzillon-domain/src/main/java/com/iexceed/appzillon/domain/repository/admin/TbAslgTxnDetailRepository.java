/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.admin;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAslgTxnDetail;

/**
 *
 * @author Vinod Rawat
 */
@Repository
public interface TbAslgTxnDetailRepository extends JpaRepository<TbAslgTxnDetail, String>,JpaSpecificationExecutor<TbAslgTxnDetail>{
	@Query("select tb.appId,tb.userId,count(*) from TbAslgTxnDetail tb where (tb.interfaceId='appzillonReLoginRequest' OR tb.interfaceId='appzillonAuthenticationRequest') AND (tb.txnStat='S' AND tb.createTs >=:stDate AND tb.createTs<=:enDate)  GROUP BY tb.appId,tb.userId having tb.appId LIKE :appId")
	List <Object[]> findLoginDetailsBetweenDate(@Param("stDate")Timestamp stDate,@Param("enDate") Timestamp enDate,
			@Param("appId") String appId);
	
	@Query("select tb.appId,tb.createTs,count(*) from TbAslgTxnDetail tb where (tb.interfaceId='appzillonReLoginRequest' OR tb.interfaceId='appzillonAuthenticationRequest') AND tb.txnStat='S'  GROUP BY tb.appId,tb.createTs having tb.createTs >=:stDate AND tb.createTs<=:enDate AND tb.appId LIKE :appId ORDER BY tb.createTs")
	List <Object[]> findAppLoginDetailsBetweenDate(@Param("stDate")Timestamp stDate,@Param("enDate") Timestamp enDate,
			@Param("appId") String appId);
	@Query("select tb.interfaceId,tb.createTs,count(*) from TbAslgTxnDetail tb  where tb.userId LIKE :userId AND tb.appId LIKE :appId AND tb.deviceId LIKE :deviceId GROUP BY tb.interfaceId,tb.createTs having tb.createTs >=:Date AND tb.interfaceId LIKE :interfaceId ORDER BY tb.createTs")
	List<Object[]> findlastMonthDateNInterfaceTrans(@Param("interfaceId") String fiId,@Param("Date") Date date,@Param("appId") String appId,@Param("userId") String userId,@Param("deviceId") String deviceId);

	@Query("select intf.description, tb.createTs,count(*) from TbAslgTxnDetail tb, TbAsmiIntfMaster intf where tb.interfaceId = intf.id.interfaceId AND tb.userId LIKE :userId AND tb.appId LIKE :appId AND tb.deviceId LIKE :deviceId GROUP BY tb.interfaceId,tb.createTs having tb.createTs >=:StartDate AND tb.createTs <=:EndDate AND tb.interfaceId LIKE :interfaceId ORDER BY tb.createTs")
	List<Object[]> findTransactionDetailBetweenGivenDateRange(@Param("interfaceId") String fiId,@Param("StartDate") Date startDate,@Param("EndDate") Date endDate, @Param("appId") String appId,@Param("userId") String userId,@Param("deviceId") String deviceId);

	@Query("select tb.txnRef, tb.deviceId, tb.appId, tb.userId, tb.stTm, tb.endTm, tb.status, intf.description, tb.extStTm , tb.extEndTm from TbAslgTxnDetail tb, TbAsmiIntfMaster intf where tb.interfaceId = intf.id.interfaceId AND tb.userId LIKE :userId AND tb.appId LIKE :appId AND tb.deviceId LIKE :deviceId AND tb.interfaceId LIKE :interfaceId AND tb.status LIKE :txnStatus AND tb.createTs >=:startDate AND tb.createTs <=:endDate")
	List<Object[]> findTransactionDetailsBetweenDateRange(@Param("interfaceId") String fiId,@Param("startDate") Timestamp startDate,@Param("endDate") Timestamp endDate, @Param("appId") String appId,@Param("userId") String userId,@Param("deviceId") String deviceId, @Param("txnStatus") String txnStatus, Pageable pageable);
	
	@Query("select tb.txnRef, tb.deviceId, tb.appId, tb.userId, tb.stTm, tb.endTm, tb.status, intf.description, tb.extStTm , tb.extEndTm from TbAslgTxnDetail tb, TbAsmiIntfMaster intf where tb.interfaceId = intf.id.interfaceId AND tb.userId LIKE :userId AND tb.appId LIKE :appId AND tb.deviceId LIKE :deviceId AND tb.interfaceId LIKE :interfaceId AND tb.status LIKE :txnStatus AND tb.createTs <=:endDate")
	List<Object[]> findTransactionDetailsBeforeDate(@Param("interfaceId") String fiId,@Param("endDate") Timestamp endDate, @Param("appId") String appId,@Param("userId") String userId,@Param("deviceId") String deviceId, @Param("txnStatus") String txnStatus, Pageable pageable);
		
	@Query("select tb.txnRef, tb.deviceId, tb.appId, tb.userId, tb.stTm, tb.endTm, tb.status, intf.description, tb.extStTm , tb.extEndTm from TbAslgTxnDetail tb, TbAsmiIntfMaster intf where tb.interfaceId = intf.id.interfaceId AND tb.userId LIKE :userId AND tb.appId LIKE :appId AND tb.deviceId LIKE :deviceId AND tb.interfaceId LIKE :interfaceId AND tb.status LIKE :txnStatus AND tb.createTs >=:startDate")
	List<Object[]> findTransactionDetailsAfterDate(@Param("interfaceId") String fiId,@Param("startDate") Timestamp startDate, @Param("appId") String appId,@Param("userId") String userId,@Param("deviceId") String deviceId, @Param("txnStatus") String txnStatus, Pageable pageable);
	
	@Query("select tb.appId,tb.userId,count(*) from TbAslgTxnDetail tb where (tb.interfaceId='appzillonReLoginRequest' OR tb.interfaceId='appzillonAuthenticationRequest') AND tb.txnStat='S'  GROUP BY tb.appId,tb.userId having tb.appId LIKE :appId")
	List<Object[]> findLoginDetails(@Param("appId") String appId);
	
	@Query("select tb.appId,tb.userId,count(*) from TbAslgTxnDetail tb where (tb.interfaceId='appzillonReLoginRequest' OR tb.interfaceId='appzillonAuthenticationRequest') AND (tb.txnStat='S' AND tb.createTs<=:enDate)  GROUP BY tb.appId,tb.userId having tb.appId LIKE :appId")
	List<Object[]> findLoginDetailsBeforeDate(@Param("enDate") Timestamp enDate, @Param("appId") String appId);
	
	@Query("select tb.appId,tb.userId,count(*) from TbAslgTxnDetail tb where (tb.interfaceId='appzillonReLoginRequest' OR tb.interfaceId='appzillonAuthenticationRequest') AND (tb.txnStat='S' AND tb.createTs>=:stDate)  GROUP BY tb.appId,tb.userId having tb.appId LIKE :appId")
	List<Object[]> findLoginDetailsAfterDate(@Param("stDate") Timestamp stDate, @Param("appId") String appId);

	@Query("select tb.appId,tb.createTs,count(*) from TbAslgTxnDetail tb where (tb.interfaceId='appzillonReLoginRequest' OR tb.interfaceId='appzillonAuthenticationRequest') AND tb.txnStat='S'  GROUP BY tb.appId,tb.createTs having  tb.appId LIKE :appId ORDER BY tb.createTs")
	List<Object[]> findAppLoginDetails(@Param("appId") String appId);
	
	@Query("select tb.appId,tb.createTs,count(*) from TbAslgTxnDetail tb where (tb.interfaceId='appzillonReLoginRequest' OR tb.interfaceId='appzillonAuthenticationRequest') AND (tb.txnStat='S' AND tb.createTs<=:enDate)  GROUP BY tb.appId,tb.createTs having  tb.appId LIKE :appId ORDER BY tb.createTs")
	List<Object[]> findAppLoginDetailsBeforeDate(@Param("enDate") Timestamp enDate, @Param("appId") String appId);
	
	@Query("select tb.appId,tb.createTs,count(*) from TbAslgTxnDetail tb where (tb.interfaceId='appzillonReLoginRequest' OR tb.interfaceId='appzillonAuthenticationRequest') AND (tb.txnStat='S' AND tb.createTs>=:stDate)  GROUP BY tb.appId,tb.createTs having  tb.appId LIKE :appId ORDER BY tb.createTs")
	List<Object[]> findAppLoginDetailsAfterDate(@Param("stDate") Timestamp stDate, @Param("appId") String appId);
	
	@Query("select vw.description,vw.txnStat,min(vw.minTime),max(vw.maxTime),avg(vw.avgTime),sum(vw.intfCount) from VwTxnAppUsage vw where vw.appId LIKE :appId AND vw.accessDate>=:stDate AND vw.accessDate<=:enDate GROUP BY vw.description,vw.txnStat")
	List<Object[]> findTxnAppUsageDetailsByDate(@Param("appId") String appId, @Param("stDate") Date stDate, @Param("enDate") Date enDate);

	@Query("select vw.description,vw.txnStat,min(vw.minTime),max(vw.maxTime),avg(vw.avgTime),sum(vw.intfCount) from VwTxnAppUsage vw where vw.appId LIKE :appId AND vw.accessDate<=:enDate GROUP BY vw.description,vw.txnStat")
    List<Object[]> findTxnAppUsageDetailsBeforeDate(@Param("appId") String appId,@Param("enDate") Date enDate);

	@Query("select vw.description,vw.txnStat,min(vw.minTime),max(vw.maxTime),avg(vw.avgTime),sum(vw.intfCount) from VwTxnAppUsage vw where vw.appId LIKE :appId AND vw.accessDate>=:stDate GROUP BY vw.description,vw.txnStat")
	List<Object[]> findTxnAppUsageDetailsAfterDate(@Param("appId") String appId,@Param("stDate") Date stDate);

	@Query("select sum(tb.totalTxns), sum(tb.logins), sum(tb.distinctLogins), accessDate from VwLoginReport tb where tb.appId =:appId")
	List<Object[]> findLoginReportDetails(@Param("appId") String appId);
	
	@Query("select sum(tb.totalTxns), sum(tb.logins), sum(tb.distinctLogins), YEAR(tb.accessDate),MONTH(tb.accessDate),DAY(tb.accessDate) from VwLoginReport tb where tb.appId LIKE :appId AND tb.accessDate >=:stDate AND tb.accessDate<=:enDate GROUP BY YEAR(tb.accessDate),MONTH(tb.accessDate),DAY(tb.accessDate)")
	List <Object[]> findLoginReportDetailsBetweenDate(@Param("stDate")Timestamp stDate,@Param("enDate") Timestamp enDate, @Param("appId") String appId);
	
	@Query("select sum(tb.totalTxns), sum(tb.logins), sum(tb.distinctLogins), YEAR(tb.accessDate),MONTH(tb.accessDate),DAY(tb.accessDate)  from VwLoginReport tb where tb.appId LIKE :appId and tb.accessDate<=:enDate GROUP BY YEAR(tb.accessDate),MONTH(tb.accessDate),DAY(tb.accessDate)")
	List<Object[]> findLoginReportDetailsBeforeDate(@Param("enDate") Timestamp enDate, @Param("appId") String appId);
	
	@Query("select sum(tb.totalTxns), sum(tb.logins), sum(tb.distinctLogins), YEAR(tb.accessDate),MONTH(tb.accessDate),DAY(tb.accessDate)  from VwLoginReport tb where tb.appId LIKE :appId and tb.accessDate>=:stDate GROUP BY YEAR(tb.accessDate),MONTH(tb.accessDate),DAY(tb.accessDate)")
	List<Object[]> findLoginReportDetailsAfterDate(@Param("stDate") Timestamp stDate, @Param("appId") String appId);

	@Query("select userId, MIN(tm), MAX(tm), AVG(tm), SUM(totalTxns), SUM(distinctTxns), SUM(logins), SUM(distinctLogins) from VwCustomerReport vtb where vtb.appId LIKE :appId AND vtb.userId LIKE :userId GROUP BY userId")
	List<Object[]> findCustomerOverview(@Param("appId")String appId,  @Param("userId")String userId);
	
	@Query("select userId, MAX(countLon), longitude, MAX(countLat), latitude, formattedAddress from VwLatLonDetail vtb where vtb.appId LIKE :appId AND vtb.userId LIKE :userId GROUP BY userId, longitude, latitude, formattedAddress")
	List<Object[]> findCustomerLocationDetails(@Param("appId")String appId,  @Param("userId")String userId);

	@Query("select tb.sessionId, tb.origination, tb.latitude, tb.longitude, tb.sublocality, tb.adminAreaLvl1, tb.adminAreaLvl2, tb.country,tb.formattedAddress from TbAslgTxnDetail tb where tb.appId LIKE :appId AND tb.userId LIKE :userId GROUP BY tb.sessionId, tb.origination, tb.latitude, tb.longitude, tb.sublocality, tb.adminAreaLvl1, tb.adminAreaLvl2, tb.country,tb.formattedAddress")
	List<Object[]> findCustomerAllLocDetails(@Param("appId")String appId, @Param("userId")String userId);

    @Query("select vw.accessDate,min(vw.stTm),max(vw.endTm),sum(vw.totalTxns),sum(vw.distinctTxns),vw.latitude,vw.longitude,vw.formattedAddress  FROM VwCustomerDetail vw WHERE vw.appId LIKE :appId AND vw.userId LIKE :userId AND vw.accessDate >=:stDate AND vw.accessDate<=:enDate Group BY vw.accessDate,vw.userId,vw.sessionId,vw.latitude,vw.longitude,vw.formattedAddress")
	List<Object[]> findCustomerDetailReport(@Param("appId") String appId,@Param("userId") String userId, @Param("stDate") Timestamp stDate, @Param("enDate") Timestamp enDate);

	@Query("select vw.accessDate,min(vw.stTm),max(vw.endTm),sum(vw.totalTxns),sum(vw.distinctTxns),vw.latitude,vw.longitude,vw.formattedAddress  FROM VwCustomerDetail vw WHERE vw.appId LIKE :appId AND vw.userId LIKE :userId  AND vw.accessDate<=:enDate Group BY vw.accessDate,vw.userId,vw.sessionId,vw.latitude,vw.longitude,vw.formattedAddress")
	List<Object[]> findCustomerDetailReportBeforeDate(@Param("appId") String appId,@Param("userId") String userId, @Param("enDate") Timestamp enDate);

	@Query("select vw.accessDate,min(vw.stTm),max(vw.endTm),sum(vw.totalTxns),sum(vw.distinctTxns),vw.latitude,vw.longitude,vw.formattedAddress  FROM VwCustomerDetail vw WHERE vw.appId LIKE :appId AND vw.userId LIKE :userId AND vw.accessDate >=:stDate Group BY vw.accessDate,vw.userId,vw.sessionId,vw.latitude,vw.longitude,vw.formattedAddress")
    List<Object[]> findCustomerDetailReportAfterDate(@Param("appId") String appId,@Param("userId") String userId, @Param("stDate") Timestamp stDate);

}