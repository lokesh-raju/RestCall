package com.iexceed.appzillon.domain.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillon.domain.entity.TbAslgDeviceLocation;
import com.iexceed.appzillon.domain.entity.TbAslgDeviceLocationPK;

@Repository
public interface TbAslgDeviceLocationRepository extends JpaRepository<TbAslgDeviceLocation, TbAslgDeviceLocationPK>{

}
