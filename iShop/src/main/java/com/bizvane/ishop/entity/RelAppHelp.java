package com.bizvane.ishop.entity;

/**
 * 关联帮助功能
 * Created by nanji on 2017/2/23.
 */
public class RelAppHelp {
    private String id;
//    //企业编号
//    private String corp_code;
    //编号
    private String rel_help_code;

    //关联帮助编号
    private String app_help_code;
    //标题
    private String app_help_title;
    //内容
    private String app_help_content;
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
    private String app_help_name;
    private AppHelp appHelp;

    public String getApp_help_name() {
        return app_help_name;
    }

    public void setApp_help_name(String app_help_name) {
        this.app_help_name = app_help_name;
    }

    public AppHelp getAppHelp() {
        return appHelp;
    }

    public void setAppHelp(AppHelp appHelp) {
        this.appHelp = appHelp;
    }

//    public String getCorp_name() {
//        return corp_name;
//    }
//
//    public void setCorp_name(String corp_name) {
//        this.corp_name = corp_name;
//    }
//
//    public Corp getCorp() {
//        return corp;
//    }
//
//    public void setCorp(Corp corp) {
//        this.corp = corp;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public String getCorp_code() {
//        return corp_code;
//    }
//
//    public void setCorp_code(String corp_code) {
//        this.corp_code = corp_code;
//    }

    public String getApp_help_code() {
        return app_help_code;
    }

    public void setApp_help_code(String app_help_code) {
        this.app_help_code = app_help_code;
    }

    public String getRel_help_code() {
        return rel_help_code;
    }

    public void setRel_help_code(String rel_help_code) {
        this.rel_help_code = rel_help_code;
    }

    public String getApp_help_title() {
        return app_help_title;
    }

    public void setApp_help_title(String app_help_title) {
        this.app_help_title = app_help_title;
    }

    public String getApp_help_content() {
        return app_help_content;
    }

    public void setApp_help_content(String app_help_content) {
        this.app_help_content = app_help_content;
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
