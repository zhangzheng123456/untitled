package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.LoginLog;
import com.bizvane.ishop.entity.TableManager;
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
import javax.servlet.http.HttpServletResponse;
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
    @Autowired
    TableManagerService tableManagerService;

    private static final Logger log = Logger.getLogger(LoginController.class);

    String id;


    @RequestMapping(value = "/")
    public String index(HttpServletRequest request) {
        String home = "";
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            System.out.println(role_code);

            if (role_code.equals(Common.ROLE_SYS)) {
                home = "home/index_admin";
            } else if (role_code.equals(Common.ROLE_GM)) {
                home = "home/index_gm";
            } else if (role_code.equals(Common.ROLE_AM)) {
                home = "home/index_am";
            } else if (role_code.equals(Common.ROLE_STAFF)) {
                home = "home/index_staff";
            } else {
                home = "login";
            }
            System.out.println(home);

            return home;
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        return "login";
    }

    @RequestMapping(value = "/login")
    public String loginIndex(HttpServletRequest request,HttpServletResponse response) {
        try {
            request.getSession().removeAttribute("user_id");
            request.getSession().removeAttribute("role_code");
            request.getSession().removeAttribute("group_code");
            request.getSession().removeAttribute("corp_code");
            request.getSession().removeAttribute("store_code");
            request.getSession().removeAttribute("menu");
            return "login";
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        return "login";
    }

    @RequestMapping(value = "/home/login")
    public String loginHome(HttpServletRequest request,HttpServletResponse response) {
        try {
            request.getSession().removeAttribute("user_id");
            request.getSession().removeAttribute("role_code");
            request.getSession().removeAttribute("group_code");
            request.getSession().removeAttribute("corp_code");
            request.getSession().removeAttribute("store_code");
            request.getSession().removeAttribute("menu");
            response.sendRedirect("/login.html");

            return "login";
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        return "";
    }

    @RequestMapping(value = "/login_out")
    public String loginOut(HttpServletRequest request) {
        try {
            request.getSession().removeAttribute("user_id");
            request.getSession().removeAttribute("role_code");
            request.getSession().removeAttribute("group_code");
            request.getSession().removeAttribute("corp_code");
            request.getSession().removeAttribute("store_code");
            request.getSession().removeAttribute("menu");

            return "login";
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        return "";
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
            log.info(ex.getMessage());
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
        try {
            String param = request.getParameter("param");
            log.info("json--authcode-------------" + param);
            JSONObject jsonObj = new JSONObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String phone = jsonObject.get("PHONENUMBER").toString();
            System.out.println(phone);
            String msg = userService.getAuthCode(phone, "网页注册");
            if (msg.equals(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("fail");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
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
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
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
            if (user_info == null || user_info.getString("status").contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(user_info.getString("error"));
            } else {
                System.out.println(user_info);
                //插入登录日志
                Date now = new Date();
                LoginLog log = new LoginLog();
                log.setPlatform("WEB");
                log.setPhone(phone);
                log.setCreated_date(Common.DATETIME_FORMAT.format(now));
                log.setModified_date(Common.DATETIME_FORMAT.format(now));
                log.setModifier("root");
                log.setCreater("root");
                log.setIsactive(Common.IS_ACTIVE_Y);

                loginLogService.insertLoginLog(log);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(user_info.toString());
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
            log.info(ex.getMessage());
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
            String user_id = request.getSession().getAttribute("user_id").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_type = request.getSession().getAttribute("user_type").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            User user = userService.getUserById(Integer.parseInt(user_id));
            menu = functionService.selectAllFunctions(corp_code,user_code, group_code, role_code);
            menus.put("menu", menu);
            menus.put("user_type", user_type);
            menus.put("role_code", role_code);
            menus.put("avatar", user.getAvatar());
            menus.put("user_name", user.getUser_name());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(menus.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 获取详细画面权限
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public String detailAction(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject menus = new JSONObject();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String function_code = request.getParameter("funcCode");
            JSONArray actions_detail = functionService.selectActionByFun(corp_code, user_code, group_code, role_code, "D" + function_code);

            JSONArray actions_fun = functionService.selectActionByFun(corp_code, user_code, group_code, role_code, function_code);
            for (int i = 0; i < actions_fun.size(); i++) {
                String act = actions_fun.get(i).toString();
                JSONObject obj = new JSONObject(act);
                if (obj.get("act_name").equals("edit")) {
                    actions_detail.add(obj);
                }
            }

            menus.put("actions", actions_detail);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(menus.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 获取列表画面可筛选列
     */
    @RequestMapping(value = "/list/filter_column", method = RequestMethod.GET)
    @ResponseBody
    public String listFilterColumn(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONArray cols = new JSONArray();
            String function_code = request.getParameter("funcCode");
            List<TableManager> col = tableManagerService.selByCode(function_code);
            for (int i = 0; i < col.size(); i++) {
                TableManager table = col.get(i);
                String col_name = table.getColumn_name();
                String show_name = table.getShow_name();
                String type = table.getFilter_type();

                JSONObject obj = new JSONObject();
                obj.put("col_name",col_name);
                obj.put("show_name",show_name);
                obj.put("type",type);
                if (type.equals("select")){
                    String value = table.getFilter_value();
                    obj.put("value",value);
                }else{
                    obj.put("value","");
                }
                cols.add(obj);
            }
            JSONObject filter = new JSONObject();
            filter.put("filter",cols);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(filter.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}