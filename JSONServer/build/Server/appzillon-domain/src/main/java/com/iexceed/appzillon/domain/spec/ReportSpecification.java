package com.iexceed.appzillon.domain.spec;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.iexceed.appzillon.domain.entity.TbAslgTxnDetail;
import com.iexceed.appzillon.utils.ServerConstants;

public class ReportSpecification {
	private ReportSpecification(){
		
	}
	public static Specification<TbAslgTxnDetail>  likeAppId( final String appId) {
		return new Specification<TbAslgTxnDetail>() {
		
	
		public Predicate toPredicate(Root<TbAslgTxnDetail> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {			
			return builder.like(root.<String>get(ServerConstants.MESSAGE_HEADER_APP_ID),appId);
		}
		};
		}
	
	public static Specification<TbAslgTxnDetail>  likeInterfaceId( final String interfaceId) {
		return new Specification<TbAslgTxnDetail>() {
		

		public Predicate toPredicate(Root<TbAslgTxnDetail> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {			
			return builder.like(root.<String>get(ServerConstants.MESSAGE_HEADER_INTERFACE_ID),interfaceId);
		}
		};
		}
	
	public static Specification<TbAslgTxnDetail>  likeDeviceId( final String deviceId) {
		return new Specification<TbAslgTxnDetail>() {
		
		
		public Predicate toPredicate(Root<TbAslgTxnDetail> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {			
			return builder.like(root.<String>get(ServerConstants.MESSAGE_HEADER_DEVICE_ID),deviceId);
		}
		};
		}
	
	public static Specification<TbAslgTxnDetail>  likeUserId( final String userId) {
		return new Specification<TbAslgTxnDetail>() {
		
		
		public Predicate toPredicate(Root<TbAslgTxnDetail> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {
			return builder.like(root.<String>get(ServerConstants.MESSAGE_HEADER_USER_ID),userId);
		}
		};
		}
	public static Specification<TbAslgTxnDetail>  afterDate( final Date date) {
		return new Specification<TbAslgTxnDetail>() {
		
		
		public Predicate toPredicate(Root<TbAslgTxnDetail> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {
			
			return builder.greaterThan(root.<Date>get(ServerConstants.CREATETS),date);
		}
		};
		}
	
	//below method added by ripu on 07-Dec-2015
	public static Specification<TbAslgTxnDetail>  beforeDate( final Date pDate) {
		return new Specification<TbAslgTxnDetail>() {
			public Predicate toPredicate(Root<TbAslgTxnDetail> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder.lessThanOrEqualTo(root.<Date>get(ServerConstants.CREATETS),pDate);
			}
		};
	}
	
	public static Specification<TbAslgTxnDetail>  likeTransactionStatus( final String pTxnStatus) {
		return new Specification<TbAslgTxnDetail>() {
		public Predicate toPredicate(Root<TbAslgTxnDetail> root, CriteriaQuery<?> query,
				CriteriaBuilder builder) {
			return builder.like(root.<String>get("txnStat"),pTxnStatus);
		}
		};
		}
}
