package com.iexceed.appzillon.domain.repository.admin;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiDlgMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiDlgMasterPK;

@Repository
public interface TbAsmiDlgMasterRepository extends JpaRepository<TbAsmiDlgMaster, TbAsmiDlgMasterPK>, JpaSpecificationExecutor<TbAsmiDlgMaster> {
	@Query("select c from TbAsmiDlgMaster as c where c.id.appId=:appId AND c.id.cnvUIId=:cnvUIId AND c.dlgSeq=1")
	TbAsmiDlgMaster findFirstDlg(@Param("appId") String appId, @Param("cnvUIId") String cnvUIId);
	
	@Query("select c from TbAsmiDlgMaster as c where c.id.appId=:appId AND c.id.cnvUIId=:cnvUIId AND "
			+ "c.id.dlgId = (select c.nxtDlgId from TbAsmiDlgMaster c where c.id.appId=:appId AND c.id.cnvUIId=:cnvUIId AND c.id.dlgId=:dlgId)")
	TbAsmiDlgMaster findNextDlg(@Param("appId") String appId, @Param("cnvUIId") String cnvUIId, @Param("dlgId") String dlgId);
	
}
