package com.bizvane.ishop.entity;

/**
 * Created by PC on 2016/11/24.
 */
public class DefGoodsMatch {
    private int id;
    private String goods_match_code;
    private String goods_match_title;
    private String goods_match_desc;
    private String corp_code;
    private String goods_code;
    //修改时间
    private String modified_date;
    //修改人
    private String modifier;
    //创建时间
    private String created_date;
    //创建者
    private String creater;
    //创建者
    private String match_display;
    //是否可用
    private String isactive;
    private String goods_image;
    private String dgmid;

    public String getMatch_display() {
        return match_display;
    }

    public void setMatch_display(String match_display) {
        this.match_display = match_display;
    }

    public String getDgmid() {
        return dgmid;
    }

    public void setDgmid(String dgmid) {
        this.dgmid = dgmid;
    }

    public String getGoods_match_title() {
        return goods_match_title;
    }

    public void setGoods_match_title(String goods_match_title) {
        this.goods_match_title = goods_match_title;
    }

    public String getGoods_match_desc() {
        return goods_match_desc;
    }

    public void setGoods_match_desc(String goods_match_desc) {
        this.goods_match_desc = goods_match_desc;
    }

    public String getGoods_image() {
        return goods_image;
    }

    public void setGoods_image(String goods_image) {
        this.goods_image = goods_image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoods_match_code() {
        return goods_match_code;
    }

    public void setGoods_match_code(String goods_match_code) {
        this.goods_match_code = goods_match_code;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
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
