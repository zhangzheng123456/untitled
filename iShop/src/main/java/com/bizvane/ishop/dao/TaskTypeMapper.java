package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.TaskType;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/7/20.
 */
public interface TaskTypeMapper {

    TaskType selectById(@Param("id") int id)throws SQLException;

    List<TaskType> selectByCode(@Param("corp_code") String corp_code,@Param("task_type_code") String task_type_code)throws SQLException;

    List<TaskType> selectByName(@Param("corp_code") String corp_code,@Param("task_type_name") String task_type_name)throws SQLException;

    List<TaskType> selectAllTaskType(@Param("corp_code") String corp_code,@Param("search_value") String search_value,@Param("isactive")String isactive,@Param("manager_corp_arr")String[] manager_corp_arr)throws SQLException;

    int insertTaskType(TaskType type)throws SQLException;

    int updateTaskType(TaskType type)throws SQLException;

    int deleteById(int id)throws SQLException;

    TaskType getTaskTypeForId(@Param("corp_code") String corp_code,@Param("task_type_code") String task_type_code)throws SQLException;

    List<TaskType> selectAllTaskTypeScreen(Map<String, Object> params)throws SQLException;

    List<TaskType> selectTaskTypeSByCorp(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;




}
