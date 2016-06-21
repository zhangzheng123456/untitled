package com.bizvane.ishop.entity;

/**
 * Created by yin on 2016/6/20.
 */
public class Feedback {
    private int id;
    private String user_code;
    private String feedback_content;
    private String phone;
    private String feedback_date;
    private String process_state;
    private String created_date;
    private String cheater;
    private String modified_date;
    private String modifier;
    private String isactive;

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

    public String getFeedback_content() {
        return feedback_content;
    }

    public void setFeedback_content(String feedback_content) {
        this.feedback_content = feedback_content;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFeedback_date() {
        return feedback_date;
    }

    public void setFeedback_date(String feedback_date) {
        this.feedback_date = feedback_date;
    }

    public String getProcess_state() {
        return process_state;
    }

    public void setProcess_state(String process_state) {
        this.process_state = process_state;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getCheater() {
        return cheater;
    }

    public void setCheater(String cheater) {
        this.cheater = cheater;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
}
