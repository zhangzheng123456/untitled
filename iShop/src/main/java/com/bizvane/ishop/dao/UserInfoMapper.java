package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoMapper {
    int deleteByUserId(int id);

    int insertUser(UserInfo record);

    UserInfo selectByUserId(@Param("user_id") int user_id,@Param("phone") String phone);

    int updateByUserId(UserInfo record);

    UserInfo selectLogin(String phone,String password);

    UserInfo selectUserCode(String user_code,String corp_code);

    List<UserInfo> selectAllUser(@Param("corp_code")String corp_code,@Param("search_value") String search_value);
}