package com.bizvane.ishop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouying on 2016-04-20.
 */

/**
 * 日志管理
 */

@Controller
@RequestMapping("/log")
public class LogController {

    /**
     * 错误日志
     */
    @RequestMapping(value = "/error/list",method = RequestMethod.GET)
    @ResponseBody
    public String errorLogManage(HttpServletRequest request) {

        return "error_log";
    }

    /**
     * 错误日志
     * 新增
     */
    @RequestMapping(value = "/error/add",method = RequestMethod.GET)
    @ResponseBody
    public String addErrorLog(HttpServletRequest request) {

        return "error_log";
    }

    /**
     * 错误日志
     * 编辑
     */
    @RequestMapping(value = "/error/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editErrorLog(HttpServletRequest request) {

        return "error_log";
    }

    /**
     * 错误日志
     * 查找
     */
    @RequestMapping(value = "/error/find",method = RequestMethod.GET)
    @ResponseBody
    public String findErrorLog(HttpServletRequest request) {

        return "error_log";
    }

    /**
     * 登录日志
     */
    @RequestMapping(value = "/login/list",method = RequestMethod.GET)
    @ResponseBody
    public String loginLogManage(HttpServletRequest request) {

        return "login_log";
    }

    /**
     * 登录日志
     * 新增
     */
    @RequestMapping(value = "/login/add",method = RequestMethod.GET)
    @ResponseBody
    public String addLoginLog(HttpServletRequest request) {

        return "login_log";
    }

    /**
     * 登录日志
     * 编辑
     */
    @RequestMapping(value = "/login/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editLoginLog(HttpServletRequest request) {

        return "login_log";
    }

    /**
     * 登录日志
     * 查找
     */
    @RequestMapping(value = "/login/find",method = RequestMethod.GET)
    @ResponseBody
    public String findLoginLog(HttpServletRequest request) {

        return "login_log";
    }

    /**
     * 验证码管理
     */
    @RequestMapping(value = "/code/list",method = RequestMethod.GET)
    @ResponseBody
    public String codeManage(HttpServletRequest request) {

        return "code_manage";
    }

    /**
     * 验证码管理
     * 新增
     */
    @RequestMapping(value = "/code/add",method = RequestMethod.GET)
    @ResponseBody
    public String addCode(HttpServletRequest request) {

        return "code_manage";
    }

    /**
     * 验证码管理
     * 编辑
     */
    @RequestMapping(value = "/code/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editCode(HttpServletRequest request) {

        return "code_manage";
    }

    /**
     * 验证码管理
     * 查找
     */
    @RequestMapping(value = "/code/find",method = RequestMethod.GET)
    @ResponseBody
    public String findCode(HttpServletRequest request) {

        return "code_manage";
    }
}
