package com.bizvane.ishop.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
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
    private TaskService taskService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private VipActivityService vipActivityService;
    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;
    String id;
    private static final Logger logger = Logger.getLogger(TaskController.class);

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String searchTaskAll(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
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
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                tasks = taskService.selectAllTask(page_number, page_size, corp_code, "", "", search_value);

                //    tasks = taskServ ice.selectAllTask(page_number, page_size, "", "", "", search_value,manager_corp);
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
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
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                tasks = taskService.selectSignAllScreen(page_number, page_size, corp_code, "", "", map);

                //   tasks = taskService.selectSignAllScreen(page_number, page_size, "", "", "", map,manager_corp);
            } else if (role_code.equals(Common.ROLE_GM)) {
                tasks = taskService.selectSignAllScreen(page_number, page_size, corp_code, "", "", map);
            } else {
                tasks = taskService.selectSignAllScreen(page_number, page_size, corp_code, "ident", user_code, map);
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


    @RequestMapping(value = "/userlist1", method = RequestMethod.POST)
    @ResponseBody
    public String selectUserlist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String task_code = jsonObject.get("task_code").toString();
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            PageInfo<TaskAllocation> taskAllocations = taskService.selectTaskAllocationByPage(page_number, page_size, corp_code, task_code);
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(taskAllocations));
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

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        int count = 0;
        String del = "";
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String list = jsonObject.get("list").toString();
            JSONArray array = JSONArray.parseArray(list);
            for (int i = 0; i < array.size(); i++) {
                String info = array.get(i).toString();
                JSONObject json = JSONObject.parseObject(info);
                String id = json.get("id").toString();
                String corp_code = json.get("corp_code").toString();
                String task_code = json.get("task_code").toString();
                Task task = taskService.selectTaskById(id);
                if (task != null) {
                  //  System.out.println("============task.getActivity_vip_code()======="+task.getActivity_vip_code());
                    String activity_code = task.getActivity_vip_code();
                    if (!("").equals(activity_code)) {

                        VipActivity activity = vipActivityService.getActivityByCode(activity_code);
                        if (activity != null) {
                            String task_code1 = activity.getTask_code();
                            if (task_code1.contains(task_code + ",")) {
                                activity.setTask_code(task_code1.replace(task_code + ",", ""));
                                vipActivityService.updateVipActivity(activity);
                            }
                            if (task_code1.contains(task_code)) {
                                activity.setTask_code(task_code1.replace(task_code, ""));
                                vipActivityService.updateVipActivity(activity);
                            }
                        }
                    }
                    if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM)) {
                        del = taskService.delTask(id, corp_code, task_code);
                      //count = Integer.parseInt(del);
                        //System.out.print("=====count = Integer.parseInt(del)========"+ Integer.parseInt(del));

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
                        baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                        //-------------------行为日志结束-----------------------------------------------------------------------------------
                    } else {
                        if (!user_code.equals(task.getCreater())) {
                            count = 2;
                            del = "无法删除他人创建的任务";
                        } else {
                            del = taskService.delTask(id, corp_code, task_code);
//                            count = Integer.parseInt(del);

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
                            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                            //-------------------行为日志结束-----------------------------------------------------------------------------------
                        }
                    }
                }
            }
            if (count == 0) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("删除成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage(del);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("删除失败");
            ex.printStackTrace();
        }

        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/addTask", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addTask(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        int count = 0;
        String path = "";

        try {
            String user_code = request.getSession().getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String task_description=jsonObject.get("task_description").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Task task = new Task();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String task_code = "T" + sdf.format(new Date()) + Math.round(Math.random() * 9);

            //富文本
            List<String> htmlImageSrcList = OssUtils.getHtmlImageSrcList(task_description);
            OssUtils ossUtils = new OssUtils();
            String bucketName = "products-image";
            path = request.getSession().getServletContext().getRealPath("/");
            for (int k = 0; k < htmlImageSrcList.size(); k++) {
                String time = "TASK/" + corp_code + "/" + task_code + "_" + Common.DATETIME_FORMAT_DAY_NUM.format(new Date()) + ".jpg";
                if (!htmlImageSrcList.get(k).contains("image/upload")) {
                    continue;
                }
                ossUtils.putObject(bucketName, time, path + "/" + htmlImageSrcList.get(k));
                task_description = task_description.replace(htmlImageSrcList.get(k), "http://" + bucketName + ".oss-cn-hangzhou.aliyuncs.com/" + time);
                LuploadHelper.deleteFile(path + "/" + htmlImageSrcList.get(k));
            }
            task.setTask_code(task_code);
            task.setTask_title(jsonObject.get("task_title").toString());
            task.setTask_type_code(jsonObject.get("task_type_code").toString());

            task.setTask_description(task_description);
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

            String add = taskService.addTask(task, phone, user_codes, user_code, "");
            count = Integer.parseInt(add);
            if (count > 0) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                Task task1 = taskService.getTaskForId(task.getCorp_code(), task.getTask_code());
                JSONObject obj = new JSONObject();
                obj.put("taskId", task1.getId());
                obj.put("taskCode", task1.getTask_code());
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
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage("新增失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("新增失败");
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String updTask(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        int count = 0;
        String path = "";
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String delImgPath = jsonObject.get("delImgPath").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String task_code = jsonObject.get("task_code").toString();
            String task_description=jsonObject.get("task_description").toString();
            List<String> htmlImageSrcList = OssUtils.getHtmlImageSrcList(task_description);
            List<String> delImgPaths = OssUtils.getHtmlImageSrcList(delImgPath);
            OssUtils ossUtils = new OssUtils();
            String bucketName = "products-image";
            path = request.getSession().getServletContext().getRealPath("/");
            for (int i = 0; i < delImgPaths.size(); i++) {
                String replace = delImgPaths.get(i).replace("http://" + bucketName + ".oss-cn-hangzhou.aliyuncs.com/", "");
                ossUtils.deleteObject(bucketName, replace);
            }
           // logger.info("================htmlImageSrcList======="+htmlImageSrcList);
            for (int k = 0; k < htmlImageSrcList.size(); k++) {
                String time = "TASK/" + corp_code + "/" + task_code + "_" + Common.DATETIME_FORMAT_DAY_NUM.format(new Date()) + ".jpg";
                if (!htmlImageSrcList.get(k).contains("image/upload")) {
                    continue;
                }

              //  logger.info("=============================htmlImageSrcList.get(k)===================="+htmlImageSrcList.get(k));
                ossUtils.putObject(bucketName, time, path + "/" + htmlImageSrcList.get(k));

                task_description = task_description.replace(htmlImageSrcList.get(k), "http://" + bucketName + ".oss-cn-hangzhou.aliyuncs.com/" + time);
              //  logger.info("=============================task_description================"+task_description);
                LuploadHelper.deleteFile(path + "/" + htmlImageSrcList.get(k));
            }
            Task task = new Task();
            task.setTask_code(jsonObject.get("task_code").toString());
            task.setTask_title(jsonObject.get("task_title").toString());
            task.setTask_type_code(jsonObject.get("task_type_code").toString());
            task.setTask_description(task_description);
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
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM)) {
                String add = taskService.updTask(task, split, user_code);
                count = Integer.parseInt(add);
                if (count > 0) {
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage("编辑成功");
                } else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("-1");
                    dataBean.setMessage("编辑失败");
                }
            } else {
                Task task_user = taskService.selectTaskById(jsonObject.get("id").toString());
                if (!user_code.equals(task_user.getCreater())) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("-1");
                    dataBean.setMessage("无法编辑他人创建的任务");
                } else {
                    String add = taskService.updTask(task, split, user_code);
                    count = Integer.parseInt(add);
                    if (count > 0) {
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
                        baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                        //-------------------行为日志结束-----------------------------------------------------------------------------------
                    } else {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("-1");
                        dataBean.setMessage("编辑失败");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("编辑失败");
        }
        return dataBean.getJsonStr();
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
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
            } else {
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
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request, "任务");
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

    /***
     * 获取任务类型
     */
    @RequestMapping(value = "/selectAllTaskType", method = RequestMethod.POST)
    @ResponseBody
    public String selectAllTaskType(HttpServletRequest request) {
        DataBean bean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            List<TaskType> taskTypes = taskService.selectAllTaskType(corp_code);
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(taskTypes));
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(result.toString());
        } catch (Exception e) {
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
    public String selectTaskById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String task_id = jsonObject.get("id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String task_code = jsonObject.get("task_code").toString();
            List<TaskAllocation> taskAllocations = taskService.selTaskAllocation(corp_code, task_code);
            JSONObject result = new JSONObject();
            result.put("list", "");
            Task task = taskService.selectTaskById(task_id);
            if(taskAllocations!=null){
                result.put("list", JSON.toJSONString(taskAllocations));
            }
            result.put("task", JSON.toJSONString(task));
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage("信息异常");
        }
        return bean.getJsonStr();

    }

    @RequestMapping(value = "/userlist", method = RequestMethod.POST)
    @ResponseBody
    public String selectUserlists(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject object = new JSONObject();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String task_code = jsonObject.get("task_code").toString();
            String store_codes = jsonObject.get("store_codes").toString();
            String user_codes = jsonObject.get("user_codes").toString();
            String brand_codes = jsonObject.get("brand_codes").toString();

            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String storeByScreen = baseService.getStoresByScreen(jsonObject, request, corp_code);
         System.out.println("============================"+storeByScreen);
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                //  System.out.println("manager_corp=====>"+manager_corp);
                String[] manager_corp_arr = null;
                if (!manager_corp.equals("")) {
                    manager_corp_arr = manager_corp.split(",");
                }
                corp_code = manager_corp_arr[0];
                if (jsonObject.containsKey("corp_code") && !String.valueOf(jsonObject.get("corp_code")).equals("")) {
                    corp_code = jsonObject.get("corp_code").toString();
                }
                System.out.println(corp_code + "<======manager_corp=====>" + manager_corp);
            }
            PageInfo<TaskAllocation> taskAllocations = null;
            if (!user_codes.equals("")) {

                if(!user_codes.contains(",")){
                  user_codes=  user_codes+",";
                }
                String[] arr_user=user_codes.split(",");
                taskAllocations= taskService.selectAllTaskByUsers(page_number, page_size, corp_code, task_code, arr_user);
            }else if (storeByScreen.equals("")) {
                taskAllocations = taskService.selectTaskAllocationByPage(page_number, page_size, corp_code, task_code);
            } else if (user_codes.equals("")) {
                String[] arr_store = storeByScreen.split(",");
                taskAllocations= taskService.selectAllTaskByStores(page_number, page_size, corp_code, task_code, arr_store);
            }
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(taskAllocations));
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


    /***
     * 导出任务凭证图片
     * 可多张
     */
    @RequestMapping(value = "/exportZip", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl_zip(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String user_id = request.getSession().getAttribute("user_id").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
           // String corp_code = request.getSession().getAttribute("corp_code").toString();

            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String task_code = jsonObject.get("task_code").toString();
            PageInfo<TaskAllocation> list = null;
            String storeByScreen = baseService.getStoresByScreen(jsonObject, request, corp_code);
            int page_size = Common.EXPORTEXECLCOUNT;
                if (role_code.equals(Common.ROLE_SYS)) {
                    corp_code = jsonObject.get("corp_code").toString();
                }
                if (role_code.equals(Common.ROLE_CM)) {
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    //  System.out.println("manager_corp=====>"+manager_corp);
                    String[] manager_corp_arr = null;
                    if (!manager_corp.equals("")) {
                        manager_corp_arr = manager_corp.split(",");
                    }
                    corp_code = manager_corp_arr[0];
                    if (jsonObject.containsKey("corp_code") && !String.valueOf(jsonObject.get("corp_code")).equals("")) {
                        corp_code = jsonObject.get("corp_code").toString();
                    }
                    System.out.println(corp_code + "<======manager_corp=====>" + manager_corp);
                }

                String user_code_json = jsonObject.get("user_codes").toString();
            if (!user_code_json.equals("")) {
                if(!user_code_json.contains(",")){
                    user_code_json=user_code_json+",";
                }
                String[] arr_user = user_code_json.split(",");
                list=taskService.selectAllTaskByUsers(1, page_size, corp_code, task_code, arr_user);
            }else if (storeByScreen.equals("")) {
                    list = taskService.selectTaskAllocationByPage(1, page_size, corp_code, task_code);
                } else if (user_code_json.equals("")) {
                    String[] arr_store = storeByScreen.split(",");
                    list= taskService.selectAllTaskByStores(1, page_size, corp_code, task_code, arr_store);
                }

            List<TaskAllocation> allocations = list.getList();

            if (allocations.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            String pathname = OutHtmlHelper.OutZip_Task_user(allocations, request);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            if (pathname.equals("空文件夹")) {
                errormessage = "导出的员工中都没有任务凭证";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("task/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();

    }


}
