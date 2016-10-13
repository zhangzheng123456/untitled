package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.AreaNew;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by PC on 2016/10/13.
 */
public interface AreaNewMapper {
    AreaNew selectByAreaId(int id) throws SQLException;

    List<AreaNew> selectAllArea(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    int insertArea(AreaNew area) throws SQLException;

    int updateArea(AreaNew area) throws SQLException;

    int deleteByAreaId(int id) throws SQLException;

    //根据企业code和用户登录进来时的区域Code查询区域
    AreaNew selectAreaByCode(@Param("corp_code") String corp_code, @Param("area_code") String area_code, @Param("isactive") String isactive) throws SQLException;

    AreaNew selectAreaByName(@Param("corp_code") String corp_code, @Param("area_name") String area_name, @Param("isactive") String isactive) throws SQLException;

    List<AreaNew> selectAllAreaScreen(Map<String, Object> params) throws SQLException;

    List<AreaNew> selectByAreaCodeSearch(Map<String, Object> params) throws SQLException;

    List<AreaNew> selAreaByCorpCode(Map<String, Object> params) throws SQLException;

    List<AreaNew>  selectArea(Map<String, Object> params) throws SQLException;
}
