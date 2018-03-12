package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.VipFsend;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/24.
 */
public interface VipFsendService {

    String getVipFsendById(int id) throws Exception;

    VipFsend getVipFsendInfoById(int id) throws Exception;

    VipFsend getVipFsendInfoByCode(String corp_code,String sms_code) throws Exception;

    PageInfo<VipFsend> getAllVipFsendByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    int updateVipFsend(VipFsend vipFsend) throws Exception;

    int insertSend(VipFsend vipFsend) throws Exception;

    String insert(VipFsend vipFsend,String user_code,String group_code,String role_code) throws Exception;

    String checkVipFsend(VipFsend vipFsend,String user_code) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<VipFsend> getAllVipFsendScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception;

    String sendMessage(VipFsend vipFsend, String user_code) throws Exception;

     int insertSend(VipFsend vipFsend,String user_code,String group_code,String role_code) throws Exception ;

    int delSendByActivityCode(String corp_code,String activity_vip_code)throws Exception;

    List<VipFsend> getSendByActivityCode(String corp_code,String activity_vip_code)throws Exception;

    String textReplace(String content,JSONObject vip_info) throws Exception;

    String sendSmsActivity(VipFsend vipFsend, String user_code,String activity_code) throws Exception;

    int getSendVipCount(String corp_code,String send_scope,String sms_vips,  String role_code, String user_brand_code, String user_area_code, String user_store_code, String user_code) throws Exception;

    void insertMongoDB(String corp_code, String user_code, String template_id, String openid, String vip_id, String vip_name, String cardno, String vip_phone,
                              String sms_code, String app_id, String content, String message_id, String send_status,String errmsg) throws Exception;

    String getVipFsendByMessage(String message) throws Exception;

    PageInfo<VipFsend> switchVipFsend(PageInfo<VipFsend> list) throws Exception;
}
