package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.VipTaskMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by yanyadong on 2017/4/24.
 */
@Service
public class VipTaskServiceImpl implements VipTaskService {
    @Autowired
    VipTaskMapper vipTaskMapper;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    VipFsendService vipFsendService;
    @Autowired
    CorpService corpService;
    @Autowired
    VipPointsAdjustService vipPointsAdjustService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    WxTemplateService wxTemplateService;
    @Autowired
    MongoDBClient mongoDBClient;

    private static final Logger logger = Logger.getLogger(VipTaskServiceImpl.class);

/*  任务类型对应的任务条件格式

            分享商品 share_goods
            {“shareCount":"1","shareUrl":"http//:www.baidu.com"}
            分享次数 share_counts
            {“shareCount":"1","shareUrl":"http//:www.baidu.com"}
            积分积累 integral_accumulate
            100
            累计消费次数  consume_count
            10
            累计消费金额 consume_money
            100
            最高客单价 ticket_sales
            100
            邀请注册  invite_registration
            100
            参与活动次数 activity_count
            {”activity_type":"活动类型","count":"1"}
            完善资料   improve_data
           [{选择的条件},{选择的条件},{选择的条件},{选择的条件}]
*/

    @Override
    public VipTask selectById(int id) throws Exception {
        VipTask vipTask=vipTaskMapper.selectById(id);
        return vipTask;
    }

    @Override
    public VipTask selectByTaskCode(String task_code) throws Exception {
        List<VipTask> vipTask=vipTaskMapper.selectByTaskCode(task_code);
        if (vipTask.size() > 0){
            return vipTask.get(0);
        }else {
            return null;
        }
    }

    @Override
    public List<VipTask> selectByTaskTitle(String corp_code, String task_title) throws Exception {
        List<VipTask> vipTask=vipTaskMapper.selectByTaskTitle(corp_code,task_title);
        return vipTask;
    }

