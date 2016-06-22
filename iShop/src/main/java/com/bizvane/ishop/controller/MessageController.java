package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.VipTagType;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.VipTagTypeService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/message")
public class MessageController {

    private SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);
    @Autowired
    private FunctionService functionService;

    @Autowired
    private VipTagTypeService vipTagTypeService;

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
            vipTagType.setModified_date(sdf.format(now));
            vipTagType.setModifier(user_id);
            vipTagType.setCreater(user_id);
            vipTagType.setCreated_date(sdf.format(now));
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
    @RequestMapping(value = "/mobile/find", method = RequestMethod.GET)
    @ResponseBody
    public String findMobile(HttpServletRequest request) {
        return "";
    }

}
