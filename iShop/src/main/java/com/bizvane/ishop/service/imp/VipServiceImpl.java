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

    public String recharge(JSONObject jsonObject,DBCollection cursor) throws Exception{
        String type = jsonObject.get("type").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String vip_id = jsonObject.get("vip_id").toString();
        String vip_name = jsonObject.get("vip_name").toString();
        String card_no = jsonObject.get("card_no").toString();//会员卡号
        String remark = jsonObject.get("remark").toString();
        String store_code = jsonObject.get("store_code").toString();//操作店仓
        String store_name = jsonObject.get("store_name").toString();//操作店仓
        String date = jsonObject.get("date").toString();//单据日期

        if (type.equals("pay")) {
            String pay_type = jsonObject.get("pay_type").toString();//1：直接充值:2：退换转充值
            String user_code = jsonObject.get("user_code").toString();//经办人
            String user_name = jsonObject.get("user_name").toString();//经办人

            String price = jsonObject.get("price").toString();//吊牌金额
            String pay_price = jsonObject.get("pay_price").toString();//实付金额
            String discount = jsonObject.get("discount").toString();//折扣

            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("BILLDATE",date);
            map.put("RECHARGE_TYPE",pay_type);
            map.put("C_VIPMONEY_STORE_ID__NAME",store_name);
            map.put("SALESREP_ID__NAME",user_name);
            map.put("C_VIP_ID__CARDNO",card_no);
            map.put("TOT_AMT_ACTUAL",price);
            map.put("AMOUNT_ACTUAL",pay_price);
            map.put("ACTIVE_CONTENT","无");
            map.put("DESCRIPTION",remark);
            String result = crmInterfaceService.addPrepaidDocuments(corp_code,map);

            JSONObject result_obj = JSONObject.parseObject(result);
            String code = result_obj.getString("code");
            if (code.equals("0")){
                result = result_obj.getString("rows");
                JSONObject obj = JSONObject.parseObject(result);
                String bill_id = obj.getString("ID");
                String bill_NO = obj.getString("DOCNO");

                DBObject object = new BasicDBObject();
                object.put("_id",corp_code+"_"+bill_id);
                object.put("corp_code",corp_code);
                object.put("check_type","pay"); //充值
                object.put("check_status","0"); //未审核
                object.put("vip_id",vip_id);
                object.put("card_no",card_no);
                object.put("vip_name",vip_name);

                object.put("store_code",store_code);
                object.put("store_name",store_name);
                object.put("user_code",user_code);
                object.put("user_name",user_name);

                object.put("billNO",bill_NO);
                object.put("date",date);
                object.put("pay_type",pay_type);
                object.put("price",price);
                object.put("pay_price",pay_price);
                object.put("discount",discount);
                object.put("remark",remark);
                cursor.save(object);

            }
        } else if (type.equals("refund")) {
            String refund_type = jsonObject.get("refund_type").toString();//1:充值单退款，2:余额退款
            String sourceNo = jsonObject.get("sourceNo").toString();//来源单号
            String price = jsonObject.get("price").toString();//吊牌金额
            String pay_price = jsonObject.get("pay_price").toString();//实付金额
            String discount = jsonObject.get("discount").toString();//折扣
            String balance = jsonObject.get("balance").toString();//折扣

            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("BILLDATE",date);
            if (refund_type.equals("1")){
                map.put("RECHARGE_TYPE","VM");
            }else if (refund_type.equals("2")){
                map.put("RECHARGE_TYPE","BA");
            }
            map.put("C_VIPMONEY_STORE_ID__NAME",store_name);
            map.put("ORGDOCNO",sourceNo);
            map.put("C_VIP_ID__CARDNO",card_no);
//                    map.put("PASS_WORD",price);
            map.put("TOT_AMT_ACTUAL",price);
            map.put("DESCRIPTION",remark);
            String result = crmInterfaceService.addPrepaidDocuments(corp_code,map);

            JSONObject result_obj = JSONObject.parseObject(result);
            String code = result_obj.getString("code");
            if (code.equals("0")){
                result = result_obj.getString("rows");
                JSONObject obj = JSONObject.parseObject(result);
                String bill_id = obj.getString("ID");
                String bill_NO = obj.getString("DOCNO");

                DBObject object = new BasicDBObject();
                object.put("_id",corp_code+"_"+bill_id);
                object.put("corp_code",corp_code);
                object.put("check_type","refund"); //退款
                object.put("check_status","0"); //未审核
                object.put("vip_id",vip_id);
                object.put("card_no",card_no);
                object.put("vip_name",vip_name);

                object.put("store_code",store_code);
                object.put("store_name",store_name);

                object.put("billNO",bill_NO);
                object.put("date",date);
                object.put("refund_type",refund_type);
                object.put("sourceNo",sourceNo);
                object.put("price",price);
                object.put("pay_price",pay_price);
                object.put("discount",discount);
                object.put("balance",balance);

                object.put("remark",remark);
                cursor.save(object);
            }

        }
        return "";
    }
}
