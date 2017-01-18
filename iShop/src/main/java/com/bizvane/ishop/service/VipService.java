package com.bizvane.ishop.service;


import com.alibaba.fastjson.JSONObject;
import com.mongodb.DBCollection;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by ZhouZhou on 2017/1/16.
 */
public interface VipService {

    String addVip(HashMap<String,Object> vipInfo) throws Exception;

    String saveVipInfo(JSONObject jsonObject, Date now) throws Exception;

    String recharge(JSONObject jsonObject,String user_code,String user_name) throws Exception;

    String sendSMS(String text,String phone) throws Exception;
}
