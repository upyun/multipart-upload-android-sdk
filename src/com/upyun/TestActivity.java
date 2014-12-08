package com.upyun;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;

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

	// 空间名
	String bucket = "";
	// 表单密钥
	String formApiSecret = "";
	// 本地文件
	private static final String localFilePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator + "test.jpg";
	// 保存到又拍云的路径
	String savePath = "/block.png";
	// 超时时间
	long expiration = Calendar.getInstance().getTimeInMillis() + 60;
	// 块大小
	int blockSize = 100 * 1024; // 单位：byte

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		new UploadTask().execute();
	}

	public class UploadTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			try {
				// ==========初始化上传==========
				File localFile = new File(localFilePath);
				HashMap<String, Object> paramsMap = new HashMap<String, Object>();
				// 必须参数
				paramsMap.put(Params.PATH, savePath);
				paramsMap.put(Params.EXPIRATION, expiration);
				paramsMap.put(Params.BLOCK_NUM, UpYunUtils.getBlockNum(localFile, blockSize));
				paramsMap.put(Params.FILE_SIZE, localFile.length());
				paramsMap.put(Params.FILE_MD5, UpYunUtils.md5Hex(new FileInputStream(localFile)));

				// 计算policy
				String policyForInitial = UpYunUtils.getPolicy(paramsMap);
				// 计算签名
				String signatureForInitial = UpYunUtils.getSignature(paramsMap, formApiSecret);
				// 初始化上传
				JSONObject initialResult = Uploader.initialUpload(policyForInitial,
						signatureForInitial, bucket);
				if (initialResult.has("error_code")){
					/* 出错时返回：
					 * {"X-Request-Id":"11d88831c76213d9082457e55b8ff478","message":"Bucket NotFound.","error_code":40401,"code":404}
					 * "X-Request-Id"说明：出现错误时，将该字段值提供给又拍云，可以用来排插错误
					 */
					System.out.println(initialResult.toString());
					return null;
				}
				String saveToken = initialResult.optString(Params.SAVE_TOKEN);
				String tokenSecret = initialResult.optString(Params.TOKEN_SECRET);

				// ==========上传分块==========
				TreeSet<Integer> indexs = new TreeSet<Integer>(); //记录未上传的分块下标
				// 每次上传一个分块都可以从返回值json中获取'status'参数来得到各个分块上传状况（1表示上传成功，0表示未上传），可以根据该字段得知当前的上传进度
				indexs.add(4);
				// indexs.add(6);
				// indexs.add(3);
				// indexs.add(2);
				// indexs.add(1);
				// indexs.add(0);
				// indexs.add(5);
				// indexs.add(7);
				// indexs.add(8);
				JSONObject uploadResult = Uploader.uploadBlocks(tokenSecret, bucket, saveToken,
						localFilePath, blockSize, expiration, indexs);
				System.out.println("上传分块：" + uploadResult.toString());

				if (uploadResult.getInt("code") == 200) {
					// ==========合并分块请求==========
					HashMap<String, Object> paramsMapFinish = new HashMap<String, Object>();
					paramsMapFinish.put(Params.EXPIRATION, expiration);
					paramsMapFinish.put(Params.SAVE_TOKEN, saveToken);
					String policyForFinish = UpYunUtils.getPolicy(paramsMapFinish);
					String signature = UpYunUtils.getSignature(paramsMapFinish, tokenSecret);
					JSONObject result = Uploader.uploadFinish(policyForFinish, signature, bucket);
					System.out.println("合并分块：" + result.toString());
				} else {
					// ...
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return "result";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "失败", Toast.LENGTH_LONG).show();
			}
		}
	}
}