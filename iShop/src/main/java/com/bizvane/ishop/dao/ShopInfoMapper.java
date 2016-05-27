package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ShopInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopInfoMapper {
    ShopInfo selectByShopInfoId(int id);

    List<ShopInfo> selectAllShop(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    ShopInfo selectShopCode(String shop_code, String corp_code);

    int deleteByShopInfoId(int id);

    int insertShopInfo(ShopInfo shopInfo);

    int updateShopInfo(ShopInfo shopInfo);

}