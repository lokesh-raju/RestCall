package com.iexceed.appzillon.domain.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAslgCnvUITxnLog;

@Repository
public interface TbAslgCnvUITxnLogRepository extends JpaRepository<TbAslgCnvUITxnLog, String>, JpaSpecificationExecutor<TbAslgCnvUITxnLog>{
}
