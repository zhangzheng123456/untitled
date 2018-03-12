package com.bizvane.ishop.utils;

import java.io.*;
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

    /**
     *
     * @param path 临时文件路径
     * @param filename 临时文件名
     * @param url  获取编码格式的文本链接
     * @return
     */
    public   String  switchEncodingToUTF8(String path,String filename,String url){
        String filepath="";
        try{
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setReadTimeout(60000);
            httpURLConnection.setConnectTimeout(60000);
            httpURLConnection.setRequestMethod("GET");
            InputStream is=httpURLConnection.getInputStream();

            //获取文本格式
            String charset=WebConnection.getFileEncoding(path,filename,url);

            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            byte[] buff=new byte[1024];
            int len=0;
            while ((len=is.read(buff))!=-1){
                byteArrayOutputStream.write(buff,0,len);
            }
            is.close();//关闭输入流
            String content=new String(byteArrayOutputStream.toByteArray(),charset);

            byteArrayOutputStream.close();//关闭输出流

            filepath=path+"/"+filename;

            File file = new File(path,filename);
            if(file.exists()){
                file.delete();
            }
            Writer outTxt = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
            outTxt.write(content);
            outTxt.close();//关闭输出流
        }catch (Exception e){
            e.printStackTrace();
        }

        return filepath;
    }

    /**
     *
     * @param path  临时文件路径
     * @param filename  临时文件名
     * @param url 获取编码格式的文本链接
     * @return
     * @throws Exception
     */
    public static String getFileEncoding(String path,String filename,String  url) throws Exception {

        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        httpURLConnection.setReadTimeout(60000);
        httpURLConnection.setConnectTimeout(60000);
        httpURLConnection.setRequestMethod("GET");
        InputStream is=httpURLConnection.getInputStream();

        filename="temp"+filename;
        OutExeclHelper outExeclHelper=new OutExeclHelper();
        outExeclHelper.writeFile(path,filename,is);
        File file=new File(path,filename);
        FileCharsetDetector fileCharsetDetector=new FileCharsetDetector();
        String charset= fileCharsetDetector.guessFileEncoding(file);
        if(file.exists()){
            file.delete();
        }
        if(charset==null){
            charset="UTF-8";
        }
        if(charset.split(",").length>1){
            if(charset.contains("GB2312")||charset.contains("GBK")){
                charset="GBK";
            }else if(charset.contains("UTF-8")){
                charset="UTF-8";
            }else{
                charset=charset.split(",")[0];
            }
        }
        System.out.println("charset>>>>>>"+charset);
        return charset;
    }
}
