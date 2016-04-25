package com.bizvane.ishop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by maoweidong on 2016/2/15.
 */
/*
*用户及权限
*/
@Controller
@RequestMapping("/user")
public class RoleController {

    private static Logger logger = LoggerFactory.getLogger((RoleController.class));

    /**
     * 角色定义
     */
    @RequestMapping(value = "/role/list",method = RequestMethod.GET)
    @ResponseBody
    public String RoleManage(HttpServletRequest request) {
        return "role";
    }

    /**
     * 角色定义
     * 新增
     */
    @RequestMapping(value = "/role/add",method = RequestMethod.POST)
    @ResponseBody
    public String addRole(HttpServletRequest request) {
        return "role_add";
    }

    /**
     * 角色定义
     * 编辑
     */
    @RequestMapping(value = "/role/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editRole(HttpServletRequest request) {
        return "role_edit";
    }

    /**
     * 角色定义之
     * 编辑角色信息之
     * 查看名单
     */
    @RequestMapping(value = "/role/check_name",method = RequestMethod.GET)
    @ResponseBody
    public String roleCheckName(HttpServletRequest request) {
        return "rolecheck_name";
    }

    /**
     * 角色定义之
     * 编辑角色信息之
     * 查看权限
     */
    @RequestMapping(value = "/role/check_power",method = RequestMethod.GET)
    @ResponseBody
    public String roleCheckPower(HttpServletRequest request) {
        return "rolecheck_power";
    }

    /**
     * 角色定义之
     * 编辑角色信息之
     * 查看权限之
     * 编辑权限
     */
    @RequestMapping(value = "/role/check_power/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editRoleCheckPower(HttpServletRequest request) {
        return "rolecheck_power_edit";
    }

    /**
     * 角色定义之
     * 编辑角色信息之
     * 查看权限之
     * 新增权限
     */
    @RequestMapping(value = "/role/check_power/add",method = RequestMethod.GET)
    @ResponseBody
    public String addRoleCheckPower(HttpServletRequest request) {
        return "rolecheck_power_add";
    }


    /**
     * 角色定义
     * 查找
     */
    @RequestMapping(value = "/role/find",method = RequestMethod.POST)
    @ResponseBody
    public String findRole(HttpServletRequest request) {
        return "";
    }

}
