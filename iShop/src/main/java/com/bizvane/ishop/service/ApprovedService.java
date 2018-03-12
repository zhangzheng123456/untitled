package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.Approved;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/4/17.
 */
public interface ApprovedService {
    Approved selectById(int id) throws Exception;

    Approved selectByApprovedName(String approved_name,String corp_code)throws Exception;

    PageInfo<Approved> selectAll(int page_num,int page_size,String corp_code, String search_value) throws Exception;

    public JSONObject approvalDetails(String corp_code,int approved_id,int page_size,int page_number,String created_date) throws Exception;

    public  JSONObject approvedList(JSONObject jsonObject ,String corp_code)throws Exception;

    public  JSONObject screenApprovedList(String message,String role_code,String corp_code) throws Exception;

    public  void add(List<Approved> list);

    int deleteById(int id) throws Exception;

    int insertApproved(Approved approved) throws Exception;

    int updateApproved(Approved approved)throws Exception;

    PageInfo<Approved> selectAllScreen(int page_num,int page_size,String corp_code,Map<String, Object> params) throws  Exception;

    public PageInfo<Approved> switchApproved(PageInfo<Approved> list) throws  Exception;

    public List<Approved> selectAll2(String corp_code, String search_value) throws Exception;
}
