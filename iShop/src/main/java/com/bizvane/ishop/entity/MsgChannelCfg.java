package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2017/5/4.
 */
public class MsgChannelCfg {
    private String id;
    //所属企业
    private String corp_code;
    //编号
    private String random_code;
    //通道类型
    private String type;
    //通道名称
    private String channel_name;
    //账户
    private String channel_account;
    //密码
    private String password;
    //生产通道关联品牌
    private String brand_code_production;

    //单价
    private String channel_price;
    //签名
    private String channel_sign;
    //子账户
    private String channel_child;
    //通道编号
    private String channel_code;
    //是否强制
    private String is_forced;
    //品牌编号
    private String brand_code;

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

    private String corp_name;
    private String ch_name;
    private String name;


    private Corp corp;

    public String getCh_name() {
        return ch_name;
    }

    public void setCh_name(String ch_name) {
        this.ch_name = ch_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }


    public String getRandom_code() {
        return random_code;
    }

    public void setRandom_code(String random_code) {
        this.random_code = random_code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getChannel_account() {
        return channel_account;
    }

    public void setChannel_account(String channel_account) {
        this.channel_account = channel_account;
    }

    public String getChannel_price() {
        return channel_price;
    }

    public void setChannel_price(String channel_price) {
        this.channel_price = channel_price;
    }

    public String getChannel_sign() {
        return channel_sign;
    }

    public void setChannel_sign(String channel_sign) {
        this.channel_sign = channel_sign;
    }

    public String getChannel_child() {
        return channel_child;
    }

    public void setChannel_child(String channel_child) {
        this.channel_child = channel_child;
    }

    public String getChannel_code() {
        return channel_code;
    }

    public void setChannel_code(String channel_code) {
        this.channel_code = channel_code;
    }


    public String getIs_forced() {
        return is_forced;
    }

    public void setIs_forced(String is_forced) {
        this.is_forced = is_forced;
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

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public String getBrand_code() {
        return brand_code;
    }

    public void setBrand_code(String brand_code) {
        this.brand_code = brand_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBrand_code_production() {
        return brand_code_production;
    }

    public void setBrand_code_production(String brand_code_production) {
        this.brand_code_production = brand_code_production;
    }
}
