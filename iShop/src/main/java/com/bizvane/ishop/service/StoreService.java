package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by nanji on 2016/5/25.
 */
public interface StoreService {

    /**
     * 分页获取店铺列表信息
     * @param page_number ： 起始页码
     * @param page_size ： 分页大小
     * @param store_code ： 店铺编号
     * @param corp_code ： 公司编号
     * @param search_value ： 产需条件
     * @return
     */
    PageInfo<Store> selectByUserId(int page_number, int page_size, String store_code, String corp_code, String search_value);

    List<Store> selectAll(String user_id, String corp_code);

    int deleteStoreUser(String user_id, String store_code);

    Store getStoreById(int id) throws SQLException;

    PageInfo<Store> getAllStore(HttpServletRequest request,int page_number, int page_size, String corp_code, String search_value);

    List<Store> getCorpStore(String corp_code) throws SQLException;

    Store getStoreByCode(String corp_code, String store_code,String isactive);

    List<User> getStoreUser(String corp_code, String store_code);

    String insert(String message, String user_id) throws SQLException;

    String update(String message, String user_id) throws SQLException;

    int updateStore(Store store) throws SQLException;

    int delete(int id) throws SQLException;

    Store getStoreByName(String corp_code, String store_name) throws SQLException;

    int selectAchCount(String store_code)throws SQLException;

    int selectCount(String created_date);

    PageInfo<Store> selectByAreaCode(int page_number,int page_size,String corp_code,String[] area_code,String search_value);
}
