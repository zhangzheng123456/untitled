package com.bizvane.ishop.entity;

/**
 * Created by PC on 2016/12/27.
 */
public class ShopMatch {
    private String corp_code;
    private String d_match_code;
    private String d_match_title;
    private String d_match_image;
    private String d_match_desc;
    private String d_match_likeCount;
    private String d_match_commentCount;
    private String d_match_collectCount;
    private String  r_match_goodsCode;
    private String  r_match_goodsImage;
    private String  r_match_goodsPrice;
    private String  r_match_goodsName;
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

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getD_match_code() {
        return d_match_code;
    }

    public void setD_match_code(String d_match_code) {
        this.d_match_code = d_match_code;
    }

    public String getD_match_title() {
        return d_match_title;
    }

    public void setD_match_title(String d_match_title) {
        this.d_match_title = d_match_title;
    }

    public String getD_match_image() {
        return d_match_image;
    }

    public void setD_match_image(String d_match_image) {
        this.d_match_image = d_match_image;
    }

    public String getD_match_desc() {
        return d_match_desc;
    }

    public void setD_match_desc(String d_match_desc) {
        this.d_match_desc = d_match_desc;
    }

    public String getD_match_likeCount() {
        return d_match_likeCount;
    }

    public void setD_match_likeCount(String d_match_likeCount) {
        this.d_match_likeCount = d_match_likeCount;
    }

    public String getD_match_commentCount() {
        return d_match_commentCount;
    }

    public void setD_match_commentCount(String d_match_commentCount) {
        this.d_match_commentCount = d_match_commentCount;
    }

    public String getD_match_collectCount() {
        return d_match_collectCount;
    }

    public void setD_match_collectCount(String d_match_collectCount) {
        this.d_match_collectCount = d_match_collectCount;
    }

    public String getR_match_goodsCode() {
        return r_match_goodsCode;
    }

    public void setR_match_goodsCode(String r_match_goodsCode) {
        this.r_match_goodsCode = r_match_goodsCode;
    }

    public String getR_match_goodsImage() {
        return r_match_goodsImage;
    }

    public void setR_match_goodsImage(String r_match_goodsImage) {
        this.r_match_goodsImage = r_match_goodsImage;
    }

    public String getR_match_goodsPrice() {
        return r_match_goodsPrice;
    }

    public void setR_match_goodsPrice(String r_match_goodsPrice) {
        this.r_match_goodsPrice = r_match_goodsPrice;
    }

    public String getR_match_goodsName() {
        return r_match_goodsName;
    }

    public void setR_match_goodsName(String r_match_goodsName) {
        this.r_match_goodsName = r_match_goodsName;
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
