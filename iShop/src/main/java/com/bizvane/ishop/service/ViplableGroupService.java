package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.ViplableGroup;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/8/31.
 */
public interface ViplableGroupService {
    PageInfo<ViplableGroup> selectViplabGroup(int page_number, int page_size, String corp_code, String search_value)throws Exception;

    PageInfo<ViplableGroup> selectViplabGroupScreen(int page_number, int page_size, String corp_code,Map<String,String> map) throws Exception;

    int delViplabGroupById(int id)throws Exception;

    String  addViplableGroup(ViplableGroup viplableGroup) throws SQLException;

    String updViplableGroupById(ViplableGroup viplableGroup) throws SQLException;

    ViplableGroup selectViplableGroupById(int id) throws SQLException;

    List<ViplableGroup> checkCodeOnly(String corp_code,String label_group_code,String isactive) throws SQLException;

    List<ViplableGroup> checkNameOnly(String corp_code,String label_group_name,String isactive) throws SQLException;

    List<ViplableGroup> selectViplabGroupList(String corp_code) throws SQLException;

}
