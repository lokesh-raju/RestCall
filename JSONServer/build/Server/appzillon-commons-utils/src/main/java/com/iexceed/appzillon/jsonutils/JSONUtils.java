package com.iexceed.appzillon.jsonutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.utils.ServerConstants;

public class JSONUtils {
	private static final com.iexceed.appzillon.logging.Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, JSONUtils.class.toString());

    public static String extractJsonString(String inputJson, String nodeName) {

        String outputString = null;

        try {
            JSONObject jo = new JSONObject(inputJson).getJSONObject(nodeName);
            outputString = jo.toString();
        } catch (JSONException e) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, e);
        }

        return outputString;
    }

    public static String getJsonValueFromKey(String inputJson, String key) {

        String outputString = null;

        try {
            outputString = new JSONObject(inputJson).getString(key);
        } catch (JSONException e) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, e);
        }
        return outputString;

    }
    
    public static String getJsonValueFromObject(JSONObject inputJson, String key) {
        String outputString = null;
        try {
        	if(inputJson.has(key)) {
        		outputString = inputJson.getString(key);
        	}
        } catch (JSONException e) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, e);
        }
        return outputString;

    }

    public static Map<String, String> getJsonHashMap(String inputJson) {
        Map<String, String> outmap = new HashMap<String, String>();
        try {
            JSONObject jsonobj = new JSONObject(inputJson);
            Iterator<?> jsonnames = jsonobj.keys();
            while (jsonnames.hasNext()) {
                String name = (String) jsonnames.next();
                String value = jsonobj.get(name).toString();
                if (value == null) {
                    value = "";
                }
                outmap.put(name, value);
            }
        } catch (JSONException jsonex) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonex);
        } catch (Exception e) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.EXCEPTION, e);

        }
        return outmap;
    }

    public static JSONObject getJsonStringFromMap(Map<String, String> inputMap) {
        JSONObject lJsonobject = null;

        lJsonobject = new JSONObject(inputMap);

        return lJsonobject;
    }

    public static String getColStringFromBody(Header pHeader,
            String pupdate, String pname) {

        String scolstring1 = "";
        try {
            JSONObject bodyobject = new JSONObject(pupdate);
            Iterator<?> ite = bodyobject.keys();
            String sname = "";
            while (ite.hasNext()) {
                sname = ite.next().toString();
                if (sname.equals(pname)) {
                    scolstring1 = bodyobject.get(sname).toString();
                }
            }

        } catch (JSONException jsone) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION,jsone);
        }
        return scolstring1;

    }

    public static String getOutputString(JSONArray recarray,
            Map<String, String> headerMap, String pname) {

        JSONObject bodyobj = new JSONObject();
        JSONObject outputobj = new JSONObject();

        try {
            bodyobj.put(pname, recarray);
            outputobj.put(ServerConstants.MESSAGE_HEADER, headerMap);
            outputobj.put(ServerConstants.MESSAGE_BODY, bodyobj);
        } catch (JSONException jsone) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION,jsone);        
        }
        return outputobj.toString();
    }

    /*
     * concatArray method returns JsonArray by concatenating 
     * list of JsonArrays in arrs array
     * Added by Samy on 11-07-2013
     * Reviewed by Siddarth
     */
    public static JSONArray concatArray(JSONArray... arrs) {
        JSONArray result = new JSONArray();
        try {
            for (JSONArray arr : arrs) {
                for (int i = 0; i < arr.length(); i++) {
                    result.put(arr.get(i));
                }
            }
        } catch (JSONException jsonex) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION,jsonex);
        }
        return result;
    }

    /*
     * putJSonObj method helps in converting 
     * a json array to json object added to a node
     * Added by Samy on 11-07-2013
     * Reviewed by Siddarth
     */
    public static JSONObject putJSonObj(JSONObject responseObj, String nodeName, JSONArray inputArr) {
        try {
            responseObj.put(nodeName, inputArr);
        } catch (JSONException jsonex) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION,jsonex);
        	}
        return responseObj;
    }

    /*
     * putJSonObj method helps in adding
     * a nodevalue with nodeKey to json object
     * Added by Samy on 30-07-2013
     * Reviewed by Siddarth
     */
    public static JSONObject putJSonObj(JSONObject responseObj, String nodeName, String nodeValue) {
        try {
            responseObj.put(nodeName, nodeValue);

        } catch (JSONException jsonex) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION,jsonex);
        }
        return responseObj;
    }
    /*
     * putJSonObj method helps in adding
     * a nodevalue with nodeKey to json object
     * Added by Samy on 10-10-2013
     * Reviewed by Siddarth
     */

    public static JSONObject putJSonObj(JSONObject responseObj, String nodeName, Object nodeValue) {
        try {
            responseObj.put(nodeName, nodeValue);

        } catch (JSONException jsonex) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION,jsonex);
        }
        return responseObj;
    }
    /*
     * getOutputString method helps in converting 
     * a json array to json object added to a node
     * Added by Samy on 11-07-2013
     * Reviewed by Siddarth
     */

    public static String getOutputString(JSONObject bodyJson,
            Map<String, String> headerMap) {
        JSONObject outputobj = new JSONObject();
        try {
            outputobj.put(ServerConstants.MESSAGE_HEADER, headerMap);
            outputobj.put(ServerConstants.MESSAGE_BODY, bodyJson);
        } catch (JSONException jsone) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION,jsone);
        }
        return outputobj.toString();
    }

    /*
     * getJsonArrayFromString method helps in converting 
     * a input JSON String to json Array
     * Added by Samy on 23-07-2013
     * Reviewed by Siddarth
     */
    public static JSONArray getJsonArrayFromString(String pInputJsonString) {
        JSONArray lRespJsonArray = null;
        try {
            lRespJsonArray = new JSONArray(pInputJsonString);

        } catch (JSONException jsone) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION,jsone);
        	}
        return lRespJsonArray;
    }

    public static JSONObject stringtoJsonObj(String inputJson) {
        JSONObject json = null;
        try {
            json = new JSONObject(inputJson);
        } catch (JSONException jsonex) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION,jsonex);
        }
        return json;
    }

    public static String[] jsonNodestoStringArr(JSONArray nodeNames) {
        String[] nodes = null;
        try {
            nodes = new String[nodeNames.length()];
            for (int i = 0; i < nodeNames.length(); i++) {
                nodes[i] = nodeNames.get(i).toString();
            }
        } catch (JSONException jsonex) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION,jsonex);
        }
        return nodes;
    }

    public static String getKeyValue(String tempJSON, String node) {
        String lOutputString = "";
        try {
            JSONObject obj = new JSONObject(tempJSON);
            Iterator<?> it = obj.keys();
            while (it.hasNext()) {
                String key = (String) it.next();

                Object nodeValue = obj.get(key);
                if (node.equals(key)) {
                    lOutputString = nodeValue.toString();
                } else if (nodeValue instanceof JSONObject) {
                    lOutputString = getKeyValue(nodeValue.toString(), node);
                } else if (nodeValue instanceof JSONArray) {
                    JSONArray array = (JSONArray) nodeValue;
                    int j=-1;
                    int i =0;
                 
                    while (i < array.length()) {
                    	Object object=array.get(i);
                    	if(object instanceof JSONObject){
                         JSONObject ob = array.getJSONObject(i);
                          if (Utils.isNullOrEmpty(lOutputString)) {
                            lOutputString = getKeyValue(ob.toString(), node);
                          } else {
                        	  String apend=getKeyValue(ob.toString(), node);
                        	  if (Utils.isNotNullOrEmpty(apend)){
                        		  lOutputString = lOutputString + "," + apend;
                        	  }
                              
                          }
                       
                        }else if( object instanceof JSONArray){
                        	array=(JSONArray) object;
                        	j=i;
                        	i=0;
                        	continue;
                        }
                    	 i++;
                    	 if(i==array.length()&j!=-1){
                    	 array=(JSONArray) nodeValue;
                    	 j++;
                    	 i=j;
                    	 }
                    }
                }
            }

        } catch (JSONException e) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, e);
        }
        lOutputString=  eliminateDuplicate(lOutputString);
        return lOutputString;
    }
    public static String eliminateDuplicate(String linputString) {
    	String loutputString = linputString;
    	if(!linputString.contains("{")&&!linputString.contains("[")&&linputString.contains(",")){
    	String[] a =	linputString.split(",");
    	 ArrayList<String> out=new ArrayList<String>(); 
    		  int i=0;
    		  while(i<a.length){
    			 if(!out.contains(a[i])){
    				 out.add(a[i]);
    			 }
    			 i++;
    		  }
    		  i=0;
    		  loutputString="";
    		  while(i<out.size()-1){
    			  loutputString+=out.get(i)+",";
     			 i++;
     		  }
    		  loutputString+=out.get(i);
    	}
		return loutputString;
	}
    /*below method written by ripu for 2.2 changes
     * objective : convert json to Map
     * date : 28-05-2014
     */ 
    public static Map<String, Object> getHashMapFromJson(JSONObject pinputJson) throws JSONException {
        Map<String, Object> outmap = new HashMap<String, Object>();
            Iterator<?> jsonnames = pinputJson.keys();
            while (jsonnames.hasNext()) {
                String name = (String)jsonnames.next();
                String value =  pinputJson.get(name).toString();
                if (value == null) {
                    value = "";
                }
                outmap.put(name, value);
            }
        return outmap;
    }
    
    public static Map<String, Object> buildParamMap(JSONObject pinputJson) throws JSONException{
		Map<String, Object> outmap = new HashMap<String, Object>();
		Map<String, Object> localmap = new HashMap<String, Object>();
		JSONObject jsonObject = new JSONObject();
		Iterator<?> jsonnames = pinputJson.keys();
		while (jsonnames.hasNext()) {
			String key = (String)jsonnames.next();
	       	Object nodeValue = pinputJson.get(key);
	       	if(nodeValue instanceof JSONObject){
	       		jsonObject = (JSONObject) nodeValue;
	       		localmap = getHashMapFromJson(jsonObject);
	       		outmap.putAll(localmap);
	       	} else{
	       		outmap.put(key, getJsonValueFromKey(pinputJson.toString(), key));
	       	}
		}
		return outmap;
	}

}
