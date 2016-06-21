package com.bizvane.ishop.entity;

/**
 * Created by yin on 2016/6/21.
 */
public class Appversion {
    private int id;
    private  String platform;
    private String download_addr;
    private String version_id;
    private String is_force_update;
    private String version_describe;
    private String crop_code;
    private String created_date;
    private String creater;
    private String modifier;
    private String modified_date;
    private String isactive;

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDownload_addr() {
        return download_addr;
    }

    public void setDownload_addr(String download_addr) {
        this.download_addr = download_addr;
    }

    public String getVersion_id() {
        return version_id;
    }

    public void setVersion_id(String version_id) {
        this.version_id = version_id;
    }

    public String getIs_force_update() {
        return is_force_update;
    }

    public void setIs_force_update(String is_force_update) {
        this.is_force_update = is_force_update;
    }

    public String getVersion_describe() {
        return version_describe;
    }

    public void setVersion_describe(String version_describe) {
        this.version_describe = version_describe;
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
