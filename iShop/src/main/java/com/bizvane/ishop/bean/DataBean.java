package com.bizvane.ishop.bean;

import com.google.gson.GsonBuilder;

/**
 * Created by Administrator on 2016/5/20.
 */
public class DataBean {
    private String id;
    private String code;
    private String message;
    private String remark;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getJsonStr() {

        GsonBuilder gb = new GsonBuilder();
        gb.disableHtmlEscaping();
        return gb.create().toJson(this);

    }
}
