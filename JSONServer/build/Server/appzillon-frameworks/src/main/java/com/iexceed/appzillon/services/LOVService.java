package com.iexceed.appzillon.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.camel.spring.SpringCamelContext;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.sql.DebugLevel;
import com.iexceed.appzillon.utils.sql.StatementFactory;

public class LOVService implements IServicesBean{

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    LOVService.class.toString());

    int lTotalnoOfPages = 0;
    int lTotalnoofrecords = 0;
    String lResultColsNames = null;
    String lResultColsDataTypes = null;
    String lCurrentPage = null;
    String lFilterColmnValues = null;
    String lBinVarValues = null;
    ResultSet lRs = null;
    JSONObject lResJson = null;
    JSONObject lRespJSON = null;
    JSONArray lRespJsonArray = null;
    String lRecordsPerPage = null;
    String lElementID = null;
    String lOrderByCol = null;
    String lOrderByType = null;
    Connection lConnection = null;
    PreparedStatement lPreparedStatement = null;
    DataSource dataSource = null;
    DebugLevel debug = DebugLevel.ON;

    @SuppressWarnings("resource")
    public String getVariablesList(Message pMessage) throws ExternalServicesRouterException {
        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside getVariablesList()..");

        try {
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "****************************** getVariablesList *****************************");
            lRespJsonArray = new JSONArray();
            String lAppId = pMessage.getHeader().getAppId();
            String lInterfaceId = pMessage.getHeader().getInterfaceId();
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVService. getVariablesList - pHeaderMap:" + pMessage.getHeader());
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVService. getVariablesList -  pPayload:" + pMessage.getRequestObject().getRequestJson());

            JSONObject lLOVPayLoad = pMessage.getRequestObject().getRequestJson();
            JSONObject lLOVPayLoadobj = (JSONObject) lLOVPayLoad.get(lInterfaceId + "Request");
            /*Below Check is added by Abhishek on 31-10-2014 for Appzillon 3.1 development
             * API to Fetch Static Data - 3.1 - 48
             * Start
             */
            if(lLOVPayLoadobj.has(ServerConstants.LOV_TYPE) && lLOVPayLoadobj.get(ServerConstants.LOV_TYPE).toString().equals(ServerConstants.LOV_TYPE_STATIC) ){
            lRespJSON = new JSONObject();
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Calling function static for static LOV as type:"+lLOVPayLoadobj.get(ServerConstants.LOV_TYPE).toString());
            lRespJSON = getVariablesListStatic(pMessage);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response JSON from getVariablesListStatic"+lRespJSON.toString());
            }
            /*
             * End API to Fetch Static Data - 3.1 - 48
             */
            else {
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getVariablesList - lLOVPayLoad:" + lLOVPayLoad);
            String lLOVReq = lLOVPayLoad.get(lInterfaceId + "Request").toString();
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getVariablesList - lLOVReq:" + lLOVReq);

            String lQueryId = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ID);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lQueryId:" + lQueryId);

            lElementID = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ELEMENT_ID);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lElementID:" + lElementID);

            lResultColsNames = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_RESULT_SET_COLUMN_NAMES);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lResultColsNames : " + lResultColsNames);

            lResultColsDataTypes = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_RESULT_SET_COLUMN_DATA_TYPES);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lResultColsName : " + lResultColsNames);

            lRecordsPerPage = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_RECORDS_PER_PAGE);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lRecordsPerPage:" + lRecordsPerPage);

            lCurrentPage = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_CURRENT_PAGE_NO);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lCurrentPage:" + lCurrentPage);

            lFilterColmnValues = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_FILTER_COLUMNS_VALUES);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lFilterColmnValues:" + lFilterColmnValues);

            lBinVarValues = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_BIND_VARIABLES_VALUES);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lBinVarValues:" + lBinVarValues);

            if (new JSONObject(lLOVReq).has(ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME)) {
                lOrderByCol = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME);
            } else {
                LOG.debug(ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME + " not found");
            }
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lOrderBy_col:" + lOrderByCol);

            if (new JSONObject(lLOVReq).has(ServerConstants.LOV_QUERY_ORDER_BY_TYPE)) {
                lOrderByType = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ORDER_BY_TYPE);
            } else {
                LOG.debug(ServerConstants.LOV_QUERY_ORDER_BY_TYPE + " not found");
            }
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lOrderBy_type:" + lOrderByType);

            JSONObject lCachedLOV = null;
            String lKey = lAppId + lInterfaceId + lQueryId;

            pMessage.getHeader().setServiceType(ServerConstants.APPZDBFETCHLOVREQUEST);
            JSONObject pResqObject = new JSONObject();
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Before putting query id - lQueryId:" + lQueryId);
            pResqObject.put(ServerConstants.LOV_QUERY_ID, lQueryId);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After building request JSON - pResqObject:" + pResqObject);
            pMessage.getRequestObject().setRequestJson(pResqObject);
            DomainStartup.getInstance().processRequest(pMessage);

            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "DomainStartup processRequest LOV Details string JSON - lQuery:" + pMessage.getResponseObject().getResponseJson());

            JSONObject lLovDetails = pMessage.getResponseObject().getResponseJson();

            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lLovDetails : " + lLovDetails);

            if (!lLovDetails.has(ServerConstants.LOV_BIND_VAR_DATA_TYPE)) {
                lLovDetails.put(ServerConstants.LOV_BIND_VAR_DATA_TYPE, "");
            }

            if (!lLovDetails.has(ServerConstants.LOV_BIND_VAR_COLS)) {
                lLovDetails.put(ServerConstants.LOV_BIND_VAR_COLS, "");
            }

            if (!lLovDetails.has(ServerConstants.LOV_FILTER_VAR_COLS)) {
                lLovDetails.put(ServerConstants.LOV_FILTER_VAR_COLS,
                        "");
            }

            if (!lLovDetails.has(ServerConstants.LOV_ORDER_BY_COLS)) {
                lLovDetails.put(ServerConstants.LOV_ORDER_BY_COLS, "");
            }

            if (!lLovDetails.has(ServerConstants.LOV_ORDER_BY_TYPE)) {
                lLovDetails.put(ServerConstants.LOV_ORDER_BY_TYPE, "");
            }

            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getVariablesList - lLovDetails:" + lLovDetails);
            if (!compareBindVariables(lCachedLOV, lLovDetails.getString(ServerConstants.LOV_BIND_VAR_DATA_TYPE), lBinVarValues, lInterfaceId, lAppId, lQueryId)
                    || !compareFilterVariables(lCachedLOV, lLovDetails.getString(ServerConstants.LOV_FILTER_VAR_COLS),
                            lFilterColmnValues, lInterfaceId, lAppId, lQueryId)) {
                InitialContext lCtx = new InitialContext();
                dataSource = (javax.sql.DataSource) lCtx.lookup(lLovDetails.getString(ServerConstants.LOV_DATA_SOURCE));

                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "data source:" + dataSource);
                lConnection = dataSource.getConnection();
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Connection obtained from data Source...." + lConnection);

                String lBuiltQuery = buildFilterCriteriaQuery(
                        lLovDetails.getString(ServerConstants.LOV_FILTER_VAR_COLS),
                        lFilterColmnValues,
                        lLovDetails.getString(ServerConstants.LOV_QUERY_STRING),
                        lLovDetails.getString(ServerConstants.LOV_ORDER_BY_COLS),
                        lLovDetails.getString(ServerConstants.LOV_ORDER_BY_TYPE),
                        lLovDetails.getString(ServerConstants.LOV_QUERY_TYPE));
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lBuiltQuery :::::" + lBuiltQuery);

                if (lLovDetails.getString(ServerConstants.LOV_QUERY_TYPE).equalsIgnoreCase(ServerConstants.LOV_QUERY_TYPE_SQL)) {

                    lPreparedStatement = appendBindVariables(lLovDetails.getString(ServerConstants.LOV_BIND_VAR_DATA_TYPE),
                            lBinVarValues, lBuiltQuery, lConnection, lAppId);

                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "PreparedStatement after appending bind variables:" + lPreparedStatement.toString());
                    if (debug == DebugLevel.ON) {
                        lPreparedStatement = lConnection.prepareStatement(lPreparedStatement.toString());
                        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lConnection.prepareStatement:" + lPreparedStatement.toString());
                    }
                    Utils.setExtTime(pMessage,"S");
                    lRs = lPreparedStatement.executeQuery();

                    Utils.setExtTime(pMessage,"E");
