package com.bizvane.ishop.entity;

/**
 * Created by zhou on 2017/5/31.
 */
public class VipActivityDetailAnniversary {
    private int id;
    //所属企业
    private String corp_code;
    //活动编号
    private String activity_code;
    //纪念日类型
    private String param_name;
    private String param_desc;
    //执行时间
    private String run_time_type;
    //发送券类型
    private String coupon_type;
    //赠送积分
    private String send_points;
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

    private String start_time;
    //批次号
    private String  batch_no;

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

    public String getActivity_code() {
        return activity_code;
    }

    public void setActivity_code(String activity_code) {
        this.activity_code = activity_code;
    }

    public String getParam_name() {
        return param_name;
    }

    public void setParam_name(String param_name) {
        this.param_name = param_name;
    }

    public String getParam_desc() {
        return param_desc;
    }

    public void setParam_desc(String param_desc) {
        this.param_desc = param_desc;
    }

    public String getRun_time_type() {
        return run_time_type;
    }

    public void setRun_time_type(String run_time_type) {
        this.run_time_type = run_time_type;
    }

    public String getCoupon_type() {
        return coupon_type;
    }

    public void setCoupon_type(String coupon_type) {
        this.coupon_type = coupon_type;
    }

    public String getSend_points() {
        return send_points;
    }

    public void setSend_points(String send_points) {
        this.send_points = send_points;
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

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }
}
