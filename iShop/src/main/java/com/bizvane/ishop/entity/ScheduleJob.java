package com.bizvane.ishop.entity;

import com.dexcoder.commons.pager.Pageable;

import java.util.Date;

/**
 * Bizvane
 * createTime : 2016-08-04
 * description : 计划任务模型
 * version : 1.0
 */
public class ScheduleJob extends Pageable {

    private static final long serialVersionUID = 4888005949821878223L;

    /** 任务id */
    private int schedule_job_id;

    /** 任务名称 */
    private String job_name;

    /** 任务别名 */
    private String alias_name;

    /** 任务分组 */
    private String job_group;

    /** 触发器 */
    private String job_trigger;

    /** 任务状态 */
    private String status;

    /** 任务运行时间表达式 */
    private String cron_expression;

    /** 任务描述 */
    private String description;

    /** 创建时间 */
    private String gmt_create;

    /** 修改时间 */
    private String gmt_modify;

    /** 任务执行方法 */
    private String func;

    public int getSchedule_job_id() {
        return schedule_job_id;
    }

    public void setSchedule_job_id(int schedule_job_id) {
        this.schedule_job_id = schedule_job_id;
    }

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getAlias_name() {
        return alias_name;
    }

    public void setAlias_name(String alias_name) {
        this.alias_name = alias_name;
    }

    public String getJob_group() {
        return job_group;
    }

    public void setJob_group(String job_group) {
        this.job_group = job_group;
    }

    public String getJob_trigger() {
        return job_trigger;
    }

    public void setJob_trigger(String job_trigger) {
        this.job_trigger = job_trigger;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCron_expression() {
        return cron_expression;
    }

    public void setCron_expression(String cron_expression) {
        this.cron_expression = cron_expression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(String gmt_create) {
        this.gmt_create = gmt_create;
    }

    public String getGmt_modify() {
        return gmt_modify;
    }

    public void setGmt_modify(String gmt_modify) {
        this.gmt_modify = gmt_modify;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    @Override
    public String toString() {
        return "ScheduleJob{" +
                "schedule_job_id=" + schedule_job_id +
                ", job_name='" + job_name + '\'' +
                ", alias_name='" + alias_name + '\'' +
                ", job_group='" + job_group + '\'' +
                ", job_trigger='" + job_trigger + '\'' +
                ", status='" + status + '\'' +
                ", cron_expression='" + cron_expression + '\'' +
                ", description='" + description + '\'' +
                ", gmt_create=" + gmt_create +
                ", gmt_modify=" + gmt_modify +
                ", func='" + func + '\'' +
                '}';
    }
}
