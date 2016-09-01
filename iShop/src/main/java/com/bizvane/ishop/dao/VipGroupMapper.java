package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.VipGroup;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by nanji on 2016/8/31.
 */
public interface VipGroupMapper {
    VipGroup selectVipGroupById(int id)throws SQLException;

    List<VipGroup> selectVipGroupByCorp(@Param("corp_code") String corp_code,@Param("id") String id) throws SQLException;


    List<VipGroup> selectVipGroups(@Param("corp_code") String corp_code) throws SQLException;

    List<VipGroup> selectAllVipGroup(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;


    int insertVipGroup(VipGroup vipGroup) throws SQLException;

    int updateVipGroup(VipGroup vipGroup) throws SQLException;

    int deleteVipGroupById(int id) throws SQLException;

    VipGroup selectByVipGroupName(@Param("corp_code") String corp_code, @Param("name") String name, @Param("isactive") String isactive) throws SQLException;


}
