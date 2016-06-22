package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.LoginLog;
import com.bizvane.ishop.service.*;
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
    ValidateCodeService validateCodeService;
    @Autowired
    LoginLogService loginLogService;
    @Autowired
    FunctionService functionService;

    private static final Logger log = Logger.getLogger(HomeController.class);

    String id;
    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);

    //系统管理员主页面
    @RequestMapping(value = "/home/sys", method = RequestMethod.GET)
    @ResponseBody
    public String sysPage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject dashboard = new JSONObject();
            JSONArray menu;
            String role_code = request.getSession().getAttribute("role_code").toString();
            if (role_code.equals(Common.ROLE_SYS)) {
                int corp_count = corpService.selectCount();
                int store_count = storeService.selectCount();
                int user_count = userService.selectCount();
                dashboard.put("corp_count", corp_count);
                dashboard.put("store_count", store_count);
                dashboard.put("user_count", user_count);

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(dashboard.toString());
                return dataBean.getJsonStr();

            }
        }catch (Exception ex) {
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