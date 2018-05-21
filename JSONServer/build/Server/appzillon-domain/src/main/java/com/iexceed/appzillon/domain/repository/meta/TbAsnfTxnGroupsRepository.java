package com.iexceed.appzillon.domain.repository.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsnfTxnGroup;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnGroupPK;

@Repository
public interface TbAsnfTxnGroupsRepository extends
		JpaRepository<TbAsnfTxnGroup, TbAsnfTxnGroupPK> {

}
