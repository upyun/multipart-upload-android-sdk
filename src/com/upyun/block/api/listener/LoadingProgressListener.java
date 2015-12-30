package com.upyun.block.api.listener;

public interface LoadingProgressListener {
	void onProgress(long bytesWritten, long totalSize);
}
