package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.VipActivityMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.common.service.redis.RedisClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
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
    private StoreService storeService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private WxTemplateService wxTemplateService;
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
    CorpParamService corpParamService;
    @Autowired
    VipPointsAdjustService vipPointsAdjustService;
    @Autowired
    RedisClient redisClient;

    private static final Logger logger = Logger.getLogger(VipActivityServiceImpl.class);

    @Override
    public PageInfo<VipActivity> selectAllActivity(int page_num, int page_size, String corp_code, String user_code, String search_value) throws Exception {
        List<VipActivity> VipActivitys;
        PageHelper.startPage(page_num, page_size);
        VipActivitys = vipActivityMapper.selectAllActivity(corp_code, user_code, search_value);
        for (VipActivity vipActivity : VipActivitys) {
            vipActivity.setIsactive(CheckUtils.CheckIsactive(vipActivity.getIsactive()));
            vipActivity.setRun_mode(CheckUtils.CheckVipActivityType(vipActivity.getRun_mode()));
        }
        PageInfo<VipActivity> page = new PageInfo<VipActivity>(VipActivitys);

        return page;
    }

    @Override
    public PageInfo<VipActivity> selectActivityAllScreen(int page_num, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("user_code", user_code);

        if (map.containsKey("created_date")) {
            JSONObject date = JSONObject.parseObject(map.get("created_date"));
            params.put("created_date_start", date.get("start").toString());
            String created_date_end = date.get("end").toString();
            if (!created_date_end.equals(""))
                created_date_end = created_date_end + " 23:59:59";
            params.put("created_date_end", created_date_end);
        }
        if (map.containsKey("start_time")) {
            JSONObject start_time = JSONObject.parseObject(map.get("start_time"));
            params.put("start_time_start", start_time.get("start").toString());
            String start_time_end = start_time.get("end").toString();
            if (!start_time_end.equals(""))
                start_time_end = start_time_end + " 23:59:59";
            params.put("start_time_end", start_time_end);

        }
        if (map.containsKey("end_time")) {
            JSONObject end_time = JSONObject.parseObject(map.get("end_time"));
            params.put("end_time_start", end_time.get("start").toString());
            String end_time_end = end_time.get("end").toString();
            if (!end_time_end.equals(""))
                end_time_end = end_time_end + " 23:59:59";
            params.put("end_time_end", end_time_end);
        }
        map.remove("created_date");
        map.remove("start_time");
        map.remove("end_time");

        for(String key:map.keySet()){
            if (key.equals("activity_state")){
                map.put("activity_state",map.get("activity_state").toString().replace("'",""));
            }
        }
        params.put("map", map);

        PageHelper.startPage(page_num, page_size);
        List<VipActivity> list1 = vipActivityMapper.selectActivityScreen(params);
        for (VipActivity vipActivity : list1) {
            vipActivity.setIsactive(CheckUtils.CheckIsactive(vipActivity.getIsactive()));
            vipActivity.setRun_mode(CheckUtils.CheckVipActivityType(vipActivity.getRun_mode()));
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
        JSONObject jsonObject = JSONObject.parseObject(message);

        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_code = "A" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(now);
        String activity_state = "0";
        VipActivity vipActivity = WebUtils.JSON2Bean(jsonObject, VipActivity.class);
        VipActivity vipActivity1 = this.getVipActivityByTheme(corp_code, vipActivity.getActivity_theme());
        String app_id = jsonObject.get("app_id").toString().trim();
        String app_name = jsonObject.get("app_name").toString().trim();

        //转run_scope 格式
        String run_scope = jsonObject.get("run_scope").toString();
        JSONObject scope_obj = JSONObject.parseObject(run_scope);
        String store_codes = scope_obj.getString("store_code");
        JSONArray store_array = JSONArray.parseArray(store_codes);
        String store_code = "";
        for (int i = 0; i < store_array.size(); i++) {
            store_code = store_code + store_array.getJSONObject(i).getString("store_code") + ",";
        }
        if (store_code.endsWith(",")) {
            store_code = store_code.substring(0, store_code.length() - 1);
        }
        String area_codes = scope_obj.getString("area_code");
        String brand_codes = scope_obj.getString("brand_code");
        scope_obj.put("store_code", store_code);
        scope_obj.put("area_code", "");
        scope_obj.put("brand_code", "");
        if (store_code.isEmpty()){
            JSONArray area_array = JSONArray.parseArray(area_codes);
            String area_code = "";
            for (int i = 0; i < area_array.size(); i++) {
                area_code = area_code + area_array.getJSONObject(i).getString("area_code") + ",";
            }
            if (area_code.endsWith(",")) {
                area_code = area_code.substring(0, area_code.length() - 1);
            }
            scope_obj.put("area_code", area_code);
            JSONArray brand_array = JSONArray.parseArray(brand_codes);
            String brand_code = "";
            for (int i = 0; i < brand_array.size(); i++) {
                brand_code = brand_code + brand_array.getJSONObject(i).getString("brand_code") + ",";
            }
            if (brand_code.endsWith(",")) {
                brand_code = brand_code.substring(0, brand_code.length() - 1);
            }
            scope_obj.put("brand_code", brand_code);
        }
        vipActivity.setRun_scope(scope_obj.toString());

        if (vipActivity1 == null) {
            vipActivity.setApp_id(app_id);
            vipActivity.setActivity_code(activity_code);
            vipActivity.setModifier(user_id);
            vipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipActivity.setCreater(user_id);
            vipActivity.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipActivity.setIsactive(Common.IS_ACTIVE_Y);
            vipActivity.setActivity_state(activity_state);
            vipActivity.setApp_name(app_name);
            vipActivity.setTask_code("");
            vipActivity.setSms_code("");
            vipActivity.setTarget_vips(jsonObject.getString("target_vips")==null?"":jsonObject.getString("target_vips"));
            vipActivity.setTarget_vips_count(jsonObject.getString("target_vips_count")==null?"":jsonObject.getString("target_vips_count"));
            vipActivity.setVip_condition(jsonObject.getString("screen")==null?"":jsonObject.getString("screen"));

            int info = 0;
            info = vipActivityMapper.insertActivity(vipActivity);
            VipActivity VipActivity1 = getActivityByCode(activity_code);
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
    public String update(String message, String user_code) throws Exception {
        String result = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_code = jsonObject.get("activity_code").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String app_name = jsonObject.get("app_name").toString().trim();
        Date now = new Date();
        VipActivity vipActivity = WebUtils.JSON2Bean(jsonObject, VipActivity.class);
        String app_id = jsonObject.get("app_id").toString().trim();
        VipActivity vipActivity2 = this.getVipActivityByTheme(corp_code, vipActivity.getActivity_theme());
        if (vipActivity2 == null || vipActivity2.getActivity_code().equals(activity_code)) {
            if (app_id == null || app_id.equals("")) {
                vipActivity.setApp_id("");
            } else {
                vipActivity.setApp_id(app_id);
            }
            //转run_scope 格式
            String run_scope = jsonObject.get("run_scope").toString();
            JSONObject scope_obj = JSONObject.parseObject(run_scope);
            String store_codes = scope_obj.getString("store_code");
            JSONArray store_array = JSONArray.parseArray(store_codes);
            String store_code = "";
            for (int i = 0; i < store_array.size(); i++) {
                store_code = store_code + store_array.getJSONObject(i).getString("store_code") + ",";
            }
            if (store_code.endsWith(",")) {
                store_code = store_code.substring(0, store_code.length() - 1);
            }
            String area_codes = scope_obj.getString("area_code");
            String brand_codes = scope_obj.getString("brand_code");
            JSONObject scope_obj1 = new JSONObject();
            scope_obj1.put("store_code", store_code);
            scope_obj1.put("area_code", "");
            scope_obj1.put("brand_code", "");
            if (store_code.isEmpty()){
                JSONArray area_array = JSONArray.parseArray(area_codes);
                String area_code = "";
                for (int i = 0; i < area_array.size(); i++) {
                    area_code = area_code + area_array.getJSONObject(i).getString("area_code") + ",";
                }
                if (area_code.endsWith(",")) {
                    area_code = area_code.substring(0, area_code.length() - 1);
                }
                scope_obj1.put("area_code", area_code);
                JSONArray brand_array = JSONArray.parseArray(brand_codes);
                String brand_code = "";
                for (int i = 0; i < brand_array.size(); i++) {
                    brand_code = brand_code + brand_array.getJSONObject(i).getString("brand_code") + ",";
                }
                if (brand_code.endsWith(",")) {
                    brand_code = brand_code.substring(0, brand_code.length() - 1);
                }
                scope_obj1.put("brand_code", brand_code);
            }
            logger.info("=================scope_obj1="+scope_obj1.toString());
            vipActivity.setRun_scope(scope_obj1.toString());
            vipActivity.setApp_name(app_name);
            vipActivity.setActivity_code(activity_code);
            vipActivity.setModifier(user_code);
            vipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipActivity.setTarget_vips(jsonObject.getString("target_vips")==null?"":jsonObject.getString("target_vips"));
            vipActivity.setTarget_vips_count(jsonObject.getString("target_vips_count")==null?"":jsonObject.getString("target_vips_count"));
            vipActivity.setVip_condition(jsonObject.getString("screen")==null?"":jsonObject.getString("screen"));

            int info = 0;
            info = vipActivityMapper.updateActivity(vipActivity);
            if (info > 0) {
                JSONObject screen_obj = JSONObject.parseObject(vipActivity.getVip_condition());
                result = Common.DATABEAN_CODE_SUCCESS;
                VipTask vipTask = vipTaskService.selectVipTaskByTaskTypeAndTitle(corp_code,"activity",activity_code);
                if(vipTask!=null){
                    vipTask.setCorp_code(corp_code);
                    vipTask.setTask_title(vipActivity.getActivity_theme());
                    vipTask.setStart_time(vipActivity.getStart_time());
                    vipTask.setEnd_time(vipActivity.getEnd_time());
                    vipTask.setTask_status(Common.ACTIVITY_STATUS_0);
                    vipTask.setTarget_vips(screen_obj.get("screen").toString());
                    vipTask.setTarget_vips_(vipActivity.getTarget_vips());
                    vipTask.setTask_condition(activity_code);
                    vipTask.setTask_description(vipActivity.getActivity_desc());
                    vipTask.setApp_id(vipActivity.getApp_id());
                    vipTask.setIs_send_notice("N");
                    vipTask.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                    vipTask.setModifier(user_code);
                    vipTask.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
                    vipTask.setCreater(user_code);
                    vipTask.setIsactive(Common.IS_ACTIVE_Y);
                    vipTaskService.updateVipTask(vipTask,user_code,"","");
                }
            } else {
                result = "编辑失败";
            }
        } else {
            result = "该企业已存在该活动标题";
        }
        return result;


    }

    @Override
    public int updateVipActivity(VipActivity vipActivity) throws Exception {
        return vipActivityMapper.updateActivity(vipActivity);
    }

    @Override
    public VipActivity getActivityById(int id) throws Exception {
        VipActivity vipActivity = vipActivityMapper.selActivityById(id);
        return vipActivity;
    }

    @Override
    public VipActivity selActivityByCode(String activity_vip_code) throws Exception {
        VipActivity vipActivity = vipActivityMapper.selActivityByCode(activity_vip_code);

        if (vipActivity != null) {
            String corp_code = vipActivity.getCorp_code();
            if (null != vipActivity.getTask_code() && vipActivity.getTask_code().endsWith(",")) {
                vipActivity.setTask_code(vipActivity.getTask_code().substring(0, vipActivity.getTask_code().length() - 1));
            }
            if (null != vipActivity.getSms_code() && vipActivity.getSms_code().endsWith(",")) {
                vipActivity.setSms_code(vipActivity.getSms_code().substring(0, vipActivity.getSms_code().length() - 1));
            }
            if (vipActivity.getRun_scope() != null && !vipActivity.getRun_scope().equals("")) {
                String activity_state = vipActivity.getActivity_state();
                if (activity_state.equals("0")){
                    JSONObject obj = JSON.parseObject(vipActivity.getRun_scope());
                    String store_code = obj.getString("store_code");
                    String area_code = obj.getString("area_code");
                    String brand_code = obj.getString("brand_code");
                    JSONArray store_array = new JSONArray();
                    if (store_code != null && !store_code.equals("")) {
                        String[] codes = store_code.split(",");
                        for (int i = 0; i < codes.length; i++) {
                            JSONObject store_obj = new JSONObject();
                            Store store = storeService.getStoreByCode(corp_code, codes[i], Common.IS_ACTIVE_Y);
                            if (store != null) {
                                store_obj.put("store_code", codes[i]);
                                store_obj.put("store_name", store.getStore_name());
                                store_array.add(store_obj);
                            }
                        }
                    }
                    obj.put("store_code", store_array);
                    JSONArray area_array = new JSONArray();
                    if (area_code != null && !area_code.equals("")) {
                        String[] codes = area_code.split(",");
                        for (int i = 0; i < codes.length; i++) {
                            JSONObject store_obj = new JSONObject();
                            Area area = areaService.getAreaByCode(corp_code, codes[i], Common.IS_ACTIVE_Y);
                            if (area != null) {
                                store_obj.put("area_code", codes[i]);
                                store_obj.put("area_name", area.getArea_name());
                                area_array.add(store_obj);
                            }
                        }
                    }
                    obj.put("area_code", area_array);
                    JSONArray brand_array = new JSONArray();
                    if (brand_code != null && !brand_code.equals("")) {
                        String[] codes = brand_code.split(",");
                        for (int i = 0; i < codes.length; i++) {
                            JSONObject store_obj = new JSONObject();
                            Brand store = brandService.getBrandByCode(corp_code, codes[i], Common.IS_ACTIVE_Y);
                            if (store != null) {
                                store_obj.put("brand_code", codes[i]);
                                store_obj.put("brand_name", store.getBrand_name());
                                brand_array.add(store_obj);
                            }
                        }
                    }
                    obj.put("brand_code", brand_array);
                    vipActivity.setRun_scope(obj.toString());
                }else {
                    List<Store> stores = getActivityStore(corp_code,vipActivity.getRun_scope());
                    vipActivity.setStore_count(String.valueOf(stores.size()));
                }

                String target_vips = vipActivity.getTarget_vips();
                int target_vip_count = 0;
                DataBox dataBox = vipGroupService.vipScreenBySolr(JSONArray.parseArray(target_vips), corp_code, "1", "3", Common.ROLE_GM, "", "", "", "","","");
                if (dataBox.status.toString().equals("SUCCESS")) {
                    String message1 = dataBox.data.get("message").value;
                    JSONObject msg_obj = JSONObject.parseObject(message1);
                    target_vip_count = msg_obj.getInteger("count");
                }
                vipActivity.setTarget_vips_count(target_vip_count+"");
            }
        }
        return vipActivity;
    }

    @Override
    public VipActivity getActivityByCode(String activity_vip_code) throws Exception {
        VipActivity vipActivity = vipActivityMapper.selActivityByCode(activity_vip_code);
        return vipActivity;
    }


    @Override
    public VipActivity selActivityByCodeAndName(String activity_code) throws Exception {
        VipActivity vipActivity = vipActivityMapper.selActivityByCodeAndName(activity_code);
        return vipActivity;
    }

    @Override
    public int updActiveCodeByType(String line_code, String line_value, String corp_code, String activity_code) throws Exception {
        return vipActivityMapper.updActiveCodeByType(line_code, line_value, corp_code, activity_code);
    }

    public VipActivity getVipActivityByTheme(String corp_code, String activity_theme) throws Exception {
        return vipActivityMapper.selActivityByTheme(corp_code, activity_theme);

    }

    public List<Store> getActivityStore(String corp_code,String run_scope) throws Exception {
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

        List<Store> stores;
        if (store_code.equals("") && brand_code.equals("") && area_code.equals("")) {
            stores = storeService.getCorpStore(corp_code);
        }else {
            stores = storeService.getStoreAndBrandArea(corp_code,area_code,brand_code,store_code,"");
        }

        return stores;
    }

    @Override
    public String saveActivity(VipActivity vipActivity, String user_code,String create_task,String group_code,String role_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        String activity_code = vipActivity.getActivity_code();
        String corp_code = vipActivity.getCorp_code();
        String end_time = vipActivity.getEnd_time();
        String start_time = vipActivity.getStart_time();

        String et = Common.DATETIME_FORMAT_NUM.format(Common.DATETIME_FORMAT.parse(end_time));
        String time = Common.DATETIME_FORMAT_NUM.format(new Date());

        if (TimeUtils.compareDateTime(et, time, Common.DATETIME_FORMAT_NUM)) {
            return "活动结束时间小于当前时间";
        }

        //创建对应的会员任务
        if (create_task.equals("Y")){
            JSONObject screen_obj = JSONObject.parseObject(vipActivity.getVip_condition());

            VipTask vipTask = new VipTask();
            String vip_task_code="VT"+corp_code+Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
            vipTask.setCorp_code(corp_code);
            vipTask.setTask_code(vip_task_code);
            vipTask.setTask_title(vipActivity.getActivity_theme());
            vipTask.setStart_time(vipActivity.getStart_time());
            String end_time1 = vipActivity.getEnd_time();

            vipTask.setEnd_time(end_time1);
            vipTask.setTask_type("activity");
            vipTask.setTask_status(Common.ACTIVITY_STATUS_0);
            vipTask.setTarget_vips(screen_obj.get("screen").toString());
            vipTask.setTarget_vips_(vipActivity.getTarget_vips());
            vipTask.setTask_condition(activity_code);
            vipTask.setTask_description(vipActivity.getActivity_desc());
            vipTask.setApp_id(vipActivity.getApp_id());
            vipTask.setIs_send_notice("N");
            vipTask.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            vipTask.setModifier(user_code);
            vipTask.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
            vipTask.setCreater(user_code);
            vipTask.setIsactive(Common.IS_ACTIVE_Y);

            //创建对应的会员任务,如果任务存在则更新
            VipTask vipTask1= vipTaskService.selectVipTaskByTaskTypeAndTitle(corp_code,"activity",activity_code);
            if(vipTask1!=null){
                vipTask.setId(vipTask1.getId());
                status= vipTaskService.updateVipTask(vipTask,user_code,group_code,role_code);
            }else{
                status = vipTaskService.inserVipTask(vipTask,user_code,group_code,role_code);
            }
            if (!status.equals(Common.DATABEAN_CODE_SUCCESS))
                return status;
            vipActivity.setVip_task(vip_task_code);
        }
        vipActivity.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
        vipActivity.setModifier(user_code);
        updateVipActivity(vipActivity);
        return status;
    }

    @Override
    public String executeActivity(VipActivity vipActivity, String user_code,String create_task,String group_code,String role_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        String activity_code = vipActivity.getActivity_code();
        String corp_code = vipActivity.getCorp_code();
        String end_time = vipActivity.getEnd_time();
        String start_time = vipActivity.getStart_time();

        String task_code = vipActivity.getTask_code();
        String sms_code = vipActivity.getSms_code();
        Date now = new Date();

        String st = Common.DATETIME_FORMAT_NUM.format(Common.DATETIME_FORMAT.parse(start_time));
        String et = Common.DATETIME_FORMAT_NUM.format(Common.DATETIME_FORMAT.parse(end_time));
        String time = Common.DATETIME_FORMAT_NUM.format(new Date());

        if (TimeUtils.compareDateTime(et, time, Common.DATETIME_FORMAT_NUM)) {
            return "活动结束时间小于当前时间";
        }
        List<VipFsend> vipFsends = vipFsendService.getSendByActivityCode(corp_code,activity_code);
        for (int i = 0; i < vipFsends.size(); i++) {
            VipFsend vipFsend = vipFsends.get(i);
            if (vipFsend.getCheck_status().equals("N")){
                if (vipFsend.getSend_time() != null && !vipFsend.getSend_time().isEmpty()){
                    if(TimeUtils.compareDateTime(vipFsend.getSend_time(),Common.DATETIME_FORMAT.format(new Date()),Common.DATETIME_FORMAT)){
                        return vipFsend.getSms_code()+"已经超过发送时间，审核失败";
                    }
                }
                String status_fsend = vipFsendService.checkVipFsend(vipFsends.get(i),user_code);
                if (!status.equals(Common.DATABEAN_CODE_SUCCESS)){
                    return status_fsend;
                }
            }
        }
        if (!task_code.trim().equals("")) {
            status = executeTask(vipActivity, user_code);
            if (!status.equals(Common.DATABEAN_CODE_SUCCESS))
                return status;
        }
        
        if (TimeUtils.compareDateTime(st, time, Common.DATETIME_FORMAT_NUM)) {
            vipActivity.setStart_time(Common.DATETIME_FORMAT.format(new Date()));

            //更新活动状态activity_state
            vipActivity.setActivity_state("1");
            vipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
            //修改创建时间为开始执行时间
            vipActivity.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipActivity.setModifier(user_code);
            updateVipActivity(vipActivity);

            if (vipActivity.getRun_mode().equals("recruit")) {
                //制度表增加活动的招募规则
                creatVipRules(activity_code, corp_code);
            }
            if (vipActivity.getRun_mode().equals("coupon")) {
                VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
                if (detail.getSend_coupon_type().equals("batch")) {
                    insertSchedule2(activity_code, corp_code, user_code);
                }else if (detail.getSend_coupon_type().equals("anniversary")) {
                    VipActivityDetailAnniversary detail1= new VipActivityDetailAnniversary();
                    detail1.setIsactive(Common.IS_ACTIVE_Y);
                    detail1.setModified_date(Common.DATETIME_FORMAT.format(now));
                    detail1.setActivity_code(activity_code);
                    vipActivityDetailService.updateDetailAnniversary(detail1);
                }else  if (detail.getSend_coupon_type().equals("consume")) {
                    VipActivityDetailConsume detail2= new VipActivityDetailConsume();
                    detail2.setIsactive(Common.IS_ACTIVE_Y);
                    detail2.setModified_date(Common.DATETIME_FORMAT.format(now));
                    detail2.setActivity_code(activity_code);
                    vipActivityDetailService.updateDetailConsume(detail2);
                }
            }
            if(vipActivity.getRun_mode().equals("online_apply")){//线上报名活动
                VipActivityDetailApply vipActivityDetailApply=new VipActivityDetailApply();
                vipActivityDetailApply.setIsactive(Common.IS_ACTIVE_Y);
                vipActivityDetailApply.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipActivityDetailApply.setActivity_code(activity_code);
                vipActivityDetailService.updateDetailApply(vipActivityDetailApply);

                VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
                String present_time = detail.getPresent_time();
                JSONObject present_time_obj = JSONObject.parseObject(present_time);
                if (present_time_obj.getString("type").equals("timing")){
                    insertSchedule4(activity_code,corp_code,user_code,present_time_obj.getString("date"));
                }
            }
        }else {
            //更新活动状态activity_state
            vipActivity.setActivity_state("0.5");
            vipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
            //修改创建时间为开始执行时间
            vipActivity.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipActivity.setModifier(user_code);
            updateVipActivity(vipActivity);
            //创建schedule，开始任务
            insertSchedule(activity_code, corp_code, start_time, user_code,"StartActivity");
        }

        String point = "";
        if (vipActivity.getRun_mode().equals("coupon")) {
            VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
            if (detail.getSend_coupon_type().equals("batch")) {
                point = detail.getPresent_point();
            }else if (detail.getSend_coupon_type().equals("card")) {
                String coupon_type = detail.getCoupon_type();
                JSONArray array = JSONArray.parseArray(coupon_type);
                for (int i = 0; i < array.size(); i++) {
                    if (array.getJSONObject(i).containsKey("present_point"))
                        point += array.getJSONObject(i).getString("present_point");
                }
            }else if (detail.getSend_coupon_type().equals("anniversary")) {
                List<VipActivityDetailAnniversary> detail1s = vipActivityDetailService.selActivityDetailAnniversary(activity_code);
                for (int i = 0; i < detail1s.size(); i++) {
                    point += detail1s.get(i).getSend_points();
                }
            }else  if (detail.getSend_coupon_type().equals("consume")) {
                List<VipActivityDetailConsume> detail1s = vipActivityDetailService.selActivityDetailConsume(activity_code);
                for (int i = 0; i < detail1s.size(); i++) {
                    point += detail1s.get(i).getSend_points();
                }
            }
        }
        if (vipActivity.getRun_mode().equals("register")) {
            VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
            String registe_data = detail.getRegister_data();
            JSONArray array = JSONArray.parseArray(registe_data);
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i).getJSONObject("present");
                if (object.containsKey("point"))
                    point += object.getString("point");
            }
        }
        if (vipActivity.getRun_mode().equals("online_apply")) {
            VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
            point = detail.getPresent_point();
        }

        if (!point.equals("")){
            VipPointsAdjust pointsAdjust = new VipPointsAdjust();
            pointsAdjust.setIsactive("Y");
            pointsAdjust.setBill_name(vipActivity.getActivity_theme());
            pointsAdjust.setBill_code(activity_code);
            pointsAdjust.setBill_state("1");
            pointsAdjust.setBill_type("1");
            pointsAdjust.setAdjust_time(vipActivity.getStart_time());
            pointsAdjust.setCorp_code(corp_code);
            pointsAdjust.setCreated_date(Common.DATETIME_FORMAT.format(now));
            pointsAdjust.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipPointsAdjustService.insertPointsAdjust(pointsAdjust);
        }
        //开始执行会员任务
        if (vipActivity.getVip_task() != null && !vipActivity.getVip_task().equals("")){
            VipTask vipTask1=vipTaskService.selectByTaskCode(vipActivity.getVip_task());
            String flag = vipTaskService.executeVipTask(vipTask1,user_code,group_code,role_code);
        }

        redisClient.set( "Vip Activity Status"+corp_code+ activity_code , "Y", 180l);
        //活动结束  定时任务
        insertSchedule(activity_code, corp_code, end_time, user_code,"changeStatus");
        return status;
    }


    //活动开始，执行任务
    public String executeTask(VipActivity vipActivity, String user_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        String activity_code = vipActivity.getActivity_code();
        String corp_code = vipActivity.getCorp_code();
        String task_code = vipActivity.getTask_code();

        //获取执行人
        String store_codes = "";
        List<Store> stores = getActivityStore(corp_code,vipActivity.getRun_scope());
        for (int i = 0; i < stores.size(); i++) {
            store_codes = store_codes + stores.get(i).getStore_code() + ",";
        }
        List<User> users = userService.selUserByStoreCode(corp_code,"",store_codes,null,Common.ROLE_SM,"");
        if (users.size() < 1) {
            return "该范围下没有执行人，无法执行";
        }
        String[] task_codes = task_code.split(",");
        for (int i = 0; i < task_codes.length; i++) {
            String task_code1 = task_codes[i];
            Task task = taskService.getTaskForId(corp_code, task_code1);
            String target_start_time = task.getTarget_start_time();
            System.out.println("--------------开始时间---------" + target_start_time);

            String st = Common.DATETIME_FORMAT_DAY.format(Common.DATETIME_FORMAT_DAY.parse(target_start_time));
            String now = Common.DATETIME_FORMAT_DAY.format(new Date());

            if (!TimeUtils.compareDateTime(now,st,Common.DATETIME_FORMAT_DAY)) {
                return "任务开始时间小于当前时间";
            }
        }
        insertSchedule3(activity_code,corp_code,store_codes,user_code);
        return status;
    }

    //活动开始，分配任务
    public String allocTask(String corp_code,String activity_code,String store_code1, String user_code,String task_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        //获取执行人
        String user_codes = "";
        String phones = "";
        String store_codes = "";
        String[] store_array = store_code1.split(",");
        for (int i = 0; i < store_array.length; i++) {
            String store = store_array[i];
            List<User> userList = userService.selUserByStoreCode(corp_code, "", store, null, Common.ROLE_SM,"");
            if (userList.size() > 0) {
                for (int j = 0; j < userList.size(); j++) {
                    user_codes = user_codes + userList.get(j).getUser_code() + ",";
                    phones = phones + userList.get(j).getPhone() + ",";
                    store_codes = store_codes + store + ",";
                }
            }
        }

        String[] task_codes = task_code.split(",");
        for (int i = 0; i < task_codes.length; i++) {
            String task_code1 = task_codes[i];
            Task task = taskService.getTaskForId(corp_code, task_code1);

            taskService.taskAllocation(task, phones, user_codes, store_codes, user_code, activity_code);
        }
        return status;
    }


    //活动开始，创建群发定时任务
    public String executeFsend(VipActivity vipActivity,String sms_code, String user_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;

        String activity_code = vipActivity.getActivity_code();
        String corp_code = vipActivity.getCorp_code();

        String[] sms_codes = sms_code.split(",");
        for (int i = 0; i < sms_codes.length; i++) {
            String sms_code1 = sms_codes[i];
            VipFsend vipFsend = vipFsendService.getVipFsendInfoByCode(corp_code, sms_code1);

            String send_time = vipFsend.getSend_time();

            String st = Common.DATETIME_FORMAT_NUM.format(Common.DATETIME_FORMAT.parse(send_time));
            String now = Common.DATETIME_FORMAT_NUM.format(new Date());
            logger.info("-------------executeFsend:"+st+"----now:"+now);

//            st.compareTo(now) < 0
            if (st.compareTo(now) <= 0) {
                System.out.println("======"+TimeUtils.compareDateTime(st,now,Common.DATETIME_FORMAT_NUM));
                return "群发时间小于当前时间";
            }
            if (vipFsend.getSend_type().equals(Common.SEND_TYPE_WX)){
                String app_id = vipFsend.getApp_id();
                List<WxTemplate> wxTemplates = wxTemplateService.selectTempByAppId(app_id,"",Common.TEMPLATE_NAME_1);
                if (wxTemplates.size() < 1)
                    return "未设置群发模板";
            }
        }
        return status;
    }

    public void creatVipRules(String activity_code, String corp_code) throws Exception {
        VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
        String recuit = detail.getRecruit();
        JSONArray array = JSONArray.parseArray(recuit);
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String vip_card_type_code = obj.getString("vip_card_type_code");
            String join_threshold = obj.getString("join_threshold");
            VipCardType card = vipCardTypeService.getVipCardTypeByCode(detail.getCorp_code(), vip_card_type_code, Common.IS_ACTIVE_Y);
            List<VipRules> vipRules1 = vipRulesService.getViprulesByCardTypeCode(corp_code,vip_card_type_code);
            if (vipRules1.size() > 0){
                VipRules vipRules = vipRules1.get(0);
                vipRules.setActivity_code(activity_code);
                vipRules.setJoin_threshold(join_threshold);
                vipRules.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                vipRules.setModifier(detail.getCreater());
                vipRules.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
                vipRules.setCreater(detail.getCreater());
                vipRulesService.insert(vipRules);
            }else {
                VipRules vipRules = new VipRules();
                vipRules.setActivity_code(activity_code);
                vipRules.setCorp_code(corp_code);
                vipRules.setVip_card_type_code(vip_card_type_code);
                vipRules.setVip_type(card.getVip_card_type_name());
                vipRules.setKeep_grade_condition("{\"price\":{\"start\":\"\",\"end\":\"\"},\"time\":\"\"}");
                vipRules.setDegree(card.getDegree());
                vipRules.setJoin_threshold(join_threshold);
                vipRules.setIsactive("Y");
                vipRules.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                vipRules.setModifier(detail.getCreater());
                vipRules.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
                vipRules.setCreater(detail.getCreater());
                vipRulesService.insert(vipRules);
            }
        }
    }

    /**
     * 获取活动未结束任务(添加参与门店时专用)
     * 策略补充后会自动分配任务
     */
    @Override
    public List<Task> unComplTask(String corp_code, String activity_code) throws Exception {
        VipActivity vipActivity = getActivityByCode(activity_code);

        String now = Common.DATETIME_FORMAT_DAY.format(new Date());
        List<Task> unComplTasks = new ArrayList<Task>();
        if (vipActivity.getTask_code() != null && !vipActivity.getTask_code().equals("")) {
            String[] task_codes = vipActivity.getTask_code().split(",");
            for (int i = 0; i < task_codes.length; i++) {
                Task task = taskService.getTaskForId(corp_code, task_codes[i]);
                Boolean bool = TimeUtils.compareDateTime(task.getTarget_end_time(), now, Common.DATETIME_FORMAT_DAY);
                if (bool == false){
                    unComplTasks.add(task);
                }
            }
        }
        return unComplTasks;
    }

    /**
     * 给新增门店分配未完成任务
     *
     * @param task_code    未完成任务
     * @param store_codes1 新增门店
     * @param user_code    操作人
     */
    public String allocUnComplTask(String corp_code, String activity_code, String task_code, String store_codes1, String user_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        String[] store_code = store_codes1.split(",");

        int count = 0;
        JSONArray array = new JSONArray();
        for (int i = 0; i < store_code.length; i++) {
            List<User> userList = userService.selUserByStoreCode(corp_code, "", store_code[i], null, Common.ROLE_SM,"");
            if (userList.size() > 0) {
                count += userList.size();
                for (int j = 0; j < userList.size(); j++) {
                    JSONObject object = new JSONObject();
                    object.put("user_code", userList.get(j).getUser_code());
                    object.put("store_code", store_code[i]);
                    object.put("phone", userList.get(j).getPhone());
                    array.add(object);
                }
            }
        }

        if (count < 1) {
            return "新增店铺下没有员工，无法执行";
        }
        String[] task_codes = task_code.split(",");
        for (int i = 0; i < task_codes.length; i++) {
            String task_code1 = task_codes[i];

            Task task = taskService.getTaskForId(corp_code, task_code1);
            taskService.taskAllocation1(task, array, user_code, activity_code);
        }
        return status;
    }

    /**
     * 获取活动下未分配未完成任务的门店
     */
    public String getUnAllocStore(VipActivity vipActivity) throws Exception {
        String corp_code = vipActivity.getCorp_code();
        String task_code = vipActivity.getTask_code();
        String now = Common.DATETIME_FORMAT_DAY.format(new Date());

        String store_codes = "";
        if (task_code != null && !task_code.equals("")) {
            String[] task_codes = task_code.split(",");
            String task_code1 = "";

            for (int i = 0; i < task_codes.length; i++) {
                Task task = taskService.getTaskForId(corp_code, task_codes[i]);
                if (task != null){
                    //判断任务是否已结束
                    Boolean bool = TimeUtils.compareDateTime(task.getTarget_end_time(), now, Common.DATETIME_FORMAT_DAY);
                    if (bool != true)
                        task_code1 = task_code1 + task.getTask_code() + ",";
                }
            }
            if (!task_code1.equals("")) {
                String[] task_codes1 = task_code1.split(",");
                List<Store> stores = getActivityStore(corp_code, vipActivity.getRun_scope());

                for (int m = 0; m < task_codes1.length; m++) {
                    String task_code_new = task_codes1[m];

                    List<TaskAllocation> taskAlloc = taskService.selTaskAllocation(corp_code, task_code_new);

                    for (int i = 0; i < stores.size(); i++) {
                        String code = stores.get(i).getStore_code();
                        List<User> userList = userService.selUserByStoreCode(corp_code, "", code, null, Common.ROLE_SM,"");
                        if (userList.size() > 0) {
                            String user_code = userList.get(0).getUser_code();
                            int flag = 0;
                            for (int j = 0; j < taskAlloc.size(); j++) {
                                if (taskAlloc.get(j).getUser_code().equals(user_code)){
                                    flag = 1;
                                    break;
                                }
                            }
                            if (flag == 0)
                                store_codes = store_codes + code + ",";
                        }

                    }
                }
            }
        }

        if (store_codes.endsWith(","))
            store_codes = store_codes.substring(0, store_codes.length() - 1);
        return store_codes;
    }

    /**
     * 创建定时任务（活动到期转为“已结束”）
     *
     * @param activity_code
     */
    public void insertSchedule(String activity_code, String corp_code, String end_time, String user_code,String flag) throws Exception {

        String corn_expression = TimeUtils.getCron(Common.DATETIME_FORMAT.parse(end_time));
        JSONObject func = new JSONObject();
        func.put("method", "vipActivity");
        func.put("corp_code", corp_code);
        func.put("user_code", user_code);
        func.put("code", activity_code);
        //创建schedule，结束时间时自动更新状态

        ScheduleJob scheduleJob = scheduleJobService.selectScheduleByJob(flag+activity_code, activity_code);
        if (scheduleJob == null) {
            scheduleJob = new ScheduleJob();
            scheduleJob.setJob_name(flag+activity_code);
            scheduleJob.setJob_group(activity_code);
            scheduleJob.setFunc(func.toString());
            scheduleJob.setCron_expression(corn_expression);
            scheduleJob.setStatus("N");
            scheduleJobService.insert(scheduleJob);
        } else {
            scheduleJob.setFunc(func.toString());
            scheduleJob.setCron_expression(corn_expression);
            scheduleJobService.update(scheduleJob);
        }
    }

    /**
     * 创建定时任务（批量发券活动）
     *
     * @param activity_code
     */
    public void insertSchedule2(String activity_code, String corp_code, String user_code) throws Exception {
        String corn_expression = TimeUtils.getCron(TimeUtils.getLastMin(new Date(),3));

        JSONObject func = new JSONObject();
        func.put("method", "vipActivity");
        func.put("corp_code", corp_code);
        func.put("user_code", user_code);
        func.put("code", activity_code);
        //创建schedule，结束时间时自动更新状态

        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJob_name("sendCoupon"+activity_code);
        scheduleJob.setJob_group(activity_code);
        scheduleJob.setFunc(func.toString());
        scheduleJob.setCron_expression(corn_expression);
        scheduleJob.setStatus("N");
        scheduleJobService.insert(scheduleJob);
    }


    /**
     * 创建定时任务（活动分配任务）
     *
     * @param activity_code
     */
    public void insertSchedule3(String activity_code, String corp_code,String store_codes, String user_code) throws Exception {
        String corn_expression = TimeUtils.getCron(TimeUtils.getLastMin(new Date(),1));

        JSONObject func = new JSONObject();
        func.put("method", "vipActivity");
        func.put("corp_code", corp_code);
        func.put("user_code", user_code);
        func.put("code", store_codes);
        //创建schedule，结束时间时自动更新状态

        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJob_name("allocTask"+activity_code);
        scheduleJob.setJob_group(activity_code);
        scheduleJob.setFunc(func.toString());
        scheduleJob.setCron_expression(corn_expression);
        scheduleJob.setStatus("N");
        scheduleJobService.insert(scheduleJob);
    }

    /**
     * 创建定时任务（批量发券活动）
     *
     * @param activity_code
     */
    public void insertSchedule4(String activity_code, String corp_code, String user_code,String time) throws Exception {
        String corn_expression = TimeUtils.getCron(Common.DATETIME_FORMAT.parse(time));

        JSONObject func = new JSONObject();
        func.put("method", "vipActivity");
        func.put("corp_code", corp_code);
        func.put("user_code", user_code);
        func.put("code", activity_code);
        //创建schedule，结束时间时自动更新状态

        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJob_name("applyActivity"+activity_code);
        scheduleJob.setJob_group(activity_code);
        scheduleJob.setFunc(func.toString());
        scheduleJob.setCron_expression(corn_expression);
        scheduleJob.setStatus("N");
        scheduleJobService.insert(scheduleJob);
    }


    /**
     * 提起结束，手动
     * 修改活动状态为已结束
     *
     * @param vipActivity
     */
    public void terminalAct(VipActivity vipActivity,String user_code) throws Exception {
        try {
            Date now = new Date();

            MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);

            vipActivity.setActivity_state(Common.ACTIVITY_STATUS_2);
            vipActivity.setEnd_time(Common.DATETIME_FORMAT.format(now));
            vipActivity.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipActivity.setModifier(user_code);
            updateVipActivity(vipActivity);

            VipActivityDetailConsume consume = new VipActivityDetailConsume();
            consume.setActivity_code(vipActivity.getActivity_code());
            consume.setIsactive(Common.IS_ACTIVE_N);
            vipActivityDetailService.updateDetailConsume(consume);

            VipActivityDetailAnniversary anniversary = new VipActivityDetailAnniversary();
            anniversary.setActivity_code(vipActivity.getActivity_code());
            anniversary.setIsactive(Common.IS_ACTIVE_N);
            vipActivityDetailService.updateDetailAnniversary(anniversary);

            String sms_code = vipActivity.getSms_code();
            String activity_code = vipActivity.getActivity_code();
            //删除招募活动的招募规则
            vipRulesService.deleteActivity(activity_code);

            //结束修改活动状态的定时任务
//            scheduleJobService.deleteScheduleByGroup(activity_code);
            scheduleJobService.updateSchedule("changeStatus"+activity_code, activity_code);
            scheduleJobService.updateSchedule("StartActivity"+activity_code, activity_code);
            scheduleJobService.updateSchedule("sendCoupon"+activity_code, activity_code);
            scheduleJobService.updateSchedule("allocTask"+activity_code, activity_code);

            //结束群发消息定时任务
//            if (!sms_code.equals("")) {
//                String[] codes = sms_code.split(",");
//                for (int i = 0; i < codes.length; i++) {
//                    scheduleJobService.delete(codes[i], activity_code);
//                }
//            }
            String corp_code = vipActivity.getCorp_code();

            redisClient.set( "Vip Activity Status"+corp_code+ activity_code , "N", 3000l);
            //关闭会员任务
            logger.info("================"+vipActivity.getVip_task());
            if (vipActivity.getVip_task() != null && !vipActivity.getVip_task().equals("")){
                VipTask vipTask=vipTaskService.selectByTaskCode(vipActivity.getVip_task());
                if (vipTask != null){
                    logger.info("================"+vipActivity.getVip_task());

                    vipTask.setTask_status(Common.ACTIVITY_STATUS_2);
                    vipTask.setEnd_time(Common.DATETIME_FORMAT.format(new Date()));
                    vipTask.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                    vipTask.setModifier(user_code);
                    vipTaskService.update(vipTask);
                    scheduleJobService.deleteScheduleByGroup(vipTask.getTask_code());
                }

                BasicDBObject queryCondition = new BasicDBObject();
                queryCondition.put("task_code",vipActivity.getVip_task());
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
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
    }

    /**
     * 记录报名信息
     *
     * @param detail
     */
    public String activityApply(VipActivityDetail detail, String phone, String vip_id, String corp_code) throws Exception {
        String phone_key = "10";
        String activity_code = detail.getActivity_code();
        String apply_desc = detail.getApply_desc();
        JSONObject desc_obj = JSONObject.parseObject(apply_desc);
        String apply_allow_vip = desc_obj.getString("apply_allow_vip");
        String apply_success_tips = desc_obj.getString("apply_success_tips");
        String apply_endtime = desc_obj.get("apply_endtime").toString().trim();

        String success_tips = "恭喜您，报名成功";
        if (!apply_success_tips.equals("")) {
            success_tips = apply_success_tips;
        }

        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_join_log);

        String ent_time = Common.DATETIME_FORMAT_NUM.format(Common.DATETIME_FORMAT_DAY.parse(apply_endtime));
        String now = Common.DATETIME_FORMAT_NUM.format(new Date());

        if (TimeUtils.compareDateTime(ent_time,now,Common.DATETIME_FORMAT_NUM)) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("已经超过报名截止时间");
            return dataBean.getJsonStr();
        } else {
            if (!phone.equals("")) {
                Map keyMap = new HashMap();
                keyMap.put("phone", phone);
                keyMap.put("activity_code", activity_code);
                BasicDBObject queryCondition = new BasicDBObject();
                queryCondition.putAll(keyMap);
                DBCursor dbCursor1 = cursor.find(queryCondition);
                if (dbCursor1.size() > 0) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("您已经报名过了");
                } else {
                    JSONArray screen = new JSONArray();
                    JSONObject screen_obj = new JSONObject();
                    screen_obj.put("key", phone_key);
                    screen_obj.put("type", "text");
                    screen_obj.put("value", phone);
                    screen.add(screen_obj);
                    DataBox vips = vipGroupService.vipScreenBySolr(screen, corp_code, "1", "10", Common.ROLE_GM, "", "", "", "", "", "");
                    String result = vips.data.get("message").value;
                    JSONObject object = JSONObject.parseObject(result);
                    String count = object.getString("count");
                    JSONArray jsonArray = object.getJSONArray("all_vip_list");

                    if (count.equals("0") && apply_allow_vip.equals("N")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("对不起，您还不是会员");
                    } else {
                        DBObject saveData = new BasicDBObject();
                        saveData.put("phone", phone);
                        saveData.put("activity_code", activity_code);
                        saveData.put("corp_code", corp_code);
                        saveData.put("created_date", Common.DATETIME_FORMAT.format(new Date()));
                        saveData.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));
                        saveData.put("sign_status", "N");
                        saveData.put("user_code", "");
                        saveData.put("user_name", "");
                        saveData.put("store_code", "");
                        saveData.put("store_name", "");
                        saveData.put("vip", null);

                        if (!count.equals("0")) {
                            saveData.put("vip", jsonArray.getJSONObject(0));
                            saveData.put("user_code", jsonArray.getJSONObject(0).getString("user_code"));
                            saveData.put("user_name", jsonArray.getJSONObject(0).getString("user_name"));
                            saveData.put("store_code", jsonArray.getJSONObject(0).getString("store_code"));
                            saveData.put("store_name", jsonArray.getJSONObject(0).getString("store_name"));

                            iceInterfaceAPIService.DisposeActivityData(corp_code,jsonArray.getJSONObject(0).getString("vip_id"),activity_code,"invite");
                        }
                        cursor.save(saveData);
                        dataBean.setId("1");
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setMessage(success_tips);
                    }
                }
            }
            if (!vip_id.equals("")) {
                Map keyMap = new HashMap();
                keyMap.put("vip_id", vip_id);
                keyMap.put("activity_code", activity_code);
                BasicDBObject queryCondition = new BasicDBObject();
                queryCondition.putAll(keyMap);
                DBCursor dbCursor1 = cursor.find(queryCondition);
                if (dbCursor1.size() > 0) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("您已经报名过了");
                } else {
                    iceInterfaceAPIService.DisposeActivityData(corp_code,vip_id,activity_code,"invite");

                    Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                    Data data_vip_id = new Data("vip_ids", vip_id, ValueType.PARAM);
                    Map datalist = new HashMap<String, Data>();
                    datalist.put(data_corp_code.key, data_corp_code);
                    datalist.put(data_vip_id.key, data_vip_id);
                    DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);

                    String vip = dataBox.data.get("message").value;
                    JSONObject vip_infos = JSONObject.parseObject(vip);
                    JSONArray jsonArray = vip_infos.getJSONArray("vip_info");
                    JSONObject vip_info = jsonArray.getJSONObject(0);

                    DBObject saveData = new BasicDBObject();
                    saveData.put("activity_code", activity_code);
                    saveData.put("corp_code", corp_code);
                    saveData.put("created_date", Common.DATETIME_FORMAT.format(new Date()));
                    saveData.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));
                    saveData.put("sign_status", "N");
                    saveData.put("user_code", vip_info.getString("user_code"));
                    saveData.put("user_name", vip_info.getString("user_name"));
                    saveData.put("store_code", vip_info.getString("store_code"));
                    saveData.put("store_name", vip_info.getString("store_name"));
                    saveData.put("phone", vip_info.getString("vip_phone"));
                    saveData.put("vip", vip_info);

                    cursor.save(saveData);
                    dataBean.setId("1");
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage(success_tips);
                }
            }
            return dataBean.getJsonStr();
        }
    }

    /**
     * 记录链接打开日志
     * 进入一次，记录一次，不去重，不叠加
     *
     * @param activity_code
     */
    public void insertOpenUrlRecord(String activity_code, String user_code, String store_code, String open_id, String corp_code, String app_id) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_openUrl_log);

        String store_name = "";
        String user_name = "";
        Store store = storeService.getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
        if (store != null)
            store_name = store.getStore_name();
        List<User> users = userService.userCodeExist(user_code, corp_code, Common.IS_ACTIVE_Y);
        if (users.size() > 0)
            user_name = users.get(0).getUser_name();

        DBObject saveData = new BasicDBObject();
        saveData.put("activity_code", activity_code);
        saveData.put("app_id", app_id);
        saveData.put("open_id", open_id);
        saveData.put("corp_code", corp_code);
        saveData.put("user_code", user_code);
        saveData.put("user_name", user_name);
        saveData.put("store_code", store_code);
        saveData.put("store_name", store_name);
        saveData.put("vip", null);
        saveData.put("created_date", Common.DATETIME_FORMAT.format(new Date()));

        DataBox dataBox = iceInterfaceService.getVipByOpenId(corp_code, open_id,"");
        JSONArray array = JSONArray.parseArray(dataBox.data.get("message").value);
        if (array.size() > 0)
            saveData.put("vip", array.get(0));

        cursor.save(saveData);
    }

    @Override
    public List<VipActivity> getVipActivityByAppid(String app_id, String activity_state, String run_mode) throws Exception {
        List<VipActivity> list=new ArrayList<VipActivity>();
        list=vipActivityMapper.getVipActivityByAppid(app_id,activity_state,run_mode);
        return list;
    }

    @Override
    public List<VipActivity> getVipActivityByCorpCode(String corp_code, String activity_state, String run_mode) throws Exception {
        List<VipActivity> list=new ArrayList<VipActivity>();
        list=vipActivityMapper.getVipActivityByCorpCode(corp_code,activity_state,run_mode);
        return list;
    }

    @Override
    public List<VipActivity> selectAllActivityByState(String corp_code, String user_code) throws Exception {
        List<VipActivity> list=vipActivityMapper.selectAllActivityByState(corp_code,user_code);
        return list;
    }


}

