package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.dao.TaskMapper;
import com.bizvane.ishop.dao.TaskTypeMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.TaskService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.System;
import java.util.*;

/**
 * Created by yin on 2016/7/27.
 */

@Service
public class   TaskServiceImpl implements TaskService{
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
     TaskTypeMapper typeMapper;
    @Autowired
    StoreMapper storeMapper;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StoreService storeService;
    @Override
    public PageInfo<Task> selectAllTask(int page_num, int page_size, String corp_code, String role_ident, String user_code, String search_value) {
        PageHelper.startPage(page_num, page_size);
        List<Task> tasks = taskMapper.selectAllTask(corp_code, role_ident, user_code, search_value,null);
        for (Task task:tasks) {
            task.setIsactive(CheckUtils.CheckIsactive(task.getIsactive()));
        }
        PageInfo<Task> page= new PageInfo<Task>(tasks);
        return page;
    }
    @Override
    public PageInfo<Task> selectAllTask(int page_num, int page_size, String corp_code, String role_ident, String user_code, String search_value,String manager_corp) {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        PageHelper.startPage(page_num, page_size);
        List<Task> tasks = taskMapper.selectAllTask(corp_code, role_ident, user_code, search_value,manager_corp_arr);
        for (Task task:tasks) {
            task.setIsactive(CheckUtils.CheckIsactive(task.getIsactive()));
        }
        PageInfo<Task> page= new PageInfo<Task>(tasks);
        return page;
    }

    @Override
    public TaskAllocation selTaskAllocationById(String id) {
        return taskMapper.selTaskAllocationById(id);
    }

    @Override
    public PageInfo<TaskAllocation> selectTaskAllocationByPage(int page_num, int page_size, String corp_code, String task_code) throws Exception {
        PageHelper.startPage(page_num, page_size);
//        Task task=getTaskForId(corp_code,task_code);
//        String end="";
//        String start="";
//        if(task!=null){
//            end= task.getTarget_end_time();
//            start= task.getTarget_start_time();
//        }

        List<TaskAllocation> taskAllocations = taskMapper.selAllTaskAllocation(corp_code, task_code);

        for (TaskAllocation allocation:taskAllocations) {

            allocation.setIsactive(CheckUtils.CheckIsactive(allocation.getIsactive()));
            if(allocation.getReal_start_time()==null){
                allocation.setReal_start_time("");
            }

            if(allocation.getReal_end_time()==null){
                allocation.setReal_end_time("");
            }
            if(allocation.getTask_back() ==null){
                allocation.setTask_back("");
            }
            if(allocation.getTask_status().equals("1")){
                allocation.setTask_status("待确认");
            }else if(allocation.getTask_status().equals("2")){
                allocation.setTask_status("已接受");
            }else if(allocation.getTask_status().equals("3")){
                allocation.setTask_status("执行中");
            }
            else if(allocation.getTask_status().equals("4")){
                allocation.setTask_status("已完成");
            }
            else if(allocation.getTask_status().equals("-1")){
                allocation.setTask_status("已拒绝");
            }
            String store_name = "";
            String user_code = allocation.getUser_code();
            String store_code = allocation.getStore_code();
           // System.out.println("=========store_code================="+store_code);
            if(null==store_code){
                store_code="";
            }
            if(null==user_code){
                user_code="";
            }
            allocation.setStore_code(store_code);
            allocation.setStore_name(store_name);
            if(!"".equals(store_code)){

                Store stores = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
                if (stores != null){
                   // System.out.println("==========555555===========");
                    store_code = stores.getStore_code();
                    store_name = stores.getStore_name();
                   // System.out.println("=========store_name==55555=============="+store_name);
                    allocation.setStore_code(store_code);
                    allocation.setStore_name(store_name);


                }
            }else {
                List<User> users = userMapper.selectUserCode(user_code,corp_code,Common.IS_ACTIVE_Y);
                if (users.size()>0){
                    String store_codes = users.get(0).getStore_code();
                    store_codes = store_codes.replace(Common.SPECIAL_HEAD,"");
                    List<Store> stores = storeService.selectByStoreCodes(store_codes,corp_code,Common.IS_ACTIVE_Y);
                  //  System.out.println("=========stores================"+stores.size());
                    if (stores.size() > 0){
                        store_code = stores.get(0).getStore_code();
                        store_name = stores.get(0).getStore_name();
                        allocation.setStore_name(store_name);
                    //    System.out.println("=========store_name==55555=============="+store_name);
                        allocation.setStore_code(store_code);
                    }
                }
            }
           // System.out.println("=========store_name==55555=============="+store_name);

            if(allocation.getTask_status().equals("已完成")) {
                // System.out.println("----setTask_proof_img_pz-----------"+ta.getTask_proof_image());
                if ((null != allocation.getTask_proof_image() && !"".equals(allocation.getTask_proof_image()))) {
                    allocation.setTask_proof_img_pz(allocation.getTask_proof_image());
                }else{
                    allocation.setTask_proof_img_pz("");
                }
                if ((null != allocation.getTask_proof_text() && !"".equals(allocation.getTask_proof_text()))) {
                    allocation.setTask_proof_img_pz(allocation.getTask_proof_text());
                }
            }else{
                allocation.setTask_proof_img_pz("");
            }
//            System.out.println("----setTask_proof_img-----------"+allocation.getTask_proof_image());
//            System.out.println("----setTask_proof_img_pz-----------"+allocation.getTask_proof_img_pz());
        }

        PageInfo<TaskAllocation> page= new PageInfo<TaskAllocation>(taskAllocations);
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
        for (Task task:tasks) {
            task.setIsactive(CheckUtils.CheckIsactive(task.getIsactive()));
        }
        PageInfo<Task> page= new PageInfo<Task>(tasks);
        return page;
    }

