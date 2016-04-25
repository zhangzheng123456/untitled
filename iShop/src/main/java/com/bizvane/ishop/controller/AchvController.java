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
@RequestMapping("/achv")
public class AchvController {


    /**
     * 店铺业绩目标
     */
    @RequestMapping(value = "/shopgoal/list",method = RequestMethod.GET)
    @ResponseBody
    public String shopGoalManage(HttpServletRequest request) {
        return "shopgoal";
    }

    /**
     * 店铺业绩目标
     * 新增
     */
    @RequestMapping(value = "/shopgoal/add",method = RequestMethod.POST)
    @ResponseBody
    public String addShopGoal(HttpServletRequest request) {
        return "shopgoal_add";
    }

    /**
     * 店铺业绩目标
     * 编辑
     */
    @RequestMapping(value = "/shopgoal/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editShopGoal(HttpServletRequest request) {
        return "shopgoal_edit";
    }

    /**
     * 店铺业绩目标
     * 查找
     */
    @RequestMapping(value = "/shopgoal/find",method = RequestMethod.POST)
    @ResponseBody
    public String findShopGoal(HttpServletRequest request) {
        return "";
    }




    /**
     * 员工业绩目标
     */
    @RequestMapping(value = "/staffgoal/list",method = RequestMethod.GET)
    @ResponseBody
    public String staffGoalManage(HttpServletRequest request) {
        return "staffgoal";
    }

    /**
     * 员工业绩目标
     * 新增
     */
    @RequestMapping(value = "/staffgoal/add",method = RequestMethod.POST)
    @ResponseBody
    public String addStaffGoal(HttpServletRequest request) {
        return "staffgoal_add";
    }


    /**
     * 员工业绩目标
     * 编辑
     */
    @RequestMapping(value = "/staffgoal/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editStaffGoal(HttpServletRequest request) {
        return "staffgoal_edit";
    }

    /**
     * 员工业绩目标
     * 查找
     */
    @RequestMapping(value = "/staffgoal/find",method = RequestMethod.POST)
    @ResponseBody
    public String findStaffGoal(HttpServletRequest request) {

        return "";
    }


}
