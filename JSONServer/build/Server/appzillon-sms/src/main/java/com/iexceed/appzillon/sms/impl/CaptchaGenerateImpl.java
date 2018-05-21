package com.iexceed.appzillon.sms.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.iface.ICaptchaGenerate;
import com.iexceed.appzillon.utils.ServerConstants;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.producer.TextProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;
import nl.captcha.text.renderer.WordRenderer;

public class CaptchaGenerateImpl implements ICaptchaGenerate {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
			CaptchaGenerateImpl.class.toString());

	@Override
	public void handleCaptchaGenerate(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Handling Captcha Generate");
		this.generateCaptcha(pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Captcha Generated and persisted successfully");
	}
	
	public void generateCaptcha(Message pMessage) {
		JSONObject lrequestJson = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_REQUEST);
		ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
		String lresultCaptcha = null;
		String sImgType = "png";
		TextProducer ltextprod = new DefaultTextProducer();
		String lanswer = ltextprod.getText();
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Generated Captcha -: " + lanswer);
		Captcha captcha = new Captcha
				.Builder(200, 50)
				.addBackground(new GradiatedBackgroundProducer())
				.addBorder()
				.build();
		BufferedImage challengeImage = captcha.getImage();
		WordRenderer lwordrend = new DefaultWordRenderer();
		lwordrend.render(lanswer, challengeImage);
		try {
			ImageIO.write(challengeImage, sImgType, imgOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		lresultCaptcha = Base64.encodeBase64String(imgOutputStream.toByteArray());
		lrequestJson.put(ServerConstants.CAPTCHA_STRING, lanswer);
		pMessage.getHeader().setServiceType(ServerConstants.PERSIST_CAPTCHA);
		DomainStartup.getInstance().processRequest(pMessage);
		JSONObject lresponseJson = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_RESPONSE);
		lresponseJson.put(ServerConstants.CAPTCHA_STRING, lresultCaptcha);
	}

	@Override
	public void handleCaptchaValidation(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Handling Captcha Validation");
		DomainStartup.getInstance().processRequest(pMessage);
		JSONObject lresponseJson = pMessage.getResponseObject().getResponseJson()
				.getJSONObject(ServerConstants.VALIDATE_CAPTCHA_RESPONSE);
		if (lresponseJson.getString(ServerConstants.CAPTCHA_STATUS).equalsIgnoreCase(ServerConstants.SUCCESS)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Captcha Validation Success");
		} else {
			String appId = pMessage.getHeader().getAppId();
			String interfaceId = pMessage.getHeader().getInterfaceId();
			String sessionId = pMessage.getHeader().getSessionId();
			JSONObject genCaptcha = new JSONObject();
			genCaptcha.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, interfaceId);
			genCaptcha.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
			genCaptcha.put(ServerConstants.MESSAGE_HEADER_SESSION_ID, sessionId);
			JSONObject lrequestJson = new JSONObject();
			lrequestJson.put(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_REQUEST, genCaptcha);
			pMessage.getRequestObject().setRequestJson(lrequestJson);
			this.generateCaptcha(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS +  "Invalid Captcha - Validation Failed !! ");
			SmsException lSmsException = SmsException.getSMSExceptionInstance();
			lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_016));
			lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_016.toString());
			lSmsException.setPriority("1");
			throw lSmsException;
		}
	}
}
