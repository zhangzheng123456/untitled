package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.ActivityVip;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.TaskType;
import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.ishop.service.ActivityVipService;
import com.bizvane.ishop.service.TaskService;
import com.bizvane.ishop.service.TaskTypeService;
import com.bizvane.ishop.service.VipGroupService;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nanji on 2016/11/16.
 */
@Controller
@RequestMapping("/activity")
public class ActivityVipController {
    @Autowired
    private ActivityVipService activityVipService;
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private TaskService taskService;

    private static final Logger logger = Logger.getLogger(ActivityVipController.class);

    String id;
    /**
     * 活动
     * 列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String goodsTrainManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<ActivityVip> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {

                list = this.activityVipService.selectAllActivity(page_number, page_size, "","");
            } else {

                    list = activityVipService.selectAllActivity(page_number, page_size, corp_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员活动
     * 添加
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addActivity(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";

        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            //this.activityVipService.insert(message,user_id,request);
           // String result= this.activityVipService.insert(message,user_id,request);

            String result= this.activityVipService.insert(message,user_id);
            if(result.equals("新增失败")){
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            }else{
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(result);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 活动编辑之前
     * 获取数据
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectActivity(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int activity_id = Integer.parseInt(jsonObject.getString("id"));
           ActivityVip activityVip =this.activityVipService.selectActivityById(activity_id);
            org.json.JSONObject result = new org.json.JSONObject();
            result.put("activityVip", JSON.toJSONString(activityVip));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }



    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String edit(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = activityVipService.update(message, user_id);
           // String result = activityVipService.update(message, user_id,request);
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
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 活动搜索
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String role_code = request.getSession(false).getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);

            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            PageInfo<ActivityVip> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = activityVipService.selectAllActivity(page_number, page_size, "", search_value);
            } else {

                    list = activityVipService.selectAllActivity(page_number, page_size, corp_code, search_value);
                }

            org.json.JSONObject result = new org.json.JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("search error");
        }
        return dataBean.getJsonStr();
    }


    /**
     * 活动执行状态记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/getState", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String getState(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";

        try {

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("error");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 删除活动
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String area_id = jsonObject.get("id").toString();
            String[] ids = area_id.split(",");
            String msg = "";
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                ActivityVip activityVip = activityVipService.selectActivityById(Integer.valueOf(ids[i]));
                if (activityVip == null) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage(msg);
                } else {

                    activityVipService.delete(Integer.parseInt(ids[i]));
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage("success");
                }

            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }



    /**
     * 活动筛选
     * @param request
     * @return
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";

        try {

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("screen error");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 活动任务类型
     * @param request
     * @return
     */

