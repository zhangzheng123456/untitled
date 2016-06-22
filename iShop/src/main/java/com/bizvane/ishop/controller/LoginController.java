package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.LoginLog;
import com.bizvane.ishop.entity.User;
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
import java.util.*;

@Controller
public class LoginController {
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

    private static final Logger log = Logger.getLogger(LoginController.class);

    String id;
    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);


    @RequestMapping(value = "/")
    public String index(HttpServletRequest request) {
        String role_code = request.getSession().getAttribute("role_code").toString();
        System.out.println(role_code);
        String home = "";
        if (role_code.equals(Common.ROLE_SYS)){
            home = "home/index_admin";
        }else if(role_code.equals(Common.ROLE_GM)){
            home = "home/index_gm";
        }else if(role_code.equals(Common.ROLE_AM)){
            home = "home/index_am";
        }else if(role_code.equals(Common.ROLE_STAFF)){
           home = "home/index_staff";
        }else {
            home = "login";
        }
        System.out.println(home);

        return home;
    }

    @RequestMapping(value = "/login")
    public String loginIndex(HttpServletRequest request) {
        request.getSession().removeAttribute("user_id");
        request.getSession().removeAttribute("role_code");
        request.getSession().removeAttribute("group_code");
        request.getSession().removeAttribute("corp_code");
        request.getSession().removeAttribute("store_code");
        request.getSession().removeAttribute("menu");

        return "login";
    }

    @RequestMapping(value = "/home/login")
    public String loginHome(HttpServletRequest request) {
        request.getSession().removeAttribute("user_id");
        request.getSession().removeAttribute("role_code");
        request.getSession().removeAttribute("group_code");
        request.getSession().removeAttribute("corp_code");
        request.getSession().removeAttribute("store_code");
        request.getSession().removeAttribute("menu");

        return "login";
    }
    @RequestMapping(value = "/login_out")
    public String loginOut(HttpServletRequest request) {
        request.getSession().removeAttribute("user_id");
        request.getSession().removeAttribute("role_code");
        request.getSession().removeAttribute("group_code");
        request.getSession().removeAttribute("corp_code");
        request.getSession().removeAttribute("store_code");
        request.getSession().removeAttribute("menu");

        return "login";
    }

    /**
     * 手机号是否已注册
     */
    @RequestMapping(value = "/phone_exist", method = RequestMethod.GET)
    @ResponseBody
    public String phoneExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            log.info("json--phoneExist-------------" + param);
            JSONObject jsonObj = new JSONObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String phone = jsonObject.get("PHONENUMBER").toString();
            System.out.println(phone);
            String user = userService.userPhoneExist(phone);
            System.out.println(user);
            if (user.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("the phone can registered");
                return dataBean.getJsonStr();
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("the phone has registered");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 获取验证码
     */
    @RequestMapping(value = "/authcode", method = RequestMethod.POST)
    @ResponseBody
    public String getAuthCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String param = request.getParameter("param");
        log.info("json--authcode-------------" + param);
        JSONObject jsonObj = new JSONObject(param);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        JSONObject jsonObject = new JSONObject(message);
        String phone = jsonObject.get("PHONENUMBER").toString();
        System.out.println(phone);
        String msg = userService.getAuthCode(phone, "网页注册");
        if (msg.equals(Common.DATABEAN_CODE_SUCCESS)) {
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } else {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage("fail");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 点击注册
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public String register(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        System.out.println("-------------------");
        try {
            String param = request.getParameter("param");
            log.info("json--register-------------" + param);
            JSONObject jsonObj = new JSONObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = userService.register(message);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(result);
                dataBean.setId(id);
                dataBean.setMessage("register success");
            } else {
                System.out.println("---------auth_code-xxx---------");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("authcode error");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 点击登录
     */
    @RequestMapping(value = "/userlogin", method = RequestMethod.POST)
    @ResponseBody
    public String login(HttpServletRequest request) {
        log.info("------------starttime" + new Date());
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            log.info("json---------------" + param);
            JSONObject jsonObj = new JSONObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String phone = jsonObject.get("phone").toString();
            String password = jsonObject.get("password").toString();
            log.info("phone:" + phone + " password:" + password);
            log.info("------------start search" + new Date());
            JSONObject user_info = userService.login(request, phone, password);
            if (user_info == null) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("login fail");
            } else {
                System.out.println(user_info);
                //插入登录日志
                Date now = new Date();
                LoginLog log = new LoginLog();
                log.setPlatform("WEB");
                log.setPhone(phone);
                log.setCreated_date(sdf.format(now));
                loginLogService.insertLoginLog(log);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(user_info.toString());
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();;
        }
        return dataBean.getJsonStr();
    }

    /**
     * 获取导航栏
     */
    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    @ResponseBody
    public String menu(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject menus = new JSONObject();
            JSONArray menu;
            String user_code = request.getSession().getAttribute("user_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_type = request.getSession().getAttribute("user_type").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            menu = functionService.selectAllFunctions(corp_code+user_code,corp_code+group_code, role_code);
            request.getSession().setAttribute("menu", menu);
            menus.put("menu",menu);
            menus.put("user_type",user_type);
            menus.put("role_code",role_code);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(menus.toString());
        }catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}