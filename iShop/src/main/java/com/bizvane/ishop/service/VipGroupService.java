package com.bizvane.ishop.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.sun.v1.common.DataBox;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/8/31.
 */
public interface VipGroupService {

    VipGroup getVipGroupById(int id) throws Exception;


    PageInfo<VipGroup> getAllVipGroupByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    List<VipGroup> selectCorpVipGroups(String corp_code, String search_value) throws Exception;

    String insert(VipGroup vipGroup,String role_code,String user_brand_code,String user_area_code,String user_store_code, String user_id) throws Exception;

    String update(VipGroup vipGroup, String role_code,String user_brand_code,String user_area_code,String user_store_code,String user_code) throws Exception;

    int updateVipGroup(VipGroup vipGroup) throws Exception;

    int delete(int id) throws Exception;

    VipGroup getVipGroupByCode(String corp_code, String code,String isactive) throws Exception;

    List<VipGroup> getVipGroupByName(String corp_code, String name,String isactive) throws Exception;

    PageInfo<VipGroup> getAllVipGrouScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception;

    DataBox vipScreenBySolr(JSONArray screen,String corp_code,String page_num,String page_size,String role_code,
                            String user_brand_code,String user_area_code,String user_store_code,String user_code1,
                            String sort_key,String sort_value) throws Exception;

    DataBox vipScreenBySolrNew(JSONArray screen,String corp_code,String page_num,String page_size,String role_code,
                            String user_brand_code,String user_area_code,String user_store_code,String user_code1,
                            String sort_key,String sort_value) throws Exception;


    JSONArray vipScreen2Array(JSONArray screen,String corp_code,String role_code, String user_brand_code, String user_area_code,
                               String user_store_code,String user_code1)throws Exception;

    JSONObject groupCustom(JSONObject condition, String corp_code)throws Exception;

    JSONObject vipGroupCustom(JSONObject condition,String corp_code,String role_code,
                              String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception;

    JSONArray vipScreen2ArrayNew(JSONArray screen,String corp_code,String role_code,
                                 String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception;

//    JSONArray vipScreen2ArrayNewV2(JSONArray screen,String corp_code,String role_code, String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception;
}
