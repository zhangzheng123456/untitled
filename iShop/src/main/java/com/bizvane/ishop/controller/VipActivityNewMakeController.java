package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.VipFsend;
import com.bizvane.ishop.service.TaskService;
import com.bizvane.ishop.service.VipActivityService;
import com.bizvane.ishop.service.VipFsendService;
import com.bizvane.ishop.utils.WebUtils;
import org.aspectj.weaver.AnnotationTargetKind;
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
            JSONObject jsonObject = new JSONObject(message);

            String tasklist = jsonObject.get("tasklist").toString();
            JSONArray jsonArray_task = JSON.parseArray(tasklist);

            int count = 0;
            String task_code_actvie = "";
            String corp_code = "";
            String activity_vip_code = "";
            for (int i = 0; i < jsonArray_task.size(); i++) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String task_code = "T" + sdf.format(new Date()) + Math.round(Math.random() * 9);
                Thread.sleep(1);
                task_code_actvie = task_code_actvie + task_code + ",";

                JSONObject task_obj = new JSONObject(jsonArray_task.get(i).toString());
                Task task = WebUtils.JSON2Bean(task_obj, Task.class);
                corp_code = task.getCorp_code();
                activity_vip_code = task.getActivity_vip_code();
                taskService.delTaskByActivityCode(corp_code, activity_vip_code);
                task.setTask_code(task_code);

                Date now = new Date();
                task.setCreated_date(Common.DATETIME_FORMAT.format(now));
                task.setCreater(user_code);
                task.setModified_date(Common.DATETIME_FORMAT.format(now));
                task.setModifier(user_code);
                task.setIsactive("Y");

                count += taskService.insertTask(task);

            }
            count += vipActivityService.updActiveCodeByType("task_code", task_code_actvie, corp_code, activity_vip_code);
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
            JSONObject jsonObject = new JSONObject(message);

            String sendlist = jsonObject.get("sendlist").toString();
            JSONArray jsonArray_send = JSON.parseArray(sendlist);

            int count = 0;
            String send_code_actvie = "";
            String corp_code = "";
            String activity_vip_code = "";
            for (int i = 0; i < jsonArray_send.size(); i++) {
                JSONObject send_obj = new JSONObject(jsonArray_send.get(i).toString());
                corp_code = send_obj.get("corp_code").toString();
                activity_vip_code = send_obj.get("activity_vip_code").toString();
                String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
                Thread.sleep(1);
                send_code_actvie = send_code_actvie + sms_code + ",";

                vipFsendService.delSendByActivityCode(corp_code, activity_vip_code);
                String content = "";
                String send_type = send_obj.get("send_type").toString();
                if (send_type.equals("wx") || send_type.equals("sms")) {
                    content = send_obj.get("content").toString();
                } else if (send_type.equals("email")) {

                }
                VipFsend vipFsend = WebUtils.JSON2Bean(send_obj, VipFsend.class);
                vipFsend.setSms_code(sms_code);
                vipFsend.setContent(content);
                vipFsend.setSend_scope("vip_condition");

                Date now = new Date();
                vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setCreater(user_code);
                vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipFsend.setModifier(user_code);
                vipFsend.setIsactive("Y");

                count += vipFsendService.insertSend(vipFsend);
            }
            count += vipActivityService.updActiveCodeByType("sms_code", send_code_actvie, corp_code, activity_vip_code);
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

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public String list(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);

            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String activity_vip_code = jsonObject.get("activity_vip_code").toString();
            List<VipFsend> sendByActivityCodes = vipFsendService.getSendByActivityCode(corp_code, activity_vip_code);

            List<Task> taskByActivityCodes = taskService.getTaskByActivityCode(corp_code, activity_vip_code);

            com.alibaba.fastjson.JSONObject result=new com.alibaba.fastjson.JSONObject();
            result.put("tasklist",taskByActivityCodes);
            result.put("sendlist",sendByActivityCodes);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("0");
            dataBean.setMessage(result.toJSONString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("失败");
        }
        return dataBean.getJsonStr();
    }

}
