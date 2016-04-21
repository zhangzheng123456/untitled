package com.bizvane.ishop.dao;

import com.bizvane.ishop.bean.User;
import java.util.List;


public interface UserDao {

    public boolean insert(User user);

    public boolean delete(int id);

    public boolean update(User user);

    public List<User> findAll();

    public User findById(int id);

}
