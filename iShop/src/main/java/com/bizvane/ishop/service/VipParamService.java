package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.CorpParam;
import com.bizvane.ishop.entity.VipParam;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/9/7.
 */
public interface VipParamService {
    VipParam selectById(int id) throws Exception;

    List<VipParam> selectByParamName(String corp_code, String param_name,String isactive) throws Exception;

    List<VipParam> selectByParamDesc(String corp_code, String param_desc,String isactive) throws Exception;

    PageInfo<VipParam> selectAllParam(int page_number, int page_size,String corp_code, String search_value) throws Exception;

    String insert(VipParam vipParam) throws Exception;

    String update(VipParam vipParam) throws Exception;

    void updateShowOrder(int id,String show_order) throws Exception ;

    int delete(int id) throws Exception;

    PageInfo<VipParam> selectAllParamScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception ;

    List<VipParam> selectParamByCorp(String corp_code) throws Exception;

    List<VipParam> selectParamByType(String corp_code,String param_type) throws Exception;

}
