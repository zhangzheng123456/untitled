package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.AppHelp;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/2/21.
 */
public interface AppHelpMapper {

    AppHelp selAppHelpById(int id) throws SQLException;

    List<AppHelp> selectAllHelp( @Param("search_value") String search_value) throws SQLException;

    int insertAppHelp(AppHelp appHelp) throws SQLException;

    int updateAppHelp(AppHelp appHelp) throws SQLException;

    int delAppHelpById(int id) throws SQLException;

    List<AppHelp> selectAppHelpScreen(Map<String, Object> params) throws SQLException;


    AppHelp selAppHelpByName(@Param("app_help_name")String vip_card_type_name,@Param("isactive") String isactive)throws SQLException;

    List<AppHelp>  selectHelps(@Param("isactive") String isactive)throws SQLException;

    String selectMaxOrder() throws SQLException;


}
