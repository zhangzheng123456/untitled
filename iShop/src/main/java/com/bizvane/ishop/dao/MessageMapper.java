package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Message;

public interface MessageMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Message record);

    Message selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Message record);


}