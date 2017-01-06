package com.bizvane.ishop.entity;

/**
 * Created by Administrator on 2016/7/20.
 */
public class Task {
    private int id;
    //任务编号
    private String task_code;
    //任务标题
    private String task_title;
    //任务类型
    private String task_type_code;

    //private String task_status;
    //任务描述
    private String task_description;
    //员工编号
    private String user_code;
    //开始时间
    private String target_start_time;
    //截止时间
    private String target_end_time;
    //企业编号
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
    //标识
    private String role_ident;
    private Corp corp;
    private String corp_name;
    private User user;
    private String task_type_name;
    private TaskType taskType;
    private String task_type_code_old;
    private String task_type_code_new;

    //关联活动
    private String activity_vip_code;
    private String task_link;

    public String getTask_link() {
        return task_link;
    }

    public void setTask_link(String task_link) {
        this.task_link = task_link;
    }

    public String getTask_type_code_old() {
        return task_type_code_old;
    }

    public void setTask_type_code_old(String task_type_code_old) {
        this.task_type_code_old = task_type_code_old;
    }

    public String getTask_type_code_new() {
        return task_type_code_new;
    }

    public void setTask_type_code_new(String task_type_code_new) {
        this.task_type_code_new = task_type_code_new;
    }

    public String getTask_type_name() {
        return task_type_name;
    }

    public void setTask_type_name(String task_type_name) {
        this.task_type_name = task_type_name;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getRole_ident() {
        return role_ident;
    }

    public void setRole_ident(String role_ident) {
        this.role_ident = role_ident;
    }

    // private String user_name;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask_code() {
        return task_code;
    }

    public void setTask_code(String task_code) {
        this.task_code = task_code;
    }

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getTask_type_code() {
        return task_type_code;
    }

    public void setTask_type_code(String task_type_code) {
        this.task_type_code = task_type_code;
    }


    public String getTask_description() {
        return task_description;
    }

    public void setTask_description(String task_description) {
        this.task_description = task_description;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getTarget_start_time() {
        return target_start_time;
    }

    public void setTarget_start_time(String target_start_time) {
        this.target_start_time = target_start_time;
    }

    public String getTarget_end_time() {
        return target_end_time;
    }

    public void setTarget_end_time(String target_end_time) {
        this.target_end_time = target_end_time;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getActivity_vip_code() {
        return activity_vip_code;
    }

    public void setActivity_vip_code(String activity_vip_code) {
        this.activity_vip_code = activity_vip_code;
    }
}
