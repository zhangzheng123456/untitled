package com.bizvane.ishop.entity;

/**
 * Created by yin on 2016/7/26.
 */
public class TaskAllocation {
    private int id;
    //任务编号
    private String task_code;
    //企业编号
    private String corp_code;
    //企业名字
    private String corp_name;
    //员工编号
    private String user_code;
    //员工名
    private String user_name;
    //员工名
    private String phone;
    //任务状态
    private String task_status;
    //实际开始时间
    private String real_start_time;
    //实际截止时间
    private String real_end_time;
    //完成任务凭证(图片)
    private String task_proof_image;
    //完成任务凭证(文字)
    private String task_proof_text;
    //完成任务凭证(显示111)
    private String task_proof_img_pz;
    //完成任务评分
    private String task_proof_mark;
    private User user;
    private Corp corp;
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

    private String store_code;

    private String count;

    private String store_name;
    private String task_back;

    public String getTask_back() {
        return task_back;
    }

    public void setTask_back(String task_back) {
        this.task_back = task_back;
    }

    public String getTask_proof_img_pz() {
        return task_proof_img_pz;
    }

    public void setTask_proof_img_pz(String task_proof_img_pz) {
        this.task_proof_img_pz = task_proof_img_pz;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public String getReal_start_time() {
        return real_start_time;
    }

    public void setReal_start_time(String real_start_time) {
        this.real_start_time = real_start_time;
    }

    public String getReal_end_time() {
        return real_end_time;
    }

    public void setReal_end_time(String real_end_time) {
        this.real_end_time = real_end_time;
    }

    public String getTask_proof_image() {
        return task_proof_image;
    }

    public void setTask_proof_image(String task_proof_image) {
        this.task_proof_image = task_proof_image;
    }

    public String getTask_proof_text() {
        return task_proof_text;
    }

    public void setTask_proof_text(String task_proof_text) {
        this.task_proof_text = task_proof_text;
    }

    public String getTask_proof_mark() {
        return task_proof_mark;
    }

    public void setTask_proof_mark(String task_proof_mark) {
        this.task_proof_mark = task_proof_mark;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public String getStore_code() {
        return store_code;
    }

    public void setStore_code(String store_code) {
        this.store_code = store_code;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
