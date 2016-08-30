package com.bizvane.ishop.entity;

/**
 * Created by yan on 2016/8/10.
 */
public class ParamConfigure {
    private int id;
    //参数
    private String param_name;
    //参数类型
    private String param_type;
    //参数可选值
    private String param_values;
    //参数对应名称
    private String param_desc;
    //备注
    private String remark;


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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
