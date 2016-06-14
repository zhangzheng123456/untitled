package com.bizvane.ishop.entity;

import java.util.Date;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
public class VipCallbackRecord {
    private int id;
    private String vip_code;
    private String user_code;
    private String collback_type;
    private Date collback_time;
    private String remark;
    private Date modified_date;
    private String isactive;
    private Date created_date;
    private String creater;
    private String corp_code;

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public VipCallbackRecord() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVip_code() {
        return vip_code;
    }

    public void setVip_code(String vip_code) {
        this.vip_code = vip_code;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getCollback_type() {
        return collback_type;
    }

    public void setCollback_type(String collback_type) {
        this.collback_type = collback_type;
    }

    public Date getCollback_time() {
        return collback_time;
    }

    public void setCollback_time(Date collback_time) {
        this.collback_time = collback_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getModified_date() {
        return modified_date;
    }

    public void setModified_date(Date modified_date) {
        this.modified_date = modified_date;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }
}
