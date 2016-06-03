package com.bizvane.ishop.entity;

import java.util.Date;

/**
 * Created by Administrator on 2016/5/19.
 */
public class Store {
    private int id;
    //店铺编号
    private String store_code;
    //店铺名称
    private String store_name;
    //品牌编号
    private String brand_code;
    //所属品牌
    private String brand_name;
    //区域编号
    private String area_code;
    //所属区域
    private String store_area;
    //是否直营
    private String flg_tob;
    //公司编号
    private String corp_code;
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

    private Corp corp;

    public Store(){}

    public Store(int id){
        this.id = id;
    }

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

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
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

    public String toString(){
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("id:").append(id);
        sb.append(", store_code:").append(store_code);
        sb.append(", store_name:").append(store_name);
        sb.append(", brand_code:").append(brand_code);
        sb.append(", brand_name:").append(brand_name);
        sb.append(", store_area:").append(store_area);
        sb.append(", flg_tob:").append(flg_tob);
        sb.append(", corp_code:").append(corp_code);
        sb.append(", modified_date:").append(modified_date);
        sb.append(", modifier:").append(modifier);
        sb.append(", created_date:").append(created_date);
        sb.append(", creater:").append(creater);
        sb.append(", isactive:").append(isactive);
        sb.append('}');
        return sb.toString();
    }
}
