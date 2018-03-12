package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.AppIcon;

import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by zhou on 2017/1/5.
 */
public interface AppIconMapper {
    List<AppIcon> selectAllIconNames(@Param("search_value") String search_value) throws SQLException;

    int updateAppIcon(AppIcon appIcon) throws SQLException;

    int insertAppIcon(AppIcon appIcon) throws SQLException;

    String selectMaxOrder() throws SQLException;

    AppIcon selAppIconById(int id);

    AppIcon selAppIconByName(@Param("isactive") String isactive, @Param("icon_name") String icon_name);
}
