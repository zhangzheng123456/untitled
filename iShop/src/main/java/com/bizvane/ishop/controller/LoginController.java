package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.CorpInfo;
import com.bizvane.ishop.entity.LogInfo;
import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.LogService;
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
    @Autowired
    LogService logService;
    @Autowired
    FunctionService functionService;
    private static Logger log = LoggerFactory.getLogger(LoginController.class);
    String[] arg = new String[]{"--Ice.Config=client.config"};
    Client client = new Client(arg);
    String id;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String index() {
        return "login";
    }

    /**
     *
     */
    @RequestMapping(value = "/authcode",method = RequestMethod.POST)
    @ResponseBody
    public String getAuthCode(HttpServletRequest request) {
        String param = request.getParameter("param");
        log.info("json---------------" + param);
        JSONObject jsonObj = new JSONObject(param);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        JSONObject jsonObject = new JSONObject(message);
        String phone = jsonObject.get("PHONENUMBER").toString();
        System.out.println(phone);

        String text = "[爱秀]您的注册验证码为：";
        Random r = new Random();
        Double d = r.nextDouble();
        String authcode=d.toString().substring(3,3+6);
        text = text+authcode+",1小时内有效";

        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_text = new Data("text", text, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_phone.key,data_phone);
        datalist.put(data_text.key,data_text);
        DataBox dataBox1 = new DataBox("1", Status.ONGOING, "", "com.bizvane.sun.app.method.SendSMS", datalist, null, null, System.currentTimeMillis());
        System.out.println(dataBox1.data);

        DataBox dataBox = client.put(dataBox1);
        log.info("SendSMSMethod -->" + dataBox.data.get("message").value);
        System.out.println("CaptchaMethod -->" + dataBox.data.get("message").value);
        String msg = dataBox.data.get("message").value;
        JSONObject obj = new JSONObject(msg);
        DataBean dataBean = new DataBean();
        if(obj.get("message").toString().equals("短信发送成功")) {
            LogInfo logInfo = logService.selectLog(0,phone);
            Date now = new Date();
            if(logInfo==null) {
                logInfo = new LogInfo();
                logInfo.setContent(authcode);
                logInfo.setPhone(phone);
                logInfo.setCreated_date(now);
                logInfo.setModified_date(now);
                logInfo.setPlatform("网页注册");
                logService.insertLoginLog(logInfo);
            }else{
                logInfo.setContent(authcode);
                logInfo.setModified_date(now);
                logInfo.setPlatform("网页注册");
                logService.updateLoginLog(logInfo);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        }else{
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage("fail");
        }
        return dataBean.getJsonStr();
    }
    /**
     * 点击注册
     */
    @RequestMapping(value = "/register",method = RequestMethod.POST)
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
            JSONObject jsonObject = new JSONObject(message);
            String phone = jsonObject.get("PHONENUMBER").toString();
            String auth_code = jsonObject.get("PHONECODE").toString();
            String user_name = jsonObject.get("USERNAME").toString();
            String password = jsonObject.get("PASSWORD").toString();
            String corp_name = jsonObject.get("COMPANY").toString();
            String address = jsonObject.get("ADDRESS").toString();
            UserInfo user = userService.phoneExist(phone);
            if(user==null) {
                System.out.println("---------user==null----------");
                LogInfo logInfo = logService.selectLog(0,phone);
                Date now = new Date();
                Date time = logInfo.getModified_date();
                long timediff=(now.getTime()-time.getTime())/1000;
                if(auth_code.equals(logInfo.getContent())&&timediff<3600) {
                    System.out.println("---------auth_code----------");
                    user = new UserInfo();
                    user.setUser_name(user_name);
                    user.setPhone(phone);
                    user.setPassword(password);
                    user.setRole_code("R500000");
                    user.setCreated_date(now);
                    user.setModified_date(now);
                    userService.insert(user);

                    CorpInfo corp = new CorpInfo();
                    corp.setCorp_name(corp_name);
                    corp.setAddress(address);
                    corp.setContact(user_name);
                    corp.setContact_phone(phone);
                    corp.setCreated_date(now);
                    corp.setModified_date(now);
                    corpService.insertCorp(corp);

                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage("register success");
                }else{
                    System.out.println("---------auth_code-xxx---------");
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("authcode error");
                }
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

                JSONArray menu = functionService.selectAllFunctions(user_id,role_code);
                JSONArray action = functionService.selectAllActions(user_id,role_code);
                request.getSession().setAttribute("user_id", user_id);
                request.getSession().setAttribute("corp_code", corp_code);
                request.getSession().setAttribute("role_code", role_code);
                request.getSession().setAttribute("menu", menu);
                request.getSession().setAttribute("action", action);
                System.out.println(request.getSession().getAttribute("user_id"));
                Date now = new Date();
                login_user.setLogin_time_recently(now);
                userService.update(login_user);

                JSONObject user_info = new JSONObject();
                user_info.put("user_id",user_id);
                user_info.put("menu",menu);
                user_info.put("action",action);
                if (login_user.getRole_code().contains("R10")) {
                    //系统管理员
                    user_info.put("uri","official");
                    user_info.put("user_type","admin");
                } else if (login_user.getRole_code().contains("R50")) {
                    //总经理
                    user_info.put("uri","common");
                    user_info.put("user_type","gm");
                }else if (login_user.getRole_code().contains("R20")) {
                    //区经
                    user_info.put("uri","common");
                    user_info.put("user_type","am");
                }else if (login_user.getRole_code().contains("R30")) {
                    //店长
                    user_info.put("uri","common");
                    user_info.put("user_type", "sm");
                }else {
                    //导购
                    user_info.put("uri","common");
                    user_info.put("user_type","staff");
                }
                System.out.println(user_info);
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