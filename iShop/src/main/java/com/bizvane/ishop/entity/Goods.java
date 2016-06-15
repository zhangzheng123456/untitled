package com.bizvane.ishop.entity;

import java.util.Date;

/**
 * Created by Administrator on 2016/5/19.
 */
public class Goods {
    //id
    private int id;
    //商品编号
    private String goods_code;
    //商品名
    private String goods_name;
    //商品图片
    private String goods_image;
    //商品价格
    private float goods_price;
    //商品时间
    private String goods_time;
    //商品季度
    private String goods_quarter;
    //波段
    private String goods_wave;
    //修改日期
    private Date modified_date;
    //修改人
    private String modifier;
    //是否可用
    private String isActive;
    //创建日期
    private Date created_date;
    //创建者
    private String creater;
    //商品描述
    private String goods_description;

    public Goods() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }


    public float getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(float goods_price) {
        this.goods_price = goods_price;
    }

    public String getGoods_time() {
        return goods_time;
    }

    public void setGoods_time(String goods_time) {
        this.goods_time = goods_time;
    }

    public String getGoods_quarter() {
        return goods_quarter;
    }

    public void setGoods_quarter(String goods_quarter) {
        this.goods_quarter = goods_quarter;
    }

    public String getGoods_wave() {
        return goods_wave;
    }

    public void setGoods_wave(String goods_wave) {
        this.goods_wave = goods_wave;
    }

    public Date getModified_date() {
        return modified_date;
    }

    public void setModified_date(Date modified_date) {
        this.modified_date = modified_date;
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

    public String getIsActive() {
        return isActive;
    }

    public String getGoods_image() {
        return goods_image;
    }

    public void setGoods_image(String goods_image) {
        this.goods_image = goods_image;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }


    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }


    public String getGoods_description() {
        return goods_description;
    }

    public void setGoods_description(String goods_description) {
        this.goods_description = goods_description;
    }
}
