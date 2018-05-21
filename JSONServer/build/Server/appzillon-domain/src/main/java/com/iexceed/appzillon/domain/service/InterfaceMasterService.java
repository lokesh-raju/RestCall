package com.iexceed.appzillon.domain.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiIntfMaster;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiIntfMasterRepository;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.IntfMasterDtls;
import com.iexceed.appzillon.utils.ServerConstants;

@Named("InterfaceMasterService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class InterfaceMasterService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
			InterfaceMasterService.class.toString());
	private static Map<String, Map<String, IntfMasterDtls>> interfaceMasterMap = new HashMap<String, Map<String, IntfMasterDtls>>();
	@Inject
	TbAsmiIntfMasterRepository cAsmiIntfMasterRepo;

	public static Map<String, Map<String, IntfMasterDtls>> getInterfaceMasterMap() {
		return interfaceMasterMap;
	}

	public static void setInterfaceMasterMap(Map<String, Map<String, IntfMasterDtls>> interfaceMasterMap) {
		InterfaceMasterService.interfaceMasterMap = interfaceMasterMap;
	}

	public void fetchInterfaceMap() {
		Map<String, IntfMasterDtls> interfaceMap = new HashMap<String, IntfMasterDtls>();
		List<TbAsmiIntfMaster> interfaceList = cAsmiIntfMasterRepo.findAll();
		for (TbAsmiIntfMaster interfaceDtls : interfaceList) {
			String appId = interfaceDtls.getTbAsmiIntfMasterPK().getAppId();
			String interfaceId = interfaceDtls.getTbAsmiIntfMasterPK().getInterfaceId();
			String category = interfaceDtls.getCategory();
			String type = interfaceDtls.getType();
			String description = interfaceDtls.getDescription();
			String createUserId = interfaceDtls.getCreateUserId();
			Date createTs = interfaceDtls.getCreateTs();
			int versionNo = interfaceDtls.getVersionNo();
			String captchaReq = interfaceDtls.getCaptchaReq();
			IntfMasterDtls intfMasterDtls = new IntfMasterDtls();
			if (interfaceMasterMap.containsKey(appId)) {
				interfaceMap = interfaceMasterMap.get(appId);
			} else {
				interfaceMap = new HashMap<String, IntfMasterDtls>();
			}
			intfMasterDtls.setAppId(appId);
			intfMasterDtls.setCategory(category);
			intfMasterDtls.setType(type);
			intfMasterDtls.setDescription(description);
			intfMasterDtls.setCreateUserId(createUserId);
			intfMasterDtls.setCreateTs(createTs);
			intfMasterDtls.setVersionNo(versionNo);
			intfMasterDtls.setCaptchaReq(captchaReq);
			intfMasterDtls.setDgTxnLogRequired(interfaceDtls.getDgTxnLogReq());
			interfaceMap.put(interfaceId, intfMasterDtls);
			interfaceMasterMap.put(appId, interfaceMap);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Populated Interface Map" + interfaceMasterMap);
	}
}
