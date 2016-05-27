package com.bizvane.ishop.controller;

import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.LoginLog;
import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.LoginLogService;
import com.bizvane.ishop.service.ValidateCodeService;
import com.bizvane.ishop.service.UserService;
import org.apache.log4j.Logger;
import org.json.JSONObject;

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
    @Autowired
    ValidateCodeService validateCodeService;
    @Autowired
    LoginLogService loginLogService;

    private static final Logger log = Logger.getLogger(LoginController.class);

    String id;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "login";
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
            log.info("json---------------" + param);
            JSONObject jsonObj = new JSONObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String phone = jsonObject.get("PHONENUMBER").toString();
            System.out.println(phone);
            UserInfo user = userService.phoneExist(phone);
            System.out.println(user);
            if (user == null) {
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
        String param = request.getParameter("param");
        log.info("json---------------" + param);
        JSONObject jsonObj = new JSONObject(param);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        JSONObject jsonObject = new JSONObject(message);
        String phone = jsonObject.get("PHONENUMBER").toString();
        System.out.println(phone);
        String msg = userService.getAuthCode(phone, "网页注册");
//
//        String text = "[爱秀]您的注册验证码为：";
//        Random r = new Random();
//        Double d = r.nextDouble();
//        String authcode=d.toString().substring(3,3+6);
//        text = text+authcode+",1小时内有效";
//
//        Data data_phone = new Data("phone", phone, ValueType.PARAM);
//        Data data_text = new Data("text", text, ValueType.PARAM);
//        Map datalist = new HashMap<String, Data>();
//        datalist.put(data_phone.key,data_phone);
//        datalist.put(data_text.key,data_text);
//        DataBox dataBox1 = new DataBox("1", Status.ONGOING, "", "com.bizvane.sun.app.method.SendSMS", datalist, null, null, System.currentTimeMillis());
//        System.out.println(dataBox1.data);
//
//        DataBox dataBox = client.put(dataBox1);
//        log.info("SendSMSMethod -->" + dataBox.data.get("message").value);
//        System.out.println("CaptchaMethod -->" + dataBox.data.get("message").value);
//        String msg = dataBox.data.get("message").value;
//        JSONObject obj = new JSONObject(msg);
//        DataBean dataBean = new DataBean();
//        if(obj.get("message").toString().equals("短信发送成功")) {
//            //验证码存表
//            ValidateCode code = validateCodeService.selectValidateCode(0,phone);
//            Date now = new Date();
//            if(code==null) {
//                code = new ValidateCode();
//                code.setValidate_code(authcode);
//                code.setPhone(phone);
//                code.setPlatform("web register");
//                code.setCreated_date(now);
//                code.setModified_date(now);
//                code.setCreater("root");
//                code.setModifier("root");
//                code.setIsactive(Common.IS_ACTIVE_Y);
//                validateCodeService.insertValidateCode(code);
//            }else{
//                code.setValidate_code(authcode);
//                code.setModified_date(now);
//                code.setModifier("root");
//                code.setPlatform("web register");
//                code.setIsactive(Common.IS_ACTIVE_Y);
//                validateCodeService.updateValidateCode(code);
//            }
        if (msg.equals(Common.DATABEAN_CODE_SUCCESS)) {
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } else {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage("fail");
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
            log.info("json---------------" + param);
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
                dataBean.setMessage("authcode error");
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
    @RequestMapping(value = "/userlogin", method = RequestMethod.POST)
    @ResponseBody
    public String Login(HttpServletRequest request) {
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
            if (user_info == null) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("login fail");
            } else {
                System.out.println(user_info);
                //插入登录日志
                Date now = new Date();
                LoginLog log = new LoginLog();
                log.setPlatform("WEB");
                log.setPhone(phone);
                log.setCreated_date(now);
                loginLogService.insertLoginLog(log);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(user_info.toString());
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}