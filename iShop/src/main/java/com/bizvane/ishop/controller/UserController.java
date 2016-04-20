package com.bizvane.ishop.controller;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by maoweidong on 2016/2/15.
 */

@Controller
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger((UserController.class));

    /**
     * 角色定义
     */
    @RequestMapping(value = "/role_define/list",method = RequestMethod.GET)
    @ResponseBody
    public String userDefineManage(HttpServletRequest request) {
        return "user_define";
    }

    /**
     * 角色定义
     * 新增
     */
    @RequestMapping(value = "/role_define/add",method = RequestMethod.GET)
    @ResponseBody
    public String addUserDefine(HttpServletRequest request) {
        return "user_define";
    }

    /**
     * 角色定义
     * 编辑
     */
    @RequestMapping(value = "/role_define/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editUserDefine(HttpServletRequest request) {
        return "user_define";
    }

    /**
     * 角色定义
     * 查找
     */
    @RequestMapping(value = "/role_define/find",method = RequestMethod.GET)
    @ResponseBody
    public String findUserDefine(HttpServletRequest request) {
        return "user_define";
    }

    /**
     * 群组管理
     */
    @RequestMapping(value = "/group/list",method = RequestMethod.GET)
    @ResponseBody
    public String groupManage(HttpServletRequest request) {
        return "group";
    }

    /**
     * 群组管理
     * 新增
     */
    @RequestMapping(value = "/group/add",method = RequestMethod.GET)
    @ResponseBody
    public String addGroup(HttpServletRequest request) {
        return "group";
    }

    /**
     * 群组管理
     * 编辑
     */
    @RequestMapping(value = "/group/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editGroup(HttpServletRequest request) {
        return "group";
    }

    /**
     * 群组管理
     * 查找
     */
    @RequestMapping(value = "/group/find",method = RequestMethod.GET)
    @ResponseBody
    public String findGroup(HttpServletRequest request) {
        return "group";
    }

    /**
     * 用户管理
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public String userManage(HttpServletRequest request) {
        return "user_manage";
    }

    /**
     * 用户管理
     * 新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @ResponseBody
    public String addUser(HttpServletRequest request) {
        return "user_manage";
    }

    /**
     * 用户管理
     * 编辑
     */
    @RequestMapping(value = "/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editUser(HttpServletRequest request) {
        return "user_manage";
    }

    /**
     * 用户管理
     * 查找
     */
    @RequestMapping(value = "/find",method = RequestMethod.GET)
    @ResponseBody
    public String findUser(HttpServletRequest request) {
        return "user_manage";
    }


}
