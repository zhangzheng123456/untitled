package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.ViplableGroup;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by yin on 2016/8/31.
 */
public interface ViplableGroupService {
    PageInfo<ViplableGroup> selectViplabGroup(int page_number, int page_size, String corp_code, String search_value)throws Exception;

    PageInfo<ViplableGroup> selectViplabGroupScreen(int page_number, int page_size, String corp_code,Map<String,String> map) throws Exception;

    int delViplabGroupById(int id)throws Exception;

    int addViplableGroup(ViplableGroup viplableGroup) throws SQLException;

    int updViplableGroupById(ViplableGroup viplableGroup) throws SQLException;

    ViplableGroup selectViplableGroupById(int id) throws SQLException;

}
