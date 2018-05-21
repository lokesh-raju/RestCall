package com.iexceed.appzillon.sms.processor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
/*
 * Author Abhishek
 */

public class SMSResponseProcessor {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
			ServerConstants.LOGGER_RESTFULL_SERVICES, SMSResponseProcessor.class.toString());
	SMSRequestProcessorImpl lsmsProcessor = null;
	public  String getResponse(String lresponse,String lrequest){
		String lservicetype = SMSProcessorUtils.serviceTypeIdentifier(lrequest);
		InputStream isr = SMSResponseProcessor.class.getClassLoader().getResourceAsStream(ServerConstants.META_INF_SMS+lservicetype+".xml");
		String result ="";
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(isr);
			result=buildResponseString(doc,lresponse);
		}catch (Exception e1) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "Exception :", e1);
		}
		return result;
	}
	public  String buildResponseString(Document pdoc,String presponse){
		String result = "";
		JSONObject resheader = new JSONObject(presponse).getJSONObject(ServerConstants.MESSAGE_HEADER);
		JSONObject bodyjson =  new JSONObject(presponse).getJSONObject(ServerConstants.MESSAGE_BODY);
		boolean hstatus = resheader.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS);
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "in buildResponseString response body" + bodyjson.toString());
		String messsageSep = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.MESSAGE_SEPERATOR);
		if (hstatus) {
			presponse = bodyjson.toString();
		}else {
			return "Sorry Unable to Process your Request";
		}
		JSONObject jsres = new JSONObject(presponse);
		String lrequestlevel = getNodeName(pdoc);
		String[] lrequestlarr = lrequestlevel.split("\\.");
		String node = "";
		int count =0;
		for(String resnode : lrequestlarr){

			if(jsres.has(resnode) && count<lrequestlarr.length-1){
				jsres = (JSONObject) jsres.get(resnode);

			}
			node = resnode;
			count++;
		}
		if(jsres.get(node) instanceof JSONArray){
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "in buildResponseString Response node is JSONArray");
			JSONArray jsarray = (JSONArray) jsres.get(node);
			for(int i=0;i<jsarray.length();i++){
				JSONObject jobj = (JSONObject) jsarray.get(i);
				result += buildResponse(pdoc,jobj.toString())+messsageSep;
			}
			result = result.substring(0, result.length()-1);

		}else if(jsres.get(node) instanceof JSONObject) {
			JSONObject jobj =  (JSONObject) jsres.get(node);
			presponse = jobj.toString();
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "in buildResponseString Response node is JSONObject");
			result = buildResponse(pdoc,presponse);
		}

		return result;
	}

	public  String buildResponse(Document pdoc,String presponse){
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String expression = "/INTERFACE/RESPONSE/TAG";	
		NodeList nodeList;
		String name = null;
		String element = null;
		JSONObject jsonres = null;
		HashMap<String,Tag> responsetagmap = new HashMap<String,Tag>();
		LinkedList<String> taglist= new LinkedList<String>();
		String result = "";

		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(pdoc, XPathConstants.NODESET);
			for(int i=0;i<nodeList.getLength();i++){
				Tag responseTag = new Tag();
				jsonres = new JSONObject(presponse);
				Node node = nodeList.item(i);
				NodeList cnodelist = node.getChildNodes();
				for(int x=0;x<cnodelist.getLength();x++){
					Node cnode = cnodelist.item(x);
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.NAME_CAPS)) {
						name = cnode.getTextContent();
						responseTag.setName(name);	
						taglist.add(name);
					}
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.NODE)) {


						String lrequestlevel = cnode.getTextContent();
						responseTag.setNode(lrequestlevel);
						/*String[] lrequestlarr = lrequestlevel.split("\\.");
						for(String resnode : lrequestlarr){

							if(jsonres.has(resnode)){
								jsonres = jsonres.getJSONObject(resnode);
							}
						}*/
					}
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.ELEMENT)){
						if(!cnode.getTextContent().equals("")){
							element = cnode.getTextContent();
							if(jsonres.has(element)){
								responseTag.setElementvalue(""+jsonres.get(element));
								responseTag.setElement(element);
							}
						}
					}
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.CONDITIONTAG)){
						if(!cnode.getTextContent().equals("")){
							responseTag.setCondition(cnode.getTextContent());
						}
					}
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.CONDITIONTYPE)){
						if(!cnode.getTextContent().equals("")){
							responseTag.setConditionType(cnode.getTextContent());
						}
					}
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.CONDITIONVALUE)){
						if(!cnode.getTextContent().equals("")){
							responseTag.setConditionValue(cnode.getTextContent());
						}
					}
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.MESSAGES)){
						if(!cnode.getTextContent().equals("")){
							responseTag.setMessageMap(getMessageMap(cnode));
						}
					}
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.APPEND)){
						if(!cnode.getTextContent().equals("")){
							responseTag.setAppender(cnode.getTextContent());
						}
					}
				}

				responsetagmap.put(name,responseTag);
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + " Response Tags Map :" + responsetagmap);
			for(Map.Entry<String ,Tag> entry: responsetagmap.entrySet()){
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "buildResponseTag  "+entry.getKey()+"     "+entry.getValue());
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "buildResponseTag   "+entry.getKey()+"     "+entry.getValue().getName()+" "+entry.getValue().getElement()+" "+entry.getValue().getMessageMap());
			}

			for(int i=0;i<taglist.size();i++){
				String tagname = taglist.get(i);
				Tag ltag = responsetagmap.get(tagname);
				String lcondition = ltag.getCondition();
				String lconditiontype = ltag.getConditionType();
				String lconditionvalue = ltag.getCondition_value();
				boolean append = false;
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "tagname :" + tagname + "; lcondition:" + lcondition + "; lconditiontype: " + lconditiontype + "; lconditionvalue:" + lconditionvalue);
				if(lcondition != null && lcondition != ""){
					if(lconditiontype.equals(ServerConstants.EQ)){
						if(responsetagmap.get(lcondition).getElementvalue().equals(lconditionvalue)){
							if(ServerConstants.YES.equalsIgnoreCase(ltag.getAppender())){
								append = true;
							}
						}
					}else if(lconditiontype.equals(ServerConstants.NEQ)){
						if(!responsetagmap.get(lcondition).getElementvalue().equals(lconditionvalue)){
							if(ServerConstants.YES.equalsIgnoreCase(ltag.getAppender())){
								append = true;
							}
						}
					}
				}else {
					if(ServerConstants.YES.equalsIgnoreCase(ltag.getAppender())){
						append = true;
					}
				}
				if(append){
					result += appendResult(ltag,pdoc);
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "buildResponseTag   "+tagname+result);
			}
		}
		catch (XPathExpressionException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " XPathExpressionException" , e);
		}
		return result;
	}
	public  HashMap<String,String> getMessageMap(Node pnode){
		HashMap<String,String> messageMap= new HashMap<String,String>();
		NodeList nodeList = pnode.getChildNodes();
		String lang="";
		String desc="";
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(ServerConstants.MESSAGENODE)) {
				Element eElement = (Element) node;
				NodeList cnodelist = eElement.getChildNodes();
				for (int j = 0; j < cnodelist.getLength(); j++) {
					Node cNode = cnodelist.item(j);
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.LANGUAGE)) {
						lang = cNode.getTextContent();
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.DESCRIPTIONNODE)) {
						desc = cNode.getTextContent();
					}

				}
				if(!lang.equals("") && !desc.equals("")){
					messageMap.put(lang, desc);
					lang = "";
					desc ="";
				}


			}
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "getMessageMap Message Map is   "+messageMap);
		return messageMap;
	}
	public  String appendResult(Tag ptag,Document pdoc){
		String result = "";
		String ldefaultlang =this.getLsmsProcessor().defaultLang;
		if(ptag.getMessageMap().get(ldefaultlang)!=null && !ptag.getMessageMap().get(ldefaultlang).equals("")){
			result += ptag.getMessageMap().get(ldefaultlang);
		}
		if(ptag.getAppender().equalsIgnoreCase(ServerConstants.YES)){
			if(ptag.getElement()!=null && !ptag.getElement().equals("")){
				if(!ptag.getMessageMap().get(ldefaultlang).contains("$")){
					if(!getTranslatedValue(ptag,pdoc).equals(""))
					{	result += getTranslatedValue(ptag,pdoc);
					}else {
						result += ptag.getElementvalue();
					}

				}else {
					result = result.replace("$",getTranslatedValue(ptag,pdoc));
				}
			}
		}

		return result;
	}
	public  String getTranslatedValue(Tag ptag,Document pdoc){
		String result="";
		NodeList nodeList;
		String ldefaultlang = this.getLsmsProcessor().defaultLang;
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String xpathexpr = "INTERFACE/TRANSLATIONS/TRANSLATION";
		String lkey ="";
		String ldesc = "";
		boolean code = false;
		boolean lnode =false;
		boolean lelement = false;
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "getTranslatedValue :  xpathexpr ="+xpathexpr);
		try {
			nodeList = (NodeList) xPath.compile(xpathexpr).evaluate(pdoc, XPathConstants.NODESET);
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "getTranslatedValue : nodeList length :" + nodeList.getLength());
			if(nodeList.getLength()==0){
				return ptag.getElementvalue();
			}
			for(int x=0;x<nodeList.getLength();x++){
				Node node = nodeList.item(x);
				NodeList cnodelist = node.getChildNodes();
				for(int i=0;i<cnodelist.getLength();i++){
					Node cnode = cnodelist.item(i);
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.NODE)) {
						if(ptag.getNode().equals(cnode.getTextContent())){

							lnode =true;
						}
					}
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.ELEMENT)) {
						if(ptag.getElement().equals(cnode.getTextContent())){

							lelement = true;
						} 
					}
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.LANGUAGE)) {
						Element eElement = (Element) cnode;
						NodeList langlist = eElement.getChildNodes();
						for(int j=0;j<langlist.getLength();j++){
							Node lang = langlist.item(j);
							if (lang.getNodeType() == Node.ELEMENT_NODE && lang.getNodeName().equals(ServerConstants.CODE)) {
								if(lang.getTextContent().equals(ldefaultlang)){
									code = true;
								}
							}
							if (lang.getNodeType() == Node.ELEMENT_NODE && lang.getNodeName().equals(ServerConstants.VALUES)) {

								eElement = (Element) lang;
								NodeList values = eElement.getChildNodes();
								for(int z=0;z<values.getLength();z++){
									Node value = values.item(z);
									if (value.getNodeType() == Node.ELEMENT_NODE && value.getNodeName().equals(ServerConstants.VALUE)) {
										NodeList gcnodelist = value.getChildNodes();
										for(int y =0;y<gcnodelist.getLength();y++){
											Node gcnode = gcnodelist.item(y);
											if (gcnode.getNodeType() == Node.ELEMENT_NODE && gcnode.getNodeName().equals(ServerConstants.KEY)){
												lkey = gcnode.getTextContent();
											}
											if (gcnode.getNodeType() == Node.ELEMENT_NODE && gcnode.getNodeName().equals(ServerConstants.DESCRIPTIONNODE)&&lkey.equals(ptag.getElementvalue())){
												if(!gcnode.getTextContent().equals("")){
													ldesc = gcnode.getTextContent();
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if(!lkey.equals("") && lnode && lelement && code){
				result = ldesc ;
			}else {
				result = ptag.getElementvalue();
			}
		}
		catch (XPathExpressionException e) {
			result = ptag.getElementvalue();
		}

		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "getTranslatedValue : result = "+result);
		return result;
	}
	public  String getNodeName(Document pdoc){
		String result="";
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String expression = "/INTERFACE/RESPONSE/TAG";	
		NodeList nodeList;
		String name = null;
		LinkedList<String> taglist= new LinkedList<String>();
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(pdoc, XPathConstants.NODESET);
			for(int i=0;i<nodeList.getLength();i++){
				Tag responseTag = new Tag();
				Node node = nodeList.item(i);
				NodeList cnodelist = node.getChildNodes();
				for(int x=0;x<cnodelist.getLength();x++){
					Node cnode = cnodelist.item(x);
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.NAME_CAPS)) {
						name = cnode.getTextContent();
						responseTag.setName(name);	
						taglist.add(name);
					}
					if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.NODE)) {
						String lrequestlevel = cnode.getTextContent();
						result = lrequestlevel;
						return lrequestlevel;

					}

				}


			}

		}
		catch (XPathExpressionException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " XPathExpressionException" , e);
		}
		return result;
	}
	public SMSRequestProcessorImpl getLsmsProcessor() {
		return lsmsProcessor;
	}
	public void setLsmsProcessor(SMSRequestProcessorImpl lsmsProcessor) {
		this.lsmsProcessor = lsmsProcessor;
	}
	
}