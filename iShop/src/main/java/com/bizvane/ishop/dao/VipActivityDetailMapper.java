package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipActivityDetail;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/1/5.
 */
public interface VipActivityDetailMapper {
    List<VipActivityDetail> selectAllActivityDetail(@Param("corp_code") String corp_code, @Param("user_code") String user_code, @Param("search_value") String search_value) throws SQLException;

    VipActivityDetail selActivityDetailById(int id)throws SQLException;

    int insertActivityDetail(VipActivityDetail activityDetail) throws SQLException;

    int updateActivityDetail(VipActivityDetail activityDetail) throws SQLException;

    int delActivityDetailById(@Param("activity_code")String  activity_code) throws SQLException;

    List<VipActivityDetail> selectActivityDetailScreen(Map<String, Object> params)throws SQLException;

    VipActivityDetail selActivityDetailByCode(@Param("activity_code") String activity_code)throws SQLException;


}


