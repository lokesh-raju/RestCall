package com.iexceed.appzillon.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.exception.Utils;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiIntfMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiIntfMasterPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiIntfMasterRepository;
import com.iexceed.appzillon.domain.spec.InterfaceMasterSpecification;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.maputils.MapUtils;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * 
 * @author Vinod Rawat
 */
@Named(ServerConstants.SERVICE_INTERFACE_MAINTENANCE)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class InterfaceMaintenance {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, InterfaceMaintenance.class.toString());
	@Inject
	TbAsmiIntfMasterRepository cintfMasterRepo;
	
	public void createInterfaceMaster(Message pMessage) {
		try {
			JSONObject lrequest = pMessage
					.getRequestObject()
					.getRequestJson()
					.getJSONObject(
							ServerConstants.APPZCREATEINFMASTERREQUEST);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Entered create interface details");
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " creating InterfaceId::"
					+ lrequest.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " creating App ID::"
					+ lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			String appId = lrequest.get(ServerConstants.MESSAGE_HEADER_APP_ID)
					.toString();
			String interfaceId = lrequest.get(
					ServerConstants.MESSAGE_HEADER_INTERFACE_ID).toString();
			String createUserId = pMessage.getHeader().getUserId();
			if (Utils.isNullOrEmpty (appId) && Utils.isNullOrEmpty (interfaceId)) {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("Column value can not be null or blank");
				dexp.setCode(DomainException.Code.APZ_DM_009.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Primary columns values not found", dexp);
				throw dexp;
			}
			TbAsmiIntfMasterPK lfrmiIntfMasterPK = new TbAsmiIntfMasterPK(
					lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
					lrequest.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " l_frmiIntfMasterPK : " + lfrmiIntfMasterPK);
			TbAsmiIntfMaster lfrmiIntfMaster = cintfMasterRepo
					.findOne(lfrmiIntfMasterPK);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " l_frmiIntfMaster:::" + lfrmiIntfMaster);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Leaving fetch interface details : ");
			if (lfrmiIntfMaster != null) {
				LOG.debug("interface details is already found in interface master.");
				DomainException ldomainException = DomainException.getDomainExceptionInstance();
				ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_024));
				ldomainException.setCode(DomainException.Code.APZ_DM_024.toString());
				ldomainException.setPriority("1");
				throw ldomainException;
			} else {
				lfrmiIntfMaster = new TbAsmiIntfMaster();
				lfrmiIntfMaster.setCategory(lrequest
						.getString(ServerConstants.CATEGORY));
				lfrmiIntfMaster.setCreateTs(new Date());
				lfrmiIntfMaster.setCreateUserId(createUserId);
				lfrmiIntfMaster.setDescription(lrequest
						.getString(ServerConstants.DESCRIPTION));
				lfrmiIntfMaster.setTbAsmiIntfMasterPK(lfrmiIntfMasterPK);
				lfrmiIntfMaster.setType(lrequest
						.getString(ServerConstants.TYPE));
				lfrmiIntfMaster.setVersionNo(1);
				cintfMasterRepo.save(lfrmiIntfMaster);

				JSONObject lintfResponseObj = new JSONObject();
				JSONObject statusobj = new JSONObject();
				statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS,
						ServerConstants.SUCCESS);
				lintfResponseObj.put(
						ServerConstants.APPZCREATEINFMASTERRESPONSE,
						statusobj);

				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Built success response.");
				pMessage.getResponseObject().setResponseJson(lintfResponseObj);

			}
		} catch (JSONException ex) {
			DomainException ldomainException = DomainException.getDomainExceptionInstance();
			ldomainException.setMessage("Failed creating a new Interface.");
			ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			ldomainException.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Failed creating a new Interface : "+ ex.getMessage(), ldomainException);
			throw ldomainException;
		}

	}

	public void fetchInterfaceMaster(Message pMessage) {
		try {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Entered fetch interface details");
			Header head = pMessage.getHeader();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " InterfaceId :: " + head.getInterfaceId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " App ID::" + head.getAppId());

			TbAsmiIntfMasterPK lfrmiIntfMasterPK = new TbAsmiIntfMasterPK(
					head.getAppId(), head.getInterfaceId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " l_frmiIntfMasterPK :: " + lfrmiIntfMasterPK);
			TbAsmiIntfMaster lfrmiIntfMaster = cintfMasterRepo
					.findOne(lfrmiIntfMasterPK);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " l_frmiIntfMaster:::" + lfrmiIntfMaster);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Leaving fetch interface details: ");
			if (lfrmiIntfMaster == null) {
				DomainException ldomainException = DomainException.getDomainExceptionInstance();
				ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_023));
				ldomainException.setCode(DomainException.Code.APZ_DM_023.toString());
				ldomainException.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Header's interface details are not found in interface master..", ldomainException);
				throw ldomainException;
			} else {
				JSONObject res = new JSONObject(MapUtils.convertObjectToMap(lfrmiIntfMaster));
				pMessage.getResponseObject().setResponseJson(res);
			}

		} catch (IllegalArgumentException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.ILLEGAL_ARGUMENT_EXCEPTION,ex);
			DomainException ldomainException = DomainException.getDomainExceptionInstance();
			ldomainException
					.setMessage(ldomainException
							.getDomainExceptionMessage(DomainException.Code.APZ_DM_006));
			ldomainException
					.setCode(DomainException.Code.APZ_DM_006.toString());
			ldomainException.setPriority("1");
			throw ldomainException;
		}

	}

	public void searchInterfaceMaster(Message pMessage) {
		JSONObject lrequest = null;
		JSONObject lresponse = null;

		try {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Searching interface details");
			lrequest = pMessage
					.getRequestObject()
					.getRequestJson()
					.getJSONObject(
							ServerConstants.APPZSEARCHINFMASTERREQUEST);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " **** searchInterfaceMaster  lrequest:" + lrequest);
			String appId = lrequest.get(ServerConstants.MESSAGE_HEADER_APP_ID)
					.toString();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " **** searchInterfaceMaster appId:" + appId);
			String interfaceId = lrequest.get(
					ServerConstants.MESSAGE_HEADER_INTERFACE_ID).toString();

			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " **** searchInterfaceMaster interfaceId:" + interfaceId);

			final String fappId;
			final String fintfId;
			if (Utils.isNullOrEmpty (appId)){
				fappId = ServerConstants.PERCENT;
			}else{
				fappId = appId;
			}

			if (Utils.isNullOrEmpty (interfaceId)){
				fintfId = ServerConstants.PERCENT;
			}else{
				fintfId = interfaceId;
			}

			List<TbAsmiIntfMaster> reslist = new ArrayList<TbAsmiIntfMaster>();
			reslist = cintfMasterRepo.findAll(Specifications.where(
					InterfaceMasterSpecification.likeAppId(fappId)).and(
					InterfaceMasterSpecification.likeInterfaceId(fintfId)));
			if (reslist.isEmpty()) {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				LOG.error("No record found ", dexp);
				throw dexp;
			}
			JSONArray jsonarray = new JSONArray();
			int i = 0;
			while (i < reslist.size()) {
				JSONObject obj = new JSONObject();
				obj.put(ServerConstants.MESSAGE_HEADER_APP_ID, reslist.get(i)
						.getTbAsmiIntfMasterPK().getAppId());
				obj.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, reslist.get(i)
						.getTbAsmiIntfMasterPK().getInterfaceId());
				obj.put(ServerConstants.DESCRIPTION, reslist.get(i)
						.getDescription());
				obj.put(ServerConstants.TYPE, reslist.get(i).getType());

				obj.put(ServerConstants.CATEGORY, reslist.get(i)
						.getCategory());

				jsonarray.put(obj);
				i++;
			}

			lresponse = new JSONObject();
			lresponse.put(ServerConstants.APPZSEARCHINFMASTERRESPONSE,
					jsonarray);
			pMessage.getResponseObject().setResponseJson(lresponse);
		} catch (JSONException ex) {
			DomainException ldomainException = DomainException.getDomainExceptionInstance();
			ldomainException
					.setMessage(ldomainException
							.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			ldomainException
					.setCode(DomainException.Code.APZ_DM_000.toString());
			ldomainException.setPriority("1");
			LOG.error("Exception while searchingInterface details...."
					+ ex.getMessage(), ldomainException);
			throw ldomainException;
		}
	}

	public void updateInterfaceMaster(Message pMessage) throws DomainException {
		try {
			JSONObject lrequest = pMessage
					.getRequestObject()
					.getRequestJson()
					.getJSONObject(
							ServerConstants.APPZUPDATEINFMASTERREQUEST);

			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Entered update interface details");
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " InterfaceId::"
					+ lrequest.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " App ID : "
					+ lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			String createUserId = pMessage.getHeader().getUserId();
			TbAsmiIntfMasterPK lfrmiIntfMasterPK = new TbAsmiIntfMasterPK(
					lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
					lrequest.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " l_frmiIntfMasterPK ::: " + lfrmiIntfMasterPK);
			TbAsmiIntfMaster lfrmiIntfMaster = cintfMasterRepo
					.findOne(lfrmiIntfMasterPK);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " l_frmiIntfMaster:::" + lfrmiIntfMaster);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Leaving fetch interface details");
			if (lfrmiIntfMaster != null) {
				lfrmiIntfMaster.setCategory(lrequest.getString(ServerConstants.CATEGORY));
				lfrmiIntfMaster.setType(lrequest.getString(ServerConstants.TYPE));
				lfrmiIntfMaster.setCreateUserId(createUserId);
				lfrmiIntfMaster.setDescription(lrequest.getString(ServerConstants.DESCRIPTION));
				lfrmiIntfMaster.setTbAsmiIntfMasterPK(lfrmiIntfMasterPK);
				lfrmiIntfMaster.setVersionNo(lfrmiIntfMaster.getVersionNo() + 1);
				cintfMasterRepo.save(lfrmiIntfMaster);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Updated master record.....");
			} else {
				DomainException ldomainException = DomainException.getDomainExceptionInstance();
				ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_025));
				ldomainException.setCode(DomainException.Code.APZ_DM_025.toString());
				ldomainException.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Interface details doesn't not exist..", ldomainException);
				throw ldomainException;
			}
			JSONObject lintfresponseobj = new JSONObject();
			JSONObject statusobj = new JSONObject();
			statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS,
					ServerConstants.SUCCESS);
			lintfresponseobj.put(
					ServerConstants.APPZUPDATEINFMASTERRESPONSE,
					statusobj);

			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Built success response");
			pMessage.getResponseObject().setResponseJson(lintfresponseobj);
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, ex);
			DomainException ldomainException = DomainException.getDomainExceptionInstance();
			ldomainException.setMessage("Failed creating a new Interface....");
			ldomainException
					.setCode(DomainException.Code.APZ_DM_000.toString());
			ldomainException.setPriority("1");
            
			throw ldomainException;
		}

	}

	public void deleteInterfaceMaster(Message pMessage) {
		try {
			JSONObject lreqJSON = null;
			Object lRequest = pMessage.getRequestObject().getRequestJson()
					.get("appzillonDeleteIntfMasterRequest");
			if (lRequest instanceof JSONArray) {
				int i = 0;
				while (i < ((JSONArray) lRequest).length()) {
					lreqJSON = ((JSONArray) lRequest).getJSONObject(i);
					deleteInterface(pMessage, lreqJSON);
					i++;
				}
			} else {
				deleteInterface(pMessage, (JSONObject) lRequest);
			}
			JSONObject status = new JSONObject();
			status.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			JSONObject lresponse = new JSONObject();
			lresponse.put(ServerConstants.APPZDELETEINFMASTERRESPONSE, status);
			pMessage.getResponseObject().setResponseJson(lresponse);

		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, ex);
			DomainException ldomainException = DomainException.getDomainExceptionInstance();
			ldomainException.setMessage("Failed deleting Interface..");
			ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			ldomainException.setPriority("1");
            
			throw ldomainException;
		}
	}

	public void deleteInterface(Message pMessage, JSONObject pjson) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " @deleteInterface : " + pjson);
		try {
			String appId = pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " appId   : " + appId);
			String interfaceId = pjson.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " interfaceId   : " + interfaceId);
			TbAsmiIntfMasterPK id = new TbAsmiIntfMasterPK(appId, interfaceId);

			TbAsmiIntfMaster res = cintfMasterRepo.findOne(id);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " res  TbAsmiIntfMaster : " + res);
			if (res != null) {
				cintfMasterRepo.delete(res);
			} else {
				DomainException ldomainException = DomainException.getDomainExceptionInstance();
				ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_026));
				ldomainException.setCode(DomainException.Code.APZ_DM_026.toString());
				ldomainException.setPriority("1");
	            
				throw ldomainException;
			}
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, ex);
			DomainException ldomainException = DomainException.getDomainExceptionInstance();
			ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			ldomainException.setPriority("1");
			throw ldomainException;
		}
	}
    public void getInterfaceDefinition(Message pMessage) {
        JSONObject lrequest ;
        JSONObject lresponse = new JSONObject();

        LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Get interface definition details");
        lrequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_GET_INTF_DEF_REQUEST);
        LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " **** getInterfacesDef  lrequest:" + lrequest);
        String appId = lrequest.get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();
        LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " **** getInterfacesDef appId:" + appId);
        JSONArray jsonArray = lrequest.getJSONArray(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
        LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " **** array of interfaceIds :" + jsonArray);
        List<String> interfaceIds = new ArrayList<String>();
        for(int i=0;i<jsonArray.length();i++){
            interfaceIds.add((String) jsonArray.get(i));
        }
        LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " **** List of interfaceIds :" + interfaceIds);
       List<Object []> interfaceDefs = cintfMasterRepo.findIntfDefbyAppIdAndInterfaceId(appId,interfaceIds) ;
       JSONArray array = new JSONArray();
        for (Object [] obj:interfaceDefs ) {
            JSONObject jsonObject = new JSONObject();
            String interfaceId = (String) obj[0];
            String interfaceDef = (String) obj[1];
            jsonObject.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID,interfaceId);
            jsonObject.put(ServerConstants.INTERFACE_DEFINITION, interfaceDef);
            array.put(jsonObject);
        }

        lresponse.put(ServerConstants.APPZILLON_ROOT_GET_INTF_DEF_RESPONSE,array);
        LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " **** Interface Definition Response :" + lresponse);
        pMessage.getResponseObject().setResponseJson(lresponse);

    }
}
