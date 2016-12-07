package com.bizvane.ishop.entity;

import java.util.List;

/**
 * Created by ZhouZhou on 2016/6/7.
 */
public class Privilege {
    private int id;
    //动作编号
    private String action_code;
    //动作名称
    private String action_name;
    private String action_show_name;
    //列名
    private String column_name;
    //列显示中文名
    private String show_name;
    //功能编号
    private String function_code;
    //功能编号
    private String function_name;
    //用户，角色或群组编号
    private String master_code;
    //可用
    private String enable;
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

    private List<TableManager> cloumns;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction_code() {
        return action_code;
    }

    public void setAction_code(String action_code) {
        this.action_code = action_code;
    }

    public String getAction_name() {
        return action_name;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
    }

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getAction_show_name() {
        return action_show_name;
    }

    public void setAction_show_name(String action_show_name) {
        this.action_show_name = action_show_name;
    }

    public String getShow_name() {
        return show_name;
    }

    public void setShow_name(String show_name) {
        this.show_name = show_name;
    }

    public String getFunction_code() {
        return function_code;
    }

    public void setFunction_code(String function_code) {
        this.function_code = function_code;
    }

    public String getFunction_name() {
        return function_name;
    }

    public void setFunction_name(String function_name) {
        this.function_name = function_name;
    }

    public String getMaster_code() {
        return master_code;
    }

    public void setMaster_code(String master_code) {
        this.master_code = master_code;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
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

    public List<TableManager> getCloumns() {
        return cloumns;
    }

    public void setCloumns(List<TableManager> cloumns) {
        this.cloumns = cloumns;
    }
}
