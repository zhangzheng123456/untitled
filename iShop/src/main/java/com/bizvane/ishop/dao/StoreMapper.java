package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.*;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface StoreMapper {
    Store selectByStoreId(int id) throws SQLException;

    List<Store> selectByCode(@Param("corp_code") String corp_code, @Param("store_code") String store_code, @Param("isactive") String isactive) throws SQLException;

    Store selStoreByStroeId(@Param("corp_code") String corp_code, @Param("store_id") String store_id, @Param("isactive") String isactive) throws SQLException;

    List<Store> selectAllStore(@Param("corp_code") String corp_code, @Param("search_value") String search_value, @Param("isactive") String isactive, @Param("search_area_code") String search_area_code,@Param("offline_area")String offline_area,@Param("store_type")String store_type,@Param("dealer")String dealer,@Param("find_type")String find_type,@Param("manager_corp_arr")String... manager_corp_arr) throws SQLException;

    List<Store> selectByCorp(@Param("corp_code") String corp_code) throws SQLException;

    List<Store> selectByAreaBrand(Map<String, Object> params) throws SQLException;

    List<Store> selStoreByAreaBrand(Map<String, Object> params) throws SQLException;

    List<Store>  selectStore(Map<String, Object> params) throws SQLException;

    List<Store> selStoreByStoreCodes(Map<String, Object> params) throws SQLException;

    List<Store> selStoreByVipRecord(@Param("corp_code") String corp_code, @Param("search_value") String search_value, @Param("isactive") String isactive) throws SQLException;

    List<Store> selectByStoreCodes(Map<String, Object> params) throws SQLException;

    int insertStore(Store store) throws SQLException;

    int updateStore(Store store) throws SQLException;

    int deleteByStoreId(int id) throws SQLException;

    int deleteStoreUser(@Param("user_id") String user_id, @Param("store_code") String store_code) throws SQLException;

    List<Store> selectByStoreName(@Param("corp_code") String corp_code, @Param("store_name") String store_name, @Param("isactive") String isactive) throws SQLException;

    int selectCount(@Param("created_date") String created_date) throws SQLException;

    int selectAchCount(@Param("corp_code") String corp_code, @Param("store_code") String store_code) throws SQLException;

    List<Store> selectAllStoreScreen(Map<String, Object> params) throws SQLException;

    List<Store> selectAllStoreScreenEasy(Map<String, Object> params) throws SQLException;

    List<Store> selectStoreCountByArea(Map<String, Object> params) throws SQLException;

    List<Store> selectStoreCountByBrand(Map<String, Object> params) throws SQLException;

    List<Store> selectStoreCity(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<Store> selectAllOrderByCity(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<Store> selectNearByStore(@Param("corp_code") String corp_code, @Param("lng") String lng, @Param("lat") String lat, @Param("distance") String distance) throws SQLException;

    List<Store> getStoreAndBrandArea(Map<String, Object> params) throws Exception;

    List<Store> selectCorpCanSearch(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;
    //------------------------StoreQrcodeMapper.xml----------

    List<StoreQrcode> selectByStoreCode(@Param("corp_code") String corp_code, @Param("store_code") String store_code) throws SQLException;

    List<StoreQrcode> selectByStoreApp(@Param("corp_code") String corp_code, @Param("store_code") String store_code, @Param("app_id") String app_id) throws SQLException;

    StoreQrcode selectByStoreQrcode(@Param("corp_code") String corp_code, @Param("store_code") String store_code, @Param("qrcode") String qrcode) throws SQLException;


    int insertStoreQrcode(StoreQrcode record);

    int deleteStoreQrcode(@Param("corp_code") String corp_code, @Param("store_code") String store_code) throws SQLException;

    int deleteStoreQrcodeOne(@Param("corp_code") String corp_code, @Param("store_code") String store_code, @Param("app_id") String app_id) throws SQLException;

    List<Store> getStoreByOdsType(@Param("corp_code")String corp_code,@Param("line_code")String line_code)throws Exception;


    List<Store> getAllStoreByCount()throws Exception;

    int updStoreDayCount();

    int updStoreTime(@Param("corp_code")String corp_code);

    List<Store> selectByStoreNameTwo(@Param("corp_code") String corp_code, @Param("store_name") String store_name, @Param("isactive") String isactive) throws SQLException;

    List<StoreQrcode> selctStoreQrcode(@Param("corp_code") String corp_code, @Param("store_code") String store_code, @Param("app_id") String app_id) throws SQLException;

    int updateStoreByCode(Store store) throws SQLException;

}