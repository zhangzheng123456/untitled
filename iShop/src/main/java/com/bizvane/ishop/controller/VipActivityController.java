package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.OssUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.BsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Created by nanji on 2016/11/16.
 */
@Controller
@RequestMapping("/vipActivity")
public class VipActivityController {
    @Autowired
    private VipActivityService vipActivityService;
    @Autowired
    TaskService taskService;
    @Autowired
    VipFsendService vipFsendService;
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    MongoDBClient mongoDBClient;
    @Autowired
    StoreService storeService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    VipActivityDetailService vipActivityDetailService;
    @Autowired
    VipTaskService vipTaskService;
    @Autowired
    ExamineConfigureService examineConfigureService;
    @Autowired
    CorpService corpService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    ActivityService activityService;

    private static final Logger logger = Logger.getLogger(VipActivityController.class);

    String id;


    /**
     * 会员活动
     * 添加
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addActivity(HttpServletRequest request) {

        MongoTemplate mongoTemplate=this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_batch_import_vip);

        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.getString("activity_code");
            String corp_code = jsonObject.get("corp_code").toString().trim();

            String  select_scope  =jsonObject.getString("select_scope");//选择类型

            String result = "";
            String screenJs=jsonObject.getString("screen");
            if(select_scope.equals("input_file")){
                BasicDBObject basicDBObject=new BasicDBObject();
                basicDBObject.put("_id",screenJs);
                DBObject dbObject=cursor.findOne(basicDBObject);
                String cardArray=dbObject.get("cardInfo").toString();
                int target_vips_count=JSON.parseArray(cardArray).size();
                jsonObject.put("target_vips_count",String.valueOf(target_vips_count));
                jsonObject.put("target_vips",screenJs);
            }else{
                if(StringUtils.isNotBlank(screenJs)) {
                    JSONObject objectJson=JSON.parseObject(screenJs);

                    //筛选会员的条件
                    JSONArray screen = objectJson.getJSONArray("screen");
                    JSONArray post_array = vipGroupService.vipScreen2Array(screen, corp_code, role_code, brand_code, area_code, store_code, user_id);
//                String  screen_value = post_array.toJSONString();
                    System.out.println("======会员活动筛选======"+post_array.toJSONString());
                    jsonObject.put("target_vips",post_array);
                    //会员总数
//                DataBox dataBox = iceInterfaceService.vipScreenMethod2("1","2",corp_code,screen_value,"join_date","desc");
                    DataBox dataBox = vipGroupService.vipScreenBySolr(post_array,corp_code,"1","2",role_code,brand_code,area_code,store_code,user_id,"join_date","desc");
                    if (dataBox.status.toString().equals("SUCCESS")) {
                        String screenValue = dataBox.data.get("message").value;
                        JSONObject object=JSON.parseObject(screenValue);
                        String target_vips_count=object.getString("count");
                        jsonObject.put("target_vips_count",target_vips_count);
                    }
                    message=jsonObject.toJSONString();
                }
            }

            //根据活动编号判断新增活动或者编辑活动
            if (activity_code == null || activity_code.equals("")) {
                result = this.vipActivityService.insert(message, user_id);
                if (result.equals("新增失败")) {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(result);
                } else if (result.equals("该企业已存在该活动标题")) {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(result);
                } else {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage(result);
                }
            } else {
                result = this.vipActivityService.update(message, user_id);
                if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage("编辑成功");
                } else {
                    dataBean.setId(id);
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(result);
                }
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
     * 活动搜索
     *
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
        String user_code = request.getSession(false).getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            PageInfo<VipActivity> list;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = vipActivityService.selectAllActivity(page_number, page_size, "", "", search_value);
            }else {
                list = vipActivityService.selectAllActivity(page_number, page_size, corp_code, "", search_value);
            }
           list= (PageInfo<VipActivity>) vipTaskService.swicthStatus("vipActivity",list);
//            else if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
//                list = vipActivityService.selectAllActivity(page_number, page_size, corp_code, "", search_value);
//            } else {
//                list = vipActivityService.selectAllActivity(page_number, page_size, corp_code, user_code, search_value);
//            }
            JSONObject result = new JSONObject();
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
     * 删除活动
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_function_check_schedule);
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
                VipActivity activityVip = vipActivityService.getActivityById(Integer.valueOf(ids[i]));
                if (activityVip != null) {
                    String activity_code = activityVip.getActivity_code();
                    String activity_state = activityVip.getActivity_state();
                    if (activity_state.equals("1") || activity_state.equals("0.5") ) {
                        msg ="\""+activityVip.getActivity_theme() +"\"为执行中活动，不可删除";
                        break;
                    } else if (activity_state.equals("0")||activity_state.equals("2")) {
                        String task_code = activityVip.getTask_code();
                        String sms_code = activityVip.getSms_code();
                        String vip_task_code = activityVip.getVip_task();
                        if (task_code != null && !task_code.equals("")) {
                            //提示之后确定删除即删除对应任务
                            taskService.delTaskByActivityCode(activityVip.getCorp_code(), activityVip.getActivity_code());
                            //删除对应的会员任务
                            VipTask vipTask1= vipTaskService.selectVipTaskByTaskTypeAndTitle(activityVip.getCorp_code(),"activity",activity_code);
                            if(vipTask1!=null){
                                vipTaskService.deleteById(vipTask1.getId());
                            }

                        }
                        if (sms_code != null && !sms_code.equals("")) {
                            vipFsendService.delSendByActivityCode(activityVip.getCorp_code(), activityVip.getActivity_code());
                        }
                        logger.info("==========="+vip_task_code);
                        if (vip_task_code != null && !vip_task_code.equals("")){
                            vipTaskService.deleteByCode(vip_task_code);
                        }
                        BasicDBObject basicDBObject=new BasicDBObject();
                        basicDBObject.put("activity.activity_code",activity_code);
                        basicDBObject.put("examine_type","vipActivity");
                        cursor.remove(basicDBObject);
                        vipActivityDetailService.delete(activity_code);
                        vipActivityService.delete(Integer.parseInt(ids[i]));
                    }
                }
            }
            if (msg.equals("")) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();
        String user_code = request.getSession(false).getAttribute("user_code").toString();

        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            JSONObject result = new JSONObject();
            PageInfo<VipActivity> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipActivityService.selectActivityAllScreen(page_number, page_size, "", "", map);
            } else {
                list = vipActivityService.selectActivityAllScreen(page_number, page_size, corp_code, "", map);
            }
//            else if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
//                list = vipActivityService.selectActivityAllScreen(page_number, page_size, corp_code, "", map);
//            } else {
//                list = vipActivityService.selectActivityAllScreen(page_number, page_size, corp_code, user_code, map);
//            }
            list= (PageInfo<VipActivity>) vipTaskService.swicthStatus("vipActivity",list);
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 活动，完成
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Transactional
    public String saveActivity(HttpServletRequest request) throws Exception {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_code = request.getSession().getAttribute("user_code").toString();
        String group_code = request.getSession().getAttribute("group_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String jsString = request.getParameter("param");
        JSONObject jsonObj = JSONObject.parseObject(jsString);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        JSONObject jsonObject = JSONObject.parseObject(message);
        try {
            String activity_code = jsonObject.get("activity_code").toString();
            String create_task = "N";
            if (jsonObject.containsKey("create_task"))
                create_task = jsonObject.get("create_task").toString();

            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
            String activity_state = vipActivity.getActivity_state();
            if (!activity_state.equals(Common.ACTIVITY_STATUS_0)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("该任务非未执行状态");
            } else {
                String target_vips_count = vipActivity.getTarget_vips_count();
                if (target_vips_count == null || target_vips_count.equals("") || target_vips_count.equals("0")){
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("活动未覆盖会员");
                }else {
                    String result = vipActivityService.saveActivity(vipActivity, user_code,create_task,group_code,role_code);
                    if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setMessage("execute success");
                    } else {
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage(result);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("execute error");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 活动，点击执行
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/execute", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Transactional
    public String executeActivity(HttpServletRequest request) throws Exception {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongoDBClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_function_check_schedule);
        String id = "";
        String user_code = request.getSession().getAttribute("user_code").toString();
        String group_code = request.getSession().getAttribute("group_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code=request.getSession().getAttribute("corp_code").toString();


        String jsString = request.getParameter("param");
        JSONObject jsonObj = JSONObject.parseObject(jsString);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        JSONObject jsonObject = JSONObject.parseObject(message);
        try {
            String activity_codes = jsonObject.get("activity_code").toString();
            String[] activitys = activity_codes.split(",");

            for (int i = 0; i < activitys.length; i++) {
                String activity_code = activitys[i];
                VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);

                if(!role_code.equals(Common.ROLE_SYS)) {
                    if (!(vipActivity.getCreater().equals(user_code)&&corp_code.equals(vipActivity.getCorp_code()))) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("该用户不允许执行操作");
                        return dataBean.getJsonStr();
                    }
                }

                if(!vipActivity.getBill_status().equals("0")&&!vipActivity.getBill_status().equals("")){
                    ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipActivity.getCorp_code(),"会员活动");
                    if(examineConfigure!=null){
                        BasicDBObject  basic_user=new BasicDBObject();
                        basic_user.put("activity.activity_code",vipActivity.getActivity_code());
                        DBCursor dbCursor=cursor.find(basic_user);
                        if(dbCursor.count()==0){
                            dataBean.setMessage("该活动正在审核中");
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId("1");
                            return dataBean.getJsonStr();
                        }else if(dbCursor.count()>0){
                            while (dbCursor.hasNext()){
                                DBObject dbObject= dbCursor.next();
                                if(dbObject.get("status").toString().equals("2")){
                                    dataBean.setMessage("该活动正在审核中");
                                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                                    dataBean.setId("1");
                                    return dataBean.getJsonStr();
                                }
                            }
                        }
                    }
                }else{
                    dataBean.setMessage("该活动未提交审核");
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    return dataBean.getJsonStr();
                }

                String activity_theme = vipActivity.getActivity_theme();
                String activity_state = vipActivity.getActivity_state();
                if (!activity_state.equals(Common.ACTIVITY_STATUS_0)) {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(activity_theme+"非未执行状态");
                    return dataBean.getJsonStr();
                } else {
                    String target_vips_count = vipActivity.getTarget_vips_count();
                    if (target_vips_count == null || target_vips_count.equals("") || target_vips_count.equals("0")){
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage(activity_theme+"未覆盖会员");
                        return dataBean.getJsonStr();
                    }else {
                        String result = vipActivityService.executeActivity(vipActivity, user_code,"",group_code,role_code);
                        if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                            dataBean.setId(id);
                            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                            dataBean.setMessage("execute success");
                        } else {
                            dataBean.setId(id);
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setMessage(result);
                            return dataBean.getJsonStr();
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("execute error");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 获取活动详细信息
     *
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.getString("activity_code");
            VipActivity activityVip = vipActivityService.selActivityByCode(activity_code);
            JSONObject result = new JSONObject();
            if (activityVip.getRun_mode().equals("register")){
                Corp corp = corpService.selectByCorpId(0,activityVip.getCorp_code(),"Y");
                JSONObject object = new JSONObject();
                object.put("pic",corp.getAvatar());
                object.put("title","您的好友邀请您注册啦");
                object.put("desc","注册成为会员即刻享受优惠");
                object.put("url",CommonValue.ishop_url+"mobile/task/link.html");
                result.put("share", object);
            }
            activityVip.setRun_mode(CheckUtils.CheckVipActivityType(activityVip.getRun_mode()));
            VipTask vipTask1= vipTaskService.selectVipTaskByTaskTypeAndTitle(activityVip.getCorp_code(),"activity",activity_code);
            if(vipTask1!=null){
                result.put("have_task","Y");
            }else{
                result.put("have_task","N");
            }

//            String store_code = vipActivityService.getUnAllocStore(activityVip);
            result.put("activityVip", JSON.toJSONString(activityVip));
            result.put("noTaskStore", "");

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 验证会员类型名称的唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/activityThemeExist", method = RequestMethod.POST)
    @ResponseBody
    public String themeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_theme = jsonObject.get("activity_theme").toString().trim();
            String activity_code = jsonObject.get("activity_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            VipActivity vipActivity = vipActivityService.getVipActivityByTheme(corp_code, activity_theme);

            if (vipActivity == null){
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("当前企业下该会员活动标题不存在");
            } else {
                VipActivity  vipActivity1=vipActivityService.getActivityByCode(activity_code);
                if(vipActivity1!=null&&vipActivity1.getId()==vipActivity.getId()){
                    dataBean.setId(id);

                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage("当前企业下该会员活动标题不存在");
                }else{
                    logger.info("themeExist---------------------");
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("当前企业下该会员活动标题已存在");

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }



    /**
     * 活动执行中
     * 提前终止
     * 修改活动时间
     * 添加活动店铺
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/changeState", method = RequestMethod.POST)
    @ResponseBody
    public String changeState(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.getString("activity_code");

            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
            String corp_code  = vipActivity.getCorp_code();

            dataBean.setMessage("success");
            if (vipActivity != null) {
                if (jsonObject.containsKey("status") && !jsonObject.getString("status").equals("")){
                    String activity_state = vipActivity.getActivity_state();
                    if (activity_state.equals(Common.ACTIVITY_STATUS_1) || activity_state.equals("0.5")) {
                        vipActivityService.terminalAct(vipActivity,user_code);
                    }
                }else if (jsonObject.containsKey("end_time")){
                    String end_time = jsonObject.getString("end_time");
                    if(!end_time.equals("")){
                        String src_end_time = vipActivity.getEnd_time();
                        if (!src_end_time.equals(end_time)){
                            vipActivity.setEnd_time(end_time);
                            vipActivity.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                            vipActivity.setModifier(user_code);
                            vipActivityService.updateVipActivity(vipActivity);
                            vipActivityService.insertSchedule(activity_code,corp_code,end_time,user_code,"changeStatus");
                        }
                    }else{
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage("活动时间为空");
                    }
                }else if (jsonObject.containsKey("store_code") && !jsonObject.getString("store_code").equals("")){
                    int count = 0;
                    //修改门店后返回所有数据
                    String store_code = jsonObject.getString("store_code");
                    JSONArray stores = JSONArray.parseArray(store_code);

                    String new_stores = "";
                    for (int i = 0; i < stores.size(); i++) {
                        JSONObject store_obj = stores.getJSONObject(i);
                        new_stores = new_stores + store_obj.getString("store_code") + ",";
                    }
                    String run_scope = vipActivity.getRun_scope();
                    JSONObject scope_obj = JSONObject.parseObject(run_scope);
                    String code = scope_obj.get("store_code").toString();
                    String area_code = scope_obj.get("area_code").toString();
                    String brand_code = scope_obj.get("brand_code").toString();
                    if (!code.equals("") || !area_code.equals("") || !brand_code.equals("")){
                        if (code.endsWith(",")){
                            code = code + new_stores;
                        }else {
                            code = code + "," + new_stores;
                        }
                        scope_obj.put("store_code",code);

                        //若新增了门店，则修改，并返回未结束任务数
                        vipActivity.setRun_scope(scope_obj.toString());
                        vipActivityService.updateVipActivity(vipActivity);
                        count = vipActivityService.unComplTask(corp_code,activity_code).size();
                    }
                    JSONObject count_obj = new JSONObject();
                    count_obj.put("count",count);
                    dataBean.setMessage(count_obj.toString());
                }
            }
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 获取活动未完成任务
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/unComplTask", method = RequestMethod.POST)
    @ResponseBody
    public String unComplTask(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String activity_code = jsonObject.getString("activity_code");
            List<Task> list = vipActivityService.unComplTask(corp_code,activity_code);
            JSONObject obj = new JSONObject();
            obj.put("list",list);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 给新增门店分配未完成任务
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/allocUnComplTask", method = RequestMethod.POST)
    @ResponseBody
    public String allocUnComplTask(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String task_code = jsonObject.getString("task_code");
            String activity_code = jsonObject.getString("activity_code");
            String store_code = jsonObject.getString("store_code");

            String result = vipActivityService.allocUnComplTask(corp_code,activity_code,task_code,store_code, user_code);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("execute success");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
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
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getSku", method = RequestMethod.POST)
    @ResponseBody
    public String getSku(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String page_size = jsonObject.getString("page_size");
            String page_num = jsonObject.getString("page_num");
            String param = jsonObject.getString("param");

            String type = "";
            if (!CheckUtils.checkJson(param)){
                type = "direct";
            }
            DataBox result = iceInterfaceAPIService.getSku(corp_code,page_num,page_size,param,type);
            if (result.status.toString().equals("SUCCESS")) {
                JSONObject sku = JSONObject.parseObject(result.data.get("message").value);
                JSONArray sku_list = sku.getJSONArray("sku_list");
                String image_path = "http://img-oss.bizvane.com/StyleImgs/";
                String image_size = "@200w_200h";
                for (int i = 0; i < sku_list.size(); i++) {
                    JSONObject sku_obj = sku_list.getJSONObject(i);
                    String brand_id = sku_obj.getString("BRAND_ID");

                    if (corp_code.equals("C10016") && (brand_id.equals("481") || brand_id.equals("480") || brand_id.equals("1205") || brand_id.equals("1305"))){
                        brand_id = "101";
                    }
                    if (corp_code.equals("C10016") && brand_id.equals("912")){
                        brand_id = "102";
                    }
                    if (corp_code.equals("C10016") && brand_id.equals("937")){
                        brand_id = "103";
                    }
                    if (corp_code.equals("C10016") && (brand_id.equals("1575") || brand_id.equals("1576"))){
                        brand_id = "104";
                    }
                    if (corp_code.equals("C10016") && brand_id.equals("1600")){
                        brand_id = "105";
                    }
                    String product_code = sku_obj.getString("PRODUCT_CODE");
                    String img = image_path + corp_code + "/" + brand_id + "/" + product_code.replace("*","") + ".jpg" + image_size;
                    if (0 != CheckUtils.isConnect(img)) {
                        img = image_path + corp_code + "/" + brand_id + "/" + product_code.replace("*","") + ".JPG" + image_size;
                    }
                    sku_obj.put("PRODUCT_IMG",img);
                }
                int count = Integer.parseInt(sku.getString("count"));
                int size = Integer.parseInt(page_size);
                int pages = 0;
                if (count % size == 0) {
                    pages = count / size;
                } else {
                    pages = count / size + 1;
                }
                if (pages == Integer.parseInt(page_num) ){
                    sku.put("lastPage","true");

                }else {
                    sku.put("lastPage","false");

                }
                sku.put("page_size",page_size);
                sku.put("page_num",page_num);
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(sku.toString());
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("获取数据失败");
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
     * 获取活动门店
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getStoreList", method = RequestMethod.POST)
    @ResponseBody
    public String getStoreList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.getString("activity_code");
            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
            JSONObject result = new JSONObject();
            List<Store> stores = new ArrayList<Store>();
            if (vipActivity.getRun_scope() != null && !vipActivity.getRun_scope().equals("")) {
                stores = vipActivityService.getActivityStore(vipActivity.getCorp_code(),vipActivity.getRun_scope());
            }
            result.put("storeList",stores);
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

    @RequestMapping(value = "/getAllActivityBySate", method = RequestMethod.POST)
    @ResponseBody
    public String getAllActivityByState(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String role_code=request.getSession(false).getAttribute("role_code").toString();
            String corp_code=request.getSession(false).getAttribute("corp_code").toString();
            String corpCode=jsonObject.getString("corp_code");
            if(role_code.equals(Common.ROLE_SYS)){
                if(StringUtils.isNotBlank(corpCode)){
                    corp_code=corpCode;
                }else{
                    corp_code="C10000";
                }
            }
            List<VipActivity> list;
            list = vipActivityService.selectAllActivityByState(corp_code, "");
            JSONObject result = new JSONObject();
            List<HashMap<String,Object>> array=new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i <list.size() ; i++) {
                HashMap<String,Object> map=new HashMap<String, Object>();
                VipActivity vipActivity=list.get(i);
                map.put("activity_theme",vipActivity.getActivity_theme());
                map.put("activity_code",vipActivity.getActivity_code());
                array.add(map);
            }
            result.put("list", array);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("error");
        }
        return dataBean.getJsonStr();
    }


    /**
     * 活动，点击执行
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/executetest", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Transactional
    public String executetest(HttpServletRequest request) throws Exception {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_code = request.getSession().getAttribute("user_code").toString();
        String group_code = request.getSession().getAttribute("group_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String activity_codes = "AC1018320170929194613826\n" +
                "AC1018320170929194613837\n" +
                "AC1018320170929194613847\n" +
                "AC1018320170929194613858\n" +
                "AC1018320170929194613868\n" +
                "AC1018320170929194613886\n" +
                "AC1018320170929194613900\n" +
                "AC1018320170929194613912\n" +
                "AC1018320170929194613923\n" +
                "AC1018320170929194613935\n" +
                "AC1018320170929194613951\n" +
                "AC1018320170929194613962\n" +
                "AC1018320170929194613973\n" +
                "AC1018320170929194613985\n" +
                "AC1018320170929194613998\n" +
                "AC1018320170929194614009\n" +
                "AC1018320170929194614020\n" +
                "AC1018320170929194614031\n" +
                "AC1018320170929194614044\n" +
                "AC1018320170929194614054\n" +
                "AC1018320170929194614065\n" +
                "AC1018320170929194614077\n" +
                "AC1018320170929194614088\n" +
                "AC1018320170929194614099\n" +
                "AC1018320170929194614110\n" +
                "AC1018320170929194614120\n" +
                "AC1018320170929194614131\n" +
                "AC1018320170929194614142\n" +
                "AC1018320170929194614154\n" +
                "AC1018320170929194614166\n" +
                "AC1018320170929194614177\n" +
                "AC1018320170929194614188\n" +
                "AC1018320170929194614199\n" +
                "AC1018320170929194614209\n" +
                "AC1018320170929194614220\n" +
                "AC1018320170929194614231\n" +
                "AC1018320170929194614242\n" +
                "AC1018320170929194614255\n" +
                "AC1018320170929194614266\n" +
                "AC1018320170929194614277\n" +
                "AC1018320170929194614290\n" +
                "AC1018320170929194614301\n" +
                "AC1018320170929194614311\n" +
                "AC1018320170929194614323\n" +
                "AC1018320170929194614334\n" +
                "AC1018320170929194614346\n" +
                "AC1018320170929194614357\n" +
                "AC1018320170929194614368\n" +
                "AC1018320170929194614379\n" +
                "AC1018320170929194614390\n" +
                "AC1018320170929194614401\n" +
                "AC1018320170929194614413\n" +
                "AC1018320170929194614423\n" +
                "AC1018320170929194614434\n" +
                "AC1018320170929194614445\n" +
                "AC1018320170929194614457\n" +
                "AC1018320170929194614469\n" +
                "AC1018320170929194614480\n" +
                "AC1018320170929194614491\n" +
                "AC1018320170929194614508\n" +
                "AC1018320170929194614523\n" +
                "AC1018320170929194614535\n" +
                "AC1018320170929194614546\n" +
                "AC1018320170929194614558\n" +
                "AC1018320170929194614569\n" +
                "AC1018320170929194614580\n" +
                "AC1018320170929194614592\n" +
                "AC1018320170929194614603\n" +
                "AC1018320170929194614614\n" +
                "AC1018320170929194614625\n" +
                "AC1018320170929194614636\n" +
                "AC1018320170929194614648\n" +
                "AC1018320170929194614665\n" +
                "AC1018320170929194614677\n" +
                "AC1018320170929194614689\n" +
                "AC1018320170929194614699\n" +
                "AC1018320170929194614710\n" +
                "AC1018320170929194614722\n" +
                "AC1018320170929194614733\n" +
                "AC1018320170929194614744\n" +
                "AC1018320170929194614757\n" +
                "AC1018320170929194614770\n" +
                "AC1018320170929194614780\n" +
                "AC1018320170929194614791\n" +
                "AC1018320170929194614802\n" +
                "AC1018320170929194614813\n" +
                "AC1018320170929194614823\n" +
                "AC1018320170929194614834\n" +
                "AC1018320170929194614845\n" +
                "AC1018320170929194614857\n" +
                "AC1018320170929194614867\n" +
                "AC1018320170929194614879\n" +
                "AC1018320170929194614890\n" +
                "AC1018320170929194614902\n" +
                "AC1018320170929194614913\n" +
                "AC1018320170929194614925\n" +
                "AC1018320170929194614936\n" +
                "AC1018320170929194614947\n" +
                "AC1018320170929194614958\n" +
                "AC1018320170929194614969\n" +
                "AC1018320170929194614981\n" +
                "AC1018320170929194614993\n" +
                "AC1018320170929194615004\n" +
                "AC1018320170929194615016\n" +
                "AC1018320170929194615028\n" +
                "AC1018320170929194615041\n" +
                "AC1018320170929194615054\n" +
                "AC1018320170929194615066\n" +
                "AC1018320170929194615077\n" +
                "AC1018320170929194615088\n" +
                "AC1018320170929194615099\n" +
                "AC1018320170929194615110\n" +
                "AC1018320170929194615121\n" +
                "AC1018320170929194615132\n" +
                "AC1018320170929194615143\n" +
                "AC1018320170929194615154\n" +
                "AC1018320170929194615165\n" +
                "AC1018320170929194615176\n" +
                "AC1018320170929194615187\n" +
                "AC1018320170929194615198\n" +
                "AC1018320170929194615210\n" +
                "AC1018320170929194615221\n" +
                "AC1018320170929194615233\n" +
                "AC1018320170929194615245\n" +
                "AC1018320170929194615259\n" +
                "AC1018320170929194615271\n" +
                "AC1018320170929194615283\n" +
                "AC1018320170929194615295\n" +
                "AC1018320170929194615306\n" +
                "AC1018320170929194615316\n" +
                "AC1018320170929194615327\n" +
                "AC1018320170929194615339\n" +
                "AC1018320170929194615349\n" +
                "AC1018320170929194615362\n" +
                "AC1018320170929194615373\n" +
                "AC1018320170929194615385\n" +
                "AC1018320170929194615398\n" +
                "AC1018320170929194615408\n" +
                "AC1018320170929194615421\n" +
                "AC1018320170929194615432\n" +
                "AC1018320170929194615442\n" +
                "AC1018320170929194615456\n" +
                "AC1018320170929194615469\n" +
                "AC1018320170929194615480\n" +
                "AC1018320170929194615491\n" +
                "AC1018320170929194615501\n" +
                "AC1018320170929194615513\n" +
                "AC1018320170929194615526\n" +
                "AC1018320170929194615538\n" +
                "AC1018320170929194615550\n" +
                "AC1018320170929194615562\n" +
                "AC1018320170929194615573\n" +
                "AC1018320170929194615588\n" +
                "AC1018320170929194615600\n" +
                "AC1018320170929194615612\n" +
                "AC1018320170929194615626\n" +
                "AC1018320170929194615637\n" +
                "AC1018320170929194615654\n" +
                "AC1018320170929194615667\n" +
                "AC1018320170929194615679\n" +
                "AC1018320170929194615690\n" +
                "AC1018320170929194615702\n" +
                "AC1018320170929194615714\n" +
                "AC1018320170929194615726\n" +
                "AC1018320170929194615738\n" +
                "AC1018320170929194615753\n" +
                "AC1018320170929194615764\n" +
                "AC1018320170929194615776\n" +
                "AC1018320170929194615788\n" +
                "AC1018320170929194615798\n" +
                "AC1018320170929194615810\n" +
                "AC1018320170929194615823\n" +
                "AC1018320170929194615835\n" +
                "AC1018320170929194615847\n" +
                "AC1018320170929194615859\n" +
                "AC1018320170929194615870\n" +
                "AC1018320170929194615881\n" +
                "AC1018320170929194615893\n" +
                "AC1018320170929194615904\n" +
                "AC1018320170929194615915\n" +
                "AC1018320170929194615926\n" +
                "AC1018320170929194615936\n" +
                "AC1018320170929194615952\n" +
                "AC1018320170929194615963\n" +
                "AC1018320170929194615975\n" +
                "AC1018320170929194615987\n" +
                "AC1018320170929194615998\n" +
                "AC1018320170929194616012\n" +
                "AC1018320170929194616023\n" +
                "AC1018320170929194616034\n" +
                "AC1018320170929194616044\n" +
                "AC1018320170929194616056\n" +
                "AC1018320170929194616069\n" +
                "AC1018320170929194616080\n" +
                "AC1018320170929194616092\n" +
                "AC1018320170929194616104\n" +
                "AC1018320170929194616115\n" +
                "AC1018320170929194616126\n" +
                "AC1018320170929194616136\n" +
                "AC1018320170929194616150\n" +
                "AC1018320170929194616161\n" +
                "AC1018320170929194616172\n" +
                "AC1018320170929194616184\n" +
                "AC1018320170929194616195\n" +
                "AC1018320170929194616207\n" +
                "AC1018320170929194616217\n" +
                "AC1018320170929194616230\n" +
                "AC1018320170929194616241\n" +
                "AC1018320170929194616253\n" +
                "AC1018320170929194616264\n" +
                "AC1018320170929194616276\n" +
                "AC1018320170929194616286\n" +
                "AC1018320170929194616298\n" +
                "AC1018320170929194616309\n" +
                "AC1018320170929194616320\n" +
                "AC1018320170929194616331\n" +
                "AC1018320170929194616342\n" +
                "AC1018320170929194616354\n" +
                "AC1018320170929194616366\n" +
                "AC1018320170929194616378\n" +
                "AC1018320170929194616389\n" +
                "AC1018320170929194616400\n" +
                "AC1018320170929194616410\n" +
                "AC1018320170929194616425\n" +
                "AC1018320170929194616436\n" +
                "AC1018320170929194616447\n" +
                "AC1018320170929194616458\n" +
                "AC1018320170929194616470\n" +
                "AC1018320170929194616480\n" +
                "AC1018320170929194616493\n" +
                "AC1018320170929194616505\n" +
                "AC1018320170929194616519\n" +
                "AC1018320170929194616531\n" +
                "AC1018320170929194616543\n" +
                "AC1018320170929194616555\n" +
                "AC1018320170929194616568\n" +
                "AC1018320170929194616581\n" +
                "AC1018320170929194616594\n" +
                "AC1018320170929194616606\n" +
                "AC1018320170929194616618\n" +
                "AC1018320170929194616631\n" +
                "AC1018320170929194616642\n" +
                "AC1018320170929194616655\n" +
                "AC1018320170929194616667\n" +
                "AC1018320170929194616679\n" +
                "AC1018320170929194616690\n" +
                "AC1018320170929194616701\n" +
                "AC1018320170929194616712\n" +
                "AC1018320170929194616723\n" +
                "AC1018320170929194616736\n" +
                "AC1018320170929194616747\n" +
                "AC1018320170929194616758\n" +
                "AC1018320170929194616769\n" +
                "AC1018320170929194616780\n" +
                "AC1018320170929194616790\n" +
                "AC1018320170929194616803\n" +
                "AC1018320170929194616814\n" +
                "AC1018320170929194616825\n" +
                "AC1018320170929194616836\n" +
                "AC1018320170929194616848\n" +
                "AC1018320170929194616859\n" +
                "AC1018320170929194616869\n" +
                "AC1018320170929194616881\n" +
                "AC1018320170929194616893\n" +
                "AC1018320170929194616905\n" +
                "AC1018320170929194616920\n" +
                "AC1018320170929194616931\n" +
                "AC1018320170929194616943\n" +
                "AC1018320170929194616955\n" +
                "AC1018320170929194616966\n" +
                "AC1018320170929194616979\n" +
                "AC1018320170929194616990\n" +
                "AC1018320170929194617004\n" +
                "AC1018320170929194617017\n" +
                "AC1018320170929194617029\n" +
                "AC1018320170929194617044\n" +
                "AC1018320170929194617056\n" +
                "AC1018320170929194617068\n" +
                "AC1018320170929194617082\n" +
                "AC1018320170929194617095\n" +
                "AC1018320170929194617106\n" +
                "AC1018320170929194617121\n" +
                "AC1018320170929194617133\n" +
                "AC1018320170929194617146\n" +
                "AC1018320170929194617159\n" +
                "AC1018320170929194617170\n" +
                "AC1018320170929194617181\n" +
                "AC1018320170929194617194\n" +
                "AC1018320170929194617205\n" +
                "AC1018320170929194617217\n" +
                "AC1018320170929194617234\n" +
                "AC1018320170929194617246\n" +
                "AC1018320170929194617259\n";
        try {
            String[] codes = activity_codes.split("\n");

            for (int i = 0; i < codes.length; i++) {
                String activity_code = codes[i];
                String create_task = "N";

                VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);

                String result = vipActivityService.executeActivity(vipActivity, user_code,create_task,group_code,role_code);
                if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage("execute success");
                } else {
                    MongoTemplate mongoTemplate = mongoDBClient.getMongoTemplate();//获取模板
                    DBCollection cursor = mongoTemplate.getCollection("vip_activity_C10183");
                    BasicDBObject object = new BasicDBObject();
                    object.put("activity_code",activity_code);
                    cursor.save(object);
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(result);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("execute error");
        }
        return dataBean.getJsonStr();
    }

    //判断批次号是否存在
    @RequestMapping(value = "/existBatchNo", method = RequestMethod.POST)
    @ResponseBody
    public String existBatchNo(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String corp_code=jsonObject.getString("corp_code");
            String batch_no=jsonObject.getString("batch_no");

            List<VipActivity> vipActivities = vipActivityService.getVipActivityByCorpCode(corp_code,"","");

            Set<String> batchs = new HashSet<String>();
             for (int i = 0; i < vipActivities.size(); i++) {
                 if (vipActivities.get(i).getBatch_no() != null){
                     String[] batch = vipActivities.get(i).getBatch_no().split(",");
                     for (int j = 0; j < batch.length; j++) {
                         batchs.add(batch[j]);
                     }
                 }
            }
            if (batchs.contains(batch_no)){
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("批次号已使用");
            }else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("error");
        }
        return dataBean.getJsonStr();
    }

    /***
     * Execl增加   活动导入会员
     */
    @RequestMapping(value = "/excludeMultipart", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String addByExecl(HttpServletRequest request){
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_batch_import_vip);
        DataBean dataBean = new DataBean();
        String result = "";
        Workbook rwb = null;
        try {
            //创建你要保存的文件的路径
            String path = request.getSession().getServletContext().getRealPath("lupload");
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> items=upload.parseRequest(request);
            Iterator<FileItem> iter = items.iterator() ;//将全部的内容变为Iterator实例
            List<HashMap<String,Object>> list=new ArrayList<HashMap<String, Object>>();
            while (iter.hasNext()){
                HashMap<String,Object> map=new HashMap<String, Object>();
                FileItem item= iter.next();
                String fieldName = item.getFieldName() ;  // 取得表单控件的名称
                if(!item.isFormField()){    // 判断是否为普通文本
                    //写入文件
                    String filename="BizvaneUpload_"+Common.DATETIME_FORMAT_NUM.format(new Date())+".xls";
                    InputStream inputStream= item.getInputStream();
                    OutExeclHelper outExecl=new OutExeclHelper();
                    outExecl.writeFile(path,filename,inputStream);//写入文件

                    //上传文件到oss
                    String file_name = item.getName();  // 取得文件的名称
                    logger.info("loggggggggggggggggggg"+fieldName+"        "+file_name);
                    String filepath=path+"/"+filename;
                    String bucketName = "products-image";
                    OssUtils ossUtils = new OssUtils();
                    String time = file_name.split("\\.")[0]+"_"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date())+".xls";
                    ossUtils.putObject(bucketName, time, filepath);
                    String oss_path= "http://" + bucketName + ".oss-cn-hangzhou.aliyuncs.com/" + time;

                    map.put("oss_path",oss_path);
                    map.put("name",fieldName);
                    map.put("value",filename);
                } else {
                    String value = item.getString();
                    map.put("name", fieldName);
                    map.put("value", value);
                }
                list.add(map);
            }


            String file_name="";
            String upload_type="";
            String corp_code="";
            String oss_path="";
            //创建你要保存的文件的路径
            for (int i = 0; i < list.size(); i++) {
                HashMap<String,Object> map=list.get(i);
                String name=map.get("name").toString();
                String value= map.get("value").toString();
                if(name.equals("type")){
                    upload_type=value;
                }
                if(name.equals("corp_code")){
                    corp_code=value;
                }
                if(name.equals("file")){
                    file_name=value;
                    oss_path=map.get("oss_path").toString();
                }
            }

            File targetFile=new File(path,file_name);
            if(!targetFile.exists()){
                targetFile.createNewFile();
            }

            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int rows = rs.getRows();//得到所有的行

            System.out.println("行数........"+rows);

            if (rows < 4) {
                result = "：请从模板第4行开始插入正确数据";
                if(targetFile.exists()){
                    targetFile.delete();
                }
                int i = 5 / 0;
            }

            if (rows > 100005) {
                result = "：数据量过大，导入失败";
                if(targetFile.exists()){
                    targetFile.delete();
                }
                int i = 5 / 0;
            }

            HashSet cardList = new HashSet();
            for (int i = 3; i < rows; i++) {
                    String card_no = rs.getCell(0, i).getContents().toString().trim();
                    if (StringUtils.isBlank(card_no)) {
                        continue;
                    }
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("cardno",card_no.trim());
                    cardList.add(jsonObject);
            }

            //删除临时文件
            if(targetFile.exists()){
                targetFile.delete();
            }
            long time=System.currentTimeMillis();

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            Date date = c.getTime();

            String _id=String.valueOf(time);
            _id="BIZVANE_"+_id;
            BasicDBObject basicDBObject=new BasicDBObject();
            basicDBObject.put("_id",_id);
            BsonArray bsonValues=new BsonArray();
            bsonValues.addAll(cardList);
            basicDBObject.put("cardInfo",bsonValues);
            basicDBObject.put("type",upload_type);
            basicDBObject.put("corp_code",corp_code);
            basicDBObject.put("modified_date",Common.DATETIME_FORMAT.format(date));
            basicDBObject.put("oss_path",oss_path);
            cursor.insert(basicDBObject);

            JSONObject jsonObject=new JSONObject();
            jsonObject.put("file_id",_id);
            jsonObject.put("cardno_num",cardList.size());

            dataBean.setMessage(jsonObject.toString());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setMessage(result);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
        }finally {
            if (rwb != null) {
                rwb.close();
            }
        }
        return  dataBean.getJsonStr();
    }


    //文件导出
    @RequestMapping(value = "/fileOutExecl", method = RequestMethod.POST)
    @ResponseBody
    public  String outExecl(HttpServletRequest request, HttpServletResponse response){
        DataBean dataBean=new DataBean();
        String errormessage = "数据异常，导出失败";
        try{
            MongoTemplate mongoTemplate=this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_batch_import_vip);
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject message_obj = JSON.parseObject(message);
            String file_id=message_obj.getString("id");//调整单id

            BasicDBObject basicDBObject=new BasicDBObject();
            basicDBObject.put("_id",file_id);
            DBObject dbObject=cursor.findOne(basicDBObject);
            String oss_path=dbObject.get("oss_path").toString();
            JSONObject result2=new JSONObject();
            result2.put("path", oss_path);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result2.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
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
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_batch_import_vip);
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.get("activity_code").toString();
            BasicDBObject basicDBObject=new BasicDBObject();
            basicDBObject.put("activity_code",activity_code);
            basicDBObject.put("type","activity");
            DBCursor dbObjects=cursor.find(basicDBObject);
            JSONArray jsonArray=new JSONArray();
            for(DBObject dbObject:dbObjects){
                String card_info=dbObject.get("cardInfo").toString();
                jsonArray=JSON.parseArray(card_info);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(jsonArray);
            if (jsonArray.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cardno","会员卡号");

            String pathname = OutExeclHelper.OutExecl2(json, jsonArray, map, response, request, "会员详情");
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
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value="/exportExcel",method=RequestMethod.POST)
    @ResponseBody
    public String exportExcel(HttpServletRequest request,HttpServletResponse response){

        System.out.println("导出excel");
        DataBean dataBean=new DataBean();
        String errormessage="数据异常，导出失败";
        String role_code=request.getSession().getAttribute("role_code").toString();
        String corp_code=request.getSession().getAttribute("corp_code").toString();
        String jsonStr=request.getParameter("param");
        JSONObject jsonobj=JSON.parseObject(jsonStr);
        String message=jsonobj.get("message").toString();
        JSONObject jsonobject=JSON.parseObject(message);
        String search_value=jsonobject.get("searchValue").toString();
        String screen=jsonobject.get("list").toString();
        PageInfo<VipActivity> activy=null;

        if (screen.equals("")) {
            activy=activityService.selectAllActivity(1,900,search_value);
        } else {
            Map<String, String> map = WebUtils.Json2Map(jsonobject);
            if (role_code.equals(Common.ROLE_SYS)) {
                try {
                    activy = activityService.selectAllCorpScreen(1, 900, "", "", map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    activy = activityService.selectAllCorpScreen(1, 900, corp_code, "", map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        List<VipActivity> vipacts=activy.getList();
        ObjectMapper objmapper=new ObjectMapper();
        objmapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = null;
        try {
            json = objmapper.writeValueAsString(vipacts);
            if(vipacts.size()>=Common.EXPORTEXECLCOUNT){
                errormessage="数据量过大";
                int i=9/0;
            }

            LinkedHashMap<String,String>  hashmap=WebUtils.Json2ShowName(jsonobject);
            String pathname = OutExeclHelper.OutExecl(json, vipacts, hashmap, response, request,"活动列表");
            JSONObject result = new JSONObject();
            System.out.println(pathname+"-------------");
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }

            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (JsonProcessingException e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }


}

