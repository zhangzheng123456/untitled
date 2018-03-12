package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.StoreQrcode;
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
    PageInfo<Store> selectByUserId(int page_number, int page_size, String store_code, String corp_code, String search_value, String isactive) throws Exception;

    PageInfo<Store> selStoreByStoreCodes(int page_number, int page_size, String store_code, String corp_code, String search_value) throws Exception;

    List<Store> selectByStoreCodes(String store_code, String corp_code, String isactive) throws Exception;

    List<Store> selectByStoreCodes1(String store_code, String corp_code, String isactive) throws Exception;

    int deleteStoreUser(String user_id, String store_code) throws Exception;

    Store getStoreById(int id) throws Exception;

    Store getById(int id) throws Exception;

    PageInfo<Store> getAllStore(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value,String isactive,String search_area_code,String offline_area,String store_type,String dealer,String find_type) throws Exception;

    PageInfo<Store> getAllStore(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value,String isactive,String search_area_code,String offline_area,String store_type,String dealer,String find_type,String manager_corp) throws Exception;


    PageInfo<Store> getAllStore1(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value,String isactive,String search_area_code,String offline_area,String store_type,String dealer,String find_type) throws Exception;

    List<Store> getCorpStore(String corp_code) throws Exception;

    Store getStoreByCode(String corp_code, String store_code, String isactive) throws Exception;

    List<User> getStoreUser(String corp_code, String store_code,String area_code,  String role_code,String isactive) throws Exception;

    PageInfo<User> getStoreUsers(int page_number,int page_size,String corp_code, String store_code, String area_code, String role_code, String isactive,String search_value) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id,String user_name) throws Exception;

    int updateStore(Store store) throws Exception;


    int delete(int id) throws Exception;

    int deleteStoreRelation(String corp_code,String store_code) throws Exception;

    List<Store> getStoreByName(String corp_code, String store_name,String isactive) throws Exception;

    Store storeIdExist(String corp_code, String store_id) throws Exception;

    int selectAchCount(String corp_code, String store_code) throws Exception;

    int selectCount(String created_date) throws Exception;

    PageInfo<Store> selectByAreaBrand(int page_number, int page_size, String corp_code, String[] area_code,String[] store_codes, String[] brand_code, String search_value,String isactive,String search_area_code,String offline_area,String store_type,String dealer,String find_type) throws Exception;

    PageInfo<Store> selStoreByAreaBrandCode(int page_number, int page_size, String corp_code, String area_code, String brand_code, String search_value,String area_store_code) throws Exception;

    PageInfo<Store> selStoreByAreaBrandCity(int page_number, int page_size, String corp_code, String area_code, String brand_code, String search_value,String area_store_code,String city,String find_type) throws Exception ;

    PageInfo<Store> selStoreByAreaBrandCity(int page_number, int page_size, String corp_code, String area_code, String brand_code, String search_value,String area_store_code,String city,String find_type,String manager_corp) throws Exception ;


    List<Store> selStoreByAreaBrandCode(String corp_code, String area_code, String brand_code, String search_value,String area_store_code) throws Exception ;

    List<Store> selStoreByAreaBrandCode(String corp_code, String area_code, String brand_code, String search_value,String area_store_code,String manager_corp) throws Exception ;

    List<Store> selStoreByAreaBrandCode1(String corp_code, String area_code, String brand_code,String search_value, String area_store_code,String isactive) throws Exception;

    List<Store> selectByAreaBrand(String corp_code, String[] area_code,String[] store_codes, String[] brand_code, String isactive) throws Exception;

    List<Store> selectStoreCountByArea(String corp_code, String area_code, String isactive) throws Exception;

    List<Store> selectStoreCountByBrand(String corp_code, String brand_code,String search_value, String isactive) throws Exception;

    String insertExecl(Store store) throws Exception;

    String updateExecl(Store store) throws Exception;

    PageInfo<Store> getAllStoreScreen(int page_number, int page_size, String corp_code, String area_codes, String brand_codes,String store_codes, Map<String, String> map,String area_store_codes,String isactive,String find_type) throws Exception;

    PageInfo<Store> getAllStoreScreen(int page_number, int page_size, String corp_code, String area_codes, String brand_codes,String store_codes, Map<String, String> map,String area_store_codes,String isactive,String find_type,String manager_corp) throws Exception;

    PageInfo<Store> getAllStoreScreen1(int page_number, int page_size, String corp_code, String area_codes, String brand_codes,String store_codes, Map<String, String> map,String area_store_codes,String isactive) throws Exception;

    List<Store> selectStoreCity(String corp_code,String search_value) throws Exception;

    int deleteStoreQrcode(String corp_code,String store_code) throws Exception;

    int deleteStoreQrcodeOne(String corp_code, String store_code, String app_id) throws Exception;

    String creatStoreQrcode(String corp_code,String store_code,String auth_appid,String user_id) throws Exception;

    List<Store> selectStore(String corp_code, String store_codes) throws SQLException;

    List<Store> getStoreByBrandCode(String corp_code, String area_codes, String brand_codes,String store_codes, Map<String, String> map,String area_store_codes,String isactive) throws Exception;

    List<Store> getStoreByBrandCode(String corp_code, String area_codes, String brand_codes,String store_codes, Map<String, String> map,String area_store_codes,String isactive,String  manager_corp) throws Exception;

    List<StoreQrcode> selectStoreQrcodeByApp(String corp_code, String store_code, String auth_appid) throws Exception;

    PageInfo<Store> selectAllOrderByCity(int page_number,int page_size,String corp_code, String search_value) throws Exception;

    List<Store> selectNearByStore(String corp_code, String lng, String lat, String distance) throws Exception;


    List<Store> getStoreByOdsType(String corp_code, String line_code)throws Exception;

     void insertStoreStartOrEnd(int id, String type, String user_code,String user_name) throws Exception;

    List<Store> getAllStoreByCount()throws Exception;

    int updStoreDayCount();

    int updStoreTime(String corp_code);

    List<Store> getStoreByNameTwo(String corp_code, String store_name,String isactive) throws Exception;

    List<Store> getStoreAndBrandArea(String corp_code, String area_codes, String brand_codes, String store_codes, String area_store_codes) throws Exception;

    List<Store> selectCorpCanSearch(String corp_code, String search_value) throws Exception;

    List<StoreQrcode> selctStoreQrcode(String corp_code, String store_code, String app_id) throws Exception;

    StoreQrcode selectAppIdByQrcode(String corp_code, String store_code, String qrcode)throws Exception;

}
