package com.iexceed.appzillon.domain.repository.meta;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAslgTxnDetail;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnLog;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnLogPK;

@Repository
public interface TbAsnfTxnLogRepository extends	JpaRepository<TbAsnfTxnLog, TbAsnfTxnLogPK>,JpaSpecificationExecutor<TbAsnfTxnLog>{

	@Query("select vw.osdates,vw.count,vw.datesos from VwMessageNotifyStat vw where vw.osdates BETWEEN :stDate AND :enDate AND vw.appId=:appId order by vw.osdates")
	List<Object[]> findNotificationsCount(@Param("appId") String appId,@Param("stDate")Timestamp stDate,@Param("enDate") Timestamp enDate);

}
