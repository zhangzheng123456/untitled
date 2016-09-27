package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ParamConfigureMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.imp.ParamConfigureServiceImpl;
import com.bizvane.ishop.utils.LuploadHelper;
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
    @Autowired
    private VipParamService vipParamService;
    @Autowired
    private AreaService areaService;
    String id;

    //成功
    @Test
    public void testselectAllFeedback() {
        try {


            LuploadHelper.deleteDirectory("E:\\Test");
//            List<String> list = new ArrayList<String>();
//            list.add("保护环境");       //向列表中添加数据
//            list.add("爱护地球");        //向列表中添加数据
//            list.add("从我做起");       //向列表中添加数据
//            list.add("从我ss起");       //向列表中添加数据
//            List<String> list1 = new ArrayList<String>();
//            list1.add("保护环境");        //向列表中添加数据
//            list1.add("爱护地球");       //向列表中添加数据
//            list1.add("cccccc");       //向列表中添加数据
//            boolean ret = list.remove(list1);    //从list中移除与list1相同的元素
//            Iterator<String> it = list.iterator();   //创建迭代器
//            while (it.hasNext()) {       //循环遍历迭代器
//                System.out.println(it.next());    //输出集合中元素
//            }



//            VipParam vipParam=new VipParam();
//            vipParam.setId(1);
//            vipParam.setParam_name("3");
//            vipParam.setParam_type("2");
//            vipParam.setParam_values("2");
//            vipParam.setCorp_code("C10000");
//            vipParam.setRemark("2");
//            vipParam.setParam_desc("2");
//            vipParam.setModified_date("2");
//            vipParam.setModifier("2");
//            vipParam.setCreated_date("2");
//            vipParam.setCreater("2");
//            vipParam.setIsactive("2");
//            String insert = vipParamService.update(vipParam);
//            System.out.println("------"+insert);

//            PageInfo<VipParam> vipParamPageInfo = vipParamService.selectAllParam(1, 20, "", "");
//            List<VipParam> list = vipParamPageInfo.getList();
//            for (VipParam vipParam:list
//                 ) {
//                System.out.println(vipParam.getCorp_name()+"---"+vipParam.getParam_name());
//            }
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


//======================================导购排行===================================================
//
//            String store_name = "";
//            String area_code = "";
//            String role_code =Common.ROLE_AM;
//            if (role_code.equals(Common.ROLE_GM)){
//                area_code = "";
//            }else if(role_code.equals(Common.ROLE_AM)){
//                    String code ="A0116,A0117,A0118,A0119,A0120,A0121";
//                    String[] area_codes = code.replace(Common.STORE_HEAD,"").split(",");
//                    area_code = area_codes[0];
//            }
//            Data data_user_id = new Data("user_id", "ABC123", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10141", ValueType.PARAM);
//            Data data_time_id = new Data("time_id", "20160823", ValueType.PARAM);
//            Data data_store_name = new Data("store_name", "", ValueType.PARAM);
//            Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);
//
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(data_time_id.key, data_time_id);
//            datalist.put(data_store_name.key, data_store_name);
//            datalist.put(data_area_code.key, data_area_code);
//
//            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.ACHVStoreRanking",datalist);
//

//            String result = dataBox.data.get("message").value;
//            System.out.println(result+"--");
//
//            String ss="§SHBS0001,";
//        String[] store_ids = ss.replace(Common.STORE_HEAD,"").split(",");
//            Data data_user_id = new Data("user_id", "AZ0007841", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10016", ValueType.PARAM);
//            Data data_time_id = new Data("time_id", "20160707", ValueType.PARAM);
//     //       Data data_store_code = new Data("store_code", "902116101", ValueType.PARAM);
//            Data data_store_id = new Data("store_id", store_ids[0], ValueType.PARAM);
//           // 751400901
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(data_time_id.key, data_time_id);
//            datalist.put(data_store_id.key, data_store_id);
//       //     datalist.put(data_store_code.key, data_store_code);
//            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.ACHVStaffRanking",datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);





//======================================活跃会员===================================================

//            String ss="§SHBS0001,";
//            String[] store_ids = ss.replace(Common.STORE_HEAD,"").split(",");
//            Data data_user_id = new Data("user_id", "AZ0007841", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10016", ValueType.PARAM);
//            Data role_code = new Data("role_code", "R3000", ValueType.PARAM);
//            Data page_num = new Data("page_num", "1", ValueType.PARAM);
//            Data area_code = new Data("area_code", "", ValueType.PARAM);
//            Data page_size = new Data("page_size", "10", ValueType.PARAM);
//            Data query_type = new Data("query_type", "0", ValueType.PARAM);
//            Data data_store_id = new Data("store_id", store_ids[0], ValueType.PARAM);
//            // 751400901
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(role_code.key, role_code);
//            datalist.put(page_num.key, page_num);
//            datalist.put(area_code.key, area_code);
//            datalist.put(page_size.key, page_size);
//            datalist.put(query_type.key, query_type);
//            datalist.put(data_store_id.key, data_store_id);
//            //     datalist.put(data_store_code.key, data_store_code);
//            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.AnalysisSleep",datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);





//
//            String ss="§SHBS0001,";
//            String[] store_ids = ss.replace(Common.STORE_HEAD,"").split(",");
//            Data data_user_id = new Data("user_id", "ABC123", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10141", ValueType.PARAM);
//            Data role_code = new Data("role_code", "R4000", ValueType.PARAM);
//            Data page_num = new Data("page_num", "1", ValueType.PARAM);
//            Data area_code = new Data("area_code", "A0116", ValueType.PARAM);
//            Data page_size = new Data("page_size", "100", ValueType.PARAM);
//            Data query_type = new Data("query_type", "0", ValueType.PARAM);
//            Data data_store_id = new Data("store_id", "", ValueType.PARAM);
//            // 751400901
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(role_code.key, role_code);
//            datalist.put(page_num.key, page_num);
//            datalist.put(area_code.key, area_code);
//            datalist.put(page_size.key, page_size);
//            datalist.put(query_type.key, query_type);
//            datalist.put(data_store_id.key, data_store_id);
//            //     datalist.put(data_store_code.key, data_store_code);
//            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.AnalysisSleep",datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);

//======================================消费排行===================================================

//
//            String ss="§SHBS0001,";
//            String[] store_ids = ss.replace(Common.STORE_HEAD,"").split(",");
//            Data data_user_id = new Data("user_id", "ABC123", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10141", ValueType.PARAM);
//            Data role_code = new Data("role_code", "R4000", ValueType.PARAM);
//            Data page_num = new Data("page_num", "1", ValueType.PARAM);
//            Data area_code = new Data("area_code", "A0116", ValueType.PARAM);
//            Data page_size = new Data("page_size", "10", ValueType.PARAM);
//            Data query_type = new Data("query_type", "1", ValueType.PARAM);
//            Data data_store_id = new Data("store_id", "", ValueType.PARAM);
//            // 751400901
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(role_code.key, role_code);
//            datalist.put(page_num.key, page_num);
//            datalist.put(area_code.key, area_code);
//            datalist.put(page_size.key, page_size);
//            datalist.put(query_type.key, query_type);
//            datalist.put(data_store_id.key, data_store_id);
//            //     datalist.put(data_store_code.key, data_store_code);
//            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.AnlysisVipAmount",datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);



//            String ss="§SHBS0001,";
//            String[] store_ids = ss.replace(Common.STORE_HEAD,"").split(",");
//            Data data_user_id = new Data("user_id", "ABC123", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10141", ValueType.PARAM);
//            Data role_code = new Data("role_code", "R4000", ValueType.PARAM);
//            Data page_num = new Data("page_num", "1", ValueType.PARAM);
//            Data area_code = new Data("area_code", "A0116", ValueType.PARAM);
//            Data page_size = new Data("page_size", "10", ValueType.PARAM);
//            Data data_store_id = new Data("store_id", "", ValueType.PARAM);
//            // 751400901
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(role_code.key, role_code);
//            datalist.put(page_num.key, page_num);
//            datalist.put(area_code.key, area_code);
//            datalist.put(page_size.key, page_size);
//            datalist.put(data_store_id.key, data_store_id);
//            //     datalist.put(data_store_code.key, data_store_code);
//            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.AnalysisVipFreq",datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);
//=========================================================================================
//            String ss="§SHBS0001,";
//            String[] store_ids = ss.replace(Common.STORE_HEAD,"").split(",");
//            Data data_user_id = new Data("user_id", "ABC123", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10141", ValueType.PARAM);
//            Data role_code = new Data("role_code", "R4000", ValueType.PARAM);
//            Data page_num = new Data("page_num", "1", ValueType.PARAM);
//            Data area_code = new Data("area_code", "A0116", ValueType.PARAM);
//            Data page_size = new Data("page_size", "100", ValueType.PARAM);
//            Data data_store_id = new Data("store_id", "", ValueType.PARAM);
//            // 751400901
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(role_code.key, role_code);
//            datalist.put(page_num.key, page_num);
//            datalist.put(area_code.key, area_code);
//            datalist.put(page_size.key, page_size);
//            datalist.put(data_store_id.key, data_store_id);
//            //     datalist.put(data_store_code.key, data_store_code);
//            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.VipDetailQuery",datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);









//
//            Data data_user_id = new Data("user_id", "AZ0000329", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10016", ValueType.PARAM);
//            Data data_time_id = new Data("time_id", "20160707", ValueType.PARAM);
//            //       Data data_store_code = new Data("store_code", "902116101", ValueType.PARAM);
//            Data data_store_id = new Data("area_code", "21", ValueType.PARAM);
//            // 751400901
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(data_time_id.key, data_time_id);
//            datalist.put(data_store_id.key, data_store_id);
//            //     datalist.put(data_store_code.key, data_store_code);
//            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.ACHVStaffRanking",datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);





//            Data data_user_id = new Data("user_id", "LL001", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10141", ValueType.PARAM);
//            Data data_store_code = new Data("store_code", "", ValueType.PARAM);
//            Data data_area_code = new Data("area_code", "", ValueType.PARAM);
//            Data data_role_code = new Data("user_role", Common.ROLE_GM, ValueType.PARAM);
//            Data data_time_id = new Data("date", "2016-07-21", ValueType.PARAM);
//
//
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(data_store_code.key, data_store_code);
//            datalist.put(data_area_code.key, data_store_code);
//            datalist.put(data_role_code.key, data_store_code);
//            datalist.put(data_time_id.key, data_time_id);
//
//            String[] date_types = new String[]{Common.TIME_TYPE_WEEK,Common.TIME_TYPE_MONTH,Common.TIME_TYPE_YEAR};
//            org.json.JSONObject object = new org.json.JSONObject();
//            for (int i = 0; i < date_types.length; i++) {
//                String date_type = date_types[i];
//                Data data_date_type = new Data("date_type", date_type, ValueType.PARAM);
//                datalist.put(data_date_type.key, data_date_type);
//                DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.ACHVAnalysisInfo",datalist);
//
//                String result = dataBox.data.get("message").value;
//                System.out.println(result);
//                object.put(date_type,result);
//            }
//            System.out.println(object.toString());



//            int[] a={1,2,3,4,5};
//            for (int i=0;i<a.length;i++){
//                if(a[i]==2){
//                    continue;
//                }
//                System.out.println("-----"+a[i]+"----------");
//            }


//            List<Area> areas = areaService.selectArea("C10141", "§A0116,§A0117,§A0118,§A0119,§A0120,§A0121,");
//            for (Area area:areas
//                 ) {
//                System.out.println(area.getArea_name());
//            }
        } catch (Exception x) {
            x.printStackTrace();
        }

    }


}