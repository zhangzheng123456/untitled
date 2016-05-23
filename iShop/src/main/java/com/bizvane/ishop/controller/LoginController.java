package com.bizvane.ishop.controller;

import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.CorpInfo;
import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.UserService;

import com.bizvane.sun.app.client.Client;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.Status;
import com.bizvane.sun.v1.common.ValueType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    CorpService corpService;
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    Client client = new Client();
    String id;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String index() {
        return "login";
    }

    /**
     *
     */
    @RequestMapping(value = "/authcode",method = RequestMethod.POST)
    public void getAuthCode(HttpServletRequest request) {
        String param = request.getParameter("param");
        log.info("json---------------" + param);
        JSONObject jsonObj = new JSONObject(param);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        JSONObject jsonObject = new JSONObject(message);
        String phone = jsonObject.get("PHONENUMBER").toString();
        System.out.println(phone);

        log.info("初始化客户端成功");

        Data data = new Data("phone", phone, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data.key,data);
        System.out.println(datalist);

        DataBox dataBox1 = new DataBox("1", Status.ONGOING, "", "com.bizvane.sun.app.method.Captcha", datalist, null, null, System.currentTimeMillis());
        System.out.println(dataBox1.data);

        DataBox dataBox = client.put(dataBox1);
        log.info("CaptchaMethod -->" + dataBox.data.get("message").value);
        System.out.println("CaptchaMethod -->" + dataBox.data.get("message").value);
    }
    /**
     * 点击注册
     */
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    @ResponseBody
    public String register(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            log.info("json---------------" + param);
            JSONObject jsonObj = new JSONObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String phone = jsonObject.get("phone").toString();
            String user_name = jsonObject.get("username").toString();
            String password = jsonObject.get("password").toString();
            String corp_name = jsonObject.get("corpname").toString();
            String address = jsonObject.get("address").toString();
            UserInfo user = userService.phoneExist(phone);
            if(user==null) {
                user = new UserInfo();
                user.setUser_name(user_name);
                user.setPhone(phone);
                user.setPassword(password);
                userService.insert(user);
                CorpInfo corp = new CorpInfo();
                corp.setCorp_name(corp_name);
                corp.setAddress(address);
                corpService.insertCorp(corp);

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("register success");
            }else{
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
     * 点击登录
     */
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
                String corp_code = login_user.getCorp_code();
                String role_code = login_user.getRole_code();
                request.getSession().setAttribute("user_id", user_id);
                request.getSession().setAttribute("corp_code", corp_code);
                request.getSession().setAttribute("role_code", role_code);
                System.out.println(request.getSession().getAttribute("user_id"));
                Date now = new Date();
                login_user.setLogin_time_recently(now);
                userService.update(login_user);

                JSONObject user_info = new JSONObject();
                user_info.put("user_id",user_id);
                if (login_user.getRole_code().equals("R100000")) {
                    //系统管理员
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