package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.dao.UserInfoMapper;
import com.bizvane.ishop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfo getUserById(int id) throws SQLException {
        return userInfoMapper.selectByUserId(id);
    }

    public int insert(UserInfo userInfo) throws SQLException {
        return userInfoMapper.insertUser(userInfo);
    }

    public int update(UserInfo userInfo) throws SQLException {
        return userInfoMapper.updateByUserId(userInfo);
    }

    public int delete(int id) throws SQLException {
        return userInfoMapper.deleteByUserId(id);
    }

    /**
     * 登录查询
     */
    public UserInfo login(String phone, String password) throws SQLException {
        System.out.println("---------login--------");
        return userInfoMapper.selectLogin(phone, password);
    }

    /**
     * 验证手机号是否已存在
     */
    public UserInfo phoneExist(String phone) throws SQLException {
        return userInfoMapper.selectByPhone(phone);
    }

    /**
     * @param corp_code
     * @param search_value
     */
    public List<UserInfo> selectBySearch(String corp_code, String search_value) throws SQLException {
        return userInfoMapper.selectAllUser(corp_code, "%" + search_value + "%");
    }
}
