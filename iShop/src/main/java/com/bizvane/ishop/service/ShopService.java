package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.ShopInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by nanji on 2016/5/25.
 */
public interface ShopService {
    ShopInfo getShopInfo(int id) throws SQLException;

    List<ShopInfo> getAllShop(String corp_code,String search_value);

    int insert(ShopInfo shopInfo)throws SQLException;

    int update(ShopInfo shopInfo)throws SQLException;

    int delete(int id) throws SQLException;

}
