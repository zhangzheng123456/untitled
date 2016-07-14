package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
    private VipRecordTypeService vipRecordTypeService;
    @Autowired
    private MessageTypeService messageTypeService;

    @Autowired
    private MessageService messageService;
    @Autowired
    private TableManagerService managerService;
    @Autowired
    private VipRecordService vipRecordService;
    String id;
    /**
     * 爱秀消息
     */
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String ishopManage(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String group_code = request.getSession(false).getAttribute("group_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//            String user_code = request.getSession(false).getAttribute("user_code").toString();
//
//            String function_code = request.getParameter("funcCode");
//            logger.info("list : 获取用户相应的信息");
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            logger.info("获取动作信息之前");
//            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(user_code, group_code, role_code, function_code);
//            logger.info("获取动作信息" + actions.toString());
//            org.json.JSONObject result = new org.json.JSONObject();
//            PageInfo<Message> list;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                list = messageService.selectBySearch(page_number, page_size, "", "");
//            } else if (role_code.equals(Common.ROLE_GM)) {
//                //企业管理员
//                list = messageService.selectBySearch(page_number, page_size, corp_code, "");
//            } else if (role_code.equals(Common.ROLE_STAFF)) {
//                //员工
//                list = messageService.selectByUser(page_number, page_size, corp_code, user_code);
//            } else {
//                //店长或区经
//                String store_code = request.getSession(false).getAttribute("store_code").toString();
//                list = messageService.selectBySearchPart(page_number, page_size, corp_code, store_code, role_code, "");
//                logger.info("获取店长或区经的详细信息" + list.toString());
//                List<Message> messages = list.getList();
//                PageInfo<Message> users = messageService.selectByUser(page_number, page_size, corp_code, user_code, "");
//                logger.info("获取本店长或区经的详细信息:" + users.toString());
//                list.getList().addAll(users.getList());
//                logger.info("店长或区经的详细信息:" + list.toString());
//            }
//            result.put("list", JSON.toJSONString(list));
//            result.put("actions", actions);
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
//
//    /**
//     * 爱秀消息
//     * 新增
//     */
//    @RequestMapping(value = "/ishop/add", method = RequestMethod.GET)
//    @ResponseBody
//    public String addIshop(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            logger.info("json:" + jsString);
//            System.out.println("json" + jsString);
//            org.json.JSONObject jsonObject = new org.json.JSONObject(jsString);
//            id = jsonObject.get("id").toString();
//            String message = jsonObject.get("message").toString();
//            logger.info("获取消息的详细信息:" + message);
//            org.json.JSONObject jsonObject1 = new org.json.JSONObject(message);
//            Message message1 = WebUtils.JSON2Bean(jsonObject1, Message.class);
//            logger.info("已获取信息:" + message1.toString());
//            Date now = new Date();
//            message1.setCreated_date(Common.DATETIME_FORMAT.format(now));
//            message1.setModified_date(Common.DATETIME_FORMAT.format(now));
//            int result = messageService.insert(message1);
//            logger.info("after insert result" + result);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId(id);
//            dataBean.setMessage("add  success ! ");
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.toString());
//            logger.info("insert message error : " + ex.getMessage() + ex.toString());
//        }
//        return dataBean.getJsonStr();
//    }
//
//    /**
//     * 爱秀消息
//     * 编辑
//     */
//    @RequestMapping(value = "/ishop/edit", method = RequestMethod.GET)
//    @ResponseBody
//    public String editIshop(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            logger.info("jsString:begin：");
//            String jsString = request.getParameter("param");
//            logger.info("jsString:end:" + jsString);
//            org.json.JSONObject jsonObject = new org.json.JSONObject(jsString);
//            String message = jsonObject.get("message").toString();
//            org.json.JSONObject jsonObject1 = new org.json.JSONObject(message);
//            logger.info("json to bean begin: ");
//            Message message1 = WebUtils.JSON2Bean(jsonObject1, Message.class);
//            logger.info("json to bean end : " + message1.toString());
//            this.messageService.insert(message1);
//            logger.info("insert messsage success !!");
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.toString());
//            logger.info(ex.getMessage());
//            logger.info(ex.toString());
//        }
//        return dataBean.getJsonStr();
//    }
//
//    /**
//     * 爱秀消息
//     * 查找
//     */
//    @RequestMapping(value = "/ishop/find", method = RequestMethod.GET)
//    @ResponseBody
//    public String findIshop(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String group_code = request.getSession(false).getAttribute("group_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//            String user_code = request.getSession(false).getAttribute("user_code").toString();
//
//            String function_code = request.getParameter("funcCode");
//            logger.info("list : 获取用户相应的信息");
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObject = new org.json.JSONObject(jsString);
//            String message = jsonObject.get("message").toString();
//            org.json.JSONObject jsonObject1 = new org.json.JSONObject(message);
//
//            int page_number = Integer.parseInt(jsonObject1.getString("pageNumber"));
//            int page_size = Integer.parseInt(jsonObject1.getString("pageSize"));
//            String search_value = jsonObject1.getString("searchValue");
//            logger.info("获取动作信息之前");
//            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(user_code, group_code, role_code, function_code);
//            logger.info("获取动作信息" + actions.toString());
//            org.json.JSONObject result = new org.json.JSONObject();
//            PageInfo<Message> list;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                list = messageService.selectBySearch(page_number, page_size, "", search_value);
//            } else if (role_code.equals(Common.ROLE_GM)) {
//                //企业管理员
//                list = messageService.selectBySearch(page_number, page_size, corp_code, search_value);
//            } else if (role_code.equals(Common.ROLE_STAFF)) {
//                //员工
//                list = messageService.selectByUser(page_number, page_size, corp_code, user_code, search_value);
//            } else {
//                //店长或区经
//                String store_code = request.getSession(false).getAttribute("store_code").toString();
//                list = messageService.selectBySearchPart(page_number, page_size, corp_code, store_code, role_code, search_value);
//                logger.info("获取店长或区经的详细信息" + list.toString());
//                List<Message> messages = list.getList();
//                PageInfo<Message> users = messageService.selectByUser(page_number, page_size, corp_code, user_code, search_value);
//                logger.info("获取本店长或区经的详细信息:" + users.toString());
//                list.getList().addAll(users.getList());
//                logger.info("店长或区经的详细信息:" + list.toString());
//            }
//            result.put("list", JSON.toJSONString(list));
//            result.put("actions", actions);
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

