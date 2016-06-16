package com.bizvane.ishop.entity;

import java.util.Date;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
public class VipCallbackRecord {
    private int id;
    //vip编号
    private String vip_code;
    //用户编号
    private String user_code;
    //回访类型
    private String callback_type;
    //回访时间
    private Date callback_time;
    // 评论
    private String remark;
    // 修改时间
    private Date modified_date;
    //修改人
    private String modifier;
    // 是否可用
    private String isactive;
    //创建日期
    private Date created_date;
    //创建人
    private String creater;
    //企业编号
    private String corp_code;

    public String getCorp_code() {
        return corp_code;
    }

    public String getCallback_type() {
        return callback_type;
    }

    public void setCallback_type(String callback_type) {
        this.callback_type = callback_type;
    }

    public Date getCallback_time() {
        return callback_time;
    }

    public void setCallback_time(Date callback_time) {
        this.callback_time = callback_time;
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
