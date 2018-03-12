package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.*;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by yanyadong on 2017/3/8.
 */
@Service
public class ActivityAnalyServiceImpl implements ActivityAnalyService {

    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    VipActivityService vipActivityService;
    @Autowired
    TaskService taskService;
    @Autowired
    UserService userService;
    @Autowired
    StoreService storeService;
    @Autowired
    AreaService areaService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    VipCardTypeService vipCardTypeService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    VipActivityDetailService vipActivityDetailService;
    @Autowired
    CorpService corpService;

    private static final Logger logger = Logger.getLogger(ActivityAnalyServiceImpl.class);

    //任务完成度

    /**
     * 获取活动任务执行情况
     */
    @Override
    public JSONObject executeDetail(String corp_code, String activity_code, String task_code) throws Exception {
        JSONObject result = new JSONObject();
        VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
        String run_scope = vipActivity.getRun_scope();

        //目标会员数
        String target_vips = vipActivity.getTarget_vips();
        String target_vips_count = "";
        DataBox dataBox = vipGroupService.vipScreenBySolr(JSONArray.parseArray(target_vips), corp_code, "1", "3", Common.ROLE_GM, "", "", "", "","","");
        if (dataBox.status.toString().equals("SUCCESS")) {
            String message1 = dataBox.data.get("message").value;
            JSONObject msg_obj = JSONObject.parseObject(message1);
            target_vips_count = msg_obj.getInteger("count")+"";
        }

        Double complete_vip_count = 0d;
        JSONArray task_array = new JSONArray();
        Task task = taskService.getTaskForId(corp_code, task_code);
        List<TaskAllocation> taskAllocations = taskService.selTaskAllocation(corp_code, task_code);

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_allocation);

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("corp_code", corp_code);
        dbObject.put("task_code", task_code);
        for (int i = 0; i < taskAllocations.size(); i++) {
            JSONObject task_obj = new JSONObject();
            String user_code = taskAllocations.get(i).getUser_code();
            String user_name = taskAllocations.get(i).getUser_name();
            String store_code = taskAllocations.get(i).getStore_code();
//            String store_name = taskAllocations.get(i).getStore_name();

            String store_name = "";
            if (!store_code.isEmpty()){
                Store store = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
                if (store != null)
                    store_name = store.getStore_name();

            }
            dbObject.put("user_code", user_code);
            DBCursor dbCursor = cursor.find(dbObject);
            String complete_rate = "0";
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                if (obj.containsField("vips") && (obj.get("vips").toString().equals("") || obj.get("vips").toString().equals("[]"))) {
                    complete_rate = "100";
                } else if (obj.containsField("complete_rate")) {
                    complete_rate = obj.get("complete_rate").toString();
                }
                if (obj.containsField("complete_vip_count")) {
                    String user_complete_vip_count = obj.get("complete_vip_count").toString();
                    complete_vip_count = complete_vip_count + Double.parseDouble(user_complete_vip_count);
                }
            }
            task_obj.put("user_code", user_code);
            task_obj.put("user_name", user_name);
            task_obj.put("complete_rate", complete_rate);
            task_obj.put("store_name", store_name);
            task_obj.put("store_code", store_code);
            task_array.add(task_obj);
        }
        List<User> userList= getActivityUserCode(corp_code,run_scope);

        result.put("userList", task_array);
        result.put("user_count", String.valueOf(userList.size()));
        result.put("target_vips_count", target_vips_count);
        result.put("complete_vips_count", complete_vip_count);
        result.put("taskInfo", JSON.toJSONString(task));
        return result;
    }


    /**
     * 获取活动任务执行情况(柱状图)
     */
    @Override
    public JSONArray taskCompleteDetail(String corp_code, String activity_code, String task_code) throws Exception {
        VipActivity activity = vipActivityService.getActivityByCode(activity_code);
        String start_time = activity.getCreated_date();
        String now_time = Common.DATETIME_FORMAT_DAY.format(new Date());
        int days = TimeUtils.calculateDate(now_time, start_time, Common.DATETIME_FORMAT_DAY);

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_allocation);

        BasicDBObject unwind = new BasicDBObject("$unwind", "$vips");
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("vips.status", "Y");
        dbObject.put("task_code", task_code);
        JSONArray array = new JSONArray();
        for (int i = 0; i < days + 1; i++) {
            JSONObject obj = new JSONObject();
            String date = Common.DATETIME_FORMAT_DAY.format(TimeUtils.getNextDay(start_time, i));
            Pattern pattern = Pattern.compile("^.*" + date + ".*$", Pattern.CASE_INSENSITIVE);
            dbObject.put("vips.date", pattern);
            BasicDBObject match = new BasicDBObject("$match", dbObject);
            AggregationOutput output = cursor.aggregate(unwind, match);
            Iterable<DBObject> it = output.results();
            List<DBObject> list = WebUtils.IteratorToList(it);

            obj.put("date", date);
            obj.put("complete_count", list.size());
            array.add(obj);
        }
        return array;
    }

    /**
     * 查看员工执行详情
     *
     * @param corp_code
     * @param task_code
     * @param user_code
     * @return
     * @throws Exception
     */
    public JSONArray userExecuteDetail(String corp_code, String task_code, String user_code) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_allocation);
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("task_code", task_code);
        dbObject.put("corp_code", corp_code);
        dbObject.put("user_code", user_code);
        DBCursor dbCursor = cursor.find(dbObject);
        JSONArray list = new JSONArray();
        if (dbCursor.size() > 0) {
            DBObject obj = dbCursor.next();
            if (obj.containsField("vips")) {
                String vips = obj.get("vips").toString();
                vips = vips.replace("~!@#$%&*","无");
                list = JSONArray.parseArray(vips);
            }
        }
        return list;
    }


    //导购影响力占比
    @Override
    public JSONObject getShareSize(String message) throws Exception {

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_share_log);

        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBObject.put("activity_code",activity_code);
        //已分享导购数
        int shareUsers= cursor.distinct("user_code",basicDBObject).size();
        //下达导购数
        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        String corp_code=activityVip.getCorp_code();
        String run_scope = activityVip.getRun_scope();


       List<User> userList= getActivityUserCode(corp_code,run_scope);
        JSONObject result=new JSONObject();
        result.put("shareUsers",shareUsers);
        result.put("users",userList.size());
        return  result;
    }

    //导购影响力分享图表
    @Override
    public JSONObject getShareViewByDate(String message) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_share_log);
        DBCursor dbCursor=null;
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();

        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        //获取活动的开始时间
        String[] start_time=activityVip.getStart_time().split(" ");
        String startTime=start_time[0];
        //获取活动的结束时间
        String[] end_time=activityVip.getEnd_time().split(" ");
        String endTime=end_time[0];
        long currentTime=System.currentTimeMillis();
        String current= TimeUtils.getTime(currentTime,new SimpleDateFormat("yyyy-MM-dd"));
        if(current.compareTo(endTime.toString())<0){
           endTime=current;
        }

        //获取两个时间段之间的所有日期(包含开始时间，结束时间)
        List<String> date= TimeUtils.getBetweenDates(startTime,endTime);

        if(endTime.compareTo(startTime)<0){
            date=new ArrayList<String>();
        }
        //获取分享集合
        JSONArray jsonArray=new JSONArray();

        int count=0;
        for(int i=0;i<date.size();i++) {
            BasicDBObject basicDBObject = new BasicDBObject();
            BasicDBList basicDBList = new BasicDBList();
            basicDBList.add(new BasicDBObject("activity_code", activity_code));
            //模糊时间匹配
            Pattern pattern = Pattern.compile("^.*" + date.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBList.add(new BasicDBObject("created_date",pattern));
            basicDBObject.put("$and", basicDBList);
            int shareCount = cursor.find(basicDBObject).count();
            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("date",date.get(i));
            jsonObject1.put("count",shareCount);
            jsonArray.add(jsonObject1);
            count+=shareCount;
        }
        JSONObject result=new JSONObject();
        result.put("list",jsonArray);
        result.put("count",count);
        return  result;
    }

    //导购影响力点击图表
    @Override
    public JSONObject getClickViewByDate(String message) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_openUrl_log);
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        //获取活动的开始时间
        String[] start_time=activityVip.getStart_time().split(" ");
        String startTime=start_time[0];
        //获取活动的结束时间
        String[] end_time=activityVip.getEnd_time().split(" ");
        String endTime=end_time[0];
        long currentTime=System.currentTimeMillis();
        String current= TimeUtils.getTime(currentTime,new SimpleDateFormat("yyyy-MM-dd"));
        if(current.compareTo(endTime.toString())<0){
            endTime=current;
        }
        //获取两个时间段之间的所有日期(包含开始时间，结束时间)
        List<String> date= TimeUtils.getBetweenDates(startTime,endTime);
        if(endTime.compareTo(startTime)<0){
            date=new ArrayList<String>();
        }
        //获取点击集合
        JSONArray jsonArray=new JSONArray();
        int count=0;
        for(int i=0;i<date.size();i++) {
            BasicDBObject basicDBObject = new BasicDBObject();
            BasicDBList basicDBList = new BasicDBList();
            basicDBList.add(new BasicDBObject("activity_code", activity_code));
            //模糊时间匹配
            Pattern pattern = Pattern.compile("^.*" + date.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBList.add(new BasicDBObject("created_date",pattern));
            basicDBObject.put("$and", basicDBList);
            int clickCount = cursor.find(basicDBObject).count();
            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("date",date.get(i));
            jsonObject1.put("count",clickCount);
            jsonArray.add(jsonObject1);
            count+=clickCount;
        }
        JSONObject result=new JSONObject();
       result.put("list",jsonArray);
        result.put("count",count);
        return  result;
    }

   //导购影响力列表
    @Override
    public JSONObject getUserList(String message) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_share_log);
        DBCollection cursor1 = mongoTemplate.getCollection(CommonValue.table_vip_activity_openUrl_log);
        DBCursor dbCursor = null;
        JSONObject jsonObject = JSONObject.parseObject(message);

        int page_size = 100;
        int page_num = 1;
        if (jsonObject.containsKey("page_size"))
            page_size = jsonObject.getInteger("page_size");
        if (jsonObject.containsKey("page_num"))
            page_num = jsonObject.getInteger("page_num");

        String activity_code = jsonObject.get("activity_code").toString();
        VipActivity vipActivity=vipActivityService.getActivityByCode(activity_code);
        String corp_code=vipActivity.getCorp_code();

        //筛选条件
        String storeCode_screen=jsonObject.get("store_code").toString().toLowerCase();
        String storeName_screen=jsonObject.get("store_name").toString().toLowerCase();
        String userCode_screen=jsonObject.get("user_code").toString().toLowerCase();
        String userName_screen=jsonObject.get("user_name").toString().toLowerCase();

//        JSONArray userArray=getUserTargrtVips(corp_code,activity_code,vipActivity.getRun_scope(),vipActivity.getTarget_vips());

