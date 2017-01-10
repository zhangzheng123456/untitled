package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipActivity;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by zhou on 2017/1/5.
 */
public interface VipActivityMapper {
    List<VipActivity> selectAllActivity(@Param("corp_code") String corp_code, @Param("user_code") String user_code, @Param("search_value") String search_value) throws SQLException;

    VipActivity selActivityById(int id)throws SQLException;

    int insertActivity(VipActivity activityVip) throws SQLException;

    int updateActivity(VipActivity activityVip) throws SQLException;

    int delActivityById(int id) throws SQLException;

    List<VipActivity> selectActivityScreen(Map<String, Object> params)throws SQLException;

    VipActivity selActivityByCode(@Param("activity_code") String activity_code)throws SQLException;


    int updActiveCodeByType(@Param("line_code") String line_code,@Param("line_value") String line_value,@Param("corp_code") String corp_code,@Param("activity_code") String activity_code)throws Exception;

    VipActivity selActivityByTheme( @Param("corp_code")String corp_code,@Param("activity_theme")String activity_theme)throws SQLException;


}
