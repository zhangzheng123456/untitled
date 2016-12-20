package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.VipRules;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/12/19.
 */
public interface VipRulesService {
    VipRules getVipRulesById(int id) throws Exception;

    PageInfo<VipRules> getAllVipRulesByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<VipRules> getAllVipRulesScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception;

    VipRules getVipRulesByType(String corp_code,String vip_type)throws Exception;

    List<VipRules> selectVipRules(String corp_code, String vip_types) throws Exception;


     String getCouponInfo(String corp_code)throws Exception;

}
