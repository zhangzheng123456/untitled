package com.bizvane.ishop.controller;

import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nanji on 2016/11/21.
 */
@Controller
@RequestMapping("/vipMessage")
public class VipMessageController {

    private static Logger logger = LoggerFactory.getLogger((VipMessageController.class));
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    private BaseService baseService;
    String id;

    /**
     * 发送消息
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try{
            String param = request.getParameter("param");
            logger.info("vipMessage---------------" + param);
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
            // String role_code = request.getSession().getAttribute("role_code").toString();
            String operator= request.getSession().getAttribute("user_code").toString();

            String corp_code = jsonObject.get("corp_code").toString();

        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.toString());
            logger.info("send notice  error : " + ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }
}
