package com.iexceed.appzillon.domain.repository.meta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillon.domain.entity.TbAstpWorkflowDet;
import com.iexceed.appzillon.domain.entity.TbAstpWorkflowDetPK;

public interface TbAstpWorkFlowDetRepository extends
JpaRepository<TbAstpWorkflowDet, TbAstpWorkflowDetPK>,
		JpaSpecificationExecutor<TbAstpWorkflowDet> {

	@Query("select c from TbAstpWorkflowDet as c where c.id.transactionRefNo=:transactionRefNo AND c.id.appId=:appId AND c.interfaceId=:interfaceId ORDER BY c.versionNo DESC")
	List<TbAstpWorkflowDet> findByCreateParam(
			@Param("transactionRefNo") String transactionRefNo,
			 @Param("appId") String appid,
			@Param("interfaceId") String interfaceId);

	
	
	@Query("select c from TbAstpWorkflowDet as c where c.id.appId=:appId AND c.userId=:userId AND c.status=:status")
	List<TbAstpWorkflowDet> findByAppIdUserId(@Param("appId") String appid, @Param("userId") String userId, @Param("status") String lStatus);
	
	/*@Query("select c from TbAstpWorkflowDet as c  where (c.id.transactionRefNo,c.id.versionNo) IN (Select tb.id.transactionRefNo,max(tb.id.versionNo) from TbAstpWorkflowDet as tb GROUP BY tb.id.transactionRefNo )AND c.id.appId=:appId AND c.userId=:userId ")
	List<TbAstpWorkflowDet> findmaxVersionRecordsByAppIdUserId(@Param("appId") String appid,
			@Param("userId") String userId);

	@Query("select c from TbAstpWorkflowDet as c where c.id.workflowId=:workflowId AND c.nextStage=:nextStage AND c.id.screenId=:screenId AND c.id.appId=:appId AND c.id.interfaceId=:interfaceId AND c.userId=:userId AND c.id.transactionRefNo=:transactionRefNo AND c.stageSeqNo=:stageSeqNo")
	List<TbAstpWorkflowDet> findByTransRefOtherCol(
			@Param("workflowId") String workflowId,
			@Param("nextStage") String nextStage,
			@Param("screenId") String screenId, @Param("appId") String appid,
			@Param("interfaceId") String interfaceId,
			@Param("transactionRefNo") String transactionRefNo,
			@Param("stageSeqNo") String stageSeqNo,
			@Param("userId") String userId);

	@Query("select c from TbAstpWorkflowDet as c where c.id.workflowId=:workflowId AND c.id.currentStage=:currentStage AND c.id.screenId=:screenId AND c.id.appId=:appId AND c.id.interfaceId=:interfaceId AND c.userId=:userId AND c.stageSeqNo=:stageSeqNo ")
	List<TbAstpWorkflowDet> findByStageSeqNoOthercols(
			@Param("workflowId") String workflowId,
			@Param("currentStage") String currentStage,
			@Param("screenId") String screenId, @Param("appId") String appid,
			@Param("interfaceId") String interfaceId,
			@Param("stageSeqNo") String stageSeqNo,
			@Param("userId") String userId);
	
	@Query("select c from TbAstpWorkflowDet as c where c.id.workflowId=:workflowId AND c.id.currentStage=:currentStage AND c.id.screenId=:screenId AND c.id.appId=:appId AND c.id.interfaceId=:interfaceId AND c.id.versionNo=:versionNo")
	List<TbAstpWorkflowDet> findByCreateParamVer(
			@Param("workflowId") String workflowId,
			@Param("currentStage") String currentStage,
			@Param("screenId") String screenId, @Param("appId") String appid,
			@Param("interfaceId") String interfaceId,
			@Param("versionNo") String versionNo);*/

	/*@Query("select c from TbAstpWorkflowDet as c where c.id.appId=:appId")
	List<TbAstpWorkflowDet> findByAppId(@Param("appId") String appid);
	
	@Query("select c from TbAstpWorkflowDet as c where c.id.interfaceId=:interfaceId")
	List<TbAstpWorkflowDet> findByInterfaceId(@Param("interfaceId") String pInterfaceId);
	
	@Query("select c from TbAstpWorkflowDet as c where c.id.transactionRefNo=:transactionRefNo")
	List<TbAstpWorkflowDet> findByTransactionRefNo(@Param("transactionRefNo") String pTransactionRefNo);
	
	@Query("select c from TbAstpWorkflowDet as c where c.id.appId=:appId AND c.id.interfaceId =:InterfaceId")
	List<TbAstpWorkflowDet> findByAppIdAndInterfaceId(@Param("appId") String appid, @Param("InterfaceId") String pInterfaceId);
	
	@Query("select c from TbAstpWorkflowDet as c where c.id.appId=:appId AND c.id.transactionRefNo =:transactionRefNo")
	List<TbAstpWorkflowDet> findByAppIdAndTxnRefNum(@Param("appId") String appid, @Param("transactionRefNo") String pTransactionRefNo);
	
	@Query("select c from TbAstpWorkflowDet as c where c.id.interfaceId =:interfaceId AND c.id.transactionRefNo =:transactionRefNo")
	List<TbAstpWorkflowDet> findByInterfaceIdAndTxnRefNum(@Param("interfaceId") String pInterfaceId, @Param("transactionRefNo") String pTransactionRefNo);*/

/*
	@Query("select c from TbAstpWorkflowDet as c where c.userId =:userId AND c.id.appId =:appId")
	List<TbAstpWorkflowDet> findByAppIdAndUserId(@Param("appId") String lAppId, @Param("userId") String lUserId);

	@Query("select c from TbAstpWorkflowDet as c where c.id.appId =:appId AND c.id.workflowId =:workflowId")
	List<TbAstpWorkflowDet> findByAppIdAndWorkFlowId(@Param("appId") String lAppId, @Param("workflowId") String lWorkFlowId);

	@Query("select c from TbAstpWorkflowDet as c where c.userId =:userId AND c.id.workflowId =:workflowId")
	List<TbAstpWorkflowDet> findByUserIdAndWorkFlowId(@Param("userId")String lUserId, @Param("workflowId") String lWorkFlowId);

	@Query("select c from TbAstpWorkflowDet as c where c.userId =:userId")
	List<TbAstpWorkflowDet> findByUserId(@Param("userId") String lUserId);

	@Query("select c from TbAstpWorkflowDet as c where c.id.workflowId =:workflowId")
	List<TbAstpWorkflowDet> findByWorkflowId(@Param("workflowId") String lWorkFlowId);*/
}
