package com.bizvane.ishop.entity;

public class ExamineConfigure {

    private  int id;
    private  String corp_code;
    private  String function_bill_name; //功能单据名称
    private  String examine_group; //审核组别
    private  String isactive;
    private  String modified_date;
    private  String modifier;
    private  String creater;
    private  String created_date;
    private  String corp_name;
    private  String examine_group_info;//审核组别详情
    private  String sms_examine;//提交审核,通知审核人
    private  String sms_progress;//通过审核,通知创建人
    private  String sms_result;//审核完成,通知创建人

    public String getSms_examine() {
        return sms_examine;
    }

    public void setSms_examine(String sms_examine) {
        this.sms_examine = sms_examine;
    }

    public String getSms_progress() {
        return sms_progress;
    }

    public void setSms_progress(String sms_progress) {
        this.sms_progress = sms_progress;
    }

    public String getSms_result() {
        return sms_result;
    }

    public void setSms_result(String sms_result) {
        this.sms_result = sms_result;
    }

    public String getExamine_group_info() {
        return examine_group_info;
    }

    public void setExamine_group_info(String examine_group_info) {
        this.examine_group_info = examine_group_info;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
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

    public String getFunction_bill_name() {
        return function_bill_name;
    }

    public void setFunction_bill_name(String function_bill_name) {
        this.function_bill_name = function_bill_name;
    }

    public String getExamine_group() {
        return examine_group;
    }

    public void setExamine_group(String examine_group) {
        this.examine_group = examine_group;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
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
}
