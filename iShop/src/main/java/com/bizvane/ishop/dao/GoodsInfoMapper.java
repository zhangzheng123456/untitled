package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.GoodsInfo;

public interface GoodsInfoMapper {
    GoodsInfo selectByPrimaryKey(int id);

    int insert(GoodsInfo record);

    int updateByPrimaryKey(GoodsInfo record);

    int deleteByPrimaryKey(int id);

}