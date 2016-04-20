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
        return "VIP_list";
    }

    /**
     * 会员列表
     * 新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @ResponseBody
    public String addVIP(HttpServletRequest request) {
        return "VIP_list";
    }

    /**
     * 会员列表
     * 编辑
     */
    @RequestMapping(value = "/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editVIP(HttpServletRequest request) {
        return "VIP_list";
    }

    /**
     * 会员列表
     * 查找
     */
    @RequestMapping(value = "/find",method = RequestMethod.GET)
    @ResponseBody
    public String findVIP(HttpServletRequest request) {
        return "VIP_list";
    }

    /**
     * 会员标签管理
     */
    @RequestMapping(value = "/label_manage/list",method = RequestMethod.GET)
    @ResponseBody
    public String VIPLabelManage(HttpServletRequest request) {
        return "label_manage";
    }

    /**
     * 会员标签管理
     * 新增
     */
    @RequestMapping(value = "/label_manage/add",method = RequestMethod.GET)
    @ResponseBody
    public String addVIPLabel(HttpServletRequest request) {
        return "label_manage";
    }

    /**
     * 会员标签管理
     * 编辑
     */
    @RequestMapping(value = "/label_manage/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editVIPLabel(HttpServletRequest request) {
        return "label_manage";
    }

    /**
     * 会员标签管理
     * 查找
     */
    @RequestMapping(value = "/label_manage/find",method = RequestMethod.GET)
    @ResponseBody
    public String findVIPLabel(HttpServletRequest request) {
        return "label_manage";
    }

    /**
     * 回访记录管理
     */
    @RequestMapping(value = "/return_visit/list",method = RequestMethod.GET)
    @ResponseBody
    public String returnVisitManage(HttpServletRequest request) {
        return "return_visit_record";
    }

    /**
     * 回访记录管理
     * 新增
     */
    @RequestMapping(value = "/return_visit/add",method = RequestMethod.GET)
    @ResponseBody
    public String addReturnVisit(HttpServletRequest request) {
        return "return_visit_record";
    }

    /**
     * 回访记录管理
     * 编辑
     */
    @RequestMapping(value = "/return_visit/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editReturnVisit(HttpServletRequest request) {
        return "return_visit_record";
    }

    /**
     * 回访记录管理
     * 查找
     */
    @RequestMapping(value = "/return_visit/find",method = RequestMethod.GET)
    @ResponseBody
    public String findReturnVisit(HttpServletRequest request) {
        return "return_visit_record";
    }


}
