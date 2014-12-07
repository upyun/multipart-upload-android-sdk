package com.upyun;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.upyun.block.api.utils.UpYunUtils;
import com.upyun.block.api.main.Uploader;
import com.upyun.block.api.statics.Params;

public class TestActivity extends Activity {

	// 保存路径
	String savePath = "/block.png";
	// 超时时间
	long expiration = Calendar.getInstance().getTimeInMillis() + 60;
	// 块大小
	int blockSize = 100 * 1024; // 块大小的设置应该要考虑到android内存的大小
	// 空间名
	String bucket = "picture-test-space";
	// 表单密钥
	String formApiSecret = "w3mRPyWWOHwGoE0CN6C57AX9pac=";

	//本地文件
	private static final String localFilePath = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ "test.jpg"; // 来源文件

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		new UploadTask().execute();

	}

	public class UploadTask extends AsyncTask<Void, Void, String> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(Void... params) {
			String string = null;

			try {
				//==========初始化上传==========
				File localFile = new File(localFilePath);
				HashMap<String, Object> paramsMap = new HashMap<String, Object>();
				//必须参数
				paramsMap.put(Params.PATH, savePath);
				paramsMap.put(Params.EXPIRATION, expiration);
				paramsMap.put(Params.BLOCK_NUM, UpYunUtils.getBlockNum(localFile, blockSize));
				paramsMap.put(Params.FILE_SIZE, localFile.length());
				paramsMap.put(Params.FILE_MD5, UpYunUtils.md5Hex(new FileInputStream(localFile)));
				//还可以加上其他的额外处理参数...
				
				//计算policy
				String policyForInitial = UpYunUtils.getPolicy(paramsMap);
				//计算签名
				String signatureForInitial = UpYunUtils.getSignature(paramsMap, formApiSecret);
				//初始化上传
				JSONObject initialResult = Uploader.initialUpload(policyForInitial, signatureForInitial,bucket);
				String saveToken = initialResult.optString(Params.SAVE_TOKEN);
				String tokenSecret = initialResult.optString(Params.TOKEN_SECRET);
				System.out.println("初始化上传--save_token:"+saveToken);
				System.out.println("初始化上传--token_secret:"+tokenSecret);

				//==========上传分块==========
				TreeSet<Integer> indexs = new TreeSet<Integer>();
				//每次上传一个分块都可以从返回值json中获取'status'参数来了解各个分块上传状况（1表示上传成功），以下模拟乱序上传
//				indexs.add(4);
//				indexs.add(6);
//				indexs.add(3);
//				indexs.add(2);
//				indexs.add(1);
//				indexs.add(0);
//				indexs.add(5);
//				indexs.add(7);
//				indexs.add(8);
				JSONObject uploadResult = Uploader.uploadBlocks(tokenSecret, bucket, saveToken, localFilePath, blockSize, expiration, indexs);
				System.out.println("上传分块："+uploadResult.toString());
				
				if(uploadResult.getInt("code") == 200){
					//==========合并分块请求==========
					HashMap<String, Object> paramsMapFinish = new HashMap<String, Object>();
					paramsMapFinish.put(Params.EXPIRATION, expiration);
					paramsMapFinish.put(Params.SAVE_TOKEN, saveToken);
					String policyForFinish= UpYunUtils.getPolicy(paramsMapFinish);
					String signature = UpYunUtils.getSignature(paramsMapFinish, tokenSecret);
					JSONObject result = Uploader.uploadFinish(policyForFinish, signature, bucket);
					System.out.println("合并分块："+result.toString());
				}else{
					//...
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return "result";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(getApplicationContext(), "失败", Toast.LENGTH_LONG)
						.show();

			}
		}

	}
}