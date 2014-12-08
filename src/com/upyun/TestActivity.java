package com.upyun;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.upyun.block.api.utils.UpYunUtils;
import com.upyun.block.api.common.Params;
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
	String savePath = "/aaa.png";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		new UploadTask().execute();
	}

	public class UploadTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			ProgressListener listener = new ProgressListener(){
				@Override
				public void transferred(long transferedBytes, long totalBytes) {
					System.out.println("trans:"+transferedBytes+"; total:"+totalBytes);
				}
			};
			
			Uploader.listener = listener;

			try {
				JSONObject result = Uploader.upload(bucket, formApiSecret, localFilePath, savePath);
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