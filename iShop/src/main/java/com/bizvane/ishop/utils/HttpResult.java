package com.bizvane.ishop.utils;

/**
 * Created by yanyadong on 2017/1/13.
 */
public class HttpResult {
    int responseCode;
    String responseMessage;
    String page;

    public HttpResult(int responseCode, String responseMessage,String page) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.page=page;
    }

    public  String getPage(){return page;}

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

}
