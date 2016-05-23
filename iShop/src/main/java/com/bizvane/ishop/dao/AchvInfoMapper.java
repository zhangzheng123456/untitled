package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.AchvInfo;

public interface AchvInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AchvInfo record);

    AchvInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(AchvInfo record);

}