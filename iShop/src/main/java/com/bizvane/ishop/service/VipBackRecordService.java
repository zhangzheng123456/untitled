package com.bizvane.ishop.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * Created by yanyadong on 2017/4/28.
 */
public interface VipBackRecordService {

    public  BasicDBObject getQuery(String param) throws Exception;

    public  BasicDBObject getScreen(String message,String corp_code) throws Exception;

    //获取每种回访类型的总人数
    public String getUserCount(DBCollection dbCollection,String corp_code, String user_code, String action) throws  Exception;
}
