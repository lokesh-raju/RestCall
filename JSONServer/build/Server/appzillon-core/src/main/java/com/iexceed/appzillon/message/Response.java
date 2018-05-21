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
public class Response {

    private JSONObject responseJson = null;

    private Response(){
        
    }
    public static Response getInstance(){
        return new Response();
    }
    public JSONObject getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(JSONObject responseJson) {
        this.responseJson = responseJson;
    }

}
