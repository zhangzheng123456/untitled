package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.LogInfo;

public interface LogInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LogInfo record);

    LogInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(LogInfo record);

}