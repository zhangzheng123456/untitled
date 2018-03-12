package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.VipActivity;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.*;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by yanyadong on 2017/3/8.
 */
@Controller
@RequestMapping("/activityAnaly")
public class ActivityAnalyController {

    @Autowired
    ActivityAnalyService activityAnalyService;
    @Autowired
    MongoDBClient mongoDBClient;
    @Autowired
    VipActivityService vipActivityService;
    @Autowired
    StoreService storeService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    VipGroupService vipGroupService;

    private static final Logger logger = Logger.getLogger(ActivityAnalyController.class);

    //--------------------------------任务完成度分析---------------------------------------------------

    /**
     * 活动(执行中)
     * 任务完成分析
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/executeDetail", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Transactional
    public String executeDetail(HttpServletRequest request) throws Exception {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String activity_code = jsonObject.getString("activity_code");
            String task_code = jsonObject.getString("task_code");

            JSONObject result_obj = activityAnalyService.executeDetail(corp_code,activity_code,task_code);
            JSONArray array = activityAnalyService.taskCompleteDetail(corp_code,activity_code,task_code);

            result_obj.put("taskComplete", array);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result_obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 获取导购执行详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/userExecuteDetail", method = RequestMethod.POST)
    @ResponseBody
    public String userExecuteDetail(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String user_code = jsonObject.getString("user_code");
            String task_code = jsonObject.getString("task_code");
            JSONArray list = activityAnalyService.userExecuteDetail(corp_code,task_code,user_code);
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



    //..................................导购影响力分析...................................................
    //导购分析占比
    @RequestMapping(value = "/userCount",method = RequestMethod.POST)
    @ResponseBody
    public String getShareUser(HttpServletRequest request){
        String id="";
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject result= activityAnalyService.getShareSize(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch(Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }
   //导购分析图表
    @RequestMapping(value = "/view",method = RequestMethod.POST)
    @ResponseBody
    public String getClickSizeView(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            //链接点击的折线图
            JSONObject clickResult= activityAnalyService.getClickViewByDate(message);
            //链接分享的折线图
            JSONObject shareResult= activityAnalyService.getShareViewByDate(message);

            JSONObject result=new JSONObject();
            result.put("shareCount",shareResult.get("count").toString());
            result.put("openCount",clickResult.get("count").toString());
            result.put("shareList",shareResult.get("list"));
            result.put("clickList",clickResult.get("list"));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }

   //导购分析列表
    @RequestMapping(value = "/userList",method = RequestMethod.POST)
    @ResponseBody
    public String getUserList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject result= activityAnalyService.getUserList(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //..................................意向客户分析...................................................
   //意向分析占比
    @RequestMapping(value = "/intentionUser",method = RequestMethod.POST)
    @ResponseBody
    public String getIntentionUser(HttpServletRequest request){
        String id="";
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject result= activityAnalyService.getIntentionSize(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch(Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }
   //意向分析图表
    @RequestMapping(value = "/intentionView",method = RequestMethod.POST)
    @ResponseBody
    public String getClickSizeIntentionView(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            //链接点击的折线图
            JSONArray clickResult= activityAnalyService.getClickViewByDateForUser(message);
            JSONObject result=new JSONObject();
            result.put("clickList",clickResult);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }
     //意向分析列表（已分页）
    @RequestMapping(value = "/intentionList",method = RequestMethod.POST)
    @ResponseBody
    public String getIntentionList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject result= activityAnalyService.getIntentionList(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //..............................优惠券分析..........................................

    //优惠券占比
    @RequestMapping(value = "/couponUser",method = RequestMethod.POST)
    @ResponseBody
    public String getCouponUser(HttpServletRequest request){
        String id="";
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject result= activityAnalyService.getCouponSize(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch(Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }

//优惠券图表
    @RequestMapping(value = "/couponView",method = RequestMethod.POST)
    @ResponseBody
    public String getCouponView(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject couponResult= activityAnalyService.getCouponView(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(couponResult.toString());

        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    //优惠券列表
    @RequestMapping(value = "/couponList",method = RequestMethod.POST)
    @ResponseBody
    public String getCouponList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject result= activityAnalyService.getCouponList(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //...................报名活动分析.........................................
    /**
     * 报名活动分析占比
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/applyAnalyRate", method = RequestMethod.POST)
    @ResponseBody
    public String applyAnalyRate(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        MongoTemplate mongoTemplate = mongoDBClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_join_log);

        JSONObject result = new JSONObject();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.getString("activity_code");

            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
            String corp_code = vipActivity.getCorp_code();
            //目标会员数
            String target_vips = vipActivity.getTarget_vips();
            int target_vip_count = 0;
            DataBox dataBox = vipGroupService.vipScreenBySolr(JSONArray.parseArray(target_vips), corp_code, "1", "3", Common.ROLE_GM, "", "", "", "","","");
            if (dataBox.status.toString().equals("SUCCESS")) {
                String message1 = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message1);
                target_vip_count = msg_obj.getInteger("count");
            }

            BasicDBObject obj = new BasicDBObject();
            obj.put("activity_code",activity_code);
            int apply_count = cursor.find(obj).count();
            obj.put("vip",new BasicDBObject("$ne",null));
            int apply_vip_count = cursor.find(obj).count();

            result.put("target_vip_count",target_vip_count+"");
            result.put("apply_count",apply_count);
            result.put("apply_vip_count",apply_vip_count);
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
     * 报名活动分析柱状图
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/applyAnalyView", method = RequestMethod.POST)
    @ResponseBody
    public String applyAnalyView(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        MongoTemplate mongoTemplate = mongoDBClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_join_log);

        JSONObject result = new JSONObject();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.getString("activity_code");

            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
            String start_time=vipActivity.getStart_time();
            //获取活动的结束时间
            String end_time=vipActivity.getEnd_time();
            String now = Common.DATETIME_FORMAT.format(new Date());
            Boolean bool = TimeUtils.compareDateTime(end_time , now, Common.DATETIME_FORMAT);
            if (bool == false)
                end_time = now;

            //获取两个时间段之间的所有日期(包含开始时间，结束时间)
            List<String> date= TimeUtils.getBetweenDates(start_time.split(" ")[0],end_time.split(" ")[0]);
            JSONArray array = new JSONArray();
            for (int i = 0; i < date.size(); i++) {
                BasicDBObject basicDBObject = new BasicDBObject();
                BasicDBList basicDBList = new BasicDBList();
                basicDBList.add(new BasicDBObject("activity_code", activity_code));
                Pattern pattern = Pattern.compile( date.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                basicDBList.add(new BasicDBObject("created_date",pattern));
                basicDBObject.put("$and", basicDBList);
                int apply_count_day = cursor.find(basicDBObject).count();

                JSONObject jsonObject1=new JSONObject();
                jsonObject1.put("date",date.get(i));
                jsonObject1.put("count",apply_count_day);
                array.add(jsonObject1);
            }
            result.put("apply_view",array);
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
     * 报名活动分析列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/applyAnalyList", method = RequestMethod.POST)
    @ResponseBody
    public String applyAnalyList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongoDBClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_join_log);

        JSONObject result = new JSONObject();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.getString("activity_code");
            int page_size = 100;
            int page_num = 1;
            if (jsonObject.containsKey("page_size"))
                page_size = jsonObject.getInteger("page_size");
            if (jsonObject.containsKey("page_num"))
                page_num = jsonObject.getInteger("page_num");

            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
            String corp_code = vipActivity.getCorp_code();
            String target_vips = vipActivity.getTarget_vips();

            //筛选条件
            String storeCode_screen=jsonObject.get("store_code").toString().toLowerCase();
            String storeName_screen=jsonObject.get("store_name").toString().toLowerCase();


            JSONArray array = new JSONArray();
            PageInfo<Store> stores = activityAnalyService.getStoreTargrtVips(corp_code,vipActivity.getRun_scope(),storeCode_screen,storeName_screen,page_num,page_size);
            List<Store> storeList = stores.getList();
            for (int i = 0 ; i < storeList.size(); i++) {
                JSONObject store_obj = new JSONObject();
                String store_code = storeList.get(i).getStore_code();
                String store_name = storeList.get(i).getStore_name();

                String store_area_code = storeList.get(i).getArea_code();
                if (store_area_code != null)
                    store_area_code = store_area_code.replace("§,","");
                JSONObject jsonObject1 = activityAnalyService.getStoreTargrtVips1(corp_code,store_code,store_area_code,target_vips);
                String area_name=jsonObject1.get("area_name").toString();
                String area_code=jsonObject1.get("area_code").toString();
                String target_vip_count=jsonObject1.get("target_vip_count").toString();

                store_obj.put("store_code",store_code);
                store_obj.put("store_name",store_name);
                store_obj.put("area_name",area_name);
                store_obj.put("area_code",area_code);
                store_obj.put("target_vip_count",target_vip_count);

                //报名人数
                BasicDBObject obj = new BasicDBObject();
                obj.put("activity_code",activity_code);
                obj.put("store_code",store_code);
                DBCursor dbcoursor = cursor.find(obj).sort(new BasicDBObject("modified_date",-1));
                obj.put("vip",new BasicDBObject("$ne",null));
                DBCursor dbcoursor1 = cursor.find(obj);

                ArrayList list = MongoUtils.dbCursorToList(dbcoursor);
                store_obj.put("apply_count",dbcoursor.count());
                store_obj.put("apply_vip_count",dbcoursor1.count());
                double apply_count = dbcoursor.count();
                double target_count = Double.parseDouble(target_vip_count);
                if (target_count != 0d){
                    store_obj.put("rate",NumberUtil.percent(apply_count/target_count));
                }else {
                    store_obj.put("rate","0%");
                }
                store_obj.put("apply_detail", JSON.toJSONString(list));

                array.add(store_obj);
            }

            result.put("list",JSON.toJSONString(array));
            result.put("total",stores.getTotal());
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("pages",stores.getPages());
            result.put("hasNext",stores.isHasNextPage());
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

    //...................通知传播分析.........................................
    @RequestMapping(value = "/noticeCard",method = RequestMethod.POST)
    @ResponseBody
    public String getNoticeAnalyByCard(HttpServletRequest request){
        String id="";
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject result= activityAnalyService.noticeAnalyByCard(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch(Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/noticeStore",method = RequestMethod.POST)
    @ResponseBody
    public String getNoticeAnalyByStore(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject result=activityAnalyService.noticeAnalyByStore1(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }

//    @RequestMapping(value = "/noticeStoreCard",method = RequestMethod.POST)
//    @ResponseBody
//    public String getNoticeStoreCard(HttpServletRequest request){
//        DataBean dataBean=new DataBean();
//        String id="";
//        try{
//            String jsString=request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject result= activityAnalyService.noticeAnaly_card_store(message);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        }catch (Exception ex){
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    //已分页
    @RequestMapping(value = "/noticeList",method = RequestMethod.POST)
    @ResponseBody
    public String getNoticeList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject result= activityAnalyService.noticeAnalyList1(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //..................................业绩分析...................................................

    /**
     * 业绩分析占比
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/achvRate", method = RequestMethod.POST)
    @ResponseBody
    public String achvRate(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json--------------achvRate-" + jsString);

            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            //业绩占比接口
            JSONObject jsonObject=activityAnalyService.getSalesRate(message);
            result.put("amt_trade",jsonObject.get("amt_trade").toString());//销售总额
            result.put("vip_trade",jsonObject.get("activity_amt_trade").toString());//活动金额
            result.put("discount_scale",jsonObject.get("activity_scale").toString());//活动占比
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("请求失败");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 业绩分析柱状图
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/achvView", method = RequestMethod.POST)
    @ResponseBody
    public String achvView(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json--------------achvView-" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            //图表
            JSONArray jsonArray=activityAnalyService.getSalesChart(message);
            JSONArray array = new JSONArray();
            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject1=new JSONObject();
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                jsonObject1.put("date",jsonObject.get("activity_date").toString());//活动时间
                jsonObject1.put("vip_trade",jsonObject.get("activity_amt_trade").toString());//活动金额
                jsonObject1.put("amt_trade",jsonObject.get("amt_trade").toString());//销售总额
                jsonObject1.put("rate", jsonObject.get("activity_scale").toString());//活动占比
                array.add(jsonObject1);
            }
            result.put("achvView",array);
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
     * 业绩分析分析列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/achvList", method = RequestMethod.POST)
    @ResponseBody
    public String achvList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json--------------achvList-" + jsString);

            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String type = jsonObject.getString("type");
            //获取列表信息
            JSONObject jsonObjectList= activityAnalyService.getSalesList(message);
            String infoValue=jsonObjectList.get("voucher_sales_list").toString();
            JSONArray jsonArrayList=JSON.parseArray(infoValue);
            JSONArray array = new JSONArray();
            if (type.equals("store")){
                for (int i = 0; i < jsonArrayList.size(); i++){
                    //列表数据
                    JSONObject jsonObject1=jsonArrayList.getJSONObject(i);
                    //详情数据
                    String info=jsonObject1.get("performance_info").toString();
                    JSONObject jsonObject2=JSON.parseObject(info);
                    JSONObject obj = new JSONObject();
                    obj.put("store_code",jsonObject1.get("store_code").toString());
                    obj.put("store_name",jsonObject1.get("store_name").toString());
                    obj.put("amt_trade",jsonObject1.get("amt_trade").toString()); //总业绩
                    obj.put("num_trade",jsonObject1.get("num_trade").toString());  //成交笔数
                    obj.put("num_sale",jsonObject1.get("num_sale").toString());  //销售件数
                    obj.put("vip_money",jsonObject1.getString("vip_money")==null?"":jsonObject1.getString("vip_money"));//会销金额
                    obj.put("all_new_vip",jsonObject1.getString("all_new_vip")==null?"":jsonObject1.getString("all_new_vip"));//总新增会员数
                    obj.put("activity_new_vip",jsonObject1.getString("activity_new_vip")==null?"":jsonObject1.getString("activity_new_vip"));//活动新增会员数
                    obj.put("activity_money",jsonObject1.getString("activity_money"));//活动金额
                    obj.put("discount",jsonObject2.get("discount").toString());//折扣
                    obj.put("sale_price",jsonObject2.get("sale_price").toString());  //件单价
                    obj.put("trade_price",jsonObject2.get("trade_price").toString()); //客单价
                    obj.put("relate_rate",jsonObject2.get("relate_rate").toString()); //连带率
                    obj.put("vip_discount",jsonObject2.get("vip_discount").toString());  //会销折扣
                    obj.put("vip_sale_price",jsonObject2.get("vip_sale_price").toString());  //会销件单价
                    obj.put("vip_trade_price",jsonObject2.get("vip_trade_price").toString()); //会销客单价
                    obj.put("vip_relate_rate",jsonObject2.get("vip_relate_rate").toString()); //会销连带率
                    obj.put("activity_discount",jsonObject2.get("activity_discount").toString());//活动折扣
                    obj.put("activity_customer_unit_price",jsonObject2.get("activity_customer_unit_price").toString());//活动客单价
                    obj.put("activity_pieces_unit_price",jsonObject2.get("activity_pieces_unit_price").toString());//活动件单价
                    obj.put("activity_joint_rate",jsonObject2.get("activity_joint_rate").toString());//连带率
                    obj.put("activity_sale",jsonObject2.getString("activity_sale")==null?"":jsonObject2.getString("activity_sale"));//活动件数
                    obj.put("activity_trade",jsonObject2.getString("activity_trade")==null?"":jsonObject2.getString("activity_trade"));//活动笔数
                    array.add(obj);
                }
            }else {
                for (int i = 0; i < jsonArrayList.size(); i++) {
                    JSONObject jsonObject1=jsonArrayList.getJSONObject(i);
                    String info=jsonObject1.get("performance_info").toString();
                    JSONObject jsonObject2=JSON.parseObject(info);
                    JSONObject obj = new JSONObject();
                    obj.put("user_code",jsonObject1.get("user_code").toString());
                    obj.put("user_name",jsonObject1.get("user_name").toString());
                    obj.put("store_code",jsonObject1.get("store_code").toString());
                    obj.put("store_name",jsonObject1.get("store_name").toString());
                    obj.put("amt_trade",jsonObject1.get("amt_trade").toString()); //总业绩
                    obj.put("num_trade",jsonObject1.get("num_trade").toString());  //成交笔数
                    obj.put("num_sale",jsonObject1.get("num_sale").toString());  //销售件数
                    obj.put("vip_money",jsonObject1.getString("vip_money")==null?"":jsonObject1.getString("vip_money"));//会销金额
                    obj.put("all_new_vip",jsonObject1.getString("all_new_vip")==null?"":jsonObject1.getString("all_new_vip"));//总新增会员数
                    obj.put("activity_new_vip",jsonObject1.getString("activity_new_vip")==null?"":jsonObject1.getString("all_new_vip"));//活动新增会员数
                    obj.put("activity_money",jsonObject1.getString("activity_money"));//活动金额
                    obj.put("discount",jsonObject2.get("discount").toString());//折扣
                    obj.put("sale_price",jsonObject2.get("sale_price").toString());  //件单价
                    obj.put("trade_price",jsonObject2.get("trade_price").toString()); //客单价
                    obj.put("relate_rate",jsonObject2.get("relate_rate").toString()); //连带率
                    obj.put("vip_discount",jsonObject2.get("vip_discount").toString());  //会销折扣
                    obj.put("vip_sale_price",jsonObject2.get("vip_sale_price").toString());  //会销件单价
                    obj.put("vip_trade_price",jsonObject2.get("vip_trade_price").toString()); //会销客单价
                    obj.put("vip_relate_rate",jsonObject2.get("vip_relate_rate").toString()); //会销连带率
                    obj.put("activity_discount",jsonObject2.get("activity_discount").toString());//活动折扣
                    obj.put("activity_customer_unit_price",jsonObject2.get("activity_customer_unit_price").toString());//活动客单价
                    obj.put("activity_pieces_unit_price",jsonObject2.get("activity_pieces_unit_price").toString());//活动件单价
                    obj.put("activity_joint_rate",jsonObject2.get("activity_joint_rate").toString());//活动连带率
                    obj.put("activity_sale",jsonObject2.getString("activity_sale")==null?"":jsonObject2.getString("activity_sale"));//活动件数
                    obj.put("activity_trade",jsonObject2.getString("activity_trade")==null?"":jsonObject2.getString("activity_trade"));//活动笔数
                    array.add(obj);
                }
            }
            result.put("pageNumber",jsonObjectList.getString("page_now"));
            result.put("pageSize",jsonObjectList.getString("page_size"));
            result.put("total_num",jsonObjectList.getString("total_num"));
            result.put("total_page",jsonObjectList.getString("total_page"));
            result.put("list",array);
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
     * 导出业绩分析分析列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/export_achvList", method = RequestMethod.POST)
    @ResponseBody
    public String export_achvList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        String id = "";
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            logger.info("json--------------achvList-" + jsString);

            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject object=JSON.parseObject(message);
            String page_num=object.getString("page_num");
            String page_size=object.getString("page_size");
            object.put("pageNumber",page_num);
            object.put("pageSize",page_size);
            message=object.toJSONString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String type = jsonObject.getString("type");
            //获取列表信息
            JSONObject jsonObjectList= activityAnalyService.getSalesList(message);
            String infoValue=jsonObjectList.get("voucher_sales_list").toString();
            JSONArray jsonArrayList=JSON.parseArray(infoValue);
            JSONArray array = new JSONArray();
            if (type.equals("store")){
                for (int i = 0; i < jsonArrayList.size(); i++){
                    JSONObject jsonObject1=jsonArrayList.getJSONObject(i);
                    String info=jsonObject1.get("performance_info").toString();
                    JSONObject jsonObject2=JSON.parseObject(info);
                    JSONObject obj = new JSONObject();
                    obj.put("store_code",jsonObject1.get("store_code").toString());
                    obj.put("store_name",jsonObject1.get("store_name").toString());
                    obj.put("amt_trade",jsonObject1.get("amt_trade").toString()); //总业绩
                    obj.put("num_trade",jsonObject1.get("num_trade").toString());  //成交笔数
                    obj.put("num_sale",jsonObject1.get("num_sale").toString());  //销售件数
                    obj.put("vip_money",jsonObject1.getString("vip_money")==null?"":jsonObject1.getString("vip_money"));//会销金额
                    obj.put("all_new_vip",jsonObject1.getString("all_new_vip")==null?"":jsonObject1.getString("all_new_vip"));//总新增会员数
                    obj.put("activity_new_vip",jsonObject1.getString("activity_new_vip")==null?"":jsonObject1.getString("activity_new_vip"));//活动新增会员数
                    obj.put("activity_money",jsonObject1.getString("activity_money"));//活动金额
                    obj.put("discount",jsonObject2.get("discount").toString());//折扣
                    obj.put("sale_price",jsonObject2.get("sale_price").toString());  //件单价
                    obj.put("trade_price",jsonObject2.get("trade_price").toString()); //客单价
                    obj.put("relate_rate",jsonObject2.get("relate_rate").toString()); //连带率
                    obj.put("vip_discount",jsonObject2.get("vip_discount").toString());  //会销折扣
                    obj.put("vip_sale_price",jsonObject2.get("vip_sale_price").toString());  //会销件单价
                    obj.put("vip_trade_price",jsonObject2.get("vip_trade_price").toString()); //会销客单价
                    obj.put("vip_relate_rate",jsonObject2.get("vip_relate_rate").toString()); //会销连带率
                    obj.put("activity_discount",jsonObject2.get("activity_discount").toString());
                    obj.put("activity_customer_unit_price",jsonObject2.get("activity_customer_unit_price").toString());
                    obj.put("activity_pieces_unit_price",jsonObject2.get("activity_pieces_unit_price").toString());
                    obj.put("activity_joint_rate",jsonObject2.get("activity_joint_rate").toString());
                    obj.put("activity_sale",jsonObject2.getString("activity_sale")==null?"":jsonObject2.getString("activity_sale"));//活动件数
                    obj.put("activity_trade",jsonObject2.getString("activity_trade")==null?"":jsonObject2.getString("activity_trade"));//活动笔数
                    array.add(obj);
                }
            }else {
                for (int i = 0; i < jsonArrayList.size(); i++) {
                    JSONObject jsonObject1=jsonArrayList.getJSONObject(i);
                    String info=jsonObject1.get("performance_info").toString();
                    JSONObject jsonObject2=JSON.parseObject(info);
                    JSONObject obj = new JSONObject();
                    obj.put("user_code",jsonObject1.get("user_code").toString());
                    obj.put("user_name",jsonObject1.get("user_name").toString());
                    obj.put("store_code",jsonObject1.get("store_code").toString());
                    obj.put("store_name",jsonObject1.get("store_name").toString());
                    obj.put("amt_trade",jsonObject1.get("amt_trade").toString()); //总业绩
                    obj.put("num_trade",jsonObject1.get("num_trade").toString());  //成交笔数
                    obj.put("num_sale",jsonObject1.get("num_sale").toString());  //销售件数
                    obj.put("vip_money",jsonObject1.getString("vip_money")==null?"":jsonObject1.getString("vip_money"));//会销金额
                    obj.put("all_new_vip",jsonObject1.getString("all_new_vip")==null?"":jsonObject1.getString("all_new_vip"));//总新增会员数
                    obj.put("activity_new_vip",jsonObject1.getString("activity_new_vip")==null?"":jsonObject1.getString("activity_new_vip"));//活动新增会员数
                    obj.put("activity_money",jsonObject1.getString("activity_money"));//活动金额
                    obj.put("discount",jsonObject2.get("discount").toString());//折扣
                    obj.put("sale_price",jsonObject2.get("sale_price").toString());  //件单价
                    obj.put("trade_price",jsonObject2.get("trade_price").toString()); //客单价
                    obj.put("relate_rate",jsonObject2.get("relate_rate").toString()); //连带率
                    obj.put("vip_discount",jsonObject2.get("vip_discount").toString());  //会销折扣
                    obj.put("vip_sale_price",jsonObject2.get("vip_sale_price").toString());  //会销件单价
                    obj.put("vip_trade_price",jsonObject2.get("vip_trade_price").toString()); //会销客单价
                    obj.put("vip_relate_rate",jsonObject2.get("vip_relate_rate").toString()); //会销连带率
                    obj.put("activity_discount",jsonObject2.get("activity_discount").toString());//活动折扣
                    obj.put("activity_customer_unit_price",jsonObject2.get("activity_customer_unit_price").toString());//活动客单价
                    obj.put("activity_pieces_unit_price",jsonObject2.get("activity_pieces_unit_price").toString());//活动件单价
                    obj.put("activity_joint_rate",jsonObject2.get("activity_joint_rate").toString());//活动连带率
                    obj.put("activity_sale",jsonObject2.getString("activity_sale")==null?"":jsonObject2.getString("activity_sale"));//活动件数
                    obj.put("activity_trade",jsonObject2.getString("activity_trade")==null?"":jsonObject2.getString("activity_trade"));//活动笔数
                    array.add(obj);
                }
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl_vip2(array,map,request,"业绩占比");

            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            JSONObject result2=new JSONObject();
            result2.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result2.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }


    //优惠券列表（导出）
    @RequestMapping(value = "/export_couponList",method = RequestMethod.POST)
    @ResponseBody
    public String export_couponList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        String errormessage = "数据异常，导出失败";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            JSONObject result= activityAnalyService.getCouponList(message);
            JSONArray array=JSON.parseArray(result.get("voucher_chat_list").toString());
            JSONArray new_array=new JSONArray();
            for (int i = 0; i < array.size(); i++) {
                JSONObject json_Obj=array.getJSONObject(i);
                JSONObject json=json_Obj.getJSONObject("vip_statis");
                json_Obj.put("num_coupon_card",json.getString("num_coupon_card"));
                json_Obj.put("verify_coupon_dis",json.getString("verify_coupon_dis"));
                json_Obj.put("coupon_rate",json.getString("coupon_rate"));
                json_Obj.put("coupon_expired",json.getString("coupon_expired"));
//                json_Obj.put("num_coupon_vip",json.getString("num_coupon_vip"));
                json_Obj.put("coupon_usage",json.getString("coupon_usage"));
                json_Obj.put("verify_coupon_total_price",json.getString("verify_coupon_total_price"));
                json_Obj.put("verify_coupon_offset_price",json.getString("verify_coupon_offset_price"));
                json_Obj.put("coupon_expired_count",json.getString("coupon_expired_count"));
                json_Obj.put("verify_coupon_price",json.getString("verify_coupon_price"));
                json_Obj.remove("vip_statis");
                new_array.add(json_Obj);
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl_vip2(new_array,map,request,"优惠券分析");

            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            JSONObject result2=new JSONObject();
            result2.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result2.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    //..................................已结束活动...................................................

    /**
     * 前四个图
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/compActi/achvRate", method = RequestMethod.POST)
    @ResponseBody
    public String lastAchvRate(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.get("activity_code").toString();
            String chart = jsonObject.get("chart").toString();

            VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
            String start_time = activityVip.getStart_time();
            String end_time = activityVip.getEnd_time();
            String corp_code=activityVip.getCorp_code();

            start_time=start_time.split(" ")[0];
            String[] endTime=end_time.split(" ");
            //大于14点 算当天业绩，小于14点不算当天业绩
            if(endTime[1].split(":")[0].compareTo("14")<0){
                end_time= Common.DATETIME_FORMAT_DAY.format(TimeUtils.getNextDay(endTime[0],1));
            }else {
                end_time=endTime[0];
            }
            end_time=end_time.split(" ")[0];


            DataBox dataBox = new DataBox();
            if (chart.equals("lastYear") || chart.equals("beforeTime") || chart.equals("price")){
                String type = jsonObject.get("type").toString();

                dataBox = iceInterfaceAPIService.lastAchvRate(corp_code,activity_code,start_time,end_time,type,chart);
            }else {
                dataBox = iceInterfaceAPIService.vipContribution(corp_code,activity_code,chart,start_time,end_time);
            }

            if (dataBox.status.toString().equals("SUCCESS")){
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(dataBox.data.get("message").value);
            }else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("获取失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //会员影响力分析
    @RequestMapping(value = "/vipEffectRate",method = RequestMethod.POST)
    @ResponseBody
    public String getVipEffectRate(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject rate= activityAnalyService.getVipEffectRate(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(rate.toString());

        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/vipEffectChat",method = RequestMethod.POST)
    @ResponseBody
    public String getVipEffectChat(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject chat= activityAnalyService.getVipEffectChat(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(chat.toString());

        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/vipEffectList",method = RequestMethod.POST)
    @ResponseBody
    public String getVipEffectList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject list= activityAnalyService.getVipEffectList1(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(list.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/registerList",method = RequestMethod.POST)
    @ResponseBody
    public String getEffectRegistertList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject list= activityAnalyService.getRegisterList(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(list.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    /*************************************************线上报名活动*******************************************************/
    @RequestMapping(value = "/onlineRate",method = RequestMethod.POST)
    @ResponseBody
    public String getOnlineRate(HttpServletRequest request) {
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject list= activityAnalyService.getOnlineRate(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(list.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/onlineChat",method = RequestMethod.POST)
    @ResponseBody
    public String getOnlineChat(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject list= activityAnalyService.getOnlineChat(message);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(list.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/onlineList",method = RequestMethod.POST)
    @ResponseBody
    public String getOnlineList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String id="";
        try{
            String jsString=request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String type=JSON.parseObject(message).getString("type");
            JSONObject obj=null;
            if(type.equals("vip")){
                obj= activityAnalyService.getOnlineListByVip(message);
            }else{
                obj=activityAnalyService.getOnlineListByStore(message);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(obj.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/onlineExportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject=JSONObject.parseObject(message);
            String type=jsonObject.getString("type");
            int pageNum=jsonObject.getInteger("page_num");
            int pageSize=jsonObject.getInteger("page_size");
            JSONObject obj=null;
            if(type.equals("vip")){
                obj= activityAnalyService.getOnlineListByVip(message);
            }else{
                obj= activityAnalyService.getOnlineListByStore(message);
            }
            String value_list= obj.getString("list");
            JSONArray value_array=JSON.parseArray(value_list);
            //导出......
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(value_array);
            if (value_array.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }

            int count=obj.getInteger("total");
            int start_line = (pageNum-1) * pageSize + 1;
            int end_line = pageNum*pageSize;
            if (count < pageNum*pageSize)
                end_line = count;

            LinkedHashMap<String, String> linkedHashMap = WebUtils.Json2ShowName(jsonObject);
            if(linkedHashMap.containsKey("apply_info_format")){
                if(value_array.size()>0){
                    JSONObject jsonObject1=value_array.getJSONObject(0);
                    String apply_info=jsonObject1.getString("apply_info");
                    JSONArray apply_info_array=JSON.parseArray(apply_info);
                    for (int j = 0; j < apply_info_array.size(); j++) {
                        JSONObject jsonObject2=apply_info_array.getJSONObject(j);
                        String param_desc=jsonObject2.getString("param_desc");
                        String column=jsonObject2.getString("column");
                        column+="ex_";
                        linkedHashMap.put(column,param_desc);
                    }
                }
                linkedHashMap.remove("apply_info_format");
            }

            String pathname = OutExeclHelper.OutExecl(json, value_array, linkedHashMap, response, request, "线上活动报名会员("+start_line+"-"+end_line+")");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }


}
