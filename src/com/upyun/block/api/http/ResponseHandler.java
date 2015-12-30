package com.upyun.block.api.http;

import android.os.Looper;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.upyun.block.api.listener.LoadingCompleteListener;
import com.upyun.block.api.listener.LoadingProgressListener;

import cz.msebera.android.httpclient.Header;


public class ResponseHandler extends AsyncHttpResponseHandler {
	private LoadingCompleteListener loadingCompleteListener;
	private LoadingProgressListener loadingProgressListener;
	
	public ResponseHandler(LoadingCompleteListener loadingCompleteListener, LoadingProgressListener loadingProgressListener) {
		super(Looper.getMainLooper());
		this.loadingCompleteListener = loadingCompleteListener;
		this.loadingProgressListener = loadingProgressListener;
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
			 error) {
		String standardResponse = ResponseJson.errorResponseJsonFormat(statusCode, headers, responseBody);
		this.loadingCompleteListener.result(false, null, standardResponse);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
		String standardResponse = ResponseJson.okResposneJsonFormat(statusCode, headers, responseBody);
		this.loadingCompleteListener.result(true, standardResponse, null);
	}
	
    @Override
    public void onProgress(long bytesWritten, long totalSize) {
        if (loadingProgressListener != null) {
        	loadingProgressListener.onProgress(bytesWritten, totalSize);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
