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

/**
 * 系统管理
 */
@Controller
@RequestMapping("/system")
public class SystemController {

    /**
     * 用户反馈
     */
    @RequestMapping(value = "/feedback/list",method = RequestMethod.GET)
    @ResponseBody
    public String feedbackManage(HttpServletRequest request) {
        return "feedback";
    }

    /**
     * 用户反馈
     * 新增
     */
    @RequestMapping(value = "/feedback/add",method = RequestMethod.GET)
    @ResponseBody
    public String addFeedback(HttpServletRequest request) {
        return "feedback_add";
    }

    /**
     * 用户反馈
     * 编辑
     */
    @RequestMapping(value = "/feedback/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editFeedback(HttpServletRequest request) {
        return "feedback_edit";
    }

    /**
     * 用户反馈
     * 查找
     */
    @RequestMapping(value = "/feedback/find",method = RequestMethod.GET)
    @ResponseBody
    public String findFeedback(HttpServletRequest request) {
        return "";
    }



    /**
     * APP版本控制
     */
    @RequestMapping(value = "/APPvesion/list",method = RequestMethod.GET)
    @ResponseBody
    public String APPVesionManage(HttpServletRequest request) {
        return "appversion";
    }

    /**
     * APP版本控制
     * 新增
     */
    @RequestMapping(value = "/APPvesion/add",method = RequestMethod.GET)
    @ResponseBody
    public String addAPPVesion(HttpServletRequest request) {
        return "appversion_add";
    }

    /**
     * APP版本控制
     * 编辑
     */
    @RequestMapping(value = "/APPvesion/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editAPPVesion(HttpServletRequest request) {
        return "appversion_edit";
    }

    /**
     * APP版本控制
     * 查找
     */
    @RequestMapping(value = "/APPvesion/find",method = RequestMethod.GET)
    @ResponseBody
    public String findAPPVesion(HttpServletRequest request) {
        return "";
    }


    /**
     * 缓存管理
     */
    @RequestMapping(value = "/cache/list",method = RequestMethod.GET)
    @ResponseBody
    public String cacheManage(HttpServletRequest request) {
        return "cache";
    }

    /**
     * 缓存管理
     * 新增
     */
    @RequestMapping(value = "/cache/add",method = RequestMethod.GET)
    @ResponseBody
    public String addCache(HttpServletRequest request) {
        return "cache_add";
    }

    /**
     * 缓存管理
     * 编辑
     */
    @RequestMapping(value = "/cache/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editCache(HttpServletRequest request) {
        return "cache_edit";
    }

    /**
     * 缓存管理
     * 查找
     */
    @RequestMapping(value = "/cache/find",method = RequestMethod.GET)
    @ResponseBody
    public String findCache(HttpServletRequest request) {
        return "";
    }


    /**
     * 接口管理
     */
    @RequestMapping(value = "/interface/list",method = RequestMethod.GET)
    @ResponseBody
    public String interfaceManage(HttpServletRequest request) {
        return "interface";
    }

    /**
     * 接口管理
     * 新增
     */
    @RequestMapping(value = "/interface/add",method = RequestMethod.GET)
    @ResponseBody
    public String addInterface(HttpServletRequest request) {
        return "interface_add";
    }

    /**
     * 接口管理
     * 编辑
     */
    @RequestMapping(value = "/interface/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editnterface(HttpServletRequest request) {
        return "interface_edit";
    }

    /**
     * 接口管理
     * 查找
     */
    @RequestMapping(value = "/interface/find",method = RequestMethod.GET)
    @ResponseBody
    public String findInterface(HttpServletRequest request) {
        return "";
    }

}
