package com.iexceed.appzillon.domain.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAstpSeqGen;
import com.iexceed.appzillon.domain.repository.meta.TbAstpSeqGenRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

@Named(ServerConstants.SERVICE_SEQUENCE)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class SequenceService {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
			SequenceService.class.toString());
	
	@Inject
	TbAstpSeqGenRepository cseqGenRepo;
	
	public void getSequenceValue(Message pMessage) {
		int sequenceValue = 0;
		JSONObject sequenceDtlsRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetch Sequence Details Request JSON -:" + sequenceDtlsRequest);
		JSONObject sequenceRequest = sequenceDtlsRequest.getJSONObject("sequenceDtlsRequest");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetch Sequence Request JSON -:" + sequenceRequest);
		String sequenceName = sequenceRequest.getString("sequenceName");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetch Sequence Name -:" + sequenceName);
		TbAstpSeqGen astpSeq = cseqGenRepo.getloggingsequencenumber(sequenceName);
		sequenceValue = astpSeq.getSequenceValue();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Sequence generated Sequence Value -:" + sequenceValue);
		JSONObject sequenceValueJson = new JSONObject();
		sequenceValueJson.put("sequenceValue", "" + sequenceValue);
		if(sequenceValue == Integer.MAX_VALUE) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Sequence Value reached limit so resetting it to 1");
			sequenceValue = 0;
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Sequence generated Response JSON -:" + sequenceValueJson);
		astpSeq.setSequenceValue((int) sequenceValue + 1);
		cseqGenRepo.save(astpSeq);

		pMessage.getResponseObject().setResponseJson(new JSONObject().put("sequenceDtlsResponse", sequenceValueJson));
	}
}
