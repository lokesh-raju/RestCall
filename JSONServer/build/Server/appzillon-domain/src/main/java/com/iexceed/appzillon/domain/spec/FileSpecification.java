package com.iexceed.appzillon.domain.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.iexceed.appzillon.domain.entity.TbAsfsFileDetails;


public class FileSpecification {
	public static Specification<TbAsfsFileDetails>  likeAppId( final String appId) {
		return new Specification<TbAsfsFileDetails>() {
		
		
		public Predicate toPredicate(Root<TbAsfsFileDetails> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {
			Path<?> d = (root).get("id");
			return builder.like(d.<String>get("appId"),appId);
		}
		};
		}
}
