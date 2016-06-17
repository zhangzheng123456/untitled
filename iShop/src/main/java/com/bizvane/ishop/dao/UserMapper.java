package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByUserId(int id);

    int insertUser(User record);

    User selectByPhone(String phone);

    int updateByUserId(User record);

    User selectLogin(String phone, String password);

    User selectUserCode(String user_code, String corp_code);

    List<User> selectGroupUser(@Param("corp_code") String corp_code, @Param("group_code") String group_code);

    List<User> selectAllUser(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    User selectUserById(int user_id);

    List<User> selectStoreUser(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    User selectUserName(@Param("user_name") String user_name, @Param("corp_oode") String corp_code);

    User selectPhone(@Param("phone") String phone, @Param("corp_code") String corp_code);

    User userEmailExist(@Param("email") String email, @Param("corp_code") String corp_code);


}