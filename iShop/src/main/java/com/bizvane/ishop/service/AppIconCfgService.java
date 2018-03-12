package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.AppIconCfg;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.AppIconCfg;

import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/15.
 */
public interface AppIconCfgService {

    PageInfo<AppIconCfg> selectAllIconCfg(int page_num, int page_size, String corp_code,String search_value) throws Exception;

    PageInfo<AppIconCfg> selectAllIconCfg(int page_num, int page_size, String corp_code,String search_value,String manager_corp) throws Exception;


    PageInfo<AppIconCfg> selectIconCfgAllScreen(int page_num, int page_size,String corp_code, Map<String, String> map) throws Exception;

    int delete(int id) throws Exception;


    String insert(AppIconCfg appIconCfg) throws Exception;

    String update(AppIconCfg appIconCfg) throws Exception;

    AppIconCfg getIconCfgById(int id) throws Exception;
    AppIconCfg getIconCfgByCorp(String corp_code,String isactive) throws Exception;

}