    @Override
    public PageInfo<Task> selectSignAllScreen(int page_num, int page_size, String corp_code, String role_ident, String user_code, Map<String, String> map,String manager_corp) {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("map",map);
        params.put("corp_code",corp_code);
        params.put("role_ident",role_ident);
        params.put("user_code",user_code);
        PageHelper.startPage(page_num, page_size);
        List<Task> tasks = taskMapper.selectAllTaskScreen(params);
        for (Task task:tasks) {
            task.setIsactive(CheckUtils.CheckIsactive(task.getIsactive()));
        }
        PageInfo<Task> page= new PageInfo<Task>(tasks);
        return page;
    }

    @Override
    public List<TaskAllocation> selTaskAllocation(String corp_code, String task_code) throws Exception {
       List<TaskAllocation> taskAllocations=taskMapper.selAllTaskAllocation(corp_code, task_code);
         return taskAllocations;

    }

    @Override
    public List<TaskAllocation> selectAllTaskByTaskCode(String[] taskCodes) throws Exception {
        Map<String, Object>  params = new HashMap<String, Object>();
        params.put("array",taskCodes);
        List<TaskAllocation> tasks=taskMapper.selectAllTaskByTaskCode(params);
        return tasks;
    }


    @Override
    @Transactional
    public String delTask(String id, String corp_code, String task_code) {
        int count=0;
        try {
            count += taskMapper.delTaskById(id);
//            List<TaskAllocation> taskAllocations = taskMapper.selAllTaskAllocation(corp_code, task_code);
//            for (TaskAllocation taskAllocation : taskAllocations) {
//               count += taskMapper.delTaskAllocationById(taskAllocation.getId() + "");
//            }
            taskMapper.delTaskAllocation(corp_code,task_code);
        }catch (Exception e){
            e.printStackTrace();
        }
        return count+"";
    }

    @Override
    @Transactional
    public int insertTask(Task task) throws Exception{
      return taskMapper.addTask(task);
    }


    @Override
    @Transactional
    public String addTask(Task task,String phone,String users,String user_code,String activity_code) throws Exception{
        int count=0;
        count+=taskMapper.addTask(task);

        String[] user_codes = users.split(",");
        String store_codes = "";
        for (int i = 0; i < user_codes.length; i++) {
            List<User> userList = userMapper.selectUserCode(user_codes[i],task.getCorp_code(),Common.IS_ACTIVE_Y);
            String store = " ";
            if (userList.size() > 0){
                if (userList.get(0).getStore_code() != null && !userList.get(0).getStore_code().equals(""))
                    store = userList.get(0).getStore_code().replace(Common.SPECIAL_HEAD,"");
                if (store.contains(","))
                    store = store.split(",")[0];
            }
            store_codes = store_codes + store + ",";
        }
        taskAllocation(task,phone,users,store_codes,user_code,activity_code);
        return count+"";
    }

