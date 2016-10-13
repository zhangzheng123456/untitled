package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.AreaNew;
import com.bizvane.ishop.entity.Store;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by PC on 2016/10/13.
 */
public interface AreaNewService {
    AreaNew getAreaById(int id) throws Exception;

    AreaNew getAreaByCode(String corp_code, String area_code, String isactive) throws Exception;

    PageInfo<AreaNew> getAllAreaByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    AreaNew getAreaByName(String corp_code, String area_name, String isactive) throws Exception;

    String insertExecl(AreaNew area) throws Exception;

    PageInfo<AreaNew> getAllAreaScreen(int page_number, int page_size, String corp_code, String area_codes, Map<String, String> map) throws Exception;

    PageInfo<AreaNew> selectByAreaCode(int page_number, int page_size, String corp_code, String area_codes, String search_value) throws Exception;

    PageInfo<AreaNew> selAreaByCorpCode(int page_number, int page_size, String corp_code, String area_codes,String store_code, String search_value)throws Exception;

    List<AreaNew> selAreaByCorpCode(String corp_code, String area_codes, String store_code) throws Exception;

   PageInfo<Store> getAllStoresByCorpCode(int page_number, int page_size, String corp_code, String search_value, String area_code) throws Exception;

    List<AreaNew> selectArea(String corp_code, String area_codes) throws SQLException;
}
