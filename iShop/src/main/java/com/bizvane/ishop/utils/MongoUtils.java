package com.bizvane.ishop.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.ArrayList;
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

//    //多条件查询
//    public static BasicDBObject orOperation(BasicDBObject queryCondition,){}

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

    //DBCursor排序分页
    //sort_type（1：正序，-1：倒序）
    public static DBCursor sortAndPage(DBCursor dbCursor,int page_num,int page_size,String sort_key,int sort_type){
        DBObject sort_obj = new BasicDBObject(sort_key, sort_type);
        dbCursor = dbCursor.sort(sort_obj).skip((page_num - 1) * page_size).limit(page_size);
        return dbCursor;
    }
}
