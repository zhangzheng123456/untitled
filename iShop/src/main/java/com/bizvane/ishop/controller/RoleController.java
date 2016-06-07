package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Role;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.RoleService;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.omg.CORBA.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.joda.DateTimeFormatterFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by maoweidong on 2016/2/15.
 */
/*
*用户及权限
*/
@Controller
@RequestMapping("/user")
public class RoleController {

    @Autowired
    private FunctionService functionService;

    @Autowired
    private RoleService roleService;

    private static Logger logger = LoggerFactory.getLogger((RoleController.class));

    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);

    /**
     * 角色定义
     */
    @RequestMapping(value = "/role/list", method = RequestMethod.GET)
    @ResponseBody
    public String RoleManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(user_id, role_code, function_code,group_code);
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<Role> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                result.put("actions", actions);
                list = roleService.selectAllRole(page_number, page_size, "");
                result.put("list", JSON.toJSONString(list));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result.toString());
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 角色定义
     * 新增
     */
    @RequestMapping(value = "/role/add", method = RequestMethod.POST)
    @ResponseBody
    public String addRole(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String user_id = request.getSession(false).getAttribute("user_id").toString();
           // String jsString = request.getSession(false).getAttribute("param").toString();
            String jsString=request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            Role role = WebUtils.JSON2Bean(jsonObject, Role.class);
            roleService.insertRole(role);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("add role success !!!!");
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/role/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteRole(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String[] role_ids = jsonObject.get("id").toString().split(",");
            for (int i = 0; role_ids != null && i < role_ids.length; i++) {
                roleService.deleteByRoleId(Integer.parseInt(role_ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success !!!!!");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            ;
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

//    @RequestMapping(value = "/role/select",method = RequestMethod.POST)
//    @ResponseBody

    /**
     * 角色定义
     * 编辑
     */
    @RequestMapping(value = "/role/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editRole(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_id = request.getSession(false).toString();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            Role role = WebUtils.JSON2Bean(jsonObject, Role.class);
            roleService.updateByRoleId(role);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("edit success !!! ");
        } catch (Exception ex) {
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 角色定义之
     * 编辑角色信息之
     * 查看名单
     */
//    @RequestMapping(value = "/role/check_name", method = RequestMethod.GET)
//    @ResponseBody
//    public String roleCheckName(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            int role_id = Integer.parseInt(jsonObj.get("role_id").toString());
//            Role role = roleService.selectByRoleId(role_id);
//            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
//            result.put("role", role);
//
//            dataBean.setId("1");
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setId("1");
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 角色定义之
     * 编辑角色信息之
     * 查看权限
     */
//    @RequestMapping(value = "/role/check_power", method = RequestMethod.GET)
//    @ResponseBody
//    public String roleCheckPower(HttpServletRequest request) {    return "rolecheck_power";
//    }

    /**
     * 角色定义之
     * 编辑角色信息之
     * 查看权限之
     * 编辑权限
     */
//    @RequestMapping(value = "/role/check_power/edit", method = RequestMethod.GET)
//    @ResponseBody
//    public String editRoleCheckPower(HttpServletRequest request) {
//        return "rolecheck_power_edit";
//    }

    /**
     * 角色定义之
     * 编辑角色信息之
     * 查看权限之
     * 新增权限
     */
//    @RequestMapping(value = "/role/check_power/add", method = RequestMethod.GET)
//    @ResponseBody
//    public String addRoleCheckPower(HttpServletRequest request) {
//        return "rolecheck_power_add";
//    }


    /**
     * 角色定义
     * 查找
     */
//    @RequestMapping(value = "/role/find", method = RequestMethod.POST)
//    @ResponseBody
//    public String findRole(HttpServletRequest request) {
//        return "";
//    }

    @RequestMapping(value = "/role/select", method = RequestMethod.POST)
    @ResponseBody
    public String editbefore(HttpServletRequest request) {
        DataBean dataBean = new DataBean();      String id = "";
        try {

            String user_id = request.getSession(false).getAttribute("user_id").toString();
            String jsString = request.getParameter("param");

            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int role_id = Integer.parseInt(jsonObject.get("id").toString());
            Role role = roleService.selectByRoleId(role_id);
            JSONObject result = new JSONObject();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(JSON.toJSONString(role));

        } catch (SQLException e) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

}
