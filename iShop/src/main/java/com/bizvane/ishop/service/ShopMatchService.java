package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.DBObject;

/**
 * Created by PC on 2016/12/28.
 */
public interface ShopMatchService {
    JSONObject getGoodsByWx(String corp_code, String pageSize, String pageIndex, String categoryId, String row_num, String productName) throws Exception;

    void insert(String corp_code, String d_match_code, String d_match_title, String d_match_image, String d_match_desc, JSONArray r_match_goods, String user_code) throws Exception;

    void addRelByType(String corp_code, String d_match_code, String operate_userCode, String operate_type, String status, String comment_text) throws Exception;

    void updRelByType(String corp_code, String d_match_code, String operate_userCode, String operate_type) throws Exception;

    DBObject selectByCode(String corp_code, String d_match_code) throws Exception;

    void deleteAll(String corp_code,String d_match_code)throws  Exception;


    }
