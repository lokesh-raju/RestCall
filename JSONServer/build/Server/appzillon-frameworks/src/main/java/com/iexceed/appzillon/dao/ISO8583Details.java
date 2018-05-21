/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.dao;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author arthanarisamy
 */
public class ISO8583Details {
	
    private String endPointURL;
    private int portNo;
    private String signonRequired;
    private int isoRespHeaderLength;
    private int timeOut;
    private Map<String,String> sigonRequest;
    private String autoGenerateField11;
    private String isoReqHeaderMask;
    private String isoReqHeaderLength;
    private String isoReqFormat;
    private String isoReqBinaryBitmap;
    private String isoRespFormat;
    private String isoRespBinaryBitmap;
    private String newLineReq;
    private String field11Length;
    private  Map<String, String> autoGenElementMap = new HashMap<String, String>();
	private  Map<String,String> translationElementMap = new HashMap<String,String>();
	private String keepAlive;
    
    
	public String getEndPointURL() {
		return endPointURL;
	}
	public void setEndPointURL(String endPointURL) {
		this.endPointURL = endPointURL;
	}
	public int getPortNo() {
		return portNo;
	}
	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public String getSignonRequired() {
		return signonRequired;
	}

	public void setSignonRequired(String signonRequired) {
		this.signonRequired = signonRequired;
	}

	public Map<String, String> getSigonRequest() {
		return sigonRequest;
	}
	public void setSigonRequest(Map<String, String> sigonRequest) {
		this.sigonRequest = sigonRequest;
	}
	public int getIsoRespHeaderLength() {
		return isoRespHeaderLength;
	}
	public void setIsoRespHeaderLength(int isoRespHeaderLength) {
		this.isoRespHeaderLength = isoRespHeaderLength;
	}
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	public String getAutoGenerateField11() {
		return autoGenerateField11;
	}
	public void setAutoGenerateField11(String autoGenerateField11) {
		this.autoGenerateField11 = autoGenerateField11;
	}
	public String getIsoReqFormat() {
		return isoReqFormat;
	}
	public void setIsoReqFormat(String isoReqFormat) {
		this.isoReqFormat = isoReqFormat;
	}
	public String getIsoReqBinaryBitmap() {
		return isoReqBinaryBitmap;
	}
	public void setIsoReqBinaryBitmap(String isoReqBinaryBitmap) {
		this.isoReqBinaryBitmap = isoReqBinaryBitmap;
	}
	public String getIsoRespFormat() {
		return isoRespFormat;
	}
	public void setIsoRespFormat(String isoRespFormat) {
		this.isoRespFormat = isoRespFormat;
	}
	public String getIsoRespBinaryBitmap() {
		return isoRespBinaryBitmap;
	}
	public void setIsoRespBinaryBitmap(String isoRespBinaryBitmap) {
		this.isoRespBinaryBitmap = isoRespBinaryBitmap;
	}
	public String getNewLineReq() {
		return newLineReq;
	}
	public void setNewLineReq(String newLineReq) {
		this.newLineReq = newLineReq;
	}
	public String getIsoReqHeaderMask() {
		return isoReqHeaderMask;
	}
	public void setIsoReqHeaderMask(String isoReqHeaderMask) {
		this.isoReqHeaderMask = isoReqHeaderMask;
	}
	public String getIsoReqHeaderLength() {
		return isoReqHeaderLength;
	}
	public void setIsoReqHeaderLength(String isoReqHeaderLength) {
		this.isoReqHeaderLength = isoReqHeaderLength;
	}
	public String getField11Length() {
		return field11Length;
	}
	public void setField11Length(String field11Length) {
		this.field11Length = field11Length;
	}
	public Map<String, String> getAutoGenElementMap() {
		return autoGenElementMap;
	}
	public void setAutoGenElementMap(Map<String, String> autoGenElementMap) {
		this.autoGenElementMap = autoGenElementMap;
	}
	public Map<String, String> getTranslationElementMap() {
		return translationElementMap;
	}
	public void setTranslationElementMap(Map<String, String> translationElementMap) {
		this.translationElementMap = translationElementMap;
	}
	public String getKeepAlive() {
		return keepAlive;
	}
	public void setKeepAlive(String flag) {
		this.keepAlive = flag;
	}
}
