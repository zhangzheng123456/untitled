package com.bizvane.ishop.entity;

/**
 * Created by PC on 2017/10/11.
 */
public class SendTicket {
    private String corp_code;//企业编号
    private String corp_name;//企业名称
    private String ticket_code_ishop;//券批次在爱秀编号
    private String ticket_type_code;//券类型编号
    private String ticket_type_name;//券类型名称
    //private String ticket_condition;//券条件
  //  private String ticket_effective_date;//有效期
    private String ticket_remark;//券说明
    private String ticket_user_code;//券所属导购编号
    private String ticket_user_name;//券所属导购名称
    private String ticket_surplus_count;//券剩余数量
    private String ticket_count;//券数量
    private String user_ticket_start_date;//员工券的可发送开始时间
    private String user_ticket_end_date;//员工券的可发送结束时间
    private String modified_date;//修改时间
    private String modifier;//修改人
    private String created_date;//创建时间
    private String creater;//创建者
    private String isactive;//是否可用
    private String brand_code;//品牌编号
    private String app_id;//公众号
    private String send_count;//发送张数
    private String use_count;//使用张数
    private String ticket_minimumcharge;//最低消费
    private String ticket_effective_days;//可用时间
    private String ticket_parvalue;//减多少
    private String ticket_start_date;//券有效期开始时间
    private String ticket_end_date;//券有效期结束时间

    public String getTicket_surplus_count() {
        return ticket_surplus_count;
    }

    public void setTicket_surplus_count(String ticket_surplus_count) {
        this.ticket_surplus_count = ticket_surplus_count;
    }

    public String getTicket_start_date() {
        return ticket_start_date;
    }

    public void setTicket_start_date(String ticket_start_date) {
        this.ticket_start_date = ticket_start_date;
    }

    public String getTicket_end_date() {
        return ticket_end_date;
    }

    public void setTicket_end_date(String ticket_end_date) {
        this.ticket_end_date = ticket_end_date;
    }

    public String getTicket_minimumcharge() {
        return ticket_minimumcharge;
    }

    public void setTicket_minimumcharge(String ticket_minimumcharge) {
        this.ticket_minimumcharge = ticket_minimumcharge;
    }

    public String getTicket_effective_days() {
        return ticket_effective_days;
    }

    public void setTicket_effective_days(String ticket_effective_days) {
        this.ticket_effective_days = ticket_effective_days;
    }

    public String getTicket_parvalue() {
        return ticket_parvalue;
    }

    public void setTicket_parvalue(String ticket_parvalue) {
        this.ticket_parvalue = ticket_parvalue;
    }

    public String getSend_count() {
        return send_count;
    }

    public void setSend_count(String send_count) {
        this.send_count = send_count;
    }

    public String getUse_count() {
        return use_count;
    }

    public void setUse_count(String use_count) {
        this.use_count = use_count;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getBrand_code() {
        return brand_code;
    }

    public void setBrand_code(String brand_code) {
        this.brand_code = brand_code;
    }

    public String getTicket_code_ishop() {
        return ticket_code_ishop;
    }

    public void setTicket_code_ishop(String ticket_code_ishop) {
        this.ticket_code_ishop = ticket_code_ishop;
    }

    public String getTicket_count() {
        return ticket_count;
    }

    public void setTicket_count(String ticket_count) {
        this.ticket_count = ticket_count;
    }

    public String getUser_ticket_end_date() {
        return user_ticket_end_date;
    }

    public void setUser_ticket_end_date(String user_ticket_end_date) {
        this.user_ticket_end_date = user_ticket_end_date;
    }

    public String getUser_ticket_start_date() {
        return user_ticket_start_date;
    }

    public void setUser_ticket_start_date(String user_ticket_start_date) {
        this.user_ticket_start_date = user_ticket_start_date;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public String getTicket_type_code() {
        return ticket_type_code;
    }

    public void setTicket_type_code(String ticket_type_code) {
        this.ticket_type_code = ticket_type_code;
    }

    public String getTicket_type_name() {
        return ticket_type_name;
    }

    public void setTicket_type_name(String ticket_type_name) {
        this.ticket_type_name = ticket_type_name;
    }

//    public String getTicket_condition() {
//        return ticket_condition;
//    }
//
//    public void setTicket_condition(String ticket_condition) {
//        this.ticket_condition = ticket_condition;
//    }

//    public String getTicket_effective_date() {
//        return ticket_effective_date;
//    }
//
//    public void setTicket_effective_date(String ticket_effective_date) {
//        this.ticket_effective_date = ticket_effective_date;
//    }

    public String getTicket_remark() {
        return ticket_remark;
    }

    public void setTicket_remark(String ticket_remark) {
        this.ticket_remark = ticket_remark;
    }

    public String getTicket_user_code() {
        return ticket_user_code;
    }

    public void setTicket_user_code(String ticket_user_code) {
        this.ticket_user_code = ticket_user_code;
    }

    public String getTicket_user_name() {
        return ticket_user_name;
    }

    public void setTicket_user_name(String ticket_user_name) {
        this.ticket_user_name = ticket_user_name;
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
