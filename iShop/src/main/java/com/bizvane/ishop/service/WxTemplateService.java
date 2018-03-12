package com.bizvane.ishop.service;


import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.WxTemplate;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by zhou on 2016/6/1.
 *
 * @@version
 */
public interface WxTemplateService {

    WxTemplate getTemplateById(int id) throws Exception;

    List<WxTemplate> selectAllWxTemplate(String corp_code, String search_value) throws Exception;

    int insertWxTemplate(WxTemplate template) throws Exception;

    int updateWxTemplate(WxTemplate template) throws Exception;

    int deleteWxTemplate(int id) throws Exception;

    List<WxTemplate> selectTempByAppId(String app_id,String app_user_name, String template_name) throws Exception;

    String sendTemplateMsg(String app_id, String open_id, String template_id, JSONObject msg_content, String template_url) throws Exception;

    JSONObject inviteRegistralNotice(String app_id,String vip_name,String card_no,JSONObject invite_vipInfo) throws Exception;

    JSONObject newVipTaskNotice(String app_id,String task_title) throws Exception;

    JSONObject vipBirthNotice(String app_id,JSONObject vip) throws Exception;

    JSONObject vipCardTypeChangeNotice(String app_id,String card_no,String old_card_type,String card_type,String change_time,String point,String desc,String vip_name) throws Exception;

    PageInfo<WxTemplate> selectAllWxTemplate(int page_num, int page_size, String corp_code, String search_value) throws Exception;

    PageInfo<WxTemplate> selectWxTemplateAllScreen(int page_number, int page_size,String corp_code, Map<String,String> map) throws Exception;

    WxTemplate selectByIdAndName(String app_id,String template_id,String template_name) throws Exception;

    JSONObject getAllTemplateName() throws  Exception;

    List<WxTemplate> selectByCorpCode(String corp_code,String app_id) throws Exception;
}
