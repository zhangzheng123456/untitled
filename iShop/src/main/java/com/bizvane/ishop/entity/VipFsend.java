package com.bizvane.ishop.entity;

/**
 * Created by nanji on 2016/11/24.
 */
public class VipFsend {
    private int id;
    //群发短信编号
    private String sms_code;
    //公司编号
    private String corp_code;
    //接收群发短信的会员
    private String sms_vips;
    //会员分组编号
    private String vip_group_code;
    //群发短信内容
    private String content;
    //修改时间
    private String modified_date;
    //修改人
    private String modifier;
    //创建时间
    private String created_date;
    //发送类型
    private String send_type;
    //发送范围
    private String send_scope;
    //创建者
    private String creater;
    //是否可用
    private String isactive;
    private String corp_name;
    private Corp corp;
    private String target_vips_count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getSms_vips() {
        return sms_vips;
    }

    public void setSms_vips(String sms_vips) {
        this.sms_vips = sms_vips;
    }

    public String getVip_group_code() {
        return vip_group_code;
    }

    public void setVip_group_code(String vip_group_code) {
        this.vip_group_code = vip_group_code;
    }

    public String getSend_scope() {
        return send_scope;
    }

    public void setSend_scope(String send_scope) {
        this.send_scope = send_scope;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getTarget_vips_count() {
        return target_vips_count;
    }

    public void setTarget_vips_count(String target_vips_count) {
        this.target_vips_count = target_vips_count;
    }

    public String getSend_type() {
        return send_type;
    }

    public void setSend_type(String send_type) {
        this.send_type = send_type;
    }
}