//        JSONArray userArray_screen=new JSONArray();
//        for (int i = 0; i <users.size() ; i++) {
//            JSONObject json_screen=userArray.getJSONObject(i);
//            String store_code1=json_screen.get("store_code").toString().toLowerCase();
//            String user_code1=json_screen.get("user_code").toString().toLowerCase();
//            String user_name1=json_screen.get("user_name").toString().toLowerCase();
//            String store_name1=json_screen.get("store_name").toString().toLowerCase();
//            if(store_code1.contains(storeCode_screen)&&store_name1.contains(storeName_screen)&&
//                    user_code1.contains(userCode_screen)&&user_name1.contains(userName_screen)){
//                userArray_screen.add(json_screen);
//            }
//        }
//        userArray=userArray_screen;
//
//        List<HashMap<String,Object>> list=new ArrayList<HashMap<String, Object>>();
//        int end_row;
//        if (userArray.size() > page_num*page_size){
//            end_row = page_num*page_size;
//        }else {
//            end_row = userArray.size();
//        }
//        for (int i = (page_num-1)*page_size ; i < end_row; i++) {
//              JSONObject userJson=userArray.getJSONObject(i);
//              String store_code=userJson.get("store_code").toString();
//              String user_code=userJson.get("user_code").toString();
//              String user_name=userJson.get("user_name").toString();
//              String store_name=userJson.get("store_name").toString();
//              BasicDBList basicDBList = new BasicDBList();
//              BasicDBObject dbObject = new BasicDBObject();
//              basicDBList.add(new BasicDBObject("activity_code", activity_code));
//              basicDBList.add(new BasicDBObject("user_code",user_code));
//              dbObject.put("$and", basicDBList);
//              int sharCount= cursor.find(dbObject).count();
//              HashMap<String, Object> map = new HashMap<String, Object>();
//              //获取分享数
//              map.put("shareUrlCount", sharCount);
//              dbCursor = cursor1.find(dbObject);
//              int clickCount = dbCursor.count();
//              //点击数
//              map.put("clickCount", clickCount);
//              //员工信息
//              map.put("user_name",user_name);
//              map.put("user_code",user_code);
//              map.put("store_code",store_code);
//              map.put("store_name",store_name);
//              //..........
//              BasicDBObject basicDBForVip = new BasicDBObject();
//              BasicDBList basicListForVip = new BasicDBList();
//              basicListForVip.add(new BasicDBObject("activity_code", activity_code));
//              basicListForVip.add(new BasicDBObject("user_code", user_code));
//              basicListForVip.add(new BasicDBObject("vip",new BasicDBObject("$ne",null)));
//              basicDBForVip.put("$and", basicListForVip);
//              int vipCount = cursor1.distinct("open_id",basicDBForVip).size();
//              //当前员工下 打开链接的会员数
//              map.put("vipCount", vipCount);
//              //当前员工下 会员打开链接的总次数
//              int urlVipCount=cursor1.find(basicDBForVip).count();
//              map.put("urlVipCount",urlVipCount);
//              //..............
//              BasicDBObject basicNoVip = new BasicDBObject();
//              BasicDBList noVipList = new BasicDBList();
//              noVipList.add(new BasicDBObject("activity_code", activity_code));
//              noVipList.add(new BasicDBObject("user_code", user_code));
//              noVipList.add(new BasicDBObject("vip",null));
//              basicNoVip.put("$and", noVipList);
//              int noVipCount = cursor1.distinct("open_id",basicNoVip).size();
//              //当前员工下 打开链接的非会员数
//              map.put("noVipCount", noVipCount);
//              //当前员工下  非会员打开链接的总次数
//              int urlNoVipCount=cursor1.find(basicNoVip).count();
//              map.put("urlNoVipCount",urlNoVipCount);
//
//              list.add(map);
//          }
//        int count=userArray.size();
        PageInfo<User> users = getActivityUserCode(corp_code,vipActivity.getRun_scope(),storeCode_screen,storeName_screen,userCode_screen,userName_screen,page_num,page_size);

        List<HashMap<String,Object>> list=new ArrayList<HashMap<String, Object>>();
        List<User> users1 = users.getList();
        for (int i = 0 ; i < users1.size() ; i++) {
            String store_code=users1.get(i).getStore_code();
            String store_name = users1.get(i).getStore_name();
            String user_code=users1.get(i).getUser_code();
            String user_name = users1.get(i).getUser_name();

            BasicDBList basicDBList = new BasicDBList();
            BasicDBObject dbObject = new BasicDBObject();
            basicDBList.add(new BasicDBObject("activity_code", activity_code));
            basicDBList.add(new BasicDBObject("user_code",user_code));
            dbObject.put("$and", basicDBList);
            int sharCount= cursor.find(dbObject).count();
            HashMap<String, Object> map = new HashMap<String, Object>();
            //获取分享数
            map.put("shareUrlCount", sharCount);
            dbCursor = cursor1.find(dbObject);
            int clickCount = dbCursor.count();
            //点击数
            map.put("clickCount", clickCount);
            //员工信息
            map.put("user_name",user_name);
            map.put("user_code",user_code);
            map.put("store_code",store_code);
            map.put("store_name",store_name);
            //..........
            BasicDBObject basicDBForVip = new BasicDBObject();
            BasicDBList basicListForVip = new BasicDBList();
            basicListForVip.add(new BasicDBObject("activity_code", activity_code));
            basicListForVip.add(new BasicDBObject("user_code", user_code));
            basicListForVip.add(new BasicDBObject("vip",new BasicDBObject("$ne",null)));
            basicDBForVip.put("$and", basicListForVip);
            int vipCount = cursor1.distinct("open_id",basicDBForVip).size();
            //当前员工下 打开链接的会员数
            map.put("vipCount", vipCount);
            //当前员工下 会员打开链接的总次数
            int urlVipCount=cursor1.find(basicDBForVip).count();
            map.put("urlVipCount",urlVipCount);
            //..............
            BasicDBObject basicNoVip = new BasicDBObject();
            BasicDBList noVipList = new BasicDBList();
            noVipList.add(new BasicDBObject("activity_code", activity_code));
            noVipList.add(new BasicDBObject("user_code", user_code));
            noVipList.add(new BasicDBObject("vip",null));
            basicNoVip.put("$and", noVipList);
            int noVipCount = cursor1.distinct("open_id",basicNoVip).size();
            //当前员工下 打开链接的非会员数
            map.put("noVipCount", noVipCount);
            //当前员工下  非会员打开链接的总次数
            int urlNoVipCount=cursor1.find(basicNoVip).count();
            map.put("urlNoVipCount",urlNoVipCount);

            list.add(map);
        }
        int pages = users.getPages();
        int count = (int)users.getTotal();
        JSONObject result=new JSONObject();
        result.put("list",list);
        result.put("total",count);
        result.put("page_num",page_num);
        result.put("page_size",page_size);
        result.put("pages",pages);
        result.put("hasNext",users.isHasNextPage());
        return result;
    }


    //...........................意向分析........................................

    //意向客户统计图表
    @Override
    public JSONArray getClickViewByDateForUser(String message) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_openUrl_log);
        DBCursor dbCursor=null;
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();

        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        //获取活动的开始时间
        String[] start_time=activityVip.getStart_time().split(" ");
        String startTime=start_time[0];
        //获取活动的结束时间
        String[] end_time=activityVip.getEnd_time().split(" ");
        String endTime=end_time[0];
        long currentTime=System.currentTimeMillis();
        String current= TimeUtils.getTime(currentTime,new SimpleDateFormat("yyyy-MM-dd"));
        if(current.compareTo(endTime.toString())<0){
            endTime=current;
        }
        //获取两个时间段之间的所有日期(包含开始时间，结束时间)
        List<String> date= TimeUtils.getBetweenDates(startTime,endTime);

        if(endTime.compareTo(startTime)<0){
            date=new ArrayList<String>();
        }
        //获取点击集合
        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<date.size();i++) {
            BasicDBObject basicDBObject = new BasicDBObject();
            BasicDBList basicDBList = new BasicDBList();
            basicDBList.add(new BasicDBObject("activity_code", activity_code));
            basicDBList.add(new BasicDBObject("vip",new BasicDBObject("$ne",null)));
            //模糊时间匹配
            Pattern pattern = Pattern.compile("" + date.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBList.add(new BasicDBObject("created_date",pattern));
            basicDBObject.put("$and", basicDBList);
            int clickCount = cursor.distinct("open_id",basicDBObject).size();
            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("date",date.get(i));
            jsonObject1.put("count",clickCount);
            jsonArray.add(jsonObject1);
        }
        return  jsonArray;
    }
    //获取目标会员数，意向会员数，链接点击数
    @Override
    public JSONObject getIntentionSize(String message) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_openUrl_log);
        DBCursor dbCursor=null;

        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        String corp_code = activityVip.getCorp_code();
        //目标会员数
        String target_vips = activityVip.getTarget_vips();
        int target_vip_count = 0;
        DataBox dataBox = vipGroupService.vipScreenBySolr(JSONArray.parseArray(target_vips), corp_code, "1", "3", Common.ROLE_GM, "", "", "", "","","");
        if (dataBox.status.toString().equals("SUCCESS")) {
            String message1 = dataBox.data.get("message").value;
            JSONObject msg_obj = JSONObject.parseObject(message1);
            target_vip_count = msg_obj.getInteger("count");
        }
        //链接点击数
        dbCursor=cursor.find(new BasicDBObject("activity_code",activity_code));
        int count=dbCursor.count();
        //意向会员数
        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBObject.put("activity_code",activity_code);
        basicDBObject.put("vip",new BasicDBObject("$ne",null));
        int vipCount=cursor.distinct("open_id",basicDBObject).size();
        JSONObject result=new JSONObject();
        result.put("target_vips",target_vip_count+"");
        result.put("intentionCount",vipCount);
        result.put("clickCount",count);

        return  result;
    }

    //意向分析列表
    @Override
    public JSONObject getIntentionList(String message) throws Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_openUrl_log);
        JSONObject jsonObject = JSONObject.parseObject(message);

        int page_size = 100;
        int page_num = 1;
        if (jsonObject.containsKey("page_size"))
            page_size = jsonObject.getInteger("page_size");
        if (jsonObject.containsKey("page_num"))
            page_num = jsonObject.getInteger("page_num");

        String activity_code=jsonObject.get("activity_code").toString();
        String storeCode_screen=jsonObject.get("store_code").toString().toLowerCase();
        String storeName_screen=jsonObject.get("store_name").toString().toLowerCase();
        String userCode_screen=jsonObject.get("user_code").toString().toLowerCase();
        String userName_screen=jsonObject.get("user_name").toString().toLowerCase();
        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        String corp_code=activityVip.getCorp_code();
        String run_scope=activityVip.getRun_scope();
        String target_vip=activityVip.getTarget_vips();
        String type=jsonObject.get("type").toString();
        JSONObject result=new JSONObject();
        List<HashMap<String,Object>> listByStore=new ArrayList<HashMap<String, Object>>();
        if(type.equals("store")){
            List<Store> stores = vipActivityService.getActivityStore(corp_code,run_scope);
            List<Store> store_screen=new ArrayList<Store>();
            //按店铺名称，店铺编号筛选
            for (int i = 0; i <stores.size() ; i++) {
                Store store=stores.get(i);
                if(store.getStore_name().toLowerCase().contains(storeName_screen)&&store.getStore_code().toLowerCase().contains(storeCode_screen)){
                 store_screen.add(store);
                }
            }
            stores=store_screen;
//            JSONArray targetJsonArray=getStoreTargrtVips(corp_code,activity_code,run_scope,target_vip);
            int end_row;
            if (stores.size() > page_num*page_size){
                end_row = page_num*page_size;
            }else {
                end_row = stores.size();
            }
            for (int i = (page_num-1)*page_size ; i < end_row; i++) {
                HashMap<String,Object> map=new HashMap<String, Object>();
                JSONObject jsonObject1 = getStoreTargrtVips1(corp_code,stores.get(i).getStore_code(),stores.get(i).getArea_code(),target_vip);
                String area_name=jsonObject1.get("area_name").toString();
                String area_code=jsonObject1.get("area_code").toString();
                String target_vip_count=jsonObject1.get("target_vip_count").toString();
                map.put("store_code",stores.get(i).getStore_code());
                map.put("store_name",stores.get(i).getStore_name());
                map.put("area_name",area_name);
                map.put("area_code",area_code);
                map.put("target_vips",target_vip_count);

                BasicDBObject basicDBObject1=new BasicDBObject();
                basicDBObject1.put("activity_code",activity_code);
                basicDBObject1.put("store_code",stores.get(i).getStore_code());
                basicDBObject1.put("vip",new BasicDBObject("$ne",null));
                BasicDBObject matchByopenId = new BasicDBObject("$match", basicDBObject1);
                 /* Group操作*/
                DBObject groupField = new BasicDBObject("_id","$open_id");
                groupField.put("count", new BasicDBObject("$sum", 1));
                DBObject group_openid = new BasicDBObject("$group", groupField);
                AggregationOutput output_openid = cursor.aggregate(matchByopenId, group_openid);
                List<String> list=new ArrayList<String>();
                //获取店铺下的所有会员open_id
                for(DBObject dbObject:output_openid.results()){
                    BasicDBObject basicDBObject2=(BasicDBObject)dbObject;
                    list.add(basicDBObject2.get("_id").toString());
                }
                //意向会员数
                map.put("intentionCount",list.size()+"");
                List<HashMap<String,String>> vipInfoList=new ArrayList<HashMap<String, String>>();
                for(int j=0;j<list.size();j++){
                    HashMap<String,String> vipMap=new HashMap<String, String>();
                    BasicDBObject basicOpen=new BasicDBObject();
                    basicOpen.put("open_id",list.get(j).toString());
                    basicOpen.put("activity_code",activity_code);
                    DBObject db= cursor.findOne(basicOpen);
                    String vipJson=db.get("vip").toString();
                    JSONObject vipJsonObject=JSON.parseObject(vipJson);
                    if(vipJsonObject.get("vip_name")==null){
                        vipMap.put("vip_name","");
                    }else{
                        vipMap.put("vip_name",vipJsonObject.get("vip_name").toString());
                    }
                    if(vipJsonObject.get("cardno")==null){
                        vipMap.put("cardno","");
                    }else{
                        vipMap.put("cardno",vipJsonObject.get("cardno").toString());
                    }
                    if(vipJsonObject.get("vip_phone")==null){
                        vipMap.put("vip_phone","");
                    }else{
                        vipMap.put("vip_phone",vipJsonObject.get("vip_phone").toString());
                    }
                    if(vipJsonObject.get("vip_card_type")==null){
                        vipMap.put("vip_card_type","");
                    }else{
                        vipMap.put("vip_card_type",vipJsonObject.get("vip_card_type").toString());
                    }
                    vipInfoList.add(vipMap);
                }
                map.put("vipList",vipInfoList);
                //意向会员占比
                if(Integer.parseInt(target_vip_count)==0){
                    map.put("proportion",0+"%");
                }else{
                    map.put("proportion", String.format("%.2f", (double) vipInfoList.size()/Integer.parseInt(target_vip_count)*100)+"%");
                }

                listByStore.add(map);
            }
            int count=stores.size();
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
            result.put("list",listByStore);
            result.put("total",count);
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("pages",pages);
            result.put("hasNext",flag);
        }else if(type.equals("user")) {
            //获取所有的user的意向占比

//            List<User> tempList=getActivityUserCode(corp_code,run_scope);
//            List<User> temp_screen=new ArrayList<User>();
//
//            //按员工名称，员工编号筛选
//            for (int i = 0; i <tempList.size() ; i++) {
//                User user=tempList.get(i);
//                if(user.getUser_name().toLowerCase().contains(userName_screen)&&user.getUser_code().toLowerCase().contains(userCode_screen)){
//                    temp_screen.add(user);
//                }
//            }
//            tempList=temp_screen;
//
//            List<HashMap<String, Object>> listByUser = new ArrayList<HashMap<String, Object>>();
//            int end_row;
//            if (tempList.size() > page_num*page_size){
//                end_row = page_num*page_size;
//            }else {
//                end_row = tempList.size();
//            }
//            for (int i = (page_num-1)*page_size ; i < end_row; i++) {
//                User user = tempList.get(i);
//                HashMap<String, Object> map = new HashMap<String, Object>();
//                JSONObject jsonObject1 = getUserTargrtVips1(corp_code,user.getUser_code(),user.getStore_code(),target_vip);
//                String store_code=jsonObject1.get("store_code").toString();
//                String store_name=jsonObject1.get("store_name").toString();
//                String target_vip_count=jsonObject1.get("target_vip_count").toString();
//                map.put("user_code", user.getUser_code());
//                map.put("user_name", user.getUser_name());
//                map.put("store_name", store_name);
//                map.put("store_code", store_code);
//                map.put("target_vips",target_vip_count);
//                BasicDBObject basicDBObject1 = new BasicDBObject();
//                basicDBObject1.put("activity_code", activity_code);
//                basicDBObject1.put("user_code", user.getUser_code());
//                basicDBObject1.put("vip", new BasicDBObject("$ne", null));
//                BasicDBObject matchByopenId = new BasicDBObject("$match", basicDBObject1);
//                 /* Group操作*/
//                DBObject groupField = new BasicDBObject("_id", "$open_id");
//                groupField.put("count", new BasicDBObject("$sum", 1));
//                DBObject group_openid = new BasicDBObject("$group", groupField);
//                AggregationOutput output_openid = cursor.aggregate(matchByopenId, group_openid);
//                List<String> list = new ArrayList<String>();
//                //获取导购下的所有会员open_id
//                for (DBObject dbObj : output_openid.results()) {
//                    BasicDBObject basicDBObject2 = (BasicDBObject) dbObj;
//                    list.add(basicDBObject2.get("_id").toString());
//                }
//                //意向会员数
//                map.put("intentionCount", list.size() + "");
//                List<HashMap<String, String>> vipInfoList = new ArrayList<HashMap<String, String>>();
//                for (int a = 0; a< list.size(); a++) {
//                    HashMap<String, String> vipMap = new HashMap<String, String>();
//                    BasicDBObject basicOpen = new BasicDBObject();
//                    basicOpen.put("open_id", list.get(a).toString());
//                    basicOpen.put("activity_code", activity_code);
//                    DBObject db = cursor.findOne(basicOpen);
//                    String vipJson = db.get("vip").toString();
//                    JSONObject vipJsonObject = JSON.parseObject(vipJson);
//                    if (vipJsonObject.get("vip_name") == null) {
//                        vipMap.put("vip_name", "");
//                    } else {
//                        vipMap.put("vip_name", vipJsonObject.get("vip_name").toString());
//                    }
//                    if (vipJsonObject.get("cardno") == null) {
//                        vipMap.put("cardno", "");
//                    } else {
//                        vipMap.put("cardno", vipJsonObject.get("cardno").toString());
//                    }
//                    if (vipJsonObject.get("vip_phone") == null) {
//                        vipMap.put("vip_phone", "");
//                    } else {
//                        vipMap.put("vip_phone", vipJsonObject.get("vip_phone").toString());
//                    }
//                    if (vipJsonObject.get("vip_card_type") == null) {
//                        vipMap.put("vip_card_type", "");
//                    } else {
//                        vipMap.put("vip_card_type", vipJsonObject.get("vip_card_type").toString());
//                    }
//                    vipInfoList.add(vipMap);
//                }
//                //导购下的会员信息
//                map.put("vipList", vipInfoList);
//                //意向会员占比
//                if(Integer.parseInt(target_vip_count)==0){
//                    map.put("proportion","0.0%");
//                }else{
//                    map.put("proportion", String.format("%.2f", (double) vipInfoList.size()/Integer.parseInt(target_vip_count)*100)+"%");
//                }
//                listByUser.add(map);
//            }
//            int count=tempList.size();
//            int pages = 0;
//            if (count % page_size == 0) {
//                pages = count / page_size;
//            } else {
//                pages = count / page_size + 1;
//            }
//            boolean flag=true;
//            if(page_num>=pages){
//                flag=false;
//            }
            List<HashMap<String, Object>> listByUser = new ArrayList<HashMap<String, Object>>();

            PageInfo<User> users = getActivityUserCode(corp_code,run_scope,"","",userCode_screen,userName_screen,page_num,page_size);
            List<User> users1 = users.getList();
            for (int i = 0 ; i < users1.size(); i++) {
                User user = users1.get(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                JSONObject jsonObject1 = getUserTargrtVips1(corp_code,user.getUser_code(),target_vip);
                String target_vip_count=jsonObject1.get("target_vip_count").toString();

                String store_code=user.getStore_code();
                String store_name=user.getStore_name();
                map.put("user_code", user.getUser_code());
                map.put("user_name", user.getUser_name());
                map.put("store_name", store_name);
                map.put("store_code", store_code);
                map.put("target_vips",target_vip_count);
                BasicDBObject basicDBObject1 = new BasicDBObject();
                basicDBObject1.put("activity_code", activity_code);
                basicDBObject1.put("user_code", user.getUser_code());
                basicDBObject1.put("vip", new BasicDBObject("$ne", null));
                BasicDBObject matchByopenId = new BasicDBObject("$match", basicDBObject1);
                 /* Group操作*/
                DBObject groupField = new BasicDBObject("_id", "$open_id");
                groupField.put("count", new BasicDBObject("$sum", 1));
                DBObject group_openid = new BasicDBObject("$group", groupField);
                AggregationOutput output_openid = cursor.aggregate(matchByopenId, group_openid);
                List<String> list = new ArrayList<String>();
                //获取导购下的所有会员open_id
                for (DBObject dbObj : output_openid.results()) {
                    BasicDBObject basicDBObject2 = (BasicDBObject) dbObj;
                    list.add(basicDBObject2.get("_id").toString());
                }
                //意向会员数
                map.put("intentionCount", list.size() + "");
                List<HashMap<String, String>> vipInfoList = new ArrayList<HashMap<String, String>>();
                for (int a = 0; a< list.size(); a++) {
                    HashMap<String, String> vipMap = new HashMap<String, String>();
                    BasicDBObject basicOpen = new BasicDBObject();
                    basicOpen.put("open_id", list.get(a).toString());
                    basicOpen.put("activity_code", activity_code);
                    DBObject db = cursor.findOne(basicOpen);
                    String vipJson = db.get("vip").toString();
                    JSONObject vipJsonObject = JSON.parseObject(vipJson);
                    if (vipJsonObject.get("vip_name") == null) {
                        vipMap.put("vip_name", "");
                    } else {
                        vipMap.put("vip_name", vipJsonObject.get("vip_name").toString());
                    }
                    if (vipJsonObject.get("cardno") == null) {
                        vipMap.put("cardno", "");
                    } else {
                        vipMap.put("cardno", vipJsonObject.get("cardno").toString());
                    }
                    if (vipJsonObject.get("vip_phone") == null) {
                        vipMap.put("vip_phone", "");
                    } else {
                        vipMap.put("vip_phone", vipJsonObject.get("vip_phone").toString());
                    }
                    if (vipJsonObject.get("vip_card_type") == null) {
                        vipMap.put("vip_card_type", "");
                    } else {
                        vipMap.put("vip_card_type", vipJsonObject.get("vip_card_type").toString());
                    }
                    vipInfoList.add(vipMap);
                }
                //导购下的会员信息
                map.put("vipList", vipInfoList);
                //意向会员占比
                if(Integer.parseInt(target_vip_count)==0){
                    map.put("proportion","0.0%");
                }else{
                    map.put("proportion", String.format("%.2f", (double) vipInfoList.size()/Integer.parseInt(target_vip_count)*100)+"%");
                }
                listByUser.add(map);
            }
            result.put("list",listByUser);
            result.put("total",users.getTotal());
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("pages",users.getPages());
            result.put("hasNext",users.isHasNextPage());
        }
        return  result;
    }

    //................................优惠券...................................

    //目标会员数,使用会员数，领取会员数
    @Override
    public JSONObject getCouponSize(String message) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_coupon_log);
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        String corp_code = activityVip.getCorp_code();
        //目标会员数
        String target_vips = activityVip.getTarget_vips();
        int target_vip_count = 0;
        DataBox dataBox = vipGroupService.vipScreenBySolr(JSONArray.parseArray(target_vips), corp_code, "1", "3", Common.ROLE_GM, "", "", "", "","","");
        if (dataBox.status.toString().equals("SUCCESS")) {
            String message1 = dataBox.data.get("message").value;
            JSONObject msg_obj = JSONObject.parseObject(message1);
            target_vip_count = msg_obj.getInteger("count");
        }
        String batch_no=activityVip.getBatch_no();
        String app_id = activityVip.getApp_id();
        String start_time=activityVip.getStart_time().split(" ")[0];
        String end_time=activityVip.getEnd_time().split(" ")[0];
        long currentTime=System.currentTimeMillis();
        String current= TimeUtils.getTime(currentTime,new SimpleDateFormat("yyyy-MM-dd"));
        if(current.compareTo(end_time)<0){
            end_time=current;
        }

        String type_coupon_code=getTypeCode(activity_code);


        logger.info("===type_code"+type_coupon_code+"===batch_no"+batch_no);
        DataBox databox=iceInterfaceAPIService.activityAnalyCoupon(corp_code,type_coupon_code,batch_no,start_time,end_time,"","","","","rate");
        String value= databox.data.get("message").value;
        JSONObject object=JSON.parseObject(value);
        //领取数
        String bind_count= object.get("bind_count").toString();
        //使用数
        String verify_count=object.get("verify_count").toString();
        //使用占比
        double proportionUse=0.0;
        if(Integer.parseInt(bind_count)!=0){
            String proUse= String.format("%.2f",(double)Integer.parseInt(verify_count)/Integer.parseInt(bind_count)*100);
            proportionUse=Double.parseDouble(proUse);
        }
        //领取占比
        double proportionReceive=0.0;
        if(target_vip_count!=0){
            String proRece=String.format("%.2f",(double)Integer.parseInt(bind_count)/target_vip_count*100);
            proportionReceive=Double.parseDouble(proRece);
        }
        HashMap<String,String> map=new HashMap<String, String>();
        map.put("vip_number",target_vip_count+"");
        map.put("vip_receive",bind_count);
        map.put("vip_consume",verify_count);
        map.put("receive_ratio",proportionReceive+"%");
        map.put("consume_ratio",proportionUse+"%");
        JSONObject result=new JSONObject();
        result.put("message",map);
        return  result;
    }


    //优惠券图表
    public JSONObject getCouponView(String message) throws Exception{
        JSONObject jsonObject = JSONObject.parseObject(message);
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_coupon_log);
        String activity_code=jsonObject.get("activity_code").toString();
        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        String corp_code=activityVip.getCorp_code();
        String batch_no=activityVip.getBatch_no();
        String app_id = activityVip.getApp_id();
        String start_time=activityVip.getStart_time().split(" ")[0];
        String end_time=activityVip.getEnd_time().split(" ")[0];
        long currentTime=System.currentTimeMillis();
        String current= TimeUtils.getTime(currentTime,new SimpleDateFormat("yyyy-MM-dd"));
        if(current.compareTo(end_time)<0){
            end_time=current;
        }
        //type_code
