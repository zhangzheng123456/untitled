package com.bizvane.ishop.entity;

/**
 * Created by ZhouZhou on 2016/10/26.
 */
public class Weimob {

    private String id;
    //企业编号
    private String corp_code;
    //授权code
    private String code;
    //
    private String client_id;
    //
    private String client_secret;
    //
    private String access_token;
    //
    private String refresh_access_token;
    //
    private String last_time;
    //
    private String last_time_refresh;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_access_token() {
        return refresh_access_token;
    }

    public void setRefresh_access_token(String refresh_access_token) {
        this.refresh_access_token = refresh_access_token;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getLast_time_refresh() {
        return last_time_refresh;
    }

    public void setLast_time_refresh(String last_time_refresh) {
        this.last_time_refresh = last_time_refresh;
    }
}
