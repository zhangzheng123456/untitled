package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.VipActivityMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.*;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by nanji on 2016/11/15.
 */
@Service
public class VipActivityServiceImpl implements VipActivityService {

    @Autowired
    private VipActivityMapper vipActivityMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private IceInterfaceService iceInterfaceService;
    @Autowired
    MongoDBClient mongodbClient;

    @Override
    public PageInfo<VipActivity> selectAllActivity(int page_num, int page_size, String corp_code, String user_code, String search_value) throws Exception {
        List<VipActivity> VipActivitys;
        PageHelper.startPage(page_num, page_size);
        VipActivitys = vipActivityMapper.selectAllActivity(corp_code, user_code, search_value);
        for (VipActivity VipActivity : VipActivitys) {
            VipActivity.setIsactive(CheckUtils.CheckIsactive(VipActivity.getIsactive()));
        }
        PageInfo<VipActivity> page = new PageInfo<VipActivity>(VipActivitys);

        return page;
    }

    @Override
    public PageInfo<VipActivity> selectActivityAllScreen(int page_num, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("user_code", user_code);

        JSONObject date = JSONObject.parseObject(map.get("created_date"));
        params.put("created_date_start", date.get("start").toString());
        String end = date.get("end").toString();
        if (!end.equals(""))
            end = end + " 23:59:59";
        params.put("created_date_end", end);
        map.remove("created_date");

        params.put("map", map);
        PageHelper.startPage(page_num, page_size);
        List<VipActivity> list1 = vipActivityMapper.selectActivityScreen(params);
        for (VipActivity VipActivity : list1) {
            VipActivity.setIsactive(CheckUtils.CheckIsactive(VipActivity.getIsactive()));
        }
        PageInfo<VipActivity> page = new PageInfo<VipActivity>(list1);
        return page;
    }

    @Override
    public int delete(int id) throws Exception {
        return vipActivityMapper.delActivityById(id);
    }

    @Override
    public String insert(String message, String user_id,HttpServletRequest request) throws Exception {
        String result = null;
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_code = "A"+corp_code+Common.DATETIME_FORMAT_DAY_NUM.format(now);
        String activity_state = "未执行";

        VipActivity VipActivity = WebUtils.JSON2Bean(jsonObject, VipActivity.class);
        VipActivity.setActivity_code(activity_code);
        VipActivity.setModifier(user_id);
        VipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
        VipActivity.setCreater(user_id);
        VipActivity.setCreated_date(Common.DATETIME_FORMAT.format(now));
        VipActivity.setActivity_state(activity_state);
        VipActivity.setTask_code("");
        int info=0;
         info= vipActivityMapper.insertActivity(VipActivity);
        VipActivity VipActivity1 = selActivityByCode(activity_code);
        if (info>0) {
            return String.valueOf(VipActivity1.getId());
        } else {
            result="新增失败";
            return result;
        }
    }

    @Override
    public String update(String message, String user_id,HttpServletRequest request) throws Exception {
       String result = "";
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        int activity_id = Integer.parseInt(jsonObject.get("id").toString().trim());
        VipActivity VipActivity1 = vipActivityMapper.selActivityById(activity_id);
        String activity_vip_code = VipActivity1.getActivity_code();
        String corp_code = jsonObject.get("corp_code").toString().trim();

        VipActivity VipActivity = WebUtils.JSON2Bean(jsonObject, VipActivity.class);
        Date now = new Date();
        VipActivity.setId(activity_id);
        VipActivity.setModifier(user_id);
        VipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
        int info=0;
        info= vipActivityMapper.updateActivity(VipActivity);
        if (info>0) {
            result=Common.DATABEAN_CODE_SUCCESS;
            return result;
        } else {
            result="编辑失败";
            return result;
        }
    }

    @Override
    public int updateVipActivity(VipActivity VipActivity) throws Exception {
        return vipActivityMapper.updateActivity(VipActivity);
    }

