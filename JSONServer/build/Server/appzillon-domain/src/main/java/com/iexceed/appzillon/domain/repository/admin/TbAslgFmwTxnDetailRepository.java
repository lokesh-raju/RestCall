package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAslgFmwTxnDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface TbAslgFmwTxnDetailRepository extends JpaRepository<TbAslgFmwTxnDetail,String>,JpaSpecificationExecutor<TbAslgFmwTxnDetail>{

}
