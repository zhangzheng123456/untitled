package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2016/11/24.
 */
public class SendCoupons {
    private String id;
    //发券编号
    private String tick_code_ishop;
    //公司编号
    private String corp_code;
    //发送券的编号
    private String coupon_code;
    //发送券的名称
    private String coupon_name;
    //所属公众号
    private String app_id;
    //所属公众号
    private String app_name;
    //所属品牌编号
    private String brand_code;
    //发券标题
    private String send_coupon_title;
    //发券描述
    private String send_coupon_desc;
    //可发券的开始时间
    private String time_start;
    //可发券的结束时间
    private String time_end;
    //发券描述
    private String send_coupon_users;
    //每人发券张数
    private String coupon_num_per;
    //发券 总数
    private String coupon_sum;
    //发券用户数
    private String user_num;
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
    private Corp corp;
    private Brand  brand;
    private String  couponInfo;

    public String getCouponInfo() {
        return couponInfo;
    }

    public void setCouponInfo(String couponInfo) {
        this.couponInfo = couponInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTick_code_ishop() {
        return tick_code_ishop;
    }

    public void setTick_code_ishop(String tick_code_ishop) {
        this.tick_code_ishop = tick_code_ishop;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getCoupon_name() {
        return coupon_name;
    }

    public void setCoupon_name(String coupon_name) {
        this.coupon_name = coupon_name;
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

    public String getBrand_code() {
        return brand_code;
    }

    public void setBrand_code(String brand_code) {
        this.brand_code = brand_code;
    }

    public String getSend_coupon_title() {
        return send_coupon_title;
    }

    public void setSend_coupon_title(String send_coupon_title) {
        this.send_coupon_title = send_coupon_title;
    }

    public String getSend_coupon_desc() {
        return send_coupon_desc;
    }

    public void setSend_coupon_desc(String send_coupon_desc) {
        this.send_coupon_desc = send_coupon_desc;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getSend_coupon_users() {
        return send_coupon_users;
    }

    public void setSend_coupon_users(String send_coupon_users) {
        this.send_coupon_users = send_coupon_users;
    }

    public String getCoupon_num_per() {
        return coupon_num_per;
    }

    public void setCoupon_num_per(String coupon_num_per) {
        this.coupon_num_per = coupon_num_per;
    }

    public String getCoupon_sum() {
        return coupon_sum;
    }

    public void setCoupon_sum(String coupon_sum) {
        this.coupon_sum = coupon_sum;
    }

    public String getUser_num() {
        return user_num;
    }

    public void setUser_num(String user_num) {
        this.user_num = user_num;
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

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
}
