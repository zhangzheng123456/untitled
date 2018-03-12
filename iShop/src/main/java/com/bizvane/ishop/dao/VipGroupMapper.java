package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipGroup;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/8/31.
 */
public interface VipGroupMapper {
    VipGroup selectVipGroupById(int id) throws SQLException;

    List<VipGroup> selectCorpVipGroups(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<VipGroup> selectAllVipGroup(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    int insertVipGroup(VipGroup vipGroup) throws SQLException;

    int updateVipGroup(VipGroup vipGroup) throws SQLException;

    int deleteVipGroupById(int id) throws SQLException;

    List<VipGroup> selectByVipGroupName(@Param("corp_code") String corp_code, @Param("vip_group_name") String vip_group_name, @Param("isactive") String isactive) throws SQLException;

    VipGroup selectByVipGroupCode(@Param("corp_code") String corp_code, @Param("vip_group_code") String vip_group_code, @Param("isactive") String isactive) throws SQLException;

    List<VipGroup> selectAllVipGroupScreen(Map<String, Object> params) throws SQLException;

}
