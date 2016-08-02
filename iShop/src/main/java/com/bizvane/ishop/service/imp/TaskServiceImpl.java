package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.TaskMapper;
import com.bizvane.ishop.dao.TaskTypeMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.dao.UserMessageMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.TaskService;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.apache.velocity.runtime.directive.Foreach;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.System;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yin on 2016/7/27.
 */

@Service
public class TaskServiceImpl implements TaskService{
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskTypeMapper typeMapper;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    private UserMessageMapper userMessageMapper;
    @Autowired
    private UserMapper userMapper;
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
    public TaskAllocation selTaskAllocationById(String id) {
        return taskMapper.selTaskAllocationById(id);
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
            count += taskMapper.delTaskById(id);
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
    public String addTask(Task task, String[] user_codes,String phone,String users,String user_code) {
        int count=0;
        try {
            count+=taskMapper.addTask(task);
           for(int i=0;i<user_codes.length;i++){
               TaskAllocation allocation=new TaskAllocation();
               allocation.setCorp_code(task.getCorp_code());
               allocation.setTask_code(task.getTask_code());
               allocation.setUser_code(user_codes[i]);
               allocation.setTask_status("1");
               allocation.setReal_start_time("");
               allocation.setReal_end_time("");
               Date now = new Date();
               allocation.setCreated_date(Common.DATETIME_FORMAT.format(now));
               allocation.setCreater(user_code);
               allocation.setModified_date(Common.DATETIME_FORMAT.format(now));
               allocation.setModifier(user_code);
               count += taskMapper.addTaskAllocation(allocation);
           }
            if(count>0){
                Data data_phone = new Data("phone", phone, ValueType.PARAM);
                Data data_corp_code = new Data("corp_code", task.getCorp_code(), ValueType.PARAM);
                Data data_task_code = new Data("task_code", task.getTask_code(), ValueType.PARAM);
                Data data_task_title = new Data("task_title", task.getTask_title(), ValueType.PARAM);
                Data data_user = new Data("user", users, ValueType.PARAM);
                Data data_user_id = new Data("user_id", user_code, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_phone.key, data_phone);
                datalist.put(data_corp_code.key, data_corp_code);
                datalist.put(data_task_code.key, data_task_code);
                datalist.put(data_task_title.key, data_task_title);
                datalist.put(data_user.key, data_user);
                datalist.put(data_user_id.key, data_user_id);
                DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.TaskNotice", datalist);
                String msg = dataBox.data.get("message").value;
                System.out.println(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return count+"";
    }

    @Override
    @Transactional
    public String updTask(Task task, String[] user_codes,String user_code) {
         int count =0;
         int appCount=0;
        try{
            count += taskMapper.updTask(task);
            TaskAllocation allocation=new TaskAllocation();
            String id="";
            String phone="";
            String users="";
            List<TaskAllocation> taskAllocations = taskMapper.selAllTaskAllocation(task.getCorp_code(), task.getTask_code());
            for (int i=0;i<user_codes.length;i++) {
                TaskAllocation taskAllocation = taskMapper.selAllTaskAllocationByUser(task.getCorp_code(),task.getTask_code(),user_codes[i]);
                id=id+taskAllocation.getId()+",";
                if(taskAllocation==null){
                    allocation.setCorp_code(task.getCorp_code());
                    allocation.setTask_code(task.getTask_code());
                    allocation.setUser_code(user_codes[i]);
                    User user = userMapper.selectUserCode(user_codes[i], task.getCorp_code());
                    String userPhone = user.getPhone();
                    phone = phone + userPhone+",";
                    users = users + user_codes[i];
                    System.out.println(phone+"----"+users);
                    allocation.setTask_status("1");
                    allocation.setReal_start_time("");
                    allocation.setReal_end_time("");
                    Date now = new Date();
                    allocation.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    allocation.setCreater(user_code);
                    allocation.setModified_date(Common.DATETIME_FORMAT.format(now));
                    allocation.setModifier(user_code);
                    count+=taskMapper.addTaskAllocation(allocation);
                    appCount = count;
                }else{
                    allocation.setCorp_code(task.getCorp_code());
                    allocation.setTask_code(task.getTask_code());
                    allocation.setUser_code(user_codes[i]);
                    allocation.setTask_status("1");
                    allocation.setReal_start_time("");
                    allocation.setReal_end_time("");
                    Date now = new Date();
                    allocation.setModified_date(Common.DATETIME_FORMAT.format(now));
                    allocation.setModifier(user_code);
                    allocation.setId(taskAllocation.getId());
                  count+=taskMapper.updTaskAllocation(allocation);
                }
            }
            for (int i=0;i<taskAllocations.size();i++){
                if(!id.contains(taskAllocations.get(i).getId()+"")){
                  count+=taskMapper.delTaskAllocationById(taskAllocations.get(i).getId()+"");
                }
            }
            if(appCount>0){
                Data data_phone = new Data("phone", phone, ValueType.PARAM);
                Data data_corp_code = new Data("corp_code", task.getCorp_code(), ValueType.PARAM);
                Data data_task_code = new Data("task_code", task.getTask_code(), ValueType.PARAM);
                Data data_task_title = new Data("task_title", task.getTask_title(), ValueType.PARAM);
                Data data_user = new Data("user_code", users, ValueType.PARAM);
                Data data_user_id = new Data("user_id", user_code, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_phone.key, data_phone);
                datalist.put(data_corp_code.key, data_corp_code);
                datalist.put(data_task_code.key, data_task_code);
                datalist.put(data_task_title.key, data_task_title);
                datalist.put(data_user.key, data_user);
                datalist.put(data_user_id.key, data_user_id);
                DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.TaskNotice", datalist);
                String msg = dataBox.data.get("message").value;
                System.out.println(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return count+"";
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

    @Override
    public List<TaskType> selectAllTaskType(String corp_code) {
        return typeMapper.selectAllTaskType(corp_code,"");
    }
}
