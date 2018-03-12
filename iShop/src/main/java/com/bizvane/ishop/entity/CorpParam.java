package com.bizvane.ishop.entity;

/**
 * Created by zhou on 2016/8/10.
 */
public class CorpParam {
    private int id;
    //企业编号
    private String corp_code;
    //企业名称
    private String corp_name;
    //参数id
    private String param_id;
    //参数名
    private String param_name;
    //参数对应值
    private String param_value;
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
    //参数类型
    private String param_type;

    //参数定义表
    //参数值
    private String param_values;
    //备注
    private String param_desc;

    public String getParam_values() {
        return param_values;
    }

    public void setParam_values(String param_values) {
        this.param_values = param_values;
    }

    public String getParam_type() {
        return param_type;
    }

    public void setParam_type(String param_type) {
        this.param_type = param_type;
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

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public String getParam_id() {
        return param_id;
    }

    public void setParam_id(String param_id) {
        this.param_id = param_id;
    }

    public String getParam_name() {
        return param_name;
    }

    public void setParam_name(String param_name) {
        this.param_name = param_name;
    }

    public String getParam_value() {
        return param_value;
    }

    public void setParam_value(String param_value) {
        this.param_value = param_value;
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

    public String getParam_desc() {
        return param_desc;
    }

    public void setParam_desc(String param_desc) {
        this.param_desc = param_desc;
    }
}
