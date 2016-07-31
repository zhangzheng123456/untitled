package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.TaskAllocation;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/7/27.
 */
public interface TaskService {
    PageInfo<Task> selectAllTask(int page_num, int page_size,String corp_code,String role_ident,String user_code,String search_value);

    PageInfo<Task> selectSignAllScreen(int page_num, int page_size, String corp_code, String role_ident, String user_code, Map<String,String> map);


    String delTask(String id,String corp_code,String task_code);

    String addTask(Task task,String[] user_codes);

    String updTask(Task task,String[] user_codes);

    Task selectTaskById(String id);

    TaskAllocation selTaskAllocationById(String id);

    List<TaskAllocation> selTaskAllocation(String corp_code,String task_code);

    String delTaskAllocation(String id);
}
