package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.SystemInfo;

public interface SystemInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SystemInfo record);

    SystemInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(SystemInfo record);

}