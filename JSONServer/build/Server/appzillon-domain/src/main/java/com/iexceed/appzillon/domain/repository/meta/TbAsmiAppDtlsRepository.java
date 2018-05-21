/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.meta;

import com.iexceed.appzillon.domain.entity.TbAsmiAppDtls;
import com.iexceed.appzillon.domain.entity.TbAsmiAppDtlsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiAppDtlsRepository extends JpaRepository<TbAsmiAppDtls, TbAsmiAppDtlsPK>{
    
}
