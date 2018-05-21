package com.iexceed.appzillon.domain.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.iexceed.appzillon.domain.entity.TbAsnfDevicesMaster;
import com.iexceed.appzillon.utils.ServerConstants;

public class DeviceMasterSpecification {

	public static Specification<TbAsnfDevicesMaster> likeDeviceName(
			final String deviceName) {
		return new Specification<TbAsnfDevicesMaster>() {

			public Predicate toPredicate(Root<TbAsnfDevicesMaster> root,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder
						.like(root.<String> get(ServerConstants.NOTIFICATION_DEVICE_NAME), deviceName);
			}
		};
	}

	public static Specification<TbAsnfDevicesMaster> likeOsId(final String osId) {
		return new Specification<TbAsnfDevicesMaster>() {

			public Predicate toPredicate(Root<TbAsnfDevicesMaster> root,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder.like(root.<String> get(ServerConstants.NOTIFICATION_OS_ID), osId);
			}
		};
	}

	public static Specification<TbAsnfDevicesMaster> deviceNameisNull() {
		return new Specification<TbAsnfDevicesMaster>() {

			public Predicate toPredicate(Root<TbAsnfDevicesMaster> root,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder.isNull(root.<String> get(ServerConstants.NOTIFICATION_DEVICE_NAME));
			}
		};
	}
	public static Specification<TbAsnfDevicesMaster> statusIsActive() {
		return new Specification<TbAsnfDevicesMaster>() {

			public Predicate toPredicate(Root<TbAsnfDevicesMaster> root,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder.equal(root.<String> get(ServerConstants.MESSAGE_HEADER_STATUS),ServerConstants.YES);
			}
		};
	}
}
