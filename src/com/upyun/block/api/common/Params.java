package com.upyun.block.api.common;

/**
 * 参数
 * 
 * @author wangxiaolong
 */
public class Params {
	public static final String SIGNATURE = "signature"; 
	public static final String POLICY = "policy";
	// 空间保存路径
	public static final String PATH = "path"; 
	// 超时时间
	public static final String EXPIRATION = "expiration"; 
	
	/*
	 *  该字段由第一次分块上传初始化请求的返回值获得，用来标识改上传的文件。
	 *  
	 *  只要该字段能标识上传的文件，那么就能支持断点续传，否则就不能支持
	 *  
	 *  save_token能标识该上传文件需要满足的条件（以初始化上传，分块上传，合并请求为一个原子操作）：
	 *  1.原子操作中 "file_hash" 不变
	 *  2.原子操作中 分块方式不变，即"file_size"和分块大小不变
	 *  3.原子操作中 "path"不变
	 *  
	 */
	public static final String SAVE_TOKEN = "save_token";
	public static final String TOKEN_SECRET = "token_secret";
	public static final String FILE_SIZE = "file_size";
	public static final String FILE_MD5 = "file_hash";
	// 分块个数
	public static final String BLOCK_NUM = "file_blocks"; 
	public static final String BLOCK_INDEX = "block_index";
	public static final String BLOCK_MD5 = "block_hash";
	public static final String BLOCK_DATA = "file";
	// http response code
	public static final String CODE = "code";
	// 分块上传情况
	public static final String STATUS = "status";
	
	public static final String ERROR_CODE = "error_code";
	public static final String X_Request_Id = "X-Request-Id";
	public static final String MESSAGE = "message";
	public static final String LOCATION = "location";
	
	public static final String INIT_REQUEST = "INIT_REQUEST";
	public static final String BLOCK_UPLOAD = "BLOCK_UPLOAD";
	public static final String MERGE_REQUEST = "MERGE_REQUESt";
}
