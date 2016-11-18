package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ActivityVipMapper;
import com.bizvane.ishop.entity.Activity;
import com.bizvane.ishop.service.ActivityVipService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/15.
 */
@Service
public class ActivityVipVipServiceImpl implements ActivityVipService {

    @Autowired
    ActivityVipMapper activityVipMapper;


    @Override
    public PageInfo<Activity> selectAllActivity(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        List<Activity> areas;
        PageHelper.startPage(page_num, page_size);
        areas = activityVipMapper.selectAllActivity(corp_code, search_value);
        for (Activity area : areas) {
            area.setIsactive(CheckUtils.CheckIsactive(area.getIsactive()));
        }
        PageInfo<Activity> page = new PageInfo<Activity>(areas);

        return page;
    }

    @Override
    public PageInfo<Activity> selectActivityAllScreen(int page_num, int page_size, String corp_code, String role_ident, String user_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_num, page_size);
        List<Activity> list1 = activityVipMapper.selectActivityScreen(params);
        for (Activity activity : list1) {
            activity.setIsactive(CheckUtils.CheckIsactive(activity.getIsactive()));
        }
        PageInfo<Activity> page = new PageInfo<Activity>(list1);
        return page;
    }

    @Override
    public int delete(int id) throws Exception {
        return activityVipMapper.delActivityById(id);
    }
