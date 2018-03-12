package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.System;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
@Controller
@RequestMapping("/userAchvGoal")
public class UserAchvGoalController {
    private static final Logger logger = Logger.getLogger(LoginController.class);

    @Autowired
    private UserAchvGoalService userAchvGoalService = null;
    @Autowired
    private CorpService corpService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;
    @Autowired
    private BaseService baseService;
    String id;

    /**
     * 用户管理
     * 用户业绩目标的列表展示
     *
     * @param request
     * @return
     */
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String userAchvGoalManage(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String user_code = request.getSession().getAttribute("user_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//
//            org.json.JSONObject result = new org.json.JSONObject();
//            int page_number = Integer.parseInt(request.getParameter("pageNumber").toString());
//            int page_size = Integer.parseInt(request.getParameter("pageSize").toString());
//            PageInfo<UserAchvGoal> pages = null;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                pages = userAchvGoalService.selectBySearch(page_number, page_size, "", "");
//            } else if (role_code.equals(Common.ROLE_GM)) {
//                pages = this.userAchvGoalService.selectBySearch(page_number, page_size, corp_code, "");
//            } else if (role_code.equals(Common.ROLE_BM)) {
//                String brand_code = request.getSession().getAttribute("brand_code").toString();
//                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
//                String store_code = "";
//                for (int i = 0; i < stores.size(); i++) {
//                    store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
//                }
//                pages = this.userAchvGoalService.selectBySearchPart(page_number, page_size, corp_code, "", store_code, "", "", role_code);
//            } else if (role_code.equals(Common.ROLE_AM)) {
//                String area_code = request.getSession(false).getAttribute("area_code").toString();
//                String store_code = request.getSession(false).getAttribute("store_code").toString();
//                pages = this.userAchvGoalService.selectBySearchPart(page_number, page_size, corp_code, "", "", area_code, store_code, role_code);
//            } else if (role_code.equals(Common.ROLE_SM)) {
//                String store_code = request.getSession(false).getAttribute("store_code").toString();
//                pages = this.userAchvGoalService.selectBySearchPart(page_number, page_size, corp_code, "", store_code, "", "", role_code);
//            } else {
//                List<UserAchvGoal> goal = userAchvGoalService.userAchvGoalExist(corp_code, user_code);
//                pages = new PageInfo<UserAchvGoal>();
//                pages.setList(goal);
//            }
//            result.put("list", JSON.toJSONString(pages));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//            logger.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }


    /**
     * 用户业绩目标
     * 编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editUserAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObject = JSONObject.parseObject(jsString);
            id = jsonObject.get("id").toString();
            String message = jsonObject.get("message").toString();
            JSONObject jsonObj = JSONObject.parseObject(message);

            UserAchvGoal userAchvGoal = new UserAchvGoal();
            userAchvGoal.setId(Integer.parseInt(jsonObj.get("id").toString()));
            userAchvGoal.setCorp_code(jsonObj.get("corp_code").toString());
            userAchvGoal.setStore_code(jsonObj.get("store_code").toString());
            userAchvGoal.setUser_code(jsonObj.get("user_code").toString());
            userAchvGoal.setUser_target(jsonObj.get("achv_goal").toString());
            String achv_type = jsonObj.get("achv_type").toString();
            userAchvGoal.setTarget_type(achv_type);

            if (achv_type.equalsIgnoreCase(Common.TIME_TYPE_WEEK)) {
                String time = jsonObj.get("end_time").toString();
                String week = TimeUtils.getWeek(time);
                userAchvGoal.setTarget_time(week);
            } else {
                userAchvGoal.setTarget_time(jsonObj.get("end_time").toString());
            }
            Date now = new Date();
            userAchvGoal.setModifier(user_id);
            userAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));
            userAchvGoal.setIsactive(jsonObj.get("isactive").toString());
            String result = userAchvGoalService.updateUserAchvGoal(userAchvGoal);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("edit success");

                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "业绩管理_员工业绩目标";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("user_code").toString();
                List<User> users = userService.userCodeExist(t_code, t_corp_code, Common.IS_ACTIVE_Y);
                if(users.size()>0){
                    String t_name = users.get(0).getUser_name();
                    String remark = action_json.get("end_time").toString()+"("+action_json.get("achv_type").toString()+")";
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);

                }
               //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("用户" + userAchvGoal.getUser_code() + "业绩目标已经设定");
            }
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户业绩目标
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String deleteUserAchvGoalById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String user_id = jsonObject.get("id").toString();
            String[] ids = user_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                UserAchvGoal userAchvGoalById = userAchvGoalService.getUserAchvGoalById(Integer.parseInt(ids[i]));
                userAchvGoalService.deleteUserAchvGoalById(ids[i]);
                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "业绩管理_员工业绩目标";
                String action = Common.ACTION_DEL;
                String t_corp_code = userAchvGoalById.getCorp_code();
                String t_code = userAchvGoalById.getUser_code();
                List<User> users = userService.userCodeExist(t_code, t_corp_code, Common.IS_ACTIVE_Y);
                String t_name = users.get(0).getUser_name();
                String remark = userAchvGoalById.getTarget_time()+"("+userAchvGoalById.getTarget_type()+")";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户业绩目标
     * 添加
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String insertUserAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user add-------------" + jsString);
            System.out.println("json---------------" + jsString);
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            UserAchvGoal userAchvGoal = new UserAchvGoal();

            String corp_code=jsonObject.get("corp_code").toString();
            String user_code=jsonObject.get("user_code").toString();
            String achv_type=jsonObject.get("achv_type").toString();
            String achv_goal=jsonObject.get("achv_goal").toString();
            String store_code=jsonObject.get("store_code").toString();
            String user_name=jsonObject.get("user_name").toString();
            String store_name=jsonObject.get("store_name").toString();
            String end_time=jsonObject.get("end_time").toString();
            String isactive=jsonObject.get("isactive").toString();

            userAchvGoal.setTarget_time(end_time);
//            if (achv_type.equalsIgnoreCase(Common.TIME_TYPE_WEEK)) {
//                String time = jsonObject.get("end_time").toString();
//                String week = TimeUtils.getWeek(time);
//                userAchvGoal.setTarget_time(week);
//            } else {
//                userAchvGoal.setTarget_time(jsonObject.get("end_time").toString());
//            }
            Date now = new Date();
            userAchvGoal.setCorp_code(corp_code);
            userAchvGoal.setStore_code(store_code);
            userAchvGoal.setUser_code(user_code);
            userAchvGoal.setUser_target(achv_goal);
            userAchvGoal.setStore_name(store_name);
            userAchvGoal.setUser_name(user_name);
            userAchvGoal.setTarget_type(achv_type);
            userAchvGoal.setModifier(user_id);
            userAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));
            userAchvGoal.setCreater(user_id);
            userAchvGoal.setCreated_date(Common.DATETIME_FORMAT.format(now));
            userAchvGoal.setIsactive(isactive);
            String result="";

    UserAchvGoal userAchvGoal2=userAchvGoalService.getUserAchvForId(corp_code, user_code,store_code, end_time,Common.IS_ACTIVE_Y);

    if(userAchvGoal2!=null){
        logger.info("=========================="+userAchvGoal2.getId());
        userAchvGoal.setId(userAchvGoal2.getId());
        userAchvGoal.setCorp_code(corp_code);
        userAchvGoal.setStore_code(store_code);
        userAchvGoal.setUser_code(user_code);
        userAchvGoal.setUser_target(achv_goal);
        userAchvGoal.setTarget_time(end_time);
        userAchvGoal.setTarget_type(achv_type);
        userAchvGoal.setStore_name(store_name);
        userAchvGoal.setUser_name(user_name);
        userAchvGoal.setModifier(user_id);
        userAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));
        userAchvGoal.setIsactive(isactive);
        String  resultInfo = userAchvGoalService.updateUserAchvGoal(userAchvGoal);
        if(resultInfo.equals(Common.DATABEAN_CODE_SUCCESS)){
            result=Common.DATABEAN_CODE_SUCCESS;
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(String.valueOf(userAchvGoal.getId()));
        }
    }else{
        userAchvGoal.setCorp_code(corp_code);
        userAchvGoal.setStore_code(store_code);
        userAchvGoal.setUser_code(user_code);
        userAchvGoal.setUser_target(achv_goal);
        userAchvGoal.setStore_name(store_name);
        userAchvGoal.setUser_name(user_name);
        userAchvGoal.setTarget_time(end_time);
        userAchvGoal.setTarget_type(achv_type);
        userAchvGoal.setModifier(user_id);
        userAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));
        userAchvGoal.setCreater(user_id);
        userAchvGoal.setCreated_date(Common.DATETIME_FORMAT.format(now));
        userAchvGoal.setIsactive(jsonObject.get("isactive").toString());
        String resultInfo = userAchvGoalService.insert(userAchvGoal);
        if(resultInfo.equals(Common.DATABEAN_CODE_SUCCESS)){
            result=Common.DATABEAN_CODE_SUCCESS;

        dataBean.setId(id);
        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
        UserAchvGoal userAchvGoal1 = userAchvGoalService.getUserAchvForId(userAchvGoal.getCorp_code(), userAchvGoal.getUser_code(),userAchvGoal.getStore_code(), userAchvGoal.getTarget_time(),Common.IS_ACTIVE_Y);
        if (userAchvGoal1!=null) {
            dataBean.setMessage(String.valueOf(userAchvGoal1.getId()));
        }else{
            dataBean.setMessage("error");
        }
        }
    }

            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {

                //----------------行为日志------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "业绩管理_员工业绩目标";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("user_code").toString();
                List<User> users = userService.userCodeExist(t_code, t_corp_code, Common.IS_ACTIVE_Y);
                String t_name="";
                if (users.size() > 0) {
                     t_name = users.get(0).getUser_name();
                    String remark = action_json.get("end_time").toString()+"("+action_json.get("achv_type").toString()+")";
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
               }

                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(Common.DATABEAN_CODE_ERROR);
            }
        } catch (Exception ex) {
            dataBean.setCode("-1");
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户业绩目标管理
     * 选择用户业绩目标
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        try {

            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String userAchvGoalId = jsonObject.get("id").toString();
            data = JSON.toJSONString(userAchvGoalService.getUserAchvGoalById(Integer.parseInt(userAchvGoalId)));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(data);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户业绩管理
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        String id = "";
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String user_code = request.getSession(false).getAttribute("user_code").toString();

            JSONObject result = new JSONObject();
            PageInfo<UserAchvGoal> list = new PageInfo<UserAchvGoal>();
            if (role_code.contains(Common.ROLE_SYS)) {
                //系统管理员
                list = userAchvGoalService.selectBySearch(page_number, page_size, "", search_value);
            } else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                String corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = userAchvGoalService.selectBySearch(page_number, page_size, corp_code, search_value);

                // list = userAchvGoalService.selectBySearch(page_number, page_size, "", search_value,manager_corp);
            }else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equalsIgnoreCase(Common.ROLE_GM)) {
                    list = userAchvGoalService.selectBySearch(page_number, page_size, corp_code, search_value);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                    String store_code = "";
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                    }
                    list = this.userAchvGoalService.selectBySearchPart(page_number, page_size, corp_code, search_value, store_code, "", "", role_code);
                } else if (role_code.equalsIgnoreCase(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    list = this.userAchvGoalService.selectBySearchPart(page_number, page_size, corp_code, search_value, "", area_code, "", role_code);
                } else if (role_code.equalsIgnoreCase(Common.ROLE_SM)) {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = this.userAchvGoalService.selectBySearchPart(page_number, page_size, corp_code, search_value, store_code, "", "", role_code);
                }else {
                    List<UserAchvGoal> goal = userAchvGoalService.userAchvGoalExist(corp_code, user_code);
                    list.setList(goal);
                }
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<UserAchvGoal> pages = null;
            if (screen.equalsIgnoreCase("")) {
                if (role_code.equalsIgnoreCase(Common.ROLE_SYS)) {
                    pages = userAchvGoalService.selectBySearch(1, Common.EXPORTEXECLCOUNT, "", search_value);
                } else if(role_code.equals(Common.ROLE_CM)){
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>"+manager_corp);
                     corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>"+corp_code);
                    pages = this.userAchvGoalService.selectBySearch(1, Common.EXPORTEXECLCOUNT, corp_code, search_value);

                    // list = userAchvGoalService.selectBySearch(page_number, page_size, "", search_value,manager_corp);
                }else if (role_code.equalsIgnoreCase(Common.ROLE_GM)) {
                    pages = this.userAchvGoalService.selectBySearch(1, Common.EXPORTEXECLCOUNT, corp_code, search_value);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                    String store_code = "";
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                    }
                    pages = this.userAchvGoalService.selectBySearchPart(1, Common.EXPORTEXECLCOUNT, corp_code, search_value, store_code, "", "", role_code);
                } else if (role_code.equalsIgnoreCase(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    pages = this.userAchvGoalService.selectBySearchPart(1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", area_code, store_code, Common.ROLE_AM);
                } else if (role_code.equalsIgnoreCase(Common.ROLE_SM)) {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    pages = this.userAchvGoalService.selectBySearchPart(1, Common.EXPORTEXECLCOUNT, corp_code, search_value, store_code, "", "", Common.ROLE_SM);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equalsIgnoreCase(Common.ROLE_SYS)) {
                    pages = userAchvGoalService.getAllUserAchScreen(1, Common.EXPORTEXECLCOUNT, "", "", "", "", map, "");
                } else if(role_code.equals(Common.ROLE_CM)){
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>"+manager_corp);
                     corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>"+corp_code);
                    pages = userAchvGoalService.getAllUserAchScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "");

                    //list = userAchvGoalService.getAllUserAchScreen(page_number, page_size, "", "", "", "", map, "",manager_corp);
                }else if (role_code.equalsIgnoreCase(Common.ROLE_GM)) {
                    pages = userAchvGoalService.getAllUserAchScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map, "");
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                    String store_code = "";
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                    }
                    pages = userAchvGoalService.getAllUserAchScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", store_code, role_code, map, "");
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    pages = userAchvGoalService.getAllUserAchScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_code, "", role_code, map, store_code);
                } else if (role_code.equals(Common.ROLE_SM)) {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    pages = userAchvGoalService.getAllUserAchScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", store_code, role_code, map, "");
                }
            }
            List<UserAchvGoal> userAchvGoals = pages.getList();
            if (userAchvGoals.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(userAchvGoals);
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json, userAchvGoals, map, response, request,"员工业绩目标");
            org.json.JSONObject result = new org.json.JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /***
     * Execl增加
     */
    @RequestMapping(value = "/addByExecl", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional()
    public String addByExecl(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String result = "";
        Workbook rwb = null;
        try {
            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = 7;//得到所有的列
            int rows = rs.getRows();//得到所有的行
//            int actualRows = LuploadHelper.getRightRows(rs);
//            if(actualRows != rows){
//                if(rows-actualRows==1){
//                    result = "：第"+rows+"行存在空白行,请删除";
//                }else{
//                    result = "：第"+(actualRows+1)+"行至第"+rows+"存在空白行,请删除";
//                }
//                int i = 5 / 0;
//            }
            if (rows < 4) {
                result = "：请从模板第4行开始插入正确数据";
                int i = 5 / 0;
            }
            if (rows > 9999) {
                result = "：数据量过大，导入失败";
                int i = 5 / 0;
            }
            Cell[] column3 = rs.getColumn(0);
            Pattern pattern1 = Pattern.compile("C\\d{5}");
            if (!role_code.equals(Common.ROLE_SYS)) {
                for (int i = 3; i < column3.length; i++) {
                    if (column3[i].getContents().toString().trim().equals("")) {
                        continue;
                    }
                    if (!column3[i].getContents().toString().trim().equals(corp_code)) {
                        result = "：第" + (i + 1) + "行企业编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                    Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                    if (matcher.matches() == false) {
                        result = "：第" + (i + 1) + "行企业编号格式有误";
                        int b = 5 / 0;
                        break;
                    }
                }
            }
            for (int i = 3; i < column3.length; i++) {
                if (column3[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行企业编号格式有误";
                    int b = 5 / 0;
                    break;
                }
                Corp corp = corpService.selectByCorpId(0, column3[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
                if (corp == null) {
                    result = "：第" + (i + 1) + "行企业编号不存在";
                    int b = 5 / 0;
                    break;
                }

            }


            Cell[] column2 = rs.getColumn(1);
            for (int i = 3; i < column2.length; i++) {
                if (column2[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Store store = storeService.getStoreByCode(column3[i].getContents().toString().trim(), column2[i].getContents().toString().trim(), "");
                if (store == null) {
                    result = "：第" + (i + 1) + "行店铺编号不存在";
                    int b = 5 / 0;
                    break;
                }
            }

            Cell[] column1 = rs.getColumn(2);
            for (int i = 3; i < column1.length; i++) {
                if (column1[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                List<User> user = userService.userCodeExist(column1[i].getContents().toString().trim(), column3[i].getContents().toString().trim(), Common.IS_ACTIVE_Y);
                if (user.size() == 0) {
                    result = "：第" + (i + 1) + "行的用户编号不存在";
                    int b = 5 / 0;
                    break;
                }
            }

            Cell[] column8 = rs.getColumn(3);
            Pattern pattern2 = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");
            for (int i = 3; i < column8.length; i++) {
                if (column8[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                Matcher matcher = pattern2.matcher(column8[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行业绩目标输入有误";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column = rs.getColumn(4);
            Cell[] column5 = rs.getColumn(5);
            for (int i = 3; i < column.length; i++) {
                if (column[i].getContents().toString().trim().equals("")) {
                    continue;
                }
                if (!column[i].getContents().toString().trim().equals("D") && !column[i].getContents().toString().trim().equals("W") && !column[i].getContents().toString().trim().equals("M") && !column[i].getContents().toString().trim().equals("Y")) {
                    result = "：第" + (i + 1) + "行的业绩日期类型缩写有误";
                    int b = 5 / 0;
                    break;
                }
                UserAchvGoal userAchvGoal = new UserAchvGoal();
                userAchvGoal.setCorp_code(column3[i].getContents().toString().trim());
                userAchvGoal.setUser_code(column1[i].getContents().toString().trim());
                userAchvGoal.setTarget_type(column[i].getContents().toString().trim());
                userAchvGoal.setTarget_time(column5[i].getContents().toString().trim());
                int count = userAchvGoalService.checkUserAchvGoal(userAchvGoal);
                if (count > 0) {
                    result = "：第" + (i + 1) + "行,用户" + userAchvGoal.getUser_code() + "业绩目标已经设定";
                    int b = 5 / 0;
                }
            }
            ArrayList<UserAchvGoal> userAchvGoals = new ArrayList<UserAchvGoal>();
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    UserAchvGoal userAchvGoal = new UserAchvGoal();
                    String cellCorp = rs.getCell(j++, i).getContents().toString().trim();
                    String store_code = rs.getCell(j++, i).getContents().toString().trim();
                    String user_code = rs.getCell(j++, i).getContents().toString().trim();
                    String user_target = rs.getCell(j++, i).getContents().toString().trim();
                    String target_type = rs.getCell(j++, i).getContents().toString().trim();
                    String cellTypeForDate = LuploadHelper.getCellTypeForDate(rs.getCell(j++, i), target_type);
                    String isactive = rs.getCell(j++, i).getContents().toString().trim();
                    if (cellCorp.equals("") && store_code.equals("") && user_code.equals("") && user_target.equals("") && target_type.equals("")) {
                        continue;
                    }
                    if (cellCorp.equals("") || store_code.equals("") || user_code.equals("") || user_target.equals("") || target_type.equals("")) {
                        result = "：第" + (i + 1) + "行信息不完整,请参照Execl中对应的批注";
                        int a = 5 / 0;
                    }
                    if (!role_code.equals(Common.ROLE_SYS)) {
                        userAchvGoal.setCorp_code(corp_code);
                    } else {
                        userAchvGoal.setCorp_code(cellCorp);
                    }
                    userAchvGoal.setStore_code(store_code);
                    userAchvGoal.setUser_code(user_code);
                    userAchvGoal.setUser_target(user_target);
                    userAchvGoal.setTarget_type(target_type);
                    if (target_type.equals(Common.TIME_TYPE_WEEK)) {
                        String week = "";
                        if (cellTypeForDate.equals("格式错误")) {
                            userAchvGoal.setTarget_time(week);
                        } else {
                            week = TimeUtils.getWeek(cellTypeForDate);
                            userAchvGoal.setTarget_time(week);
                        }
                    } else {
                        userAchvGoal.setTarget_time(cellTypeForDate);
                    }
                    if (isactive.toUpperCase().equals("N")) {
                        userAchvGoal.setIsactive("N");
                    } else {
                        userAchvGoal.setIsactive("Y");
                    }
                    userAchvGoal.setCreater(user_id);
                    Date now = new Date();
                    userAchvGoal.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    userAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));
                    userAchvGoal.setModifier(user_id);
                    userAchvGoals.add(userAchvGoal);
//                    result = String.valueOf(userAchvGoalService.insert(userAchvGoal));
//                    if (result.equals("用户业绩重复")){
//                        result = "：用户" + userAchvGoal.getUser_code() + "业绩目标已经设定";
//                        int b = 5 / 0;
//                    }
                }
            }
            for (int i = 0; i < userAchvGoals.size(); i++) {
                result = String.valueOf(userAchvGoalService.insert(userAchvGoals.get(i)));
                if (result.equals("用户业绩重复")) {
                    result = "：用户" + userAchvGoals.get(i).getUser_code() + "业绩目标已经设定";
                    int b = 5 / 0;
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } finally {
            if (rwb != null) {
                rwb.close();
            }
            System.gc();
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json-----------" + jsString);
            JSONObject jsonObject = JSONObject.parseObject(jsString);
            id = jsonObject.getString("id");
            String message = jsonObject.get("message").toString();
            JSONObject jsonObject1 = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject1.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject1.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject1);
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<UserAchvGoal> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = userAchvGoalService.getAllUserAchScreen(page_number, page_size, "", "", "", "", map, "");
            }  else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                String corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = userAchvGoalService.getAllUserAchScreen(page_number, page_size, corp_code, "", "", "", map, "");

                //list = userAchvGoalService.getAllUserAchScreen(page_number, page_size, "", "", "", "", map, "",manager_corp);
            }else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM)) {
                    list = userAchvGoalService.getAllUserAchScreen(page_number, page_size, corp_code, "", "", "", map, "");
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = userAchvGoalService.getAllUserAchScreen(page_number, page_size, corp_code, area_code, "", role_code, map, store_code);
                } else if (role_code.equals(Common.ROLE_SM)) {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = userAchvGoalService.getAllUserAchScreen(page_number, page_size, corp_code, "", store_code, role_code, map, "");
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                    String store_code = "";
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                    }
                    list = userAchvGoalService.getAllUserAchScreen(page_number, page_size, corp_code, "", store_code, role_code, map, "");
                }
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

}
