package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.VipLabelsRel;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/2/16.
 */
public interface VipLabelsRelService {

    PageInfo<VipLabelsRel> selectAllLabel(int page_number, int page_size, String corp_code, String search_value) throws  Exception;

    PageInfo<VipLabelsRel> selectAllScreen(int page_number, int page_size, String corp_code, Map<String,String> map) throws Exception;

    int delActivityVipLabelById(int id)throws Exception;

    VipLabelsRel selectVipLabelsRelById(int id)throws Exception;

    //按权限筛选
    PageInfo<VipLabelsRel> selectRoleAllScreen(int page_number,int page_size,String corp_code,String user_code,String store_code,String role_code, Map<String,String> map)throws Exception;

    //按权限查找
    PageInfo<VipLabelsRel> selectRoleAllLabel(int page_number, int page_size, String corp_code,String user_code,String store_code, String role_code,String search_value) throws  Exception;

    List<VipLabelsRel> switchDbCurSor(ArrayList list) throws Exception;

    BasicDBObject getScreenObject(JSONObject jsonObject) throws Exception;


}
