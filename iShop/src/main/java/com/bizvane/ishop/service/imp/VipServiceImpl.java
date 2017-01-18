package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.controller.VIPController;
import com.bizvane.ishop.service.CRMInterfaceService;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.VipService;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


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

    private static final Logger logger = Logger.getLogger(VipServiceImpl.class);

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
                    String time = jsonObject.get("time").toString();
                    now = Common.DATETIME_FORMAT.parse(time);
                    JSONObject memo_obj = new JSONObject();
                    memo_obj.put("content", content);
                    memo_obj.put("time", time);
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
                if (jsonObject.containsKey("memo")) {
                    String content = jsonObject.get("memo").toString();
                    String time = jsonObject.get("time").toString();
                    now = Common.DATETIME_FORMAT.parse(time);
                    JSONObject memo_obj = new JSONObject();
                    memo_obj.put("content", content);
                    memo_obj.put("time", time);
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

    public String recharge(JSONObject jsonObject,String user_code,String user_name) throws Exception{
        String status = Common.DATABEAN_CODE_SUCCESS;
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_check);

        String type = jsonObject.get("type").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String vip_id = jsonObject.get("vip_id").toString();
        String vip_name = jsonObject.get("vip_name").toString();
        String card_no = jsonObject.get("card_no").toString();//会员卡号
        String remark = jsonObject.get("remark").toString();
        String store_code = jsonObject.get("store_code").toString();//操作店仓
        String store_name = jsonObject.get("store_name").toString();//操作店仓
        String date = jsonObject.get("date").toString().replace("-","");//单据日期
        if (jsonObject.containsKey("user_code") && !jsonObject.getString("user_code").equals(""))
            user_code = jsonObject.get("user_code").toString();//经办人
        if (jsonObject.containsKey("user_name") && !jsonObject.getString("user_name").equals(""))
            user_name = jsonObject.get("user_name").toString();//经办人

        DBObject object = new BasicDBObject();
        object.put("corp_code",corp_code);
        object.put("status","0"); //未审核
        object.put("vip_id",vip_id);
        object.put("card_no",card_no);
        object.put("vip_name",vip_name);

        object.put("created_date",date);
        object.put("modified_date",date);
        object.put("remark",remark);
        if (type.equals("pay")) {
            String recharge_type = jsonObject.get("pay_type").toString();//1：直接充值:2：退换转充值

            String tag_price = jsonObject.get("price").toString();//吊牌金额
            String pay_price = jsonObject.get("pay_price").toString();//实付金额
            String discount = jsonObject.get("discount").toString();//折扣

            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("BILLDATE",date.replace("-",""));
            map.put("RECHARGE_TYPE",recharge_type);
            map.put("C_VIPMONEY_STORE_ID__NAME",store_name);
            map.put("SALESREP_ID__NAME",user_name);
            map.put("C_VIP_ID__CARDNO",card_no);
            map.put("TOT_AMT_ACTUAL",tag_price);
            map.put("AMOUNT_ACTUAL",pay_price);
            map.put("ACTIVE_CONTENT","无");
            map.put("DESCRIPTION",remark);
            String result = crmInterfaceService.addPrepaidDocuments(corp_code,map);

            logger.info("json------------addPrepaidDocuments---" + result);

            JSONObject result_obj = JSONObject.parseObject(result);
            String code = result_obj.getString("code");
            if (code.equals("0")){
                result = result_obj.getString("rows");
                JSONObject obj = JSONObject.parseObject(result);
                String bill_id = obj.getString("ID");
                String bill_no = obj.getString("DOCNO");

                object.put("_id",corp_code+"_"+bill_id+"_"+System.currentTimeMillis());
                object.put("check_type","pay"); //充值
                object.put("bill_no",bill_no);

                object.put("bill_date",date);
                object.put("store_name",store_name);
                object.put("user_name",user_name);
                object.put("recharge_type",recharge_type);
                object.put("tag_price",tag_price);
                object.put("pay_price",pay_price);
                object.put("active_content","无");

//                JSONArray array = new JSONArray();
//                JSONObject content = new JSONObject();
//                content.put("show_name","操作店铺");
//                content.put("show_value",store_name);
//                content.put("key","store_name");
//
//                JSONObject content1 = new JSONObject();
//                content1.put("show_name","操作店铺编号");
//                content1.put("show_value",store_code);
//                content1.put("key","store_code");
//
//                JSONObject content2 = new JSONObject();
//                content2.put("show_name","操作人");
//                content2.put("show_value",user_name);
//                content2.put("key","user_name");
//
//                JSONObject content3 = new JSONObject();
//                content3.put("show_name","操作人编号");
//                content3.put("show_value",user_code);
//                content3.put("key","user_code");

//                JSONObject content4 = new JSONObject();
//                content4.put("show_name","充值类型");
//                content4.put("show_value",pay_type);
//                content4.put("key","recharge_type");
//
//                JSONObject content5 = new JSONObject();
//                content5.put("show_name"," 折合吊牌金额");
//                content5.put("show_value",tag_price);
//                content5.put("key","tag_price");

//                JSONObject content6 = new JSONObject();
//                content6.put("show_name"," 实付金额");
//                content6.put("show_value",pay_price);
//                content6.put("key","pay_price");
//
//                JSONObject content7 = new JSONObject();
//                content7.put("show_name"," 活动内容");
//                content7.put("show_value","无");
//                content7.put("key","active_content");
//
//                JSONObject content8 = new JSONObject();
//                content8.put("show_name"," 单据编号");
//                content8.put("show_value", bill_NO);
//                content8.put("key","bill_no");
//
//                array.add(content);
//                array.add(content1);
//                array.add(content2);
//                array.add(content3);
//                array.add(content4);
//                array.add(content5);
//                array.add(content6);
//                array.add(content7);
//                array.add(content8);
//                object.put("content",array);

                cursor.save(object);

            }else {
                return result_obj.getString("message");
            }
        } else if (type.equals("refund")) {
            String recharge_type = jsonObject.get("pay_type").toString();//1:充值单退款，2:余额退款
            String source_no = jsonObject.get("sourceNo").toString();//来源单号
//            String price = jsonObject.get("price").toString();//吊牌金额
//            String pay_price = jsonObject.get("pay_price").toString();//实付金额
//            String discount = jsonObject.get("discount").toString();//折扣
//            String balance = jsonObject.get("balance").toString();//折扣

            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("BILLDATE",date);
            if (recharge_type.equals("1")){
                map.put("RECHARGE_TYPE","VM");
            }else if (recharge_type.equals("2")){
                map.put("RECHARGE_TYPE","BA");
            }
            map.put("C_VIPMONEY_STORE_ID__NAME",store_name);
            map.put("ORGDOCNO",source_no);
            map.put("C_VIP_ID__CARDNO",card_no);
//            map.put("TOT_AMT_ACTUAL",price);
            map.put("DESCRIPTION",remark);
            String result = crmInterfaceService.addRefund(corp_code,map);

            logger.info("json------------addRefund---" + result);

            JSONObject result_obj = JSONObject.parseObject(result);
            String code = result_obj.getString("code");
            if (code.equals("0")){
                result = result_obj.getString("rows");
                JSONObject obj = JSONObject.parseObject(result);
                String bill_id = obj.getString("ID");
                String bill_no = obj.getString("DOCNO");

                object.put("_id",corp_code+"_"+bill_id+"_"+System.currentTimeMillis());
                object.put("check_type","refund"); //退款
                object.put("bill_no",bill_no);

                object.put("bill_date",date);
                object.put("store_name",store_name);
                object.put("user_name",user_name);
                object.put("recharge_type",recharge_type);
                object.put("source_no",source_no);

//                JSONArray array = new JSONArray();
//                JSONObject content = new JSONObject();
//                content.put("show_name","操作店铺");
//                content.put("show_value",store_name);
//                content.put("key","store_name");
//
//                JSONObject content1 = new JSONObject();
//                content1.put("show_name","操作店铺编号");
//                content1.put("show_value",store_code);
//                content1.put("key","store_code");
//
//                JSONObject content2 = new JSONObject();
//                content2.put("show_name","操作人");
//                content2.put("show_value",user_name);
//                content2.put("key","user_name");
//
//                JSONObject content3 = new JSONObject();
//                content3.put("show_name","操作人编号");
//                content3.put("show_value",user_code);
//                content3.put("key","user_code");

//                JSONObject content4 = new JSONObject();
//                content4.put("show_name","退款类型");
//                content4.put("show_value",recharge_type);
//                content4.put("key","recharge_type");

//                JSONObject content5 = new JSONObject();
//                content5.put("show_name","来源单号");
//                content5.put("show_value",sourceNo);
//                content5.put("key","sourceNo");

//                JSONObject content8 = new JSONObject();
//                content8.put("show_name"," 单据编号");
//                content8.put("show_value", bill_NO);
//                content8.put("key","bill_no");
//
//                array.add(content);
//                array.add(content1);
//                array.add(content2);
//                array.add(content3);
//                array.add(content4);
//                array.add(content5);
//                array.add(content8);
//
//                object.put("content",array);
                cursor.save(object);
            }else {
                return result_obj.getString("message");
            }
        }
        return status;
    }

    /**
     * 获取验证码
     */
    @Transactional
    public String sendSMS(String text,String phone) throws Exception {
        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_text = new Data("text", text, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_phone.key, data_phone);
        datalist.put(data_text.key, data_text);

        DataBox dataBox = iceInterfaceService.iceInterface("SendSMS", datalist);
        return "";
    }
}
