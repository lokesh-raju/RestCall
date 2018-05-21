/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.message;

import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.multipart.FormDataMultiPart;

/**
 *
 * @author arthanarisamy
 */
public class Message {

	private Request requestObject = null;
	private Response responseObject = null;
	private Header header = null;
	private List<Error> errors = null;
	private InterfaceDetails intfDtls = null;
	private Session session = null;
	private SecurityParams securityParams = null;
	private FormDataMultiPart multiPart = null;
	

	public static Message getInstance() {
		return new Message();
	}

	private Message() {
		this.errors = new ArrayList<Error>();
		this.header = Header.getInstance();
		this.intfDtls = InterfaceDetails.getInstance();
		this.requestObject = Request.getInstance();
		this.responseObject = Response.getInstance();
		this.session = Session.getInstance();
		this.securityParams = SecurityParams.getInstance();
		this.multiPart = new FormDataMultiPart();
	}

	public Request getRequestObject() {
		return requestObject;
	}

	public void setRequestObject(Request requestObject) {
		this.requestObject = requestObject;
	}

	public Response getResponseObject() {
		return responseObject;
	}

	public FormDataMultiPart getFormDataMultiPart() {
		return multiPart;
	}

	public void setFormDataMultiPart(FormDataMultiPart multiPart) {
		this.multiPart = multiPart;
	}

	public void setResponseObject(Response responseObject) {
		this.responseObject = responseObject;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

	public InterfaceDetails getIntfDtls() {
		return intfDtls;
	}

	public void setIntfDtls(InterfaceDetails intfDtls) {
		this.intfDtls = intfDtls;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * @return the securityParams
	 */
	public SecurityParams getSecurityParams() {
		return securityParams;
	}

	/**
	 * @param securityParams
	 *            the securityParams to set
	 */
	public void setSecurityParams(SecurityParams securityParams) {
		this.securityParams = securityParams;
	}
	
}