//
//    /**
//     * 手机短信
//     */
//    @RequestMapping(value = "/mobile/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String mobileManage(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            String group_code = request.getSession().getAttribute("group_code").toString();
//            String corp_code = request.getSession().getAttribute("corp_code").toString();
//            String user_code = request.getSession().getAttribute("user_code").toString();
//            String function_code = request.getParameter("funcCode");
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);
//            org.json.JSONObject result = new org.json.JSONObject();
//            PageInfo<VipTagType> list = null;
//            if (role_code.contains(Common.ROLE_SYS)) {
//                list = this.vipTagTypeService.selectBySearch(page_number, page_size, "", "");
//            } else {
//                list = this.vipTagTypeService.selectBySearch(page_number, page_size, corp_code, "");
//            }
//            result.put("actions", actions);
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//    /**
//     * 手机短信
//     * 新增
//     */
//    @RequestMapping(value = "/mobile/add", method = RequestMethod.GET)
//    @ResponseBody
//    public String addMobile(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String user_id = request.getSession(false).getAttribute("user_code").toString();
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            VipTagType vipTagType = WebUtils.JSON2Bean(jsonObject, VipTagType.class);
//            Date now = new Date();
//            vipTagType.setModified_date(Common.DATETIME_FORMAT.format(now));
//            vipTagType.setModifier(user_id);
//            vipTagType.setCreater(user_id);
//            vipTagType.setCreated_date(Common.DATETIME_FORMAT.format(now));
//            String result = String.valueOf(this.vipTagTypeService.insert(vipTagType));
//            if (result.equals(Common.DATABEAN_CODE_ERROR)) {
//                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setId(id);
//                dataBean.setMessage(result);
//            } else {
//                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                dataBean.setId(id);
//                dataBean.setMessage("add success");
//            }
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//    /**
//     * 手机短信
//     * 编辑
//     */
//    @RequestMapping(value = "/mobile/edit", method = RequestMethod.GET)
//    @ResponseBody
//    public String editMobile(HttpServletRequest request) {
//
//
//        return "mobile_edit";
//    }
//
//    /**
//     * 手机短信
//     * 查找
//     */
//    @RequestMapping(value = "/mobile/find", method = RequestMethod.POST)
//    @ResponseBody
//    public String findMobile(HttpServletRequest request) {
//        return "";
//    }
//
//

