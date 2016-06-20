package com.bizvane.ishop.entity;

import java.util.Date;

/**
 * Created by maoweidong on 2016/2/24.
 */
public class VIPInfo {
    private int id;
    private String vip_code;
    private String vip_name;
    private String phone;
    private String birthday;
    private String vip_type;
    private String vip_card_number;
    private String sex;
    private String register_time;
    private String user_code;
    private String store_code;
    private String corp_code;
    private String modified_date;
    private String modifier;
    private String created_date;
    private String creater;
    private String isactive;

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public VIPInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVip_code() {
        return vip_code;
    }

    public void setVip_code(String vip_code) {
        this.vip_code = vip_code;
    }

    public String getVip_name() {
        return vip_name;
    }

    public void setVip_name(String vip_name) {
        this.vip_name = vip_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getVip_type() {
        return vip_type;
    }

    public void setVip_type(String vip_type) {
        this.vip_type = vip_type;
    }

    public String getVip_card_number() {
        return vip_card_number;
    }

    public void setVip_card_number(String vip_card_number) {
        this.vip_card_number = vip_card_number;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getStore_code() {
        return store_code;
    }

    public void setStore_code(String store_code) {
        this.store_code = store_code;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }


    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRegister_time() {
        return register_time;
    }

    public void setRegister_time(String register_time) {
        this.register_time = register_time;
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
