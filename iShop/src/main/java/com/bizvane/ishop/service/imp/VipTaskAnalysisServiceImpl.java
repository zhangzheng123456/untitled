package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.Questionnaire;
import com.bizvane.ishop.entity.VipTask;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by yanyadong on 2017/11/28.
 */
@Service
public class VipTaskAnalysisServiceImpl implements VipTaskAnalysisService {

    @Autowired
    MongoDBClient mongoDBClient;

    @Autowired
    VipTaskService vipTaskService;

    @Autowired
    IceInterfaceService iceInterfaceService;

    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;

    @Autowired
    QuestionnaireService questionnaireService;

    @Autowired
    VipGroupService vipGroupService;

    /**********************************************邀请注册**************************************************/

    @Override
    public JSONObject getRegisterRate(String message, HttpServletRequest request) throws Exception {

        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();

        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_shareUrl=mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
        DBCollection dbCollection_register=mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);

        JSONObject json_message=JSON.parseObject(message);
        String task_code=json_message.getString("task_code");
        VipTask vipTask = vipTaskService.selectByTaskCode(task_code);

        String task_start_time=vipTask.getStart_time().split(" ")[0];
        String task_end_time=vipTask.getEnd_time().split(" ")[0];

        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBObject.put("vipTask.task_code",task_code);
        basicDBObject.put("vipTask.task_type","invite_registration");
        DBCursor dbCursor=dbCollection_shareUrl.find(basicDBObject);
        int share_url_num=dbCursor.count();//分享次数
        int share_vip_num=dbCollection_shareUrl.distinct("open_id",basicDBObject).size();//分享人数

        BasicDBList basicDBList=new BasicDBList();
        basicDBList.add(new BasicDBObject("app_id",vipTask.getApp_id()));
        basicDBList.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.GTE, task_start_time+" 00:00:00")));
        basicDBList.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.LTE, task_end_time+" 23:59:59")));
        BasicDBObject basicDBObject1=new BasicDBObject();
        basicDBObject1.put("$and",basicDBList);
        DBCursor dbCursor1=dbCollection_register.find(basicDBObject1);
        int register_vip_num=dbCursor1.count();//注册人数

        //目标会员数
