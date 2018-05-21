/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.domain.repository.admin;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAslgJmsReqResp;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAslgJMSReqRespRepository extends CrudRepository<TbAslgJmsReqResp,Integer>{
    
	TbAslgJmsReqResp findBymsgId(String pMsgId);
    
}
