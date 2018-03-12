package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.ExamineConfigure;
import com.github.pagehelper.PageInfo;

import java.util.Map;

public interface ExamineConfigureService {

    ExamineConfigure selectById(int id) throws Exception;

    PageInfo<ExamineConfigure> selectAll(String corp_code,String search_value,int page_num,int page_size) throws Exception;

    PageInfo<ExamineConfigure> selectAllScreen(String corp_code, Map<String,Object> map, int page_num, int page_size) throws Exception;

    String deleteById( int id) throws Exception;

    ExamineConfigure selectByName(String corp_code,String function_bill_name) throws Exception;

    int insertExamine(ExamineConfigure examineConfigure) throws Exception;

    String updateExamine(ExamineConfigure examineConfigure) throws Exception;
}
