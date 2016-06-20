package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsMapper {
    Goods selectByPrimaryKey(int id);

    int insert(Goods record);

    int updateByPrimaryKey(Goods record);

    int deleteByPrimaryKey(int id);

    List<Goods> selectAllGoods(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    Goods getGoodsByCode(@Param("corp_code") String corp_code, @Param("goods_code") String goods_code);

    Goods getGoodsByName(@Param("corp_code") String corp_code,@Param("goods_name") String goods_name);
}