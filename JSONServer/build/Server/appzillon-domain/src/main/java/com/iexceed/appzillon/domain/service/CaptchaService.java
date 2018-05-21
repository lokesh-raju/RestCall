package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiCaptchaDtls;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiCaptchaDtlsRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiIntfMasterRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.ServerConstants;

@Named("CaptchaService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class CaptchaService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
			CaptchaService.class.toString());

	@Inject
	TbAsmiCaptchaDtlsRepository cAsmiCaptchaDtlsRepository;
	@Inject
	TbAsmiIntfMasterRepository cAsmiIntfMasterRepo;

	public void persistCaptcha(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Inside persistCaptcha() ");
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to delete processed captcha records ");
		List<TbAsmiCaptchaDtls> asmiCaptchaDtlsDelete = cAsmiCaptchaDtlsRepository.findAsmiCaptchaDtlsByCaptchaStatus();
		cAsmiCaptchaDtlsRepository.delete(asmiCaptchaDtlsDelete);
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Deleted all processed captcha records ");

		JSONObject requestJson = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_REQUEST);
		String appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String interfaceId = requestJson.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
		String sessionId = requestJson.getString(ServerConstants.MESSAGE_HEADER_SESSION_ID);
		String captchaString = requestJson.getString(ServerConstants.CAPTCHA_STRING);
		captchaString = HashUtils.hashSHA256(captchaString, appId + interfaceId);
		Timestamp createTs = new Timestamp(System.currentTimeMillis());
		TbAsmiCaptchaDtls asmiCaptchaDtls = new TbAsmiCaptchaDtls();

		if (requestJson.has(ServerConstants.CAPTCHA_REF)) {
			asmiCaptchaDtls.setCaptchaRef(Integer.parseInt(requestJson.getString(ServerConstants.CAPTCHA_REF)));
		}
		asmiCaptchaDtls.setAppId(appId);
		asmiCaptchaDtls.setInterfaceId(interfaceId);
		asmiCaptchaDtls.setSessionId(sessionId);
		asmiCaptchaDtls.setCaptchaString(captchaString);
		asmiCaptchaDtls.setCreateTs(createTs);
		asmiCaptchaDtls.setCaptchaStatus("NP");

		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Captcha asmiCaptchaDtls :" + asmiCaptchaDtls);
		cAsmiCaptchaDtlsRepository.save(asmiCaptchaDtls);
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Captcha Details persisted successfully..");
		pMessage.getResponseObject()
				.setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_RESPONSE,
						new JSONObject().put("captchaRef", asmiCaptchaDtls.getCaptchaRef())));

	}

	public void validateCaptcha(Message pMessage) {
		String refNo = pMessage.getHeader().getCaptchaRef();
		TbAsmiCaptchaDtls asmiCaptchaDtls = cAsmiCaptchaDtlsRepository.findOne(Integer.parseInt(refNo));
		if (asmiCaptchaDtls != null) {
			asmiCaptchaDtls.setCaptchaStatus("P");
			String captchaAnswer = pMessage.getHeader().getCaptchaString();
			captchaAnswer = HashUtils.hashSHA256(captchaAnswer,
					pMessage.getHeader().getAppId() + pMessage.getHeader().getInterfaceId());
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside validateCaptcha() Captcha details found");
			if (asmiCaptchaDtls.getCaptchaString().equals(captchaAnswer)) {
				Timestamp validateTs = new Timestamp(System.currentTimeMillis());
				asmiCaptchaDtls.setValidateTs(validateTs);
				String captchaStatus = ServerConstants.SUCCESS;
				JSONObject responseJson = new JSONObject();
				responseJson.put(ServerConstants.CAPTCHA_STATUS, captchaStatus);
				pMessage.getResponseObject()
						.setResponseJson(new JSONObject().put(ServerConstants.VALIDATE_CAPTCHA_RESPONSE, responseJson));
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Captcha asmiCaptchaDtls :" + asmiCaptchaDtls);
			} else {
				String captchaStatus = ServerConstants.FAILURE;
				JSONObject responseJson = new JSONObject();
				responseJson.put(ServerConstants.CAPTCHA_STATUS, captchaStatus);
				pMessage.getResponseObject()
						.setResponseJson(new JSONObject().put(ServerConstants.VALIDATE_CAPTCHA_RESPONSE, responseJson));
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Captcha asmiCaptchaDtls :" + asmiCaptchaDtls);
			}
			cAsmiCaptchaDtlsRepository.save(asmiCaptchaDtls);
		} else {
			String captchaStatus = ServerConstants.FAILURE;
			JSONObject responseJson = new JSONObject();
			responseJson.put(ServerConstants.CAPTCHA_STATUS, captchaStatus);
			pMessage.getResponseObject()
					.setResponseJson(new JSONObject().put(ServerConstants.VALIDATE_CAPTCHA_RESPONSE, responseJson));
		}
	}

}