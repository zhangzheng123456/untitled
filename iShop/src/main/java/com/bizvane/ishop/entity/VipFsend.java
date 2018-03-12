package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2016/11/24.
 */
public class VipFsend {
    private int id;
    //群发短信编号
    private String sms_code;
    //公司编号
    private String corp_code;
    //接收群发短信的会员
    private String sms_vips;
    private String sms_vips_;
    //模板id
    private String template_name;
    //会员分组编号
    private String vip_group_code;
    //群发短信内容
    private String content;
    //发送类型
    private String send_type;
    //发送范围
    private String send_scope;
    //审核状态
    private String check_status;
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

    private String corp_name;
    private Corp corp;
    private String target_vips_count;
    private String activity_vip_code;
    private String send_time;

    private String vips_count;

    private String app_id;

    private String app_name;

    private  int fail_count;

    private  int success_count;

    private String select_type;

    public int getFail_count() {
        return fail_count;
    }

    public void setFail_count(int fail_count) {
        this.fail_count = fail_count;
    }

    public int getSuccess_count() {
        return success_count;
    }

    public void setSuccess_count(int success_count) {
        this.success_count = success_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getSms_vips() {
        return sms_vips;
    }

    public void setSms_vips(String sms_vips) {
        this.sms_vips = sms_vips;
    }


    public String getSms_vips_() {
        return sms_vips_;
    }

    public void setSms_vips_(String sms_vips_) {
        this.sms_vips_ = sms_vips_;
    }

    public String getVip_group_code() {
        return vip_group_code;
    }

    public void setVip_group_code(String vip_group_code) {
        this.vip_group_code = vip_group_code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSend_type() {
        return send_type;
    }

    public void setSend_type(String send_type) {
        this.send_type = send_type;
    }

    public String getSend_scope() {
        return send_scope;
    }

    public void setSend_scope(String send_scope) {
        this.send_scope = send_scope;
    }

    public String getCheck_status() {
        return check_status;
    }

    public void setCheck_status(String check_status) {
        this.check_status = check_status;
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

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public String getTarget_vips_count() {
        return target_vips_count;
    }

    public void setTarget_vips_count(String target_vips_count) {
        this.target_vips_count = target_vips_count;
    }

    public String getActivity_vip_code() {
        return activity_vip_code;
    }

    public void setActivity_vip_code(String activity_vip_code) {
        this.activity_vip_code = activity_vip_code;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getVips_count() {
        return vips_count;
    }

    public void setVips_count(String vips_count) {
        this.vips_count = vips_count;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public String getSelect_type() {
        return select_type;
    }

    public void setSelect_type(String select_type) {
        this.select_type = select_type;
    }
}
