package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.service.CRMInterfaceService;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.VipService;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by ZhouZhou on 2017/1/16.
 */
@Service
public class VipServiceImpl implements VipService {

    @Autowired
    CRMInterfaceService crmInterfaceService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    MongoDBClient mongodbClient;

    public String addVip(HashMap<String,Object> vipInfo) throws Exception{

        return "";
    }

    public String saveVipInfo(JSONObject jsonObject,Date now) throws Exception{
        String vip_id = jsonObject.get("vip_id").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String card_no = jsonObject.get("card_no").toString();
        String phone = jsonObject.get("phone").toString();
        if (jsonObject.containsKey("extend") && !jsonObject.get("extend").toString().equals("")) {
            //扩展信息
            String extend = jsonObject.get("extend").toString();
            JSONObject extend_obj = JSONObject.parseObject(extend);
            Iterator<String> iter = extend_obj.keySet().iterator();
            JSONArray array = new JSONArray();
            while (iter.hasNext()) {
                JSONObject obj = new JSONObject();
                String name = iter.next();
                String value = extend_obj.get(name).toString();
                obj.put("column",name);
                obj.put("value",value);
                array.add(obj);
            }
            iceInterfaceService.saveVipExtendInfo(corp_code,vip_id, JSON.toJSONString(array));
        }else {
            MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
            Map keyMap = new HashMap();
            keyMap.put("_id", corp_code + vip_id);
            BasicDBObject queryCondition = new BasicDBObject();
            queryCondition.putAll(keyMap);
            DBCursor dbCursor1 = cursor.find(queryCondition);
            if (dbCursor1.size() > 0) {
                //记录存在，更新
                DBObject updateCondition = new BasicDBObject();
                updateCondition.put("_id", corp_code + vip_id);

                DBObject updatedValue = new BasicDBObject();
                if (jsonObject.containsKey("remark")) {
                    //备注
                    String remark = jsonObject.get("remark").toString();
                    updatedValue.put("remark", remark);
                }
                if (jsonObject.containsKey("avatar") && !jsonObject.get("avatar").toString().equals("")) {
                    //头像
                    String avatar = jsonObject.get("avatar").toString();
                    updatedValue.put("avatar", avatar);
                }
                if (jsonObject.containsKey("image_url") && !jsonObject.get("image_url").toString().equals("")) {
                    //相册
                    DBObject obj = dbCursor1.next();
                    JSONArray array = new JSONArray();
                    if (obj.containsField("album")) {
                        String album = obj.get("album").toString();
                        array = JSON.parseArray(album);
                    }
                    String image_url = jsonObject.get("image_url").toString();
                    JSONObject image = new JSONObject();
                    image.put("image_url", image_url);
                    image.put("time", Common.DATETIME_FORMAT.format(now));
                    array.add(image);

                    updatedValue.put("album", array);
                }
                if (jsonObject.containsKey("memo") && !jsonObject.get("memo").toString().equals("")) {
                    //备忘
                    DBObject obj = dbCursor1.next();
                    JSONArray array = new JSONArray();
                    if (obj.containsField("memo")) {
                        String memo = obj.get("memo").toString();
                        array = JSON.parseArray(memo);
                    }
                    String content = jsonObject.get("memo").toString();
                    JSONObject memo_obj = new JSONObject();
                    memo_obj.put("content", content);
                    memo_obj.put("time", Common.DATETIME_FORMAT.format(now));
                    memo_obj.put("memoid", corp_code + vip_id + Common.DATETIME_FORMAT_DAY_NUM.format(now));
                    array.add(memo_obj);

                    updatedValue.put("memo", array);
                }
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                cursor.update(updateCondition, updateSetValue);
            } else {
                //记录不存在，插入
                DBObject saveData = new BasicDBObject();
                saveData.put("_id", corp_code + vip_id);
                saveData.put("vip_id", vip_id);
                saveData.put("corp_code", corp_code);
                saveData.put("card_no", card_no);
                saveData.put("phone", phone);
                saveData.put("corp_code", corp_code);
                String remark = "";
                String avatar = "";
                JSONArray album_array = new JSONArray();
                JSONArray memo_array = new JSONArray();

                if (jsonObject.containsKey("remark")) {
                    remark = jsonObject.get("remark").toString();
                }
                if (jsonObject.containsKey("avatar")) {
                    avatar = jsonObject.get("avatar").toString();
                }
                if (jsonObject.containsKey("image_url")) {
                    String image_url = jsonObject.get("image_url").toString();
                    JSONObject image = new JSONObject();
                    image.put("image_url", image_url);
                    image.put("time", Common.DATETIME_FORMAT.format(now));
                    album_array.add(image);
                }
                if (jsonObject.containsKey("image_url")) {
                    String content = jsonObject.get("memo").toString();
                    JSONObject memo_obj = new JSONObject();
                    memo_obj.put("content", content);
                    memo_obj.put("time", Common.DATETIME_FORMAT.format(now));
                    memo_obj.put("memoid", corp_code + vip_id + Common.DATETIME_FORMAT_DAY_NUM.format(now));
                    memo_array.add(memo_obj);
                }
                saveData.put("remark", remark);
                saveData.put("avatar", avatar);
                saveData.put("album", album_array);
                saveData.put("memo", memo_array);
                cursor.save(saveData);
            }
        }
        return "";
    }

}
