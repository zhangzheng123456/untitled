package com.bizvane.ishop.entity;

/**
 * Created by yin on 2016/9/7.
 */
public class VipParam {
    private int id;
    //参数名
    private String param_name;
    //参数类型
    private String param_type;
    //参数值
    private String param_values;
    //参数说明
    private String param_desc;
    //企业编号
    private String corp_code;
    //企业名称
    private String corp_name;
    //备注
    private String remark;
    //参数分类
    private String param_class;
    //是否必填
    private String required;
    //参数属性
    private  String param_attribute;
    //显示顺序
    private String show_order;
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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParam_name() {
        return param_name;
    }

    public void setParam_name(String param_name) {
        this.param_name = param_name;
    }

    public String getParam_type() {
        return param_type;
    }

    public void setParam_type(String param_type) {
        this.param_type = param_type;
    }

    public String getParam_values() {
        return param_values;
    }

    public void setParam_values(String param_values) {
        this.param_values = param_values;
    }

    public String getParam_desc() {
        return param_desc;
    }

    public void setParam_desc(String param_desc) {
        this.param_desc = param_desc;
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

    public String getParam_class() {
        return param_class;
    }

    public void setParam_class(String param_class) {
        this.param_class = param_class;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getParam_attribute() {
        return param_attribute;
    }

    public void setParam_attribute(String param_attribute) {
        this.param_attribute = param_attribute;
    }

    public String getShow_order() {
        return show_order;
    }

    public void setShow_order(String show_order) {
        this.show_order = show_order;
    }
}
