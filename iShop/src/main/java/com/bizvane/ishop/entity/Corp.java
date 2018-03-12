package com.bizvane.ishop.entity;

import com.alibaba.fastjson.JSONObject;

import java.lang.*;
import java.util.List;

/**
 * Created by Administrator on 2016/5/19.
 */
public class Corp {
    private int id;
    //企业编号
    private String corp_code;
    //企业名称
    private String corp_name;
    //地址
    private String address;
    //联系人
    private String contact;
    //企业默认客服
    private String cus_user_code;
    //联系电话
    private String contact_phone;
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
    //图片路径
    private String avatar;

    private String   use_offline;


    private List<CorpWechat> wechats;

    private List<JSONObject> cus_user;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Corp() {
    }

    public Corp(int id) {
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

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact_phone() {
        return contact_phone;
    }

    public void setContact_phone(String contact_phone) {
        this.contact_phone = contact_phone;
    }

    public String getCus_user_code() {
        return cus_user_code;
    }

    public void setCus_user_code(String cus_user_code) {
        this.cus_user_code = cus_user_code;
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

//    public String getApp_id() {
//        return app_id;
//    }
//
//    public void setApp_id(String app_id) {
//        this.app_id = app_id;
//    }
//
//    public String getIs_authorize() {
//        return is_authorize;
//    }
//
//    public void setIs_authorize(String is_authorize) {
//        this.is_authorize = is_authorize;
//    }


    public List<CorpWechat> getWechats() {
        return wechats;
    }

    public void setWechats(List<CorpWechat> wechats) {
        this.wechats = wechats;
    }

    public List<JSONObject> getCus_user() {
        return cus_user;
    }

    public void setCus_user(List<JSONObject> cus_user) {
        this.cus_user = cus_user;
    }

    public String getUse_offline() {
        return use_offline;
    }

    public void setUse_offline(String use_offline) {
        this.use_offline = use_offline;
    }
}
