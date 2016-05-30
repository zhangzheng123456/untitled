package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Store;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StoreMapper {
    Store selectByStoreId(int id);

    Store selectByUserStore(@Param("corp_code") String corp_code, @Param("store_code") String store_code);

    List<Store> selectAllStore(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    Store selectStoreCode(String shop_code, String corp_code);

    int deleteByStoreId(int id);

    int insertStore(Store store);

    int updateStore(Store store);

}