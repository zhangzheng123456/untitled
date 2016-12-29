package com.bizvane.ishop.service;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OssUtils;
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
import java.util.*;

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
    @Autowired
    private TableManagerService managerService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private AppLoginLogService loginLogService;
    String id;

    //成功
    @Test
    public void testselectAllFeedback() {
        try {
//            String user_id = "ABC123";
//            String corp_code = "C10141";
//            String pageNumber = "4";
//            String vip_id = "";
//
//            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
//            Data data_corp_id = new Data("corp_code", corp_code, ValueType.PARAM);
//            Data data_number = new Data("row_num", pageNumber, ValueType.PARAM);
//            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
//
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_id.key, data_corp_id);
//            datalist.put(data_number.key, data_number);
//            datalist.put(data_vip_id.key, data_vip_id);
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("ChatView", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);
//            HashMap<String, Data> paramList=new HashMap<String, Data>();
//            paramList.put("start",new Data("start","1", ValueType.PARAM));
//            paramList.put("limit",new Data("limit","10", ValueType.PARAM));
//            paramList.put("open_id",new Data("open_id","", ValueType.PARAM));
//            paramList.put("corp_code",new Data("corp_code",corp_code, ValueType.PARAM));
//            Data data_corp_id = new Data("corp_code", "C10000", ValueType.PARAM);
//            Data data_open_id = new Data("open_id", "omKEMw4xgBRa82oUMidp8LldZ95c", ValueType.PARAM);
//            Data data_app_id = new Data("app_id", "", ValueType.PARAM);
//
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_corp_id.key, data_corp_id);
//            datalist.put(data_open_id.key, data_open_id);
//            datalist.put(data_app_id.key, data_app_id);
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("QueryUserCodeByOpenID", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);
//            String date="2008-02-30";
//            String checkDate = LuploadHelper.checkDate(date);
//            System.out.println(checkDate);
//            String str="{\"type\":\"between\",\"value\":{\"start\":\"2\",\"end\":\"12\"}}";
//            boolean b = CheckUtils.checkJson(str);
//            System.out.println(b);

//String str="<img src=\"/image/upload/20161018/1476771476538038849.jpg\" _src=\"/image/upload/20161018/1476771476538038849.jpg\" title=\"1476771476538038849.jpg\" alt=\"1476771476538038849.jpg\" width=\"87\" height=\"78\" border=\"0\" vspace=\"0\" style=\"width: 87px; height: 78px; float: right;\">";
//            List<String> htmlImageSrcList = OssUtils.getHtmlImageSrcList(str);
//      //      System.out.println(htmlImageSrcList.size());
//            for (String s:htmlImageSrcList
//                 ) {
//                System.out.println(s);
//            }

//            String el2Str = WebUtils.El2Str("*$|");
//            System.out.println(el2Str);
//            Map<String,String> map=new HashMap<String, String>();
//            map.put("brand_name","'"+el2Str+"'");
//            PageInfo<Brand> c10141 = brandService.getAllBrandScreen(1, 10, "C10141", null, map);
//            List<Brand> list = c10141.getList();
//            for (Brand brand:list
//                 ) {
//                System.out.println(brand.getCorp_name()+"------------"+brand.getBrand_name());
//            }
            //------------------跟新table_code----------------------------
//            List<TableManager> tableManagers = managerService.selTableList();
//            int i1=0;
//            for (int i = 0; i < tableManagers.size(); i++) {
//                i1+= managerService.updateTable("T" + WebUtils.generateSerialNumber(i), tableManagers.get(i).getId() + "");
//
//            }
//            System.out.println("-----------:"+i1);



//---------------新增系统管理员列表权限----------------------------------------------------
//            List<TableManager> tableManagers = managerService.selTableList();
//            int i1=0;
//            for (int i = 0; i < tableManagers.size(); i++) {
//                TablePrivilege tablePrivilege=new TablePrivilege();
//                tablePrivilege.setColumn_name(tableManagers.get(i).getColumn_name());
//                tablePrivilege.setFunction_code(tableManagers.get(i).getFunction_code());
//                tablePrivilege.setMaster_code("R6000");
//                tablePrivilege.setEnable("Y");
//                Date now = new Date();
//                tablePrivilege.setCreater("10000");
//                tablePrivilege.setCreated_date(Common.DATETIME_FORMAT.format(now));
//                tablePrivilege.setModified_date(Common.DATETIME_FORMAT.format(now));
//                tablePrivilege.setModifier("10000");
//                tablePrivilege.setIsactive("Y");
//                i1+= managerService.insert(tablePrivilege);
//
//            }
            System.out.println("-----------:"+Math.round(Math.random() * 9)+ Math.round(Math.random() * 9)+ Math.round(Math.random() * 9));

         //   ------------------------------测试登录次数------------------------------------------------
