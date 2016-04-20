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
    @RequestMapping(value = "/train/list",method = RequestMethod.GET)
    @ResponseBody
    public String goodsTrainManage(HttpServletRequest request) {
        return "goods_train";
    }

    /**
     * 商品培训
     * 添加
     */
    @RequestMapping(value = "/train/add",method = RequestMethod.GET)
    @ResponseBody
    public String addGoodsTrain(HttpServletRequest request) {
        return "goods_train";
    }

    /**
     * 商品培训
     * 编辑
     */
    @RequestMapping(value = "/train/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editGoodsTrain(HttpServletRequest request) {
        return "goods_train";
    }

    /**
     * 商品培训
     * 查找
     */
    @RequestMapping(value = "/train/find",method = RequestMethod.GET)
    @ResponseBody
    public String findGoodsTrain(HttpServletRequest request) {
        return "goods_train";
    }


    /**
     * 秀搭管理
     */
    @RequestMapping(value = "/show_match/list",method = RequestMethod.GET)
    @ResponseBody
    public String showMatchManage(HttpServletRequest request) {
        return "show_match";
    }


}
