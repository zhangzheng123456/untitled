package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2017/2/21.
 */
public class AppHelp {
    private String id;
    //编号
    private String app_help_code;
    //名称
    private String app_help_name;
    //公司编号
//    private String corp_code;
    //修改时间
    private String modified_date;
    //修改人
    private String modifier;
    //创建时间
    private String created_date;
    //创建者
    private String creater;
    //显示顺序
    private String show_order;
    //是否可用
    private String isactive;
//    private String corp_name;
//    private Corp corp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApp_help_code() {
        return app_help_code;
    }

    public void setApp_help_code(String app_help_code) {
        this.app_help_code = app_help_code;
    }

    public String getApp_help_name() {
        return app_help_name;
    }

    public void setApp_help_name(String app_help_name) {
        this.app_help_name = app_help_name;
    }

//    public String getCorp_code() {
//        return corp_code;
//    }
//
//    public void setCorp_code(String corp_code) {
//        this.corp_code = corp_code;
//    }

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

    public String getShow_order() {
        return show_order;
    }

    public void setShow_order(String show_order) {
        this.show_order = show_order;
    }
}
