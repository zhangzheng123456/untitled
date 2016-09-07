package com.bizvane.ishop.dao;


import com.bizvane.ishop.entity.SmsTemplateType;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by nanji on 2016/9/7.
 */
public interface SmsTemplateTypeMapper {

    List<SmsTemplateType> selectAllSmsTemplateType(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    int deleteSmsTemplateTypeById(Integer id) throws SQLException;

    int insertSmsTemplateType(SmsTemplateType smsTemplateType) throws SQLException;

    SmsTemplateType selectBySmsTemplateTypeName(@Param("corp_code") String corp_code, @Param("template_type_name") String template_type_name, @Param("isactive") String isactive) throws SQLException;

    SmsTemplateType selectBySmsTemplateTypeCode(@Param("corp_code") String corp_code, @Param("template_type_code") String template_type_code, @Param("isactive") String isactive) throws SQLException;


}
