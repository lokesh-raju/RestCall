package com.iexceed.appzillon.domain.repository.admin;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIMasterPK;

@Repository
public interface TbAsmiCnvUIMasterRepository extends JpaRepository<TbAsmiCnvUIMaster, TbAsmiCnvUIMasterPK>, JpaSpecificationExecutor<TbAsmiCnvUIMaster> {
}
