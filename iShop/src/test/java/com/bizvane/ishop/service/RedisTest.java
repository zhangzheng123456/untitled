package com.bizvane.ishop.service;

import com.bizvane.ishop.dao.CorpMapper;
import com.bizvane.ishop.entity.Corp;
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
            List<Corp> list = new ArrayList<Corp>();
            if (redisClient.get("CorpList") == null) {
                list = corpMapper.selectCorps("");
                redisClient.set("CorpList", list);
            } else {
                list = (List<Corp>) redisClient.get("CorpList");
            }
            System.out.println("------corp"+list);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @Test
    public void testMongo(){
        MongoClient client = mongodbClient.getMongoClient();
        try {
            // 取得Collecton句柄
            MongoDatabase database = client.getDatabase("ishow");
            MongoCollection<Document> collection = database.getCollection("test");

            // 插入数据
            Document doc = new Document();
            String demoname = "JAVA:" + UUID.randomUUID();
            doc.append("DEMO", demoname);
            doc.append("MESG", "Hello AliCoudDB For MongoDB");
//            collection.insertOne(doc);
            System.out.println("insert document: " + doc);
            // 读取数据
            BsonDocument filter = new BsonDocument();
            filter.append("DEMO", new BsonString(demoname));
            MongoCursor<Document> cursor = collection.find(filter).iterator();
            String result = null;
            while (cursor.hasNext()) {
                result = "find document: " + cursor.next();
            }
            System.out.print("------------"+result);
        } finally {
            //关闭Client，释放资源
            client.close();
        }
    }
}
