package com.bizvane.ishop.entity;

/**
 * 会员活动类别详情(线上报名活动)
 */
public class VipActivityDetailApply {

    private  int id;
    private String corp_code;//所属企业
    private String activity_code;//活动编号
    private String item_name;//项目名称
    private String sku_id;
    private  String limit_count;//人数上限
    private  String last_count;//剩余人数
    private  String fee_money;//费用-金额
    private  String fee_points;//费用-积分
    private  String item_picture;//项目图片
    private  String modified_date;//修改时间
    private  String modifier;//修改人
    private  String created_date;//创建时间
    private  String creater;//创建人
    private  String isactive;//是否可用


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

    public String getActivity_code() {
        return activity_code;
    }

    public void setActivity_code(String activity_code) {
        this.activity_code = activity_code;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getSku_id() {
        return sku_id;
    }

    public void setSku_id(String sku_id) {
        this.sku_id = sku_id;
    }

    public String getLimit_count() {
        return limit_count;
    }

    public void setLimit_count(String limit_count) {
        this.limit_count = limit_count;
    }

    public String getLast_count() {
        return last_count;
    }

    public void setLast_count(String last_count) {
        this.last_count = last_count;
    }

    public String getFee_money() {
        return fee_money;
    }

    public void setFee_money(String fee_money) {
        this.fee_money = fee_money;
    }

    public String getFee_points() {
        return fee_points;
    }

    public void setFee_points(String fee_points) {
        this.fee_points = fee_points;
    }

    public String getItem_picture() {
        return item_picture;
    }

    public void setItem_picture(String item_picture) {
        this.item_picture = item_picture;
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

}
