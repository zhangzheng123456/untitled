package com.bizvane.ishop.entity;

import java.util.Date;

/**
 * Created by Administrator on 2016/5/19.
 */
public class Message {
    private int id;
    //消息编号
    private String tem_code;
    //消息内容
    private String tem_content;
    //是否可用
    private String isactive;
    //企业编号
    private String corp_code;
    //修改日期
    private String modified_date;
    //修改人
    private String modifier;
    //创建日期
    private String created_date;
    //消息类型
    private String type_code;
    private Corp corp;
    private Message_type message_type;

    public Message() {
    }


    public String getTem_code() {
        return tem_code;
    }

    public void setTem_code(String tem_code) {
        this.tem_code = tem_code;
    }

    public String getTem_content() {
        return tem_content;
    }

    public void setTem_content(String tem_content) {
        this.tem_content = tem_content;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public Message_type getMessage_type() {
        return message_type;
    }

    public void setMessage_type(Message_type message_type) {
        this.message_type = message_type;
    }
}
