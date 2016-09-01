package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VipGroup;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Created by nanji on 2016/8/31.
 */
public interface VipGroupService {

    VipGroup getVipGroupById(int id) throws Exception;


    PageInfo<VipGroup> getAllVipGroupByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    List<VipGroup> getAllVipGroup(String corp_code) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    List<VipGroup> selectVipGroupByCorp(String corp_code,String id) throws Exception ;

    VipGroup   getVipGroupByCode(String corp_code, String code,String isactive) throws Exception;

    VipGroup   getVipGroupByName(String corp_code, String name,String isactive) throws Exception;


}
