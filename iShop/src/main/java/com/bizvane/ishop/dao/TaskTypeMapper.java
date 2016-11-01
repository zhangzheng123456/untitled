package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.TaskType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/7/20.
 */
public interface TaskTypeMapper {

    TaskType selectById(@Param("id") int id);

    List<TaskType> selectByCode(@Param("corp_code") String corp_code,@Param("task_type_code") String task_type_code);

    List<TaskType> selectByName(@Param("corp_code") String corp_code,@Param("task_type_name") String task_type_name);

    List<TaskType> selectAllTaskType(@Param("corp_code") String corp_code,@Param("search_value") String search_value,@Param("isactive")String isactive);

    int insertTaskType(TaskType type);

    int updateTaskType(TaskType type);

    int deleteById(int id);
    TaskType getTaskTypeForId(@Param("corp_code") String corp_code,@Param("task_type_code") String task_type_code);
    List<TaskType> selectAllTaskTypeScreen(Map<String, Object> params);
}
