package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.UserInfo;
import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface UserService {

    UserInfo getUserById(int id);

    int insert(UserInfo userInfo);

    int update(UserInfo userInfo);

    int delete(int id);

    UserInfo login(String phone,String password);

    UserInfo phoneExist(String phone);

    List<UserInfo> selectAll();

    List<UserInfo> selectBySearch(String search_value);
}