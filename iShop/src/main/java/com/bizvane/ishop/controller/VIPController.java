package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.VIPInfo;
import com.bizvane.ishop.entity.VIPtag;
import com.bizvane.ishop.entity.VipCallbackRecord;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.github.pagehelper.PageInfo;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/VIP")
public class VIPController {


    @Autowired
    private UserService userService;

    @Autowired
    private VIPTagService vipTagService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private VipService vipService;

    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);


    /**
     * 会员列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String VIPManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession(false).getAttribute("group_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(user_id, role_code, function_code, group_code);
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VIPInfo> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipService.selectBySearch(page_number, page_size, "", "");
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipService.selectBySearch(page_number, page_size, corp_code, "");
            }
            result.put("list", list);
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addVIP(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_id = request.getSession(false).getAttribute("user_id").toString();

        try {
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            //  String message = jsonObj.getString("message");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            VIPInfo vipInfo = WebUtils.JSON2Bean(jsonObject, VIPInfo.class);
            Date now = new Date();
            vipInfo.setRegister_time(now);
            vipInfo.setModified_date(now);
            vipInfo.setModifier(user_id);
            vipInfo.setCreated_date(now);
            vipInfo.setCreater(user_id);
            String exist = vipService.vipCodeExist(vipInfo.getVip_code(), corp_code);
            if (exist.equals(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("此编号已经存在");
            }
            vipService.insert(vipInfo);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("add success !!!");

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editVIP(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String user_id = request.getSession(false).getAttribute("user_id").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.getString("message");
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            VIPInfo vipInfo = WebUtils.JSON2Bean(jsonObject, VIPInfo.class);
            vipInfo.setModifier(user_id);
            vipInfo.setModified_date(new Date());
            vipService.update(vipInfo);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success !!! ");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表
     * 查找
     */
    @RequestMapping(value = "/find", method = RequestMethod.POST)
    @ResponseBody
    public String findVIP(HttpServletRequest request) {
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
            PageInfo<VIPInfo> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipService.selectBySearch(page_Size, page_Number, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipService.selectBySearch(page_Size, page_Number, corp_code, search_value);
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
     * 会员标签管理
     */
    @RequestMapping(value = "/label/list", method = RequestMethod.GET)
    @ResponseBody
    public String VIPLabelManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession(false).getAttribute("group_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(user_id, role_code, function_code, group_code);
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VIPtag> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                // list = vipService.selectBySearch(page_number, page_size, "", "");
                list = vipTagService.selectBySearch(page_number, page_size, "", "");
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipTagService.selectBySearch(page_number, page_size, corp_code, "");
            }
            result.put("list", list);
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员标签管理
     * 新增
     */
    @RequestMapping(value = "/label/add", method = RequestMethod.GET)
    @ResponseBody
    public String addVIPLabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_id = request.getSession(false).getAttribute("user_id").toString();
        try {
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String messsage = jsonObj.getString("message");
            org.json.JSONObject jsonObject = new org.json.JSONObject(messsage);
            VipCallbackRecord vipCallbackRecord = WebUtils.JSON2Bean(jsonObject, VipCallbackRecord.class);
            //Data now=new Date();
        } catch (Exception ex) {

        }

        return "viplabel_add";
    }

    /**
     * 会员标签管理
     * 编辑
     */
    @RequestMapping(value = "/label/edit", method = RequestMethod.GET)
    @ResponseBody
    public String editVIPLabel(HttpServletRequest request) {
        return "viplabel_edit";
    }

    /**
     * 会员标签管理
     * 查找
     */
    @RequestMapping(value = "/label/find", method = RequestMethod.GET)
    @ResponseBody
    public String findVIPLabel(HttpServletRequest request) {
        return "";
    }


    /**
     * 回访记录管理
     */
    @RequestMapping(value = "/callback/list", method = RequestMethod.GET)
    @ResponseBody
    public String callBackManage(HttpServletRequest request) {
        return "callback";
    }

    /**
     * 回访记录管理
     * 新增
     */
    @RequestMapping(value = "/callback/add", method = RequestMethod.GET)
    @ResponseBody
    public String addCallBack(HttpServletRequest request) {
        return "callback_add";
    }

    /**
     * 回访记录管理
     * 编辑
     */
    @RequestMapping(value = "/callback/edit", method = RequestMethod.GET)
    @ResponseBody
    public String editCallBack(HttpServletRequest request) {
        return "callback_edit";
    }

    /**
     * 回访记录管理
     * 查找
     */
    @RequestMapping(value = "/callback/find", method = RequestMethod.GET)
    @ResponseBody
    public String findCallBack(HttpServletRequest request) {
        return "";
    }


}
