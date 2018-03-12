package com.bizvane.ishop.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/5/19.
 */
public class Store {
    private int id;
    private String store_id;
    //店铺编号
    private String store_code;
    //店铺名称
    private String store_name;
    //品牌编号
    private String brand_code;
    //区域编号
    private String area_code;
    //是否直营
    private String flg_tob;
    //公司编号
    private String corp_code;
    //省
    private String province;
    private String province_location_name;
    //市
    private String city;
    private String city_location_name;
    //区
    private String area;
    private String area_location_name;
    //街道
    private String street;
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

    //线下区域
    private String offline_area;
    //店铺坐标
    private String store_location;
    //经度
    private String lng;
    //纬度
    private String lat;
    //距离
    private String distance;
    //所属品牌
    private String brand_name;
    //所属企业
    private String corp_name;
    //是否为该区域店铺
    private String is_this_area;
    //角色名称
    private String area_name;

    //店铺未分配会员数
    private String unassort_count;
    //未分配会员平均时长
    private String time_avg;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    //添加标记
    private String logo;

    private Corp corp;
    //二维码地址
    private String qrcode;

    private String app_logo;

    private List<StoreQrcode> qrcodeList;

    //所属经销商
    private String dealer;
    //店铺类型
    private String store_type;
    //开启爱秀
    private String isopen;
    private String open_date;
    private String close_date;
    private String open_date_count;
private String open_person;
//二维码logo
private String qrcode_logo;
    public String getOpen_person() {
        return open_person;
    }

    public void setOpen_person(String open_person) {
        this.open_person = open_person;
    }

    public String getOpen_date() {
        return open_date;
    }

    public void setOpen_date(String open_date) {
        this.open_date = open_date;
    }

    public String getClose_date() {
        return close_date;
    }

    public void setClose_date(String close_date) {
        this.close_date = close_date;
    }

    public String getOpen_date_count() {
        return open_date_count;
    }

    public void setOpen_date_count(String open_date_count) {
        this.open_date_count = open_date_count;
    }

    public String getIsopen() {
        return isopen;
    }

    public void setIsopen(String isopen) {
        this.isopen = isopen;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getStore_type() {
        return store_type;
    }

    public void setStore_type(String store_type) {
        this.store_type = store_type;
    }

    public Store() {
    }

    public Store(int id) {
        this.id = id;
    }

    public String getStore_id() {
        return store_id;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
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

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getFlg_tob() {
        return flg_tob;
    }

    public void setFlg_tob(String flg_tob) {
        this.flg_tob = flg_tob;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getProvince_location_name() {
        return province_location_name;
    }

    public void setProvince_location_name(String province_location_name) {
        this.province_location_name = province_location_name;
    }

    public String getCity_location_name() {
        return city_location_name;
    }

    public void setCity_location_name(String city_location_name) {
        this.city_location_name = city_location_name;
    }

    public String getArea_location_name() {
        return area_location_name;
    }

    public void setArea_location_name(String area_location_name) {
        this.area_location_name = area_location_name;
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

    public String getStore_location() {

        return store_location;
    }

    public void setStore_location(String store_location) {
        this.store_location = store_location;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public String getIs_this_area() {
        return is_this_area;
    }

    public void setIs_this_area(String is_this_area) {
        this.is_this_area = is_this_area;
    }

    public List<StoreQrcode> getQrcodeList() {
        return qrcodeList;
    }

    public void setQrcodeList(List<StoreQrcode> qrcodeList) {
        this.qrcodeList = qrcodeList;
    }

    public String getOffline_area() {
        return offline_area;
    }

    public void setOffline_area(String offline_area) {
        this.offline_area = offline_area;
    }

    public String getUnassort_count() {
        return unassort_count;
    }

    public void setUnassort_count(String unassort_count) {
        this.unassort_count = unassort_count;
    }

    public String getTime_avg() {
        return time_avg;
    }

    public void setTime_avg(String time_avg) {
        this.time_avg = time_avg;
    }

    public String getApp_logo() {
        return app_logo;
    }

    public void setApp_logo(String app_logo) {
        this.app_logo = app_logo;
    }

    public String getQrcode_logo() {
        return qrcode_logo;
    }

    public void setQrcode_logo(String qrcode_logo) {
        this.qrcode_logo = qrcode_logo;
    }
}
