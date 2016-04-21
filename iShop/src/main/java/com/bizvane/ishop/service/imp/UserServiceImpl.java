package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.bean.User;
import com.bizvane.ishop.dao.UserDao;
import com.bizvane.ishop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

@Service("UserService")
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDao userDao;

    public boolean insert(User user) {

        return userDao.insert(user);
    }

    public boolean delete(int id) {
        return userDao.delete(id);
    }

    public boolean update(User user) {
        return userDao.update(user);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User findById(int id) {
        return userDao.findById(id);
    }
}
