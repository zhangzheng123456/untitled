package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;

import com.bizvane.ishop.dao.ShopMatchTypeMapper;
import com.bizvane.ishop.entity.ShopMatchType;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.ShopMatchService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by PC on 2016/12/27.
 */
@Service
public class ShopMatchServiceImpl implements ShopMatchService {
    @Autowired
    private IceInterfaceService iceInterfaceService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    private UserService userService;
    @Autowired
    private ShopMatchTypeMapper matchTypeMapper;
    public String getGoodsByWx(String corp_code, String pageSize, String pageIndex, String categoryId, String row_num, String productName,String user_id,String store_id,String brand_code,String searchType) throws Exception {

        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_pageSize = new Data("pageSize", pageSize, ValueType.PARAM);
        Data data_pageIndex = new Data("pageIndex", pageIndex, ValueType.PARAM);
        Data data_categoryId = new Data("categoryId", categoryId, ValueType.PARAM);
        Data data_row_num = new Data("row_num", row_num, ValueType.PARAM);
        Data data_productName = new Data("productName", productName, ValueType.PARAM);
        Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
        String value="NAME";
        if(StringUtils.isBlank(searchType)){
            value="NAME";
        }else{
            value=searchType;
        }
        Data data_searchType= new Data("searchType", value, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_pageSize.key, data_pageSize);
        datalist.put(data_pageIndex.key, data_pageIndex);
        datalist.put(data_categoryId.key, data_categoryId);
        datalist.put(data_row_num.key, data_row_num);
        datalist.put(data_productName.key, data_productName);
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_searchType.key, data_searchType);
        if(null!=store_id && !store_id.equals("") && !store_id.equals("null")){
            Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
            datalist.put(data_store_id.key, data_store_id);
        }
        if(null!=brand_code && !brand_code.equals("") && !brand_code.equals("null")){
            Data data_brand_code = new Data("brand_code", brand_code, ValueType.PARAM);
            datalist.put(data_brand_code.key, data_brand_code);
        }
        DataBox dataBox = iceInterfaceService.iceInterface("ProductList",datalist);

        String result = dataBox.data.get("message").value;

