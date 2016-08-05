package com.bizvane.ishop.dao;


import com.bizvane.ishop.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    int deleteByUserId(int id);

    int insertUser(User record);

    int updateByUserId(User record);

    List<User> selectByPhone(String phone);

    List<User> selectLogin(String phone, String password);

    List<User> userEmailExist(@Param("email") String email);

    List<User> selectUserCode(String user_code, String corp_code);

    List<User> selectGroupUser(@Param("corp_code") String corp_code, @Param("group_code") String group_code);

    List<User> selectAllUser(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    List<User> selectPartUser(Map<String, Object> params);


    List<User> selUserByStoreCode(Map<String, Object> params);

    User selectUserById(int user_id);

    User selectById(int user_id);

    List<User> selectStoreUser(@Param("corp_code") String corp_code, @Param("store_code") String store_code,@Param("area_code") String area_code, @Param("role_code") String role_code);

//    User selectUserName(@Param("user_name") String user_name, @Param("corp_oode") String corp_code);
//
//    User selectPhone(@Param("phone") String phone, @Param("corp_code") String corp_code);

    int selectCount(@Param("created_date") String created_date);

    List<User> selectPartScreen(Map<String, Object> params);


    List<User> selectAllUserScreen(Map<String, Object> params);
}