//            Map<String, String> map=new HashMap<String, String>();
//          //  {"screen_key":"time_count","screen_value":{"type": "","value":"" }}
//            map.put("time_count","{\"type\": \"gt\",\"value\":\"3\" }");
//            map.put("time_bucket","{\"start\":\"\",\"end\":\"\"}");
//            map.put("corp_name","");
//            PageInfo<AppLoginLog> appLoginLogs = loginLogService.selectAllScreen(1, 20, "C10141", map);
//            List<AppLoginLog> list = appLoginLogs.getList();
//
//            for (AppLoginLog appLoginLog:list
//                 ) {
//                System.out.println(appLoginLog.getCorp_name()+"-----"+appLoginLog.getTime()+"-----"+appLoginLog.getUser_name());
//            }
//            Area area2=new Area();
//            area2.setCorp_code("C10141");
//            area2.setArea_code("98656");
//            area2.setArea_name("自营|南通");
//            area2.setIsactive("Y");
//            Date now = new Date();
//            area2.setCreater("ly");
//            area2.setCreated_date(Common.DATETIME_FORMAT.format(now));
//            area2.setModified_date(Common.DATETIME_FORMAT.format(now));
//            area2.setModifier("ly");
//            areaService.insertExecl(area2);

//
//            String screen = "|";
//            if (screen.startsWith("|")) {
//                screen = screen.substring(1);
//            }
//            if (screen.endsWith("|")) {
//                screen = screen.substring(0, screen.length() - 1);
//            }
//            System.out.println(screen);
//
//            Map<String, String> map =new HashMap<String, String>();
//            map.put("area_name","南通|自营");
//            map.put("area_code","A012");
//            PageInfo<Area> pageInfo = areaService.getAllAreaScreen(1, 20, "C10141", null, map);
//            List<Area> list = pageInfo.getList();
//            for (Area area:list
//                 ) {
//                System.out.println(area.getArea_name());
//            }
//            for (int i = 0; i < 3; i++) {
//                if(i==1){
//                    continue;
//                }
//                System.out.println(i);
//            }

//            LuploadHelper.deleteDirectory("E:\\Test");
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
//            DataBox dataBox = iceInterfaceService.iceInterface("ACHVStoreRanking",datalist);
//
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result+"--");
////
//            String ss="§SHBS0001,";
//            String[] store_ids = ss.replace(Common.STORE_HEAD,"").split(",");
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
//            DataBox dataBox = iceInterfaceService.iceInterfaceV2("ACHVStaffRanking",datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);


//
//            String user_id = "ABC123";
//            String corp_code = "C10141";
//            String pageNumber = "1";
//            String vip_id = "oStyzuDUUI3PztcguF-6TjAvu5Bk";
//
//            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
//            Data data_corp_id = new Data("corp_code", corp_code, ValueType.PARAM);
//            Data data_number = new Data("row_num", pageNumber, ValueType.PARAM);
//            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
//
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_id.key, data_corp_id);
//            datalist.put(data_number.key, data_number);
//            datalist.put(data_vip_id.key, data_vip_id);
//            DataBox dataBox = iceInterfaceService.iceInterface("ChatView", datalist);
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
//            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisSleep",datalist);
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
//            String ss="§902102501,";
//            String[] store_ids = ss.replace(Common.STORE_HEAD,"").split(",");
//            Data data_user_id = new Data("user_id", "1221", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10141", ValueType.PARAM);
//            Data role_code = new Data("role_code", "R3000", ValueType.PARAM);
//            Data page_num = new Data("page_num", "1", ValueType.PARAM);
//            Data area_code = new Data("area_code", "A0116", ValueType.PARAM);
//            Data page_size = new Data("page_size", "200", ValueType.PARAM);
//            Data query_type = new Data("query_type", "2", ValueType.PARAM);
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
//            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnlysisVipAmount",datalist);
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