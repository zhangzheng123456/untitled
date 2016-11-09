package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Sign;
import com.github.pagehelper.PageInfo;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/23.
 */
public interface SignService {
    PageInfo<Sign> selectSignByInp(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String role_code,String area_store_code) throws Exception;

    PageInfo<Sign> selectByUser(int page_number, int page_size, String corp_code, String user_code, String search_value) throws Exception;

    int delSignById(int id) throws Exception;

    PageInfo<Sign> selectSignAllScreen(int page_number, int page_size, String corp_code, String area_code, String store_code, String role_code, Map<String, String> map,String area_store_code) throws Exception;

    PageInfo<Sign> selectSignAllScreenByUser(int page_number, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception;

    int insert(Sign sign) throws Exception;

    int deleteByUser(String user_code,String corp_code)throws Exception;

    List<Sign> selectUserRecord(String corp_code, String user_code, String date,String status) throws Exception;
}
