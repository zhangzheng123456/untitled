package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Store;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StoreMapper {
    Store selectByStoreId(int id);

    Store selectByUserStore(@Param("corp_code") String corp_code, @Param("store_code") String store_code);

    List<Store> selectAllStore(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    List<Store> selectByUserId(@Param("user_id")String user_id);

    Store selectStoreCode(String shop_code, String corp_code);

    int deleteStoreByUser_id(@Param("user_id") String user_id,@Param("store_id") String store_id);

    int insertStore(Store store);

    int updateStore(Store store);

    int deleteByStoreId(int id);
}