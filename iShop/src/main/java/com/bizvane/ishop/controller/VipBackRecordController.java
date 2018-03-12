package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by yanyadong on 2017/4/28.
 */
@Controller
@RequestMapping("/record")
public class VipBackRecordController {
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    UserService userService;
    @Autowired
    StoreService storeService;
    @Autowired
    BrandService brandService;
    @Autowired
    VipBackRecordService vipBackRecordService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    String id = "";

    //搜索,列表
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    @ResponseBody
    public  String searchAllRecord(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor_user=mongoTemplate.getCollection(CommonValue.table_vip_back_record);
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            if(role_code.equals(Common.ROLE_SYS))
                corp_code="C10000";
            System.out.println("corp_code......"+corp_code);
            //分页
            int page_num=Integer.parseInt(jsonObject.getString("pageNumber"));
            int page_size=Integer.parseInt(jsonObject.getString("pageSize"));

            //搜索
            String search_value=jsonObject.get("searchValue").toString();
            BasicDBObject basicDBObject=vipBackRecordService.getQuery(search_value);
            //MongoDB对企业下的导购分组
            BasicDBObject basicDBObject1=new BasicDBObject();
            basicDBObject1.put("corp_code",corp_code);
            BasicDBList dbList=new BasicDBList();
            BasicDBObject basDb=new BasicDBObject();
            dbList.add(new BasicDBObject("user_id",new BasicDBObject("$ne","")));
            dbList.add(new BasicDBObject("user_id",new BasicDBObject("$ne",null)));
            dbList.add(new BasicDBObject("user_id",new BasicDBObject("$ne","null")));
            if(search_value.equals("")){
                dbList.add(basicDBObject1);
                basDb.put("$and",dbList);
            }else{
                dbList.add(basicDBObject);
                dbList.add(basicDBObject1);
                basDb.put("$and",dbList);
            }
            BasicDBObject match_user = new BasicDBObject("$match", basDb);
            /* Group操作*/
            DBObject groupField = new BasicDBObject("_id","$user_id");
            groupField.put("count", new BasicDBObject("$sum", 1));
            DBObject group_user = new BasicDBObject("$group", groupField);
            //分页
            BasicDBObject basicSkip=new BasicDBObject("$skip",(page_num - 1) * page_size);
            BasicDBObject basicLimit=new BasicDBObject("$limit",page_size);
            AggregationOutput output_user = cursor_user.aggregate(match_user,group_user,basicSkip,basicLimit);

            //获取企业下的所有导购信息的集合
            List<HashMap<String,Object>> userList=new ArrayList<HashMap<String, Object>>();
            for(DBObject dbObject:output_user.results()) {
                //获取每个导购下的所有信息
                HashMap<String, Object> userMap = new HashMap<String, Object>();
                //分组结果中无数据时的默认值
                userMap.put("phoneCount", "0");
                userMap.put("smsCount", "0");
                userMap.put("wechatCount", "0");
                userMap.put("user_phoneCount", "0");
                userMap.put("user_smsCount", "0");
                userMap.put("user_wechatCount", "0");

                BasicDBObject basicDBObject2 = (BasicDBObject) dbObject;
                //获取user_code
                String user_code = basicDBObject2.get("_id").toString();
                //....................................获取user...............................
                BasicDBObject basicuser= new BasicDBObject();
                basicuser.put("corp_code", corp_code);
                basicuser.put("user_id", user_code);
              DBCursor dbCursor_user= cursor_user.find(basicuser);
                DBObject sort_obj = new BasicDBObject("created_date", -1);
                dbCursor_user= dbCursor_user.sort(sort_obj);
                List<DBObject> dbObjectList=dbCursor_user.toArray();
                String user_name="";
                String store_name="";
                String brand_name="";
                String user_phone="";
                String store_code="";
                DBObject dbObject_user = dbObjectList.get(0);
                user_name = dbObject_user.get("user_name") == null ? "" : dbObject_user.get("user_name").toString();
                store_name = dbObject_user.get("store_name") == null ? "" : dbObject_user.get("store_name").toString();
                brand_name = dbObject_user.get("brand_name") == null ? "" : dbObject_user.get("brand_name").toString();
                user_phone = dbObject_user.get("user_phone") == null ? "" : dbObject_user.get("user_phone").toString();
                store_code = dbObject_user.get("store_code") == null ? "" : dbObject_user.get("store_code").toString();
//                }else {
//                    User user = userService.selectUserByCode(corp_code, user_code, "Y");
//
//                    if(user!=null) {
                        //所属店铺
//                        store_code = user.getStore_code();
//                        store_code=store_code.replace(Common.SPECIAL_HEAD,"");
//                        List<Store> storeList = storeService.selectStore(corp_code, store_code);
//
//                        for (int j = 0; j < storeList.size(); j++) {
//                            store_name += storeList.get(j).getStore_name() + ",";
//                        }
//                        if (store_name.endsWith(",")) {
//                            store_name = store_name.substring(0, store_name.length() - 1);
//                        }
//
//                        String brand_code="";
//                        for(int b=0;b<storeList.size();b++){
//                            brand_code+=storeList.get(b).getBrand_code()+",";
//                        }
//                        //所属品牌
//                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                        String[] brandCodes = brand_code.split(",");
//                        List<Brand> brandList = brandService.selectBrandByLabel(corp_code, brandCodes);
//                        for (int i = 0; i < brandList.size(); i++) {
//                            brand_name += brandList.get(i).getBrand_name() + ",";
//                        }
//                        if (brand_name.endsWith(",")) {
//                            brand_name = brand_name.substring(0, brand_name.length() - 1);
//                        }
//                        user_name=user.getUser_name();
//                        user_phone=user.getPhone();
//                    }
//                }
                //对导购下的回访类型分组
                BasicDBObject basicDB1 = new BasicDBObject();
                basicDB1.put("corp_code", corp_code);
                basicDB1.put("user_id", user_code);
                BasicDBObject match_action = new BasicDBObject("$match", basicDB1);
                /* Group操作*/
                DBObject groupField_action = new BasicDBObject("_id", "$action");
                groupField_action.put("count", new BasicDBObject("$sum", 1));
                DBObject group_action = new BasicDBObject("$group", groupField_action);
                AggregationOutput output_action = cursor_user.aggregate(match_action, group_action);
                //回访总数集合
                List<String> countList = new ArrayList<String>();
                List<String> countUserList = new ArrayList<String>();
                for (DBObject dbObject_action : output_action.results()) {
                    BasicDBObject basicDB_action = (BasicDBObject) dbObject_action;
                    String action = basicDB_action.getString("_id");
                    if (action.equals("1")) {
                        //电话回访总条数
                        String phoneCount = basicDB_action.getString("count");
                        countList.add(phoneCount);
                        userMap.put("phoneCount", phoneCount);
                        //电话回访总人数
                        String user_phoneCount = vipBackRecordService.getUserCount(cursor_user, corp_code, user_code, action);
                        userMap.put("user_phoneCount", user_phoneCount);
                        countUserList.add(user_phoneCount);
                    } else if (action.equals("2")) {
                        //短信回访总条数
                        String smsCount = basicDB_action.getString("count");
                        userMap.put("smsCount", smsCount);
                        countList.add(smsCount);
                        //短信回访总人数
                        String user_smsCount = vipBackRecordService.getUserCount(cursor_user, corp_code, user_code, action);
                        userMap.put("user_smsCount", user_smsCount);
                        countUserList.add(user_smsCount);
                    } else if (action.equals("3")) {
                        //微信回访总条数
                        String wechatCount = basicDB_action.getString("count");
                        userMap.put("wechatCount", wechatCount);
                        countList.add(wechatCount);
                        //微信回访总人数
                        String user_wechatCount = vipBackRecordService.getUserCount(cursor_user, corp_code, user_code, action);
                        userMap.put("user_wechatCount", user_wechatCount);
                        countUserList.add(user_wechatCount);
                    }
                }
                //回访记录总数
                int count = 0;
                for (int c = 0; c < countList.size(); c++) {
                    count += Integer.parseInt(countList.get(c));
                }
                //每个回访类型相对应的回访人数的总和
                int user_count = 0;
                for (int u = 0; u < countUserList.size(); u++) {
                    user_count += Integer.parseInt(countUserList.get(u));
                }
                userMap.put("user_code", user_code);
                userMap.put("user_name", user_name);
                userMap.put("store_name", store_name);
                userMap.put("phone", user_phone);
                userMap.put("brand_name", brand_name);
                userMap.put("user_count", user_count);
                userMap.put("count", count);
                //覆盖会员数
                BasicDBObject basic = new BasicDBObject();
                basic.put("corp_code", corp_code);
                basic.put("user_id", user_code);
                basic.put("vip_id", new BasicDBObject("$ne", null));
                int coverCount = cursor_user.distinct("vip_id", basic).size();
                userMap.put("coverCount", coverCount);
                //全部会员
                DataBox dataBox = iceInterfaceService.getAllVipByUser(corp_code,user_code,store_code);
                String value = dataBox.data.get("message").value.toString();
                JSONObject jsonObject1=JSON.parseObject(value);
                userMap.put("allVip", jsonObject1.get("count"));
                userList.add(userMap);
            }
            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("list",userList);
            jsonObject1.put("pageNumber",page_num);
            jsonObject1.put("pageSize",page_size);

            int count=cursor_user.distinct("user_id",basDb).size();
            jsonObject1.put("count",count);
            int pages = 0;
            if (count % page_size == 0) {
                pages = count / page_size;
            } else {
                pages = count / page_size + 1;
            }
            jsonObject1.put("pages",pages);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject1.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/screen",method = RequestMethod.POST)
    @ResponseBody
    public  String screenRecord(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor_user=mongoTemplate.getCollection(CommonValue.table_vip_back_record);
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            if(role_code.equals(Common.ROLE_SYS))
                corp_code="C10000";
            System.out.println("corp_code......"+corp_code);
            //分页
            int page_num=Integer.parseInt(jsonObject.getString("pageNumber"));
            int page_size=Integer.parseInt(jsonObject.getString("pageSize"));

            //MongoDB对企业下的导购分组
            BasicDBObject basicDBObject1=new BasicDBObject();
            basicDBObject1.put("corp_code",corp_code);
            BasicDBList dbList=new BasicDBList();
            BasicDBObject basDb=new BasicDBObject();
            dbList.add(new BasicDBObject("user_id",new BasicDBObject("$ne","")));
            dbList.add(new BasicDBObject("user_id",new BasicDBObject("$ne",null)));
            dbList.add(new BasicDBObject("user_id",new BasicDBObject("$ne","null")));
            if(jsonObject.getString("list").equals("")){
                dbList.add(basicDBObject1);
                basDb.put("$and",dbList);
            }else{
                //筛选
                BasicDBObject basicScreen=vipBackRecordService.getScreen(message,corp_code);
                dbList.add(basicScreen);
                dbList.add(basicDBObject1);
                basDb.put("$and",dbList);
            }
            BasicDBObject match_user = new BasicDBObject("$match", basDb);
            /* Group操作*/
            DBObject groupField = new BasicDBObject("_id","$user_id");
            groupField.put("count", new BasicDBObject("$sum", 1));
            DBObject group_user = new BasicDBObject("$group", groupField);
            //分页
            BasicDBObject basicSkip=new BasicDBObject("$skip",(page_num - 1) * page_size);
            BasicDBObject basicLimit=new BasicDBObject("$limit",page_size);
            AggregationOutput output_user = cursor_user.aggregate(match_user, group_user,basicSkip,basicLimit);

            //获取企业下的所有导购信息的集合
            List<HashMap<String,Object>> userList=new ArrayList<HashMap<String, Object>>();
            for(DBObject dbObject:output_user.results()) {

                //获取每个导购下的所有信息
                HashMap<String, Object> userMap = new HashMap<String, Object>();
                //分组结果中无数据时的默认值
                userMap.put("phoneCount", "0");
                userMap.put("smsCount", "0");
                userMap.put("wechatCount", "0");
                userMap.put("user_phoneCount", "0");
                userMap.put("user_smsCount", "0");
                userMap.put("user_wechatCount", "0");

                BasicDBObject basicDBObject2 = (BasicDBObject) dbObject;
                //获取user_code
                String user_code = basicDBObject2.get("_id").toString();

                //....................................获取user...............................
                BasicDBObject basicuser= new BasicDBObject();
                basicuser.put("corp_code", corp_code);
                basicuser.put("user_id", user_code);
                DBCursor dbCursor_user= cursor_user.find(basicuser);
                DBObject sort_obj = new BasicDBObject("created_date", -1);
                dbCursor_user= dbCursor_user.sort(sort_obj);
                List<DBObject> dbObjectList=dbCursor_user.toArray();
                String user_name="";
                String store_name="";
                String brand_name="";
                String user_phone="";
                String store_code="";
                if(dbObjectList.size()>0) {
                    DBObject dbObject_user = dbObjectList.get(0);
                     user_name = dbObject_user.get("user_name") == null ? "" : dbObject_user.get("user_name").toString();
                     store_name = dbObject_user.get("store_name") == null ? "" : dbObject_user.get("store_name").toString();
                     brand_name = dbObject_user.get("brand_name") == null ? "" : dbObject_user.get("brand_name").toString();
                     user_phone = dbObject_user.get("user_phone") == null ? "" : dbObject_user.get("user_phone").toString();
                     store_code = dbObject_user.get("store_code") == null ? "" : dbObject_user.get("store_code").toString();
                }
//                else {
//                    User user=null;
//                    if(!user_code.equals("")&&user_code!=null) {
//                        user = userService.selectUserByCode(corp_code, user_code, "Y");
//                    }
//                    if(user!=null) {
//                        //所属店铺
//                        store_code = user.getStore_code();
//                        store_code=store_code.replace(Common.SPECIAL_HEAD,"");
//                        List<Store> storeList = storeService.selectStore(corp_code, store_code);
//
//                        for (int j = 0; j < storeList.size(); j++) {
//                            store_name += storeList.get(j).getStore_name() + ",";
//                        }
//                        if (store_name.endsWith(",")) {
//                            store_name = store_name.substring(0, store_name.length() - 1);
//                        }
//
//                        String brand_code="";
//                        for(int b=0;b<storeList.size();b++){
//                            brand_code+=storeList.get(b).getBrand_code()+",";
//                        }
//                        //所属品牌
//                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                        String[] brandCodes = brand_code.split(",");
//                        List<Brand> brandList = brandService.selectBrandByLabel(corp_code, brandCodes);
//                        for (int i = 0; i < brandList.size(); i++) {
//                            brand_name += brandList.get(i).getBrand_name() + ",";
//                        }
//                        if (brand_name.endsWith(",")) {
//                            brand_name = brand_name.substring(0, brand_name.length() - 1);
//                        }
//                        user_name=user.getUser_name();
//                        user_phone=user.getPhone();
//                    }
//                }
                //对导购下的回访类型分组
                BasicDBObject basicDB1 = new BasicDBObject();
                basicDB1.put("corp_code", corp_code);
                basicDB1.put("user_id", user_code);
                BasicDBObject match_action = new BasicDBObject("$match", basicDB1);
                /* Group操作*/
                DBObject groupField_action = new BasicDBObject("_id", "$action");
                groupField_action.put("count", new BasicDBObject("$sum", 1));
                DBObject group_action = new BasicDBObject("$group", groupField_action);
                AggregationOutput output_action = cursor_user.aggregate(match_action, group_action);
                //回访总数集合
                List<String> countList = new ArrayList<String>();
                List<String> countUserList = new ArrayList<String>();
                for (DBObject dbObject_action : output_action.results()) {
                    BasicDBObject basicDB_action = (BasicDBObject) dbObject_action;
                    String action = basicDB_action.getString("_id");
                        if (action.equals("1")) {
                            //电话回访总条数
                            String phoneCount = basicDB_action.getString("count");
                            countList.add(phoneCount);
                            userMap.put("phoneCount", phoneCount);
                            //电话回访总人数
                            String user_phoneCount = vipBackRecordService.getUserCount(cursor_user, corp_code, user_code, action);
                            userMap.put("user_phoneCount", user_phoneCount);
                            countUserList.add(user_phoneCount);

                        } else if (action.equals("2")) {
                            //短信回访总条数
                            String smsCount = basicDB_action.getString("count");
                            userMap.put("smsCount", smsCount);
                            countList.add(smsCount);
                            //短信回访总人数
                            String user_smsCount = vipBackRecordService.getUserCount(cursor_user, corp_code, user_code, action);
                            userMap.put("user_smsCount", user_smsCount);
                            countUserList.add(user_smsCount);
                        } else if(action.equals("3")){
                           //微信回访总条数
                            String wechatCount = basicDB_action.getString("count");
                            userMap.put("wechatCount", wechatCount);
                            countList.add(wechatCount);
                            //微信回访总人数
                            String user_wechatCount = vipBackRecordService.getUserCount(cursor_user, corp_code, user_code, action);
                            userMap.put("user_wechatCount", user_wechatCount);
                            countUserList.add(user_wechatCount);
                    }
                }
                //回访记录总数
                int count = 0;
                for (int c = 0; c < countList.size(); c++) {
                    count += Integer.parseInt(countList.get(c));
                }
                //每个回访类型相对应的回访人数的总和
                int user_count = 0;
                for (int u = 0; u < countUserList.size(); u++) {
                    user_count += Integer.parseInt(countUserList.get(u));
                }
                userMap.put("user_code", user_code);
                userMap.put("user_name", user_name);
                userMap.put("store_name", store_name);
                userMap.put("phone", user_phone);
                userMap.put("brand_name", brand_name);
                userMap.put("user_count", user_count);
                userMap.put("count", count);
                //覆盖会员数
                BasicDBObject basic = new BasicDBObject();
                basic.put("corp_code", corp_code);
                basic.put("user_id", user_code);
                basic.put("vip_id", new BasicDBObject("$ne", null));
                int coverCount = cursor_user.distinct("vip_id", basic).size();
                userMap.put("coverCount", coverCount);
                //全部会员
                DataBox dataBox = iceInterfaceService.getAllVipByUser(corp_code, user_code,store_code);
                String value = dataBox.data.get("message").value.toString();
                JSONObject jsonObject1=JSON.parseObject(value);
                userMap.put("allVip", jsonObject1.get("count"));
                userList.add(userMap);
            }
            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("list",userList);
            jsonObject1.put("pageNumber",page_num);
            jsonObject1.put("pageSize",page_size);
            int count=cursor_user.distinct("user_id",basDb).size();
            jsonObject1.put("count",count);
            int pages = 0;
            if (count % page_size == 0) {
                pages = count / page_size;
            } else {
                pages = count / page_size + 1;
            }
            jsonObject1.put("pages",pages);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject1.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }



    //导出
    @RequestMapping(value = "/exportExecl",method = RequestMethod.POST)
    @ResponseBody
    public  String getBackRecordExportExecl(HttpServletRequest request,HttpServletResponse response){
        DataBean dataBean=new DataBean();
        String errormessage = "数据异常，导出失败";
        try{
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor_user=mongoTemplate.getCollection(CommonValue.table_vip_back_record);
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
       //     String corp_code = jsonObject.get("corp_code").toString();
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            if(role_code.equals(Common.ROLE_SYS)){
                corp_code="C10000";
            }
            int page_num=Integer.parseInt(jsonObject.getString("page_num"));
            int page_size=Integer.parseInt(jsonObject.getString("page_size"));

            BasicDBObject basicDBObject=new BasicDBObject();
            //搜索
            String search_value=jsonObject.get("searchValue").toString();
            String screen=jsonObject.getString("list").toString();

            //搜索，筛选
            //MongoDB对企业下的导购分组
            BasicDBObject basicDBObject1=new BasicDBObject();
            basicDBObject1.put("corp_code",corp_code);
            BasicDBList dbList=new BasicDBList();
            dbList.add(new BasicDBObject("user_id",new BasicDBObject("$ne","")));
            dbList.add(new BasicDBObject("user_id",new BasicDBObject("$ne",null)));
            dbList.add(new BasicDBObject("user_id",new BasicDBObject("$ne","null")));
            if(!search_value.equals("")) {
                basicDBObject=vipBackRecordService.getQuery(search_value);
                dbList.add(basicDBObject);
                dbList.add(basicDBObject1);
            }else if(!screen.equals("")){
                basicDBObject= vipBackRecordService.getScreen(message,corp_code);
                dbList.add(basicDBObject);
                dbList.add(basicDBObject1);
            }else{
                dbList.add(basicDBObject1);
            }
            BasicDBObject basDb=new BasicDBObject();
            basDb.put("$and",dbList);

            BasicDBObject match_user = new BasicDBObject("$match", basDb);
            /* Group操作*/
            DBObject groupField = new BasicDBObject("_id","$user_id");
            groupField.put("count", new BasicDBObject("$sum", 1));
            DBObject group_user = new BasicDBObject("$group", groupField);
            //分页
            BasicDBObject basicSkip=new BasicDBObject("$skip",(page_num - 1) * page_size);
            BasicDBObject basicLimit=new BasicDBObject("$limit",page_size);
            AggregationOutput output_user = cursor_user.aggregate(match_user,group_user,basicSkip,basicLimit);
            //  List<String> storeIdList=new ArrayList<String>();
            //获取企业下的所有导购信息的集合
            List<HashMap<String,Object>> userList=new ArrayList<HashMap<String, Object>>();
            for(DBObject dbObject:output_user.results()) {
                //获取每个导购下的所有信息
                HashMap<String, Object> userMap = new HashMap<String, Object>();
                //分组结果中无数据时的默认值
                userMap.put("phoneCount", "0");
                userMap.put("smsCount", "0");
                userMap.put("wechatCount", "0");
                userMap.put("user_phoneCount", "0");
                userMap.put("user_smsCount", "0");
                userMap.put("user_wechatCount", "0");

                BasicDBObject basicDBObject2 = (BasicDBObject) dbObject;
                //获取user_code
                String user_code = basicDBObject2.get("_id").toString();

                //....................................获取user...............................
                BasicDBObject basicuser= new BasicDBObject();
                basicuser.put("corp_code", corp_code);
                basicuser.put("user_id", user_code);
                DBCursor dbCursor_user= cursor_user.find(basicuser);
                DBObject sort_obj = new BasicDBObject("created_date", -1);
                dbCursor_user= dbCursor_user.sort(sort_obj);
                List<DBObject> dbObjectList=dbCursor_user.toArray();
                String user_name="";
                String store_name="";
                String brand_name="";
                String user_phone="";
                String store_code="";
                if(dbObjectList.size()>0) {
                    DBObject dbObject_user = dbObjectList.get(0);
                     user_name = dbObject_user.get("user_name") == null ? "" : dbObject_user.get("user_name").toString();
                     store_name = dbObject_user.get("store_name") == null ? "" : dbObject_user.get("store_name").toString();
                     brand_name = dbObject_user.get("brand_name") == null ? "" : dbObject_user.get("brand_name").toString();
                     user_phone = dbObject_user.get("user_phone") == null ? "" : dbObject_user.get("user_phone").toString();
                     store_code = dbObject_user.get("store_code") == null ? "" : dbObject_user.get("store_code").toString();
                }
//                else{
//                    User user=null;
//                if(!user_code.equals("")&&user_code!=null) {
//                     user = userService.selectUserByCode(corp_code, user_code, "Y");
//                }
//                if(user!=null) {
//                    //所属店铺
//                    store_code = user.getStore_code();
//                    store_code=store_code.replace(Common.SPECIAL_HEAD,"");
//                    List<Store> storeList = storeService.selectStore(corp_code, store_code);
//
//                    for (int j = 0; j < storeList.size(); j++) {
//                        store_name += storeList.get(j).getStore_name() + ",";
//                    }
//                    if (store_name.endsWith(",")) {
//                        store_name = store_name.substring(0, store_name.length() - 1);
//                    }
//
//                    String brand_code="";
//                    for(int b=0;b<storeList.size();b++){
//                        brand_code+=storeList.get(b).getBrand_code()+",";
//                    }
//                    //所属品牌
//                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                    String[] brandCodes = brand_code.split(",");
//                    List<Brand> brandList = brandService.selectBrandByLabel(corp_code, brandCodes);
//                    for (int i = 0; i < brandList.size(); i++) {
//                        brand_name += brandList.get(i).getBrand_name() + ",";
//                    }
//                    if (brand_name.endsWith(",")) {
//                        brand_name = brand_name.substring(0, brand_name.length() - 1);
//                    }
//                    user_name=user.getUser_name();
//                    user_phone=user.getPhone();
//                   }
//                }
                //对导购下的回访类型分组
                BasicDBObject basicDB1 = new BasicDBObject();
                basicDB1.put("corp_code", corp_code);
                basicDB1.put("user_id", user_code);
                BasicDBObject match_action = new BasicDBObject("$match", basicDB1);
                /* Group操作*/
                DBObject groupField_action = new BasicDBObject("_id", "$action");
                groupField_action.put("count", new BasicDBObject("$sum", 1));
                DBObject group_action = new BasicDBObject("$group", groupField_action);
                AggregationOutput output_action = cursor_user.aggregate(match_action, group_action);
                //回访总数集合
                List<String> countList = new ArrayList<String>();
                List<String> countUserList = new ArrayList<String>();
                for (DBObject dbObject_action : output_action.results()) {
                    BasicDBObject basicDB_action = (BasicDBObject) dbObject_action;
                    String action = basicDB_action.getString("_id");
                        if (action.equals("1")) {
                            //电话回访总条数
                            String phoneCount = basicDB_action.getString("count");
                            countList.add(phoneCount);
                            userMap.put("phoneCount", phoneCount);
                            //电话回访总人数
                            String user_phoneCount = vipBackRecordService.getUserCount(cursor_user, corp_code, user_code, action);
                            userMap.put("user_phoneCount", user_phoneCount);
                            countUserList.add(user_phoneCount);

                        } else if (action.equals("2")) {
                            //短信回访总条数
                            String smsCount = basicDB_action.getString("count");
                            userMap.put("smsCount", smsCount);
                            countList.add(smsCount);
                            //短信回访总人数
                            String user_smsCount = vipBackRecordService.getUserCount(cursor_user, corp_code, user_code, action);
                            userMap.put("user_smsCount", user_smsCount);
                            countUserList.add(user_smsCount);
                        } else if(action.equals("3")){
                        //微信回访总条数
                        String wechatCount = basicDB_action.getString("count");
                        userMap.put("wechatCount", wechatCount);
                        countList.add(wechatCount);
                        //微信回访总人数
                        String user_wechatCount = vipBackRecordService.getUserCount(cursor_user, corp_code, user_code, action);
                        userMap.put("user_wechatCount", user_wechatCount);
                        countUserList.add(user_wechatCount);
                    }
                }
                //回访记录总数
                int count = 0;
                for (int c = 0; c < countList.size(); c++) {
                    count += Integer.parseInt(countList.get(c));
                }
                //每个回访类型相对应的回访人数的总和
                int user_count = 0;
                for (int u = 0; u < countUserList.size(); u++) {
                    user_count += Integer.parseInt(countUserList.get(u));
                }
                userMap.put("user_code", user_code);
                userMap.put("user_name", user_name);
                userMap.put("store_name", store_name);
                userMap.put("phone", user_phone);
                userMap.put("brand_name", brand_name);
                userMap.put("user_count", user_count);
                userMap.put("count", count);
                //覆盖会员数
                BasicDBObject basic = new BasicDBObject();
                basic.put("corp_code", corp_code);
                basic.put("user_id", user_code);
                basic.put("vip_id", new BasicDBObject("$ne", null));
                int coverCount = cursor_user.distinct("vip_id", basic).size();
                userMap.put("coverCount", coverCount);
                //全部会员
                DataBox dataBox = iceInterfaceService.getAllVipByUser(corp_code, user_code,store_code);
                String value = dataBox.data.get("message").value.toString();
                JSONObject jsonObject1=JSON.parseObject(value);
                userMap.put("allVip", jsonObject1.getString("count"));
                userList.add(userMap);
            }
            /**
             * 导出操作..................................................
             */
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(userList);
            if (userList.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, userList, map, response, request, "回访分析");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(errormessage);
        }
        return  dataBean.getJsonStr();
    }

}
