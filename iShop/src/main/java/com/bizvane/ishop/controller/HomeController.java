package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.entity.LoginLog;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.TimeUtils;
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

@Controller
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
    ValidateCodeService validateCodeService;
    @Autowired
    LoginLogService loginLogService;
    @Autowired
    FunctionService functionService;

    private static final Logger log = Logger.getLogger(HomeController.class);

    String id;

    //系统管理员主页面
    @RequestMapping(value = "/home/sys", method = RequestMethod.GET)
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
}