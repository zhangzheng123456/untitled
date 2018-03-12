package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.RelAppHelp;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/2/23.
 */
public interface RelAppHelpMapper {
    RelAppHelp selectById(int id) throws SQLException;

    List<RelAppHelp> selectAllRelAppHelp( @Param("search_value") String search_value) throws SQLException;

    int insertRelAppHelp(RelAppHelp relAppHelp) throws SQLException;

    int updateRelAppHelp(RelAppHelp relAppHelp) throws SQLException;

    int deleteById(int id) throws SQLException;

    List<RelAppHelp> selectRelAppHelpScreen(Map<String, Object> params) throws SQLException;

   // List<RelAppHelp> selectByAppHelp(@Param("corp_code") String corp_code,@Param("app_help_code") String app_help_code,@Param("app_help_title") String app_help_title,@Param("isactive") String isactive)throws SQLException;

    RelAppHelp selectByAppHelp(@Param("app_help_code") String app_help_code,@Param("app_help_title") String app_help_title,@Param("isactive") String isactive)throws SQLException;

    List<RelAppHelp>  selectRelHelps(@Param("isactive") String isactive)throws SQLException;

    List<RelAppHelp> selectByAppHelpCode (@Param("app_help_code") String app_help_code)throws SQLException;


}
