package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.TaskAllocation;
import com.bizvane.ishop.entity.TaskType;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/7/27.
 */
public interface TaskService {
    PageInfo<Task> selectAllTask(int page_num, int page_size, String corp_code, String role_ident, String user_code, String search_value) throws Exception;

    PageInfo<Task> selectAllTask(int page_num, int page_size, String corp_code, String role_ident, String user_code, String search_value,String manager_corp) throws Exception;


    PageInfo<Task> selectSignAllScreen(int page_num, int page_size, String corp_code, String role_ident, String user_code, Map<String, String> map) throws Exception;

    PageInfo<Task> selectSignAllScreen(int page_num, int page_size, String corp_code, String role_ident, String user_code, Map<String, String> map,String manager_corp) throws Exception;

    List<TaskAllocation> selTaskAllocation(String corp_code, String task_code) throws Exception;

    List<TaskAllocation> selectAllTaskByTaskCode(String[] taskCodes)throws Exception;

    String delTask(String id, String corp_code, String task_code) throws Exception;

    String addTask(Task task, String phone, String users, String user_code,String activity_vip_code) throws Exception;

    String taskAllocation(Task task,String phone,String users,String stores,String user_code,String activity_code) throws Exception;

    String taskAllocation1(Task task, JSONArray array, String user_code, String activity_code) throws Exception;

    String updTask(Task task, String[] user_codes, String user_code) throws Exception;

    Task selectTaskById(String id) throws Exception;

    Task getTaskForId(String corp_code, String task_code) throws Exception;

    TaskAllocation selTaskAllocationById(String id) throws Exception;

    PageInfo<TaskAllocation> selectTaskAllocationByPage(int page_num,int page_size,String corp_code, String task_code) throws Exception;

    String delTaskAllocation(String id) throws Exception;

    List<TaskType> selectAllTaskType(String corp_code) throws Exception;

    List<Task> selectTaskByTaskType(String corp_code, String task_type_code) throws Exception;

    List<Task> getTaskByActivityCode(String corp_code,String activity_vip_code)throws Exception;

    int delTaskByActivityCode(String corp_code,String activity_vip_code)throws Exception;

     int insertTask(Task task) throws Exception;

    List<TaskAllocation> selUserComTaskCount(String corp_code, String store_code, String task_status) throws Exception;

    List<TaskAllocation> selUserTask(String corp_code, String task_code, String user_code) throws Exception;
    TaskAllocation selTaskByUser(String corp_code, String task_code, String user_code) throws Exception;

    PageInfo<TaskAllocation> selTaskStore(int page_num,int page_size,String corp_code,String search_value) throws Exception;

    List<TaskAllocation> selStoreComTaskCount(String corp_code,String store_code, String task_status) throws Exception;

    PageInfo<TaskAllocation> selectAllTaskByUsers(int page_num, int page_size,String corp_code, String task_code,String[] userCodes)throws Exception;
    PageInfo<TaskAllocation> selectAllTaskByStores(int page_num, int page_size,String corp_code, String task_code,String[] storeCodes)throws Exception;

}
