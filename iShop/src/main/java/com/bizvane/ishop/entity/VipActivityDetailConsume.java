package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2017/1/5.
 */
public class VipActivityDetailConsume {
    private int id;
    //所属企业
    private String corp_code;
    //活动编号
    private String activity_code;
    //消费商品条件
    private String consume_goods;
    private String goods_condition;
    //会员参与次数
    private String vip_join_count;
    //优先级
    private String priority;
    //发送券类型
    private String coupon_type;
    //赠送积分
    private String send_points;
    //整单金额
    private String trade_start;
    private String trade_end;
    //消费折扣
    private String discount_start;
    private String discount_end;
    //购买件数
    private String num_start;
    private String num_end;

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
    //批次号
    private String  batch_no;

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

    public String getConsume_goods() {
        return consume_goods;
    }

    public void setConsume_goods(String consume_goods) {
        this.consume_goods = consume_goods;
    }

    public String getGoods_condition() {
        return goods_condition;
    }

    public void setGoods_condition(String goods_condition) {
        this.goods_condition = goods_condition;
    }

    public String getVip_join_count() {
        return vip_join_count;
    }

    public void setVip_join_count(String vip_join_count) {
        this.vip_join_count = vip_join_count;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCoupon_type() {
        return coupon_type;
    }

    public void setCoupon_type(String coupon_type) {
        this.coupon_type = coupon_type;
    }

    public String getSend_points() {
        return send_points;
    }

    public void setSend_points(String send_points) {
        this.send_points = send_points;
    }

    public String getTrade_start() {
        return trade_start;
    }

    public void setTrade_start(String trade_start) {
        this.trade_start = trade_start;
    }

    public String getTrade_end() {
        return trade_end;
    }

    public void setTrade_end(String trade_end) {
        this.trade_end = trade_end;
    }

    public String getDiscount_start() {
        return discount_start;
    }

    public void setDiscount_start(String discount_start) {
        this.discount_start = discount_start;
    }

    public String getDiscount_end() {
        return discount_end;
    }

    public void setDiscount_end(String discount_end) {
        this.discount_end = discount_end;
    }

    public String getNum_start() {
        return num_start;
    }

    public void setNum_start(String num_start) {
        this.num_start = num_start;
    }

    public String getNum_end() {
        return num_end;
    }

    public void setNum_end(String num_end) {
        this.num_end = num_end;
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

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }
}