//                    lTotalnoofrecords = lRs.getFetchSize();

                    String[] lRespCols = Utils.split(lResultColsNames, ServerConstants.SEPARATOR_PIPE);
                    String[] lRespColsDataTypes = Utils.split(lResultColsDataTypes, ServerConstants.SEPARATOR_PIPE);
                    while (lRs.next()) {
                        lResJson = new JSONObject();
                        if (lRespColsDataTypes.length == lRespCols.length) {
                            for (int i = 0; i < lRespCols.length; i++) {
                                String fColName = lRespCols[i];
                                lResJson = JSONUtils.putJSonObj(lResJson, fColName, lRs.getObject(fColName));
                            }
                            lTotalnoofrecords++;
                            lRespJsonArray.put(lResJson);
                        } else {
                            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_013.toString());
                            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_013));
                            exsrvcallexp.setPriority("1");
                            lPreparedStatement.close();
                            throw exsrvcallexp;
                        }
                    }
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Closing Prepared statment.....");
                    lPreparedStatement.close();
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Closed Prepared statment.....");
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Total number of records after:" + lTotalnoofrecords);

                } else if (lLovDetails.getString(ServerConstants.LOV_QUERY_TYPE).equalsIgnoreCase(ServerConstants.LOV_QUERY_TYPE_HQL)) {
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Calling appendBindVariablesHQL.....");
                    Utils.setExtTime(pMessage,"S");
                    List<Object[]> lHqllist = appendBindVariablesHQL(
                            lLovDetails.getString(ServerConstants.LOV_BIND_VAR_DATA_TYPE),
                            lBinVarValues,
                            lBuiltQuery,
                            lLovDetails.getString(ServerConstants.LOV_BIND_VAR_COLS));
                    Utils.setExtTime(pMessage,"E");
                    lTotalnoofrecords = lHqllist.size();
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " lTotalnoofrecords - lHqllist size:" + lHqllist.size());
                    lRespJsonArray = getJSonFromHQLList(lHqllist, lResultColsNames, lResultColsDataTypes);
                }

                JSONObject lCachingLOV = new JSONObject();
                lCachingLOV.put(ServerConstants.LOV_BIND_VARIABLES_DATA_TYPE, lLovDetails.getString(ServerConstants.LOV_BIND_VAR_DATA_TYPE));
                lCachingLOV.put(ServerConstants.LOV_BIND_VARIABLES_VALUES, lBinVarValues);
                lCachingLOV.put(ServerConstants.LOV_FILTER_VARIABLES_COLS, lLovDetails.getString(ServerConstants.LOV_FILTER_VAR_COLS));
                lCachingLOV.put(ServerConstants.LOV_FILTER_VARIABLES_VALUES, lFilterColmnValues);
                lCachingLOV.put(ServerConstants.LOV_QUERY_RESULT, lRespJsonArray);
                JSONObject lLOVObject = new JSONObject();
                lLOVObject.put(lKey, lCachingLOV);

                JSONObject lRespJson = getPagedResponse(lRespJsonArray, lTotalnoofrecords, Integer.parseInt(lCurrentPage), Integer.parseInt(lRecordsPerPage));

                lResJson = new JSONObject();
                lResJson.put(ServerConstants.LOV_QUERY_RESULT_NODE_NAME, lRespJson);

                lTotalnoOfPages = calculateTotalNoOfRows(lTotalnoofrecords, Integer.parseInt(lRecordsPerPage));
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "result set - lTotalnoOfPages:" + lTotalnoOfPages);
                lResJson.put(ServerConstants.LOV_QUERY_TOTAL_NO_OF_PAGES, lTotalnoOfPages);
            }
            lRespJSON = new JSONObject();
            lRespJSON.put(lInterfaceId + "Response", lResJson);
        }
        }
        catch (ExternalServicesRouterException servicesex) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Caught in ExternalServicesRouterException block and Throwing ExternalServicesRouterException...");
            throw servicesex;
        } catch (Exception ex) {
            LOG.error("Exception",ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_001.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_001));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } finally {
            try {
                if (lConnection != null) {
                    lConnection.close();
                }
                if (lPreparedStatement != null) {
                    lPreparedStatement.close();
                }
            } catch (Exception ex) {
            	LOG.error("Exception",ex);
            }
        }
        
       // pMessage.getResponseObject().setResponseJson(lRespJSON);
        return lRespJSON.toString();
        
    }
    /*
     * Below method is added by Abhishek on 31-10-2014 as part of Appzillon 3.1 development
     * API to Fetch Static Data - 3.1 - 48
     * Start
     */
    @SuppressWarnings("resource")
	public JSONObject getVariablesListStatic(Message pMessage) throws ExternalServicesRouterException {
    	 LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside getVariablesListStatic()..");
         try {
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "****************************** getVariablesListStatic *****************************");
             lRespJsonArray = new JSONArray();
             String lAppId = pMessage.getHeader().getAppId();
             String lInterfaceId = pMessage.getHeader().getInterfaceId();
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVService. getVariablesList - pHeaderMap:" + pMessage.getHeader());
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVService. getVariablesList -  pPayload:" + pMessage.getRequestObject().getRequestJson());

             JSONObject lLOVPayLoad = pMessage.getRequestObject().getRequestJson();
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getVariablesList - lLOVPayLoad:" + lLOVPayLoad);
             String lLOVReq = lLOVPayLoad.get(lInterfaceId + "Request").toString();
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getVariablesList - lLOVReq:" + lLOVReq);
             JSONObject lLOVReqobj = ((JSONObject)lLOVPayLoad.get(lInterfaceId + "Request")).getJSONObject(ServerConstants.LOV_REQOBJ);
             String lquery = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_QUERY);
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lquery:" + lquery);
             
             String ljndi = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_JNDI);
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ljndi:" + ljndi);
             
             String lbindcolumns = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_BINDCOLUMNS);
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lbindcolumns:" + lbindcolumns );
             
             String lbindvardatatype = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_BIND_VAR_DATA_TYPE);
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lbindvardatatype:" + lbindvardatatype);
          
             lResultColsNames = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_QUERY_RESULT_SET_COLUMN_NAMES);
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lResultColsNames : " + lResultColsNames);

             lResultColsDataTypes = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_QUERY_RESULT_SET_COLUMN_DATA_TYPES);
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lResultColsName : " + lResultColsNames);

             lBinVarValues = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_QUERY_BIND_VARIABLES_VALUES);
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lBinVarValues:" + lBinVarValues);

             if (new JSONObject(lLOVReq).has(ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME)) {
                 lOrderByCol = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME);
             } else {
                 LOG.debug(ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME + " not found");
             }
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lOrderBy_col:" + lOrderByCol);

             if (new JSONObject(lLOVReq).has(ServerConstants.LOV_QUERY_ORDER_BY_TYPE)) {
                 lOrderByType = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ORDER_BY_TYPE);
             } else {
                 LOG.debug(ServerConstants.LOV_QUERY_ORDER_BY_TYPE + " not found");
             }
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lOrderBy_type:" + lOrderByType);

             JSONObject lCachedLOV = null;

             pMessage.getHeader().setServiceType(ServerConstants.APPZDBFETCHLOVREQUEST);
             JSONObject pResqObject = new JSONObject();
             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After building request JSON - pResqObject:" + pResqObject);
             pMessage.getRequestObject().setRequestJson(pResqObject);
             JSONObject lLovDetails = pMessage.getResponseObject().getResponseJson();

             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lLovDetails : " + lLovDetails);

             if (!lLovDetails.has(ServerConstants.LOV_BIND_VAR_DATA_TYPE)) {
                 lLovDetails.put(ServerConstants.LOV_BIND_VAR_DATA_TYPE, lbindvardatatype);
             }

             if (!lLovDetails.has(ServerConstants.LOV_BIND_VAR_COLS)) {
                 lLovDetails.put(ServerConstants.LOV_BIND_VAR_COLS, lbindcolumns);
             }

             if (!lLovDetails.has(ServerConstants.LOV_FILTER_VAR_COLS)) {
                 lLovDetails.put(ServerConstants.LOV_FILTER_VAR_COLS,
                         "");
             }

             if (!lLovDetails.has(ServerConstants.LOV_ORDER_BY_COLS)) {
                 lLovDetails.put(ServerConstants.LOV_ORDER_BY_COLS, "");
             }

             if (!lLovDetails.has(ServerConstants.LOV_ORDER_BY_TYPE)) {
                 lLovDetails.put(ServerConstants.LOV_ORDER_BY_TYPE, "");
             }

             LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getVariablesList - lLovDetails:" + lLovDetails);
             if (!compareBindVariables(lCachedLOV, lLovDetails.getString(ServerConstants.LOV_BIND_VAR_DATA_TYPE), lBinVarValues, lInterfaceId, lAppId, null)
                     || !compareFilterVariables(lCachedLOV, lLovDetails.getString(ServerConstants.LOV_FILTER_VAR_COLS),
                             lFilterColmnValues, lInterfaceId, lAppId, null)) {
                 InitialContext lCtx = new InitialContext();
                 dataSource = (javax.sql.DataSource) lCtx.lookup(ljndi);

                 LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "data source:" + dataSource);
                 lConnection = dataSource.getConnection();
                 LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Connection obtained from data Source...." + lConnection);
                   
                     lPreparedStatement = appendBindVariables(lLovDetails.getString(ServerConstants.LOV_BIND_VAR_DATA_TYPE),
                             lBinVarValues, lquery, lConnection, lAppId);

                     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "PreparedStatement after appending bind variables:" + lPreparedStatement.toString());
                     if (debug == DebugLevel.ON) {
                         lPreparedStatement = lConnection.prepareStatement(lPreparedStatement.toString());
                         LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lConnection.prepareStatement:" + lPreparedStatement.toString());
                     }
                     Utils.setExtTime(pMessage,"S");
                     lRs = lPreparedStatement.executeQuery();
                     Utils.setExtTime(pMessage,"E");

                     String[] lRespCols = Utils.split(lResultColsNames, ServerConstants.SEPARATOR_PIPE);
                     String[] lRespColsDataTypes = Utils.split(lResultColsDataTypes, ServerConstants.SEPARATOR_PIPE);
                     while (lRs.next()) {
                         lResJson = new JSONObject();
                         if (lRespColsDataTypes.length == lRespCols.length) {
                             for (int i = 0; i < lRespCols.length; i++) {
                                 String fColName = lRespCols[i];
                                 lResJson = JSONUtils.putJSonObj(lResJson, fColName, lRs.getObject(fColName));
                             }
                             lTotalnoofrecords++;
                             lRespJsonArray.put(lResJson);
                         } else {
                             ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                             exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_013.toString());
                             exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_013));
                             exsrvcallexp.setPriority("1");
                             lPreparedStatement.close();
                             throw exsrvcallexp;
                         }
                     }
                     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Closing Prepared statment.....");
                     lPreparedStatement.close();
                     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Closed Prepared statment.....");
                     LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Total number of records after:" + lTotalnoofrecords);


                 JSONObject lRespJsonobj = getPagedResponse(lRespJsonArray, lTotalnoofrecords, 1 , lTotalnoofrecords);
             
             lRespJSON = new JSONObject();
             lRespJSON.put(lInterfaceId + "Response", lRespJsonobj);
             }
         }
             catch (ExternalServicesRouterException servicesex) {
                 LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Caught in ExternalServicesRouterException block and Throwing ExternalServicesRouterException...");
                 throw servicesex;
             } catch (Exception ex) {
                 LOG.error("Exception",ex);
                 ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                 exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_001.toString());
                 exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_001));
                 exsrvcallexp.setPriority("1");
                 throw exsrvcallexp;
             } finally {
                 try {
                     if (lConnection != null) {
                         lConnection.close();
                     }
                     if (lPreparedStatement != null) {
                         lPreparedStatement.close();
                     }
                 } catch (Exception ex) {
                 	LOG.error("Exception",ex);
                 }
             }
             
             return lRespJSON;
    }
    /*
     * End API to Fetch Static Data - 3.1 - 48
     */
    public JSONObject getPagedResponse(JSONArray pCompleteresponseJsonArr,
            int pNoOfRows, int pPageno, int pRecordsPerPage) {
        JSONObject lRespJsonObject = null;
        JSONArray lRespJsonArray = null;
        try {
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getPagedResponse - pNoOfRows:" + pNoOfRows);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getPagedResponse - pPageno:" + pPageno);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getPagedResponse - pRecordsPerPage:" + pRecordsPerPage);

            lRespJsonArray = new JSONArray();

            int lLoopStartIndex = 0;
            int lLoopEndIndex = 0;
            if ((pRecordsPerPage != 0 && pPageno != 0)) {
                lLoopStartIndex = pRecordsPerPage * (pPageno - 1);

                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getPagedResponse - lLoopStartIndex:"
                        + lLoopStartIndex);
                lLoopEndIndex = pRecordsPerPage * pPageno;
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getPagedResponse - lLoopEndIndex:" + lLoopEndIndex);
            } else {
            	
                pRecordsPerPage = pCompleteresponseJsonArr
                        .length();
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "current page no is sent empty for Dynamic LOV, hence setting the end index to the max response array lenght....");
                lLoopEndIndex = pCompleteresponseJsonArr.length();
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "getPagedResponse - lLoopEndIndex for Dynamic LOV:"
                        + lLoopEndIndex);
            }

            int lTotalnoOfPages = calculateTotalNoOfRows(pNoOfRows,
                    pRecordsPerPage);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Total no of pages - lTotalnoOfPages:" + lTotalnoOfPages);

            if (pPageno > lTotalnoOfPages) {
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Total number of page is less than the requested page no.");
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_005.toString());
                exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_005));
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;

            }
            if (pCompleteresponseJsonArr.length() < lLoopEndIndex) {
                lLoopEndIndex = pCompleteresponseJsonArr.length();
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lLoopEndIndex***********" + lLoopEndIndex);
            }

            for (int i = lLoopStartIndex; i < lLoopEndIndex; i++) {
                lRespJsonObject = new JSONObject();
                lRespJsonObject = (JSONObject) pCompleteresponseJsonArr.get(i);
                lRespJsonArray.put(lRespJsonObject);
            }
            lRespJsonObject = new JSONObject();
            lRespJsonObject.put(
                    ServerConstants.LOV_QUERY_RESULT_NODE_ARRAY_NAME,
                    lRespJsonArray);

        } catch (JSONException jsonex) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSON EXCEPTION ******");
            LOG.error("JSONException",jsonex);

            lRespJsonObject = new JSONObject();
            try {
                lRespJsonObject.put(
                        ServerConstants.LOV_QUERY_RESULT_NODE_ARRAY_NAME,
                        lRespJsonArray);
            } catch (JSONException e) {
                LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Error while adding json arry in exception block:"
                        ,e);
            }
        } catch (Exception ex) {
            LOG.debug(Utils.getStackTrace(ex));

            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_006.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_006));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;

        }
        return lRespJsonObject;
    }

    public String buildFilterCriteriaQuery(String pFilterColmns,
            String pFilterColmnsValue, String pActualQuery, String pOrderByCol,
            String pOrderByType, String pQueryType) {
        StringBuilder lFinalQuery = null;
        String[] lColmnsname = null;
        String[] lColmnsValue = null;
        try {
            lFinalQuery = new StringBuilder();
            LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "buildFilterCriteriaQuery.pFilterColmnsValue :"
                    + pFilterColmnsValue);
            lColmnsname = Utils.split(pFilterColmns,
                    ServerConstants.SEPARATOR_PIPE);
            lColmnsValue = Utils.split(pFilterColmnsValue,
                    ServerConstants.SEPARATOR_PIPE);
            LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "buildFilterCriteriaQuery.No of columns"
                    + lColmnsname.length);
            LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "buildFilterCriteriaQuery.No of values"
                    + lColmnsValue.length);
            if (lColmnsname.length == lColmnsValue.length
                    && (lColmnsname.length != 0 && lColmnsValue.length != 0)) {
                if (!pQueryType
                        .equalsIgnoreCase(ServerConstants.LOV_QUERY_TYPE_HQL)) {
                    lFinalQuery
                            .append(ServerConstants.LOV_QUERY_OUTER_QUERY);
                }

                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Query after appending Outer query:"
                        + lFinalQuery.toString());
                lFinalQuery.append(pActualQuery);
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Query after appending the actual query:"
                        + lFinalQuery.toString());
                boolean isWhere = false;
                boolean allEmpty = true;
                for (int i = 0; i < lColmnsname.length; i++) {

                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "buildFilterCriteriaQuery - i:" + i
                            + ", lColmnsname[i]:" + lColmnsname[i]
                            + ", lColmnsValue[i]:" + lColmnsValue[i]
                            + ", lColmnsValue[i].length:"
                            + lColmnsValue[i].length());
                    if (lColmnsValue[i] != null && lColmnsValue[i].length() > 0) {
                        allEmpty = false;
                        if (!isWhere) {
                            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Adding where clause.....");
                            if (!pQueryType
                                    .equalsIgnoreCase(ServerConstants.LOV_QUERY_TYPE_HQL)) {
                                lFinalQuery
                                        .append(ServerConstants.LOV_QUERY_OUTER_QUERY_WHERE_CLAUSE);
                                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Query after appending where clause:"
                                        + lFinalQuery.toString());
                            } else {
                                if (lFinalQuery.toString().toLowerCase()
                                        .contains(ServerConstants.WHERE)) {
                                    lFinalQuery.append(ServerConstants.AND);
                                } else {
                                    lFinalQuery.append(ServerConstants.WHERE);
                                }

                                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "**** HQL Query after appending where clause:"
                                        + lFinalQuery.toString());
                            }

                            isWhere = true;
                        }
                        /**
                         * Below changes are made by Vinod as part of 
                         * Querying case sensitive and case insensitive values.
                         * Appzillon 3.1 - 62 -- Start
                         */
                       boolean caseSenstivityReq=false; 
                     if(lColmnsValue[i].endsWith("\"")&lColmnsValue[i].startsWith("\"")){
                    	 LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" caseSenstivityReq for Column "+lColmnsname[i]);
                    	 caseSenstivityReq=true;
                    
                     }
                        if(caseSenstivityReq)
                        	 lFinalQuery.append(lColmnsname[i]);
                     else{
                        	 lFinalQuery.append("upper("+lColmnsname[i]+")");
                        }
                        if (lColmnsValue[i].contains("%")) {
                            LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appending IS LIKE as % found");
                            lFinalQuery.append(" LIKE '");
                        } else {
                            LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appending =  as % not found");
                            lFinalQuery.append(" = '");
                        }
                        if(caseSenstivityReq){
                        	String valueInCot=lColmnsValue[i].substring(1, lColmnsValue[i].length()-1);
                        	LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "value in cots :"+valueInCot );
                        	 lFinalQuery.append(valueInCot);
                        }else{
                        lFinalQuery.append(lColmnsValue[i].toUpperCase());
                        }
                        /** Appzillon 3.1 - 62 -- END */
                        lFinalQuery.append("'");
                        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Checking based on the column values instead of column names....");
                        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lColmnsValue.length > 1 && lColmnsValue.length-1 != i && (lColmnsValue[i+1] != null && lColmnsValue[i+1].length() > 0):"
                                + (lColmnsValue.length > 1
                                && lColmnsValue.length - 1 != i && (lColmnsValue[i + 1] != null && lColmnsValue[i + 1]
                                .length() > 0)));
                        if (lColmnsValue.length > 1
                                && lColmnsValue.length - 1 != i
                                && (lColmnsValue[i + 1] != null && lColmnsValue[i + 1]
                                .length() > 0)) {
                            lFinalQuery.append(" and ");

                        }
                    }

                }
                if (allEmpty) {
                    LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "allEmpty found  true all string are empty String");
                    if (!pQueryType
                            .equalsIgnoreCase(ServerConstants.LOV_QUERY_TYPE_HQL)) {
                        lFinalQuery
                                .append(ServerConstants.LOV_QUERY_OUTER_QUERY_END);
                    }
                }

            } else {
                lFinalQuery.append(pActualQuery);
            }
            if (pOrderByCol != null && pOrderByCol.length() > 0) {
                lFinalQuery
                        .append(ServerConstants.LOV_QUERY_OUTER_QUERY_ORDER_BY_CLAUSE);
                lFinalQuery.append(pOrderByCol);
                lFinalQuery.append(" ");
                lFinalQuery.append(pOrderByType);
            }

            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Query after appending filter criteria:"
                    + lFinalQuery.toString());

            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Query finally returned:" + lFinalQuery.toString());

        } catch (Exception ex) {
            LOG.debug(Utils.getStackTrace(ex));

            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_007.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_007));
            exsrvcallexp.setPriority("1");

            throw exsrvcallexp;

        }
        return lFinalQuery.toString();
        }

    public PreparedStatement appendBindVariables(String pBindVariableDataType,
            String pBindVariableValues, PreparedStatement pPreStatement) {
        PreparedStatement lPreparedStatement = null;
        String[] lBindVarDataTypesArr = null;
        String[] lBindVarValuesArr = null;
        try {
            lPreparedStatement = pPreStatement;
            lBindVarDataTypesArr = Utils.split(pBindVariableDataType,
                    ServerConstants.SEPARATOR_PIPE);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lBindVarDataTypesArr length : "
                    + lBindVarDataTypesArr.length);
            lBindVarValuesArr = Utils.split(pBindVariableValues,
                    ServerConstants.SEPARATOR_PIPE);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lBindVarValuesArr length : " + lBindVarValuesArr.length);

            if (lBindVarDataTypesArr.length == lBindVarValuesArr.length) {
                for (int i = 1; i <= lBindVarDataTypesArr.length; i++) {
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "i:" + i + " , *** lBindVarValuesArr[i-1] : "
                            + lBindVarValuesArr[i - 1]);
                    String lBindValue = lBindVarValuesArr[i - 1];
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "*** lBindValue : " + lBindValue);
                    lPreparedStatement.setObject(i, lBindVarValuesArr[i - 1]);
                }
            } else {
                lPreparedStatement = pPreStatement;
            }
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "********lPreparedStatement : "
                    + pPreStatement.toString());
        } catch (Exception ex) {
            LOG.error("Exception",ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_008.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_008));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        return lPreparedStatement;

    }

    public PreparedStatement appendBindVariables(String pBindVariableDataType,
            String pBindVariableValues, String pQuery, Connection pConnection, String pAppId) {
        PreparedStatement lPreparedStatement = null;
        String[] lBindVarDataTypesArr = null;
        String[] lBindVarValuesArr = null;
        DebugLevel debug = DebugLevel.ON;
        try {
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendBindVariables - pQuery:" + pQuery);

            if (debug == DebugLevel.ON) {
                lPreparedStatement = StatementFactory.getStatement(pConnection,
                        pQuery, debug);
            } else {
                lPreparedStatement = pConnection.prepareStatement(pQuery);
            }

            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendBindVariables - lPreparedStatement:"
                    + lPreparedStatement.toString());
            lBindVarDataTypesArr = Utils.split(pBindVariableDataType,
                    ServerConstants.SEPARATOR_PIPE);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lBindVarDataTypesArr.length:"
                    + lBindVarDataTypesArr.length);
            lBindVarValuesArr = Utils.split(pBindVariableValues,
                    ServerConstants.SEPARATOR_PIPE);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lBindVarValuesArr.length:" + lBindVarValuesArr.length);

            if (lBindVarDataTypesArr.length == lBindVarValuesArr.length) {
                for (int i = 1; i <= lBindVarDataTypesArr.length; i++) {
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "i:" + i + " , **** lBindVarDataTypesArr[i-1]:"
                            + lBindVarDataTypesArr[i - 1]);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "i:" + i + " , **** lBindVarValuesArr[i-1]:"
                            + lBindVarValuesArr[i - 1]);

                    String lBindDataType = lBindVarDataTypesArr[i - 1];
                    String lBindValue = lBindVarValuesArr[i - 1];
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "**** lBindValue:" + lBindValue);
                    if ("string".equalsIgnoreCase(lBindDataType)) {
                        lPreparedStatement.setString(i, lBindValue);
                        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lPreparedStatement set String....");
                    } else if ("int".equalsIgnoreCase(lBindDataType)) {
                        lPreparedStatement.setInt(i,
                                Integer.parseInt(lBindValue));
                        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lPreparedStatement set Int....");
                    } else if ("date".equalsIgnoreCase(lBindDataType)) {
                        String dateFormat = PropertyUtils.getPropValue(pAppId, "datePatternForLogFiles").toString()
                                .trim();
                        SimpleDateFormat formatter = new SimpleDateFormat(
                                dateFormat);
                        Date lBindDate = formatter.parse(lBindValue);
                        lPreparedStatement
                                .setDate(i, (java.sql.Date) lBindDate);
                        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "lPreparedStatement set Date....");
                    }

                }
            }
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "**********lPreparedStatement:"
                    + lPreparedStatement.toString());
        } catch (Exception ex) {
        	LOG.error("Exception",ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_008.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_008));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        return lPreparedStatement;

    }

    @SuppressWarnings("unchecked")
    public List<Object[]> appendBindVariablesHQL(String pBindVariableDataType,
            String pBindVariableValues, String pQuery,
            String pBindVariablesColNames) {
        String[] lBindVarDataTypesArr = null;
        String[] lBindVarValuesArr = null;
        String[] lBindVarColNamesArr = null;
        List<Object[]> lResults = null;

        try {
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendBindVariablesHQL - pQuery:" + pQuery);
            lBindVarDataTypesArr = Utils.split(pBindVariableDataType,
                    ServerConstants.SEPARATOR_PIPE);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendBindVariablesHQL - lBindVarDataTypesArr.length:"
                    + lBindVarDataTypesArr.length);
            lBindVarValuesArr = Utils.split(pBindVariableValues,
                    ServerConstants.SEPARATOR_PIPE);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendBindVariablesHQL - lBindVarValuesArr.length:"
                    + lBindVarValuesArr.length);
            lBindVarColNamesArr = Utils.split(pBindVariablesColNames,
                    ServerConstants.SEPARATOR_PIPE);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendBindVariablesHQL - l_bindVarColNamesArr.length:"
                    + lBindVarColNamesArr.length);

            if ((lBindVarDataTypesArr.length == lBindVarValuesArr.length)
                    && (lBindVarColNamesArr.length == lBindVarValuesArr.length)) {
                Configuration cfg = new Configuration()
                        .configure(ServerConstants.HIBERNATE_CFG_XML);
                @SuppressWarnings("deprecation")
                SessionFactory sessionFactory = cfg.buildSessionFactory();
                Session session = sessionFactory.openSession();
                session.getTransaction().begin();
                Query lQuery = session.createQuery(pQuery);
                for (int i = 0; i < lBindVarColNamesArr.length; i++) {
                    lQuery.setParameter(lBindVarColNamesArr[i],
                            lBindVarValuesArr[i]);
                }
                lResults = (List<Object[]>) lQuery.list();
                session.getTransaction().commit();
                session.close();

                sessionFactory.close();
            } else {

                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_015.toString());
                exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_015));
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            }

        } catch (Exception ex) {
            LOG.debug(Utils.getStackTrace(ex));

            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_016.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_016));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        return lResults;

    }

    public JSONArray getJSonFromHQLList(List<Object[]> pResult,
            String pResColsName, String pRespColsDataTypes) {
        JSONObject lResJson = null;
        JSONArray lRespJsonArray = new JSONArray();
        String[] lRespColNames = Utils.split(pResColsName,
                ServerConstants.SEPARATOR_PIPE);
        String[] lRespColDataTypes = Utils.split(pRespColsDataTypes,
                ServerConstants.SEPARATOR_PIPE);

        for (Object[] fObject : pResult) {
            lResJson = new JSONObject();
            if (fObject.length == lRespColNames.length
                    && fObject.length == lRespColDataTypes.length) {
                for (int i = 0; i < fObject.length; i++) {
                    String fColName = lRespColNames[i];
                    String fColValue = fObject[i].toString();
                    lResJson = JSONUtils.putJSonObj(lResJson, fColName,
                            fColValue);
                }
                lRespJsonArray.put(lResJson);
            } else {
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_013.toString());
                exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_013));
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            }

        }
        return lRespJsonArray;

    }

    public int calculateTotalNoOfRows(int pNoOfRows, int pRecordsPerPage) {
        int lTotalnoOfPages = 1;
        try {
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "calculateTotalNoOfRows - pNoOfRows:" + pNoOfRows);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "calculateTotalNoOfRows - pRecordsPerPage:"
                    + pRecordsPerPage);
            if (!"0".equals(pNoOfRows) && !"0".equals(pRecordsPerPage)) {
                int lModValue = pNoOfRows% pRecordsPerPage;

                if (lModValue > 0) {
                    lTotalnoOfPages = (pNoOfRows / pRecordsPerPage) + 1;
                } else if (lModValue < 0) {
                    lTotalnoOfPages = 1;
                } else {
                    lTotalnoOfPages = pNoOfRows / pRecordsPerPage;
                }
            }

        } catch (Exception ex) {
        	LOG.error("Exception",ex);
        }
        return lTotalnoOfPages;
    }

    public boolean compareBindVariables(JSONObject pCachedJson,
            String pBindVarDataType, String pBindVarVal, String pInterfaceId,
            String pAppId, String pQueryId) {
        boolean lExists = false;
        try {
            if (pCachedJson != null) {
                String lKey = pAppId + pInterfaceId + pQueryId;
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSON Node names:" + lKey);
                JSONObject lLOVRequest = (JSONObject) pCachedJson.get(lKey);
                if (lLOVRequest != null) {
                    String lBindVars = lLOVRequest
                            .getString("bindvariablesDataTypes");
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVService.lBindVars:" + lBindVars);
                    String lBindValues = lLOVRequest
                            .getString("bindvariablesValues");
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVService.lBindValues:" + lBindValues);
                    if (lBindVars != null && lBindVars.length() > 1) {
                        if (pBindVarDataType.equalsIgnoreCase(lBindVars)
                                && lBindValues.equals(pBindVarVal)) {
                            lExists = true;

                        }
                    }

                }

            }

            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVService.compareBindVariables - lExists:" + lExists);
        } catch (JSONException ex) {
        	LOG.error("JSONException",ex);
        }
        return lExists;
    }

    public boolean compareFilterVariables(JSONObject pCachedJson,
            String pFilterVarCols, String pFilterVarVal, String pInterfaceId,
            String pAppId, String pQueryId) {
        boolean lExists = false;
        try {
            if (pCachedJson != null) {
                String lKey = pAppId + pInterfaceId + pQueryId;
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSON Node name :" + lKey);
                JSONObject lLOVRequest = (JSONObject) pCachedJson.get(lKey);
                if (lLOVRequest != null) {
                    String lFilterVars = lLOVRequest
                            .getString("filtervariableCols");
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVService.lFilterVars:" + lFilterVars);
                    String lFilterValues = lLOVRequest
                            .getString("filtervariablesValues");
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVService.lFilterValues:" + lFilterValues);
                    if (lFilterVars != null || lFilterValues.length() > 1) {
                        if (pFilterVarCols.equalsIgnoreCase(lFilterVars)
                                && lFilterValues.equals(pFilterVarVal)) {
                            lExists = true;
                        }
                    }

                }

            }

            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVService.compareFilterVariables - lExists:" + lExists);
        } catch (JSONException ex) {
        	LOG.error("JSONException",ex);
        }

        return lExists;
    }

    @SuppressWarnings("deprecation")
    public String clearHibernateCache() {
        JSONObject lResponse = null;

        try {
            lResponse = new JSONObject();
            Configuration cfg = new Configuration()
                    .configure(ServerConstants.HIBERNATE_CFG_XML);
            SessionFactory sessionFactory = cfg.buildSessionFactory();
            Map<String, ClassMetadata> classesMetadata = sessionFactory
                    .getAllClassMetadata();
            for (String entityName : classesMetadata.keySet()) {
                LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Evicting Entity from 2nd level cache : " + entityName);
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Evicting Entity from 2nd level cache: " + entityName);
                sessionFactory.evictEntity(entityName);
                lResponse.put(entityName, "Cleared from cache memory....");
            }

            sessionFactory.close();
        } catch (Exception pEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Exception - clearHibernateCache:"
                   ,pEx);
        }

        return lResponse.toString();

    }

    @Override
	public Object buildRequest(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		LOG.debug("[FrameWorksServices] Inside LOV buildRequest");
		return pRequestPayLoad;
	}

	@Override
	public Object processResponse(Message pMessage, Object pResponse,
			SpringCamelContext pContext) {
		LOG.debug("[FrameWorksServices] Inside LOV processResponse");
		return pResponse;
	}

	@Override
	public Object callService(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		JSONObject requestJson=(JSONObject) buildRequest(pMessage, pRequestPayLoad, pContext);
		pMessage.getRequestObject().setRequestJson(requestJson);
		String output= getVariablesList(pMessage);
		 output=(String) processResponse(pMessage, output, pContext);
		return new JSONObject(output);
	}
}
