package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Message_type;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/21.
 *
 * @@version
 */
public interface MessageTypeService {

    Message_type getMessageTypeById(int id) throws SQLException;

    int insert(Message_type messageType) throws SQLException;

    int update(Message_type messageType) throws SQLException;

    PageInfo<Message_type> selectBySearch(int page_number, int page_size, String corp_code, String search_value);

    String MessageTypeCodeExist(String type_code, String corp_code);

    String MessageTypeNameExist(String type_name, String corp_code);

}