//    /**
//     * 手机消息类型
//     */
//    @RequestMapping(value = "/mobile/type/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String MessageType(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            String group_code = request.getSession().getAttribute("group_code").toString();
//            String corp_code = request.getSession().getAttribute("corp_code").toString();
//            String user_code = request.getSession().getAttribute("user_code").toString();
//            String function_code = request.getParameter("funcCode");
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);
//            org.json.JSONObject result = new org.json.JSONObject();
//            PageInfo<Message_type> list = null;
//            if (role_code.contains(Common.ROLE_SYS)) {
//                list = this.messageTypeService.selectBySearch(page_number, page_size, "", "");
//            } else {
//                list = this.messageTypeService.selectBySearch(page_number, page_size, corp_code, "");
//            }
//            result.put("actions", actions);
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//    /**
//     * 手机消息类型编辑
//     */
//    @RequestMapping(value = "/mobile/type/edit", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String MessageTypeEdit(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String user_id = WebUtils.getValueForSession(request, "user_code");
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            Message_type message_type = WebUtils.JSON2Bean(jsonObject, Message_type.class);
//            String result = this.messageTypeService.update(message_type);
//            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
//                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                dataBean.setMessage("更改成功！！");
//            } else {
//                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setMessage(result);
//            }
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage("edit error !!! ");
//        }
//        return dataBean.getJsonStr();
//    }
//
//    /**
//     * 手机消息类型删除
//     */
//    @RequestMapping(value = "/mobile/type/delete", method = RequestMethod.GET)
//    @ResponseBody
//    @Transactional
//    public String MessageTypeDelete(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "1";
//        try {
//            String jsString = WebUtils.getValueForSession(request, "param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.getString("id");
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            String messageType_id = jsonObject.getString("id");
//            String[] ids = messageType_id.split(",");
//            int count = 0;
//            String msg = null;
//            for (int i = 0; ids != null && i < ids.length; i++) {
//                Message_type message_type = this.messageTypeService.getMessageTypeById(Integer.parseInt(ids[i]));
//                count = this.messageTypeService.selectMessageTemplateCount(message_type.getType_code(), message_type.getCorp_code());
//                if (count > 0) {
//                    msg = "请先删除使用消息类型的消息" + message_type.getType_code();
//                    break;
//                }
//                messageTypeService.deleteById(Integer.parseInt(ids[i]));
//            }
//            if (count > 0) {
//                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setId(id);
//                dataBean.setMessage(msg);
//            } else {
//                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                dataBean.setId(id);
//                dataBean.setMessage("scuccess!!!!");
//            }
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//
//    /**
//     * 手机消息类型添加
//     */
//    @RequestMapping(value = "/mobile/type/add", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String MessageTypeAdd(HttpServletRequest request) {
//
//        DataBean dataBean = new DataBean();
//        String user_id = WebUtils.getValueForSession(request, "user_code");
//        String corp_code = WebUtils.getValueForSession(request, "corp_code");
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            Message_type message_type = WebUtils.JSON2Bean(jsonObject, Message_type.class);
//            message_type.setModifier(user_id);
//            Date now = new Date();
//            message_type.setModified_date(Common.DATETIME_FORMAT.format(now));
//            message_type.setCreated_date(Common.DATETIME_FORMAT.format(now));
//            message_type.setCreater(user_id);
//
//            String existInfo1 = this.messageTypeService.messageTypeCodeExist(message_type.getType_code(), message_type.getType_code());
//            String existInfo2 = this.messageTypeService.messageTypeNameExist(message_type.getType_name(), message_type.getType_name());
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            if (existInfo1.equals(Common.DATABEAN_CODE_ERROR)) {
//                dataBean.setMessage("信息类型编号已经存在！！！");
//            } else if (existInfo2.contains(Common.DATABEAN_CODE_ERROR)) {
//                dataBean.setMessage("信息类型名已经存在！！！");
//            } else {
//                this.messageTypeService.update(message_type);
//                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                dataBean.setMessage("add success !!!");
//            }
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//    /**
//     * 手机消息类型模板查找
//     */
//    @RequestMapping(value = "/mobile/type/find", method = RequestMethod.POST)
//    @ResponseBody
//    public String MessagetypeFind(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.getString("id");
//            String message = jsonObj.getString("message");
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            int page_Number = jsonObject.getInt("pageNumber");
//            int page_Size = jsonObject.getInt("pageSize");
//            String search_value = jsonObject.getString("search_value").toString();
//            String role_code = jsonObject.getString("role_code");
//            org.json.JSONObject result = new org.json.JSONObject();
//            PageInfo<Message_type> list;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                list = this.messageTypeService.selectBySearch(page_Number, page_Size, "", search_value);
//            } else {
//                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//                list = this.messageTypeService.selectBySearch(page_Number, page_Size, corp_code, search_value);
//            }
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId(id);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }


    /**
     * 消息模板
     * 列表
     */
    @RequestMapping(value = "/mobile/template/list", method = RequestMethod.GET)
    @ResponseBody
    public String SmsTemplate(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<SmsTemplate> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = this.smsTemplateService.selectBySearch(page_number, page_size, "", "");
            } else {
                list = this.smsTemplateService.selectBySearch(page_number, page_size, corp_code, "");
            }
            result.put("actions", actions);
            result.put("list", JSON.toJSONString(list));
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
     * 消息模板
     * 选择
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/template/select", method = RequestMethod.POST)
    @ResponseBody
    public String messageModernSearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int SmsTemplate_id = Integer.parseInt(jsonObject.getString("id"));
            SmsTemplate SmsTemplate = smsTemplateService.getSmsTemplateById(SmsTemplate_id);
            data = JSON.toJSONString(SmsTemplate);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(data);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 消息模板
     * 编辑
     */
    @RequestMapping(value = "/mobile/template/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String MessageModernEdit(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        //     String user_id = WebUtils.getValueForSession(request, "user_id");
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            SmsTemplate SmsTemplate = WebUtils.JSON2Bean(jsonObject, SmsTemplate.class);
            SmsTemplate.setModifier(user_id);
            SmsTemplate.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            String result = this.smsTemplateService.update(SmsTemplate);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("更改成功！！");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("edit error !!! ");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 消息模板
     * 删除
     */
    @RequestMapping(value = "/mobile/template/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String MessageModernDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "1";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String messageType_id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String[] ids = jsonObject.getString("id").split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                smsTemplateService.delete(Integer.parseInt(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess!!!!");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 消息类型模板
     * 查找
     */
    @RequestMapping(value = "/mobile/template/find", method = RequestMethod.POST)
    @ResponseBody
    public String SmsTemplateFind(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_Number = jsonObject.getInt("pageNumber");
            int page_Size = jsonObject.getInt("pageSize");
            String search_value = jsonObject.getString("searchValue").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<SmsTemplate> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.smsTemplateService.selectBySearch(page_Number, page_Size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = this.smsTemplateService.selectBySearch(page_Number, page_Number, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
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

//    @RequestMapping(value = "/mobile/template/types", method = RequestMethod.POST)
//    @ResponseBody
//    public String getTypes(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            List<TemplateType> list = this.smsTemplateService.getTypes();
//            com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
//            com.alibaba.fastjson.JSONObject json = null;
//            for (int i = 0; list != null && i < list.size(); i++) {
//                String type_code = String.valueOf(list.get(i).getId());
//                String type_name = list.get(i).getType_name();
//                json = new com.alibaba.fastjson.JSONObject();
//                json.put("type_code", type_code);
//                json.put("type_name", type_name);
//                array.add(json);
//            }
//            org.json.JSONObject result = new org.json.JSONObject();
//            result.put("types", array);
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage("success!!");
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.toString());
//        }
//        return dataBean.getJsonStr();
//    }

//    /**
//     * 获取消息模板类型。
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/mobile/template/types", method = RequestMethod.POST)
//    @ResponseBody
//    public String getTypes(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            List<TemplateType> list = this.smsTemplateService.getTypes();
//            com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
//            com.alibaba.fastjson.JSONObject json = null;
//            for (int i = 0; list != null && i < list.size(); i++) {
//                String type_code = String.valueOf(list.get(i).getId());
//                String type_name = list.get(i).getType_name();
//                json = new com.alibaba.fastjson.JSONObject();
//                json.put("id", type_code);
//                json.put("type_name", type_name);
//                array.add(json);
//            }
//            org.json.JSONObject result = new org.json.JSONObject();
//            result.put("types", array);
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.toString());
//        }
//        return dataBean.getJsonStr();
//    }


    /**
     * 消息模板类型添加
     *
     */
    @RequestMapping(value = "/mobile/template/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String SmsTemplateAdd(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        String user_id = WebUtils.getValueForSession(request, "user_code");
        String corp_code = WebUtils.getValueForSession(request, "corp_code");
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            SmsTemplate SmsTemplate = WebUtils.JSON2Bean(jsonObject, SmsTemplate.class);
            SmsTemplate.setModifier(user_id);
            Date now = new Date();
            SmsTemplate.setModified_date(Common.DATETIME_FORMAT.format(now));
            SmsTemplate.setCreated_date(Common.DATETIME_FORMAT.format(now));
            SmsTemplate.setCreater(user_id);
            String existInfo1 = smsTemplateService.SmsTemplateExist(SmsTemplate.getCorp_code(), SmsTemplate.getTemplate_code());
            String existInfo2 = smsTemplateService.SmsTemplateNameExist(SmsTemplate.getCorp_code(), SmsTemplate.getTemplate_name());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            if (existInfo1.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setMessage("消息模板编号已经存在！！！");
            } else if (existInfo2.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setMessage("消息模板名称已经存在!!!");
            } else {
                this.smsTemplateService.insert(SmsTemplate);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("add succcess !!!");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 判断消息模板类型名称是否存在，确保消息模板名称的唯一性
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/template/messageTemplateNameExist", method = RequestMethod.POST)
    @ResponseBody
    public String SmsTemplateNameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObject1 = new org.json.JSONObject(jsString);
            id = jsonObject1.get("id").toString();
            String message = jsonObject1.get("message").toString();
            org.json.JSONObject jsonObject2 = new org.json.JSONObject(message);
            String corp_code = jsonObject2.getString("corp_code");
            String template_name = jsonObject2.getString("template_name");
            String result = this.smsTemplateService.SmsTemplateNameExist(corp_code, template_name);
            dataBean.setId(id);
            dataBean.setCode(result);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 判断消息模板编号是否存在，确保模板编号的唯一性
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/template/messageTemplateCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String SmsTemplateCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObject1 = new org.json.JSONObject(jsString);
            id = jsonObject1.get("id").toString();
            String message = jsonObject1.get("message").toString();
            org.json.JSONObject jsonObject2 = new org.json.JSONObject(message);
            String corp_code = jsonObject2.getString("corp_code");
            String template_code = jsonObject2.getString("template_code");
            String result = this.smsTemplateService.SmsTemplateExist(corp_code, template_code);
            dataBean.setId(id);
            dataBean.setCode(result);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /***
     * 查出要导出的列
     */
    @RequestMapping(value = "getCols", method = RequestMethod.POST)
    public String selAllByCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String function_code = jsonObject.get("function_code").toString();
            List<TableManager> tableManagers = managerService.selAllByCode(function_code);
            JSONObject result = new JSONObject();
            result.put("tableManagers", JSON.toJSONString(tableManagers));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    /***
     * 导出数据
     */
//    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
//    @ResponseBody
//    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
//        DataBean dataBean=new DataBean();
//        try{
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//            String user_code = request.getSession(false).getAttribute("user_code").toString();
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            PageInfo<Message> list;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                list = messageService.selectBySearch(1, 10000, "", "");
//            } else if (role_code.equals(Common.ROLE_GM)) {
//                //企业管理员
//                list = messageService.selectBySearch(1, 10000, corp_code, "");
//            } else if (role_code.equals(Common.ROLE_STAFF)) {
//                //员工
//                list = messageService.selectByUser(1, 10000, corp_code, user_code);
//            } else {
//                //店长或区经
//                String store_code = request.getSession(false).getAttribute("store_code").toString();
//                list = messageService.selectBySearchPart(1, 10000, corp_code, store_code, role_code, "");
//                logger.info("获取店长或区经的详细信息" + list.toString());
//                List<Message> messages = list.getList();
//                PageInfo<Message> users = messageService.selectByUser(1, 10000, corp_code, user_code, "");
//                logger.info("获取本店长或区经的详细信息:" + users.toString());
//                list.getList().addAll(users.getList());
//                logger.info("店长或区经的详细信息:" + list.toString());
//            }
//            List<Message> messages = list.getList();
//            String column_name = jsonObject.get("column_name").toString();
//            String[] cols = column_name.split(",");//前台传过来的字段
//            OutExeclHelper.OutExecl(messages,cols,response);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage("word success");
//        }
//        catch (Exception e){
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(e.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
}
