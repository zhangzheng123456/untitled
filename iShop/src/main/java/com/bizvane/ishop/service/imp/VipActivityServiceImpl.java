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
    private TaskService taskService;
    @Autowired
    private VipFsendService vipFsendService;
    @Autowired
    private IceInterfaceService iceInterfaceService;
    @Autowired
    private ScheduleJobService scheduleJobService;
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
    public String insert(String message, String user_id) throws Exception {
        String result = null;
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_code = "A" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(now);
        String activity_state = "未执行";
        VipActivity vipActivity = WebUtils.JSON2Bean(jsonObject, VipActivity.class);
        VipActivity vipActivity1 = this.getVipActivityByTheme(corp_code, vipActivity.getActivity_theme(), Common.IS_ACTIVE_Y);
        if (vipActivity1 == null) {
            System.out.print("=======");
            vipActivity.setActivity_code(activity_code);
            vipActivity.setModifier(user_id);
            vipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipActivity.setCreater(user_id);
            vipActivity.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipActivity.setActivity_state(activity_state);
            vipActivity.setTask_code("");
            vipActivity.setSms_code("");
            int info = 0;
            info = vipActivityMapper.insertActivity(vipActivity);
            VipActivity VipActivity1 = selActivityByCode(activity_code);
            if (info > 0) {
                result = String.valueOf(VipActivity1.getActivity_code());
            } else {
                result = "新增失败";
            }
        } else {
            result = "该企业已存在该活动标题";
        }
        return result;

    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String result = "";
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        String activity_code = jsonObject.get("activity_code").toString().trim();
        VipActivity vipActivity1 = this.selActivityByCode(activity_code);
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String isactive = jsonObject.get("isactive").toString().trim();
        Date now = new Date();
        VipActivity vipActivity = WebUtils.JSON2Bean(jsonObject, VipActivity.class);
        VipActivity vipActivity2 = this.getVipActivityByTheme(corp_code, vipActivity.getActivity_theme(), Common.IS_ACTIVE_Y);
        if (vipActivity2 == null) {
            vipActivity.setActivity_code(activity_code);
            vipActivity.setModifier(user_id);
            vipActivity.setIsactive(isactive);
            vipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
            int info = 0;
            info = vipActivityMapper.updateActivity(vipActivity);
            if (info > 0) {
                result = Common.DATABEAN_CODE_SUCCESS;

            } else {
                result = "编辑失败";

            }
        } else {
            result = "该企业已存在该活动标题";
        }
        return result;


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
            DataBox dataBox = iceInterfaceService.vipScreenMethod("1", "3", corp_code, area_code, brand_code, store_code, user_code);
            String result = dataBox.data.get("message").value;
            JSONObject result_obj = JSONObject.parseObject(result);
            String count = result_obj.get("count").toString();
            VipActivity.setTarget_vips_count(count);
        } else if (type.equals("2")) {
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
            task_obj.put("store_name", store_name);
            task_obj.put("area_name", area_name);
            task_obj.put("complete_rate", complete_rate);
            task_array.add(task_obj);
        }
        result.put("userList", task_array);
        result.put("target_vips_count", target_vips_count);
        result.put("complete_vips_count", complete_vip_count);

        return result;
    }


    /**
     * 查看员工执行详情
     * @param corp_code
     * @param activity_vip_code
     * @param user_code
     * @return
     * @throws Exception
     */
    public ArrayList userExecuteDetail(String corp_code, String activity_vip_code, String user_code) throws Exception {
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
    public int updActiveCodeByType(String line_code, String line_value, String corp_code, String activity_code) throws Exception {
        return vipActivityMapper.updActiveCodeByType(line_code, line_value, corp_code, activity_code);
    }

    public VipActivity getVipActivityByTheme(String corp_code, String activity_theme, String isactive) throws Exception {
        return vipActivityMapper.selActivityByTheme(corp_code, activity_theme, isactive);

    }

    @Override
    public String executeActivity(VipActivity vipActivity,String user_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        String task_code = vipActivity.getTask_code();
        String sms_code = vipActivity.getSms_code();
        Date now = new Date();

        if (!task_code.trim().equals("")){
            status = executeTask(vipActivity,user_code);
        }
        if (!sms_code.trim().equals("")){
//            status = executeFsend(vipActivity,user_code);
        }
        //更新活动状态activity_state
        vipActivity.setActivity_state("1");
        vipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
        vipActivity.setModifier(user_code);
        updateVipActivity(vipActivity);
        return status;
    }

    //执行任务
    public String executeTask(VipActivity vipActivity,String user_code) throws Exception{
        String status = Common.DATABEAN_CODE_SUCCESS;

        String activity_code = vipActivity.getActivity_code();
        String corp_code = vipActivity.getCorp_code();
        String task_code = vipActivity.getTask_code();
        String activity_store_code = vipActivity.getActivity_store_code();

        //获取执行人
        String user_codes = "";
        String phones = "";
        List<User> userList = userService.selUserByStoreCode(corp_code,"",activity_store_code,null,"");
        if (userList.size() > 0) {
            for (int i = 0; i < userList.size(); i++) {
                user_codes = user_codes + userList.get(i).getUser_code() + ",";
                phones = phones + userList.get(i).getPhone() + ",";
            }
        }else {
            return "该范围下没有执行人，无法执行";
        }
        String[] task_codes = task_code.split(",");
        for (int i = 0; i < task_codes.length; i++) {
            String task_code1 = task_codes[i];
            Task task = taskService.getTaskForId(corp_code,task_code1);
            taskService.taskAllocation(task, phones, user_codes, user_code,activity_code);
        }
        return status;
    }

    //执行定时群发
    public String executeFsend(VipActivity vipActivity,String user_code) throws Exception{
        String status = Common.DATABEAN_CODE_SUCCESS;

        String activity_code = vipActivity.getActivity_code();
        String corp_code = vipActivity.getCorp_code();
        String sms_code = vipActivity.getSms_code();

        String[] sms_codes = sms_code.split(",");
        for (int i = 0; i < sms_codes.length; i++) {
            String sms_code1 = sms_codes[i];
            VipFsend vipFsend = vipFsendService.getVipFsendInfoByCode(corp_code, sms_code1);

            String send_time = vipFsend.getSend_time();
            String st = Common.DATETIME_FORMAT_DAY_NUM.format(Common.DATETIME_FORMAT.parse(send_time));
            String now = Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
            long aa = Integer.parseInt(st);
            long bb = Integer.parseInt(now);
            if (aa < bb) {
                return "群发时间小于当前时间";
            }
        }
        for (int i = 0; i < sms_codes.length; i++) {
            String sms_code1 = sms_codes[i];
            VipFsend vipFsend = vipFsendService.getVipFsendInfoByCode(corp_code, sms_code1);
            String send_time = vipFsend.getSend_time();
            String st = Common.DATETIME_FORMAT_DAY_NUM.format(Common.DATETIME_FORMAT.parse(send_time));
            String job_name = sms_code1;
            String job_group = activity_code;

            String corn_expression = "s min h d m ?";
            String month = st.substring(4,6);
            String day = st.substring(6,8);
            String hour = st.substring(8,10);
            String min = st.substring(10,12);
            String ss = st.substring(12,14);
            corn_expression = corn_expression.replace("s",ss).replace("min",min).replace("h",hour).replace("d",day).replace("m",month);

            JSONObject func = new JSONObject();
            func.put("method","sendSMS");
            func.put("corp_code",corp_code);
            func.put("user_code",user_code);
            func.put("code",sms_code1);
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setJob_name(job_name);
            scheduleJob.setJob_group(job_group);
            scheduleJob.setFunc(func.toString());
            scheduleJob.setCron_expression(corn_expression);
            scheduleJobService.insert(scheduleJob);
        }
        return status;
    }
}
