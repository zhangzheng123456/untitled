package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.VipTask;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/4/24.
 */
public interface VipTaskService {

    VipTask selectById(int id) throws  Exception;

    List<VipTask> selectByTaskTitle(String corp_code,String task_title) throws  Exception;

    VipTask selectByTaskCode(String task_code) throws Exception;

    PageInfo<VipTask> selectAll(int page_num,int page_size,String corp_code,String search_value) throws  Exception;

    List<VipTask> selectAllByStatus(String corp_code, String search_value, String status, String is_advance_show) throws Exception;

    int deleteById(int id) throws  Exception;

    int deleteByCode(String vip_task_code) throws Exception;

    VipTask selectTargetCount(VipTask vipTask) throws Exception;

    String inserVipTask(VipTask vipTask,String user_code,String group_code,String role_code) throws Exception;

    String updateVipTask(VipTask vipTask,String user_code,String group_code,String role_code)throws  Exception;

    String executeVipTask(VipTask vipTask,String user_code,String group_code,String role_code) throws Exception;

    void update(VipTask vipTask) throws Exception;

    PageInfo<VipTask> selectAllScreen(int page_num,int page_size,String corp_code,Map<String, Object> map)throws Exception;

    int updateVipTaskSchedule(VipTask task, String card_no,String app_id,String open_id, JSONObject vip, String status,JSONArray schedule) throws Exception;

    VipTask vipTaskSchedule(VipTask vipTask,String card_no,JSONObject vip_info,int flag,String app_id,String open_id) throws Exception;

    public PageInfo<VipTask> switchTaskType(PageInfo<VipTask> list) throws Exception;

    void sendPresent(VipTask vipTask,String corp_code,String vip_id,String app_id,String open_id,String desc) throws Exception;

    List<VipTask> selectAllByStatus(String corp_code) throws Exception;

    List<VipTask> selectMobileShow(String corp_code,String app_id,String status,String is_advance_show) throws Exception;

    List<VipTask> selectVipTaskByTaskType(String corp_code,String task_type)throws Exception;

    VipTask switchTaskType(VipTask vipTask, HttpServletRequest request) throws Exception;

    VipTask selectVipTaskByTaskTypeAndTitle(String corp_code,String task_type,String activity_code) throws Exception;

    Object swicthStatus(String examine_type, Object o) throws Exception;
}
