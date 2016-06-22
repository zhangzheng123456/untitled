package com.bizvane.ishop.entity;

/**
 * Created by yin on 2016/6/22.
 */
public class Interfacers {
    private int id;
    private String version;
    private String crop_code;
    private String created_date;
    private String creater;
    private String modified_date;
    private String modifier;
    private String isactive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCrop_code() {
        return crop_code;
    }

    public void setCrop_code(String crop_code) {
        this.crop_code = crop_code;
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

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }
}
