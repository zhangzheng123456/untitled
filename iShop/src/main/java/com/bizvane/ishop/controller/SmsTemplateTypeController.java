package com.bizvane.ishop.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.SmsTemplate;
import com.bizvane.ishop.entity.SmsTemplateType;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.SmsTemplateTypeService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/9/7.
 */
@Controller
@RequestMapping("/smsTemplateType")
public class SmsTemplateTypeController {
    private static final Logger logger = Logger.getLogger(SmsTemplateTypeController.class);
    @Autowired
    private SmsTemplateTypeService smsTemplateTypeService;
    @Autowired
    private BaseService baseService;
    String id;


    /**
     * 根据id查看
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        try {
            JSONObject result = new JSONObject();
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String id = jsonObject.get("id").toString();
            data = JSON.toJSONString(smsTemplateTypeService.getSmsTemplateTypeById(Integer.parseInt(id)));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(data);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 新增
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addSmsTemplateType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--vipGroup add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String user_id = request.getSession().getAttribute("user_code").toString();
            String result = smsTemplateTypeService.insert(message, user_id);
            if (result.equals("该消息模板分组编号已存在")){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            } else  if(result.equals("该消息模板分组名称已存在")){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result);

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
                String function = "消息管理_消息模板分组";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("template_type_code").toString();
                String t_name = action_json.get("template_type_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }

        return dataBean.getJsonStr();
    }

    /**
     * 编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String updateSmsTemplateType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json------updateSmsTemplateType---------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = smsTemplateTypeService.update(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");

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
                String function = "消息管理_消息模板分组";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("template_type_code").toString();
                String t_name = action_json.get("template_type_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteSmsTemplateType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--------deleteVipGroup-------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String smsTemplateTypeId = jsonObject.get("id").toString();
            String[] ids = smsTemplateTypeId.split(",");
            String msg = null;
            for (int i = 0; i < ids.length; i++) {
                logger.info("inter---------------" + Integer.valueOf(ids[i]));
                SmsTemplateType smsTemplateType = smsTemplateTypeService.getSmsTemplateTypeById(Integer.valueOf(ids[i]));
                if (smsTemplateType != null) {
                    String template_type_code = smsTemplateType.getTemplate_type_code();
                    String corp_code = smsTemplateType.getCorp_code();
                    List<SmsTemplate> smsTemplates = smsTemplateTypeService.selectByTemplateType(corp_code, template_type_code);
                    if (smsTemplates.size() > 0) {
                        msg = "消息模板分类" + template_type_code + "下有消息模板，请处理后再删除";
                        break;
                    }
                }
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
                String function = "消息管理_消息模板分组";
                String action = Common.ACTION_DEL;
                String t_corp_code = smsTemplateType.getCorp_code();
                String t_code = smsTemplateType.getTemplate_type_code();
                String t_name = smsTemplateType.getTemplate_type_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
            if (msg == null) {
                for (int i = 0; i < ids.length; i++) {
                    smsTemplateTypeService.delete(Integer.valueOf(ids[i]));
                }
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("删除成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 验证消息模板分组编号唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/smsTemplateTypeCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String smsTemplateTypeCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String template_type_code = jsonObject.get("template_type_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            SmsTemplateType smsTemplateType = smsTemplateTypeService.getSmsTemplateTypeByCode(corp_code, template_type_code, Common.IS_ACTIVE_Y);
            if (smsTemplateType != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("消息模板分组编号已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("消息模板分组编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);

            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();


    }

    /**
     * 验证消息模板分组名称唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/smsTemplateTypeNameExist", method = RequestMethod.POST)
    @ResponseBody
    public String smsTemplateTypeNameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String template_type_name = jsonObject.get("template_type_name").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            SmsTemplateType smsTemplateType = smsTemplateTypeService.getSmsTemplateTypeByName(corp_code, template_type_name, Common.IS_ACTIVE_Y);
            if (smsTemplateType != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("消息模板分组名称已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("消息模板分组名称不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);

            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);


        }
        return dataBean.getJsonStr();
    }

    /**
     * 搜索
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String searchVipGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<SmsTemplateType> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = smsTemplateTypeService.getAllSmsTemplateTypeByPage(page_number, page_size, "", search_value);
            }else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = smsTemplateTypeService.getAllSmsTemplateTypeByPage(page_number, page_size, corp_code, search_value);

                //  list = smsTemplateTypeService.getAllSmsTemplateTypeByPage(page_number, page_size, "", search_value,manager_corp);
            } else {
                list = smsTemplateTypeService.getAllSmsTemplateTypeByPage(page_number, page_size, corp_code, search_value);
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

    /**
     * 筛选
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String vipGroupScreen(HttpServletRequest request) {

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
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<SmsTemplateType> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = smsTemplateTypeService.getAllSmsTemplateTypeScreen(page_number, page_size, "","", map);
            } else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = smsTemplateTypeService.getAllSmsTemplateTypeScreen(page_number, page_size, corp_code,"" ,map);
                //   list = smsTemplateTypeService.getAllSmsTemplateTypeScreen(page_number, page_size, "", map,manager_corp);
            } else {
                list = smsTemplateTypeService.getAllSmsTemplateTypeScreen(page_number, page_size, corp_code,"", map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * @param request
     * @return
     */
    @RequestMapping(value = "/getSmsTemplateTypeInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getSmsTemplateTypeInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();

            JSONObject params = new JSONObject();
            JSONArray array = new JSONArray();
            List<SmsTemplateType> list = smsTemplateTypeService.getAllSmsTemplateType(corp_code);
            for (int i = 0; i < list.size(); i++) {
                SmsTemplateType smsTemplateType = list.get(i);
                String template_type_name = smsTemplateType.getTemplate_type_name();
                int smsTemplateTypeId = smsTemplateType.getId();
                String template_type_code = smsTemplateType.getTemplate_type_code();

                JSONObject obj = new JSONObject();
                obj.put("template_type_name", template_type_name);
                obj.put("template_type_code", template_type_code);
                obj.put("smsTemplateTypeId", smsTemplateTypeId);
                array.add(obj);
            }
            params.put("params", array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(params.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }

        return dataBean.getJsonStr();
    }
}
