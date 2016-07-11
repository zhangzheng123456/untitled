package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.SmsTemplate;
import com.bizvane.ishop.entity.TemplateType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmsTemplateMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(SmsTemplate smsTemplate);

    SmsTemplate selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(SmsTemplate smsTemplate);

    List<SmsTemplate> selectBySearch(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    List<SmsTemplate> selectByCode(@Param("corp_code") String corp_code, @Param("template_code") String template_code);

    List<SmsTemplate> selectByName(@Param("template_name") String template_name, @Param("corp_code") String corp_code);

    List<TemplateType> getTypes();
}