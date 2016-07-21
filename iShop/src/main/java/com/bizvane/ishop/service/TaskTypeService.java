package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.TaskType;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Created by ZhouZhou on 2016/7/21.
 */
public interface TaskTypeService {
    TaskType selectById(String id);

    List<TaskType> selectByCode(String corp_code, String task_type_code);

    PageInfo<TaskType> selectAllTaskType(int page_num, int page_size, String corp_code, String search_value);

    String insertTaskType(String message,String user_code);

    String updateTaskType(String message,String user_code);

    int deleteTaskType(int id);

    List<TaskType> codeExist(String corp_code,String task_type_code);

    List<TaskType> nameExist(String corp_code,String task_type_name);
}
