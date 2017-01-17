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

    String recharge(JSONObject jsonObject,DBCollection cursor) throws Exception;
}
