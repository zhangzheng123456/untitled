package com.bizvane.ishop.entity;

/**
 * Created by yanyadong on 2017/4/24.
 */
public class VipTask {
    private int id;
    private  String corp_code;//企业编号
    private  String task_code;//任务编号
    private  String task_title;//任务标题
    private  String start_time;//开始时间
    private  String end_time;//结束时间
    private  String task_type;//任务类型
    private  String target_vips;//目标会员
    private String target_vips_;//转换后的目标会员
    private  String target_vips_condition;//目标会员(Solr条件)
    private String target_type;//目标会员条件类型

    private  String task_condition; //任务条件
    private  String present_coupon; //赠送券
    private  String  present_point; //赠送积分
    private  String task_description;//任务描述
    private  String share_content;//分享配置
    private String task_status; //任务状态(未执行，执行中，已结束)
    private  String app_id;
    private String schedule;  //任务进度
    private  String modified_date;
    private  String created_date;
    private  String creater;
    private  String modifier;
    private  String isactive;
    private  String app_name;
    private  String is_advance_show;

    private String target_count;

    private String complete_count;

    private  String task_type_name;//任务类型名称

    private  String corp_name; //企业名称

    private  String target_vip_count; //会员数量

    private  String batch_no; //批次号

    private  String bill_status;//单据状态

    public String getBill_status() {
        return bill_status;
    }

    public void setBill_status(String bill_status) {
        this.bill_status = bill_status;
    }

    public String getTask_type_name() {
        return task_type_name;
    }

    public void setTask_type_name(String task_type_name) {
        this.task_type_name = task_type_name;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public String getTarget_vip_count() {
        return target_vip_count;
    }

    public void setTarget_vip_count(String target_vip_count) {
        this.target_vip_count = target_vip_count;
    }

    //是否发送新任务提醒
    private String is_send_notice;

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

    public String getApp_name() {
        return app_name==null?"":app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getTask_code() {
        return task_code;
    }

    public void setTask_code(String task_code) {
        this.task_code = task_code;
    }

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getTarget_vips() {
        return target_vips;
    }

    public void setTarget_vips(String target_vips) {
        this.target_vips = target_vips;
    }

    public String getTarget_vips_() {
        return target_vips_;
    }

    public void setTarget_vips_(String target_vips_) {
        this.target_vips_ = target_vips_;
    }

    public String getTarget_vips_condition() {
        return target_vips_condition;
    }

    public void setTarget_vips_condition(String target_vips_condition) {
        this.target_vips_condition = target_vips_condition;
    }

    public String getTarget_type() {
        return target_type;
    }

    public void setTarget_type(String target_type) {
        this.target_type = target_type;
    }

    public String getTask_condition() {
        return task_condition;
    }

    public void setTask_condition(String task_condition) {
        this.task_condition = task_condition;
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

    public String getTask_description() {
        return task_description;
    }

    public void setTask_description(String task_description) {
        this.task_description = task_description;
    }

    public String getShare_content() {
        return share_content;
    }

    public void setShare_content(String share_content) {
        this.share_content = share_content;
    }

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
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

    public String getTarget_count() {
        return target_count;
    }

    public void setTarget_count(String target_count) {
        this.target_count = target_count;
    }

    public String getComplete_count() {
        return complete_count;
    }

    public void setComplete_count(String complete_count) {
        this.complete_count = complete_count;
    }

    public String getIs_send_notice() {
        return is_send_notice;
    }

    public void setIs_send_notice(String is_send_notice) {
        this.is_send_notice = is_send_notice;
    }

    public String getIs_advance_show() {
        return is_advance_show;
    }

    public void setIs_advance_show(String is_advance_show) {
        this.is_advance_show = is_advance_show;
    }

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }
}
