/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.message;

import com.iexceed.appzillon.json.JSONObject;

/**
 *
 * @author arthanarisamy
 */
public class Request {

    private JSONObject requestJson = null;

    private Request() {

    }

    public static Request getInstance() {
        return new Request();
    }

    public JSONObject getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(JSONObject requestJson) {
        this.requestJson = requestJson;
    }

}
