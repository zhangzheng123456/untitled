package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/4/24.
 */
public interface VipTaskMapper {

    VipTask selectById(int id) throws  Exception;

    List<VipTask> selectByTaskCode(@Param("task_code")String task_code) throws  Exception;

    List<VipTask> selectByTaskTitle(@Param("corp_code")String corp_code,@Param("task_title")String task_title) throws  Exception;

    List<VipTask> selectAll(@Param("corp_code")String corp_code, @Param("search_value")String search_value, @Param("status")String status, @Param("is_advance_show")String is_advance_show) throws  Exception;

    int deleteById(int id) throws  Exception;

    int deleteByCode(@Param("task_code")String task_code) throws  Exception;

    int inserVipTask(VipTask vipTask) throws Exception;

    int updateVipTask(VipTask vipTask)throws  Exception;

    List<VipTask> selectAllScreen(Map<String, Object> map)throws Exception;

    List<VipTask> selectMobileShow(@Param("corp_code")String corp_code, @Param("app_id")String app_id, @Param("status")String status, @Param("is_advance_show")String is_advance_show) throws  Exception;

    List<VipTask> selectAllByStatus(@Param("corp_code")String corp_code) throws Exception;

    List<VipTask> selectVipTaskByTaskType(@Param("corp_code") String corp_code,@Param("task_type")String task_type)throws Exception;

    VipTask selectVipTaskByTaskTypeAndTitle(@Param("corp_code") String corp_code,@Param("task_type")String task_type,@Param("task_condition") String task_condition) throws Exception;

}
