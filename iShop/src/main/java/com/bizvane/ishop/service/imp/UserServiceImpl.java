package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.dao.UserInfoMapper;
import com.bizvane.ishop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by maoweidong on 2016/2/15.
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfo getUserById(int id) {
        return userInfoMapper.selectByPrimaryKey(id);
    }

    public int insert(UserInfo userInfo) {
        return userInfoMapper.insert(userInfo);
    }

    public int update(UserInfo userInfo){
        return userInfoMapper.updateByPrimaryKey(userInfo);
    }

    public int delete(int id){
        return userInfoMapper.deleteByPrimaryKey(id);
    }

    public UserInfo login(String phone,String password){
        return userInfoMapper.selectByPMP(phone,password);
    }
}
