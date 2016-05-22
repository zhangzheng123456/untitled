package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.service.UserService;
import com.google.gson.Gson;
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
    String id;
    /**
     * 用户管理
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public String userManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
      //      String corp_code = request.getSession().getAttribute("corp_code").toString();
            List<UserInfo> userInfo = userService.selectAll();
            Gson gson = new Gson();
            String result = gson.toJson(userInfo);
            JSONArray array = JSONArray.parseArray(result);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(array.get(0).toString());
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
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            UserInfo user = new UserInfo();
            user.setUser_code(jsonObject.get("comment").toString());
            user.setUser_name(jsonObject.get("username").toString());
            user.setAvatar(jsonObject.get("username").toString());
            user.setPhone(jsonObject.get("password").toString());
            user.setEmail(jsonObject.get("password").toString());
            user.setSex(jsonObject.get("password").toString());
            user.setCorp_code(jsonObject.get("password").toString());
            user.setRole_code(jsonObject.get("comment").toString());
            userService.insert(user);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("add success");
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
        try{
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            UserInfo user = new UserInfo();
            user.setUser_code(jsonObject.get("id").toString());
            user.setUser_name(jsonObject.get("username").toString());
            user.setPassword(jsonObject.get("password").toString());
            user.setCorp_code(jsonObject.get("is_admin").toString());
            user.setRole_code(jsonObject.get("comment").toString());
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
     * 选择单个用户
     */
    @RequestMapping("/find/{id}")
    @ResponseBody
    public String findById(@PathVariable Integer id) {
        DataBean bean=new DataBean();
        String data = null;
        try {
            data = JSON.toJSONString(userService.getUserById(id));
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
}
