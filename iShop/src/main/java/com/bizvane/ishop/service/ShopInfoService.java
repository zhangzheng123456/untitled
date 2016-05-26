package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.ShopInfo;

import java.sql.SQLException;

/**
 * Created by nanji on 2016/5/25.
 */
public interface ShopInfoService {
    ShopInfo getShopInfo(int id) throws SQLException;

    int insert(ShopInfo shopInfo)throws SQLException;

    int update(ShopInfo shopInfo)throws SQLException;

    int delete(int id) throws SQLException;

}
