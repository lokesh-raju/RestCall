/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.maputils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * 
 * @author arthanarisamy
 */
public class MapUtils {
	
	private static final com.iexceed.appzillon.logging.Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, MapUtils.class.toString());

    public static Map<String, String> convertObjectToMap(Object pObject) {
        Method[] methods = pObject.getClass().getMethods();

        Map<String, String> map = new HashMap<String, String>();
        for (Method m : methods) {
            if (m.getName().startsWith("get") && !m.getName().startsWith("getClass")) {
                try {
					if (m.invoke(pObject) != null) {
					    Object value = (Object) m.invoke(pObject);
					    map.put(m.getName().substring(3), value.toString());
					}
				} catch (IllegalAccessException e) {
					LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "IllegalAccessException -:", e);
				} catch (IllegalArgumentException e) {
					LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "IllegalArgumentException -:", e);
				} catch (InvocationTargetException e) {
					LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "InvocationTargetException -:", e);
				}
            }
        }
        return map;
    }
}
