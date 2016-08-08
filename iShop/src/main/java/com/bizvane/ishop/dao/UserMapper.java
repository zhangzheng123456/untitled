package com.bizvane.ishop.dao;


import com.bizvane.ishop.entity.User;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UserMapper {
    int deleteByUserId(int id) throws SQLException;

    int insertUser(User record) throws SQLException;

    int updateByUserId(User record) throws SQLException;

    List<User> selectByPhone(String phone) throws SQLException;

    List<User> selectLogin(String phone, String password) throws SQLException;

    List<User> userEmailExist(@Param("email") String email) throws SQLException;

    List<User> selectUserCode(String user_code, String corp_code) throws SQLException;

    List<User> selectGroupUser(@Param("corp_code") String corp_code, @Param("group_code") String group_code) throws SQLException;

    List<User> selectAllUser(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<User> selectPartUser(Map<String, Object> params) throws SQLException;


    List<User> selUserByStoreCode(Map<String, Object> params) throws SQLException;

    User selectUserById(int user_id) throws SQLException;

    User selectById(int user_id) throws SQLException;

    List<User> selectStoreUser(@Param("corp_code") String corp_code, @Param("store_code") String store_code,@Param("area_code") String area_code, @Param("role_code") String role_code) throws SQLException;

//    User selectUserName(@Param("user_name") String user_name, @Param("corp_oode") String corp_code);
//
//    User selectPhone(@Param("phone") String phone, @Param("corp_code") String corp_code);

    int selectCount(@Param("created_date") String created_date) throws SQLException;

    List<User> selectPartScreen(Map<String, Object> params) throws SQLException;

    List<User> selectAllUserScreen(Map<String, Object> params) throws SQLException;
}