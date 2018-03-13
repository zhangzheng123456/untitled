package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.service.imp.NetWorkServiceImpl;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yanyadong on 2017/4/24.
 */
@Controller
@RequestMapping("/vipTask")
public class VipTaskController {

    @Autowired
    VipTaskService vipTaskService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    CorpService corpService;
    @Autowired
    CRMInterfaceService crmInterfaceService;
    @Autowired
    private BaseService baseService;
    @Autowired
    MongoDBClient mongoDBClient;
    @Autowired
    VipFsendService vipFsendService;
    @Autowired
    NetWorkServiceImpl netWorkService;

    @Autowired
    VipParamService vipParamService;

    @Autowired
    ExamineConfigureService examineConfigureService;

    @Autowired
    FunctionService functionService;

    @Autowired
    VipActivityService vipActivityService;

    @Autowired
    UserService userService;

    private static final Logger logger = Logger.getLogger(VipTaskController.class);

    String id="";
    @RequestMapping(value = "/select",method = RequestMethod.POST)
    @ResponseBody
    public String selectVipTask(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            int task_id=Integer.parseInt(jsonObject.get("id").toString());
            VipTask vipTask=vipTaskService.selectById(task_id);
            String task_type=vipTask.getTask_type();
            String url = "";
            String title = "";
            if(task_type.equals("share_goods")){
                title = "分享商品";
            }else if(task_type.equals("share_counts")){
                title = "分享链接";
            }else if(task_type.equals("invite_registration")){
                title = "邀请注册";
            }
            VipTask vipTask1=vipTaskService.switchTaskType(vipTask,request);
            vipTask1 = vipTaskService.selectTargetCount(vipTask1);

            String app_id = vipTask1.getApp_id();
            CorpWechat corpWechat = corpService.getCorpByAppId(vipTask1.getCorp_code(),app_id);
            String app_name = "";
            if (corpWechat != null)
                app_name = corpWechat.getApp_name();
            vipTask1.setApp_name(app_name);

            JSONObject result = new JSONObject();
            result.put("share_title",title);
            result.put("share_url",url);
            result.put("task",vipTask1);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/search",method = RequestMethod.POST)
    @ResponseBody
    public String selectAllVipTask(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            int  page_number=Integer.parseInt(jsonObject.getString("pageNumber").toString());
            int page_size=Integer.parseInt(jsonObject.getString("pageSize").toString());
            String search_value=jsonObject.getString("searchValue").toString();
            PageInfo<VipTask> list=null;
            if(role_code.equals(Common.ROLE_SYS)){
                list=vipTaskService.selectAll(page_number,page_size,"",search_value);
            }else{
                list=vipTaskService.selectAll(page_number,page_size,corp_code,search_value);
            }
            list= vipTaskService.switchTaskType(list);
            list= (PageInfo<VipTask>) vipTaskService.swicthStatus("vipTask",list);
            JSONObject result=new JSONObject();
            result.put("list",JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String deleteVipTask(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_function_check_schedule);
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            String task_id=jsonObject.get("id").toString();
            String[] ids=task_id.split(",");

            for(int i=0;i<ids.length;i++){
                VipTask vipTask=vipTaskService.selectById(Integer.valueOf(ids[i]));
                if (vipTask != null){
                    vipFsendService.delSendByActivityCode(vipTask.getCorp_code(),"VIPTASK"+vipTask.getTask_code());
                    String type= vipTask.getTask_type();
                    String desc= vipTask.getTask_description();
                    String title= vipTask.getTask_title();
                    String corp= vipTask.getCorp_code();
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
                    String function = "会员管理_会员任务";
                    String action = Common.ACTION_DEL;
                    String t_corp_code = corp;
                    String t_code = title;
                    String t_name = type;
                    String remark = desc;
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                    //-------------------行为日志结束-----------------------------------------------------------------------------------

                    BasicDBObject basicDBObject=new BasicDBObject();
                    basicDBObject.put("vipTask.task_code",vipTask.getTask_code());
                    basicDBObject.put("examine_type","vipTask");
                    cursor.remove(basicDBObject);
                    vipTaskService.deleteById(Integer.parseInt(ids[i]));

                }
            }
            // JSONObject result=new JSONObject();
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("success");
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();

    }

    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    @ResponseBody
    public String insertVipTask(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String role_code=request.getSession().getAttribute("role_code").toString();
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code = request.getSession().getAttribute("area_code").toString();
            String store_code = request.getSession().getAttribute("store_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            List<VipTask> vipTask1=vipTaskService.selectByTaskTitle(jsonObject.getString("corp_code").toString(),
                    jsonObject.get("task_title").toString());
            if(vipTask1.size()>0){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("该任务名称已存在");
                return dataBean.getJsonStr();
            }

            VipTask vipTask2=WebUtils.JSON2Bean(jsonObject,VipTask.class);
            String corpCode=jsonObject.getString("corp_code").toString();
            String task_code="VT"+corpCode+Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
            vipTask2.setTask_code(task_code);
            vipTask2.setTask_status(Common.ACTIVITY_STATUS_0);
            vipTask2.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            vipTask2.setModifier(user_code);
            //创建时间
            vipTask2.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
            vipTask2.setCreater(user_code);
            vipTask2.setIsactive(jsonObject.get("isactive").toString());

            //转换vip
            if(vipTask2.getSelect_scope().equals("condition_vip")){
                JSONArray array = JSONArray.parseArray(vipTask2.getTarget_vips());
                array = vipGroupService.vipScreen2Array(array,corpCode,role_code,brand_code,area_code,store_code,user_code);
                vipTask2.setTarget_vips_(JSON.toJSONString(array));
            }

            String flag = vipTaskService.inserVipTask(vipTask2,user_code,group_code,role_code);
            if (!flag.equals(Common.DATABEAN_CODE_SUCCESS)){
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(flag);
                return  dataBean.getJsonStr();
            }
            JSONObject result=new JSONObject();
            VipTask vipTask3= vipTaskService.selectByTaskCode(task_code);
            result.put("id",vipTask3.getId());
            result.put("task_status",vipTask3.getTask_status());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");

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
            String function = "会员管理_会员任务";
            String action = Common.ACTION_ADD;
            String t_corp_code = action_json.get("corp_code").toString();
            String t_code = action_json.get("task_title").toString();
            String t_name = action_json.get("task_type").toString();
            String remark = action_json.get("task_description").toString();
            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
            //-------------------行为日志结束----

            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public String updateVipTask(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String role_code=request.getSession().getAttribute("role_code").toString();
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code = request.getSession().getAttribute("area_code").toString();
            String store_code = request.getSession().getAttribute("store_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            VipTask vipTask1=vipTaskService.selectById(Integer.parseInt(jsonObject.get("id").toString()));

            /**
             * 审核中的任务不允许编辑
             */
            System.out.println("sssssss   "+vipTask1.getBill_status());
            if(!vipTask1.getBill_status().equals("0")&&StringUtils.isNotBlank(vipTask1.getBill_status())){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("该任务已在审核中,不允许编辑");
                return dataBean.getJsonStr();
            }

            if(!vipTask1.getTask_title().equals(jsonObject.get("task_title").toString())){

                List<VipTask> vipTask2=vipTaskService.selectByTaskTitle(vipTask1.getCorp_code(),
                        jsonObject.get("task_title").toString());
                if(vipTask2.size()>0){
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("该任务名称已存在");
                    return dataBean.getJsonStr();
                }
            }
            String task_code = vipTask1.getTask_code();
            vipTask1=WebUtils.JSON2Bean(jsonObject,VipTask.class);
            vipTask1.setTask_code(task_code);

            if(vipTask1.getSelect_scope().equals("condition_vip")){
                JSONArray array = JSONArray.parseArray(vipTask1.getTarget_vips());
                array = vipGroupService.vipScreen2Array(array,vipTask1.getCorp_code(),role_code,brand_code,area_code,store_code,user_code);
                vipTask1.setTarget_vips_(JSON.toJSONString(array));
            }

            vipTask1.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            vipTask1.setModifier(user_code);
            String flag = vipTaskService.updateVipTask(vipTask1,user_code,group_code,role_code);

            if (!flag.equals(Common.DATABEAN_CODE_SUCCESS)){
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(flag);
                return  dataBean.getJsonStr();
            }
            VipTask vipTask3=vipTaskService.selectById(Integer.parseInt(jsonObject.get("id").toString()));

            JSONObject result=new JSONObject();
            result.put("task_status",vipTask3.getTask_status());

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/execute",method = RequestMethod.POST)
    @ResponseBody
    public String executeVipTask(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_function_check_schedule);
        try{
            String user_code=request.getSession().getAttribute("user_code").toString();
            String group_code=request.getSession().getAttribute("group_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String corp_code=request.getSession().getAttribute("corp_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String task_id = jsonObject.get("id").toString();
            String[] ids = task_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                VipTask vipTask1=vipTaskService.selectById(Integer.parseInt(ids[i]));

                if(!role_code.equals(Common.ROLE_SYS)) {
                    if (!(vipTask1.getCreater().equals(user_code)&&corp_code.equals(vipTask1.getCorp_code()))){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("该用户不允许执行操作");
                        return dataBean.getJsonStr();
                    }
                }
                if(!vipTask1.getBill_status().equals("0")&&!vipTask1.getBill_status().equals("")){
                    ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipTask1.getCorp_code(),"会员任务");
                    if(examineConfigure!=null){
                        BasicDBObject  basic_user=new BasicDBObject();
                        basic_user.put("vipTask.id",vipTask1.getId());
                        DBCursor dbCursor=cursor.find(basic_user);
                        if(dbCursor.count()==0){
                            dataBean.setMessage("该任务正在审核中");
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId("1");
                            return dataBean.getJsonStr();
                        }else{
                            while (dbCursor.hasNext()){
                                DBObject dbObject= dbCursor.next();
                                if(dbObject.get("status").toString().equals("2")){
                                    dataBean.setMessage("该任务正在审核中");
                                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                                    dataBean.setId("1");
                                    return dataBean.getJsonStr();
                                }
                            }
                        }
                    }
                }else{
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("该任务未提交审核");
                    return dataBean.getJsonStr();
                }

                String task_status = vipTask1.getTask_status();
                if (task_status.equals(Common.ACTIVITY_STATUS_0)) {
                    String flag = vipTaskService.executeVipTask(vipTask1,user_code,group_code,role_code);
                }else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage(vipTask1.getTask_title()+"为不可执行状态");
                    return  dataBean.getJsonStr();
                }
            }

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("success");
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/screen",method = RequestMethod.POST)
    @ResponseBody
    public String screenVipTask(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            int  page_number=Integer.parseInt(jsonObject.getString("pageNumber").toString());
            int page_size=Integer.parseInt(jsonObject.getString("pageSize").toString());
            Map<String,Object> map = WebUtils.Json2Map(jsonObject);
            PageInfo<VipTask> list=null;
            if(role_code.equals(Common.ROLE_SYS)){
                list=vipTaskService.selectAllScreen(page_number,page_size,"",map);
            }else{
                list=vipTaskService.selectAllScreen(page_number,page_size,corp_code,map);
            }
            list=vipTaskService.switchTaskType(list);
            list= (PageInfo<VipTask>) vipTaskService.swicthStatus("vipTask",list);
            JSONObject result=new JSONObject();
            result.put("list",JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    /**
     * 提前终止任务
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/terminateTask", method = RequestMethod.POST)
    @ResponseBody
    public String terminateTask(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String task_id = jsonObject.get("id").toString();
            String[] ids = task_id.split(",");
            String task_codes = "";
            for (int i = 0; i < ids.length; i++) {
                VipTask vipTask=vipTaskService.selectById(Integer.parseInt(ids[i]));
                if (vipTask != null){
                    task_codes = task_codes + vipTask.getTask_code() + ",";
                    vipTask.setTask_status(Common.ACTIVITY_STATUS_2);
                    vipTask.setEnd_time(Common.DATETIME_FORMAT.format(new Date()));
                    vipTask.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                    vipTask.setModifier(user_code);
                    vipTaskService.update(vipTask);
                    scheduleJobService.deleteScheduleByGroup(vipTask.getTask_code());
                }
            }
            BasicDBList storeList = new BasicDBList();
            String[] codes = task_codes.split(",");
            for (int i = 0; i < codes.length; i++) {
                storeList.add(codes[i]);
            }
            BasicDBObject queryCondition = new BasicDBObject();
            queryCondition.put("task_code",new BasicDBObject("$in",storeList));
            DBCursor dbCursor = cursor.find(queryCondition);

            while (dbCursor.hasNext()) {
                //记录存在，更新
                DBObject dbObject = dbCursor.next();
                String task_code = dbObject.get("task_code").toString();
                DBObject updateCondition = new BasicDBObject();
                updateCondition.put("task_code", task_code);

                DBObject updatedValue = new BasicDBObject();
                updatedValue.put("task.task_status", "2");
                updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));

                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                cursor.update(updateCondition, updateSetValue,false,true);
            }
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    //获取该会员任务的已完成未完成情况
    @RequestMapping(value = "/mobile/vipTask",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String mobileVipTask(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        DBCollection  batch_import_vip=mongoTemplate.getCollection(CommonValue.table_batch_import_vip);
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String corp_code = jsonObject.getString("corp_code").toUpperCase();
            String card_no = jsonObject.getString("card_no");
            String app_id = jsonObject.getString("app_id");
            String open_id = jsonObject.getString("open_id");

            if (card_no.equals("")){
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_REDIRECT);
                dataBean.setMessage(CommonValue.wei_member_url.replace("@appid@",app_id));
                return  dataBean.getJsonStr();
            }
            List<VipTask> new_list = new ArrayList<VipTask>();
            //获取所有任务
            List<VipTask> list = vipTaskService.selectMobileShow(corp_code,app_id,Common.ACTIVITY_STATUS_1,"Y");
            logger.info("====================mobileVipTask==list.size()"+list.size());

            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("key","VIP_OPEN_ID");
            object.put("type","text");
            object.put("value",open_id);

//            object.put("key",Common.VIP_SCREEN_CARDNO_KEY);
//            object.put("type","text");
//            object.put("value",card_no);

            JSONObject vip_info = new JSONObject();

//            DataBox dataBox1 = iceInterfaceAPIService.getVipInfoByCard(corp_code,card_no,"");
//            String message1 = dataBox1.data.get("message").value;
//            JSONObject result_obj1 = JSONObject.parseObject(message1);
//            JSONArray vip_array1 = result_obj1.getJSONArray("vip_detail_info");
//
//            if (vip_array1.size() > 0){
//                vip_info = vip_array1.getJSONObject(0);
//            }
            //获取每个会员可参与的任务
            int flag = 0;
            for (int i = 0; i < list.size(); i++) {
                //判断此任务是全部会员还是有筛选条件的会员
                String task_type = list.get(i).getTask_type();
                String a = list.get(i).getTarget_vips_();
                String status = list.get(i).getTask_status();
                String target_vip=list.get(i).getTarget_vips();
                String select_scope=list.get(i).getSelect_scope();

                if (a != null && !a.equals("") && !a.equals("[]")&&select_scope.equals("condition_vip")){
                    array = JSONArray.parseArray(a);
                    array.add(object);
                    DataBox dataBox = iceInterfaceService.vipScreenMethod2("1", "3", corp_code,JSON.toJSONString(array),"","");

//                    DataBox dataBox = vipGroupService.vipScreenBySolr(array,corp_code,"1","3","","","","","","","");
                    String result = dataBox.data.get("message").value;
                    JSONObject result_obj = JSONObject.parseObject(result);
                    JSONArray vip_array = result_obj.getJSONArray("all_vip_list");
                    //如果该会员在该任务的目标会员中
                    if (vip_array.size() > 0){
                        vip_info = vip_array.getJSONObject(0);
                        new_list.add(list.get(i));
                        if (status.equals("1") && (task_type.equals("consume_count") || task_type.equals("consume_money") || task_type.equals("ticket_sales"))){
                            flag = 1;
                        }
                    }
                }else if(select_scope.equals("input_file")){
                   DBObject dbObject= batch_import_vip.findOne(new BasicDBObject("_id",target_vip));
                   String cardInfo=dbObject.get("cardInfo").toString();
                   JSONArray jsonArray=JSON.parseArray(cardInfo);
                   int size=jsonArray.size();
                    if (size > 0){
                        List<String> list1=new ArrayList<String>();
                        for (int j = 0; j < size; j++) {
                            JSONObject jsonObject2=jsonArray.getJSONObject(j);
                            String cardno=jsonObject2.getString("cardno");
                            list1.add(cardno);
                        }
                        if(list1.contains(card_no)){
                            DataBox dataBox =  iceInterfaceService.getVipByOpenId(corp_code,open_id,"");
                            JSONArray vip_array = JSONArray.parseArray(dataBox.data.get("message").value);
                            if (vip_array.size() > 0)
                                vip_info = vip_array.getJSONObject(0);
                            new_list.add(list.get(i));
                            if (status.equals("1") && (task_type.equals("consume_count") || task_type.equals("consume_money") || task_type.equals("ticket_sales"))){
                                flag = 1;
                            }
                        }
                    }
                } else {
                    DataBox dataBox =  iceInterfaceService.getVipByOpenId(corp_code,open_id,"");
                    JSONArray vip_array = JSONArray.parseArray(dataBox.data.get("message").value);
                    if (vip_array.size() > 0)
                        vip_info = vip_array.getJSONObject(0);
                    new_list.add(list.get(i));
                    if (status.equals("1") && (task_type.equals("consume_count") || task_type.equals("consume_money") || task_type.equals("ticket_sales"))){
                        flag = 1;
                    }
                }

            }
            logger.info("=========mobileVipTask==new_list.size()"+new_list.size());

            JSONObject result=new JSONObject();
            List<VipTask> compl = new ArrayList<VipTask>();
            List<VipTask> uncompl = new ArrayList<VipTask>();

            //获取未完成任务及进度
            if (new_list.size() > 0){
                for (int i = 0; i < new_list.size(); i++) {
                    VipTask vipTask = new_list.get(i);
                    vipTask = vipTaskService.vipTaskSchedule(vipTask,card_no,vip_info,flag,app_id,open_id);
                    if (vipTask.getSchedule().equals("未完成"))
                        uncompl.add(vipTask);
                }
            }

            Map keyMap = new HashMap();
            keyMap.put("corp_code", corp_code);
            keyMap.put("open_id", open_id);
            keyMap.put("status", "1");
            BasicDBObject queryCondition = new BasicDBObject();
            queryCondition.putAll(keyMap);
            DBCursor dbCursor1 = cursor.find(queryCondition).sort(new BasicDBObject("modified_date",-1));
            while (dbCursor1.hasNext()){
                DBObject dbObject1=dbCursor1.next();
                JSONObject task_obj = JSONObject.parseObject(dbObject1.get("task").toString());
                VipTask task = WebUtils.JSON2Bean(task_obj,VipTask.class);
                task.setModified_date(dbObject1.get("modified_date").toString());
                compl.add(task);
            }

            result.put("com",compl);
            result.put("uncom",uncompl);
            result.put("vip_url",CommonValue.wei_member_url.replace("@appid@",app_id));

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/mobile/select",method = RequestMethod.POST)
    @ResponseBody
    public String mobileSelectVipTask(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            int task_id=Integer.parseInt(jsonObject.get("id").toString());
            String card_no = jsonObject.getString("card_no");
            String app_id = jsonObject.getString("app_id");
            String open_id = jsonObject.getString("open_id");
            logger.info("=======================mobileSelectVipTask"+open_id);

            VipTask vipTask=vipTaskService.selectById(task_id);
            String corp_code = vipTask.getCorp_code();
            JSONObject vip_info = new JSONObject();

            DataBox dataBox =  iceInterfaceService.getVipByOpenId(corp_code,open_id,"");
            JSONArray vip_array = JSONArray.parseArray(dataBox.data.get("message").value);
            if (vip_array.size() > 0) {
                vip_info = vip_array.getJSONObject(0);
            }
            String task_type=vipTask.getTask_type();
            String url = "";
            String title = "";
            String desc = "";
            String pic = "";
            int flag = 0;
            if (task_type.equals("consume_count") || task_type.equals("consume_money") || task_type.equals("ticket_sales")){
                flag = 1;
                url = CommonValue.wei_home_url;
                url = url.replace("@appid@",app_id).replace("@openid@",open_id);
                if (corp_code.equals("C10234"))
                    url = "";
            }
            if(task_type.equals("share_goods")){
                title = "分享商品";
                url = CommonValue.wei_home_url;
                url = url.replace("@appid@",app_id).replace("@openid@",open_id);

            }else if(task_type.equals("share_counts") || task_type.equals("invite_registration")){
                String share_content = vipTask.getShare_content();
                JSONObject con_obj = JSONObject.parseObject(share_content);
                title = con_obj.getString("share_title");
                desc = con_obj.getString("share_desc");
                pic = con_obj.getString("pic");

                url = CommonValue.ishop_url+"mobile/task/link.html";
            }
            vipTask = vipTaskService.vipTaskSchedule(vipTask,card_no,vip_info,flag,app_id,open_id);

            JSONObject result = new JSONObject();
            result.put("share_title",title);
            result.put("share_url",url);
            result.put("share_desc",desc);
            result.put("share_pic",pic);

            if(vipTask.getTask_type().equals("activity_count")){
                String task_condition=vipTask.getTask_condition();
                JSONObject task_obj=JSON.parseObject(task_condition);
                String activity_type=task_obj.getString("activity_type");
                if(activity_type.equals("sales")){
                    task_obj.put("activity_type","促销活动");
                }else if(activity_type.equals("recruit")){
                    task_obj.put("activity_type","招募活动");
                }else  if(activity_type.equals("h5")){
                    task_obj.put("activity_type","网页活动");
                }else  if(activity_type.equals("coupon")){
                    task_obj.put("activity_type","优惠券活动");
                }else  if(activity_type.equals("invite")){
                    task_obj.put("activity_type","线下报名活动");
                }else  if(activity_type.equals("festival")){
                    task_obj.put("activity_type","节日活动");
                }
                vipTask.setTask_condition(task_obj.toString());
            }

            result.put("task",vipTask);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/mobile/getShareUrl",method = RequestMethod.POST)
    @ResponseBody
    public String mobileGetShareUrl(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String id = jsonObject.get("id").toString();
            String app_id = jsonObject.getString("app_id");
            String card_no = jsonObject.getString("card_no");
            String open_id = jsonObject.getString("open_id");
            logger.info("=======================mobileGetShareUrl"+open_id);

            String url = "";
            String flag = "Y";
            if (jsonObject.containsKey("type")){
                String type = jsonObject.getString("type");
                VipActivity vipActivity = vipActivityService.getActivityByCode(id);
                String corp_code = vipActivity.getCorp_code();
                if (vipActivity.getRun_mode().equals("register")){
                    url = CommonValue.wei_register_url;
                    url = url.replace("@appid@",app_id).replace("@openid@",open_id);
                    if (corp_code.equals("C10016")){
                        String url1 = netWorkService.getCardUrl(corp_code,app_id);
                        if (!url1.trim().equals("")){
                            String url_param_0 = url1.split("\\?")[0];
                            String url_param = url1.split("\\?")[1];

                            String[] params = url_param.split("&");
                            String new_url_param = "";
                            for (int i = 0; i < params.length; i++) {
                                String param = params[i];
                                if (param.contains("outer_str")){
                                    param = param+"_"+open_id;
                                }
                                if (i< params.length -1)
                                    param = param +"&";
                                new_url_param += param;
                            }
                            url = url_param_0+"?"+new_url_param;
                        }
                    }
                }
            }else {
                int task_id=Integer.parseInt(id);
                VipTask vipTask=vipTaskService.selectById(task_id);
                String task_type=vipTask.getTask_type();
                String corp_code = vipTask.getCorp_code();
                if(task_type.equals("share_counts")){
                    String condition = vipTask.getTask_condition();
                    JSONObject con_obj = JSONObject.parseObject(condition);
                    url = con_obj.getString("shareUrl");
                }else if(task_type.equals("invite_registration")) {
                    url = CommonValue.wei_register_url;
                    url = url.replace("@appid@",app_id).replace("@openid@",open_id);
                    if (corp_code.equals("C10016")){
                        String url1 = netWorkService.getCardUrl(corp_code,app_id);
                        if (!url1.trim().equals("")){
                            String url_param_0 = url1.split("\\?")[0];
                            String url_param = url1.split("\\?")[1];

                            String[] params = url_param.split("&");
                            String new_url_param = "";
                            for (int i = 0; i < params.length; i++) {
                                String param = params[i];
                                if (param.contains("outer_str")){
                                    param = param+"_"+open_id;
                                }
                                if (i< params.length -1)
                                    param = param +"&";
                                new_url_param += param;
                            }
                            url = url_param_0+"?"+new_url_param;
                        }
                    }
                }
            }
            JSONObject result = new JSONObject();
            result.put("share_url",url);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/mobile/shareUrl",method = RequestMethod.POST)
    @ResponseBody
    public String mobileShareUrl(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String id = jsonObject.get("id").toString();
            String app_id = jsonObject.getString("app_id");
            String card_no = jsonObject.getString("card_no");
            String open_id = jsonObject.getString("open_id");

            logger.info("=======================mobileGetShareUrl"+open_id);

            VipTask vipTask = new VipTask();
            if (jsonObject.containsKey("type")){
                vipTask = vipTaskService.selectVipTaskByTaskTypeAndTitle("","",id);
            }else {
                int task_id=Integer.parseInt(id);
                vipTask=vipTaskService.selectById(task_id);
            }

            String vip_id = "";
            JSONObject vip_info = new JSONObject();

            DataBox dataBox =  iceInterfaceService.getVipByOpenId(vipTask.getCorp_code(),open_id,"");
            JSONArray vip_array = JSONArray.parseArray(dataBox.data.get("message").value);
            if (vip_array.size() > 0) {
                vip_info = vip_array.getJSONObject(0);
                vip_id = vip_info.getString("vip_id");
            }
            if (vipTask.getTask_status().equals("1")){
                if (vipTask.getTask_type().equals("share_counts")){
                    JSONArray array1 = new JSONArray();
                    JSONObject schedule = new JSONObject();
                    schedule.put("share_time",Common.DATETIME_FORMAT.format(new Date()));
                    array1.add(schedule);
                    int flag = vipTaskService.updateVipTaskSchedule(vipTask,card_no,app_id,open_id,vip_info,"0",array1);
                    logger.info("flag"+flag);
                    if (flag == 1){
                        vipTaskService.sendPresent(vipTask,vipTask.getCorp_code(),vip_id,app_id,open_id,"分享链接任务赠送");
                    }
                }
            }

            //插入MongoDB
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
            DBObject dbObject=new BasicDBObject();
            dbObject.put("app_id",app_id);
            dbObject.put("open_id",open_id);
            dbObject.put("card_no",card_no);
            dbObject.put("corp_code",vipTask.getCorp_code());
            dbObject.put("share_time",Common.DATETIME_FORMAT.format(new Date()));
            DBObject dbObject1=WebUtils.bean2DBObject(vipTask);
            dbObject.put("vipTask",dbObject1);
            dbObject.put("vip",vip_info);
            cursor.save(dbObject);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("");
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    /**
     * 完善会员拓展资料
     * 返回最新拓展资料
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/improveData",method = RequestMethod.POST)
    @ResponseBody
    public String improveData(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String app_id = jsonObject.get("app_id").toString();
            String open_id = jsonObject.get("open_id").toString();
            String card_no = jsonObject.getString("card_no");
            int task_id=Integer.parseInt(jsonObject.get("task_id").toString());

            VipTask vipTask=vipTaskService.selectById(task_id);
            String task_condition = vipTask.getTask_condition();
            JSONArray cust_array = JSONArray.parseArray(task_condition);
            String cust_cols = "";
            for (int i = 0; i <cust_array.size() ; i++) {
                if (cust_array.getJSONObject(i).getString("param_name").startsWith("CUST_")){
                    cust_cols = cust_cols + cust_array.getJSONObject(i).getString("param_name") + ",";
                }
            }
            int count = -1;
            String vip_id = "";

            MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
            BasicDBObject queryCondition = new BasicDBObject();
            queryCondition.put("corp_code",corp_code);
            queryCondition.put("app_id",app_id);
            queryCondition.put("open_id",open_id);
            queryCondition.put("task_code",vipTask.getTask_code());

            //保存拓展参数
            if (jsonObject.containsKey("extend") && !jsonObject.get("extend").toString().equals("")) {
                //报存扩展信息
                DBCursor dbCursor1 = cursor.find(queryCondition);
                if (dbCursor1.size() > 0) {
                    DBObject object = dbCursor1.next();
                    if (object.get("status").equals("1")){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("任务已完成");
                        return  dataBean.getJsonStr();
                    }
                }

                String extend = jsonObject.get("extend").toString();
                vip_id = jsonObject.get("vip_id").toString();

                JSONObject extend_obj = JSONObject.parseObject(extend);
                Iterator<String> iter = extend_obj.keySet().iterator();
                JSONArray array = new JSONArray();
                count = 0;
                JSONArray schedule = new JSONArray();
                String vip_name = "";
                String sex = "";
                String birthday = "";
                String province = "";
                String address = "";
                while (iter.hasNext()) {
                    JSONObject obj = new JSONObject();
                    String name = iter.next();
                    String value = extend_obj.get(name).toString();
                    obj.put("column", name);
                    obj.put("value", value);
                    if (value.equals("")){
                        count = -1;
                    }else {
                        if (name.startsWith("CUST_")){
                            //处理有参数属性的字段
                            List<VipParam> vipParamList = vipParamService.selectByParamName(corp_code, name, "Y");
                            if (vipParamList.size() > 0) {
                                VipParam vipParam = vipParamList.get(0);
                                String param_attribute = vipParam.getParam_attribute();
                                if ("sex".equals(param_attribute)) {
                                    if ("男".equals(value)) {
                                        value = "1";
                                    } else if ("女".equals(value)) {
                                        value = "0";
                                    }
                                }
                                obj.put("column", name);
                                obj.put("value", value);
                            }
                            array.add(obj);
                        }
                        if (name.equals("vip_name"))
                            vip_name = value;
                        if (name.equals("sex_vip")){
                            if (value.equals("女")){
                                sex = "F";
                            }else {
                                sex = "M";
                            }
                        }
                        if (name.equals("birthday"))
                            birthday = value;
                        if (name.equals("address"))
                            address = value;
                        if (name.equals("province")){
                            province = value;
                            if (!province.isEmpty()){
                                JSONObject p = JSONObject.parseObject(province);
                                String province1 = p.getString("province");
                                if (province1.isEmpty())
                                    continue;
                            }
                        }
                        schedule.add(obj);
                    }
                }
                if (array.size() > 0) {
                    DataBox dataBox = iceInterfaceService.saveVipExtendInfo(corp_code, vip_id,"", JSON.toJSONString(array));
                    logger.info("==========saveVipExtendInfo="+dataBox.status.toString());
                    if (!dataBox.status.toString().equals("SUCCESS")){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage(dataBox.msg);
                        return  dataBean.getJsonStr();
                    }
                }
                if (!vip_name.equals("") || !birthday.equals("") || !sex.equals("") || !province.equals("") || !address.equals("")){
                    if (corp_code.equals("C10016")){
                        HashMap<String,Object> vipInfo = new HashMap<String, Object>();
                        vipInfo.put("id",vip_id);
                        if (!vip_name.equals(""))
                            vipInfo.put("VIPNAME",vip_name);
                        if (!birthday.equals(""))
                            vipInfo.put("BIRTHDAY",birthday.replace("-",""));
                        if (!sex.equals(""))
                            vipInfo.put("SEX",sex);
                        crmInterfaceService.modInfoVip(corp_code,vipInfo);
                    }
                    String province1 = "";
                    String city = "";
                    String area = "";
                    if (!province.isEmpty()){
                        JSONObject p = JSONObject.parseObject(province);
                        province1 = p.getString("province");
                        city = p.getString("city");
                        area = p.getString("area");
                    }

                    DataBox dataBox = iceInterfaceAPIService.vipProfileBackup(corp_code,vip_id,"","",vip_name,birthday,sex,"",province1,city,area,address,"","","","");
                    logger.info("==========vipProfileBackup="+dataBox.status.toString());
                    if (!dataBox.status.toString().equals("SUCCESS")){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage(dataBox.msg);
                        return  dataBean.getJsonStr();
                    }
                }
                vipTaskService.updateVipTaskSchedule(vipTask,card_no,app_id,open_id,new JSONObject(),"0",schedule);
            }
            DataBox dataBox =  iceInterfaceService.getVipByOpenId(corp_code,open_id,cust_cols);
            JSONArray vip_array = JSONArray.parseArray(dataBox.data.get("message").value);
            JSONObject vip_info;

            if (vip_array.size() > 0){
                vip_info = vip_array.getJSONObject(0);
                String sex_vip = vip_info.getString("sex");
                if (sex_vip != null && (sex_vip.equalsIgnoreCase("W") || sex_vip.equals("0") || sex_vip.equalsIgnoreCase("F"))){
                    vip_info.put("sex_vip","女");
                }else {
                    vip_info.put("sex_vip","男");
                }
                vip_info.put("birthday",vip_info.getString("vip_birthday") != null?vip_info.getString("vip_birthday").replace("无",""):"");
                vip_info.put("vip_name",vip_info.getString("vip_name") !=null?vip_info.getString("vip_name"):"");
                String province = vip_info.containsKey("province")?vip_info.getString("province"):"";
                String city = vip_info.containsKey("city")?vip_info.getString("city"):"";
                String area = vip_info.containsKey("area")?vip_info.getString("area"):"";
                JSONObject p = new JSONObject();
                p.put("province",province);
                p.put("city",city);
                p.put("area",area);

                vip_info.put("province",p);
                vip_info.put("address",vip_info.containsKey("address")?vip_info.getString("address"):"");
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("对不起，您还不是会员，请先注册或绑卡");
                return  dataBean.getJsonStr();
            }
            vipTask.setSchedule("未完成");
            if (count == 0){
                //记录该任务已完成
                vipTask.setSchedule("已完成");
                int flag = vipTaskService.updateVipTaskSchedule(vipTask,card_no,app_id,open_id,vip_info,"1",null);
                if (flag == 1)
                    vipTaskService.sendPresent(vipTask,corp_code,vip_id,app_id,open_id,"完成完善资料任务赠送");

            }
            JSONObject result = new JSONObject();
            result.put("task",vipTask);
            result.put("vipInfo",vip_info);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    /**
     * 完善会员拓展资料
     * 返回最新拓展资料
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/questionNaire",method = RequestMethod.POST)
    @ResponseBody
    public String questionNaire(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String app_id = jsonObject.get("app_id").toString();
            String open_id = jsonObject.get("open_id").toString();
            String card_no = jsonObject.getString("card_no");
            String extend = jsonObject.get("result").toString();
            int task_id=Integer.parseInt(jsonObject.get("task_id").toString());

            VipTask vipTask=vipTaskService.selectById(task_id);

            DataBox dataBox =  iceInterfaceService.getVipByOpenId(corp_code,open_id,"");
            JSONArray vip_array = JSONArray.parseArray(dataBox.data.get("message").value);

            if (vip_array.size() > 0 && vipTask != null){
                JSONObject vip_info = vip_array.getJSONObject(0);
                String vip_id = vip_info.getString("vip_id");

                DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
//                Map keyMap = new HashMap();
//                keyMap.put("_id", corp_code+"_"+vipTask.getTask_code()+"_"+card_no);
                BasicDBObject queryCondition = new BasicDBObject();
//                queryCondition.putAll(keyMap);
                queryCondition.put("corp_code",corp_code);
                queryCondition.put("app_id",app_id);
                queryCondition.put("open_id",open_id);
                queryCondition.put("task_code",vipTask.getTask_code());
                //报存
                DBCursor dbCursor1 = cursor.find(queryCondition);
                if (dbCursor1.size() > 0) {
                    DBObject object = dbCursor1.next();
                    if (object.get("status").equals("1")){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("任务已完成");
                        return  dataBean.getJsonStr();
                    }
                }
                JSONArray schedule = JSONArray.parseArray(extend);

                vipTask.setSchedule("已完成");
                int flag = vipTaskService.updateVipTaskSchedule(vipTask,card_no,app_id,open_id,vip_info,"1",schedule);
                if (flag == 1)
                    vipTaskService.sendPresent(vipTask,corp_code,vip_id,app_id,open_id,"完成问卷任务赠送");

                JSONObject result = new JSONObject();
                result.put("task",vipTask);
                result.put("vipInfo",vip_info);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result.toString());
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("对不起，您还不是会员，请先注册或绑卡");
                return  dataBean.getJsonStr();
            }


        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/getAllByStatus",method = RequestMethod.POST)
    @ResponseBody
    public String getAllByStatus(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
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
            List<VipTask> list=null;
            list=vipTaskService.selectAllByStatus(corp_code);
            List<HashMap<String,Object>> array=new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < list.size(); i++) {
                HashMap<String,Object> map=new HashMap<String, Object>();
                VipTask vipTask=list.get(i);
               String task_code= vipTask.getTask_code();
               String task_title= vipTask.getTask_title();
                map.put("task_code",task_code);
                map.put("task_title",task_title);
                array.add(map);
            }
            JSONObject result=new JSONObject();
            result.put("list",array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    //创建人 提交审核（保存后方可 提交审核）
    @RequestMapping(value = "/examine/creater",method = RequestMethod.POST)
    @ResponseBody
    public  String  submitExamineByCreater(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            //本企业或者系统管理员
            String user_code = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String function_code=jsonObject.getString("function_code");
            if(function_code.equals("F0063")){//会员任务
                int task_id=jsonObject.getInteger("id");
                VipTask vipTask=vipTaskService.selectById(task_id);
                if(vipTask==null){
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("会员任务不存在");
                    return dataBean.getJsonStr();
                }

                //是系统管理员 可提交所有人的
                //不是系统管理员 只可以提交自己创建的
                if(!role_code.equals(Common.ROLE_SYS)){
                    if(!(user_code.equals(vipTask.getCreater())&&corp_code.equals(vipTask.getCorp_code()))){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您没有提交审核权限，需创建人提交审核");
                        return dataBean.getJsonStr();
                    }
                }

                String user_name="";
                User creater_user=userService.selectUserByCode(vipTask.getCorp_code(),vipTask.getCreater(),"Y");
                if(creater_user==null){
                    user_name="系统管理员";
                }else{
                    user_name=creater_user.getUser_name();
                }

                vipTask.setId(task_id);
                vipTask.setBill_status("1");
                vipTaskService.update(vipTask);
                //提交审核成功，通知审核人
                ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipTask.getCorp_code(),"会员任务");
                if(examineConfigure!=null){
                    String sms_examine=examineConfigure.getSms_examine();
                    if(sms_examine.equals("Y")){
                        String examine_group=examineConfigure.getExamine_group();
                        JSONArray jsonArray=JSON.parseArray(examine_group);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONArray jsonArray1=jsonArray.getJSONArray(i);
                            for (int j = 0; j < jsonArray1.size(); j++) {
                                String examine_user_code=jsonArray1.getString(j);
                                User user=userService.selectUserByCode(examineConfigure.getCorp_code(),examine_user_code,"Y");
                                String phone=user.getPhone();

                                StringBuilder stringBuilder=new StringBuilder();
                                stringBuilder.append("由");
                                stringBuilder.append("\"");
                                stringBuilder.append(user_name);
                                stringBuilder.append("\"");
                                stringBuilder.append("创建的");
                                stringBuilder.append("\"");
                                stringBuilder.append(vipTask.getTask_title());
                                stringBuilder.append("\"");
                                stringBuilder.append("任务已提交审核，请及时审核");

                                iceInterfaceService.sendSmsV2(examineConfigure.getCorp_code(),stringBuilder.toString(),phone,examine_user_code,"审核通知");
                            }

                        }
                    }
                }

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage("提交审核成功");
            }else if(function_code.equals("F0032")){//会员活动
                String activity_code=jsonObject.getString("activity_code");
                VipActivity vipActivity=vipActivityService.getActivityByCode(activity_code);
                if(vipActivity==null){
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("会员活动不存在");
                    return dataBean.getJsonStr();
                }

                //是系统管理员 可提交所有人的
                //不是系统管理员 只可以提交自己创建的

                if(!role_code.equals(Common.ROLE_SYS)){
                    if(!(user_code.equals(vipActivity.getCreater())&&corp_code.equals(vipActivity.getCorp_code()))){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您没有提交审核权限，需创建人提交审核");
                        return dataBean.getJsonStr();
                    }
                }

                String user_name="";
                User creater_user=userService.selectUserByCode(vipActivity.getCorp_code(),vipActivity.getCreater(),"Y");
                if(creater_user==null){
                    user_name="系统管理员";
                }else{
                    user_name=creater_user.getUser_name();
                }


                vipActivity.setActivity_code(activity_code);
                vipActivity.setBill_status("1");
                vipActivityService.updateVipActivity(vipActivity);

                //將活動相對應的會員任務的狀態改變
                VipTask vipTask=vipTaskService.selectVipTaskByTaskTypeAndTitle(vipActivity.getCorp_code(),"activity",vipActivity.getActivity_code());
                if(vipTask!=null) {
                    vipTask.setBill_status("1");
                    vipTaskService.update(vipTask);
                }

                //提交审核成功，通知审核人
                ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipActivity.getCorp_code(),"会员活动");
                if(examineConfigure!=null){
                    if(examineConfigure.getSms_examine().equals("Y")){
                        String examine_group=examineConfigure.getExamine_group();
                        JSONArray jsonArray=JSON.parseArray(examine_group);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONArray jsonArray1=jsonArray.getJSONArray(i);
                            for (int j = 0; j < jsonArray1.size(); j++) {
                                String examine_user_code=jsonArray1.getString(j);
                                User user=userService.selectUserByCode(examineConfigure.getCorp_code(),examine_user_code,"Y");
                                String phone=user.getPhone();
                                StringBuilder stringBuilder=new StringBuilder();
                                stringBuilder.append("由");
                                stringBuilder.append("\"");
                                stringBuilder.append(user_name);
                                stringBuilder.append("\"");
                                stringBuilder.append("创建的");
                                stringBuilder.append("\"");
                                stringBuilder.append(vipActivity.getActivity_theme());
                                stringBuilder.append("\"");
                                stringBuilder.append("活动已提交审核，请及时审核");

                                iceInterfaceService.sendSmsV2(examineConfigure.getCorp_code(),stringBuilder.toString(),phone,examine_user_code,"审核通知");
                            }

                        }
                    }

                }
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage("提交审核成功");
            }

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(e.getMessage());
            return dataBean.getJsonStr();
        }
        return  dataBean.getJsonStr();
    }


    //审核人点击按钮通过审核
    @RequestMapping(value = "/examine/user",method = RequestMethod.POST)
    @ResponseBody
    public  String  submitExamineByUser(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_function_check_schedule);
        try{
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String function_code=jsonObject.getString("function_code");
            if(function_code.equals("F0063")){//会员任务

                int task_id=jsonObject.getInteger("id");
                String type=jsonObject.getString("type");//审核或者撤销审核
                VipTask vipTask=vipTaskService.selectById(task_id);
                if(vipTask!=null){

                    if(type.equals("check")){

                        //判断该审核人有没有审核过
                        BasicDBObject  basic_user=new BasicDBObject();
                        basic_user.put("vipTask.id",task_id);
                        basic_user.put("examine_user",user_code);
                        int count_check=cursor.find(basic_user).count();
                        if(count_check>0){
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId("1");
                            dataBean.setMessage("请勿重复审核");
                            return dataBean.getJsonStr();
                        }

                        ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipTask.getCorp_code(),"会员任务");
                        if(examineConfigure==null){
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setMessage("会员任务未设置审核人");
                            dataBean.setId("1");
                            return  dataBean.getJsonStr();
                        }
                        String examine_group=examineConfigure.getExamine_group();
                        JSONArray jsonArray=JSON.parseArray(examine_group);
                        boolean isflag=false;
                        int array_size=jsonArray.size();
                        for (int i = 0; i <array_size ; i++) {
                            JSONArray jsonArray1=jsonArray.getJSONArray(i);
                            if(jsonArray1.contains(user_code)&&corp_code.equals(examineConfigure.getCorp_code())){
                                User user=userService.selectUserByCode(corp_code,user_code,"Y");
                                BasicDBObject basicDBObject=new BasicDBObject();
                                JSONObject user_josn=JSONObject.parseObject(JSON.toJSONString(user));
                                BSONObject bsonObject=new BasicBSONObject();
                                bsonObject.putAll(user_josn);
                                basicDBObject.put("user",bsonObject);
                                JSONObject task_josn=JSONObject.parseObject(JSON.toJSONString(vipTask));
                                BSONObject bsonObject1=new BasicBSONObject();
                                bsonObject1.putAll(task_josn);
                                basicDBObject.put("vipTask",bsonObject1);
                                basicDBObject.put("examine_group",String.valueOf(i));
                                basicDBObject.put("created_date",simpleDateFormat.format(new Date()));
                                basicDBObject.put("examine_user",user_code);
                                basicDBObject.put("examine_type","vipTask");
                                basicDBObject.put("status","2");//已审核
                                cursor.insert(basicDBObject);
                                isflag=true;
                                break;
                            }
                        }
                        if(isflag==true){
                            /**
                             * 审核成功的人数
                             */
                            int check_success_num=0;
                            BasicDBObject basDb=new BasicDBObject();
                            basDb.put("vipTask.id",task_id);
                            basDb.put("examine_type","vipTask");
                            basDb.put("examine_group",new BasicDBObject("$ne",null));
                            BasicDBObject match_user = new BasicDBObject("$match", basDb);
                            /* Group操作*/
                            DBObject groupField = new BasicDBObject("_id","$examine_group");
                            groupField.put("count", new BasicDBObject("$sum", 1));
                            DBObject group_user = new BasicDBObject("$group", groupField);
                            AggregationOutput output_user = cursor.aggregate(match_user,group_user);
                            for(DBObject dbObject:output_user.results()){
                                String count=dbObject.get("count").toString();
                                if(Integer.parseInt(count)!=0){
                                    check_success_num++;
                                }
                            }

                            //如果审核进度完成 状态改为待执行按钮
                            if(check_success_num==array_size){
                              BasicDBObject basicDBObject=new BasicDBObject();
                              basicDBObject.put("examine_type","vipTask");
                              basicDBObject.put("vipTask.id",task_id);
                              BasicDBObject update=new BasicDBObject();
                              update.put("$set",new BasicDBObject("status","3"));//全部审核完成
                              cursor.update(basicDBObject,update,false,true);

                              VipTask vipTask1=new VipTask();
                              vipTask1.setId(task_id);
                              vipTask1.setBill_status("2");
                              vipTaskService.update(vipTask1);
                            }

                            //审核通过发送给创建人,审核进度发送给审核人，创建人
                            if(check_success_num==array_size){
                                if(examineConfigure.getSms_result().equals("Y")) {
                                    User user = userService.selectUserByCode(vipTask.getCorp_code(), vipTask.getCreater(), "Y");
                                    if(user!=null) {
                                        StringBuilder stringBuilder=new StringBuilder();
                                        stringBuilder.append("\"");
                                        stringBuilder.append(vipTask.getTask_title());
                                        stringBuilder.append("\"");
                                        stringBuilder.append("任务通过审核，请开始执行");
                                        iceInterfaceService.sendSmsV2(vipTask.getCorp_code(), stringBuilder.toString(), user.getPhone(), user.getUser_code(), "审核完成");
                                    }
                                }
                            } else{
                                if(examineConfigure.getSms_progress().equals("Y")){
                                    User user = userService.selectUserByCode(vipTask.getCorp_code(), vipTask.getCreater(), "Y");
                                    if(user!=null) {
                                        JSONArray objects=new JSONArray();
                                        JSONArray jsonArray1=JSON.parseArray(examineConfigure.getExamine_group_info());
                                        for (int i = 0; i < jsonArray1.size(); i++) {
                                            JSONArray jsonArray2=jsonArray1.getJSONArray(i);
                                            objects.addAll(jsonArray2); //全部需审核员工
                                        }

                                        List<String> examineList=new ArrayList<String>();

                                        String examine_success="";
                                        String examine_fail="";
                                        BasicDBObject basicDBObject=new BasicDBObject();
                                        basicDBObject.put("examine_type","vipTask");
                                        basicDBObject.put("vipTask.id",vipTask.getId());
                                        DBCursor dbObjects=cursor.find(basicDBObject);
                                        for(DBObject dbObject:dbObjects){
                                            String creater=dbObject.get("examine_user").toString();
                                            for (int i = 0; i < objects.size(); i++) {
                                                String creater_v2=objects.getString(i).split("\\(")[1].replace(")","").trim();
                                                if(creater_v2.equals(creater)){
                                                    examine_success+=objects.getString(i)+"、";
                                                    examineList.add(objects.get(i).toString());
                                                }
                                            }
                                        }
                                        for (int i = 0; i < examineList.size(); i++) {
                                            if(objects.contains(examineList.get(i))){
                                                objects.remove(examineList.get(i));
                                            }
                                        }
                                        for (int i = 0; i < objects.size(); i++) {
                                            examine_fail+=objects.getString(i)+"、";
                                        }
                                        if(examine_success.endsWith("、")){
                                            examine_success=examine_success.substring(0,examine_success.length()-1);
                                        }
                                        if(examine_fail.endsWith("、")){
                                            examine_fail=examine_fail.substring(0,examine_fail.length()-1);
                                        }
                                        StringBuilder stringBuilder=new StringBuilder();
                                        stringBuilder.append("\"");
                                        stringBuilder.append(vipTask.getTask_title());
                                        stringBuilder.append("\"");
                                        stringBuilder.append("任务进度反馈：");
                                        stringBuilder.append("\"");
                                        stringBuilder.append(examine_success);
                                        stringBuilder.append("\"");
                                        stringBuilder.append("已审核通过，");
                                        stringBuilder.append("\"");
                                        stringBuilder.append(examine_fail);
                                        stringBuilder.append("\"");
                                        stringBuilder.append("未审核");
                                        iceInterfaceService.sendSmsV2(vipTask.getCorp_code(), stringBuilder.toString(), user.getPhone(), user.getUser_code(), "审核进度");
                                    }
                                }
//                                if(examineConfigure.getSms_progress().equals("Y")) {
//                                    boolean isflag_v2 = false;
//                                    if (user_code.equals(vipTask.getCreater()) && corp_code.equals(vipTask.getCorp_code())) {
//                                        isflag_v2 = true;
//                                    }
//                                    if (isflag_v2 == false) {
//                                        User user = userService.selectUserByCode(vipTask.getCorp_code(), vipTask.getCreater(), "Y");
//                                        if(user!=null) {
//                                            iceInterfaceService.sendSmsV2(vipTask.getCorp_code(), "你有一条会员任务,任务名称:" + vipTask.getTask_title() + ",已完成进度:" + check_success_num + "/" + array_size, user.getPhone(), user.getUser_code(), "审核进度");
//                                        }
//                                    }
//                                    JSONArray jsonArray1 = JSONArray.parseArray(examine_group);
//                                    for (int i = 0; i < jsonArray1.size(); i++) {
//                                        JSONArray jsonArray2 = jsonArray1.getJSONArray(i);
//                                        for (int j = 0; j < jsonArray2.size(); j++) {
//                                            String examine_user_code = jsonArray2.getString(j);
//                                            User user = userService.selectUserByCode(examineConfigure.getCorp_code(), examine_user_code, "Y");
//                                            String phone = user.getPhone();
//                                            iceInterfaceService.sendSmsV2(examineConfigure.getCorp_code(), "你有一条会员任务,任务名称:" + vipTask.getTask_title() + ",已完成进度:" + check_success_num + "/" + array_size, phone, examine_user_code, "审核进度");
//                                        }
//                                    }
//                                }
                            }
                            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                            dataBean.setId("1");
                            dataBean.setMessage("审核成功");
                        }else{
                            dataBean.setMessage("您没有审核权限");
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId("1");
                        }

                    }else if(type.equals("uncheck")){

                        //创建人等于审核人 系统管理员
                        if(role_code.equals(Common.ROLE_SYS)||(user_code.equals(vipTask.getCreater())&&corp_code.equals(vipTask.getCorp_code()))){
                            BasicDBObject basicDBObject=new BasicDBObject();
                            basicDBObject.put("vipTask.id",task_id);
                            basicDBObject.put("examine_type","vipTask");
                            cursor.remove(basicDBObject);
                            VipTask vipTask1=new VipTask();
                            vipTask1.setBill_status("0");
                            vipTask1.setId(task_id);
                            vipTaskService.update(vipTask1);

                            ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipTask.getCorp_code(),"会员任务");
                            if(examineConfigure!=null&&examineConfigure.getSms_result().equals("Y")){
                                User user=userService.selectUserByCode(vipTask.getCorp_code(),vipTask.getCreater(),"Y");
                                if(user!=null) {
                                    StringBuilder stringBuilder=new StringBuilder();
                                    stringBuilder.append("\"");
                                    stringBuilder.append(vipTask.getTask_title());
                                    stringBuilder.append("\"");
                                    stringBuilder.append("任务已被");
                                    stringBuilder.append("\"");
                                    stringBuilder.append(user.getUser_name());
                                    stringBuilder.append("\"");
                                    stringBuilder.append("驳回");
                                    stringBuilder.append("，请重新编辑");
                                    iceInterfaceService.sendSmsV2(vipTask.getCorp_code(),stringBuilder.toString(), user.getPhone(), user.getUser_code(), "取消提交");
                                }
                            }
                            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                            dataBean.setId("1");
                            dataBean.setMessage("取消提交成功");
                            return  dataBean.getJsonStr();
                        }

                        ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipTask.getCorp_code(),"会员任务");
                        if(examineConfigure==null){
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setMessage("会员任务未设置审核人");
                            dataBean.setId("1");
                            return  dataBean.getJsonStr();
                        }

                        boolean isflag=false;
                        String user_name="";
                        String examine_group=examineConfigure.getExamine_group();
                        JSONArray jsonArray=JSON.parseArray(examine_group);
                        int array_size=jsonArray.size();
                        for (int i = 0; i <array_size ; i++) {
                            JSONArray jsonArray1=jsonArray.getJSONArray(i);
                            if(jsonArray1.contains(user_code)&&corp_code.equals(examineConfigure.getCorp_code())){
                                isflag=true;
                                User user=userService.selectUserByCode(examineConfigure.getCorp_code(),user_code,"Y");
                                user_name=user.getUser_name();
                                break;
                            }
                        }
                        if(isflag==true){
                            //撤销审核 删表 状态改为0
                            BasicDBObject basicDBObject=new BasicDBObject();
                            basicDBObject.put("vipTask.id",task_id);
                            basicDBObject.put("examine_type","vipTask");
                            cursor.remove(basicDBObject);
                            VipTask vipTask1=new VipTask();
                            vipTask1.setBill_status("0");
                            vipTask1.setId(task_id);
                            vipTaskService.update(vipTask1);

                            if(examineConfigure.getSms_result().equals("Y")) {
                                User user = userService.selectUserByCode(vipTask.getCorp_code(), vipTask.getCreater(), "Y");
                                if(user!=null) {
                                    StringBuilder stringBuilder=new StringBuilder();
                                    stringBuilder.append("\"");
                                    stringBuilder.append(vipTask.getTask_title());
                                    stringBuilder.append("\"");
                                    stringBuilder.append("任务已被");
                                    stringBuilder.append("\"");
                                    stringBuilder.append(user_name);
                                    stringBuilder.append("\"");
                                    stringBuilder.append("驳回");
                                    stringBuilder.append("，请重新编辑");
                                    iceInterfaceService.sendSmsV2(vipTask.getCorp_code(),stringBuilder.toString(), user.getPhone(), user.getUser_code(), "取消提交");
                                }
                            }
                            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                            dataBean.setId("1");
                            dataBean.setMessage("取消提交成功");
                        }else{
                            dataBean.setMessage("您没有审核权限");
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId("1");
                        }
                    }
                }else{
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("会员任务不存在");
                }

            }else if(function_code.equals("F0032")){//会员活动

                String activity_code=jsonObject.getString("activity_code");
                String type=jsonObject.getString("type");//审核或者撤销审核
                VipActivity vipActivity=vipActivityService.getActivityByCode(activity_code);
                if(vipActivity!=null){
                    if(type.equals("check")){

                        //判断该审核人有没有审核过
                        BasicDBObject  basic_user=new BasicDBObject();
                        basic_user.put("activity.activity_code",activity_code);
                        basic_user.put("examine_user",user_code);
                        int count_check=cursor.find(basic_user).count();
                        if(count_check>0){
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId("1");
                            dataBean.setMessage("请勿重复审核");
                            return dataBean.getJsonStr();
                        }

                        ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipActivity.getCorp_code(),"会员活动");
                        if(examineConfigure==null){
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setMessage("会员活动未设置审核人");
                            dataBean.setId("1");
                            return  dataBean.getJsonStr();
                        }
                        String examine_group=examineConfigure.getExamine_group();
                        JSONArray jsonArray=JSON.parseArray(examine_group);
                        boolean isflag=false;
                        int array_size=jsonArray.size();
                        for (int i = 0; i <array_size ; i++) {
                            JSONArray jsonArray1=jsonArray.getJSONArray(i);
                            if(jsonArray1.contains(user_code)&&corp_code.equals(examineConfigure.getCorp_code())){
                                User user=userService.selectUserByCode(corp_code,user_code,"Y");
                                BasicDBObject basicDBObject=new BasicDBObject();
                                JSONObject user_josn=JSONObject.parseObject(JSON.toJSONString(user));
                                BSONObject bsonObject=new BasicBSONObject();
                                bsonObject.putAll(user_josn);
                                basicDBObject.put("user",bsonObject);
                                JSONObject activity_josn=JSONObject.parseObject(JSON.toJSONString(vipActivity));
                                BSONObject bsonObject1=new BasicBSONObject();
                                bsonObject1.putAll(activity_josn);
                                basicDBObject.put("activity",bsonObject1);
                                basicDBObject.put("examine_group",String.valueOf(i));
                                basicDBObject.put("created_date",simpleDateFormat.format(new Date()));
                                basicDBObject.put("examine_user",user_code);
                                basicDBObject.put("examine_type","vipActivity");
                                basicDBObject.put("status","2");
                                cursor.insert(basicDBObject);
                                isflag=true;
                                break;
                            }
                        }
                        if(isflag==true){
                            /**
                             * 审核成功的人数
                             */
                            int check_success_num=0;
                            BasicDBObject basDb=new BasicDBObject();
                            basDb.put("activity.activity_code",activity_code);
                            basDb.put("examine_type","vipActivity");
                            basDb.put("examine_group",new BasicDBObject("$ne",null));
                            BasicDBObject match_user = new BasicDBObject("$match", basDb);
                            /* Group操作*/
                            DBObject groupField = new BasicDBObject("_id","$examine_group");
                            groupField.put("count", new BasicDBObject("$sum", 1));
                            DBObject group_user = new BasicDBObject("$group", groupField);
                            AggregationOutput output_user = cursor.aggregate(match_user,group_user);
                            for(DBObject dbObject:output_user.results()){
                                String count=dbObject.get("count").toString();
                                if(Integer.parseInt(count)!=0){
                                    check_success_num++;
                                }
                            }

                            //如果审核进度完成 状态改为待执行按钮
                            if(check_success_num==array_size){
                                BasicDBObject basicDBObject=new BasicDBObject();
                                basicDBObject.put("activity.activity_code",activity_code);
                                basicDBObject.put("examine_type","vipActivity");
                                BasicDBObject update=new BasicDBObject();
                                update.put("$set",new BasicDBObject("status","3"));//全部审核完成
                                cursor.update(basicDBObject,update,false,true);

                                VipActivity vipActivity1=new VipActivity();
                                vipActivity1.setActivity_code(activity_code);
                                vipActivity1.setBill_status("2");
                                vipActivityService.updateVipActivity(vipActivity1);

                                //將活動相對應的會員任務的狀態改變
                                VipTask vipTask=vipTaskService.selectVipTaskByTaskTypeAndTitle(vipActivity.getCorp_code(),"activity",vipActivity.getActivity_code());
                                if(vipTask!=null) {
                                    vipTask.setBill_status("2");
                                    vipTaskService.update(vipTask);
                                }
                            }


                            //审核通过发送给创建人,审核进度发送给审核人，创建人
                            if(check_success_num==array_size){
                                if(examineConfigure.getSms_result().equals("Y")) {
                                    User user = userService.selectUserByCode(vipActivity.getCorp_code(), vipActivity.getCreater(), "Y");
                                    if(user!=null) {
                                        StringBuilder stringBuilder=new StringBuilder();
                                        stringBuilder.append("\"");
                                        stringBuilder.append(vipActivity.getActivity_theme());
                                        stringBuilder.append("\"");
                                        stringBuilder.append("活动通过审核，请开始执行");
                                        iceInterfaceService.sendSmsV2(corp_code, stringBuilder.toString(), user.getPhone(), user.getUser_code(), "审核完成");
                                    }
                                }
                            } else{
                                if(examineConfigure.getSms_progress().equals("Y")){
                                    User user = userService.selectUserByCode(vipActivity.getCorp_code(), vipActivity.getCreater(), "Y");
                                    if(user!=null) {
                                        JSONArray objects=new JSONArray();
                                        JSONArray jsonArray1=JSON.parseArray(examineConfigure.getExamine_group_info());
                                        for (int i = 0; i < jsonArray1.size(); i++) {
                                            JSONArray jsonArray2=jsonArray1.getJSONArray(i);
                                            objects.addAll(jsonArray2); //全部需审核员工
                                        }

                                        List<String> examineList=new ArrayList<String>();
                                        String examine_success="";
                                        String examine_fail="";
                                        BasicDBObject basicDBObject=new BasicDBObject();
                                        basicDBObject.put("examine_type","vipActivity");
                                        basicDBObject.put("activity.activity_code",activity_code);
                                        DBCursor dbObjects=cursor.find(basicDBObject);
                                        for(DBObject dbObject:dbObjects){
                                            String creater=dbObject.get("examine_user").toString();
                                            for (int i = 0; i < objects.size(); i++) {
                                                String creater_v2=objects.getString(i).split("\\(")[1].replace(")","");
                                                if(creater_v2.equals(creater)){
                                                    examine_success+=objects.getString(i)+"、";
                                                    examineList.add(objects.get(i).toString());
                                                }
                                            }
                                        }
                                        for (int i = 0; i <examineList.size() ; i++) {
                                            if(objects.contains(examineList.get(i))){
                                                objects.remove(examineList.get(i));
                                            }
                                        }
                                        for (int i = 0; i < objects.size(); i++) {
                                            examine_fail+=objects.getString(i)+"、";
                                        }
                                        if(examine_success.endsWith("、")){
                                            examine_success=examine_success.substring(0,examine_success.length()-1);
                                        }
                                        if(examine_fail.endsWith("、")){
                                            examine_fail=examine_fail.substring(0,examine_fail.length()-1);
                                        }
                                        StringBuilder stringBuilder=new StringBuilder();
                                        stringBuilder.append("\"");
                                        stringBuilder.append(vipActivity.getActivity_theme());
                                        stringBuilder.append("\"");
                                        stringBuilder.append("活动进度反馈：");
                                        stringBuilder.append("\"");
                                        stringBuilder.append(examine_success);
                                        stringBuilder.append("\"");
                                        stringBuilder.append("已审核通过，");
                                        stringBuilder.append("\"");
                                        stringBuilder.append(examine_fail);
                                        stringBuilder.append("\"");
                                        stringBuilder.append("未审核");
                                        iceInterfaceService.sendSmsV2(vipActivity.getCorp_code(),stringBuilder.toString(), user.getPhone(), user.getUser_code(), "审核进度");
                                    }
                                }
//                                if(examineConfigure.getSms_progress().equals("Y")) {
//                                    boolean isflag_v2 = false;
//                                    if (user_code.equals(vipActivity.getCreater()) && corp_code.equals(vipActivity.getCorp_code())) {
//                                        isflag_v2 = true;
//                                    }
//                                    if (isflag_v2 == false) {
//                                        User user = userService.selectUserByCode(vipActivity.getCorp_code(), vipActivity.getCreater(), "Y");
//                                        if(user!=null) {
//                                            iceInterfaceService.sendSmsV2(vipActivity.getCorp_code(), "你有一条会员活动,活动名称:" + vipActivity.getActivity_theme() + ",已完成进度:" + check_success_num + "/" + array_size, user.getPhone(), user.getUser_code(), "审核进度");
//                                        }
//                                    }
//                                    JSONArray jsonArray1 = JSONArray.parseArray(examine_group);
//                                    for (int i = 0; i < jsonArray1.size(); i++) {
//                                        JSONArray jsonArray2 = jsonArray1.getJSONArray(i);
//                                        for (int j = 0; j < jsonArray2.size(); j++) {
//                                            String examine_user_code = jsonArray2.getString(j);
//                                            User user = userService.selectUserByCode(examineConfigure.getCorp_code(), examine_user_code, "Y");
//                                            String phone = user.getPhone();
//                                            iceInterfaceService.sendSmsV2(examineConfigure.getCorp_code(), "你有一条会员活动,活动名称:" + vipActivity.getActivity_theme() + ",已完成进度:" + check_success_num + "/" + array_size, phone, examine_user_code, "审核进度");
//                                        }
//                                    }
//                                }
                            }

                            dataBean.setMessage("审核成功");
                            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                            dataBean.setId("1");
                        }else{
                            dataBean.setMessage("您没有审核权限");
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId("1");
                        }
                    }else if(type.equals("uncheck")){

                        //创建人与审核人相同时  或者为系统管理员
                        if(role_code.equals(Common.ROLE_SYS)||(user_code.equals(vipActivity.getCreater())&&corp_code.equals(vipActivity.getCorp_code()))){
                            //撤销审核 删表 状态改为0
                            BasicDBObject basicDBObject=new BasicDBObject();
                            basicDBObject.put("activity.activity_code",activity_code);
                            basicDBObject.put("examine_type","vipActivity");
                            cursor.remove(basicDBObject);
                            VipActivity vipActivity1=new VipActivity();
                            vipActivity1.setBill_status("0");
                            vipActivity1.setActivity_code(activity_code);
                            vipActivityService.updateVipActivity(vipActivity1);

                            ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipActivity.getCorp_code(),"会员活动");
                            if(examineConfigure!=null&&examineConfigure.getSms_result().equals("Y")){
                                User user=userService.selectUserByCode(vipActivity.getCorp_code(),vipActivity.getCreater(),"Y");
                                if(user!=null) {
                                    StringBuilder stringBuilder=new StringBuilder();
                                    stringBuilder.append("\"");
                                    stringBuilder.append(vipActivity.getActivity_theme());
                                    stringBuilder.append("\"");
                                    stringBuilder.append("活动已被");
                                    stringBuilder.append("\"");
                                    stringBuilder.append(user.getUser_name());
                                    stringBuilder.append("\"");
                                    stringBuilder.append("驳回");
                                    stringBuilder.append("，请重新编辑");
                                    iceInterfaceService.sendSmsV2(vipActivity.getCorp_code(),stringBuilder.toString(), user.getPhone(), user.getUser_code(), "取消提交");
                                }
                            }

                            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                            dataBean.setId("1");
                            dataBean.setMessage("取消提交成功");
                            return  dataBean.getJsonStr();
                        }

                        ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipActivity.getCorp_code(),"会员活动");
                        if(examineConfigure==null){
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setMessage("会员活动未设置审核人");
                            dataBean.setId("1");
                            return  dataBean.getJsonStr();
                        }

