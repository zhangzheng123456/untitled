package com.bizvane.ishop.entity;

/**
 * Created by yanyadong on 2017/4/17.
 */
public class Approved {
    private  int id;
    private  String corp_code;
    private  String approved_name; //名称
    private  String approved_cycle; //核准周期
    private  String approved_type;//核准类型
    private  String remarks;//备注
    private  String creater;//创建人
    private  String created_date;//核准时间
    private  String modifier;//修改人
    private  String modified_date;
    private  String isactive;//可用
    private  int degrade; //降级会员
    private  int keep;     //原级会员
    private  int expired_member;//过期会员

    public int getExpired_member() {
        return expired_member;
    }

    public void setExpired_member(int expired_member) {
        this.expired_member = expired_member;
    }

    public int getDegrade() {
        return degrade;
    }

    public void setDegrade(int degrade) {
        this.degrade = degrade;
    }

    public int getKeep() {
        return keep;
    }

    public void setKeep(int keep) {
        this.keep = keep;
    }


    public String getApproved_name() {
        return approved_name;
    }

    public void setApproved_name(String approved_name) {
        this.approved_name = approved_name;
    }


    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApproved_cycle() {
        return approved_cycle;
    }

    public void setApproved_cycle(String approved_cycle) {
        this.approved_cycle = approved_cycle;
    }

    public String getApproved_type() {
        return approved_type;
    }

    public void setApproved_type(String approved_type) {
        this.approved_type = approved_type;
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


}
