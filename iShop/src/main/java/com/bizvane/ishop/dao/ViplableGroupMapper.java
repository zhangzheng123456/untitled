package com.bizvane.ishop.dao;


import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.ViplableGroup;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/8/31.
 */
public interface ViplableGroupMapper {
    //查询全部
    List<ViplableGroup> selectViplabGroup(@Param("corp_code")String corp_code, @Param("search_value")String search_value)throws Exception;
    //筛选
    List<ViplableGroup> selectViplabGroupScreen(Map<String,Object> params)throws Exception;
    //删除
    int delViplabGroupById(int id)throws Exception;

    int addViplableGroup(ViplableGroup viplableGroup) throws SQLException;

    int updViplableGroupById(ViplableGroup viplableGroup) throws SQLException;

    ViplableGroup selectViplableGroupById(@Param("id") int id) throws SQLException;
}
