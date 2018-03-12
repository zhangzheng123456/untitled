package com.bizvane.ishop.dao;


import com.bizvane.ishop.entity.Appversion;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/21.
 */
public interface AppversionMapper {

    List<Appversion> selectAllAppversion(@Param("search_value") String search_value) throws SQLException;
    List<Appversion> selectAllAppversionByPlatform(@Param("search_value") String search_value) throws SQLException;

    List<Appversion> selectAllScreen(Map<String, Object> params) throws SQLException;

    int addAppversion(Appversion appversion) throws SQLException;

    int updAppversionById(Appversion appversion) throws SQLException;

    int delAppversionById(int id) throws SQLException;

    Appversion selAppversionById(@Param("id") int id) throws SQLException;

    Appversion selAppversionForId(@Param("corp_code") String corp_code, @Param("version_id") String version_id, @Param("platform") String platform) throws SQLException;

    List<Appversion> selLatestVersion() throws SQLException;
}
