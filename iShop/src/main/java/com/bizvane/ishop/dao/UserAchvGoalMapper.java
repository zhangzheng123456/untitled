package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.UserAchvGoal;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public interface UserAchvGoalMapper {

    UserAchvGoal selectById(int id)throws SQLException;

    int insert(UserAchvGoal userAchvGoal)throws SQLException;

    int update(UserAchvGoal userAchvGoal)throws SQLException;

    int delelteByUser_code(String user_code)throws SQLException;


    List<UserAchvGoal> selectUserAchvGoalBySearch(String userAchvGoalId, String search_value)throws SQLException;


    int userAchvGoalExist(String user_code)throws SQLException;
}
