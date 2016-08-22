package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.ParamConfigure;

import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yan on 2016/8/10.
 */
public interface ParamConfigureService {

    ParamConfigure getParamById(int id) throws SQLException;

    ParamConfigure getParamByKey(String param_key) throws Exception;
    ParamConfigure getParamByName(String param_name) throws Exception;
    PageInfo<ParamConfigure> getAllParamByPage(int page_number, int page_size, String search_value) throws Exception;
    List<ParamConfigure> getAllParams() throws Exception;

    String insert(String message) throws Exception;

    String update(String message) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<ParamConfigure> selectByParamSearch(int page_number, int page_size, String search_value) throws Exception;

    PageInfo<ParamConfigure> selectParamScreen(int page_number, int page_size , Map<String, String> map) throws Exception;

}
