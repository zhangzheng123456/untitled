package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.SmsTemplate;
import com.bizvane.ishop.entity.SmsTemplateType;
import com.bizvane.ishop.entity.VipGroup;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/9/7.
 */
public interface SmsTemplateTypeService {

    SmsTemplateType  getSmsTemplateTypeById(int id) throws Exception;

    PageInfo<SmsTemplateType> getAllSmsTemplateTypeByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    PageInfo<SmsTemplateType> getAllSmsTemplateTypeByPage(int page_number, int page_size, String corp_code, String search_value,String manager_corp) throws Exception;


    List<SmsTemplateType> getAllSmsTemplateType(String corp_code) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    SmsTemplateType getSmsTemplateTypeByCode(String corp_code, String code,String isactive) throws Exception;

    SmsTemplateType   getSmsTemplateTypeByName(String corp_code, String name,String isactive) throws Exception;

    PageInfo<SmsTemplateType> getAllSmsTemplateTypeScreen(int page_number, int page_size, String corp_code, String brand_codes, Map<String, String> map) throws Exception;

    PageInfo<SmsTemplateType> getAllSmsTemplateTypeScreen(int page_number, int page_size, String corp_code,  String brand_codes,Map<String, String> map,String manager_corp) throws Exception;


    List<SmsTemplate> selectByTemplateType(String corp_code, String template_type) throws Exception;

    List<SmsTemplateType> selectTemplateTypeCountByBrand(String corp_code, String brand_code,String search_value, String isactive) throws Exception;

}
