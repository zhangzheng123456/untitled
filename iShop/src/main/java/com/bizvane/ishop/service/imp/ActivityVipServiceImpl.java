package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ActivityVipMapper;
import com.bizvane.ishop.entity.ActivityVip;
import com.bizvane.ishop.service.ActivityVipService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class ActivityVipServiceImpl implements ActivityVipService {

    @Autowired
    ActivityVipMapper activityVipMapper;


    @Override
    public PageInfo<ActivityVip> selectAllActivity(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        List<ActivityVip> areas;
        PageHelper.startPage(page_num, page_size);
        areas = activityVipMapper.selectAllActivity(corp_code, search_value);
        for (ActivityVip area : areas) {
            area.setIsactive(CheckUtils.CheckIsactive(area.getIsactive()));
        }
        PageInfo<ActivityVip> page = new PageInfo<ActivityVip>(areas);

        return page;
    }

    @Override
    public PageInfo<ActivityVip> selectActivityAllScreen(int page_num, int page_size, String corp_code, String role_ident, String user_code, Map<String, String> map) throws Exception {
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
//,HttpServletRequest request
    @Override
    public String insert(String message, String user_id) throws Exception {
        String result = null;
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_theme = jsonObject.get("activity_theme").toString().trim();
        String run_mode = jsonObject.get("run_mode").toString().trim();
        String start_time = jsonObject.get("start_time").toString().trim();
        String end_time = jsonObject.get("end_time").toString().trim();
        String target_vips = jsonObject.get("target_vips").toString().trim();
        String operators = jsonObject.get("operators").toString().trim();
        //任务类型
        String task_title = jsonObject.get("task_title").toString().trim();
        String task_desc = jsonObject.get("task_desc").toString().trim();
        //公众号消息推送
        String wechat_title = jsonObject.get("wechat_title").toString().trim();
        String wechat_desc = jsonObject.get("wechat_desc").toString().trim();
        //短信
        String msg_info = jsonObject.get("msg_info").toString().trim();

        //活动内容
        String activity_url = jsonObject.get("activity_url").toString().trim();
        String activity_content = jsonObject.get("activity_content").toString().trim();
        String content_url = jsonObject.get("content_url").toString().trim();
        String activity_state = "未执行";

        ActivityVip activityVip = WebUtils.JSON2Bean(jsonObject, ActivityVip.class);
       /* String activity_content = activityVip.getActivity_content();
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
        }*/
        Date now = new Date();
        activityVip.setCorp_code(corp_code);
        activityVip.setActivity_theme(activity_theme);
        activityVip.setRun_mode(run_mode);
        activityVip.setStart_time(start_time);
        activityVip.setEnd_time(end_time);
        activityVip.setTarget_vips(target_vips);
        activityVip.setOperators(operators);

        activityVip.setTask_title(task_title);
        activityVip.setTask_desc(task_desc);

        activityVip.setWechat_title(wechat_title);
        activityVip.setWechat_desc(wechat_desc);

        activityVip.setMsg_info(msg_info);

        activityVip.setActivity_url(activity_url);
        activityVip.setActivity_content(activity_content);
        activityVip.setContent_url(content_url);
        activityVip.setModifier(user_id);
        activityVip.setModified_date(Common.DATETIME_FORMAT.format(now));
        activityVip.setCreater(user_id);
        activityVip.setCreated_date(Common.DATETIME_FORMAT.format(now));
        activityVip.setIsactive(jsonObject.get("isactive").toString().trim());
        activityVip.setActivity_state(activity_state);
//        activityVip.setTask_code(task_code);
        int info=0;
         info= activityVipMapper.insertActivity(activityVip);
        ActivityVip activityVip1 = this.getActivityForId(corp_code, activityVip.getActivity_theme(), activityVip.getRun_mode(), activityVip.getCreated_date());

        if (info>0) {
            //result=String.valueOf(activityVip1.getId());
            return String.valueOf(activityVip1.getId());
        } else {
            result="新增失败";
            return result;
        }
    }

    @Override
    public String update(String message, String user_id) throws Exception {
       String result = "";
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
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
        ActivityVip activityVip = WebUtils.JSON2Bean(jsonObject, ActivityVip.class);
      /*  String activity_content = activityVip.getActivity_content();
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
*/
        Date now = new Date();
        activityVip.setId(activity_id);
        activityVip.setCorp_code(corp_code);
        activityVip.setActivity_theme(activity_theme);
        activityVip.setRun_mode(run_mode);
        activityVip.setStart_time(start_time);
        activityVip.setEnd_time(end_time);
        activityVip.setTarget_vips(target_vips);
        activityVip.setOperators(operators);
        activityVip.setTask_title(task_title);
        activityVip.setTask_desc(task_desc);
        activityVip.setWechat_title(wechat_title);
        activityVip.setWechat_desc(wechat_desc);
        activityVip.setActivity_url(activity_url);
        activityVip.setActivity_content(activity_content);
        activityVip.setContent_url(content_url);
        activityVip.setModifier(user_id);
        activityVip.setModified_date(Common.DATETIME_FORMAT.format(now));
        activityVip.setCreater(user_id);
        activityVip.setCreated_date(Common.DATETIME_FORMAT.format(now));
        activityVip.setIsactive(jsonObject.get("isactive").toString().trim());
        activityVip.setTask_code(task_code);
        int info=0;
        info= activityVipMapper.updateActivity(activityVip);
        ActivityVip activityVip1 = this.getActivityForId(activityVip.getCorp_code(), activityVip.getActivity_theme(), activityVip.getRun_mode(), activityVip.getCreated_date());

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
        return activityVipMapper.selActivityById(id);
    }

    @Override
    public ActivityVip getActivityForId(String corp_code, String activity_theme, String run_mode, String created_date) throws Exception {
        return activityVipMapper.getActivityForID( corp_code,  activity_theme,  run_mode, created_date);
    }

    @Override
    public ActivityVip selActivityByCode(String corp_code, String activity_code) throws Exception {
        return activityVipMapper.selActivityByCode( corp_code, activity_code);
    }

}
