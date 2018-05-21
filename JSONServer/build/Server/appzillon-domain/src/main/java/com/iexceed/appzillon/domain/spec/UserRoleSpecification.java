package com.iexceed.appzillon.domain.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.iexceed.appzillon.domain.entity.TbAsmiUserRole;
import com.iexceed.appzillon.utils.ServerConstants;

public class UserRoleSpecification{
	private UserRoleSpecification(){
		
	}
		public static Specification<TbAsmiUserRole>  likeAppId( final String appId) {
		return new Specification<TbAsmiUserRole>() {
		
		
		public Predicate toPredicate(Root<TbAsmiUserRole> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {
			Path<?> d = (root).get("id");
			return builder.like(d.<String>get(ServerConstants.MESSAGE_HEADER_APP_ID),appId);
		}
		};
		}
		public static Specification<TbAsmiUserRole>  likeUserId( final String userId) {
			return new Specification<TbAsmiUserRole>() {
			
			
			public Predicate toPredicate(Root<TbAsmiUserRole> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				Path<?> d = (root).get("id");
				return builder.like(d.<String>get(ServerConstants.MESSAGE_HEADER_USER_ID),userId);
			}
			};
			}
		
		public static Specification<TbAsmiUserRole>  likeRoleId( final String roleId) {
			return new Specification<TbAsmiUserRole>() {
			
			
			public Predicate toPredicate(Root<TbAsmiUserRole> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				Path<?> d = (root).get("id");
				return builder.like(d.<String>get(ServerConstants.MESSAGE_HEADER_USER_ID),roleId);
			}
			};
			}
		
}
