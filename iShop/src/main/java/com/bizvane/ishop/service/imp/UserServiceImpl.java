package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.bean.UserInfo;
import com.bizvane.ishop.dao.UserInfoMapper;
import com.bizvane.ishop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfo getUserById(int id) {
        return userInfoMapper.selectByPrimaryKey(id);
    }

    public List<UserInfo> getUsers() {
        return userInfoMapper.selectAll();
    }

    public int insert(UserInfo userInfo) {
        int result = userInfoMapper.insert(userInfo);
        System.out.println(result);
        return result;
    }

}
