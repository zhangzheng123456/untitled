package com.bizvane.ishop.controller;

import com.bizvane.ishop.dao.UserAchvGoalMapper;
import com.bizvane.ishop.entity.UserAchvGoal;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public class UserAchvGoalControl {

    @Autowired
    private UserAchvGoalMapper userAchvGoalMapper=null;

    public UserAchvGoal getUserAchvGoal(int id){
        return this.userAchvGoalMapper.selectById(id);
    }

    public int updateUserAchvGoal(UserAchvGoal userAchvGoal){
         return this.updateUserAchvGoal(userAchvGoal);
    }

    public int deleteUserAchvGoalById(int id){
        return this.userAchvGoalMapper.delelteById(id);
    }

    public int insertUserAchvGoal(UserAchvGoal userAchvGoal){
        return this.userAchvGoalMapper.insert(userAchvGoal);
    }
}
