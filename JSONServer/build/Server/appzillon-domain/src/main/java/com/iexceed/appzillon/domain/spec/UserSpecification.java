package com.iexceed.appzillon.domain.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.iexceed.appzillon.domain.entity.TbAsmiUser;

public class UserSpecification {
	private UserSpecification(){
		
	}
	public static Specification<TbAsmiUser>  likeAppId( final String appId) {
		return new Specification<TbAsmiUser>() {
		
		
		public Predicate toPredicate(Root<TbAsmiUser> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {
			Path<?> d = (root).get("id");
			return builder.like(d.<String>get("appId"),appId);
		}
		};
		}
		public static Specification<TbAsmiUser>  likeUserId( final String userId) {
			return new Specification<TbAsmiUser>() {
			
			
			public Predicate toPredicate(Root<TbAsmiUser> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				Path<?> d = (root).get("id");
				return builder.like(d.<String>get("userId"),userId);
			}
			};
			}
		
	// added on 30-6-2014
	public static Specification<TbAsmiUser> likeUserName(
			final String userName) {
		return new Specification<TbAsmiUser>() {

			public Predicate toPredicate(Root<TbAsmiUser> root,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder.like(root.<String> get("userName"), userName);
			}
		};
	}

}
