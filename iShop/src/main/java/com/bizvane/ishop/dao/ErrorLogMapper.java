package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ErrorLog;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/8/24.
 */
public interface ErrorLogMapper {

    ErrorLog selectByLogId(int id) throws SQLException;

    List<ErrorLog> selectAllLog( @Param("search_value") String search_value) throws SQLException;
    List<ErrorLog> selectAllLogScreen(Map<String, Object> params) throws SQLException;


    int deleteByLogId(int id) throws SQLException;
}
