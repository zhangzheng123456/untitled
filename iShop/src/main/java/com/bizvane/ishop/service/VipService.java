package com.bizvane.ishop.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by ZhouZhou on 2017/1/16.
 */
public interface VipService {

    DBCursor findVipByMongo(String corp_code, String vip_id) throws Exception;

    JSONObject addVip(String corp_code,JSONObject jsonObject) throws Exception;

    JSONObject saveVipInfo(JSONObject jsonObject, Date now) throws Exception;

    String recharge(JSONObject jsonObject,String user_code,String user_name) throws Exception;

    String numberFormat(String text) throws Exception;

    String vipAvatar(String corp_code,String result) throws Exception;

    String vipLastSendTime(String corp_code,String result) throws Exception;

    JSONArray vipAnalysisTime(JSONArray jsonArray, String start_time , String end_time)throws Exception;

    JSONObject getVip(String corp_code,String phone,String vip_card_type) throws Exception;

    JSONObject pareseData1(JSONObject object1);

    JSONObject getVipAuthCode(String corp_code,String vip_id,String phone,String vip_name,String type) throws Exception;
}
