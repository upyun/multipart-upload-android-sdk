package com.upyun.block.api.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.upyun.block.api.common.Params;
import com.upyun.block.api.exception.UpYunException;
import com.upyun.block.api.http.HttpManager;
import com.upyun.block.api.http.PostData;
import com.upyun.block.api.http.ResponseJson;
import com.upyun.block.api.listener.CompleteListener;
import com.upyun.block.api.listener.LoadingCompleteListener;
import com.upyun.block.api.listener.LoadingProgressListener;
import com.upyun.block.api.listener.ProgressListener;
import com.upyun.block.api.utils.UpYunUtils;

public class BlockUploader implements Runnable{
	private String host = "http://m0.api.upyun.com/";
	private String bucket;
	private int blockSize = 500 * 1024;
	private long expiration = Calendar.getInstance().getTimeInMillis() + 60*1000; // 60s
	private ProgressListener progressListener = null;
	private CompleteListener completeListener = null;
	private File localFile;
	private HttpManager httpManager;
	
	private String userPolicy;
	private String userSignature;
	private int totalBlockNum;
	private String saveToken;
	private String tokenSecret;
	private RandomAccessFile randomAccessFile = null;
	private long fileSize;
	private int[] blockIndex;
	private int historyUploadSize = 0;
	
	public BlockUploader(HttpManager httpManager, String host, String bucket, File localFile, int blockSize, long expiration,
			String policy, String signature, ProgressListener progressListener, CompleteListener completeListener) {
		super();
		this.httpManager = httpManager;
		this.host = host;
		this.bucket = bucket;
		this.blockSize = blockSize;
		this.expiration = expiration;
		this.progressListener = progressListener;
		this.completeListener = completeListener;
		this.localFile = localFile;
		this.userPolicy = policy;
		this.userSignature = signature;
	}

	@Override
	public void run() {
		try {
			this.randomAccessFile = new RandomAccessFile(this.localFile, "r");
			this.fileSize = this.localFile.length();
			this.totalBlockNum = UpYunUtils.getBlockNum(this.localFile, this.blockSize);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UpYunException e) {
			e.printStackTrace();
		}
		nextTask(Params.INIT_REQUEST, -1);
	}
	
