package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.UserAchvGoal;

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



}
