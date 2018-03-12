//package com.bizvane.ishop.service.imp;
//
//import com.alibaba.fastjson.JSONObject;
//import com.bizvane.ishop.constant.CommonValue;
//import com.bizvane.ishop.entity.VipTask;
//import com.bizvane.ishop.service.VipTaskAnalyService;
//import com.bizvane.ishop.service.VipTaskService;
//import com.bizvane.sun.common.service.mongodb.MongoDBClient;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBCollection;
//import com.mongodb.DBCursor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Service;
//
///**
// * Created by gyy on 2017/11/23.
// */
//@Service
//public class VipTaskAnalyServiceImpl implements VipTaskAnalyService {
//    @Autowired
//    MongoDBClient mongodbClient;
//    @Autowired
//    private VipTaskService vipTaskService;
//
//    //获取总分享人数，总分享次数，总注册人数
//    @Override
//    public JSONObject getShareAndRegistCount(String message) throws Exception {
//        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_shareUrl_log_wx);
//        DBCursor dbCursor = null;
//
//        JSONObject jsonObject = JSONObject.parseObject(message);
//        String task_code = jsonObject.get("task_code").toString();
//        BasicDBObject basicDBObject=new BasicDBObject();
//        basicDBObject.put("task_code",task_code);
//
//        //总分享次数
//        dbCursor = cursor.find(new BasicDBObject("target_count",target_count));
//        int count = dbCursor.count();
//        //总分享人数
//        int shareNum = cursor.distinct("target_count",basicDBObject).size();
//        //总注册人数
//        // TODO: 2017/11/24  ....
//
//    }
//}
