package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.UserAchvGoal;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public interface UserAchvGoalService {


    /**
     * 获取用户业绩目标，通过ID
     *
     * @param id ： 用户业绩目标ID
     * @return
     * @throws SQLException
     */
    UserAchvGoal getUserAchvGoalById(int id) throws Exception;

    /**
     * 更新用户业绩目标ID
     *
     * @param userAchvGoal
     * @return
     * @throws SQLException
     */
    String updateUserAchvGoal(UserAchvGoal userAchvGoal) throws Exception;

    /**
     * 删除用户业绩目标ID
     *
     * @param id
     * @return
     * @throws SQLException
     */
    int deleteUserAchvGoalById(String id) throws Exception;

    /**
     * 插入用户业绩目标ID
     *
     * @param userAchvGoal
     * @return
     * @throws SQLException
     */
    String insert(UserAchvGoal userAchvGoal) throws Exception;

    /**
     * 获取用户业绩目标的分页信息
     *
     * @param page_number  ： 起始分页
     * @param page_size    ： 分页大小
     * @param corp_code    ： 公司编号
     * @param search_value ： 查询信息
     * @return
     * @throws SQLException
     */
    PageInfo<UserAchvGoal> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    PageInfo<UserAchvGoal> selectBySearch(int page_number, int page_size, String corp_code, String search_value,String manager_corp) throws Exception;


    PageInfo<UserAchvGoal> selectBySearchPart(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code,String area_store_code, String role_code) throws Exception;

    int checkUserAchvGoal(UserAchvGoal userAchvGoal)throws Exception;
    /**
     * 显示当前用户业绩目标
     *
     * @param user_code ： 用户编号
     * @param corp_code ： 企业编号
     * @return
     * @throws SQLException
     */
    UserAchvGoal getUserAchvForId(String corp_code, String user_code,String store_code,String target_type,String target_time )throws  Exception;

    List<UserAchvGoal> userAchvGoalExist(String corp_code, String user_code) throws Exception;

    PageInfo<UserAchvGoal> getAllUserAchScreen(int page_number, int page_size, String corp_code, String area_code, String store_code, String role_code, Map<String, String> map,String area_store_code) throws Exception;

    PageInfo<UserAchvGoal> getAllUserAchScreen(int page_number, int page_size, String corp_code, String area_code, String store_code, String role_code, Map<String, String> map,String area_store_code,String manager_corp) throws Exception;

    int userAchvGoalIfExist(String corp_code, String user_code,String target_type, String target_time,String isactive,String store_code) throws Exception;

}
