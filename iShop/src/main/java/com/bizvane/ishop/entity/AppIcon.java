package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2017/5/4.
 */
public class AppIcon {
    private String  id;
    //所属企业
    private String icon_name;
    //标志
    private String flag;
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
    private String show_order;

    public String getShow_order() {
        return show_order;
    }

    public void setShow_order(String show_order) {
        this.show_order = show_order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon_name() {
        return icon_name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setIcon_name(String icon_name) {
        this.icon_name = icon_name;
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
