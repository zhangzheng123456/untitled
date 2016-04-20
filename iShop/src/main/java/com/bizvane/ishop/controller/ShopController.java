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
 * 店铺管理
 */

@Controller
@RequestMapping("/shop")
public class ShopController {

    /**
     * 店铺管理
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public String shopManage(HttpServletRequest request) {
        return "shop_manage";
    }

    /**
     * 新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @ResponseBody
    public String addShop(HttpServletRequest request) {
        return "shop_manage";
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editShop(HttpServletRequest request) {
        return "shop_manage";
    }

    /**
     * 查找
     */
    @RequestMapping(value = "/find",method = RequestMethod.GET)
    @ResponseBody
    public String findShop(HttpServletRequest request) {
        return "shop_manage";
    }

}
