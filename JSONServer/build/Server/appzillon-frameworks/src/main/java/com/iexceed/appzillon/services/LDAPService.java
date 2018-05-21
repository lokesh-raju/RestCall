package com.iexceed.appzillon.services;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import com.iexceed.appzillon.exception.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.springldap.SpringLdapProducer;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.ldap.core.LdapTemplate;

import com.iexceed.appzillon.dao.LDAPDetails;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServicesUtil;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;

public class LDAPService implements IServicesBean{

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    LDAPService.class.toString());

    protected LDAPDetails ldapDtls = null;

    public String update(Message pMessage, SpringCamelContext pContext,
            ProducerTemplate pProducer) {
        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside LDAPService update");

        String dn = null;
        String addAttributes = null;
        String replaceAttributes = null;
        String deleteAttributes = null;
        JSONObject load = pMessage.getRequestObject().getRequestJson();
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        LdapTemplate ldapTemplate = (LdapTemplate) pContext
                .getApplicationContext().getBean(
                        appId + "_" + interfaceId + ServerConstants.LDAP_TEMPLATE);

        try {
            dn = load.get(ServerConstants.LDAP_CONSTANTS_DN).toString();
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "dn value" + dn);
            if (load.has(ServerConstants.LDAP_CONSTANTS_ADD_ATTRIBUTES)) {
                addAttributes = load.get(
                        ServerConstants.LDAP_CONSTANTS_ADD_ATTRIBUTES)
                        .toString();
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "add attributes" + addAttributes);
            }
            if (load.has(ServerConstants.LDAP_CONSTANTS_REPLACE_ATTRIBUTES)) {
                replaceAttributes = load.get(
                        ServerConstants.LDAP_CONSTANTS_REPLACE_ATTRIBUTES)
                        .toString();
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Replace attributes" + replaceAttributes);
            }
            if (load.has(ServerConstants.LDAP_CONSTANTS_DELETE_ATTRIBUTES)) {
                deleteAttributes = load.get(
                        ServerConstants.LDAP_CONSTANTS_DELETE_ATTRIBUTES)
                        .toString();
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Delete attributes" + deleteAttributes);
            }

        } catch (JSONException e1) {
            LOG.error("JSONException",e1);
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(e1.getMessage());
            exExp.setPriority("1");
            throw exExp;
        }

        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "1 ADD :" + addAttributes + "     2 REPLACE:"
                + replaceAttributes + "   3 DELETE:" + deleteAttributes);

        String[] addArray = new String[0];
        String[] replaceArray = new String[0];
        String[] deleteArray = new String[0];
        if (Utils.isNotNullOrEmpty(addAttributes)) {
            addArray = addAttributes.split(",");
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "total no of items to be added " + addArray.length);

        }
        if (Utils.isNotNullOrEmpty(replaceAttributes)) {
            replaceArray = replaceAttributes.split(",");
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "total no of items to be replaced " + replaceArray.length);

        }

        if (Utils.isNotNullOrEmpty(deleteAttributes)) {
            deleteArray = deleteAttributes.split(",");
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "total no of items to be deleted " + deleteArray.length);

        }
        int mod = deleteArray.length + replaceArray.length + addArray.length;
        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "total no of items to be modified " + mod);
        ModificationItem[] mods = new ModificationItem[mod];
        int itemCounter = 0;
        int i = 0;
        while (i < addArray.length) {
            String[] split = addArray[i].split(":");
            Attribute item = new BasicAttribute(split[0], split[1]);
            ModificationItem addItem = new ModificationItem(
                    DirContext.ADD_ATTRIBUTE, item);
            mods[itemCounter] = addItem;
            itemCounter++;
            i++;
        }

        int j = 0;
        while (j < replaceArray.length) {
            String[] split = replaceArray[j].split(":");
            Attribute item = new BasicAttribute(split[0], split[1]);
            ModificationItem repItem = new ModificationItem(
                    DirContext.REPLACE_ATTRIBUTE, item);
            mods[itemCounter] = repItem;
            itemCounter++;
            j++;
        }

        int k = 0;
        while (k < deleteArray.length) {
            String[] split = deleteArray[k].split(":");
            Attribute item = new BasicAttribute(split[0], split[1]);
            ModificationItem delItem = new ModificationItem(
                    DirContext.REMOVE_ATTRIBUTE, item);
            mods[itemCounter] = delItem;
            itemCounter++;
            k++;
        }

        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "" + mods);
        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Ldap template used " + ldapTemplate);
        try {
            Utils.setExtTime(pMessage,"S");
            ldapTemplate.modifyAttributes(dn, mods);
            Utils.setExtTime(pMessage,"E");
            ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.SUCCESS,"",mods);

        } catch (Exception e) {
        	LOG.error("Exception",e);
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(e.getMessage());
            exExp.setPriority("1");
            ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,e.getMessage(),mods);
            throw exExp;
        }
       
		return "{\"response\":\"updated successfully\"}";

    }

    public String performSearch(Message pMessage, SpringCamelContext pContext,
            ProducerTemplate pProducer) {
        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside LDAPService performSearch");
        String output = "";
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        String endpointURI = ServerConstants.LDAP_SPRING_URI_PREFIX + appId
                + "_" + interfaceId + ServerConstants.LDAP_TEMPLATE;
        String scope = "subtree";
        String dn = null;
        String filter = null;
        Exchange exchange = null;
        JSONObject load = pMessage.getRequestObject().getRequestJson();
        if (load.has(ServerConstants.LDAP_CONSTANTS_SCOPE)) {
            try {
                scope = load.getString(ServerConstants.LDAP_CONSTANTS_SCOPE);
            } catch (JSONException e) {
                LOG.warn("LDAP Scope <subtree, object, onelevel> not found so setting default :subtree");
            }
        }
        String operation = "?operation=search&scope=" + scope;

        try {
            endpointURI = endpointURI + operation;
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ldap endpoint " + endpointURI);
            dn = load.get(ServerConstants.LDAP_CONSTANTS_DN).toString();
            LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "dn " + dn);

            filter = load.get(ServerConstants.LDAP_CONSTANTS_FILTER)
                    .toString();
            LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "filter " + filter);

        } catch (JSONException e1) {
        	LOG.error("JSONException",e1);
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(e1.getMessage());
            exExp.setPriority("1");
            throw exExp;
        }
        final String dN = dn;
        final String fILTER = filter;
        Utils.setExtTime(pMessage,"S");
        final Map<String, String> map = new HashMap<String, String>();
        exchange = pProducer.request(endpointURI, new Processor() {
            public void process(Exchange exchng) throws Exception {

                map.put(SpringLdapProducer.DN, dN);
                map.put(SpringLdapProducer.FILTER, fILTER);
                exchng.getIn().setBody(map);
            }
        });
        Utils.setExtTime(pMessage,"E");
        if (exchange.getException() == null) {
            output = exchange.getIn().getBody(String.class);
            ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.SUCCESS,output,map);
        } else {
            LOG.error("Exchange Exception",exchange.getException());
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,exchange.getException().getMessage(),map);
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(exchange.getException().getMessage());
            exExp.setPriority("1");
            throw exExp;
        }

       return "{\"respone\":\"" + output + "\"}";
    }

    public String create(Message pMessage, SpringCamelContext pContext,
            ProducerTemplate pProducer) {
        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Inside LDAPService create");

        String output = "";
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        String endpointURI = ServerConstants.LDAP_SPRING_URI_PREFIX + appId
                + "_" + interfaceId + ServerConstants.LDAP_TEMPLATE;
        String scope = "subtree";
        String dn = null;
        JSONObject load = pMessage.getRequestObject().getRequestJson();
        Exchange exchange = null;
        if (load.has(ServerConstants.LDAP_CONSTANTS_SCOPE)) {
            try {
                scope = load.getString(ServerConstants.LDAP_CONSTANTS_SCOPE);
            } catch (JSONException e) {

                LOG.warn("LDAP Scope <subtree, object, onelevel> not found so setting default :subtree");
            }
        }
        String operation = "?operation=bind&scope=" + scope;
        String attrs = null;
        String classes = null;

        try {
            endpointURI = endpointURI + operation;
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ldap endpoint " + endpointURI);
            attrs = load.get(ServerConstants.LDAP_CONSTANTS_ATTRIBUTES)
                    .toString();

            classes = load
                    .get(ServerConstants.LDAP_CONSTANTS_OBJECT_CLASSES)
                    .toString();
            dn = load.get(ServerConstants.LDAP_CONSTANTS_DN).toString();
        } catch (JSONException e1) {
        	LOG.error("JSONException",e1);
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(e1.getMessage());
            exExp.setPriority("1");
            throw exExp;
        }

        String[] aTTRS = attrs.split(",");
        String[] objectclasses = classes.split(",");
        int i = 0;
        Attributes attributes = new BasicAttributes();

        while (i < aTTRS.length) {
            String[] spliter = aTTRS[i].split(":");
            attributes.put(spliter[0], spliter[1]);
            i++;
        }
        BasicAttribute oc = new BasicAttribute(
                ServerConstants.LDAP_CONSTANTS_OBJECTCLASS);
        i = 0;
        while (i < objectclasses.length) {
            oc.add(objectclasses[i]);
            i++;
        }
        attributes.put(oc);

        final String dN = dn;
        final Attributes aTTRIBUTES = attributes;
        Utils.setExtTime(pMessage,"S");
       final Map<String, Serializable> map = new HashMap<String, Serializable>();
        exchange = pProducer.request(endpointURI, new Processor() {
            public void process(Exchange exchng) throws Exception {

                map.put(SpringLdapProducer.DN, dN);
                map.put(SpringLdapProducer.ATTRIBUTES, aTTRIBUTES);
                exchng.getIn().setBody(map);
            }
        });
        Utils.setExtTime(pMessage,"E");
        if (exchange.getException() == null) {
            output = exchange.getIn().getBody(String.class);
            ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.SUCCESS,output,map);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "output from Service" + output);
        } else {
            LOG.error("Exchange Exception",exchange.getException());
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,exchange.getException().getMessage(),map);
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(exchange.getException().getMessage());
            exExp.setPriority("1");
            throw exExp;
        }

		return "{\"response\":\"createded successfully\"}";

    }

    public String delete(Message pMessage, SpringCamelContext pContext,
            ProducerTemplate pProducer) {
        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside LDAPService delete");

        String dn = null;
        String output = "";
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        String endpointURI = ServerConstants.LDAP_SPRING_URI_PREFIX + appId
                + "_" + interfaceId + "_ldapTemplate";
        String scope = "subtree";
        Exchange exchange = null;
        JSONObject load = pMessage.getRequestObject().getRequestJson();
        if (load.has(ServerConstants.LDAP_CONSTANTS_SCOPE)) {
            try {
                scope = load.getString(ServerConstants.LDAP_CONSTANTS_SCOPE);
            } catch (JSONException e) {
                LOG.warn("LDAP Scope <subtree, object, onelevel> not found so setting default :subtree");
            }
        }
        String operation = "?operation=unbind&scope=" + scope;
        try {
            endpointURI = endpointURI + operation;
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ldap endpoint " + endpointURI);
            dn = load.get(ServerConstants.LDAP_CONSTANTS_DN).toString();
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "dn :" + dn);
        } catch (JSONException e1) {
        	LOG.error("JSONException",e1);
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(e1.getMessage());
            exExp.setPriority("1");
            throw exExp;
        }

        final String dN = dn;

        Utils.setExtTime(pMessage,"S");
        final Map<String, String> map = new HashMap<String, String>();
        exchange = pProducer.request(endpointURI, new Processor() {
            public void process(Exchange exchng) throws Exception {

                map.put(SpringLdapProducer.DN, dN);
                exchng.getIn().setBody(map);
            }
        });
        Utils.setExtTime(pMessage,"E");
        if (exchange.getException() == null) {
            output = exchange.getIn().getBody(String.class);
            ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.SUCCESS,output,map);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "output from Service" + output);
        } else {
            LOG.error("Exchange Exception",exchange.getException());
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            ServicesUtil.processFmwTxnDetails(pMessage,ServerConstants.ERROR,exchange.getException().getMessage(),map);
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(exchange.getException().getMessage());
            exExp.setPriority("1");
            throw exExp;
        }
       
		return "{\"response\":\"deleted successfully\"}";

    }

	@Override
	public Object buildRequest(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		return pRequestPayLoad;
	}

	@Override
	public Object processResponse(Message pMessage, Object pResponse,
			SpringCamelContext pContext) {
		return pResponse;
	}

	@Override
	public Object callService(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		String output=null;
		
		ProducerTemplate lproducer=ExternalServicesRouter.createProducerTemplate(pContext);
        String lappId = pMessage.getHeader().getAppId();
        String linterfaceId = pMessage.getHeader().getInterfaceId();
        String ldaoBeanID = lappId + "_" + linterfaceId;
       
        String[] loperations = null;
        ldapDtls = (LDAPDetails)ExternalServicesRouter
                .injectBeanFromSpringContext(ldaoBeanID, pContext);
        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ldap server ip configured " + ldapDtls.getHost());
        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ldap server port configured " + ldapDtls.getPort());
        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ldap server isAuthenticationReq boolean"
                + ldapDtls.getAuthenticationReq());

        if (ldapDtls.getOperationAllowed() != null) {
            loperations = ldapDtls.getOperationAllowed().split(",");
        } else {
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage("Parameter operationAllowed not configured");
            exExp.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Parameter operation Allowed not configured.", exExp);
            throw exExp;

        }
        pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad, ldapDtls.getAutoGenElementMap(), ldapDtls.getTranslationElementMap());
        pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad+""));
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Appending Request Json With MaskedId : " + pMessage.getRequestObject().getRequestJson());
        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Calling build Request of Superclass");
		String lpayLoad = (String) buildRequest(pMessage, pRequestPayLoad.toString(), pContext);
        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Payload after buildRequest "+lpayLoad);
        JSONObject load = new JSONObject(lpayLoad);

        String operation = load
                .get(ServerConstants.LDAP_CONSTANTS_OPERATION).toString();
        int i = 0;
        boolean flag = false;
        while (i < loperations.length) {
            LOG.info((i + 1) + " method configured " + loperations[i]);
            if (loperations[i].equals(operation)) {
                flag = true;
                break;
            }
            i++;
        }
        if (flag) {
            LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "operation found : " + operation);
            if (operation.equals(ServerConstants.LDAP_CONSTANTS_ADD)) {
            	output=  this.create(pMessage, pContext, lproducer);
            } else if (operation
                    .equals(ServerConstants.LDAP_CONSTANTS_UPDATE)) {
            	output= this.update(pMessage, pContext, lproducer);
            } else if (operation
                    .equals(ServerConstants.LDAP_CONSTANTS_DELETE)) {
            	output= this.delete(pMessage, pContext, lproducer);
            } else if (operation
                    .equals(ServerConstants.LDAP_CONSTANTS_SEARCH)) {
            	 output= this.performSearch(pMessage, pContext, lproducer);
            } else {
                ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
                exExp.setMessage("Method not allowed");
                exExp.setPriority("1");
                LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "method not allowed", exExp);
                throw exExp;
            }
        } else {
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage("Method not supported by  Ldap Service");
            exExp.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "method not supported by  Ldap Service", exExp);
            throw exExp;
        }
        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Calling processResponse method ");
        output=(String) processResponse(pMessage, output, pContext);
        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response processResponse method "+output);
		return new JSONObject(output);

    }
}
