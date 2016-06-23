package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.github.pagehelper.PageInfo;
import org.json.HTTP;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.lang.System;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by maoweidong on 2016/2/15.
 */

/*
*用户及权限
*/
@Controller
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger((UserController.class));

    @Autowired
    private UserService userService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private GroupService groupService;

    String id;


    /**
     * 用户管理
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String userManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);

            JSONObject result = new JSONObject();
            PageInfo<User> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = userService.selectBySearch(page_number, page_size, "", "");
            } else {
                list = userService.selectBySearch(page_number, page_size, corp_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 普通用户新增
     * 获取公司编号
     */
    @RequestMapping(value = "/add_code", method = RequestMethod.POST)
    @ResponseBody
    public String addCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        System.out.println("add-corp_code" + corp_code);
        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
        dataBean.setId(id);
        dataBean.setMessage(corp_code);
        return dataBean.getJsonStr();
    }

    /**
     * 用户管理
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            User user = new User();
            String user_code = jsonObject.get("user_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            user.setUser_code(user_code);
            user.setUser_name(jsonObject.get("username").toString());
            user.setAvatar(jsonObject.get("avater").toString());
            user.setPhone(jsonObject.get("phone").toString());
            user.setEmail(jsonObject.get("email").toString());
            user.setSex(jsonObject.get("sex").toString());
            //     user.setBirthday(jsonObject.get("birthday").toString());
            user.setCorp_code(corp_code);
            user.setGroup_code(jsonObject.get("group_code").toString());
            String store_code = jsonObject.get("store_code").toString();
            if (!store_code.endsWith(",")){
                store_code = store_code + ",";
            }
            user.setStore_code(store_code);
            user.setPassword(user_code);
            Date now = new Date();
            user.setLogin_time_recently("");
            user.setCreated_date(Common.DATETIME_FORMAT.format(now));
            user.setCreater(user_id);
            user.setModified_date(Common.DATETIME_FORMAT.format(now));
            user.setModifier(user_id);
            user.setIsactive(jsonObject.get("isactive").toString());
            user.setCan_login(jsonObject.get("can_login").toString());

            logger.info("------insert user" + user.toString());

            String exist = userService.userCodeExist(user_code, corp_code);
            if (exist.equals(Common.DATABEAN_CODE_ERROR)) {
                logger.info("------insert user-----用户编号已存在！--");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("用户编号已存在！");
            } else {
                userService.insert(user);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户管理
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            User user = new User();
            user.setId(Integer.parseInt(jsonObject.get("id").toString()));
            user.setUser_code(jsonObject.get("user_code").toString());
            user.setUser_name(jsonObject.get("username").toString());
            user.setPassword(jsonObject.get("password").toString());
            user.setAvatar(jsonObject.get("avater").toString());
            user.setPhone(jsonObject.get("phone").toString());
            user.setEmail(jsonObject.get("email").toString());
            user.setSex(jsonObject.get("sex").toString());
            //       user.setBirthday(jsonObject.get("birthday").toString());
            user.setCorp_code(jsonObject.get("corp_code").toString());
            user.setGroup_code(jsonObject.get("group_code").toString());
            String store_code = jsonObject.get("store_code").toString();
            if (!store_code.endsWith(",")){
                store_code = store_code + ",";
            }
            user.setStore_code(store_code);
            Date now = new Date();
            user.setModified_date(Common.DATETIME_FORMAT.format(now));
            user.setModifier(user_id);
            user.setIsactive(jsonObject.get("isactive").toString());
            user.setCan_login(jsonObject.get("can_login").toString());
            logger.info("------update user" + user.toString());
            userService.update(user);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        logger.info("info--------" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_id = jsonObject.get("id").toString();
            String[] ids = user_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete user--" + Integer.valueOf(ids[i]));
                userService.delete(Integer.valueOf(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 用户管理
     * 选择用户
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user select-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_id = jsonObject.get("id").toString();

            data = JSON.toJSONString(userService.getUserById(Integer.parseInt(user_id)));
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(data);
        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage(e.getMessage());
        }
        logger.info("info-----" + bean.getJsonStr());
        return bean.getJsonStr();
    }

    /**
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<User> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = userService.selectBySearch(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                list = userService.selectBySearch(page_number, page_size, corp_code, search_value);
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


    /**
     * 根据登录用户的角色类型
     * 输入的企业编号
     * 获得该用户可选择的所有群组
     */
    @RequestMapping(value = "/role", method = RequestMethod.POST)
    @ResponseBody
    public String userGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject groups = new JSONObject();
            List<Group> group;
            if (role_code.equals(Common.ROLE_SYS)) {
//                if (corp_code.equals("")) {
//                    //列出系统管理员，role_code=r1000
//                    group = groupService.selectByRole(Common.ROLE_SYS);
//                    System.out.println("-------");
//                } else {
                //列出企业下所有,corp_code=
                group = groupService.selectUserGroup(corp_code, "");
                //               }
            } else {
                //比登陆用户角色级别低的群组
                String login_corp_code = request.getSession().getAttribute("corp_code").toString();
                group = groupService.selectUserGroup(login_corp_code, role_code);
            }
            groups.put("group", JSON.toJSONString(group));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(groups.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 根据登录用户的角色类型
     * 输入的企业编号
     * 查找该企业，该用户可选择的所有店铺
     */
    @RequestMapping(value = "/store", method = RequestMethod.POST)
    @ResponseBody
    public String userStore(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user store-------------" + jsString);
            System.out.println("json--user store-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            JSONObject stores = new JSONObject();
            String corp_code = jsonObject.get("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
//            if (corp_code.equals("")) {
//                //新增编辑系统管理员，corp_code为空
//                stores.put("stores", "");
//            } else {
            if (role_code.equals(Common.ROLE_SYS)) {
                //登录用户为admin
                List<Store> list;
                list = storeService.getCorpStore(corp_code);
                stores.put("stores", JSON.toJSONString(list));
            } else {
                //登录用户为普通用户
                String store_code = request.getSession().getAttribute("store_code").toString();
                String corp_code1 = request.getSession().getAttribute("corp_code").toString();
                System.out.println(store_code);
                String[] ids = store_code.split(",");
                Store store;
                JSONArray array = new JSONArray();
                for (int i = 0; i < ids.length; i++) {
                    logger.info("-------------store_code" + ids[i]);
                    store = storeService.getStoreByCode(corp_code1, ids[i],Common.IS_ACTIVE_Y);
                    array.add(store);
                }
                stores.put("stores", JSON.toJSONString(array));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(stores.toString());
//            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 根据用户的ID输出用户的企业
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getCorpByUser", method = RequestMethod.POST)
    @ResponseBody
    public String getCorpByUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject corps = new JSONObject();
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONArray array = new JSONArray();
            if (role_code.equals((Common.ROLE_SYS))) {
                List<Corp> list = corpService.selectAllCorp();
                for (int i = 0; i < list.size(); i++) {
                    Corp corp = list.get(i);
                    String c_code = corp.getCorp_code();
                    String corp_name = corp.getCorp_name();
                    JSONObject obj = new JSONObject();
                    obj.put("corp_code", c_code);
                    obj.put("corp_name", corp_name);
                    array.add(obj);
                }
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                Corp corp = corpService.selectByCorpId(0, corp_code);
                String c_code = corp.getCorp_code();
                String corp_name = corp.getCorp_name();
                JSONObject obj = new JSONObject();
                obj.put("corp_code", c_code);
                obj.put("corp_name", corp_name);
                array.add(obj);
            }
            corps.put("corps", array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(corps.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户管理
     * 查看权限
     */
    @RequestMapping(value = "/check_power", method = RequestMethod.POST)
    @ResponseBody
    public String groupCheckPower(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String login_user_code = request.getSession().getAttribute("user_code").toString();
            String login_corp_code = request.getSession().getAttribute("corp_code").toString();
            String login_role_code = request.getSession().getAttribute("role_code").toString();
            String login_group_code = request.getSession().getAttribute("group_code").toString();

            String search_value = "";
            if (jsonObject.has("searchValue")){
                search_value = jsonObject.get("searchValue").toString();
            }
            //获取登录用户的所有权限
            List<Function> funcs = functionService.selectAllPrivilege(login_role_code, login_corp_code + login_user_code, login_corp_code + login_group_code,search_value);

            String group_code = jsonObject.get("group_code").toString();
            String user_id = jsonObject.get("user_id").toString();
            String corp_code = userService.getUserById(Integer.parseInt(user_id)).getCorp_code();
            String role_code = groupService.selectByCode(corp_code ,group_code,Common.IS_ACTIVE_Y).getRole_code();
            String user_code = userService.getUserById(Integer.parseInt(user_id)).getUser_code();

            //获取群组自定义的权限
            JSONArray group_privilege = functionService.selectRAGPrivilege(role_code, corp_code + group_code);

            //获取用户自定义的权限
            JSONArray user_privilege = functionService.selectUserPrivilege(corp_code + user_code);

            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(funcs));
            result.put("die", group_privilege);
            result.put("live", user_privilege);

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

    @RequestMapping(value = "/UserCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String UserCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String user_code = jsonObject.get("user_code").toString();

            String corp_code = jsonObject.get("corp_code").toString();
            String current_corp_code = request.getSession(false).getAttribute("corp_code").toString();
            corp_code = (corp_code == null || corp_code.isEmpty()) ? current_corp_code : corp_code;
            String existInfo = userService.userCodeExist(user_code, corp_code);
            if (existInfo.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("用户编号已被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("用户编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/PhoneExist", method = RequestMethod.POST)
    @ResponseBody
    public String PhoneExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String phone = jsonObject.get("phone").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String existInfo = userService.userPhoneExist(phone);
            if (existInfo.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("手机号码已被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("手机号码未被使用！！！");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/EamilExist", method = RequestMethod.POST)
    @ResponseBody
    public String EamilExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String email = jsonObject.get("email").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String existInfo = userService.userEmailExist(email);
            if (existInfo.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("email已被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("email未被使用！！！");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/creatQrcode", method = RequestMethod.POST)
    @ResponseBody
    public String creatQrcode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String user_code = jsonObject.get("user_code").toString();
            String auth_appid = corpService.selectByCorpId(0,corp_code).getApp_id();

            String url = "http://wx.bizvane.com/wechat/creatQrcode?auth_appid="+auth_appid+"&guider_code="+user_code;
            String result = IshowHttpClient.get(url);
            dataBean.setId(id);
            dataBean.setMessage(result);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }
}
