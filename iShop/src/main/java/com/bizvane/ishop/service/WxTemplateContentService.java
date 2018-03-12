package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.WxTemplateContent;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/6/27.
 */
public interface WxTemplateContentService {

    WxTemplateContent selectById(int id) throws Exception;

    PageInfo<WxTemplateContent> selectAllWxTemplateContent(int page_num,int page_size,String corp_code, String search_value) throws Exception;

    PageInfo<WxTemplateContent> selectWxTemplateContentScreen(int page_number, int page_size,String corp_code,Map<String,Object> map) throws Exception;

    int insertWxTemplateContent(WxTemplateContent wxTemplateContent) throws Exception;

    int updateWxTemplateContent(WxTemplateContent wxTemplateContent) throws Exception;

    int deleteWxTemplateContent(int id) throws Exception;

    List<WxTemplateContent> selectByName(String app_id,String template_name) throws Exception;

    List<WxTemplateContent> selectContentById(String app_id, String template_name, String type,String vip_card_type) throws Exception;

    List<WxTemplateContent> selectContentByCardId(String app_id, String template_name, String type,String vip_card_type_id) throws Exception;


}
