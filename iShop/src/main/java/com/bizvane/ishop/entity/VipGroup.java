package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2016/8/31.
 */
public class VipGroup {
    private int id;
    //会员分组编号
    private String vip_group_code;
    //会员分组名称
    private String vip_group_name;
    //分组方式
    private String group_type;
    //分组条件
    private String group_condition;
    //企业编号
    private String corp_code;
    //备注
    private String remark;
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

    private String is_public;

    private String corp_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVip_group_code() {
        return vip_group_code;
    }

    public void setVip_group_code(String vip_group_code) {
        this.vip_group_code = vip_group_code;
    }

    public String getVip_group_name() {
        return vip_group_name;
    }

    public void setVip_group_name(String vip_group_name) {
        this.vip_group_name = vip_group_name;
    }

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public String getGroup_condition() {
        return group_condition;
    }

    public void setGroup_condition(String group_condition) {
        this.group_condition = group_condition;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getIs_public() {
        return is_public;
    }

    public void setIs_public(String is_public) {
        this.is_public = is_public;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }
}
