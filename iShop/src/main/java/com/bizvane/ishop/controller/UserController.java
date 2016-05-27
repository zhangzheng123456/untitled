package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.ShopInfo;
import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

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

    String id;
    /**
     * 用户管理
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public String userManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession().getAttribute("user_id").toString());
            String role_code = request.getSession().getAttribute("role_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));

            JSONArray actions = functionService.selectActionByFun(user_id,role_code,function_code);
            JSONObject result = new JSONObject();
            List<UserInfo> list;
            if(role_code.contains(Common.ROLE_SYS_HEAD)) {
                //系统管理员
                PageHelper.startPage(page_number, page_size);
                list = userService.selectBySearch("","");
            }else{
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                PageHelper.startPage(page_number, page_size);
                list = userService.selectBySearch(corp_code, "");
            }
            PageInfo<UserInfo> page = new PageInfo<UserInfo>(list);
            result.put("user",list);
            result.put("actions",actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户管理
     * 新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            UserInfo user = new UserInfo();
            String user_code = jsonObject.get("user_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            user.setUser_code(user_code);
            user.setUser_name(jsonObject.get("username").toString());
            user.setAvatar(jsonObject.get("avater").toString());
            user.setPhone(jsonObject.get("phone").toString());
            user.setEmail(jsonObject.get("email").toString());
            user.setSex(jsonObject.get("sex").toString());
            user.setBirthday(jsonObject.get("birthday").toString());

            user.setCorp_code(corp_code);
            user.setRole_code(jsonObject.get("role_code").toString());
            user.setStore_code(jsonObject.get("store_code").toString());
            user.setPassword(jsonObject.get("password").toString());

            Date now = new Date();
            user.setCreated_date(now);
            user.setCreater(user_id);
            user.setModified_date(now);
            user.setModifier(user_id);
            user.setIsactive(jsonObject.get("isactive").toString());
            String exist = userService.userCodeExist(user_code,corp_code);
            if (exist.equals(Common.DATABEAN_CODE_ERROR)){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("用户编号已存在！");
            }else {
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
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try{
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            UserInfo user = new UserInfo();
            user.setUser_code(jsonObject.get("user_code").toString());
            user.setUser_name(jsonObject.get("username").toString());
            user.setPassword(jsonObject.get("password").toString());
            user.setAvatar(jsonObject.get("avater").toString());
            user.setPhone(jsonObject.get("phone").toString());
            user.setEmail(jsonObject.get("email").toString());
            user.setSex(jsonObject.get("sex").toString());
            user.setBirthday(jsonObject.get("birthday").toString());
            user.setCorp_code(jsonObject.get("corp_code").toString());
            user.setRole_code(jsonObject.get("role_code").toString());
            user.setStore_code(jsonObject.get("store_code").toString());
            Date now = new Date();
            user.setModified_date(now);
            user.setModifier(user_id);
            user.setIsactive(jsonObject.get("isactive").toString());
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
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_id = jsonObject.get("id").toString();
            String type = jsonObject.get("type").toString();
            String[] ids = user_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                UserInfo user = new UserInfo(Integer.valueOf(ids[i]));
                logger.info("inter---------------" + Integer.valueOf(ids[i]));
                userService.delete(Integer.valueOf(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            //	return "Error deleting the user:" + ex.toString();
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
    @RequestMapping("/find/{id}")
    @ResponseBody
    public String findById(@PathVariable Integer user_id) {
        DataBean bean=new DataBean();
        String data = null;
        try {
            data = JSON.toJSONString(userService.getUserById(user_id));
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
        DataBean dataBean=new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            List<UserInfo> list;
            if(role_code.contains(Common.ROLE_SYS_HEAD)) {
                //系统管理员
                PageHelper.startPage(page_number, page_size);
                list = userService.selectBySearch("",search_value);
            }else{
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                PageHelper.startPage(page_number, page_size);
                list = userService.selectBySearch(corp_code, search_value);
            }
            PageInfo<UserInfo> page = new PageInfo<UserInfo>(list);
            result.put("users",list);
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
