package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Goods;

public interface GoodsMapper {
    Goods selectByPrimaryKey(int id);

    int insert(Goods record);

    int updateByPrimaryKey(Goods record);

    int deleteByPrimaryKey(int id);

}