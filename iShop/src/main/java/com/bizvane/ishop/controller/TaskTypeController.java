package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.TaskType;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.TaskTypeService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
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

    String id;

    private static final Logger logger = Logger.getLogger(TaskTypeController.class);

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String taskTypeList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_code = request.getSession().getAttribute("user_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            int page_num = Integer.parseInt(request.getParameter("pageNumber"));
            String function_code = request.getParameter("funcCode");
            JSONArray actions = functionService.selectActionByFun(user_code, group_code, role_code, function_code);

            JSONObject result = new JSONObject();
            PageInfo<TaskType> tasktype;
            if (role_code.equals(Common.ROLE_SYS)) {
                tasktype = taskTypeService.selectAllTaskType(page_num, page_size, "", "");
            } else {
                tasktype = taskTypeService.selectAllTaskType(page_num, page_size, corp_code, "");
            }
            result.put("actions", actions);
            result.put("list", JSON.toJSONString(tasktype));
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

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addTaskType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--taskType add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            String result = taskTypeService.insertTaskType(message, user_code);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add success");
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

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editTaskType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--taskType add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            String result = taskTypeService.updateTaskType(message, user_code);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            JSONObject result = new JSONObject();
            PageInfo<TaskType> tasktype;
            if (role_code.equals(Common.ROLE_SYS)) {
                tasktype = taskTypeService.selectAllTaskType(page_number, page_size, "", search_value);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_id = jsonObject.get("id").toString();
            String[] ids = user_id.split(",");
            int count = 0;
            String msg = "";
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete user--" + Integer.valueOf(ids[i]));
//                User user = userService.getById(Integer.parseInt(ids[i]));
//                List<UserAchvGoal> goal = userService.selectUserAchvCount(user.getCorp_code(), user.getUser_code());
//                count = goal.size();
//                if (count > 0) {
//                    msg = "请先删除用户的业绩目标，再删除用户" + user.getUser_code();
//                    break;
//                }
                taskTypeService.deleteTaskType(Integer.valueOf(ids[i]));
            }
//            if (count > 0) {
//                dataBean.setId(id);
//                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setMessage(msg);
//            } else {
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
//            }
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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
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
            org.json.JSONObject jsonObject1 = new org.json.JSONObject(jsString);
            id = jsonObject1.getString("id");
            String message = jsonObject1.get("message").toString();
            org.json.JSONObject jsonObject2 = new org.json.JSONObject(message);
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
}
