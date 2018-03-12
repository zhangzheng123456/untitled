package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.VipBackRecordService;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by yanyadong on 2017/4/28.
 */
@Service
public class VipBackRecordServiceImpl implements VipBackRecordService {

    @Autowired
    StoreService storeService;
    @Autowired
    UserService userService;

    //获取每个导购下的回访人数(其中电话回访与短信回访为会员，微信可为非会员和会员)
    @Override
    public String getUserCount(DBCollection dbCollection,String corp_code, String user_code, String action) throws Exception {
      String count="";
        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBObject.put("corp_code",corp_code);
        basicDBObject.put("user_id",user_code);
        if(action.equals("1") || action.equals("2")){
           // basicDBObject.put("vip_id",new BasicDBObject("$ne",null));
            basicDBObject.put("action",action);
            count=dbCollection.distinct("vip_id",basicDBObject).size()+"";
        }else{
            basicDBObject.put("action",action);
            count=dbCollection.distinct("open_id",basicDBObject).size()+"";
        }
        return count;
    }

    //搜索
    public  BasicDBObject getQuery(String param) throws Exception{
        BasicDBList basicDBList=new BasicDBList();
        Pattern pattern = Pattern.compile("^.*" + param + ".*$", Pattern.CASE_INSENSITIVE);
        basicDBList.add(new BasicDBObject("user_name",pattern));
        basicDBList.add(new BasicDBObject("user_id",pattern));
       // basicDBList.add(new BasicDBObject("corp_code",pattern));
        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBObject.put("$or",basicDBList);
        return basicDBObject;
    }

    //筛选
    public  BasicDBObject getScreen(String message,String corp_code) throws Exception{
        JSONObject jsonObject = JSONObject.parseObject(message);
        String screen=jsonObject.getString("list");
        JSONArray jsonArray=JSON.parseArray(screen);
        BasicDBList basicDBList=new BasicDBList();
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject1=jsonArray.getJSONObject(i);
            String screen_key=jsonObject1.getString("screen_key");
            if(screen_key.equals("user_name")){
                if(!jsonObject1.getString("screen_value").equals("")) {
                    String screen_value=jsonObject1.getString("screen_value").toString();
                    String[] str_value=screen_value.split(",");
                    BasicDBList basic_value=new BasicDBList();
                    for(int s=0;s<str_value.length;s++){
                        Pattern pattern = Pattern.compile("^.*" + str_value[s] + ".*$", Pattern.CASE_INSENSITIVE);
                        BasicDBObject basicDBObject=new BasicDBObject("user_name",pattern);
                        basic_value.add(basicDBObject);
                    }
                    basicDBList.add(new BasicDBObject("$or", basic_value));
                }
            }else if(screen_key.equals("user_code")){
                if(!jsonObject1.getString("screen_value").equals("")) {
                    String screen_value=jsonObject1.getString("screen_value").toString();
                    String[] str_value=screen_value.split(",");
                    BasicDBList basic_value=new BasicDBList();
                    for(int s=0;s<str_value.length;s++){
                        Pattern pattern = Pattern.compile("^.*" + str_value[s] + ".*$", Pattern.CASE_INSENSITIVE);
                        BasicDBObject basicDBObject=new BasicDBObject("user_id",pattern);
                        basic_value.add(basicDBObject);
                    }
                    basicDBList.add(new BasicDBObject("$or", basic_value));
                }
            }else if(screen_key.equals("corp_code")){
                if(!jsonObject1.getString("screen_value").equals("")) {
                    String screen_value=jsonObject1.getString("screen_value").toString();
                    String[] str_value=screen_value.split(",");
                    BasicDBList basic_value=new BasicDBList();
                    for(int s=0;s<str_value.length;s++){
                        Pattern pattern = Pattern.compile("^.*" + str_value[s] + ".*$", Pattern.CASE_INSENSITIVE);
                        BasicDBObject basicDBObject=new BasicDBObject("corp_code",pattern);
                        basic_value.add(basicDBObject);
                    }
                    basicDBList.add(new BasicDBObject("$or", basic_value));
                }
            }
            else  if(screen_key.equals("store_name")){
                if(!jsonObject1.getString("screen_value").equals("")) {
                    String screen_value=jsonObject1.getString("screen_value").toString();
                    String[] str_value=screen_value.split(",");
                    BasicDBList userDbList=new BasicDBList();
                    for(int s=0;s<str_value.length;s++) {
                        List<Store> stores = storeService.getStoreByNameTwo(corp_code, str_value[s], "Y");
                        String store_code = "";
                        for (int st = 0; st < stores.size(); st++) {
                            store_code += stores.get(st).getStore_code()+",";
                         }
                         if(StringUtils.isBlank(store_code)){
                             continue;
                         }
                        List<User> userList = userService.selUserByStoreCode(corp_code, "", store_code, null, "","");
                        for(int u=0;u<userList.size();u++){
                            userDbList.add(userList.get(u).getUser_code());
                        }
                    }
                    BasicDBObject basicDBObject=new BasicDBObject("user_id",new BasicDBObject("$in",userDbList));

                    basicDBList.add(basicDBObject);
                }
            }
        }
        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBObject.put("$and",basicDBList);
        return basicDBObject;
    }
}
