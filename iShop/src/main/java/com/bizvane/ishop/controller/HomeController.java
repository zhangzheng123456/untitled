package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.entity.LoginLog;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    UserService userService;
    @Autowired
    CorpService corpService;
    @Autowired
    StoreService storeService;
    @Autowired
    FeedbackService feedbackService;
    @Autowired
    IceInterfaceService iceInterfaceService;


    private static final Logger logger = Logger.getLogger(HomeController.class);

    String id;

    //系统管理员主页面
    @RequestMapping(value = "/sys", method = RequestMethod.GET)
    @ResponseBody
    public String sysPage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject dashboard = new JSONObject();

            int corp_count = corpService.selectCount("");
            int store_count = storeService.selectCount("");
            int user_count = userService.selectCount("");
            String yesterday = TimeUtils.beforDays(1);
            int corp_new_count = corpService.selectCount(yesterday);
            int store_new_count = storeService.selectCount(yesterday);
            int user_new_count = userService.selectCount(yesterday);

            PageInfo<Feedback> feedback = feedbackService.selectAllFeedback(1, 6, "");
            dashboard.put("corp_count", corp_count);
            dashboard.put("store_count", store_count);
            dashboard.put("user_count", user_count);
            dashboard.put("corp_new_count", corp_new_count);
            dashboard.put("store_new_count", store_new_count);
            dashboard.put("user_new_count", user_new_count);
            dashboard.put("feedback", JSON.toJSONString(feedback.getList()));

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("");
            return dataBean.getJsonStr();

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        dataBean.setId(id);
        dataBean.setMessage("非系统管理员");
        return dataBean.getJsonStr();
    }

    //企业管理员主页面（区域排序）
    @RequestMapping(value = "/areaRanking", method = RequestMethod.POST)
    @ResponseBody
    public String areaRanking(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_id = request.getSession().getAttribute("user_id").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = new JSONObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String time = jsonObject.get("time").toString();
            String area_name = jsonObject.get("area_name").toString();
            String time_id = time;

            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_time_id = new Data("time_id", time_id, ValueType.PARAM);
            Data data_area_name = new Data("area_name", area_name, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_time_id.key, data_time_id);
            datalist.put(data_area_name.key, data_area_name);

            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.ACHVAreaRanking",datalist);
            logger.info("======"+dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //企业管理员/区经主页面（店铺排序）
    @RequestMapping(value = "/storeRanking", method = RequestMethod.POST)
    @ResponseBody
    public String storeRanking(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String time_id;
            String area_code = "";
            String user_id = request.getSession().getAttribute("user_id").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = new JSONObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String store_name = jsonObject.get("store_name").toString();

            if (jsonObject.has("time")) {
                time_id = jsonObject.get("time").toString().replace("-","");
            }else {
                time_id = Common.DATETIME_FORMAT_DAY_NO.format(new Data());
            }
            if (role_code.equals(Common.ROLE_GM)){
                area_code = "";
            }else if(role_code.equals(Common.ROLE_AM)){
                if (jsonObject.has("area_code")) {
                    area_code = jsonObject.get("area_code").toString();
                }else {
                    String code = request.getSession().getAttribute("area_code").toString();
                    String[] area_codes = code.replace(Common.STORE_HEAD,"").split(",");
                    area_code = area_codes[0];
                }
            }

            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_time_id = new Data("time_id", time_id, ValueType.PARAM);
            Data data_store_name = new Data("store_name", store_name, ValueType.PARAM);
            Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_time_id.key, data_time_id);
            datalist.put(data_store_name.key, data_store_name);
            datalist.put(data_area_code.key, data_area_code);

            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.ACHVStoreRanking",datalist);
            logger.info("======"+dataBox.data.get("message").value);

            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //店长/导购主页面（员工排序）
    @RequestMapping(value = "/staffRanking", method = RequestMethod.POST)
    @ResponseBody
    public String staffRanking(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String time_id;
            String store_id;
            String user_id = request.getSession().getAttribute("user_id").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = new JSONObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            if (jsonObject.has("time")) {
                time_id = jsonObject.get("time").toString().replace("-","");
            }else {
                time_id = Common.DATETIME_FORMAT_DAY_NO.format(new Data());
            }
            if (jsonObject.has("store_code")){
                store_id = jsonObject.get("store_code").toString();
            }else {
                String store_code = request.getSession().getAttribute("store_code").toString();
                String[] store_ids = store_code.replace(Common.STORE_HEAD,"").split(",");
                store_id = store_ids[0];
            }

            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_time_id = new Data("time_id", time_id, ValueType.PARAM);
            Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_time_id.key, data_time_id);
            datalist.put(data_store_id.key, data_store_id);

            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.ACHVStaffRanking",datalist);
            logger.info(dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //导购主页面
    @RequestMapping(value = "/staff", method = RequestMethod.GET)
    @ResponseBody
    public String staffPage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_id = request.getSession().getAttribute("user_id").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String store_code = request.getSession().getAttribute("store_code").toString();
            store_code = store_code.substring(1,store_code.length()-1);
            String time_id = Common.DATETIME_FORMAT_DAY_NO.format(new Data());

            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
            Data data_time_id = new Data("time_id", time_id, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_code.key, data_store_code);
            datalist.put(data_time_id.key, data_time_id);

            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.StaffACHVDashboard",datalist);
            logger.info(dataBox.data.get("message").value);
            String message = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(message);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}