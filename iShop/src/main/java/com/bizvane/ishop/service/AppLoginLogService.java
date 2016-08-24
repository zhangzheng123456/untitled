package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.AppLoginLog;
import com.bizvane.ishop.entity.Appversion;
import com.github.pagehelper.PageInfo;

import java.util.Map;

/**
 * Created by yin on 2016/8/24.
 */
public interface AppLoginLogService {
    PageInfo<AppLoginLog> selectAllAppLoginLog(int page_number, int page_size,String corp_code,String search_value);

    PageInfo<AppLoginLog> selectAllScreen(int page_number, int page_size, String corp_code,Map<String,String> map) throws Exception;

    int delAppLoginlogById(int id);
}
