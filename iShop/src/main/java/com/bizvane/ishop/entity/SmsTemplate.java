package com.bizvane.ishop.entity;

import org.apache.velocity.Template;

/**
 * Created by lixiang on 2016/6/22.
 *
 * @@version
 */
public class SmsTemplate {

    private int id;
    //模板编号
    private String template_code;
    //模板名称
    private String template_name;
    //模板内容
    private String template_content;
    //模板分组
    private String template_type;
    //企业编号
    private String corp_code;
    //修改日期
    private String modified_date;
    //修改人
    private String modifier;
    //创建日期
    private String created_date;
    //创建人
    private String creater;
    //是否可用
    private String isactive;
    //private String template_title;
    private Corp corp;
    private String corp_name;
    private String template_type_name;



    public String getTemplate_type_name() {
        return template_type_name;
    }

    public void setTemplate_type_name(String template_type_name) {
        this.template_type_name = template_type_name;
    }

    public String getTemplate_type() {
        return template_type;
    }

    public void setTemplate_type(String template_type) {
        this.template_type = template_type;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public SmsTemplate() {
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemplate_code() {
        return template_code;
    }

    public void setTemplate_code(String template_code) {
        this.template_code = template_code;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public String getTemplate_content() {
        return template_content;
    }

    public void setTemplate_content(String template_content) {
        this.template_content = template_content;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
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
}
