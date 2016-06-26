package com.bizvane.ishop.entity;

/**
 * Created by lixiang on 2016/6/22.
 *
 * @@version
 */
public class MessageTemplate {
    private int id;
    //模板编号
    private String tem_code;
    //模板名称
    private String tem_name;
    //模板内容
    private String tem_content;
    //是否可用
    private String isactive;

    //修改日期
    private String modified_date;
    //修改人
    private String modifier;
    //创建日期
    private String created_date;
    //创建人
    private String creater;
    //消息类型
    private String type_code;

    private Message_type message_type;

    //企业编号
    private String corp_code;
    private Corp corp;

    public MessageTemplate() {
    }

    public Message_type getMessage_type() {
        return message_type;
    }

    public void setMessage_type(Message_type message_type) {
        this.message_type = message_type;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTem_code() {
        return tem_code;
    }

    public void setTem_code(String tem_code) {
        this.tem_code = tem_code;
    }

    public String getTem_name() {
        return tem_name;
    }

    public void setTem_name(String tem_name) {
        this.tem_name = tem_name;
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

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }


    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }
}
