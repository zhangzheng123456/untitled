package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.UserAchvGoal;

import java.util.List;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public interface UserAchvGoalMapper {

    UserAchvGoal selectById(int id);

    int insert(UserAchvGoal userAchvGoal);

    int update(UserAchvGoal userAchvGoal);

    int delelteById(int id );


    List<UserAchvGoal> selectUserAchvGoalBySearch(String userAchvGoalId, String search_value);
}
