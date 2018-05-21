package com.iexceed.appzillon.domain.repository.meta;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillon.domain.entity.history.TbAshsWorkflowDet;
import com.iexceed.appzillon.domain.entity.history.TbAshsWorkflowDetPK;

/**
 * @author Ripu
 *
 */

public interface TbAshsWorkFlowDetRepository extends
		CrudRepository<TbAshsWorkflowDet, TbAshsWorkflowDetPK>,
		JpaSpecificationExecutor<TbAshsWorkflowDet> {
	//@Query("select c from TbAshsWorkflowDet as c where (c.id.versionNo) IN (Select max(tb.id.versionNo) from TbAshsWorkflowDet as tb) AND c.id.appId=:appId AND c.id.transactionRefNo=:transactionRefNo AND c.id.workflowId=:workflowId ")
	@Query("select c from TbAshsWorkflowDet as c  where (c.id.transactionRefNo,c.id.versionNo) IN (Select tb.id.transactionRefNo,max(tb.id.versionNo) from TbAshsWorkflowDet as tb GROUP BY tb.id.transactionRefNo )AND c.id.appId=:appId AND c.id.transactionRefNo=:transactionRefNo AND c.id.workflowId=:workflowId ")
	TbAshsWorkflowDet findmaxVersionByAppIdWorkFlowIdTxnID(@Param("appId") String appid, @Param("transactionRefNo") String transactionRefNo, @Param("workflowId") String workflowId);
	/*

	@Query("select c from TbAshsWorkflowDet as c where c.id.transactionRefNo=:transactionRefNo AND c.id.appId=:appId AND c.id.interfaceId=:interfaceId ORDER BY c.id.versionNo DESC")
	List<TbAshsWorkflowDet> findByCreateParam(
			@Param("transactionRefNo") String transactionRefNo,
			 @Param("appId") String appid,
			@Param("interfaceId") String interfaceId);

	
	
	@Query("select c from TbAshsWorkflowDet as c where c.id.appId=:appId AND c.userId=:userId ")
	List<TbAshsWorkflowDet> findByAppIdUserId(@Param("appId") String appid,
			@Param("userId") String userId);
	
	@Query("select c from TbAshsWorkflowDet as c  where (c.id.transactionRefNo,c.id.versionNo) IN (Select tb.id.transactionRefNo,max(tb.id.versionNo) from TbAshsWorkflowDet as tb GROUP BY tb.id.transactionRefNo )AND c.id.appId=:appId AND c.userId=:userId ")
	List<TbAshsWorkflowDet> findmaxVersionRecordsByAppIdUserId(@Param("appId") String appid,
			@Param("userId") String userId);

	@Query("select c from TbAshsWorkflowDet as c where c.id.workflowId=:workflowId AND c.nextStage=:nextStage AND c.id.screenId=:screenId AND c.id.appId=:appId AND c.id.interfaceId=:interfaceId AND c.userId=:userId AND c.id.transactionRefNo=:transactionRefNo AND c.stageSeqNo=:stageSeqNo")
	List<TbAshsWorkflowDet> findByTransRefOtherCol(
			@Param("workflowId") String workflowId,
			@Param("nextStage") String nextStage,
			@Param("screenId") String screenId, @Param("appId") String appid,
			@Param("interfaceId") String interfaceId,
			@Param("transactionRefNo") String transactionRefNo,
			@Param("stageSeqNo") String stageSeqNo,
			@Param("userId") String userId);

	@Query("select c from TbAshsWorkflowDet as c where c.id.workflowId=:workflowId AND c.id.currentStage=:currentStage AND c.id.screenId=:screenId AND c.id.appId=:appId AND c.id.interfaceId=:interfaceId AND c.userId=:userId AND c.stageSeqNo=:stageSeqNo ")
	List<TbAshsWorkflowDet> findByStageSeqNoOthercols(
			@Param("workflowId") String workflowId,
			@Param("currentStage") String currentStage,
			@Param("screenId") String screenId, @Param("appId") String appid,
			@Param("interfaceId") String interfaceId,
			@Param("stageSeqNo") String stageSeqNo,
			@Param("userId") String userId);
	
	@Query("select c from TbAshsWorkflowDet as c where c.id.workflowId=:workflowId AND c.id.currentStage=:currentStage AND c.id.screenId=:screenId AND c.id.appId=:appId AND c.id.interfaceId=:interfaceId AND c.id.versionNo=:versionNo")
	List<TbAshsWorkflowDet> findByCreateParamVer(
			@Param("workflowId") String workflowId,
			@Param("currentStage") String currentStage,
			@Param("screenId") String screenId, @Param("appId") String appid,
			@Param("interfaceId") String interfaceId,
			@Param("versionNo") String versionNo);

*/}
