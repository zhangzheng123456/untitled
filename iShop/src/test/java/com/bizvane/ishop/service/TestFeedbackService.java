package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.utils.*;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.csvreader.CsvReader;
import com.github.pagehelper.PageInfo;
import com.mongodb.DBCursor;
import io.netty.util.internal.ConcurrentSet;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.HashedMap;
import org.apache.velocity.runtime.directive.Foreach;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import javax.swing.*;
import java.io.*;
import java.lang.System;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.bizvane.ishop.utils.RSATool.decryptByRSA1;
import static com.bizvane.ishop.utils.RSATool.encryptByRSA1;
import static com.bizvane.ishop.utils.RSATool.giveRSAKeyPairInByte;

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
    @Autowired
    private AppManagerService appManagerService;
    @Autowired
    private VipTaskService vipTaskService;
    String id;

    //成功
    @Test
    public void testselectAllFeedback() {
        try {
//            String str="爱秀社区平台,offical,太平鸟,上海商帆演示,贵人鸟,江南布衣,安正时尚集团股份有限公司,罗莱家纺,ENDEAR,中意商贸,宜宾瑞琅,上海婉甸服饰有限公司,欣贺股份有限公司,上海悦娅实业发展有限公司,迪丰,佳木斯,米谷骏,桃花季,地素,深圳市梵思诺时尚服饰有限公司,动感驿站,内江尚美鞋业,广元淘美会,广州蒙娜丽莎时尚制品有限公司,红润,海明,千释,赛颂,上赫,爱帛服饰,tommy,丝恩道,悦娅,杭州娜利服饰有限公司,华鼎菲妮迪国际时装零售有限公司,慕丰,动向,晨风（江苏)服装有限公司,诺曼琦,衣舍,彩虹现代商贸（深圳）有限公司,浙江红蜻蜓鞋业股份有限公司,上海三润服装有限公司,云弘,昆明市五华区讯玺服饰店,蕾妮国际,赛颂测试,清心草,红纺,三枪,拉如达薇亚,衣生有你,海螺,饰库服饰,思朋服饰,上海满心,哈瑞,拍普儿,勇航商贸,深港鞋业,云南劲鳄,如斯,恒健集团,容子木,江南布衣,安正有数,卓卡,盛夏,大东鞋超市,提香,利标,森睿,利标,上海同瑞服饰,内外,ECCO,奥龙世博,熙辰,征程商贸,江南布衣-新,test,艾美娜,隶岚,环球,红绥带,凯莉米洛,佰利加,上海嘉韩,尚品,金百博路,深圳市嘉汶服饰有限公司,江南布衣有数测试,隶岚,例外,金利来,刚泰测试,春达鞋业,Bizvane 商帆测试,睿族,利标(中间库正式）";
//            System.out.println(str.length());
            String readCsv = OutCsvHelper.read_csv("E:\\QQ生成文档\\331393451\\FileRecv\\公众号95-2018-03-1生日发券作废券号.csv");
            System.out.println(readCsv);
            String[] split = readCsv.split(",");
            System.out.println(split.length);
            Map datalist = new HashMap<String, Data>();
            Data data_corp_code = new Data("corp_code", "C10224", ValueType.PARAM);
            Data data_user_id = new Data("vou_no","784-16ADD", ValueType.PARAM);
            Data data_state= new Data("state","-1", ValueType.PARAM);
//
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_state.key, data_state);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("EditCoupon", datalist);
            String result = dataBox.data.get("message").value;
            System.out.println(result);

//            JSONArray array = JSONArray.parseArray("[{\"screen_key\":\"task_status\",\"screen_value\":\"3\"}]");
//            JSONObject object = new JSONObject();
//            object.put("list",array);
//            Map<String,Object> map = new HashMap<String, Object>();
//            map.put("a","你好");
//            System.out.println(map);
//            PageInfo<VipTask> list=vipTaskService.selectAllScreen(1,2,"",map);
//            System.out.println(JSONObject.toJSONString(list.getList()));
//            Object[] v = giveRSAKeyPairInByte();
//            //byte[] sourcepri_pub = MdigestSHA("假设这是要加密的客户数据");
//            byte[] sourcepri_pub = ("13265986584ccc3164 xcxcxcxcxcxcadadad      cazzczczbncvcxczasdfgnhjhmgnfxbcvxfxnghnfbxcvzxccbbvnccbxvzdsgdmhnbv cxcccccccccccc946465rrrr4648649801pdddddddddddddssvsvswdffffbxvzxcxcvxvvvvvvvvvvvvvvvvvvvvvvxqwertyuioprivate").getBytes("UTF-8");
//
//            //使用私钥对摘要进行加密 获得密文
//            byte[] signpri_pri =encryptByRSA1((byte[]) v[0] ,sourcepri_pub);
//
////            System.out.println("私钥加密密文："+new String(Base64.encodeBase64(signpri_pri)));
//            //使用公钥对密文进行解密 返回解密后的数据
//            byte[] newSourcepri_pub=decryptByRSA1((byte[]) v[1],signpri_pri);
//
//
//
//            System.out.println("公钥解密："+new String(newSourcepri_pub,"UTF-8"));

//            System.out.println(MD5Sum.getMD5Str32("1234" + "1469257687" + "5678").toUpperCase());

//            Map datalist = new HashMap<String, Data>();
//            Data data_code = new Data("corp_code", "C10016TEST", ValueType.PARAM);
//            Data data_year = new Data("open_id", "otctQwFrNeTEP9zQTotXUvfvRhvs", ValueType.PARAM);
//            Data data_month = new Data("row_num", "0", ValueType.PARAM);
//            Data vip_id = new Data("vip_id", "", ValueType.PARAM);
//            Data page_size = new Data("page_size", "20", ValueType.PARAM);
//            Data start_time = new Data("start_time", "2017-10-01", ValueType.PARAM);
//            Data end_time = new Data("end_time", "2017-11-03", ValueType.PARAM);
//            datalist.put(vip_id.key, vip_id);
//            datalist.put(data_code.key, data_code);
//            datalist.put(data_year.key, data_year);
//            datalist.put(data_month.key, data_month);
//            datalist.put(page_size.key, page_size);
//            datalist.put(start_time.key, start_time);
//            datalist.put(end_time.key, end_time);
////
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("ChatMessageApi", datalist);
//            String result = JSON.toJSONString(dataBox);
//            System.out.println(result);

//            String store_name="Z/P武商广场";
//            if (store_name.contains("/")) {
//                store_name = store_name.replace("/", "");
//            }
//            System.out.println(store_name);
//            Map datalist = new HashMap<String, Data>();
//            Data data_corp_code = new Data("corp_code", "C10208", ValueType.PARAM);
//            Data data_user_id = new Data("user_id", "LZ008", ValueType.PARAM);
//
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(data_user_id.key, data_user_id);
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetUserId4Web", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);

//           final ConcurrentSet<String> set=new ConcurrentSet();
//            set.add("1");
//
//            String[] bb=new String[]{"2","3","1","4"};
//            Set<String> set_brand = new HashSet();
//            for (int i = 0; i < bb.length; i++) {
//                for (String ss:set) {
//                    if(!bb.equals(ss)){
//                        set.add(bb[i]);
//                        set_brand.add(bb[i]);
//                    }else{
//                        set_brand.remove(bb[i]);
//                    }
//                }
//
//            }
//
//
//            for (String str:set_brand
//                 ) {
//                System.out.println(str);
//            }
//            String modified_date = "2017-11-11 10:00:00";
//            String currentTime = String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//
//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date d1 = df.parse(modified_date);
//            Date d2 = df.parse(currentTime);
//            long diff = d2.getTime() - d1.getTime();
//            long minute = diff / (1000 * 60);
//            System.out.println(minute);
//            Map datalist = new HashMap<String, Data>();
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("PraiseCopy", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);

//            System.out.println(20 / 20 + 1);
//            Map datalist = new HashMap<String, Data>();
//            Data data_code = new Data("corp_code", "C10000", ValueType.PARAM);
//            Data data_user_code = new Data("user_code", "LY001", ValueType.PARAM);
//            Data data_vip_id= new Data("vip_id", "2209512", ValueType.PARAM);
//            Data data_couponCode= new Data("couponCode", "21457", ValueType.PARAM);
//            Data data_description = new Data("description", "测试现金券", ValueType.PARAM);
//            Data data_ticket_code_ishop= new Data("ticket_code_ishop", "TC1000020171016160124973491", ValueType.PARAM);
//
//            datalist.put(data_code.key, data_code);
//            datalist.put(data_user_code.key, data_user_code);
//            datalist.put(data_vip_id.key, data_vip_id);
//            datalist.put(data_couponCode.key, data_couponCode);
//            datalist.put(data_description.key, data_description);
//            datalist.put(data_ticket_code_ishop.key, data_ticket_code_ishop);
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("SendTicketByUser", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);

//
//            Map datalist = new HashMap<String, Data>();
//            Data data_code = new Data("corp_code", "C20000", ValueType.PARAM);
//            Data data_app_id = new Data("app_id", "wx8032b19f5c57fef7", ValueType.PARAM);
//
//            datalist.put(data_code.key, data_code);
//            datalist.put(data_app_id.key, data_app_id);
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetCoupon", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);
////
//

//            Map datalist = new HashMap<String, Data>();
//            Data data_code = new Data("corp_code", "C10141", ValueType.PARAM);
//            Data data_user_code = new Data("user_code", "ABC123", ValueType.PARAM);
//            Data data_pageNum= new Data("pageNum", "1", ValueType.PARAM);
//            Data data_pageSize = new Data("pageSize", "10", ValueType.PARAM);
//
//            datalist.put(data_code.key, data_code);
//            datalist.put(data_user_code.key, data_user_code);
//            datalist.put(data_pageNum.key, data_pageNum);
//            datalist.put(data_pageSize.key, data_pageSize);
//
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetTicketListByUser", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);

//            for (int i = 0; i < 1000; i++) {
//                Map datalist = new HashMap<String, Data>();
//                Data data_corp_code = new Data("corp_code", "C10141", ValueType.PARAM);
//                Data data_store_id = new Data("store_id", "902100301", ValueType.PARAM);
//                Data data_user_id= new Data("user_id", "ABC123", ValueType.PARAM);
//                Data data_user_code= new Data("user_code", "ABC123", ValueType.PARAM);
//                Data data_row_num= new Data("row_num", "0", ValueType.PARAM);
//
//                datalist.put(data_corp_code.key, data_corp_code);
//                datalist.put(data_store_id.key, data_store_id);
//                datalist.put(data_user_id.key, data_user_id);
//                datalist.put(data_user_code.key, data_user_code);
//                datalist.put(data_row_num.key, data_row_num);
//
//                DataBox dataBox = iceInterfaceService.iceInterfaceV2("VipAll", datalist);
//                System.out.println(i+"======="+dataBox.status.toString());
//            }


//            JSONArray array=new JSONArray();
//            JSONArray array1=new JSONArray();
//            JSONObject object=new JSONObject();
//            object.put("a","1");
//            array1.add(object);
//            array.addAll(array1);
//            JSONArray array2=new JSONArray();
//            array2.add(object);
//            array=array2;
//            System.out.println(array.size());
//            System.out.println( "".substring(0, 1));

//            int count = 0;
//            String result = "";
//            for (int i = 0; i < 3; i++) {
//                count += 1;
//                result = "======" + count;
//
//            }
//            System.out.println(result);
//            if(0%10==0){
//                System.out.println("============="+0%10);
//            }
//            String a="";
//            String[] split = a.split(",");
//            System.out.println(split.length);


//            SimpleDateFormat DATETIME_FORMAT_DATE_MS = new SimpleDateFormat("yyyyMMddHH");
//            System.out.println( TimeUtils.getCurrentTimeInString(DATETIME_FORMAT_DATE_MS));
//            String encode = URLEncoder.encode("http://wechat.app.bizvane.com/app/wechat/getOpenIdByWx","UTF-8");
//            System.out.println(encode);
//            System.out.println(URLDecoder.decode(encode,"UTF-8"));
//
//            Map datalist = new HashMap<String, Data>();
//            Data data_code = new Data("error_message", "数据库连接断开", ValueType.PARAM);
//            datalist.put(data_code.key, data_code);
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("SendErrorByFourm", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);


//            String error_message="社区数据库连接断开";
//            System.out.println(error_message);
//            Map datalist = new HashMap<String, Data>();
//            Data data_code = new Data("corp_code", "C10055", ValueType.PARAM);
//            Data data_year = new Data("store_code", "9DA22124", ValueType.PARAM);
//            Data data_month = new Data("user_code", "13614-49", ValueType.PARAM);
//
//            datalist.put(data_code.key, data_code);
//            datalist.put(data_year.key, data_year);
//            datalist.put(data_month.key, data_month);
//
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("CheckStaffToHbase", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);


//            Map datalist = new HashMap<String, Data>();
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("AddLabelByMySql", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);
//            String label="#1#2#3#4#";
//            String[] split = label.split("#");
//            for (int i = 0; i < split.length; i++) {
//                System.out.println(split[i]+"-----"+split.length);
//            }

//            JSONObject object=new JSONObject();
//            JSONArray array_user=new JSONArray();
//            JSONObject object_user=new JSONObject();
//            object_user.put("role_code","R2000");
//            JSONObject date_user=new JSONObject();
//            date_user.put("day","2");
//            date_user.put("month","10");
//            object_user.put("type",date_user);
//            array_user.add(object_user);
//            object.put("user",array_user);
//
//
//            JSONObject date_vip=new JSONObject();
//            date_vip.put("day","2");
//            date_vip.put("month","10");
//            object.put("vip",date_vip);
//            System.out.println(JSON.toJSONString(object));


//            SimpleDateFormat sdf_day = new SimpleDateFormat("yyyy-MM-dd");
//            String format_day = sdf_day.format(new Date());
//            SimpleDateFormat sdf_mon = new SimpleDateFormat("yyyy-MM");
//            String format_month = sdf_mon.format(new Date());
//            System.out.println(format_day+"===="+format_month);
//            String product_urls="http://ishop.dev.bizvane.com/goods/mobile_v2/goods.html?corp_code=C10183TEST&id=2614&type=app&user_id=U0001";
//            String product_sub = product_urls.substring(product_urls.indexOf("id="));
//            String substring = product_sub.substring(3, product_sub.indexOf("&"));
//            System.out.println(substring);


//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//            Date date = new Date();
//            String time_id_0 = sdf.format(date);
//            String time_id_2 = sdf.format(TimeUtils.getLastDateByDay(TimeUtils.getLastDate(date, -3), -1));
//            String time_id_3 = sdf.format(TimeUtils.getLastDate(date, -3));
//
//            System.out.println(time_id_2);
//            System.out.println(time_id_3);

//            System.out.println("-----------------C10125---------------------------------------");
//            String str="基本会员管理,会员资料管理,会员沟通,业绩管理,商品信息,店员管理,店铺管理,任务管理,活动管理,我的账号管理";
//            String[] split = str.split(",");
//            for (int j = 0; j < split.length; j++) {
//                List<AppManager> actionList = appManagerService.getActionList(split[j], null, null);
//                String action_codes="";
//                for (int i = 0; i < actionList.size(); i++) {
//                    action_codes+= actionList.get(i).getApp_action_code()+",";
//                }
//                Map datalist = new HashMap<String, Data>();
//                Data data_code = new Data("corp_code", "C10016", ValueType.PARAM);
//                Data data_year = new Data("year", "2017", ValueType.PARAM);
//                Data data_month= new Data("month", "06", ValueType.PARAM);
//                Data data_eventId = new Data("eventId", action_codes, ValueType.PARAM);
//                Data data_role= new Data("role", "R4000", ValueType.PARAM);
//
//                datalist.put(data_code.key, data_code);
//                datalist.put(data_year.key, data_year);
//                datalist.put(data_month.key, data_month);
//                datalist.put(data_eventId.key, data_eventId);
//                datalist.put(data_role.key, data_role);
//
//
//                DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetEventByRole", datalist);
//                String result = dataBox.data.get("message").value;
//                System.out.println(split[j]+":"+result);
//            }


////            System.out.println("-----------------C10125---------------------------------------");
//            String str="基本会员管理,会员资料管理,会员沟通,业绩管理,商品信息,店员管理,店铺管理,任务管理,活动管理,我的账号管理";
//            String[] split = str.split(",");
//            String corp_code="C10055";
//            int vip_mk=0;
//            int my_mk=0;
//            int yj_mk=0;
//            int sp_mk=0;
//            for (int j = 0; j < split.length; j++) {
//                List<AppManager> actionList = appManagerService.getActionList(split[j], null, null);
//                String action_codes="";
//                for (int i = 0; i < actionList.size(); i++) {
//                    action_codes+= actionList.get(i).getApp_action_code()+",";
//                }
//                Map datalist = new HashMap<String, Data>();
//                Data data_code = new Data("corp_code", corp_code, ValueType.PARAM);
//                Data data_year = new Data("year", "2017", ValueType.PARAM);
//                Data data_month= new Data("month", "10", ValueType.PARAM);
//                Data data_eventId = new Data("eventId", action_codes, ValueType.PARAM);
//
//
//                datalist.put(data_code.key, data_code);
//                datalist.put(data_year.key, data_year);
//                datalist.put(data_month.key, data_month);
//                datalist.put(data_eventId.key, data_eventId);
//
//                DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetEventByMonth", datalist);
//                String result = dataBox.data.get("message").value;
//                if(j==0 ||j==1 || j==2){
//                    JSONArray array = JSON.parseArray(result);
//                    vip_mk+= array.getJSONObject(0).getInteger(corp_code);
//                }
//                if(j>=5){
//                    JSONArray array = JSON.parseArray(result);
//                    my_mk+= array.getJSONObject(0).getInteger(corp_code);
//                }
//                if(j==3){
//                    JSONArray array = JSON.parseArray(result);
//                    yj_mk+= array.getJSONObject(0).getInteger(corp_code);
//                }
//                if(j==4){
//                    JSONArray array = JSON.parseArray(result);
//                    sp_mk+= array.getJSONObject(0).getInteger(corp_code);
//                }
//                System.out.println(split[j]+":"+result);
//            }
//            System.out.println("=会员模块点击量="+vip_mk+"=我的模块点击量="+my_mk+"=业绩模块点击量="+yj_mk+"=商品模块点击量="+sp_mk);

//            DBCursor cursor=null;
//            if(cursor.hasNext()){
//                System.out.println("222222");
//            }
//            System.out.println("1111");
//            Map datalist = new HashMap<String, Data>();
//            Data data_code = new Data("corp_code", "C10183TEST", ValueType.PARAM);
//            Data data_corp_code = new Data("goods_code", "005", ValueType.PARAM);
//
//
//            datalist.put(data_code.key, data_code);
//            datalist.put(data_corp_code.key, data_corp_code);
//
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetGoodsMatchByCode", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);

//            String corp_code="C10184";
//            String goods_code="EW722056";
//            String sql = " SELECT dgm.*,\n" +
//                    "        dg.goods_image\n" +
//                    "        FROM def_goods_match dgm,def_goods dg\n" +
//                    "        WHERE dgm.corp_code = dg.corp_code\n" +
//                    "        AND dgm.goods_code = dg.goods_code " +
//                    "        AND dgm.corp_code='" +corp_code+"'"+
//                    "        AND dgm.goods_match_code in\n" +
//                    "        (SELECT goods_match_code FROM def_goods_match\n" +
//                    "        WHERE 1=1 AND " +
//                    "        goods_code='"+goods_code+"'\n" +
//                    "        ) " +
//                    "        AND dg.isactive ='Y'";
//            System.out.println(sql);

//            Map datalist = new HashMap<String, Data>();
//            Data data_code = new Data("join_phone", "13800138000", ValueType.PARAM);
//            Data data_corp_code = new Data("join_user_id", "ABC123", ValueType.PARAM);
//
//
//            datalist.put(data_code.key, data_code);
//            datalist.put(data_corp_code.key, data_corp_code);
//
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("SendMessage2App", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);


//            List<Map<String, String>> users = new ArrayList<Map<String, String>>();
//            Map<String, String> map = new HashedMap();
//            map.put("user_code", String.valueOf("null"));
//            map.put("user_type", "emp");
//            users.add(map);
//            if (users.size() == 0 || users.get(0).get("user_code").equals("null")) {
//                System.out.println("---------");
//            }
            //          System.out.println(Integer.parseInt("-1"));
//            String aa="9.9";
//            System.out.println(CheckUtils.isNumeric(aa));
//            String isopen="Y";
//            String close_date="aa";
//            if((null==close_date||close_date.equals("") )&& "Y".equals(isopen)){
//                System.out.println("111111111");
//            }else{
//                System.out.println("222222222");
//            }
//            Map datalist = new HashMap<String, Data>();
//            Data data_code = new Data("d_match_code", "P20170512222656381881", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10041", ValueType.PARAM);
//
//
//            datalist.put(data_code.key, data_code);
//            datalist.put(data_corp_code.key, data_corp_code);
//
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("EditShopMatchCount", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);

//            String aa = "1,2,3";
//            String[] split_ids = aa.split(",");
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < split_ids.length; i++) {
//                if (i == split_ids.length - 1) {
//                    sb.append(split_ids[i]);
//                } else {
//                    sb.append(split_ids[i] + "%2c");
//                }
//            }
            //           System.out.println(sb);
//            String aa="https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3a%2f%2fwechat.dev.bizvane.com%2fapp%2fwechat%2fgetOpenIdByWx&response_type=code&scope=snsapi_base&component_appid=wx722fb7eaa40020e9#wechat_redirect";
//            System.out.println(aa.length());
//            Map datalist = new HashMap<String, Data>();


//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("ChangOpenDate", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);


//            Map datalist = new HashMap<String, Data>();
//            Data data_code = new Data("code", "2721530300 2724502910", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10197", ValueType.PARAM);
//
//
//            datalist.put(data_code.key, data_code);
//            datalist.put(data_corp_code.key, data_corp_code);
//
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetProuctDetails", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);
//            System.out.println();
//            List<String> dates=new ArrayList<String>();
//            List<String> dates_1 = TimeUtils.getMonthAllDays("2017-01-01");
//            List<String> dates_2 = TimeUtils.getMonthAllDays("2017-02-01");
//            List<String> dates_3 = TimeUtils.getMonthAllDays("2017-03-01");
//            List<String> dates_4 = TimeUtils.getMonthAllDays("2017-04-01");
//            dates.addAll(dates_1);
//            dates.addAll(dates_2);
//            dates.addAll(dates_3);
//            dates.addAll(dates_4);
//            for (int i = 0; i < dates.size(); i++) {
//                System.out.println(dates.get(i));
//            }
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            String start_time = "2017-05-02 14:33:38";
//            String end_time = Common.DATETIME_FORMAT.format(new Date());
//
//            Date date_start = format.parse(start_time);
//            Date date_end = format.parse(end_time);
//           String open_date_count = String.valueOf(TimeUtils.getDiscrepantDays(date_start, date_end));
//            System.out.println(open_date_count);
//            Map datalist = new HashMap<String, Data>();
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("MessageCopyAllToHf", datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);

//            Map datalist = new HashMap<String, Data>();
//            Data data_time_type = new Data("product_id", "MWWD50005", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10016", ValueType.PARAM);
//            Data data_store_id = new Data("store_id", "AHAQ0105,SH000990,SH000988,CL000225,SH000992,SH000993", ValueType.PARAM);
//
//            datalist.put(data_time_type.key, data_time_type);
//            datalist.put(data_corp_code.key, data_corp_code);
//
//            datalist.put(data_store_id.key, data_store_id);
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetStockAndSale", datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);

//            Date now = new Date();
//            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
//            String end_time = Common.DATETIME_FORMAT.format(now);
//            Date date_start=format.parse("2017-04-19");
//            Date date_end=format.parse(end_time);
//            int time_count = TimeUtils.getDiscrepantDays(date_start, date_end);
//            System.out.println("-----"+time_count);

//            Map datalist = new HashMap<String, Data>();
//            Data data_time_type = new Data("product_id", "MWWD72102", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10016", ValueType.PARAM);
//
//
//            datalist.put(data_time_type.key, data_time_type);
//            datalist.put(data_corp_code.key, data_corp_code);
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("ProductDetail", datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);

//            Timer timer = new Timer();
//            timer.schedule(new TimerTaskTest(), 1000,1000);
//
//
//            Thread.sleep(1000*10);
//            JSONObject object=new JSONObject();
//            object.put("","444");
//
//            System.out.println("-----"+object.get(""));
//            java.util.Timer timer = new java.util.Timer(true);
//            TimerTask task = new TimerTask() {
//                public void run() {
//                    System.out.println("哈哈"); //每次需要执行的代码放到这里面。
//                }
//            };
//            timer.schedule(task,1);
//            System.out.println("---------");
//             SimpleDateFormat DATE_FORMAT_DATE_NO = new SimpleDateFormat("yyyyMMdd");
//            System.out.println( TimeUtils.getCurrentTimeInString(DATE_FORMAT_DATE_NO));

//            String aa="SH001504,SHYZ0196,SH000987,SH000599,SH000864,SH000550,CL000131,CL000143,SCYM0006,SH000819,SH000814,CL000135,CL000114,CL000127,CL000208,SH000511,SH000933,SH000934,SXYM0007,SXYM0004,CL000160,SHMM0002,SHYZ0007,SHBS0001,SHCS0001,SHYZ0045,SHYZ0170,SHYZ0001,SHJG0001,SHXJ0001,CL000166,SHYZ0189,CL000167,SHYZ0018,SHYZ0125,ZZYM0003,GZYM0002,SH000108,SXXA0125,SH000874,SH000985,SH000923,SH000986,CL000210,SH000523,SCYM0004,SH000563,SH000867,SH000826,SDWF0119,CL000121,SHYZ0164,SHYZ0169,TJYM0001,BJYM0006,HLDQ0509";
//            String[] split = aa.split(",");
//            String bb="";
//            for (int i = 0; i < split.length; i++) {
//                bb=bb+"'"+split[i]+"',";
//            }
//            System.out.println(bb);
//            String imgUrl="http://www.ex.com.cn/ffalf.jpg";
//       String new1=     imgUrl.substring(imgUrl.indexOf("/", 8)+1);  //http://www.ex.com.cn/ffalf.jpg
//            System.out.println(new1);
//            //new一个URL对象
//            URL url = new URL("http://products-image.oss-cn-hangzhou.aliyuncs.com/FAB/C10016/01489398822155.jpg");
//            //打开链接
//            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//            //设置请求方式为"GET"
//            conn.setRequestMethod("GET");
//            //超时响应时间为5秒
//            conn.setConnectTimeout(5 * 1000);
//            //通过输入流获取图片数据
//            InputStream inStream = conn.getInputStream();
//            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
//            byte[] data = OutHtmlHelper.readInputStream(inStream);
//            //new一个文件对象用来保存图片，默认保存当前工程根目录
//            File imageFile = new File("E:\\图片\\1.jpg");
//            //创建输出流
//            FileOutputStream outStream = new FileOutputStream(imageFile);
//            //写入数据
//            outStream.write(data);
//            //关闭输出流
//            outStream.close();
//            List<Store> storeByOdsType = storeService.getStoreByOdsType("C10016", "dealer");
//            System.out.println(storeByOdsType.size());
//            for (Store store:storeByOdsType
//                 ) {
//                System.out.println(store.getDealer());
//            }

//            String aa="SHYZ0198,SHYZ0195,SHYZ0157,SHYZ0197,SHYZ0163,SHYZ0196,SHYZ0172,SHYZ0191,SHYZ0185,SHYZ0187,SHYZ0186,SHYZ0193,SHYZ0171,SHYZ0192,SHYZ0189,SHYZ0169,SHYZ0020,SHYZ0058,SHYZ0122,SHYZ0124,SHYZ0151,SHYZ0155,SHYZ0156,SHXJ0001,SHYZ0022,SHYZ0153,SHYZ0158,SHYZ0161,SHBS0001,SHCS0001,SHYZ0001,SHYZ0006,SHYZ0007,SHYZ0013,SHYZ0014,SHYZ0015,SHYZ0016,SHYZ0018,SHYZ0034,SHYZ0037,SHYZ0045,SHYZ0049,SHYZ0050,SHYZ0059,SHYZ0060,SHYZ0125,SHYZ0145,SHYZ0146,SHYZ0147,SHYZ0148,SHYZ0149,SHYZ0150,SHYZ0152,SHYZ0154,SHYZ0165,SHYZ0166,SHYZ0132,SHYZ0170,SHYZ0168,SHYZ0048,SHYZ0017,SHYZ0021,SHYZ0055,SHYZ0131,SHYZ0140,SHYZ0160,SHYZ0164,SHYZ0039,SHYZ0042,SHYZ0030,SHYZ0008,SHYZ0159,SHYZ0162,SHYZ0138,SHYZ0137,SHYZ0123,SHYZ0040,SHYZ0057,SHQP0001,SHYZ0029,SHYZ0047,SHYZ0009,SHYZ0011,SHYZ0033,SHYZ0038,SHYZ0053,SHYZ0054,SHYZ0056,SHYZ0002,SHYZ0036,SHYZ0046,SHYZ0051,SHYZ0052";

//            Map datalist = new HashMap<String, Data>();
//            Data data_time_type = new Data("time_type", "M", ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code", "C10016", ValueType.PARAM);
//            Data data_time_id = new Data("date_value", "2017-02-18", ValueType.PARAM);
//            Data data_store_id = new Data("store_code", "BJYM0003", ValueType.PARAM);
//
//            datalist.put(data_time_type.key, data_time_type);
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(data_time_id.key, data_time_id);
//            datalist.put(data_store_id.key, data_store_id);
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("CorpSkuSales", datalist);
//
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date date=new Date();
//
//            System.out.println(sdf.format(TimeUtils.getNextDay("2017-01-01",6)));
//            for (int i = 0; i < 7; i++) {
//                System.out.println(sdf.format(TimeUtils.getNextDay("2017-01-01",i)));
//            }
//            ArrayList list_title = new ArrayList();
//            ArrayList list_value_amt = new ArrayList();
//            ArrayList list_value_num = new ArrayList();
//            ArrayList list_value_discount = new ArrayList();
//
//            list_title.add("类型");
//            list_title.add("春");
//            list_title.add("夏");
//            list_title.add("秋");
//            list_title.add("东");
//
//            list_value_amt.add("金额");
//            list_value_amt.add("1");
//            list_value_amt.add("1");
//            list_value_amt.add("1");
//            list_value_amt.add("1");
//
//            list_value_num.add("件数");
//            list_value_num.add("2");
//            list_value_num.add("2");
//            list_value_num.add("2");
//            list_value_num.add("2");
//
//            list_value_discount.add("折扣");
//            list_value_discount.add("3");
//            list_value_discount.add("5");
//            list_value_discount.add("7");
//            list_value_discount.add("1");

            //    OutExeclHelper.OutExecl_view(list_title,list_value_amt,list_value_num,list_value_discount);
//            List<AppManager> c10055 = appManagerService.getFunctionList("C10055");
//            for (AppManager appManager:c10055
//                 ) {
//                System.out.println(appManager.getApp_function());
//            }
//            Data data_corp_code = new Data("corp_code", "C10055", ValueType.PARAM);
//            Data data_store_code = new Data("store_code", "2DA23902,5DA44917", ValueType.PARAM);
//            Data data_user_code = new Data("user_code", "", ValueType.PARAM);
//            Data data_role_code = new Data("role_code", "", ValueType.PARAM);
//            Data data_app_action_code = new Data("app_action_code", "click_to_all_vip_list,click_to_birth_vip_list", ValueType.PARAM);
//            Data data_dev_type = new Data("dev_type", "", ValueType.PARAM);
//            Data data_date_type = new Data("date_type", "M", ValueType.PARAM);
//            Data data_date_value = new Data("date_value", "2017-02-03", ValueType.PARAM);
//            Data data_page_number = new Data("page_number", "1", ValueType.PARAM);
//            Data data_page_size = new Data("page_size", "10", ValueType.PARAM);
//
//
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(data_store_code.key, data_store_code);
//            datalist.put(data_user_code.key, data_user_code);
//            datalist.put(data_role_code.key, data_role_code);
//            datalist.put(data_app_action_code.key, data_app_action_code);
//            datalist.put(data_dev_type.key, data_dev_type);
//            datalist.put(data_date_type.key, data_date_type);
//            datalist.put(data_date_value.key, data_date_value);
//            datalist.put(data_page_number.key, data_page_number);
//            datalist.put(data_page_size.key, data_page_size);
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("ObtainEventTable", datalist);
//            String result = dataBox.data.get("message").value;
////
//         System.out.println(result);
//            List<String> yearAllDays = TimeUtils.getYearAllDays("2017-02-08");
//            for (String str:yearAllDays
//                 ) {
//                System.out.println(str);
//            }
//          int  userCountByStore = userService.getUserCountByStore("C00000", null);
//            System.out.println(userCountByStore);
//            String week = TimeUtils.getWeek("2017-02-12");
//            String[] split = week.split(" ");
//            for (String str:split
//                 ) {
//                System.out.println(str);
//            }

//            Data data_row_num = new Data("row_num","100" , ValueType.PARAM);
//            Data data_corp_code = new Data("corp_code","" , ValueType.PARAM);
//            Data data_open_id = new Data("open_id", "", ValueType.PARAM);
//            Data data_vip_card_no = new Data("vip_card_no", "", ValueType.PARAM);
//            Data data_type= new Data("type", "1", ValueType.PARAM);
//
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_row_num.key, data_row_num);
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(data_open_id.key, data_open_id);
//            datalist.put(data_vip_card_no.key, data_vip_card_no);
//            datalist.put(data_type.key, data_type);
//
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("Favorites", datalist);
//            String result = dataBox.data.get("message").value;

