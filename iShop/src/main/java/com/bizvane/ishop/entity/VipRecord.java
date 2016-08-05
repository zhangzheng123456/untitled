package com.bizvane.ishop.entity;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
public class VipRecord {
    private int id;
    //vip编号
    private String vip_code;
    //用户编号
    private String user_code;
    //动作
    private String action;
    // 修改时间
    private String modified_date;
    //修改人
    private String modifier;
    // 是否可用
    private String isactive;
    //创建日期
    private String created_date;
    //创建人
    private String creater;
    //企业编号
    private String corp_code;
    private String type_name;
    private String corp_name;
    private String user_name;

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    private User user;
    private Corp corp;
    private VIPInfo vipInfo;
    private VipRecordType vipRecordType;

    public VipRecord() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVip_code() {
        return vip_code;
    }

    public void setVip_code(String vip_code) {
        this.vip_code = vip_code;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
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

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public VIPInfo getVipInfo() {
        return vipInfo;
    }

    public void setVipInfo(VIPInfo vipInfo) {
        this.vipInfo = vipInfo;
    }

    public VipRecordType getVipRecordType() {
        return vipRecordType;
    }

    public void setVipRecordType(VipRecordType vipRecordType) {
        this.vipRecordType = vipRecordType;
    }
}