                        boolean isflag=false;
                        String user_name="";
                        String examine_group=examineConfigure.getExamine_group();
                        JSONArray jsonArray=JSON.parseArray(examine_group);
                        int array_size=jsonArray.size();
                        for (int i = 0; i <array_size ; i++) {
                            JSONArray jsonArray1=jsonArray.getJSONArray(i);
                            if(jsonArray1.contains(user_code)&&corp_code.equals(examineConfigure.getCorp_code())){
                                isflag=true;
                                User user=userService.selectUserByCode(examineConfigure.getCorp_code(),user_code,"Y");
                                user_name=user.getUser_name();
                                break;
                            }
                        }
                        if(isflag==true){
                            //撤销审核 删表 状态改为0
                            BasicDBObject basicDBObject=new BasicDBObject();
                            basicDBObject.put("activity.activity_code",activity_code);
                            basicDBObject.put("examine_type","vipActivity");
                            cursor.remove(basicDBObject);
                            VipActivity vipActivity1=new VipActivity();
                            vipActivity1.setBill_status("0");
                            vipActivity1.setActivity_code(activity_code);
                            vipActivityService.updateVipActivity(vipActivity1);

                            if(examineConfigure.getSms_result().equals("Y")) {
                                User user = userService.selectUserByCode(vipActivity.getCorp_code(), vipActivity.getCreater(), "Y");
                                if(user!=null) {
                                    StringBuilder stringBuilder=new StringBuilder();
                                    stringBuilder.append("\"");
                                    stringBuilder.append(vipActivity.getActivity_theme());
                                    stringBuilder.append("\"");
                                    stringBuilder.append("活动已被");
                                    stringBuilder.append("\"");
                                    stringBuilder.append(user_name);
                                    stringBuilder.append("\"");
                                    stringBuilder.append("驳回");
                                    stringBuilder.append("，请重新编辑");
                                    iceInterfaceService.sendSmsV2(vipActivity.getCorp_code(),stringBuilder.toString(), user.getPhone(), user.getUser_code(), "取消提交");
                                }
                            }
                            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                            dataBean.setId("1");
                            dataBean.setMessage("取消提交成功");
                        }else{
                            dataBean.setMessage("您没有审核权限");
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId("1");
                        }
                    }
                }else{
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("会员活动不存在");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    /*
     code -1  代码异常 弹出异常 （关闭按钮）
     code 0   status 1   审核人未审核（通过审核按钮  取消提交按钮  关闭）
     code 0   status 2   审核人已审核,审核已完成 （审核进度按钮  取消提交按钮  关闭）  注：审核人账号下
     code 0   status 3    审核已完成（开始执行按钮  取消提交按钮  关闭） 注:创建人账号下

     code 1   对应既不是审核人也不是创建人  （审核进度按钮  关闭）
     code 2   对应是创建人但不是审核人      （审核进度按钮  取消提交按钮  关闭）
     code 3   会员任务未设置审核人,立即执行  （开始执行按钮  取消提交按钮  关闭） 注:创建人账号下
     code 4   只显示关闭按钮
     */

    @RequestMapping(value = "/user/status",method = RequestMethod.POST)
    @ResponseBody
    public  String  seletUserStatus(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_function_check_schedule);
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String function_code=jsonObject.getString("function_code");
            if(function_code.equals("F0063")) {//会员任务

                int  task_id=jsonObject.getInteger("id");
                VipTask vipTask=vipTaskService.selectById(task_id);
                ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipTask.getCorp_code(),"会员任务");
                if(examineConfigure==null){
                    if(role_code.equals(Common.ROLE_SYS)||(user_code.equals(vipTask.getCreater())&&corp_code.equals(vipTask.getCorp_code()))){
                        dataBean.setCode("3"); //只显示取消提交的按钮
                        dataBean.setMessage("会员任务未设置审核人,立即执行");
                        dataBean.setId("1");
                    }else{
                        dataBean.setCode("4");//只显示关闭按钮
                        dataBean.setMessage("会员任务未设置审核人");
                        dataBean.setId("1");
                    }
                    return  dataBean.getJsonStr();
                }

                //判断此用户是否为创建人
                if(role_code.equals(Common.ROLE_SYS)||(user_code.equals(vipTask.getCreater())&&corp_code.equals(vipTask.getCorp_code()))){
                    BasicDBObject  basic_user=new BasicDBObject();
                    basic_user.put("vipTask.id",task_id);
                    DBCursor dbCursor=cursor.find(basic_user);
                    if(dbCursor.count()>0){
                        while (dbCursor.hasNext()){
                            DBObject dbObject= dbCursor.next();
                            if(dbObject.get("status").toString().equals("3")){
                                JSONObject jsonObject2=new JSONObject();
                                jsonObject2.put("status","3");
                                dataBean.setMessage(jsonObject2.toString());
                                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                                dataBean.setId("1");
                                return dataBean.getJsonStr();
                            }
                        }
                    }
                }

                boolean isflag=false;
                String examine_group=examineConfigure.getExamine_group();
                JSONArray jsonArray=JSON.parseArray(examine_group);
                int array_size=jsonArray.size();
                for (int i = 0; i <array_size ; i++) {
                    JSONArray jsonArray1=jsonArray.getJSONArray(i);
                    if(jsonArray1.contains(user_code)&&corp_code.equals(examineConfigure.getCorp_code())){
                        isflag=true;
                        break;
                    }
                }
                if(isflag==true){ //判断当前用户的审核状态
                    BasicDBObject  basic_user=new BasicDBObject();
                    basic_user.put("vipTask.id",task_id);
//                    DBCursor dbCursor=cursor.find(basic_user);
//                    if(dbCursor.count()>0){
//                        while (dbCursor.hasNext()){
//                            DBObject dbObject= dbCursor.next();
//                            if(dbObject.get("status").toString().equals("3")){
//                                JSONObject jsonObject2=new JSONObject();
//                                jsonObject2.put("status","3");
//                                dataBean.setMessage(jsonObject2.toString());
//                                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                                dataBean.setId("1");
//                                return dataBean.getJsonStr();
//                            }
//                        }
//                    }

                    basic_user.put("examine_user",user_code);
                    DBCursor dbObjects=cursor.find(basic_user);
                    if(dbObjects.count()>0){
                        for (DBObject dbObject:dbObjects){
                            String status=dbObject.get("status").toString();
                            if(status.equals("2")||status.equals("3")){
                                //显示进度
                                int check_success_num=0;
                                BasicDBObject basDb=new BasicDBObject();
                                basDb.put("vipTask.id",task_id);
                                basDb.put("examine_type","vipTask");
                                basDb.put("examine_group",new BasicDBObject("$ne",null));
                                BasicDBObject match_user = new BasicDBObject("$match", basDb);
                                /* Group操作*/
                                DBObject groupField = new BasicDBObject("_id","$examine_group");
                                groupField.put("count", new BasicDBObject("$sum", 1));
                                DBObject group_user = new BasicDBObject("$group", groupField);
                                AggregationOutput output_user = cursor.aggregate(match_user,group_user);
                                for(DBObject dbObject1:output_user.results()){
                                    String count=dbObject1.get("count").toString();
                                    if(Integer.parseInt(count)!=0){
                                        check_success_num++;
                                    }
                                }

                                JSONObject jsonObject2=new JSONObject();
                                jsonObject2.put("status","2");
                                jsonObject2.put("rate",check_success_num+"/"+array_size);
                                dataBean.setMessage(jsonObject2.toString());
                                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                                dataBean.setId("1");
                                return dataBean.getJsonStr();
                            }
                        }
                    }else{
                        JSONObject jsonObject2=new JSONObject();
                        jsonObject2.put("status","1");
                        dataBean.setMessage(jsonObject2.toString());
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setId("1");
                        return dataBean.getJsonStr();
                    }
                }else{
                    if(!user_code.equals(vipTask.getCreater())&&!role_code.equals(Common.ROLE_SYS)) {//既不是创建人也不是审核人
                        //显示进度
                        int check_success_num=0;
                        BasicDBObject basDb=new BasicDBObject();
                        basDb.put("vipTask.id",task_id);
                        basDb.put("examine_type","vipTask");
                        basDb.put("examine_group",new BasicDBObject("$ne",null));
                        BasicDBObject match_user = new BasicDBObject("$match", basDb);
                        /* Group操作*/
                        DBObject groupField = new BasicDBObject("_id","$examine_group");
                        groupField.put("count", new BasicDBObject("$sum", 1));
                        DBObject group_user = new BasicDBObject("$group", groupField);
                        AggregationOutput output_user = cursor.aggregate(match_user,group_user);
                        for(DBObject dbObject1:output_user.results()){
                            String count=dbObject1.get("count").toString();
                            if(Integer.parseInt(count)!=0){
                                check_success_num++;
                            }
                        }

                        JSONObject jsonObject2=new JSONObject();
                        jsonObject2.put("rate",check_success_num+"/"+array_size);
                        dataBean.setMessage(jsonObject2.toString());
                        dataBean.setCode("1");
                        dataBean.setId("1");
                        return dataBean.getJsonStr();
                    }else if((user_code.equals(vipTask.getCreater())&&corp_code.equals(vipTask.getCorp_code()))||role_code.equals(Common.ROLE_SYS)){ //是创建人，但不是审核人
                        int check_success_num=0;
                        BasicDBObject basDb=new BasicDBObject();
                        basDb.put("vipTask.id",task_id);
                        basDb.put("examine_type","vipTask");
                        basDb.put("examine_group",new BasicDBObject("$ne",null));
                        BasicDBObject match_user = new BasicDBObject("$match", basDb);
                        /* Group操作*/
                        DBObject groupField = new BasicDBObject("_id","$examine_group");
                        groupField.put("count", new BasicDBObject("$sum", 1));
                        DBObject group_user = new BasicDBObject("$group", groupField);
                        AggregationOutput output_user = cursor.aggregate(match_user,group_user);
                        for(DBObject dbObject1:output_user.results()){
                            String count=dbObject1.get("count").toString();
                            if(Integer.parseInt(count)!=0){
                                check_success_num++;
                            }
                        }

                        JSONObject jsonObject2=new JSONObject();
                        jsonObject2.put("rate",check_success_num+"/"+array_size);
                        dataBean.setMessage(jsonObject2.toString());
                        dataBean.setCode("2");
                        dataBean.setId("1");
                        return  dataBean.getJsonStr();
                    }
                }
            }else if(function_code.equals("F0032")){

                String  activity_code=jsonObject.getString("activity_code");
                VipActivity vipActivity=vipActivityService.getActivityByCode(activity_code);
                ExamineConfigure examineConfigure=examineConfigureService.selectByName(vipActivity.getCorp_code(),"会员活动");
                if(examineConfigure==null){
                    if(role_code.equals(Common.ROLE_SYS)||(user_code.equals(vipActivity.getCreater())&&corp_code.equals(vipActivity.getCorp_code()))){
                        dataBean.setCode("3");
                        dataBean.setMessage("会员活动未设置审核人,立即执行");
                        dataBean.setId("1");
                    }else{
                        dataBean.setCode("4");//只显示关闭按钮
                        dataBean.setMessage("会员活动未设置审核人");
                        dataBean.setId("1");
                    }
                    return  dataBean.getJsonStr();
                }

                //判断此用户是否为创建人
                if(role_code.equals(Common.ROLE_SYS)||(user_code.equals(vipActivity.getCreater())&&corp_code.equals(vipActivity.getCorp_code()))){
                    BasicDBObject  basic_user=new BasicDBObject();
                    basic_user.put("activity.activity_code",activity_code);
                    DBCursor dbCursor=cursor.find(basic_user);
                    if(dbCursor.count()>0){
                        while (dbCursor.hasNext()){
                            DBObject dbObject= dbCursor.next();
                            if(dbObject.get("status").toString().equals("3")){
                                JSONObject jsonObject2=new JSONObject();
                                jsonObject2.put("status","3");
                                dataBean.setMessage(jsonObject2.toString());
                                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                                dataBean.setId("1");
                                return dataBean.getJsonStr();
                            }
                        }
                    }
                }

                boolean isflag=false;
                String examine_group=examineConfigure.getExamine_group();
                JSONArray jsonArray=JSON.parseArray(examine_group);
                int array_size=jsonArray.size();
                for (int i = 0; i <array_size ; i++) {
                    JSONArray jsonArray1=jsonArray.getJSONArray(i);
                    if(jsonArray1.contains(user_code)&&corp_code.equals(examineConfigure.getCorp_code())){
                        isflag=true;
                        break;
                    }
                }
                if(isflag==true){ //判断当前用户的审核状态
                    BasicDBObject  basic_user=new BasicDBObject();
                    basic_user.put("activity.activity_code",activity_code);
//                    DBCursor dbCursor=cursor.find(basic_user);
//                    if(dbCursor.count()>0){
//                        while (dbCursor.hasNext()){
//                          DBObject dbObject= dbCursor.next();
//                          if(dbObject.get("status").toString().equals("3")){
//                              JSONObject jsonObject2=new JSONObject();
//                              jsonObject2.put("status","3");
//                              dataBean.setMessage(jsonObject2.toString());
//                              dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                              dataBean.setId("1");
//                              return dataBean.getJsonStr();
//                          }
//                        }
//                    }

                    basic_user.put("examine_user",user_code);
                    DBCursor dbObjects=cursor.find(basic_user);
                    if(dbObjects.count()>0){
                        for (DBObject dbObject:dbObjects){
                            String status=dbObject.get("status").toString();
                            if(status.equals("2")||status.equals("3")){
                                //显示进度
                                int check_success_num=0;
                                BasicDBObject basDb=new BasicDBObject();
                                basDb.put("activity.activity_code",activity_code);
                                basDb.put("examine_type","vipActivity");
                                basDb.put("examine_group",new BasicDBObject("$ne",null));
                                BasicDBObject match_user = new BasicDBObject("$match", basDb);
                                /* Group操作*/
                                DBObject groupField = new BasicDBObject("_id","$examine_group");
                                groupField.put("count", new BasicDBObject("$sum", 1));
                                DBObject group_user = new BasicDBObject("$group", groupField);
                                AggregationOutput output_user = cursor.aggregate(match_user,group_user);
                                for(DBObject dbObject1:output_user.results()){
                                    String count=dbObject1.get("count").toString();
                                    if(Integer.parseInt(count)!=0){
                                        check_success_num++;
                                    }
                                }

                                JSONObject jsonObject2=new JSONObject();
                                jsonObject2.put("status","2");
                                jsonObject2.put("rate",check_success_num+"/"+array_size);
                                dataBean.setMessage(jsonObject2.toString());
                                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                                dataBean.setId("1");
                                return dataBean.getJsonStr();
                            }
                        }
                    }else{
                        JSONObject jsonObject2=new JSONObject();
                        jsonObject2.put("status","1");
                        dataBean.setMessage(jsonObject2.toString());
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setId("1");
                        return dataBean.getJsonStr();
                    }
                }else{
                    if(!user_code.equals(vipActivity.getCreater())&&!role_code.equals(Common.ROLE_SYS)){ //既不是创建人也不是审核人
                        // 显示进度
                        int check_success_num=0;
                        BasicDBObject basDb=new BasicDBObject();
                        basDb.put("activity.activity_code",activity_code);
                        basDb.put("examine_type","vipActivity");
                        basDb.put("examine_group",new BasicDBObject("$ne",null));
                        BasicDBObject match_user = new BasicDBObject("$match", basDb);
                        /* Group操作*/
                        DBObject groupField = new BasicDBObject("_id","$examine_group");
                        groupField.put("count", new BasicDBObject("$sum", 1));
                        DBObject group_user = new BasicDBObject("$group", groupField);
                        AggregationOutput output_user = cursor.aggregate(match_user,group_user);
                        for(DBObject dbObject1:output_user.results()){
                            String count=dbObject1.get("count").toString();
                            if(Integer.parseInt(count)!=0){
                                check_success_num++;
                            }
                        }
                        JSONObject jsonObject2=new JSONObject();
                        jsonObject2.put("rate",check_success_num+"/"+array_size);
                        dataBean.setMessage(jsonObject2.toString());
                        dataBean.setCode("1");
                        dataBean.setId("1");
                        return dataBean.getJsonStr();
                    }else if((user_code.equals(vipActivity.getCreater())&&corp_code.equals(vipActivity.getCorp_code()))||role_code.equals(Common.ROLE_SYS)){//是创建人 但不是审核人
                        int check_success_num=0;
                        BasicDBObject basDb=new BasicDBObject();
                        basDb.put("activity.activity_code",activity_code);
                        basDb.put("examine_type","vipActivity");
                        basDb.put("examine_group",new BasicDBObject("$ne",null));
                        BasicDBObject match_user = new BasicDBObject("$match", basDb);
                        /* Group操作*/
                        DBObject groupField = new BasicDBObject("_id","$examine_group");
                        groupField.put("count", new BasicDBObject("$sum", 1));
                        DBObject group_user = new BasicDBObject("$group", groupField);
                        AggregationOutput output_user = cursor.aggregate(match_user,group_user);
                        for(DBObject dbObject1:output_user.results()){
                            String count=dbObject1.get("count").toString();
                            if(Integer.parseInt(count)!=0){
                                check_success_num++;
                            }
                        }
                        JSONObject jsonObject2=new JSONObject();
                        jsonObject2.put("rate",check_success_num+"/"+array_size);
                        dataBean.setMessage(jsonObject2.toString());
                        dataBean.setCode("2");
                        dataBean.setId("1");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setMessage(e.getMessage());
            dataBean.setCode("-1");//代码异常返回code
            dataBean.setId("1");
        }
        return  dataBean.getJsonStr();
    }

}
