package com.bizvane.ishop.service;


import com.bizvane.ishop.entity.WxTemplate;

import java.util.List;

/**
 * Created by zhou on 2016/6/1.
 *
 * @@version
 */
public interface WxTemplateService {

    List<WxTemplate> selectAllWxTemplate(String corp_code, String search_value) throws Exception;

    int insertWxTemplate(WxTemplate template) throws Exception;

    int updateWxTemplate(WxTemplate template) throws Exception;

    int deleteWxTemplate(int id) throws Exception;
}
