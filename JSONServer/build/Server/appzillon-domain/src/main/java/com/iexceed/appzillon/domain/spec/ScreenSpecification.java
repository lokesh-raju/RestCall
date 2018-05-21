package com.iexceed.appzillon.domain.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.iexceed.appzillon.domain.entity.TbAsmiScrMaster;

public class ScreenSpecification {
	private ScreenSpecification(){
		
	}
	public static Specification<TbAsmiScrMaster>  likeScreenId( final String screenId) {
		return new Specification<TbAsmiScrMaster>() {
		
		
		public Predicate toPredicate(Root<TbAsmiScrMaster> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {
			Path<?> d = (root).get("id");
			return builder.like(d.<String>get("screenId"),screenId);
		}
		};
		}
	public static Specification<TbAsmiScrMaster>  likeDesc( final String description) {
		return new Specification<TbAsmiScrMaster>() {
		
	
		public Predicate toPredicate(Root<TbAsmiScrMaster> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {
			
			return builder.like(root.<String>get("screenDesc"),description);
		}
		};
		}
	public static Specification<TbAsmiScrMaster>  likeAppId( final String appId) {
		return new Specification<TbAsmiScrMaster>() {
		

		public Predicate toPredicate(Root<TbAsmiScrMaster> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {
			Path<?> d = (root).get("id");
			return builder.like(d.<String>get("appId"),appId);
		}
		};
		}
}
