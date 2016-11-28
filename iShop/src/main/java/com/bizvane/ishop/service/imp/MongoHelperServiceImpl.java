package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.mongodb.*;

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
            if (CheckUtils.checkJson(screen_value) == false && !screen_key.equals("operation_time")) {
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                values.add(new BasicDBObject(screen_key, pattern));
            }
            if (screen_key.equals("operation_time")) {
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

        }
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
            if (CheckUtils.checkJson(screen_value) == false && !screen_key.equals("created_date") && !screen_key.equals("count")) {
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
        queryCondition.put("$and", values);
        return queryCondition;
    }

    //DBCursor数据集转arrayList+id+can_login+品牌名(登录日志)
    public static ArrayList dbCursorToList_canLogin(DBCursor dbCursor, List<User> users) {
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            String user_id = obj.get("user_id").toString();
            String brand_name = obj.get("brand_name").toString();
            String replaceStr = WebUtils.StringFilter(brand_name);
            obj.put("brand_name", replaceStr);
            obj.put("id", id);
            obj.removeField("_id");
            for (int i = 0; i < users.size(); i++) {
                if (user_id.equals(users.get(i).getUser_code())) {
                    obj.put("user_can_login", "离职");
                } else {
                    obj.put("user_can_login", "在职");
                }
            }
            list.add(obj.toMap());
        }
        return list;
    }
}
