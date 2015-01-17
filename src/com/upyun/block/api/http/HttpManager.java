package com.upyun.block.api.http;

import java.io.ByteArrayInputStream;

import org.apache.http.client.params.ClientPNames;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.upyun.block.api.listener.LoadingCompleteListener;
import com.upyun.block.api.listener.LoadingProgressListener;

public class HttpManager {
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	public HttpManager() {
		client.setConnectTimeout(60*1000);  //default 60s
		client.setResponseTimeout(60*1000);
		client.setEnableRedirects(true);
		client.getHttpClient().getParams().setParameter(ClientPNames.MAX_REDIRECTS, 3);
	}
	
	/**
	 * 
	 * @param connectTimeout 单位：s
	 */
	public void setConnectTimeout(int connectTimeout) {
		client.setConnectTimeout(connectTimeout * 1000);
	}
	
	/**
	 * 
	 * @param responseTimeout 单位：s
	 */
	public void setResponseTimeout(int responseTimeout) {
		client.setResponseTimeout(responseTimeout * 1000);
	}
	
	public void doPost(String URL, RequestParams requestParams, LoadingProgressListener loadingProgressListener,
			LoadingCompleteListener loadingCompletionListener){
		AsyncHttpResponseHandler handler = new ResponseHandler(loadingCompletionListener, loadingProgressListener);
		client.post(URL, requestParams, handler);
	}
	
	public void doMutipartPost(String url, PostData postData, LoadingProgressListener loadingProgressListener,
			LoadingCompleteListener loadingCompletionListener) {
		RequestParams requestParams = new RequestParams(postData.params);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(postData.data);
		requestParams.put("file", byteArrayInputStream, postData.fileName);
		
		AsyncHttpResponseHandler handler = new ResponseHandler(loadingCompletionListener, loadingProgressListener);
		client.post(url, requestParams, handler);
	}
	
}
