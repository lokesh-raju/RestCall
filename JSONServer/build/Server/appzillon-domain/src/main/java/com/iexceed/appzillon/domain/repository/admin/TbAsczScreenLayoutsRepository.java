package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAsczScreenLayouts;
import com.iexceed.appzillon.domain.entity.TbAsczScreenLayoutsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 8:09 PM
 */
@Repository
public interface TbAsczScreenLayoutsRepository extends JpaRepository<TbAsczScreenLayouts, TbAsczScreenLayoutsPK> {
}
