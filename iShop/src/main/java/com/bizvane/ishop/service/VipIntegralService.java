package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VipIntegral;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/4/7.
 */
public interface VipIntegralService {

    VipIntegral selectIntegralById(int id) throws Exception;

    VipIntegral selectIntegralByBillno(String bill_no) throws Exception;

    PageInfo<VipIntegral> selectIntegralAll(int page_number,int page_size,String corp_code,String search_value) throws Exception;

    int deleteIntegralById(int id) throws Exception;

    String insertVipIntegral(VipIntegral vipIntegral,String user_code,String group_code,String role_code) throws Exception;

    String updateVipIntegral(VipIntegral vipIntegral,String user_code,String group_code,String role_code)throws  Exception;

    void updateVipIntegral(VipIntegral vipIntegral) throws Exception;

    PageInfo<VipIntegral> selectIntegralAllScreen(int page_number,int page_size,String corp_code,Map<String, Object> params)throws Exception;

    VipIntegral selectIntegralByName(String corp_code,String integral_name) throws  Exception;

    List<VipIntegral> selectIntegralScreen(String corp_code, Map<String, Object> params) throws  Exception;

    PageInfo<VipIntegral> switchList(PageInfo<VipIntegral> list) throws Exception;
}
