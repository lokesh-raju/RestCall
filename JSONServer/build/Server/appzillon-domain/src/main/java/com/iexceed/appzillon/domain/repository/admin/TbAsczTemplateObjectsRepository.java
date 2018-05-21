package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAsczTemplateObjects;
import com.iexceed.appzillon.domain.entity.TbAsczTemplateObjectsPK;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 8:10 PM
 */
@Repository
public interface TbAsczTemplateObjectsRepository extends JpaRepository<TbAsczTemplateObjects, TbAsczTemplateObjectsPK> {

	@Query("select tb.id.parentId,tb.id.chiledId from TbAsczTemplateObjects tb WHERE tb.id.appId=:appId AND tb.id.screenId=:screenId AND tb.id.layoutId=:layoutId AND tb.id.templateId LIKE :templateId ORDER BY tb.childSeq")
	List<Object[]> findTemplateObjectOrderByParentIdAndChildSeq(@Param("appId") String appId, @Param("screenId") String screenId, @Param("layoutId") String layoutId, @Param("templateId") String templateId);

	@Modifying
	@Query("delete from TbAsczTemplateObjects tb WHERE tb.id.appId=:appId AND tb.id.screenId=:screenId AND tb.id.layoutId=:layoutId AND tb.id.templateId =:templateId ")
    int deleteDesignReceiverByAppIdScreenIdLayoutIdTemplateId(@Param("appId") String appId,@Param("screenId") String screenId,@Param("layoutId") String layoutId,@Param("templateId") String templateId);

}
