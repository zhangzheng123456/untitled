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

    /**
     * 获取信息类型信息，通过
     * @param id
     * @return
     * @throws SQLException
     */
    Message_type getMessageTypeById(int id) throws SQLException;

    /**
     * 插入信息类型信息
     * @param messageType
     * @return
     * @throws SQLException
     */
    int insert(Message_type messageType) throws SQLException;

    /**
     * 更新企业类型信息
     * @param messageType
     * @return
     * @throws SQLException
     */
    int update(Message_type messageType) throws SQLException;

    /**
     * 获取信息编号信息，
     * @param page_number
     * @param page_size
     * @param corp_code
     * @param search_value
     * @return
     */
    PageInfo<Message_type> selectBySearch(int page_number, int page_size, String corp_code, String search_value);

    /**
     *
     * @param type_code
     * @param corp_code
     * @return
     */
    String MessageTypeCodeExist(String type_code, String corp_code);

    String MessageTypeNameExist(String type_name, String corp_code);

    int deleteById(int id);
}