    @Override
    public VipActivity selectActivityById(int id) throws Exception {
        VipActivity VipActivity = vipActivityMapper.selActivityById(id);
        String corp_code = VipActivity.getCorp_code();
        String target_vips = VipActivity.getTarget_vips();
        JSONObject vips_obj = JSONObject.parseObject(target_vips);
        String type = vips_obj.get("type").toString();
        if (type.equals("1")) {
            String area_code = vips_obj.get("area_code").toString();
            String brand_code = vips_obj.get("brand_code").toString();
            String store_code = vips_obj.get("store_code").toString();
            String user_code = vips_obj.get("user_code").toString();
            DataBox dataBox = iceInterfaceService.vipScreenMethod("1","3",corp_code,area_code,brand_code,store_code,user_code);
            String result = dataBox.data.get("message").value;
            JSONObject result_obj = JSONObject.parseObject(result);
            String count = result_obj.get("count").toString();
            VipActivity.setTarget_vips_count(count);
        }else if (type.equals("2")){
            String vips = vips_obj.get("vips").toString();
            String[] vips_array = vips.split(",");
            VipActivity.setTarget_vips_count(String.valueOf(vips_array.length));
        }
        return VipActivity;
    }

    @Override
    public VipActivity getActivityById(int id) throws Exception {
        VipActivity VipActivity = vipActivityMapper.selActivityById(id);
        return VipActivity;
    }

    @Override
    public VipActivity selActivityByCode(String activity_vip_code) throws Exception {
        return vipActivityMapper.selActivityByCode(activity_vip_code);
    }



    /**
     * 获取活动任务执行情况
     *
     */
    @Override
    public JSONObject executeDetail(VipActivity VipActivity) throws Exception {
        JSONObject result = new JSONObject();
        String task_code = VipActivity.getTask_code();
        String corp_code = VipActivity.getCorp_code();
        String target_vips_count = VipActivity.getTarget_vips_count();

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_allocation);

        List<TaskAllocation> taskAllocations = taskService.selTaskAllocation(corp_code, task_code);
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("corp_code", corp_code);
        dbObject.put("task_code", task_code);
        JSONArray task_array = new JSONArray();
        Double complete_vip_count = 0d;
        for (int i = 0; i < taskAllocations.size(); i++) {
            JSONObject task_obj = new JSONObject();
            String user_code = taskAllocations.get(i).getUser_code();
            String user_name = taskAllocations.get(i).getUser_name();
            String store_code = taskAllocations.get(i).getStore_code();
            dbObject.put("user_code", user_code);
            DBCursor dbCursor = cursor.find(dbObject);
            String complete_rate = "0";
            String store_name = "";
            String area_name = "";
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                if (obj.containsField("vips") && (obj.get("vips").toString().equals("") || obj.get("vips").toString().equals("[]"))){
                    complete_rate = "100";
                } else if (obj.containsField("complete_rate")){
                    complete_rate = obj.get("complete_rate").toString();
                }
                if (obj.containsField("complete_vip_count")){
                    String user_complete_vip_count = obj.get("complete_vip_count").toString();
                    complete_vip_count = complete_vip_count + Double.parseDouble(user_complete_vip_count);
                }
            }
            task_obj.put("user_code",user_code);
            task_obj.put("user_name",user_name);
            task_obj.put("store_name",store_name);
            task_obj.put("area_name",area_name);
            task_obj.put("complete_rate",complete_rate);
            task_array.add(task_obj);
        }
        result.put("userList", task_array);
        result.put("target_vips_count", target_vips_count);
        result.put("complete_vips_count", complete_vip_count);

        return result;
    }