        return result;
    }

    @Override
    public String getProductCategoryByWx(String corp_code,String brand_code) throws Exception {

        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        if(null!=brand_code && !brand_code.equals("") && !brand_code.equals("null")){
            Data data_brand_code = new Data("brand_code", brand_code, ValueType.PARAM);
            datalist.put(data_brand_code.key, data_brand_code);
        }
        DataBox dataBox = iceInterfaceService.iceInterface("ProductCategory",datalist);

        String result = dataBox.data.get("message").value;

        return result;
    }

    public void insert(String corp_code, String d_match_code, String d_match_title, String d_match_image, String d_match_desc, String d_match_type, JSONArray r_match_goods, String user_code, String isactive,String d_match_show_wx,String d_match_category)throws Exception{
        Date now = new Date();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
        DBObject saveData=new BasicDBObject();
        saveData.put("corp_code", corp_code);
        saveData.put("d_match_code", d_match_code);
        saveData.put("d_match_title", d_match_title);
        saveData.put("d_match_image", d_match_image);
        saveData.put("d_match_desc", d_match_desc);
        saveData.put("d_match_type", d_match_type);
        saveData.put("r_match_goods", r_match_goods);
        saveData.put("d_match_likeCount", 0);
        saveData.put("d_match_commentCount", 0);
        saveData.put("d_match_collectCount", 0);
        saveData.put("visitors_count", 0);
        saveData.put("shopCount", 0);
        saveData.put("shopMoney", 0);
        saveData.put("pageViews", 0);
        saveData.put("shareCount", 0);
        saveData.put("modified_date",  Common.DATETIME_FORMAT.format(now));
        saveData.put("created_date",  Common.DATETIME_FORMAT.format(now));
        saveData.put("creater", user_code);
        saveData.put("modifier", user_code);
        saveData.put("isactive", isactive);
        saveData.put("d_match_show_wx", d_match_show_wx);
        saveData.put("d_match_category", d_match_category);
        collection.insert(saveData);
    }

    public  void addRelByType(String corp_code,String d_match_code,String operate_userCode,String operate_type,String status ,String comment_text)throws Exception{
        Date now = new Date();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);
        DBObject saveData=new BasicDBObject();
        saveData.put("corp_code", corp_code);
        saveData.put("d_match_code", d_match_code);
        //验证企业下用户编号是否已存在
        List<User> userList = userService.userCodeExist(operate_userCode, corp_code, Common.IS_ACTIVE_Y);
        String user_name="";
        if(userList.size()==0 || userList.size()>1){
            user_name="未知";
        }else{
            user_name  = userList.get(0).getUser_name();
        }
        String avatar="";
        if(userList.size()==0 || userList.size()>1){
            avatar="";
        }else{
            avatar  = userList.get(0).getAvatar();
        }
        System.out.println(avatar+"==============user_name点赞人======================"+user_name);
        saveData.put("operate_userCode", operate_userCode);
        saveData.put("operate_userName", user_name);
        saveData.put("operate_userAvatar", avatar);
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


    public  void updRelByType(String corp_code,String d_match_code,String operate_userCode,String operate_type)throws Exception{

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection_rel = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);
        //operate_type:collect(收藏) like(喜欢)  comment(评论)
        BasicDBList value = new BasicDBList();
        value.add(new BasicDBObject("corp_code", corp_code));
        value.add(new BasicDBObject("d_match_code", d_match_code));
        value.add(new BasicDBObject("operate_type", operate_type));
        value.add(new BasicDBObject("operate_userCode", operate_userCode));
        BasicDBObject queryCondition1 = new BasicDBObject();
        queryCondition1.put("$and", value);

        WriteResult remove = collection_rel.remove(queryCondition1);
        System.out.println("--------删除成功？？？？-------------"+remove.toString());
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

    public void deleteAll(String corp_code,String d_match_code)throws  Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        //秀搭详情
        DBCollection collection_rel = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);
       //秀搭定义
        DBCollection collection_def = mongoTemplate.getCollection(CommonValue.table_shop_match_def);

        BasicDBList value = new BasicDBList();
        value.add(new BasicDBObject("corp_code", corp_code));
        value.add(new BasicDBObject("d_match_code", d_match_code));
        BasicDBObject queryCondition1 = new BasicDBObject();
        queryCondition1.put("$and", value);
        //从秀搭定义移除
        collection_def.remove(queryCondition1);
        //从秀搭详情移除
        collection_rel.remove(queryCondition1);
    }


    public void deleteByBatch(String d_match_codes)throws  Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection_rel = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);

        DBCollection collection_def = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
       //用逗号分隔，根据搭配编号
        String[] ids = d_match_codes.split(",");

        for (int i = 0; i < ids.length; i++) {
            DBObject deleteRecord = new BasicDBObject();
            deleteRecord.put("d_match_code", ids[i]);
            collection_def.remove(deleteRecord);
            collection_rel.remove(deleteRecord);
        }



    }

    //DBCursor数据集转arrayList+id
    public  ArrayList dbCursorToList_shop(DBCursor dbCursor) {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_def);

        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String id = obj.get("_id").toString();
            String corp_code = obj.get("corp_code").toString();
            String d_match_code = obj.get("d_match_code").toString();
            obj.put("id", id);
            obj.removeField("_id");


            DBObject deleteRecord = new BasicDBObject();
            deleteRecord.put("corp_code",corp_code);
            deleteRecord.put("d_match_code",d_match_code);
            DBCursor dbObjects = cursor.find(deleteRecord);
            DBObject dbObject=null;
            while (dbObjects.hasNext()) {
                dbObject  = dbObjects.next();
            }
            obj.put("shop", dbObject);
            list.add(obj.toMap());
        }
        return list;
    }

    @Override
    public PageInfo<ShopMatchType> selectAllMatchType(int page_number, int page_size,String corp_code, String search_value,String role_type) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<ShopMatchType> shopMatchTypes = matchTypeMapper.selectAllMatchType(corp_code,search_value,role_type,null);
        PageInfo<ShopMatchType> page = new PageInfo<ShopMatchType>(shopMatchTypes);
        return page;
    }

    @Override
    public PageInfo<ShopMatchType> selectAllMatchType(int page_number, int page_size,String corp_code, String search_value,String role_type,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        PageHelper.startPage(page_number, page_size);
        List<ShopMatchType> shopMatchTypes = matchTypeMapper.selectAllMatchType(corp_code,search_value,role_type,manager_corp_arr);
        PageInfo<ShopMatchType> page = new PageInfo<ShopMatchType>(shopMatchTypes);
        return page;
    }

    @Override
    public int addShopMatchType(ShopMatchType shopMatchType) throws Exception {

        return matchTypeMapper.addShopMatchType(shopMatchType);
    }

    @Override
    public String updShopMatchType(ShopMatchType shopMatchType) throws Exception {
        int id = shopMatchType.getId();
        String corp_code = shopMatchType.getCorp_code();
        String shopmatch_type = shopMatchType.getShopmatch_type();
        ShopMatchType shopMatchType_old = this.selShopMatchTypeById(id);
        List<ShopMatchType> shopMatchTypes_new = checkName(corp_code, shopmatch_type);
        if(corp_code.equals(shopMatchType_old.getCorp_code())){
            if(shopMatchTypes_new.size()>0 && shopMatchTypes_new.get(0).getId()!=shopMatchType_old.getId()){
                return "名称已存在";
            }else {
                matchTypeMapper.updShopMatchType(shopMatchType);
                return Common.DATABEAN_CODE_SUCCESS;
            }
        }else{
            return "不可更改企业";
        }
    }

    @Override
    public int delShopMatchTypeById(int id) throws Exception {
        return matchTypeMapper.delShopMatchTypeById(id);
    }

    @Override
    public ShopMatchType selShopMatchTypeById(int id) throws Exception {
        return matchTypeMapper.selShopMatchTypeById(id);
    }

    @Override
    public List<ShopMatchType> checkName(String corp_code, String shopmatch_type) throws Exception {
        return matchTypeMapper.checkName(corp_code,shopmatch_type);
    }

    @Override
    public String AddShopMatchPageViewsLog(String corp_code, String user_id, String d_match_code, String headImg, String nickName, String open_id, String app_id,String store_id) throws Exception {
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_user_id= new Data("user_id", user_id, ValueType.PARAM);
        Data data_d_match_code = new Data("d_match_code", d_match_code, ValueType.PARAM);
        Data data_headImg = new Data("headImg", headImg, ValueType.PARAM);
        Data data_nickName = new Data("nickName", nickName, ValueType.PARAM);
        Data data_open_id= new Data("open_id", open_id, ValueType.PARAM);
        Data data_app_id= new Data("app_id", app_id, ValueType.PARAM);
        Data data_store_id= new Data("store_id", store_id, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_d_match_code.key, data_d_match_code);
        datalist.put(data_headImg.key, data_headImg);
        datalist.put(data_nickName.key, data_nickName);
        datalist.put(data_open_id.key, data_open_id);
        datalist.put(data_app_id.key, data_app_id);
        datalist.put(data_store_id.key, data_store_id);

        DataBox dataBox = iceInterfaceService.iceInterfaceV3("AddShopMatchPageViewsLog",datalist);

        String result = dataBox.data.get("message").value;

        return result;
    }

    @Override
    public String EditShopMatchByColmn(String corp_code, String user_id, String d_match_code, String column, String value) throws Exception {
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_user_id= new Data("user_id", user_id, ValueType.PARAM);
        Data data_d_match_code = new Data("d_match_code", d_match_code, ValueType.PARAM);
        Data data_column = new Data("column", column, ValueType.PARAM);
        Data data_value= new Data("value", value, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_d_match_code.key, data_d_match_code);
        datalist.put(data_column.key, data_column);
        datalist.put(data_value.key, data_value);

        DataBox dataBox = iceInterfaceService.iceInterfaceV3("EditShopMatchByColmn",datalist);
        String result = dataBox.data.get("message").value;
        return result;
    }

    public int getCollect(String corp_code,String user_code)throws Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor_rel = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
        BasicDBList value_rel = new BasicDBList();
        value_rel.add(new BasicDBObject("corp_code", corp_code));
        value_rel.add(new BasicDBObject("operate_userCode", user_code));
        value_rel.add(new BasicDBObject("operate_type", "collect"));
        BasicDBObject queryCondition_rel = new BasicDBObject();
        queryCondition_rel.put("$and", value_rel);
        DBCursor dbCursor_rel = cursor_rel.find(queryCondition_rel);

        BasicDBList value = new BasicDBList();


        value.add(new BasicDBObject("corp_code", corp_code));
        value.add(new BasicDBObject("isactive", "Y"));

        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList value2 = new BasicDBList();
        while (dbCursor_rel.hasNext()) {
            DBObject obj = dbCursor_rel.next();
            String d_match_code = obj.get("d_match_code").toString();
            value2.add(new BasicDBObject("d_match_code", d_match_code));
        }
        if (dbCursor_rel.count() > 0) {
            queryCondition.put("$or", value2);
            value.add(queryCondition);
        }
        if (dbCursor_rel.count() == 0) {
            value.add(new BasicDBObject("corp_code", "==不存在=="));
        }
        BasicDBObject queryCondition1 = new BasicDBObject();
        queryCondition1.put("$and", value);
        DBCursor dbCursor2 = cursor.find(queryCondition1);
        return dbCursor2.count();
    }

}
