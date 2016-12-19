package com.bizvane.ishop.entity;

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

    private Corp corp;
    private String corp_name;

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
}