//        JSONArray jsonArray= vipGroupService.vipScreen2Array(JSON.parseArray(vipTask.getTarget_vips()),vipTask.getCorp_code(),role_code, brand_code, area_code, store_code, user_code);
        DataBox databox=iceInterfaceService.vipScreenMethod2("1","1",vipTask.getCorp_code(),vipTask.getTarget_vips_(),null,null);
        String target_count="0";
        if (databox.status.toString().equals("SUCCESS")) {
            String result = databox.data.get("message").value;
            JSONObject result_obj = JSONObject.parseObject(result);
            target_count=result_obj.getString("count");
        }

        JSONObject result=new JSONObject();
        result.put("share_url_num",share_url_num);
        result.put("share_vip_num",share_vip_num);
        result.put("register_vip_num",register_vip_num);
        result.put("vip_target_count",target_count);

        return result;
    }

    @Override
    public JSONObject getRegisterChart(String message) throws Exception {

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_shareUrl=mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
        DBCollection dbCollection_register=mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);

        JSONObject json_message=JSON.parseObject(message);
        String start_date=json_message.getString("start_time");
        String end_date=json_message.getString("end_time");
        String task_code=json_message.getString("task_code");
        VipTask vipTask = vipTaskService.selectByTaskCode(task_code);
        String task_start_time=vipTask.getStart_time().split(" ")[0];
        String task_end_time=vipTask.getEnd_time().split(" ")[0];

        if(StringUtils.isNotBlank(start_date)&&StringUtils.isBlank(end_date)){
            end_date=task_end_time;
        }else if(StringUtils.isBlank(start_date)&&StringUtils.isNotBlank(end_date)){
            start_date=task_start_time;
        }else if(StringUtils.isBlank(start_date)&&StringUtils.isBlank(end_date)){
            end_date=simpleDateFormat.format(new Date());
            if(end_date.compareTo(task_end_time)>0){
                end_date=task_end_time;
            }
            if(end_date.compareTo(task_start_time)<0){
                end_date=task_start_time;
            }
            Date date= TimeUtils.getNextDay(end_date,7);
            start_date=simpleDateFormat.format(date);
            if(start_date.compareTo(task_start_time)<0){
                start_date=task_start_time;
            }
        }
        JSONArray result_array=new JSONArray();
        List<String> list=TimeUtils.getBetweenDates(start_date,end_date);
        for (int i = 0; i < list.size(); i++) {
            Pattern pattern = Pattern.compile("^.*" + list.get(i) + ".*$", Pattern.CASE_INSENSITIVE);

            BasicDBObject basicDBObject=new BasicDBObject();
            BasicDBList basicurl_list=new BasicDBList();
            basicurl_list.add(new BasicDBObject("vipTask.task_code",task_code));
            basicurl_list.add(new BasicDBObject("vipTask.task_type","invite_registration"));
            basicurl_list.add(new BasicDBObject("share_time",pattern));
            basicDBObject.put("$and",basicurl_list);

            DBCursor dbCursor=dbCollection_shareUrl.find(basicDBObject);
            int share_url_num=dbCursor.count();//分享次数
            int share_vip_num=dbCollection_shareUrl.distinct("open_id",basicDBObject).size();//分享人数

            BasicDBList basicDBList_register=new BasicDBList();
            basicDBList_register.add(new BasicDBObject("app_id",vipTask.getApp_id()));
            basicDBList_register.add(new BasicDBObject("register_time",pattern));
            BasicDBObject basicDBObject1=new BasicDBObject();
            basicDBObject1.put("$and",basicDBList_register);
            DBCursor dbCursor1=dbCollection_register.find(basicDBObject1);
            int register_vip_num=dbCursor1.count();//注册人数

            JSONObject result=new JSONObject();
            result.put("share_url_num",share_url_num);
            result.put("share_vip_num",share_vip_num);
            result.put("register_vip_num",register_vip_num);
            result.put("time",list.get(i));
            result_array.add(result);
        }

        JSONObject  time_obj=new JSONObject();
        time_obj.put("list",result_array);
        return time_obj;
    }

    //列表显示的为已完成任务的会员
    @Override
    public JSONObject getRegisterList(String message) throws Exception {
        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_shareUrl=mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
        DBCollection dbCollection_register=mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);
        DBCollection dbCollection_schedule=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);

        JSONObject message_obj=JSON.parseObject(message);
        int page_num=message_obj.getInteger("page_num");
        int page_size=message_obj.getInteger("page_size");
        String task_code=message_obj.getString("task_code");
        String screen=message_obj.getString("screen");
        JSONObject screen_obj=JSON.parseObject(screen);
        String vip_name=screen_obj.getString("vip_name");
        String vip_cardno=screen_obj.getString("vip_cardno");
        String vip_phone=screen_obj.getString("vip_phone");
        String complete_start=JSONArray.parseObject(screen_obj.getString("complete_date")).getString("start");
        String complete_end=JSONArray.parseObject(screen_obj.getString("complete_date")).getString("end");
        String share_start=JSON.parseObject(screen_obj.getString("share_count")).getString("start");
        String share_end=JSON.parseObject(screen_obj.getString("share_count")).getString("end");
        String register_start=JSON.parseObject(screen_obj.getString("register_count")).getString("start");
        String register_end=JSON.parseObject(screen_obj.getString("register_count")).getString("end");

        VipTask vipTask=vipTaskService.selectByTaskCode(task_code);
        String task_start_time=vipTask.getStart_time().split(" ")[0];
        String task_end_time=vipTask.getEnd_time().split(" ")[0];

        BasicDBObject  basicDBObject=new BasicDBObject();
        BasicDBList basicDBList_screen=new BasicDBList();
        //basicDBList_screen.add(new BasicDBObject("status","1"));
        basicDBList_screen.add(new BasicDBObject("task.task_code",task_code));
        if(StringUtils.isNotBlank(vip_name)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
        }
        if(StringUtils.isNotBlank(vip_cardno)){
            basicDBList_screen.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",vip_cardno)));
        }
        if(StringUtils.isNotBlank(vip_phone)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
        }
        if(StringUtils.isNotBlank(complete_start)){
            basicDBList_screen.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, complete_start+" 00:00:00")));
        }
        if(StringUtils.isNotBlank(complete_end)){
            basicDBList_screen.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, complete_end+" 23:59:59")));
        }
        basicDBObject.put("$and",basicDBList_screen);
        DBCursor dbCursor=dbCollection_schedule.find(basicDBObject).sort(new BasicDBObject("modified_date",-1));

        JSONArray vip_info_array=new JSONArray();
        while (dbCursor.hasNext()){

            DBObject dbObject= dbCursor.next();
            String vip_info= dbObject.get("vip").toString();
            JSONObject vip_obj=JSON.parseObject(vip_info);
            String open_id=dbObject.get("open_id").toString();
            vip_obj.put("open_id",open_id);
            if(StringUtils.isNotBlank(vip_obj.getString("name_vip"))){
                vip_obj.put("vip_name",vip_obj.getString("name_vip"));
            }
            if(StringUtils.isNotBlank(vip_obj.getString("card_no_vip"))){
                vip_obj.put("cardno",vip_obj.getString("card_no_vip"));
            }
            if(StringUtils.isNotBlank(vip_obj.getString("mobile_vip"))){
                vip_obj.put("vip_phone",vip_obj.getString("mobile_vip"));
            }
            if(StringUtils.isNotBlank(vip_obj.getString("card_type_id"))){
                vip_obj.put("vip_card_type",vip_obj.getString("card_type_id"));
            }
            String complete_date=dbObject.get("modified_date").toString();
            String statues=dbObject.get("status").toString();

            BasicDBObject basicDBObject_share=new BasicDBObject();
            basicDBObject_share.put("vipTask.task_code",task_code);
            basicDBObject_share.put("vipTask.task_type","invite_registration");
            basicDBObject_share.put("open_id",open_id);
            DBCursor dbCursor_share=dbCollection_shareUrl.find(basicDBObject_share);
            int share_url_num=dbCursor_share.count();//分享次数


            BasicDBList basicDBList=new BasicDBList();
            basicDBList.add(new BasicDBObject("invite_open_id",open_id));
            basicDBList.add(new BasicDBObject("app_id",vipTask.getApp_id()));
            basicDBList.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.GTE, task_start_time+" 00:00:00")));
            basicDBList.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.LTE, task_end_time+" 23:59:59")));
            BasicDBObject basicDBObject1=new BasicDBObject();
            basicDBObject1.put("$and",basicDBList);
            DBCursor dbCursor1=dbCollection_register.find(basicDBObject1);
            int register_vip_num=dbCursor1.count();//注册人数

            JSONObject vip_info_obj=new JSONObject();
            vip_info_obj.put("vip_info",vip_obj);
            vip_info_obj.put("share_url_num",share_url_num);
            vip_info_obj.put("register_vip_num",register_vip_num);

            vip_info_obj.put("click_url_num",0);//邀请链接被打开次数
            vip_info_obj.put("register_sale_amt",0);//被邀请会员的首单消费总和
            if(statues.equals("1")) {
                vip_info_obj.put("complete_date", complete_date);
            }else{
                vip_info_obj.put("complete_date","");
            }

            if(register_vip_num==0){
                continue;
            }
            vip_info_array.add(vip_info_obj);
        }
        JSONArray new_array=new JSONArray();
        for (int i = 0; i < vip_info_array.size(); i++) {
            JSONObject obj=vip_info_array.getJSONObject(i);
            int share_num=obj.getInteger("share_url_num");
            int register_num=obj.getInteger("register_vip_num");
            if(StringUtils.isNotBlank(share_start)){
                if(share_num<Integer.parseInt(share_start)){
                    continue;
                }
            }
            if(StringUtils.isNotBlank(share_end)){
                if(share_num>Integer.parseInt(share_end)){
                    continue;
                }
            }

            if(StringUtils.isNotBlank(register_start)){
                if(register_num<Integer.parseInt(register_start)){
                    continue;
                }
            }
            if(StringUtils.isNotBlank(register_end)){
                if(register_num>Integer.parseInt(register_end)){
                    continue;
                }
            }
            new_array.add(obj);
        }

        int end_row = 0;
        if (new_array.size() > page_num*page_size){
            end_row = page_num*page_size;
        }else {
            end_row = new_array.size();
        }

        JSONArray new_array_v2=new JSONArray();
        for (int i = (page_num-1)*page_size ; i < end_row; i++){
            new_array_v2.add(new_array.getJSONObject(i));
        }

        int count=new_array.size();
        int pages = 0;
        if (count % page_size == 0) {
            pages = count / page_size;
        } else {
            pages = count / page_size + 1;
        }
        boolean flag=true;
        if(page_num>=pages){
            flag=false;
        }
        JSONObject reslut=new JSONObject();
        reslut.put("list",new_array_v2);
        reslut.put("page_num",page_num);
        reslut.put("page_size",page_size);
        reslut.put("total",count);
        reslut.put("pages",pages);
        reslut.put("is_next",flag);
        return reslut;
    }

    public  JSONObject getRegisterVipInfo(String message) throws Exception{

        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_register=mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);

        JSONObject message_obj=JSON.parseObject(message);
        int page_num=message_obj.getInteger("page_num");
        int page_size=message_obj.getInteger("page_size");
        String task_code=message_obj.getString("task_code");
        String open_id=message_obj.getString("open_id");

        VipTask vipTask=vipTaskService.selectByTaskCode(task_code);
        String task_start_time=vipTask.getStart_time().split(" ")[0];
        String task_end_time=vipTask.getEnd_time().split(" ")[0];

        BasicDBList basicDBList=new BasicDBList();
        basicDBList.add(new BasicDBObject("invite_open_id",open_id));
        basicDBList.add(new BasicDBObject("app_id",vipTask.getApp_id()));
        basicDBList.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.GTE, task_start_time+" 00:00:00")));
        basicDBList.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.LTE, task_end_time+" 23:59:59")));
        BasicDBObject basicDBObject1=new BasicDBObject();
        basicDBObject1.put("$and",basicDBList);
        DBCursor dbCursor1=dbCollection_register.find(basicDBObject1);

        int total = dbCursor1.count();
        int pages = MongoUtils.getPages(dbCursor1, page_size);
        DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor1, page_num, page_size, "register_time", -1);
        ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
        String vip_ids="";
        for (int i = 0; i < list.size(); i++) {
            Map map= (Map) list.get(i);
            String vip_id=JSON.parseObject(map.get("register_vipInfo").toString()).getString("VIP_ID");
            vip_ids+=vip_id+",";
        }

        JSONArray all_vip_list=new JSONArray();
        DataBox databox1=iceInterfaceService.vipSearchByHbase(vipTask.getCorp_code(),vip_ids);
        if((databox1.status.toString().equals("SUCCESS"))){
            String result = databox1.data.get("message").value;
            JSONObject vip_obj=JSON.parseObject(result);
            all_vip_list=vip_obj.getJSONArray("all_vip_list");
        }
        boolean flag=true;
        if(page_num>=pages){
            flag=false;
        }
        JSONObject result=new JSONObject();
        result.put("list", all_vip_list);
        result.put("pages", pages);
        result.put("page_num", page_num);
        result.put("page_size", page_size);
        result.put("total", total);
        result.put("is_next",flag);
        return  result;
    }

    /*************************************完善资料*************************************************************/

    //完善字段大于一个的
    @Override
    public JSONObject getCompleteRate(String message,HttpServletRequest request) throws Exception {

        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();

        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_schedule=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        JSONObject message_obj=JSON.parseObject(message);
        String task_code=message_obj.getString("task_code");
        VipTask vipTask=vipTaskService.selectByTaskCode(task_code);
        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBObject.put("task.task_type","improve_data");
        basicDBObject.put("task.task_code",task_code);
        basicDBObject.put("schedule.0",new BasicDBObject("$exists",1));//数组的第一个元素必须存在
        DBCursor dbcursor=dbCollection_schedule.find(basicDBObject);
        int partake_num=dbcursor.count();//参与总数
        basicDBObject.put("status","1");
        DBCursor dbcursor1=dbCollection_schedule.find(basicDBObject);
        int complete_num=dbcursor1.count();//完成总数

        //目标会员数
//        JSONArray jsonArray= vipGroupService.vipScreen2Array(JSON.parseArray(vipTask.getTarget_vips()),vipTask.getCorp_code(),role_code, brand_code, area_code, store_code, user_code);
        DataBox databox=iceInterfaceService.vipScreenMethod2("1","1",vipTask.getCorp_code(),vipTask.getTarget_vips_(),null,null);
        String target_count="0";
        if (databox.status.toString().equals("SUCCESS")) {
            String result = databox.data.get("message").value;
            JSONObject result_obj = JSONObject.parseObject(result);
            target_count=result_obj.getString("count");
        }

        JSONObject result=new JSONObject();
        result.put("partake_num",partake_num);
        result.put("complete_num",complete_num);
        result.put("vip_target_count",target_count);
        return result;
    }

    @Override
    public JSONObject getCompleteChart(String message) throws Exception {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_schedule=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        JSONObject json_message=JSON.parseObject(message);
        String task_code=json_message.getString("task_code");
        String start_date=json_message.getString("start_time");
        String end_date=json_message.getString("end_time");
        VipTask vipTask = vipTaskService.selectByTaskCode(task_code);
        String task_start_time=vipTask.getStart_time().split(" ")[0];
        String task_end_time=vipTask.getEnd_time().split(" ")[0];

        if(StringUtils.isNotBlank(start_date)&&StringUtils.isBlank(end_date)){
            end_date=task_end_time;
        }else if(StringUtils.isBlank(start_date)&&StringUtils.isNotBlank(end_date)){
            start_date=task_start_time;
        }else if(StringUtils.isBlank(start_date)&&StringUtils.isBlank(end_date)){
            end_date=simpleDateFormat.format(new Date());
            if(end_date.compareTo(task_end_time)>0){
                end_date=task_end_time;
            }
            if(end_date.compareTo(task_start_time)<0){
                end_date=task_start_time;
            }
            Date date= TimeUtils.getNextDay(end_date,7);
            start_date=simpleDateFormat.format(date);
            if(start_date.compareTo(task_start_time)<0){
                start_date=task_start_time;
            }
        }
        JSONArray result_array=new JSONArray();
        List<String> list=TimeUtils.getBetweenDates(start_date,end_date);
        for (int i = 0; i < list.size(); i++) {
            Pattern pattern = Pattern.compile("^.*" + list.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
            BasicDBObject basicDBObject=new BasicDBObject();
            BasicDBList basicurl_list=new BasicDBList();
            basicurl_list.add(new BasicDBObject("task.task_type","improve_data"));
            basicurl_list.add(new BasicDBObject("task.task_code",task_code));
            basicurl_list.add(new BasicDBObject("schedule.0",new BasicDBObject("$exists",1)));//数组的第一个元素必须存在
            basicurl_list.add(new BasicDBObject("modified_date",pattern));
            basicDBObject.put("$and",basicurl_list);
            DBCursor dbcursor=dbCollection_schedule.find(basicDBObject);
            int partake_num=dbcursor.count();//参与总数

            basicurl_list.add(new BasicDBObject("status","1"));
            BasicDBObject basicDBObject1=new BasicDBObject();
            basicDBObject1.put("$and",basicurl_list);
            DBCursor dbcursor1= dbCollection_schedule.find(basicDBObject1);
            int complete_num=dbcursor1.count();//完成总数

            JSONObject result=new JSONObject();
            result.put("partake_num",partake_num);
            result.put("complete_num",complete_num);
            result.put("time",list.get(i));
            result_array.add(result);
        }
        JSONObject json_obj=new JSONObject();
        json_obj.put("list",result_array);
        return json_obj;
    }

    @Override
    public JSONObject getCompleteList(String message) throws Exception {
        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_schedule=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        JSONObject json_message=JSON.parseObject(message);
        String task_code=json_message.getString("task_code");
        int page_num=json_message.getInteger("page_num");
        int page_size=json_message.getInteger("page_size");
        String screen=json_message.getString("screen");
        JSONObject screen_obj=JSON.parseObject(screen);
        String vip_name=screen_obj.getString("vip_name");
        String vip_cardno=screen_obj.getString("vip_cardno");
        String vip_phone=screen_obj.getString("vip_phone");
        String status=screen_obj.getString("status");
        String complete_value=screen_obj.getString("complete_value");
        String complete_start=JSONArray.parseObject(screen_obj.getString("complete_date")).getString("start");
        String complete_end=JSONArray.parseObject(screen_obj.getString("complete_date")).getString("end");

        BasicDBObject  basicDBObject=new BasicDBObject();
        BasicDBList basicDBList_screen=new BasicDBList();
        basicDBList_screen.add(new BasicDBObject("task.task_code",task_code));
        basicDBList_screen.add(new BasicDBObject("schedule.0",new BasicDBObject("$exists",1)));//数组的第一个元素必须存在
        if(StringUtils.isNotBlank(vip_name)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
        }
        if(StringUtils.isNotBlank(vip_cardno)){
            basicDBList_screen.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",vip_cardno)));
        }
        if(StringUtils.isNotBlank(vip_phone)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
        }
        if(StringUtils.isNotBlank(status)){
            basicDBList_screen.add(new BasicDBObject("status",status));
        }
        if(StringUtils.isNotBlank(complete_start)){
            basicDBList_screen.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, complete_start+" 00:00:00")));
        }
        if(StringUtils.isNotBlank(complete_end)){
            basicDBList_screen.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, complete_end+" 23:59:59")));
        }

        if(StringUtils.isNotBlank(complete_value)){
            String[] completeValues=complete_value.split(",");
            BasicDBObject basic_obj=new BasicDBObject();
            BasicDBList basic_list=new BasicDBList();
            for (int i = 0; i < completeValues.length; i++) {
                BasicDBObject query=new BasicDBObject();
                query.put("column",completeValues[i]);
                basic_list.add(new BasicDBObject("schedule",new BasicDBObject("$elemMatch",query)));
            }
            basic_obj.put("$or",basic_list);
            basicDBList_screen.add(basic_obj);
        }
        basicDBObject.put("$and",basicDBList_screen);
        DBCursor dbCursor=dbCollection_schedule.find(basicDBObject);
        int total = dbCursor.count();
        int pages = MongoUtils.getPages(dbCursor, page_size);
        DBCursor dbCursor1 = MongoUtils.sortAndPage(dbCursor, page_num, page_size, "modified_date", -1);

        JSONArray list_array=new JSONArray();
        while (dbCursor1.hasNext()){
            JSONObject obj=new JSONObject();
            DBObject dbobject= dbCursor1.next();
            String vip=dbobject.get("vip").toString();
            JSONObject vip_obj= JSON.parseObject(vip);//会员信息
            String  task=dbobject.get("task").toString();
            JSONObject task_obj=JSON.parseObject(task);
            String task_condition=task_obj.getString("task_condition");
            int total_num=JSON.parseArray(task_condition).size();//需要完成的总数
            JSONArray total_array=JSON.parseArray(task_condition);
            String schedule=dbobject.get("schedule").toString();
            JSONArray complete_array=JSON.parseArray(schedule);//完成的集合
            int complete_num=complete_array.size();//完成的数目
            String vip_status=dbobject.get("status").toString();
            if(vip_status.equals("0")){
                vip_status="否";
            }else{
                vip_status="是";
            }
            String complete_date=dbobject.get("modified_date").toString();//完成的时间
            obj.put("vip",vip_obj);
            obj.put("status",vip_status);
            obj.put("total_num",total_num);
            obj.put("total_array",total_array);
            obj.put("complete_num",complete_num);
            obj.put("complete_array",complete_array);
            obj.put("complete_date",complete_date);
            list_array.add(obj);
        }
        boolean flag=true;
        if(page_num>=pages){
            flag=false;
        }
        JSONObject result=new JSONObject();
        result.put("list", list_array);
        result.put("pages", pages);
        result.put("page_num", page_num);
        result.put("page_size", page_size);
        result.put("total", total);
        result.put("is_next",flag);
        return result;
    }

    /************************************累积消费次数、累积消费金额、客单价 问卷调查（占比 图表）*************************************/
    @Override
    public JSONObject getSaleInfoRate(String message,HttpServletRequest request) throws Exception {
        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();

        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_schedule=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        JSONObject json_message=JSON.parseObject(message);
        String task_code=json_message.getString("task_code");
        VipTask vipTask=vipTaskService.selectByTaskCode(task_code);
        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBObject.put("task.task_code",task_code);
        basicDBObject.put("status","1");
        DBCursor dbCursor1=dbCollection_schedule.find(basicDBObject);
        int complete_vip_num=dbCursor1.count();//完成的会员数

        //目标会员数
//        JSONArray jsonArray= vipGroupService.vipScreen2Array(JSON.parseArray(vipTask.getTarget_vips()),vipTask.getCorp_code(),role_code, brand_code, area_code, store_code, user_code);
        DataBox databox=iceInterfaceService.vipScreenMethod2("1","1",vipTask.getCorp_code(),vipTask.getTarget_vips_(),null,null);
        String target_count="0";
        if (databox.status.toString().equals("SUCCESS")) {
            String result = databox.data.get("message").value;
            JSONObject result_obj = JSONObject.parseObject(result);
            target_count=result_obj.getString("count");
        }

        JSONObject result=new JSONObject();
        result.put("complete_vip_num",complete_vip_num);
        result.put("vip_target_count",target_count);
        return result;
    }

    @Override
    public JSONObject getSaleInfoChart(String message) throws Exception {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_schedule=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        JSONObject json_message=JSON.parseObject(message);
        String task_code=json_message.getString("task_code");
        String start_date=json_message.getString("start_time");
        String end_date=json_message.getString("end_time");
        VipTask vipTask = vipTaskService.selectByTaskCode(task_code);
        String task_start_time=vipTask.getStart_time().split(" ")[0];
        String task_end_time=vipTask.getEnd_time().split(" ")[0];

        if(StringUtils.isNotBlank(start_date)&&StringUtils.isBlank(end_date)){
            end_date=task_end_time;
        }else if(StringUtils.isBlank(start_date)&&StringUtils.isNotBlank(end_date)){
            start_date=task_start_time;
        }else if(StringUtils.isBlank(start_date)&&StringUtils.isBlank(end_date)){
            end_date=simpleDateFormat.format(new Date());
            if(end_date.compareTo(task_end_time)>0){
                end_date=task_end_time;
            }
            if(end_date.compareTo(task_start_time)<0){
                end_date=task_start_time;
            }
            Date date= TimeUtils.getNextDay(end_date,7);
            start_date=simpleDateFormat.format(date);
            if(start_date.compareTo(task_start_time)<0){
                start_date=task_start_time;
            }
        }
        JSONArray result_array=new JSONArray();
        List<String> list=TimeUtils.getBetweenDates(start_date,end_date);

        //获取任务下时间范围内每天的完成人数
        List<String> comp_list=TimeUtils.getBetweenDates(task_start_time,task_end_time);
        for (int i = 0; i < comp_list.size(); i++) {
            Pattern pattern = Pattern.compile("^.*" + comp_list.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
            BasicDBObject basicDBObject=new BasicDBObject();
            basicDBObject.put("modified_date",pattern);
            basicDBObject.put("task.task_code",task_code);
            basicDBObject.put("status","1");
            DBCursor dbCursor1=dbCollection_schedule.find(basicDBObject);
            int complete_vip_num=dbCursor1.count();//完成的会员数
            JSONObject result=new JSONObject();
            result.put("time",comp_list.get(i));
            result.put("complete_vip_num",complete_vip_num);
            result_array.add(result);
        }

        JSONArray all_array=new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            int all_complete_vip_num=0;
            for (int j = 0; j < result_array.size(); j++) {
                JSONObject result_obj=result_array.getJSONObject(j);
                String time=result_obj.getString("time");
                int complete_vip_num=result_obj.getInteger("complete_vip_num");
                if(list.get(i).compareTo(time)<0){
                    break;
                }
                all_complete_vip_num+=complete_vip_num;
            }
            JSONObject all_obj=new JSONObject();
            all_obj.put("time",list.get(i));
            all_obj.put("complete_vip_num",all_complete_vip_num);
            all_array.add(all_obj);
        }
        JSONObject obj=new JSONObject();
        obj.put("list",all_array);
        return obj;
    }

    //已完成任务的列表
    @Override
    public JSONObject getSaleInfoList(String message) throws Exception {
        JSONObject json_message=JSON.parseObject(message);
        String task_code=json_message.getString("task_code");
        int page_num=json_message.getInteger("page_num");
        int page_size=json_message.getInteger("page_size");
        String screen=json_message.getString("screen");
        JSONObject screen_obj=JSON.parseObject(screen);
        String vip_name=screen_obj.getString("vip_name");
        String vip_cardno=screen_obj.getString("vip_cardno");
        String vip_phone=screen_obj.getString("vip_phone");
        String vip_card_type=screen_obj.getString("vip_card_type");
        String complete_start=JSONArray.parseObject(screen_obj.getString("complete_date")).getString("start");
        String complete_end=JSONArray.parseObject(screen_obj.getString("complete_date")).getString("end");

        VipTask viptask=vipTaskService.selectByTaskCode(task_code);
        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_schedule=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);

        BasicDBObject  basicDBObject=new BasicDBObject();
        BasicDBList basicDBList_screen=new BasicDBList();
        basicDBList_screen.add(new BasicDBObject("task.task_code",task_code));
        basicDBList_screen.add(new BasicDBObject("status","1"));
        if(StringUtils.isNotBlank(vip_name)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
        }
        if(StringUtils.isNotBlank(vip_cardno)){
            basicDBList_screen.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",vip_cardno)));
        }
        if(StringUtils.isNotBlank(vip_phone)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
        }
        if(StringUtils.isNotBlank(vip_card_type)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_card_type",new BasicDBObject("$regex",vip_card_type)));
        }
        if(StringUtils.isNotBlank(complete_start)){
            basicDBList_screen.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, complete_start+" 00:00:00")));
        }
        if(StringUtils.isNotBlank(complete_end)){
            basicDBList_screen.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, complete_end+" 23:59:59")));
        }
        basicDBObject.put("$and",basicDBList_screen);
        DBCursor dbCursor=dbCollection_schedule.find(basicDBObject);
        int total = dbCursor.count();
        int pages = MongoUtils.getPages(dbCursor, page_size);
        DBCursor dbCursor1 = MongoUtils.sortAndPage(dbCursor, page_num, page_size, "modified_date", -1);
        JSONArray obj_array=new JSONArray();
        while (dbCursor1.hasNext()){
            DBObject dbobject= dbCursor1.next();
            String complete_date=dbobject.get("modified_date").toString();//完成的时间
            String vip=dbobject.get("vip").toString();
            JSONObject vip_obj= JSON.parseObject(vip);//会员信息
            String  cardno=vip_obj.getString("cardno");
            String  task=dbobject.get("task").toString();
            JSONObject task_obj=JSON.parseObject(task);
            String task_condition_js=task_obj.getString("task_condition");
            JSONObject task_condition=JSON.parseObject(task_condition_js);//"task_condition":{"start_time":"","end_time":"","count":"11"}
            String start_time=task_condition.getString("start_time");
            String end_time=task_condition.getString("end_time");
            DataBox dataBox = iceInterfaceAPIService.VipDetail(viptask.getCorp_code(),"",cardno,"",start_time,end_time);
            double amt_trade=0d;
            double num_trade=0d;
            double ticket_sales=0d;
            if (dataBox.status.toString().equals("SUCCESS")){
                String msg = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(msg);
                amt_trade = Double.parseDouble(msg_obj.get("amt_trade").toString());//金额
                num_trade = Double.parseDouble(msg_obj.get("num_trade").toString());//笔数
                if (msg_obj.containsKey("ticket_sales"))//客单价
                    ticket_sales = Double.parseDouble(msg_obj.get("ticket_sales").toString());
            }
            JSONObject obj=new JSONObject();
            obj.put("vip",vip_obj);
            obj.put("complete_date",complete_date);
            obj.put("amt_trade",amt_trade);
            obj.put("num_trade",num_trade);
            obj.put("ticket_sales",ticket_sales);
            obj_array.add(obj);
        }
        boolean flag=true;
        if(page_num>=pages){
            flag=false;
        }
        JSONObject result=new JSONObject();
        result.put("list",obj_array);
        result.put("pages", pages);
        result.put("page_num", page_num);
        result.put("page_size", page_size);
        result.put("total", total);
        result.put("is_next",flag);
        return result;
    }


    /********************************************问卷调查 列表***********************************************************/

    //已完成任务的列表
    @Override
    public JSONObject getQuestionNaireList(String message) throws Exception {
        JSONObject json_message=JSON.parseObject(message);
        String task_code=json_message.getString("task_code");
        int page_num=json_message.getInteger("page_num");
        int page_size=json_message.getInteger("page_size");
        String screen=json_message.getString("screen");
        JSONObject screen_obj=JSON.parseObject(screen);
        String vip_name=screen_obj.getString("vip_name");
        String vip_cardno=screen_obj.getString("vip_cardno");
        String vip_phone=screen_obj.getString("vip_phone");
        String vip_card_type=screen_obj.getString("vip_card_type");
        String answer_start=JSONObject.parseObject(screen_obj.getString("answer_num")).getString("start");//答题数
        String answer_end=JSONObject.parseObject(screen_obj.getString("answer_num")).getString("end");//答题数
        String complete_start=JSONArray.parseObject(screen_obj.getString("complete_date")).getString("start");
        String complete_end=JSONArray.parseObject(screen_obj.getString("complete_date")).getString("end");

        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_schedule=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);

        BasicDBObject  basicDBObject=new BasicDBObject();
        BasicDBList basicDBList_screen=new BasicDBList();
        basicDBList_screen.add(new BasicDBObject("task.task_code",task_code));
        basicDBList_screen.add(new BasicDBObject("status","1"));
        if(StringUtils.isNotBlank(vip_name)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
        }
        if(StringUtils.isNotBlank(vip_cardno)){
            basicDBList_screen.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",vip_cardno)));
        }
        if(StringUtils.isNotBlank(vip_phone)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
        }
        if(StringUtils.isNotBlank(vip_card_type)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_card_type",new BasicDBObject("$regex",vip_card_type)));
        }
        if(StringUtils.isNotBlank(complete_start)){
            basicDBList_screen.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, complete_start+" 00:00:00")));
        }
        if(StringUtils.isNotBlank(complete_end)){
            basicDBList_screen.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, complete_end+" 23:59:59")));
        }
        basicDBObject.put("$and",basicDBList_screen);
        DBCursor dbCursor=dbCollection_schedule.find(basicDBObject).sort(new BasicDBObject("modified_date",-1));
        JSONArray obj_array=new JSONArray();
        while (dbCursor.hasNext()){
            DBObject dbobject= dbCursor.next();
            String complete_date=dbobject.get("modified_date").toString();//完成的时间
            String vip=dbobject.get("vip").toString();
            JSONObject vip_obj= JSON.parseObject(vip);//会员信息
            //答题数
            String schedule=dbobject.get("schedule").toString();
            JSONArray jsonArray=JSON.parseArray(schedule);

            List<String> list1=new ArrayList<String>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json=jsonArray.getJSONObject(i);
                String value=json.getString("value");
                String type=json.getString("type");
                if(StringUtils.isNotBlank(value)&&!type.equals("title")){
                    list1.add(value);
                }
            }
            int answer_num=list1.size(); //回答数


            String task=dbobject.get("task").toString();
            int qtNaire_id=JSON.parseObject(task).getInteger("qtNaire_id");
            Questionnaire questionnaire=questionnaireService.selectById(qtNaire_id);

            JSONObject obj=new JSONObject();
            obj.put("vip",vip_obj);
            obj.put("complete_date",complete_date);
            obj.put("answer_num",answer_num);
            obj.put("answer_info",jsonArray);
            obj.put("qtNaire_info",questionnaire);
            obj_array.add(obj);
        }
        //对答题数筛选
        JSONArray new_array=new JSONArray();
        for (int i = 0; i < obj_array.size(); i++) {
            JSONObject obj=obj_array.getJSONObject(i);
            int answer_num=obj.getInteger("answer_num");

            if(StringUtils.isNotBlank(answer_start)){
                if(answer_num<Integer.parseInt(answer_start)){
                    continue;
                }
            }
            if(StringUtils.isNotBlank(answer_end)){
                if(answer_num>Integer.parseInt(answer_end)){
                    continue;
                }
            }
            new_array.add(obj);
        }

        int end_row = 0;
        if (new_array.size() > page_num*page_size){
            end_row = page_num*page_size;
        }else {
            end_row = new_array.size();
        }

        JSONArray new_array_v2=new JSONArray();
        for (int i = (page_num-1)*page_size ; i < end_row; i++){
            new_array_v2.add(new_array.getJSONObject(i));
        }

        int count=new_array.size();
        int pages = 0;
        if (count % page_size == 0) {
            pages = count / page_size;
        } else {
            pages = count / page_size + 1;
        }
        boolean flag=true;
        if(page_num>=pages){
            flag=false;
        }
        JSONObject reslut=new JSONObject();
        reslut.put("list",new_array_v2);
        reslut.put("page_num",page_num);
        reslut.put("page_size",page_size);
        reslut.put("total",count);
        reslut.put("pages",pages);
        reslut.put("is_next",flag);
        return reslut;
    }

    /*****************************************分享次数********************************************************************/
    @Override
    public JSONObject getShareUrlRate(String message,HttpServletRequest request) throws Exception {

        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();

        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_shareUrl=mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
        DBCollection dbCollection_register=mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);
        DBCollection dbCollection_schedule=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);

        JSONObject json_message=JSON.parseObject(message);
        String task_code=json_message.getString("task_code");
        VipTask vipTask = vipTaskService.selectByTaskCode(task_code);
