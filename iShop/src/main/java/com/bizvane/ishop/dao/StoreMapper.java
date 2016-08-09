package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Store;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface StoreMapper {
    Store selectByStoreId(int id) throws SQLException;

    Store selectByCode(@Param("corp_code") String corp_code, @Param("store_code") String store_code, @Param("isactive") String isactive) throws SQLException;

    List<Store> selectAllStore(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<Store> selectStores(@Param("corp_code") String corp_code) throws SQLException;

    List<Store> selectByUserId(Map<String, Object> params) throws SQLException;

    List<Store> selStoreByUserCode(Map<String, Object> params) throws SQLException;

    List<Store> selectStoreBrandArea(@Param("corp_code") String corp_code, @Param("brand_code") String brand_code, @Param("area_code") String area_code) throws SQLException;

    int insertStore(Store store) throws SQLException;

    int updateStore(Store store) throws SQLException;

    int deleteByStoreId(int id) throws SQLException;

    int deleteStoreByUserid(@Param("user_id") String user_id, @Param("store_code") String store_code) throws SQLException;

    Store selectByStoreName(@Param("corp_code") String corp_code, @Param("store_name") String store_name, @Param("isactive") String isactive) throws SQLException;

    int selectCount(@Param("created_date") String created_date) throws SQLException;

    int selectAchCount(@Param("corp_code") String corp_code, @Param("store_code") String store_code) throws SQLException;

    List<Store> selectByAreaCode(Map<String, Object> params) throws SQLException;

    List<Store> selStoreByAreaCode(Map<String, Object> params) throws SQLException;


    List<Store> selectAllStoreScreen(Map<String, Object> params) throws SQLException;

    List<Brand> selectBrandsStore(@Param("corp_code") String corp_code, @Param("brand_code") String brand_code) throws SQLException;
}