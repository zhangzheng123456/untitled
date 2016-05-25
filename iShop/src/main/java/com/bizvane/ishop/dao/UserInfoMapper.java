package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoMapper {
    int deleteByUserId(int id);

    int insertUser(UserInfo record);

    UserInfo selectByUserId(int id);

    int updateByUserId(UserInfo record);

    UserInfo selectLogin(String phone,String password);

    UserInfo selectByPhone(String phone);

    List<UserInfo> selectAllUser(@Param("corp_code")String corp_code,@Param("search_value") String search_value);
}