//        String task_start_time=vipTask.getStart_time().split(" ")[0];
//        String task_end_time=vipTask.getEnd_time().split(" ")[0];

        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBObject.put("vipTask.task_code",task_code);
        basicDBObject.put("vipTask.task_type","share_counts");
        DBCursor dbCursor=dbCollection_shareUrl.find(basicDBObject);
        int share_url_num=dbCursor.count();//分享次数
        int share_vip_num=dbCollection_shareUrl.distinct("open_id",basicDBObject).size();//分享人数


        //目标会员数
//        JSONArray jsonArray= vipGroupService.vipScreen2Array(JSON.parseArray(vipTask.getTarget_vips()),vipTask.getCorp_code(),role_code, brand_code, area_code, store_code, user_code);
        DataBox databox=iceInterfaceService.vipScreenMethod2("1","1",vipTask.getCorp_code(),vipTask.getTarget_vips_(),null,null);
        String target_count="0";
        if (databox.status.toString().equals("SUCCESS")) {
            String result = databox.data.get("message").value;
            JSONObject result_obj = JSONObject.parseObject(result);
            target_count=result_obj.getString("count");
        }

        //完成人数
        BasicDBObject basicDBObject_schedule=new BasicDBObject();
        basicDBObject_schedule.put("task.task_code",task_code);
        basicDBObject_schedule.put("status","1");
        DBCursor dbCursor1=dbCollection_schedule.find(basicDBObject_schedule);
        int complete_vip_num=dbCursor1.count();//完成的会员数

        JSONObject result=new JSONObject();
        result.put("share_url_num",share_url_num);//分享次数
        result.put("share_vip_num",share_vip_num);//分享人数
       // result.put("click_url_num",0);//点击次数
        result.put("vip_target_count",target_count);
        result.put("complete_vip_num",complete_vip_num);
        return result;
    }

    @Override
    public JSONObject getShareUrlChart(String message) throws Exception {

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_shareUrl=mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
        DBCollection dbCollection_schedule=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);

        JSONObject json_message=JSON.parseObject(message);
        String start_date=json_message.getString("start_time");
        String end_date=json_message.getString("end_time");
        String task_code=json_message.getString("task_code");
        VipTask vipTask = vipTaskService.selectByTaskCode(task_code);
        String task_start_time=vipTask.getStart_time().split(" ")[0];
        String task_end_time=vipTask.getEnd_time().split(" ")[0];
       // System.out.println("task_start_time:  "+task_start_time+"   "+"  task_end_time :"+task_end_time);

        if(StringUtils.isNotBlank(start_date)&&StringUtils.isBlank(end_date)){
            end_date=task_end_time;
        }else if(StringUtils.isBlank(start_date)&&StringUtils.isNotBlank(end_date)){
            start_date=task_start_time;
        }else if(StringUtils.isBlank(start_date)&&StringUtils.isBlank(end_date)){
            end_date=simpleDateFormat.format(new Date());
            if(end_date.compareTo(task_end_time)>0){
                end_date=task_end_time;
       //         System.out.println("start_date:   "+start_date+"   end_date:   "+end_date);
            }
            if(end_date.compareTo(task_start_time)<0){
                end_date=task_start_time;
       //         System.out.println("start_date:   "+start_date+"   end_date:   "+end_date);
            }
            Date date= TimeUtils.getNextDay(end_date,7);
            start_date=simpleDateFormat.format(date);
            if(start_date.compareTo(task_start_time)<0){
                start_date=task_start_time;
       //         System.out.println("start_date:   "+start_date+"   end_date:   "+end_date);
            }
        }

     //   System.out.println("start_date:   "+start_date+"   end_date:   "+end_date);
        JSONArray result_array=new JSONArray();
        List<String> list=TimeUtils.getBetweenDates(start_date,end_date);
        for (int i = 0; i < list.size(); i++) {
            Pattern pattern = Pattern.compile("^.*" + list.get(i) + ".*$", Pattern.CASE_INSENSITIVE);

            BasicDBObject basicDBObject=new BasicDBObject();
            BasicDBList basicurl_list=new BasicDBList();
            basicurl_list.add(new BasicDBObject("vipTask.task_code",task_code));
            basicurl_list.add(new BasicDBObject("vipTask.task_type","share_counts"));
            basicurl_list.add(new BasicDBObject("share_time",pattern));
            basicDBObject.put("$and",basicurl_list);

            DBCursor dbCursor=dbCollection_shareUrl.find(basicDBObject);
            int share_url_num=dbCursor.count();//分享次数
            int share_vip_num=dbCollection_shareUrl.distinct("open_id",basicDBObject).size();//分享人数


            BasicDBObject basicDBObject_schedule=new BasicDBObject();
            basicDBObject_schedule.put("modified_date",pattern);
            basicDBObject_schedule.put("task.task_code",task_code);
            basicDBObject_schedule.put("status","1");
            DBCursor dbCursor1=dbCollection_schedule.find(basicDBObject_schedule);
            int complete_vip_num=dbCursor1.count();//完成的会员数


            JSONObject result=new JSONObject();
            result.put("share_url_num",share_url_num);
            result.put("share_vip_num",share_vip_num);
            result.put("click_url_num",0);
            result.put("complete_vip_num",complete_vip_num);
            result.put("time",list.get(i));
            result_array.add(result);
        }

        JSONObject  time_obj=new JSONObject();
        time_obj.put("list",result_array);
        return time_obj;
    }

    //分享次数大于等1
    @Override
    public JSONObject getShareUrlList(String message) throws Exception {
        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection dbCollection_shareUrl=mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
        DBCollection dbCollection_schedule=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);

        JSONObject message_obj=JSON.parseObject(message);
        int page_num=message_obj.getInteger("page_num");
        int page_size=message_obj.getInteger("page_size");
        String task_code=message_obj.getString("task_code");
        String screen=message_obj.getString("screen");
        JSONObject screen_obj=JSON.parseObject(screen);
        String vip_name=screen_obj.getString("vip_name");
        String vip_cardno=screen_obj.getString("vip_cardno");
        String vip_phone=screen_obj.getString("vip_phone");
        String status_screen=screen_obj.getString("status");
        String complete_start=JSONArray.parseObject(screen_obj.getString("complete_date")).getString("start");
        String complete_end=JSONArray.parseObject(screen_obj.getString("complete_date")).getString("end");
        String share_start=JSON.parseObject(screen_obj.getString("share_count")).getString("start");
        String share_end=JSON.parseObject(screen_obj.getString("share_count")).getString("end");
