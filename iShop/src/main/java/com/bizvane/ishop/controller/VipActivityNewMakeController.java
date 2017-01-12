package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.VipActivity;
import com.bizvane.ishop.entity.VipFsend;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by PC on 2017/1/5.
 */
@Controller
@RequestMapping("/vipActivity/arrange")
public class VipActivityNewMakeController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private VipFsendService vipFsendService;
    @Autowired
    private VipActivityService vipActivityService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    private VipActivityDetailService vipActivityDetailService;
    @Autowired
    private VipActivityMakeService vipActivityMakeService;


    @ResponseBody
    @Transactional
    @RequestMapping(value = "/addOrUpdateTask", method = RequestMethod.POST)
    public String addOrUpdateTask(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String user_code = request.getSession().getAttribute("user_code").toString();

            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);

            String message = jsonObj.get("message").toString();
            int count = vipActivityMakeService.addOrUpdateTask(message, user_code);
            if (count > 0) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("0");
                dataBean.setMessage("成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage("失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("失败");
        }
        return dataBean.getJsonStr();
    }


    @ResponseBody
    @Transactional
    @RequestMapping(value = "/addOrUpdateSend", method = RequestMethod.POST)
    public String addOrUpdateSend(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_code = request.getSession().getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);

            String message = jsonObj.get("message").toString();
           int count = vipActivityMakeService.addOrUpdateSend(message,user_code);
            if (count > 0) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("0");
                dataBean.setMessage("成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage("失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("失败");
        }
        return dataBean.getJsonStr();
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public String list(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);

            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);


            String activity_vip_code = jsonObject.get("activity_vip_code").toString();
            VipActivity vipActivity = vipActivityService.selActivityByCode(activity_vip_code);

            String corp_code = vipActivity.getCorp_code();
            JSONArray jsonArray_wx = new JSONArray();
            JSONArray jsonArray_sms = new JSONArray();
            JSONArray jsonArray_em = new JSONArray();

            List<VipFsend> sendByActivityCodes = vipFsendService.getSendByActivityCode(corp_code, activity_vip_code);
            for (int i = 0; i < sendByActivityCodes.size(); i++) {
                VipFsend vipFsend = sendByActivityCodes.get(i);
                if (vipFsend.getSend_type().equals("wx")) {
                    jsonArray_wx.add(vipFsend);
                } else if (vipFsend.getSend_type().equals("sms")) {
                    jsonArray_sms.add(vipFsend);
                } else if (vipFsend.getSend_type().equals("email")) {
                    jsonArray_em.add(vipFsend);
                }
            }

            List<Task> taskByActivityCodes = taskService.getTaskByActivityCode(corp_code, activity_vip_code);

            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
            result.put("tasklist", taskByActivityCodes);
            result.put("wxlist", jsonArray_wx);
            result.put("smslist", jsonArray_sms);
            result.put("emlist", jsonArray_em);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("0");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("失败");
        }
        return dataBean.getJsonStr();
    }

    @ResponseBody
    @RequestMapping(value = "/addOrUpdateVip", method = RequestMethod.POST)
    public String addOrUpdateVip(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            JSONArray screen = jsonObject.getJSONArray("screen");
            String activity_vip_code = jsonObject.get("activity_vip_code").toString();
            String target_vips_count = jsonObject.get("target_vips_count").toString();
            VipActivity vipActivity = vipActivityService.selActivityByCode(activity_vip_code);
            String corp_code = vipActivity.getCorp_code();

            JSONArray post_array = vipGroupService.vipScreen2Array(screen, corp_code, role_code, brand_code, area_code, store_code, user_code);

            String screen_value = post_array.toJSONString();
            int target_vips = vipActivityService.updActiveCodeByType("target_vips", screen_value, corp_code, activity_vip_code);
            target_vips += vipActivityService.updActiveCodeByType("target_vips_count", target_vips_count, corp_code, activity_vip_code);
            System.out.println("=========target_vips=========" + target_vips);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("0");
            dataBean.setMessage("成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("失败");
        }
        return dataBean.getJsonStr();
    }

    @ResponseBody
    @RequestMapping(value = "/addStrategyByTask", method = RequestMethod.POST)
    public String addStrategyByTask(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_code = request.getSession().getAttribute("user_code").toString();
            String param = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            String activity_vip_code = jsonObject.get("activity_vip_code").toString();
            VipActivity vipActivity = vipActivityService.selActivityByCode(activity_vip_code);
            String corp_code = vipActivity.getCorp_code();

            String tasklist = jsonObject.get("tasklist").toString();
            JSONArray jsonArray_task = JSON.parseArray(tasklist);

            int count = 0;
            String task_code_actvie_old = vipActivity.getTask_code();
            String task_code_actvie_new = "";
            for (int i = 0; i < jsonArray_task.size(); i++) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String task_code = "T" + sdf.format(new Date()) + Math.round(Math.random() * 9);
                Thread.sleep(1);
                task_code_actvie_new = task_code_actvie_new + task_code + ",";
                task_code_actvie_old = task_code_actvie_old + task_code + ",";

                JSONObject task_obj = new JSONObject(jsonArray_task.get(i).toString());
                Task task = WebUtils.JSON2Bean(task_obj, Task.class);
                if(task.getTask_title().equals("")||task.getTask_type_code().equals("")||task.getTask_description().equals("")||task.getTarget_start_time().equals("")||task.getTarget_end_time().equals("")){
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
            count += vipActivityService.updActiveCodeByType("task_code", task_code_actvie_old, corp_code, activity_vip_code);

            VipActivity vipActivity_new = new VipActivity();
            vipActivity_new.setCorp_code(corp_code);
            vipActivity_new.setActivity_code(activity_vip_code);
            vipActivity_new.setActivity_store_code(vipActivity.getActivity_store_code());
            vipActivity_new.setTask_code(task_code_actvie_new);

            String status = vipActivityService.executeTask(vipActivity_new, user_code);

            if (count > 0) {
                if(status.equals(Common.DATABEAN_CODE_SUCCESS)){
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId("0");
                    dataBean.setMessage("成功");
                }else{
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("0");
                    dataBean.setMessage(status);
                }
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage("失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("失败");
        }
        return dataBean.getJsonStr();
    }


    @ResponseBody
    @RequestMapping(value = "/addStrategyBySend", method = RequestMethod.POST)
    public String addStrategyBySend(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_code = request.getSession().getAttribute("user_code").toString();

            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);

            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);

            String activity_vip_code = jsonObject.get("activity_vip_code").toString();
            VipActivity vipActivity = vipActivityService.selActivityByCode(activity_vip_code);

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
            int count = 0;
            String send_code_actvie_old = vipActivity.getSms_code();
            String send_code_actvie_new = "";

            String corp_code = vipActivity.getCorp_code();

            for (int i = 0; i < jsonArray_wx.size(); i++) {
                JSONObject send_obj = new JSONObject(jsonArray_wx.get(i).toString());
                String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
                Thread.sleep(1);
                send_code_actvie_old = send_code_actvie_old + sms_code + ",";
                send_code_actvie_new = send_code_actvie_new + sms_code + ",";

                String content = "";
                String send_type = Common.SEND_TYPE_WX;
                content = send_obj.get("content").toString();

                VipFsend vipFsend = WebUtils.JSON2Bean(send_obj, VipFsend.class);

                if(vipFsend.getSend_time().equals("")||vipFsend.getContent().equals("")){
                    continue;
                }
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
                String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
                Thread.sleep(1);
                send_code_actvie_old = send_code_actvie_old + sms_code + ",";
                send_code_actvie_new = send_code_actvie_new + sms_code + ",";

                String content = "";
                String send_type = Common.SEND_TYPE_SMS;
                content = send_obj.get("content").toString();

                VipFsend vipFsend = WebUtils.JSON2Bean(send_obj, VipFsend.class);
                if(vipFsend.getSend_time().equals("")||vipFsend.getContent().equals("")){
                    continue;
                }
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
                String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
                Thread.sleep(1);
                send_code_actvie_old = send_code_actvie_old + sms_code + ",";
                send_code_actvie_new = send_code_actvie_new + sms_code + ",";

                String content = "";
                String send_type = Common.SEND_TYPE_EMAIL;
                content = send_obj.get("content").toString();

                VipFsend vipFsend = WebUtils.JSON2Bean(send_obj, VipFsend.class);
                if(vipFsend.getSend_time().equals("")||vipFsend.getContent().equals("")){
                    continue;
                }
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
            count += vipActivityService.updActiveCodeByType("sms_code", send_code_actvie_old, corp_code, activity_vip_code);

            VipActivity vipActivity_new = new VipActivity();
            vipActivity_new.setCorp_code(corp_code);
            vipActivity_new.setActivity_code(activity_vip_code);
            vipActivity_new.setActivity_store_code(vipActivity.getActivity_store_code());
            vipActivity_new.setSms_code(send_code_actvie_new);

            String status = vipActivityService.executeFsend(vipActivity_new, user_code);
            if (count > 0) {
                if(status.equals(Common.DATABEAN_CODE_SUCCESS)){
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId("0");
                    dataBean.setMessage("成功");
                }else{
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("0");
                    dataBean.setMessage(status);
                }
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage("失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("失败");
        }
        return dataBean.getJsonStr();
    }


    @ResponseBody
    @Transactional
    @RequestMapping(value = "/addStrategyByTicket", method = RequestMethod.POST)
    public String addStrategyByTicket(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = "";
            result = vipActivityDetailService.insert_new(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("成功");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("失败");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
