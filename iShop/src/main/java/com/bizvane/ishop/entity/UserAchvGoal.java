package com.bizvane.ishop.entity;

import java.util.Date;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public class UserAchvGoal {

    private int id;
    private String user_code;
    private String user_name;
    private String store_code;
    private String corp_code;
    private double achv_goal;
    private String achv_type;
    private String start_time;
    private String end_time;
    private String modified_date;
    private String modifier;
    private String created_date;
    private String creater;
    private String isactive;
    private User user;
    private Store store;
    private Corp corp;


    public UserAchvGoal() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getStore_code() {
        return store_code;
    }

    public void setStore_code(String store_code) {
        this.store_code = store_code;
    }

    public double getAchv_goal() {
        return achv_goal;
    }

    public void setAchv_goal(double achv_goal) {
        this.achv_goal = achv_goal;
    }

    public String getAchv_type() {
        return achv_type;
    }

    public void setAchv_type(String achv_type) {
        this.achv_type = achv_type;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
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

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
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
}
