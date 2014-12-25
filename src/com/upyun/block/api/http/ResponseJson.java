package com.upyun.block.api.http;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.upyun.block.api.common.Params;

public class ResponseJson {
	public static String errorResponseJsonFormat(int statusCode, Header[] headers, byte[] responseBody) {
		String result = "";
		try {
			if (responseBody != null) {
				String bodyString = new String(responseBody);
				
					JSONObject obj = new JSONObject(bodyString);
					obj.put(Params.CODE, statusCode);
					for(Header header : headers){
						if (Params.X_Request_Id.equals(header.getName())){
							obj.put(Params.X_Request_Id, header.getValue());
						}
					}
					result = obj.toString();
				
			} else {
				JSONObject obj = new JSONObject();
				obj.put(Params.CODE, 408);
				obj.put(Params.ERROR_CODE, 40800);
				obj.put(Params.X_Request_Id, "NONE");
				obj.put(Params.MESSAGE, "There is nothing responsed, mybe timeout happend.");
				result = obj.toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String okResposneJsonFormat(int statusCode, Header[] headers, byte[] responseBody) {
		String result = "";
		try {
			if (responseBody != null) {
				String bodyString = new String(responseBody);
				JSONObject obj = new JSONObject(bodyString);
				obj.put(Params.CODE, statusCode);
				result = obj.toString();
			} else {
				JSONObject obj = new JSONObject();
				obj.put(Params.CODE, 200);
				result = obj.toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String exceptionJsonFormat(int error_code, String errorMsg){
		String result = "";
		try {
			JSONObject obj = new JSONObject();
			obj.put(Params.CODE, 500);  //server error
			obj.put(Params.ERROR_CODE, error_code);
			obj.put(Params.MESSAGE, errorMsg);
			obj.put(Params.X_Request_Id, "NONE");
			result = obj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
