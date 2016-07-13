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

    int insert(Message messavge) throws SQLException;

    int delete(int id) throws SQLException;

    PageInfo<Message> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws SQLException;

}
