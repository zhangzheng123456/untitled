package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Message;
import com.bizvane.ishop.entity.MessageInfo;
import com.bizvane.ishop.entity.MessageType;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/17.
 *
 * @@version
 */
public interface MessageService {

    MessageInfo getMessageById(int id) throws SQLException;

    List<Message> getMessageDetail(String message_code) throws SQLException;

    String insert(String message,String user_code);

    int delete(int id) throws SQLException;

    PageInfo<MessageInfo> selectBySearch(int page_number, int page_size, String corp_code, String user_code, String search_value) throws SQLException;

    List<MessageType> selectAllMessageType() throws SQLException;
}
