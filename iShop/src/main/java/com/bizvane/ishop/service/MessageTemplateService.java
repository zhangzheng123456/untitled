package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.MessageTemplate;
import com.bizvane.ishop.entity.Message_type;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/22.
 *
 * @@version
 */
public interface MessageTemplateService {

    /**
     * 获取短信模板，通过ID
     *
     * @param id
     * @return
     * @throws SQLException
     */
    MessageTemplate getMessageTemplateById(int id) throws SQLException;

    /**
     * 插入短您模板信息
     *
     * @param messageTemplate
     * @return
     * @throws SQLException
     */
    int insert(MessageTemplate messageTemplate) throws SQLException;

    /**
     * 删除短信模板信息，通过ID
     *
     * @param id
     * @return
     * @throws SQLException
     */
    int delete(int id) throws SQLException;

    /**
     * 更新短信模板信息，通过ID
     *
     * @param messageTemplate
     * @return
     */
    String update(MessageTemplate messageTemplate) throws SQLException;

    /**
     * 获取分页信息
     *
     * @param page_number  ： 页面的起始页码
     * @param page_size    ：分页大小
     * @param corp_code    ： 惬意编码
     * @param search_value ：查询信息
     * @return
     */
    PageInfo<MessageTemplate> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws SQLException;

    /**
     * 通过模板编号，判断模板在企业内是否存在
     *
     * @param template_code
     * @param corp_code
     * @return
     * @throws SQLException
     */
    String messageTemplateExist(String corp_code, String template_code) throws SQLException;

    /**
     * 通过模板名称，来判断模板名在企业内是否唯一
     *
     * @param template_name
     * @param corp_code
     * @return
     * @throws SQLException
     */
    String messageTemplateNameExist(String corp_code, String template_name) throws SQLException;

}
