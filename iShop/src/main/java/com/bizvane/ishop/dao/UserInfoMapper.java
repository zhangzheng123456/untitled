package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoMapper {
    int deleteByPrimaryKey(int id);

    int insert(UserInfo record);

    UserInfo selectByPrimaryKey(int id);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectLogin(String phone,String password);

    List<UserInfo> selectAll(@Param("search_value") String search_value);
}