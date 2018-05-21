package com.iexceed.appzillon.rest;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class AppzillonRespFilter implements ContainerResponseFilter{

	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {

		response.getHttpHeaders().add("X-Frame-Options", "DENY");
		response.getHttpHeaders().add("X-XSS-Protection", "1; mode=block");
		response.getHttpHeaders().add("X-Content-Type-Options", "nosniff");
		response.getHttpHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		
		return response;
	}
	
	

}
