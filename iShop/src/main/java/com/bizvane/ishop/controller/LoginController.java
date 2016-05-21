package com.bizvane.ishop.controller;

import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.service.UserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    @Autowired
    UserService userService;
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    String id;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String index() {
        return "login";
    }
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register() {
        return "register";
    }

    @RequestMapping(value = "/userlogin",method = RequestMethod.POST)
    @ResponseBody
    public String Login(HttpServletRequest request) {
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
            log.info("phone:"+phone+" password:"+password);
            UserInfo login_user = userService.login(phone,password);

            if (login_user == null) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("fail");
            }else {
                int user_id = login_user.getId();
                JSONObject user_info = new JSONObject();
                user_info.put("user_id",user_id);
                if (login_user.getRole_code().equals("R00001")) {
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    user_info.put("user_type","admin");
                    dataBean.setMessage(user_info.toString());
                } else {
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    user_info.put("user_type","am");
                    dataBean.setMessage(user_info.toString());
                }
            }
            } catch (Exception ex) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(ex.getMessage());
            }
            return dataBean.getJsonStr();
        }

}