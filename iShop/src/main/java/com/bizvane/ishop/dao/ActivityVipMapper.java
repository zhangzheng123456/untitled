package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ActivityVip;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/15.
 */
public interface ActivityVipMapper {
    List<ActivityVip> selectAllActivity(@Param("corp_code") String corp_code, @Param("user_code") String user_code,@Param("search_value") String search_value) throws SQLException;

    ActivityVip selActivityById(int id)throws SQLException;

    int insertActivity(ActivityVip activityVip) throws SQLException;

    int updateActivity(ActivityVip activityVip) throws SQLException;

    int delActivityById(int id) throws SQLException;

    List<ActivityVip> selectActivityScreen(Map<String, Object> params)throws SQLException;

    ActivityVip selActivityByCode(@Param("activity_vip_code")String activity_vip_code)throws SQLException;
}
