package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.MessageTemplate;
import com.bizvane.ishop.entity.Message_type;
import com.bizvane.ishop.entity.VipTagType;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.MessageTemplateService;
import com.bizvane.ishop.service.MessageTypeService;
import com.bizvane.ishop.service.VipTagTypeService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private FunctionService functionService;

    @Autowired
    private VipTagTypeService vipTagTypeService;

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Autowired
    private MessageTypeService messageTypeService;

    /**
     * 爱秀消息
     */
    @RequestMapping(value = "/ishop/list", method = RequestMethod.GET)
    @ResponseBody
    public String ishopManage(HttpServletRequest request) {


        return "iShop";
    }

    /**
     * 爱秀消息
     * 新增
     */
    @RequestMapping(value = "/ishop/add", method = RequestMethod.GET)
    @ResponseBody
    public String addIshop(HttpServletRequest request) {
        return "iShop_add";
    }

    /**
     * 爱秀消息
     * 编辑
     */
    @RequestMapping(value = "/ishop/edit", method = RequestMethod.GET)
    @ResponseBody
    public String editIshop(HttpServletRequest request) {
        return "iShop_edit";
    }

    /**
     * 爱秀消息
     * 查找
     */
    @RequestMapping(value = "/ishop/find", method = RequestMethod.GET)
    @ResponseBody
    public String findIshop(HttpServletRequest request) {
        return "";
    }


    /**
     * 手机短信
     */
    @RequestMapping(value = "/mobile/list", method = RequestMethod.GET)
    @ResponseBody
    public String mobileManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VipTagType> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = this.vipTagTypeService.selectBySearch(page_number, page_size, "", "");
            } else {
                list = this.vipTagTypeService.selectBySearch(page_number, page_size, corp_code, "");
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
     * 手机短信
     * 新增
     */
    @RequestMapping(value = "/mobile/add", method = RequestMethod.GET)
    @ResponseBody
    public String addMobile(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String user_id = request.getSession(false).getAttribute("user_id").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            VipTagType vipTagType = WebUtils.JSON2Bean(jsonObject, VipTagType.class);
            Date now = new Date();
            vipTagType.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipTagType.setModifier(user_id);
            vipTagType.setCreater(user_id);
            vipTagType.setCreated_date(Common.DATETIME_FORMAT.format(now));
            this.vipTagTypeService.insert(vipTagType);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("添加成功！！！");
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 手机短信
     * 编辑
     */
    @RequestMapping(value = "/mobile/edit", method = RequestMethod.GET)
    @ResponseBody
    public String editMobile(HttpServletRequest request) {


        return "mobile_edit";
    }

    /**
     * 手机短信
     * 查找
     */
    @RequestMapping(value = "/mobile/find", method = RequestMethod.POST)
    @ResponseBody
    public String findMobile(HttpServletRequest request) {
        return "";
    }


    /**
     * 手机消息类型
     */
    @RequestMapping(value = "/mobile/type/list", method = RequestMethod.GET)
    @ResponseBody
    public String MessageType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<Message_type> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = this.messageTypeService.selectBySearch(page_number, page_size, "", "");
            } else {
                list = this.messageTypeService.selectBySearch(page_number, page_size, corp_code, "");
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
     * 手机消息类型编辑
     */
    @RequestMapping(value = "/mobile/type/edit", method = RequestMethod.GET)
    @ResponseBody
    public String MessageTypeEdit(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        String user_id = WebUtils.getValueForSession(request, "user_id");
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            Message_type message_type = WebUtils.JSON2Bean(jsonObject, Message_type.class);
            this.messageTypeService.update(message_type);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success ");

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 手机消息类型删除
     */
    @RequestMapping(value = "/mobile/type/delete", method = RequestMethod.GET)
    @ResponseBody
    public String MessageTypeDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "1";
        try {
            String jsString = WebUtils.getValueForSession(request, "param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String messageType_id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String[] ids = messageType_id.split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                messageTypeService.deleteById(Integer.parseInt(ids[i]));
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
     * 手机消息类型添加
     */
    @RequestMapping(value = "/mobile/type/add", method = RequestMethod.POST)
    @ResponseBody
    public String MessageTypeAdd(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        String user_id = WebUtils.getValueForSession(request, "user_id");
        String corp_code = WebUtils.getValueForSession(request, "corp_code");
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            Message_type message_type = WebUtils.JSON2Bean(jsonObject, Message_type.class);
            message_type.setModifier(user_id);
            Date now = new Date();
            message_type.setModified_date(Common.DATETIME_FORMAT.format(now));
            message_type.setCreated_date(Common.DATETIME_FORMAT.format(now));
            message_type.setCreater(user_id);
            String existInfo = this.messageTypeService.MessageTypeCodeExist(corp_code, message_type.getType_code());
            if (existInfo.equals(Common.DATABEAN_CODE_ERROR)) {
                messageTypeService.insert(message_type);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add error ");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("信息类型编号已存在！！");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 手机消息类型模板查找
     */
    @RequestMapping(value = "/mobile/type/find", method = RequestMethod.POST)
    @ResponseBody
    public String MessagetypeFind(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.getString("message");
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_Number = jsonObject.getInt("pageNumber");
            int page_Size = jsonObject.getInt("pageSize");
            String search_value = jsonObject.getString("search_value").toString();
            String role_code = jsonObject.getString("role_code");
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<Message_type> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.messageTypeService.selectBySearch(page_Number, page_Size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = this.messageTypeService.selectBySearch(page_Number, page_Size, corp_code, search_value);
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

    /**
     * 根据用户的ID输出用户的企业
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getMessageTypeByUser", method = RequestMethod.POST)
    @ResponseBody
    public String getMessageTypeByUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject corps = new JSONObject();
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONArray array = new JSONArray();
            if (role_code.equals((Common.ROLE_SYS))) {
                //List<Corp> list = corpService.selectAllCorp();
                List<Message_type> list = messageTypeService.selectAllMessageType();
                for (int i = 0; i < list.size(); i++) {
                    //Corp corp = list.get(i);
                    Message_type message_type = list.get(i);
                    String type_code = message_type.getType_code();
                    String type_name = message_type.getType_name();
                    //   String corp_name = corp.getCorp_name();
                    JSONObject obj = new JSONObject();
                    obj.put("type_code", type_code);
                    obj.put("type_name", type_name);
                    array.add(obj);
                }
            } else {
//                String corp_code = request.getSession().getAttribute("corp_code").toString();
//                // Corp corp = corpService.selectByCorpId(0, corp_code);
//                Message_type message_type = messageTypeService.getMessageTypeByCorp(corp_code);
//                String c_code = corp.getCorp_code();
//                String corp_name = corp.getCorp_name();
//                JSONObject obj = new JSONObject();
//                obj.put("corp_code", c_code);
//                obj.put("corp_name", corp_name);
//                array.add(obj);
            }
            corps.put("corps", array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(corps.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 手机消息模板
     */
    @RequestMapping(value = "/mobile/template/list", method = RequestMethod.GET)
    @ResponseBody
    public String MessageTemplate(HttpServletRequest request) {
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
            PageInfo<MessageTemplate> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = this.messageTemplateService.selectBySearch(page_number, page_size, "", "");
            } else {
                list = this.messageTemplateService.selectBySearch(page_number, page_size, corp_code, "");
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
     * 短信模板管理
     * 短信模板的选择
     *
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
            int messageTemplate_id = Integer.parseInt(jsonObject.getString("id"));
            MessageTemplate messageTemplate = messageTemplateService.getMessageTemplateById(messageTemplate_id);
            data = JSON.toJSONString(messageTemplate);
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
     * 手机消息模板
     * 编辑
     */
    @RequestMapping(value = "/mobile/template/edit", method = RequestMethod.POST)
    @ResponseBody
    public String MessageModernEdit(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = WebUtils.getValueForSession(request, "user_id");
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            MessageTemplate messageTemplate = WebUtils.JSON2Bean(jsonObject, MessageTemplate.class);
            this.messageTemplateService.update(messageTemplate);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success ");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 手机消息模板删除
     */
    @RequestMapping(value = "/mobile/template/delete", method = RequestMethod.POST)
    @ResponseBody
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
                messageTemplateService.delete(Integer.parseInt(ids[i]));
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
     * 手机消息类型模板查找
     */
    @RequestMapping(value = "/mobile/template/find", method = RequestMethod.POST)
    @ResponseBody
    public String MessageTemplateFind(HttpServletRequest request) {
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
            String search_value = jsonObject.getString("search_value").toString();
            String role_code = jsonObject.getString("role_code");
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<MessageTemplate> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.messageTemplateService.selectBySearch(page_Number, page_Size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = this.messageTemplateService.selectBySearch(page_Number, page_Number, corp_code, search_value);
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


    /**
     * 手机消息类型模板添加
     */
    @RequestMapping(value = "/mobile/template/add", method = RequestMethod.POST)
    @ResponseBody
    public String MessageTemplateAdd(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        String user_id = WebUtils.getValueForSession(request, "user_id");
        String corp_code = WebUtils.getValueForSession(request, "corp_code");
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            MessageTemplate messageTemplate = WebUtils.JSON2Bean(jsonObject, MessageTemplate.class);
            messageTemplate.setModifier(user_id);
            Date now = new Date();
            messageTemplate.setModified_date(Common.DATETIME_FORMAT.format(now));
            messageTemplate.setCreated_date(Common.DATETIME_FORMAT.format(now));
            messageTemplate.setCreater(user_id);
            String existInfo = this.messageTemplateService.messageTemplateExist(corp_code, messageTemplate.getTem_code());
            if (existInfo.equals(Common.DATABEAN_CODE_SUCCESS)) {
                messageTemplateService.insert(messageTemplate);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add error ");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("模板类型编号已存在！！");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


}
