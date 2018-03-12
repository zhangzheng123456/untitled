package com.bizvane.ishop.entity;

/**
 * Created by Administrator on 2016/5/19.
 */
public class CorpJD {
    private int id;
    //企业编号
    private String corp_code;
    //京东ID
    private String u_id;
    //京东昵称
    private String user_nick;

    //授权方的令牌
    private String authorizer_token;
    //授权方的刷新令牌
    private String authorizer_refresh_token;
    //令牌有效时长
    private String expires_in;
    //令牌产生时间
    private String last_time;
    //是否已授权
    private String is_authorize;
    //品牌编号
    private String brand_code;
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


    public CorpJD() {
    }

    public CorpJD(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }

    public String getAuthorizer_token() {
        return authorizer_token;
    }

    public void setAuthorizer_token(String authorizer_token) {
        this.authorizer_token = authorizer_token;
    }

    public String getAuthorizer_refresh_token() {
        return authorizer_refresh_token;
    }

    public void setAuthorizer_refresh_token(String authorizer_refresh_token) {
        this.authorizer_refresh_token = authorizer_refresh_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getBrand_code() {
        return brand_code;
    }

    public void setBrand_code(String brand_code) {
        this.brand_code = brand_code;
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

    public String getIs_authorize() {
        return is_authorize;
    }

    public void setIs_authorize(String is_authorize) {
        this.is_authorize = is_authorize;
    }

}
