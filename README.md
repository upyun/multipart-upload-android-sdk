##这个 SDK 将不再维护, 请使用[新版SDK](https://github.com/upyun/android-sdk)


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
