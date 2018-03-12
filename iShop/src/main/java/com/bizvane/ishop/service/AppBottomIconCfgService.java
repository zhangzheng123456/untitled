package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.AppBottomIConCfg;
import com.bizvane.ishop.entity.AppBottomIConCfg;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/2/21.
 */
public interface AppBottomIconCfgService {

    AppBottomIConCfg getAppBottomIConCfgById(int id) throws Exception;

    PageInfo<AppBottomIConCfg> getAppBottomIConCfgByPage(int page_number, int page_size, String search_value) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<AppBottomIConCfg> getAppBottomIConCfgScreen(int page_number, int page_size, Map<String, String> map) throws Exception;

    AppBottomIConCfg getAppBottomIConCfgByCorp(String isactive, String corp_code) throws Exception;

    List<AppBottomIConCfg> getListByCorp(String isactive, String corp_code) throws Exception;


}
