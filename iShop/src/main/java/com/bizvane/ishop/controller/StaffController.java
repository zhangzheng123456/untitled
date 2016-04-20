package com.bizvane.ishop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhouying on 2016-04-20.
 */


@Controller
@RequestMapping("/staff")
public class StaffController {

    /**
     * 员工列表
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public String staffManage(HttpServletRequest request) {

        return "staff_list";
    }

    /**
     * 员工列表
     * 新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @ResponseBody
    public String addStaff(HttpServletRequest request) {

        return "staff_list";
    }

    /**
     * 员工列表
     * 编辑
     */
    @RequestMapping(value = "/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editStaff(HttpServletRequest request) {

        return "staff_list";
    }

    /**
     * 员工列表
     * 查找
     */
    @RequestMapping(value = "/find",method = RequestMethod.GET)
    @ResponseBody
    public String findStaff(HttpServletRequest request) {

        return "staff_list";
    }

    /**
     * 签到管理
     */
    @RequestMapping(value = "/sign_in/list",method = RequestMethod.GET)
    @ResponseBody
    public String signInManage(HttpServletRequest request) {

        return "sign_in";
    }

    /**
     * 签到管理
     * 新增
     */
    @RequestMapping(value = "/sign_in/add",method = RequestMethod.GET)
    @ResponseBody
    public String addSignIn(HttpServletRequest request) {

        return "sign_in";
    }
    /**
     * 签到管理
     * 编辑
     */
    @RequestMapping(value = "/sign_in/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editSignIn(HttpServletRequest request) {

        return "sign_in";
    }
    /**
     * 签到管理
     * 查找
     */
    @RequestMapping(value = "/sign_in/find",method = RequestMethod.GET)
    @ResponseBody
    public String findSignIn(HttpServletRequest request) {

        return "sign_in";
    }

}
