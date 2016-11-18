package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Activity;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/15.
 */
public interface ActivityVipMapper {
    List<Activity> selectAllActivity(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    Activity selActivityById(int id)throws SQLException;

    int insertActivity(Activity activity) throws SQLException;

    int updateActivity(Activity activity) throws SQLException;

    int delActivityById(int id) throws SQLException;

    List<Activity> selectActivityScreen(Map<String, Object> params)throws SQLException;

    Activity getActivityForID(@Param("corp_code")String corp_code,@Param("activity_theme")String activity_theme,@Param("run_mode")String run_mode,@Param("created_date")String created_date)throws SQLException;


}
