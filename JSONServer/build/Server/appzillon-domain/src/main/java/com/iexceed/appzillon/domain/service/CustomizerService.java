package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAsczScreenLayouts;
import com.iexceed.appzillon.domain.entity.TbAsczScreenLayoutsPK;
import com.iexceed.appzillon.domain.entity.TbAsczTemplateObjects;
import com.iexceed.appzillon.domain.entity.TbAsczTemplateObjectsPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsczScreenLayoutsRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsczTemplateObjectsRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiScreenLayoutsRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 4:19 PM
 */

@Named(ServerConstants.SERVICE_CUSTOMIZER)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class CustomizerService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
			CustomizerService.class.toString());

	@Inject
	TbAsczTemplateObjectsRepository objectsRepository;
	@Inject
	TbAsczScreenLayoutsRepository repository;
	/*@Inject
	TbAsmiLayoutDesignRepository tbAsmiLayoutDesignRepository;*/
	@Inject
	TbAsmiScreenLayoutsRepository tbAsmiScreensLayoutRepository;

	public void fetchQueryDesignData(Message pMessage) {
		/*try {
			JSONObject request = pMessage.getRequestObject().getRequestJson()
					.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_QUERYDESIGNER_REQUEST);
			String appId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String screenId = request.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String layOutId = request.getString(ServerConstants.LAYOUT_ID);
			// Fetching ScreenDesigns:
			List<Object[]> screenDesigns = null;
			JSONArray lScrDesignsArr = new JSONArray();
			screenDesigns = tbAsmiLayoutDesignRepository.findScreenDesigns(appId, screenId, layOutId);
			if (!screenDesigns.isEmpty()) {
				for (Object[] obj : screenDesigns) {
					JSONObject json = new JSONObject();
					json.put(ServerConstants.SCREEN_ID, obj[0]);
					json.put(ServerConstants.DESIGN_ID, obj[1]);
					json.put(ServerConstants.DESIGN_NAME, obj[2]);
					json.put(ServerConstants.DESIGN_ICON, obj[3]);
					lScrDesignsArr.put(json);
				}
			}
			// Fetching CurrentDesign
			String currentDesigns = null;
			JSONObject lcurrentDesJSon = new JSONObject();
			currentDesigns = tbAsmiScreensLayoutRepository.findCurrentDesigns(appId, layOutId, screenId);
			if (currentDesigns != null && !currentDesigns.isEmpty()) {
				lcurrentDesJSon.put(ServerConstants.DESIGN_ID, currentDesigns);
			}
			// Fetching MicroApps
			List<Object[]> microApps = null;
			JSONArray lmicroAppsArray = new JSONArray();
			microApps = tbAsmiScreensLayoutRepository.findMicroApps(appId, screenId);
			if (!microApps.isEmpty()) {
				for (Object[] obj : microApps) {
					JSONObject json = new JSONObject();
					json.put(ServerConstants.MICRO_APP_ID, obj[0]);
					json.put(ServerConstants.MICRO_APP_NAME, obj[1]);
					json.put(ServerConstants.MICRO_APP_ICON, obj[2]);
					lmicroAppsArray.put(json);
				}
			}
			// Fetching Navigators
			List<Object[]> navigators = null;
			JSONArray lNavigatorsArray = new JSONArray();
			navigators = tbAsmiScreensLayoutRepository.findNavigators(appId, screenId);
			if (!navigators.isEmpty()) {
				for (Object[] obj : navigators) {
					JSONObject json = new JSONObject();
					json.put(ServerConstants.NAVIGATOR_ID, obj[0]);
					json.put(ServerConstants.NAVIGATOR_NAME, obj[1]);
					json.put(ServerConstants.NAVIGATOR_ICON, obj[2]);
					lNavigatorsArray.put(json);
				}
			}
			// Fetching Widgets
			List<Object[]> widgets = null;
			JSONArray lWidgetsArray = new JSONArray();
			widgets = tbAsmiScreensLayoutRepository.findWidgets(appId, screenId);
			if (!widgets.isEmpty()) {
				for (Object[] obj : widgets) {
					JSONObject json = new JSONObject();
					json.put(ServerConstants.WIDGET_ID, obj[0]);
					json.put(ServerConstants.WIDGET_NAME, obj[1]);
					json.put(ServerConstants.WIDGET_ICON, obj[2]);
					lWidgetsArray.put(json);
				}
			}

			JSONObject lResponse = new JSONObject();
			lResponse.put(ServerConstants.SCREEN_DESIGNS, lScrDesignsArr);
			lResponse.put(ServerConstants.CURRENT_DESIGNS, lcurrentDesJSon);
			lResponse.put(ServerConstants.MICRO_APPS, lmicroAppsArray);
			lResponse.put(ServerConstants.NAVIGATORS, lNavigatorsArray);
			lResponse.put(ServerConstants.WIDGETS, lWidgetsArray);
			JSONObject pResponse = new JSONObject();
			pResponse.put(ServerConstants.APPZILLON_ROOT_GET_QUERYDESIGNER_RESPONSE, lResponse);
			pMessage.getResponseObject().setResponseJson(pResponse);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Response from Domain CustomizerService is:"
					+ pMessage.getResponseObject().getResponseJson());

		} catch (JSONException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, e);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(e.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}*/
	}

	public void saveCustomizationData(Message pMessage) {
		JSONObject request = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.APPZILLON_ROOT_SAVE_CUSTOMIZATION_DATA_REQUEST);
		JSONObject customeDesign = request.getJSONObject(ServerConstants.CUSTOME_DESIGN);
		String cAppId = customeDesign.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String cScreenId = customeDesign.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
		String cLayOutId = customeDesign.getString(ServerConstants.LAYOUT_ID);
		String cDesignId = customeDesign.getString(ServerConstants.DESIGN_ID);

		TbAsczScreenLayoutsPK tbAsczScreenLayoutsPK = new TbAsczScreenLayoutsPK(cAppId, cScreenId, cLayOutId);
		TbAsczScreenLayouts tbAsczScreenLayouts = new TbAsczScreenLayouts();
		tbAsczScreenLayouts.setId(tbAsczScreenLayoutsPK);
		tbAsczScreenLayouts.setDefaultTemplate(cDesignId);
		tbAsczScreenLayouts.setAuthStatus("U");
		tbAsczScreenLayouts.setCheckerId(null);
		tbAsczScreenLayouts.setMakerId(pMessage.getHeader().getUserId());
		tbAsczScreenLayouts.setMakerTs(new Date());
		tbAsczScreenLayouts.setVersionNo(1);
		repository.save(tbAsczScreenLayouts);

		// JSONObject designReceivers =
		// request.getJSONObject(ServerConstants.DESIGN_RECEIVERS);

        // delete existing design receiver
        objectsRepository.deleteDesignReceiverByAppIdScreenIdLayoutIdTemplateId(cAppId,cScreenId,cLayOutId,cDesignId);

		JSONArray designReceivers = request.getJSONArray(ServerConstants.DESIGN_RECEIVERS);
		List<TbAsczTemplateObjects> tbAsczTemplateObjects = new ArrayList<TbAsczTemplateObjects>();
		for (int i = 0; i < designReceivers.length(); i++) {
			String lAppId = designReceivers.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String lScreenId = designReceivers.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String lLayOutId = designReceivers.getJSONObject(i).getString(ServerConstants.LAYOUT_ID);
			String lDesignId = designReceivers.getJSONObject(i).getString(ServerConstants.DESIGN_ID);
			String lParentId = designReceivers.getJSONObject(i).getString(ServerConstants.PARENT_ID);
			String lChildId = designReceivers.getJSONObject(i).getString(ServerConstants.CHILD_ID);
			String lChildSeq = designReceivers.getJSONObject(i).getString(ServerConstants.CHILD_SEQ);
			TbAsczTemplateObjectsPK tbAsczTemplateObjectsPK = new TbAsczTemplateObjectsPK(lAppId, lScreenId, lLayOutId,
					lDesignId, lParentId, lChildId);
			TbAsczTemplateObjects templateObjects = new TbAsczTemplateObjects();
			templateObjects.setId(tbAsczTemplateObjectsPK);
			templateObjects.setChildSeq(Integer.parseInt(lChildSeq));
            tbAsczTemplateObjects.add(templateObjects);
		}
        objectsRepository.save(tbAsczTemplateObjects);
		JSONObject response = new JSONObject();
		response.put(ServerConstants.APPZILLON_ROOT_SAVE_CUSTOMIZATION_DATA_RESPONSE, getCustomizerDetails(cAppId,cScreenId,ServerConstants.YES,cLayOutId,cDesignId));
		pMessage.getResponseObject().setResponseJson(response);
	}

	public void queryDeviceGroups(Message pMessage) {
		JSONArray lResponse = new JSONArray();
		try {
			JSONObject jsonObject = pMessage.getRequestObject().getRequestJson()
					.getJSONObject(ServerConstants.APPZILLON_ROOT_DEVICEGRPQUERY_REQUEST);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request json is:" + jsonObject);
			String appId = jsonObject.getString("appId");
			List<Object[]> lResp = null;
			lResp = tbAsmiScreensLayoutRepository.findDeviceGroupsByAppId(appId);
			if (!lResp.isEmpty()) {

				for (Object[] obj : lResp) {
					JSONObject json = new JSONObject();
					json.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, obj[0]);
					json.put(ServerConstants.DEVICE_DESC, obj[1]);
					json.put(ServerConstants.OS, obj[2]);
					json.put(ServerConstants.HEIGHT, obj[3]);
					json.put(ServerConstants.WIDTH, obj[4]);
					json.put(ServerConstants.ORIENTATION, obj[5]);
					lResponse.put(json);
				}

				JSONObject deviceGrps = new JSONObject();
				deviceGrps.put(ServerConstants.DEVICE_GROUPS, lResponse);
				JSONObject pResp = new JSONObject();
				pResp.put(ServerConstants.APPZILLON_ROOT_DEVICEGRPQUERY_RESPONSE, deviceGrps);
				pMessage.getResponseObject().setResponseJson(pResp);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Device Groups Query Response from Doamin is:"
						+ pMessage.getResponseObject().getResponseJson());

			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No record found", dexp);
				throw dexp;
			}
		} catch (JSONException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, e);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(e.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}

	}

	public void getListOfScreens(Message pMessage) {
		/*JSONArray lResponse = new JSONArray();
		try {
			JSONObject jsonObject = pMessage.getRequestObject().getRequestJson()
					.getJSONObject(ServerConstants.APPZILLON_ROOT_APPSCREENQUERY_REQUEST);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request json is:" + jsonObject);
			String appId = jsonObject.getString("appId");
			String deviceGroupId = jsonObject.getString("deviceGroupId");
			String orientation = jsonObject.getString("orientation");
			List<Object[]> lResp = null;
			lResp = tbAsmiScreensLayoutRepository.findListOfScreensB(appId, deviceGroupId, orientation);
			if (!lResp.isEmpty()) {
				for (Object[] obj : lResp) {
					JSONObject json = new JSONObject();
					json.put(ServerConstants.SCREEN_ID, obj[0]);
					json.put(ServerConstants.SCREEN_NAME, obj[1]);
					json.put(ServerConstants.LAYOUT_ID, obj[2]);
					lResponse.put(json);
				}
				JSONObject deviceGrps = new JSONObject();
				deviceGrps.put(ServerConstants.APP_SCREENS, lResponse);
				JSONObject pResp = new JSONObject();
				pResp.put(ServerConstants.APPZILLON_ROOT_APPSCREENQUERY_RESPONSE, deviceGrps);
				pMessage.getResponseObject().setResponseJson(pResp);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "List of Screens Query Response from Doamin is:"
						+ pMessage.getResponseObject().getResponseJson());
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No record found", dexp);
				throw dexp;
			}
		} catch (JSONException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, e);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(e.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}*/

	}
	

	public void getCustomizerServiceDetails(Message pMessage) {
		JSONObject request = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_CUSTOMIZER_DETAILS_REQUEST);
		String appId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String screenId = request.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
		String customizer = request.getString(ServerConstants.CUSTOMIZER);
		String layoutId = request.getString(ServerConstants.LAYOUT_ID);
		String templateId = request.has(ServerConstants.TEMPLATE_ID) ? request.getString(ServerConstants.TEMPLATE_ID) : null;


		pMessage.getResponseObject().setResponseJson(new JSONObject().
				put(ServerConstants.APPZILLON_ROOT_GET_CUSTOMIZER_DETAILS_RESPONSE, getCustomizerDetails(appId,screenId,customizer,layoutId,templateId)));
	}
	private JSONObject getCustomizerDetails(String appId,String screenId,String customizer,String layoutId,String templateId){
	    JSONObject lResponse = new JSONObject();
        TbAsczScreenLayoutsPK tbAsczScreenLayoutsPK = new TbAsczScreenLayoutsPK(appId, screenId, layoutId);
        TbAsczScreenLayouts tbAsczScreenLayouts = repository.findOne(tbAsczScreenLayoutsPK);
        if(Utils.isNullOrEmpty(templateId)){
            templateId=tbAsczScreenLayouts != null ? tbAsczScreenLayouts.getDefaultTemplate():ServerConstants.PERCENT;
        }
        if (tbAsczScreenLayouts != null) {
            lResponse.put(ServerConstants.ISMODIFIED,true);
            lResponse.put(ServerConstants.TEMLT, templateId);
        } else {
            lResponse.put(ServerConstants.ISMODIFIED,false);
        }

        List<Object[]> objects = objectsRepository.findTemplateObjectOrderByParentIdAndChildSeq(appId,screenId,layoutId,templateId);
        JSONObject containers = new JSONObject();
        Iterator<Object[]> iterator = objects.iterator();
        while (iterator.hasNext()) {
            Object[] objects1 = iterator.next();
            String parentId = (String) objects1[0];
            String childSeq = (String) objects1[1];
            if (containers.has(parentId)) {
                ((ArrayList<String>) containers.get(parentId)).add(childSeq);
            } else {
                ArrayList<String> strings = new ArrayList<String>();
                strings.add(childSeq);
                containers.put(parentId, strings);
            }
        }

        Iterator<String> jsonKeys = containers.keys();
        JSONArray lContainers = new JSONArray();
        while (jsonKeys.hasNext()) {
            String key = jsonKeys.next();
            JSONObject json = new JSONObject();
            json.put(ServerConstants.ID, key);
            json.put(ServerConstants.SEQUENCE, containers.get(key));
            lContainers.put(json);
        }
        lResponse.put(ServerConstants.CONTAINERS, lContainers);

		if (ServerConstants.YES.equalsIgnoreCase(customizer)) {
			List<Object[]> microApps;
			microApps = tbAsmiScreensLayoutRepository.findMicroApps(appId, screenId);
			ArrayList<String> microAppList = new ArrayList<String>();
			for (Object[] microApp : microApps) {
				microAppList.add((String) microApp[0]);

			}
			lResponse.put(ServerConstants.MICROAPPS, microAppList);

			List<Object[]> widgets;
			widgets = tbAsmiScreensLayoutRepository.findWidgets(appId, screenId);
			ArrayList<String> widgetsList = new ArrayList<String>();
			for (Object[] widget : widgets) {
				widgetsList.add((String) widget[0]);

			}
			lResponse.put(ServerConstants.CALLFORMS, widgetsList);

			List<Object[]> navigators;
			navigators = tbAsmiScreensLayoutRepository.findNavigators(appId, screenId);
			ArrayList<String> navigatorsList = new ArrayList<String>();
			for (Object[] navigator : navigators) {
				navigatorsList.add((String) navigator[0]);

			}
			lResponse.put(ServerConstants.NAVIGATORS, navigatorsList);

        }
	    return  lResponse;

    }
}
