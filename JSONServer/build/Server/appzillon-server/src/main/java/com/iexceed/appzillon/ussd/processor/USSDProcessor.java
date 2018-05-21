package com.iexceed.appzillon.ussd.processor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
/*
 * Author Abhishek
 */
public class USSDProcessor {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
			ServerConstants.LOGGER_RESTFULL_SERVICES, USSDProcessor.class.toString());
	
	private static USSDProcessor ussdinstance;
	private List<Action> actionlist;
	private String appId ;
	
	private USSDProcessor(){
		loadXML();
	}
	
	public static USSDProcessor getInstance(){
		if(ussdinstance==null){
			ussdinstance = new USSDProcessor();
		}
		return ussdinstance;
	}
	
	public  List<Action> getActionlist() {
		return actionlist;
	}
	public  void setActionlist(List<Action> actionlist) {
		this.actionlist = actionlist;
	}
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	private void loadXML(){
		InputStream isr = USSDProcessor.class.getClassLoader().getResourceAsStream(ServerConstants.META_INF_USSD+"USSD.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc=null;;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(isr);
			doc.getDocumentElement().normalize();
			actionlist = getActionList(doc);
		}catch (Exception e1) {
			e1.printStackTrace();
			LOG.error("Exception "+e1.getMessage());

		}
		LOG.debug("loadXML  action list size is"+actionlist.size());
		LOG.debug("loadXML:  startup value "+actionlist.get(0).getSteps().get(0).getResponse().getConditions().get(0).getConditionValue());
		setActionlist(actionlist);
	    String lappId = getAppId(doc,ServerConstants.MESSAGE_HEADER_APP_ID);
	    setAppId(lappId);
	}
	private static String getAppId(Document pdoc,String pnode){
		NodeList nodeList = pdoc.getDocumentElement().getChildNodes();
		String result = null ;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeName().equals(pnode)) {				
				if (node.getNodeType() == Node.ELEMENT_NODE){
					result = node.getTextContent();
					break;
				}
			}
		}

		return result;
	}
	public List<Action> getActionList(Document pdoc){
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String expression = "USSD/ACTION";
		NodeList nodeList;
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(pdoc, XPathConstants.NODESET);
			actionlist = new ArrayList<Action>();
			Action action = new Action();
			for (int i = 0; i < nodeList.getLength(); i++) {
				action = new Action();
				int count =0;
				List<Step> steplist = new ArrayList<Step>();
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					NodeList cnodelist = eElement.getChildNodes();
					for (int j = 0; j < cnodelist.getLength(); j++) {
						Node cNode = cnodelist.item(j);
						if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.USSD_ID)) {
							action.setId(cNode.getTextContent());
						}
						if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.DESCRIPTIONNODE)) {
							action.setDesc(cNode.getTextContent());
						}
						if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.STEP)) {
							steplist.add(count, getStep(cNode));
							count++;
						}

					}
					action.setSteps(steplist);
					actionlist.add(i, action);
				}
			}
		}
		catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return actionlist;

	}
	private Step getStep(Node pnode){
		NodeList nodeList;
		Step step = new Step();
		try {
			nodeList = pnode.getChildNodes();			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					if (node.getNodeName().equalsIgnoreCase(ServerConstants.USSD_ID)) {
						step.setId(node.getTextContent());
					}
					if (node.getNodeName().equalsIgnoreCase(ServerConstants.CALLSERVER)) {
						step.setCallserverreq(node.getTextContent());
					}
					if (node.getNodeName().equalsIgnoreCase(ServerConstants.ELEMENTNAME)) {
						step.setInputelement(node.getTextContent());
					}
					if (node.getNodeName().equalsIgnoreCase(ServerConstants.USSDRESPONSE)) {
						step.setResponse(getResponse(node));
					}
					if (node.getNodeName().equalsIgnoreCase(ServerConstants.INTERFACEID)) {
						step.setInterfaceid(node.getTextContent());
					}
					if (node.getNodeName().equalsIgnoreCase(ServerConstants.PERSISTENCE)) {
						step.setPersist(node.getTextContent());
					}
					if (node.getNodeName().equalsIgnoreCase(ServerConstants.USSD_INTERFACE)) {
						LOG.debug("getStepList:  getInterface will be called interface id is ");

						step.setLinterface(getInterface(node));
					}	

				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return step;

	}
	private Interface getInterface(Node pnode){

		NodeList nodeList;
		Interface linterface = new Interface();
		try {
			nodeList = pnode.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {

					if (node.getNodeName().equalsIgnoreCase(ServerConstants.INTERFACEID)) {
						linterface.setInterfaceId(node.getTextContent());
						LOG.debug("getInterface:   interfaceId is "+linterface.getInterfaceId());
					}
					if (node.getNodeName().equalsIgnoreCase(ServerConstants.USSDREQUEST)) {
						linterface.setRequest(getRequest(node));
					}
					if (node.getNodeName().equalsIgnoreCase(ServerConstants.USSDRESPONSE)) {
						linterface.setResponse(getResponse(node));
					}


				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return linterface;

	}
	
	private Request getRequest(Node pnode){

		NodeList nodeList;
		Request request = new Request();
		try {
			nodeList = pnode.getChildNodes();

			request.setTags(buildtagList(nodeList));

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return request;

	}
	
	private Response getResponse(Node pnode){
		NodeList nodeList;
		Response response = new Response();
		try {
			nodeList = pnode.getChildNodes();
			response.setConditions(buildConditionList(nodeList));

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}
	private List<Tag> buildtagList(NodeList nodeList){
		List<Tag> tags = new ArrayList<Tag>();
		Tag tag = new Tag();
		int count =0;
		for (int i = 0; i < nodeList.getLength(); i++) {
			tag = new Tag();
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(ServerConstants.TAG)) {
				Element eElement = (Element) node;
				NodeList cnodelist = eElement.getChildNodes();
				for (int j = 0; j < cnodelist.getLength(); j++) {
					Node cNode = cnodelist.item(j);
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.NODE)) {
						tag.setNode(cNode.getTextContent());
						LOG.debug("going to set node  for tags");
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.ELEMENT)) {
						tag.setElement(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.ELEMENTVALUE)) {
						tag.setElementvalue(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.NAME_CAPS)) {
						tag.setName(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.FROM)) {
						tag.setFrom(cNode.getTextContent());
					}

				}
				tags.add(count, tag);
				count++;


			}
		}
		LOG.debug("buildtagList No of tags built is  "+tags.size());
		return tags;
	}
	private List<Condition> buildConditionList(NodeList nodeList){
		List<Condition> conditions = new ArrayList<Condition>();
		Condition condition = new Condition();
		int count =0;
		for (int i = 0; i < nodeList.getLength(); i++) {
			condition = new Condition();
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(ServerConstants.CONDITION)) {
				Element eElement = (Element) node;
				NodeList cnodelist = eElement.getChildNodes();
				for (int j = 0; j < cnodelist.getLength(); j++) {
					Node cNode = cnodelist.item(j);
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.NODE)) {
						condition.setNode(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.ELEMENT)) {
						condition.setElement(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.USSD_ID)) {
						condition.setId(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.CONDITIONTYPE)) {
						condition.setConditionType(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.CONDITIONID)) {
						condition.setConditionId(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.CONDITIONVALUE)) {
						condition.setConditionValue(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.NEXTACTION)) {
						condition.setNextAction(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.NEXTSTEP)) {
						condition.setNextStep(cNode.getTextContent());
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.MESSAGES)) {
						condition.setMessageMap(getMessageMap(cNode));
					}
					if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.PERSISTENCE)) {
						condition.setPersist(cNode.getTextContent());
					}

				}
				conditions.add(count, condition);
				count ++;
			}
		}
		LOG.debug("buildConditionList No of conditions built is  "+conditions.size());
		return conditions;
	}
    private HashMap<String,String> getMessageMap(Node pnode){
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
						String temp = cNode.getTextContent();
						desc = temp.replaceAll("newline", "\\\n");
					}

				}
				if(!lang.equals("") && !desc.equals("")){
					messageMap.put(lang, desc);
					lang = "";
					desc ="";
				}


			}
		}
		LOG.debug("getMessageMap Message Map is   "+messageMap);
    	return messageMap;
    }
}
