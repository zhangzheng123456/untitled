package com.bizvane.ishop.entity;

import com.alibaba.fastjson.JSONArray;

/**
 * Created by nanji on 2016/12/19.
 */
public class VipRules {

    private int id;

    //公司编号
    private String corp_code;
    //会员类型
    private String vip_type;
    //上一级会员类型
    private String high_vip_type;
    //折扣
    private String discount;
    //招募门槛
    private String join_threshold;
    //升级门槛-时间
    private String upgrade_time;
    //升级门槛-消费金额
    private String upgrade_amount;
    //积分比例
    private String points_value;
    //限制开卡门店
    private String store_code;
    //赠送类型：赠送积分，
    private String present_coupon;
    //2：赠送券
    private String present_point;
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
    //会员类型编号
    private String vip_card_type_code;
    //会员等级
    private String degree;
    //高级会员类型编号
    private String high_vip_card_type_code;
    //高级会员等级
    private String high_degree;

    private Corp corp;
    private String corp_name;

    private JSONArray stores;

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

    public String getVip_type() {
        return vip_type;
    }

    public void setVip_type(String vip_type) {
        this.vip_type = vip_type;
    }

    public String getHigh_vip_type() {
        return high_vip_type;
    }

    public void setHigh_vip_type(String high_vip_type) {
        this.high_vip_type = high_vip_type;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getJoin_threshold() {
        return join_threshold;
    }

    public void setJoin_threshold(String join_threshold) {
        this.join_threshold = join_threshold;
    }

    public String getUpgrade_time() {
        return upgrade_time;
    }

    public void setUpgrade_time(String upgrade_time) {
        this.upgrade_time = upgrade_time;
    }

    public String getUpgrade_amount() {
        return upgrade_amount;
    }

    public void setUpgrade_amount(String upgrade_amount) {
        this.upgrade_amount = upgrade_amount;
    }

    public String getPoints_value() {
        return points_value;
    }

    public void setPoints_value(String points_value) {
        this.points_value = points_value;
    }

    public String getStore_code() {
        return store_code;
    }

    public void setStore_code(String store_code) {
        this.store_code = store_code;
    }

    public String getPresent_coupon() {
        return present_coupon;
    }

    public void setPresent_coupon(String present_coupon) {
        this.present_coupon = present_coupon;
    }

    public String getPresent_point() {
        return present_point;
    }

    public void setPresent_point(String present_point) {
        this.present_point = present_point;
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

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public JSONArray getStores() {
        return stores;
    }

    public void setStores(JSONArray stores) {
        this.stores = stores;
    }

    public String getVip_card_type_code() {
        return vip_card_type_code;
    }

    public void setVip_card_type_code(String vip_card_type_code) {
        this.vip_card_type_code = vip_card_type_code;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getHigh_vip_card_type_code() {
        return high_vip_card_type_code;
    }

    public void setHigh_vip_card_type_code(String high_vip_card_type_code) {
        this.high_vip_card_type_code = high_vip_card_type_code;
    }

    public String getHigh_degree() {
        return high_degree;
    }

    public void setHigh_degree(String high_degree) {
        this.high_degree = high_degree;
    }
}
