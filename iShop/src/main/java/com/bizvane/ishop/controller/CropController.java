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
 * 企业管理
 */

@Controller
@RequestMapping("/crop")
public class CropController {

    /*
    * 列表
    * */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public String cropManage(HttpServletRequest request) {
        return "crop";
    }

    /*
   * 列表
   * */
    @RequestMapping(value = "/user",method = RequestMethod.GET)
    @ResponseBody
    public String cropUserManage(HttpServletRequest request) {
        return "crop_user";
    }

    /**
     * 新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @ResponseBody
    public String addCrop(HttpServletRequest request) {
        return "crop_add";
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editCrop(HttpServletRequest request) {
        return "crop_add";
    }

    /**
     * 查找
     */
    @RequestMapping(value = "/find",method = RequestMethod.GET)
    @ResponseBody
    public String findCrop(HttpServletRequest request) {
        return "";
    }


}
