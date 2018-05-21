package com.iexceed.appzillon.utils.jms;

import java.util.List;

import com.iexceed.appzillon.exception.Utils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class CamelJMSQueueResolver implements
        InitializingBean, DisposableBean {

  private String requestQueue;
  private List<String> responseQueues;

  
	@Override
	public void destroy() throws Exception {
		
	}
	
	@Override
    public void afterPropertiesSet() throws Exception {
        if (Utils.isNullOrEmpty(requestQueue)) {
            throw new BeanInitializationException(
                    "You must set a value for requestQueue");
        }
    }
       
    public String getRequestQueue() {
        return this.requestQueue;
    }

    public void setRequestQueue(String requestQueue) {
        this.requestQueue = requestQueue;
    }


    public List<String> getResponseQueues() {
        return responseQueues;
    }

    public void setResponseQueues(List<String> responseQueues) {
        this.responseQueues = responseQueues;
    }


}
