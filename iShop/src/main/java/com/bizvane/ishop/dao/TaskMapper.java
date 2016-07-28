package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.TaskAllocation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yin on 2016/7/21.
 */
public interface TaskMapper {
        List<Task> selectAllTask(@Param("corp_code")String corp_code,@Param("role_ident")String role_ident,@Param("user_code")String user_code,@Param("search_value")String search_value);

        Task selTaskById(@Param("id")String id);

        List<TaskAllocation> selAllTaskAllocation(@Param("corp_code")String corp_code,@Param("task_code")String task_code);

        int delTaskById(@Param("id")String id);

        int delTaskAllocation(@Param("corp_code")String corp_code,@Param("task_code")String task_code);

        int delTaskAllocationById(@Param("id")String id);

        int addTask(Task task);

        int addTaskAllocation(TaskAllocation taskAllocation);

        int updTask(Task task);

        int updTaskAllocation(TaskAllocation taskAllocation);

}