    @Override
    public PageInfo<VipTask> selectAll(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_num,page_size);
        List<VipTask> vipTaskList;
        vipTaskList=vipTaskMapper.selectAll(corp_code,search_value,"","");
        PageInfo<VipTask> pageInfo=new PageInfo<VipTask>(vipTaskList);
        return pageInfo;
    }

    @Override
    public List<VipTask> selectAllByStatus(String corp_code, String search_value, String status, String is_advance_show) throws Exception {
        List<VipTask> vipTaskList=vipTaskMapper.selectAll(corp_code,search_value,status,is_advance_show);
        return vipTaskList;
    }


    @Override
    public int deleteById(int id) throws Exception {
        VipTask vipTask = selectById(id);
        if (vipTask != null) {
            scheduleJobService.deleteScheduleByGroup(vipTask.getTask_code());

            MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);

            DBObject updateCondition = new BasicDBObject();
            updateCondition.put("task_code", vipTask.getTask_code());

            cursor.remove(updateCondition);
        }
        vipTaskMapper.deleteById(id);
        return 0;
    }

    @Override
    public int deleteByCode(String vip_task_code) throws Exception {
        VipTask vipTask = selectByTaskCode(vip_task_code);
        if (vipTask != null) {
            scheduleJobService.deleteScheduleByGroup(vipTask.getTask_code());

            MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);

            DBObject updateCondition = new BasicDBObject();
            updateCondition.put("task_code", vipTask.getTask_code());

            cursor.remove(updateCondition);
        }
        vipTaskMapper.deleteByCode(vip_task_code);
        return 0;
    }

    public VipTask selectTargetCount(VipTask vipTask) throws Exception {
        String task_type = vipTask.getTask_type();
        String task_condition = vipTask.getTask_condition();
        vipTask.setTarget_count("0");
        String[] types = {"consume_count","consume_money","ticket_sales","invite_registration","share_goods"};

        if (Arrays.asList(types).contains(task_type)) {
            if (task_condition.startsWith("{")){
                JSONObject condition_obj = JSONObject.parseObject(task_condition);
                vipTask.setTarget_count(condition_obj.getString("count"));
            }else {
                vipTask.setTarget_count(task_condition);
            }
        }else if (task_type.equals("share_counts")) {
            JSONObject condition_obj = JSONObject.parseObject(task_condition);
            vipTask.setTarget_count(condition_obj.getString("shareCount"));
        } else if (task_type.equals("activity_count")) {
            JSONObject condition_obj = JSONObject.parseObject(task_condition);
            vipTask.setTarget_count(condition_obj.getString("count"));
        }else if (task_type.equals("improve_data")) {
            JSONArray array = JSONArray.parseArray(task_condition);
            vipTask.setTarget_count(String.valueOf(array.size()));
        }
        return vipTask;
    }

    @Override
    public String inserVipTask(VipTask vipTask,String user_code,String group_code,String role_code) throws Exception {
        String result = Common.DATABEAN_CODE_SUCCESS;
        String end_time=vipTask.getEnd_time();
        String start_time = vipTask.getStart_time();

        if (TimeUtils.compareDateTime(end_time,Common.DATETIME_FORMAT.format(new Date()),Common.DATETIME_FORMAT)){
            return "结束时间不能早于当前时间";
        }
        if (TimeUtils.compareDateTime(start_time,Common.DATETIME_FORMAT.format(new Date()),Common.DATETIME_FORMAT)){
            vipTask.setTask_status(Common.ACTIVITY_STATUS_0);
            start_time = Common.DATETIME_FORMAT.format(new Date());
            vipTask.setStart_time(start_time);
        }
        DataBox dataBox = iceInterfaceService.getSolrParam(vipTask.getTarget_vips_(),vipTask.getCorp_code(),"old");
        if (dataBox.status.toString().equals("SUCCESS")){
            String message=JSONObject.parseObject(dataBox.data.get("message").value).getString("message");
            vipTask.setTarget_vips_condition(message);
        }
        vipTaskMapper.inserVipTask(vipTask);
        Date now = new Date();
        String is_send_notice = vipTask.getIs_send_notice();
        List<VipFsend> vipFsends = vipFsendService.getSendByActivityCode(vipTask.getCorp_code(),"VIPTASK"+vipTask.getTask_code());
        if (is_send_notice == null || (is_send_notice != null && is_send_notice.equals("Y"))){

            JSONObject notice = wxTemplateService.newVipTaskNotice(vipTask.getApp_id(),vipTask.getTask_title());
            if (notice == null){
                return "未设置任务通知模板";
            }
            if (vipFsends.size() == 0){
                VipFsend vipFsend = new VipFsend();
                vipFsend.setSms_code("Fs" + vipTask.getCorp_code() + Common.DATETIME_FORMAT_DAY_NUM.format(new Date()));
                vipFsend.setSend_type("wxmass");
                vipFsend.setCorp_code(vipTask.getCorp_code());
                vipFsend.setSend_scope("vip_condition");
                vipFsend.setActivity_vip_code("VIPTASK"+vipTask.getTask_code());
                vipFsend.setApp_id(vipTask.getApp_id());
                vipFsend.setCheck_status("N");
                vipFsend.setSms_vips(vipTask.getTarget_vips());
                vipFsend.setSms_vips_(vipTask.getTarget_vips_());
                vipFsend.setSend_time("");

                vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setCreater(user_code);
                vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setModifier(user_code);
                vipFsend.setIsactive("Y");
                //群发消息筛选条件
                vipFsendService.insertSend(vipFsend);
            }
        }
        return result;
    }

    @Override
    public String updateVipTask(VipTask vipTask,String user_code,String group_code,String role_code) throws Exception {
        String result = Common.DATABEAN_CODE_SUCCESS;
        String end_time=vipTask.getEnd_time();

        if (TimeUtils.compareDateTime(end_time,Common.DATETIME_FORMAT.format(new Date()),Common.DATETIME_FORMAT)){
            return "结束时间不能早于当前时间";
        }
        vipTask.setTask_status(Common.ACTIVITY_STATUS_0);

        DataBox dataBox = iceInterfaceService.getSolrParam(vipTask.getTarget_vips_(),vipTask.getCorp_code(),"old");
        if (dataBox.status.toString().equals("SUCCESS")){
            String message=JSONObject.parseObject(dataBox.data.get("message").value).getString("message");
            vipTask.setTarget_vips_condition(message);
        }
        Date now = new Date();
        vipTask.setModified_date(Common.DATETIME_FORMAT.format(now));
        vipTask.setModifier(user_code);
        vipTaskMapper.updateVipTask(vipTask);

        String is_send_notice = vipTask.getIs_send_notice();
        List<VipFsend> vipFsends = vipFsendService.getSendByActivityCode(vipTask.getCorp_code(),"VIPTASK"+vipTask.getTask_code());
        if (is_send_notice == null || (is_send_notice != null && is_send_notice.equals("Y"))){
            JSONObject notice = wxTemplateService.newVipTaskNotice(vipTask.getApp_id(),vipTask.getTask_title());
            if (notice == null){
                return "未设置任务通知模板";
            }
            if (vipFsends.size() == 0){
                VipFsend vipFsend = new VipFsend();
                vipFsend.setSms_code("Fs" + vipTask.getCorp_code() + Common.DATETIME_FORMAT_DAY_NUM.format(new Date()));
                vipFsend.setSend_type("wxmass");
                vipFsend.setCorp_code(vipTask.getCorp_code());
                vipFsend.setSend_scope("vip_condition");
                vipFsend.setActivity_vip_code("VIPTASK"+vipTask.getTask_code());
                vipFsend.setApp_id(vipTask.getApp_id());
                vipFsend.setCheck_status("N");
                vipFsend.setSms_vips(vipTask.getTarget_vips());
                vipFsend.setSms_vips_(vipTask.getTarget_vips_());
                vipFsend.setSend_time("");

                vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setCreater(user_code);
                vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setModifier(user_code);
                vipFsend.setIsactive("Y");
                //群发消息筛选条件
                vipFsendService.insertSend(vipFsend);
            }
        }
        return result;
    }

    @Override
    public String executeVipTask(VipTask vipTask,String user_code,String group_code,String role_code) throws Exception {
        String result = Common.DATABEAN_CODE_SUCCESS;
        String end_time=vipTask.getEnd_time();
        String start_time = vipTask.getStart_time();

        JSONObject func = new JSONObject();
        func.put("method","VipTask");
        func.put("corp_code",vipTask.getCorp_code());
        func.put("code",vipTask.getTask_code());

        List<VipFsend> vipFsends = vipFsendService.getSendByActivityCode(vipTask.getCorp_code(),"VIPTASK"+vipTask.getTask_code());
        if (vipFsends.size() > 0){
            VipFsend vipFsend = vipFsends.get(0);
            String send_time = Common.DATETIME_FORMAT.format(TimeUtils.getLastMin(new Date(),2));
            vipFsend.setSend_time(send_time);
            vipFsendService.updateVipFsend(vipFsend);
            result = vipFsendService.checkVipFsend(vipFsends.get(0),user_code);
        }
        // 开始时间 < 当前时间
        if (TimeUtils.compareDateTime(start_time,Common.DATETIME_FORMAT.format(new Date()),Common.DATETIME_FORMAT)){
            //执行已生效
            vipTask.setTask_status(Common.ACTIVITY_STATUS_1);
        }else {
            //执行未生效
            vipTask.setTask_status("0.5");
            //创建schedule，开始任务
            String start_corn = TimeUtils.getCron(Common.DATETIME_FORMAT.parse(start_time));

            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setJob_name("StartVipTask"+vipTask.getTask_code());
            scheduleJob.setJob_group(vipTask.getTask_code());
            scheduleJob.setFunc(func.toString());
            scheduleJob.setCron_expression(start_corn);
            scheduleJob.setStatus("N");
            scheduleJobService.insert(scheduleJob);
        }

        Date now = new Date();
        vipTask.setModified_date(Common.DATETIME_FORMAT.format(now));
        vipTask.setModifier(user_code);
        vipTaskMapper.updateVipTask(vipTask);

        String point = vipTask.getPresent_point();
        if (!point.equals("")){
            VipPointsAdjust pointsAdjust = vipPointsAdjustService.selectPointsAdjustByBillCode(vipTask.getTask_code());
            if (pointsAdjust == null){
                pointsAdjust = new VipPointsAdjust();
                pointsAdjust.setIsactive("Y");
                pointsAdjust.setBill_name(vipTask.getTask_title());
                pointsAdjust.setBill_code(vipTask.getTask_code());
                pointsAdjust.setBill_state("1");
                pointsAdjust.setBill_type("2");
                pointsAdjust.setAdjust_time(vipTask.getStart_time());
                pointsAdjust.setCorp_code(vipTask.getCorp_code());
                pointsAdjust.setCreated_date(Common.DATETIME_FORMAT.format(now));
                pointsAdjust.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipPointsAdjustService.insertPointsAdjust(pointsAdjust);
            }
        }

        //定时任务，结束任务
        String end_corn = TimeUtils.getCron(Common.DATETIME_FORMAT.parse(end_time));
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJob_name("EndVipTask"+vipTask.getTask_code());
        scheduleJob.setJob_group(vipTask.getTask_code());
        scheduleJob.setFunc(func.toString());
        scheduleJob.setCron_expression(end_corn);
        scheduleJob.setStatus("N");
        scheduleJobService.insert(scheduleJob);

        return result;
    }

    @Override
    public void update(VipTask vipTask) throws Exception {
        vipTaskMapper.updateVipTask(vipTask);
        return ;
    }

    @Override
    public PageInfo<VipTask> selectAllScreen(int page_num, int page_size, String corp_code, Map<String, Object> map) throws Exception {

        List<VipTask> vipTaskList;
        PageHelper.startPage(page_num,page_size);
        HashMap<String,Object> param=new HashMap<String, Object>();

        Set<String> sets=map.keySet();
        if(sets.contains("created_date")) {
            JSONObject date = JSONObject.parseObject(map.get("created_date").toString());
            param.put("created_date_start", date.get("start").toString());
            String end = date.get("end").toString();
             if (!end.equals(""))
                 end = end + " 23:59:59";
            param.put("created_date_end", end);
            map.remove("created_date");
        }
        if(sets.contains("start_time")) {
            JSONObject date = JSONObject.parseObject(map.get("start_time").toString());
            param.put("taskS_time_start", date.get("start").toString());
            String end = date.get("end").toString();
            if (!end.equals(""))
                end = end + " 23:59:59";
            param.put("taskS_time_end", end);
            map.remove("start_time");
        }
        if(sets.contains("end_time")) {
            JSONObject date = JSONObject.parseObject(map.get("end_time").toString());
            param.put("taskE_time_start", date.get("start").toString());
            String end = date.get("end").toString();
            if (!end.equals(""))
                end = end + " 23:59:59";
            param.put("taskE_time_end", end);
            map.remove("end_time");
        }

        param.put("corp_code",corp_code);
        for(String key:map.keySet()){
            if (key.equals("task_status")){
                map.put("task_status",map.get("task_status").toString().replace("'",""));
            }
        }
        param.put("map",map);

        System.out.println(map);
        if (map.size() == 0){
            vipTaskList=vipTaskMapper.selectAll(corp_code,"","","");
        }else {
            vipTaskList=vipTaskMapper.selectAllScreen(param);

        }
        PageInfo<VipTask> pageInfo=new PageInfo<VipTask>(vipTaskList);
        return pageInfo;
    }


    /**
     *
     * @param task
     * @param card_no
     * @param vip
     * @param status  0：未完成，1：已完成
     * @return flag: 1 赠送积分券；flag: 0 不赠送
     * @throws Exception
     */
    public int updateVipTaskSchedule(VipTask task,String card_no,String app_id,String open_id,JSONObject vip,String status,JSONArray schedule) throws Exception {
        String corp_code = task.getCorp_code();
        String task_code = task.getTask_code();
        task = selectTargetCount(task);
        int target_count = Integer.parseInt(task.getTarget_count());
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
//        Map keyMap = new HashMap();
//        keyMap.put("_id", corp_code+"_"+task_code+"_"+card_no);
        BasicDBObject queryCondition = new BasicDBObject();
//        queryCondition.putAll(keyMap);
        queryCondition.put("corp_code",corp_code);
        queryCondition.put("app_id",app_id);
        queryCondition.put("open_id",open_id);
        queryCondition.put("task_code",task_code);

        DBCursor dbCursor1 = cursor.find(queryCondition);
        int flag = 0;
        if (dbCursor1.size() > 0) {
            //记录存在，更新
            DBObject dbObject = dbCursor1.next();

            JSONArray array = new JSONArray();
            String status_com = dbObject.get("status").toString();
            if (status_com.equals("0")){
                if (dbObject.containsField("schedule")){
                    String schedule1 = dbObject.get("schedule").toString();
                    array = JSONArray.parseArray(schedule1);
                }
                DBObject updateCondition = new BasicDBObject();
                updateCondition.put("_id", corp_code+"_"+task_code+"_"+open_id);

                DBObject updatedValue = new BasicDBObject();
                updatedValue.put("status", status);
                if (schedule != null && !schedule.isEmpty()){
                    if (task.getTask_type().equals("improve_data")){
                        array = schedule;
                    }else {
                        array.addAll(schedule);
                    }

                    updatedValue.put("schedule", array);
                    if (task.getTask_type().equals("share_counts") && array.size() >= target_count){
                        updatedValue.put("status", "1");
                        if (!(dbObject.containsField("is_send") && dbObject.get("is_send").equals("Y"))){
                            flag = 1;
                            updatedValue.put("is_send", "Y");
                        }
                    }
                }
                if (status.equals("1")){
                    updatedValue.put("is_send", "Y");
                    flag = 1;
                }
                if (dbObject.containsField("is_send") && dbObject.get("is_send").equals("Y"))
                    flag = 0;

                updatedValue.put("vip", vip);
                updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                cursor.update(updateCondition, updateSetValue);

                //更新vipSearch
                DBCursor dbCursor2 = cursor.find(queryCondition);
                if ( dbCursor2.next().get("status").toString().equals("1")){
                    DataBox dataBox = iceInterfaceAPIService.DisposeTaskData(corp_code,vip.getString("vip_id"),task_code);
                    logger.info("====更新vipSearch==DisposeTaskData corp_code:"+corp_code+"==vip_id:"+vip.getString("vip_id")+"=====task_code:"+task_code+ dataBox.status.toString()+dataBox.msg);
                }
            }

        } else {
            //记录不存在，插入
            DBObject saveData = new BasicDBObject();
            saveData.put("_id", corp_code+"_"+task_code+"_"+open_id);
            saveData.put("corp_code", corp_code);
            saveData.put("card_no", card_no);
            saveData.put("app_id", app_id);
            saveData.put("open_id", open_id);
            saveData.put("vip_id", vip.getString("vip_id"));
            saveData.put("vip", vip);
            saveData.put("task_code", task_code);
            saveData.put("target_count", task.getTarget_count());

            JSONObject task_obj = WebUtils.bean2JSONObject(task);
            String task_type = task.getTask_type();
            if (task_type.equals("questionnaire")){
                JSONObject obj = JSONObject.parseObject(task.getTask_condition());
                task_obj.put("qtNaire_id",obj.getString("id"));
            }
            saveData.put("task",task_obj );

            JSONArray array = new JSONArray();
            if (schedule != null && !schedule.isEmpty())
                array.addAll(schedule);
            saveData.put("schedule", array);

            saveData.put("status", status);
            if (status.equals("1")){
                saveData.put("is_send", "Y");
                flag = 1;
            }
            saveData.put("created_date", Common.DATETIME_FORMAT.format(new Date()));
            saveData.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));

            if (task_type.equals("consume_count") || task_type.equals("consume_money") || task_type.equals("ticket_sales")){
                sendPresent(task,corp_code,vip.getString("vip_id"),app_id,open_id,"");
                saveData.put("is_send", "Y");
                flag = 1;
            }
            cursor.save(saveData);

            //更新vipSearch
            DBCursor dbCursor2 = cursor.find(queryCondition);
            if ( dbCursor2.next().get("status").toString().equals("1")){
                DataBox dataBox = iceInterfaceAPIService.DisposeTaskData(corp_code,vip.getString("vip_id"),task_code);
                logger.info("====更新vipSearch==DisposeTaskData corp_code:"+corp_code+"==vip_id:"+vip.getString("vip_id")+"=====task_code:"+task_code+ dataBox.status.toString()+dataBox.msg);
            }
        }

        return flag;
    }


    public VipTask vipTaskSchedule(VipTask vipTask,String card_no,JSONObject vip_info,int flag,String app_id,String open_id) throws Exception{
        String corp_code = vipTask.getCorp_code();
        String task_type = vipTask.getTask_type();
        String vip_id = vip_info.getString("vip_id");
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);

        Double num_trade = 0d;
        Double amt_trade = 0d;
        Double ticket_sales = 0d;
        vipTask = selectTargetCount(vipTask);
        if (vipTask.getTask_status().equals("1")){
            if (flag == 1){
                String task_condition = vipTask.getTask_condition();
                String start_time = "";
                String end_time = "";
                if (task_condition.startsWith("{")) {
                    JSONObject condition_obj = JSONObject.parseObject(task_condition);
                    start_time = condition_obj.getString("start_time");
                    end_time = condition_obj.getString("end_time");
                }
                DataBox dataBox = iceInterfaceAPIService.VipDetail(corp_code,"",card_no,"",start_time,end_time);
                if (dataBox.status.toString().equals("SUCCESS")){
                    String msg = dataBox.data.get("message").value;
                    JSONObject msg_obj = JSONObject.parseObject(msg);
                    amt_trade = Double.parseDouble(msg_obj.get("amt_trade").toString());//金额
                    num_trade = Double.parseDouble(msg_obj.get("num_trade").toString());//笔数
                    if (msg_obj.containsKey("ticket_sales"))//客单价
                        ticket_sales = Double.parseDouble(msg_obj.get("ticket_sales").toString());
                }
            }
            vipTask.setSchedule("未完成");
            if (task_type.equals("consume_count")){
                int task_count = Integer.parseInt(vipTask.getTarget_count());
                if (task_count > num_trade){
                    vipTask.setComplete_count(String.valueOf(num_trade.intValue()));
                }else {
                    vipTask.setSchedule("已完成");
                    updateVipTaskSchedule(vipTask,card_no,app_id,open_id,vip_info,"1",null);
                }
            }else if (task_type.equals("consume_money")){
                Double task_count = Double.parseDouble(vipTask.getTarget_count());
                if (task_count > amt_trade){
                    vipTask.setComplete_count(String.valueOf(amt_trade));
                }else {
                    vipTask.setSchedule("已完成");
                    updateVipTaskSchedule(vipTask,card_no,app_id,open_id,vip_info,"1",null);
                }
            }else if (task_type.equals("ticket_sales")){
                vipTask.setComplete_count("0.0");
                Double task_count = Double.parseDouble(vipTask.getTarget_count());
                if (task_count > ticket_sales){
                    vipTask.setComplete_count(String.valueOf(ticket_sales));
                }else {
                    vipTask.setSchedule("已完成");
                    updateVipTaskSchedule(vipTask,card_no,app_id,open_id,vip_info,"1",null);
                }
            }else {
//                Map keyMap = new HashMap();
//                keyMap.put("_id", corp_code+"_"+vipTask.getTask_code()+"_"+card_no);
                BasicDBObject queryCondition = new BasicDBObject();
                queryCondition.put("corp_code",corp_code);
                queryCondition.put("app_id",app_id);
                queryCondition.put("open_id",open_id);
                queryCondition.put("task_code",vipTask.getTask_code());
//                queryCondition.putAll(keyMap);
                DBCursor dbCursor1 = collection.find(queryCondition);
                if (dbCursor1.hasNext()){
                    DBObject object = dbCursor1.next();
                    if (object.get("status").equals("1")){
                        vipTask.setSchedule("已完成");
                    }
                    if (object.get("status").equals("0") && object.containsField("schedule")){
                        String schedule = object.get("schedule").toString();
                        vipTask.setComplete_count(String.valueOf(JSONArray.parseArray(schedule).size()));
                    }else {
                        vipTask.setComplete_count("0");
                    }
                }else {
                    vipTask.setComplete_count("0");
                    updateVipTaskSchedule(vipTask,card_no,app_id,open_id,vip_info,"0",null);
                }
            }
        }else {
            vipTask.setSchedule("未完成");
            vipTask.setComplete_count("0");
        }
        return vipTask;
    }

    //转换任务类型
    public PageInfo<VipTask> switchTaskType(PageInfo<VipTask> list) throws Exception{

        for(int i=0;i<list.getList().size();i++){
            VipTask vipTask=list.getList().get(i);
            String task_type=vipTask.getTask_type();
            String isactive=vipTask.getIsactive();
            String task_status=vipTask.getTask_status();
            if(task_type.equals("share_goods")){
                vipTask.setTask_type("分享商品");
            }else if(task_type.equals("share_counts")){
                vipTask.setTask_type("分享次数");
            }else if(task_type.equals("integral_accumulate")){
                vipTask.setTask_type("积分积累");
            }else if(task_type.equals("consume_count")){
                vipTask.setTask_type("累计消费次数");
            }else if(task_type.equals("consume_money")){
                vipTask.setTask_type("累计消费金额");
            }else if(task_type.equals("ticket_sales")){
                vipTask.setTask_type("最高客单价");
            }else if(task_type.equals("invite_registration")){
                vipTask.setTask_type("邀请注册");
            }else if(task_type.equals("activity_count")){
                vipTask.setTask_type("参与活动次数");
            }else if(task_type.equals("improve_data")){
                vipTask.setTask_type("完善资料");
            }else if(task_type.equals("activity")){
                vipTask.setTask_type("会员活动");
            }else if(task_type.equals("questionnaire")){
                vipTask.setTask_type("问卷调查");
            }

            if(isactive.equals("Y")){
                vipTask.setIsactive("是");
            }else{
                vipTask.setIsactive("否");
            }
//            if(task_status.equals("0")){
//                vipTask.setTask_status("未执行");
//            }else if(task_status.equals("0.5")){
//                vipTask.setTask_status("执行未生效");
//            }else if(task_status.equals("1")){
//                vipTask.setTask_status("执行已生效");
//            }else if(task_status.equals("2")){
//                vipTask.setTask_status("已结束");
//            }

        }
        return  list;

    }

    //转换任务类型
    public VipTask switchTaskType(VipTask vipTask, HttpServletRequest request) throws Exception{
        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
            String task_type=vipTask.getTask_type();
            if(task_type.equals("share_goods")){
                vipTask.setTask_type_name("分享商品");
            }else if(task_type.equals("share_counts")){
                vipTask.setTask_type_name("分享次数");
            }else if(task_type.equals("integral_accumulate")){
                vipTask.setTask_type_name("积分积累");
            }else if(task_type.equals("consume_count")){
                vipTask.setTask_type_name("累计消费次数");
            }else if(task_type.equals("consume_money")){
                vipTask.setTask_type_name("累计消费金额");
            }else if(task_type.equals("ticket_sales")){
                vipTask.setTask_type_name("最高客单价");
            }else if(task_type.equals("invite_registration")){
                vipTask.setTask_type_name("邀请注册");
            }else if(task_type.equals("activity_count")){
                vipTask.setTask_type_name("参与活动次数");
            }else if(task_type.equals("improve_data")){
                vipTask.setTask_type_name("完善资料");
            }else if(task_type.equals("activity")){
                vipTask.setTask_type_name("会员活动");
            }else if(task_type.equals("questionnaire")){
                vipTask.setTask_type_name("问卷调查");
            }
//        JSONArray jsonArray= vipGroupService.vipScreen2Array(JSON.parseArray(vipTask.getTarget_vips()),vipTask.getCorp_code(),role_code, brand_code, area_code, store_code, user_code);
        //目标会员数
        DataBox databox=iceInterfaceService.vipScreenMethod2("1","1",vipTask.getCorp_code(),vipTask.getTarget_vips_(),null,null);
        String target_count="0";
        if (databox.status.toString().equals("SUCCESS")) {
            String result = databox.data.get("message").value;
            JSONObject result_obj = JSONObject.parseObject(result);
            target_count=result_obj.getString("count");
        }
        vipTask.setTarget_vip_count(target_count);

        //企业名称
        Corp corp=corpService.selectByCorpId(0,vipTask.getCorp_code(),null);
        vipTask.setCorp_name(corp.getCorp_name());
        return  vipTask;

    }

    public void sendPresent(VipTask vipTask,String corp_code,String vip_id,String app_id,String open_id,String desc) throws Exception{
        logger.info("--------sendPresent--------"+vipTask.getTask_code());
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor2 = mongoTemplate.getCollection(CommonValue.table_vip_points_adjust);

        String present_coupon = vipTask.getPresent_coupon();
        String task_code = vipTask.getTask_code();
        String batch_no = vipTask.getBatch_no();
        if (batch_no == null || batch_no.equals("")){
            String batchDate = TimeUtils.getCurrentTimeInString(Common.DATETIME_FORMAT_DAY_NO);
            batch_no = String.valueOf(Long.toString(Long.parseLong(batchDate), 36).toUpperCase() + "mm" + Long.toString(System.currentTimeMillis(), 36).toUpperCase());
            vipTask.setBatch_no(batch_no);
            update(vipTask);
        }
        if (present_coupon != null && !present_coupon.equals("")){
            JSONArray coupon_array = JSONArray.parseArray(present_coupon);
            for (int i = 0; i < coupon_array.size(); i++) {
                JSONObject coupon_obj = coupon_array.getJSONObject(i);
                String coupon_code = coupon_obj.getString("coupon_code");
                String coupon_name = coupon_obj.getString("coupon_name");

                DataBox data = iceInterfaceAPIService.sendCoupons(corp_code,vip_id,coupon_code,coupon_name,app_id,open_id,desc,"",batch_no,task_code+"#"+coupon_code+i+vip_id);
                logger.info("--------sendcoupon--------"+data.status);
            }
        }
        String present_point = vipTask.getPresent_point();
        if (present_point != null && !present_point.equals("")){
            DataBox data = iceInterfaceAPIService.sendPoints(corp_code,vip_id,present_point,task_code+"#"+vip_id);

            DataBox dataBox =  iceInterfaceService.getVipByOpenId(corp_code,open_id,"");
            JSONArray vip_array = JSONArray.parseArray(dataBox.data.get("message").value);
            JSONObject vip_info = vip_array.getJSONObject(0);

            String result = "{\"code\":\"0\",\"data\":\"\",\"message\":\"积分赠送成功\"}";
            String state = "Y";
            if (!data.status.toString().equals("SUCCESS")){
                result = "{\"code\":\"-1\",\"data\":\"\",\"message\":\"积分赠送失败\"}";
                state = "N";
            }
            VipPointsAdjust vipPointsAdjust = vipPointsAdjustService.selectPointsAdjustByBillCode(task_code);
            if (vipPointsAdjust != null){
                JSONObject adjust_obj= WebUtils.bean2JSONObject(vipPointsAdjust);
                BasicDBObject dbObject = new BasicDBObject();
                dbObject.put("vip",vip_info);
                dbObject.put("adJustInfo",adjust_obj);//单据信息
                dbObject.put("corp_code",corp_code);
                dbObject.put("sendPoints", present_point);
                dbObject.put("modified_date",Common.DATETIME_FORMAT.format(new Date())); //调整时间
                dbObject.put("_id",task_code+vip_id);
                dbObject.put("state",state);//未提交
                dbObject.put("vip_bill_code",task_code+vip_id);//线下单据
                dbObject.put("state_info",result);
                cursor2.save(dbObject);
            }

            logger.info("--------sendcoupon--------"+data.status);
        }
    }

    @Override
    public List<VipTask> selectAllByStatus(String corp_code) throws Exception {
        List<VipTask> list=vipTaskMapper.selectAllByStatus(corp_code);
        return list;
    }

    @Override
    public List<VipTask> selectMobileShow(String corp_code,String app_id,String status,String is_advance_show) throws Exception {
        List<VipTask> list=vipTaskMapper.selectMobileShow(corp_code,app_id,status,is_advance_show);
        return list;
    }

    public List<VipTask> selectVipTaskByTaskType(String corp_code,String task_type) throws Exception{

        List<VipTask> vipTaskList=vipTaskMapper.selectVipTaskByTaskType(corp_code,task_type);
        return  vipTaskList;
    }

    public VipTask selectVipTaskByTaskTypeAndTitle(String corp_code,String task_type,String activity_code) throws Exception{
        VipTask vipTask=vipTaskMapper.selectVipTaskByTaskTypeAndTitle(corp_code,task_type,activity_code);
        return  vipTask;
    }

    public  Object swicthStatus(String examine_type, Object o) throws Exception{
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_function_check_schedule);
        if(examine_type.equals("vipTask")){
            PageInfo<VipTask> vipTaskPageInfo= (PageInfo<VipTask>) o;
            for(int i=0;i<vipTaskPageInfo.getList().size();i++) {
                VipTask vipTask = vipTaskPageInfo.getList().get(i);
                String task_status=vipTask.getTask_status();
                String bill_status = vipTask.getBill_status();
                if (task_status.equals("0")) {
                    if (bill_status.equals("0") || bill_status.equals("")) {
                        vipTask.setTask_status("待提交");
                    } else  if(bill_status.equals("1")){
                        vipTask.setTask_status("审核中");
                    } else if(bill_status.equals("2")){
                        vipTask.setTask_status("待执行");
                    }
                } else if (task_status.equals("0.5")) {
                    vipTask.setTask_status("执行未生效");
                } else if (task_status.equals("1")) {
                    vipTask.setTask_status("执行已生效");
                } else if (task_status.equals("2")) {
                    vipTask.setTask_status("已结束");
                }
            }
        }else if(examine_type.equals("vipActivity")){
            PageInfo<VipActivity> vipActivityPageInfo= (PageInfo<VipActivity>) o;
            for(int i=0;i<vipActivityPageInfo.getList().size();i++) {
                VipActivity vipActivity = vipActivityPageInfo.getList().get(i);
                String bill_status = vipActivity.getBill_status();
                String activity_status = vipActivity.getActivity_state();
                if (activity_status.equals("0")) {
                    if (bill_status.equals("0") || bill_status.equals("")) {
                        vipActivity.setActivity_state("待提交");
                    } else  if(bill_status.equals("1")){
                        vipActivity.setActivity_state("审核中");
                    } else if(bill_status.equals("2")){
                        vipActivity.setActivity_state("待执行");
                    }
                } else if (activity_status.equals("0.5")) {
                    vipActivity.setActivity_state("执行未生效");
                } else if (activity_status.equals("1")) {
                    vipActivity.setActivity_state("执行已生效");
                } else if (activity_status.equals("2")) {
                    vipActivity.setActivity_state("已结束");
                }
            }
        }
        return  o;
    }
}
