package com.bizvane.ishop.entity;

import java.util.Date;

/**
 * Created by Administrator on 2016/5/19.
 */
public class ShopInfo {
    private int id;
    //店铺编号
    private String store_code;
    //店铺名称
    private String store_name;
    //品牌编号
    private String brand_code;
    //所属品牌
    private String brand_name;
    //所属区域
    private String store_area;
    //是否直营
    private String flg_tob;
    //公司编号
    private String corp_code;
    //修改时间
    private Date modified_date;
    //修改人
    private String modifier;
    //创建时间
    private Date created_date;
    //创建者
    private String creater;
    //是否可用
    private String isactive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStore_code() {
        return store_code;
    }

    public void setStore_code(String store_code) {
        this.store_code = store_code;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
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

    public String getStore_area() {
        return store_area;
    }

    public void setStore_area(String store_area) {
        this.store_area = store_area;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getFlg_tob() {
        return flg_tob;
    }

    public void setFlg_tob(String flg_tob) {
        this.flg_tob = flg_tob;
    }

    public Date getModified_date() {
        return modified_date;
    }

    public void setModified_date(Date modified_date) {
        this.modified_date = modified_date;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
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
