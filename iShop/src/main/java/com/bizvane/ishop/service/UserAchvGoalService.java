package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.UserAchvGoal;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public interface UserAchvGoalService {


    /**
     * 获取用户业绩目标，通过ID
     * @param id ： 用户业绩目标ID
     * @return
     * @throws SQLException
     */
    UserAchvGoal getUserAchvGoalById(int id )throws SQLException;

    /**
     * 更新用户业绩目标ID
     * @param userAchvGoal
     * @return
     * @throws SQLException
     */
    int updateUserAchvGoal(UserAchvGoal userAchvGoal)throws SQLException;

    /**
     * 删除用户业绩目标ID
     * @param user_code
     * @return
     * @throws SQLException
     */
    int deleteUserAchvGoalById(String user_code)throws SQLException;

    /**
     * 插入用户业绩目标ID
     * @param userAchvGoal
     * @return
     * @throws SQLException
     */
    int insert(UserAchvGoal userAchvGoal)throws SQLException;

    /**
     *
     * @param page_number
     * @param page_size
     * @param s
     * @param s1
     * @return
     * @throws SQLException
     */
    PageInfo<UserAchvGoal> selectBySearch(int page_number, int page_size, String corp_code, String search_value)throws SQLException;


    String userAchvGoalExist(String user_code)throws SQLException;
}
