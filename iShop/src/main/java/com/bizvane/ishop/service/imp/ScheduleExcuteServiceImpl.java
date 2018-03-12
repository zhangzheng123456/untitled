package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.VipActivityMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhouy on 2017/05/10.
 */
@Service("ScheduleExcuteService")
public class ScheduleExcuteServiceImpl {

    @Autowired
    private VipActivityMapper vipActivityMapper;
    @Autowired
    private VipActivityService vipActivityService;
    @Autowired
    private VipFsendService vipFsendService;
    @Autowired
    private IceInterfaceService iceInterfaceService;
    @Autowired
    private ScheduleJobService scheduleJobService;
    @Autowired
    private StoreService storeService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    VipActivityDetailService vipActivityDetailService;
    @Autowired
    VipCardTypeService vipCardTypeService;
    @Autowired
    VipRulesService vipRulesService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    VipIntegralService vipIntegralService;
    @Autowired
    VipTaskService vipTaskService;
    @Autowired
    WxTemplateService wxTemplateService;
    @Autowired
    VipPointsAdjustService vipPointsAdjustService;

    private static final Logger logger = Logger.getLogger(ScheduleExcuteServiceImpl.class);

    //===============================定时任务==================================

    /**
     * 定时任务
     * 修改活动状态为已结束
     * 停止活动schedule
     * 停止发券schedule
     *
     * @param activity_code
     */
    public void updateStatusSchedule(String activity_code,String job_name,String job_group) {
        try {
            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
            if (vipActivity != null ) {
                vipActivity.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                vipActivity.setActivity_state(Common.ACTIVITY_STATUS_2);
                vipActivityService.updateVipActivity(vipActivity);
                vipRulesService.deleteActivity(activity_code);

                VipActivityDetailConsume consume = new VipActivityDetailConsume();
                consume.setActivity_code(vipActivity.getActivity_code());
                consume.setIsactive(Common.IS_ACTIVE_N);
                vipActivityDetailService.updateDetailConsume(consume);

                VipActivityDetailAnniversary anniversary1 = new VipActivityDetailAnniversary();
                anniversary1.setActivity_code(vipActivity.getActivity_code());
                anniversary1.setIsactive(Common.IS_ACTIVE_N);
                vipActivityDetailService.updateDetailAnniversary(anniversary1);

                List<VipActivityDetailAnniversary> anniversaries = vipActivityDetailService.selActivityDetailAnniversary(activity_code);
                for (int i = 0; i < anniversaries.size(); i++) {
                    VipActivityDetailAnniversary anniversary = anniversaries.get(i);

                    String last_month = Common.DATETIME_FORMAT.format(TimeUtils.getLastDate(new Date(),-1));
                    String time_type = "D";
                    if (TimeUtils.compareDateTime(last_month,anniversary.getCreated_date(),Common.DATETIME_FORMAT))
                        time_type = "M";
                    logger.info("----anniverActiRetroative===-corp_code"+anniversary.getCorp_code()+"=====Activity_code"+anniversary.getActivity_code()+"=========id"+anniversary.getId()+"=======Target_vips"+vipActivity.getTarget_vips()
                            +"====time_type"+time_type+"=====Send_points"+anniversary.getSend_points()+"=====Coupon_type"+anniversary.getCoupon_type());

                    //已结束时补发纪念日
                    iceInterfaceAPIService.VipActivityCop(anniversary.getCorp_code(), anniversary.getActivity_code(), String.valueOf(anniversary.getId()), vipActivity.getTarget_vips(), time_type, anniversary.getSend_points(), anniversary.getCoupon_type(), "");
                }
                scheduleJobService.updateSchedule(job_name,job_group);
                System.out.println("集群列子1  updateStatusSchedule："+ activity_code + " 在 " + Common.DATETIME_FORMAT.format(new Date())+" 时运行");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
        //关闭对应的定时任务
        scheduleJobService.updateSchedule("changeStatus"+activity_code, activity_code);
        scheduleJobService.updateSchedule("sendCoupon"+activity_code, activity_code);
    }

    /**
     * 定时任务
     * 群发消息(活动)
     *
     * @param corp_code
     * @param user_code
     */
    public void fsendActivitySchedule(String corp_code, String user_code,String job_name, String job_group) {
        try {
            VipFsend vipFsend = vipFsendService.getVipFsendInfoByCode(corp_code, job_group);
            if (vipFsend != null && vipFsend.getIsactive().equals("Y")) {
                if (vipFsend.getActivity_vip_code() != null){
                    String code = vipFsend.getActivity_vip_code();
                    if (code.contains("INTEGRAL")){
                        ScheduleJob scheduleJob = scheduleJobService.selectScheduleByJob(job_name, job_group);
                        VipIntegral vipIntegral = vipIntegralService.selectIntegralByBillno(code.replace("INTEGRAL",""));
                        if (vipIntegral != null && vipIntegral.getIsactive().equals("Y")){
                            String clean_cycle = vipIntegral.getClear_cycle();
                            String corn = scheduleJob.getCron_expression();
                            String m = clean_cycle.split(" ")[4];
                            String d = clean_cycle.split(" ")[3];
                            String send_type = vipFsend.getSend_type();

                            String now = Common.DATETIME_FORMAT_DAY.format(new Date());
                            int now_day = Integer.parseInt(now.split("-")[2]);

                            if (m.equals("*")){
                                if (now_day > Integer.parseInt(d)){
                                    m = Common.DATETIME_FORMAT_DAY.format(TimeUtils.getLastDate(new Date(), 1)).split("-")[1];
                                }else {
                                    m = now.split("-")[1];
                                }
                            }
                            if (m.length() == 1){
                                m = "0"+m;
                            }
                            if (d.length() == 1){
                                d = "0"+d;
                            }
                            if (send_type.equals("sms")){
                                iceInterfaceAPIService.vipPointsClean(corp_code,vipIntegral.getTarget_vips_() , vipIntegral.getIntegral_duration(),vipIntegral.getBill_no(),"smsNotice",vipFsend.getContent(),"",m+"月"+d+"日",now.split("-")[0]+m+d);
                            }else {
                                String app_id = vipFsend.getApp_id();
                                List<WxTemplate> wxTemplates = wxTemplateService.selectTempByAppId(app_id,"",Common.TEMPLATE_NAME_4);
                                if (wxTemplates.size() > 0) {
                                    String template_id = wxTemplates.get(0).getTemplate_id();
                                    iceInterfaceAPIService.vipPointsClean(corp_code, vipIntegral.getTarget_vips_(), vipIntegral.getIntegral_duration(),vipIntegral.getBill_no(),"wxNotice",template_id,app_id,m+"月"+d+"日",now.split("-")[0]+m+d);
                                }
                            }
                            if (!corn.endsWith("?")){
                                scheduleJobService.updateSchedule(job_name, job_group);
                            }
                        }

                    }else if (code.contains("VIPTASK")){
                        VipTask vipTask = vipTaskService.selectByTaskCode(code.replace("VIPTASK",""));
                        if (vipTask != null && vipTask.getIsactive().equals("Y")){
                            scheduleJobService.updateSchedule(job_name, job_group);
                            String app_id = vipTask.getApp_id();
                            JSONObject notice = wxTemplateService.newVipTaskNotice(app_id,vipTask.getTask_title());
                            if (notice != null){
                                String template_id = notice.getString("template_id");
                                JSONObject template_content = notice.getJSONObject("template_content");
                                String template_url = notice.getString("template_url");

                                logger.info(corp_code+"========"+vipTask.getTarget_vips_());
                                DataBox dataBox = iceInterfaceService.vipScreenMethod2("1", "100000", corp_code,vipTask.getTarget_vips_(),"","");

//                                DataBox dataBox = vipGroupService.vipScreenBySolr(JSONArray.parseArray(vipTask.getTarget_vips()),corp_code,"1","10000","","","","","","","");
                                String result = dataBox.data.get("message").value;
                                JSONObject result_obj = JSONObject.parseObject(result);
                                JSONArray vip_array = result_obj.getJSONArray("all_vip_list");
                                for (int i = 0; i < vip_array.size(); i++) {
                                    JSONObject template_content1 = new JSONObject();
                                    String first = template_content.getString("first");
                                    String remark = template_content.getString("remark");

                                    template_content1.put("first",first);
                                    template_content1.put("keyword1",template_content.getString("keyword1"));
                                    template_content1.put("keyword2",template_content.getString("keyword2"));
                                    template_content1.put("remark",remark);

//                                JSONObject template_content1 = JSONObject.parseObject(template_content.toString());
                                    JSONObject vip = vip_array.getJSONObject(i);
                                    String open_id = vip.getString("open_id");
                                    String vip_id = vip.getString("vip_id");
                                    String message_id = corp_code + vip_id + System.currentTimeMillis();
                                    if (!open_id.isEmpty()){
                                        first = first.replace("\"#name#\"",vip.getString("vip_name"));
                                        first = first.replace("\"#birthday#\"",vip.getString("vip_birthday"));
                                        first = first.replace("\"#join_time#\"",vip.getString("join_date"));
                                        first = first.replace("\"#sex#\"",vip.getString("sex"));
                                        first = first.replace("\"#store#\"",vip.getString("store_name"));

                                        remark = remark.replace("\"#name#\"",vip.getString("vip_name"));
                                        remark = remark.replace("\"#birthday#\"",vip.getString("vip_birthday"));
                                        remark = remark.replace("\"#join_time#\"",vip.getString("join_date"));
                                        remark = remark.replace("\"#sex#\"",vip.getString("sex"));
                                        remark = remark.replace("\"#store#\"",vip.getString("store_name"));

                                        template_content1.put("first",first);
                                        template_content1.put("remark",remark);
                                        String result2 = wxTemplateService.sendTemplateMsg(app_id,open_id,template_id,template_content1,template_url);

                                        JSONObject info = JSONObject.parseObject(result2);
                                        String errcode = info.getString("errcode");
                                        String errmsg = info.getString("errmsg");
                                        if ("0".equals(errcode)) {
                                            vipFsendService.insertMongoDB(corp_code, user_code, template_id, open_id, vip_id, vip.getString("vip_name"), vip.getString("cardno"), vip.getString("vip_phone"), job_name, app_id, template_content1.toString(), message_id, "Y",errmsg);
                                        }else {
                                            vipFsendService.insertMongoDB(corp_code, user_code, template_id, open_id, vip_id, vip.getString("vip_name"), vip.getString("cardno"), vip.getString("vip_phone"), job_name, app_id, template_content1.toString(), message_id, "N",errmsg);
                                        }
                                    }else {
                                        vipFsendService.insertMongoDB(corp_code, user_code, template_id, "", vip_id, vip.getString("vip_name"), vip.getString("cardno"), vip.getString("vip_phone"), job_name, app_id, template_content1.toString(), message_id, "N","");
                                    }
                                }
                            }
                        }
                    }else {
                        scheduleJobService.updateSchedule(job_name, job_group);
                        vipFsendService.sendSmsActivity(vipFsend, user_code, code);
                    }
                }else {
                    scheduleJobService.updateSchedule(job_name, job_group);
                    vipFsendService.sendMessage(vipFsend, user_code);
                }
                System.out.println("集群列子1  fsendActivitySchedule："+ job_group+"+"+job_name + " 在 " + Common.DATETIME_FORMAT.format(new Date())+" 时运行");
            }else {
                scheduleJobService.delete(job_name, job_group);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 定时任务
     * 批量发券
     *
     * @param corp_code
     * @param activity_code
     * @param
     */
    public void sendCouponSchedule(String corp_code, String activity_code, String user_code) {
        try {
            VipActivity vipActivity = vipActivityMapper.selActivityByCode(activity_code);
            VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
            if (vipActivity != null && vipActivity.getIsactive().equals("Y") && vipActivity.getActivity_state().equals(Common.ACTIVITY_STATUS_1)) {
                String app_id = vipActivity.getApp_id();
                JSONArray target_vip = JSONArray.parseArray(vipActivity.getTarget_vips());
                //target_vip = vipGroupService.vipScreen2Array(target_vip, "", "", "", "", "", "");
                String coupon_type = detail.getCoupon_type();
                scheduleJobService.updateSchedule("sendCoupon"+activity_code,activity_code);
//                if (coupon_type != null && !coupon_type.equals("") && !coupon_type.equals("[]")){
                iceInterfaceAPIService.batchSendCoupons(corp_code, app_id, activity_code, JSON.toJSONString(target_vip), coupon_type,detail.getPresent_point());

                System.out.println("集群列子1  sendCouponSchedule："+ activity_code + " 在 " + Common.DATETIME_FORMAT.format(new Date())+" 时运行");
            } else {
                scheduleJobService.deleteScheduleByGroup(activity_code);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
        System.out.println("it's test1 " + Common.DATETIME_FORMAT.format(new Date()));
    }

    /**
     * 会员活动
     * @param code
     */
    public void vipActivitySchedule(String code,String user_code, String job_name,String job_group) {
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_apply);
        DBCollection cursor2 = mongoTemplate.getCollection(CommonValue.table_vip_points_adjust);
        try {
            VipActivity vipActivity = vipActivityService.getActivityByCode(job_group);
            String activity_code = job_group;
            String app_id = vipActivity.getApp_id();
            if (vipActivity != null ) {

                String corp_code = vipActivity.getCorp_code();
                scheduleJobService.updateSchedule(job_name,job_group);

                if (job_name.contains("applyActivity") ){
                    VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(job_group);

                    String present_coupon = detail.getCoupon_type();
                    String present_point = detail.getPresent_point();

                    BasicDBObject basicDBObject = new BasicDBObject();
                    basicDBObject.put("sign_status","Y");
                    basicDBObject.put("status","1");
                    basicDBObject.put("activity_code",activity_code);
                    DBCursor dbCursor = cursor.find(basicDBObject);
                    while (dbCursor.hasNext()){
                        DBObject dbObject1 = dbCursor.next();
                        JSONObject vip_info = JSONObject.parseObject( dbObject1.get("vip").toString());
                        String open_id = dbObject1.get("open_id").toString();
                        String vip_id = vip_info.getString("vip_id");

                        if (present_coupon != null && !present_coupon.equals("")){
                            JSONArray coupon_array = JSONArray.parseArray(present_coupon);
                            for (int i = 0; i < coupon_array.size(); i++) {
                                JSONObject coupon_obj = coupon_array.getJSONObject(i);
                                String coupon_code = coupon_obj.getString("coupon_code");
                                String coupon_name = coupon_obj.getString("coupon_name");

                                DataBox dataBox1 = iceInterfaceAPIService.sendCoupons(corp_code,vip_id,coupon_code,coupon_name,app_id,open_id,"报名活动赠送","","",activity_code+"#"+coupon_code+i+vip_id);
                            }
                        }
                        if (present_point != null && !present_point.equals("")){
                            logger.info("=================报名活动赠送积分=======");
                            DataBox dataBox1 = iceInterfaceAPIService.sendPoints(corp_code,vip_id,present_point,activity_code+"#"+vip_id);

                            String result = "{\"code\":\"0\",\"data\":\"\",\"message\":\"积分赠送成功\"}";
                            String state = "Y";
                            if (!dataBox1.status.toString().equals("SUCCESS")){
                                logger.info("=================报名活动赠送积分======="+dataBox1.status.toString()+dataBox1.msg);

                                result = "{\"code\":\"-1\",\"data\":\"\",\"message\":\"积分赠送失败\"}";
                                state = "N";
                            }
                            VipPointsAdjust vipPointsAdjust = vipPointsAdjustService.selectPointsAdjustByBillCode(activity_code);
                            if (vipPointsAdjust != null){
                                JSONObject adjust_obj= WebUtils.bean2JSONObject(vipPointsAdjust);
                                BasicDBObject dbObject = new BasicDBObject();
                                dbObject.put("vip",vip_info);
                                dbObject.put("adJustInfo",adjust_obj);//单据信息
                                dbObject.put("corp_code",corp_code);
                                dbObject.put("sendPoints", present_point);
                                dbObject.put("modified_date",Common.DATETIME_FORMAT.format(new Date())); //调整时间
                                dbObject.put("state",state);//未提交
                                dbObject.put("vip_bill_code",activity_code+vip_id);//线下单据
                                dbObject.put("state_info",result);
                                cursor2.save(dbObject);
                            }
                        }
                    }
                }

                if (!vipActivity.getActivity_state().equals(Common.ACTIVITY_STATUS_2)){
                    if (job_name.contains("allocTask")){
                        String task_code = vipActivity.getTask_code();
                        vipActivityService.allocTask(vipActivity.getCorp_code(),job_group,code, user_code, task_code);
                    }
                    if (job_name.contains("sendCoupon") && vipActivity.getActivity_state().equals(Common.ACTIVITY_STATUS_1)){
                        VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(job_group);

                        JSONArray target_vip = JSONArray.parseArray(vipActivity.getTarget_vips());
                        // target_vip = vipGroupService.vipScreen2Array(target_vip, "", "", "", "", "", "");
                        String coupon_type = detail.getCoupon_type();
                        iceInterfaceAPIService.batchSendCoupons(vipActivity.getCorp_code(), app_id, job_group, JSON.toJSONString(target_vip), coupon_type,detail.getPresent_point());
                    }

                    if (job_name.contains("StartActivity")){
                        //更新活动状态activity_state
                        vipActivity.setActivity_state("1");
                        vipActivity.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                        //修改创建时间为开始执行时间
                        vipActivity.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
                        vipActivity.setModifier(user_code);
                        vipActivityService.updateVipActivity(vipActivity);

                        if (vipActivity.getRun_mode().equals("recruit")) {
                            //制度表增加活动的招募规则
                            vipActivityService.creatVipRules(job_group, corp_code);
                        }
                        if (vipActivity.getRun_mode().equals("coupon")) {
                            VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(job_group);
                            if (detail.getSend_coupon_type().equals("batch")) {
                                vipActivityService.insertSchedule2(job_group, corp_code, user_code);
                            } else  if (detail.getSend_coupon_type().equals("anniversary")) {
                                VipActivityDetailAnniversary detail1= new VipActivityDetailAnniversary();
                                detail1.setIsactive(Common.IS_ACTIVE_Y);
                                detail1.setActivity_code(job_group);
                                vipActivityDetailService.updateDetailAnniversary(detail1);
                            }else  if (detail.getSend_coupon_type().equals("consume")) {
                                VipActivityDetailConsume detail2= new VipActivityDetailConsume();
                                detail2.setIsactive(Common.IS_ACTIVE_Y);
                                detail2.setActivity_code(job_group);
                                vipActivityDetailService.updateDetailConsume(detail2);
                            }
                        }

                        if(vipActivity.getRun_mode().equals("online_apply")){//线上报名活动
                            VipActivityDetailApply vipActivityDetailApply=new VipActivityDetailApply();
                            vipActivityDetailApply.setIsactive(Common.IS_ACTIVE_Y);
                            vipActivityDetailApply.setActivity_code(vipActivity.getActivity_code());
                            vipActivityDetailService.updateDetailApply(vipActivityDetailApply);

                            VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(vipActivity.getActivity_code());
                            String present_time = detail.getPresent_time();
                            JSONObject present_time_obj = JSONObject.parseObject(present_time);
                            if (present_time_obj.getString("type").equals("timing")){
                                vipActivityService.insertSchedule4(vipActivity.getActivity_code(),corp_code,user_code,present_time_obj.getString("date"));
                            }
                        }
                    }
                    if (job_name.contains("changeStatus")){
                        vipActivity.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                        vipActivity.setActivity_state(Common.ACTIVITY_STATUS_2);
                        vipActivityService.updateVipActivity(vipActivity);
                        vipRulesService.deleteActivity(job_group);

                        VipActivityDetailConsume consume = new VipActivityDetailConsume();
                        consume.setActivity_code(vipActivity.getActivity_code());
                        consume.setIsactive(Common.IS_ACTIVE_N);
                        vipActivityDetailService.updateDetailConsume(consume);

                        VipActivityDetailAnniversary anniversary1 = new VipActivityDetailAnniversary();
                        anniversary1.setActivity_code(vipActivity.getActivity_code());
                        anniversary1.setIsactive(Common.IS_ACTIVE_N);
                        vipActivityDetailService.updateDetailAnniversary(anniversary1);


                        VipActivityDetailApply vipActivityDetailApply=new VipActivityDetailApply();
                        vipActivityDetailApply.setIsactive(Common.IS_ACTIVE_N);
                        vipActivityDetailApply.setActivity_code(vipActivity.getActivity_code());
                        vipActivityDetailService.updateDetailApply(vipActivityDetailApply);


                        List<VipActivityDetailAnniversary> anniversaries = vipActivityDetailService.selActivityDetailAnniversary(job_group);

                        for (int i = 0; i < anniversaries.size(); i++) {
                            VipActivityDetailAnniversary anniversary = anniversaries.get(i);
                            String last_month = Common.DATETIME_FORMAT.format(TimeUtils.getLastDate(new Date(),-1));
                            String time_type = "D";
                            if (TimeUtils.compareDateTime(last_month,anniversary.getCreated_date(),Common.DATETIME_FORMAT))
                                time_type = "M";
                            logger.info("----anniverActiRetroative===-corp_code"+anniversary.getCorp_code()+"=====Activity_code"+anniversary.getActivity_code()+"=========id"+anniversary.getId()+"=======Target_vips"+vipActivity.getTarget_vips()
                                    +"====time_type"+time_type+"=====Send_points"+anniversary.getSend_points()+"=====Coupon_type"+anniversary.getCoupon_type());
                            iceInterfaceAPIService.VipActivityCop(anniversary.getCorp_code(), anniversary.getActivity_code(), String.valueOf(anniversary.getId()), vipActivity.getTarget_vips(), time_type, anniversary.getSend_points(), anniversary.getCoupon_type(), "");
                        }

                        scheduleJobService.updateSchedule("changeStatus"+activity_code, activity_code);
                        scheduleJobService.updateSchedule("StartActivity"+activity_code, activity_code);
                        scheduleJobService.updateSchedule("sendCoupon"+activity_code, activity_code);
                        scheduleJobService.updateSchedule("allocTask"+activity_code, activity_code);
                    }
                }


                System.out.println("集群列子1  vipActivitySchedule："+ job_group+"+"+job_name + " 在 " + Common.DATETIME_FORMAT.format(new Date())+" 时运行");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
        //关闭对应的定时任务
        scheduleJobService.updateSchedule(job_name, job_group);
    }

    /**
     * 定时任务
     * 群发消息（积分）
     *
     * @param corp_code
     * @param sms_code
     * @param user_code
     */
    public void fsendIntegralSchedule(String corp_code, String sms_code, String user_code, String job_group) {
        try {
            ScheduleJob scheduleJob = scheduleJobService.selectScheduleByJob(sms_code, job_group);
            VipFsend vipFsend = vipFsendService.getVipFsendInfoByCode(corp_code, sms_code);
            VipIntegral vipIntegral = vipIntegralService.selectIntegralByBillno(job_group);
            if (vipFsend != null && vipFsend.getIsactive().equals("Y") && vipIntegral != null && vipIntegral.getIsactive().equals("Y")) {
                String clean_cycle = vipIntegral.getClear_cycle();
                String corn = scheduleJob.getCron_expression();
                String m = clean_cycle.split(" ")[4];
                String d = clean_cycle.split(" ")[3];
                String send_type = vipFsend.getSend_type();

                String now = Common.DATETIME_FORMAT_DAY.format(new Date());
                int now_day = Integer.parseInt(now.split("-")[2]);

                if (m.equals("*")){
                    if (now_day > Integer.parseInt(d)){
                        m = Common.DATETIME_FORMAT_DAY.format(TimeUtils.getLastDate(new Date(), 1)).split("-")[1];
                    }else {
                        m = now.split("-")[1];
                    }
                }
                if (d.length() == 1){
                    d = "0"+d;
                }
                if (m.length() == 1){
                    m = "0"+m;
                }
//                JSONArray sms_screen = JSONArray.parseArray(vipFsend.getSms_vips());
//                JSONArray array = vipGroupService.vipScreen2ArrayNew(sms_screen,corp_code,"","","","","");
                if (send_type.equals("sms")){
                    iceInterfaceAPIService.vipPointsClean(corp_code, vipFsend.getSms_vips_(), vipIntegral.getIntegral_duration(),job_group,"smsNotice",vipFsend.getContent(),"",m+"月"+d+"日",now.split("-")[0]+m+d);
                }else {
                    String app_id = vipFsend.getApp_id();
                    List<WxTemplate> wxTemplates = wxTemplateService.selectTempByAppId(app_id,"",Common.TEMPLATE_NAME_4);
                    if (wxTemplates.size() > 0) {
                        String template_id = wxTemplates.get(0).getTemplate_id();
                        iceInterfaceAPIService.vipPointsClean(corp_code,  vipFsend.getSms_vips_(), vipIntegral.getIntegral_duration(),job_group,"wxNotice",template_id,app_id,m+"月"+d+"日",now.split("-")[0]+m+d);
                    }
                }

//                vipFsendService.sendSmsIntegral(vipFsend, Common.ROLE_GM, "", "", "", user_code, job_group);
                if (!corn.endsWith("?")){
                    scheduleJobService.updateSchedule(sms_code, job_group);
                }
                System.out.println("集群列子1  fsendIntegralSchedule："+ job_group+"+"+sms_code + " 在 " + Common.DATETIME_FORMAT.format(new Date())+" 时运行");
            }else {
                scheduleJobService.delete(sms_code, job_group);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
    }

    /**
     * 定时任务
     * 会员积分调整
     *
     * @param corp_code
     * @param bill_no
     * @param user_code
     */
    public void vipIntegralSchedule(String corp_code, String bill_no, String user_code) {
        try {
            ScheduleJob scheduleJob = scheduleJobService.selectScheduleByJob(bill_no, bill_no);
            VipIntegral vipIntegral = vipIntegralService.selectIntegralByBillno(bill_no);
            if (vipIntegral != null && vipIntegral.getIsactive().equals("Y")) {
                String corn = scheduleJob.getCron_expression();
                String clean_cycle = vipIntegral.getClear_cycle();

                String now = Common.DATETIME_FORMAT_DAY.format(new Date());
                int now_day = Integer.parseInt(now.split("-")[2]);
                String m = clean_cycle.split(" ")[4];
                String d = clean_cycle.split(" ")[3];
                if (m.equals("*")){
                    if (now_day > Integer.parseInt(d)){
                        m = Common.DATETIME_FORMAT_DAY.format(TimeUtils.getLastDate(new Date(), 1)).split("-")[1];
                    }else {
                        m = now.split("-")[1];
                    }
                }
                if (d.length() == 1){
                    d = "0"+d;
                }
                if (m.length() == 1){
                    m = "0"+m;
                }
                iceInterfaceAPIService.vipPointsClean(corp_code, vipIntegral.getTarget_vips_(), vipIntegral.getIntegral_duration(),bill_no,"clean","","","",now.split("-")[0]+m+d);
                vipIntegral.setRecent_clean_time(Common.DATETIME_FORMAT.format(new Date()));
                vipIntegralService.updateVipIntegral(vipIntegral);
                if (!corn.endsWith("?")){
                    scheduleJobService.updateSchedule(bill_no, bill_no);
                }
                System.out.println("集群列子1  vipIntegralSchedule："+ bill_no + " 在 " + Common.DATETIME_FORMAT.format(new Date())+" 时运行");

            }else {
                scheduleJobService.deleteScheduleByGroup(bill_no);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
    }

    /**
     * 定时任务
     * 开始/结束 会员任务
     *
     * @param corp_code
     * @param task_code
     * @param job_name
     */
    public void vipTaskSchedule(String corp_code, String task_code, String job_name) {
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        try {
            VipTask vipTask = vipTaskService.selectByTaskCode(task_code);
            if (vipTask != null && vipTask.getIsactive().equals("Y") && !vipTask.getTask_status().equals(Common.ACTIVITY_STATUS_2)) {
                if (job_name.contains("StartVipTask")) {
                    vipTask.setTask_status(Common.ACTIVITY_STATUS_1);
                    vipTaskService.update(vipTask);
                } else if (job_name.contains("EndVipTask")) {
                    vipTask.setTask_status(Common.ACTIVITY_STATUS_2);

                    BasicDBObject queryCondition = new BasicDBObject();
                    queryCondition.put("task_code",task_code);
                    DBCursor dbCursor = cursor.find(queryCondition);

                    while (dbCursor.hasNext()) {
                        //记录存在，更新
                        DBObject dbObject = dbCursor.next();
                        String task_code1 = dbObject.get("task_code").toString();
                        DBObject updateCondition = new BasicDBObject();
                        updateCondition.put("task_code", task_code1);

                        DBObject updatedValue = new BasicDBObject();
                        updatedValue.put("task.task_status", "2");

                        DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                        cursor.update(updateCondition, updateSetValue,false,true);
                    }
                    vipTaskService.update(vipTask);
                }else if (job_name.contains("Notice")){
                    String user_code = vipTask.getCreater();
                    String app_id = vipTask.getApp_id();
                    JSONObject notice = wxTemplateService.newVipTaskNotice(app_id,vipTask.getTask_title());
                    List<VipFsend> vipFsends = vipFsendService.getSendByActivityCode(corp_code,"VIPTASK"+task_code);

                    if (notice != null && vipFsends.size() > 0){
                        String template_id = notice.getString("template_id");
                        JSONObject template_content = notice.getJSONObject("template_content");
                        String template_url = notice.getString("template_url");

                        String sms_code = vipFsends.get(0).getSms_code();
                        DataBox dataBox = iceInterfaceService.vipScreenMethod2("1", "100000", corp_code,JSON.toJSONString(vipFsends.get(0).getSms_vips_()),"","");
//                        DataBox dataBox = vipGroupService.vipScreenBySolr(JSONArray.parseArray(vipTask.getTarget_vips()),corp_code,"1","10000","","","","","","","");

                        String result = dataBox.data.get("message").value;
                        JSONObject result_obj = JSONObject.parseObject(result);
                        JSONArray vip_array = result_obj.getJSONArray("all_vip_list");
                        for (int i = 0; i < vip_array.size(); i++) {
                            JSONObject template_content1 = new JSONObject();
                            String first = template_content.getString("first");
                            String remark = template_content.getString("remark");

                            template_content1.put("first",first);
                            template_content1.put("keyword1",template_content.getString("keyword1"));
                            template_content1.put("keyword2",template_content.getString("keyword2"));
                            template_content1.put("remark",remark);

//                                JSONObject template_content1 = JSONObject.parseObject(template_content.toString());
                            JSONObject vip = vip_array.getJSONObject(i);
                            String open_id = vip.getString("open_id");
                            String vip_id = vip.getString("vip_id");
                            String message_id = corp_code + vip_id + System.currentTimeMillis();
                            if (!open_id.isEmpty()){
                                first = first.replace("\"#name#\"",vip.getString("vip_name"));
                                first = first.replace("\"#birthday#\"",vip.getString("vip_birthday"));
                                first = first.replace("\"#join_time#\"",vip.getString("join_date"));
                                first = first.replace("\"#sex#\"",vip.getString("sex"));
                                first = first.replace("\"#store#\"",vip.getString("store_name"));

                                remark = remark.replace("\"#name#\"",vip.getString("vip_name"));
                                remark = remark.replace("\"#birthday#\"",vip.getString("vip_birthday"));
                                remark = remark.replace("\"#join_time#\"",vip.getString("join_date"));
                                remark = remark.replace("\"#sex#\"",vip.getString("sex"));
                                remark = remark.replace("\"#store#\"",vip.getString("store_name"));

                                template_content1.put("first",first);
                                template_content1.put("remark",remark);
                                String result2 = wxTemplateService.sendTemplateMsg(app_id,open_id,template_id,template_content1,template_url);

                                JSONObject info = JSONObject.parseObject(result2);
                                String errcode = info.getString("errcode");
                                String errmsg = info.getString("errmsg");
                                if ("0".equals(errcode)) {
                                    vipFsendService.insertMongoDB(corp_code, user_code, template_id, open_id, vip_id, vip.getString("vip_name"), vip.getString("cardno"), vip.getString("vip_phone"), sms_code, app_id, template_content1.toString(), message_id, "Y",errmsg);
                                }else {
                                    vipFsendService.insertMongoDB(corp_code, user_code, template_id, open_id, vip_id, vip.getString("vip_name"), vip.getString("cardno"), vip.getString("vip_phone"), sms_code, app_id, template_content1.toString(), message_id, "N",errmsg);
                                }
                                System.out.println("=================template_content1."+template_content1.toString());
                                System.out.println("=================template_content"+template_content.toString());
                            }else {
                                vipFsendService.insertMongoDB(corp_code, user_code, template_id, "", vip_id, vip.getString("vip_name"), vip.getString("cardno"), vip.getString("vip_phone"), sms_code, app_id, template_content1.toString(), message_id, "N","");
                            }
                        }
                    }
                }
                System.out.println("集群列子1  vipTaskSchedule："+ task_code+"+"+job_name + " 在 " + Common.DATETIME_FORMAT.format(new Date())+" 时运行");
            }
            scheduleJobService.updateSchedule(job_name, task_code);
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
    }

    /**
     * 定时任务
     * 每天给生日会员发通知
     *
     * @param corp_code
     */
    public void VipBirthNotice(String corp_code,String job_group,String job_name) {
        try {
            ScheduleJob job = scheduleJobService.selectScheduleByJob(job_name,job_group);
            if (job != null ){
                if (job.getGmt_modify() != null && job.getGmt_modify().equals("")){
                    String gmt_modify = job.getGmt_modify();
                    String today = Common.DATETIME_FORMAT_DAY.format(new Date());
                    if (gmt_modify.contains(today)){
                        logger.info("=========发过生日提醒啦");
                        return;
                    }
                }
                JSONArray screen = new JSONArray();
                //生日为当天
                JSONObject post_obj = new JSONObject();
                post_obj.put("key",Common.VIP_SCREEN_BIRTH_KEY);
                post_obj.put("type","json");
                JSONObject date = new JSONObject();
                date.put("start",Common.DATETIME_FORMAT_DAY.format(new Date()));
                date.put("end",Common.DATETIME_FORMAT_DAY.format(new Date()));
                post_obj.put("value",date);
                //open_id不为空
                JSONObject post_obj1 = new JSONObject();
                post_obj1.put("key",Common.VIP_SCREEN_OPENID_KEY);
                post_obj1.put("type","text");
                post_obj1.put("value","Y");

                screen.add(post_obj);
                screen.add(post_obj1);

                DataBox dataBox = iceInterfaceService.vipScreenMethod2("1","100000", corp_code,JSON.toJSONString(screen),"","");

                logger.info("------VipBirthNotice-vip列表" + dataBox.status.toString());
                if ( dataBox.status.toString().equals("SUCCESS")){
                    String result = dataBox.data.get("message").value;
                    JSONObject result_obj = JSONObject.parseObject(result);
                    JSONArray vip_array = result_obj.getJSONArray("all_vip_list");
                    logger.info("------VipBirthNotice-vip列表" + vip_array.size());

//                    JSONObject msg_content = new JSONObject();
//                    msg_content.put("first","亲爱的ECCO会员，我们为您准备了专属礼遇，请点击该模板消息查看");
//                    msg_content.put("keyword1","ECCO祝您生日快乐");
//                    msg_content.put("keyword2","点击领取您的生日八折券和生日礼物");
//                    msg_content.put("remark","常规会员折扣（仅限正价商品）：普卡9折，银卡8.8折，金卡8.5折\n" +
//                            "会员消费1元累计1积分，50积分可抵扣1元，直接用于消费。");

                    String template_id = "";
                    List<WxTemplate> wxTempls = wxTemplateService.selectAllWxTemplate(corp_code,Common.TEMPLATE_NAME_1);
                    logger.info("------VipBirthNotice-wxTempls" + wxTempls.size());
                    if (wxTempls.size() == 0){
                        return;
                    }

                    job.setJob_group(job_group);
                    job.setJob_name(job_name);
                    job.setGmt_modify(Common.DATETIME_FORMAT.format(new Date()));
                    scheduleJobService.update(job);

                    String template_url = CommonValue.wei_birthPresent_url;
                    for (int i = 0; i < vip_array.size(); i++) {
                        JSONObject vip = vip_array.getJSONObject(i);
                        String open_id = vip.getString("open_id");
                        String app_id = vip.getString("app_id");
                        if (!open_id.isEmpty()){
                            if (app_id == null || app_id.equals("") || app_id.equals("null")){
                                template_id = wxTempls.get(0).getTemplate_id();
                                app_id = wxTempls.get(0).getApp_id();
                            }else {
                                for (int j = 0; j < wxTempls.size(); j++) {
                                    if (wxTempls.get(j).getApp_id().equals(app_id))
                                        template_id = wxTempls.get(j).getTemplate_id();
                                }
                            }
                            JSONObject template_content = wxTemplateService.vipBirthNotice(app_id,vip);
                            JSONObject msg_content = template_content.getJSONObject("template_content");
                            template_url = template_url.replace("@appid@",app_id);
                            String result1 = wxTemplateService.sendTemplateMsg(app_id,open_id,template_id,msg_content,template_url);
                            logger.info("------VipBirthNotice-app_id:"+app_id+"--open_id:" + open_id+"--template_id:" + template_id+"--result1:" + result1);
                        }
                    }
                }
                System.out.println("集群列子1  VipBirthNotice："+ job_group+"+"+job_name + " 在 " + Common.DATETIME_FORMAT.format(new Date())+" 时运行");

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
    }

    /**
     * 定时任务
     * 计算店铺使用时间
     *勿删，以后有用
     * @param job_group
     * @param job_name
     */
    @Transactional
    public void changeStoreCount(String job_name, String job_group) {
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_store_start_end_log);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        try {
            List<Store> allStoreByCount = storeService.getAllStoreByCount();
            if (allStoreByCount.size() > 0) {
                for (Store store : allStoreByCount) {
                    String isopen = store.getIsopen();
                    String open_date = store.getOpen_date();
                    String close_date = store.getClose_date();
                    String store_code = store.getStore_code();
                    String corp_code = store.getCorp_code();
                    String open_date_count = "0";
                    if (null == open_date || "".equals(open_date)) {
                        open_date = Common.DATETIME_FORMAT.format(now);
                    }
                    if ((null == close_date || "".equals(close_date)) && isopen.equals("Y")) {
                        String start_time = open_date;
                        String end_time = Common.DATETIME_FORMAT.format(now);

                        Date date_start = format.parse(start_time);
                        Date date_end = format.parse(end_time);
                        open_date_count = String.valueOf(TimeUtils.getDiscrepantDays(date_start, date_end));

                    } else if ((null != close_date || !close_date.equals("")) && isopen.equals("Y")) {
                        String start_time = close_date;
                        String end_time = Common.DATETIME_FORMAT.format(now);
                        Date date_start = format.parse(start_time);
                        Date date_end = format.parse(end_time);
                        int date_count = TimeUtils.getDiscrepantDays(date_start, date_end);
                        String open_date_count_sql = store.getOpen_date_count();
                        int count1 = date_count + Integer.parseInt(open_date_count_sql);
                        open_date_count = String.valueOf(count1);
                    } else if ((null != close_date || !close_date.equals("")) && isopen.equals("N")) {
                        open_date_count = store.getOpen_date_count();
                    }
                    Store store_new = new Store();
                    store_new.setId(store.getId());
                    store_new.setOpen_date_count(String.valueOf(open_date_count));
                    storeService.updateStore(store_new);

                    BasicDBObject queryCondition = new BasicDBObject();
                    BasicDBList values = new BasicDBList();
                    values.add(new BasicDBObject("corp_code", corp_code));
                    values.add(new BasicDBObject("store_code", store_code));
                    values.add(new BasicDBObject("is_open", "Y"));
                    queryCondition.put("$and", values);
                    DBCursor dbCursor = collection.find(queryCondition);

                    String start_time = "";
                    String end_time = Common.DATETIME_FORMAT.format(now);
                    while (dbCursor.hasNext()) {
                        DBObject obj = dbCursor.next();
                        start_time = obj.get("start_time").toString();
                    }
                    if (start_time.equals("")) {
                        start_time = open_date;
                    }
                    Date date_start = format.parse(start_time);
                    Date date_end = format.parse(end_time);
                    int time_count = TimeUtils.getDiscrepantDays(date_start, date_end);

                    DBObject updatedValue = new BasicDBObject();
                    updatedValue.put("time_count", time_count);
                    DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                    collection.update(queryCondition, updateSetValue);
                }
            }
            scheduleJobService.updateSchedule(job_name, job_group);
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
    }

    /**
     * 定时任务
     * 计算店铺使用时间
     *
     * @param job_group
     * @param job_name
     */
    @Transactional
    public void changeStore(String job_name, String job_group) {
        try {
            System.out.println("------计算店铺使用时间（定时任务）-----------");
            storeService.updStoreDayCount();

//            scheduleJobService.updateSchedule(job_name, job_group);

        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
    }


    //根据企业编号，vip_id,获取优惠券活动（纪念日类型）（执行中）下的优惠券和积分
    public void anniverActiRetroative(String job_name,String job_group){
        //获取该企业下所有执行中的纪念日活动
        try {
            ScheduleJob job = scheduleJobService.selectScheduleByJob(job_name,job_group);
            if (job != null ) {
               String func = job.getFunc();
               JSONObject object = JSONObject.parseObject(func);
               String code = object.getString("code");
               iceInterfaceAPIService.activityAnniversaryAct("",code);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateSchedule(ScheduleJob scheduleJob) {
        try {
//            ScheduleJob scheduleJob = scheduleJobService.selectScheduleByJob(job_name,job_group);
//            if (scheduleJob != null){
                scheduleJob.setGmt_modify(Common.DATETIME_FORMAT.format(new Date()));
                scheduleJobService.update(scheduleJob);
//            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public ScheduleJob checkSchedule(String job_name,String job_group){
        try{
            ScheduleJob scheduleJob = scheduleJobService.selectScheduleByJob(job_name,job_group);
            if (scheduleJob != null){
                return scheduleJob;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //判断今天是否执行过该任务，若已经执行过则返回false
    public boolean today_execute(String job_name,String job_group,ScheduleJob scheduleJob) {
        try{
            String modified = scheduleJob.getGmt_modify();
            String corn = scheduleJob.getCron_expression();
            System.out.println("today_execute "+ job_group+"+"+job_name +"计划时间"+corn+ " 在 " + Common.DATETIME_FORMAT.format(new Date())+" 时运行");
            if (modified.contains(Common.DATETIME_FORMAT_DAY.format(new Date())))
                return false;
            if (scheduleJob.getStatus().equals("Y"))
                return false;
            if (corn.endsWith("?")){
                String now = Common.DATETIME_FORMAT.format(new Date());
                String now_month = now.split("-")[1];
                String now_day = now.split("-")[2].split(" ")[0];

                String m = corn.split(" ")[4];
                String d = corn.split(" ")[3];

                if (!m.equals("*") && !m.equals(now_month))
                    return false;
                if (!d.equals("*") && !d.equals(now_day))
                    return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}

