package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Store;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StoreMapper {
    Store selectByStoreId(int id);

    Store selectByCorp(@Param("corp_code") String corp_code, @Param("store_code") String store_code);

    List<Store> selectAllStore(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    List<Store> selectByUserId(@Param("user_id")String user_id);

    List<Store> selectStoreBrandArea(@Param("corp_code") String corp_code, @Param("brand_code") String brand_code, @Param("area_code") String area_code);

    int insertStore(Store store);

    int updateStore(Store store);

    int deleteByStoreId(int id);

    int deleteStoreByUserid(@Param("user_id") String user_id, @Param("store_id") String store_id);

}