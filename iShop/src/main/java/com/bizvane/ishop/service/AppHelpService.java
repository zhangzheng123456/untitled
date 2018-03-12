package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.AppHelp;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/2/21.
 */
public interface AppHelpService {

    AppHelp getAppHelpById(int id) throws Exception;

    PageInfo<AppHelp> getAppHelpByPage(int page_number, int page_size, String search_value) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<AppHelp> getAppHelpScreen(int page_number, int page_size, Map<String, String> map) throws Exception;

    AppHelp getAppHelpByName(String vip_card_type_name, String isactive) throws Exception;

    List<AppHelp> getAppHelps( String isactive) throws Exception;

    void updateShowOrder(int id,String show_order) throws Exception ;
}
