package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.System;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by yin on 2016/7/28.
 */
@Controller
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private FunctionService functionService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private BaseService baseService;
    String id;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String taskList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_code = request.getSession().getAttribute("user_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            int page_num = Integer.parseInt(request.getParameter("pageNumber"));
            JSONObject result = new JSONObject();
            PageInfo<Task> tasks;
            if (role_code.equals(Common.ROLE_SYS)) {
                tasks = taskService.selectAllTask(page_num, page_size, "", "", "", "");
            } else if (role_code.equals(Common.ROLE_GM)) {
                tasks = taskService.selectAllTask(page_num, page_size, corp_code, "", "", "");
            } else {
                tasks = taskService.selectAllTask(page_num, page_size, corp_code, "ident", user_code, "");
            }
            result.put("list", JSON.toJSONString(tasks));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String searchTaskAll(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            JSONObject result = new JSONObject();
            PageInfo<Task> tasks = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                tasks = taskService.selectAllTask(page_number, page_size, "", "", "", search_value);
            } else if (role_code.equals(Common.ROLE_GM)) {
                tasks = taskService.selectAllTask(page_number, page_size, corp_code, "", "", search_value);
            } else {
                tasks = taskService.selectAllTask(page_number, page_size, corp_code, "ident", user_code, search_value);
            }
            result.put("list", JSON.toJSONString(tasks));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String screenList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Task> tasks = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                tasks = taskService.selectSignAllScreen(page_number, page_size, "", "", "", map);
            } else if (role_code.equals(Common.ROLE_GM)) {
                tasks = taskService.selectSignAllScreen(page_number, page_size, corp_code, "", "", map);
            } else {
                tasks = taskService.selectSignAllScreen(page_number, page_size, corp_code, "ident", user_code, map);
            }
            result.put("list", JSON.toJSONString(tasks));
            result.put("list", JSON.toJSONString(tasks));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/userlist", method = RequestMethod.POST)
    @ResponseBody
    public String selectUserlist(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String task_code = jsonObject.get("task_code").toString();
            List<TaskAllocation> taskAllocations = taskService.selTaskAllocation(corp_code, task_code);
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(taskAllocations));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        int count=0;
        String del="";
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String list = jsonObject.get("list").toString();
            com.alibaba.fastjson.JSONArray array = com.alibaba.fastjson.JSONArray.parseArray(list);
            for (int i = 0; i < array.size(); i++) {
                String info = array.get(i).toString();
                JSONObject json = new JSONObject(info);
                String id = json.get("id").toString();
                String corp_code = json.get("corp_code").toString();
                String task_code = json.get("task_code").toString();
                Task task = taskService.selectTaskById(id);
                if (task != null){
                    if(role_code.equals(Common.ROLE_SYS)||role_code.equals(Common.ROLE_GM)) {
                        del= taskService.delTask(id, corp_code, task_code);
                        count = Integer.parseInt(del);

                        //----------------行为日志开始------------------------------------------
                        /**
                         * mongodb插入用户操作记录
                         * @param operation_corp_code 操作者corp_code
                         * @param operation_user_code 操作者user_code
                         * @param function 功能
                         * @param action 动作
                         * @param corp_code 被操作corp_code
                         * @param code 被操作code
                         * @param name 被操作name
                         * @throws Exception
                         */
                        String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                        String operation_user_code = request.getSession().getAttribute("user_code").toString();
                        String function = "任务管理_任务列表";
                        String action = Common.ACTION_DEL;
                        String t_corp_code = task.getCorp_code();
                        String t_code = task.getTask_code();
                        String t_name = task.getTask_title();
                        String remark = "";
                        baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                        //-------------------行为日志结束-----------------------------------------------------------------------------------
                    }else{
                        if(!user_code.equals(task.getCreater())){
                            del="无法删除他人创建的任务";
                        }else{
                            del= taskService.delTask(id, corp_code, task_code);
                            count = Integer.parseInt(del);

                            //----------------行为日志开始------------------------------------------
                            /**
                             * mongodb插入用户操作记录
                             * @param operation_corp_code 操作者corp_code
                             * @param operation_user_code 操作者user_code
                             * @param function 功能
                             * @param action 动作
                             * @param corp_code 被操作corp_code
                             * @param code 被操作code
                             * @param name 被操作name
                             * @throws Exception
                             */
                            String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                            String operation_user_code = request.getSession().getAttribute("user_code").toString();
                            String function = "任务管理_任务列表";
                            String action = Common.ACTION_DEL;
                            String t_corp_code = task.getCorp_code();
                            String t_code = task.getTask_code();
                            String t_name = task.getTask_title();
                            String remark = "";
                            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                            //-------------------行为日志结束-----------------------------------------------------------------------------------
                        }
                    }
                }
            }
            if(count>0){
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("删除成功");
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage(del);
            }
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("删除失败");
        }
        return dataBean.getJsonStr();
    }
    @RequestMapping(value = "/addTask", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addTask(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        int count = 0;
        try {
            String user_code = request.getSession().getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            Task task=new Task();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String task_code = "T" + sdf.format(new Date()) + Math.round(Math.random() * 9);
            task.setTask_code(task_code);
            task.setTask_title(jsonObject.get("task_title").toString());
            task.setTask_type_code(jsonObject.get("task_type_code").toString());
            task.setTask_description(jsonObject.get("task_description").toString());
            task.setTarget_start_time(jsonObject.get("target_start_time").toString());
            task.setTarget_end_time(jsonObject.get("target_end_time").toString());
            task.setCorp_code(jsonObject.get("corp_code").toString());
            Date now = new Date();
            task.setCreated_date(Common.DATETIME_FORMAT.format(now));
            task.setCreater(user_code);
            task.setModified_date(Common.DATETIME_FORMAT.format(now));
            task.setModifier(user_code);
            task.setIsactive(jsonObject.get("isactive").toString());
            String user_codes = jsonObject.get("user_codes").toString();
            String phone = jsonObject.get("phone").toString();
            String add = taskService.addTask(task,phone,user_codes,user_code);
            count=Integer.parseInt(add);
            if(count>0){
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                Task task1=taskService.getTaskForId(task.getCorp_code(),task.getTask_type_code(),task.getTask_code());
                JSONObject obj=new JSONObject();
                obj.put("taskId",task1.getId());
                obj.put("taskCode",task1.getTask_code());
                dataBean.setMessage(String.valueOf(task1.getId()));
                dataBean.setMessage(obj.toString());

                //----------------行为日志------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "任务管理_任务列表";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = task_code;
                String t_name = action_json.get("task_title").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage("新增失败");
            }
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("新增失败");
        }
        return  dataBean.getJsonStr();
    }
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String updTask(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        int count = 0;
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            Task task=new Task();
            task.setTask_code(jsonObject.get("task_code").toString());
            task.setTask_title(jsonObject.get("task_title").toString());
            task.setTask_type_code(jsonObject.get("task_type_code").toString());
            task.setTask_description(jsonObject.get("task_description").toString());
            task.setTarget_start_time(jsonObject.get("target_start_time").toString());
            task.setTarget_end_time(jsonObject.get("target_end_time").toString());
            task.setCorp_code(jsonObject.get("corp_code").toString());
            task.setId(Integer.parseInt(jsonObject.get("id").toString()));
            Date now = new Date();
            task.setModified_date(Common.DATETIME_FORMAT.format(now));
            task.setModifier(user_code);
            task.setIsactive(jsonObject.get("isactive").toString());
            String user_codes = jsonObject.get("user_codes").toString();
            String[] split = user_codes.split(",");
            if(role_code.equals(Common.ROLE_SYS)||role_code.equals(Common.ROLE_GM)) {
                String add = taskService.updTask(task, split,user_code);
                count=Integer.parseInt(add);
                if(count>0){
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage("编辑成功");
                }else{
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("-1");
                    dataBean.setMessage("编辑失败");
                }
            }else{
                Task task_user = taskService.selectTaskById(jsonObject.get("id").toString());
                if(!user_code.equals(task_user.getCreater())){
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("-1");
                    dataBean.setMessage("无法编辑他人创建的任务");
                }else{
                    String add = taskService.updTask(task, split,user_code);
                    count=Integer.parseInt(add);
                    if(count>0){
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setId(id);
                        dataBean.setMessage("编辑成功");

                        //----------------行为日志------------------------------------------
                        /**
                         * mongodb插入用户操作记录
                         * @param operation_corp_code 操作者corp_code
                         * @param operation_user_code 操作者user_code
                         * @param function 功能
                         * @param action 动作
                         * @param corp_code 被操作corp_code
                         * @param code 被操作code
                         * @param name 被操作name
                         * @throws Exception
                         */
                        com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                        String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                        String operation_user_code = request.getSession().getAttribute("user_code").toString();
                        String function = "任务管理_任务列表";
                        String action = Common.ACTION_UPD;
                        String t_corp_code = action_json.get("corp_code").toString();
                        String t_code = action_json.get("task_code").toString();
                        String t_name = action_json.get("task_title").toString();
                        String remark = "";
                        baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                        //-------------------行为日志结束-----------------------------------------------------------------------------------
                    }else{
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("-1");
                        dataBean.setMessage("编辑失败");
                    }
                }
            }
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("编辑失败");
        }
        return  dataBean.getJsonStr();
    }
    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<Task> tasks = null;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    tasks = taskService.selectAllTask(1, Common.EXPORTEXECLCOUNT, "", "", "", search_value);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    tasks = taskService.selectAllTask(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", search_value);
                } else {
                    tasks = taskService.selectAllTask(1, Common.EXPORTEXECLCOUNT, corp_code, "ident", user_code, search_value);
                }
            }else{
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    tasks = taskService.selectSignAllScreen(1, Common.EXPORTEXECLCOUNT, "", "", "", map);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    tasks = taskService.selectSignAllScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", map);
                } else {
                    tasks = taskService.selectSignAllScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "ident", user_code, map);
                }
            }
            List<Task> list = tasks.getList();
            if (list.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(list);
            LinkedHashMap<String,String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json,list, map, response, request);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }
    /***
     * 获取任务类型
     */
    @RequestMapping(value = "/selectAllTaskType", method = RequestMethod.POST)
    @ResponseBody
    public String selectAllTaskType(HttpServletRequest request){
        DataBean bean=new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            List<TaskType> taskTypes = taskService.selectAllTaskType(corp_code);
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(taskTypes));
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(result.toString());
        }catch (Exception e){
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage("类型异常");
        }
        return bean.getJsonStr();
    }
    /***
     * 查询该任务的详情
     */
    @RequestMapping(value = "/selectTaskById", method = RequestMethod.POST)
    @ResponseBody
    public String selectTaskById(HttpServletRequest request){
        DataBean bean=new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String task_id = jsonObject.get("id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String task_code = jsonObject.get("task_code").toString();
            List<TaskAllocation> taskAllocations = taskService.selTaskAllocation(corp_code, task_code);
            Task task = taskService.selectTaskById(task_id);
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(taskAllocations));
            result.put("task", JSON.toJSONString(task));
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(result.toString());
        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage("信息异常");
        }
        return bean.getJsonStr();

    }


}
