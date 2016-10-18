package com.bizvane.ishop.utils;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.bizvane.ishop.constant.CommonValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public  void putObject(String bucketName, String key, String filePath) throws FileNotFoundException {
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

    public static List<String> getHtmlImageSrcList(String htmlText)
    {
        List<String> imgSrc = new ArrayList<String>();
//        List<String> _imgSrc = new ArrayList<String>();
//        Matcher m2 = Pattern.compile("_src=\"?(.*?)(\"|>|\\s+)").matcher(htmlText);
//        while(m2.find())
//        {
//            System.out.println("--------"+m2.group(1));
//            _imgSrc.add(m2.group(1));
//        }
//        for (int i=0;i<_imgSrc.size();i++){
//           htmlText= htmlText.replaceAll(_imgSrc.get(i),"");
//            System.out.println("======="+htmlText);
//        }
        Matcher m = Pattern.compile("_src=\"?(.*?)(\"|>|\\s+)").matcher(htmlText);
        while(m.find())
        {
            imgSrc.add(m.group(1));
        }
        return imgSrc;
    }

    public void deleteObject(String bucketName, String key) {
        // 删除Object
        client.deleteObject(bucketName, key);
    }
}
