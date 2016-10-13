package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.StoreGroup;
import com.bizvane.ishop.entity.Store;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/6/4.
 */
public interface StoreGroupService {
    StoreGroup getAreaById(int id) throws Exception;

    StoreGroup getAreaByCode(String corp_code, String area_code, String isactive) throws Exception;

    PageInfo<StoreGroup> getAllAreaByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    StoreGroup getAreaByName(String corp_code, String area_name, String isactive) throws Exception;

    String insertExecl(StoreGroup storeGroup) throws Exception;

    PageInfo<StoreGroup> getAllAreaScreen(int page_number, int page_size, String corp_code, String area_codes, Map<String, String> map) throws Exception;

    PageInfo<StoreGroup> selectByAreaCode(int page_number, int page_size, String corp_code, String area_codes, String search_value) throws Exception;

    PageInfo<StoreGroup> selAreaByCorpCode(int page_number, int page_size, String corp_code, String area_codes, String store_code, String search_value)throws Exception;

    List<StoreGroup> selAreaByCorpCode(String corp_code, String area_codes, String store_code) throws Exception;

    PageInfo<Store> getAllStoresByCorpCode( int page_number, int page_size, String corp_code, String search_value,String area_code) throws Exception;

    List<StoreGroup> selectArea(String corp_code, String area_codes) throws SQLException;
}
