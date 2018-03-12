package com.bizvane.ishop.entity;

/**
 * Created by yanyadong on 2017/8/29.
 *   问卷调查
 */

public class Questionnaire {
    private  int id;
    private  String corp_code;
    private  String title; //问卷名称
    private  String illustrate;  //问卷说明
    private  String template;  //模板
    private  String modified_date;
    private  String modifier;
    private  String created_date;
    private  String creater;
    private  String isactive;
    private  String corp_name;
    private  String isrelease; //是否发布
    private  int qtNaire_num;//答卷数量
    private  String bg_url; //背景图片地址

    public String getBg_url() {
        return bg_url;
    }

    public void setBg_url(String bg_url) {
        this.bg_url = bg_url;
    }

    public int getQtNaire_num() {
        return qtNaire_num;
    }

    public void setQtNaire_num(int qtNaire_num) {
        this.qtNaire_num = qtNaire_num;
    }

    public String getIsrelease() {
        return isrelease;
    }

    public void setIsrelease(String isrelease) {
        this.isrelease = isrelease;
    }

    public String getIllustrate() {
        return illustrate;
    }

    public void setIllustrate(String illustrate) {
        this.illustrate = illustrate;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
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
