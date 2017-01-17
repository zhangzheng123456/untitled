package com.bizvane.ishop.service;

import java.util.HashMap;

/**
 * Created by yanyadong on 2017/1/13.
 */
public interface CRMInterfaceService {

    public String addVip(String corpcode,HashMap<String,Object> vipInfo);

    public  String selVip(String corpcode,int id);

    public  String  modInfoVip(String corpcode,HashMap<String,Object> modVip);

    public  String  modIntegral_passwordVip(String corpcode,HashMap<String,Object> integral_passwordVip);

    public  String  modfiy_passwordVip(String corpcode,HashMap<String,Object> passwordMap);

    public String couponInfo(String corpcode,int vipid);

    public  String  addPrepaidDocuments(String corpcode,HashMap<String,Object> documentInfo);

//    public  String  modPrepaidStatus(String corpcode,HashMap<String,Object> modStatus);

    public  String addRefund(String corpcode,HashMap<String,Object> refundInfo);

  //  public  String  modRefundStatus(String corpcode,HashMap<String,Object> modStatusRefund);

    public String submitPrepaidBill(String corpcode,int id);

    public String canclePrepaidBill(String corpcode,int id);

    public String submitRefundBill(String corpcode,int id);

    public String cancleRefundBill(String corpcode,int id);

    public  String selBill(String table,String corpcode,int id);

    public  String  confRefundBalance(String corpcode,String docno);

    public  String  confPrepaidOrder(String corpcode,String docno);
}
