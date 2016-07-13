package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.SmsTemplate;
import com.bizvane.ishop.entity.VipRecord;
import com.bizvane.ishop.entity.VipRecordType;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/22.
 *
 * @@version
 */
public interface SmsTemplateService {

    /**
     * 获取短信模板，通过ID
     *
     * @param id
     * @return
     * @throws SQLException
     */
    SmsTemplate getSmsTemplateById(int id) throws SQLException;

    /**
     * 插入短您模板信息
     *
     * @param SmsTemplate
     * @return
     * @throws SQLException
     */
    int insert(SmsTemplate SmsTemplate) throws SQLException;

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
     * @param SmsTemplate
     * @return
     */
    String update(SmsTemplate SmsTemplate) throws SQLException;

    /**
     * 获取分页信息
     *
     * @param page_number  ： 页面的起始页码
     * @param page_size    ：分页大小
     * @param corp_code    ： 惬意编码
     * @param search_value ：查询信息
     * @return
     */
    PageInfo<SmsTemplate> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws SQLException;

    /**
     * 通过模板编号，判断模板在企业内是否存在
     *
     * @param template_code
     * @param corp_code
     * @return
     * @throws SQLException
     */
    String SmsTemplateExist(String corp_code, String template_code) throws SQLException;

    /**
     * 通过模板名称，来判断模板名在企业内是否唯一
     *
     * @param template_name
     * @param corp_code
     * @return
     * @throws SQLException
     */
    String SmsTemplateNameExist(String corp_code, String template_name) throws SQLException;

//    List<TemplateType> getTypes();

//    List<VipRecordType> getMessageTypeByCorp(String corp_code, String s);
}
