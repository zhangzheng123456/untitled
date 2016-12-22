package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.dao.CorpMapper;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.common.service.redis.RedisClient;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ZhouZhou on 2016/8/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:spring-mybatis.xml"})
public class RedisTest {
    @Autowired
    private CorpMapper corpMapper = null;

    @Autowired
    RedisClient redisClient;
    @Autowired
    MongoDBClient mongodbClient;

    //成功
    @Test
    public void test() {
        try {
            JSONObject obj = new JSONObject();
            JSONObject aa = new JSONObject();
            obj.put("appid","wxc9c9111020955324");
            obj.put("ts","1482129612509");
            obj.put("sig","4E733E69DC02AA26DC21D938A4A4CA5E");
            obj.put("method","o2ocoupontype");
            obj.put("params",aa);
            String bb = IshowHttpClient.post("http://www.dev-wechat.bizvane.com/rest/api",obj);

System.out.println(bb);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @Test
    public void testMongo(){
        MongoClient client = mongodbClient.getMongoClient();
        try {

            JSONArray array = new JSONArray();
            for (int i = 0; i < 3; i++) {
                JSONObject obj = new JSONObject();
                obj.put("appid","wxc9c9111020955324");
                obj.put("ts","1");
                obj.put("sig",i);
                array.add(obj);
            }
            System.out.print("------------"+array.toJSONString());

            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                obj.put("ok","ok");

            }
            System.out.print("------------"+array.toJSONString());

        } finally {
            //关闭Client，释放资源
            client.close();
        }
    }
}
