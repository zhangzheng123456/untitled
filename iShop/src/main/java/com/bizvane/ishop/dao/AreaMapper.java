package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Area;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface AreaMapper {
    Area selectByAreaId(int id) throws SQLException;

    List<Area> selectAllArea(@Param("corp_code") String corp_code, @Param("search_value") String search_value,@Param("manager_corp_arr")String[] manager_corp_arr) throws SQLException;

    int insertArea(Area area) throws SQLException;

    int updateArea(Area area) throws SQLException;

    int deleteByAreaId(int id) throws SQLException;

    //根据企业code和用户登录进来时的区域Code查询区域
    Area selectAreaByCode(@Param("corp_code") String corp_code, @Param("area_code") String area_code, @Param("isactive") String isactive) throws SQLException;

    Area selectAreaByName(@Param("corp_code") String corp_code, @Param("area_name") String area_name, @Param("isactive") String isactive) throws SQLException;

    List<Area> selectAllAreaScreen(Map<String, Object> params) throws SQLException;

    List<Area> selectByAreaCodeSearch(Map<String, Object> params) throws SQLException;

    List<Area> selAreaByCorpCode(Map<String, Object> params) throws SQLException;

    List<Area>  selectArea(Map<String, Object> params) throws SQLException;

    List<Area> selectAllAreaTwo(@Param("corp_code") String corp_code, @Param("search_value") String search_value,@Param("manager_corp_arr")String[] manager_corp_arr) throws SQLException;


}