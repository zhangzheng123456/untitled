package com.bizvane.ishop.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2016/1/13.
 */
public  class WebConnection {
    private HttpURLConnection httpURLConnection=null;
    private static CookieManager cm=null;

    public WebConnection() {
        if(cm==null) {
            synchronized (CookieManager.class) {
                if (cm == null) {
                    cm = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
                    CookieHandler.setDefault(cm);
                }
            }
        }
    }

    public  HttpResult Request(String url,String method,Map<String,String> params,byte[] data ) throws IOException {
        httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        httpURLConnection.setReadTimeout(60000);
        httpURLConnection.setConnectTimeout(60000);
        httpURLConnection.setRequestMethod(method);
        if(params!=null){
            for(String key : params.keySet()){
                httpURLConnection.setRequestProperty(key,params.get(key));
            }
        }

        if(httpURLConnection.getRequestMethod()=="POST"){
            httpURLConnection.setDoOutput(true);
        }

        if(data!=null&&data.length>0){
            httpURLConnection.getOutputStream().write(data);
        }



        String page=GetPageContent(httpURLConnection);
        return new HttpResult(httpURLConnection.getResponseCode(), httpURLConnection.getResponseMessage(),page);
    }

    private static String GetPageContent(HttpURLConnection httpURLConnection) throws IOException {

        //BufferedInputStream bis=new BufferedInputStream(httpURLConnection.getInputStream());

      //  StringBuffer sb=new StringBuffer();

    //    InputStreamReader is = new InputStreamReader(httpURLConnection.getInputStream());

        InputStream is=httpURLConnection.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

        byte[] buff=new byte[1024];
        int len=0;
        while ((len=is.read(buff))!=-1){
         byteArrayOutputStream.write(buff,0,len);
        }

        String page=new String(byteArrayOutputStream.toByteArray());
        return  page;
    }

    public List<HttpCookie> getCookies(){
        return cm.getCookieStore().getCookies();
    }
}
