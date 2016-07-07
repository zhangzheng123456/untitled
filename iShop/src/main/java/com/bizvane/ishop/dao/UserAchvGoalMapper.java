package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.UserAchvGoal;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public interface UserAchvGoalMapper {

    UserAchvGoal selectById(int id) throws SQLException;

    int insert(UserAchvGoal userAchvGoal) throws SQLException;

    int update(UserAchvGoal userAchvGoal) throws SQLException;

    int delete(@Param("id")int id) throws SQLException;

    List<UserAchvGoal> selectUserAchvGoalBySearch(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<UserAchvGoal> selectPartUserAchvGoalBySearch(Map<String, Object> params) throws SQLException;
//    UserAchvGoal userAchvGoalExist(@Param("user_code") String user_code) throws SQLException;
}