	/**
	 * index 为 blockIndex的下标
	 * 
	 * @param type
	 * @param index
	 */
	private void nextTask(final String type, final int index) {
		if (Params.INIT_REQUEST.equals(type)) {
			RequestParams requestParams = new RequestParams();
			requestParams.put(Params.POLICY, userPolicy);
			requestParams.put(Params.SIGNATURE, userSignature);
			LoadingCompleteListener loadingCompleteListener = new LoadingCompleteListener(){

				@Override
				public void result(boolean isSuccess, String response, String error) {
					if (!isSuccess) {
						completeListener.result(false, null, error);
					} else {
						try {
							JSONObject initialResult = new JSONObject(response);
							saveToken = initialResult.optString(Params.SAVE_TOKEN);
							tokenSecret = initialResult.optString(Params.TOKEN_SECRET);
							JSONArray array = initialResult.getJSONArray(Params.STATUS);
							blockIndex = getBlockIndex(array);
							
							if (blockIndex.length == 0) {
								nextTask(Params.MERGE_REQUEST, -1);
								return;
							}
	
							if (blockIndex.length != 0) {
								// 上传分块
								nextTask(Params.BLOCK_UPLOAD, 0);
							}
						} catch (Exception err) {
							String exceptionJson = ResponseJson.exceptionJsonFormat(Params.INIT_REQUEST_ERROR, err.getMessage());
							completeListener.result(false, null, exceptionJson);
							err.printStackTrace();
						}
					}
				}
			};
			httpManager.doPost(getUrl(this.bucket), requestParams, null, loadingCompleteListener);
		} else if (Params.MERGE_REQUEST.equals(type)) {
			HashMap<String, Object> paramsMapFinish = new HashMap<String, Object>();
			paramsMapFinish.put(Params.EXPIRATION, expiration);
			paramsMapFinish.put(Params.SAVE_TOKEN, saveToken);
			String policyForMerge = UpYunUtils.getPolicy(paramsMapFinish);
			String signatureForMerge = UpYunUtils.getSignature(paramsMapFinish, tokenSecret);
			
			RequestParams requestParams = new RequestParams();
			requestParams.put(Params.POLICY, policyForMerge);
			requestParams.put(Params.SIGNATURE, signatureForMerge);
			
			LoadingCompleteListener loadingCompleteListener = new LoadingCompleteListener() {

				@Override
				public void result(boolean isSuccess, String response, String error) {
					if (!isSuccess) {
						completeListener.result(false, null, error);
					} else {
						completeListener.result(true, response, null);
					}
				}
			};
			httpManager.doPost(getUrl(this.bucket), requestParams, null, loadingCompleteListener);
		} else if (Params.BLOCK_UPLOAD.equals(type)) {
			byte[] block = null;
			try {
				block = readBlockByIndex(index);
			} catch (UpYunException e) {
				String exceptionJson = ResponseJson.exceptionJsonFormat(Params.BLOCK_UPLOAD_ERROR, e.getMessage());
				completeListener.result(false, null, exceptionJson);
				e.printStackTrace();
			}
			
			LoadingCompleteListener loadingCompeteListener = new LoadingCompleteListener() {

				@Override
				public void result(boolean isSuccess, String response, String error) {
					if (!isSuccess) {
						completeListener.result(false, response, null);
					} else {
						if (index == (blockIndex.length-1)) {
							nextTask(Params.MERGE_REQUEST, -1);
						}else {
							nextTask(Params.BLOCK_UPLOAD, index+1);
						}
					}
				}
			};
			
			LoadingProgressListener loadingProgressListener = new LoadingProgressListener() {
				
				@Override
				public void onProgress(int bytesWritten, int blockSize) {
					if (progressListener != null) {
						int showSize = historyUploadSize + bytesWritten;
						if (showSize > fileSize) {              //do some trick
							showSize = (int) (fileSize - 1000);
							if (showSize < 0) {
								showSize = 0;
							}
						}
						progressListener.transferred(showSize, fileSize);
						if (blockSize == bytesWritten) {
							historyUploadSize = historyUploadSize + blockSize;
						}
					}
				}
			};
			
			HashMap<String, Object> policyMap = new HashMap<String, Object>();
			policyMap.put(Params.SAVE_TOKEN, saveToken);
			policyMap.put(Params.EXPIRATION, expiration);
			policyMap.put(Params.BLOCK_INDEX, blockIndex[index]);
			policyMap.put(Params.BLOCK_MD5, UpYunUtils.md5Hex(block));
			String policy = UpYunUtils.getPolicy(policyMap);
			String signature = UpYunUtils.getSignature(policyMap, this.tokenSecret);
			
			Map<String ,String> map = new HashMap<String, String>();
			map.put(Params.POLICY, policy);
			map.put(Params.SIGNATURE, signature);
			PostData postData = new PostData();
			postData.data = block;
			postData.fileName = "block";
			postData.params = map;
			httpManager.doMutipartPost(getUrl(this.bucket), postData, loadingProgressListener, loadingCompeteListener);
		}
	}

	private String getUrl(String bucket) {
		return this.host + bucket + "/";
	}
	
	/**
	 * 从文件中读取块
	 * 
	 * index begin at 0
	 * 
	 * @param index
	 * @return
	 * @throws IOException
	 */
	private byte[] readBlockByIndex(int index) throws UpYunException {
		if (index > this.totalBlockNum) {
			Log.e("Block index error", "the index is bigger than totalBlockNum.");
			throw new UpYunException("readBlockByIndex: the index is bigger than totalBlockNum.");
		}
		byte[] block = new byte[this.blockSize];
		int readedSize = 0;
		try {
			int offset ;
			if (blockIndex[index] == 0) {
				offset = 0;
			} else {
				offset = (blockIndex[index]) * blockSize;
			}
			randomAccessFile.seek(offset);
			readedSize = randomAccessFile.read(block, 0, blockSize);
		} catch (IOException e) {
			throw new UpYunException(e.getMessage());
		}
		
		// read last block, adjust byte size
		if (readedSize < blockSize) {
			byte[] notFullBlock = new byte[readedSize];
			System.arraycopy(block, 0, notFullBlock, 0, readedSize);
			return notFullBlock;
		}
		return block;
	}

	/**
	 * 获取没有上传的分块下标
	 * 
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	private int[] getBlockIndex(JSONArray array) throws JSONException{
		int size = 0;
		for (int i = 0; i < array.length(); i++) {
			if (array.getInt(i) == 0) {
				size ++;
			}
		}
		// 获取未上传的块下标
		int[] blockIndex = new int[size];
		int index = 0;
		for (int i = 0; i < array.length(); i++) {
			if (array.getInt(i) == 0) {
				blockIndex[index] = i;
				index ++;
			}
		}
		return blockIndex;
	}

}
