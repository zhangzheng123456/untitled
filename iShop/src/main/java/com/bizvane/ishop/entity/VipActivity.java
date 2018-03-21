package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2016/11/15.
 */
public class VipActivity {
    private int id;
    //所属企业
    private String corp_code;
    //公众号
    private String app_id;
    //活动编号
    private String activity_code;
    //活动主题
    private String activity_theme;
    //活动执行状态
    private String activity_state;
    //活动类别
    private String run_mode;
    //活动开始时间
    private String start_time;
    //活动结束时间
    private String end_time;
    //活动目标会员
    private String target_vips;
    //活动详情
    private String activity_desc;
    //任务编号
    private String task_code;
    //群发消息编号
    private String sms_code;
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

    private String target_vips_count;

    private String task_status;

    private String send_status;

    private String app_name;

    private  String batch_no;

    private String run_scope;

    private String store_count;

    private  String vip_condition; //页面选择的筛选的会员条件

    private  String bill_status;//活动审核状态

    private  String target_vips_import;//目标会员为导入会员

    private  String qrcode;

    private  String qrcode_content;

    private  String select_scope;

    public String getSelect_scope() {
        return select_scope;
    }

    public void setSelect_scope(String select_scope) {
        this.select_scope = select_scope;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getQrcode_content() {
        return qrcode_content;
    }

    public void setQrcode_content(String qrcode_content) {
        this.qrcode_content = qrcode_content;
    }

    public String getTarget_vips_import() {
        return target_vips_import;
    }

    public void setTarget_vips_import(String target_vips_import) {
        this.target_vips_import = target_vips_import;
    }

    public String getBill_status() {
        return bill_status;
    }

    public void setBill_status(String bill_status) {
        this.bill_status = bill_status;
    }

    //是否创建会员任务
    private String vip_task;

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public String getSend_status() {
        return send_status;
    }

    public void setSend_status(String send_status) {
        this.send_status = send_status;
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

    public String getActivity_code() {
        return activity_code;
    }

    public void setActivity_code(String activity_code) {
        this.activity_code = activity_code;
    }

    public String getActivity_theme() {
        return activity_theme;
    }

    public void setActivity_theme(String activity_theme) {
        this.activity_theme = activity_theme;
    }

    public String getRun_mode() {
        return run_mode;
    }

    public void setRun_mode(String run_mode) {
        this.run_mode = run_mode;
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

    public String getTarget_vips() {
        return target_vips;
    }

    public void setTarget_vips(String target_vips) {
        this.target_vips = target_vips;
    }

    public String getActivity_desc() {
        return activity_desc;
    }

    public void setActivity_desc(String activity_desc) {
        this.activity_desc = activity_desc;
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

    public String getActivity_state() {
        return activity_state;
    }

    public void setActivity_state(String activity_state) {
        this.activity_state = activity_state;
    }

    public String getTask_code() {
        return task_code;
    }

    public void setTask_code(String task_code) {
        this.task_code = task_code;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public String getTarget_vips_count() {
        return target_vips_count;
    }

    public void setTarget_vips_count(String target_vips_count) {
        this.target_vips_count = target_vips_count;
    }

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getRun_scope() {
        return run_scope;
    }

    public void setRun_scope(String run_scope) {
        this.run_scope = run_scope;
    }

    public String getStore_count() {
        return store_count;
    }

    public void setStore_count(String store_count) {
        this.store_count = store_count;
    }
    public String getVip_condition() {
        return vip_condition;
    }

    public void setVip_condition(String vip_condition) {
        this.vip_condition = vip_condition;
    }

    public String getVip_task() {
        return vip_task;
    }

    public void setVip_task(String vip_task) {
        this.vip_task = vip_task;
    }

}
