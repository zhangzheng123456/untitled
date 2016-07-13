package com.bizvane.ishop.entity;

/**
 * Created by lixiang on 2016/6/22.
 *
 * @@version
 */
public class MessageType {
    //消息id
    private int id;
    //消息类型编号
    private String type_code;
    //消息类型名称
    private String type_name;
    //消息类型描述
    private String type_description;
    //修改时间
    private String modified_date;
    //修改人
    private String modifier;
    //创建人
    private String creater;
    //创建时间
    private String created_date;
    //是否可用
    private String isactive;

    //企业编号
    private String corp_code;
    private Corp corp;

    public MessageType() {
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

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
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


}
