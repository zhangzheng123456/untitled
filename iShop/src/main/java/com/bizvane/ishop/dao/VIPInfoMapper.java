package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VIPInfo;

public interface VIPInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(VIPInfo record);

    VIPInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(VIPInfo record);

}