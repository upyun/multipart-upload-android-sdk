package com.upyun.block.api.listener;

public interface LoadingProgressListener {
	void onProgress(int bytesWritten, int totalSize);
}
