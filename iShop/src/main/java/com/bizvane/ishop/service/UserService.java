package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.UserInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface UserService {

    UserInfo getUserById(int id) throws SQLException;

    int insert(UserInfo userInfo) throws SQLException;

    int update(UserInfo userInfo) throws SQLException;

    int delete(int id) throws SQLException;

    UserInfo login(String phone,String password) throws SQLException;

    UserInfo phoneExist(String phone) throws SQLException;

    List<UserInfo> selectBySearch(String corp_code,String search_value) throws SQLException;
}