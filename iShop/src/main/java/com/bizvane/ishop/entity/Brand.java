package com.bizvane.ishop.entity;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2016/5/19.
 */
public class Brand {
    private int id;
    //品牌编号xxx
    private String brand_id;
    //品牌编号
    private String brand_code;
    //品牌名称
    private String brand_name;
    //公司编号
    private String corp_code;
    //品牌默认客服
    private String cus_user_code;
    //修改时间
    private String modified_date;


    private String channel_production;
    private String channel_marketing;


    //修改人
    private String modifier;
    //创建时间
    private String created_date;
    //创建者
    private String creater;
    //是否可用
    private String isactive;


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    //标记
    private  String logo;

    private Corp corp;
    private String corp_name;

    private List<JSONObject> cus_user;

    private String app_id;
    private String market_id;
    private String product_id;


    private String app_name;
    //所属公众号logo
    private String app_logo;

    public String getApp_logo() {
        return app_logo;
    }

    public void setApp_logo(String app_logo) {
        this.app_logo = app_logo;
    }
    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public Brand() {
    }



    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public Brand(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand_code() {

        return brand_code;
    }

    public void setBrand_code(String brand_code) {
        this.brand_code = brand_code;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
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

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public List<JSONObject> getCus_user() {
        return cus_user;
    }

    public void setCus_user(List<JSONObject> cus_user) {
        this.cus_user = cus_user;
    }

    public String getChannel_production() {
        return channel_production;
    }

    public void setChannel_production(String channel_production) {
        this.channel_production = channel_production;
    }

    public String getChannel_marketing() {
        return channel_marketing;
    }

    public void setChannel_marketing(String channel_marketing) {
        this.channel_marketing = channel_marketing;
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

    public String getMarket_id() {
        return market_id;
    }

    public void setMarket_id(String market_id) {
        this.market_id = market_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }
}
