package com.iexceed.webcontainer.utils.hash;

import org.json.JSONObject;
import org.json.JSONException;
/**
 * 
 * @author arthanarisamy
 *
 */
public interface IAppzillonHashing {

	/**
	 * 
	 * @param json
	 * @param lString
	 * @return
	 * @throws JSONException
	 */
	JSONObject generateHashedPin(JSONObject json, String lString) throws JSONException;
}
