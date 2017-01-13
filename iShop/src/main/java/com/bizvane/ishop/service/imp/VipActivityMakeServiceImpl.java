package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.VipActivity;
import com.bizvane.ishop.entity.VipFsend;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by PC on 2017/1/12.
 */
@Service
public class VipActivityMakeServiceImpl implements VipActivityMakeService{
    @Autowired
    private TaskService taskService;
    @Autowired
    private VipFsendService vipFsendService;
    @Autowired
    private VipActivityService vipActivityService;
    @Autowired
    VipGroupService vipGroupService;


    @Transactional
    public int addOrUpdateTask(String message, String user_code) throws Exception {
        JSONObject jsonObject = new JSONObject(message);
        String activity_vip_code = jsonObject.get("activity_vip_code").toString();

        String task_status = jsonObject.get("task_status").toString();
        VipActivity vipActivity = vipActivityService.selActivityByCode(activity_vip_code);
        String corp_code = vipActivity.getCorp_code();
        int count = 0;
        if(task_status.equals("N")){
            count += vipActivityService.updActiveCodeByType("task_status", "N", corp_code, activity_vip_code);
            count += vipActivityService.updActiveCodeByType("task_code", "", corp_code, activity_vip_code);
            taskService.delTaskByActivityCode(corp_code, activity_vip_code);
        }else {
            String tasklist = jsonObject.get("tasklist").toString();
            JSONArray jsonArray_task = JSON.parseArray(tasklist);

            String task_code_actvie = "";

            taskService.delTaskByActivityCode(corp_code, activity_vip_code);
            for (int i = 0; i < jsonArray_task.size(); i++) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String task_code = "T" + sdf.format(new Date()) + Math.round(Math.random() * 9);
                Thread.sleep(1);
                task_code_actvie = task_code_actvie + task_code + ",";

                JSONObject task_obj = new JSONObject(jsonArray_task.get(i).toString());
                Task task = WebUtils.JSON2Bean(task_obj, Task.class);

                if (task.getTask_title().equals("") || task.getTask_type_code().equals("") || task.getTask_description().equals("") || task.getTarget_start_time().equals("") || task.getTarget_end_time().equals("")) {
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
        }

        return count;
    }

    @Transactional
    public int addOrUpdateSend(String message, String user_code) throws Exception {
        JSONObject jsonObject = new JSONObject(message);
        String activity_vip_code = jsonObject.get("activity_vip_code").toString();
        VipActivity vipActivity = vipActivityService.selActivityByCode(activity_vip_code);
        String corp_code = vipActivity.getCorp_code();
        String send_status = jsonObject.get("send_status").toString();
        int count = 0;
        if(send_status.equals("N")){
            count += vipActivityService.updActiveCodeByType("send_status", "N", corp_code, activity_vip_code);
            count += vipActivityService.updActiveCodeByType("sms_code", "", corp_code, activity_vip_code);
            vipFsendService.delSendByActivityCode(corp_code, activity_vip_code);
        }else {
            String wxlist = jsonObject.get("wxlist").toString();
            JSONArray jsonArray_wx = new JSONArray();
            if (!wxlist.equals("")) {
                jsonArray_wx = JSON.parseArray(wxlist);
            }

            String smslist = jsonObject.get("smslist").toString();
            JSONArray jsonArray_sms = new JSONArray();
            if (!smslist.equals("")) {
                jsonArray_sms = JSON.parseArray(smslist);
            }

            String emlist = jsonObject.get("emlist").toString();
            JSONArray jsonArray_em = new JSONArray();
            if (!emlist.equals("")) {
                jsonArray_em = JSON.parseArray(emlist);
            }

            String send_code_actvie = "";


            vipFsendService.delSendByActivityCode(corp_code, activity_vip_code);
            for (int i = 0; i < jsonArray_wx.size(); i++) {
                JSONObject send_obj = new JSONObject(jsonArray_wx.get(i).toString());
                VipFsend vipFsend = WebUtils.JSON2Bean(send_obj, VipFsend.class);
                if (vipFsend.getSend_time().equals("") || vipFsend.getContent().equals("")) {
                    continue;
                }
                String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
                Thread.sleep(1);
                send_code_actvie = send_code_actvie + sms_code + ",";


                String content = "";
                String send_type = "wxmass";
                content = send_obj.get("content").toString();


                vipFsend.setSms_code(sms_code);
                vipFsend.setSend_type(send_type);
                vipFsend.setCorp_code(corp_code);
                vipFsend.setContent(content);
                vipFsend.setSend_scope("vip_condition");
                vipFsend.setActivity_vip_code(activity_vip_code);

                Date now = new Date();
                vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setCreater(user_code);
                vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setModifier(user_code);
                vipFsend.setIsactive("Y");

                count += vipFsendService.insertSend(vipFsend);
            }
            for (int i = 0; i < jsonArray_sms.size(); i++) {
                JSONObject send_obj = new JSONObject(jsonArray_sms.get(i).toString());
                VipFsend vipFsend = WebUtils.JSON2Bean(send_obj, VipFsend.class);
                if (vipFsend.getSend_time().equals("") || vipFsend.getContent().equals("")) {
                    continue;
                }
                String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
                Thread.sleep(1);
                send_code_actvie = send_code_actvie + sms_code + ",";

                String content = "";
                String send_type = "sms";
                content = send_obj.get("content").toString();


                vipFsend.setSms_code(sms_code);
                vipFsend.setSend_type(send_type);
                vipFsend.setCorp_code(corp_code);
                vipFsend.setContent(content);
                vipFsend.setSend_scope("vip_condition");
                vipFsend.setActivity_vip_code(activity_vip_code);

                Date now = new Date();
                vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setCreater(user_code);
                vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setModifier(user_code);
                vipFsend.setIsactive("Y");

                count += vipFsendService.insertSend(vipFsend);
            }
            for (int i = 0; i < jsonArray_em.size(); i++) {
                JSONObject send_obj = new JSONObject(jsonArray_em.get(i).toString());
                VipFsend vipFsend = WebUtils.JSON2Bean(send_obj, VipFsend.class);
                if (vipFsend.getSend_time().equals("") || vipFsend.getContent().equals("")) {
                    continue;
                }
                String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
                Thread.sleep(1);
                send_code_actvie = send_code_actvie + sms_code + ",";

                String content = "";
                String send_type = "email";
                content = send_obj.get("content").toString();


                vipFsend.setSms_code(sms_code);
                vipFsend.setSend_type(send_type);
                vipFsend.setCorp_code(corp_code);
                vipFsend.setContent(content);
                vipFsend.setSend_scope("vip_condition");
                vipFsend.setActivity_vip_code(activity_vip_code);

                Date now = new Date();
                vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setCreater(user_code);
                vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setModifier(user_code);
                vipFsend.setIsactive("Y");

                count += vipFsendService.insertSend(vipFsend);
            }
            count += vipActivityService.updActiveCodeByType("send_status", "Y", corp_code, activity_vip_code);
            count += vipActivityService.updActiveCodeByType("sms_code", send_code_actvie, corp_code, activity_vip_code);
        }
        return count;
    }
}
