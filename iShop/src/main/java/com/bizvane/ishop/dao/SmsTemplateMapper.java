package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.SmsTemplate;

import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface SmsTemplateMapper {

    int deleteByPrimaryKey(Integer id) throws SQLException;

    int insert(SmsTemplate smsTemplate) throws SQLException;

    SmsTemplate selectByPrimaryKey(Integer id) throws SQLException;

    int updateByPrimaryKey(SmsTemplate smsTemplate) throws SQLException;
    SmsTemplate getSmsTemplateForId(@Param("corp_code") String corp_code, @Param("template_code") String template_code) throws SQLException;

    List<SmsTemplate> selectBySearch(@Param("corp_code") String corp_code, @Param("search_value") String search_value,@Param("manager_corp_arr") String[] manager_corp_arr) throws SQLException;

    List<SmsTemplate> selectByCode(@Param("corp_code") String corp_code, @Param("template_code") String template_code) throws SQLException;

    List<SmsTemplate> selectByName(@Param("template_name") String template_name, @Param("corp_code") String corp_code) throws SQLException;

    List<SmsTemplate> selectAllSmsTemplateScreen(Map<String, Object> params) throws SQLException;

    List<SmsTemplate> selectByTemplateType(@Param("corp_code") String corp_code, @Param("template_type") String template_type) throws SQLException;
    /*List<TemplateType> getTypes();*/
}