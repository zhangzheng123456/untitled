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
@RequestMapping("/goods")
public class GoodsController {

    /**
     * 商品培训
     */
    @RequestMapping(value = "/fab/list",method = RequestMethod.GET)
    @ResponseBody
    public String goodsTrainManage(HttpServletRequest request) {
        return "fab";
    }

    /**
     * 商品培训
     * 添加
     */
    @RequestMapping(value = "/fab/add",method = RequestMethod.POST)
    @ResponseBody
    public String addGoodsTrain(HttpServletRequest request) {
        return "fab_add";
    }

    /**
     * 商品培训
     * 编辑
     */
    @RequestMapping(value = "/fab/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editGoodsTrain(HttpServletRequest request) {
        return "fab_edit";
    }

    /**
     * 商品培训
     * 查找
     */
    @RequestMapping(value = "/fab/find",method = RequestMethod.POST)
    @ResponseBody
    public String findGoodsTrain(HttpServletRequest request) {
        return "";
    }

    /**
     * 商品培训
     * 删除
     */
    @RequestMapping(value = "/fab/delete",method = RequestMethod.POST)
    @ResponseBody
    public String deleteGoodsTrain(HttpServletRequest request) {
        return "";
    }


    /**
     * 秀搭管理
     */
    @RequestMapping(value = "/xiuda/list",method = RequestMethod.GET)
    @ResponseBody
    public String showMatchManage(HttpServletRequest request) {
        return "xiuda";
    }


}
