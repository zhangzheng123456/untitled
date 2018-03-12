package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.SendCoupons;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/24.
 */

public interface CouponsLogService {

    JSONArray transLog(DBCursor dbCursor)throws Exception;
     BasicDBObject getScreen(JSONArray screen, String corp_code) throws Exception;
    

}
