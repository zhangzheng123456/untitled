package com.bizvane.ishop.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.common.utils.SpringUtil;
import com.mongodb.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            if (!screen_value.equals("")){
                if (screen_value.startsWith("|") || screen_value.startsWith(",") || screen_value.startsWith("，")) {
                    screen_value = screen_value.substring(1);
                }
                if (screen_value.endsWith("|") || screen_value.endsWith(",") || screen_value.endsWith("，")) {
                    screen_value = screen_value.substring(0, screen_value.length() - 1);
                }
                screen_value = screen_value.replaceAll(",", "|");
                screen_value = screen_value.replaceAll("，", "|");
                screen_value = WebUtils.El2Str1(screen_value);
                System.out.println("========screen_value1:"+screen_value);

                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                values.add(new BasicDBObject(screen_key, pattern));
            }
        }
        if (values.size() > 0)
        queryCondition.put("$and", values);
        return queryCondition;
    }

    //多个“或”查询(搜索)
    public static BasicDBObject orOperation(String[] column_names, String search_value) {
        search_value = WebUtils.El2Str1(search_value);
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

    //多个“and”查询(搜索)查询签到状态
    public static BasicDBObject andOperation2(String corp_code, String user_code, String date,String status) {
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("corp_code", corp_code));
        values.add(new BasicDBObject("user_code", user_code));
        Pattern pattern = Pattern.compile("^.*" + date + ".*$", Pattern.CASE_INSENSITIVE);
        values.add(new BasicDBObject("sign_time", pattern));
        values.add(new BasicDBObject("status", status));
        queryCondition.put("$and", values);
        return queryCondition;
    }

    //多个“and”查询(搜索)
    public static BasicDBObject andOperation2(String corp_code, String user_code) {
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("corp_code", corp_code));
        values.add(new BasicDBObject("creater", user_code));
        queryCondition.put("$and", values);
        return queryCondition;
    }


    //多个“and”查询(搜索)
    public static BasicDBObject andOperation3(String corp_code, String d_match_code) {
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("corp_code", corp_code));
        values.add(new BasicDBObject("d_match_code", d_match_code));
        queryCondition.put("$and", values);
        return queryCondition;
    }

    //多个“and”查询(搜索)
    public static BasicDBObject andOperation3(String corp_code, String d_match_code,String user_code,String operate_type) {
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("corp_code", corp_code));
        values.add(new BasicDBObject("d_match_code", d_match_code));
        values.add(new BasicDBObject("operate_userCode", user_code));
        values.add(new BasicDBObject("operate_type", operate_type));
        queryCondition.put("$and", values);
        return queryCondition;
    }
    //in查询
    public static BasicDBObject inOperation(String[] args, String condition) {
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
    public static int getPages(DBCursor dbCursor, int page_size) {
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
    public static ArrayList dbCursorToList(DBCursor dbCursor) {
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            list.add(obj.toMap());

        }
        return list;
    }

    //DBCursor数据集转arrayList+id
    public static ArrayList dbCursorToList_id(DBCursor dbCursor) {
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            obj.put("id", id);
            obj.removeField("_id");
            list.add(obj.toMap());
        }
        return list;
    }


    //DBCursor数据集转arrayList+id
    public static ArrayList dbCursorToListPageViewsLog(DBCursor dbCursor,String user_id) {
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            obj.put("id", id);
            obj.removeField("_id");
            if(obj.containsField("user_id")){
                String user_id_mongo = obj.get("user_id").toString();
                if(!user_id.equals(user_id_mongo)){
                    if(obj.containsField("nickName")){
                        if(null!= obj.get("nickName") && !"".equals(obj.get("nickName"))){
                            try {
                                String nickName = String.valueOf(obj.get("nickName")).substring(0, 1);
                                obj.put("nickName", nickName + "*****");
                            }catch (Exception ex){
                                obj.put("nickName", "昵称未知");
                            }
                        }else{
                            obj.put("nickName", "昵称未知");
                        }
                    }
                }
            }
            list.add(obj.toMap());
        }
        return list;
    }


    //DBCursor排序分页
    //sort_type（1：正序，-1：倒序）
    public static DBCursor sortAndPage(DBCursor dbCursor, int page_num, int page_size, String sort_key, int sort_type) {
        DBObject sort_obj = new BasicDBObject(sort_key, sort_type);
        dbCursor = dbCursor.sort(sort_obj).skip((page_num - 1) * page_size).limit(page_size);
        return dbCursor;
    }

    //根据条件查找并更新
    public static void findAndUpdate(String key, String value, DBCollection collection, String update_key, Object update_value) {
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
    private static void insertData(DBCollection collection) {
        DBObject saveData = new BasicDBObject();
        saveData.put("userName", "iwtxokhtd");
        saveData.put("age", "26");
        saveData.put("gender", "m");
        collection.insert(saveData);
    }

    //insert批量插入数据
    private static void insertListData(DBCollection collection) {
        DBObject insertData1 = new BasicDBObject();
        insertData1.put("userName", "iwtxokhtd");
        insertData1.put("age", "26");
        insertData1.put("gender", "m");

        DBObject insertData2 = new BasicDBObject();
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
//获取会员标签的自增ID
    public static String getNextId(String tabel,String field,String values,String increment)throws  Exception{
        MongoDBClient client= SpringUtil.getBean("mongodbClient");
        MongoTemplate mongoTemplate = client.getMongoTemplate();//获取模板
        DBCollection cursor_content = mongoTemplate.getCollection(tabel);//获取操作表
        BasicDBObject queryDBObject=new BasicDBObject();
        queryDBObject.put(field,values);
        BasicDBObject updateDBObject=new BasicDBObject();
        updateDBObject.put("$inc",new BasicDBObject(increment,1));
        DBObject dbCursor=cursor_content.findAndModify(queryDBObject,(DBObject)null,(DBObject)null,false,updateDBObject,true,false);
        return dbCursor.get(increment).toString();
    }

//    //会员活动筛选
//    public  static  BasicDBList getScreenByActivity(JSONObject jsonObject,BasicDBList basicDBList){
//        //筛选条件
//        String user_code = "";
//        String user_name = "";
//        String store_code = "";
//        String store_name = "";
//        String screen = jsonObject.get("screen").toString();
//        if (screen != null && !screen.equals("")) {
//            JSONObject screenJson = JSON.parseObject(screen);
//            user_code = screenJson.get("user_code").toString();
//            user_name = screenJson.get("user_name").toString();
//            store_code = screenJson.get("store_code").toString();
//            store_name = screenJson.get("store_name").toString();
//        }
//        if (user_code != null && !user_code.equals("")) {
//            Pattern pattern = Pattern.compile("^.*" + user_code + ".*$", Pattern.CASE_INSENSITIVE);
//            basicDBList.add(new BasicDBObject("user_code", pattern));
//        }
//        if (user_name != null && !user_name.equals("")) {
//            Pattern pattern = Pattern.compile("^.*" + user_name + ".*$", Pattern.CASE_INSENSITIVE);
//            basicDBList.add(new BasicDBObject("user_name", pattern));
//        }
//        if (store_code != null && !store_code.equals("")) {
//            Pattern pattern = Pattern.compile("^.*" + store_code + ".*$", Pattern.CASE_INSENSITIVE);
//            basicDBList.add(new BasicDBObject("store_code", pattern));
//        }
//
//        if (store_name != null && !store_name.equals("")) {
//            Pattern pattern = Pattern.compile("^.*" + store_name + ".*$", Pattern.CASE_INSENSITIVE);
//            basicDBList.add(new BasicDBObject("store_name", pattern));
//        }
//        return  basicDBList;
//    }



}
