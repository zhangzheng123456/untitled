package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.*;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface StoreMapper {
    Store selectByStoreId(int id) throws SQLException;

    Store selectByCode(@Param("corp_code") String corp_code, @Param("store_code") String store_code, @Param("isactive") String isactive) throws SQLException;

    Store selStoreByStroeId(@Param("corp_code") String corp_code, @Param("store_id") String store_id, @Param("isactive") String isactive) throws SQLException;

    List<Store> selectAllStore(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<Store> selectStores(@Param("corp_code") String corp_code) throws SQLException;

    List<Store> selectByAreaBrand(Map<String, Object> params) throws SQLException;

    List<Store> selStoreByAreaBrand(Map<String, Object> params) throws SQLException;

    List<Store>  selectStore(Map<String, Object> params) throws SQLException;

    List<Store> selStoreByStoreCodes(Map<String, Object> params) throws SQLException;

    List<Store> selectByUserId(Map<String, Object> params) throws SQLException;

    int insertStore(Store store) throws SQLException;

    int updateStore(Store store) throws SQLException;

    int deleteByStoreId(int id) throws SQLException;

    int deleteStoreUser(@Param("user_id") String user_id, @Param("store_code") String store_code) throws SQLException;

    Store selectByStoreName(@Param("corp_code") String corp_code, @Param("store_name") String store_name, @Param("isactive") String isactive) throws SQLException;

    int selectCount(@Param("created_date") String created_date) throws SQLException;

    int selectAchCount(@Param("corp_code") String corp_code, @Param("store_code") String store_code) throws SQLException;

    List<Store> selectAllStoreScreen(Map<String, Object> params) throws SQLException;

    List<Store> selectAllStoreScreenEasy(Map<String, Object> params) throws SQLException;

    List<Store> selectAllStoresByCorpCode(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<Store> selectStoreCountByArea(Map<String, Object> params) throws SQLException;

    List<Store> selectStoreCountByBrand(Map<String, Object> params) throws SQLException;


    //------------------------StoreQrcodeMapper.xml----------

    List<StoreQrcode> selectByStoreCode(@Param("corp_code") String corp_code, @Param("store_code") String store_code) throws SQLException;

    StoreQrcode selectByStoreApp(@Param("corp_code") String corp_code, @Param("store_code") String store_code, @Param("app_id") String app_id) throws SQLException;

    int insertStoreQrcode(StoreQrcode record);

    int deleteStoreQrcode(@Param("corp_code") String corp_code, @Param("store_code") String store_code) throws SQLException;

    int deleteStoreQrcodeOne(@Param("corp_code") String corp_code, @Param("store_code") String store_code, @Param("app_id") String app_id) throws SQLException;

}