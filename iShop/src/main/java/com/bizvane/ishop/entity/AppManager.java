package com.bizvane.ishop.entity;

/**
 * Created by PC on 2017/2/9.
 */
public class AppManager {
    private int id;
    private String app_function;
    private String app_action_code;
    private String app_action_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp_function() {
        return app_function;
    }

    public void setApp_function(String app_function) {
        this.app_function = app_function;
    }

    public String getApp_action_code() {
        return app_action_code;
    }

    public void setApp_action_code(String app_action_code) {
        this.app_action_code = app_action_code;
    }

    public String getApp_action_name() {
        return app_action_name;
    }

    public void setApp_action_name(String app_action_name) {
        this.app_action_name = app_action_name;
    }
}
