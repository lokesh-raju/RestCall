package com.iexceed.appzillon.sms.processor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.iexceed.appzillon.exception.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
/*
 * Author Abhishek
 */
public class SMSProcessorUtils {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
			ServerConstants.LOGGER_RESTFULL_SERVICES, SMSProcessorUtils.class.toString());
	/**
	 * 
	 * @param nodeList
	 * @return
	 */
	public static List<Tag> buildtagList(NodeList nodeList){
		List<Tag> tags = new ArrayList<Tag>();
		Tag tag = new Tag();
		for (int i = 0; i < nodeList.getLength(); i++) {
			tag = new Tag();
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				NodeList cnodelist = eElement.getChildNodes();
				for (int j = 0; j < cnodelist.getLength(); j++) {
					Node cNode = cnodelist.item(j);
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.NODE)) {
						tag.setNode(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.ELEMENT)) {
						tag.setElement(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.NAME_CAPS)) {
						tag.setName(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.FROM)) {
						tag.setFrom(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.ELEMENTVALUE)) {
						tag.setElementvalue(cNode.getTextContent());
					}
				}
				tags.add(i, tag);


			}
		}
		//	LOG.debug("buildtagList No of tags built is  "+tags.size());
		return tags;
	}

	/**
	 * 
	 * @param previousJSON
	 * @param tag
	 * @param requestPayLad
	 * @param index
	 * @param pmobilenumber
	 * @return
	 */
	public static void buildRequestJson(JSONObject previousJSON, Tag tag, String[] requestPayLad, int index, String pmobilenumber){
		String[] larry = split(tag.getNode(), ".");
		//JSONObject ljsonobject =new JSONObject();
		JSONObject ljsonobject = previousJSON;
		JSONObject lchildobj=null;
		String value = "";
		String ltoken = tag.getElementvalue();
		if(tag.getFrom().equals(ServerConstants.APPZILLON)){			
			if (ltoken.startsWith("$DATE")) {
				value = getDate(ltoken);
			} else if (ltoken.equals("$MOBILENUMBER")) {
				value = pmobilenumber;
			}
		} else  {
			value = ltoken;
			int lidx = value.indexOf("#");
			while (lidx >= 0) {
				String ltagnostr = value.substring(lidx + 1, lidx + 4);
				int ltagno = -1;
				try {
					ltagno = Integer.parseInt(ltagnostr);
				} catch (Exception ex) {
					ltagno = -1;
				}
				if (ltagno >= 0) {
					String ltagval = requestPayLad[ltagno];
					value = value.substring(0, lidx) + ltagval + value.substring(lidx + 4);
				}
				lidx = value.indexOf("#");
			}
		}
		for (int i = 0; i < larry.length; i++) {
			if(i==0){
				//	if(!previousJSON.has(larry[i])){
				if(previousJSON.toString()!="{}"){
					ljsonobject = previousJSON;
				}
				if(larry.length == 1){

					//	ljsonobject.accumulate(larry[i].toString(), new JSONObject().put(tag.getElement(),value));	
					ljsonobject.put(tag.getElement(), value);

				}else {
					ljsonobject.accumulate(larry[i].toString(), new JSONObject());
				}
				/*
				}else {
					ljsonobject = previousJSON;
				}*/
			}else{
				if(lchildobj==null){
					if(i == larry.length-1){

						lchildobj= createChild(
								larry[i].toString(),(JSONObject)
								ljsonobject.get(larry[i-1].toString()),tag.getElement(),
								value);

					} else {

						lchildobj= createChildWhenNull(
								larry[i].toString(),(JSONObject)
								ljsonobject.get(larry[i-1].toString()),tag.getElement(),
								value);

					}

				}else{
					if(i == larry.length-1){ 

						lchildobj= createChild(
								larry[i].toString(),(JSONObject)
								lchildobj.get(larry[i-1].toString()),tag.getElement(),
								value);

					}else {

						lchildobj= createChildWhenNull(
								larry[i].toString(),(JSONObject)
								lchildobj.get(larry[i-1]),tag.getElement(), value);

					}

				}
			}
		}
		//	LOG.debug("newJSON :   JSON returnes is "+ljsonobject.toString());
	}

	/**
	 * 
	 * @param pNode
	 * @param pjsonobj
	 * @param element
	 * @param value
	 * @return
	 */
	public  static JSONObject createChildWhenNull(String pNode,JSONObject pjsonobj, String element, String value){
		JSONObject lobj=new JSONObject();
		try{
			if(pjsonobj.has(pNode)){
				JSONObject tempJSON = pjsonobj.getJSONObject(pNode);
				tempJSON.put(element, value);
				pjsonobj.put(pNode, tempJSON);
			}else {
				pjsonobj.accumulate(pNode, lobj);
			}

		}catch(JSONException jx){
		}
		//LOG.debug("getJSONObjectnull :   JSON returnes is "+pjsonobj.toString());
		return pjsonobj;

	}
	/**
	 * 
	 * @param p
	 * @param pjsonobj
	 * @param element
	 * @param value
	 * @return
	 */
	public  static JSONObject createChild(String p,JSONObject pjsonobj, String element, String value){
		JSONObject lobj=new JSONObject();
		try{
			if(pjsonobj.has(p)){
				JSONObject tempJSON = pjsonobj.getJSONObject(p);
				tempJSON.put(element, value);
				pjsonobj.put(p, tempJSON);
			}else {
				pjsonobj.accumulate(p, lobj.put(element, value));
			}

		}catch(JSONException jx){
		}
		//LOG.debug("getJSONObject :   JSON returnes is "+pjsonobj.toString());
		return pjsonobj;

	}

	/**
	 * 
	 * @param pOriginalText
	 * @param pSeparator
	 * @return
	 */
	public static String[] split(String pOriginalText, String pSeparator) {
		Vector<String> nodes = new Vector<String>();
		String[] result = null;
		try {
			int index = pOriginalText.indexOf(pSeparator);
			while (index >= 0) {
				nodes.addElement(pOriginalText.substring(0, index));
				pOriginalText = pOriginalText.substring(index + pSeparator.length());
				index = pOriginalText.indexOf(pSeparator);
			}
			if (Utils.isNotNullOrEmpty(pOriginalText.trim())) {
				nodes.addElement(pOriginalText);
			}
			result = new String[nodes.size()];
			if (!nodes.isEmpty()) {
				for (int loop = 0; loop < nodes.size(); loop++) {
					result[loop] = (String) nodes.elementAt(loop);
				}
			}
		} catch (Exception e) {
			//System.out.println("Parsing Failed " + e.getMessage());
		}
		return result;
	}

	/**
	 * 
	 * @param actualRequest
	 * @return
	 */
	public static String serviceTypeIdentifier(String actualRequest) {
		String serviceType = "";

		serviceType =split(actualRequest, ServerConstants.SEPARATOR_SPACE)[0];
		LOG.debug("After identifying service request, service type is -:"
				+ serviceType);

		return serviceType.toUpperCase();
	}

	public static String getDate(String token){
		String date=token;
		try{
			int start = token.indexOf('(');
			int last = token.indexOf(')');
			if(start>-1 && last>-1){
				String format = token.substring(start+1, last-1);
				SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
				date = dateFormatter.format(new Date());
			}
		}catch(Exception e){
			LOG.debug("In getDate exception is "
					+ e.getMessage());
		}
		return date;
	}

}
