package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.StoreGroup;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface StoreGroupMapper {
    StoreGroup selectByAreaId(int id) throws SQLException;

    List<StoreGroup> selectAllArea(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    int insertArea(StoreGroup storeGroup) throws SQLException;

    int updateArea(StoreGroup storeGroup) throws SQLException;

    int deleteByAreaId(int id) throws SQLException;

    //根据企业code和用户登录进来时的区域Code查询区域
    StoreGroup selectAreaByCode(@Param("corp_code") String corp_code, @Param("store_group_code") String store_group_code, @Param("isactive") String isactive) throws SQLException;

    StoreGroup selectAreaByName(@Param("corp_code") String corp_code, @Param("store_group_name") String store_group_name, @Param("isactive") String isactive) throws SQLException;

    List<StoreGroup> selectAllAreaScreen(Map<String, Object> params) throws SQLException;

    List<StoreGroup> selectByAreaCodeSearch(Map<String, Object> params) throws SQLException;

    List<StoreGroup> selAreaByCorpCode(Map<String, Object> params) throws SQLException;

    List<StoreGroup>  selectArea(Map<String, Object> params) throws SQLException;
}