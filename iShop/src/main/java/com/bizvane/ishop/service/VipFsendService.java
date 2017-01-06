package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VipFsend;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/24.
 */
public interface VipFsendService {

    String getVipFsendById(int id) throws Exception;

    VipFsend getVipFsendInfoById(int id) throws Exception;

    PageInfo<VipFsend> getAllVipFsendByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    String insert(String message, String user_id,int tem_id) throws Exception;


    int delete(int id) throws Exception;

    PageInfo<VipFsend> getAllVipFsendScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception;

    void test1();

    void test2();

     int insertSend(VipFsend vipFsend) throws Exception ;

    int delSendByActivityCode(String corp_code,String activity_vip_code)throws Exception;


}
