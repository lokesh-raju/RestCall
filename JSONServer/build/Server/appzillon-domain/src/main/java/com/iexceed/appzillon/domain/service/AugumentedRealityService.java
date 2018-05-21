package com.iexceed.appzillon.domain.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.exception.Utils;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAstpARMaster;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.meta.TbAstpARMasterRepository;
import com.iexceed.appzillon.domain.spec.AugumentedRealitySpecification;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * 
 * @author arthanarisamy
 *
 */
@Named(ServerConstants.SERVICE_AUGUMENTED_REALITY)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class AugumentedRealityService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getDomainLogger(ServerConstants.LOGGER_DOMAIN,
					AugumentedRealityService.class.toString());
	@Inject
	TbAstpARMasterRepository ctbASTPARMasterRepo;
	
	/**
	 * 
	 * @param pMessage
	 */
	public void getAugementDetails(Message pMessage){
		List<TbAstpARMaster> tbAstpARMasterList = null;
		JSONObject lARReqJSON = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request JSON to fetch Augumented Request -:" + lARReqJSON);
		JSONObject lARDetails = lARReqJSON.getJSONObject("fetchARDetails");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request JSON to fetch Augumented Details -:" + lARDetails);
		String appId = "%";
		String regionCode = "%";
		String category = "%";
		String latitude = "%";
		String longitude = "%";
		String title = "%";
		String additionalInfo = "%";
		if(lARDetails.has("appId") && Utils.isNotNullOrEmpty(lARDetails.getString("appId"))){
			appId = lARDetails.getString("appId");
		}

		if(lARDetails.has("regionCode") && Utils.isNotNullOrEmpty(lARDetails.getString("regionCode"))){
			regionCode = lARDetails.getString("regionCode");
			
		}
		
		if(lARDetails.has("category") && Utils.isNotNullOrEmpty(lARDetails.getString("category"))){
			category = lARDetails.getString("category");
			
		}
		if(lARDetails.has("radius") && Utils.isNotNullOrEmpty(lARDetails.getString("radius"))){

			if(lARDetails.has("latitude") && Utils.isNotNullOrEmpty(lARDetails.getString("latitude"))){
				latitude = lARDetails.getString("latitude");
				
			}
			
			if(lARDetails.has("longitude") && Utils.isNotNullOrEmpty(lARDetails.getString("longitude"))){
				longitude = lARDetails.getString("longitude");
				
			}
		}
		
		
		if(lARDetails.has("title") && Utils.isNotNullOrEmpty(lARDetails.getString("title"))){
			title = lARDetails.getString("title");
			
		}
		
		if(lARDetails.has("additionalInfo") && Utils.isNotNullOrEmpty(lARDetails.getString("additionalInfo"))){
			additionalInfo = lARDetails.getString("additionalInfo");
			
		}

		
		tbAstpARMasterList = ctbASTPARMasterRepo.findAll(Specifications.where(AugumentedRealitySpecification.likeAppId(appId))
				.and(AugumentedRealitySpecification.likeRegionCode(regionCode)).and(AugumentedRealitySpecification.likeCategory(category))
				.and(AugumentedRealitySpecification.likeLatitude(latitude)).and(AugumentedRealitySpecification.likeLongitude(longitude))
				.and(AugumentedRealitySpecification.likeTitle(title)).and(AugumentedRealitySpecification.likeAdditionalInfo(additionalInfo)));
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No of records fetched -:" + tbAstpARMasterList.size());
		if(lARDetails.has("radius")){
			tbAstpARMasterList = calculateRadiusnBuildResponse(tbAstpARMasterList, pMessage);
		}		
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No of records fetched After calculating radius-:" + tbAstpARMasterList.size());
		buildResponse(tbAstpARMasterList, pMessage);
		
		
		/*if(lARDetails.has("appId") && !"".equalsIgnoreCase(lARDetails.getString("appId"))
				&& lARDetails.has("regionCode") && !"".equalsIgnoreCase(lARDetails.getString("regionCode"))
				&& lARDetails.has("category") && !"".equalsIgnoreCase(lARDetails.getString("category"))
				&& lARDetails.has("radius") && !"".equalsIgnoreCase(lARDetails.getString("radius"))){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetchign using Query with AppId and RegionCode for Radius....");
			tbAstpARMasterList = ctbASTPARMasterRepo.findByAppIdRegionCodeCategory(lARDetails.getString("appId"), 
					lARDetails.getString("regionCode"), lARDetails.getString("category"));
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No of records fetched -:" + tbAstpARMasterList.size());
			calculateRadiusnBuildResponse(tbAstpARMasterList, pMessage);
			LOG.debug(ServerConstants.LOGGER_PREprivate String additionalInfo;
	@Column(name = "DESCRIPTION")
	private String description;
	@Lob
	@Column(name="IMAGE")
	private String image;FIX_DOMAIN + "No of records fetched After calculating radius-:" + tbAstpARMasterList.size());
		}else if(lARDetails.has("appId") && !"".equalsIgnoreCase(lARDetails.getString("appId"))
				&& lARDetails.has("regionCode") && !"".equalsIgnoreCase(lARDetails.getString("regionCode"))){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetchign using Query with AppId and RegionCode....");
			tbAstpARMasterList = ctbASTPARMasterRepo.findByAppIdRegionCode(lARDetails.getString("appId"), 
					lARDetails.getString("regionCode"));			
		} else if(lARDetails.has("appId") && !"".equalsIgnoreCase(lARDetails.getString("appId"))
				&& lARDetails.has("regionCode") && !"".equalsIgnoreCase(lARDetails.getString("regionCode"))
				&& lARDetails.has("category") && !"".equalsIgnoreCase(lARDetails.getString("category"))){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetchign using Query with AppId, RegionCode and Category....");
			tbAstpARMasterList = ctbASTPARMasterRepo.findByAppIdRegionCodeCategory(lARDetails.getString("appId"), 
					lARDetails.getString("regionCode"), lARDetails.getString("category"));
		} else if(lARDetails.has("appId") && !"".equalsIgnoreCase(lARDetails.getString("appId"))
				&& lARDetails.has("latitude") && !"".equalsIgnoreCase(lARDetails.getString("latitude"))
				&& lARDetails.has("longitude") && !"".equalsIgnoreCase(lARDetails.getString("longitude"))){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetchign using Query with AppId, Latitude and Longitude....");
			tbAstpARMasterList = ctbASTPARMasterRepo.findByAppIdLatLong(lARDetails.getString("appId"), 
					lARDetails.getString("latitude"), lARDetails.getString("longitude"));
		} else if(lARDetails.has("appId") && !"".equalsIgnoreCase(lARDetails.getString("appId"))
				&& lARDetails.has("category") && !"".equalsIgnoreCase(lARDetails.getString("category"))
				&& lARDetails.has("latitude") && !"".equalsIgnoreCase(lARDetails.getString("latitude"))
				&& lARDetails.has("longitude") && !"".equalsIgnoreCase(lARDetails.getString("longitude"))){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetchign using Query with AppId, Category, Latitude and Longitude....");
			tbAstpARMasterList = ctbASTPARMasterRepo.findByAppIdLatLongCategory(lARDetails.getString("appId"), 
					lARDetails.getString("category"), lARDetails.getString("latitude"), lARDetails.getString("longitude"));
		} else if(lARDetails.has("appId") && !"".equalsIgnoreCase(lARDetails.getString("appId"))
				&& lARDetails.has("regionCode") && !"".equalsIgnoreCase(lARDetails.getString("regionCode"))
				&& lARDetails.has("category") && !"".equalsIgnoreCase(lARDetails.getString("category"))
				&& lARDetails.has("latitude") && !"".equalsIgnoreCase(lARDetails.getString("latitude"))
				&& lARDetails.has("longitude") && !"".equalsIgnoreCase(lARDetails.getString("longitude"))
				&& lARDetails.has("title") && !"".equalsIgnoreCase(lARDetails.getString("title"))
				&& lARDetails.has("additionalInfo") && !"".equalsIgnoreCase(lARDetails.getString("additionalInfo"))){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetchign using Query with AppId, RegionCode, Category, Latitude, Longitude, Title and AdditionalInfo....");
			tbAstpARMasterList = ctbASTPARMasterRepo.findByAll(lARDetails.getString("appId"), 
					lARDetails.getString("regionCode"), lARDetails.getString("category"), lARDetails.getString("latitude"), 
					lARDetails.getString("longitude"),lARDetails.getString("title"),lARDetails.getString("additionalInfo"));
		}*/
		
	}
	
	/**
	 * 
	 * @param pTbAstpARMAsterDtlsList
	 * @param pMessage
	 */
	private void buildResponse(List<TbAstpARMaster> pTbAstpARMAsterDtlsList, Message pMessage){
		JSONArray lARMasterDetails = new JSONArray();
		JSONObject lARMasterDetail = null;
		if(pTbAstpARMAsterDtlsList != null && !pTbAstpARMAsterDtlsList.isEmpty()){
			for(int i=0; i<pTbAstpARMAsterDtlsList.size(); i++){
				TbAstpARMaster tbAstpMaster = pTbAstpARMAsterDtlsList.get(i);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Entity AR Master Details of Index -:" + i +" and tbAstpMaster -:" + tbAstpMaster);
				lARMasterDetail = new JSONObject();
				lARMasterDetail.put("appId", tbAstpMaster.getAppId());
				lARMasterDetail.put("category", tbAstpMaster.getCategory());
				lARMasterDetail.put("Id", tbAstpMaster.getId());
				lARMasterDetail.put("latitude", tbAstpMaster.getLatitude());
				lARMasterDetail.put("longitude", tbAstpMaster.getLongitude());
				lARMasterDetail.put("regionCode", tbAstpMaster.getRegionCode());
				lARMasterDetail.put("title", tbAstpMaster.getTitle());
				lARMasterDetail.put("additionalInfo", tbAstpMaster.getAdditionalInfo());
				lARMasterDetail.put("description", tbAstpMaster.getDescription());
				lARMasterDetail.put("image", tbAstpMaster.getImage());
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Building AR Master Details of Index -:" + i +" and JSON -:" + lARMasterDetail);
				lARMasterDetails.put(lARMasterDetail);
			}
		}else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Details found....");
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_008);
			lDomainException.setMessage(emsg);
			lDomainException.setCode(DomainException.Code.APZ_DM_008.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		
		pMessage.getResponseObject().setResponseJson(lARMasterDetails);
	}
	private List<TbAstpARMaster> calculateRadiusnBuildResponse(List<TbAstpARMaster> pTbAstpARMAsterDtlsList, Message pMessage){
		List<TbAstpARMaster> lTbAstpARMasterList = new ArrayList<TbAstpARMaster>();
		JSONObject lARReqJSON = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " calculateRadiusnBuildResponse Request JSON Augumented Request -:" + lARReqJSON);
		JSONObject lARDetails = lARReqJSON.getJSONObject("fetchARDetails");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "calculateRadiusnBuildResponse Request  Details -:" + lARDetails);
		String lRadius = lARDetails.getString("radius");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "calculateRadiusnBuildResponse Request Details Radius-:" + lRadius);
		String latitude = lARDetails.getString("latitude");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "calculateRadiusnBuildResponse Request Details latitude-:" + latitude);
		String longitude = lARDetails.getString("longitude");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "calculateRadiusnBuildResponse Request Details longitude-:" + longitude);
		
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "calculateRadiusnBuildResponse pTbAstpARMAsterDtlsList Size-:" + pTbAstpARMAsterDtlsList.size());
		
		for(int i=0; i<pTbAstpARMAsterDtlsList.size(); i++){
			TbAstpARMaster tbAstpMaster = pTbAstpARMAsterDtlsList.get(i);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Entity AR Master Details of Index -:" + i +" and tbAstpMaster -:" + tbAstpMaster);
		}
		if(pTbAstpARMAsterDtlsList != null && !pTbAstpARMAsterDtlsList.isEmpty()){
			for(int i=0; i<pTbAstpARMAsterDtlsList.size(); i++){
				TbAstpARMaster tbAstpMaster = pTbAstpARMAsterDtlsList.get(i);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "******************* Entity AR Master Details of Index -:" + i +" and tbAstpMaster -:" + tbAstpMaster);
				String lLatitude = tbAstpMaster.getLatitude();
				String lLongitude = tbAstpMaster.getLongitude();
				boolean status = distance(Double.parseDouble(latitude), Double.parseDouble(longitude), Double.parseDouble(lLatitude), Double.parseDouble(lLongitude), "ME", lRadius);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Calculated Radius status -:" + status);
				if(status){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + ">>>>>>>>>>>>>>>>>>>> Removing the record since it is outside configured range...." + i);
					lTbAstpARMasterList.add(tbAstpMaster);
				}
			}
		}
		
		return lTbAstpARMasterList;
		
	}
	private static boolean distance(double lat1, double lon1, double lat2, double lon2, String unit, String radius) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		boolean status = true;
		if (unit == "K") {
			dist = dist * 1.609344;
		}else if (unit == "ME") {
			dist = dist * 1.609344 * 1000;
		} else if (unit == "N") {
			dist = dist * 0.8684;
		}
		double lRadius = Long.parseLong(radius);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "dist > lRadius " + dist + " > " + lRadius + " -:" + (dist > lRadius));
		if( dist > lRadius){
			status = false;
		}
			
		return status;
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
}