//,HttpServletRequest request
    @Override
    public String insert(String message, String user_id) throws Exception {
        String result = null;
        JSONObject jsonObject = new JSONObject(message);
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_theme = jsonObject.get("activity_theme").toString().trim();
        String run_mode = jsonObject.get("run_mode").toString().trim();
        String start_time = jsonObject.get("start_time").toString().trim();
        String end_time = jsonObject.get("end_time").toString().trim();
        String target_vips = jsonObject.get("target_vips").toString().trim();
        String operators = jsonObject.get("operators").toString().trim();
        String task_title = jsonObject.get("task_title").toString().trim();
        String task_desc = jsonObject.get("task_desc").toString().trim();
        String wechat_title = jsonObject.get("wechat_title").toString().trim();
        String wechat_desc = jsonObject.get("wechat_desc").toString().trim();
        String activity_url = jsonObject.get("activity_url").toString().trim();
       String activity_content = jsonObject.get("activity_content").toString().trim();
        String content_url = jsonObject.get("content_url").toString().trim();
        String activity_state = jsonObject.get("activity_state").toString().trim();

        String task_code = jsonObject.get("task_code").toString().trim();
        String path="";
        Activity activity = WebUtils.JSON2Bean(jsonObject, Activity.class);
       /* String activity_content = activity.getActivity_content();
        List<String> htmlImageSrcList = OssUtils.getHtmlImageSrcList(activity_content);
        OssUtils ossUtils=new OssUtils();
        String bucketName="products-image";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        path = request.getSession().getServletContext().getRealPath("/");
        for (int k = 0; k < htmlImageSrcList.size(); k++) {
            String time="Activity/Vip/"+corp_code+"/"+activity.getId()+"_"+sdf.format(new Date())+".jpg";
            if(!htmlImageSrcList.get(k).contains("image/upload")){
                continue;
            }
            ossUtils.putObject(bucketName,time,path+"/"+htmlImageSrcList.get(k));
            activity_content = activity_content.replace(htmlImageSrcList.get(k),"http://"+bucketName+".oss-cn-hangzhou.aliyuncs.com/"+time);
            LuploadHelper.deleteFile(path+"/"+htmlImageSrcList.get(k));
        }*/
        Date now = new Date();
        activity.setCorp_code(corp_code);
        activity.setActivity_theme(activity_theme);
        activity.setRun_mode(run_mode);
        activity.setStart_time(start_time);
        activity.setEnd_time(end_time);
        activity.setTarget_vips(target_vips);
      activity.setOperators(operators);
        activity.setTask_title(task_title);
        activity.setTask_desc(task_desc);
        activity.setWechat_title(wechat_title);
        activity.setWechat_desc(wechat_desc);
        activity.setActivity_url(activity_url);
        activity.setActivity_content(activity_content);
        activity.setContent_url(content_url);
        activity.setModifier(user_id);
        activity.setModified_date(Common.DATETIME_FORMAT.format(now));
        activity.setCreater(user_id);
        activity.setCreated_date(Common.DATETIME_FORMAT.format(now));
        activity.setIsactive(jsonObject.get("isactive").toString().trim());
        activity.setActivity_state(activity_state);
        activity.setTask_code(task_code);
        int info=0;
         info= activityVipMapper.insertActivity(activity);
        Activity activity1= this.getActivityForId(activity.getCorp_code(),activity.getActivity_theme(),activity.getRun_mode(),activity.getCreated_date());

        if (info>0) {
            //result=String.valueOf(activity1.getId());
            return result;
        } else {
            result="新增失败";
            return result;
        }
    }

    @Override
    public String update(String message, String user_id) throws Exception {
       String result = "";
        JSONObject jsonObject = new JSONObject(message);
        int activity_id = Integer.parseInt(jsonObject.get("id").toString().trim());

        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_theme = jsonObject.get("activity_theme").toString().trim();
        String run_mode = jsonObject.get("run_mode").toString().trim();
        String start_time = jsonObject.get("start_time").toString().trim();
        String end_time = jsonObject.get("end_time").toString().trim();
       String target_vips = jsonObject.get("target_vips").toString().trim();
        String operators = jsonObject.get("operators").toString().trim();
        String task_title = jsonObject.get("task_title").toString().trim();
        String task_desc = jsonObject.get("task_desc").toString().trim();
        String wechat_title = jsonObject.get("wechat_title").toString().trim();
        String wechat_desc = jsonObject.get("wechat_desc").toString().trim();
        String activity_url = jsonObject.get("activity_url").toString().trim();
      String activity_content = jsonObject.get("activity_content").toString().trim();
        String content_url = jsonObject.get("content_url").toString().trim();

        String task_code = jsonObject.get("task_code").toString().trim();
        String path="";
        Activity activity = WebUtils.JSON2Bean(jsonObject, Activity.class);
      /*  String activity_content = activity.getActivity_content();
        List<String> htmlImageSrcList = OssUtils.getHtmlImageSrcList(activity_content);
        OssUtils ossUtils=new OssUtils();
        String bucketName="products-image";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        path = request.getSession().getServletContext().getRealPath("/");
        for (int k = 0; k < htmlImageSrcList.size(); k++) {
            String time="Activity/Vip/"+corp_code+"/"+activity.getId()+"_"+sdf.format(new Date())+".jpg";
            if(!htmlImageSrcList.get(k).contains("image/upload")){
                continue;
            }
            ossUtils.putObject(bucketName,time,path+"/"+htmlImageSrcList.get(k));
            activity_content = activity_content.replace(htmlImageSrcList.get(k),"http://"+bucketName+".oss-cn-hangzhou.aliyuncs.com/"+time);
            LuploadHelper.deleteFile(path+"/"+htmlImageSrcList.get(k));
        }
*/
        Date now = new Date();
        activity.setId(activity_id);
        activity.setCorp_code(corp_code);
        activity.setActivity_theme(activity_theme);
        activity.setRun_mode(run_mode);
        activity.setStart_time(start_time);
        activity.setEnd_time(end_time);
        activity.setTarget_vips(target_vips);
        activity.setOperators(operators);
        activity.setTask_title(task_title);
        activity.setTask_desc(task_desc);
        activity.setWechat_title(wechat_title);
        activity.setWechat_desc(wechat_desc);
        activity.setActivity_url(activity_url);
        activity.setActivity_content(activity_content);
        activity.setContent_url(content_url);
        activity.setModifier(user_id);
        activity.setModified_date(Common.DATETIME_FORMAT.format(now));
        activity.setCreater(user_id);
        activity.setCreated_date(Common.DATETIME_FORMAT.format(now));
        activity.setIsactive(jsonObject.get("isactive").toString().trim());
        activity.setTask_code(task_code);
        int info=0;
        info= activityVipMapper.updateActivity(activity);
        Activity activity1= this.getActivityForId(activity.getCorp_code(),activity.getActivity_theme(),activity.getRun_mode(),activity.getCreated_date());

        if (info>0) {
            result=Common.DATABEAN_CODE_SUCCESS;
            return result;
        } else {
            result="编辑失败";
            return result;
        }
    }

    @Override
    public Activity selectActivityById(int id) throws Exception {
        return activityVipMapper.selActivityById(id);
    }

    @Override
    public Activity getActivityForId(String corp_code, String activity_theme, String run_mode, String created_date) throws Exception {
        return activityVipMapper.getActivityForID( corp_code,  activity_theme,  run_mode, created_date);
    }



}
