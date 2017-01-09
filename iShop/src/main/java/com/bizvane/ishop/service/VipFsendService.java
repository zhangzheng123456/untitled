package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VipFsend;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
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

    String insert(VipFsend vipFsend,int tem_id,String role_code,String user_brand_code, String user_area_code,String user_store_code,String user_code) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<VipFsend> getAllVipFsendScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception;

    void test1();

    void test2();

     int insertSend(VipFsend vipFsend) throws Exception ;

    int delSendByActivityCode(String corp_code,String activity_vip_code)throws Exception;

    List<VipFsend> getSendByActivityCode(String corp_code,String activity_vip_code)throws Exception;


}
