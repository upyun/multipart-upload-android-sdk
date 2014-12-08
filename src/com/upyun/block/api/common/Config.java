package com.upyun.block.api.common;

import java.util.Calendar;

public class Config {
	//块最小1M
	public static final int MIN_BLOCK_SIZE = 100 * 1024; 
	
	//请求地址
	public static final String HOST = "http://m0.api.upyun.com/";
	
	public static final String boundary = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	//超时时间
	public static long expiration = Calendar.getInstance().getTimeInMillis() + 60;
	
	//客户分块大小
	public static int blockSize = 100 * 1024;
}
