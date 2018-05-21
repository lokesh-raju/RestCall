package com.iexceed.appzillon.domain.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsmiCaptchaDtls;

@Repository
public interface TbAsmiCaptchaDtlsRepository
		extends JpaRepository<TbAsmiCaptchaDtls, Integer>, JpaSpecificationExecutor<TbAsmiCaptchaDtls> {

	@Query("select tb from TbAsmiCaptchaDtls tb where tb.captchaStatus = 'P'  ")
	List<TbAsmiCaptchaDtls> findAsmiCaptchaDtlsByCaptchaStatus();
}
