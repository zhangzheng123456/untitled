package com.bizvane.ishop.entity;

/**
 * Created by lixiang on 2016/6/22.
 *
 * @@version
 */
public class Message_type {
    private int id;
    private String type_code;
    private String type_name;
    private String type_description;
    private String modified_date;
    private String modifier;
    private String creater;
    private String created_date;
    private String isactive;

    private String corp_code;
    private Corp corp;
    private Message_type message_type;

    public Message_type() {
    }


    public Message_type getMessage_type() {
        return message_type;
    }

    public void setMessage_type(Message_type message_type) {
        this.message_type = message_type;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getType_description() {
        return type_description;
    }

    public void setType_description(String type_description) {
        this.type_description = type_description;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

}