//            Data data_corp_code= new Data("corp_code","C10141" , ValueType.PARAM);
//
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_corp_code.key, data_corp_code);
//            DataBox dataBox = iceInterfaceService.iceInterfaceV3("DimSeasonPrd", datalist);
//            String result = dataBox.data.get("message").value;
//
//            Data data_corp_code = new Data("corp_code", "C10183", ValueType.PARAM);
//            Data data_vip_id = new Data("vip_id", "436749", ValueType.PARAM);
//            Data data_store_id = new Data("store_id", "", ValueType.PARAM);
//            Data data_group_id = new Data("group_id", "", ValueType.PARAM);
//
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_corp_code.key, data_corp_code);
//            datalist.put(data_vip_id.key, data_vip_id);
//            datalist.put(data_store_id.key, data_store_id);
//            datalist.put(data_group_id.key, data_group_id);
//
//            DataBox  dataBox = iceInterfaceService.iceInterfaceV3("VipTagSearchForWeb", datalist);
//            String result = dataBox.data.get("message").value;
//            System.out.println(result);
//            Data data_user_id = new Data("table_name", "BizvaneV2.CorpStoreSales", ValueType.PARAM);
//            Data data_corp_id = new Data("row_key", "{\"CORP_ID\":\"C10183\",\"T_BL_Y\":\"2016\",\"T_BL_M\":\"11\"}", ValueType.PARAM);
//            Data data_number = new Data("query_type", "array", ValueType.PARAM);
//            Data data_vip_id = new Data("colum", "{\"STORE_ID\":\"902105201\"}", ValueType.PARAM);
//
//            Map datalist = new HashMap<String, Data>();
//            datalist.put(data_user_id.key, data_user_id);
//            datalist.put(data_corp_id.key, data_corp_id);
//            datalist.put(data_number.key, data_number);
//            datalist.put(data_vip_id.key, data_vip_id);
//            DataBox dataBox = iceInterfaceService.iceInterface("HbaseResult", datalist);
//            String result = dataBox.data.get("message").value;
//            JSONObject jsonObject = JSON.parseObject(result);
//            String toString = jsonObject.get("result").toString();
//            JSONArray jsonArray = JSON.parseArray(toString);
//            //  JSONArray jsonArray = JSON.parseArray(result);
//            System.out.println(jsonArray.size());
//
//            LinkedHashMap<String,String> cols =new LinkedHashMap<String,String>();
//            cols.put("STORE_ID","店铺编号");
//            cols.put("AMT_TRADE","成交金额");
//            cols.put("AMT_SUG","原价金额");
//            cols.put("NUM_TRADE","销售笔数");
//            cols.put("NUM_SALES","销售件数");
//
//            JSONArray array=new JSONArray();
//
//            for (int i=0;i<jsonArray.size();i++){
//                JSONObject object=new JSONObject();
//
//                JSONObject jsonObject1 = JSON.parseObject(jsonArray.get(i).toString());
//                String num_trade = jsonObject1.get("NUM_TRADE").toString();
//                 num_trade = NumberUtil.keepPrecision(num_trade,0);
//
//                String amt_trade = jsonObject1.get("AMT_TRADE").toString();
//                 amt_trade = NumberUtil.keepPrecision(amt_trade);
//
//                String amt_sug = jsonObject1.get("AMT_SUG").toString();
//                 amt_sug = NumberUtil.keepPrecision(amt_sug);
//
//                String num_sales = jsonObject1.get("NUM_SALES").toString();
//                num_sales = NumberUtil.keepPrecision(num_sales, 0);
//
//                String store_id = jsonObject1.get("STORE_ID").toString();
//                object.put("NUM_TRADE",num_trade);
//                object.put("AMT_TRADE",amt_trade);
//                object.put("AMT_SUG",amt_sug);
//                object.put("NUM_SALES",num_sales);
//                object.put("STORE_ID",store_id);
//
//                array.add(object);
//            }
//
//            OutExeclHelper.OutExecl_vip2(array,cols);

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
            //        System.out.println("-----------:"+Math.round(Math.random() * 9)+ Math.round(Math.random() * 9)+ Math.round(Math.random() * 9));

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
////
////
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