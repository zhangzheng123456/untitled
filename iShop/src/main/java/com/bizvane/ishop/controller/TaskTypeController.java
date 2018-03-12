package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.TaskType;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.TaskService;
import com.bizvane.ishop.service.TaskTypeService;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/7/21.
 */
@Controller
@RequestMapping("/task_type")
public class TaskTypeController {

    @Autowired
    TaskTypeService taskTypeService;
    @Autowired
    FunctionService functionService;
    @Autowired
    TaskService taskService;
    @Autowired
    private BaseService baseService;
    String id;

    private static final Logger logger = Logger.getLogger(TaskTypeController.class);

//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String taskTypeList(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            String corp_code = request.getSession().getAttribute("corp_code").toString();
//
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            int page_num = Integer.parseInt(request.getParameter("pageNumber"));
//
//            JSONObject result = new JSONObject();
//            PageInfo<TaskType> tasktype;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                tasktype = taskTypeService.selectAllTaskType(page_num, page_size, "", "");
//            } else {
//                tasktype = taskTypeService.selectAllTaskType(page_num, page_size, corp_code, "");
//            }
//            result.put("list", JSON.toJSONString(tasktype));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage() + ex.toString());
//            logger.info(ex.getMessage() + ex.toString());
//        }
//        return dataBean.getJsonStr();
//    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addTaskType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--taskType add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = taskTypeService.insertTaskType(message, user_code);
            if (result.equals("任务类型编号已存在")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            } else if (result.equals("任务类型名称已存在")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            } else if (result.equals("新增失败，请稍后再试")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result);

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
                String function = "任务管理_任务类型";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("task_type_code").toString();
                String t_name = action_json.get("task_type_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editTaskType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--taskType add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            String result = taskTypeService.updateTaskType(message, user_code);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");

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
                String function = "任务管理_任务类型";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("task_type_code").toString();
                String t_name = action_json.get("task_type_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");
            logger.info("json--taskType select-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String type_id = jsonObject.get("id").toString();
            data = JSON.toJSONString(taskTypeService.selectById(type_id));
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(data);
        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage("任务类型信息异常");
            logger.info(e.getMessage() + e.toString());
        }
        logger.info("info-----" + bean.getJsonStr());
        return bean.getJsonStr();
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            JSONObject result = new JSONObject();
            PageInfo<TaskType> tasktype;
            if (role_code.equals(Common.ROLE_SYS)) {
                tasktype = taskTypeService.selectAllTaskType(page_number, page_size, "", search_value);
            }else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                tasktype = taskTypeService.selectAllTaskType(page_number, page_size, corp_code, search_value);

                //  tasktype = taskTypeService.selectAllTaskType(page_number, page_size, "", search_value,manager_corp);
            } else {
                tasktype = taskTypeService.selectAllTaskType(page_number, page_size, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(tasktype));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--taskType delete-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String user_id = jsonObject.get("id").toString();
            String[] ids = user_id.split(",");
            String msg = null;
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete user--" + Integer.valueOf(ids[i]));
                TaskType taskType = taskTypeService.selectById(ids[i]);
                if (taskType != null) {
                    String task_type_code = taskType.getTask_type_code();
                    String corp_code = taskType.getCorp_code();
                    List<Task> tasks = taskService.selectTaskByTaskType(corp_code, task_type_code);
                    if (tasks.size() > 0) {
                        msg = "该任务类型" + task_type_code + "下有任务，请先处理后再删除";
                    }
                }
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
                String function = "任务管理_任务类型";
                String action = Common.ACTION_DEL;
                String t_corp_code = taskType.getCorp_code();
                String t_code = taskType.getTask_type_code();
                String t_name = taskType.getTask_type_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
            if (msg == null) {
                for (int i = 0; i < ids.length; i++) {
                    taskTypeService.deleteTaskType(Integer.valueOf(ids[i]));
                }
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("删除成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/codeExist", method = RequestMethod.POST)
    @ResponseBody
    public String codeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String task_type_code = jsonObject.get("task_type_code").toString();

            List<TaskType> task_type = taskTypeService.codeExist(corp_code, task_type_code);
            if (task_type.size() > 0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("编号已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/nameExist", method = RequestMethod.POST)
    @ResponseBody
    public String nameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String task_type_name = jsonObject.get("task_type_name").toString();

            List<TaskType> task_type = taskTypeService.nameExist(corp_code, task_type_name);
            if (task_type.size() > 0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("名称已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("名称不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String taskTypeScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObject1 = JSONObject.parseObject(jsString);
            id = jsonObject1.getString("id");
            String message = jsonObject1.get("message").toString();
            JSONObject jsonObject2 = JSONObject.parseObject(message);
            int page_number = Integer.parseInt(jsonObject2.get("pageNumber").toString());
            int page_size = Integer.parseInt(jsonObject2.get("pageSize").toString());
//            String screen = jsonObject2.get("screen").toString();
//            org.json.JSONObject jsonScreen = new JSONObject(screen);
            Map<String, String> map = WebUtils.Json2Map(jsonObject2);
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<TaskType> taskType;
            if (role_code.equals(Common.ROLE_SYS)) {
                taskType = taskTypeService.selectAllTaskTypeScreen(page_number, page_size, "", map);
            }else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                taskType = taskTypeService.selectAllTaskTypeScreen(page_number, page_size, corp_code, map);

                //  taskType = taskTypeService.selectAllTaskTypeScreen(page_number, page_size, "", map,manager_corp);
            } else {
                taskType = taskTypeService.selectAllTaskTypeScreen(page_number, page_size, corp_code, map);
            }
            result.put("list", JSON.toJSONString(taskType));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 导出数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
           JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<TaskType> taskTypes = null;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    taskTypes = taskTypeService.selectAllTaskType(1, Common.EXPORTEXECLCOUNT, "", search_value);
                } else {
                    taskTypes = taskTypeService.selectAllTaskType(1, Common.EXPORTEXECLCOUNT, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    taskTypes = taskTypeService.selectAllTaskTypeScreen(1, Common.EXPORTEXECLCOUNT, "", map);
                } else {
                    taskTypes = taskTypeService.selectAllTaskTypeScreen(1, Common.EXPORTEXECLCOUNT, corp_code, map);
                }
            }
            List<TaskType> list = taskTypes.getList();
            if (list.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(list);
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request,"任务类型");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 获取企业下任务类型
     * @param request
     * @return
     */
    @RequestMapping(value = "/getCorpTaskTypes", method = RequestMethod.POST)
    @ResponseBody
    public String getCorpGroups(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String search_value = "";
            if (jsonObject.containsKey("searchValue"))
                search_value = jsonObject.get("searchValue").toString();

            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
            List<TaskType> taskTypes =taskTypeService.selectCorpTaskType(corp_code,search_value);
            result.put("list", JSON.toJSONString(taskTypes));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();

    }
}
