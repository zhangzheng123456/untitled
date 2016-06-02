package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.UserAchvGoal;
/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public interface UserAchvGoalService {

    UserAchvGoal getUserAchvGoalById(int id);

    int update(UserAchvGoal userAchvGoal);

    int delete(int id);

    int insert(UserAchvGoal userAchvGoal);

}
