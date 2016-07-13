package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Store;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StoreMapper {
    Store selectByStoreId(int id);

    Store selectByCode(@Param("corp_code") String corp_code, @Param("store_code") String store_code, @Param("isactive") String isactive);

    List<Store> selectAllStore(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    List<Store> selectStores(@Param("corp_code") String corp_code);

    List<Store> selectByUserId(Map<String, Object> params);

    List<Store> selectStoreBrandArea(@Param("corp_code") String corp_code, @Param("brand_code") String brand_code, @Param("area_code") String area_code);

    int insertStore(Store store);

    int updateStore(Store store);

    int deleteByStoreId(int id);

    int deleteStoreByUserid(@Param("user_id") String user_id, @Param("store_code") String store_code);

    Store selectByStoreName(@Param("corp_code") String corp_code, @Param("store_name") String store_name);

    int selectCount(@Param("created_date") String created_date);

    int selectAchCount(@Param("store_code") String store_code);

    List<Store> selectByAreaCode(Map<String, Object> params);

    int selectUserCount(String corp_code, String store_code);
}