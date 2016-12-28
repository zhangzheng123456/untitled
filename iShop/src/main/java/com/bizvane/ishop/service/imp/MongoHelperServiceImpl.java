package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.mongodb.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
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
            if (!screen_value.equals("") && CheckUtils.checkJson(screen_value) == false && !screen_key.equals("operation_time")&& !screen_key.equals("created_date")) {
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
        if (values.size()>0)
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
            if (!screen_value.equals("") && !screen_key.equals("user_can_login") && CheckUtils.checkJson(screen_value) == false && !screen_key.equals("created_date") && !screen_key.equals("count")) {
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
            if (screen_key.equals("count")) {
                JSONObject time_count = JSON.parseObject(screen_value);
                String type = time_count.get("type").toString();
                String value = time_count.get("value").toString();
                System.out.println("=========count:" + value);
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
        if (values.size()>0)
            queryCondition.put("$and", values);
        return queryCondition;
    }

    //DBCursor数据集转arrayList+id+can_login+品牌名(登录日志)
    public static ArrayList dbCursorToList_canLogin(DBCursor dbCursor, List<User> users,String user_can_login) {
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            String user_id="";
            String brand_name="";
//            System.out.println("====id======="+id);
//            System.out.println("====user_id======="+String.valueOf(obj.get("user_id").toString()));
            if(null!=obj.get("user_id")&&!String.valueOf(obj.get("user_id").toString()).equals("null") && obj.containsField("user_id")  ){
                user_id =obj.get("user_id").toString();
            }else{
                user_id="";
            }
            if(null!=obj.get("brand_name")&&!String.valueOf(obj.get("brand_name").toString()).equals("null") && obj.containsField("brand_name")){
                brand_name =obj.get("brand_name").toString();
            }else{
                brand_name="";
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
            list.add(obj.toMap());
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
        if (values.size()>0)
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
            if(label_type==null||label_type.equals("")){
                obj.put("label_type", "");
            }else if(label_type.equals("user")){
                obj.put("label_type", "用户");
            }else if(label_type.equals("sys")){
                obj.put("label_type", "系统");
            }else if(label_type.equals("org")){
                obj.put("label_type", "企业");
            }
            list.add(obj.toMap());
        }
        return list;
    }

    //DBCursor数据集转换status类型（签到管理）
    public  static  ArrayList dbCursorToList_status(DBCursor dbCursor){

        ArrayList list=new ArrayList();
        while(dbCursor.hasNext()){

            DBObject object=dbCursor.next();
            String id = object.get("_id").toString();
            object.put("id", id);
            object.removeField("_id");
            String status=object.get("status").toString();
            if(status==null||status.equals("")){
                object.put("status","");
            }else if(status.equals("0")){
                object.put("status","签到");
            }
            else if(status.equals("-1")){
                object.put("status","签退");
            }
            list.add(object.toMap());
        }

        return  list;
    }

    public static DBObject selectByCode(String corp_code,String d_match_code,String user_code,String operate_type)throws Exception{
        MongoDBClient mongodbClient=new MongoDBClient();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);
        BasicDBObject basicDBObject = MongoUtils.andOperation3(corp_code, d_match_code,user_code,operate_type);
        DBCursor dbObjects = cursor.find(basicDBObject);
        DBObject object=null;
        while (dbObjects.hasNext()) {
            object  = dbObjects.next();
        }
        return object;
    }

    //DBCursor数据集转arrayList+id
    public static ArrayList dbCursorToList_shop(DBCursor dbCursor,String user_code) throws Exception {
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            String corp_code = obj.get("corp_code").toString();
            String d_match_code = obj.get("d_match_code").toString();
            DBObject object = selectByCode(corp_code, d_match_code, user_code, "like");
            String like_status="N";
            String collect_status="N";
            if(object!=null){
                like_status = object.get("status").toString();
            }
            DBObject object2 = selectByCode(corp_code, d_match_code, user_code, "collect");
            if(object2!=null){
                collect_status = object.get("status").toString();
            }
            obj.put("like_status", like_status);
            obj.put("collect_status", collect_status);
            obj.removeField("_id");
            list.add(obj.toMap());
        }
        return list;
    }

}
