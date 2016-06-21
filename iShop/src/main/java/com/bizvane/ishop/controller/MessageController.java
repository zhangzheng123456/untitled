package com.bizvane.ishop.controller;

import com.bizvane.ishop.bean.DataBean;
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
@RequestMapping("/message")
public class MessageController {

    /**
     * 爱秀消息
     */
    @RequestMapping(value = "/ishop/list", method = RequestMethod.GET)
    @ResponseBody
    public String ishopManage(HttpServletRequest request) {


        return "iShop";
    }

    /**
     * 爱秀消息
     * 新增
     */
    @RequestMapping(value = "/ishop/add", method = RequestMethod.GET)
    @ResponseBody
    public String addIshop(HttpServletRequest request) {
        return "iShop_add";
    }

    /**
     * 爱秀消息
     * 编辑
     */
    @RequestMapping(value = "/ishop/edit", method = RequestMethod.GET)
    @ResponseBody
    public String editIshop(HttpServletRequest request) {
        return "iShop_edit";
    }

    /**
     * 爱秀消息
     * 查找
     */
    @RequestMapping(value = "/ishop/find", method = RequestMethod.GET)
    @ResponseBody
    public String findIshop(HttpServletRequest request) {
        return "";
    }


    /**
     * 手机短信
     */
    @RequestMapping(value = "/mobile/list", method = RequestMethod.GET)
    @ResponseBody
    public String mobileManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try{

        }catch (Exception ex){
            dataBean.setId(id);
        //    dataBean.
        }


        return "mobile";
    }

    /**
     * 手机短信
     * 新增
     */
    @RequestMapping(value = "/mobile/add", method = RequestMethod.GET)
    @ResponseBody
    public String addMobile(HttpServletRequest request) {
        return "mobile_add";
    }

    /**
     * 手机短信
     * 编辑
     */
    @RequestMapping(value = "/mobile/edit", method = RequestMethod.GET)
    @ResponseBody
    public String editMobile(HttpServletRequest request) {
        return "mobile_edit";
    }

    /**
     * 手机短信
     * 查找
     */
    @RequestMapping(value = "/mobile/find", method = RequestMethod.GET)
    @ResponseBody
    public String findMobile(HttpServletRequest request) {
        return "";
    }

}
