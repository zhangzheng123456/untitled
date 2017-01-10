package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.VipActivity;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by nanji on 2016/11/15.
 */
public interface VipActivityService {
    /**
     * @param page_num
     * @param page_size
     * @param corp_code
     * @param user_code
     * @param search_value
     * @return
     * @throws Exception
     */
    PageInfo<VipActivity> selectAllActivity(int page_num, int page_size, String corp_code, String user_code, String search_value) throws Exception;

    /**
     * @param page_num
     * @param page_size
     * @param corp_code
     * @param user_code
     * @param map
     * @return
     * @throws Exception
     */
    PageInfo<VipActivity> selectActivityAllScreen(int page_num, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception;

    /**
     * @param id
     * @return
     * @throws Exception
     */
    int delete(int id) throws Exception;

    /**
     * @param message
     * @param user_id
     * @return
     * @throws Exception
     */
    String insert(String message, String user_id) throws Exception;

    /**
     * @param message
     * @param user_id

     * @return
     * @throws Exception
     */
    String update(String message, String user_id) throws Exception;

    /**
     * @param activityVip
     * @return
     * @throws Exception
     */
    int updateVipActivity(VipActivity activityVip) throws Exception;

    /**
     * @param id
     * @return
     * @throws Exception
     */
    VipActivity selectActivityById(int id) throws Exception;

    /**
     * @param id
     * @return
     * @throws Exception
     */
    VipActivity getActivityById(int id) throws Exception;

    /**
     * @param activity_code
     * @return
     * @throws Exception
     */
    VipActivity selActivityByCode(String activity_code) throws Exception;


    /**
     * @param corp_code
     * @param activity_code
     * @param task_code
     * @return
     * @throws Exception
     */
    JSONObject executeDetail(String corp_code,String activity_code,String task_code) throws Exception;

    ArrayList userExecuteDetail(String corp_code, String activity_vip_code, String user_code) throws Exception;


    int updActiveCodeByType(String line_code, String line_value,String corp_code, String activity_code)throws Exception;

    VipActivity getVipActivityByTheme(String corp_code,String activity_theme,String isactive)throws Exception;


    String executeActivity(VipActivity vipActivity,String user_code) throws Exception;
}
