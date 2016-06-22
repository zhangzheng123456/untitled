package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.MessageTemplate;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/22.
 *
 * @@version
 */
public interface MessageTemplateService {

    MessageTemplate getMessageTemplateById(int id) throws SQLException;

    int insert(MessageTemplate messageTemplate) throws SQLException;

    int delete(int id) throws SQLException;

    int update(MessageTemplate messageTemplate);

    PageInfo<MessageTemplate> selectBySearch(int page_number, int page_size, String corp_code, String search_value);

    String messageTemplateExist(String tem_code, String corp_code) throws SQLException;

    String messageTemplateNameExist(String tem_name, String corp_code) throws SQLException;

}
