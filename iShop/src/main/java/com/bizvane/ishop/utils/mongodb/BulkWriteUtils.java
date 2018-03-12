package com.bizvane.ishop.utils.mongodb;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.utils.WebUtils;
import com.mongodb.BasicDBList;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanyadong on 2017/11/10.
 *
 * 批量，并行处理数据
 *
 */
public class BulkWriteUtils {

      /* update
      Criteria criteria=new Criteria();
        ObjectId objectId=new ObjectId("58c3e85cfb98665a90098a0e");
        criteria.and("_id").is(objectId);

        Criteria criteria1=new Criteria();
        ObjectId objectId1=new ObjectId("58c3e871fb98665a90098a0f");
        criteria1.and("_id").is(objectId1);

        Criteria[] criterias=new Criteria[2];
        criterias[0]=criteria;
        criterias[1]=criteria1;
        Criteria criteria2=new Criteria();
        criteria2.orOperator(criterias);


        Update update=new Update();
        update.set("name","updateXXXXXXXXXX");
        bulkOperations.updateMulti(Query.query(criteria2),update).execute();
        */


      /*   insert
      List<WriteModel<Document>> requests = new ArrayList<WriteModel<Document>>();
        for (int i = 0; i <400000 ; i++) {
            //构造插入单个文档的操作模型
            Model product = new Model(String.valueOf(i),"书籍","追风筝的人");
            //将java对象转换成json字符串
            //将json字符串解析成Document对象
            GsonBuilder gb = new GsonBuilder();
            gb.disableHtmlEscaping();

            Document docProduct = Document.parse(gb.create().toJson(product).toString());
            InsertOneModel<Document> iom = new InsertOneModel<Document>(docProduct);
            requests.add(iom);
        }
        BulkOperations bulkOperations=mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED,"group");
        bulkOperations.insert(requests).execute();
        */

    public static void bulkWriteUpdate(MongoTemplate mongoTemplate, String collectionName, Query query, Update update, BulkOperations.BulkMode bulkMode){
        try {
            BulkOperations bulkOperations = mongoTemplate.bulkOps(bulkMode, collectionName);
            bulkOperations.updateMulti(query, update).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //Query query1=Query.query(new Criteria());
    public  static  void  bulkWriteRemove(MongoTemplate mongoTemplate, String collectionName, Query query,BulkOperations.BulkMode bulkMode){
        try {
            BulkOperations bulkOperations = mongoTemplate.bulkOps(bulkMode, collectionName);
           bulkOperations.remove(query);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public  static <T> void   bulkWriteInsert(MongoTemplate mongoTemplate, String collectionName, List<T> list,BulkOperations.BulkMode bulkMode){
        try {
            int size = list.size();
            List<WriteModel<Document>> requests = new ArrayList<WriteModel<Document>>();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject= WebUtils.bean2JSONObject(list.get(i));
                Document docProduct = Document.parse(jsonObject.toString());
                InsertOneModel<Document> iom = new InsertOneModel<Document>(docProduct);
                requests.add(iom);
            }
            BulkOperations bulkOperations = mongoTemplate.bulkOps(bulkMode, collectionName);
            bulkOperations.insert(requests).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


   /**
    *
    *插入数据
    *BulkOperations.BulkMode.ORDERED 有序 顺序插入 插入失败 直接终止
    *BulkOperations.BulkMode.UNORDERED  无序  并行插入  插入失败 跳过失败的文档 继续插入（例如 有相同的id情况时）
    *
    */
    public  static  void   bulkWriteInsert(MongoTemplate mongoTemplate, String collectionName, BasicDBList list,BulkOperations.BulkMode bulkMode){
        try {
            BulkOperations bulkOperations = mongoTemplate.bulkOps(bulkMode, collectionName);
            bulkOperations.insert(list).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
