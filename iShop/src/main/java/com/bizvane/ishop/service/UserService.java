package com.bizvane.ishop.service;

import com.bizvane.ishop.bean.UserInfo;

import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface UserService {

    UserInfo getUserById(int id);

    List<UserInfo> getUsers();

    int insert(UserInfo userInfo);
}