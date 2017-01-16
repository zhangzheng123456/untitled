package com.bizvane.ishop.service;

import java.util.HashMap;

/**
 * Created by yanyadong on 2017/1/13.
 */
public interface CRMInterfaceService {

    public String addVip(HashMap<String,Object> vipInfo);

    public  String selVip(int id);

    public  String  modInfoVip(HashMap<String,Object> modVip);

    public  String  modPasswordVip(HashMap<String,Object> modVip);

    public String couponInfo(int vipid);
}
