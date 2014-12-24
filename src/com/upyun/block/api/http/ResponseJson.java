package com.upyun.block.api.http;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.upyun.block.api.common.Params;

public class ResponseJson {
	public static String errorResponseJsonFormat(int statusCode, Header[] headers, byte[] responseBody) {
		String bodyString = new String(responseBody);
		JSONObject obj = null;
		try {
			obj = new JSONObject(bodyString);
			obj.put(Params.CODE, statusCode);
			for(Header header : headers){
				if (Params.X_Request_Id.equals(header.getName())){
					obj.put(Params.X_Request_Id, header.getValue());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}
	
	public static String okResposneJsonFormat(int statusCode, Header[] headers, byte[] responseBody) {
		String bodyString = new String(responseBody);
		JSONObject obj = null;
		try {
			obj = new JSONObject(bodyString);
			obj.put(Params.CODE, statusCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}
}
