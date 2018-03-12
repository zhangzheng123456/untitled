package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.VipActivity;
import com.bizvane.ishop.entity.VipFsend;
import com.bizvane.ishop.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
    private VipActivityMakeService vipActivityMakeService;


    @ResponseBody
    @Transactional
    @RequestMapping(value = "/addOrUpdateTask", method = RequestMethod.POST)
    public String addOrUpdateTask(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String user_code = request.getSession().getAttribute("user_code").toString();

            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);

            String message = jsonObj.get("message").toString();
            int count = vipActivityMakeService.addOrUpdateTask(message, user_code);
            if (count > 0) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("0");
                dataBean.setMessage("成功");
            }else if(count==-1){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("0");
                dataBean.setMessage("最多只能创建10条任务");
            }  else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage("添加任务失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("添加任务失败");
        }
        return dataBean.getJsonStr();
    }


    @ResponseBody
    @Transactional
    @RequestMapping(value = "/addOrUpdateSend", method = RequestMethod.POST)
    public String addOrUpdateSend(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String group_code = request.getSession().getAttribute("group_code").toString();

        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj =  JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String status = vipActivityMakeService.addOrUpdateSend(message,user_code,group_code,role_code,brand_code,area_code,store_code);
            if (status.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("0");
                dataBean.setMessage("成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage(status);
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
            JSONObject jsonObj =  JSONObject.parseObject(jsString);

            String message = jsonObj.get("message").toString();
            JSONObject jsonObject =  JSONObject.parseObject(message);
            String activity_vip_code = jsonObject.get("activity_vip_code").toString();
            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_vip_code);

            String corp_code = vipActivity.getCorp_code();
            JSONArray jsonArray_wx = new JSONArray();
            JSONArray jsonArray_sms = new JSONArray();
            JSONArray jsonArray_em = new JSONArray();

            List<VipFsend> sendByActivityCodes = vipFsendService.getSendByActivityCode(corp_code, activity_vip_code);
            for (int i = 0; i < sendByActivityCodes.size(); i++) {
                VipFsend vipFsend = sendByActivityCodes.get(i);
                if (vipFsend.getSend_type().equals("wxmass")) {
                    jsonArray_wx.add(vipFsend);
                } else if (vipFsend.getSend_type().equals("sms")) {
                    jsonArray_sms.add(vipFsend);
                } else if (vipFsend.getSend_type().equals("email")) {
                    jsonArray_em.add(vipFsend);
                }
            }

            List<Task> taskByActivityCodes = taskService.getTaskByActivityCode(corp_code, activity_vip_code);

            JSONObject result = new JSONObject();
            result.put("tasklist", taskByActivityCodes);
            result.put("wxlist", jsonArray_wx);
            result.put("smslist", jsonArray_sms);
            result.put("emlist", jsonArray_em);
            result.put("task_status", vipActivity.getTask_status());
            result.put("send_status", vipActivity.getSend_status());

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
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            JSONArray screen = jsonObject.getJSONArray("screen");
            String activity_vip_code = jsonObject.get("activity_vip_code").toString();
            String target_vips_count = jsonObject.get("target_vips_count").toString();
            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_vip_code);
            String corp_code = vipActivity.getCorp_code();

            JSONArray post_array = vipGroupService.vipScreen2Array(screen, corp_code, role_code, brand_code, area_code, store_code, user_code);

            String screen_value = post_array.toJSONString();

            int target_vips = vipActivityMakeService.addOrUpdateVip(screen_value, target_vips_count, corp_code, activity_vip_code);

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
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            String count = vipActivityMakeService.addStrategyByTask(message, user_code);
            if (count.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("0");
                dataBean.setMessage("success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage(count);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("添加任务失败");
        }
        return dataBean.getJsonStr();
    }


    @ResponseBody
    @RequestMapping(value = "/addStrategyBySend", method = RequestMethod.POST)
    public String addStrategyBySend(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String result="群发失败";
        try {
            String user_code = request.getSession().getAttribute("user_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code = request.getSession().getAttribute("area_code").toString();
            String store_code = request.getSession().getAttribute("store_code").toString();
            
            String jsString = request.getParameter("param");
            JSONObject jsonObj =  JSONObject.parseObject(jsString);

            String message = jsonObj.get("message").toString();

            result = vipActivityMakeService.addStrategyBySend(message, user_code,group_code,role_code,brand_code,area_code,store_code);

            if (result.equals("成功")) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("0");
                dataBean.setMessage("成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("-1");
                dataBean.setMessage(result);
            }
        }catch (java.lang.ArithmeticException im){
            System.out.println("===============自定义异常========================================");
            result = "群发时间小于当前时间";
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(result);
            im.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(result);
        }
        return dataBean.getJsonStr();
    }


}
