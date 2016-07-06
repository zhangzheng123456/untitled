package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.MessageTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageTemplateMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(MessageTemplate messageTemplate);

    MessageTemplate selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(MessageTemplate messageTemplate);

    List<MessageTemplate> selectBySearch(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    List<MessageTemplate> selectByCode(@Param("corp_code") String corp_code, @Param("template_code") String template_code);

    List<MessageTemplate> selectByName(@Param("template_name") String template_name, @Param("corp_code") String corp_code);
}