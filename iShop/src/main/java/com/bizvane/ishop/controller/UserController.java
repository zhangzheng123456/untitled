package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Role;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.RoleService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.UserService;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
    private RoleService roleService;
    @Autowired
    private StoreService storeService;

    String id;
    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);


    /**
     * 用户管理
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String userManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession().getAttribute("user_id").toString());
            String role_code = request.getSession().getAttribute("role_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));

            JSONArray actions = functionService.selectActionByFun(user_id, role_code, function_code);
            JSONObject result = new JSONObject();
            PageInfo<User> list;
            if (role_code.contains(Common.ROLE_SYS_HEAD)) {
                //系统管理员
                list = userService.selectBySearch(page_number, page_size, "", "");
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
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
     *普通用户新增
     * 获取公司编号
     */
    @RequestMapping(value = "/add_code", method = RequestMethod.POST)
    @ResponseBody
    public String addCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        System.out.println("add-corp_code"+corp_code);
        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
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
            user.setRole_code(jsonObject.get("role_code").toString());
            user.setStore_code(jsonObject.get("store_code").toString());
            user.setPassword(user_code);
            Date now = new Date();
            user.setCreated_date(sdf.format(now));
            user.setCreater(user_id);
            user.setModified_date(sdf.format(now));
            user.setModifier(user_id);
            user.setIsactive(jsonObject.get("isactive").toString());
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
            user.setRole_code(jsonObject.get("role_code").toString());
            user.setStore_code(jsonObject.get("store_code").toString());
            Date now = new Date();
            user.setModified_date(sdf.format(now));
            user.setModifier(user_id);
            user.setIsactive(jsonObject.get("isactive").toString());
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
            System.out.println("json--user select-------------" + jsString);

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
            if (role_code.contains(Common.ROLE_SYS_HEAD)) {
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
     * 查找该企业，该用户可选择的所有角色
     */
    @RequestMapping(value = "/role", method = RequestMethod.POST)
    @ResponseBody
    public String userRole(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            JSONObject roles = new JSONObject();
            System.out.println(jsonObject.get("role_code").toString());
            if(jsonObject.get("role_code").toString().contains(Common.ROLE_SYS_HEAD)){
                String role = "[{\"role_name\":\"系统管理员\"}]";
                roles.put("roles",role);
            }else {
                String corp_code = jsonObject.get("corp_code").toString();

                String role_code = request.getSession().getAttribute("role_code").toString();
                List<Role> list;
                if (role_code.contains(Common.ROLE_SYS_HEAD)){
                    list = roleService.selectCorpRole(corp_code,"");
                }else{
                    list = roleService.selectCorpRole(corp_code,role_code);
                }
                roles.put("roles",JSON.toJSONString(list));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(roles.toString());
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
            if(!jsonObject.get("role_code").toString().contains(Common.ROLE_SYS_HEAD)){
                String corp_code = jsonObject.get("corp_code").toString();
                String role_code = request.getSession().getAttribute("role_code").toString();
                List<Store> list;
                if (role_code.contains(Common.ROLE_SYS_HEAD)){
                    list = storeService.getCorpStore(corp_code);
                    stores.put("stores",JSON.toJSONString(list));
                }else{
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    System.out.println(store_code);
                    String[] ids = store_code.split(",");
                    Store store;
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < ids.length; i++) {
                        logger.info("-------------store_code" + ids[i]);
                        store = storeService.getUserStore(corp_code, ids[i]);
                        array.add(store);
                    }
                    stores.put("stores",JSON.toJSONString(array));
                }
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(stores.toString());
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
