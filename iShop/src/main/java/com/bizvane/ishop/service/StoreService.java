package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/5/25.
 */
public interface StoreService {

    /**
     * 分页获取店铺列表信息
     *
     * @param page_number  ： 起始页码
     * @param page_size    ： 分页大小
     * @param store_code   ： 店铺编号
     * @param corp_code    ： 公司编号
     * @param search_value ： 产需条件
     * @return
     */
    PageInfo<Store> selectByUserId(int page_number, int page_size, String store_code, String corp_code, String search_value) throws Exception;

    PageInfo<Store> selStoreByStoreCodes(int page_number, int page_size, String store_code, String corp_code, String search_value) throws Exception;

    List<Store> selectByStoreCodes(String store_code, String corp_code, String isactive) throws Exception;

    int deleteStoreUser(String user_id, String store_code) throws Exception;

    Store getStoreById(int id) throws Exception;

    Store getById(int id) throws Exception;

    PageInfo<Store> getAllStore(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value,String isactive,String search_area_code) throws Exception;

    List<Store> getCorpStore(String corp_code) throws Exception;

    Store getStoreByCode(String corp_code, String store_code, String isactive) throws Exception;

    Store selStoreByStroeId(String corp_code, String store_id, String isactive) throws Exception;

    List<User> getStoreUser(String corp_code, String store_code,String area_code,  String role_code,String isactive) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int updateStore(Store store) throws Exception;

    int delete(int id) throws Exception;

    List<Store> getStoreByName(String corp_code, String store_name,String isactive) throws Exception;

    Store storeIdExist(String corp_code, String store_id) throws Exception;

    int selectAchCount(String corp_code, String store_code) throws Exception;

    int selectCount(String created_date) throws Exception;

    PageInfo<Store> selectByAreaBrand(int page_number, int page_size, String corp_code, String[] area_code,String[] store_codes, String[] brand_code, String search_value,String isactive,String search_area_code) throws Exception;

    PageInfo<Store> selStoreByAreaBrandCode(int page_number, int page_size, String corp_code, String area_code, String brand_code, String search_value,String area_store_code) throws Exception;

    PageInfo<Store> selStoreByAreaBrandCity(int page_number, int page_size, String corp_code, String area_code, String brand_code, String search_value,String area_store_code,String city) throws Exception ;

    List<Store> selStoreByAreaBrandCode(String corp_code, String area_code, String brand_code, String search_value,String area_store_code) throws Exception ;

    List<Store> selectByAreaBrand(String corp_code, String[] area_code,String[] store_codes, String[] brand_code, String isactive) throws Exception;

    List<Store> selectStoreCountByArea(String corp_code, String area_code, String isactive) throws Exception;

    List<Store> selectStoreCountByBrand(String corp_code, String brand_code,String search_value, String isactive) throws Exception;

    String insertExecl(Store store) throws Exception;

    PageInfo<Store> getAllStoreScreen(int page_number, int page_size, String corp_code, String area_codes, String brand_codes,String store_codes, Map<String, String> map,String area_store_codes,String isactive) throws Exception;

    List<Store> selectStoreCity(String corp_code,String search_value) throws Exception;

    int deleteStoreQrcode(String corp_code,String store_code) throws Exception;

    int deleteStoreQrcodeOne(String corp_code, String store_code, String app_id) throws Exception;

    String creatStoreQrcode(String corp_code,String store_code,String auth_appid,String user_id) throws Exception;

    List<Store> selectStore(String corp_code, String store_codes) throws SQLException;

    List<Store> getStoreByBrandCode(String corp_code, String area_codes, String brand_codes,String store_codes, Map<String, String> map,String area_store_codes,String isactive) throws Exception;
}
