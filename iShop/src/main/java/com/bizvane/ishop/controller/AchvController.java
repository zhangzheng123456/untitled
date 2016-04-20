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
    @RequestMapping(value = "/shop_goal/list",method = RequestMethod.GET)
    @ResponseBody
    public String shopGoalManage(HttpServletRequest request) {
        return "shop_goal";
    }

    /**
     * 店铺业绩目标
     * 新增
     */
    @RequestMapping(value = "/shop_goal/add",method = RequestMethod.GET)
    @ResponseBody
    public String addShopGoal(HttpServletRequest request) {
        return "shop_goal";
    }

    /**
     * 店铺业绩目标
     * 编辑
     */
    @RequestMapping(value = "/shop_goal/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editShopGoal(HttpServletRequest request) {
        return "shop_goal";
    }

    /**
     * 店铺业绩目标
     * 查找
     */
    @RequestMapping(value = "/shop_goal/find",method = RequestMethod.GET)
    @ResponseBody
    public String findShopGoal(HttpServletRequest request) {
        return "shop_goal";
    }




    /**
     * 员工业绩目标
     */
    @RequestMapping(value = "/staff_goal/list",method = RequestMethod.GET)
    @ResponseBody
    public String staffGoalManage(HttpServletRequest request) {
        return "staff_goal";
    }

    /**
     * 员工业绩目标
     * 新增
     */
    @RequestMapping(value = "/staff_goal/add",method = RequestMethod.GET)
    @ResponseBody
    public String addStaffGoal(HttpServletRequest request) {
        return "staff_goal";
    }


    /**
     * 员工业绩目标
     * 编辑
     */
    @RequestMapping(value = "/staff_goal/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editStaffGoal(HttpServletRequest request) {
        return "staff_goal";
    }

    /**
     * 员工业绩目标
     * 查找
     */
    @RequestMapping(value = "/shop_goal/find",method = RequestMethod.GET)
    @ResponseBody
    public String findStaffGoal(HttpServletRequest request) {
        return "shop_goal";
    }


}
