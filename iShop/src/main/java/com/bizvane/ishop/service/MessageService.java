package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Message;
import com.bizvane.ishop.entity.MessageInfo;
import com.bizvane.ishop.entity.MessageType;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiang on 2016/6/17.
 *
 * @@version
 */
public interface MessageService {

    MessageInfo getMessageById(int id) throws Exception;

    List<Message> getMessageDetail(String message_code) throws Exception;

    List<Message> selectMessageByCode(String message_code) throws Exception;

    String insert(String message,String user_id) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<MessageInfo> selectBySearch(int page_number, int page_size, String corp_code, String user_code, String search_value) throws Exception;

    PageInfo<MessageInfo> selectBySearch(int page_number, int page_size, String corp_code, String user_code, String search_value,String manager_corp) throws Exception;


    List<MessageType> selectAllMessageType() throws Exception;

    PageInfo<MessageInfo> selectByScreen(int page_number, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception;

    PageInfo<MessageInfo> selectByScreen(int page_number, int page_size, String corp_code, String user_code, Map<String, String> map,String manager_corp) throws Exception;


}
