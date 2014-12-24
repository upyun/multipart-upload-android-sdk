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
	
	public static String exceptionJsonFormat(int error_code, String errorMsg){
		JSONObject obj = new JSONObject();
		try {
			obj.put(Params.CODE, 500);  //server error
			obj.put(Params.ERROR_CODE, error_code);
			obj.put(Params.MESSAGE, errorMsg);
			obj.put(Params.X_Request_Id, "NONE");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return obj.toString();
	}
}
