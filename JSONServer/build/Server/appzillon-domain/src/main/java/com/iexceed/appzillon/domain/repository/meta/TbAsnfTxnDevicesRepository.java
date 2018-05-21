package com.iexceed.appzillon.domain.repository.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAsnfTxnDevice;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnDevicePK;

@Repository
public interface TbAsnfTxnDevicesRepository extends
		JpaRepository<TbAsnfTxnDevice, TbAsnfTxnDevicePK> {

}
