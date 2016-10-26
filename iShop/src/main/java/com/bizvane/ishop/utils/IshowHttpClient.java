package com.bizvane.ishop.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class IshowHttpClient {
	 /** 
     * 发送 get请求 
     */  
    public static final String get(String url1) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String context="";
        try {  
        	 URL url = new URL(url1);
			 URI uri = new URI(url.getProtocol(), url.getHost() + ":" + url.getPort(),url.getPath(), url.getQuery(), null);
	            // 创建httpget.      
            HttpGet httpget = new HttpGet(uri);  
            System.out.println("executing request " + httpget.getURI());  
            // 执行get请求.    
            CloseableHttpResponse response = httpclient.execute(httpget);  
            // 获取响应实体   
            HttpEntity entity = response.getEntity();  
            System.out.println("--------------------------------------");  
            // 打印响应状态    
            System.out.println(response.getStatusLine());  
            if (entity != null) {  
                // 打印响应内容    
            	context = EntityUtils.toString(entity, "UTF-8");
            	System.out.println(context);	               
            }  
           response.close();   
           return context;            
        } catch (Exception e) {  
            e.printStackTrace(); 
            return "Exception";
        } 
        finally {  
        	   //          关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
    } 
    /** 
     * 发送 post请求访问本地应用并根据传递参数不同返回不同结果 
     */  
    public static final String post(String url,JSONObject param) {
    	 String context="";
         // 创建默认的httpClient实例.    
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {
        	URL url1 = new URL(url);   
            URI uri = new URI(url1.getProtocol(), url1.getHost(), url1.getPath(), url1.getQuery(), null); 
        	// 创建httppost  
        	HttpPost httppost = new HttpPost(uri);  
        	// 绑定到请求 Entry
        	StringEntity se = new StringEntity(param.toString() ,"UTF-8");//解决传送的Json中文乱码
			httppost.setEntity(se);
			// 发送请求 
            CloseableHttpResponse response = httpclient.execute(httppost); 
            // 得到应答的字符串，这也是一个 JSON 格式保存的数据
            HttpEntity entity = response.getEntity();  
            if (entity != null) {           
               context = EntityUtils.toString(entity, "UTF-8");
            }   
           response.close(); 
           return context;
        } catch (Exception e) {  
            e.printStackTrace(); 
            return "Exception";
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }

}