//        VipActivityDetail vipActivityDetail=vipActivityDetailService.selActivityDetailByCode(activity_code);
//        String type_codes=vipActivityDetail.getCoupon_type();
//        String type_coupon_code = "";
//        if(type_codes!=null&&!type_codes.equals("")) {
//            JSONArray jsonArray = JSON.parseArray(type_codes);
//            for (int i = 0; i < jsonArray.size(); i++) {
//                type_coupon_code += jsonArray.getJSONObject(i).get("coupon_code").toString() + ",";
//            }
//        }
//        if(type_coupon_code.endsWith(",")){
//            type_coupon_code=type_coupon_code.substring(0,type_coupon_code.length()-1);
//        }
        String type_coupon_code=getTypeCode(activity_code);
        //store_id
        List<Store> stores = vipActivityService.getActivityStore(corp_code,activityVip.getRun_scope());
        String store_codes = "";
        for (int i = 0; i < stores.size(); i++) {
            store_codes = store_codes + stores.get(i).getStore_code() + ",";
        }
        if(store_codes.endsWith(",")){
            store_codes=store_codes.substring(0,store_codes.length()-1);
        }
        DataBox databox=iceInterfaceAPIService.activityAnalyCoupon(corp_code,type_coupon_code,batch_no,start_time,end_time,store_codes,"1","10","","chart");
          String data=databox.data.get("message").value;
        JSONObject result=new JSONObject();
        JSONObject jsonObject1=JSON.parseObject(data);

        JSONArray array = jsonObject1.getJSONArray("voucher_chat_list");
//        for (int i = 0; i < array.size(); i++) {
//            JSONObject obj = array.getJSONObject(i);
//            String day = obj.getString("time_id");
//            BasicDBObject db = new BasicDBObject();
//            db.put("params.batch_no",batch_no);
//            db.put("is_active","Y");
//            db.put("appid",app_id);
//            Pattern pattern = Pattern.compile("^.*" + day + ".*$", Pattern.CASE_INSENSITIVE);
//            db.put("send_time",pattern);
//            DBCursor dbObjects = cursor.find(db);
//            String bind_count = String.valueOf(dbObjects.count());
//            obj.put("bind_count",bind_count);
//        }
        result.put("list",jsonObject1);
        return result;
    }

    //按门店分组
    @Override
    public JSONObject getCouponList(String message) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        //筛选参数
        String store_code=jsonObject.get("store_code").toString();
        String coupon_code=jsonObject.get("coupon_code").toString();
        String page_num=jsonObject.getString("page_num");
        String page_size=jsonObject.getString("page_size");
        if (page_num == null || page_num.isEmpty())
            page_num = "1";
        if (page_size == null || page_size.isEmpty())
            page_size = "10000";

        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        String corp_code=activityVip.getCorp_code();
        String batch_no=activityVip.getBatch_no();
        String start_time=activityVip.getStart_time().split(" ")[0];
        String end_time=activityVip.getEnd_time().split(" ")[0];
        long currentTime=System.currentTimeMillis();
        String current= TimeUtils.getTime(currentTime,new SimpleDateFormat("yyyy-MM-dd"));
        if(current.compareTo(end_time)<0){
            end_time=current;
        }
        //type_code
//        VipActivityDetail vipActivityDetail=vipActivityDetailService.selActivityDetailByCode(activity_code);
//        String type_codes=vipActivityDetail.getCoupon_type();
//        String type_coupon_code="";
//        if(type_codes!=null&&!type_codes.equals("")) {
//            JSONArray jsonArray=JSON.parseArray(type_codes);
//            for (int i = 0; i < jsonArray.size(); i++) {
//                type_coupon_code += jsonArray.getJSONObject(i).get("coupon_code").toString() + ",";
//            }
//        }
//        if(type_coupon_code.endsWith(",")){
//            type_coupon_code=type_coupon_code.substring(0,type_coupon_code.length()-1);
//        }
        String type_coupon_code=getTypeCode(activity_code);
        if(type_coupon_code.endsWith(",")){
            type_coupon_code=type_coupon_code.substring(0,type_coupon_code.length()-1);
        }
        List<HashMap<String,Object>> coupon_list=getCouponByActivityCode1(activity_code);
        String coupon_list_js=JSON.toJSONString(coupon_list);
        //store_id
        List<Store> stores = vipActivityService.getActivityStore(corp_code,activityVip.getRun_scope());
        String store_codes = "";
        for (int i = 0; i < stores.size(); i++) {
            store_codes = store_codes + stores.get(i).getStore_code() + ",";
        }
        if(store_codes.endsWith(",")){
            store_codes=store_codes.substring(0,store_codes.length()-1);
        }
        if(!store_code.equals("")&&store_code!=null) {
            store_codes = store_code;
        }
        if(!coupon_code.equals("")&&coupon_code!=null) {
            type_coupon_code = coupon_code;
        }
        DataBox databox=iceInterfaceAPIService.activityAnalyCoupon(corp_code,type_coupon_code,batch_no,start_time,end_time,store_codes,page_num,page_size,coupon_list_js,"list");
        String data=databox.data.get("message").value;
        JSONObject result=JSON.parseObject(data);
        return result;
    }
    //...............................通知传播分析...........................................

    //会员卡类型点击次数
    @Override
    public JSONObject noticeAnalyByCard(String message) throws Exception {

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_openUrl_log);

        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code = jsonObject.get("activity_code").toString();
        String corp_code = vipActivityService.getActivityByCode(activity_code).getCorp_code().toString();
        //分次数查询
        BasicDBList basicDBList1=new BasicDBList();
        BasicDBList basicDBList2=new BasicDBList();
        BasicDBList basicDBList3=new BasicDBList();
        BasicDBList basicDBList4=new BasicDBList();
        BasicDBList basicDBList5=new BasicDBList();
        BasicDBList basicDBListMore=new BasicDBList();
        //查询会员卡类型
//        List<VipCardType> vipCardTypeList=vipCardTypeService.getVipCardTypes(corp_code,"Y");
        BasicDBList basicDBList = new BasicDBList();
        BasicDBObject dbObject = new BasicDBObject();
        basicDBList.add(new BasicDBObject("activity_code", activity_code));
        basicDBList.add(new BasicDBObject("vip",new BasicDBObject("$ne",null)));
        dbObject.put("$and", basicDBList);
        BasicDBObject match = new BasicDBObject("$match", dbObject);
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("_id", "$open_id");
        basicDBObject.put("count", new BasicDBObject("$sum", 1));
        BasicDBObject group = new BasicDBObject("$group", basicDBObject);
        AggregationOutput output = cursor.aggregate(match, group);
        for(DBObject obj:output.results()){
            BasicDBObject dbObject1=(BasicDBObject)obj;
            String open_id=dbObject1.get("_id").toString();
            int count=Integer.parseInt(dbObject1.get("count").toString());
            if(count==1){
                basicDBList1.add(open_id);
            }else if(count==2){
                basicDBList2.add(open_id);
            }else if(count==3){
                basicDBList3.add(open_id);
            }else  if(count==4){
                basicDBList4.add(open_id);
            }else if(count==5){
                basicDBList5.add(open_id);
            }else if(count>5){
                basicDBListMore.add(open_id);
            }
        }

        BasicDBObject dbObject1 = new BasicDBObject();
        dbObject1.put("activity_code", activity_code);
        dbObject1.put("vip",new BasicDBObject("$ne",null));
        BasicDBObject match1 = new BasicDBObject("$match", dbObject1);
        BasicDBObject basicDBObject1 = new BasicDBObject();
        basicDBObject1.put("_id", "$vip.vip_card_type");
        basicDBObject1.put("count", new BasicDBObject("$sum", 1));
        BasicDBObject group1 = new BasicDBObject("$group", basicDBObject1);
        AggregationOutput output1 = cursor.aggregate(match1, group1);
        List<HashMap<String,String>> card_type_list = new ArrayList<HashMap<String, String>>();
        for(DBObject obj:output1.results()){
            BasicDBObject Object=(BasicDBObject)obj;
            HashMap<String,String> card_type_map = new HashMap<String, String>();
            card_type_map.put("card_name",Object.get("_id").toString());
            card_type_map.put("count",Object.get("count").toString());
            card_type_list.add(card_type_map);
        }
        if (card_type_list.size() > 6)
            card_type_list = getCardList(card_type_list);

        JSONArray array = new JSONArray();
        for (int i = 0; i < card_type_list.size(); i++) {
            JSONObject obj = new JSONObject();
            String vip_card_type_name = card_type_list.get(i).get("card_name").toString();
            obj.put("vip_card_type_name",vip_card_type_name);
            if (!vip_card_type_name.contains("其他")){
                int openOne=getNoticeActivity(activity_code,basicDBList1,cursor,vip_card_type_name);
                int openTwo=getNoticeActivity(activity_code,basicDBList2,cursor,vip_card_type_name);
                int openThree=getNoticeActivity(activity_code,basicDBList3,cursor,vip_card_type_name);
                int openFour=getNoticeActivity(activity_code,basicDBList4,cursor,vip_card_type_name);
                int openFive=getNoticeActivity(activity_code,basicDBList5,cursor,vip_card_type_name);
                int openMore=getNoticeActivity(activity_code,basicDBListMore,cursor,vip_card_type_name);
                HashMap<String,Integer> map = new HashMap<String, Integer>();
                map.put("one",openOne);
                map.put("two",openTwo);
                map.put("three",openThree);
                map.put("four",openFour);
                map.put("five",openFive);
                map.put("more",openMore);
                obj.put("count",map);
                array.add(obj);
            }
        }
        JSONObject noVipList=getNoticeActivityNumber(cursor,activity_code);
        array.add(noVipList);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("list",array);
        return jsonObject1;
    }


    @Override
    public JSONObject noticeAnalyByStore1(String message) throws Exception {

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_openUrl_log);

        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code = jsonObject.get("activity_code").toString();

        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);

        List<Store> stores = vipActivityService.getActivityStore(activityVip.getCorp_code(),activityVip.getRun_scope());

        List<HashMap<String,String>> list=new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < stores.size(); i++) {
            HashMap<String,String> map=new HashMap<String, String>();
            String store_code = stores.get(i).getStore_code();
            String store_name = stores.get(i).getStore_name();
            BasicDBObject dbObject = new BasicDBObject();
            dbObject.put("activity_code", activity_code);
            dbObject.put("store_code", store_code);
            BasicDBObject match = new BasicDBObject("$match", dbObject);
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put("_id", "$open_id");
            basicDBObject.put("count", new BasicDBObject("$sum", 1));
            BasicDBObject group = new BasicDBObject("$group", basicDBObject);
            AggregationOutput output = cursor.aggregate(match, group);
            BasicDBList basicDBList1=new BasicDBList();
            BasicDBList basicDBList2=new BasicDBList();
            BasicDBList basicDBList3=new BasicDBList();
            BasicDBList basicDBList4=new BasicDBList();
            BasicDBList basicDBList5=new BasicDBList();
            BasicDBList basicDBListMore=new BasicDBList();
            for(DBObject obj:output.results()){
                BasicDBObject basicDBObject1=(BasicDBObject)obj;
                String open_id=basicDBObject1.get("_id").toString();
                int count=Integer.parseInt(basicDBObject1.get("count").toString());
                if(count==1){
                    basicDBList1.add(open_id);
                }else if(count==2){
                    basicDBList2.add(open_id);
                }else if(count==3){
                    basicDBList3.add(open_id);
                }else  if(count==4){
                    basicDBList4.add(open_id);
                }else if(count==5){
                    basicDBList5.add(open_id);
                }else if(count>5){
                    basicDBListMore.add(open_id);
                }
            }
            map.put("storeCode",store_code);
            map.put("storeName",store_name);
            map.put("One",String.valueOf(basicDBList1.size()));
            map.put("Two",String.valueOf(basicDBList2.size()));
            map.put("Three",String.valueOf(basicDBList3.size()));
            map.put("Four",String.valueOf(basicDBList4.size()));
            map.put("Five",String.valueOf(basicDBList5.size()));
            map.put("More",String.valueOf(basicDBListMore.size()));
                        list.add(map);
        }
        JSONObject result=new JSONObject();
        result.put("list",list);
        return  result;
    }

    //店铺-会员卡类型点击次数统计
