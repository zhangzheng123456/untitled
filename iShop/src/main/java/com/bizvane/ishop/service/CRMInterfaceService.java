package com.bizvane.ishop.service;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yanyadong on 2017/1/13.
 */
public interface CRMInterfaceService {

    public String addVip(String corp_code,HashMap<String,Object> vipInfomap) throws Exception;

    public  String selVip(List<String> drplist, int vipId) throws Exception;


    public  String  modInfoVip(String corp_code,HashMap<String,Object> modVip) throws Exception;

    public  String  modIntegral_passwordVip(String corp_code,HashMap<String,Object> integral_passwordVip) throws Exception;

    public  String  modfiy_passwordVip(String corp_code,HashMap<String,Object> passwordMap) throws Exception;

    public String couponInfo(String corp_code,HashMap<String,Object> couponMap) throws Exception;

    public  String  addPrepaidDocuments(String corp_code,HashMap<String,Object> documentInfo) throws Exception;

    public  String  modPrepaidStatus(String corp_code,HashMap<String,Object> modStatus) throws Exception;

    public  String addRefund(String corp_code,HashMap<String,Object> refundInfo) throws Exception;

    public  String  modRefundStatus(String corp_code,HashMap<String,Object> modStatusRefund) throws Exception;

    public String submitPrepaidBill(String corp_code,int id) throws Exception;

    public String canclePrepaidBill(String corp_code,int id) throws Exception;

    public String submitRefundBill(String corp_code,int id) throws Exception;

    public String cancleRefundBill(String corp_code,int id) throws Exception;

    public  String selBill(String table,List<String> drplist,int id) throws Exception;

    public  String  getBalance(String corp_code,String vipId) throws Exception;

    public  String  getPrepaidOrder(String corp_code,HashMap<String,Object> prepaidMap) throws Exception;

    public  String getRefundOrder(String corp_code,HashMap<String,Object> refundMap) throws Exception;

    public List<String>  connectionDrpSql(String corp_code) throws Exception;

    public  String updateBaseInfoVip(String corp_code,HashMap<String,Object> modBaseVip) throws Exception;

    public String getVipRechargeRecord(String corp_code,int vipId) throws Exception;

    //获取生日券
    public  String  getVipBirthTicket(String corp_code,HashMap<String,Object> couponMap) throws Exception;
   //卡类型
    public  String getVipTypeCardInfo(String corp_code,int card_id) throws Exception;

    //删除充值单据
    public String delPrepaidBill(String corp_code,int id) throws Exception;
    //删除充值退款单据
    public String delRefundOrder(String corp_code,int id) throws Exception;

    //根据C_CUSTOMER_ID	经销商，MOBIL 手机号查询
    public  String  selVipByVipInfo(String corp_code,HashMap<String,Object> hashMap) throws Exception;
}
