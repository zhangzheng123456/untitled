package com.bizvane.ishop.entity;

/**
 * Created by yin on 2016/8/31.
 */
public class ViplableGroup {
    private int id;
    private String label_group_code;
    private String label_group_name;
    private String modified_date;
    private String created_date;
    private String creater;
    private String modifier;
    private String isactive;
    private String corp_code;
    private String corp_name;
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel_group_code() {
        return label_group_code;
    }

    public void setLabel_group_code(String label_group_code) {
        this.label_group_code = label_group_code;
    }

    public String getLabel_group_name() {
        return label_group_name;
    }

    public void setLabel_group_name(String label_group_name) {
        this.label_group_name = label_group_name;
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



}