    @Override
    @Transactional
    public String taskAllocation(Task task,String phone,String users,String stores,String user_code,String activity_code) throws Exception{
        int count=0;
        String[] user_codes = users.split(",");
        String[] store_codes = stores.split(",");
        for(int i=0;i<user_codes.length;i++){
            List<TaskAllocation> allocations = selUserTask(task.getCorp_code(),task.getTask_code(),user_codes[i]);
            if (allocations.size() < 1){
                TaskAllocation allocation=new TaskAllocation();
                allocation.setCorp_code(task.getCorp_code());
                allocation.setTask_code(task.getTask_code());
                allocation.setUser_code(user_codes[i]);
                allocation.setStore_code(store_codes[i].trim());
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
        }
        if(count>0){
            iceInterfaceService.sendNotice(task.getCorp_code(),task.getTask_code(),task.getTask_title(),phone,users,user_code,activity_code);
        }
        return count+"";
    }

    //补发，加判断
    @Override
    @Transactional
    public String taskAllocation1(Task task, JSONArray array, String user_code, String activity_code) throws Exception{
        int count=0;
        String new_user_code = "";
        String new_phone = "";
        for(int i=0; i < array.size(); i++){
            JSONObject obj = array.getJSONObject(i);
            String user = obj.getString("user_code");
            String store = obj.getString("store_code");
            String phone = obj.getString("phone");

            List<TaskAllocation> allocations = selUserTask(task.getCorp_code(),task.getTask_code(),user);
            if (allocations.size() < 1){
                TaskAllocation allocation=new TaskAllocation();
                allocation.setCorp_code(task.getCorp_code());
                allocation.setTask_code(task.getTask_code());
                allocation.setUser_code(user);
                allocation.setStore_code(store);
                allocation.setTask_status("1");
                allocation.setReal_start_time("");
                allocation.setReal_end_time("");
                Date now = new Date();
                allocation.setCreated_date(Common.DATETIME_FORMAT.format(now));
                allocation.setCreater(user_code);
                allocation.setModified_date(Common.DATETIME_FORMAT.format(now));
                allocation.setModifier(user_code);
                count += taskMapper.addTaskAllocation(allocation);
                new_user_code = new_user_code + user + ",";
                new_phone = new_phone + phone + ",";
            }
        }
        if(count>0){
            iceInterfaceService.sendNotice(task.getCorp_code(),task.getTask_code(),task.getTask_title(),new_phone,new_user_code,user_code,activity_code);
        }
        return count+"";
    }

    @Override
    @Transactional
    public String updTask(Task task, String[] user_codes,String user_code) throws Exception {
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

                if(taskAllocation==null){
                    allocation.setCorp_code(task.getCorp_code());
                    allocation.setTask_code(task.getTask_code());
                    allocation.setUser_code(user_codes[i]);
                    List<User> userList = userMapper.selectUserCode(user_codes[i], task.getCorp_code(),"");
                    User user = userList.get(0);
                    String store_code = "";
                    if (user.getStore_code() != null && !user.getStore_code().equals("")){
                        store_code = user.getStore_code().replace(Common.SPECIAL_HEAD,"").split(",")[0];
                    }
                    String userPhone = user.getPhone();
                    phone = phone + userPhone+",";
                    users = users + user_codes[i];
                    allocation.setStore_code(store_code);
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
                    id=id+taskAllocation.getId()+",";
                    allocation.setCorp_code(task.getCorp_code());
                    allocation.setTask_code(task.getTask_code());
                    allocation.setUser_code(user_codes[i]);
                    Date now = new Date();
                    allocation.setModified_date(Common.DATETIME_FORMAT.format(now));
                    allocation.setModifier(user_code);
                    allocation.setId(taskAllocation.getId());
                    count+=taskMapper.updTaskAllocation(allocation);
                }
            }
            for (int i=0;i<taskAllocations.size();i++){
                if(!id.contains(taskAllocations.get(i).getId()+"")){
                  System.out.println(taskAllocations.get(i).getId()+"--"+id);
                  count+=taskMapper.delTaskAllocationById(taskAllocations.get(i).getId()+"");
                }
            }
            if(appCount>0){
                iceInterfaceService.sendNotice(task.getCorp_code(),task.getTask_code(),task.getTask_title(),phone,users,user_code,"");
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
    public Task getTaskForId(String corp_code, String task_code) throws Exception {
        return taskMapper.getTaskForId(corp_code,task_code);
    }



    @Override
    public String delTaskAllocation(String id) {
        return null;
    }

    @Override
    public List<TaskType> selectAllTaskType(String corp_code)throws Exception {
        return typeMapper.selectAllTaskType(corp_code,"","Y",null);
    }

    @Override
    public List<Task> selectTaskByTaskType(String corp_code,String task_type_code) throws Exception {
        return taskMapper.selectTaskByTaskType(corp_code,task_type_code);
    }

    @Override
    public List<Task> getTaskByActivityCode(String corp_code, String activity_vip_code) throws Exception {
        return taskMapper.getTaskByActivityCode(corp_code,activity_vip_code);
    }

    @Override
    public int delTaskByActivityCode(String corp_code, String activity_vip_code) throws Exception {
        return taskMapper.delTaskByActivityCode(corp_code,activity_vip_code);
    }


    @Override
    public List<TaskAllocation> selUserComTaskCount(String corp_code, String store_code, String task_status) throws Exception {
        return taskMapper.selUserComTaskCount(corp_code,store_code,task_status);
    }

    @Override
    public List<TaskAllocation> selUserTask(String corp_code, String task_code, String user_code) throws Exception {
        String[] task_codes = task_code.split(",");
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("corp_code",corp_code);
        map.put("array",task_codes);
        map.put("user_code",user_code);
        return taskMapper.selUserTask(map);

    }

    @Override
    public TaskAllocation selTaskByUser(String corp_code, String task_code, String user_code) throws Exception {
        return taskMapper.selAllTaskAllocationByUser(corp_code,task_code,user_code);
    }

    @Override
    public PageInfo<TaskAllocation> selTaskStore(int page_num,int page_size,String corp_code,String search_value) throws Exception {
        PageHelper.startPage(page_num, page_size);

        List<TaskAllocation> list = taskMapper.selTaskStore(corp_code,search_value);
        PageInfo<TaskAllocation> page= new PageInfo<TaskAllocation>(list);
        return page;

    }

    @Override
    public List<TaskAllocation> selStoreComTaskCount(String corp_code,String store_code, String task_status) throws Exception {
        List<TaskAllocation> list = taskMapper.selStoreComTaskCount(corp_code,store_code,task_status);
        return list;

    }

    @Override
    public PageInfo<TaskAllocation> selectAllTaskByUsers(int page_num, int page_size,String corp_code, String task_code, String[] userCodes) throws Exception {
        Map<String, Object>  params = new HashMap<String, Object>();
        params.put("corp_code",corp_code);
        params.put("task_code",task_code);
        params.put("array",userCodes);
        PageHelper.startPage(page_num, page_size);
        List<TaskAllocation> tasks=taskMapper.selAllTaskAllocationByUsers(params);

        for (TaskAllocation allocation:tasks) {
            //System.out.println("----setTask_proof_img-------3----"+allocation.getTask_proof_image());
            //System.out.println("----setTask_proof_img_pz-------4----"+allocation.getTask_proof_img_pz());
            allocation.setIsactive(CheckUtils.CheckIsactive(allocation.getIsactive()));
            if(allocation.getReal_start_time()==null){
                allocation.setReal_start_time("");
            }
            if(allocation.getReal_end_time()==null){
                allocation.setReal_end_time("");
            }
            if(allocation.getTask_back() ==null){
                allocation.setTask_back("");
            }
            if(allocation.getTask_status().equals("1")){
                allocation.setTask_status("待确认");
            }else if(allocation.getTask_status().equals("2")){
                allocation.setTask_status("已接受");
            }else if(allocation.getTask_status().equals("3")){
                allocation.setTask_status("执行中");
            }
            else if(allocation.getTask_status().equals("4")){
                allocation.setTask_status("已完成");
            }
            else if(allocation.getTask_status().equals("-1")){
                allocation.setTask_status("已拒绝");
            }
            String store_name = "";
            String user_code = allocation.getUser_code();
            String store_code = allocation.getStore_code();
            if(null==store_code){
                store_code="";
            }
            if(null==user_code){
                user_code="";
            }
            if (!store_code.isEmpty()){
                Store stores = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
                if (stores != null){
                    store_code = stores.getStore_code();
                    store_name = stores.getStore_name();
                }
            }else {
                List<User> users = userMapper.selectUserCode(user_code,corp_code,Common.IS_ACTIVE_Y);
                if (users.size()>0){
                    String store_codes = users.get(0).getStore_code();
                    store_codes = store_codes.replace(Common.SPECIAL_HEAD,"");
                    List<Store> stores = storeService.selectByStoreCodes(store_codes,corp_code,Common.IS_ACTIVE_Y);
                    if (stores.size() > 0){
                        store_code = stores.get(0).getStore_code();
                        store_name = stores.get(0).getStore_name();
                    }
                }
            }

            allocation.setStore_code(store_code);
            allocation.setStore_name(store_name);
           // System.out.println("----setTask_proof_img---1--------"+allocation.getTask_proof_image());
           // System.out.println("----setTask_proof_img_pz-----2------"+allocation.getTask_proof_img_pz());
            if(allocation.getTask_status().equals("已完成")) {
               //  System.out.println("----setTask_proof_img-----------"+allocation.getTask_proof_image());
               // System.out.println("----setTask_proof_img_pz-----------"+allocation.getTask_proof_img_pz());
                if ((null != allocation.getTask_proof_image() && !"".equals(allocation.getTask_proof_image()))) {
                    allocation.setTask_proof_img_pz(allocation.getTask_proof_image());
                }else{
                    allocation.setTask_proof_img_pz("");
                }
                if ((null != allocation.getTask_proof_text() && !"".equals(allocation.getTask_proof_text()))) {
                    allocation.setTask_proof_img_pz(allocation.getTask_proof_text());
                }
            }else{
                allocation.setTask_proof_img_pz("");
            }
        }
        PageInfo<TaskAllocation> page= new PageInfo<TaskAllocation>(tasks);
        return page;
    }

    @Override
    public PageInfo<TaskAllocation> selectAllTaskByStores(int page_num, int page_size,String corp_code, String task_code, String[] storeCodes) throws Exception {
        Map<String, Object>  params = new HashMap<String, Object>();
        params.put("corp_code",corp_code);
        params.put("task_code",task_code);
        params.put("array",storeCodes);
        PageHelper.startPage(page_num, page_size);
        List<TaskAllocation> tasks=taskMapper.selAllTaskAllocationByStore(params);

        for (TaskAllocation allocation:tasks) {

            allocation.setIsactive(CheckUtils.CheckIsactive(allocation.getIsactive()));
            if(allocation.getReal_start_time()==null){
                allocation.setReal_start_time("");
            }
            if(allocation.getReal_end_time()==null){
                allocation.setReal_end_time("");
            }
            if(allocation.getTask_back() ==null){
                allocation.setTask_back("");
            }
            if(allocation.getTask_status().equals("1")){
                allocation.setTask_status("待确认");
            }else if(allocation.getTask_status().equals("2")){
                allocation.setTask_status("已接受");
            }else if(allocation.getTask_status().equals("3")){
                allocation.setTask_status("执行中");
            }
            else if(allocation.getTask_status().equals("4")){
                allocation.setTask_status("已完成");
            }
            else if(allocation.getTask_status().equals("-1")){
                allocation.setTask_status("已拒绝");
            }
            String store_name = "";
            String user_code = allocation.getUser_code();
            String store_code = allocation.getStore_code();
            if(null==store_code){
                store_code="";
            }
            if(null==user_code){
                user_code="";
            }
            if (!store_code.isEmpty()){
                Store stores = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
                if (stores != null){
                    store_code = stores.getStore_code();
                    store_name = stores.getStore_name();
                }
            }else {
                List<User> users = userMapper.selectUserCode(user_code,corp_code,Common.IS_ACTIVE_Y);
                if (users.size()>0){
                    String store_codes = users.get(0).getStore_code();
                    store_codes = store_codes.replace(Common.SPECIAL_HEAD,"");
                    List<Store> stores = storeService.selectByStoreCodes(store_codes,corp_code,Common.IS_ACTIVE_Y);
                    if (stores.size() > 0){
                        store_code = stores.get(0).getStore_code();
                        store_name = stores.get(0).getStore_name();
                    }
                }
            }

            allocation.setStore_code(store_code);
            allocation.setStore_name(store_name);

            if(allocation.getTask_status().equals("已完成")) {
                // System.out.println("----setTask_proof_img_pz-----------"+ta.getTask_proof_image());
                if ((null != allocation.getTask_proof_image() && !"".equals(allocation.getTask_proof_image()))) {
                    allocation.setTask_proof_img_pz(allocation.getTask_proof_image());
                }else{
                    allocation.setTask_proof_img_pz("");
                }
                if ((null != allocation.getTask_proof_text() && !"".equals(allocation.getTask_proof_text()))) {
                    allocation.setTask_proof_img_pz(allocation.getTask_proof_text());
                }
            }else{
                allocation.setTask_proof_img_pz("");
            }
        }
        PageInfo<TaskAllocation> page= new PageInfo<TaskAllocation>(tasks);
        return page;
    }

}
