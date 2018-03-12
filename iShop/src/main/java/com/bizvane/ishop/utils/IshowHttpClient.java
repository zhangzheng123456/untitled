package com.bizvane.ishop.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class IshowHttpClient {
	 /** 
     * 发送 get请求 
     */  
    public static final String get(String url1) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
//        httpclient = wrapClient(httpclient);
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

    /**
     * 避免HttpClient的”SSLPeerUnverifiedException: peer not authenticated”异常
     * 不用导入SSL证书
     * @author shipengzhi(shipengzhi@sogou-inc.com)
     *
     */

    public static CloseableHttpClient wrapClient(CloseableHttpClient base) {
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            };
            sslcontext.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
            ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
            return new DefaultHttpClient(mgr, base.getParams());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


}
