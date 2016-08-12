package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ParamConfigureMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.imp.ParamConfigureServiceImpl;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.lang.System;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yin on 2016/6/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:spring-mybatis.xml"})
public class TestFeedbackService {
    @Autowired
    private FeedbackService feedbackService = null;
    @Autowired
    private AppversionService appversionService = null;
    @Autowired
    private InterfaceService interfaceService = null;
    @Autowired
    private ValidateCodeService validateCodeService = null;
    @Autowired
    private GroupService groupService = null;
    @Autowired
    private TaskService taskService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;
    @Autowired
    private ParamConfigureService paramConfigureService;
    String id;

    //成功
    @Test
    public void testselectAllFeedback() {
        try {
            //"[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
            String str = "aaa,bbb,ccc,dddd";
            //  System.out.println(WebUtils.StringFilter(str));
//            Task task=new Task();
//            task.setTask_code("T201608051153111446");
//            task.setTask_title("顺哥DiuDiu~");
//            task.setTask_type_code("T0001");
//            task.setTask_description("加班加班Gogogogo");
//            task.setCorp_code("C00001");
//            task.setId(26);
//            String[] user_codes={"9999","008"};
//            String user_code="";
//            String result = taskService.updTask(task, user_codes, user_code);
//            System.out.println("-------"+result);
        } catch (Exception x) {
            x.printStackTrace();
        }

    }

    @Test
    public void testList() throws SQLException {

        try {
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<ParamConfigure> list = null;
            list = paramConfigureService.getAllParamByPage(1, 1, "");
            result.put("list", JSON.toJSONString(list));

            System.out.println("=======success========" + result.toString());
        } catch (Exception ex) {
            System.out.println("=======erroe====");
        }


    }

    @Test
    public void testInsert() throws Exception {
        try {
            JSONObject obj = new JSONObject();
            obj.put("param_key", "fifth");
            obj.put("param_name", "china");
            obj.put("param_value", "No1");
            obj.put("remark", "happy");

            String message = obj.toString();
            System.out.println("======" + message);
            String result = paramConfigureService.insert(message);
            System.out.println("======success");
        } catch (Exception ex) {
            System.out.println("======error");
        }
    }

    @Test
    public void testUpdate() throws Exception {
        try {
            JSONObject obj = new JSONObject();
            obj.put("param_key", "fifth1");
            obj.put("param_name", "china1");
            obj.put("param_value", "No11");
            obj.put("remark", "happyhappy");
            obj.put("id", "20");
            int id = 21;
            String message = obj.toString();
            System.out.println("======" + message);
            String result = paramConfigureService.update(message);
            System.out.println("======success");
        } catch (Exception ex) {
            System.out.println("======error");
        }


    }

    @Test
    public void testDelete() throws Exception {

        paramConfigureService.delete(19);
        System.out.println("success========");

    }

    @Test
    public void testSearch() throws SQLException{
        try {

            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<ParamConfigure> list = null;
            list = paramConfigureService.selectByParamSearch(1, 2, "", "li");
            result.put("list", JSON.toJSONString(list));
            System.out.println("=======success========" + result.toString());
        } catch (Exception ex) {
            System.out.println("=======erroe====");
        }

    }

}