package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.MessageInfo;

public interface MessageInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MessageInfo record);

    MessageInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(MessageInfo record);

}