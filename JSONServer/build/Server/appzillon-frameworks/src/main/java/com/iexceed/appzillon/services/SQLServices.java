package com.iexceed.appzillon.services;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.iexceed.appzillon.exception.Utils;
import org.apache.camel.spring.SpringCamelContext;

import com.iexceed.appzillon.dao.SQLDetails;
import com.iexceed.appzillon.dbutils.DBUtils;
import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.sql.NamedParameterStatement;

/**
 * 
 * @author arthanarisamy
 *
 */
public class SQLServices implements IServicesBean{
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					SQLServices.class.toString());
	protected SQLDetails sqlDetails;

	@Override
	public Object buildRequest(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		
		return null;
	}

	@Override
	public Object callService(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		
		String appId = pMessage.getHeader().getAppId();
		String interfaceId = pMessage.getHeader().getInterfaceId();
		getSQLDetails(interfaceId, appId, pContext);
		JSONArray lResponseJson = new JSONArray();
		NamedParameterStatement lNamedParamStmt = null;
		ResultSet lResultSet = null;
		Connection lConnection = null;
		try {
			buildRequest(pMessage, pRequestPayLoad, pContext);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Creating a new Connection from the Datasource....");
			lConnection = DBUtils.getConnectionFromDataSource(sqlDetails.getDSName());
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Connection obtained -:" + lConnection);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Obtaining a pStatement from connection....");		

			JSONObject lQueryParams = pMessage.getRequestObject().getRequestJson();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Query Parameters from request payload -:" + lQueryParams);
			Iterator<String> lJsonKeys = lQueryParams.keys();
			lNamedParamStmt = new NamedParameterStatement(lConnection, sqlDetails.getQuery(), sqlDetails.getTimeOut());
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Named Parameter Statement created....");
			while(lJsonKeys.hasNext()){
				String lKey = lJsonKeys.next();
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Looping through JSON to set named parameters -:" + lKey);
				lNamedParamStmt.setObject(lKey, lQueryParams.get(lKey));
			}
			Utils.setExtTime(pMessage,"S");
			lResultSet = lNamedParamStmt.executeQuery();
			Utils.setExtTime(pMessage,"E");
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " QueryExecuted and ResultSet Obtained....");
			ResultSetMetaData lResultSetMetaData = lResultSet.getMetaData();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Finding out the resultant columns....");
			int lColumnCount = lResultSetMetaData.getColumnCount();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Resultant Column Length -:" + lColumnCount);
			List<String> lColumnNames = new ArrayList<String>();
			for(int i=1; i<= lColumnCount; i++){
				String lColumnName = lResultSetMetaData.getColumnName(i);
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Resultant column name -:" + lColumnName);
				lColumnNames.add(lColumnName);
			}
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " FinalResultant column names -:" + lColumnNames);
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Fetching query result from result set.... " + lResultSet.getFetchSize());
			while(lResultSet.next()){
				JSONObject lRecord = new JSONObject();
				for(int i=0; i<lColumnNames.size();i++){
					lRecord.put(lColumnNames.get(i), lResultSet.getObject(lColumnNames.get(i)));
				}
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Record -:" + lRecord);
				lResponseJson.put(lRecord);
			}
			lResponseJson = (JSONArray) processResponse(pMessage, lResponseJson, pContext);
			if(lResponseJson.length()<=0){
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Exception from external system hence will be throwing an exception....");
				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_038.toString());
				exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_038));
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;	
			}
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Query Response -:" + lResponseJson);
			} catch (AppzillonException ae) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Exception from external system hence will be throwing an exception....");
				LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " AppzillonException -:", ae);
/*				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
				exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
				exsrvcallexp.setPriority("1");*/
				throw ae;
			}catch (SQLException e) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Exception from external system hence will be throwing an exception....");
				LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " SQLException -:", e);
				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
				exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;
			}finally{
				try {
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Closing Statment....");
					if(lNamedParamStmt != null){
						lNamedParamStmt.close();
					}
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Statement Closed....");
					if(lResultSet!=null){
						LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Closing ResultSet....");
						lResultSet.close();
					}
					LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Closed Statement and Resultset....");
					if(lConnection!=null){
						LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Closing Connection....");
						lConnection.close();
						LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Connection closed....");
					}
				} catch (SQLException e) {
					LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "SQLException ", e);
					if(lConnection!=null){
						LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Closing Connection....");
						try {
							lConnection.close();
						} catch (SQLException e1) {
							LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "SQLException -:", e1);
						}
						LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Connection closed....");
					}
				}
			}
			return lResponseJson;
	}
	
	@Override
	public Object processResponse(Message pMessage, Object pResponse,
			SpringCamelContext pContext) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Returning response from processResponse" + pResponse);		
		return pResponse;
	}

	public void getSQLDetails(String pInterfaceID, String pAppId,
			SpringCamelContext pContext) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getSQLDetails ifID:" + pInterfaceID);
		sqlDetails = (SQLDetails) ExternalServicesRouter
				.injectBeanFromSpringContext(pAppId + "_" + pInterfaceID,
						pContext);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "DataSource Name -:" + sqlDetails.getDSName()
				+ "Query String: " + sqlDetails.getQuery() + ", timeOut :"
				+ sqlDetails.getTimeOut());
		int timeOut = sqlDetails.getTimeOut();
		if (timeOut == 0) {
			LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Timeout value not configured will use default timeOut");
			timeOut = Integer.parseInt(PropertyUtils.getPropValue(pAppId, ServerConstants.DEFAULT_TIMEOUT).trim()) ;
		}
		sqlDetails.setTimeOut(timeOut);
	}
}
