package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.SmsTemplate;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by zhouying on 2016-04-20.
 * 消息模板管理
 */
@Controller
@RequestMapping("/message")
public class SmsTemplateController {

    private static Logger logger = LoggerFactory.getLogger((SmsTemplateController.class));

    @Autowired
    private SmsTemplateService smsTemplateService;
    @Autowired
    private BaseService baseService;
    String id;

    /**
     * 消息模板
     * 列表
     */
//    @RequestMapping(value = "/mobile/template/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String SmsTemplate(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            JSONObject jsonObj = new JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            String corp_code = request.getSession().getAttribute("corp_code").toString();
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            JSONObject result = new JSONObject();
//            PageInfo<SmsTemplate> list = null;
//            if (role_code.contains(Common.ROLE_SYS)) {
//                list = this.smsTemplateService.selectBySearch(page_number, page_size, "", "");
//            } else {
//                list = this.smsTemplateService.selectBySearch(page_number, page_size, corp_code, "");
//            }
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

    /**
     * 消息模板
     * 选择
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
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
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            SmsTemplate smsTemplate = WebUtils.JSON2Bean(jsonObject, SmsTemplate.class);
            smsTemplate.setModifier(user_id);
            smsTemplate.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            String result = this.smsTemplateService.update(smsTemplate);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("更改成功");

                //----------------行为日志开始------------------------------------------
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
                String function = "消息管理_消息模板";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("template_code").toString();
                String t_name = action_json.get("template_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("edit error  ");
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
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String[] ids = jsonObject.getString("id").split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                SmsTemplate smsTemplateById = smsTemplateService.getSmsTemplateById(Integer.parseInt(ids[i]));
                smsTemplateService.delete(Integer.parseInt(ids[i]));

                //----------------行为日志开始------------------------------------------
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
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "消息管理_消息模板";
                String action = Common.ACTION_DEL;
                String t_corp_code = smsTemplateById.getCorp_code();
                String t_code = smsTemplateById.getTemplate_code();
                String t_name = smsTemplateById.getTemplate_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("scuccess");
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_Number = jsonObject.getInteger("pageNumber");
            int page_Size = jsonObject.getInteger("pageSize");
            String search_value = jsonObject.getString("searchValue").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<SmsTemplate> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.smsTemplateService.selectBySearch(page_Number, page_Size, "", search_value);
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                String corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                list = this.smsTemplateService.selectBySearch(page_Number, page_Size, corp_code, search_value);

                //   list = this.smsTemplateService.selectBySearch(page_Number, page_Size, "", search_value,manager_corp);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = this.smsTemplateService.selectBySearch(page_Number, page_Size, corp_code, search_value);
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
     * 消息模板添加
     */
    @RequestMapping(value = "/mobile/template/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String SmsTemplateAdd(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        String user_id = WebUtils.getValueForSession(request, "user_code");
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            SmsTemplate SmsTemplate = WebUtils.JSON2Bean(jsonObject, SmsTemplate.class);
            SmsTemplate.setModifier(user_id);
            Date now = new Date();
            SmsTemplate.setModified_date(Common.DATETIME_FORMAT.format(now));
            SmsTemplate.setCreated_date(Common.DATETIME_FORMAT.format(now));
            SmsTemplate.setCreater(user_id);
            String existInfo1 = smsTemplateService.SmsTemplateCodeExist(SmsTemplate.getCorp_code(), SmsTemplate.getTemplate_code());
            String existInfo2 = smsTemplateService.SmsTemplateNameExist(SmsTemplate.getCorp_code(), SmsTemplate.getTemplate_name());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            if (existInfo1.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setMessage("消息模板编号已经存在");
            } else if (existInfo2.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setMessage("消息模板名称已经存在");
            } else {
                this.smsTemplateService.insert(SmsTemplate);
                SmsTemplate smsTemplate = smsTemplateService.getSmsTemplateForId(SmsTemplate.getCorp_code(), SmsTemplate.getTemplate_code());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(String.valueOf(smsTemplate.getId()));


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
                String function = "消息管理_消息模板";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("template_code").toString();
                String t_name = action_json.get("template_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 判断消息模板名称是否存在，确保消息模板名称的唯一性
     *
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
            JSONObject jsonObject1 = JSONObject.parseObject(jsString);
            id = jsonObject1.get("id").toString();
            String message = jsonObject1.get("message").toString();
            JSONObject jsonObject2 = JSONObject.parseObject(message);
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
     *
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
            JSONObject jsonObject1 = JSONObject.parseObject(jsString);
            id = jsonObject1.get("id").toString();
            String message = jsonObject1.get("message").toString();
            JSONObject jsonObject2 = JSONObject.parseObject(message);
            String corp_code = jsonObject2.getString("corp_code");
            String template_code = jsonObject2.getString("template_code");
            String result = this.smsTemplateService.SmsTemplateCodeExist(corp_code, template_code);
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
     * 导出数据
     */
    @RequestMapping(value = "/mobile/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {

        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String user_code = request.getSession(false).getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<SmsTemplate> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.smsTemplateService.selectBySearch(1, 30000, "", search_value);
                } else {
                    list = this.smsTemplateService.selectBySearch(1, 30000, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = smsTemplateService.getAllSmsTemplateScreen(1, 30000, "", map);
                } else {
                    list = smsTemplateService.getAllSmsTemplateScreen(1, 30000, corp_code, map);
                }
            }
            List<SmsTemplate> smsTemplates = list.getList();
            if (smsTemplates.size() >= 29999) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(smsTemplates);
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, smsTemplates, map, response, request, "短信模板");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 消息管理
     * （模板筛选）
     */
    @RequestMapping(value = "/mobile/template/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
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
//            String screen = jsonObject.get("screen").toString();
//            JSONObject jsonScreen = new JSONObject(screen);
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<SmsTemplate> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = smsTemplateService.getAllSmsTemplateScreen(page_number, page_size, "", map);
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                String corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                list = smsTemplateService.getAllSmsTemplateScreen(page_number, page_size, corp_code, map);

                //  list = smsTemplateService.getAllSmsTemplateScreen(page_number, page_size, "", map, manager_corp);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = smsTemplateService.getAllSmsTemplateScreen(page_number, page_size, corp_code, map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

}
