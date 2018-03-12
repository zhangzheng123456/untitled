package com.bizvane.ishop.entity;

/**
 * Created by ZhouZhou on 2017/3/14.
 */
public class QrCode {
    private  int id;
    private String corp_code;

    private String app_id;

    private String app_name;

    private String app_user_name;
    //类型
    private String qrcode_type;
    //时效
    private String aging;

    private String qrcode_name;
    //二维码地址（链接）
    private String qrcode;
    //二维码内容
    private String qrcode_content;

    private String remark;
    //生成二维码时间
    String create_qrcode_time;
    //修改时间
    private String modified_date;
    //修改人
    private String modifier;
    //创建时间
    private String created_date;
    //创建者
    private String creater;
    //是否可用
    private String isactive;


    public String getAging() {
        return aging;
    }

    public void setAging(String aging) {
        this.aging = aging;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_user_name() {
        return app_user_name;
    }

    public void setApp_user_name(String app_user_name) {
        this.app_user_name = app_user_name;
    }

    public String getQrcode_type() {
        return qrcode_type;
    }

    public void setQrcode_type(String qrcode_type) {
        this.qrcode_type = qrcode_type;
    }

    public String getQrcode_name() {
        return qrcode_name;
    }

    public void setQrcode_name(String qrcode_name) {
        this.qrcode_name = qrcode_name;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getQrcode_content() {
        return qrcode_content;
    }

    public void setQrcode_content(String qrcode_content) {
        this.qrcode_content = qrcode_content;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreate_qrcode_time() {
        return create_qrcode_time;
    }

    public void setCreate_qrcode_time(String create_qrcode_time) {
        this.create_qrcode_time = create_qrcode_time;
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

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }
}
