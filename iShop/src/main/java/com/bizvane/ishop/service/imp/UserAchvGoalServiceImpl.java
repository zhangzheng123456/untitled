package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.controller.UserAchvGoalControl;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.bizvane.ishop.service.UserAchvGoalService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public class UserAchvGoalServiceImpl implements UserAchvGoalService{

    @Autowired
    private UserAchvGoalControl userAchvGoalControl;


    @Override
    public UserAchvGoal getUserAchvGoalById(int id) {
        return this.userAchvGoalControl.getUserAchvGoal(id);
    }

    @Override
    public int update(UserAchvGoal userAchvGoal) {
        return this.userAchvGoalControl.updateUserAchvGoal(userAchvGoal);
    }

    @Override
    public int delete(int id) {
        return this.userAchvGoalControl.deleteUserAchvGoalById(id);
    }

    @Override
    public int insert(UserAchvGoal userAchvGoal) {
        return this.insert(userAchvGoal);
    }
}
