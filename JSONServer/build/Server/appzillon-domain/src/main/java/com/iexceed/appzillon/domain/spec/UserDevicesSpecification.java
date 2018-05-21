package com.iexceed.appzillon.domain.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.iexceed.appzillon.domain.entity.TbAsmiUserDevices;

public class UserDevicesSpecification {
	private UserDevicesSpecification(){
		
	}
	public static Specification<TbAsmiUserDevices> likeUserId(
			final String userId) {
		return new Specification<TbAsmiUserDevices>() {

			public Predicate toPredicate(Root<TbAsmiUserDevices> root,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				Path<?> d = (root).get("id");
				return builder.like(d.<String> get("userId"), userId);
			}
		};
	}

	public static Specification<TbAsmiUserDevices> likeDeviceId(
			final String deviceId) {
		return new Specification<TbAsmiUserDevices>() {

			public Predicate toPredicate(Root<TbAsmiUserDevices> root,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				Path<?> d = (root).get("id");
				return builder.like(d.<String> get("deviceId"), deviceId);
			}
		};
	}

}
