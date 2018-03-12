package com.bizvane.ishop.service;


import com.bizvane.ishop.entity.AppIcon;

import com.github.pagehelper.PageInfo;


/**
 * Created by nanji on 2016/11/15.
 */
public interface AppIconService {

    AppIcon getAppIconById(int id) throws Exception;

    PageInfo<AppIcon> selectAllIcons(int page_num, int page_size, String search_value) throws Exception;

    String update(String message, String user_id) throws Exception;

    AppIcon getAppIconByName(String isactive, String  icon_name) throws Exception;

    String insert(String message, String user_id) throws Exception;

    void updateShowOrder(String icon_name, String show_order) throws Exception;

}
