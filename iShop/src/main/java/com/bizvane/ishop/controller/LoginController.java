package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.OssUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.*;

@Controller
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    CorpService corpService;
    @Autowired
    GroupService groupService;
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
    @Autowired
    LocationService locationService;
    @Autowired
    WeimobService weimobService;
    @Autowired
    AppversionService appversionService;
    @Autowired
    VipParamService vipParamService;
    @Autowired
    ParamConfigureService paramConfigureService;
    @Autowired
    CorpParamService corpParamService;
    @Autowired
    VipActivityService vipActivityService;

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
            request.getSession().removeAttribute("user_code");
            request.getSession().removeAttribute("role_code");
            request.getSession().removeAttribute("group_code");
            request.getSession().removeAttribute("corp_code");
            request.getSession().removeAttribute("store_code");
            request.getSession().removeAttribute("area_code");
            request.getSession().removeAttribute("brand_code");
            request.getSession().removeAttribute("manager_corp");
            request.getSession().removeAttribute("corp_code_cm");
            request.getSession().removeAttribute("logout");

            return "login";
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        return "login";
    }

    @RequestMapping(value = "/home/login")
    public String loginHome(HttpServletRequest request,HttpServletResponse response) {
        try {
            request.getSession().removeAttribute("user_code");
            request.getSession().removeAttribute("role_code");
            request.getSession().removeAttribute("group_code");
            request.getSession().removeAttribute("corp_code");
            request.getSession().removeAttribute("store_code");
            request.getSession().removeAttribute("area_code");
            request.getSession().removeAttribute("brand_code");
            request.getSession().removeAttribute("manager_corp");
            request.getSession().removeAttribute("corp_code_cm");
            request.getSession().removeAttribute("logout");
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
            request.getSession().removeAttribute("user_code");
            request.getSession().removeAttribute("role_code");
            request.getSession().removeAttribute("group_code");
            request.getSession().removeAttribute("corp_code");
            request.getSession().removeAttribute("store_code");
            request.getSession().removeAttribute("area_code");
            request.getSession().removeAttribute("brand_code");
            request.getSession().removeAttribute("manager_corp");
            request.getSession().removeAttribute("corp_code_cm");
            request.getSession().removeAttribute("logout");
            return "login";
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        return "";
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
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String phone = jsonObject.get("PHONENUMBER").toString();
            String corp_code = "";
            if (jsonObject.containsKey("activity_code")){
                VipActivity activity = vipActivityService.getActivityByCode(jsonObject.getString("activity_code"));
                corp_code = activity.getCorp_code();
            }
            System.out.println(phone);
            String msg = userService.getAuthCode(phone,corp_code);
            if (corp_code.equals(""))
                //验证码记录
                userService.saveAuthCode(phone,msg,"web");

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(msg);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 校验验证码
     */
    @RequestMapping(value = "/checkAuthcode", method = RequestMethod.POST)
    @ResponseBody
    public String checkAuthcode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            log.info("json--authcode-------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String phone = jsonObject.get("phone").toString();
            String authcode = jsonObject.get("authcode").toString();

            ValidateCode validateCode = validateCodeService.selectByPhone(phone,authcode);
            if (validateCode == null) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("fail");
            } else {
                Date nowtime = new Date();
                Date modifided_date = Common.DATETIME_FORMAT.parse(validateCode.getModified_date());
                long timediff = (nowtime.getTime() - modifided_date.getTime());
                if (timediff<3600000) {
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage("ok");
                }else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("fail");
                }
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
//    @RequestMapping(value = "/register", method = RequestMethod.POST)
//    @ResponseBody
//    public String register(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        System.out.println("-------------------");
//        try {
//            String param = request.getParameter("param");
//            log.info("json--register-------------" + param);
//            JSONObject jsonObj = new JSONObject(param);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            String result = userService.register(message);
//            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
//                dataBean.setCode(result);
//                dataBean.setId(id);
//                dataBean.setMessage("register success");
//            } else {
//                System.out.println("---------auth_code-xxx---------");
//                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setId(id);
//                dataBean.setMessage(result);
//            }
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

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
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String phone = jsonObject.get("phone").toString();
            String password = jsonObject.get("password").toString();
            log.info("phone:" + phone + " password:" + password);
            log.info("------------start search" + new Date());
            JSONObject user_info = userService.login(request, phone, password);
//            weimobService.generateToken(CommonValue.CLIENT_ID, CommonValue.CLIENT_SECRET);

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

            User user = userService.selectUserById(user_id);
            menu = functionService.selectAllFunctions(corp_code,user_code, group_code, role_code);
            menus.put("menu", menu);
            menus.put("user_type", user_type);
            menus.put("role_code", role_code);
            menus.put("avatar", user.getAvatar());
            menus.put("user_name", user.getUser_name());
            menus.put("corp_name", user.getCorp_name());
            String version_id = "";
            if (user.getVersion_id() != null)
                version_id = user.getVersion_id();

            menus.put("version_id", version_id);
            menus.put("version_describe", "");
            List<Appversion> appversion = appversionService.selLatestVersion();
            if (appversion.size() > 0 && !version_id.equals(appversion.get(0).getVersion_id())){
                String describe = appversion.get(0).getVersion_describe();
                menus.put("version_describe", describe);
                version_id = appversion.get(0).getVersion_id();
            }
            user.setVersion_id(version_id);
            userService.updateUser(user);

            //是否显示【退出登录】（Y:显示，N:不显示）
            String logout = "Y";
            if (request.getSession().getAttribute("logout") != null && !request.getSession().getAttribute("logout").equals("")
                    && request.getSession().getAttribute("logout").equals("N")){
                logout = "N";
            }
            menus.put("logout", logout);


            String home_logo = "";
            ParamConfigure param = paramConfigureService.getParamByKey(CommonValue.HOME_LOGO, Common.IS_ACTIVE_Y);
            if (param != null){
                String id = String.valueOf(param.getId());
                List<CorpParam> corpParams = corpParamService.selectByCorpParam(corp_code, id, Common.IS_ACTIVE_Y);
                if (corpParams.size() > 0)
                    home_logo = corpParams.get(0).getParam_value();
            }
            menus.put("home_logo", home_logo);


            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(menus.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 获取列表画面权限
     */
    @RequestMapping(value = "/list/action", method = RequestMethod.POST)
    @ResponseBody
    public String listAction(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject menus = new JSONObject();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String param = request.getParameter("param");
            log.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String function_code = jsonObject.get("funcCode").toString();
            //获取动作权限
            List<Map<String,String>> actions = functionService.selectActionByFun(corp_code, user_code, group_code, role_code, function_code);
            //获取列表显示字段权限
            List<Map<String,String>> columns = functionService.selectColumnByFun(corp_code, user_code, group_code, role_code, function_code);

//            String HIDE_BASIC_SCREEN = "N";
//            List<CorpParam> corpParams = corpParamService.selectParamByName(corp_code,CommonValue.HIDE_BASIC_SCREEN);
//            if (corpParams.size() > 0){
//                HIDE_BASIC_SCREEN = corpParams.get(0).getParam_value();
//            }
//            Map<String,String> action = new HashMap<String, String>();
//            action.put("HIDE_BASIC_SCREEN", HIDE_BASIC_SCREEN);
//            actions.add(action);
            menus.put("actions", actions);
            menus.put("columns", columns);
            menus.put("role_code",role_code);
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
    @RequestMapping(value = "/detail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
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
            List<Map<String,String>> actions_detail = functionService.selectActionByFun(corp_code, user_code, group_code, role_code, "D" + function_code);

            //获取详情页动作权限
            List<Map<String,String>> actions_fun = functionService.selectActionByFun(corp_code, user_code, group_code, role_code, function_code);
            for (int i = 0; i < actions_fun.size(); i++) {
                Map<String,String> act = actions_fun.get(i);
                if (act.get("act_name").equals("edit") || act.get("act_name").equals("cancelVip")) {
                    actions_detail.add(act);
                }
            }

            //获取详情可编辑字段
            List<Map<String,String>> columns = functionService.selectRWByFun(corp_code, user_code, group_code, role_code, function_code);

            menus.put("actions", actions_detail);
            menus.put("columns", columns);

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
     * 获取列表画面可筛选列(公共方法)
     */
    @RequestMapping(value = "/list/filter_column", method = RequestMethod.GET)
    @ResponseBody
    public String listFilterColumn(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            JSONArray cols = new JSONArray();
            String function_code = request.getParameter("funcCode");
            //获取可筛选列表
            List<TableManager> col = tableManagerService.selByCode(function_code);

            //获取列表显示字段权限
            List<Map<String,String>> columns = functionService.selectColumnByFun(corp_code, user_code, group_code, role_code, function_code);

            for (int i = 0; i < col.size(); i++) {
                TableManager table = col.get(i);
                String col_name = table.getColumn_name();
                String show_name = table.getShow_name();
                String type = table.getFilter_type();


                //过滤，不显示列，筛选条件不显示
                for (int j = 0; j < columns.size(); j++) {
                    if (col_name.equals(columns.get(j).get("column_name"))){
                        JSONObject obj = new JSONObject();
                        obj.put("col_name",col_name);
                        obj.put("show_name",show_name);
                        obj.put("type",type);
                        if (type.equals("select")){
                            JSONArray values = new JSONArray();
                            if (col_name.equals("group_name")){
                                List<Group> groups = new ArrayList<Group>();
                                if (role_code.equals(Common.ROLE_GM)){
                                    groups = groupService.getGroupAll(corp_code,"");
                                }else if (role_code.equals(Common.ROLE_SYS)){
                                    obj.put("value","");
                                    obj.put("type","text");
                                    cols.add(obj);
                                    continue;
                                }else {
                                    groups = groupService.getGroupAll(corp_code,role_code);
                                }
                                for (int k = 0; k < groups.size(); k++) {
                                    JSONObject object = new JSONObject();
                                    object.put("key",groups.get(k).getGroup_name());
                                    object.put("value",groups.get(k).getGroup_name());
                                    values.add(object);
                                }
                                JSONObject object = new JSONObject();
                                object.put("key","全部");
                                object.put("value","");
                                values.add(object);
                            }else {
                                String value = table.getFilter_value();
                                values = JSONArray.parseArray(value);
                            }
                            obj.put("value",values);
                        }else{
                            obj.put("value","");
                        }
                        cols.add(obj);
                        break;
                    }else if (function_code.equals("F0005") && col_name.equals("brand_name")){
                        JSONObject obj = new JSONObject();
                        obj.put("col_name",col_name);
                        obj.put("show_name",show_name);
                        obj.put("type",type);
                        obj.put("value","");
                        cols.add(obj);
                        break;
                    }else if (function_code.equals("F0012") && col_name.equals("store_name")){
                        JSONObject obj = new JSONObject();
                        obj.put("col_name",col_name);
                        obj.put("show_name",show_name);
                        obj.put("type",type);
                        obj.put("value","");
                        cols.add(obj);
                        break;
                    }else if (function_code.equals("F0005") && col_name.equals("user_back")){
                        JSONObject obj = new JSONObject();
                        obj.put("col_name",col_name);
                        obj.put("show_name",show_name);
                        obj.put("type",type);
                        String value1 = table.getFilter_value();
                        obj.put("value", JSONArray.parseArray(value1));
                        cols.add(obj);
                        break;
                    }else if ( (function_code.equals("F0064") || function_code.equals("F0065")) &&
                            (col_name.equals("start_time") || col_name.equals("end_time"))){
                        JSONObject obj = new JSONObject();
                        obj.put("col_name",col_name);
                        obj.put("show_name",show_name);
                        obj.put("type",type);
                        obj.put("value","");
                        cols.add(obj);
                        break;
                    }
                }

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
    /**
     * 导入获取Corp_code(公共方法)
     */
    @RequestMapping(value = "/list/input_code", method = RequestMethod.GET)
    @ResponseBody
    public String inputCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String result="";
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            if(role_code.equals(Common.ROLE_SYS)){
                result="";
            }else{
                result="企业编号："+corp_code;
            }
            JSONObject filter = new JSONObject();
            filter.put("corp_code",result);
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
    /**
     * 查出可导出的列(公共方法)
     */
    @RequestMapping(value = "/list/getCols", method = RequestMethod.POST)
    @ResponseBody
    public String selAllByCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String function_code = jsonObject.get("function_code").toString();

            //会员分组的导出列表与会员档案的导出列表保持一致
            if(function_code.equals("F0040")){
                function_code="F0010";
            }
            List<TableManager> tableManagers = tableManagerService.selAllByCode(function_code);
            if (function_code.equals("F0010")) {
                if (role_code.equals(Common.ROLE_SYS))
                    corp_code = "C10000";
                List<VipParam> vipParams = vipParamService.selectParamByCorp(corp_code);
                List<VipParam> list = new ArrayList<VipParam>();
                for (int i = 0; i < vipParams.size(); i++) {
                    VipParam vipParam = vipParams.get(i);
                    String type = vipParams.get(i).getParam_type();
                    if (!type.equals("rule"))
                        list.add(vipParam);
                }
                for (int i = 0; i < list.size(); i++) {
                    TableManager manager = new TableManager();
                    manager.setColumn_name(list.get(i).getParam_name());
                    manager.setShow_name(list.get(i).getParam_desc());
                    manager.setFunction_code("F0010");
                    tableManagers.add(manager);
                }
            }
            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
            result.put("tableManagers", JSON.toJSONString(tableManagers));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 获取所有的省,市,区
     */
    @RequestMapping(value = "/location/getProvince", method = RequestMethod.POST)
    @ResponseBody
    public String getProvince(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            List<Location> locations;
            if (jsonObject.containsKey("higher_level_code") && !jsonObject.get("higher_level_code").equals("")){
                String higher_level_code = jsonObject.get("higher_level_code").toString();
                locations = locationService.selectByHigherLevelCode(higher_level_code);
            }else {
                locations = locationService.selectAllProvince();
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(JSON.toJSONString(locations));
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 查出下载文件
     */
    @RequestMapping(value = "/excel/getOutputExcel", method = RequestMethod.GET)
    @ResponseBody
    public String getOutputExcel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String path = request.getSession().getServletContext().getRealPath("lupload");
        String path_api = request.getSession().getServletContext().getRealPath("api");

        OssUtils ossUtils = new OssUtils();
        try {
            ArrayList<String> aa = OutExeclHelper.findFiles(path,path_api,corp_code+user_code);

            ArrayList<String> bb = ossUtils.listObjects("/"+corp_code+"/"+user_code);

            aa.addAll(bb);

            Collections.sort(aa, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    String[] o1s = o1.split("_");
                    String[] o2s = o2.split("_");
                    long diff = Long.parseLong(o1s[o1s.length-1].replace(".xls","").replace(".zip","")) - Long.parseLong(o2s[o2s.length-1].replace(".xls","").replace(".zip",""));
                    if (diff > 0)
                        return 1;
                    else if (diff == 0)
                        return 0;
                    else
                        return -1;
                }
            });

            ArrayList<String> list1 = new ArrayList<String>();

            String replace = "http://products-image.oss-cn-hangzhou.aliyuncs.com/exportExcel/"+corp_code+"/"+user_code+"/";
            JSONArray array = new JSONArray();
            for (int i = 0; i < aa.size(); i++) {
                String url = aa.get(i);

                String[] urls = url.split("_");
                JSONObject object = new JSONObject();
                object.put("url",url);
                if (url.startsWith("http://products-image.oss-cn-hangzhou.aliyuncs.com")){
                    object.put("file_type","oss");
                }else {
                    object.put("file_type","");
                }
                object.put("file_name",url.replace(replace,""));
                object.put("time",urls[urls.length-1].replace(".zip","").replace(".xls",""));

                array.add(object);
                list1.add(aa.get(i));
            }


            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(array));
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/user/getCmCorpBySession", method = RequestMethod.POST)
    @ResponseBody
    public String getCmBySession(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = String.valueOf(jsonObject.get("corp_code"));
            request.getSession().setAttribute("corp_code_cm",corp_code);
            request.getSession().setAttribute("corp_code",corp_code);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("成功");
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage("失败");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

}