package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.VipActivity;
import com.bizvane.ishop.entity.VipFsend;
import com.bizvane.ishop.entity.WxTemplate;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by PC on 2017/1/12.
 */
@Service
public class VipActivityMakeServiceImpl implements VipActivityMakeService {
    @Autowired
    private TaskService taskService;
    @Autowired
    private VipFsendService vipFsendService;
    @Autowired
    private VipActivityService vipActivityService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    WxTemplateService wxTemplateService;

    @Transactional
    public int addOrUpdateTask(String message, String user_code) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_vip_code = jsonObject.get("activity_vip_code").toString();

        String task_status = jsonObject.get("task_status").toString();
        VipActivity vipActivity = vipActivityService.getActivityByCode(activity_vip_code);
        String corp_code = vipActivity.getCorp_code();
        int count = 0;
        if (task_status.equals("N")) {
            count += vipActivityService.updActiveCodeByType("task_status", "N", corp_code, activity_vip_code);
            count += vipActivityService.updActiveCodeByType("task_code", "", corp_code, activity_vip_code);
            taskService.delTaskByActivityCode(corp_code, activity_vip_code);
        } else {


            String tasklist = jsonObject.get("tasklist").toString();
            JSONArray jsonArray_task = JSON.parseArray(tasklist);
            if (jsonArray_task.size() <= 10) {
                String task_code_actvie = "";

                taskService.delTaskByActivityCode(corp_code, activity_vip_code);
                for (int i = 0; i < jsonArray_task.size(); i++) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    String task_code = "T" + sdf.format(new Date()) + Math.round(Math.random() * 9);
                    Thread.sleep(1);
                    task_code_actvie = task_code_actvie + task_code + ",";

                    JSONObject task_obj = JSONObject.parseObject(jsonArray_task.get(i).toString());
                    Task task = WebUtils.JSON2Bean(task_obj, Task.class);

                    if (task.getTask_title().equals("") || task.getTask_type_code().equals("") || task.getTarget_start_time().equals("") || task.getTarget_end_time().equals("")) {
                        continue;
                    }
                    task.setActivity_vip_code(activity_vip_code);

                    task.setTask_code(task_code);
                    task.setCorp_code(corp_code);

                    Date now = new Date();
                    task.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    task.setCreater(user_code);
                    task.setModified_date(Common.DATETIME_FORMAT.format(now));
                    task.setModifier(user_code);
                    task.setIsactive("Y");

                    count += taskService.insertTask(task);

                }
                count += vipActivityService.updActiveCodeByType("task_status", "Y", corp_code, activity_vip_code);
                count += vipActivityService.updActiveCodeByType("task_code", task_code_actvie, corp_code, activity_vip_code);
            }else{
                count = -1;
            }

        }

        return count;
    }

    @Transactional
    public String addOrUpdateSend(String message, String user_code,String group_code,String role_code,String brand_code,String area_code,String store_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_vip_code = jsonObject.get("activity_vip_code").toString();
        VipActivity vipActivity = vipActivityService.getActivityByCode(activity_vip_code);
        String corp_code = vipActivity.getCorp_code();
        String send_status = jsonObject.get("send_status").toString();

        int count = 0;
        if (send_status.equals("N")) {
            count += vipActivityService.updActiveCodeByType("send_status", "N", corp_code, activity_vip_code);
            count += vipActivityService.updActiveCodeByType("sms_code", "", corp_code, activity_vip_code);
            vipFsendService.delSendByActivityCode(corp_code, activity_vip_code);
        } else {
            String wxlist = jsonObject.get("wxlist").toString();
            JSONArray jsonArray_wx = new JSONArray();
            JSONArray wx=JSON.parseArray(wxlist);
            if (!wxlist.equals("")&&wx.size()>0) {
                if (StringUtils.isBlank(vipActivity.getApp_id())) {
                    return "活动未设置公众号，无法群发微信模板消息";
                }
                List<WxTemplate> templateList = wxTemplateService.selectTempByAppId(vipActivity.getApp_id(),"",Common.TEMPLATE_NAME_1);
                if (templateList.size() < 1){
                    return "未设置微信模板，无法群发微信模板消息";
                }
                jsonArray_wx = JSON.parseArray(wxlist);
            }

            String smslist = jsonObject.get("smslist").toString();
            JSONArray jsonArray_sms = new JSONArray();
            if (!smslist.equals("")) {
                jsonArray_sms = JSON.parseArray(smslist);
            }

            String send_code_actvie = "";

            vipFsendService.delSendByActivityCode(corp_code, activity_vip_code);
            for (int i = 0; i < jsonArray_wx.size(); i++) {
                JSONObject send_obj = JSONObject.parseObject(jsonArray_wx.get(i).toString());
                String screenJs = send_obj.getString("screen");
                VipFsend vipFsend = WebUtils.JSON2Bean(send_obj, VipFsend.class);
                if (vipFsend.getSend_time().equals("") || vipFsend.getContent().equals("")) {
                    continue;
                }
                String screen_value="";
                if(StringUtils.isNotBlank(screenJs)) {
                    JSONArray screen = JSON.parseArray(screenJs);
                    JSONArray post_array = vipGroupService.vipScreen2ArrayNew(screen, corp_code, role_code, brand_code, area_code, store_code, user_code);
                    screen_value = post_array.toJSONString();
                }

                String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
                send_code_actvie = send_code_actvie + sms_code + ",";

                vipFsend.setSms_code(sms_code);
                vipFsend.setSend_type("wxmass");
                vipFsend.setCorp_code(corp_code);
                vipFsend.setContent(send_obj.get("content").toString());
                vipFsend.setSend_scope("vip_condition_new");
                vipFsend.setActivity_vip_code(activity_vip_code);
                vipFsend.setApp_id(vipActivity.getApp_id());
                vipFsend.setCheck_status("N");
                vipFsend.setSms_vips(screenJs);
                vipFsend.setSms_vips_(screen_value);

                Date now = new Date();
                vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setCreater(user_code);
                vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setModifier(user_code);
                vipFsend.setIsactive("Y");
                //群发消息筛选条件
                count += vipFsendService.insertSend(vipFsend);
            }
            for (int i = 0; i < jsonArray_sms.size(); i++) {
                JSONObject send_obj = JSONObject.parseObject(jsonArray_sms.get(i).toString());
                VipFsend vipFsend = WebUtils.JSON2Bean(send_obj, VipFsend.class);
                if (vipFsend.getSend_time().equals("") || vipFsend.getContent().equals("")) {
                    continue;
                }
                String screenJs = send_obj.getString("screen");
                String screen_value="";
                if(StringUtils.isNotBlank(screenJs)) {
                    JSONArray screen = JSON.parseArray(screenJs);
                    JSONArray post_array = vipGroupService.vipScreen2ArrayNew(screen, corp_code, role_code, brand_code, area_code, store_code, user_code);
                    screen_value = post_array.toJSONString();
                }
                String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
                send_code_actvie = send_code_actvie + sms_code + ",";

                vipFsend.setSms_code(sms_code);
                vipFsend.setSend_type("sms");
                vipFsend.setCorp_code(corp_code);
                vipFsend.setContent(send_obj.get("content").toString());
                vipFsend.setSend_scope("vip_condition_new");
                vipFsend.setActivity_vip_code(activity_vip_code);
                vipFsend.setApp_id(vipActivity.getApp_id());
                vipFsend.setCheck_status("N");
                vipFsend.setSms_vips(screenJs);
                vipFsend.setSms_vips_(screen_value);

                Date now = new Date();
                vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setCreater(user_code);
                vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setModifier(user_code);
                vipFsend.setIsactive("Y");
                //群发消息筛选条件
                count += vipFsendService.insertSend(vipFsend);
            }

            //开关，Y为开，N为关
            count += vipActivityService.updActiveCodeByType("send_status", "Y", corp_code, activity_vip_code);
            count += vipActivityService.updActiveCodeByType("sms_code", send_code_actvie, corp_code, activity_vip_code);
        }
        return status;
    }

    @Transactional
    public String addStrategyByTask(String message, String user_code) throws Exception {
        String status = "";
        JSONObject jsonObject = JSONObject.parseObject(message);

        String activity_vip_code = jsonObject.get("activity_vip_code").toString();
        VipActivity vipActivity = vipActivityService.getActivityByCode(activity_vip_code);
        String corp_code = vipActivity.getCorp_code();

        String tasklist = jsonObject.get("tasklist").toString();
        JSONArray jsonArray_task = JSON.parseArray(tasklist);

        int count = 0;
        String task_code_actvie_old = vipActivity.getTask_code();
        int length = task_code_actvie_old.split(",").length;
        int size = jsonArray_task.size();
        String task_code_actvie_new = "";
        if(length+size<=10) {
            for (int i = 0; i < jsonArray_task.size(); i++) {
                JSONObject task_obj = JSONObject.parseObject(jsonArray_task.get(i).toString());
                Task task = WebUtils.JSON2Bean(task_obj, Task.class);
                if (task.getTask_title().equals("") || task.getTask_type_code().equals("") || task.getTarget_start_time().equals("") || task.getTarget_end_time().equals("")) {
                    continue;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

                String task_code = "T" + sdf.format(new Date()) + Math.round(Math.random() * 9);
                Thread.sleep(1);
                task_code_actvie_new = task_code_actvie_new + task_code + ",";
                if (task_code_actvie_old == null || task_code_actvie_old.equals("")) {
                    task_code_actvie_old = task_code + ",";
                } else if (task_code_actvie_old.endsWith(",")) {
                    task_code_actvie_old = task_code_actvie_old + task_code + ",";
                } else {
                    task_code_actvie_old = task_code_actvie_old + "," + task_code + ",";
                }

                task.setActivity_vip_code(activity_vip_code);
                task.setTask_code(task_code);
                task.setCorp_code(corp_code);

                Date now = new Date();
                task.setCreated_date(Common.DATETIME_FORMAT.format(now));
                task.setCreater(user_code);
                task.setModified_date(Common.DATETIME_FORMAT.format(now));
                task.setModifier(user_code);
                task.setIsactive("Y");

                count += taskService.insertTask(task);

            }
            System.out.println("-----------old------------" + task_code_actvie_old);
            System.out.println("-----------new------------" + task_code_actvie_new);
            if (count > 0) {
                count += vipActivityService.updActiveCodeByType("task_status", "Y", corp_code, activity_vip_code);
                count += vipActivityService.updActiveCodeByType("task_code", task_code_actvie_old, corp_code, activity_vip_code);

                VipActivity vipActivity_new = new VipActivity();
                vipActivity_new.setCorp_code(corp_code);
                vipActivity_new.setActivity_code(activity_vip_code);
                vipActivity_new.setTask_code(task_code_actvie_new);
//                vipActivity_new.setRun_scope(vipActivity.getRun_scope());

                status = vipActivityService.executeTask(vipActivity_new, user_code);
            }
            if (count <= 0) {
                status = "添加任务失败";
                count = 0;
                int i = 8 / 0;
            }
        }else{
            status = "最多只能创建10条任务";
            count =-1;
        }
        return status;
    }

    @Transactional
    public String addStrategyBySend(String message, String user_code,String group_code,String role_code,String brand_code,String area_code,String store_code) throws Exception {
        String result = "群发失败";
        JSONObject jsonObject = JSONObject.parseObject(message);
        String activity_vip_code = jsonObject.get("activity_vip_code").toString();

        VipActivity vipActivity = vipActivityService.getActivityByCode(activity_vip_code);
        String corp_code = vipActivity.getCorp_code();
        String send_code_actvie_old = vipActivity.getSms_code();

        String wxlist = jsonObject.get("wxlist").toString();
        String smslist = jsonObject.get("smslist").toString();
        JSONArray jsonArray_wx = new JSONArray();
        if (!wxlist.equals("")) {
            jsonArray_wx = JSON.parseArray(wxlist);
        }

        JSONArray jsonArray_sms = new JSONArray();
        if (!smslist.equals("")) {
            jsonArray_sms = JSON.parseArray(smslist);
        }

        int count = 0;
        String send_code_actvie_new = "";
        for (int i = 0; i < jsonArray_wx.size(); i++) {
            JSONObject send_obj = JSONObject.parseObject(jsonArray_wx.get(i).toString());

            String screen = send_obj.get("screen").toString();
            JSONArray screen_new = JSONArray.parseArray(screen);
            screen_new = vipGroupService.vipScreen2ArrayNew(screen_new,corp_code,role_code,brand_code,area_code,store_code,user_code);

            VipFsend vipFsend = WebUtils.JSON2Bean(send_obj, VipFsend.class);
            if (vipFsend.getSend_time().equals("") || vipFsend.getContent().equals("")) {
                continue;
            }
            String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
            if (send_code_actvie_old == null || send_code_actvie_old.equals("")) {
                send_code_actvie_old = sms_code + ",";
            } else if (send_code_actvie_old.endsWith(",")) {
                send_code_actvie_old = send_code_actvie_old + sms_code + ",";
            } else {
                send_code_actvie_old = send_code_actvie_old + "," + sms_code + ",";
            }
            send_code_actvie_new = send_code_actvie_new + sms_code + ",";

            vipFsend.setSms_code(sms_code);
            vipFsend.setSend_type(Common.SEND_TYPE_WX);
            vipFsend.setCorp_code(corp_code);
            vipFsend.setContent(send_obj.get("content").toString());
            vipFsend.setSend_scope("vip_condition_new");
            vipFsend.setSms_vips(screen);
            vipFsend.setSms_vips_(JSON.toJSONString(screen_new));
            vipFsend.setActivity_vip_code(activity_vip_code);
            vipFsend.setApp_id(vipActivity.getApp_id());
            vipFsend.setCheck_status("N");

            Date now = new Date();
            vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipFsend.setCreater(user_code);
            vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipFsend.setModifier(user_code);
            vipFsend.setIsactive("Y");

            count += vipFsendService.insertSend(vipFsend);
        }
        for (int i = 0; i < jsonArray_sms.size(); i++) {
            JSONObject send_obj = JSONObject.parseObject(jsonArray_sms.get(i).toString());
            String screen = send_obj.get("screen").toString();
            JSONArray screen_new = JSONArray.parseArray(screen);
            screen_new = vipGroupService.vipScreen2ArrayNew(screen_new,corp_code,role_code,brand_code,area_code,store_code,user_code);

            VipFsend vipFsend = WebUtils.JSON2Bean(send_obj, VipFsend.class);
            if (vipFsend.getSend_time().equals("") || vipFsend.getContent().equals("")) {
                continue;
            }
            String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
            if (send_code_actvie_old == null || send_code_actvie_old.equals("")) {
                send_code_actvie_old = sms_code + ",";
            } else if (send_code_actvie_old == null || send_code_actvie_old.equals("") || send_code_actvie_old.endsWith(",")) {
                send_code_actvie_old = send_code_actvie_old + sms_code + ",";
            } else {
                send_code_actvie_old = send_code_actvie_old + "," + sms_code + ",";
            }
            send_code_actvie_new = send_code_actvie_new + sms_code + ",";

            vipFsend.setSms_code(sms_code);
            vipFsend.setSend_type(Common.SEND_TYPE_SMS);
            vipFsend.setCorp_code(corp_code);
            vipFsend.setContent(send_obj.get("content").toString());
            vipFsend.setSend_scope("vip_condition_new");
            vipFsend.setSms_vips(screen);
            vipFsend.setSms_vips_(JSON.toJSONString(screen_new));
            vipFsend.setActivity_vip_code(activity_vip_code);
            vipFsend.setApp_id(vipActivity.getApp_id());
            vipFsend.setCheck_status("N");

            Date now = new Date();
            vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipFsend.setCreater(user_code);
            vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipFsend.setModifier(user_code);
            vipFsend.setIsactive("Y");

            count += vipFsendService.insertSend(vipFsend);
        }
        String status = "";
        if (count > 0) {
            count += vipActivityService.updActiveCodeByType("sms_code", send_code_actvie_old, corp_code, activity_vip_code);
            status = vipActivityService.executeFsend(vipActivity,send_code_actvie_new, user_code);
        }
        if (count > 0) {
            if (status.equals(Common.DATABEAN_CODE_SUCCESS)) {
                result = "成功";
            } else {
                result = status;
//                int i = 8 / 0;
            }
        } else {
            result = "群发失败";
            //int i = 8 / 0;
        }
        return result;
    }

    @Transactional
    public int addOrUpdateVip(String screen_value, String target_vips_count, String corp_code, String activity_vip_code) throws Exception {
        int target_vips = vipActivityService.updActiveCodeByType("target_vips", screen_value, corp_code, activity_vip_code);
        target_vips += vipActivityService.updActiveCodeByType("target_vips_count", target_vips_count, corp_code, activity_vip_code);
     //   target_vips += vipFsendService.updSendByType("sms_vips", screen_value, activity_vip_code);
        return target_vips;
    }
}
