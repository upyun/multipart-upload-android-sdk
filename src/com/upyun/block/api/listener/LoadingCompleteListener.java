package com.upyun.block.api.listener;

public interface LoadingCompleteListener {
	void result(boolean isSuccess, String response, String error);
}
