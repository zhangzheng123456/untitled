package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.ActivityVip;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by nanji on 2016/11/15.
 */
public interface ActivityVipService {
    PageInfo<ActivityVip> selectAllActivity(int page_num, int page_size, String corp_code, String search_value) throws Exception;

    PageInfo<ActivityVip> selectActivityAllScreen(int page_num, int page_size, String corp_code, String role_ident, String user_code, Map<String, String> map) throws Exception;


    int delete(int id) throws Exception;

    String insert(String message, String user_id, HttpServletRequest request) throws Exception;


    String update(String message, String user_id, HttpServletRequest request) throws Exception;


    int updateActivityVip(ActivityVip activityVip) throws Exception;

    ActivityVip selectActivityById(int id) throws Exception;

    ActivityVip getActivityForId(String corp_code, String activity_theme, String run_mode, String created_date) throws Exception;

    ActivityVip selActivityByCode(String corp_code, String activity_code) throws Exception;
}
