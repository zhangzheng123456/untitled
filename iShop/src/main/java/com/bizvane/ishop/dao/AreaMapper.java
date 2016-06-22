package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Area;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AreaMapper {
    Area selectByAreaId(int id);

    Area selectCorpArea(@Param("corp_code") String corp_code, @Param("area_code") String area_code);

    List<Area> selectAllArea(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    List<Area> selectAreas(@Param("corp_code") String corp_code);

    int insertArea(Area area);

    int updateArea(Area area);

    int deleteByAreaId(int id);

    Area selectArea_Name(@Param("corp_code") String corp_code, @Param("area_name") String area_name);


    List<Area> getAreaByCorp(@Param("corp_code") String corp_code);

}