package com.iexceed.appzillon.services;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;

import com.iexceed.appzillon.dao.EJBDetails;
import com.iexceed.appzillon.dao.EJBParamDetails;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.XMLToJsonConverter;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;

public class EJBService implements IServicesBean{

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					EJBService.class.toString());
	protected EJBDetails cEjbDetails = null;
	private Object responsePayload=null;

	/**
	 * 
	 * @param pMessage
	 * @param pContext
	 * @return
	 */
	public Object buildRequest(com.iexceed.appzillon.message.Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		Object[] lParamArray = null;
		  JSONObject lRequestJson = null;
		  String lParamValue = null;

		  
		   LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Building request for external EJB service....");
		   List<EJBParamDetails> lparamList = cEjbDetails.getParamList();
		   LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "EJB Service request's parameters list -:"
		     + lparamList);
		   if(lparamList!=null){
		   LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Initializing EJB Service request's Object Parameters array of size:"
		     + lparamList.size());
		   lParamArray = new Object[lparamList.size()];
		   LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Looping through the request json and parameters index to build request....");
		   
		   for (EJBParamDetails fParamlist : lparamList) {
		    int lParamArrayIndex = Integer.parseInt(fParamlist.getParamOrder());
			String paramType=fParamlist.getParamType();
		    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "  Parameter type for param index " + lParamArrayIndex +" is :"+paramType);
		    // Fetching the node structure value from the input JSON
		    String[] nodestructure = fParamlist.getNodeStructureName()
		      .split("[\\.]");
		    // Initializing j for looping
		    int j = 0;
		    // Assing value to JSONObject
		    lRequestJson = new JSONObject(pRequestPayLoad.toString());
		    lParamValue = null;
		    for (; j < nodestructure.length - 1; j++) {
		     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Node at index [" + j + "] -:" + nodestructure[j]);
		     if (lRequestJson.get(nodestructure[j]) instanceof JSONObject) {
		      LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "  Json Instance found and the value is -:" + lRequestJson.getJSONObject(nodestructure[j]));
		      lRequestJson = lRequestJson.getJSONObject(nodestructure[j]);

		     } else if (lRequestJson.get(nodestructure[j]) instanceof String) {
		      lParamValue = lRequestJson.get(nodestructure[j]).toString();
		      LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "  String Instance found and the value is -:" + lParamValue);
		     }
		    }
		    if (lParamValue == null || lParamValue.isEmpty()) {
		    if(paramType.equals("JSON") || paramType.equals("SERIALIZABLEJAVAOBJECT") ){
		   	     lParamArray[lParamArrayIndex] = lRequestJson.getJSONObject(nodestructure[j]);
			     // Storing node value to parameter value variable
			     lParamValue = lRequestJson.getJSONObject(nodestructure[j]).toString();
		    	}
		    	else{
		   	     lParamArray[lParamArrayIndex] = lRequestJson.getString(nodestructure[j]);
			     // Storing node value to parameter value variable
			     lParamValue = lRequestJson.getString(nodestructure[j]);
		    	}
		     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After setting Parameters value -:" + lParamValue);
		    } else {

		     lParamArray[lParamArrayIndex] = lParamValue;
		    }

		    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Request built before checking the parameters type -:"+ lParamValue);
		    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Checking parameter type and the type is -:"+ fParamlist.getParamType());
		    
		    // Checking if the Parameter type is serializable object
		    if (fParamlist.getParamType().equalsIgnoreCase(
		      ServerConstants.EJB_SERIALIZABLE_OBJECT_TYPE)) {
		     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Parameter Type is found to be Serializable java object, hence converting it into a XML....");
		     // Getting XML from JSON
		     String lXML = ExternalServicesRouter
		       .getJSONtoXML(lParamValue);
		     LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Parameter Type is found to be Serializable java object,after converting to an XML -:" + lXML);
		     LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Paramter type is serializable object hence unmarshalling the input xml to java object of class -:" + fParamlist.getQualifiedClassName());
		     Object lReqObject = ExternalServicesRouter
		       .getUnMarshalled(lXML,
		         fParamlist.getQualifiedClassName());
		     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After unmarshalling the request object:"
		       + lReqObject);
		     lParamArray[lParamArrayIndex] = lReqObject;
		     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Added the unmarshalled object to the parameter object array");
		    }
		   }
		   }
		   LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After adding request parameters, final request Parameter array is-:"+ lParamArray);
		  
		  return lParamArray;
	}

	/**
	 * 
	 * @param pMessage
	 * @param pContext
	 */
	public void getEJBDetails(com.iexceed.appzillon.message.Message pMessage,
			SpringCamelContext pContext) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ApplicationId -:"
				+ pMessage.getHeader().getAppId() + ", InterfaceId -:"
				+ pMessage.getHeader().getInterfaceId()
				+ ", and Service Details BeanId -:"
				+ pMessage.getHeader().getAppId() + "_"
				+ pMessage.getHeader().getInterfaceId());
		cEjbDetails = (EJBDetails) ExternalServicesRouter
				.injectBeanFromSpringContext(pMessage.getHeader().getAppId()
						+ "_" + pMessage.getHeader().getInterfaceId(), pContext);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Service Details Bean is injected."
				+ cEjbDetails);
		int timeOut = cEjbDetails.getTimeOut();
        /**
         * Below changes are made by Vinod as part of 
         * At app level, service time out should be configurable.
         * Appzillon 3.1 - 63 -- Start
         */
		if (timeOut == 0) {
				LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Timeout value not configured will use default timeOut");
				timeOut =Integer.parseInt(PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.DEFAULT_TIMEOUT).trim()) ;
			}
		/** Appzillon 3.1 - 63 -- END */
		cEjbDetails.setTimeOut(timeOut);
	}

	/**
	 *
	 * @param pMessage
	 * @param pRequestPayLoad
	 * @param pContext
	 * @return
	 */
	public Object callService(com.iexceed.appzillon.message.Message pMessage,
			Object pRequestPayLoad, SpringCamelContext pContext) {
		LOG.debug("Inside SuperClass : callService ");
		String output = "";
		long startTime;
		Exchange exchange = null;
		String appId = pMessage.getHeader().getAppId();
		String interfaceId = pMessage.getHeader().getInterfaceId();
		getEJBDetails(pMessage,pContext);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Getting producer Template....");
		ProducerTemplate pProducerTemplate = FrameworksStartup.getInstance().getProducerTemplate();
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Built producer Template -:" + pProducerTemplate);
		
		String camelID = (appId + "__" + interfaceId).replaceAll("[\\.]", "__");
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "EndPoint Id is built for the AppId -:" + appId +", InterfaceId -:" + interfaceId + ", and the Camel EndPoint Id -:" + camelID);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Requesting to build request Object....");
		pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad, 
				cEjbDetails.getAutoGenElementMap(), cEjbDetails.getTranslationElementMap());
		pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad+""));
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Appending Request Json With MaskedId : " + pMessage.getRequestObject().getRequestJson());
		Object buildParam = buildRequest(pMessage, pRequestPayLoad.toString(), pContext);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Request Object built is -:" + buildParam);
		final Object[] lparamArray = (Object[]) buildParam;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "converting request object to an  Object Array -:"+ lparamArray);
		
		if (lparamArray != null) {
			for (int k = 0; k < lparamArray.length; k++) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Request Parameter Array[" + k + "] , value is -:"
						+ lparamArray[k]);
			}
		}

		Message msg = null;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Getting EndPoint for the Camel Endpoint Id -:" + camelID);
		Endpoint endPoint = pContext.getEndpoint(camelID);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "EndPoint for the Camel Endpoint Id -:" + camelID + " is -:" + endPoint);
		
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Building Future<Exchange> object by setting request body parameter array....");
		Utils.setExtTime(pMessage,"S");
		Future<Exchange> futurexchange = (Future<Exchange>) pProducerTemplate
				.asyncSend(endPoint, new Processor() {
					public void process(Exchange exchange) throws Exception {
						if (lparamArray != null) {
							exchange.getIn().setHeader(
									Exchange.BEAN_MULTI_PARAMETER_ARRAY, true);
						}
						exchange.getIn().setBody(lparamArray);
					}

				});
		
		startTime = System.currentTimeMillis();
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Future Exchange request sent at :" + startTime);
		try {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Setting Furture<Exchange> time out....");
			exchange = futurexchange.get(cEjbDetails.getTimeOut(),
					TimeUnit.SECONDS);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Future Exchange received -:" + exchange);
		} catch (InterruptedException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Future - InterruptedException:",e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (ExecutionException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Future - ExecutionException:",e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (TimeoutException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Future - TimeoutException:",e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} finally {
			futurexchange.cancel(true);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Stopping future exchange....");
		}
		Utils.setExtTime(pMessage,"E");
		if (exchange.getException() == null) {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "No exception hence proceeding to process response....");
		    long endTime = System.currentTimeMillis();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response received at -:" + endTime);
			if ((endTime - startTime) > 1000) {
				long quotient = (endTime - startTime) / 1000;
				long Remainder = (endTime - startTime) % 1000;
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Time Taken for the service -:" + interfaceId + " is -: " + quotient
						+ "." + Remainder + " Seconds");
			} else {
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Time Taken for the service -: " + interfaceId + " is -: "
						+ (endTime - startTime) + " MilliSeconds");
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Getting Camel Message from the exchange....");
			msg = exchange.getOut();


			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Camel Message received from the exchange is -:" + msg);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Requesting to processing the response....");
			output = (String) processResponse(pMessage,msg, pContext);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After processing response, the response is -:" + output);
			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.SUCCESS,responsePayload,lparamArray);
		} else {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "callExternalService - Exchange-exception :"
					+ Utils.getStackTrace(exchange.getException()));
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();

			ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,exchange.getException().getMessage(),lparamArray);

			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;

		}

		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response returned after calling external service is -:" + output);
		return new JSONObject(output);
	}

	@SuppressWarnings("unchecked")
	public Object processResponse(com.iexceed.appzillon.message.Message pMessage, Object pResponse, SpringCamelContext pContext) {
		Message pMsg = (Message) pResponse;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Processing response and the response recevied is -: "+pMsg);
		String outputJSON = null;
		try {
			if (ServerConstants.XML.equalsIgnoreCase(cEjbDetails
					.getResponseContentType())) {
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response content type is XML and the response is -:" + pMsg.getMandatoryBody(String.class) +" -> and will be converting it to a JSON....");
				outputJSON=pMsg.getMandatoryBody(String.class);
				responsePayload=outputJSON;
				outputJSON = XMLToJsonConverter.xmlToJson(outputJSON);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Response content type is XML, After converting response XML to JSON String:"
						+ outputJSON);
			} else if (ServerConstants.EJB_SERIALIZABLE_OBJECT_TYPE
					.equalsIgnoreCase(cEjbDetails.getResponseContentType())) {
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response content type is serializable java object and the response is -:" + pMsg.getMandatoryBody(String.class));
				@SuppressWarnings("rawtypes")
				Class lclass = null;
				try {
					lclass = Class.forName(cEjbDetails
							.getResponseFullyQualifiedClassName());
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response content type is serializable java object and of class -" + lclass.getClass());
				} catch (ClassNotFoundException e) {
					LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response content type is serializable java object Class is Not Found -:"
							, e);
					ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
					exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
					exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017)
							);
					exsrvcallexp.setPriority("1");
					throw exsrvcallexp;
				}

				Object lresponseObj;
				lresponseObj = pMsg.getMandatoryBody(lclass);
				responsePayload=lresponseObj;
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Converting the serializable java object to XML to convert it to a JSON.");
				// Marshalling the object to XML string
				outputJSON = ExternalServicesRouter.getMarshalled(lresponseObj,
						cEjbDetails.getResponseFullyQualifiedClassName());
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Marshelling to XML from response Object -:" + outputJSON);
				// Converting XML string to JSON String
				outputJSON = ExternalServicesRouter.getXMLToJSON(outputJSON);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After converting XML to a JSON String:" + outputJSON);
			} else {
				outputJSON = pMsg.getMandatoryBody(String.class);
				responsePayload=outputJSON;
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response content type is  string and the response is -:" + outputJSON);
			}
		} catch (InvalidPayloadException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "InvalidPayloadException: Error parsing  external response"
					, e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017)
					);
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		}
		return outputJSON;
	}

	/**
	 * 
	 * @param pHeaderMap
	 * @param pContext
	 */

	/*public void callExternalService(
			com.iexceed.appzillon.message.Message pMessage,
			SpringCamelContext p_context, ProducerTemplate p_ProducerTemplate)
			throws ExternalServicesRouterException, InvalidPayloadException,
			ClassNotFoundException {
		LOG.debug("Inside EJB client : callExternalService ");
		Exchange lExchange = null;
		Message lmsg = null;
		String lappId = pMessage.getHeader().getAppId();
		String linterfaceId = pMessage.getHeader().getInterfaceId();
		String lpayLoad = pMessage.getRequestObject().getRequestJson()
				.toString();
		String loutput = "";
		long lstartTime;
		EJBDetails lEJBDetails = (EJBDetails) new EJBServicesImpl()
				.injectBeanFromSpringContext(lappId + "_" + linterfaceId,
						p_context);
		LOG.debug("callExternalService - l_EJBDetails :" + lEJBDetails);
		LOG.info("callExternalService - l_EJBDetails :" + lEJBDetails);

		String camelID = (lappId + "__" + linterfaceId).replaceAll("[\\.]",
				"__");
		LOG.debug("callExternalService- camelID:" + camelID);
		LOG.debug("Calling getQueryString(interfaceId, payLoad,p_context)");
		final Object[] l_paramArray = getQueryString(lappId, linterfaceId,
				lpayLoad, p_context, lEJBDetails);
		LOG.debug("After Calling getQueryString(interfaceId, payLoad,p_context): Object Array Returned:"
				+ l_paramArray);
		LOG.info("After Calling getQueryString(interfaceId, payLoad,p_context): Object Array Returned:"
				+ l_paramArray);

		if (l_paramArray != null) {
			for (int k = 0; k < l_paramArray.length; k++) {
				LOG.debug("Array returned: l_paramArray[" + k + "]:"
						+ l_paramArray[k]);
				LOG.info("Array returned: l_paramArray[" + k + "]:"
						+ l_paramArray[k]);
			}
		}

		LOG.debug("Camel ID:" + camelID);
		LOG.debug("p_context:" + p_context);
		LOG.debug("p_ProducerTemplate:" + p_ProducerTemplate);
		Endpoint end = p_context.getEndpoint(camelID);
		LOG.debug("direct start endpoint..." + end);

		LOG.debug("Calling producer.request which returns Future exchange****");

		Future<Exchange> futurexchange = (Future<Exchange>) p_ProducerTemplate
				.asyncSend(end, new Processor() {

					public void process(Exchange exchange) throws Exception {
						if (l_paramArray != null) {

							exchange.getIn().setHeader(
									Exchange.BEAN_MULTI_PARAMETER_ARRAY, true);

						}
						exchange.getIn().setBody(l_paramArray);
					}

				});
		LOG.debug("**** Future Exchange request sent....");

		lstartTime = System.currentTimeMillis();
		LOG.info("Request Start Time" + lstartTime);
		try {
			LOG.debug("**** Future Exchange setting time out....");
			lExchange = futurexchange.get(lEJBDetails.getTimeOut(),
					TimeUnit.SECONDS);
			LOG.debug("**** Future Exchange received exchange...." + lExchange);
		} catch (InterruptedException e) {
			LOG.error("Future - InterruptedException:" + Utils.getStackTrace(e));
			LOG.debug("InterruptedException hence will be throwing an exception....");
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			EnumMap<EXCEPTION_CODE, String> exceptionMap = exsrvcallexp.FRAME_WORK_EXCEPTIONS;
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exceptionMap
					.get(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (ExecutionException e) {
			LOG.error("Future - ExecutionException:" + Utils.getStackTrace(e));
			LOG.debug("ExecutionException hence will be throwing an exception....");
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			EnumMap<EXCEPTION_CODE, String> exceptionMap = exsrvcallexp.FRAME_WORK_EXCEPTIONS;
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exceptionMap
					.get(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (TimeoutException e) {
			LOG.error("Future - TimeoutException:" + Utils.getStackTrace(e));
			LOG.debug("Time out Exception hence will be throwing an exception....");
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			EnumMap<EXCEPTION_CODE, String> exceptionMap = exsrvcallexp.FRAME_WORK_EXCEPTIONS;
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exceptionMap
					.get(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} finally {
			futurexchange.cancel(true);
			LOG.debug("Stopping future exchange....");
		}

		LOG.debug("***** exchange:" + lExchange);

		LOG.debug("Didn't receive response hence timeing out....");
		if (lExchange.getException() == null) {
			LOG.debug("No exception hence processing the response.....");
			LOG.debug("exchange....." + lExchange);
			LOG.debug("exchange....exception." + lExchange.getException());

			long EndTime = System.currentTimeMillis();
			LOG.info("Request End Time" + EndTime);
			if ((EndTime - lstartTime) > 1000) {
				long quotient = ((EndTime - lstartTime) / 1000);
				long Remainder = ((EndTime - lstartTime) % 1000);
				LOG.info("Time Taken :: " + linterfaceId + ":: " + quotient
						+ "." + Remainder + " Seconds");
			} else {
				LOG.info("Time Taken :: " + linterfaceId + ":: "
						+ (EndTime - lstartTime) + " MilliSeconds");
			}

			lmsg = lExchange.getOut();

			if (ServerConstants.XML.equalsIgnoreCase(lEJBDetails
					.getResponseContentType())) {
				LOG.debug("Response content type is xml");
				loutput = XMLToJsonConverter.XMLToJson(lmsg
						.getMandatoryBody(String.class));
				LOG.debug("After converting response XML to JSON String:"
						+ loutput);
			} else if (ServerConstants.EJB_SERIALIZABLE_OBJECT_TYPE
					.equalsIgnoreCase(lEJBDetails.getResponseContentType())) {
				LOG.debug("Inside EJB_SERIALIZABLE_OBJECT_TYPE");

				@SuppressWarnings("rawtypes")
				Class lc = Class.forName(lEJBDetails
						.getResponseFullyQualifiedClassName());
				LOG.debug("l_c.getClass()-" + lc.getClass());
				LOG.debug("l_c:" + lc);
				@SuppressWarnings("unchecked")
				Object l_responseObj = lmsg.getMandatoryBody(lc);

				loutput = new EJBServicesImpl().getMarshalled(l_responseObj,
						lEJBDetails.getResponseFullyQualifiedClassName());
				LOG.debug("Marshelled XML from response Object - output:"
						+ loutput);

				loutput = new EJBServicesImpl().getXMLToJSON(loutput);
				LOG.debug("After converting XML String to JSON String:"
						+ loutput);
			} else {

				loutput = lmsg.getMandatoryBody(String.class);
				LOG.debug("String type:" + loutput);
			}
		} else {

			LOG.debug("Exception from external system hence will be throwing an exception....");
			LOG.error("callExternalService - Exchange-exception :"
					+ Utils.getStackTrace(lExchange.getException()));
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			EnumMap<EXCEPTION_CODE, String> exceptionMap = exsrvcallexp.FRAME_WORK_EXCEPTIONS;
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
			exsrvcallexp.setMessage(exceptionMap
					.get(EXCEPTION_CODE.APZ_FM_EX_017));
			exsrvcallexp.setPriority("1");

			throw exsrvcallexp;

		}
		LOG.info("callExternalService - output:" + loutput);
		pMessage.getResponseObject().setResponseJson(new JSONObject(loutput));

	}

	public Object[] getQueryString(String pappId, String pinterfaceId,
			String prequestPayload, SpringCamelContext pcontext,
			EJBDetails pejbDetails) {
		Object[] lparamArray = null;
		JSONObject lscreenObj2 = null;
		String lparamValue = null;

		try {
			LOG.debug("Using Spring injection: EJBDetails:" + pejbDetails);
			LOG.debug("p_ejbDetails.getResponseContentType:"
					+ pejbDetails.getResponseContentType());
			LOG.debug("p_ejbDetails.getResponseFullyQualifiedClassName:"
					+ pejbDetails.getResponseFullyQualifiedClassName());

			LOG.debug("getQueryString- p_interfaceId:" + pinterfaceId);
			LOG.debug("getQueryString- p_interfaceId:" + prequestPayload);

			int timeOut = pejbDetails.getTimeOut();
			LOG.debug("getQueryString- timeOut:" + timeOut);
			if (timeOut == 0) {
				LOG.debug("timeOut value is not set by the user hence setting it to 120 seconds....");
				timeOut = 120;
				pejbDetails.setTimeOut(120);
			}

			if (pejbDetails != null) {

				LOG.debug("EJBDetails is not null");
				List<EJBParamDetails> lparamList = pejbDetails
						.getEJB_ParamList();
				LOG.debug("Getting params array list from EJBDetails: Parameter Array list:"
						+ lparamList);

				LOG.debug("Initializing parameter object array of size:"
						+ lparamList.size());
				lparamArray = new Object[lparamList.size()];
				LOG.debug("looping through the JSON array list");

				for (EJBParamDetails fparamlist : lparamList) {

					int lparamArrayIndex = Integer.parseInt(fparamlist
							.getEJB_ParamOrder());
					LOG.debug("Parameter index from l_EJBDetails:"
							+ lparamArrayIndex);

					String[] nodestructure = fparamlist
							.getEJB_NodeStructureName().split("[\\.]");

					for (int i = 0; i < nodestructure.length; i++) {
						LOG.debug("======= Nodestructure[" + i + "]:"
								+ nodestructure[i]);
					}
					int j = 0;

					lscreenObj2 = new JSONObject(prequestPayload);
					lparamValue = null;
					for (; j < nodestructure.length - 1; j++) {
						LOG.debug("nodestructure[" + j + "]:"
								+ nodestructure[j]);
						if (lscreenObj2.get(nodestructure[j]) instanceof JSONObject) {
							LOG.debug("JSONObject Instance");
							LOG.debug("screenObj2.getJSONObject(nodestructure[j]):"
									+ lscreenObj2
											.getJSONObject(nodestructure[j]));
							lscreenObj2 = lscreenObj2
									.getJSONObject(nodestructure[j]);

						} else if (lscreenObj2.get(nodestructure[j]) instanceof String) {
							LOG.debug("String instance- l_paramValue:"
									+ lparamValue);
							lparamValue = lscreenObj2.get(nodestructure[j])
									.toString();
						}
					}
					LOG.debug("l_paramValue:::::::::" + lparamValue);
					if (lparamValue == null || lparamValue.equals(null)
							|| lparamValue.equals("")) {

						LOG.debug("Value of J:" + j);
						LOG.debug("Value screenObj2.getString(nodestructure[j-1]):"
								+ lscreenObj2.getString(nodestructure[j]));
						lparamArray[lparamArrayIndex] = lscreenObj2
								.getString(nodestructure[j]);

						lparamValue = lscreenObj2.getString(nodestructure[j]);
						LOG.debug("After setting l_paramValue - l_paramValue:"
								+ lparamValue);
					} else {

						lparamArray[lparamArrayIndex] = lparamValue;
					}

					LOG.debug("Getting node value from the input json payload:"
							+ lparamValue);
					LOG.debug("Parameter Type from l_EJBDetails:"
							+ fparamlist.getEJB_ParamType());
					LOG.debug("Getting node value from the input json payload:"
							+ lparamValue);

					if (fparamlist.getEJB_ParamType().equalsIgnoreCase(
							ServerConstants.EJB_SERIALIZABLE_OBJECT_TYPE)) {

						String lXML = new EJBServicesImpl()
								.getJSONtoXML(lparamValue);
						LOG.debug("After converting JSON to XML - l_XML:"
								+ lXML);
						LOG.debug("After converting JSON to XML - l_XML:"
								+ lXML);
						LOG.debug("Paramter type is serializable object hence unmarshalling the input xml to java object");
						LOG.debug("f_paramlist.getEJB_QalifiedClassName():"
								+ fparamlist.getEJB_QualifiedClassName());
						Object lrespObject = new EJBServicesImpl()
								.getUnMarshalled(lXML,
										fparamlist.getEJB_QualifiedClassName());
						LOG.debug("After unmarshalling the response object:"
								+ lrespObject);
						LOG.debug("After unmarshalling the response object:"
								+ lrespObject);
						lparamArray[lparamArrayIndex] = lrespObject;
						LOG.debug("Adding the unmarshalled object to the parameter object array");
						LOG.debug("Adding the unmarshalled object to the parameter object array");
					}
				}
				LOG.debug("After adding the parameters to parameter object array:l_paramArray:"
						+ lparamArray);
			}
		} catch (Exception ex) {

			ex.printStackTrace();
			LOG.error("Exception:" + Utils.getStackTrace(ex));
		}

		return lparamArray;

	}*/
}