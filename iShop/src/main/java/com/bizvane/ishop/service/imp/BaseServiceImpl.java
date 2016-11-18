package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.BaseMapper;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/7/14.
 */
@Service
public class BaseServiceImpl implements BaseService{
    @Autowired
    private BaseMapper baseMapper;
    @Autowired
    StoreMapper storeMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    MongoDBClient mongodbClient;

    @Override
    public PageInfo<HashMap<String, Object>> queryMetaList(int page_number, int page_size, Map<String, Object> params) throws SQLException {
       List<HashMap<String, Object>> list;
        PageHelper.startPage(page_number, page_size);
        list=baseMapper.queryMetaList(params);
        PageInfo<HashMap<String, Object>> page = new PageInfo<HashMap<String, Object>>(list);
        return page;
    }

    /**
     * store_id转store_code
     * @param corp_code
     * @param store_id
     * @return store_code
     * @throws Exception
     */
    public String storeIdConvertStoreCode(String corp_code,String store_id)throws Exception{
        String store_code = "";
        Store store = storeMapper.selStoreByStroeId(corp_code,store_id, Common.IS_ACTIVE_Y);
        if (store != null)
            store_code = store.getStore_code();
        return store_code;
    }

    /**
     * store_code转store_id
     * @param corp_code
     * @param store_code
     * @return store_id
     * @throws Exception
     */
    public String storeCodeConvertStoreId(String corp_code,String store_code)throws Exception{
        String store_id = "";
        Store store = storeMapper.selectByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
        if (store != null)
            store_id = store.getStore_id();
        return store_id;
    }

    /**
     * user_id转user_code
     * @param corp_code
     * @param user_id
     * @return user_code
     * @throws Exception
     */
    public String userIdConvertUserCode(String corp_code,String user_id)throws Exception{
        String user_code = "";
        List<User> user = userMapper.selUserByUserId(user_id,corp_code,Common.IS_ACTIVE_Y);
        if (user.size()>0)
            user_code = user.get(0).getUser_code();
        return user_code;
    }

    /**
     * user_code转user_id
     * @param corp_code
     * @param user_code
     * @return user_id
     * @throws Exception
     */
    public String userCodeConvertUserId(String corp_code,String user_code)throws Exception{
        String user_id = "";
        List<User> user = userMapper.selectUserCode(user_code,corp_code,Common.IS_ACTIVE_Y);
        if (user.size()>0)
            user_id = user.get(0).getUser_id();
        return user_id;
    }

    /**
     * mongodb插入用户操作记录
     * @param operation_corp_code 操作者corp_code
     * @param operation_user_code 操作者user_code
     * @param function 功能
     * @param action 动作
     * @param corp_code 被操作corp_code
     * @param code 被操作code
     * @param name 被操作name
     * @throws Exception
     */
    public void insertUserOperation(String operation_corp_code,String operation_user_code,String function,String action,String corp_code, String code,String name,String remark)throws Exception{
        Date now = new Date();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_log_user_operation);
        DBObject saveData=new BasicDBObject();
        saveData.put("function", function);
        saveData.put("action", action);
        saveData.put("corp_code", corp_code);
        saveData.put("code", code);
        saveData.put("name", name);
        saveData.put("operation_corp_code", operation_corp_code);
        saveData.put("operation_user_code", operation_user_code);
        saveData.put("remark", remark);
        saveData.put("operation_time", Common.DATETIME_FORMAT.format(now));
        collection.insert(saveData);
    }

}
