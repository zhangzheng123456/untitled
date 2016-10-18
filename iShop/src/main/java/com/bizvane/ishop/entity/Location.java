package com.bizvane.ishop.entity;

/**
 * Created by ZhouZhou on 2016/10/18.
 */
public class Location {

    //编号
    private String location_code;
    //名字
    private String location_name;
    //上一级code
    private String higher_level_code;
    //简称
    private String short_name;
    //纬度
    private String lat;
    //经度
    private String lng;
    //拼音
    private String pinyin;

    public String getLocation_code() {
        return location_code;
    }

    public void setLocation_code(String location_code) {
        this.location_code = location_code;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getHigher_level_code() {
        return higher_level_code;
    }

    public void setHigher_level_code(String higher_level_code) {
        this.higher_level_code = higher_level_code;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}
