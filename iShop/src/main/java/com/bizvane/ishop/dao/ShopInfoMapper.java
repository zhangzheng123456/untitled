package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ShopInfo;

public interface ShopInfoMapper {
    ShopInfo selectByShopInfoId(int id);

    int deleteByShopInfoId(int id);

    int insertShopInfo(ShopInfo shopInfo);

    int updateShopInfo(ShopInfo shopInfo);

}