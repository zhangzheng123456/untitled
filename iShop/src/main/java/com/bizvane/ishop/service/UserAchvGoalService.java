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
     * 获取用户业绩目标的分页信息
     * @param page_number ： 起始分页
     * @param page_size ： 分页大小
     * @param corp_code ： 公司编号
     * @param search_value ： 查询信息
     * @return
     * @throws SQLException
     */
    PageInfo<UserAchvGoal> selectBySearch(int page_number, int page_size, String corp_code, String search_value)throws SQLException;

    /**
     * 判断用户业绩业绩是否存在
     * @param user_code ： 用户编号
     * @return
     * @throws SQLException
     */
    String userAchvGoalExist(String user_code)throws SQLException;
}
