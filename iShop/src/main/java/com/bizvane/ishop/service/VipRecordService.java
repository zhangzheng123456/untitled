package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.VIPInfo;
import com.bizvane.ishop.entity.VipRecord;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
public interface VipRecordService {

    JSONArray transRecord(DBCursor dbCursor) throws Exception;

    JSONArray transFeedBack(DBCursor dbCursor,String corp_code) throws Exception;

    BasicDBObject getScreen(JSONArray screen, String corp_code) throws Exception;

    BasicDBList getRoleCondition(HttpServletRequest request, String corp_code, BasicDBList value) throws Exception;
}
