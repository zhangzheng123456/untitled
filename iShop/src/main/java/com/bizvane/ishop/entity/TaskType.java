package com.bizvane.ishop.entity;

/**
 * Created by Administrator on 2016/7/20.
 */
public class TaskType {
    private int id;
    //任务类型编号
    private String task_type_code;
    //任务类型名称
    private String task_type_name;
    //公司编号
    private String corp_code;
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

    private String corp_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask_type_code() {
        return task_type_code;
    }

    public void setTask_type_code(String task_type_code) {
        this.task_type_code = task_type_code;
    }

    public String getTask_type_name() {
        return task_type_name;
    }

    public void setTask_type_name(String task_type_name) {
        this.task_type_name = task_type_name;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
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

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }
}
