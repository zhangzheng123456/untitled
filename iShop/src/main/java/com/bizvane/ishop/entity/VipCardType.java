package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2016/12/29.
 */
public class VipCardType {
    private String id;
    //所属企业
    private String corp_code;
    //会员卡类型编号
    private String vip_card_type_id;
    //会员卡类型编号
    private String vip_card_type_code;
    //会员卡类型名称
    private String vip_card_type_name;
    //会员卡类型级别
    private String degree;
    //所属品牌
    private String brand_code;
    //所属店铺群组
    private String store_group_code;
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
    private String corp_name;

    private String brand_name;

    private String store_group_name;

    private Corp corp;


    public String getVip_card_type_id() {
        return vip_card_type_id;
    }

    public void setVip_card_type_id(String vip_card_type_id) {
        this.vip_card_type_id = vip_card_type_id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getVip_card_type_code() {
        return vip_card_type_code;
    }

    public void setVip_card_type_code(String vip_card_type_code) {
        this.vip_card_type_code = vip_card_type_code;
    }

    public String getVip_card_type_name() {
        return vip_card_type_name;
    }

    public void setVip_card_type_name(String vip_card_type_name) {
        this.vip_card_type_name = vip_card_type_name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getBrand_code() {
        return brand_code;
    }

    public void setBrand_code(String brand_code) {
        this.brand_code = brand_code;
    }

    public String getStore_group_code() {
        return store_group_code;
    }

    public void setStore_group_code(String store_group_code) {
        this.store_group_code = store_group_code;
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

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getStore_group_name() {
        return store_group_name;
    }

    public void setStore_group_name(String store_group_name) {
        this.store_group_name = store_group_name;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }
}
