package com.iexceed.appzillon.domain.repository.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsnfTxnMaster;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnMasterPK;

@Repository
public interface TbAsnfTxnMasterRepository extends
		JpaRepository<TbAsnfTxnMaster, TbAsnfTxnMasterPK> {

}
