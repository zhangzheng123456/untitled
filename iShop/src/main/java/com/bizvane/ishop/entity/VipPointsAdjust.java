package com.bizvane.ishop.entity;

/**
 * Created by gyy on 2017/11/10.
 */
public class VipPointsAdjust {
    private int id;
    private String bill_name;//单据名称
    private String bill_code;//单据编号
    private String adjust_time;//调整时间
    private String bill_state;//单据状态
    private String bill_type;//单据类型
    private String bill_voucher;//单据凭证
    private String corp_code;//企业编号
    private String remarks;//备注
    private String isactive;//是否可用
    private String modified_date;//修改时间
    private String modifier;//修改人
    private String creater;//创建人
    private String created_date;//创建时间
    private String corp_name;//企业名称

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBill_name() {
        return bill_name;
    }

    public void setBill_name(String bill_name) {
        this.bill_name = bill_name;
    }

    public String getBill_code() {
        return bill_code;
    }

    public void setBill_code(String bill_code) {
        this.bill_code = bill_code;
    }

    public String getAdjust_time() {
        return adjust_time;
    }

    public void setAdjust_time(String adjust_time) {
        this.adjust_time = adjust_time;
    }

    public String getBill_state() {
        return bill_state;
    }

    public void setBill_state(String bill_state) {
        this.bill_state = bill_state;
    }

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
    }

    public String getBill_voucher() {
        return bill_voucher;
    }

    public void setBill_voucher(String bill_voucher) {
        this.bill_voucher = bill_voucher;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getCorp_name() { return corp_name; }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }
}

