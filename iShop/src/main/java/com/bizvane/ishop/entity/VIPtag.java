package com.bizvane.ishop.entity;

import java.util.Date;

/**
 * Created by lixiang on 2016/6/12.
 * 用户标签
 *
 * @@version
 */
public class VIPtag {
    private int id;
    //用户标签编号
    private String tag_name;
    //标签类型
    private String tag_type;
    //企业编号
    private String corp_code;
    //修改日期
    private String modified_date;
    //修改人
    private String modifier;
    //创建日期
    private String created_date;
    //创建者
    private String creater;
    //是否可用
    private String isactive;
    private Corp corp;

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public VIPtag() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getTag_type() {
        return tag_type;
    }

    public void setTag_type(String tag_type) {
        this.tag_type = tag_type;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }


    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
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
