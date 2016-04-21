package com.bizvane.ishop.service;

import com.bizvane.ishop.bean.User;

import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface UserService {

    public boolean insert(User user);

//    public boolean save(User user);

    public boolean delete(int id);

    public boolean update(User user);

    public List<User> findAll();

    public User findById(int id);

}
