package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageInfo;
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
import java.util.*;


/**
 * Created by zhouying on 2016-04-20.
 * 消息管理
 */
@Controller
@RequestMapping("/message")
public class MessageController {

    private static Logger logger = LoggerFactory.getLogger((MessageController.class));

    @Autowired
    private MessageService messageService;
    @Autowired
    StoreService storeService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    private BaseService baseService;
    String id;

    /**
     * 发送消息
     */
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String messageManage(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//            String user_code = request.getSession(false).getAttribute("user_code").toString();
//
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            JSONObject result = new JSONObject();
//            PageInfo<MessageInfo> list = null;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                list = messageService.selectBySearch(page_number, page_size, "", "", "");
//            } else if (role_code.equals(Common.ROLE_GM)) {
//                //企业管理员
//                list = messageService.selectBySearch(page_number, page_size, corp_code, "", "");
//            } else {
//                list = messageService.selectBySearch(page_number, page_size, corp_code, user_code, "");
//            }
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setId("1");
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.toString());
//            logger.info("错误信息:" + ex.getMessage() + ex.toString());
//        }
//        return dataBean.getJsonStr();
//    }
    @RequestMapping(value = "/unReadMessage", method = RequestMethod.GET)
    @ResponseBody
    public String UnReadMessage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try{
            String user_code= request.getSession().getAttribute("user_code").toString();
            String corp_code= request.getSession().getAttribute("corp_code").toString();
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_user_code = new Data("user_id", user_code, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_user_code.key, data_user_code);
            DataBox dataBox = iceInterfaceService.iceInterface("UnReadMessage", datalist);
            String result = dataBox.data.get("message").value;
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("获取失败");

        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/updateMessageState", method = RequestMethod.GET)
    @ResponseBody
    public String MessageState(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try{
            String user_code= request.getSession().getAttribute("user_code").toString();
            String corp_code= request.getSession().getAttribute("corp_code").toString();

            String message_id = request.getParameter("message_id");
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_type = new Data("type", "one", ValueType.PARAM);
            Data data_user_code = new Data("user_id", user_code, ValueType.PARAM);
            Data data_message_id= new Data("message_id", message_id, ValueType.PARAM);
            Data data_phone= new Data("phone", "", ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_type.key, data_type);
            datalist.put(data_user_code.key, data_user_code);
            datalist.put(data_message_id.key, data_message_id);
            datalist.put(data_phone.key, data_phone);

            DataBox dataBox = iceInterfaceService.iceInterface("MessageState", datalist);
            String result = dataBox.data.get("message").value;
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("获取失败");

        }
        return dataBean.getJsonStr();
    }
    /**
     * 获取个人消息
     *
     */
    @RequestMapping(value = "/getUserMessage", method = RequestMethod.GET)
    @ResponseBody
    public String getUserMessage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try{
            String user_code= request.getSession().getAttribute("user_code").toString();
            String corp_code= request.getSession().getAttribute("corp_code").toString();

            String row_num = request.getParameter("row_num");
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_type = new Data("type", "", ValueType.PARAM);
            Data data_user_code = new Data("user_id", user_code, ValueType.PARAM);
            Data data_row_num= new Data("row_num", row_num, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_type.key, data_type);
            datalist.put(data_user_code.key, data_user_code);
            datalist.put(data_row_num.key, data_row_num);

            DataBox dataBox = iceInterfaceService.iceInterface("Message", datalist);
            String result = dataBox.data.get("message").value;
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("获取消息失败");

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
       try{
        String param = request.getParameter("param");
        logger.info("json---------------" + param);
        com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
          // String role_code = request.getSession().getAttribute("role_code").toString();
           String operator= request.getSession().getAttribute("user_code").toString();

           String corp_code = jsonObject.get("corp_code").toString();
            String receiver_type =jsonObject.get("receiver_type").toString();
           String user_id =  jsonObject.get("user_id").toString();
           String area_code = jsonObject.get("area_code").toString();
           String store_id = jsonObject.get("store_id").toString();
           String title=jsonObject.get("title").toString();
           String message_content=jsonObject.get("message_content").toString();
           String message_type="text";

           Data data_operator = new Data("operator", operator, ValueType.PARAM);
           Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
           Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
           Data data_store_id = new Data("store_id",store_id , ValueType.PARAM);
           Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);
           Data data_receiver_type = new Data("receiver_type", receiver_type, ValueType.PARAM);
           Data data_title = new Data("title", title, ValueType.PARAM);
           Data data_message_content = new Data("message_content", message_content, ValueType.PARAM);
           Data data_message_type = new Data("message_type", message_type, ValueType.PARAM);

           Map datalist = new HashMap<String, Data>();
           datalist.put(data_user_id.key, data_user_id);
           datalist.put(data_operator.key, data_operator);
           datalist.put(data_corp_code.key, data_corp_code);
           datalist.put(data_store_id.key, data_store_id);
           datalist.put(data_area_code.key, data_area_code);
           datalist.put(data_receiver_type.key, data_receiver_type);
           datalist.put(data_title.key, data_title);
           datalist.put(data_message_content.key, data_message_content);
           datalist.put(data_message_type.key, data_message_type);

           logger.info("-------发送通知" +datalist.toString());
           DataBox dataBox = iceInterfaceService.iceInterfaceV3("MessageForWeb", datalist);
           logger.info("-------发送通知" + dataBox.status);

           if (dataBox.status.toString().equals("SUCCESS")) {
               dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
               dataBean.setId(id);
               dataBean.setMessage("SUCCESS");

               //----------------行为日志------------------------------------------
               /**
                * mongodb插入用户操作记录
                * @param operation_corp_code 操作者corp_code
                * @param operation_user_code 操作者user_code
                * @param function 功能
                * @param action 动作
                * @param corp_code 被操作corp_code
                * @param code 被操作code
                * @param name 被操作name
                * @throws Exception
                */
               com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
               String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
               String operation_user_code = request.getSession().getAttribute("user_code").toString();
               String function = "消息管理_通知管理";
               String action = Common.ACTION_ADD;
               String t_corp_code = action_json.get("corp_code").toString();
               String t_code = user_id;
               String t_name = receiver_type;
               String remark ="";
               baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
               //-------------------行为日志结束--------------------------------------------------------------------------------

           }else {
               dataBean.setId(id);
               dataBean.setCode(Common.DATABEAN_CODE_ERROR);
               dataBean.setMessage("fail");
           }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.toString());
            logger.info("send notice  error : " + ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }
    /**
     * 发送消息
     * 新增
     * /message/pullSendScope
     */
    @RequestMapping(value = "/pullSendScope", method = RequestMethod.POST)
    @ResponseBody
    public String pullSendScope(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try{
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONArray scope=new JSONArray();
            if (role_code.equals(Common.ROLE_SYS)) {
                scope.add("全体成员");
                scope.add("指定区域");
                scope.add("指定店铺");
                scope.add("指定员工");
            } else if (role_code.equals(Common.ROLE_CM)){
                scope.add("全体成员");
                scope.add("指定区域");
                scope.add("指定店铺");
                scope.add("指定员工");
            } else if (role_code.equals(Common.ROLE_GM)){
                scope.add("全体成员");
                scope.add("指定区域");
                scope.add("指定店铺");
                scope.add("指定员工");
            } else if (role_code.equals(Common.ROLE_AM)){

                scope.add("指定区域");
                scope.add("指定店铺");
                scope.add("指定员工");
            } else if (role_code.equals(Common.ROLE_SM)){

                scope.add("指定店铺");
                scope.add("指定员工");
            } else if (role_code.equals(Common.ROLE_STAFF)){

                scope.add("指定员工");
            }
             com.alibaba.fastjson.JSONObject obj=new com.alibaba.fastjson.JSONObject();
            obj.put("send_scope",scope);


            String result = obj.toString();
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result);

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
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
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
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
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String user_id = jsonObject.get("id").toString();
            String[] ids = user_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete message--" + Integer.valueOf(ids[i]));
                MessageInfo messageById = messageService.getMessageById(Integer.valueOf(ids[i]));
                messageService.delete(Integer.valueOf(ids[i]));

                //----------------行为日志------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                if (messageById != null){
                    com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                    String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                    String operation_user_code = request.getSession().getAttribute("user_code").toString();
                    String function = "消息管理_通知管理";
                    String action = Common.ACTION_DEL;
                    String t_corp_code = messageById.getCorp_code();
                    String t_code = messageById.getMessage_sender();
                    String t_name = messageById.getReceiver_type();
                    String remark ="";
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);

                }
                   //-------------------行为日志结束--------------------------------------------------------------------------------
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<MessageInfo> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = messageService.selectBySearch(page_number, page_size, "", "", search_value);
            }else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = messageService.selectBySearch(page_number, page_size, corp_code, "", search_value);

              //  list = messageService.selectBySearch(page_number, page_size, "", "", search_value,manager_corp);
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
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
            } else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = messageService.selectByScreen(page_number, page_size, corp_code, "", map);

                //   list = messageService.selectByScreen(page_number, page_size, "", "", map,manager_corp);
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
