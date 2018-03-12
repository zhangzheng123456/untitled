package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.TaskType;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/7/21.
 */
public interface TaskTypeService {
    TaskType selectById(String id)throws Exception;

    List<TaskType> selectByCode(String corp_code, String task_type_code)throws Exception;

    PageInfo<TaskType> selectAllTaskType(int page_num, int page_size, String corp_code, String search_value)throws Exception;

    PageInfo<TaskType> selectAllTaskType(int page_num, int page_size, String corp_code, String search_value,String manager_corp)throws Exception;


    String insertTaskType(String message,String user_code)throws Exception;

    String updateTaskType(String message,String user_code)throws Exception;

    int deleteTaskType(int id)throws Exception;

    List<TaskType> codeExist(String corp_code,String task_type_code)throws Exception;

    List<TaskType> nameExist(String corp_code,String task_type_name)throws Exception;

    PageInfo<TaskType> selectAllTaskTypeScreen(int page_number, int page_size, String corp_code, Map<String, String> map)throws Exception;

    PageInfo<TaskType> selectAllTaskTypeScreen(int page_number, int page_size, String corp_code, Map<String, String> map,String manager_corp)throws Exception;


    TaskType getTaskTypeForId(String corp_code,String task_type_code)throws Exception;

    List<TaskType> selectCorpTaskType(String corp_code,String search_value) throws Exception;
}