    @RequestMapping(value = "/taskType", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String taskType(HttpServletRequest request) throws Exception {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_code = request.getSession().getAttribute("user_code").toString();
        String jsString = request.getParameter("param");
        com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String task_type_code="";
        try {
            String corp_code = jsonObject.get("corp_code").toString();
            String search_value = "";
            if (jsonObject.containsKey("run_mode"))
                search_value = jsonObject.get("run_mode").toString();

            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
            List<TaskType> taskTypes = taskTypeService.selectCorpTaskType(corp_code, search_value);
            result.put("list", JSON.toJSONString(taskTypes));

            if (taskTypes.size() == 0) {
                Date now = new Date();
                com.alibaba.fastjson.JSONObject message1=new com.alibaba.fastjson.JSONObject();
                message1.put("task_type_code",corp_code+sdf.format(new Date()));
                message1.put("task_type_name",search_value);
                message1.put("corp_code","C10000");
                message1.put("created_date",Common.DATETIME_FORMAT.format(now));
                message1.put("modified_date",Common.DATETIME_FORMAT.format(now));
                message1.put("creater","10000");
                message1.put("modifier","10000");
                message1.put("isactive","Y");
                String result1 = taskTypeService.insertTaskType(message1.toString(), user_code);
                 task_type_code= taskTypeService.selectById(result1).getTask_type_code();
                return task_type_code;
            } else {
                task_type_code=taskTypes.get(0).getTask_type_code();
                return task_type_code;
            }
        }catch(Exception ex){
                ex.printStackTrace();
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("get tasktype error");
            }
        return task_type_code;
        }

    /**
     * 活动，点击执行
     * @param request
     * @return
     */

    @RequestMapping(value = "/executeActivity", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String executeActivity(HttpServletRequest request) throws Exception {
        DataBean dataBean = new DataBean();
        Date now = new Date();
        String id = "";
        String user_code = request.getSession().getAttribute("user_code").toString();
        String jsString = request.getParameter("param");
        JSONObject jsonObj = JSONObject.parseObject(jsString);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        JSONObject jsonObject = JSONObject.parseObject(message);
        try {
            String activity_vip_code = jsonObject.get("activity_vip_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            ActivityVip activityVip = activityVipService.selActivityByCode(corp_code, activity_vip_code);
            String run_mode = activityVip.getRun_mode();

            if (run_mode.contains("任务")){
                String task_title = activityVip.getTask_title();
                String task_desc = activityVip.getTask_desc();
                String operators = activityVip.getOperators();
                String start_time = activityVip.getStart_time();
                String end_time = activityVip.getEnd_time();

                //判断是否存在【任务类型】，没有则新建
                List<TaskType> taskTypes = taskTypeService.nameExist(corp_code, run_mode);
                String task_type_code = "";
                if (taskTypes.size() > 0){
                    task_type_code = taskTypes.get(0).getTask_type_code();
                }else {
                    JSONObject message1=new JSONObject();
                    task_type_code = "T"+Common.DATETIME_FORMAT_DAY_NUM.format(now);
                    message1.put("task_type_code",task_type_code);
                    message1.put("task_type_name",run_mode);
                    message1.put("corp_code",corp_code);
                    message1.put("isactive","Y");
                    message1.put("created_date",Common.DATETIME_FORMAT.format(now));
                    message1.put("modified_date",Common.DATETIME_FORMAT.format(now));
                    message1.put("creater",user_code);
                    message1.put("modifier",user_code);
                    taskTypeService.insertTaskType(message1.toString(), user_code);
                }

                //创建任务并分配给执行人
                JSONArray operators_array = JSONArray.parseArray(operators);
                String user_codes = "";
                String phones = "";
                for (int i = 0; i <operators_array.size() ; i++) {
                    user_codes = user_codes + operators_array.getJSONObject(i).get("user_code")+",";
                    phones = phones + operators_array.getJSONObject(i).get("phone")+",";
                }
                Task task=new Task();
                String task_code = "T" + Common.DATETIME_FORMAT_DAY_NUM.format(now) + Math.round(Math.random() * 9);
                task.setTask_code(task_code);
                task.setTask_title(task_title);
                task.setTask_type_code(task_type_code);
                task.setTask_description(task_desc);
                task.setTarget_start_time(start_time);
                task.setTarget_end_time(end_time);
                task.setCorp_code(corp_code);
                task.setCreated_date(Common.DATETIME_FORMAT.format(now));
                task.setCreater(user_code);
                task.setModified_date(Common.DATETIME_FORMAT.format(now));
                task.setModifier(user_code);
                task.setIsactive(Common.IS_ACTIVE_Y);
                taskService.addTask(task,phones,user_codes,user_code);

                //更新活动表中task_code
                activityVip.setTask_code(task_code);
                //更新活动状态activity_state
                activityVip.setActivity_state("执行中");

                activityVip.setModified_date(Common.DATETIME_FORMAT.format(now));
                activityVip.setModifier(user_code);
                activityVipService.updateActivityVip(activityVip);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("get tasktype error");
        }
        return dataBean.getJsonStr();
    }

    }

