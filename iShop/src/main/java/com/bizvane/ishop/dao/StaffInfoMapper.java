package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.StaffInfo;

public interface StaffInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(StaffInfo record);

    StaffInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(StaffInfo record);

}