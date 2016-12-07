package com.bizvane.ishop.entity;

/**
 * Created by yin on 2016/6/30.
 */
public class TableManager {
    private int id;
//    private String column_code;
    //功能编号
    private String function_code;
    //列名
    private String column_name;
    //页面显示名
    private String show_name;
    //筛选 是否显示
    private String is_show;
    //筛选 显示顺序
    private String filter_weight;
    //筛选 显示格式（text/select）
    private String filter_type;
    //筛选 下拉框值（select用）
    private String filter_value;

//    public String getColumn_code() {
//        return column_code;
//    }

//    public void setColumn_code(String column_code) {
//        this.column_code = column_code;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIs_show() {
        return is_show;
    }

    public void setIs_show(String is_show) {
        this.is_show = is_show;
    }

    public String getFunction_code() {
        return function_code;
    }

    public void setFunction_code(String function_code) {
        this.function_code = function_code;
    }

    public String getShow_name() {
        return show_name;
    }

    public void setShow_name(String show_name) {
        this.show_name = show_name;
    }

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getFilter_weight() {
        return filter_weight;
    }

    public void setFilter_weight(String filter_weight) {
        this.filter_weight = filter_weight;
    }

    public String getFilter_type() {
        return filter_type;
    }

    public void setFilter_type(String filter_type) {
        this.filter_type = filter_type;
    }

    public String getFilter_value() {
        return filter_value;
    }

    public void setFilter_value(String filter_value) {
        this.filter_value = filter_value;
    }
}
