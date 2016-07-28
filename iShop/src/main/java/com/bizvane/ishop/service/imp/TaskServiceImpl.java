package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.TaskMapper;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.TaskAllocation;
import com.bizvane.ishop.entity.TaskType;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.TaskService;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/7/27.
 */

@Service
public class TaskServiceImpl implements TaskService{
    @Autowired
    private TaskMapper taskMapper;
    @Override
    public PageInfo<Task> selectAllTask(int page_num, int page_size, String corp_code, String role_ident, String user_code, String search_value) {

//        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
//        Data data_role_ident = new Data("role_ident", role_ident, ValueType.PARAM);
//        Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);
//        Data data_search_value = new Data("search_value", search_value, ValueType.PARAM);
//        Map datalist = new HashMap<String, Data>();
//        datalist.put(data_corp_code.key, data_corp_code);
//        datalist.put(data_role_ident.key, data_role_ident);
//        datalist.put(data_user_code.key, data_user_code);
//        datalist.put(data_search_value.key, data_search_value);
//
//        DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.SendSMS", datalist);
//        logger.info("SendSMSMethod -->" + dataBox.data.get("message").value);
//        String msg = dataBox.data.get("message").value;
//        JSONArray array =new JSONArray(msg);
//        PageHelper.startPage(page_number, page_size);
//        List<Task> list = WebUtils.Json2List(array);
//        PageInfo<Task> page = new PageInfo<Task>(list);
        PageHelper.startPage(page_num, page_size);
        List<Task> tasks = taskMapper.selectAllTask(corp_code, role_ident, user_code, search_value);
        PageInfo<Task> page= new PageInfo<Task>(tasks);
        return page;
    }

    @Override
    public PageInfo<Task> selectSignAllScreen(int page_num, int page_size, String corp_code, String role_ident, String user_code, Map<String, String> map) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map",map);
        params.put("corp_code",corp_code);
        params.put("role_ident",role_ident);
        params.put("user_code",user_code);
        PageHelper.startPage(page_num, page_size);
        List<Task> tasks = taskMapper.selectAllTaskScreen(params);
        PageInfo<Task> page= new PageInfo<Task>(tasks);
        return page;
    }

    @Override
    @Transactional
    public String delTask(String id, String corp_code, String task_code) {
        int count=0;
        try {
            count = taskMapper.delTaskById(id);
            List<TaskAllocation> taskAllocations = taskMapper.selAllTaskAllocation(corp_code, task_code);
            for (TaskAllocation taskAllocation : taskAllocations) {
               count += taskMapper.delTaskAllocationById(taskAllocation.getId() + "");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return count+"";
    }

    @Override
    @Transactional
    public String addTask(Task task, String[] user_codes) {
        int count=0;
        try {
            count=taskMapper.addTask(task);
           for(int i=0;i<user_codes.length;i++){
               TaskAllocation allocation=new TaskAllocation();
               allocation.setCorp_code(task.getCorp_code());
               allocation.setTask_code(task.getTask_code());
               allocation.setUser_code(user_codes[i]);
               allocation.setTask_status("1");
               allocation.setReal_start_time("");
               allocation.setReal_end_time("");
              count += taskMapper.addTaskAllocation(allocation);
           }
        }catch (Exception e){
            e.printStackTrace();
        }
        return count+"";
    }

    @Override
    public String updTask(Task task, String[] user_codes) {
        return null;
    }

    @Override
    public Task selectTaskById(String id) {
        Task task = taskMapper.selTaskById(id);
        return task;
    }

    @Override
    public List<TaskAllocation> selTaskAllocation(String corp_code, String task_code) {
        return taskMapper.selAllTaskAllocation(corp_code,task_code);
    }

    @Override
    public String delTaskAllocation(String id) {
        return null;
    }
}
