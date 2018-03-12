package com.bizvane.ishop.dao;

import IceInternal.Ex;
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

    int delete(@Param("id") int id) throws SQLException;

    List<UserAchvGoal> selectUserAchvGoalBySearch(@Param("corp_code") String corp_code, @Param("search_value") String search_value,@Param("manager_corp_arr")String[] manager_corp_arr) throws SQLException;

    List<UserAchvGoal> selectPartUserAchvGoalBySearch(Map<String, Object> params) throws SQLException;

    List<UserAchvGoal> selectUserAchvCount(@Param("corp_code") String corp_code, @Param("user_code") String user_code) throws SQLException;
    UserAchvGoal getUserAchvForId(@Param("corp_code") String corp_code, @Param("user_code") String user_code,@Param("store_code") String store_code,@Param("target_time") String target_time,@Param("isactive") String isactive)throws Exception;

    int deleteStoreUserAchv(@Param("corp_code") String corp_code, @Param("store_code") String store_code, @Param("user_code") String user_code) throws SQLException;

    List<UserAchvGoal> selectAllUserAchvScreen(Map<String, Object> params) throws SQLException;

    int selectUserAchvCountType(@Param("corp_code") String corp_code, @Param("user_code") String user_code, @Param("target_type") String target_type, @Param("target_time") String target_time,@Param("isactive")String isactive,@Param("store_code")String store_code) throws SQLException;
}
