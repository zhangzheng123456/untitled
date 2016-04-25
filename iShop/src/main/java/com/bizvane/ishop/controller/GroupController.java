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
public class GroupController {

    private static Logger logger = LoggerFactory.getLogger((GroupController.class));

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
    @RequestMapping(value = "/group/add",method = RequestMethod.POST)
    @ResponseBody
    public String addGroup(HttpServletRequest request) {
        return "group_add";
    }

    /**
     * 群组管理
     * 编辑
     */
    @RequestMapping(value = "/group/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editGroup(HttpServletRequest request) {
        return "group_edit";
    }

    /**
     * 群组管理之
     * 编辑群组信息之
     * 查看名单
     */
    @RequestMapping(value = "/group/check_name",method = RequestMethod.POST)
    @ResponseBody
    public String groupCheckName(HttpServletRequest request) {
        return "groupcheck_name";
    }

    /**
     *  群组管理之
     *  编辑群组信息
     *  之查看权限
     */
    @RequestMapping(value = "/group/check_power",method = RequestMethod.POST)
    @ResponseBody
    public String groupCheckPower(HttpServletRequest request) {
        return "groupcheck_power";
    }

    /**
     * 群组管理之
     * 编辑群组信息之
     * 查看权限之
     * 新增权限
     */
    @RequestMapping(value = "/group/check_power/add",method = RequestMethod.POST)
    @ResponseBody
    public String addGroupCheckPower(HttpServletRequest request) {
        return "groupcheck_power_add";
    }

    /**
     * 群组管理之
     * 编辑群组信息之
     * 查看权限之
     * 编辑权限
     */
    @RequestMapping(value = "/group/check_power/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editGroupCheckPower(HttpServletRequest request) {
        return "groupcheck_power_edit";
    }



    /**
     * 群组管理
     * 查找
     */
    @RequestMapping(value = "/group/find",method = RequestMethod.POST)
    @ResponseBody
    public String findGroup(HttpServletRequest request) {
        return "";
    }


}
