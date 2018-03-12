package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.User;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/7/14.
 */
public interface BaseService {

    PageInfo<HashMap<String, Object>> queryMetaList(int page_number, int page_size, Map<String, Object> params) throws SQLException;

    String storeIdConvertStoreCode(String corp_code, String store_id) throws Exception;

    String storeCodeConvertStoreId(String corp_code, String store_code) throws Exception;

//    String userIdConvertUserCode(String corp_code,String user_id)throws Exception;
//
//    String userCodeConvertUserId(String corp_code,String user_code)throws Exception;

    void insertUserOperation(String operation_corp_code, String operation_user_code, String function, String action, String corp_code, String code, String name, String remark) throws Exception;

    Corp selectByCorpcode(String corp_code) throws SQLException;

    String getStoreByScreen(JSONObject jsonObject, HttpServletRequest request) throws Exception;

    //功能使用分析
    String getStoreByScreen(JSONObject jsonObject, HttpServletRequest request, String corp_code) throws Exception;

    //任务列表详情
    String getStoresByScreen(JSONObject jsonObject, HttpServletRequest request, String corp_code) throws Exception;

    List<Brand> getBrandByUser(HttpServletRequest request, String corp_code_new) throws Exception;

    String getUserByScreen(JSONObject jsonObject, HttpServletRequest request, String corp_code) throws Exception;

    List<Brand> getBrandByUserForApp(String corp_code_app, String brand_code_app, String area_code_app, String store_code_app, String role_code_app) throws Exception;

}
