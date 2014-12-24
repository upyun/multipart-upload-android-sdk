package com.upyun.block.api.http;

import org.apache.http.Header;
import android.os.Looper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.upyun.block.api.listener.LoadingCompleteListener;
import com.upyun.block.api.listener.LoadingProgressListener;

public class ResponseHandler extends AsyncHttpResponseHandler{
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
		this.loadingCompleteListener.result(false, null, new String(responseBody));
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
		this.loadingCompleteListener.result(true, new String(responseBody), null);
	}
	
    @Override
    public void onProgress(int bytesWritten, int totalSize) {
        if (loadingProgressListener != null) {
        	loadingProgressListener.onProgress(bytesWritten, totalSize);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