//    //执行任务活动
//    public String executeTask(VipActivity VipActivity,String user_code) throws Exception{
//        String status = Common.DATABEAN_CODE_SUCCESS;
//
//        String activity_vip_code = VipActivity.getActivity_code();
//        String corp_code = VipActivity.getCorp_code();
//        String run_mode = VipActivity.getRun_mode();
//        Date now = new Date();
//        String task_title = VipActivity.getTask_title();
//        String task_desc = VipActivity.getTask_desc();
//        String operators = VipActivity.getOperators();
//        String start_time = VipActivity.getStart_time();
//        String end_time = VipActivity.getEnd_time();
//
//        //判断是否存在【任务类型】，没有则新建
//        List<TaskType> taskTypes = taskTypeService.nameExist(corp_code, run_mode);
//        String task_type_code = "";
//        if (taskTypes.size() > 0) {
//            task_type_code = taskTypes.get(0).getTask_type_code();
//        } else {
//            JSONObject message1 = new JSONObject();
//            task_type_code = "T" + Common.DATETIME_FORMAT_DAY_NUM.format(now);
//            message1.put("task_type_code", task_type_code);
//            message1.put("task_type_name", run_mode);
//            message1.put("corp_code", corp_code);
//            message1.put("isactive", "Y");
//            message1.put("created_date", Common.DATETIME_FORMAT.format(now));
//            message1.put("modified_date", Common.DATETIME_FORMAT.format(now));
//            message1.put("creater", user_code);
//            message1.put("modifier", user_code);
//            taskTypeService.insertTaskType(message1.toString(), user_code);
//        }
//
//        //创建任务并分配给执行人
//        String user_codes = "";
//        String phones = "";
//
//        JSONArray store_codes_array = JSONArray.parseArray(operators);
//        String store_codes = "";
//        for (int i = 0; i <store_codes_array.size() ; i++) {
//            store_codes = store_codes + store_codes_array.getJSONObject(i).get("store_code")+",";
//        }
//        List<User> userList = userService.selUserByStoreCode(corp_code,"",store_codes,null,"");
//        if (userList.size() > 0){
//            for (int i = 0; i < userList.size(); i++) {
//                user_codes = user_codes + userList.get(i).getUser_code() + ",";
//                phones = phones + userList.get(i).getPhone() + ",";
//            }
//            Task task = new Task();
//            String task_code = "T" + Common.DATETIME_FORMAT_DAY_NUM.format(now) + Math.round(Math.random() * 9);
//            task.setTask_code(task_code);
//            task.setTask_title(task_title);
//            task.setTask_type_code(task_type_code);
//            task.setTask_description(task_desc);
//            task.setTarget_start_time(start_time);
//            task.setTarget_end_time(end_time);
//            task.setCorp_code(corp_code);
//            task.setCreated_date(Common.DATETIME_FORMAT.format(now));
//            task.setCreater(user_code);
//            task.setModified_date(Common.DATETIME_FORMAT.format(now));
//            task.setModifier(user_code);
//            task.setIsactive(Common.IS_ACTIVE_Y);
//            task.setActivity_vip_code(activity_vip_code);
//            taskService.addTask(task, phones, user_codes, user_code,activity_vip_code);
//
//            //更新活动表中task_code
//            VipActivity.setTask_code(task_code);
//            //更新活动状态activity_state
//            VipActivity.setActivity_state("执行中");
//            VipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
//            VipActivity.setModifier(user_code);
//            updateVipActivity(VipActivity);
//        }else {
//            return "该范围下没有执行人，无法执行";
//        }
//        return status;
//    }
//
//    //执行系统发送短信活动
//    public String executeSysMsg(VipActivity VipActivity,String user_code) throws Exception{
//        String status = Common.DATABEAN_CODE_SUCCESS;
//        Date now = new Date();
//
//        String corp_code = VipActivity.getCorp_code();
//        String target_vips = VipActivity.getTarget_vips();
//        String msg_info = VipActivity.getMsg_info();
//        JSONObject target_vips_obj = JSONObject.parseObject(target_vips);
//        String type = target_vips_obj.getString("type");
//        String phone = "";
//        if (type.equals("1")){
//            String area_code = target_vips_obj.get("area_code").toString();
//            String brand_code = target_vips_obj.get("brand_code").toString();
//            String store_code = target_vips_obj.get("store_code").toString();
//            String vip_user_code = target_vips_obj.get("user_code").toString();
//            if (vip_user_code.equals("")){
//                if (store_code.equals("")) {
//                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
//                    for (int i = 0; i < storeList.size(); i++) {
//                        store_code = store_code + storeList.get(i).getStore_code() + ",";
//                    }
//                }
//                Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
//                Data data_store_code = new Data("store_codes", store_code, ValueType.PARAM);
//
//                Map datalist = new HashMap<String, Data>();
//                datalist.put(data_corp_code.key, data_corp_code);
//                datalist.put(data_store_code.key, data_store_code);
//                DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo",datalist);
//                String message = dataBox.data.get("message").value;
//                JSONObject msg_obj = JSONObject.parseObject(message);
//                JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
//                for (int i = 0; i < vip_infos.size(); i++) {
//                    JSONObject vip_obj = vip_infos.getJSONObject(i);
//                    phone = phone + vip_obj.getString("vip_phone") + ",";
//                }
//            }else {
//                Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
//                Data data_user_code = new Data("user_codes", user_code, ValueType.PARAM);
//
//                Map datalist = new HashMap<String, Data>();
//                datalist.put(data_corp_code.key, data_corp_code);
//                datalist.put(data_user_code.key, data_user_code);
//                DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo",datalist);
//                String message = dataBox.data.get("message").value;
//                JSONObject msg_obj = JSONObject.parseObject(message);
//                JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
//                for (int i = 0; i < vip_infos.size(); i++) {
//                    JSONObject vip_obj = vip_infos.getJSONObject(i);
//                    phone = phone + vip_obj.getString("vip_phone") + ",";
//                }
//            }
//        }else {
//            String vips = target_vips_obj.get("vips").toString();
//
//            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
//            Data data_vip_id = new Data("vip_ids", vips, ValueType.PARAM);
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(data_vip_id.key, data_vip_id);
//            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo",datalist);
//            String message = dataBox.data.get("message").value;
//            JSONObject msg_obj = JSONObject.parseObject(message);
//            JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
//            for (int i = 0; i < vip_infos.size(); i++) {
//                JSONObject vip_obj = vip_infos.getJSONObject(i);
//                phone = phone + vip_obj.getString("vip_phone") + ",";
//            }
//        }
//        Data data_channel = new Data("channel", "santong", ValueType.PARAM);
//        Data data_phone = new Data("phone", phone, ValueType.PARAM);
//        Data data_text = new Data("text", msg_info, ValueType.PARAM);
//
//        Map datalist = new HashMap<String, Data>();
//        datalist.put(data_channel.key, data_channel);
//        datalist.put(data_phone.key, data_phone);
//        datalist.put(data_text.key, data_text);
//        DataBox dataBox = iceInterfaceService.iceInterfaceV3("SendSMS",datalist);
//        if (!dataBox.status.toString().equals("SUCCESS")){
//            status = "执行失败";
//        }else {
//            VipActivity.setActivity_state("已完成");
//            VipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
//            VipActivity.setModifier(user_code);
//            updateVipActivity(VipActivity);
//        }
//        return status;
//    }

    public ArrayList userExecuteDetail(String corp_code, String activity_vip_code, String user_code) throws Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_allocation);
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("activity_vip_code", activity_vip_code);
        dbObject.put("corp_code", corp_code);
        dbObject.put("user_code", user_code);
        DBCursor dbCursor = cursor.find(dbObject);
        ArrayList list = MongoUtils.dbCursorToList(dbCursor);
        return list;
    }

    @Override
    public VipActivity getVipActivityByTheme(String corp_code, String activity_theme, String isactive) throws Exception {
        return vipActivityMapper.selActivityByTheme(corp_code,activity_theme,isactive);
    }
}
