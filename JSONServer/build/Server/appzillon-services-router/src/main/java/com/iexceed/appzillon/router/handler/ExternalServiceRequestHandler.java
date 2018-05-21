package com.iexceed.appzillon.router.handler;

import org.apache.camel.InvalidPayloadException;
import org.apache.commons.httpclient.URIException;

import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.router.exception.RouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;

public class ExternalServiceRequestHandler implements IRequestHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES,
                    ExternalServiceRequestHandler.class.toString());

    @Override
    public void handleRequest(Message pMessage) throws RouterException {
        LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Routing To External Services Processing.. Inside handleRequest");
        try {
            FrameworksStartup.getInstance().processRequest(pMessage);
        } catch (ExternalServicesRouterException externalServicesRouterException) {
            LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "externalServicesRouterException", externalServicesRouterException);
            throw externalServicesRouterException;
        } catch (JSONException ex) {
            LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Exception:" , ex);
            RouterException dexp = RouterException.getInstance();
            dexp.setMessage(ex.getMessage());
            dexp.setCode(RouterException.EXCEPTION_CODE.APZ_RS_003.toString());
            dexp.setPriority("1");
            throw dexp;
        }catch(ClassNotFoundException ex) {
            LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Exception:" , ex);
            RouterException dexp = RouterException.getInstance();
            dexp.setMessage(ex.getMessage());
            dexp.setCode(RouterException.EXCEPTION_CODE.APZ_RS_003.toString());
            dexp.setPriority("1");
            throw dexp;
        }catch(InvalidPayloadException ex) {
            LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Exception:" , ex);
            RouterException dexp = RouterException.getInstance();
            dexp.setMessage(ex.getMessage());
            dexp.setCode(RouterException.EXCEPTION_CODE.APZ_RS_003.toString());
            dexp.setPriority("1");
            throw dexp;
        }catch(URIException ex) {
            LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Exception:" , ex);
            RouterException dexp = RouterException.getInstance();
            dexp.setMessage(ex.getMessage());
            dexp.setCode(RouterException.EXCEPTION_CODE.APZ_RS_003.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Response from Service RequestHandler :" + pMessage);
    }

}
