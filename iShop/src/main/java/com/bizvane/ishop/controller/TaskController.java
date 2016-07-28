package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.TaskAllocation;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.TaskService;
import com.bizvane.ishop.service.TaskTypeService;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by yin on 2016/7/28.
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    @Autowired
    TaskService taskService;
    @Autowired
    FunctionService functionService;

    String id;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String selectAllTask(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try {
            String user_code = request.getSession().getAttribute("user_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            int page_num = Integer.parseInt(request.getParameter("pageNumber"));
            String function_code = request.getParameter("funcCode");
            JSONArray actions = functionService.selectActionByFun(corp_code,user_code, group_code, role_code, function_code);
            JSONObject result = new JSONObject();
            PageInfo<Task> tasks;
            if (role_code.equals(Common.ROLE_SYS)) {
                tasks = taskService.selectAllTask(page_size, page_num, "", "", "", "");
            } else if (role_code.equals(Common.ROLE_GM)) {
                tasks = taskService.selectAllTask(page_size, page_num, corp_code, "", "", "");
            } else {
                tasks = taskService.selectAllTask(page_size, page_num, corp_code, "ident", user_code, "");
            }
            result.put("actions", actions);
            result.put("list", JSON.toJSONString(tasks));
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
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String searchTaskAll(HttpServletRequest request){
        DataBean dataBean=new DataBean();
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
                tasks = taskService.selectAllTask(page_size, page_number, "", "", "", search_value);
            } else if (role_code.equals(Common.ROLE_GM)) {
                tasks = taskService.selectAllTask(page_size, page_number, corp_code, "", "", search_value);
            } else {
                tasks = taskService.selectAllTask(page_size, page_number, corp_code, "ident", user_code, search_value);
            }
            result.put("list", JSON.toJSONString(tasks));
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
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        int count=0;
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String list = jsonObject.get("list").toString();
            com.alibaba.fastjson.JSONArray array = com.alibaba.fastjson.JSONArray.parseArray(list);

            for (int i=0;i<array.size();i++){
                String info = array.get(i).toString();
                JSONObject json = new JSONObject(info);
                String id = json.get("id").toString();
                String corp_code = json.get("corp_code").toString();
                String task_code = json.get("task_code").toString();
                taskService.delTask(id,corp_code,task_code);
            }
        }catch (Exception ex){
        }
        return dataBean.getJsonStr();
    }

}
