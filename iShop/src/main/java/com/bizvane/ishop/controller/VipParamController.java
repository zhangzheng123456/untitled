package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.VipParam;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.VipParamService;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by yin on 2016/9/7.
 */
@Controller
@RequestMapping("/vipparam")
public class VipParamController {
    @Autowired
    private VipParamService vipParamService;
    @Autowired
    private BaseService baseService;
    String id;

    //条件查询
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            //-------------------------------------------------------
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipParam> list=new PageInfo<VipParam>();
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipParamService.selectAllParam(page_number, page_size, "", search_value);
            }else {
                list = vipParamService.selectAllParam(page_number, page_size, corp_code, search_value);
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

    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String selectByScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipParam> list=new PageInfo<VipParam>();
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipParamService.selectAllParamScreen(page_number, page_size, "", map);
            }else {
                list = vipParamService.selectAllParamScreen(page_number, page_size, corp_code, map);
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
     * 删除(用了事务)
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String result="";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String app_id = jsonObject.get("id").toString();
            String[] ids = app_id.split(",");
//            for (int i=0;i<ids.length;i++){
//                ViplableGroup viplableGroup = viplableGroupService.selectViplableGroupById(Integer.valueOf(ids[i]));
//                List<VipLabel> vipLabels = vipLabelService.lableList(viplableGroup.getCorp_code(), viplableGroup.getLabel_group_code());
//                if(vipLabels.size()>0){
//                    result="所选标签分组编号为 "+viplableGroup.getLabel_group_code()+" 下有会员标签正在使用,不可删除";
//                    int a=5/0;
//                }
//            }
            for (int i = 0; i < ids.length; i++) {
                VipParam vipParam = vipParamService.selectById(Integer.valueOf(ids[i]));
                vipParamService.delete(Integer.valueOf(ids[i]));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");


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
                String function = "会员管理_会员参数";
                String action = Common.ACTION_DEL;
                String t_corp_code = vipParam.getCorp_code();
                String t_code = vipParam.getParam_type();
                String t_name = vipParam.getParam_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(result);
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String param_values=jsonObject.getString("param_values").replace("，",",");
            String[] params=param_values.split(",");
            String param="";
            for (int i = 0; i < params.length; i++) {
                if(params[i].trim().equals("")){
                    continue;
                }
                param+=params[i]+",";
            }
            if(param.endsWith(",")){
               param=param.substring(0,param.length()-1);
            }
            jsonObject.put("param_values",param);
            VipParam vipParam = WebUtils.JSON2Bean(jsonObject, VipParam.class);
            String corp_code = jsonObject.getString("corp_code");
//            String param_name = jsonObject.getString("param_name");
            String param_name = "CUST_"+System.currentTimeMillis();
            vipParam.setParam_name(param_name);
            //------------操作日期-------------
            Date date = new Date();
            vipParam.setCreated_date(Common.DATETIME_FORMAT.format(date));
            vipParam.setCreater(user_id);
            vipParam.setModified_date(Common.DATETIME_FORMAT.format(date));
            vipParam.setModifier(user_id);
            String result = vipParamService.insert(vipParam);
            if(result.equals(Common.DATABEAN_CODE_SUCCESS)){
                List<VipParam> vipParam1 = vipParamService.selectByParamName(corp_code,param_name,vipParam.getIsactive());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(vipParam1.get(0).getId()));


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
                String function = "会员管理_会员参数";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("param_type").toString();
                String t_name = action_json.get("param_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束--------------------------------------------------------------------------------
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String app_id = jsonObject.get("id").toString();
            final VipParam vipParam = vipParamService.selectById(Integer.parseInt(app_id));
            JSONObject result = new JSONObject();
            result.put("vipParam", JSON.toJSONString(vipParam));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            VipParam vipParam = WebUtils.JSON2Bean(jsonObject, VipParam.class);
            //------------操作日期-------------
            Date date = new Date();
            String param_name = vipParam.getParam_name();
            if (!param_name.startsWith("CUST_"))
                param_name = "CUST_"+param_name;
            vipParam.setParam_name(param_name.toUpperCase());
            vipParam.setModified_date(Common.DATETIME_FORMAT.format(date));
            vipParam.setModifier(user_id);
            String param_values=jsonObject.getString("param_values").replace("，",",");
            String[] params=param_values.split(",");
            String param="";
            for (int i = 0; i < params.length; i++) {
                if(params[i].trim().equals("")){
                    continue;
                }
                param+=params[i]+",";
            }
            if(param.endsWith(",")){
                param=param.substring(0,param.length()-1);
            }
            vipParam.setParam_values(param);
            String result = vipParamService.update(vipParam);
            if(result.equals(Common.DATABEAN_CODE_SUCCESS)){
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
                String function = "会员管理_会员参数";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("param_type").toString();
                String t_name = action_json.get("param_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/checkNameOnly", method = RequestMethod.POST)
    @ResponseBody
    public String checkNameOnly(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String param_name = jsonObject.get("param_name").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            List<VipParam> vipParams = vipParamService.selectByParamName(corp_code, param_name,Common.IS_ACTIVE_Y);
            if(vipParams.size()>0){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("会员参数名称已被使用");
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("会员参数名称可以使用");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/checkDescOnly", method = RequestMethod.POST)
    @ResponseBody
    public String checkDescOnly(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String param_desc = jsonObject.get("param_desc").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            List<VipParam> vipParams = vipParamService.selectByParamDesc(corp_code, param_desc,Common.IS_ACTIVE_Y);
            if(vipParams.size()>0){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("会员参数名称已被使用");
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("会员参数名称可以使用");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/updateShowOrder", method = RequestMethod.POST)
    @ResponseBody
    public String updateShowOrder(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String params = jsonObject.get("param").toString();

            JSONArray array = JSONArray.parseArray(params);
            for (int i = 0; i < array.size(); i++) {
                String object = array.get(i).toString();
                JSONObject obj = JSONObject.parseObject(object);
                int id = Integer.parseInt(obj.get("id").toString());
                String show_order = obj.get("show_order").toString();
                vipParamService.updateShowOrder(id,show_order);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<VipParam> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = vipParamService.selectAllParam(1, Common.EXPORTEXECLCOUNT, "", search_value);
                }else {
                    list = vipParamService.selectAllParam(1, Common.EXPORTEXECLCOUNT, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = vipParamService.selectAllParamScreen(1, Common.EXPORTEXECLCOUNT, "", map);
                }else {
                    list = vipParamService.selectAllParamScreen(1, Common.EXPORTEXECLCOUNT, corp_code, map);
                }
            }
            List<VipParam> vipParams = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(vipParams);
            if (vipParams.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json,vipParams, map, response, request,"");
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
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    //显示企业下可用参数
    @RequestMapping(value = "/corpVipParams", method = RequestMethod.POST)
    @ResponseBody
    public String corpVipParams(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            //-------------------------------------------------------
            JSONObject result = new JSONObject();
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.getString("corp_code");
            }
            List<VipParam> vipParams = vipParamService.selectParamByCorp(corp_code);
            List<VipParam> list = new ArrayList<VipParam>();
            for (int i = 0; i < vipParams.size(); i++) {
                VipParam vipParam = vipParams.get(i);
                String type = vipParams.get(i).getParam_type();
                if (!type.equals("rule"))
                    list.add(vipParam);
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


    //显示企业下可用日期类型参数
    @RequestMapping(value = "/dateVipParams", method = RequestMethod.POST)
    @ResponseBody
    public String dateVipParams(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String param_type = Common.VIP_PARAM_TYPE_DATE;
            JSONObject result = new JSONObject();
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.getString("corp_code");
            }
            List<VipParam> vipParams = vipParamService.selectParamByType(corp_code,param_type);

            VipParam param = new VipParam();
            param.setParam_name("BIRTHDAY");
            param.setParam_desc("生日（个人资料）");
            param.setIsactive("Y");
            param.setCorp_code(corp_code);
            param.setParam_type(Common.VIP_PARAM_TYPE_DATE);
            vipParams.add(param);

            result.put("list", JSON.toJSONString(vipParams));
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

    //显示企业下可用参数(增加资料字段)
    @RequestMapping(value = "/vipInfoParams", method = RequestMethod.POST)
    @ResponseBody
    public String vipInfoParams(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            //-------------------------------------------------------
            JSONObject result = new JSONObject();
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.getString("corp_code");
            }
            List<VipParam> list = new ArrayList<VipParam>();

            VipParam param1 = new VipParam();
            param1.setParam_name("vip_name");
            param1.setParam_desc("姓名（个人资料）");
            param1.setRequired("Y");
            param1.setIsactive("Y");
            param1.setCorp_code(corp_code);
            param1.setParam_type(Common.VIP_PARAM_TYPE_TEXT);
            list.add(param1);

            VipParam param2 = new VipParam();
            param2.setParam_name("sex_vip");
            param2.setParam_desc("性别（个人资料）");
            param2.setRequired("Y");
            param2.setIsactive("Y");
            param2.setCorp_code(corp_code);
            param2.setParam_type(Common.VIP_PARAM_TYPE_SELECT);
            param2.setParam_values("女,男");
            list.add(param2);

            VipParam param3 = new VipParam();
            param3.setParam_name("birthday");
            param3.setParam_desc("生日（个人资料）");
            param3.setRequired("Y");
            param3.setIsactive("Y");
            param3.setCorp_code(corp_code);
            param3.setParam_type(Common.VIP_PARAM_TYPE_DATE);
            list.add(param3);

            VipParam param4 = new VipParam();
            param4.setParam_name("province");
            param4.setParam_desc("省市区（个人资料）");
            param4.setRequired("Y");
            param4.setIsactive("Y");
            param4.setCorp_code(corp_code);
            param4.setParam_type(Common.VIP_PARAM_TYPE_TEXT);
            list.add(param4);

            VipParam param7 = new VipParam();
            param7.setParam_name("address");
            param7.setParam_desc("详细地址（个人资料）");
            param7.setRequired("Y");
            param7.setIsactive("Y");
            param7.setCorp_code(corp_code);
            param7.setParam_type(Common.VIP_PARAM_TYPE_TEXT);
            list.add(param7);

//            VipParam param4 = new VipParam();
//            param4.setParam_name("mobile");
//            param4.setParam_desc("手机号（个人资料）");
//            param4.setIsactive("Y");
//            param4.setCorp_code(corp_code);
//            param4.setParam_type(Common.VIP_PARAM_TYPE_TEXT);
//            list.add(param4);


            List<VipParam> vipParams = vipParamService.selectParamByCorp(corp_code);
            for (int i = 0; i < vipParams.size(); i++) {
                VipParam vipParam = vipParams.get(i);
                String type = vipParams.get(i).getParam_type();
                if (!type.equals("rule"))
                    list.add(vipParam);
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

}
