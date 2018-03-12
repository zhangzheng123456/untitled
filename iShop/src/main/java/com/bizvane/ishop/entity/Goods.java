package com.bizvane.ishop.entity;

import java.util.List;

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
    private String goods_price;
    //商品时间
    private String goods_time;
    //商品季度
    private String goods_quarter;
    //波段
    private String goods_wave;
    //修改日期
    private String modified_date;
    //修改人
    private String modifier;
    //是否可用
    private String isactive;
    //创建日期
    private String created_date;
    //创建者
    private String creater;
    //商品卖点
    private String goods_description;
    //分享内容
    private String share_description;
    //商品品牌
    private String brand_code;
    //企业编号
    private String corp_code;
    //商品年份
    private String goods_year;

    private Brand brand;
    private Corp corp;
    private String corp_name;
    private String brand_name;
    private List<DefGoodsMatch> matchgoods;
    private String match_goods;
    private String num_sales;
    private String num_stocks;
    private String standard;
    private String goods_source;


    public String getGoods_year() {
        return goods_year;
    }

    public void setGoods_year(String goods_year) {
        this.goods_year = goods_year;
    }

    public String getGoods_source() {
        return goods_source;
    }

    public void setGoods_source(String goods_source) {
        this.goods_source = goods_source;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getNum_sales() {
        return num_sales;
    }

    public void setNum_sales(String num_sales) {
        this.num_sales = num_sales;
    }

    public String getNum_stocks() {
        return num_stocks;
    }

    public void setNum_stocks(String num_stocks) {
        this.num_stocks = num_stocks;
    }

    public String getMatch_goods() {
        return match_goods;
    }

    public void setMatch_goods(String match_goods) {
        this.match_goods = match_goods;
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

    public String getGoods_image() {
        return goods_image;
    }

    public void setGoods_image(String goods_image) {
        this.goods_image = goods_image;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(String goods_price) {
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

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
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

    public String getGoods_description() {
        return goods_description;
    }

    public void setGoods_description(String goods_description) {
        this.goods_description = goods_description;
    }

    public String getShare_description() {
        return share_description;
    }

    public void setShare_description(String share_description) {
        this.share_description = share_description;
    }

    public String getBrand_code() {
        return brand_code;
    }

    public void setBrand_code(String brand_code) {
        this.brand_code = brand_code;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public List<DefGoodsMatch> getMatchgoods() {
        return matchgoods;
    }

    public void setMatchgoods(List<DefGoodsMatch> matchgoods) {
        this.matchgoods = matchgoods;
    }
}
