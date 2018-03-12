package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.CorpParam;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/8/11.
 */
public interface CorpParamService {
    CorpParam selectById(int id) throws Exception;

    List<CorpParam> selectByParamId(String param_id) throws Exception;

    List<CorpParam> selectByCorpParam(String corp_code, String param_id,String isactive) throws Exception;

    PageInfo<CorpParam> selectAllParam(int page_number, int page_size, String search_value) throws Exception;

    String insert(String message, String user_code) throws Exception;

    String update(String message, String user_code) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<CorpParam> selectAllParamScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception ;

    CorpParam selectByParamName(String param_name,String isactive) throws Exception;

    List<CorpParam> selectParamByName(String corp_code,String param_name) throws Exception;

}
