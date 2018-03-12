package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.github.pagehelper.PageInfo;
import com.mongodb.DBCursor;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yanyadong on 2017/3/8.
 */
public interface ActivityAnalyService {

    JSONObject executeDetail(String corp_code,String activity_code,String task_code) throws Exception;

    JSONArray taskCompleteDetail(String corp_code,String activity_code,String task_code) throws Exception;

    JSONArray userExecuteDetail(String corp_code, String task_code, String user_code) throws Exception;

    public JSONObject getShareSize(String message) throws  Exception;

    public JSONObject getShareViewByDate(String message) throws  Exception;

    public JSONObject getClickViewByDate(String message) throws Exception;

    public JSONObject getUserList(String message) throws Exception;

    public JSONObject getIntentionSize(String message) throws Exception;

    public JSONObject getIntentionList(String message) throws Exception;

    public  JSONObject getCouponSize(String message) throws  Exception;

    public JSONObject getCouponList(String message) throws  Exception;

    public JSONObject getCouponView(String message) throws Exception;


    public JSONObject noticeAnalyByCard(String message) throws Exception;

    public JSONObject noticeAnalyByStore1(String message) throws Exception;

    public JSONObject noticeAnalyList1(String message) throws Exception;

    public JSONArray getClickViewByDateForUser(String message) throws Exception;

    PageInfo<Store> getStoreTargrtVips(String corp_code, String run_scope, String screen_store_code , String screen_store_name, int page_num, int page_size) throws Exception;

    JSONObject getStoreTargrtVips1(String corp_code,String store_code,String area_code ,String target_vip) throws Exception;

    public List<User> getActivityUserCode(String corp_code, String activity_store_code) throws  Exception;

    public  JSONObject getSalesRate(String message) throws Exception;

    public  JSONArray getSalesChart(String message) throws Exception;

    public JSONObject getSalesList(String message) throws  Exception;

    public String getTypeCode(String activity_code) throws Exception;

    public List<HashMap<String,Object>> getCouponByActivityCode1(String activity_code) throws Exception;

    public List<Object> switchJsonArray(JSONArray jsonArray) throws Exception;

    public  JSONObject  getVipEffectRate(String message) throws Exception;

    public  JSONObject getVipEffectChat(String message) throws Exception;

    public JSONObject getRegisterList(String message)throws Exception;

    public JSONObject getVipEffectList1(String message) throws Exception;


    public  JSONObject getOnlineChat(String message) throws Exception;

    public  JSONObject getOnlineRate(String message) throws Exception;

    public  JSONObject getOnlineListByVip(String message) throws Exception;

    public  JSONObject getOnlineListByStore(String message) throws Exception;

    public  List<HashMap<String,Object>> getOnlineSwitchVip(DBCursor dbObjects)throws Exception;



}
