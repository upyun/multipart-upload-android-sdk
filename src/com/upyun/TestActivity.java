package com.upyun;

import java.io.File;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.upyun.block.api.main.Uploader;
import com.upyun.block.api.main.CountingHttpEntity.ProgressListener;

public class TestActivity extends Activity {

	// 空间名
	String bucket = "picture-test-space";
	// 表单密钥
	String formApiSecret = "w3mRPyWWOHwGoE0CN6C57AX9pac=";
	// 本地文件路径
	private static final String localFilePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator + "test.jpg";
	// 保存到又拍云的路径
	String savePath = "/bbb.png";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		new UploadTask().execute();
	}

	public class UploadTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			/*
			 * 设置进度条回掉函数
			 * 
			 * 注意：由于在计算发送的字节数中包含了图片以外的其他信息，最终上传的大小总是大于图片实际大小，
			 * 为了解决这个问题，代码会判断如果实际传送的大小大于图片，就将实际传送的大小设置成'totalBytes-1000'（最小为0）
			 */
			Uploader.listener = new ProgressListener(){
				@Override
				public void transferred(long transferedBytes, long totalBytes) {
					//do something...
					System.out.println("trans:"+transferedBytes+"; total:"+totalBytes);
				}
			};

			try {
				JSONObject result = Uploader.upload(bucket, formApiSecret, localFilePath, savePath, null);
				System.out.println(result);
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