//    @Override
//    public JSONObject noticeAnaly_card_store(String message) throws Exception {
//
//        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_openUrl_log);
//        JSONObject jsonObject = JSONObject.parseObject(message);
//        String activity_code = jsonObject.get("activity_code").toString();
//        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
//        String corp_code=activityVip.getCorp_code();
//        String target_vip=activityVip.getTarget_vips();
//        JSONArray targetJsonArray=getStoreTargrtVips(corp_code,activity_code,activityVip.getRun_scope(),target_vip);
//        //查询会员卡类型(VIP不为空)
//        List<VipCardType> vipCardTypeList=vipCardTypeService.getVipCardTypes(corp_code,"Y","");
//        List<HashMap<String,Object>> mapList=new ArrayList<HashMap<String, Object>>();
//        for(int i=0;i<targetJsonArray.size();i++){
//            HashMap<String,Object> hashMap=new HashMap<String, Object>();
//            JSONObject jsonStore=(JSONObject)targetJsonArray.get(i);
//            String store_code=jsonStore.get("store_code").toString();
//            String store_name=jsonStore.get("store_name").toString();
//            List<HashMap<String,String>> list=new ArrayList<HashMap<String, String>>();
//            for(int j=0;j<vipCardTypeList.size();j++){
//                HashMap<String,String> cardTypeMap=new HashMap<String, String>();
//                BasicDBObject basicDBObject1=new BasicDBObject();
//                basicDBObject1.put("activity_code",activity_code);
//                basicDBObject1.put("store_code",store_code);
//                basicDBObject1.put("vip",new BasicDBObject("$ne",null));
//                basicDBObject1.put("vip.vip_card_type",vipCardTypeList.get(j).getVip_card_type_code());
//                int count=cursor.distinct("open_id",basicDBObject1).size();
//                cardTypeMap.put("vip_card_name",vipCardTypeList.get(j).getVip_card_type_name());
//                cardTypeMap.put("count",count+"");
//                list.add(cardTypeMap);
//            }
//            if(list.size()>6){
//               list=getCardList(list);
//            }
//            //非会员
//            BasicDBObject basicDBObject2=new BasicDBObject();
//            basicDBObject2.put("activity_code",activity_code);
//            basicDBObject2.put("store_code",store_code);
//            basicDBObject2.put("vip",null);
//            int count=cursor.distinct("open_id",basicDBObject2).size();
//            HashMap<String,String> noVipMap=new HashMap<String, String>();
//            noVipMap.put("vip_card_name","非会员");
//            noVipMap.put("count",count+"");
//            list.add(noVipMap);
//
//            hashMap.put("cardType",list);
//            hashMap.put("storeName",store_name);
//            mapList.add(hashMap);
//        }
//        JSONObject jsonObject1=new JSONObject();
//        jsonObject1.put("list",mapList);
//        return jsonObject1;
//    }


    //按导购/店铺查看
    public JSONObject noticeAnalyList1(String message) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_openUrl_log);
        DBCollection cursorShare = mongoTemplate.getCollection(CommonValue.table_vip_share_log);
        JSONObject jsonObject = JSONObject.parseObject(message);
        int page_size = 100;
        int page_num = 1;
        if (jsonObject.containsKey("page_size"))
            page_size = jsonObject.getInteger("page_size");
        if (jsonObject.containsKey("page_num"))
            page_num = jsonObject.getInteger("page_num");
        String activity_code = jsonObject.get("activity_code").toString();
        String store_name_screen = jsonObject.get("store_name").toString().toLowerCase();
        String user_name_screen= jsonObject.get("user_name").toString().toLowerCase();
        String user_code_screen = "";
        if (jsonObject.containsKey("user_code"))
            user_code_screen= jsonObject.get("user_code").toString().toLowerCase();

        String type=jsonObject.get("type").toString();
        JSONObject result=new JSONObject();
        //分次数查询
        if(type.equals("store")){
            //查询活动下的参与店铺
            VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
            List<Store> stores = vipActivityService.getActivityStore(activityVip.getCorp_code(),activityVip.getRun_scope());
            List<Store> store_screen=new ArrayList<Store>();
            //筛选
            for (int i = 0; i <stores.size() ; i++) {
                Store store=stores.get(i);
                if(store.getStore_name().toLowerCase().contains(store_name_screen)){
                    store_screen.add(store);
                }
            }
            stores=store_screen;

            int end_row = 0;
            if (stores.size() > page_num*page_size){
                end_row = page_num*page_size;
            }else {
                end_row = stores.size();
            }
            List<HashMap<String,String>> list=new ArrayList<HashMap<String, String>>();
            for (int i = (page_num-1)*page_size ; i < end_row; i++) {
                HashMap<String,String> map=new HashMap<String, String>();
                String store_code = stores.get(i).getStore_code();
                String store_name = stores.get(i).getStore_name();
                BasicDBObject dbObject = new BasicDBObject();
                dbObject.put("activity_code", activity_code);
                dbObject.put("store_code", store_code);
                BasicDBObject match = new BasicDBObject("$match", dbObject);
                BasicDBObject basicDBObject = new BasicDBObject();
                basicDBObject.put("_id", "$open_id");
                basicDBObject.put("count", new BasicDBObject("$sum", 1));
                BasicDBObject group = new BasicDBObject("$group", basicDBObject);
                AggregationOutput output = cursor.aggregate(match, group);

                BasicDBList basicDBList1=new BasicDBList();
                BasicDBList basicDBList2=new BasicDBList();
                BasicDBList basicDBList3=new BasicDBList();
                BasicDBList basicDBList4=new BasicDBList();
                BasicDBList basicDBList5=new BasicDBList();
                BasicDBList basicDBListMore=new BasicDBList();
                for(DBObject obj:output.results()){
                    BasicDBObject basicDBObject1=(BasicDBObject)obj;
                    String open_id=basicDBObject1.get("_id").toString();
                    int count=Integer.parseInt(basicDBObject1.get("count").toString());
                    if(count==1){
                        basicDBList1.add(open_id);
                    }else if(count==2){
                        basicDBList2.add(open_id);
                    }else if(count==3){
                        basicDBList3.add(open_id);
                    }else  if(count==4){
                        basicDBList4.add(open_id);
                    }else if(count==5){
                        basicDBList5.add(open_id);
                    }else if(count>5){
                        basicDBListMore.add(open_id);
                    }
                }
                map.put("storeCode",store_code);
                map.put("storeName",store_name);
                map.put("One",String.valueOf(basicDBList1.size()));
                map.put("Two",String.valueOf(basicDBList2.size()));
                map.put("Three",String.valueOf(basicDBList3.size()));
                map.put("Four",String.valueOf(basicDBList4.size()));
                map.put("Five",String.valueOf(basicDBList5.size()));
                map.put("More",String.valueOf(basicDBListMore.size()));

                BasicDBObject basicShare=new BasicDBObject();
                basicShare.put("activity_code",activity_code);
                basicShare.put("store_code",store_code);
                //此店铺下分享总数
                int shareCount=cursorShare.find(basicShare).count();
                //此店铺下链接总点击数
                int  openCount=cursor.find(basicShare).count();
                map.put("shareCount",shareCount+"");
                map.put("openCount",openCount+"");
                list.add(map);
            }
            int count=stores.size();
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
            result.put("list",list);
            result.put("total",count);
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("pages",pages);
            result.put("hasNext",flag);
        }else if(type.equals("user")){
            //下达导购数
            VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
            String corp_code=activityVip.getCorp_code();
//            List<User> userList = getActivityUserCode(corp_code,activityVip.getRun_scope());
//            List<User> user_screen=new ArrayList<User>();
//            //筛选
//            for (int i = 0; i <userList.size() ; i++) {
//                User user=userList.get(i);
//                if(user.getUser_name().toLowerCase().contains(user_name_screen)){
//                    user_screen.add(user);
//                }
//            }
//            userList=user_screen;
//
//            int end_row = 0;
//            if (userList.size() > page_num*page_size){
//                end_row = page_num*page_size;
//            }else {
//                end_row = userList.size();
//            }
//            List<HashMap<String,String>> list=new ArrayList<HashMap<String, String>>();
//            for (int i = (page_num-1)*page_size ; i < end_row; i++) {
//                HashMap<String,String> map=new HashMap<String, String>();
//                String user_code = userList.get(i).getUser_code();
//                String user_name = userList.get(i).getUser_name();
//
//                //筛选
//                BasicDBObject dbObject = new BasicDBObject();
//                dbObject.put("activity_code", activity_code);
//                dbObject.put("user_code", user_code);
//                BasicDBObject match = new BasicDBObject("$match", dbObject);
//                BasicDBObject basicDBObject = new BasicDBObject();
//                basicDBObject.put("_id", "$open_id");
//                basicDBObject.put("count", new BasicDBObject("$sum", 1));
//                BasicDBObject group = new BasicDBObject("$group", basicDBObject);
//                AggregationOutput output = cursor.aggregate(match, group);
//
//                BasicDBList basicDBList1=new BasicDBList();
//                BasicDBList basicDBList2=new BasicDBList();
//                BasicDBList basicDBList3=new BasicDBList();
//                BasicDBList basicDBList4=new BasicDBList();
//                BasicDBList basicDBList5=new BasicDBList();
//                BasicDBList basicDBListMore=new BasicDBList();
//
//                for(DBObject obj:output.results()){
//                    BasicDBObject basicDBObject1=(BasicDBObject)obj;
//                    String open_id=basicDBObject1.get("_id").toString();
//                    int count=Integer.parseInt(basicDBObject1.get("count").toString());
//                    if(count==1){
//                        basicDBList1.add(open_id);
//                    }else if(count==2){
//                        basicDBList2.add(open_id);
//                    }else if(count==3){
//                        basicDBList3.add(open_id);
//                    }else  if(count==4){
//                        basicDBList4.add(open_id);
//                    }else if(count==5){
//                        basicDBList4.add(open_id);
//                    }else if(count>5){
//                        basicDBListMore.add(open_id);
//                    }
//                }
//
//                map.put("userCode",user_code);
//                map.put("userName",user_name);
//                map.put("One",String.valueOf(basicDBList1.size()));
//                map.put("Two",String.valueOf(basicDBList2.size()));
//                map.put("Three",String.valueOf(basicDBList3.size()));
//                map.put("Four",String.valueOf(basicDBList4.size()));
//                map.put("Five",String.valueOf(basicDBList5.size()));
//                map.put("More",String.valueOf(basicDBListMore.size()));
//                BasicDBObject basicShare=new BasicDBObject();
//                basicShare.put("activity_code",activity_code);
//                basicShare.put("user_code",user_code);
//                //此导购下分享总数
//                int shareCount=cursorShare.find(basicShare).count();
//                //此导购下链接总点击数
//                int  openCount=cursor.find(basicShare).count();
//                map.put("shareCount",shareCount+"");
//                map.put("openCount",openCount+"");
//                list.add(map);
//            }
//            int count=userList.size();
//            int pages = 0;
//            if (count % page_size == 0) {
//                pages = count / page_size;
//            } else {
//                pages = count / page_size + 1;
//            }
//            boolean flag=true;
//            if(page_num>=pages){
//                flag=false;
//            }
            List<HashMap<String,String>> list=new ArrayList<HashMap<String, String>>();

            PageInfo<User> users = getActivityUserCode(corp_code,activityVip.getRun_scope(),"","",user_code_screen,user_name_screen,page_num,page_size);
            List<User> userList = users.getList();
            for (int i = 0 ; i < userList.size(); i++) {
                HashMap<String,String> map=new HashMap<String, String>();
                String user_code = userList.get(i).getUser_code();
                String user_name = userList.get(i).getUser_name();

                //筛选
                BasicDBObject dbObject = new BasicDBObject();
                dbObject.put("activity_code", activity_code);
                dbObject.put("user_code", user_code);
                BasicDBObject match = new BasicDBObject("$match", dbObject);
                BasicDBObject basicDBObject = new BasicDBObject();
                basicDBObject.put("_id", "$open_id");
                basicDBObject.put("count", new BasicDBObject("$sum", 1));
                BasicDBObject group = new BasicDBObject("$group", basicDBObject);
                AggregationOutput output = cursor.aggregate(match, group);

                BasicDBList basicDBList1=new BasicDBList();
                BasicDBList basicDBList2=new BasicDBList();
                BasicDBList basicDBList3=new BasicDBList();
                BasicDBList basicDBList4=new BasicDBList();
                BasicDBList basicDBList5=new BasicDBList();
                BasicDBList basicDBListMore=new BasicDBList();

                for(DBObject obj:output.results()){
                    BasicDBObject basicDBObject1=(BasicDBObject)obj;
                    String open_id=basicDBObject1.get("_id").toString();
                    int count=Integer.parseInt(basicDBObject1.get("count").toString());
                    if(count==1){
                        basicDBList1.add(open_id);
                    }else if(count==2){
                        basicDBList2.add(open_id);
                    }else if(count==3){
                        basicDBList3.add(open_id);
                    }else  if(count==4){
                        basicDBList4.add(open_id);
                    }else if(count==5){
                        basicDBList4.add(open_id);
                    }else if(count>5){
                        basicDBListMore.add(open_id);
                    }
                }

                map.put("userCode",user_code);
                map.put("userName",user_name);
                map.put("One",String.valueOf(basicDBList1.size()));
                map.put("Two",String.valueOf(basicDBList2.size()));
                map.put("Three",String.valueOf(basicDBList3.size()));
                map.put("Four",String.valueOf(basicDBList4.size()));
                map.put("Five",String.valueOf(basicDBList5.size()));
                map.put("More",String.valueOf(basicDBListMore.size()));
                BasicDBObject basicShare=new BasicDBObject();
                basicShare.put("activity_code",activity_code);
                basicShare.put("user_code",user_code);
                //此导购下分享总数
                int shareCount=cursorShare.find(basicShare).count();
                //此导购下链接总点击数
                int  openCount=cursor.find(basicShare).count();
                map.put("shareCount",shareCount+"");
                map.put("openCount",openCount+"");
                list.add(map);
            }

            result.put("list",list);
            result.put("total",users.getTotal());
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("pages",users.getPages());
            result.put("hasNext",users.isHasNextPage());
        }
        return  result;
    }

    //...............................业绩占比............................................
    //占比
    public  JSONObject getSalesRate(String message) throws Exception{
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        //目标会员数
        String run_mode = activityVip.getRun_mode();
        String corp_code=activityVip.getCorp_code();
        String start_time=activityVip.getStart_time().split(" ")[0];
        String end_time="";
        String[] endTime=activityVip.getEnd_time().split(" ");
        //大于14点 算当天业绩，小于14点不算当天业绩
        if(endTime[1].split(":")[0].compareTo("14")<0){
            end_time= Common.DATETIME_FORMAT_DAY.format(TimeUtils.getNextDay(endTime[0],1));
        }else {
            end_time=endTime[0];
        }
        String bathch_no=activityVip.getBatch_no();
        //获取优惠券编号
        String type_code=getTypeCode(activity_code);

        DataBox dataBox=iceInterfaceAPIService.activityAnalySalesRate("0","",type_code,bathch_no,start_time,end_time,corp_code,activity_code,run_mode,"1","10","","rate");
        JSONObject result = null;
        if (dataBox.status.toString().equals("SUCCESS")){
            String value=dataBox.data.get("message").value.toString();
            result=JSON.parseObject(value);
        }
        return result;
    }

    //业绩图表
    public  JSONArray getSalesChart(String message) throws Exception{
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        //目标会员数
        String run_mode = activityVip.getRun_mode();
        String corp_code=activityVip.getCorp_code();
        String start_time=activityVip.getStart_time().split(" ")[0];
        String end_time=activityVip.getEnd_time();

        long currentTime=System.currentTimeMillis();
        String current= TimeUtils.getTime(currentTime,Common.DATETIME_FORMAT);
        if(current.compareTo(end_time)<0){
            end_time=current;
        }
        String[] endTime=end_time.split(" ");
        if (endTime[0].compareTo(start_time) == 0){
            end_time=endTime[0];
        }else {
            //大于14点 算当天业绩，小于14点不算当天业绩
            if(endTime[1].split(":")[0].compareTo("14")<0){
                end_time= Common.DATETIME_FORMAT_DAY.format(TimeUtils.getNextDay(endTime[0],1));
            }else {
                end_time=endTime[0];
            }
        }

        String bathch_no=activityVip.getBatch_no();
        //获取优惠券编号
        String type_code=getTypeCode(activity_code);
        DataBox dataBox=iceInterfaceAPIService.activityAnalySalesRate("0","",type_code,bathch_no,start_time,end_time,corp_code,activity_code,run_mode,"1","10","","chart");

        JSONArray result = new JSONArray();
        if (dataBox.status.toString().equals("SUCCESS")){
            String value=dataBox.data.get("message").value.toString();
            result=JSON.parseArray(value);
        }
        return result;
    }

    //业绩列表
    public JSONObject getSalesList(String message) throws  Exception{
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        String screen= jsonObject.getString("screen");
        String page_now=jsonObject.getString("pageNumber");
        String page_size=jsonObject.getString("pageSize");
        if (page_now == null || page_now.isEmpty())
            page_now = "1";
        if (page_size == null || page_size.isEmpty())
            page_size = "10000";
        String type=jsonObject.get("type").toString();
        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        String corp_code=activityVip.getCorp_code();
        String start_time=activityVip.getStart_time().split(" ")[0];
//        String end_time=activityVip.getEnd_time().split(" ")[0];
//        long currentTime=System.currentTimeMillis();
//        String current= TimeUtils.getTime(currentTime,new SimpleDateFormat("yyyy-MM-dd"));
//        if(current.compareTo(end_time)<0){
//            end_time=current;
//        }
        String end_time="";
        String[] endTime=activityVip.getEnd_time().split(" ");
        //大于14点 算当天业绩，小于14点不算当天业绩
        if(endTime[1].split(":")[0].compareTo("14")<0){
            end_time= Common.DATETIME_FORMAT_DAY.format(TimeUtils.getNextDay(endTime[0],1));
        }else {
            end_time=endTime[0];
        }

        String bathch_no=activityVip.getBatch_no();
        String run_mode = activityVip.getRun_mode();

//        VipActivityDetail vipActivityDetail=vipActivityDetailService.selActivityDetailByCode(activity_code);
//        String send_type_code=vipActivityDetail.getSend_coupon_type();
//        String coupon_type=vipActivityDetail.getCoupon_type();
        //获取优惠券编号
        String type_code=getTypeCode(activity_code);

        if(type.equals("store")){
            DataBox dataBox=iceInterfaceAPIService.activityAnalySalesRate("1","",type_code,bathch_no,start_time,end_time,corp_code,activity_code,run_mode,page_now,page_size,screen,"list");
            String value=dataBox.data.get("message").value;
            JSONObject jsonObject1=JSON.parseObject(value);
            return  jsonObject1;
        }else{
            DataBox dataBox=iceInterfaceAPIService.activityAnalySalesRate("2","",type_code,bathch_no,start_time,end_time,corp_code,activity_code,run_mode,page_now,page_size,screen,"list");
            String value=dataBox.data.get("message").value;
            JSONObject jsonObject1=JSON.parseObject(value);
            return  jsonObject1;
        }
    }

    //..........................会员影响力分析............................................

    //会员影响力占比
    public  JSONObject  getVipEffectRate(String message) throws Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        //分享链接表
        DBCollection cursor_share = mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
        //注册表
        DBCollection cursor_invite = mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);
        //邀请成功的会员
        DBCollection cursor_invite_success=mongoTemplate.getCollection(CommonValue.table_vip_activity_join_log);

        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        VipActivity vipActivity= vipActivityService.selActivityByCode(activity_code);
        String start= vipActivity.getStart_time().split(" ")[0];
        String end=vipActivity.getEnd_time().split(" ")[0];
        String app_id=vipActivity.getApp_id();
        //注册会员数
        BasicDBList  basicDBList=new BasicDBList();
        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBList.add(new BasicDBObject("app_id",app_id));
        basicDBList.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.GTE, start+" 00:00:00")));
        basicDBList.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.LTE, end+" 23:59:59")));
        basicDBObject.put("$and",basicDBList);
        int invite_num=cursor_invite.find(basicDBObject).count();
        //分享数
        BasicDBObject basicDBObject1=new BasicDBObject();
        basicDBObject1.put("app_id",app_id);
        BasicDBList basicDBList1 = new BasicDBList();
        basicDBList1.add(new BasicDBObject("share_time", new BasicDBObject(QueryOperators.GTE, start+" 00:00:00")));
        basicDBList1.add(new BasicDBObject("share_time", new BasicDBObject(QueryOperators.LTE, end+" 23:59:59")));
        basicDBObject1.put("$and",basicDBList1);
        BasicDBList basicDBList2 = new BasicDBList();
        basicDBList2.add(new BasicDBObject("vipTask.task_type","invite_registration"));
        basicDBList2.add(new BasicDBObject("vipTask.task_type","activity"));
        basicDBObject1.put("$or",basicDBList2);

        logger.info("========"+basicDBObject1.toString());
        int share_num=cursor_share.find(basicDBObject1).count();
        //邀请成功率
        String rate="0";
        if(share_num==0){
            rate="0%";
        }else{
           rate=String.format("%.2f", (double) invite_num/share_num*100)+"%";
        }
        JSONObject result=new JSONObject();
        result.put("invite_num",invite_num);//注册会员
        result.put("share_num",share_num);//邀请会员
        result.put("rate",rate);
        return result;
    }
    //会员影响力图表
    public  JSONObject getVipEffectChat(String message) throws Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        //分享链接表
        DBCollection cursor_share = mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
        //注册表
        DBCollection cursor_invite = mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);

        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        VipActivity vipActivity= vipActivityService.selActivityByCode(activity_code);
        String app_id=vipActivity.getApp_id();
        String start=vipActivity.getStart_time().split(" ")[0];
        String end=vipActivity.getEnd_time().split(" ")[0];
        long currentTime=System.currentTimeMillis();
        String current= TimeUtils.getTime(currentTime,new SimpleDateFormat("yyyy-MM-dd"));
        if(current.compareTo(end)<0){
            end=current;
        }

        //注册会员数
        BasicDBList  basicDBList_1=new BasicDBList();
        BasicDBObject basicDBObject_1=new BasicDBObject();
        basicDBList_1.add(new BasicDBObject("app_id",app_id));
        basicDBList_1.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.GTE, start+" 00:00:00")));
        basicDBList_1.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.LTE, end+" 23:59:59")));
        basicDBObject_1.put("$and",basicDBList_1);
        int invite_num=cursor_invite.find(basicDBObject_1).count();
        //分享数
        BasicDBObject basicDBObject1=new BasicDBObject();
        basicDBObject1.put("app_id",app_id);
        BasicDBList basicDBList3 = new BasicDBList();
        basicDBList3.add(new BasicDBObject("share_time", new BasicDBObject(QueryOperators.GTE, start+" 00:00:00")));
        basicDBList3.add(new BasicDBObject("share_time", new BasicDBObject(QueryOperators.LTE, end+" 23:59:59")));
        basicDBObject1.put("$and",basicDBList3);
        BasicDBList basicDBList2 = new BasicDBList();
        basicDBList2.add(new BasicDBObject("vipTask.task_type","invite_registration"));
        basicDBList2.add(new BasicDBObject("vipTask.task_type","activity"));
        basicDBObject1.put("$or",basicDBList2);

        int share_num=cursor_share.find(basicDBObject1).count();
        //获取两个时间段之间的所有日期(包含开始时间，结束时间)
        List<String> date= TimeUtils.getBetweenDates(start,end);
        if(end.compareTo(start)<0){
            date=new ArrayList<String>();
        }
        //注册会员日期.........................
        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<date.size();i++) {
            BasicDBObject basicDBObject3 = new BasicDBObject();
            BasicDBList basicDBList = new BasicDBList();
            basicDBList.add(new BasicDBObject("app_id", app_id));
            //模糊时间匹配
            Pattern pattern = Pattern.compile("^.*" + date.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBList.add(new BasicDBObject("register_time",pattern));
            basicDBObject3.put("$and", basicDBList);
            int invite_count = cursor_invite.find(basicDBObject3).count();
            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("date",date.get(i));
            jsonObject1.put("count",invite_count);
            jsonArray.add(jsonObject1);
        }
        //分享 日期...................................
        JSONArray jsonArray1=new JSONArray();
        for(int i=0;i<date.size();i++) {
            Pattern pattern = Pattern.compile("^.*" + date.get(i) + ".*$", Pattern.CASE_INSENSITIVE);

            BasicDBObject basicDBObject_4=new BasicDBObject();
            basicDBObject_4.put("app_id",app_id);
            basicDBObject_4.put("share_time",pattern);
            BasicDBList basicDBList1 = new BasicDBList();
            basicDBList1.add(new BasicDBObject("vipTask.task_type","invite_registration"));
            basicDBList1.add(new BasicDBObject("vipTask.task_type","activity"));
            basicDBObject_4.put("$or",basicDBList1);
            int share_count = cursor_share.find(basicDBObject_4).count();
            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("date",date.get(i));
            jsonObject1.put("count",share_count);
            jsonArray1.add(jsonObject1);
        }
        JSONObject result=new JSONObject();
        result.put("invite_num",invite_num);
        result.put("share_num",share_num);
        result.put("invite_chat",jsonArray);
        result.put("share_chat",jsonArray1);
        return result;
    }
    //列表
    public JSONObject getVipEffectList(String message) throws Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        //分享链接表
        DBCollection cursor_share = mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
        //注册表
        DBCollection cursor_invite = mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        int page_num=Integer.parseInt(jsonObject.getString("page_num"));
        int page_size=Integer.parseInt(jsonObject.getString("page_size"));
        String screen=jsonObject.getString("screen");
        JSONObject screenJs=JSON.parseObject(screen);
        String vip_name=screenJs.getString("vip_name");
        String cardno=screenJs.getString("cardno");
        String vip_phone=screenJs.getString("vip_phone");
        String vip_card_type=screenJs.getString("vip_card_type");
        VipActivity vipActivity= vipActivityService.selActivityByCode(activity_code);
        //活动开始时间，结束时间
        String start_time=vipActivity.getStart_time().split(" ")[0];
        String end_time=vipActivity.getEnd_time().split(" ")[0];
        String app_id=vipActivity.getApp_id();
        BasicDBObject basicDBObject=new BasicDBObject();
        if(!vip_name.equals("")){
            Pattern pattern = Pattern.compile("^.*" + vip_name + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBObject.put("vip.vip_name",pattern);
        }
        if(!cardno.equals("")){
            Pattern pattern = Pattern.compile("^.*" + cardno + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBObject.put("vip.cardno",pattern);
        }
        if(!vip_phone.equals("")){
            Pattern pattern = Pattern.compile("^.*" + vip_phone + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBObject.put("vip.vip_phone",pattern);
        }
        if(!vip_card_type.equals("")){
            Pattern pattern = Pattern.compile("^.*" + vip_card_type + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBObject.put("vip.vip_card_type",pattern);
        }
        //时间限制
        basicDBObject.put("share_time", new BasicDBObject(QueryOperators.GTE, start_time+" 00:00:00"));
        basicDBObject.put("share_time", new BasicDBObject(QueryOperators.LTE, end_time+" 23:59:59"));
        basicDBObject.put("app_id",app_id);

        BasicDBList basicDBList=new BasicDBList();
        basicDBList.add(new BasicDBObject("vipTask.task_type","invite_registration"));
        basicDBList.add(new BasicDBObject("vipTask.task_type","activity"));
        basicDBObject.put("$or",basicDBList);

        BasicDBObject match=new BasicDBObject("$match",basicDBObject);
         /* Group操作*/
        DBObject groupField = new BasicDBObject("_id", "$open_id");
        groupField.put("count", new BasicDBObject("$sum", 1));
        DBObject group= new BasicDBObject("$group", groupField);
        //分页
        BasicDBObject basicSkip=new BasicDBObject("$skip",(page_num - 1) * page_size);
        BasicDBObject basicLimit=new BasicDBObject("$limit",page_size);
        AggregationOutput output = cursor_share.aggregate(match, group,basicSkip,basicLimit);
        //总集合
        JSONArray objects=new JSONArray();
        for(DBObject dbObject:output.results()){
            //会员信息
            String open_id=dbObject.get("_id").toString();

            BasicDBObject basicDBObject1=new BasicDBObject();
            basicDBObject1.put("app_id",app_id);
            BasicDBList basicDBList3 = new BasicDBList();
            basicDBList3.add(new BasicDBObject("share_time", new BasicDBObject(QueryOperators.GTE, start_time+" 00:00:00")));
            basicDBList3.add(new BasicDBObject("share_time", new BasicDBObject(QueryOperators.LTE, end_time+" 23:59:59")));
            basicDBObject1.put("$and",basicDBList3);
            BasicDBList basicDBList2 = new BasicDBList();
            basicDBList2.add(new BasicDBObject("vipTask.task_type","invite_registration"));
            basicDBList2.add(new BasicDBObject("vipTask.task_type","activity"));
            basicDBObject1.put("$or",basicDBList2);

            //分享次数
            int  share_num=cursor_share.find(basicDBObject1).count();
            //会员信息
            DBObject dbObject1=cursor_share.findOne(basicDBObject1);
            String vipInfo=dbObject1.get("vip").toString();
            JSONObject jsonObject1=JSON.parseObject(vipInfo);

            //注册会员数
            BasicDBObject basicDBObject2=new BasicDBObject();
            BasicDBList basicDBList_2=new BasicDBList();
            basicDBList_2.add(new BasicDBObject("app_id",app_id));
            basicDBList_2.add(new BasicDBObject("invite_open_id",open_id));
            basicDBList_2.add(new BasicDBObject("register_time",new BasicDBObject(QueryOperators.GTE, start_time+" 00:00:00")));
            basicDBList_2.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.LTE, end_time+" 23:59:59")));
            basicDBObject2.put("$and",basicDBList_2);
            int invite_num= cursor_invite.find(basicDBObject2).count();

            JSONObject result=new JSONObject();
            result.put("share_num",share_num);
            result.put("invite_num",invite_num);
            result.put("vip_name",jsonObject1.getString("vip_name"));
            result.put("cardno",jsonObject1.getString("cardno"));
            result.put("vip_phone",jsonObject1.getString("vip_phone"));
            result.put("vip_card_type",jsonObject1.getString("vip_card_type"));
            result.put("open_id",open_id);
            objects.add(result);
        }
        System.out.println("====size======="+objects.size());
        JSONObject jsob=new JSONObject();
        BasicDBObject basicCount=new BasicDBObject();
        BasicDBList basicDBList_count=new BasicDBList();
        basicDBList_count.add(new BasicDBObject("share_time",new BasicDBObject(QueryOperators.GTE, start_time+" 00:00:00")));
        basicDBList_count.add(new BasicDBObject("share_time", new BasicDBObject(QueryOperators.LTE, end_time+" 23:59:59")));
        basicDBList_count.add(new BasicDBObject("app_id",app_id));
        basicDBList_count.add(new BasicDBObject("vipTask.task_type","invite_registration"));
        basicCount.put("$and",basicDBList_count);
        int count=cursor_share.distinct("open_id",basicCount).size();
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
        jsob.put("list",objects);
        jsob.put("total",count);
        jsob.put("page_num",page_num);
        jsob.put("page_size",page_size);
        jsob.put("pages",pages);
        jsob.put("hasNext",flag);
        jsob.put("app_id",app_id);
        return jsob;
    }

    //获取每个会员邀请成功的注册会员
    public JSONObject getRegisterList(String message)throws Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        //注册表
        DBCollection cursor_invite = mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);
        JSONObject jsonObject = JSONObject.parseObject(message);
        // String activity_code=jsonObject.get("activity_code").toString();
        String open_id=jsonObject.getString("open_id");
        String app_id=jsonObject.getString("app_id");
        String activity_code=jsonObject.getString("activity_code");
        //int page_num=Integer.parseInt(jsonObject.getString("page_num"));
        //int page_size=Integer.parseInt(jsonObject.getString("page_size"));
        VipActivity vipActivity= vipActivityService.selActivityByCode(activity_code);
        //String app_id=vipActivity.getApp_id();
        String start_tiem=vipActivity.getStart_time().split(" ")[0];
        String end_time=vipActivity.getEnd_time().split(" ")[0];
        BasicDBObject basicDBObject=new BasicDBObject();
        BasicDBList basicDBList=new BasicDBList();
        basicDBList.add(new BasicDBObject("app_id",app_id));
        basicDBList.add(new BasicDBObject("invite_open_id",open_id));
        basicDBList.add(new BasicDBObject("register_time",new BasicDBObject(QueryOperators.GTE, start_tiem+" 00:00:00")));
        basicDBList.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.LTE, end_time+" 23:59:59")));
        basicDBObject.put("$and",basicDBList);
        //注册详情
        List<HashMap<String,Object>> hashMapList=new ArrayList<HashMap<String, Object>>();
        DBCursor dbCursor=cursor_invite.find(basicDBObject);
        while (dbCursor.hasNext()){
            DBObject dbObject2=dbCursor.next();
            String register_vipInfo=dbObject2.get("register_vipInfo").toString();
            JSONObject jsonObject1=JSON.parseObject(register_vipInfo);
            String vip_id=jsonObject1.getString("VIP_ID");
            String CORP_ID=jsonObject1.getString("CORP_ID");
            DataBox dataBox= iceInterfaceService.getVipInfo(CORP_ID,vip_id);
            String vip_info= dataBox.data.get("message").value;
            JSONObject list_obj = JSONObject.parseObject(vip_info);
            JSONArray vips_array = list_obj.getJSONArray("vip_info");
            if(vips_array.size()>0) {
                HashMap<String,Object> map=new HashMap<String, Object>();
                JSONObject jsonObject2=vips_array.getJSONObject(0);
                for(String key:jsonObject2.keySet()){
                    map.put(key,jsonObject2.getString(key));
                }
                hashMapList.add(map);
            }
        }
        JSONObject result=new JSONObject();
        result.put("list",hashMapList);
        return result;
    }


    //列表
    public JSONObject getVipEffectList1(String message) throws Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        //分享链接表
        DBCollection cursor_share = mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
        //注册表
        DBCollection cursor_invite = mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);
        //邀请成功的会员
        DBCollection cursor_invite_success=mongoTemplate.getCollection(CommonValue.table_vip_activity_join_log);

        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        int page_num=Integer.parseInt(jsonObject.getString("page_num"));
        int page_size=Integer.parseInt(jsonObject.getString("page_size"));
        String screen=jsonObject.getString("screen");
        JSONObject screenJs=JSON.parseObject(screen);
        String vip_name=screenJs.getString("vip_name");
        String cardno=screenJs.getString("cardno");
        String vip_phone=screenJs.getString("vip_phone");
        String vip_card_type=screenJs.getString("vip_card_type");
        VipActivity vipActivity= vipActivityService.selActivityByCode(activity_code);
        //活动开始时间，结束时间
        String start_time=vipActivity.getStart_time().split(" ")[0];
        String end_time=vipActivity.getEnd_time().split(" ")[0];
        String app_id=vipActivity.getApp_id();
        BasicDBObject basicDBObject=new BasicDBObject();
        /**
         * 筛选条件
         */
        BasicDBList basicDBList=new BasicDBList();
        if(!vip_name.equals("")){
            Pattern pattern = Pattern.compile("^.*" + vip_name + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBList.add(new BasicDBObject("vip.vip_name",pattern));
        }
        if(!cardno.equals("")){
            Pattern pattern = Pattern.compile("^.*" + cardno + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBList.add(new BasicDBObject("vip.cardno",pattern));
        }
        if(!vip_phone.equals("")){
            Pattern pattern = Pattern.compile("^.*" + vip_phone + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBList.add(new BasicDBObject("vip.vip_phone",pattern));
        }
        if(!vip_card_type.equals("")){
            Pattern pattern = Pattern.compile("^.*" + vip_card_type + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBList.add(new BasicDBObject("vip.vip_card_type",pattern));
        }
        /**
         * 默认情况下参与该活动的会员
         */
        basicDBList.add(new BasicDBObject("activity_code",activity_code));
        //basicDBList.add(new BasicDBObject("app_id",app_id));
        basicDBObject.put("$and",basicDBList);
        BasicDBObject match=new BasicDBObject("$match",basicDBObject);
         /* Group操作*/
        DBObject groupField = new BasicDBObject("_id", "$vip.open_id");
        groupField.put("count", new BasicDBObject("$sum", 1));
        DBObject group= new BasicDBObject("$group", groupField);
        //分页
        BasicDBObject basicSkip=new BasicDBObject("$skip",(page_num - 1) * page_size);
        BasicDBObject basicLimit=new BasicDBObject("$limit",page_size);
        AggregationOutput output = cursor_invite_success.aggregate(match, group,basicSkip,basicLimit);
        //总集合
        JSONArray objects=new JSONArray();
        /**
         *  处理每个邀请人的相关信息
         */
        for(DBObject dbObject:output.results()){
            /*分享表查询该邀请人在该活动时间里分享的次数*/
            String open_id=dbObject.get("_id").toString();
//            BasicDBObject basicDBObject1=new BasicDBObject();
//            BasicDBList basicDBList_1=new BasicDBList();
//            basicDBList_1.add(new BasicDBObject("app_id",app_id));
//            basicDBList_1.add(new BasicDBObject("open_id",open_id));
//            basicDBList_1.add(new BasicDBObject("vipTask.task_type","invite_registration"));
//            basicDBList_1.add(new BasicDBObject("share_time",new BasicDBObject(QueryOperators.GTE, start_tiem+" 00:00:00")));
//            basicDBList_1.add(new BasicDBObject("share_time", new BasicDBObject(QueryOperators.LTE, end_time+" 23:59:59")));
//            basicDBObject1.put("$and",basicDBList_1);

            BasicDBList basicDBList1=new BasicDBList();

            basicDBList1.add(new BasicDBObject("app_id",app_id));

            BasicDBObject basicDBObject1=new BasicDBObject();
            BasicDBList basicDBList3 = new BasicDBList();
            basicDBList3.add(new BasicDBObject("share_time", new BasicDBObject(QueryOperators.GTE, start_time+" 00:00:00")));
            basicDBList3.add(new BasicDBObject("share_time", new BasicDBObject(QueryOperators.LTE, end_time+" 23:59:59")));
            basicDBObject1.put("$and",basicDBList3);
            basicDBList1.add(basicDBObject1);

            BasicDBObject basicDBObject2=new BasicDBObject();
            BasicDBList basicDBList2 = new BasicDBList();
            basicDBList2.add(new BasicDBObject("vipTask.task_type","invite_registration"));
            basicDBList2.add(new BasicDBObject("vipTask.task_type","activity"));
            basicDBObject2.put("$or",basicDBList2);

            basicDBList1.add(basicDBObject2);

            BasicDBObject basicDBObject3=new BasicDBObject();
            basicDBObject3.put("$and",basicDBList1);
            //分享次数
            int  share_num=cursor_share.find(basicDBObject3).count();


            //会员信息
            BasicDBObject basicDBObject4=new BasicDBObject();
            basicDBObject4.put("activity_code",activity_code);
            basicDBObject4.put("vip.open_id",open_id);
            DBObject dbObject1=cursor_invite_success.findOne(basicDBObject4);
            String vipInfo=dbObject1.get("vip").toString();
            JSONObject jsonObject1=JSON.parseObject(vipInfo);
            /*注册表查询该邀请人在该活动时间里邀请的那些成功注册的人的数量*/
            BasicDBObject basicDBObject5=new BasicDBObject();
            BasicDBList basicDBList_2=new BasicDBList();
            basicDBList_2.add(new BasicDBObject("app_id",app_id));
            basicDBList_2.add(new BasicDBObject("invite_open_id",open_id));
            basicDBList_2.add(new BasicDBObject("register_time",new BasicDBObject(QueryOperators.GTE, start_time+" 00:00:00")));
            basicDBList_2.add(new BasicDBObject("register_time", new BasicDBObject(QueryOperators.LTE, end_time+" 23:59:59")));
            basicDBObject5.put("$and",basicDBList_2);
            int invite_num= cursor_invite.find(basicDBObject5).count();

            JSONObject result=new JSONObject();
            result.put("share_num",share_num);
            result.put("invite_num",invite_num);
            result.put("vip_name",jsonObject1.getString("vip_name"));
            result.put("cardno",jsonObject1.getString("cardno"));
            result.put("vip_phone",jsonObject1.getString("vip_phone"));
            result.put("vip_card_type",jsonObject1.getString("vip_card_type"));
            result.put("open_id",open_id);
            objects.add(result);
        }
        System.out.println("====size======="+objects.size());
        JSONObject jsob=new JSONObject();
        BasicDBObject basicCount=new BasicDBObject();
        BasicDBList basicDBList_count=new BasicDBList();
        basicDBList_count.add(new BasicDBObject("activity_code",activity_code));
        basicDBList_count.add(new BasicDBObject("app_id",app_id));
        basicCount.put("$and",basicDBList_count);
        int count=cursor_invite_success.distinct("open_id",basicCount).size();
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
        jsob.put("list",objects);
        jsob.put("total",count);
        jsob.put("page_num",page_num);
        jsob.put("page_size",page_size);
        jsob.put("pages",pages);
        jsob.put("hasNext",flag);
        jsob.put("app_id",app_id);
        return jsob;
    }

    /**********************************线上报名活动**************************************************************************/
    @Override
    public JSONObject getOnlineRate(String message) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection dbCollection = mongoTemplate.getCollection(CommonValue.table_vip_activity_apply);
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();

        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        String corp_code = activityVip.getCorp_code();
        //目标会员数
        String target_vips = activityVip.getTarget_vips();
        int target_vip_count = 0;
        DataBox dataBox = vipGroupService.vipScreenBySolr(JSONArray.parseArray(target_vips), corp_code, "1", "3", Common.ROLE_GM, "", "", "", "","","");
        if (dataBox.status.toString().equals("SUCCESS")) {
            String message1 = dataBox.data.get("message").value;
            JSONObject msg_obj = JSONObject.parseObject(message1);
            target_vip_count = msg_obj.getInteger("count");
        }
        //报名成功人数
        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBObject.put("activity_code",activity_code);
        basicDBObject.put("status","1");
        int enroll_count=dbCollection.find(basicDBObject).count();

        JSONObject result=new JSONObject();
        result.put("target_vip_count",target_vip_count);
        result.put("enroll_count",enroll_count);
        return result;
    }

    @Override
    public JSONObject getOnlineChat(String message) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection dbCollection = mongoTemplate.getCollection(CommonValue.table_vip_activity_apply);
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code=jsonObject.get("activity_code").toString();
        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        String activity_start_time=activityVip.getStart_time().split(" ")[0];
        String activity_end_time=activityVip.getEnd_time().split(" ")[0];

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String end_date=simpleDateFormat.format(new Date());

        if(end_date.compareTo(activity_end_time)>0){
            end_date=activity_end_time;
        }
        if(end_date.compareTo(activity_start_time)<0){
            end_date=activity_start_time;
        }
        Date date= TimeUtils.getNextDay(end_date,7);
        String start_date=simpleDateFormat.format(date);
        if(start_date.compareTo(activity_start_time)<0){
            start_date=activity_start_time;
        }

        JSONArray result_array=new JSONArray();
        List<String> list=TimeUtils.getBetweenDates(start_date,end_date);
        for (int i = 0; i < list.size(); i++) {
            Pattern pattern = Pattern.compile("^.*" + list.get(i) + ".*$", Pattern.CASE_INSENSITIVE);

            BasicDBObject basicDBObject=new BasicDBObject();
            BasicDBList basicurl_list=new BasicDBList();
            basicurl_list.add(new BasicDBObject("status","1"));
            basicurl_list.add(new BasicDBObject("activity_code",activity_code));
            basicurl_list.add(new BasicDBObject("modified_date",pattern));
            basicDBObject.put("$and",basicurl_list);

            DBCursor dbCursor=dbCollection.find(basicDBObject);
            int enroll_count=dbCursor.count();//报名成功人数
            JSONObject result=new JSONObject();
            result.put("count",enroll_count);
            result.put("date",list.get(i));
            result_array.add(result);
        }
        JSONObject  time_obj=new JSONObject();
        time_obj.put("list",result_array);
        return time_obj;
    }

    @Override
    public JSONObject getOnlineListByVip(String message) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection dbCollection = mongoTemplate.getCollection(CommonValue.table_vip_activity_apply);

        JSONObject message_obj=JSON.parseObject(message);
        int page_num=message_obj.getInteger("page_num");
        int page_size=message_obj.getInteger("page_size");
        String activity_code=message_obj.getString("activity_code");
        String screen=message_obj.getString("screen");
        JSONObject screen_obj=JSON.parseObject(screen);
        String vip_name=screen_obj.getString("vip_name");
        String vip_cardno=screen_obj.getString("vip_cardno");
        String vip_phone=screen_obj.getString("vip_phone");
        String item_name=screen_obj.getString("item_name");
        String pay_type=screen_obj.getString("pay_type");
        String order_id=screen_obj.getString("order_id");
        String sign_status=screen_obj.getString("sign_status");

        BasicDBObject  basicDBObject=new BasicDBObject();
        BasicDBList basicDBList_screen=new BasicDBList();
        basicDBList_screen.add(new BasicDBObject("activity_code",activity_code));
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
        if(StringUtils.isNotBlank(item_name)){
            basicDBList_screen.add(new BasicDBObject("apply_item.item_name",new BasicDBObject("$regex",item_name)));
        }
        if(StringUtils.isNotBlank(pay_type)){
            basicDBList_screen.add(new BasicDBObject("pay_type",pay_type));
        }
        if(StringUtils.isNotBlank(order_id)){
            basicDBList_screen.add(new BasicDBObject("order_id",new BasicDBObject("$regex",order_id)));
        }
        if(StringUtils.isNotBlank(sign_status)){
            basicDBList_screen.add(new BasicDBObject("sign_status",sign_status));
        }
        basicDBObject.put("$and",basicDBList_screen);

        DBCursor dbCursor=dbCollection.find(basicDBObject);
        int total = dbCursor.count();
        int pages = MongoUtils.getPages(dbCursor, page_size);
        boolean flag=true;
        if(page_num>=pages){
            flag=false;
        }
        dbCursor = MongoUtils.sortAndPage(dbCursor, page_num, page_size, "modified_date", -1);
        List<HashMap<String,Object>> hashMapList=getOnlineSwitchVip(dbCursor);
        JSONObject result=new JSONObject();
        result.put("list", JSON.toJSONString(hashMapList));
        result.put("pages", pages);
        result.put("page_num", page_num);
        result.put("page_size", page_size);
        result.put("total", total);
        result.put("is_next",flag);
        return result;
    }

    @Override
    public JSONObject getOnlineListByStore(String message) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection dbCollection = mongoTemplate.getCollection(CommonValue.table_vip_activity_apply);

        JSONObject jsonObject=JSON.parseObject(message);
        int page_num = jsonObject.getInteger("page_num");
        int page_size = jsonObject.getInteger("page_size");
        String activity_code=jsonObject.get("activity_code").toString();
        //筛选条件
        JSONObject screen_obj=JSON.parseObject(jsonObject.getString("screen"));
        String storeCode_screen=screen_obj.get("store_code").toString().toLowerCase();
        String storeName_screen=screen_obj.get("store_name").toString().toLowerCase();
        VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
        String corp_code = vipActivity.getCorp_code();
        String target_vips = vipActivity.getTarget_vips();

        PageInfo<Store> stores =getStoreTargrtVips(corp_code,vipActivity.getRun_scope(),storeCode_screen,storeName_screen,page_num,page_size);
        List<Store> storeList = stores.getList();
        List<HashMap<String,Object>> array=new ArrayList<HashMap<String, Object>>();
        for (int i = 0 ; i < storeList.size(); i++) {
            HashMap<String,Object> store_obj = new HashMap<String, Object>();
            String store_code = storeList.get(i).getStore_code();
            String store_name = storeList.get(i).getStore_name();

            String store_area_code = storeList.get(i).getArea_code();
            if (store_area_code != null)
                store_area_code = store_area_code.replace("§,","");
            JSONObject jsonObject1 = getStoreTargrtVips1(corp_code,store_code,store_area_code,target_vips);
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
            obj.put("vip.store_code",store_code);
            obj.put("vip",new BasicDBObject("$ne",null));
            obj.put("status","1");
            DBCursor dbcoursor = dbCollection.find(obj).sort(new BasicDBObject("modified_date",-1));
            List<HashMap<String,Object>> list = getOnlineSwitchVip(dbcoursor);
            store_obj.put("apply_vip_count",dbcoursor.count());
            double apply_count = dbcoursor.count();
            double target_count = Double.parseDouble(target_vip_count);
            if (target_count != 0d){
                store_obj.put("rate", NumberUtil.percent(apply_count/target_count));
            }else {
                store_obj.put("rate","0%");
            }
            store_obj.put("apply_detail", JSON.toJSONString(list));

            array.add(store_obj);
        }

        JSONObject result=new JSONObject();
        result.put("list",JSON.toJSONString(array));
        result.put("total",stores.getTotal());
        result.put("page_num",page_num);
        result.put("page_size",page_size);
        result.put("pages",stores.getPages());
        result.put("is_next",stores.isHasNextPage());
        return result;
    }

    public  List<HashMap<String,Object>> getOnlineSwitchVip(DBCursor dbObjects)throws Exception{
        List<HashMap<String,Object>> hashMapList=new ArrayList<HashMap<String, Object>>();
        for (DBObject dbObject:dbObjects){
                JSONObject vip_obj=JSON.parseObject(dbObject.get("vip").toString());
                JSONObject apply_item_obj=JSON.parseObject(dbObject.get("apply_item").toString());
                HashMap<String,Object> hashMap=new HashMap<String, Object>();
                hashMap.put("vip_name",vip_obj.getString("vip_name"));
                hashMap.put("cardno",vip_obj.getString("cardno"));
                hashMap.put("vip_phone",vip_obj.getString("vip_phone"));
                hashMap.put("item_name",apply_item_obj.getString("item_name"));
                if(dbObject.get("pay_type").toString().equals("1")){
                    hashMap.put("pay_type","积分");
                    hashMap.put("fee",apply_item_obj.getString("fee_points"));
                }else{
                    hashMap.put("pay_type","现金");
                    hashMap.put("fee",apply_item_obj.getString("fee_money"));
                }
                hashMap.put("order_id",dbObject.get("order_id").toString());//支付单号
                String  sign_status=dbObject.get("sign_status")==null?"":dbObject.get("sign_status").toString();
                if(sign_status.equals("Y")){
                    sign_status="是";
                }else{
                    sign_status="否";
                }

            hashMap.put("sign_status",sign_status);//是否签到
            hashMap.put("sign_date",dbObject.get("sign_date")==null?"":dbObject.get("sign_date").toString());//是否签到
            //hashMap.put("","");//备注
                String apply_info=dbObject.get("apply_info").toString();
                hashMap.put("apply_info",apply_info);
                JSONArray apply_info_array=JSON.parseArray(apply_info);
                StringBuilder stringBuilder=new StringBuilder();
                for (int j = 0; j < apply_info_array.size(); j++) {
                    JSONObject jsonObject2=apply_info_array.getJSONObject(j);
                    String param_desc=jsonObject2.getString("param_desc");
                    String value=jsonObject2.getString("value");
                    String column=jsonObject2.getString("column");
                    if(column.equals("province")){
                       JSONObject map_obj= JSONObject.parseObject(value);
                        String province=map_obj.getString("province").equals("")?"/":map_obj.getString("province");
                        String city=map_obj.getString("city").equals("")?"/":map_obj.getString("city");
                        String area=map_obj.getString("area").equals("")?"/":map_obj.getString("area");
                        value=province+"-"+city+"-"+area;
                    }
                    stringBuilder.append(param_desc);
                    stringBuilder.append(":");
                    stringBuilder.append(value);
                    stringBuilder.append("\n\r");
                    column+="ex_";
                    hashMap.put(column,value);
                }
                hashMap.put("apply_info_format",stringBuilder.toString());//报名资料
                hashMap.put("modified_date",dbObject.get("modified_date").toString());
                hashMapList.add(hashMap);
        }
        return  hashMapList;
    }


    //..............................................使用的工具方法.....................................................//

    //VIP不为空的情况会员卡点击次数
    public int getNoticeActivity(String activity_code,BasicDBList basicDBList1,DBCollection cursor,String vip_card_type_name){
        BasicDBObject basicDBObject1=new BasicDBObject();
        basicDBObject1.put("activity_code",activity_code);
        basicDBObject1.put("vip",new BasicDBObject("$ne",null));
        basicDBObject1.put("open_id",new BasicDBObject("$in",basicDBList1));
        basicDBObject1.put("vip.vip_card_type",vip_card_type_name);
        int j=cursor.distinct("open_id",basicDBObject1).size();
        return j;
    }

    //vip为空的情况（会员卡类型点击次数）
    public  JSONObject getNoticeActivityNumber(DBCollection cursor,String activity_code){
        int one=0;
        int two=0;
        int three=0;
        int four=0;
        int five=0;
        int more=0;
        BasicDBList basicDBList = new BasicDBList();
        BasicDBObject dbObject = new BasicDBObject();
        basicDBList.add(new BasicDBObject("activity_code", activity_code));
        basicDBList.add(new BasicDBObject("vip",null));
        dbObject.put("$and", basicDBList);
        BasicDBObject match = new BasicDBObject("$match", dbObject);
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("_id", "$open_id");
        basicDBObject.put("count", new BasicDBObject("$sum", 1));
        BasicDBObject group = new BasicDBObject("$group", basicDBObject);
        AggregationOutput output = cursor.aggregate(match, group);
        for(DBObject obj:output.results()){
            String open_id=obj.get("_id").toString();
            int count=Integer.parseInt(obj.get("count").toString());
            if(count==1){
                ++one;
            }else if(count==2){
                ++two;
            }else if(count==3){
                ++three;
            }else  if(count==4){
                ++four;
            }else if(count==5){
                ++five;
            }else if(count>5){
                ++more;
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("vip_card_type_name","非会员");

        HashMap<String,String> map= new HashMap<String, String>();
        map.put("one",one+"");
        map.put("two",two+"");
        map.put("three",three+"");
        map.put("four",four+"");
        map.put("five",five+"");
        map.put("more",more+"");

        obj.put("count",map);
        return obj;
    }



//    public JSONArray getStoreTargrtVips(String corp_code,String activity_code, String run_scope,String target_vip) throws Exception{
//        JSONArray array = new JSONArray();
//
//        JSONArray screen = JSONArray.parseArray(target_vip);
//        List<Store> stores = vipActivityService.getActivityStore(corp_code,run_scope);
//        String store_codes = "";
//        for (int i = 0; i < stores.size(); i++) {
//            store_codes = store_codes + stores.get(i).getStore_code() + ",";
//        }
//        List<Store> storeLists = storeService.selectByStoreCodes1(store_codes,corp_code, Common.IS_ACTIVE_Y);
//        for (int i = 0; i < storeLists.size() ; i++) {
//            Store store = storeLists.get(i);
//            //获取店铺目标会员数
//            int target_vip_count = 0;
//            JSONObject store_screen = new JSONObject();
//            store_screen.put("key",Common.VIP_SCREEN_STORE_KEY);
//            store_screen.put("value",store.getStore_code());
//            store_screen.put("type","text");
//            screen.add(store_screen);
//            DataBox dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,"1","3","","","","","","","");
//            if (dataBox.status.toString().equals("SUCCESS")){
//                String result = dataBox.data.get("message").value;
//                JSONObject result_obj = JSONObject.parseObject(result);
//                target_vip_count = Integer.parseInt(result_obj.getString("count"));
//            }
//            JSONObject obj = new JSONObject();
//            obj.put("store_code",store.getStore_code());
//            obj.put("store_name",store.getStore_name());
//            obj.put("area_code",store.getArea_code());
//            obj.put("area_name",store.getArea_name());
//            obj.put("target_vip_count",target_vip_count);
//            array.add(obj);
//        }
//        return array;
//    }

    /**
     * 店铺维度，获取目标店铺
     * @param corp_code
     * @param run_scope  活动参与门店范围
     * @return
     * @throws Exception
     */
    public PageInfo<Store> getStoreTargrtVips(String corp_code, String run_scope,String screen_store_code ,String screen_store_name,int page_num,int page_size) throws Exception{
        JSONObject obj = JSON.parseObject(run_scope);
        String store_code = "";
        String area_code = "";
        String brand_code = "";
        if (obj.containsKey("store_code"))
            store_code = obj.getString("store_code");
        if (obj.containsKey("area_code"))
            area_code = obj.getString("area_code");
        if (obj.containsKey("brand_code"))
            brand_code = obj.getString("brand_code");

        Map<String, String> map = new HashMap<String, String>();
        if(!screen_store_code.equals("")) {
            map.put("store_code", "'"+screen_store_code+"'");
        }
        if(!screen_store_name.equals("")) {
            map.put("store_name", "'"+screen_store_name+"'");
        }
        if (map.size() == 0)
            map = null;
        PageInfo<Store> stores = storeService.getAllStoreScreen(page_num,page_size,corp_code,area_code,brand_code,store_code,map,"",Common.IS_ACTIVE_Y,"");

        return stores;
    }

    /**
     * 店铺维度，获取店铺下目标会员数
     * @param corp_code
     * @param target_vip 活动目标会员条件
     * @return
     * @throws Exception
     */
    public JSONObject getStoreTargrtVips1(String corp_code,String store_code,String area_code ,String target_vip) throws Exception{
        int target_vip_count = 0;

        JSONArray screen = JSONArray.parseArray(target_vip);
        //获取店铺目标会员数
        JSONObject store_screen = new JSONObject();
        store_screen.put("key",Common.VIP_SCREEN_STORE_KEY);
        store_screen.put("value",store_code);
        store_screen.put("type","text");
        screen.add(store_screen);
        DataBox dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,"1","3","","","","","","","");
        if (dataBox.status.toString().equals("SUCCESS")){
            String result = dataBox.data.get("message").value;
            JSONObject result_obj = JSONObject.parseObject(result);
            target_vip_count = Integer.parseInt(result_obj.getString("count"));
        }
        JSONObject obj = new JSONObject();

        String area_name = "";
        String area_code1 = "";
        if (area_code != null && !area_code.equals("")) {
            List<Area> areas = areaService.selectArea(corp_code,area_code);
            for (int j = 0; j < areas.size(); j++) {
                Area area = areas.get(j);
                if (area != null) {
                    String area_name1 = area.getArea_name();
                    area_name = area_name + area_name1 + ",";
                    area_code1 = area_code1 + area.getArea_code() + ",";
                }
            }
            if (area_name.endsWith(","))
                area_name = area_name.substring(0, area_name.length() - 1);
            if (area_code1.endsWith(","))
                area_code1 = area_code1.substring(0, area_code1.length() - 1);
        }

        obj.put("area_code",area_code1);
        obj.put("area_name",area_name);
        obj.put("target_vip_count",target_vip_count);
        return obj;
    }


//    /**
//     * 导购维度，获取导购下目标会员数
//     * @param corp_code
//     * @param activity_code
//     * @param run_scope  活动参与门店范围
//     * @param target_vip 活动目标会员条件
//     * @return
//     * @throws Exception
//     */
//    public JSONArray getUserTargrtVips(String corp_code,String activity_code, String run_scope,String target_vip) throws Exception{
//        JSONArray array = new JSONArray();
//        JSONArray screen = JSONArray.parseArray(target_vip);
//        //获取店铺下的所有导购
//        List<User> tempList=getActivityUserCode(corp_code,run_scope);
//        for (int i = 0; i < tempList.size() ; i++) {
//            String user_code  = tempList.get(i).getUser_code();
//            //获取导购下目标会员数
//            int target_vip_count = 0;
//            JSONObject user_screen = new JSONObject();
//            user_screen.put("key",Common.VIP_SCREEN_USER_KEY);
//            user_screen.put("value",user_code);
//            user_screen.put("type","text");
//            screen.add(user_screen);
//            DataBox dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,"1","3","","","","","","","");
//            if (dataBox.status.toString().equals("SUCCESS")){
//                String result = dataBox.data.get("message").value;
//                JSONObject result_obj = JSONObject.parseObject(result);
//                target_vip_count = Integer.parseInt(result_obj.getString("count"));
//            }
//            JSONObject obj = new JSONObject();
//            obj.put("user_code",user_code);
//            obj.put("target_vip_count",target_vip_count);
//            obj.put("user_name",tempList.get(i).getUser_name());
//            String store_code = tempList.get(i).getStore_code().replaceAll(Common.SPECIAL_HEAD,"");
//            if (store_code.endsWith(","))
//                store_code = store_code.substring(0,store_code.length()-1);
//            obj.put("store_code",store_code);
//            //通过store_code查store_name
//            List<Store> storeLists = storeService.selectByStoreCodes(tempList.get(i).getStore_code(),corp_code, Common.IS_ACTIVE_Y);
//            String storeName="";
//            for(int j=0;j<storeLists.size();j++){
//                storeName+= storeLists.get(j).getStore_name()+",";
//            }
//            if(storeName.endsWith(",")){
//                storeName=storeName.substring(0,storeName.length()-1);
//            }
//            obj.put("store_name",storeName);
//            array.add(obj);
//        }
//        return array;
//    }


    /**
     * 导购维度，获取导购下目标会员数
     * @param corp_code
     * @param target_vip 活动目标会员条件
     * @return
     * @throws Exception
     */
    public JSONObject getUserTargrtVips1(String corp_code, String user_code,String target_vip) throws Exception{

        //获取导购下目标会员数
        int target_vip_count = 0;
        JSONArray screen = JSONArray.parseArray(target_vip);
        JSONObject user_screen = new JSONObject();
        user_screen.put("key",Common.VIP_SCREEN_USER_KEY);
        user_screen.put("value",user_code);
        user_screen.put("type","text");
        screen.add(user_screen);
        DataBox dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,"1","3","","","","","","","");
        if (dataBox.status.toString().equals("SUCCESS")){
            String result = dataBox.data.get("message").value;
            JSONObject result_obj = JSONObject.parseObject(result);
            target_vip_count = Integer.parseInt(result_obj.getString("count"));
        }
        JSONObject obj = new JSONObject();
        obj.put("user_code",user_code);
        obj.put("target_vip_count",target_vip_count);
        return obj;
    }


    //获取活动下的店铺下的所有导购
    public List<User> getActivityUserCode(String corp_code,String run_scope) throws  Exception{
        List<User> userList;
        List<Store> stores = vipActivityService.getActivityStore(corp_code,run_scope);
        //获取活动下的店铺
        String store_codes = "";
        for (int i = 0; i < stores.size(); i++) {
            store_codes = store_codes + stores.get(i).getStore_code() + ",";
        }
        userList = userService.selUserByStoreCode(corp_code,"",store_codes,null,"","");
        return  userList;
    }

