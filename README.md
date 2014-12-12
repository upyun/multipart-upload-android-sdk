### 返回值说明

#### 成功

```json

{
    "last_modified":1418394078,
    "mimetype":"image\/jpeg",
    "file_size":"156881",
    "image_frames":1,
    "bucket_name":"picture-test-space",
    "image_width":1280,
    "path":"\/test11.png",
    "image_height":822,
    "code":200,
    "signature":"07a061373d6653ae0439a924e8366d48"
}

```

#### 302重定向返回

```json

{
    "location":"www.google.com?bucket_name=picture-test-space&path=%2Ftest9.png&
                image_width=1280&image_height=822&
                image_frames=1&mimetype=image%2Fjpeg&file_size=156881&last_modified=1418393721&
                signature=8efaae3a8791b7758b092be37386ae85",
    
    "code”:302
}

```

#### 错误返回

```json

{
    "message":"Bucket NotFound.",
    "X-Request-Id":"235dd1a77bccac5363cbf0157c037ffa", //提供又拍云该id，可以更好的排查错误
    "error_code":40401,
    "code":404
}

```
