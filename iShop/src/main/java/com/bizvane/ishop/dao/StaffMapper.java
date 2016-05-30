package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Staff;

public interface StaffMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Staff record);

    Staff selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Staff record);

}