//    //获取活动下的店铺下的所有导购(筛选)
//    public  PageInfo<User> getActivityUserCode(String corp_code,String run_scope,String screen_store_code ,String screen_store_name,
//                                                String screen_user_code,String screen_user_name,int page_num,int page_size) throws  Exception{
//        //获取活动下所有店铺
//        JSONObject obj = JSON.parseObject(run_scope);
//        String store_code = "";
//        String area_code = "";
//        String brand_code = "";
//        if (obj.containsKey("store_code"))
//            store_code = obj.getString("store_code");
//        if (obj.containsKey("area_code"))
//            area_code = obj.getString("area_code");
//        if (obj.containsKey("brand_code"))
//            brand_code = obj.getString("brand_code");
//
//        PageInfo<User> userList = new PageInfo<User>();
//        if (screen_store_code.equals("") && screen_store_name.equals("") && screen_user_code.equals("") && screen_user_name.equals("")){
//            if (store_code.equals("") && brand_code.equals("") && area_code.equals("")) {
//                List<Store> stores = storeService.getCorpStore(corp_code);
//                String store_codes = "";
//                for (int i = 0; i < stores.size(); i++) {
//                    store_codes = store_codes + stores.get(i).getStore_code() + ",";
//                }
//                //获取店铺下的所有导购
//                userList = userService.selUserByStoreCode(page_num,page_size,corp_code,"",store_codes,null,"","");
//            }else {
//               // userList = userService.selUserByStoreCode(page_num,page_size,corp_code,"","",null,"","");
//                List<Store> storeList = storeService.getStoreAndBrandArea(corp_code,area_code,brand_code,store_code,"");
//                String store_codes = "";
//                for (int i = 0; i < storeList.size(); i++) {
//                    store_codes = store_codes + storeList.get(i).getStore_code() + ",";
//                }
//                //获取店铺下的所有导购
//                userList = userService.selUserByStoreCode(page_num,page_size,corp_code,"",store_codes,null,"","");
//
//            }
//        }else if (!screen_user_code.equals("") || !screen_user_name.equals("")){
//            Map<String, String> map = new HashMap<String, String>();
//            if(!screen_user_code.equals("")){
//                map.put("user_code","'"+screen_user_code+"'");
//            }
//            if(!screen_user_name.equals("")){
//                map.put("user_name","'"+screen_user_name+"'");
//            }
//            userList = userService.getAllUserScreen(page_num,page_size,corp_code,map);
//        }else {
//            Map<String, String> map = new HashMap<String, String>();
//            if(!screen_store_code.equals("")) {
//                map.put("store_code", "'"+screen_store_code+"'");
//            }
//
//            if(!screen_store_name.equals("")){
//                map.put("store_name","'"+screen_store_name+"'");
//            }
//
//
//            List<Store> stores = storeService.getAllStoreScreen1(1,10000,corp_code,"","","",map,"",Common.IS_ACTIVE_Y).getList();
//            String store_codes = "";
//            for (int i = 0; i < stores.size(); i++) {
//                store_codes = store_codes + stores.get(i).getStore_code() + ",";
//            }
//            //获取店铺下的所有导购
//            userList = userService.selUserByStoreCode(page_num,page_size,corp_code,"",store_codes,null,"","");
//        }
//        List<User> users = userList.getList();
//        for (int j = 0; j < users.size(); j++) {
//            User user = users.get(j);
//            String store_code1 = user.getStore_code().replace(Common.SPECIAL_HEAD, "");
//            if (store_code1.endsWith(","))
//                store_code1 = store_code1.substring(0,store_code1.length()-1);
//            String store_name = "";
//            if (store_code1 != null && !store_code1.equals("")) {
//                String[] ids = store_code1.split(",");
//                for (int i = 0; i < ids.length; i++) {
//                    Store store = storeService.getStoreByCode(corp_code, ids[i], Common.IS_ACTIVE_Y);
//                    if (store != null) {
//                        store_name = store_name + store.getStore_name();
//                        if (i != ids.length - 1) {
//                            store_name = store_name + ",";
//                        }
//                    }
//                }
//            }
//            user.setStore_code(store_code1);
//            user.setStore_name(store_name);
//        }
//        userList.setList(users);
//        return  userList;
//    }
//获取活动下的店铺下的所有导购(筛选)
public  PageInfo<User> getActivityUserCode(String corp_code,String run_scope,String screen_store_code ,String screen_store_name,
                                           String screen_user_code,String screen_user_name,int page_num,int page_size) throws  Exception{
    //获取活动下所有店铺
    JSONObject obj = JSON.parseObject(run_scope);
    String store_code = "";
    String area_code = "";
    String brand_code = "";
    if (obj.containsKey("store_code"))
        store_code = obj.getString("store_code");
    if (obj.containsKey("area_code"))
        area_code = obj.getString("area_code");
    if (obj.containsKey("brand_code"))
        brand_code = obj.getString("brand_code");

    PageInfo<User> userList = new PageInfo<User>();
    if (store_code.equals("") && brand_code.equals("") && area_code.equals("")) {
        List<Store> stores = storeService.getCorpStore(corp_code);
        String store_codes = "";
        for (int i = 0; i < stores.size(); i++) {
            store_codes = store_codes + stores.get(i).getStore_code() + ",";
        }
        //获取店铺下的所有导购
        userList = userService.selUserByStoreCode(page_num,page_size,corp_code,"",store_codes,null,"","");
    }else {
        // userList = userService.selUserByStoreCode(page_num,page_size,corp_code,"","",null,"","");
        List<Store> storeList = storeService.getStoreAndBrandArea(corp_code,area_code,brand_code,store_code,"");
        String store_codes = "";
        for (int i = 0; i < storeList.size(); i++) {
            store_codes = store_codes + storeList.get(i).getStore_code() + ",";
        }
        //获取店铺下的所有导购
        userList = userService.selUserByStoreCode(page_num,page_size,corp_code,"",store_codes,null,"","");
    }
    List<User> users = userList.getList();
    //***************************对筛选条件进行过滤*******************************//
    List<User> userList1=new ArrayList<User>();
    for (int i = 0; i <users.size() ; i++) {
        User user=users.get(i);
        if(StringUtils.isNotBlank(screen_user_code)){
            if(!user.getUser_code().contains(screen_user_code)){
                continue;
            }
        }
        if(StringUtils.isNotBlank(screen_user_name)){
            if(!user.getUser_name().contains(screen_user_name)){
                continue;
            }
        }
        if(StringUtils.isNotBlank(screen_store_code)){
            if(!user.getStore_code().contains(screen_store_code)){
                continue;
            }
        }
        if(StringUtils.isNotBlank(screen_store_name)){
            if(!user.getStore_name().contains(screen_store_name)){
                continue;
            }
        }
        userList1.add(user);
    }
    users=userList1;

    for (int j = 0; j < users.size(); j++) {
        User user = users.get(j);
        String store_code1 = user.getStore_code().replace(Common.SPECIAL_HEAD, "");
        if (store_code1.endsWith(","))
            store_code1 = store_code1.substring(0,store_code1.length()-1);
        String store_name = "";
        if (store_code1 != null && !store_code1.equals("")) {
            String[] ids = store_code1.split(",");
            for (int i = 0; i < ids.length; i++) {
                Store store = storeService.getStoreByCode(corp_code, ids[i], Common.IS_ACTIVE_Y);
                if (store != null) {
                    store_name = store_name + store.getStore_name();
                    if (i != ids.length - 1) {
                        store_name = store_name + ",";
                    }
                }
            }
        }
        user.setStore_code(store_code1);
        user.setStore_name(store_name);
    }
    userList.setList(users);
    return  userList;
}

    //对卡类型进行处理
    public List<HashMap<String,String>> getCardList(List<HashMap<String,String>> hashMapList){
        Collections.sort(hashMapList, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                if(Integer.parseInt(o1.get("count"))<Integer.parseInt(o2.get("count"))){
                    return  1;
                }else  if(Integer.parseInt(o1.get("count"))>Integer.parseInt(o2.get("count"))){
                    return  -1;
                }
                return 0;
            }
        });
        List<HashMap<String,String>> servenList=new ArrayList<HashMap<String, String>>();
        for(int s=0;s<6;s++){
            servenList.add(hashMapList.get(s));
        }
        int otherCount=0;
        for(int x=6;x<hashMapList.size();x++){
            otherCount+=Integer.parseInt(hashMapList.get(x).get("count").toString());
        }
        HashMap<String,String> otherMap=new HashMap<String, String>();
        otherMap.put("vip_card_type_name","其他");
        otherMap.put("count",otherCount+"");
        servenList.add(otherMap);
        return  servenList;
    }


    //获取活动下的优惠券编号

    public String getTypeCode(String activity_code) throws Exception{

        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        String run_mode = activityVip.getRun_mode();
        VipActivityDetail vipActivityDetail=vipActivityDetailService.selActivityDetailByCode(activity_code);
        String send_type_code=vipActivityDetail.getSend_coupon_type();
        String coupon_type=vipActivityDetail.getCoupon_type();
        String type_code="";

        List<String> types = new ArrayList<String>();
        if(run_mode.equals("coupon")){
            if(send_type_code.equals("batch")||send_type_code.equals("card")){
                JSONArray jsonArray1=JSON.parseArray(coupon_type);
                for(int i=0;i<jsonArray1.size();i++){
                    JSONObject jsonObject1=jsonArray1.getJSONObject(i);

                    if (!types.contains(jsonObject1.getString("coupon_code"))){
                        types.add(jsonObject1.getString("coupon_code"));
                        type_code+=jsonObject1.getString("coupon_code")+",";
                    }
                }
            }else if(send_type_code.equals("anniversary")){
                List<VipActivityDetailAnniversary> vipActivityDetailAnniversary=vipActivityDetailService.selActivityDetailAnniversary(activity_code);

                for(int i=0;i<vipActivityDetailAnniversary.size();i++) {
                    String coupon_type1 = vipActivityDetailAnniversary.get(i).getCoupon_type();
                    JSONArray jsonArray2= JSONArray.parseArray(coupon_type1);
                    for(int j=0;j<jsonArray2.size();j++){
                        JSONObject jsonObject3=jsonArray2.getJSONObject(j);
                        if (!types.contains(jsonObject3.getString("coupon_code"))){
                            types.add(jsonObject3.getString("coupon_code"));
                            type_code+=jsonObject3.getString("coupon_code")+",";
                        }
                    }
                }
            }else if(send_type_code.equals("consume")){
                List<VipActivityDetailConsume>  consumeList= vipActivityDetailService.selActivityDetailConsume(activity_code);
                logger.info("=============consumeList.size"+consumeList.size());
                for(int i=0;i<consumeList.size();i++){
                    VipActivityDetailConsume activityDetailConsume=consumeList.get(i);
                    String coupon=activityDetailConsume.getCoupon_type();
                    logger.info("=============consumeList.size"+coupon);

                    JSONArray jsonArray1=JSON.parseArray(coupon);
                    for(int j=0;j<jsonArray1.size();j++){
                        JSONObject jsonObject1=jsonArray1.getJSONObject(j);
                        String coupon_code = jsonObject1.getString("coupon_code");
                        if (!types.contains(coupon_code)){
                            types.add(coupon_code);
                            type_code+=coupon_code+",";
                        }
                    }
                }
            }
        }else if(run_mode.equals("register")){
            String register_data=vipActivityDetail.getRegister_data();
            JSONArray jsonArray1=JSON.parseArray(register_data);
            for(int i=0;i<jsonArray1.size();i++){
                JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                JSONObject jsonObject2=jsonObject1.getJSONObject("present");
                JSONArray jsonArray2=jsonObject2.getJSONArray("coupon");
                for(int j=0;j<jsonArray2.size();j++){
                    JSONObject jsonObject3=jsonArray2.getJSONObject(j);
                    if (!types.contains(jsonObject1.getString("coupon_code"))){
                        types.add(jsonObject3.getString("coupon_code"));
                        type_code+=jsonObject3.getString("coupon_code")+",";
                    }
                }
            }
        }

//        String[] types=type_code.split(",");
//        List<Object> list=new ArrayList<Object>();
//        for(int i=0;i<types.length;i++){
//            list.add(types[i]);
//        }
//       List<Object> list1= CheckUtils.removeDuplicate(list);
//        String typeCode="";
//        for(int i=0;i<types.size();i++){
//            typeCode+=types.get(i).toString()+",";
//        }
        return  type_code;
    }


    //获取活动下的优惠券编号
    public String getCouponByActivityCode(String activity_code) throws Exception{
        List<Object>  list=new ArrayList<Object>();
        VipActivity activityVip=vipActivityService.getActivityByCode(activity_code);
        String run_mode = activityVip.getRun_mode();
        VipActivityDetail vipActivityDetail=vipActivityDetailService.selActivityDetailByCode(activity_code);
        String send_type_code=vipActivityDetail.getSend_coupon_type();
        String coupon_type=vipActivityDetail.getCoupon_type();
        String type_code="";
        if(run_mode.equals("coupon")){
            if(send_type_code.equals("batch")){
                JSONArray jsonArray1=JSON.parseArray(coupon_type);
                list=switchJsonArray(jsonArray1);
                list= CheckUtils.removeDuplicate(list);
            }else if(send_type_code.equals("card")){
                JSONArray all_array=new JSONArray();
                JSONArray jsonArray1=JSON.parseArray(coupon_type);
                for(int i=0;i<jsonArray1.size();i++){
                    JSONObject jsonObject=jsonArray1.getJSONObject(i);
                    String[] coupon_code= jsonObject.getString("coupon_code").split(",");
                    String[] coupon_name= jsonObject.getString("coupon_name").split(",");
                    JSONArray jsonArray=new JSONArray();
                    for(int j=0;j<coupon_code.length;j++){
                        JSONObject json=new JSONObject();
                        json.put("coupon_code",coupon_code[j]);
                        json.put("coupon_name",coupon_name[j]);
                        jsonArray.add(json);
                    }
                    all_array.addAll(jsonArray);
                }
                list=switchJsonArray(all_array);
                list= CheckUtils.removeDuplicate(list);
            }else if(send_type_code.equals("anniversary")){
                JSONArray jsonArray1=JSON.parseArray(coupon_type);
                JSONArray all_array=new JSONArray();
                for(int i=0;i<jsonArray1.size();i++){
                    JSONObject jsonObject=jsonArray1.getJSONObject(i);
                    JSONArray jsonArray=jsonObject.getJSONArray("coupon_type");
                    all_array.addAll(jsonArray);
                }
                list=switchJsonArray(all_array);
                list= CheckUtils.removeDuplicate(list);
            }else if(send_type_code.equals("consume")){
                JSONArray all_array=new JSONArray();
                List<VipActivityDetailConsume>  consumeList= vipActivityDetailService.selActivityDetailConsume(activity_code);
                for(int i=0;i<consumeList.size();i++){
                    VipActivityDetailConsume activityDetailConsume=consumeList.get(i);
                    String coupon=activityDetailConsume.getCoupon_type();
                    JSONArray jsonArray1=JSON.parseArray(coupon);
                    all_array.addAll(jsonArray1);
                }
                list=switchJsonArray(all_array);
                list= CheckUtils.removeDuplicate(list);
            }
        }else if(run_mode.equals("register")){
            JSONArray all_array=new JSONArray();
            String register_data=vipActivityDetail.getRegister_data();
            JSONArray jsonArray1=JSON.parseArray(register_data);
            for(int i=0;i<jsonArray1.size();i++){
                JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                JSONObject jsonObject2=jsonObject1.getJSONObject("present");
                JSONArray jsonArray2=jsonObject2.getJSONArray("coupon");
                all_array.addAll(jsonArray2);
            }
            list=switchJsonArray(all_array);
            list= CheckUtils.removeDuplicate(list);
        }
        JSONObject result=new JSONObject();
        result.put("list",list);
        return  result.toString();
    }

    public List<Object> switchJsonArray(JSONArray jsonArray) throws Exception{
        List<Object> list=new ArrayList<Object>();
        for (int i = 0; i <jsonArray.size() ; i++) {
            list.add(jsonArray.getJSONObject(i).toString());
        }
        return  list;
    }



    //获取活动下的优惠券编号
    public List<HashMap<String,Object>> getCouponByActivityCode1(String activity_code) throws Exception{
        List<Object>  list=new ArrayList<Object>();//new一个list
        VipActivity activityVip=vipActivityService.selActivityByCodeAndName(activity_code);//用活动唯一标识查询run_mode活动类别
        String run_mode = activityVip.getRun_mode(); //提取活动的类别
        //String run_mode = "coupon";
        VipActivityDetail vipActivityDetail=vipActivityDetailService.selActivityDetailByCode(activity_code);//用活动唯一标识查询需要coupon_code,coupon_name
        String send_type_code=vipActivityDetail.getSend_coupon_type();//提取send_type_code发券的类型
        String coupon_type=vipActivityDetail.getCoupon_type();//提取coupon_type券的类型
        String type_code="";
        if(run_mode.equals("coupon")){          //如果活动类别是coupon
            if(send_type_code.equals("batch")){     //如果是批量发券
                JSONArray jsonArray1=JSON.parseArray(coupon_type); //把刚提取的coupon_type  json化
                list=switchJsonArray1(jsonArray1);//把数据加到json里
                list= CheckUtils.removeDuplicate(list);  //去掉里面重复的优惠券
            }else if(send_type_code.equals("card")){    //如果是card形式发券
                JSONArray all_array=new JSONArray();
                JSONArray jsonArray1=JSON.parseArray(coupon_type);
                for(int i=0;i<jsonArray1.size();i++){
                    JSONObject jsonObject=jsonArray1.getJSONObject(i);
                    String[] coupon_code= jsonObject.getString("coupon_code").split(",");
                    String[] coupon_name= jsonObject.getString("coupon_name").split(",");
                    JSONArray jsonArray=new JSONArray();
                    for(int j=0;j<coupon_code.length;j++){
                        JSONObject json=new JSONObject();
                        json.put("coupon_code",coupon_code[j]);
                        json.put("coupon_name",coupon_name[j]);
                        jsonArray.add(json);
                    }
                    all_array.addAll(jsonArray);
                }
                list=switchJsonArray1(all_array);
                list= CheckUtils.removeDuplicate(list);
            }else if(send_type_code.equals("anniversary")){
                JSONArray jsonArray1=JSON.parseArray(coupon_type);
                JSONArray all_array=new JSONArray();
                for(int i=0;i<jsonArray1.size();i++){
                    JSONObject jsonObject=jsonArray1.getJSONObject(i);
                    JSONArray jsonArray=jsonObject.getJSONArray("coupon_type");
                    all_array.addAll(jsonArray);
                }
                list=switchJsonArray(all_array);
                list= CheckUtils.removeDuplicate(list);
            }else if(send_type_code.equals("consume")){
                JSONArray all_array=new JSONArray();
                List<VipActivityDetailConsume>  consumeList= vipActivityDetailService.selActivityDetailConsume(activity_code);
                for(int i=0;i<consumeList.size();i++){
                    VipActivityDetailConsume activityDetailConsume=consumeList.get(i);
                    String coupon=activityDetailConsume.getCoupon_type();
                    JSONArray jsonArray1=JSON.parseArray(coupon);
                    all_array.addAll(jsonArray1);
                }
                list=switchJsonArray1(all_array);
                list= CheckUtils.removeDuplicate(list);
            }
        }else if(run_mode.equals("register")){
            JSONArray all_array=new JSONArray();
            String register_data=vipActivityDetail.getRegister_data();
            JSONArray jsonArray1=JSON.parseArray(register_data);
            for(int i=0;i<jsonArray1.size();i++){
                JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                JSONObject jsonObject2=jsonObject1.getJSONObject("present");
                JSONArray jsonArray2=jsonObject2.getJSONArray("coupon");
                all_array.addAll(jsonArray2);
            }
            list=switchJsonArray1(all_array);
            list= CheckUtils.removeDuplicate(list);
        }
        List<HashMap<String,Object>> coupon_list=new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i <list.size() ; i++) {
            HashMap<String,Object> map=new HashMap<String, Object>();
            JSONObject jsonObject=JSON.parseObject(list.get(i).toString());
            map.put("coupon_code",jsonObject.getString("coupon_code"));
            map.put("coupon_name",jsonObject.getString("coupon_name"));
            coupon_list.add(map);
        }
        return  coupon_list;
    }

    public List<Object> switchJsonArray1(JSONArray jsonArray) throws Exception{
        List<Object> list=new ArrayList<Object>();
        for (int i = 0; i <jsonArray.size() ; i++) {
            list.add(jsonArray.getJSONObject(i).toString());
        }
        return  list;
    }



}







