package com.iexceed.appzillon.domain.repository.meta;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAstpSeqGen;

@Repository
public interface TbAstpSeqGenRepository extends
		CrudRepository<TbAstpSeqGen, TbAstpSeqGen> {
	@Query("select tb from TbAstpSeqGen tb where tb.sequenceName =:psequenceName")
	TbAstpSeqGen getloggingsequencenumber(
			@Param("psequenceName") String psequenceName);

}
