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
            System.out.println(phone +"****"+password);

            log.info("phone:"+phone+" password:"+password);
            UserInfo loginuser = userService.login(phone,password);
            if (loginuser == null) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("fail");
            }
            if(loginuser.getRole_code().equals("R00001")) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("admin");
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("am");
            }
            } catch (Exception ex) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(ex.getMessage());
            }
            return dataBean.getJsonStr();
        }

}