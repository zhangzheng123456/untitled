package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.ShopMatchService;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC on 2016/12/27.
 */
@Service
public class ShopMatchServiceImpl implements ShopMatchService {
    @Autowired
    private IceInterfaceService iceInterfaceService;
    @Autowired
    MongoDBClient mongodbClient;
    public JSONObject getGoodsByWx(String corp_code, String pageSize, String pageIndex, String categoryId, String row_num, String productName) throws Exception {
        JSONObject jsonObject = new JSONObject();

        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_pageSize = new Data("pageSize", pageSize, ValueType.PARAM);
        Data data_pageIndex = new Data("pageIndex", pageIndex, ValueType.PARAM);
        Data data_categoryId = new Data("categoryId", categoryId, ValueType.PARAM);
        Data data_row_num = new Data("row_num", row_num, ValueType.PARAM);
        Data data_productName = new Data("productName", productName, ValueType.PARAM);


        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_pageSize.key, data_pageSize);
        datalist.put(data_pageIndex.key, data_pageIndex);
        datalist.put(data_categoryId.key, data_categoryId);
        datalist.put(data_row_num.key, data_row_num);
        datalist.put(data_productName.key, data_productName);
        DataBox dataBox = iceInterfaceService.iceInterface("ProductList",datalist);

        String result = dataBox.data.get("message").value;
        jsonObject = JSON.parseObject(result);
        return jsonObject;
    }



    public void insert(String corp_code,String d_match_code,String d_match_title,String d_match_image,String d_match_desc,JSONArray r_match_goods,String user_code)throws Exception{
        Date now = new Date();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
        DBObject saveData=new BasicDBObject();
        saveData.put("corp_code", corp_code);
        saveData.put("d_match_code", d_match_code);
        saveData.put("d_match_title", d_match_title);
        saveData.put("d_match_image", d_match_image);
        saveData.put("d_match_desc", d_match_desc);
        saveData.put("r_match_goods", r_match_goods);
        saveData.put("d_match_likeCount", "0");
        saveData.put("d_match_commentCount", "0");
        saveData.put("d_match_collectCount", "0");
        saveData.put("modified_date",  Common.DATETIME_FORMAT.format(now));
        saveData.put("created_date",  Common.DATETIME_FORMAT.format(now));
        saveData.put("creater", user_code);
        saveData.put("modifier", user_code);
        saveData.put("isactive", "Y");
        collection.insert(saveData);
    }

    public  void addRelByType(String corp_code,String d_match_code,String operate_userCode,String operate_type,String status ,String comment_text)throws Exception{
        Date now = new Date();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);
        DBObject saveData=new BasicDBObject();
        saveData.put("corp_code", corp_code);
        saveData.put("d_match_code", d_match_code);
        saveData.put("operate_userCode", operate_userCode);
        saveData.put("operate_type", operate_type);
        saveData.put("status", status);
        saveData.put("comment_text", comment_text);
        saveData.put("modified_date",  Common.DATETIME_FORMAT.format(now));
        saveData.put("created_date",  Common.DATETIME_FORMAT.format(now));
        saveData.put("creater", operate_userCode);
        saveData.put("modifier", operate_userCode);
        saveData.put("isactive", "Y");
        collection.insert(saveData);
    }


    public  DBObject selectByCode(String corp_code,String d_match_code)throws Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
        BasicDBObject basicDBObject = MongoUtils.andOperation3(corp_code, d_match_code);
        DBCursor dbObjects = cursor.find(basicDBObject);
        DBObject object=null;
        while (dbObjects.hasNext()) {
            object  = dbObjects.next();
        }
        return object;
    }
}
