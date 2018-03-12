package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.VipRules;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/12/19.
 */
public interface VipRulesService {
    VipRules getVipRulesById(int id) throws Exception;

    PageInfo<VipRules> getAllVipRulesByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    List<VipRules> getViprulesList(String corp_code,String isactive)throws Exception;

    String insert(String message, String user_id) throws Exception;

    int insert(VipRules vipRules) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<VipRules> getAllVipRulesScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception;

    List<VipRules> getVipRulesByType(String corp_code,String vip_type,String high_vip_type,String isactive)throws Exception;

    List<VipRules> getViprulesByCardTypeCode(String corp_code,String vip_card_type_code)throws Exception;

    List<VipRules> selectByCardHighCode(String corp_code, String high_vip_card_type_code) throws Exception;

    int deleteActivity(String activity_code) throws Exception;

    JSONArray getAllCoupon(String appid, String access_key) throws Exception;

    List<VipRules> selectByVipCardTypeCode(String corp_code,String vip_card_type_code,String high_vip_card_type_code,String isactive) throws Exception;
}
