package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.WechatReply;
import com.bizvane.ishop.service.WeChatReplyService;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;



/**
 * Created by PC on 2017/8/9.
 */
@Service
public class WeChatReplyServiceImpl implements WeChatReplyService{
    @Autowired
    MongoDBClient mongodbClient;
    public void insert(WechatReply wechatReply)throws Exception{
        Date now = new Date();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_wechat_reply_def);
        DBObject saveData=new BasicDBObject();
        saveData.put("corp_code", wechatReply.getCorp_code());
        saveData.put("corp_name", wechatReply.getCorp_name());
        saveData.put("code", wechatReply.getCode());
        saveData.put("name", wechatReply.getName());
        saveData.put("content", wechatReply.getContent());
        saveData.put("brand_code", wechatReply.getBrand_code());
        saveData.put("type", wechatReply.getType());
        saveData.put("brand_name", wechatReply.getBrand_name());
        saveData.put("modified_date", Common.DATETIME_FORMAT.format(now));
        saveData.put("modifier", wechatReply.getModifier());
        saveData.put("created_date", Common.DATETIME_FORMAT.format(now));
        saveData.put("creater", wechatReply.getCreater());
        saveData.put("isactive", wechatReply.getIsactive());
        saveData.put("reply_list", wechatReply.getReply_list());
        collection.insert(saveData);
    }


}
