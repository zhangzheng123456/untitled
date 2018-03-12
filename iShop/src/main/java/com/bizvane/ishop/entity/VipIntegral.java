package com.bizvane.ishop.entity;

/**
 * Created by yanyadong on 2017/4/7.
 */
public class VipIntegral {
    private int id;
    private String corp_code;//企业编号
    private String integral_name;//名称
    private String bill_no;//单据编号
    private String target_vips;//目标会员
    private String target_vips_;//转换后的目标会员
    private String integral_duration;//保留积分时长
    private String clear_cycle;//清零周期
    private String remind;//提醒
    private String remarks;//备注
    private String creater;//创建人
    private String created_date;//创建时间
    private String modifier;//修改人
    private String modified_date;//修改时间
    private String isactive;//可用
    private String clear_type; //清零周期类型
    private String target_vip_type; //目标会员类型
    private String target_vips_condition;  //目标会员条件
    private String priority; //优先级
    private String recent_clean_time; //最近清理时间
    private String sms_code; //群发消息

    private String app_id; //发送通知时必填

    private String app_name; //公众号
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


    public String getBill_no() {
        return bill_no;
    }

    public void setBill_no(String bill_no) {
        this.bill_no = bill_no;
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

    public String getIntegral_duration() {
        return integral_duration;
    }

    public void setIntegral_duration(String integral_duration) {
        this.integral_duration = integral_duration;
    }

    public String getClear_cycle() {
        return clear_cycle;
    }

    public void setClear_cycle(String clear_cycle) {
        this.clear_cycle = clear_cycle;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getIntegral_name() {
        return integral_name;
    }

    public void setIntegral_name(String integral_name) {
        this.integral_name = integral_name;
    }

    public String getClear_type() {
        return clear_type;
    }

    public void setClear_type(String clear_type) {
        this.clear_type = clear_type;
    }

    public String getTarget_vip_type() {
        return target_vip_type;
    }

    public void setTarget_vip_type(String target_vip_type) {
        this.target_vip_type = target_vip_type;
    }

    public String getTarget_vips_condition() {
        return target_vips_condition;
    }

    public void setTarget_vips_condition(String target_vips_condition) {
        this.target_vips_condition = target_vips_condition;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRecent_clean_time() {
        return recent_clean_time;
    }

    public void setRecent_clean_time(String recent_clean_time) {
        this.recent_clean_time = recent_clean_time;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_name() {
        return app_name;
    }
}
