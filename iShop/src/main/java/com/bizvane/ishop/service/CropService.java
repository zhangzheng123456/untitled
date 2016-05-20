package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.exception.UserException;

import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface CropService {

    public boolean insert(UserInfo userInfo)throws UserException;

    public boolean delete(int id)throws UserException;

    public boolean update(UserInfo userInfo)throws UserException;

    public List<UserInfo> findAll()throws UserException;

    public UserInfo findById(int id)throws UserException;

}
