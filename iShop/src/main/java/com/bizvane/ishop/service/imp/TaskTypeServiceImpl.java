package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.TaskMapper;
import com.bizvane.ishop.dao.TaskTypeMapper;
import com.bizvane.ishop.entity.Interfacers;
import com.bizvane.ishop.entity.TaskType;
import com.bizvane.ishop.service.TaskTypeService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/7/21.
 */
@Service
public class TaskTypeServiceImpl implements TaskTypeService {

    @Autowired
    TaskTypeMapper taskTypeMapper;
    @Autowired
    TaskMapper taskMapper;

    public TaskType selectById(String id)throws Exception {
        return taskTypeMapper.selectById(Integer.parseInt(id));
    }

    public List<TaskType> selectByCode(String corp_code, String task_type_code)throws Exception  {
        return taskTypeMapper.selectByCode(corp_code, task_type_code);
    }

    public PageInfo<TaskType> selectAllTaskType(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_num, page_size);
        List<TaskType> task_types = taskTypeMapper.selectAllTaskType(corp_code, search_value,"",null);
        for (TaskType taskType:task_types) {
            taskType.setIsactive(CheckUtils.CheckIsactive(taskType.getIsactive()));
        }
        PageInfo<TaskType> task = new PageInfo<TaskType>(task_types);
        return task;
    }

    public PageInfo<TaskType> selectAllTaskType(int page_num, int page_size, String corp_code, String search_value,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        PageHelper.startPage(page_num, page_size);
        List<TaskType> task_types = taskTypeMapper.selectAllTaskType(corp_code, search_value,"",manager_corp_arr);
        for (TaskType taskType:task_types) {
            taskType.setIsactive(CheckUtils.CheckIsactive(taskType.getIsactive()));
        }
        PageInfo<TaskType> task = new PageInfo<TaskType>(task_types);
        return task;
    }
    public String insertTaskType(String message, String user_code) throws Exception{
        JSONObject jsonObject = new JSONObject(message);
        String task_type_code = jsonObject.get("task_type_code").toString().trim();
        String task_type_name = jsonObject.get("task_type_name").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        List<TaskType> task_type1 = taskTypeMapper.selectByCode(corp_code, task_type_code);
        List<TaskType> task_type2 = taskTypeMapper.selectByName(corp_code, task_type_name);

        if (task_type1.size() > 0) {
            return "任务类型编号已存在";
        }
        if (task_type2.size() > 0) {
            return "任务类型名称已存在";
        }
        TaskType task_type = new TaskType();
        task_type.setTask_type_code(task_type_code);
        task_type.setTask_type_name(task_type_name);
        task_type.setCorp_code(corp_code);
        Date now = new Date();
        task_type.setCreated_date(Common.DATETIME_FORMAT.format(now));
        task_type.setCreater(user_code);
        task_type.setModified_date(Common.DATETIME_FORMAT.format(now));
        task_type.setModifier(user_code);
        task_type.setIsactive(jsonObject.get("isactive").toString().trim());
        int result = taskTypeMapper.insertTaskType(task_type);
        if (result == 1) {
            TaskType taskType1=this.getTaskTypeForId(task_type.getCorp_code(),task_type.getTask_type_code());

            return String.valueOf(taskType1.getId());
        } else {
            return "新增失败，请稍后再试";
        }
    }

    public String updateTaskType(String message, String user_code) throws Exception {
        JSONObject jsonObject = new JSONObject(message);
        int id = Integer.parseInt(jsonObject.get("id").toString());
        String task_type_code = jsonObject.get("task_type_code").toString().trim();
        String task_type_name = jsonObject.get("task_type_name").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        TaskType task_type0 = taskTypeMapper.selectById(id);
        List<TaskType> task_type1 = taskTypeMapper.selectByCode(corp_code, task_type_code);
        List<TaskType> task_type2 = taskTypeMapper.selectByName(corp_code, task_type_name);

        if (task_type1.size() > 0 &&
                !(task_type0.getTask_type_code().trim().equals(task_type_code) && task_type0.getCorp_code().trim().equals(corp_code))) {
            return "任务类型编号已存在";
        }
        if (task_type2.size() > 0 &&
                !task_type0.getTask_type_name().trim().equals(task_type_name) && task_type0.getCorp_code().trim().equals(corp_code)) {
            return "任务类型名称已存在";
        }
        TaskType task_type = new TaskType();
        task_type.setId(id);
        task_type.setTask_type_code(task_type_code);
        task_type.setTask_type_name(task_type_name);
        task_type.setCorp_code(corp_code);
        Date now = new Date();
        task_type.setCreated_date(Common.DATETIME_FORMAT.format(now));
        task_type.setCreater(user_code);
        task_type.setModified_date(Common.DATETIME_FORMAT.format(now));
        task_type.setModifier(user_code);
        task_type.setIsactive(jsonObject.get("isactive").toString().trim());
        taskMapper.updTaskBycode(task_type_code,corp_code,task_type0.getTask_type_code().trim());
        int result = taskTypeMapper.updateTaskType(task_type);
        //修改任务下面的任务类型
        if (result == 1) {
            return Common.DATABEAN_CODE_SUCCESS;
        } else {
            return "编辑失败，请稍后再试";
        }
    }

    public int deleteTaskType(int id)throws Exception  {
        return taskTypeMapper.deleteById(id);
    }

    public List<TaskType> codeExist(String corp_code, String task_type_code) throws Exception {
        return taskTypeMapper.selectByCode(corp_code, task_type_code);
    }

    public List<TaskType> nameExist(String corp_code, String task_type_name)throws Exception  {
        return taskTypeMapper.selectByName(corp_code, task_type_name);
    }

    @Override
    public PageInfo<TaskType> selectAllTaskTypeScreen(int page_number, int page_size, String corp_code, Map<String, String> map)throws Exception  {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<TaskType> list = taskTypeMapper.selectAllTaskTypeScreen(params);
        for (TaskType taskType:list) {
            taskType.setIsactive(CheckUtils.CheckIsactive(taskType.getIsactive()));
        }
        PageInfo<TaskType> page = new PageInfo<TaskType>(list);
        return page;
    }

    @Override
    public PageInfo<TaskType> selectAllTaskTypeScreen(int page_number, int page_size, String corp_code, Map<String, String> map,String manager_corp)throws Exception  {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<TaskType> list = taskTypeMapper.selectAllTaskTypeScreen(params);
        for (TaskType taskType:list) {
            taskType.setIsactive(CheckUtils.CheckIsactive(taskType.getIsactive()));
        }
        PageInfo<TaskType> page = new PageInfo<TaskType>(list);
        return page;
    }

    @Override
    public TaskType getTaskTypeForId(String corp_code, String task_type_code) throws Exception {
        return taskTypeMapper.getTaskTypeForId(corp_code, task_type_code);
    }

    @Override
    public List<TaskType> selectCorpTaskType(String corp_code, String search_value) throws Exception {
        List<TaskType> taskTypes;
        taskTypes = taskTypeMapper.selectTaskTypeSByCorp(corp_code,search_value);

        return taskTypes;

    }
}
