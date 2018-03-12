package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.SendCoupons;
import com.bizvane.ishop.entity.SendCoupons;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/24.
 */

public interface SendCouponsService {

    SendCoupons getSendCouponsById(int id) throws Exception;

    SendCoupons getSendCouponsInfoById(int id) throws Exception;

    SendCoupons getSendCouponsInfoByCode(String corp_code, String ticket_code) throws Exception;

    PageInfo<SendCoupons> getAllSendCouponsByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    String updateSendCoupons(String message,String user_code) throws Exception;

    String insert(String  message,String user_code) throws Exception;

    String checkSendCoupons(SendCoupons vipFsend, String ticket_code) throws Exception;

    String delete(int id,String user_code) throws Exception;

    PageInfo<SendCoupons> getAllSendCouponsScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception;

    String sendMessage(SendCoupons sendCoupons, String user_code) throws Exception;


    int delSendByCode(String corp_code, String ticket_code)throws Exception;

    List<SendCoupons> getSendByCode(String corp_code, String ticket_code)throws Exception;

    PageInfo getInfo(int page_number, int page_size,String ids,String search_value) throws Exception;


}
