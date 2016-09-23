package com.bizvane.ishop.utils;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.bizvane.ishop.constant.CommonValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by ZhouZhou on 2016/9/23.
 */
public class OssUtils {
    OSSClient client = new OSSClient(CommonValue.ACCESS_KEY_ID, CommonValue.ACCESS_KEY_SECRET);

    /**
     * oss上传文件上
     * @Param bucketName oss文件夹名字
     * @Param key 文件名
     * @Param filePath 需要上传文件的路径
     */
    public void putObject(String bucketName, String key, String filePath) throws FileNotFoundException {
        // 获取指定文件的输入流
        File file = new File(filePath);
        InputStream content = new FileInputStream(file);

        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        meta.setContentLength(file.length());

        // 上传Object.
        PutObjectResult result = client.putObject(bucketName, key, content, meta);

        // 打印ETag
        System.out.println(result.getETag());
    }
}
