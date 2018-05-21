package com.iexceed.appzillon.domain.repository.admin;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScr;
import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScrPK;


@Repository
public interface TbAsmiCnvUIScrRepository extends JpaRepository<TbAsmiCnvUIScr, TbAsmiCnvUIScrPK>, JpaSpecificationExecutor<TbAsmiCnvUIScr> {
}
