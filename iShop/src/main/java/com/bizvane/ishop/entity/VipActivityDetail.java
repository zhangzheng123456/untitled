package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2017/1/5.
 */
public class VipActivityDetail {
    private int id;
    //所属企业
    private String corp_code;
    //活动编号
    private String activity_code;
    //活动类别
    private String activity_type;
    //活动链接
    private String activity_url;
    //招募
    private String recruit;
    //促销编号
    private String sales_no;
    //节日开始时间
    private String festival_start;
    //节日结束时间
    private String festival_end;
    //发券类型
    private String send_coupon_type;
    //券类型
    private String coupon_type;
    //报名标题
//    private String apply_title;
    //报名截止时间
//    private String apply_endtime;
    //报名简介
    private String apply_desc;
    //报名成功提示
//    private String apply_success_tips;
    //报名logo
//    private String apply_logo;
    //是否允许非会员
//    private String apply_allow_vip;
    //会员参与总次数
    private String join_count;
    //消费条件
    private String consume_condition;
    //纪念日日期
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

    //邀请注册
    private  String register_data;
    //批次号
    private String  batch_no;
    //活动开始时间
    private String activity_start_time;

    //目标会员
    private String target_vips;

    private String corp_name;

    //线上报名活动
    private  String td_allow;//是否允许退订
    private  String td_end_time; //退订截至时间
    private  String present_time;//优惠赠送时间(奖励发放)
    private  String apply_type;//报名项目类型
    private  String apply_condition;//报名资料
    private  String apply_agreement;//报名免责协议
    private  String apply_type_info;//报名项目的详情

    public String getApply_type_info() {
        return apply_type_info;
    }

    public void setApply_type_info(String apply_type_info) {
        this.apply_type_info = apply_type_info;
    }



    public String getTd_allow() {
        return td_allow;
    }

    public void setTd_allow(String td_allow) {
        this.td_allow = td_allow;
    }

    public String getTd_end_time() {
        return td_end_time;
    }

    public void setTd_end_time(String td_end_time) {
        this.td_end_time = td_end_time;
    }

    public String getPresent_time() {
        return present_time;
    }

    public void setPresent_time(String present_time) {
        this.present_time = present_time;
    }

    public String getApply_type() {
        return apply_type;
    }

    public void setApply_type(String apply_type) {
        this.apply_type = apply_type;
    }

    public String getApply_condition() {
        return apply_condition;
    }

    public void setApply_condition(String apply_condition) {
        this.apply_condition = apply_condition;
    }

    public String getTarget_vips() {
        return target_vips;
    }

    public void setTarget_vips(String target_vips) {
        this.target_vips = target_vips;
    }

    public String getActivity_start_time() {
        return activity_start_time;
    }

    public void setActivity_start_time(String activity_start_time) {
        this.activity_start_time = activity_start_time;
    }

    public String getRegister_data() {
        return register_data;
    }

    public void setRegister_data(String register_data) {
        this.register_data = register_data;
    }

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

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getRecruit() {
        return recruit;
    }

    public void setRecruit(String recruit) {
        this.recruit = recruit;
    }

    public String getSales_no() {
        return sales_no;
    }

    public void setSales_no(String sales_no) {
        this.sales_no = sales_no;
    }

    public String getFestival_start() {
        return festival_start;
    }

    public void setFestival_start(String festival_start) {
        this.festival_start = festival_start;
    }

    public String getFestival_end() {
        return festival_end;
    }

    public void setFestival_end(String festival_end) {
        this.festival_end = festival_end;
    }

    public String getSend_coupon_type() {
        return send_coupon_type;
    }

    public void setSend_coupon_type(String send_coupon_type) {
        this.send_coupon_type = send_coupon_type;
    }

    public String getCoupon_type() {
        return coupon_type;
    }

    public void setCoupon_type(String coupon_type) {
        this.coupon_type = coupon_type;
    }

    public String getApply_desc() {
        return apply_desc;
    }

    public void setApply_desc(String apply_desc) {
        this.apply_desc = apply_desc;
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

    public String getActivity_url() {
        return activity_url;
    }

    public void setActivity_url(String activity_url) {
        this.activity_url = activity_url;
    }

    public String getJoin_count() {
        return join_count;
    }

    public void setJoin_count(String join_count) {
        this.join_count = join_count;
    }

    public String getConsume_condition() {
        return consume_condition;
    }

    public void setConsume_condition(String consume_condition) {
        this.consume_condition = consume_condition;
    }

    public String getPresent_point() {
        return present_point;
    }

    public void setPresent_point(String present_point) {
        this.present_point = present_point;
    }

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getApply_agreement() {
        return apply_agreement;
    }

    public void setApply_agreement(String apply_agreement) {
        this.apply_agreement = apply_agreement;
    }
}
