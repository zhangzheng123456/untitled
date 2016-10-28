package com.bizvane.ishop.service;


import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.VipGroup;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/8/31.
 */
public interface VipGroupService {

    VipGroup getVipGroupById(int id) throws Exception;


    PageInfo<VipGroup> getAllVipGroupByPage(int page_number, int page_size, String corp_code,String user_code1, String search_value) throws Exception;

    List<VipGroup> selectCorpVipGroups(String corp_code,String search_value) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int updateVipGroup(VipGroup vipGroup) throws Exception;

    int delete(int id) throws Exception;

    VipGroup   getVipGroupByCode(String corp_code, String code,String isactive) throws Exception;

    VipGroup   getVipGroupByName(String corp_code, String name,String isactive) throws Exception;

    PageInfo<VipGroup> getAllVipGrouScreen(int page_number, int page_size, String corp_code, String user_code1, Map<String, String> map) throws Exception;

//    JSONArray findVipsGroup(JSONArray array) throws Exception;

    JSONArray checkVipsGroup(JSONArray array,String vip_ids) throws Exception;
}
