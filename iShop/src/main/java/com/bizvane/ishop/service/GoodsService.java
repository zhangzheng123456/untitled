package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Goods;

import java.sql.SQLException;

/**
 * Created by nanji on 2016/5/30.
 */
public interface GoodsService {
    Goods getGoodsById(int id)throws SQLException;

    int insert(Goods goods)throws SQLException;

    int update(Goods goods) throws SQLException;

    int delete(int id) throws SQLException;
}
