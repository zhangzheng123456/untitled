package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by nanji on 2016/5/25.
 */
public interface StoreService {

    PageInfo<Store> selectByUserId(int page_number, int page_size, String user_id, String corp_code, String search_value);

    int deleteStoreByUserid(String user_id, String store_id);

    Store getStoreById(int id) throws SQLException;

    PageInfo<Store> getAllStore(int page_number, int page_size, String corp_code, String search_value);

    List<Store> getCorpStore(String corp_code) throws SQLException;


    Store getStoreByCode(String corp_code, String store_code,String isactive);

    List<User> getStoreUser(String corp_code, String store_code);

    String insert(String message, String user_id) throws SQLException;

    String update(String message, String user_id);

    int delete(int id) throws SQLException;

    Store getStoreByName(String corp_code, String store_name) throws SQLException;

    int selectCount() throws SQLException;
}
