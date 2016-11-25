package com.bizvane.ishop.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.User;
import com.mongodb.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by ZhouZhou on 2016/9/9.
 */
public class MongoUtils {

    //多个“与”查询(筛选)
    public static BasicDBObject andOperation(JSONArray array) {
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        for (int i = 0; i < array.size(); i++) {
            String info = array.get(i).toString();
            JSONObject json = JSONObject.parseObject(info);
            String screen_key = json.get("screen_key").toString();
            String screen_value = json.get("screen_value").toString();
            Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
            values.add(new BasicDBObject(screen_key, pattern));
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
            if (CheckUtils.checkJson(screen_value) == false) {
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                values.add(new BasicDBObject(screen_key, pattern));
            }
            if(screen_key.equals("created_date")){
                JSONObject date = JSON.parseObject(screen_value);
                String start = date.get("start").toString();

                String end = date.get("end").toString();
                if(!start.equals("") && start!=null){
                    values.add(new BasicDBObject(screen_key, new BasicDBObject("$gte",start)));
                }
                if(!end.equals("") && end!=null){
                    values.add(new BasicDBObject(screen_key, new BasicDBObject("$lte", end)));
                }
            }
            if(screen_key.equals("count")){
                JSONObject time_count = JSON.parseObject(screen_value);
                String type = time_count.get("type").toString();
                String value = time_count.get("value").toString();
                int count =0;
                if(value!=null && !value.equals("")) {
                    count = Integer.parseInt(value);
                }
                if (type.equals("gt")) {
                    //大于
                    values.add(new BasicDBObject(screen_key,new BasicDBObject("$gte",count)));
                } else if (type.equals("lt")) {
                    //小于
                    values.add(new BasicDBObject(screen_key,new BasicDBObject("$lte",count)));
                } else if (type.equals("between")) {
                    //介于
                    JSONObject values2 = JSONObject.parseObject(value);
                    String start = values2.get("start").toString();
                    String end = values2.get("end").toString();
                    if(!start.equals("") && start!=null){
                        values.add(new BasicDBObject(screen_key, new BasicDBObject("$gte", count)));
                    }
                    if(!end.equals("") && end!=null){
                        values.add(new BasicDBObject(screen_key, new BasicDBObject("$lte",count)));
                    }
                } else if (type.equals("eq")) {
                    //等于
                    values.add(new BasicDBObject(screen_key,count));
                } else if (type.equals("all")) {
                    Pattern pattern = Pattern.compile("^.*" + "" + ".*$", Pattern.CASE_INSENSITIVE);
                    values.add(new BasicDBObject(screen_key, pattern));
                }
            }

        }
        queryCondition.put("$and", values);
        return queryCondition;
    }

    //多个“或”查询(搜索)
    public static BasicDBObject orOperation(String[] column_names,String search_value) {
        Pattern pattern = Pattern.compile("^.*" + search_value + ".*$", Pattern.CASE_INSENSITIVE);
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        for (int i = 0; i < column_names.length; i++) {
            String column_name = column_names[i];
            values.add(new BasicDBObject(column_name, pattern));
        }
        queryCondition.put("$or", values);
        return queryCondition;
    }

    //in查询
    public static BasicDBObject inOperation(String[] args,String condition){
        BasicDBObject queryCondition = new BasicDBObject();
        //age in [13, 47]
        BasicDBList values = new BasicDBList();
        for (int i = 0; i < args.length; i++) {
            values.add(args[i]);
        }
        queryCondition.put(condition, new BasicDBObject("$in", values));
        return queryCondition;
    }

    //获取总页数
    public static int getPages(DBCursor dbCursor,int page_size){
        int pages = 0;
        int count = Integer.parseInt(String.valueOf(dbCursor.count()));
        if (count % page_size == 0) {
            pages = count / page_size;
        } else {
            pages = count / page_size + 1;
        }
        return pages;
    }

    //DBCursor数据集转arrayList
    public static ArrayList dbCursorToList(DBCursor dbCursor){
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            list.add(obj.toMap());

        }
        return list;
    }

    //DBCursor数据集转arrayList+id
    public static ArrayList dbCursorToList_id(DBCursor dbCursor){
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            obj.put("id",id);
            list.add(obj.toMap());
        }
        return list;
    }

    //DBCursor数据集转arrayList+id+can_login
    public static ArrayList dbCursorToList_canLogin(DBCursor dbCursor, List<User> users){
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            String user_id = obj.get("user_id").toString();
            obj.put("id",id);
            obj.removeField("_id");
        for (int i=0;i<users.size();i++){
            if(user_id.equals(users.get(i).getUser_code())){
                obj.put("user_can_login","离职");
            }else{
                obj.put("user_can_login","在职");
            }
        }
            list.add(obj.toMap());
        }
        return list;
    }
    //DBCursor排序分页
    //sort_type（1：正序，-1：倒序）
    public static DBCursor sortAndPage(DBCursor dbCursor,int page_num,int page_size,String sort_key,int sort_type){
        DBObject sort_obj = new BasicDBObject(sort_key, sort_type);
        dbCursor = dbCursor.sort(sort_obj).skip((page_num - 1) * page_size).limit(page_size);
        return dbCursor;
    }

    //根据条件查找并更新
    public static void findAndUpdate(String key,String value,DBCollection collection,String update_key,Object update_value) {
        Map keyMap = new HashMap();
        keyMap.put(key, value);
        BasicDBObject queryCondition = new BasicDBObject();
        queryCondition.putAll(keyMap);
        DBCursor dbCursor1 = collection.find(queryCondition);
        if (dbCursor1.size() > 0) {
            //记录存在，更新
            DBObject updateCondition = new BasicDBObject();
            updateCondition.put(key, value);
            DBObject updatedValue = new BasicDBObject();
            updatedValue.put(update_key, update_value);
            DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
            collection.update(updateCondition, updateSetValue);
        }
    }
//mongodb插入、删除模板

    //save方式插入数据(id相同会覆盖)
//    public static void saveData(DBCollection collection, Entity entity){
//        DBObject saveData=new BasicDBObject();
//        saveData.put("userName", "iwtxokhtd");
//        saveData.put("age", "26");
//        saveData.put("gender", "m");
//
//        DBObject infoData=new BasicDBObject();
//        infoData.put("height", 16.3);
//        infoData.put("weight", 22);
//
//        saveData.put("info", infoData);
//
//        collection.save(entity);
//    }

    //insert方式插入数据
    private static void insertData(DBCollection collection){
        DBObject saveData=new BasicDBObject();
        saveData.put("userName", "iwtxokhtd");
        saveData.put("age", "26");
        saveData.put("gender", "m");
        collection.insert(saveData);
    }

    //insert批量插入数据
    private static void insertListData(DBCollection collection){
        DBObject insertData1=new BasicDBObject();
        insertData1.put("userName", "iwtxokhtd");
        insertData1.put("age", "26");
        insertData1.put("gender", "m");

        DBObject insertData2=new BasicDBObject();
        insertData2.put("userName", "iwtxokhtd");
        insertData2.put("age", "26");
        insertData2.put("gender", "m");

        List<DBObject> insertListData = new ArrayList<DBObject>();
        insertListData.add(insertData1);
        insertListData.add(insertData2);

        collection.insert(insertListData);
    }

    //删除数据
    private static void deleteDate(DBCollection collection) {
        DBObject deletePig = new BasicDBObject();
        deletePig.put("name", "pig");
        collection.remove(deletePig);
    }



}
