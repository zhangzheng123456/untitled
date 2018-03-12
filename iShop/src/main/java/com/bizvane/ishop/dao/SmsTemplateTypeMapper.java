package com.bizvane.ishop.dao;


import com.bizvane.ishop.entity.SmsTemplateType;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/9/7.
 */
public interface SmsTemplateTypeMapper {

    SmsTemplateType selectSmsTemplateById(int id) throws SQLException;

    List<SmsTemplateType> selectAllSmsTemplateType(@Param("corp_code") String corp_code, @Param("search_value") String search_value,@Param("manager_corp_arr") String[] manager_corp_arr) throws SQLException;

    List<SmsTemplateType> selectSmsTwmplateTypes(@Param("corp_code") String corp_code) throws SQLException;

    int deleteSmsTemplateTypeById(Integer id) throws SQLException;

    int insertSmsTemplateType(SmsTemplateType smsTemplateType) throws SQLException;

    int updateSmsTemplateType(SmsTemplateType smsTemplateType) throws SQLException;


    SmsTemplateType selectBySmsTemplateTypeName(@Param("corp_code") String corp_code, @Param("template_type_name") String template_type_name, @Param("isactive") String isactive) throws SQLException;

    SmsTemplateType selectBySmsTemplateTypeCode(@Param("corp_code") String corp_code, @Param("template_type_code") String template_type_code, @Param("isactive") String isactive) throws SQLException;

    List<SmsTemplateType> selectSmsTemplateTypeScreen(Map<String, Object> params) throws SQLException;

    List<SmsTemplateType> selectTemplateTypeCountByBrand(Map<String, Object> params) throws SQLException;

}
