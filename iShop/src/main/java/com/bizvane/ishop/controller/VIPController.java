package com.bizvane.ishop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/VIP")
public class VIPController {

    /**
     * 会员列表
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public String VIPManage(HttpServletRequest request) {
        return "vip";
    }

    /**
     * 会员列表
     * 新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addVIP(HttpServletRequest request) {
        return "vip_add";
    }

    /**
     * 会员列表
     * 编辑
     */
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editVIP(HttpServletRequest request) {
        return "vip_edit";
    }

    /**
     * 会员列表
     * 查找
     */
    @RequestMapping(value = "/find",method = RequestMethod.POST)
    @ResponseBody
    public String findVIP(HttpServletRequest request) {
        return "";
    }


    /**
     * 会员标签管理
     */
    @RequestMapping(value = "/label/list",method = RequestMethod.GET)
    @ResponseBody
    public String VIPLabelManage(HttpServletRequest request) {
        return "viplabel";
    }

    /**
     * 会员标签管理
     * 新增
     */
    @RequestMapping(value = "/label/add",method = RequestMethod.GET)
    @ResponseBody
    public String addVIPLabel(HttpServletRequest request) {
        return "viplabel_add";
    }

    /**
     * 会员标签管理
     * 编辑
     */
    @RequestMapping(value = "/label/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editVIPLabel(HttpServletRequest request) {
        return "viplabel_edit";
    }

    /**
     * 会员标签管理
     * 查找
     */
    @RequestMapping(value = "/label/find",method = RequestMethod.GET)
    @ResponseBody
    public String findVIPLabel(HttpServletRequest request) {
        return "";
    }



    /**
     * 回访记录管理
     */
    @RequestMapping(value = "/callback/list",method = RequestMethod.GET)
    @ResponseBody
    public String callBackManage(HttpServletRequest request) {
        return "callback";
    }

    /**
     * 回访记录管理
     * 新增
     */
    @RequestMapping(value = "/callback/add",method = RequestMethod.GET)
    @ResponseBody
    public String addCallBack(HttpServletRequest request) {
        return "callback_add";
    }

    /**
     * 回访记录管理
     * 编辑
     */
    @RequestMapping(value = "/callback/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editCallBack(HttpServletRequest request) {
        return "callback_edit";
    }

    /**
     * 回访记录管理
     * 查找
     */
    @RequestMapping(value = "/callback/find",method = RequestMethod.GET)
    @ResponseBody
    public String findCallBack(HttpServletRequest request) {
        return "";
    }


}
