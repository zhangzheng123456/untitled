package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ActivityVipMapper;
import com.bizvane.ishop.entity.ActivityVip;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.ValidateCode;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OssUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ValidateCodeService validateService;
    @Autowired
    private IceInterfaceService iceInterfaceService;

    @Override
    public PageInfo<ActivityVip> selectAllActivity(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        List<ActivityVip> activityVips;
        PageHelper.startPage(page_num, page_size);
        activityVips = activityVipMapper.selectAllActivity(corp_code, search_value);
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

//        String activity_theme = jsonObject.get("activity_theme").toString().trim();
//        String run_mode = jsonObject.get("run_mode").toString().trim();
//        String start_time = jsonObject.get("start_time").toString().trim();
//        String end_time = jsonObject.get("end_time").toString().trim();
//        String target_vips = jsonObject.get("target_vips").toString().trim();
//        String operators = jsonObject.get("operators").toString().trim();
//        //任务类型
//        String task_title = jsonObject.get("task_title").toString().trim();
//        String task_desc = jsonObject.get("task_desc").toString().trim();
//        //公众号消息推送
//        String wechat_title = jsonObject.get("wechat_title").toString().trim();
//        String wechat_desc = jsonObject.get("wechat_desc").toString().trim();
//        //短信
//        String msg_info = jsonObject.get("msg_info").toString().trim();
//
//        //活动内容
//        String activity_url = jsonObject.get("activity_url").toString().trim();
//       // String activity_content = jsonObject.get("activity_content").toString().trim();
//        String content_url = jsonObject.get("content_url").toString().trim();
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

//        activityVip.setCorp_code(corp_code);
//        activityVip.setActivity_theme(activity_theme);
//        activityVip.setRun_mode(run_mode);
//        activityVip.setStart_time(start_time);
//        activityVip.setEnd_time(end_time);
//        activityVip.setTarget_vips(target_vips);
//        activityVip.setOperators(operators);
//
//        activityVip.setTask_title(task_title);
//        activityVip.setTask_desc(task_desc);
//
//        activityVip.setWechat_title(wechat_title);
//        activityVip.setWechat_desc(wechat_desc);
//
//        activityVip.setMsg_info(msg_info);
//
//        activityVip.setActivity_url(activity_url);
        activityVip.setActivity_vip_code(activity_code);
        activityVip.setActivity_content(activity_content);
//        activityVip.setContent_url(content_url);
        activityVip.setModifier(user_id);
        activityVip.setModified_date(Common.DATETIME_FORMAT.format(now));
        activityVip.setCreater(user_id);
        activityVip.setCreated_date(Common.DATETIME_FORMAT.format(now));
//        activityVip.setIsactive(jsonObject.get("isactive").toString().trim());
        activityVip.setActivity_state(activity_state);
        activityVip.setTask_code("");
        int info=0;
         info= activityVipMapper.insertActivity(activityVip);
        ActivityVip activityVip1 = selActivityByCode(corp_code, activity_code);
        if (info>0) {
            //result=String.valueOf(activityVip1.getId());
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

        String corp_code = jsonObject.get("corp_code").toString().trim();
//        String activity_theme = jsonObject.get("activity_theme").toString().trim();
//        String run_mode = jsonObject.get("run_mode").toString().trim();
//        String start_time = jsonObject.get("start_time").toString().trim();
//        String end_time = jsonObject.get("end_time").toString().trim();
//       String target_vips = jsonObject.get("target_vips").toString().trim();
//        String operators = jsonObject.get("operators").toString().trim();
//        String task_title = jsonObject.get("task_title").toString().trim();
//        String task_desc = jsonObject.get("task_desc").toString().trim();
//        String wechat_title = jsonObject.get("wechat_title").toString().trim();
//        String wechat_desc = jsonObject.get("wechat_desc").toString().trim();
//        String activity_url = jsonObject.get("activity_url").toString().trim();
     // String activity_content = jsonObject.get("activity_content").toString().trim();
//        String content_url = jsonObject.get("content_url").toString().trim();

//        String task_code = jsonObject.get("task_code").toString().trim();
        String path="";
        ActivityVip activityVip = WebUtils.JSON2Bean(jsonObject, ActivityVip.class);
        String activity_content = activityVip.getActivity_content();
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
        Date now = new Date();
        activityVip.setId(activity_id);
//        activityVip.setCorp_code(corp_code);
//        activityVip.setActivity_theme(activity_theme);
//        activityVip.setRun_mode(run_mode);
//        activityVip.setStart_time(start_time);
//        activityVip.setEnd_time(end_time);
//        activityVip.setTarget_vips(target_vips);
//        activityVip.setOperators(operators);
//        activityVip.setTask_title(task_title);
//        activityVip.setTask_desc(task_desc);
//        activityVip.setWechat_title(wechat_title);
//        activityVip.setWechat_desc(wechat_desc);
//        activityVip.setActivity_url(activity_url);
        activityVip.setActivity_content(activity_content);
//        activityVip.setContent_url(content_url);
        activityVip.setModifier(user_id);
        activityVip.setModified_date(Common.DATETIME_FORMAT.format(now));
//        activityVip.setCreater(user_id);
//        activityVip.setCreated_date(Common.DATETIME_FORMAT.format(now));
//        activityVip.setIsactive(jsonObject.get("isactive").toString().trim());
//        activityVip.setTask_code(task_code);
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
    public ActivityVip selActivityByCode(String corp_code, String activity_vip_code) throws Exception {
        return activityVipMapper.selActivityByCode( corp_code, activity_vip_code);
    }

    /**
     * 根据选择的vip，
     * 显示对应的执行人
     */
    @Override
    public PageInfo<User> selUserByVip(int page_number, int page_size, String corp_code, String search_value, JSONObject target_vips) throws Exception {
        String type = target_vips.get("type").toString();
        PageInfo<User> userList = new PageInfo<User>();
        if (type.equals("1")){
            String area_code = target_vips.get("area_code").toString();
            String brand_code = target_vips.get("brand_code").toString();
            String store_code = target_vips.get("store_code").toString();
            String user_code = target_vips.get("user_code").toString();
            if (!user_code.equals("")){
                userList = userService.selectUsersByUserCode(page_number,page_size,corp_code,search_value,user_code);
            }else if (!store_code.equals("")) {
//                    String[] areas = area_code.split(",");
                userList = userService.selUserByStoreCode(page_number, page_size, corp_code, search_value, store_code, null, Common.ROLE_STAFF);
            }else if(!area_code.equals("") || !brand_code.equals("")){
                //拉取区域下所有员工（包括区经）
                String[] areas = area_code.split(",");
                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,area_code,brand_code,search_value,"");
                for (int i = 0; i < stores.size(); i++) {
                    store_code = store_code + stores.get(i).getStore_code();
                }
                userList = userService.selectUsersByRole(page_number, page_size, corp_code, search_value, store_code, "",areas, "");
            }else {
                userList = userService.selectUsersByRole(page_number, page_size, corp_code, search_value, store_code, area_code,null, "");
            }
        }else if (type.equals("2")){
            String vips = target_vips.get("vips").toString();

            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_vip_id = new Data("vip_ids", vips, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_vip_id.key, data_vip_id);

            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipUser", datalist);
            String result = dataBox.data.get("message").value;
            JSONObject result_obj= JSONObject.parseObject(result);
            String store_codes = result_obj.get("store_codes").toString();
            String user_codes = result_obj.get("user_codes").toString();
            store_codes = store_codes.replace("[","");
            store_codes = store_codes.replace("]","");
            store_codes = store_codes.replace("\"","");
            user_codes = user_codes.replace("[","");
            user_codes = user_codes.replace("]","");
            user_codes = user_codes.replace("\"","");

            if (!store_codes.equals("")){
                String[] stores = store_codes.split(",");
                for (int i = 0; i < stores.length; i++) {
                    String store_code = stores[i];
                    String[] codes = store_code.split("&&");
                    String store_id = codes[0];
                    store_code = codes[1];
                    List<User> users1 = userService.selectSMByStoreCode(corp_code,store_code,store_id,Common.ROLE_SM,search_value);
                    if (users1.size() > 0){
                        if (users1.size() == 1){
                            user_codes = user_codes + users1.get(0).getUser_code() + ",";
                        }else {
                            for (int j = 0; j < users1.size(); j++) {
                                ValidateCode vaildates = validateService.selectPhoneExist("app",users1.get(j).getPhone(),Common.IS_ACTIVE_Y);
                                if (vaildates != null){
                                    user_codes = user_codes + users1.get(j).getUser_code() + ",";
                                    break;
                                }
                                if (j==users1.size()-1){
                                    user_codes = user_codes + users1.get(j).getUser_code() + ",";
                                }
                            }
                        }
                    }
                }
            }
            if (!user_codes.equals("")){
                userList = userService.selectUsersByUserCode(page_number,page_size,corp_code,search_value,user_codes);
            }
        }
        return userList;
    }

}
