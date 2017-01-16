package com.bizvane.ishop.service;

import java.util.HashMap;

/**
 * Created by yanyadong on 2017/1/13.
 */
public interface CRMInterfaceService {

    public String addVip(String corpcode,HashMap<String,Object> vipInfo);

    public  String selVip(String corpcode,int id);

    public  String  modInfoVip(String corpcode,HashMap<String,Object> modVip);

    public  String  modPasswordVip(String corpcode,HashMap<String,Object> modVip);

    public String couponInfo(String corpcode,int vipid);

    public  String  addPrepaidDocuments(String corpcode,HashMap<String,Object> documentInfo);

    public  String  modPrepaidStatus(String corpcode,HashMap<String,Object> modStatus);
}
