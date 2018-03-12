package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.AppIconCfg;
import com.bizvane.ishop.entity.AppIconCfg;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by zhou on 2017/1/5.
 */
public interface AppIconCfgMapper {
    List<AppIconCfg> selectAllIconCfg(@Param("corp_code") String corp_code,  @Param("search_value") String search_value,@Param("manager_corp_arr") String[] manager_corp_arr) throws SQLException;

    AppIconCfg selActivityById(int id)throws SQLException;

    AppIconCfg selActivityByCorp(@Param("corp_code") String corp_code,@Param("isactive") String isactive)throws SQLException;

    int insertIconCfg(AppIconCfg activityVip) throws SQLException;

    int updateIconCfg(AppIconCfg activityVip) throws SQLException;

    int delIconCfgById(int id) throws SQLException;

    List<AppIconCfg> selectIconCfgScreen(Map<String, Object> params)throws SQLException;


}
