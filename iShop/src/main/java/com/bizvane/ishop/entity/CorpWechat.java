package com.bizvane.ishop.entity;

/**
 * Created by Administrator on 2016/5/19.
 */
public class CorpWechat {
    private int id;
    //企业编号
    private String corp_code;
    //企业编号
    private String crm_corp_code;
    //微信公众号app_id
    private String app_id;
    //微信公众号app_name
    private String app_name;
    //微信公众号app_user_name
    private String app_user_name;
    //是否已授权
    private String is_authorize;
    //品牌编号
    private String brand_code;
    //key(微商城)
    private String access_key;
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
    //公众号logo
    private String app_logo;

    public CorpWechat() {
    }

    public CorpWechat(int id) {
        this.id = id;
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

    public String getCrm_corp_code() {
        return crm_corp_code;
    }

    public void setCrm_corp_code(String crm_corp_code) {
        this.crm_corp_code = crm_corp_code;
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

    public String getBrand_code() {
        return brand_code;
    }

    public void setBrand_code(String brand_code) {
        this.brand_code = brand_code;
    }

    public String getAccess_key() {
        return access_key;
    }

    public void setAccess_key(String access_key) {
        this.access_key = access_key;
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

    public String getIs_authorize() {
        return is_authorize;
    }

    public void setIs_authorize(String is_authorize) {
        this.is_authorize = is_authorize;
    }

    public String getApp_logo() {
        return app_logo;
    }

    public void setApp_logo(String app_logo) {
        this.app_logo = app_logo;
    }
}
