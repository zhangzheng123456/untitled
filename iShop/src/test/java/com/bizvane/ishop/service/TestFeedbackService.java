package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ParamConfigureMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.imp.ParamConfigureServiceImpl;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
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
    @Autowired
    IceInterfaceService iceInterfaceService;
    String id;

    //成功
    @Test
    public void testselectAllFeedback() {
        try {
            //"[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
       //     String str = "aaa,bbb,ccc,dddd";
         //    System.out.println(WebUtils.StringFilter(str));
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
//            Pattern pattern = Pattern.compile("(^(http:\\/\\/)(.*?)(\\/(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$))");
//            String path="http://products-image.oss-cn-hangzhou.aliyuncs.com/yigu.jpg";
//            Matcher matcher = pattern.matcher(path);
//            if(matcher.matches()==false){
//                System.out.println("输入有误");
//            }else {
//                System.out.println("正确");
//            }
//            String aa=String.valueOf(null+"");
//            System.out.println(aa.length()+"--"+aa);
            String store_name = "";
            String area_code = "";
            String role_code =Common.ROLE_AM;
            if (role_code.equals(Common.ROLE_GM)){
                area_code = "";
            }else if(role_code.equals(Common.ROLE_AM)){
                    String code ="A0116,A0117,A0118,A0119,A0120,A0121";
                    String[] area_codes = code.replace(Common.STORE_HEAD,"").split(",");
                    area_code = area_codes[0];
            }
            Data data_user_id = new Data("user_id", "ABC123", ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", "C10141", ValueType.PARAM);
            Data data_time_id = new Data("time_id", "20160823", ValueType.PARAM);
            Data data_store_name = new Data("store_name", "", ValueType.PARAM);
            Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_time_id.key, data_time_id);
            datalist.put(data_store_name.key, data_store_name);
            datalist.put(data_area_code.key, data_area_code);

            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.ACHVStoreRanking",datalist);


            String result = dataBox.data.get("message").value;
            System.out.println(result+"--");
        } catch (Exception x) {
            x.printStackTrace();
        }

    }


}