package com.bizvane.ishop.entity;

/**
 * Created by Administrator on 2016/5/19.
 */
public class WxTemplateContent {
    private int id;
    //企业编号
    private String corp_code;
    //微信公众号app_id
    private String app_id;
    //微信公众号app_name
    private String app_name;
    //微信公众号app_user_name
    private String app_user_name;
    //模板编号
    private String template_id;
    //模板名称
    private String template_name;
    //导航语
    private String template_first;
    //备注
    private String template_remark;
    //链接
    private String template_url;

    private String type;

    private String vip_card_type;

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

    private  String corp_name;

    private  String  vip_card_type_id;

    public String getVip_card_type_id() {
        return vip_card_type_id;
    }

    public void setVip_card_type_id(String vip_card_type_id) {
        this.vip_card_type_id = vip_card_type_id;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
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

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
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

    public String getTemplate_first() {
        return template_first;
    }

    public void setTemplate_first(String template_first) {
        this.template_first = template_first;
    }

    public String getTemplate_remark() {
        return template_remark;
    }

    public void setTemplate_remark(String template_remark) {
        this.template_remark = template_remark;
    }

    public String getTemplate_url() {
        return template_url;
    }

    public void setTemplate_url(String template_url) {
        this.template_url = template_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVip_card_type() {
        return vip_card_type;
    }

    public void setVip_card_type(String vip_card_type) {
        this.vip_card_type = vip_card_type;
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
