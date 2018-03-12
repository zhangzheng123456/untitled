package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.VipActivity;
import com.bizvane.ishop.entity.VipActivityDetail;
import com.github.pagehelper.PageInfo;

import java.util.List;
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
    VipActivity getActivityById(int id) throws Exception;

    /**
     * @param activity_code
     * @return
     * @throws Exception
     */
    VipActivity selActivityByCode(String activity_code) throws Exception;

    VipActivity getActivityByCode(String activity_vip_code) throws Exception;

    VipActivity selActivityByCodeAndName(String activity_code) throws Exception;

    List<Store> getActivityStore(String corp_code,String run_scope) throws Exception;


    List<Task> unComplTask(String corp_code, String activity_code) throws Exception;

    String allocUnComplTask(String corp_code,String activity_code,String task_code,String store_codes,String user_code) throws Exception;

    String getUnAllocStore(VipActivity vipActivity) throws Exception;

    int updActiveCodeByType(String line_code, String line_value,String corp_code, String activity_code)throws Exception;

    VipActivity getVipActivityByTheme(String corp_code,String activity_theme)throws Exception;

    void creatVipRules(String activity_code, String corp_code) throws Exception;

    void insertSchedule2(String activity_code, String corp_code, String user_code) throws Exception;

    void insertSchedule4(String activity_code, String corp_code, String user_code,String time) throws Exception;

    String saveActivity(VipActivity vipActivity, String user_code,String create_task,String group_code,String role_code) throws Exception;

    String executeActivity(VipActivity vipActivity,String user_code,String create_task,String group_code,String role_code) throws Exception;

    String executeTask(VipActivity vipActivity,String user_code) throws Exception;

    String allocTask(String corp_code,String activity_code,String store_code1, String user_code,String task_code) throws Exception;

    String executeFsend(VipActivity vipActivity,String sms_code,String user_code) throws Exception;

    void insertSchedule(String activity_code,String corp_code,String end_time,String user_code,String flag)throws Exception;

    void terminalAct(VipActivity vipActivity,String user_code) throws Exception;

    String activityApply(VipActivityDetail detail, String phone, String vip_id, String corp_code) throws Exception;

    void insertOpenUrlRecord(String activity_code,String user_code,String store_code,String open_id,String corp_code,String app_id) throws Exception;

   List<VipActivity>  getVipActivityByAppid(String app_id,String activity_state,String run_mode) throws Exception;

    List<VipActivity>  getVipActivityByCorpCode(String corp_code,String activity_state,String run_mode) throws Exception;

    List<VipActivity> selectAllActivityByState(String corp_code,String user_code) throws Exception;
}
