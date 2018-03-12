package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2016/7/7.
 */
public class VipLabel {
    private int id;
    //标签名称
    private String label_name;
    //标签类型
    private String label_type;
    //修改日期
    private String modified_date;
    //创建日期
    private String created_date;
    //创建者
    private String creater;
    //修改人
    private String modifier;
    //是否可用
    private String isactive;
    private String corp_code;
    private Corp corp;
    private String corp_name;
    private ViplableGroup viplablegroup;
    private String label_group_code;
    private String label_group_name;
    //使用量
    private String countlable;
    private String label_id;
    private String rid;
    private String label_sign;
    private String brand_code;
    private String brand_name;
    private String count;
    private String cnt;

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getBrand_code() {
        return brand_code;
    }

    public void setBrand_code(String brand_code) {
        this.brand_code = brand_code;
    }

    public String getLabel_sign() {
        return label_sign;
    }

    public void setLabel_sign(String label_sign) {
        this.label_sign = label_sign;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getCountlable() {
        return countlable;
    }

    public void setCountlable(String countlable) {
        this.countlable = countlable;
    }

    public String getLabel_id() {
        return label_id;
    }

    public void setLabel_id(String label_id) {
        this.label_id = label_id;
    }

    public ViplableGroup getViplablegroup() {
        return viplablegroup;
    }

    public void setViplablegroup(ViplableGroup viplablegroup) {
        this.viplablegroup = viplablegroup;
    }

    public String getLabel_group_code() {
        return label_group_code;
    }

    public void setLabel_group_code(String label_group_code) {
        this.label_group_code = label_group_code;
    }

    public String getLabel_group_name() {
        return label_group_name;
    }

    public void setLabel_group_name(String label_group_name) {
        this.label_group_name = label_group_name;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public VipLabel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel_name() {
        return label_name;
    }

    public void setLabel_name(String label_name) {
        this.label_name = label_name;
    }

    public String getLabel_type() {
        return label_type;
    }

    public void setLabel_type(String label_type) {
        this.label_type = label_type;
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

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }
}
