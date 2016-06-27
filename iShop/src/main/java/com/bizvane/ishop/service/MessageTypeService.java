package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Message_type;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/21.
 *
 * @@version
 */
public interface MessageTypeService {

    /**
     * 获取信息类型信息，通过
     *
     * @param id
     * @return
     * @throws SQLException
     */
    Message_type getMessageTypeById(int id) throws SQLException;

    /**
     * 插入信息类型信息
     *
     * @param messageType
     * @return
     * @throws SQLException
     */
    int insert(Message_type messageType) throws SQLException;

    /**
     * 更新企业类型信息
     *
     * @param messageType
     * @return
     * @throws SQLException
     */
    String update(Message_type messageType) throws SQLException;

    /**
     * 获取信息编号信息，
     *
     * @param page_number
     * @param page_size
     * @param corp_code
     * @param search_value
     * @return
     */
    PageInfo<Message_type> selectBySearch(int page_number, int page_size, String corp_code, String search_value);

    /**
     * 判断短信类型编号是否存在，通过短信类型编号
     *
     * @param type_code ： 短信类型的编号
     * @param corp_code ：短信类型所在企业
     * @return
     */
    String MessageTypeCodeExist(String type_code, String corp_code);

    /**
     * 短信类型名称是否存在，通过短信类型名称
     *
     * @param type_name ：短信类型名称
     * @param corp_code ： 短信类型所在企业编号
     * @return
     */
    String MessageTypeNameExist(String type_name, String corp_code);

    /**
     * 删除短信类型，通过ID
     *
     * @param id
     * @return
     */
    int deleteById(int id);

    List<Message_type> selectAllMessageType();


    List<Message_type> getMessageTypeByCorp(String corp_code, String search_value);

    String messageTypeNameExist(String corp_code, String type_name);

    String messageTypeCodeExist(String type_code, String corp_code);
}
