package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Message;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/17.
 *
 * @@version
 */
public interface MessageService {

    Message getMessageById(int id) throws SQLException;

    //  PageInfo<Message> selectBySearch(Message message) throws SQLException;

    int insert(Message message) throws SQLException;

    int update(Message message) throws SQLException;

    int delete(int id) throws SQLException;

    Message getMessageByCode(String corp_code, String message_code) throws SQLException;

//    String messageExist(String corp_code, String type_code) throws SQLException;

    PageInfo<Message> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws SQLException;

    PageInfo<Message> selectByUser(int page_number, int page_size, String corp_code, String user_code) throws SQLException;

    PageInfo<Message> selectBySearchPart(int page_number, int page_size, String corp_code, String search_values, String store_code, String role_code);

    //String

}
