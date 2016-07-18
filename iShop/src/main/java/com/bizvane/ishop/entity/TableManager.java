package com.bizvane.ishop.entity;

/**
 * Created by yin on 2016/6/30.
 */
public class TableManager {
    private int id;
    private String function_code;
    private String column_name;
    private String show_name;
    private String is_show;
    private String filter_weight;

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
}
