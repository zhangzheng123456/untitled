package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.MessageQuickReply;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/9/10.
 */
public interface MessageQuickReplyService {

    MessageQuickReply getQuickReplyById(int id) throws Exception;


    PageInfo<MessageQuickReply> getAllQuickReplyByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    PageInfo<MessageQuickReply> getAllQuickReplyByPage(int page_number, int page_size, String corp_code, String search_value,String manager_corp) throws Exception;


    List<MessageQuickReply> getAllQuickReply(String corp_code) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;


    MessageQuickReply getQuickReplyByCode(String corp_code, String code, String isactive) throws Exception;

    PageInfo<MessageQuickReply> getAllQuickReplyScreen(int page_number, int page_size, String corp_code, String brand_codes,Map<String, String> map) throws Exception;

    PageInfo<MessageQuickReply> getAllQuickReplyScreen(int page_number, int page_size, String corp_code, String brand_codes,Map<String, String> map,String manager_corp) throws Exception;

    List<MessageQuickReply> selectQuickReplyByBrand(String corp_code, String brand_code,String search_value, String isactive) throws Exception;


}
