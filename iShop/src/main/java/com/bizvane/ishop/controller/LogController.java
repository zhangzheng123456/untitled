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

        return "errorlog";
    }

    /**
     * 错误日志
     * 新增
     */
    @RequestMapping(value = "/error/add",method = RequestMethod.POST)
    @ResponseBody
    public String addErrorLog(HttpServletRequest request) {

        return "errorlog_add";
    }

    /**
     * 错误日志
     * 编辑
     */
    @RequestMapping(value = "/error/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editErrorLog(HttpServletRequest request) {

        return "errorlog_edit";
    }

    /**
     * 错误日志
     * 查找
     */
    @RequestMapping(value = "/error/find",method = RequestMethod.POST)
    @ResponseBody
    public String findErrorLog(HttpServletRequest request) {

        return "";
    }

    /**
     * 登录日志
     */
    @RequestMapping(value = "/login/list",method = RequestMethod.GET)
    @ResponseBody
    public String loginLogManage(HttpServletRequest request) {

        return "logging";
    }

    /**
     * 登录日志
     * 新增
     */
    @RequestMapping(value = "/login/add",method = RequestMethod.POST)
    @ResponseBody
    public String addLoginLog(HttpServletRequest request) {

        return "logging_add";
    }

    /**
     * 登录日志
     * 编辑
     */
    @RequestMapping(value = "/login/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editLoginLog(HttpServletRequest request) {

        return "logging_edit";
    }

    /**
     * 登录日志
     * 查找
     */
    @RequestMapping(value = "/login/find",method = RequestMethod.POST)
    @ResponseBody
    public String findLoginLog(HttpServletRequest request) {

        return "";
    }


    /**
     * 验证码管理
     */
    @RequestMapping(value = "/authcode/list",method = RequestMethod.GET)
    @ResponseBody
    public String authCodeManage(HttpServletRequest request) {

        return "authcode";
    }

    /**
     * 验证码管理
     * 新增
     */
    @RequestMapping(value = "/authcode/add",method = RequestMethod.POST)
    @ResponseBody
    public String addAuthCode(HttpServletRequest request) {

        return "authcode_add";
    }

    /**
     * 验证码管理
     * 编辑
     */
    @RequestMapping(value = "/authcode/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editAuthCode(HttpServletRequest request) {

        return "authcode_edit";
    }

    /**
     * 验证码管理
     * 查找
     */
    @RequestMapping(value = "/authcode/find",method = RequestMethod.POST)
    @ResponseBody
    public String findAuthCode(HttpServletRequest request) {

        return "";
    }

    /**
     * 验证码管理
     * 删除
     */
    @RequestMapping(value = "/authcode/delete",method = RequestMethod.POST)
    @ResponseBody
    public String deleteAuthCode(HttpServletRequest request) {

        return "";
    }
}
