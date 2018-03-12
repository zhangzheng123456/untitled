package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.VipLabelService;
import com.bizvane.ishop.service.ViplableGroupService;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/8/31.
 */
@Controller
@RequestMapping("/viplablegroup")
public class ViplableGroupController {
    @Autowired
    private ViplableGroupService viplableGroupService;

    @Autowired
    private VipLabelService vipLabelService;
    @Autowired
    private BaseService baseService;
    String id;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    //列表
    public String selectAll(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONObject result = new JSONObject();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            PageInfo<ViplableGroup> list=new PageInfo<ViplableGroup>();
            if (role_code.equals(Common.ROLE_SYS)) {
                list = viplableGroupService.selectViplabGroup(page_number, page_size, "", "");
            }else {
                list = viplableGroupService.selectViplabGroup(page_number, page_size, corp_code, "");
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
            PageInfo<ViplableGroup> list=new PageInfo<ViplableGroup>();
            if (role_code.equals(Common.ROLE_SYS)) {
                list = viplableGroupService.selectViplabGroup(page_number, page_size, "", search_value);
            }else {
                list = viplableGroupService.selectViplabGroup(page_number, page_size, corp_code, search_value);
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
            PageInfo<ViplableGroup> list=new PageInfo<ViplableGroup>();
            if (role_code.equals(Common.ROLE_SYS)) {
                list = viplableGroupService.selectViplabGroupScreen(page_number, page_size, "", map);
            }else {
                list = viplableGroupService.selectViplabGroupScreen(page_number, page_size, corp_code, map);
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
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String app_id = jsonObject.get("id").toString();
            String[] ids = app_id.split(",");
            String msg=null;
            for (int i=0;i<ids.length;i++){
                ViplableGroup viplableGroup = viplableGroupService.selectViplableGroupById(Integer.valueOf(ids[i]));
                if (viplableGroup != null) {
                    List<VipLabel> vipLabels = vipLabelService.lableList(viplableGroup.getCorp_code(), viplableGroup.getLabel_group_code());
                    if (vipLabels.size() > 0) {
                        msg = "有使用标签分组 " + viplableGroup.getLabel_group_code() + "的会员标签,请先处理再删除";
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
                String function = "会员高级管理_标签分组";
                String action = Common.ACTION_DEL;
                String t_corp_code = viplableGroup.getCorp_code();
                String t_code = viplableGroup.getLabel_group_code();
                String t_name = viplableGroup.getLabel_group_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
            if (msg == null) {
                for (int i = 0; i < ids.length; i++) {
                    viplableGroupService.delViplabGroupById(Integer.valueOf(ids[i]));
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
            dataBean.setMessage(ex.getMessage());
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

            ViplableGroup viplableGroup = WebUtils.JSON2Bean(jsonObject, ViplableGroup.class);
            //------------操作日期-------------
            Date date = new Date();
            viplableGroup.setCreated_date(Common.DATETIME_FORMAT.format(date));
            viplableGroup.setCreater(user_id);
            viplableGroup.setModified_date(Common.DATETIME_FORMAT.format(date));
            viplableGroup.setModifier(user_id);
            String result = viplableGroupService.addViplableGroup(viplableGroup);
            if(result.equals(Common.DATABEAN_CODE_SUCCESS)){
                List<ViplableGroup> viplableGroups = viplableGroupService.checkCodeOnly(viplableGroup.getCorp_code(),viplableGroup.getLabel_group_code(),viplableGroup.getIsactive());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(viplableGroups.get(0).getId()));

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
                String function = "会员高级管理_标签分组";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("label_group_code").toString();
                String t_name = action_json.get("label_group_name").toString();
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
            final ViplableGroup viplableGroup = viplableGroupService.selectViplableGroupById(Integer.parseInt(app_id));
            JSONObject result = new JSONObject();
            result.put("viplableGroup", JSON.toJSONString(viplableGroup));
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
            ViplableGroup viplableGroup = WebUtils.JSON2Bean(jsonObject, ViplableGroup.class);
            //------------操作日期-------------
            Date date = new Date();
            viplableGroup.setModified_date(Common.DATETIME_FORMAT.format(date));
            viplableGroup.setModifier(user_id);
            String result = viplableGroupService.updViplableGroupById(viplableGroup);
            if(result.equals(Common.DATABEAN_CODE_SUCCESS)){
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
                String function = "会员高级管理_标签分组";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("label_group_code").toString();
                String t_name = action_json.get("label_group_name").toString();
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


    @RequestMapping(value = "/checkCodeOnly", method = RequestMethod.POST)
    @ResponseBody
    public String checkCodeOnly(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String label_group_code = jsonObject.get("label_group_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            List<ViplableGroup> viplableGroups = viplableGroupService.checkCodeOnly(corp_code, label_group_code, Common.IS_ACTIVE_Y);
            if(viplableGroups.size()>0){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("会员标签分组编号已被使用");
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("会员标签分组编号可以使用");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
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
            String label_group_name = jsonObject.get("label_group_name").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            List<ViplableGroup> viplableGroups = viplableGroupService.checkNameOnly(corp_code, label_group_name, Common.IS_ACTIVE_Y);
            if(viplableGroups.size()>0){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("会员标签分组名称已被使用");
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("会员标签分组名称可以使用");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/selectViplabGroupList", method = RequestMethod.POST)
    @ResponseBody
    public String selectViplabGroupList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            List<ViplableGroup> viplableGroups = viplableGroupService.selectViplabGroupList(corp_code);
            JSONObject result = new JSONObject();
            result.put("viplableGroups", JSON.toJSONString(viplableGroups));
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
            PageInfo<ViplableGroup> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = viplableGroupService.selectViplabGroup(1, Common.EXPORTEXECLCOUNT, "", search_value);
                }else {
                    list = viplableGroupService.selectViplabGroup(1, Common.EXPORTEXECLCOUNT, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = viplableGroupService.selectViplabGroupScreen(1, Common.EXPORTEXECLCOUNT, "", map);
                }else {
                    list = viplableGroupService.selectViplabGroupScreen(1, Common.EXPORTEXECLCOUNT, corp_code, map);
                }
            }
            List<ViplableGroup> viplableGroups = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(viplableGroups);
            if (viplableGroups.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json,viplableGroups, map, response, request,"");
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
}
