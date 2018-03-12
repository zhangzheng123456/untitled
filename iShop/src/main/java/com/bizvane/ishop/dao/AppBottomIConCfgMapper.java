package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.AppBottomIConCfg;
import com.bizvane.ishop.entity.AppBottomIConCfg;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/2/21.
 */
public interface AppBottomIConCfgMapper {

    AppBottomIConCfg selAppBottomIConCfgById(int id) throws SQLException;

    List<AppBottomIConCfg> selectAllCfg(@Param("search_value") String search_value) throws SQLException;

    int insertAppBottomIConCfg(AppBottomIConCfg appBottomIConCfg) throws SQLException;

    int updateAppBottomIConCfg(AppBottomIConCfg appBottomIConCfg) throws SQLException;

    int delAppBottomIConCfgById(int id) throws SQLException;

    List<AppBottomIConCfg> selectAppBottomIConCfgScreen(Map<String, Object> params) throws SQLException;

    AppBottomIConCfg selAppBottomIConCfgByCode(@Param("isactive") String isactive, @Param("corp_code") String corp_code)throws SQLException;

    List<AppBottomIConCfg> getListByCorp(@Param("isactive") String isactive, @Param("corp_code") String corp_code)throws SQLException;
}
