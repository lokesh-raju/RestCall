/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.meta;

import com.iexceed.appzillon.domain.entity.TbAsmiOsDtls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiOsDtlsRepository extends JpaRepository<TbAsmiOsDtls, String>{
    
}
