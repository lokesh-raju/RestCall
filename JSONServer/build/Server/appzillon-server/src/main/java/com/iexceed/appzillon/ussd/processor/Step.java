package com.iexceed.appzillon.ussd.processor;

public class Step {
	private String id;
	private String inputelement;
	private Response response;
	private Interface linterface;
	private String callserverreq;
	private String interfaceid;
	private String persist;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}
	public Interface getLinterface() {
		return linterface;
	}
	public void setLinterface(Interface linterface) {
		this.linterface = linterface;
	}
	public String getInputelement() {
		return inputelement;
	}
	public void setInputelement(String inputelement) {
		this.inputelement = inputelement;
	}
	public String getCallserverreq() {
		return callserverreq;
	}
	public void setCallserverreq(String callserverreq) {
		this.callserverreq = callserverreq;
	}
	public String getInterfaceid() {
		return interfaceid;
	}
	public void setInterfaceid(String interfaceid) {
		this.interfaceid = interfaceid;
	}
	public String getPersist() {
		return persist;
	}
	public void setPersist(String persist) {
		this.persist = persist;
	}

	@Override
	public String toString() {
		return "Step [id=" + id + ", inputelement=" + inputelement + ", response="
				+ response + ", linterface=" + linterface + ", callserverreq="
				+ callserverreq + ", interfaceid=" + interfaceid + ", persist="
				+ persist + "]";
	}

}
