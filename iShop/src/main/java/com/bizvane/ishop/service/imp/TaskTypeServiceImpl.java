package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.TaskTypeMapper;
import com.bizvane.ishop.entity.TaskType;
import com.bizvane.ishop.service.TaskTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/7/21.
 */
@Service
public class TaskTypeServiceImpl implements TaskTypeService{

    @Autowired
    TaskTypeMapper taskTypeMapper;

    public TaskType selectById(String id){
        return taskTypeMapper.selectById(Integer.parseInt(id));
    }

    public List<TaskType> selectByCode(String corp_code,String task_type_code){
        return taskTypeMapper.selectByCode(corp_code,task_type_code);
    }

    public PageInfo<TaskType> selectAllTaskType(int page_num,int page_size,String corp_code, String search_value){
        PageHelper.startPage(page_num,page_size);
        List<TaskType> task_types = taskTypeMapper.selectAllTaskType(corp_code,search_value);
        PageInfo<TaskType> task= new PageInfo<TaskType>(task_types);
        return task;
    }

    public String insertTaskType(String message,String user_code){
        JSONObject jsonObject = new JSONObject(message);
        String task_type_code = jsonObject.get("task_type_code").toString();
        String task_type_name = jsonObject.get("task_type_name").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        List<TaskType> task_type1 = taskTypeMapper.selectByCode(corp_code,task_type_code);
        List<TaskType> task_type2 = taskTypeMapper.selectByName(corp_code,task_type_name);

        if (task_type1.size()>0){
            return "任务类型编号已存在";
        }
        if (task_type2.size()>0){
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
        task_type.setIsactive(jsonObject.get("isactive").toString());
        int result = taskTypeMapper.insertTaskType(task_type);
        if (result == 1) {
            return Common.DATABEAN_CODE_SUCCESS;
        }else {
            return "新增失败，请稍后再试";
        }
    }

    public String updateTaskType(String message,String user_code){
        JSONObject jsonObject = new JSONObject(message);
        int id = Integer.parseInt(jsonObject.get("id").toString());
        String task_type_code = jsonObject.get("task_type_code").toString();
        String task_type_name = jsonObject.get("task_type_name").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        TaskType task_type0 = taskTypeMapper.selectById(id);
        List<TaskType> task_type1 = taskTypeMapper.selectByCode(corp_code,task_type_code);
        List<TaskType> task_type2 = taskTypeMapper.selectByName(corp_code,task_type_name);

        if (task_type1.size()>0 &&
                !(task_type0.getTask_type_code().equals(task_type_code) && task_type0.getCorp_code().equals(corp_code))){
            return "任务类型编号已存在";
        }
        if (task_type2.size()>0 &&
                !task_type0.getTask_type_name().equals(task_type_name) && task_type0.getCorp_code().equals(corp_code)){
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
        task_type.setIsactive(jsonObject.get("isactive").toString());
        int result = taskTypeMapper.insertTaskType(task_type);
        if (result == 1) {
            return Common.DATABEAN_CODE_SUCCESS;
        }else {
            return "编辑失败，请稍后再试";
        }
    }

    public int deleteTaskType(int id){
        return taskTypeMapper.deleteById(id);
    }

    public List<TaskType> codeExist(String corp_code,String task_type_code){
        return taskTypeMapper.selectByCode(corp_code,task_type_code);
    }

    public List<TaskType> nameExist(String corp_code,String task_type_name){
        return taskTypeMapper.selectByName(corp_code,task_type_name);
    }
}
