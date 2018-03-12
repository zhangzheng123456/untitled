package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.User;

import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.NumberUtil;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.mongodb.*;

import org.springframework.data.mongodb.core.MongoTemplate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by PC on 2016/11/28.
 */
public class MongoHelperServiceImpl {
    //多个“与”查询(筛选)（操作日志）
    public static BasicDBObject andUserOperScreen(JSONArray array) {
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        for (int i = 0; i < array.size(); i++) {
            String info = array.get(i).toString();
            JSONObject json = JSONObject.parseObject(info);
            String screen_key = json.get("screen_key").toString();
            String screen_value = json.get("screen_value").toString();

            if (!screen_value.equals("") && CheckUtils.checkJson(screen_value) == false && !screen_key.equals("operation_time") && !screen_key.equals("created_date")) {
                System.out.println(screen_value + "---筛选条件----" + screen_key);
                if (screen_value.startsWith("|") || screen_value.startsWith(",") || screen_value.startsWith("，")) {
                    screen_value = screen_value.substring(1);
                }
                if (screen_value.endsWith("|") || screen_value.endsWith(",") || screen_value.endsWith("，")) {
                    screen_value = screen_value.substring(0, screen_value.length() - 1);
                }
                screen_value = screen_value.replaceAll(",", "|");
                screen_value = screen_value.replaceAll("，", "|");
                screen_value = WebUtils.El2Str1(screen_value);
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
//                if(!screen_key.equals("store_name")) {
                values.add(new BasicDBObject(screen_key, pattern));
//                }
            }
            if (screen_key.equals("operation_time") || screen_key.equals("created_date") || screen_key.equals("message_date")) {
                JSONObject date = JSON.parseObject(screen_value);
                String start = date.get("start").toString();//2016-11-25 14:43:15

                String end = date.get("end").toString();
                if (!start.equals("") && start != null) {
                    System.out.println("=========start:" + start);
                    values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, start + " 00:00:00")));
                }
                if (!end.equals("") && end != null) {
                    System.out.println("=========end:" + end);
                    values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.LTE, end + " 23:59:59")));
                }
            }
