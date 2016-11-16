package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2016/11/15.
 */
public class Activity {
    private int id;
    //所属企业
    private String corp_code;
    //活动主题
    private String activity_theme;
    //活动执行方式
    private String run_mode;
    //活动开始时间
    private String start_time;
    //活动结束时间
    private String end_time;
    //活动目标会员
    private String activity_vip;
    //活动执行人
    private String activity_operator;
    //短信正文
    private String msg_info;
    //任务标题
    private String task_title;
    //任务描述
    private String task_desc;


    //推送标题
    private String wechat_title;
    //推送内容
    private String wechat_desc;


    //活动外部链接
    private String activity_url;
    //活动详情
    private String activity_content;
    //活动生成链接
    private String content_url;
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

    public String getActivity_vip() {
        return activity_vip;
    }

    public void setActivity_vip(String activity_vip) {
        this.activity_vip = activity_vip;
    }

    public String getActivity_operator() {
        return activity_operator;
    }

    public void setActivity_operator(String activity_operator) {
        this.activity_operator = activity_operator;
    }

    public String getMsg_info() {
        return msg_info;
    }

    public void setMsg_info(String msg_info) {
        this.msg_info = msg_info;
    }

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getTask_desc() {
        return task_desc;
    }

    public void setTask_desc(String task_desc) {
        this.task_desc = task_desc;
    }

    public String getWechat_title() {
        return wechat_title;
    }

    public void setWechat_title(String wechat_title) {
        this.wechat_title = wechat_title;
    }

    public String getWechat_desc() {
        return wechat_desc;
    }

    public void setWechat_desc(String wechat_desc) {
        this.wechat_desc = wechat_desc;
    }

    public String getActivity_url() {
        return activity_url;
    }

    public void setActivity_url(String activity_url) {
        this.activity_url = activity_url;
    }

    public String getActivity_content() {
        return activity_content;
    }

    public void setActivity_content(String activity_content) {
        this.activity_content = activity_content;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
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
}
