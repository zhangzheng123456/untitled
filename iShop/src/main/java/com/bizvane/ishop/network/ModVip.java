package com.bizvane.ishop.network;

import java.io.Serializable;

/**
 * Created by yanyadong on 2017/1/13.
 */
public class ModVip implements Serializable {

    private int id;  //ID
    private String ad_client_id;  //所属公司
    private String ad_org_id;     //所属组织
    private String vipname;      //VIP姓名
    private String salesrep_id;   //开卡人
    private String cardno;       //VIP卡号(AK)
    private String c_customer_id;    //经销商
    private String c_store_id;      //开卡店仓
    private  String c_viptype_id;     //VIP类型
    private  String docnos;          //零售单号
    private String salehremp_id;     //所属导购
    private String vipcardno;        //推荐入会的VIP卡号
    private int mobil;              //手机
    private String address;     //通信地址
    private  String sex;     //性别
    private  String birthday;    //出生日期(阳历)
    private  String ownerid;    //创建人

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAd_client_id() {
        return ad_client_id;
    }

    public void setAd_client_id(String ad_client_id) {
        this.ad_client_id = ad_client_id;
    }

    public String getAd_org_id() {
        return ad_org_id;
    }

    public void setAd_org_id(String ad_org_id) {
        this.ad_org_id = ad_org_id;
    }

    public String getVipname() {
        return vipname;
    }

    public void setVipname(String vipname) {
        this.vipname = vipname;
    }

    public String getSalesrep_id() {
        return salesrep_id;
    }

    public void setSalesrep_id(String salesrep_id) {
        this.salesrep_id = salesrep_id;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getC_customer_id() {
        return c_customer_id;
    }

    public void setC_customer_id(String c_customer_id) {
        this.c_customer_id = c_customer_id;
    }

    public String getC_store_id() {
        return c_store_id;
    }

    public void setC_store_id(String c_store_id) {
        this.c_store_id = c_store_id;
    }

    public String getC_viptype_id() {
        return c_viptype_id;
    }

    public void setC_viptype_id(String c_viptype_id) {
        this.c_viptype_id = c_viptype_id;
    }

    public String getDocnos() {
        return docnos;
    }

    public void setDocnos(String docnos) {
        this.docnos = docnos;
    }

    public String getSalehremp_id() {
        return salehremp_id;
    }

    public void setSalehremp_id(String salehremp_id) {
        this.salehremp_id = salehremp_id;
    }

    public String getVipcardno() {
        return vipcardno;
    }

    public void setVipcardno(String vipcardno) {
        this.vipcardno = vipcardno;
    }

    public int getMobil() {
        return mobil;
    }

    public void setMobil(int mobil) {
        this.mobil = mobil;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getModifierid() {
        return modifierid;
    }

    public void setModifierid(String modifierid) {
        this.modifierid = modifierid;
    }

    public String getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate;
    }

    public String getModifieddate() {
        return modifieddate;
    }

    public void setModifieddate(String modifieddate) {
        this.modifieddate = modifieddate;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    private  String modifierid;   //修改人
    private  String creationdate;   //创建时间
    private  String modifieddate;  //修改时间
    private  String isactive;   //可用








}
