### SDK 使用方式

``` java

ProgressListener progressListener = new ProgressListener() {
					@Override
					public void transferred(long transferedBytes, long totalBytes) {
						// do something...
						System.out.println("trans:" + transferedBytes + "; total:" + totalBytes);
					}
				};
				
				CompleteListener completeListener = new CompleteListener() {
					@Override
					public void result(boolean isComplete, String result, String error) {
						// do something...
						System.out.println("isComplete:"+isComplete+";result:"+result+";error:"+error);
					}
				};
				
				UploaderManager uploaderManager = UploaderManager.getInstance(bucket);
				uploaderManager.setConnectTimeout(60);  //设置超时时间
				uploaderManager.setResponseTimeout(60);
				uploaderManager.setHost("http://m0.api.upyun.com/");//设置又拍云host，默认为:http://m0.api.upyun.com/
				uploaderManager.setBlockSize(500 * 1024);//分块最小为500 * 1024，如果小于改值将抛出异常
				uploaderManager.setExpiration(60*1000);//设置超时时间，默认60s
				
				Map<String, Object> paramsMap = uploaderManager.fetchFileInfoDictionaryWith(localFile, savePath);
				paramsMap.put("return_url", "http://httpbin.org/get"); //设置回调地址
				// signature & policy 建议从服务端获取
				String policyForInitial = UpYunUtils.getPolicy(paramsMap);
				String signatureForInitial = UpYunUtils.getSignature(paramsMap, formApiSecret);
				uploaderManager.upload(policyForInitial, signatureForInitial, localFile, progressListener, completeListener);
				

```


### 返回值说明

#### 成功

```json

{

    "code":200,
    "last_modified":1418394078,
    "mimetype":"image\/jpeg",
    "file_size":"156881",
    "image_frames":1,
    "bucket_name":"picture-test-space",
    "image_width":1280,
    "path":"\/test11.png",
    "image_height":822,
    "signature":"07a061373d6653ae0439a924e8366d48"
}

```

#### 302重定向返回

```json

{
    "code": xxx,
    ...//回调服务器返回的json内容
}

```

#### 错误返回

又拍错误返回：

```json

{
    "code":404,
    "message":"Bucket NotFound.",
    "X-Request-Id":"235dd1a77bccac5363cbf0157c037ffa", //提供又拍云该id，可以更好的排查错误
    "error_code":40401
}

```

回调服务器错误返回：

```json

{
    "code":400,
    ...//回调服务器返回json内容
}

```
