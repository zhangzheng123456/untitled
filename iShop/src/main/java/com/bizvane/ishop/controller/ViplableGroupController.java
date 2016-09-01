package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.AppLoginLog;
import com.bizvane.ishop.entity.ValidateCode;
import com.bizvane.ishop.entity.VipLabel;
import com.bizvane.ishop.entity.ViplableGroup;
import com.bizvane.ishop.service.AppLoginLogService;
import com.bizvane.ishop.service.VipLabelService;
import com.bizvane.ishop.service.ViplableGroupService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
        String result="";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String app_id = jsonObject.get("id").toString();
            String[] ids = app_id.split(",");
            for (int i=0;i<ids.length;i++){
                ViplableGroup viplableGroup = viplableGroupService.selectViplableGroupById(Integer.valueOf(ids[i]));
                List<VipLabel> vipLabels = vipLabelService.lableList(viplableGroup.getCorp_code(), viplableGroup.getLabel_group_code());
                if(vipLabels.size()>0){
                    result="所选标签分组编号为 "+viplableGroup.getLabel_group_code()+" 下有会员标签正在使用,不可删除";
                    int a=5/0;
                }
            }
            for (int i = 0; i < ids.length; i++) {
                viplableGroupService.delViplabGroupById(Integer.valueOf(ids[i]));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
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
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            ViplableGroup viplableGroup = WebUtils.JSON2Bean(jsonObject, ViplableGroup.class);
            //------------操作日期-------------
            Date date = new Date();
            viplableGroup.setCreated_date(Common.DATETIME_FORMAT.format(date));
            viplableGroup.setCreater(user_id);
            viplableGroup.setModified_date(Common.DATETIME_FORMAT.format(date));
            viplableGroup.setModifier(user_id);
            String result = viplableGroupService.addViplableGroup(viplableGroup);
            if(result.equals(Common.DATABEAN_CODE_SUCCESS)){
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add success");
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success");
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
}
