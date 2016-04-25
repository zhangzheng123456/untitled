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

        return "staff";
    }

    /**
     * 员工列表
     * 新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @ResponseBody
    public String addStaff(HttpServletRequest request) {

        return "staff_add";
    }

    /**
     * 员工列表
     * 编辑
     */
    @RequestMapping(value = "/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editStaff(HttpServletRequest request) {

        return "staff_edit";
    }


    /**
     * 员工列表
     * 查找
     */
    @RequestMapping(value = "/find",method = RequestMethod.GET)
    @ResponseBody
    public String findStaff(HttpServletRequest request) {

        return "";
    }

    /**
     * 签到管理
     */
    @RequestMapping(value = "/checkin/list",method = RequestMethod.GET)
    @ResponseBody
    public String checkInManage(HttpServletRequest request) {

        return "checkin";
    }

    /**
     * 签到管理
     * 新增
     */
    @RequestMapping(value = "/checkin/add",method = RequestMethod.GET)
    @ResponseBody
    public String addCheckIn(HttpServletRequest request) {

        return "checkin_add";
    }
    /**
     * 签到管理
     * 编辑
     */
    @RequestMapping(value = "/checkin/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editCheckIn(HttpServletRequest request) {

        return "checkin_edit";
    }
    /**
     * 签到管理
     * 查找
     */
    @RequestMapping(value = "/checkin/find",method = RequestMethod.GET)
    @ResponseBody
    public String findCheckIn(HttpServletRequest request) {

        return "";
    }

}
