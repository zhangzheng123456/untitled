package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ShopInfo;

public interface ShopInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShopInfo record);

    ShopInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(ShopInfo record);

}