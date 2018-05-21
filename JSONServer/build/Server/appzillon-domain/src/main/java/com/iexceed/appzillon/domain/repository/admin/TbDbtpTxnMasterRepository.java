package com.iexceed.appzillon.domain.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.iexceed.appzillon.domain.entity.TbDbtpTxnMaster;

public interface TbDbtpTxnMasterRepository extends JpaRepository<TbDbtpTxnMaster, String>, JpaSpecificationExecutor<TbDbtpTxnMaster>{

}
