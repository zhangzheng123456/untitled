package com.bizvane.ishop.service;


import com.alibaba.fastjson.JSONObject;
import com.bizvane.sun.v1.common.DataBox;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by zhou on 2016/7/6.
 *
 * @@version
 */
public interface IceInterfaceService {
    DataBox iceInterface(String method , Map datalist) throws Exception;

    DataBox iceInterfaceV2(String method ,Map datalist) throws Exception;

    DataBox iceInterfaceV3(String method ,Map datalist) throws Exception;

    Map vipBasicMethod(String page_num, String page_size, String corp_code, HttpServletRequest request) throws Exception;

    Map vipAnalysisBasicMethod(JSONObject jsonObject, HttpServletRequest request) throws Exception;

    DataBox vipScreenMethod(String page_num,String page_size,String corp_code,String area_code,String brand_code,String store_code,String user_code) throws Exception;

    DataBox vipScreenMethod2(String page_num,String page_size,String corp_code,String screen) throws Exception;

    DataBox vipScreen2ExeclMethod(String page_num,String page_size,String corp_code,String area_code,String brand_code,String store_code,String user_code,String output_message) throws Exception;

    DataBox addNewVip(String corp_code,String vip_id,String vip_name,String sex,String birthday,String phone,
                      String vip_card_type,String card_no,String store_code,String user_code) throws Exception;

    DataBox saveVipExtendInfo(String corp_code,String vip_id,String custom) throws Exception;

    DataBox changeVipType(String corp_code,String vip_id,String card_type) throws Exception;
}
