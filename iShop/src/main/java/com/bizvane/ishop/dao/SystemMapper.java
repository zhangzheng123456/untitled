package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.System;

public interface SystemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(System record);

    System selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(System record);

}