package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Message;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/17.
 *
 * @@version
 */
public interface MessageService {

    Message getMessageById(int id) throws SQLException;

    PageInfo<Message> selectBySearch(Message message) throws SQLException;

    int insert(Message message) throws SQLException;

    int update(Message message) throws SQLException;

    int delete(int id) throws SQLException;

    Message getMessageByCode(String corp_code, String message_code);

    String messageExist(String corp_code, String type_code);

    //String

}
