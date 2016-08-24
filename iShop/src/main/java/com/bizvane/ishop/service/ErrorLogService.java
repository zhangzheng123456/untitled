package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.ErrorLog;
import com.github.pagehelper.PageInfo;

import java.util.Map;

/**
 * Created by nanji on 2016/8/24.
 */
public interface ErrorLogService   {
    ErrorLog getLogById(int id) throws Exception;

    PageInfo<ErrorLog> getAllLog(int page_number, int page_size, String search_value) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<ErrorLog> selectAllLogScreen(int page_number, int page_size, Map<String, String> map) throws Exception ;

    }
