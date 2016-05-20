package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.UserInfo;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface UserService {

    UserInfo getUserById(int id);

    int insert(UserInfo userInfo);

    int update(UserInfo userInfo);

    int delete(int id);

    UserInfo login(String phone,String password);
}