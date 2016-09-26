package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.System;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by zhouying on 2016-04-20.
 * 消息管理
 */
@Controller
@RequestMapping("/message")
public class MessageController {

    private static Logger logger = LoggerFactory.getLogger((MessageController.class));
    @Autowired
    private FunctionService functionService;
    @Autowired
    private SmsTemplateService smsTemplateService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private TableManagerService managerService;

    String id;

    /**
     * 发送消息
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String messageManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String user_code = request.getSession(false).getAttribute("user_code").toString();

            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONObject result = new JSONObject();
            PageInfo<MessageInfo> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = messageService.selectBySearch(page_number, page_size, "", "", "");
            } else if (role_code.equals(Common.ROLE_GM)) {
                //企业管理员
                list = messageService.selectBySearch(page_number, page_size, corp_code, "", "");
            } else {
                list = messageService.selectBySearch(page_number, page_size, corp_code, user_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.toString());
            logger.info("错误信息:" + ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 发送消息
     * 获取消息类型
     */
    @RequestMapping(value = "/type", method = RequestMethod.GET)
    @ResponseBody
    public String getMessageType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            List<MessageType> type = messageService.selectAllMessageType();
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(JSON.toJSONString(type));
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.toString());
            logger.info("insert message error : " + ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 发送消息
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json:" + jsString);
            System.out.println("json" + jsString);
            JSONObject jsonObject = new JSONObject(jsString);
            id = jsonObject.get("id").toString();
            String message = jsonObject.get("message").toString();
            String user_id = request.getSession(false).getAttribute("user_id").toString();

            String result = messageService.insert(message, user_id);
            logger.info("after insert result" + result);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add  success  ");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.toString());
            logger.info("insert message error : " + ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 发送消息
     * 选择
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String messageSelect(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int message_id = Integer.parseInt(jsonObject.getString("id"));
            MessageInfo message1 = messageService.getMessageById(message_id);
            String message_code = message1.getMessage_code();
            List<Message> messages = messageService.getMessageDetail(message_code);
            JSONObject result = new JSONObject();
            result.put("info",JSON.toJSONString(message1));
            result.put("receiver",messages);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 发送消息
     * 查看详细
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/detailInfo", method = RequestMethod.POST)
    @ResponseBody
    public String messageDetailInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String message_code = jsonObject.getString("message_code");
            List<Message> messages = messageService.getMessageDetail(message_code);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(JSON.toJSONString(messages));
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 发送消息
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String messageDelete(HttpServletRequest request) {
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
                logger.info("-------------delete message--" + Integer.valueOf(ids[i]));
                messageService.delete(Integer.valueOf(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }


    /**
     * 发送消息
     * 查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String messageSearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String user_code = request.getSession(false).getAttribute("user_code").toString();

            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<MessageInfo> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = messageService.selectBySearch(page_number, page_size, "", "", search_value);
            } else if (role_code.equals(Common.ROLE_GM)) {
                //企业管理员
                list = messageService.selectBySearch(page_number, page_size, corp_code, "", search_value);
            } else {
                list = messageService.selectBySearch(page_number, page_size, corp_code, user_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.toString());
            logger.info("错误信息:" + ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 发送消息
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String messageScreen(HttpServletRequest request) {
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
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            JSONObject result = new JSONObject();
            PageInfo<MessageInfo> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = messageService.selectByScreen(page_number, page_size, "", "", map);
            } else if (role_code.equals(Common.ROLE_GM)) {
                //企业管理员
                list = messageService.selectByScreen(page_number, page_size, corp_code, "", map);
            } else {
                list = messageService.selectByScreen(page_number, page_size, corp_code, user_code, map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

}