//
//        String click_vip_start=JSONObject.parseObject(screen_obj.getString("click_vip")).getString("start");
//        String click_vip_end=JSONObject.parseObject(screen_obj.getString("click_vip")).getString("end");
//
//        String click_num_start=JSONObject.parseObject(screen_obj.getString("click_num")).getString("start");
//        String click_num_end=JSONObject.parseObject(screen_obj.getString("click_num")).getString("end");

//        VipTask vipTask=vipTaskService.selectByTaskCode(task_code);
//        String task_start_time=vipTask.getStart_time().split(" ")[0];
//        String task_end_time=vipTask.getEnd_time().split(" ")[0];

        BasicDBObject  basicDBObject=new BasicDBObject();
        BasicDBList basicDBList_screen=new BasicDBList();
        basicDBList_screen.add(new BasicDBObject("task.task_code",task_code));
        if(StringUtils.isNotBlank(vip_name)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
        }
        if(StringUtils.isNotBlank(vip_cardno)){
            basicDBList_screen.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",vip_cardno)));
        }
        if(StringUtils.isNotBlank(vip_phone)){
            basicDBList_screen.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
        }
        if(StringUtils.isNotBlank(complete_start)){
            basicDBList_screen.add(new BasicDBObject("complete_date", new BasicDBObject(QueryOperators.GTE, complete_start+" 00:00:00")));
        }
        if(StringUtils.isNotBlank(complete_end)){
            basicDBList_screen.add(new BasicDBObject("complete_date", new BasicDBObject(QueryOperators.LTE, complete_end+" 23:59:59")));
        }
        if(StringUtils.isNotBlank(status_screen)){
            basicDBList_screen.add(new BasicDBObject("status",status_screen));
        }
        basicDBObject.put("$and",basicDBList_screen);
        DBCursor dbCursor=dbCollection_schedule.find(basicDBObject).sort(new BasicDBObject("modified_date",-1));

        JSONArray vip_info_array=new JSONArray();
        while (dbCursor.hasNext()){

            DBObject dbObject= dbCursor.next();
            String vip_info= dbObject.get("vip").toString();
            JSONObject vip_obj=JSON.parseObject(vip_info);
            String open_id=vip_obj.getString("open_id");

            String complete_date=dbObject.get("modified_date").toString();
            String status=dbObject.get("status").toString();
            if(status.equals("0")){
                status="否";
            }else{
                status="是";
            }
            BasicDBObject basicDBObject_share=new BasicDBObject();
            basicDBObject_share.put("vipTask.task_code",task_code);
            basicDBObject_share.put("vipTask.task_type","share_counts");
            basicDBObject_share.put("open_id",open_id);
            DBCursor dbCursor_share=dbCollection_shareUrl.find(basicDBObject_share);
            int share_url_num=dbCursor_share.count();//分享次数

            JSONObject vip_info_obj=new JSONObject();
            vip_info_obj.put("vip_info",vip_obj);
            vip_info_obj.put("share_url_num",share_url_num);
           // vip_info_obj.put("click_url_num",0);//点击次数
           // vip_info_obj.put("click_vip_num",0);//点击人数
            vip_info_obj.put("status",status);
            if(status.equals("是")) {
                vip_info_obj.put("complete_date", complete_date);
            }else{
                vip_info_obj.put("complete_date", "");
            }

            vip_info_array.add(vip_info_obj);
        }
        JSONArray new_array=new JSONArray();
        for (int i = 0; i < vip_info_array.size(); i++) {
            JSONObject obj=vip_info_array.getJSONObject(i);
            int share_num=obj.getInteger("share_url_num");
          //  int click_url_num=obj.getInteger("click_url_num");
          //  int click_vip_num=obj.getInteger("click_vip_num");
            if(share_num==0){
                continue;
            }
            if(StringUtils.isNotBlank(share_start)){
                if(share_num<Integer.parseInt(share_start)){
                    continue;
                }
            }
            if(StringUtils.isNotBlank(share_end)){
                if(share_num>Integer.parseInt(share_end)){
                    continue;
                }
            }

//            if(StringUtils.isNotBlank(click_num_start)){
//                if(click_url_num<Integer.parseInt(click_num_start)){
//                    continue;
//                }
//            }
//            if(StringUtils.isNotBlank(click_num_end)){
//                if(click_url_num>Integer.parseInt(click_num_end)){
//                    continue;
//                }
//            }
//
//            if(StringUtils.isNotBlank(click_vip_start)){
//                if(click_vip_num<Integer.parseInt(click_vip_start)){
//                    continue;
//                }
//            }
//            if(StringUtils.isNotBlank(click_vip_end)){
//                if(click_vip_num>Integer.parseInt(click_vip_end)){
//                    continue;
//                }
//            }


            new_array.add(obj);
        }

        int end_row = 0;
        if (new_array.size() > page_num*page_size){
            end_row = page_num*page_size;
        }else {
            end_row = new_array.size();
        }

        JSONArray new_array_v2=new JSONArray();
        for (int i = (page_num-1)*page_size ; i < end_row; i++){
            new_array_v2.add(new_array.getJSONObject(i));
        }

        int count=new_array.size();
        int pages = 0;
        if (count % page_size == 0) {
            pages = count / page_size;
        } else {
            pages = count / page_size + 1;
        }
        boolean flag=true;
        if(page_num>=pages){
            flag=false;
        }
        JSONObject reslut=new JSONObject();
        reslut.put("list",new_array_v2);
        reslut.put("page_num",page_num);
        reslut.put("page_size",page_size);
        reslut.put("total",count);
        reslut.put("pages",pages);
        reslut.put("is_next",flag);
        return reslut;
    }
}
