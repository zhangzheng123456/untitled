package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ActivityMapper;
import com.bizvane.ishop.entity.Activity;
import com.bizvane.ishop.service.ActivityService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OssUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/15.
 */
@Service
public class ActivityServiceImpl implements ActivityService{

    @Autowired
    ActivityMapper activityMapper;


    @Override
    public PageInfo<Activity> selectAllActivity(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        List<Activity> areas;
        PageHelper.startPage(page_num, page_size);
        areas = activityMapper.selectAllActivity(corp_code, search_value);
        for (Activity area : areas) {
            area.setIsactive(CheckUtils.CheckIsactive(area.getIsactive()));
        }
        PageInfo<Activity> page = new PageInfo<Activity>(areas);

        return page;
    }

    @Override
    public PageInfo<Activity> selectActivityAllScreen(int page_num, int page_size, String corp_code, String role_ident, String user_code, Map<String, String> map) throws Exception {
        return null;
    }

    @Override
    public int delete(int id) throws Exception {
        return activityMapper.delActivityById(id);
    }
//,HttpServletRequest request
    @Override
    public String insert(String message, String user_id,HttpServletRequest request) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_theme = jsonObject.get("activity_theme").toString().trim();
        String run_mode = jsonObject.get("run_mode").toString().trim();
        String start_time = jsonObject.get("start_time").toString().trim();
        String end_time = jsonObject.get("end_time").toString().trim();
        String activity_vip = jsonObject.get("activity_vip").toString().trim();
        String activity_operator = jsonObject.get("activity_operator").toString().trim();
        String task_title = jsonObject.get("task_title").toString().trim();
        String task_desc = jsonObject.get("task_desc").toString().trim();
        String wechat_title = jsonObject.get("wechat_title").toString().trim();
        String wechat_desc = jsonObject.get("wechat_desc").toString().trim();
        String activity_url = jsonObject.get("activity_url").toString().trim();
       // String activity_content = jsonObject.get("activity_content").toString().trim();
        String content_url = jsonObject.get("content_url").toString().trim();
        String path="";
        Activity activity = WebUtils.JSON2Bean(jsonObject, Activity.class);
        String activity_content = activity.getActivity_content();
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
        Date now = new Date();
        activity.setCorp_code(corp_code);
        activity.setActivity_theme(activity_theme);
        activity.setRun_mode(run_mode);
        activity.setStart_time(start_time);
        activity.setEnd_time(end_time);
        activity.setActivity_vip(activity_vip);
        activity.setActivity_operator(activity_operator);
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
        activityMapper.insertActivity(activity);
        Activity activity1= this.getActivityForId(activity.getCorp_code(),activity.getActivity_theme(),activity.getRun_mode(),activity.getCreated_date());

        if (activity1 !=null) {
             result=String.valueOf(activity1.getId());
            return result;
        } else {
            result="新增失败";
            return result;
        }
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        return null;
    }

    @Override
    public Activity selectActivityById(int id) throws Exception {
        return activityMapper.selActivityById(id);
    }

    @Override
    public Activity getActivityForId(String corp_code, String activity_theme, String run_mode, String created_date) throws Exception {
        return activityMapper.getActivityForID( corp_code,  activity_theme,  run_mode, created_date);
    }



}
