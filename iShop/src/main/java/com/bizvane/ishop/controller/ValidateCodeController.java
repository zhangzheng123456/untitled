package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Interfacers;
import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.entity.ValidateCode;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.TableManagerService;
import com.bizvane.ishop.service.ValidateCodeService;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by yin on 2016/6/23.
 */
@Controller
@RequestMapping("/validatecode")
public class ValidateCodeController {
    @Autowired
    private ValidateCodeService validateCodeService;
    @Autowired
    private BaseService baseService;
    String id;

    private static final Logger logger = Logger.getLogger(ValidateCodeController.class);

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    //列表
    public String selectAll(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String phone = request.getSession().getAttribute("phone").toString();

        try {
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONObject result = new JSONObject();
            PageInfo<ValidateCode> list;
            if (role_code.equals(Common.ROLE_SYS) || phone.equals("18900001111")) {
                list = validateCodeService.selectAllValidateCode(page_number, page_size, "");
            }else {
                list = validateCodeService.selectValidateCodeByCorp(page_number, page_size,corp_code, "");
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

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    //条件查询
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String phone = request.getSession().getAttribute("phone").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            //-------------------------------------------------------
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            PageInfo<ValidateCode> list;
            if (role_code.equals(Common.ROLE_SYS) || phone.equals("18900001111")) {
                list = validateCodeService.selectAllValidateCode(page_number, page_size, search_value);
            }else {
                list = validateCodeService.selectValidateCodeByCorp(page_number, page_size,corp_code, search_value);
            }
            JSONObject result = new JSONObject();
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

    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    //条件查询
    public String selectByScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();

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
            JSONObject result = new JSONObject();
            PageInfo<ValidateCode> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = validateCodeService.selectAllScreen(page_number, page_size, map);
            }else {
                list = validateCodeService.selectByCorpScreen(page_number, page_size, corp_code, map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }
    /**
     * 增加（用了事务）
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addValidateCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            ValidateCode validateCode = WebUtils.JSON2Bean(jsonObject, ValidateCode.class);
            //------------操作日期-------------
            Date date = new Date();
            validateCode.setCreated_date(Common.DATETIME_FORMAT.format(date));
            validateCode.setCreater(user_id);
            validateCode.setModified_date(Common.DATETIME_FORMAT.format(date));
            validateCode.setModifier(user_id);
            int i = validateCodeService.insertValidateCode(validateCode);
            if (i==1){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("该手机号已存在");
                return dataBean.getJsonStr();
            }
            ValidateCode validateCode1 = validateCodeService.selectPhoneExist(validateCode.getPlatform(),validateCode.getPhone(),validateCode.getIsactive());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(String.valueOf(validateCode1.getId()));

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
            String function = "系统管理_验证码管理";
            String action = Common.ACTION_ADD;
            String t_corp_code = "";
            String t_code = action_json.get("phone").toString();
            String t_name = action_json.get("platform").toString();
            String remark = "";
            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
            //-------------------行为日志结束-----------------------------------------------------------------------------------
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 删除(用了事务)
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String inter_id = jsonObject.get("id").toString();
            String[] ids = inter_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                ValidateCode validateCode = validateCodeService.selValidateCodeById(Integer.valueOf(ids[i]));
                validateCodeService.deleteValidateCode(Integer.valueOf(ids[i]));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");

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
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "系统管理_验证码管理";
                String action = Common.ACTION_DEL;
                String t_corp_code = "";
                String t_code = validateCode.getPhone();
                String t_name = validateCode.getPlatform();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 根据ID查询
     */
    @RequestMapping(value = "/selectById", method = RequestMethod.POST)
    @ResponseBody
    public String selectById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String app_id = jsonObject.get("id").toString();
            final ValidateCode validateCode = validateCodeService.selValidateCodeById(Integer.parseInt(app_id));
            JSONObject result = new JSONObject();
            result.put("validateCode", JSON.toJSONString(validateCode));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("selectById-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 编辑(加了事务)
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editValidateCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            ValidateCode validateCode = WebUtils.JSON2Bean(jsonObject, ValidateCode.class);
            //------------操作日期-------------
            Date date = new Date();
            validateCode.setModified_date(Common.DATETIME_FORMAT.format(date));
            validateCode.setModifier(user_id);
            validateCodeService.updateValidateCode(validateCode);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success");

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
            String function = "系统管理_验证码管理";
            String action = Common.ACTION_UPD;
            String t_corp_code = "";
            String t_code = action_json.get("phone").toString();
            String t_name = action_json.get("platform").toString();
            String remark = "";
            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
            //-------------------行为日志结束----------------------------------------------------------------------------------
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        logger.info("info--------" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }



    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            //系统管理员(官方画面)
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<ValidateCode> corpInfo = null;
            if (screen.equals("")) {
                corpInfo= validateCodeService.selectAllValidateCode(1, Common.EXPORTEXECLCOUNT, search_value);
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                corpInfo = validateCodeService.selectAllScreen(1, Common.EXPORTEXECLCOUNT, map);
            }
            List<ValidateCode> feedbacks = corpInfo.getList();
            if (feedbacks.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(feedbacks);
            LinkedHashMap<String,String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json,feedbacks, map, response, request);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

}

