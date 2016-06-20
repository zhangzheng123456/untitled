package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Goods;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by nanji on 2016/5/30.
 */
public interface GoodsService {
    Goods getGoodsById(int id) throws SQLException;

    int insert(Goods goods) throws SQLException;

    int update(Goods goods) throws SQLException;

    int delete(int id) throws SQLException;

    PageInfo<Goods> selectBySearch(int page_number, int page_size, String corp_code, String search_value);

    Goods getGoodsByCode(String corp_code, String goods_code);

    String goodsCodeExist(String corp_code, String goods_code);

    String goodsNameExist(String corp_code, String goods_name);
}
