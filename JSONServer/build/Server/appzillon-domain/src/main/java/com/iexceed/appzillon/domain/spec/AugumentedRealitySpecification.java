package com.iexceed.appzillon.domain.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.iexceed.appzillon.domain.entity.TbAstpARMaster;

public class AugumentedRealitySpecification {

	private AugumentedRealitySpecification() {
	}

	public static Specification<TbAstpARMaster> likeAppId(final String appId) {
		return new Specification<TbAstpARMaster>() {

			public Predicate toPredicate(Root<TbAstpARMaster> tbAstpARMaster,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder
						.like(tbAstpARMaster.<String> get("appId"), appId);
			}
		};
	}
	
	public static Specification<TbAstpARMaster> likeRegionCode(final String regionCode) {
		return new Specification<TbAstpARMaster>() {

			public Predicate toPredicate(Root<TbAstpARMaster> tbAstpARMaster,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder
						.like(tbAstpARMaster.<String> get("regionCode"), regionCode);
			}
		};
	}

	public static Specification<TbAstpARMaster> likeCategory(final String category) {
		return new Specification<TbAstpARMaster>() {

			public Predicate toPredicate(Root<TbAstpARMaster> tbAstpARMaster,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder
						.like(tbAstpARMaster.<String> get("category"), category);
			}
		};
	}
	
	public static Specification<TbAstpARMaster> likeLatitude(final String latitude) {
		return new Specification<TbAstpARMaster>() {

			public Predicate toPredicate(Root<TbAstpARMaster> tbAstpARMaster,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder
						.like(tbAstpARMaster.<String> get("latitude"), latitude);
			}
		};
	}
	public static Specification<TbAstpARMaster> likeLongitude(final String longitude) {
		return new Specification<TbAstpARMaster>() {

			public Predicate toPredicate(Root<TbAstpARMaster> tbAstpARMaster,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder
						.like(tbAstpARMaster.<String> get("longitude"), longitude);
			}
		};
	}
	
	public static Specification<TbAstpARMaster> likeTitle(final String title) {
		return new Specification<TbAstpARMaster>() {

			public Predicate toPredicate(Root<TbAstpARMaster> tbAstpARMaster,
					CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder
						.like(tbAstpARMaster.<String> get("title"), title);
			}
		};
		
	}
	public static Specification<TbAstpARMaster> likeAdditionalInfo(final String additionalInfo) {
			return new Specification<TbAstpARMaster>() {

				public Predicate toPredicate(Root<TbAstpARMaster> tbAstpARMaster,
						CriteriaQuery<?> query, CriteriaBuilder builder) {
					return builder
							.like(tbAstpARMaster.<String> get("additionalInfo"), additionalInfo);
				}
			};
	}
	
}
