/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiLovQueries;
import com.iexceed.appzillon.domain.entity.TbAsmiLovQueriesPK;

/**
 *
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiLovQueriesRepository extends JpaRepository<TbAsmiLovQueries, TbAsmiLovQueriesPK>{

    @Query("select tb from TbAsmiLovQueries as tb where tb.id.appId=:pappid and tb.id.queryId=:pqueryId")
	TbAsmiLovQueries fetchQueryString(
			@Param("pappid") String pAppId,
			@Param("pqueryId") String pQueryId);
}
