package com.upyun.block.api.listener;

public interface ProgressListener {
    void transferred(long transferedBytes, long totalBytes);
}
