package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.ActivityVipMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OssUtils;
import com.bizvane.ishop.utils.WebUtils;
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
public class ActivityVipServiceImpl implements ActivityVipService {

    @Autowired
    ActivityVipMapper activityVipMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private ValidateCodeService validateService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private IceInterfaceService iceInterfaceService;
    @Autowired
    MongoDBClient mongodbClient;

    @Override
    public PageInfo<ActivityVip> selectAllActivity(int page_num, int page_size, String corp_code, String user_code, String search_value) throws Exception {
        List<ActivityVip> activityVips;
        PageHelper.startPage(page_num, page_size);
        activityVips = activityVipMapper.selectAllActivity(corp_code, user_code, search_value);
        for (ActivityVip activityVip : activityVips) {
            activityVip.setIsactive(CheckUtils.CheckIsactive(activityVip.getIsactive()));
        }
        PageInfo<ActivityVip> page = new PageInfo<ActivityVip>(activityVips);

        return page;
    }

    @Override
    public PageInfo<ActivityVip> selectActivityAllScreen(int page_num, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception {
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
        List<ActivityVip> list1 = activityVipMapper.selectActivityScreen(params);
        for (ActivityVip activityVip : list1) {
            activityVip.setIsactive(CheckUtils.CheckIsactive(activityVip.getIsactive()));
        }
        PageInfo<ActivityVip> page = new PageInfo<ActivityVip>(list1);
        return page;
    }

    @Override
    public int delete(int id) throws Exception {
        return activityVipMapper.delActivityById(id);
    }
//
    @Override
    public String insert(String message, String user_id,HttpServletRequest request) throws Exception {
        String result = null;
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_code = "A"+corp_code+Common.DATETIME_FORMAT_DAY_NUM.format(now);
        String activity_state = "未执行";

        ActivityVip activityVip = WebUtils.JSON2Bean(jsonObject, ActivityVip.class);
        String activity_content = activityVip.getActivity_content();
        if (activity_content != null && !activity_content.equals("")){
            List<String> htmlImageSrcList = OssUtils.getHtmlImageSrcList(activity_content);
            OssUtils ossUtils=new OssUtils();
            String bucketName="products-image";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String  path="";
            path = request.getSession().getServletContext().getRealPath("/");
            for (int k = 0; k < htmlImageSrcList.size(); k++) {
                String time="ActivityVip/Vip/"+corp_code+"/"+activityVip.getId()+"_"+sdf.format(new Date())+".jpg";
                if(!htmlImageSrcList.get(k).contains("image/upload")){
                    continue;
                }
                ossUtils.putObject(bucketName,time,path+"/"+htmlImageSrcList.get(k));
                activity_content = activity_content.replace(htmlImageSrcList.get(k),"http://"+bucketName+".oss-cn-hangzhou.aliyuncs.com/"+time);
                LuploadHelper.deleteFile(path+"/"+htmlImageSrcList.get(k));
            }
        }
        activityVip.setActivity_vip_code(activity_code);
        activityVip.setActivity_content(activity_content);
        activityVip.setModifier(user_id);
        activityVip.setModified_date(Common.DATETIME_FORMAT.format(now));
        activityVip.setCreater(user_id);
        activityVip.setCreated_date(Common.DATETIME_FORMAT.format(now));
        activityVip.setActivity_state(activity_state);
        activityVip.setTask_code("");
        int info=0;
         info= activityVipMapper.insertActivity(activityVip);
        ActivityVip activityVip1 = selActivityByCode(activity_code);
        if (info>0) {
            return String.valueOf(activityVip1.getId());
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
        ActivityVip activityVip1 = activityVipMapper.selActivityById(activity_id);
        String activity_vip_code = activityVip1.getActivity_vip_code();
        String corp_code = jsonObject.get("corp_code").toString().trim();

        String path="";
        ActivityVip activityVip = WebUtils.JSON2Bean(jsonObject, ActivityVip.class);
        String activity_content = activityVip.getActivity_content();
        if (activity_content != null && !activity_content.equals("")){
            List<String> htmlImageSrcList = OssUtils.getHtmlImageSrcList(activity_content);
            OssUtils ossUtils=new OssUtils();
            String bucketName="products-image";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            path = request.getSession().getServletContext().getRealPath("/");
            for (int k = 0; k < htmlImageSrcList.size(); k++) {
                String time="ActivityVip/Vip/"+corp_code+"/"+activityVip.getId()+"_"+sdf.format(new Date())+".jpg";
                if(!htmlImageSrcList.get(k).contains("image/upload")){
                    continue;
                }
                ossUtils.putObject(bucketName,time,path+"/"+htmlImageSrcList.get(k));
                activity_content = activity_content.replace(htmlImageSrcList.get(k),"http://"+bucketName+".oss-cn-hangzhou.aliyuncs.com/"+time);
                LuploadHelper.deleteFile(path+"/"+htmlImageSrcList.get(k));
            }
        }
        Date now = new Date();
        activityVip.setId(activity_id);
        activityVip.setActivity_vip_code(activity_vip_code);
        activityVip.setActivity_content(activity_content);
        activityVip.setModifier(user_id);
        activityVip.setModified_date(Common.DATETIME_FORMAT.format(now));
        int info=0;
        info= activityVipMapper.updateActivity(activityVip);
        if (info>0) {
            result=Common.DATABEAN_CODE_SUCCESS;
            return result;
        } else {
            result="编辑失败";
            return result;
        }
    }

    @Override
    public int updateActivityVip(ActivityVip activityVip) throws Exception {
        return activityVipMapper.updateActivity(activityVip);
    }

    @Override
    public ActivityVip selectActivityById(int id) throws Exception {
        ActivityVip activityVip = activityVipMapper.selActivityById(id);
        String corp_code = activityVip.getCorp_code();
        String target_vips = activityVip.getTarget_vips();
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
            activityVip.setTarget_vips_count(count);
        }else if (type.equals("2")){
            String vips = vips_obj.get("vips").toString();
            String[] vips_array = vips.split(",");
            activityVip.setTarget_vips_count(String.valueOf(vips_array.length));
        }
        return activityVip;
    }

    @Override
    public ActivityVip getActivityById(int id) throws Exception {
        ActivityVip activityVip = activityVipMapper.selActivityById(id);
        return activityVip;
    }

    @Override
    public ActivityVip selActivityByCode(String activity_vip_code) throws Exception {
        return activityVipMapper.selActivityByCode(activity_vip_code);
    }

    /**
     * 根据选择的vip，
     * 显示对应的执行人
     */
//    @Override
//    public PageInfo<User> selUserByVip(int page_number, int page_size, String corp_code, String search_value, JSONObject target_vips) throws Exception {
//        String type = target_vips.get("type").toString();
//        PageInfo<User> userList = new PageInfo<User>();
//        if (type.equals("1")){
//            String area_code = target_vips.get("area_code").toString();
//            String brand_code = target_vips.get("brand_code").toString();
//            String store_code = target_vips.get("store_code").toString();
//            String user_code = target_vips.get("user_code").toString();
//            if (!user_code.equals("")){
//                userList = userService.selectUsersByUserCode(page_number,page_size,corp_code,search_value,user_code);
//            }else if (!store_code.equals("")) {
////                    String[] areas = area_code.split(",");
//                userList = userService.selUserByStoreCode(page_number, page_size, corp_code, search_value, store_code, null, Common.ROLE_STAFF);
//            }else if(!area_code.equals("") || !brand_code.equals("")){
//                //拉取区域下所有员工（包括区经）
//                String[] areas = area_code.split(",");
//                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,area_code,brand_code,search_value,"");
//                for (int i = 0; i < stores.size(); i++) {
//                    store_code = store_code + stores.get(i).getStore_code();
//                }
//                userList = userService.selectUsersByRole(page_number, page_size, corp_code, search_value, store_code, "",areas, "");
//            }else {
//                userList = userService.selectUsersByRole(page_number, page_size, corp_code, search_value, store_code, area_code,null, "");
//            }
//        }else if (type.equals("2")){
//            String vips = target_vips.get("vips").toString();
//
//            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
//            Data data_vip_id = new Data("vip_ids", vips, ValueType.PARAM);
//
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(data_vip_id.key, data_vip_id);
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipUser", datalist);
//            String result = dataBox.data.get("message").value;
//            JSONObject result_obj= JSONObject.parseObject(result);
//            String store_codes = result_obj.get("store_codes").toString();
//            String user_codes = result_obj.get("user_codes").toString();
//            store_codes = store_codes.replace("[","");
//            store_codes = store_codes.replace("]","");
//            store_codes = store_codes.replace("\"","");
//            user_codes = user_codes.replace("[","");
//            user_codes = user_codes.replace("]","");
//            user_codes = user_codes.replace("\"","");
//
//            if (!store_codes.equals("")){
//                String[] stores = store_codes.split(",");
//                for (int i = 0; i < stores.length; i++) {
//                    String store_code = stores[i];
//                    String[] codes = store_code.split("&&");
//                    String store_id = codes[0];
//                    store_code = codes[1];
//                    List<User> users1 = userService.selectSMByStoreCode(corp_code,store_code,store_id,Common.ROLE_SM,search_value);
//                    if (users1.size() > 0){
//                        if (users1.size() == 1){
//                            user_codes = user_codes + users1.get(0).getUser_code() + ",";
//                        }else {
//                            for (int j = 0; j < users1.size(); j++) {
//                                ValidateCode vaildates = validateService.selectPhoneExist("app",users1.get(j).getPhone(),Common.IS_ACTIVE_Y);
//                                if (vaildates != null){
//                                    user_codes = user_codes + users1.get(j).getUser_code() + ",";
//                                    break;
//                                }
//                                if (j==users1.size()-1){
//                                    user_codes = user_codes + users1.get(j).getUser_code() + ",";
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            if (!user_codes.equals("")){
//                userList = userService.selectUsersByUserCode(page_number,page_size,corp_code,search_value,user_codes);
//            }
//        }
//        return userList;
//    }

    /**
     * 点击执行
     *
     */
    @Override
    public String executeActivity(ActivityVip activityVip,String user_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        String run_mode = activityVip.getRun_mode();
        if (run_mode.contains("任务")) {
            status = executeTask(activityVip,user_code);
        }else{
            if (run_mode.contains("系统短信")){
                status = executeSysMsg(activityVip,user_code);
            }else if (run_mode.contains("微信")){

            }
        }
        return status;
    }

    /**
     * 获取活动任务执行情况
     *
     */
    @Override
    public JSONObject executeDetail(ActivityVip activityVip) throws Exception {
        JSONObject result = new JSONObject();
        String task_code = activityVip.getTask_code();
        String corp_code = activityVip.getCorp_code();
        String operators = activityVip.getOperators();
        String target_vips_count = activityVip.getTarget_vips_count();

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
            //目标店铺
            JSONArray store_array = JSONArray.parseArray(operators);
            //任务执行人的店铺编号
            store_code = store_code.replace(Common.SPECIAL_HEAD,"");
            String[] codes = store_code.split(",");
            for (int j = 0; j < store_array.size(); j++) {
                JSONObject store_obj = store_array.getJSONObject(j);
                String store_obj_code = store_obj.get("store_code").toString();
                String store_obj_name = store_obj.get("store_name").toString();
                for (int k = 0; k < codes.length; k++) {
                    if (store_obj_code.equals(codes[k])){
                        Store store = storeService.getStoreByCode(corp_code,codes[k],Common.IS_ACTIVE_Y);
                        if (store != null){
                            store_name = store_obj_name;
                            String area_code = store.getArea_code().replace(Common.SPECIAL_HEAD,"");
                            String code  = area_code.split(",")[0];
                            Area area = areaService.getAreaByCode(corp_code,code,Common.IS_ACTIVE_Y);
                            if (area != null)
                                area_name = area.getArea_name();
                        }
                        break;
                    }
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

    //执行任务活动
    public String executeTask(ActivityVip activityVip,String user_code) throws Exception{
        String status = Common.DATABEAN_CODE_SUCCESS;

        String activity_vip_code = activityVip.getActivity_vip_code();
        String corp_code = activityVip.getCorp_code();
        String run_mode = activityVip.getRun_mode();
        Date now = new Date();
        String task_title = activityVip.getTask_title();
        String task_desc = activityVip.getTask_desc();
        String operators = activityVip.getOperators();
        String start_time = activityVip.getStart_time();
        String end_time = activityVip.getEnd_time();

        //判断是否存在【任务类型】，没有则新建
        List<TaskType> taskTypes = taskTypeService.nameExist(corp_code, run_mode);
        String task_type_code = "";
        if (taskTypes.size() > 0) {
            task_type_code = taskTypes.get(0).getTask_type_code();
        } else {
            JSONObject message1 = new JSONObject();
            task_type_code = "T" + Common.DATETIME_FORMAT_DAY_NUM.format(now);
            message1.put("task_type_code", task_type_code);
            message1.put("task_type_name", run_mode);
            message1.put("corp_code", corp_code);
            message1.put("isactive", "Y");
            message1.put("created_date", Common.DATETIME_FORMAT.format(now));
            message1.put("modified_date", Common.DATETIME_FORMAT.format(now));
            message1.put("creater", user_code);
            message1.put("modifier", user_code);
            taskTypeService.insertTaskType(message1.toString(), user_code);
        }

        //创建任务并分配给执行人
        String user_codes = "";
        String phones = "";

        JSONArray store_codes_array = JSONArray.parseArray(operators);
        String store_codes = "";
        for (int i = 0; i <store_codes_array.size() ; i++) {
            store_codes = store_codes + store_codes_array.getJSONObject(i).get("store_code")+",";
        }
        List<User> userList = userService.selUserByStoreCode(corp_code,"",store_codes,null,"");
        if (userList.size() > 0){
            for (int i = 0; i < userList.size(); i++) {
                user_codes = user_codes + userList.get(i).getUser_code() + ",";
                phones = phones + userList.get(i).getPhone() + ",";
            }
            Task task = new Task();
            String task_code = "T" + Common.DATETIME_FORMAT_DAY_NUM.format(now) + Math.round(Math.random() * 9);
            task.setTask_code(task_code);
            task.setTask_title(task_title);
            task.setTask_type_code(task_type_code);
            task.setTask_description(task_desc);
            task.setTarget_start_time(start_time);
            task.setTarget_end_time(end_time);
            task.setCorp_code(corp_code);
            task.setCreated_date(Common.DATETIME_FORMAT.format(now));
            task.setCreater(user_code);
            task.setModified_date(Common.DATETIME_FORMAT.format(now));
            task.setModifier(user_code);
            task.setIsactive(Common.IS_ACTIVE_Y);
            task.setActivity_vip_code(activity_vip_code);
            taskService.addTask(task, phones, user_codes, user_code);

            //更新活动表中task_code
            activityVip.setTask_code(task_code);
            //更新活动状态activity_state
            activityVip.setActivity_state("执行中");
            activityVip.setModified_date(Common.DATETIME_FORMAT.format(now));
            activityVip.setModifier(user_code);
            updateActivityVip(activityVip);
        }else {
            return "该范围下没有执行人，无法执行";
        }
        return status;
    }

    //执行系统发送短信活动
    public String executeSysMsg(ActivityVip activityVip,String user_code) throws Exception{
        String status = Common.DATABEAN_CODE_SUCCESS;
        Date now = new Date();

        String corp_code = activityVip.getCorp_code();
        String target_vips = activityVip.getTarget_vips();
        String msg_info = activityVip.getMsg_info();
        JSONObject target_vips_obj = JSONObject.parseObject(target_vips);
        String type = target_vips_obj.getString("type");
        String phone = "";
        if (type.equals("1")){
            String area_code = target_vips_obj.get("area_code").toString();
            String brand_code = target_vips_obj.get("brand_code").toString();
            String store_code = target_vips_obj.get("store_code").toString();
            String vip_user_code = target_vips_obj.get("user_code").toString();
            if (vip_user_code.equals("")){
                if (store_code.equals("")) {
                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                    for (int i = 0; i < storeList.size(); i++) {
                        store_code = store_code + storeList.get(i).getStore_code() + ",";
                    }
                }
                Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                Data data_store_code = new Data("store_codes", store_code, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_corp_code.key, data_corp_code);
                datalist.put(data_store_code.key, data_store_code);
                DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo",datalist);
                String message = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message);
                JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
                for (int i = 0; i < vip_infos.size(); i++) {
                    JSONObject vip_obj = vip_infos.getJSONObject(i);
                    phone = phone + vip_obj.getString("MOBILE_VIP") + ",";
                }
            }else {
                Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                Data data_user_code = new Data("user_codes", user_code, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_corp_code.key, data_corp_code);
                datalist.put(data_user_code.key, data_user_code);
                DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo",datalist);
                String message = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message);
                JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
                for (int i = 0; i < vip_infos.size(); i++) {
                    JSONObject vip_obj = vip_infos.getJSONObject(i);
                    phone = phone + vip_obj.getString("MOBILE_VIP") + ",";
                }
            }
        }else {
            String vips = target_vips_obj.get("vips").toString();

            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_vip_id = new Data("vip_ids", vips, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_vip_id.key, data_vip_id);
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo",datalist);
            String message = dataBox.data.get("message").value;
            JSONObject msg_obj = JSONObject.parseObject(message);
            JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
            for (int i = 0; i < vip_infos.size(); i++) {
                JSONObject vip_obj = vip_infos.getJSONObject(i);
                phone = phone + vip_obj.getString("MOBILE_VIP") + ",";
            }
        }
        Data data_channel = new Data("channel", "santong", ValueType.PARAM);
        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_text = new Data("text", msg_info, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_channel.key, data_channel);
        datalist.put(data_phone.key, data_phone);
        datalist.put(data_text.key, data_text);
        DataBox dataBox = iceInterfaceService.iceInterfaceV3("SendSMS",datalist);
        if (!dataBox.status.toString().equals("SUCCESS")){
            status = "执行失败";
        }else {
            activityVip.setActivity_state("已完成");
            activityVip.setModified_date(Common.DATETIME_FORMAT.format(now));
            activityVip.setModifier(user_code);
            updateActivityVip(activityVip);
        }
        return status;
    }
}
