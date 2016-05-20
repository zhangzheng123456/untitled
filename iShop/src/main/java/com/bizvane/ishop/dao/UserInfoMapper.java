package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.UserInfo;

public interface UserInfoMapper {
    int deleteByPrimaryKey(int id);

    int insert(UserInfo record);

    UserInfo selectByPrimaryKey(int id);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectByPMP(String phone,String password);
}