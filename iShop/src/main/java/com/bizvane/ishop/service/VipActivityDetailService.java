package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VipActivityDetail;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by nanji on 2017/1/5.
 */
public interface VipActivityDetailService {

    PageInfo<VipActivityDetail> selectAllActivityDetail(int page_num, int page_size, String corp_code, String user_code, String search_value) throws Exception;

    PageInfo<VipActivityDetail> selectllScreen(int page_num, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception;

    int delete(String activity_code) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    VipActivityDetail selectActivityById(int id) throws Exception;

    VipActivityDetail selActivityDetailByCode(String activity_code) throws Exception;

    String creatActivityInviteQrcode(String corp_code,String auth_appid, String activity_code,String user_id) throws Exception;

     String insert_new(String message, String user_id) throws Exception;
}
