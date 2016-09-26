package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.User;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/7/14.
 */
public interface BaseService {

    PageInfo<HashMap<String,Object>> queryMetaList(int page_number, int page_size,Map<String, Object> params) throws SQLException;

    String storeIdConvertStoreCode(String corp_code,String store_id)throws Exception;

    String storeCodeConvertStoreId(String corp_code,String store_code)throws Exception;

    String userIdConvertUserCode(String corp_code,String user_id)throws Exception;

    String userCodeConvertUserId(String corp_code,String user_code)throws Exception;
}