//            if (screen_key.equals("created_date")) {
//                JSONObject date = JSON.parseObject(screen_value);
//                String start = date.get("start").toString();//2016-11-25 14:43:15
//
//                String end = date.get("end").toString();
//                if (!start.equals("") && start != null) {
//                    System.out.println("=========start:" + start);
//                    values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, start + " 00:00:00")));
//                }
//                if (!end.equals("") && end != null) {
//                    System.out.println("=========end:" + end);
//                    values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.LTE, end + " 23:59:59")));
//                }
//            }
        }
        if (values.size() > 0)
            queryCondition.put("$and", values);
        return queryCondition;
    }

    //多个“与”查询(筛选)登录日志
    public static BasicDBObject andLoginlogScreen(JSONArray array) {
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        for (int i = 0; i < array.size(); i++) {
            String info = array.get(i).toString();
            JSONObject json = JSONObject.parseObject(info);
            String screen_key = json.get("screen_key").toString();
            String screen_value = json.get("screen_value").toString();
            if (!screen_value.equals("") && !screen_key.equals("user_can_login") && CheckUtils.checkJson(screen_value) == false && !screen_key.equals("created_date") && !screen_key.equals("count") && !screen_key.equals("d_match_likeCount")) {
                if (screen_value.startsWith("|") || screen_value.startsWith(",") || screen_value.startsWith("，")) {
                    screen_value = screen_value.substring(1);
                }
                if (screen_value.endsWith("|") || screen_value.endsWith(",") || screen_value.endsWith("，")) {
                    screen_value = screen_value.substring(0, screen_value.length() - 1);
                }
                screen_value = screen_value.replaceAll(",", "|");
                screen_value = screen_value.replaceAll("，", "|");
                screen_value = WebUtils.El2Str1(screen_value);
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                values.add(new BasicDBObject(screen_key, pattern));
            }
            if (screen_key.equals("created_date")) {
                JSONObject date = JSON.parseObject(screen_value);
                String start = date.get("start").toString();

                String end = date.get("end").toString();
                if (!start.equals("") && start != null) {
                    System.out.println("=========start:" + start);
                    values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, start)));
                }
                if (!end.equals("") && end != null) {
                    System.out.println("=========end:" + end);
                    values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.LTE, end)));
                }
            }
            if (screen_key.equals("count") || screen_key.equals("d_match_likeCount") || screen_key.equals("d_match_commentCount") || screen_key.equals("d_match_collectCount")) {
                JSONObject time_count = JSON.parseObject(screen_value);
                String type = time_count.get("type").toString();
                String value = time_count.get("value").toString();
                System.out.println("=========count:" + value);
                if (!value.equals("")) {
                    if (type.equals("gt")) {
                        //大于
                        values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, Integer.parseInt(value))));
                    } else if (type.equals("lt")) {
                        //小于
                        values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.LTE, Integer.parseInt(value))));
                    } else if (type.equals("between")) {
                        //介于
                        JSONObject values2 = JSONObject.parseObject(value);
                        String start = values2.get("start").toString();
                        String end = values2.get("end").toString();
                        if (!start.equals("") && start != null) {
                            values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, Integer.parseInt(start))));
                        }
                        if (!end.equals("") && end != null) {
                            values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.LTE, Integer.parseInt(end))));
                        }
                    } else if (type.equals("eq")) {
                        //等于
                        values.add(new BasicDBObject(screen_key, Integer.parseInt(value)));
                    } else if (type.equals("all")) {
                        values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, 0)));
                    }
                }
            }
        }
        if (values.size() > 0)
            queryCondition.put("$and", values);
        return queryCondition;
    }


    //多个“与”查询(筛选)登录日志
    public static BasicDBObject andStartStoreLogListByScreen(JSONArray array) {
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        for (int i = 0; i < array.size(); i++) {
            String info = array.get(i).toString();
            JSONObject json = JSONObject.parseObject(info);
            String screen_key = json.get("screen_key").toString();
            String screen_value = json.get("screen_value").toString();
            if (!screen_value.equals("") && CheckUtils.checkJson(screen_value) == false && !screen_key.equals("start_time") && !screen_key.equals("end_time") && !screen_key.equals("time_count")) {
                if (screen_value.startsWith("|") || screen_value.startsWith(",") || screen_value.startsWith("，")) {
                    screen_value = screen_value.substring(1);
                }
                if (screen_value.endsWith("|") || screen_value.endsWith(",") || screen_value.endsWith("，")) {
                    screen_value = screen_value.substring(0, screen_value.length() - 1);
                }
                screen_value = screen_value.replaceAll(",", "|");
                screen_value = screen_value.replaceAll("，", "|");
                screen_value = WebUtils.El2Str1(screen_value);
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                values.add(new BasicDBObject(screen_key, pattern));
            }
            if (screen_key.equals("start_time") || screen_key.equals("end_time")) {
                JSONObject date = JSON.parseObject(screen_value);
                String start = date.get("start").toString();

                String end = date.get("end").toString();
                if (!start.equals("") && start != null) {
                    System.out.println("=========start:" + start);
                    values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, start)));
                }
                if (!end.equals("") && end != null) {
                    System.out.println("=========end:" + end);
                    values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.LTE, end)));
                }
            }
            if (screen_key.equals("time_count")) {
                JSONObject time_count = JSON.parseObject(screen_value);
                String type = time_count.get("type").toString();
                String value = time_count.get("value").toString();
                System.out.println("=========count:" + value);
                if (!value.equals("")) {
                    if (type.equals("gt")) {
                        //大于
                        values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, Integer.parseInt(value))));
                    } else if (type.equals("lt")) {
                        //小于
                        values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.LTE, Integer.parseInt(value))));
                    } else if (type.equals("between")) {
                        //介于
                        JSONObject values2 = JSONObject.parseObject(value);
                        String start = values2.get("start").toString();
                        String end = values2.get("end").toString();
                        if (!start.equals("") && start != null) {
                            values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, Integer.parseInt(start))));
                        }
                        if (!end.equals("") && end != null) {
                            values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.LTE, Integer.parseInt(end))));
                        }
                    } else if (type.equals("eq")) {
                        //等于
                        values.add(new BasicDBObject(screen_key, Integer.parseInt(value)));
                    } else if (type.equals("all")) {
                        values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, 0)));
                    }
                }
            }
        }
        if (values.size() > 0)
            queryCondition.put("$and", values);
        return queryCondition;
    }

    //DBCursor数据集转arrayList+id+can_login+品牌名(登录日志)
    public static ArrayList dbCursorToList_smslog(DBCursor dbCursor) {
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            obj.put("id", id);
            obj.removeField("_id");

        }
        return list;
    }

    //DBCursor数据集转arrayList+id+can_login+品牌名(登录日志)
    public static ArrayList dbCursorToList_canLogin(DBCursor dbCursor, List<User> users, String user_can_login) {
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            String user_id = "";
            String brand_name = "";
//            System.out.println("====id======="+id);
//            System.out.println("====user_id======="+String.valueOf(obj.get("user_id").toString()));
            if (null != obj.get("user_id") && !String.valueOf(obj.get("user_id").toString()).equals("null") && obj.containsField("user_id")) {
                user_id = obj.get("user_id").toString();
            } else {
                user_id = "";
            }
            if (null != obj.get("brand_name") && !String.valueOf(obj.get("brand_name").toString()).equals("null") && obj.containsField("brand_name")) {
                brand_name = obj.get("brand_name").toString();
            } else {
                brand_name = "";
            }
            String replaceStr = WebUtils.StringFilter(brand_name);
            obj.put("brand_name", replaceStr);
            obj.put("id", id);
            obj.removeField("_id");
            for (int i = 0; i < users.size(); i++) {
                if (user_id.equals(users.get(i).getUser_code())) {
                    obj.put("user_can_login", "离职");
                    break;
                } else {
                    obj.put("user_can_login", "在职");
                }
            }
            if (user_can_login.equals("Y")) {
                if (obj.get("user_can_login").toString().equals("在职")) {
                    list.add(obj.toMap());
                }
            } else if (user_can_login.equals("N")) {
                if (obj.get("user_can_login").toString().equals("离职")) {
                    list.add(obj.toMap());
                }
            } else {
                list.add(obj.toMap());
            }

        }
        return list;
    }


    //多个“与”查询(筛选)签到管理

    public static BasicDBObject andSignScreen(JSONArray array) {
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        //...
        for (int i = 0; i < array.size(); i++) {
            String info = array.get(i).toString();
            JSONObject json = JSONObject.parseObject(info);
            String screen_key = json.get("screen_key").toString();
            String screen_value = json.get("screen_value").toString();
            if (!screen_value.equals("") && CheckUtils.checkJson(screen_value) == false && !screen_key.equals("sign_time")) {
                if (screen_value.startsWith("|") || screen_value.startsWith(",") || screen_value.startsWith("，")) {
                    screen_value = screen_value.substring(1);
                }
                if (screen_value.endsWith("|") || screen_value.endsWith(",") || screen_value.endsWith("，")) {
                    screen_value = screen_value.substring(0, screen_value.length() - 1);
                }
                screen_value = screen_value.replaceAll(",", "|");
                screen_value = screen_value.replaceAll("，", "|");
                screen_value = WebUtils.El2Str1(screen_value);
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                values.add(new BasicDBObject(screen_key, pattern));
            }
            if (screen_key.equals("modified_date")) {
                JSONObject date = JSON.parseObject(screen_value);
                String start = date.get("start").toString();

                String end = date.get("end").toString();
                if (!start.equals("") && start != null) {
                    System.out.println("=========start:" + start);
                    values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, start)));
                }
                if (!end.equals("") && end != null) {
                    end = end + " 23:59:59";
                    System.out.println("=========end:" + end);
                    values.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.LTE, end)));
                }
            }
        }
        if (values.size() > 0)
            queryCondition.put("$and", values);
        return queryCondition;
    }


    //DBCursor数据集转arrayList+id+标签类型(会员标签)
    public static ArrayList dbCursorToList_labelType(DBCursor dbCursor) {
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            obj.put("id", id);
            obj.removeField("_id");
            String label_type = obj.get("label_type").toString();
            if (label_type == null || label_type.equals("")) {
                obj.put("label_type", "");
            } else if (label_type.equals("user")) {
                obj.put("label_type", "用户");
            } else if (label_type.equals("sys")) {
                obj.put("label_type", "系统");
            } else if (label_type.equals("org")) {
                obj.put("label_type", "企业");
            }
            list.add(obj.toMap());
        }
        return list;
    }

    //DBCursor数据集转换status类型（签到管理）
    public static ArrayList dbCursorToList_status(DBCursor dbCursor) {

        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {

            DBObject object = dbCursor.next();
            String id = object.get("_id").toString();
            object.put("id", id);
            object.removeField("_id");
            String status = object.get("status").toString();
            if (!object.containsField("serial_number") || null == object.get("serial_number")) {
                object.put("serial_number", "");
            }

            if (null == object.get("location")) {
                object.put("location", "");
            }
            if (null == object.get("store_location") || object.get("store_location").equals("0.0")) {
                object.put("store_location", "");
            }
            if (status == null || status.equals("")) {
                object.put("status", "");
            } else if (status.equals("0")) {
                object.put("status", "签到");
            } else if (status.equals("-1")) {
                object.put("status", "签退");
            }
            list.add(object.toMap());
        }

        return list;
    }
    //DBCursor数据集转换类型（短信日志）
    public static ArrayList dbCursorToList_SmsLogApp(DBCursor dbCursor) {

        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {

            DBObject object = dbCursor.next();
            String id = object.get("_id").toString();
            object.put("id", id);
            object.removeField("_id");

            if (null == object.get("vip")) {
                object.put("vip_name", "");
            }else{
                try {
                    String vip = String.valueOf(object.get("vip"));
                    if (vip.contains("name_vip")) {
                        JSONArray array = JSONArray.parseArray(vip);
                        String vip_name = JSONObject.parseObject(String.valueOf(array.get(0))).getString("name_vip");
                        object.put("vip_name", vip_name);
                    } else {
                        object.put("vip_name", "");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    object.put("vip_name", "");
                }
            }
            object.removeField("vip");
            if(null == object.get("user_id")){
                object.put("user_id", "");
            }
            if(null == object.get("user_name")){
                object.put("user_name", "");
            }
            if(null == object.get("store_name")){
                object.put("store_name", "");
            }
            if(null == object.get("price")){
                object.put("price", "");
            }
            if(null == object.get("created_date")){
                object.put("created_date", "");
            }
            if(null == object.get("user")){
                object.put("user", "");
            }
            if(null == object.get("corp_name")){
                object.put("corp_name", "");
            }
            if(null == object.get("brand_name")){
                object.put("brand_name", "");
            }
            list.add(object.toMap());
        }

        return list;
    }
    //会员发展分析
    public static ArrayList<HashMap<String, String>> dbCursorToList_develop(DBCursor dbCursor) {

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        while (dbCursor.hasNext()) {
            DBObject object = dbCursor.next();
            HashMap<String, String> hashMap = new HashMap<String, String>();

            if (object.get("open_id") == null) {
                hashMap.put("open_id", "");
            } else {
                hashMap.put("open_id", object.get("open_id").toString());
            }
            if (object.get("nick_name") == null) {
                hashMap.put("nick_name", "");
            } else {
                hashMap.put("nick_name", object.get("nick_name").toString());
            }
            if (object.get("head_img") == null) {
                hashMap.put("head_img", "");
            } else {
                hashMap.put("head_img", object.get("head_img").toString());
            }
            if (object.get("user_code") == null) {
                hashMap.put("user_code", "");
            } else {
                hashMap.put("user_code", object.get("user_code").toString());
            }
            if (object.get("app_user_name") == null) {
                hashMap.put("app_user_name", "");
            } else {
                hashMap.put("app_user_name", object.get("app_user_name").toString());
            }
            if (object.get("app_id") == null) {
                hashMap.put("app_id", "");
            } else {
                hashMap.put("app_id", object.get("app_id").toString());
            }
            if (object.get("app_name") == null) {
                hashMap.put("app_name", "");
            } else {
                hashMap.put("app_name", object.get("app_name").toString());
            }

            //修改时间
            if (object.get("modified_date") == null) {
                hashMap.put("date", "");
            } else {
                hashMap.put("date", object.get("modified_date").toString());
            }

            //获取user集合
            String jsString = object.get("user").toString();
            JSONObject jsonObject = JSON.parseObject(jsString);
            if (jsonObject.get("user_name") == null) {
                hashMap.put("user_name", "");
            } else {
                hashMap.put("user_name", jsonObject.get("user_name").toString());
            }
            if (jsonObject.get("phone") == null) {
                hashMap.put("phone", "");
            } else {
                hashMap.put("phone", jsonObject.get("phone").toString());
            }
            if (jsonObject.get("store_code") == null) {
                hashMap.put("store_code", "");
            }else {
                hashMap.put("store_code", jsonObject.get("store_code").toString());
            }
            if (null != object.get("store_code")) {
                hashMap.put("store_code", object.get("store_code").toString());
            }

            list.add(hashMap);
        }

        return list;
    }

    public static DBObject selectByCode(String corp_code, String d_match_code, String user_code, String operate_type) throws Exception {
        MongoDBClient mongodbClient = new MongoDBClient();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);
        BasicDBObject basicDBObject = MongoUtils.andOperation3(corp_code, d_match_code, user_code, operate_type);
        DBCursor dbObjects = cursor.find(basicDBObject);
        DBObject object = null;
        while (dbObjects.hasNext()) {
            object = dbObjects.next();
        }
        return object;
    }

    public static int selectShareCount(String corp_code, String d_match_code) throws Exception {
        MongoDBClient mongodbClient = new MongoDBClient();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_log_production_share);
        BasicDBObject queryCondition_2 = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        Pattern pattern = Pattern.compile("^.*" + d_match_code + ".*$", Pattern.CASE_INSENSITIVE);
        values.add(new BasicDBObject("product_url", pattern));
        values.add(new BasicDBObject("corp_code", corp_code));
        values.add(new BasicDBObject("source", "Xiuda"));
        queryCondition_2.put("$and", values);
        DBCursor dbObjects = cursor.find(queryCondition_2);
        int count = dbObjects.count();
        return count;
    }


    //DBCursor数据集转arrayList+id
    public static ArrayList dbCursorToList_shop(DBCursor dbCursor, String user_code) throws Exception {
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            obj.put("id", id);
            String corp_code = obj.get("corp_code").toString();
            String d_match_code = obj.get("d_match_code").toString();
            String isactive = obj.get("isactive").toString();
            DBObject object = selectByCode(corp_code, d_match_code, user_code, "like");
            String like_status = "N";
            String collect_status = "N";
            if (object != null) {
                like_status = object.get("status").toString();
            }
            DBObject object2 = selectByCode(corp_code, d_match_code, user_code, "collect");
            if (object2 != null) {
                collect_status = object2.get("status").toString();
            }
            obj.put("like_status", like_status);
            obj.put("collect_status", collect_status);
            obj.removeField("_id");
            if (isactive == null || isactive.equals("")) {
                obj.put("isactive", "");
            } else if (isactive.equals("Y")) {
                obj.put("isactive", "是");
            } else if (isactive.equals("N")) {
                obj.put("isactive", "否");
            }
            if (null == obj.get("shopMoney") || "".equals(obj.get("shopMoney"))) {
                obj.put("shopMoney", 0);
            } else {
                obj.put("shopMoney", String.valueOf(obj.get("shopMoney")));
            }
            if (null == obj.get("pageViews") || "".equals(obj.get("pageViews"))) {
                obj.put("pageViews", 0);
            } else {
                obj.put("pageViews", String.valueOf(obj.get("pageViews")));
            }
            if (null == obj.get("shopCount") || "".equals(obj.get("shopCount"))) {
                obj.put("shopCount", 0);
            } else {
                obj.put("shopCount", String.valueOf(obj.get("shopCount")));
            }
            int count=0;
            if (null == obj.get("shareCount") || "".equals(obj.get("shareCount"))) {
                obj.put("shareCount", 0);
                count=0;
            } else {
                obj.put("shareCount", String.valueOf(obj.get("shareCount")));
                count=Integer.parseInt(String.valueOf(obj.get("shareCount")));
            }
//            int count = selectShareCount(corp_code, d_match_code);
//            obj.put("shareCount", count);
            if (count != 0) {
                if (null == obj.get("shopCount") || "".equals(obj.get("shopCount"))) {
                    obj.put("buyRate", "0.00%");
                } else {
                    obj.put("buyRate", (NumberUtil.keepPrecision(Double.parseDouble(NumberUtil.keepPrecision((Double.parseDouble(String.valueOf(obj.get("shopCount"))) / Double.parseDouble(count + "")))) * 100)) + "%");
                }
            } else {
                obj.put("buyRate", "0.00%");
            }
            if (null == obj.get("d_match_category") || "".equals(obj.get("d_match_category"))) {
                obj.put("d_match_category", "");
            } else {
                obj.put("d_match_category", String.valueOf(obj.get("d_match_category")));
            }
            list.add(obj.toMap());
        }
        return list;
    }

    //screen_value 支持多个字段模糊匹配
    public static Pattern screen_valueScreen(String screen_value) throws Exception{
        if (screen_value.startsWith("|") || screen_value.startsWith(",") || screen_value.startsWith("，")) {
            screen_value = screen_value.substring(1);
        }
        if (screen_value.endsWith("|") || screen_value.endsWith(",") || screen_value.endsWith("，")) {
            screen_value = screen_value.substring(0, screen_value.length() - 1);
        }
        screen_value = screen_value.replaceAll(",", "|");
        screen_value = screen_value.replaceAll("，", "|");
        screen_value = WebUtils.El2Str1(screen_value);
        Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
        return pattern;
    }


    //DBCursor数据集转arrayList+id
    public static ArrayList dbCursorToList_id(DBCursor dbCursor) {
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            obj.put("id", id);
            obj.removeField("_id");
            String state=obj.get("state").toString();
            if(state.equals("Y")){
                state="已提交";
            }else{
                state="未提交";
            }
            obj.put("state",state);
            list.add(obj.toMap());
        }
        return list;
    }
}
