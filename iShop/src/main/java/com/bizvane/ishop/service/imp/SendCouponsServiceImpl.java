package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.controller.VIPRecordController;
import com.bizvane.ishop.dao.SendCouponsMapper;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.SendCoupons;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static com.bizvane.ishop.utils.MongoUtils.dbCursorToList;

/**
 * Created by nanji on 2017/11/28.
 */
@Service
public class SendCouponsServiceImpl implements SendCouponsService {
    private static final Logger logger = Logger.getLogger(SendCouponsServiceImpl.class);
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    CorpService corpService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    SendCouponsMapper sendCouponsMapper;
    @Autowired
    private UserService userservicce;
    @Autowired
    private StoreService storeservice;
    private static final Logger log = Logger.getLogger(SendCouponsServiceImpl.class);

    @Override
    public SendCoupons getSendCouponsById(int id) throws Exception {
        SendCoupons m = sendCouponsMapper.selectById(id);
       // logger.info("=========m=============================" + m.getCouponInfo());
        return m;
    }

    @Override
    public SendCoupons getSendCouponsInfoById(int id) throws Exception {
        SendCoupons sendCoupons = sendCouponsMapper.selectById(id);
        //   sendCoupons.getCouponInfo();

        return sendCoupons;
    }

    @Override
    public SendCoupons getSendCouponsInfoByCode(String corp_code, String ticket_code) throws Exception {
        SendCoupons sendCoupons;
        if (corp_code.equals("")) {
            sendCoupons = sendCouponsMapper.selectByCode1(ticket_code);
        } else {
            sendCoupons = sendCouponsMapper.selectByCode(corp_code, ticket_code);
        }
        return sendCoupons;
    }

    @Override
    public PageInfo<SendCoupons> getAllSendCouponsByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<SendCoupons> sendCoupons1;

        PageHelper.startPage(page_number, page_size);
        sendCoupons1 = sendCouponsMapper.selectAllSendCoupons(corp_code, search_value);
        for (SendCoupons sendCoupons : sendCoupons1) {
            sendCoupons.setIsactive(CheckUtils.CheckIsactive(sendCoupons.getIsactive()));
        }
        PageInfo<SendCoupons> page = new PageInfo<SendCoupons>(sendCoupons1);
        return page;
    }

    @Override
    public String updateSendCoupons(String message, String user_code) throws Exception {

        Date now = new Date();
        String state = "";
        String new_phone = "";
        String new_codes = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        int num = 0;

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_user_ticket);

        String id = jsonObject.get("id").toString().trim();

        //原来的信息
        SendCoupons sendCoupons_old = sendCouponsMapper.selectById(Integer.valueOf(id));
        String ticket_code = sendCoupons_old.getTick_code_ishop();
        String corp_code = sendCoupons_old.getCorp_code();

        String users = sendCoupons_old.getSend_coupon_users();
        JSONArray arr_old= JSON.parseArray(users);
        Set set_user_code=new HashSet();
        set_user_code.addAll(arr_old);

        String isactive_old = sendCoupons_old.getIsactive();

        String endtime_old = sendCoupons_old.getTime_end();

        String starttime_old = sendCoupons_old.getTime_start();

        String title_old = sendCoupons_old.getSend_coupon_title();
        String desc_old = sendCoupons_old.getSend_coupon_desc();

        String coupon_innfo = jsonObject.get("coupon_innfo").toString().trim();
        String send_coupon_title = jsonObject.getString("send_coupon_title");
        String send_coupon_desc = jsonObject.getString("send_coupon_desc");
        String time_start = jsonObject.getString("time_start");
        String time_end = jsonObject.getString("end_time");
        //选择发券的对象
        String send_coupon_users = jsonObject.get("send_coupon_user").toString().trim();
        String isactive = jsonObject.getString("isactive");
        String coupon_num_per = jsonObject.getString("coupon_num_per");
        //新列表
        JSONArray arr_users_new = JSON.parseArray(send_coupon_users);
        JSONObject objcoupon = JSON.parseObject(coupon_innfo);
        String coupon_code = objcoupon.getString("couponcode");
        String effective_days = objcoupon.getString("effective_days");
        String parvalue = objcoupon.getString("parvalue");
        String minimumcharge = objcoupon.getString("minimumcharge");
        String end_time = "";
        String start_time = "";
        String quan_details = "";
        //查找mongoDB对应的发券给员工记录 根据编号
        BasicDBObject query = new BasicDBObject();
        query.put("ticket_code_ishop", ticket_code);

        if (objcoupon.containsKey("end_time")) {
            end_time = objcoupon.getString("end_time");
        }
        if (objcoupon.containsKey("start_time")) {
            start_time = objcoupon.getString("start_time");
        }
        if (objcoupon.containsKey("quan_details")) {
            quan_details = objcoupon.getString("quan_details");
        }

        int size = arr_users_new.size();
        String user_num = String.valueOf(size);
        int count = Integer.valueOf(coupon_num_per) * size;
        String coupon_sum = String.valueOf(count);
        String coupon_name = objcoupon.getString("name");
        String corp_name = "";
        Corp corp = corpService.selectByCorpId(0, corp_code, Common.IS_ACTIVE_Y);
        if (corp != null) {
            corp_name = corp.getCorp_name();
        }
        SendCoupons sendCoupons = WebUtils.JSON2Bean(jsonObject, SendCoupons.class);
        sendCoupons.setCorp_code(corp_code);
        sendCoupons.setCoupon_code(coupon_code);
        sendCoupons.setCoupon_name(coupon_name);
        sendCoupons.setCoupon_num_per(coupon_num_per);
        sendCoupons.setCoupon_sum(coupon_sum);
        sendCoupons.setCouponInfo(coupon_innfo);
      //  logger.info("=======sendCoupons==========" + sendCoupons.getCouponInfo());
        sendCoupons.setUser_num(user_num);
        sendCoupons.setSend_coupon_users(send_coupon_users);
        sendCoupons.setTime_end(time_end);
        sendCoupons.setTime_start(time_start);
        sendCoupons.setModified_date(Common.DATETIME_FORMAT.format(now));
        sendCoupons.setModifier(user_code);
        sendCoupons.setCreated_date(Common.DATETIME_FORMAT.format(now));
        sendCoupons.setCreater(user_code);
        BasicDBObject query_for_update = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("ticket_code_ishop", ticket_code));
        values.add(new BasicDBObject("corp_code", corp_code));
        query_for_update.put("$and", values);
        //旧的列表

      //  JSONArray arr_old = JSON.parseArray(users);
        //状态更新的时候
        if (!isactive.equals(isactive_old)) {
            sendCoupons.setIsactive(isactive);
            //更新可用状态
            DBObject updatedValue = new BasicDBObject();
            updatedValue.put("isactive", isactive);
            updatedValue.put("modifier", user_code);
            updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(now));
            DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
            collection.updateMulti(query_for_update, updateSetValue);

        }
        //标题
        if (!send_coupon_title.equals(title_old)) {
            sendCoupons.setSend_coupon_title(send_coupon_title);
        }
        if (!send_coupon_desc.equals(desc_old)) {
            sendCoupons.setSend_coupon_desc(send_coupon_desc);
        }
        if (!time_end.equals(endtime_old)) {
            //更新结束时间

            sendCoupons.setTime_end(time_end);
            DBObject updatedValue = new BasicDBObject();
            updatedValue.put("user_ticket_end_date", time_end);
            updatedValue.put("modifier", user_code);
            updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(now));
            DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
            collection.updateMulti(query_for_update, updateSetValue);
        }
        if (!time_start.equals(starttime_old)) {
            sendCoupons.setTime_start(time_start);
            DBObject updatedValue = new BasicDBObject();
            updatedValue.put("user_ticket_start_date", time_start);
            updatedValue.put("modifier", user_code);
            updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(now));
            DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
            collection.updateMulti(query_for_update, updateSetValue);
        }

       //员工有增/删的时候
        if (!send_coupon_users.equals(users)) {
            //删除旧的
            BasicDBObject queryCondition = new BasicDBObject();
            BasicDBList values2 = new BasicDBList();
            values2.add(new BasicDBObject("corp_code", sendCoupons_old.getCorp_code()));
            values2.add(new BasicDBObject("ticket_code_ishop", ticket_code));
            queryCondition.put("$and", values2);
            collection.remove(queryCondition);
            //新增新的列表
            for (int j = 0; j < arr_users_new.size(); j++) {

                //新的
                JSONObject object = arr_users_new.getJSONObject(j);
                String ticket_user_code = object.getString("user_code");
                String ticket_user_name = object.getString("user_name");
                String ticket_user_phone = object.getString("user_phone");
                    new_phone=new_phone+ticket_user_phone+",";
                    new_codes=new_codes+ticket_user_code+",";
                    //新增的
                    //插mongoDB表
                    DBObject saveData = new BasicDBObject();

                saveData.put("corp_code", sendCoupons.getCorp_code());
                saveData.put("corp_name", corp_name);
                saveData.put("ticket_code_ishop", ticket_code);
                saveData.put("ticket_type_code", sendCoupons.getCoupon_code());
                saveData.put("ticket_type_name", sendCoupons.getCoupon_name());
                saveData.put("ticket_minimumcharge", minimumcharge);
                saveData.put("ticket_effective_days", effective_days);
                saveData.put("ticket_parvalue", parvalue);
                saveData.put("ticket_remark", quan_details);
                saveData.put("ticket_user_code", ticket_user_code);
                saveData.put("ticket_user_name", ticket_user_name);
                saveData.put("ticket_count", Integer.parseInt(coupon_num_per));
                saveData.put("ticket_surplus_count", Integer.parseInt(coupon_num_per));
                saveData.put("user_ticket_start_date", sendCoupons.getTime_start());
                saveData.put("user_ticket_end_date", sendCoupons.getTime_end());
                saveData.put("modified_date", Common.DATETIME_FORMAT.format(now));
                saveData.put("modifier", sendCoupons.getModifier());
                saveData.put("created_date", Common.DATETIME_FORMAT.format(now));
                saveData.put("creater", sendCoupons.getCreater());
                saveData.put("isactive", sendCoupons.getIsactive());
                saveData.put("brand_code", sendCoupons.getBrand_code());
                saveData.put("send_count", "0");
                saveData.put("use_count", "0");
                saveData.put("ticket_start_date", start_time);
                saveData.put("ticket_end_date", end_time);
                    try {
                        Map datalist = new HashMap<String, Data>();
                        Data data_corp_code = new Data("corp_code", sendCoupons.getCorp_code(), ValueType.PARAM);
                        Data data_brand_code = new Data("brand_code", sendCoupons.getBrand_code(), ValueType.PARAM);
                        datalist.put(data_corp_code.key, data_corp_code);
                        datalist.put(data_brand_code.key, data_brand_code);
                        DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetAppId", datalist);
                        String result = dataBox.data.get("message").value;
                        JSONObject object1 = JSON.parseObject(result);
                        String appid = object1.get("appid").toString();
                        saveData.put("app_id", appid);
                    } catch (Exception e) {
                        saveData.put("app_id", "");
                    }
                WriteResult result=collection.insert(saveData);
                int m=result.getN();
               // logger.info(m+"==================================");
                if(!set_user_code.contains(object)){
                    new_codes=new_codes+object.getString("user_code")+",";
                    new_phone=new_phone+object.getString("user_phone")+",";
                }
                }
            //新加的人补发通知
            iceInterfaceService.sendCouponNotice(corp_code, ticket_code, send_coupon_title, new_phone, new_codes, user_code);
        }
        //插mysql
        num = sendCouponsMapper.updateSendCoupons(sendCoupons);
        if (num > 0) {
            state = Common.DATABEAN_CODE_SUCCESS;
        } else {
            state = Common.DATABEAN_CODE_ERROR;
        }
        return state;
    }

    @Override
    public String insert(String message, String user_code) throws Exception {
        Date now = new Date();
        String status = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        int num = 0;
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String coupon_innfo = jsonObject.get("coupon_innfo").toString().trim();
        //选择发券的对象
        String send_coupon_users = jsonObject.get("send_coupon_user").toString().trim();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String ticket_code_ishop = "T" + corp_code + sdf.format(new Date()) + Math.round(Math.random() * 9) + Math.round(Math.random() * 9) + Math.round(Math.random() * 9);
        String brand_code = jsonObject.getString("brand_code");
        String app_id = jsonObject.getString("app_id");
        String app_name = jsonObject.getString("app_name");
        String isactive = jsonObject.getString("isactive");
        String send_coupon_title = jsonObject.getString("send_coupon_title");
        String send_coupon_desc = jsonObject.getString("send_coupon_desc");
        String time_start = jsonObject.getString("time_start");
        String time_end = jsonObject.getString("end_time");
        String coupon_num_per = jsonObject.getString("coupon_num_per");
        JSONArray arr_users = JSON.parseArray(send_coupon_users);
        JSONObject objcoupon = JSON.parseObject(coupon_innfo);
        String coupon_code = objcoupon.getString("couponcode");
        String effective_days = objcoupon.getString("effective_days");
        String parvalue = objcoupon.getString("parvalue");
        String minimumcharge = objcoupon.getString("minimumcharge");
        String end_time = "";
        String start_time = "";
        String quan_details = "";
        if (objcoupon.containsKey("end_time")) {
            end_time = objcoupon.getString("end_time");
        }
        if (objcoupon.containsKey("start_time")) {
            start_time = objcoupon.getString("start_time");
        }
        if (objcoupon.containsKey("quan_details")) {
            quan_details = objcoupon.getString("quan_details");
        }

        int size = arr_users.size();
        String user_num = String.valueOf(size);
        int count = Integer.valueOf(coupon_num_per) * size;
        String coupon_sum = String.valueOf(count);
        String coupon_name = objcoupon.getString("name");
        String corp_name = "";
        String new_phone = "";
        String new_user_code = "";
        Corp corp = corpService.selectByCorpId(0, corp_code, Common.IS_ACTIVE_Y);
        if (corp != null) {
            corp_name = corp.getCorp_name();
        }

        SendCoupons sendCoupons = WebUtils.JSON2Bean(jsonObject, SendCoupons.class);
        sendCoupons.setApp_id(app_id);
        sendCoupons.setCorp_code(corp_code);

        sendCoupons.setApp_name(app_name);
        sendCoupons.setCoupon_code(coupon_code);
        sendCoupons.setCoupon_name(coupon_name);
        sendCoupons.setCoupon_num_per(coupon_num_per);
        sendCoupons.setCoupon_sum(coupon_sum);
        sendCoupons.setUser_num(user_num);
        sendCoupons.setCouponInfo(coupon_innfo);

        sendCoupons.setSend_coupon_users(send_coupon_users);
        sendCoupons.setTime_end(time_end);
        sendCoupons.setSend_coupon_title(send_coupon_title);
        sendCoupons.setBrand_code(brand_code);
        sendCoupons.setTime_start(time_start);
        sendCoupons.setSend_coupon_desc(send_coupon_desc);
        sendCoupons.setTick_code_ishop(ticket_code_ishop);
        sendCoupons.setModified_date(Common.DATETIME_FORMAT.format(now));
        sendCoupons.setModifier(user_code);
        sendCoupons.setCreated_date(Common.DATETIME_FORMAT.format(now));
        sendCoupons.setCreater(user_code);
        sendCoupons.setIsactive(isactive);

        //插mongoDB表
        for (int i = 0; i < arr_users.size(); i++) {
            JSONObject object = arr_users.getJSONObject(i);
            String ticket_user_code = object.getString("user_code");
            String ticket_user_name = object.getString("user_name");
            String ticket_user_phone = object.getString("user_phone");
            new_phone = new_phone + ticket_user_phone + ",";
            new_user_code = new_user_code + ticket_user_code + ",";

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection collection = mongoTemplate.getCollection(CommonValue.table_user_ticket);
            DBObject saveData = new BasicDBObject();

            saveData.put("corp_code", sendCoupons.getCorp_code());
            saveData.put("corp_name", corp_name);
            saveData.put("ticket_code_ishop", sendCoupons.getTick_code_ishop());
            saveData.put("ticket_type_code", sendCoupons.getCoupon_code());
            saveData.put("ticket_type_name", sendCoupons.getCoupon_name());
            saveData.put("ticket_minimumcharge", minimumcharge);
            saveData.put("ticket_effective_days", effective_days);
            saveData.put("ticket_parvalue", parvalue);
            saveData.put("ticket_remark", quan_details);
            saveData.put("ticket_user_code", ticket_user_code);
            saveData.put("ticket_user_name", ticket_user_name);
            saveData.put("ticket_count", Integer.parseInt(coupon_num_per));
            saveData.put("ticket_surplus_count", Integer.parseInt(coupon_num_per));
            saveData.put("user_ticket_start_date", sendCoupons.getTime_start());
            saveData.put("user_ticket_end_date", sendCoupons.getTime_end());
            saveData.put("modified_date", Common.DATETIME_FORMAT.format(now));
            saveData.put("modifier", sendCoupons.getModifier());
            saveData.put("created_date", Common.DATETIME_FORMAT.format(now));
            saveData.put("creater", sendCoupons.getCreater());
            saveData.put("isactive", sendCoupons.getIsactive());
            saveData.put("brand_code", sendCoupons.getBrand_code());
            saveData.put("send_count", "0");
            saveData.put("use_count", "0");
            saveData.put("ticket_start_date", start_time);
            saveData.put("ticket_end_date", end_time);
            try {
                Map datalist = new HashMap<String, Data>();
                Data data_corp_code = new Data("corp_code", sendCoupons.getCorp_code(), ValueType.PARAM);
                Data data_brand_code = new Data("brand_code", sendCoupons.getBrand_code(), ValueType.PARAM);
                datalist.put(data_corp_code.key, data_corp_code);
                datalist.put(data_brand_code.key, data_brand_code);
                DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetAppId", datalist);
                String result = dataBox.data.get("message").value;
                JSONObject object1 = JSON.parseObject(result);
                String appid = object1.get("appid").toString();
                saveData.put("app_id", appid);
            } catch (Exception e) {
                saveData.put("app_id", "");
            }
            collection.insert(saveData);
            //发通知
        }
        iceInterfaceService.sendCouponNotice(corp_code, ticket_code_ishop, send_coupon_title, new_phone, new_user_code, user_code);

        //插mysql
        num = sendCouponsMapper.insertSendCoupons(sendCoupons);


        if (num > 0) {
            SendCoupons sendCoupons1 = getSendCouponsInfoByCode(corp_code, ticket_code_ishop);
            return sendCoupons1.getId();
        } else {
            return Common.DATABEAN_CODE_ERROR;
        }

    }

    @Override
    public String checkSendCoupons(SendCoupons sendCoupons, String ticket_code) throws Exception {
        return null;
    }

    @Override
    public String delete(int id, String user_code) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_user_ticket);
        Date now = new Date();
        String status = "";
        int i = 0;
        //原来的信息
        SendCoupons sendCoupons_old = sendCouponsMapper.selectById(Integer.valueOf(id));
        String ticket_code = sendCoupons_old.getTick_code_ishop();
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("corp_code", sendCoupons_old.getCorp_code()));
        values.add(new BasicDBObject("ticket_code_ishop", ticket_code));
        queryCondition.put("$and", values);
        //logger.info("=========queryCondition.size();=============" + queryCondition.size());
//        DBObject updatedValue = new BasicDBObject();
//        updatedValue.put("isactive", "N");
//        updatedValue.put("modifier", user_code);
//        updatedValue.put("modified_date",Common.DATETIME_FORMAT.format(now));
//        DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
//        collection.updateMulti(queryCondition, updateSetValue);
        collection.remove(queryCondition);

        i = sendCouponsMapper.deleteById(id);

        if (i > 0) {

            status = Common.DATABEAN_CODE_SUCCESS;
        } else {
            status = Common.DATABEAN_CODE_ERROR;
        }
        return status;
    }

    @Override
    public PageInfo<SendCoupons> getAllSendCouponsScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        if (map.containsKey("created_date")) {
            JSONObject date = JSONObject.parseObject(map.get("created_date"));
            params.put("created_date_start", date.get("start").toString());
            String end = date.get("end").toString();
            if (!end.equals(""))
                end = end + " 23:59:59";
            params.put("created_date_end", end);
            map.remove("created_date");
        }
        if (map.containsKey("time_start")) {
            JSONObject date = JSONObject.parseObject(map.get("time_start"));
            params.put("time_start_start", date.get("start").toString());
            String end = date.get("end").toString();
            if (!end.equals(""))
                end = end + " 23:59:59";
            params.put("time_start_end", end);
            map.remove("time_start");
        }
        if (map.containsKey("time_end")) {
            JSONObject date = JSONObject.parseObject(map.get("time_end"));
            params.put("time_end_start", date.get("start").toString());
            String end = date.get("end").toString();
            if (!end.equals(""))
                end = end + " 23:59:59";
            params.put("time_end_end", end);
            map.remove("time_end");
        }

        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<SendCoupons> list1 = sendCouponsMapper.selectAllFsendScreen(params);


        for (SendCoupons vipRules1 : list1) {
            vipRules1.setIsactive(CheckUtils.CheckIsactive(vipRules1.getIsactive()));
        }
        PageInfo<SendCoupons> page = new PageInfo<SendCoupons>(list1);
        return page;

    }

    @Override
    public String sendMessage(SendCoupons sendCoupons, String user_code) throws Exception {
        return null;
    }

    @Override
    public int delSendByCode(String corp_code, String ticket_code) throws Exception {
        return sendCouponsMapper.delSendByCode(corp_code, ticket_code);
    }

    @Override
    public List<SendCoupons> getSendByCode(String corp_code, String ticket_code) throws Exception {
        return sendCouponsMapper.getSendCouponsByCode(corp_code, ticket_code);
    }

    @Override

    public PageInfo getInfo(int page_number, int page_size, String ids, String search_value) throws Exception {

        PageHelper.startPage(page_number, page_size);
        Page page1=new Page(page_number,page_size);
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_user_ticket);
        SendCoupons sendCoupons = sendCouponsMapper.selectById(Integer.valueOf(ids));
        String corp_code = sendCoupons.getCorp_code();
        String ticket_code = sendCoupons.getTick_code_ishop();
        String brand_name = sendCoupons.getBrand_name();
        String num = sendCoupons.getCoupon_sum();
        String users = sendCoupons.getSend_coupon_users();
        JSONArray arr = JSON.parseArray(users);
        JSONArray arr_info = new JSONArray();
        PageInfo page = null;
        if ("".equals(search_value)) {
            BasicDBObject query = new BasicDBObject();
            query.put("ticket_code_ishop", ticket_code);
            query.put("corp_code", corp_code);
            DBCursor dbCursor1 = collection.find(query);
            List<Map<String, Object>> list1 = dbCursorToList(dbCursor1);
            if (list1.size() > 0) {
                for (int i = 0; i < list1.size(); i++) {
                    JSONObject obj_list = new JSONObject();

                    String send_num = list1.get(i).get("send_count").toString();
                    String use_num = list1.get(i).get("use_count").toString();
                    String ticket_user_code1 = list1.get(i).get("ticket_user_code").toString();
                    obj_list.put("user_code", ticket_user_code1);
                    obj_list.put("user_name", list1.get(i).get("ticket_user_name").toString());
                    User user = userservicce.selectUserByCode(corp_code, ticket_user_code1, Common.IS_ACTIVE_Y);
                    String store_user = user.getStore_code();
                    if ("".equals(store_user)) {
                        obj_list.put("store_name", "");
                    } else {
                        store_user = store_user.replace(Common.SPECIAL_HEAD, "");

                        String codes[] = store_user.split(",");

                        if (codes.length > 0) {
                            Store store = storeservice.getStoreByCode(corp_code, codes[0], Common.IS_ACTIVE_Y);
                            if (null != store) {
                                String name = store.getStore_name();
                                obj_list.put("store_name", name);
                            } else {

                                obj_list.put("store_name", "");
                            }
                        }
                    }
                    obj_list.put("brand_name", brand_name);
                    obj_list.put("sum", num);
                    obj_list.put("send_num", send_num);
                    obj_list.put("recruit_num", use_num);
                    //arr_info.add(obj_list);
                    page1.add(obj_list);
                }
                page = new PageInfo(page1);

            }else{
                PageInfo list = null;
                list=new PageInfo(page1);
                return list;
            }
        } else {
            //根据【员工编号/员工姓名 】搜索

            BasicDBObject query2 = new BasicDBObject();
            query2.put("corp_code", corp_code);
            query2.put("ticket_code_ishop", ticket_code);

            BasicDBList dblist = new BasicDBList();
            Pattern pattern = Pattern.compile("^.*" + search_value + ".*$", Pattern.CASE_INSENSITIVE);

            dblist.add(new BasicDBObject("ticket_user_name", pattern));
            dblist.add(new BasicDBObject("ticket_user_code", pattern));
            BasicDBObject oo = new BasicDBObject();
            oo.put("$or", dblist);
            BasicDBList dblist1 = new BasicDBList();
            dblist1.add(oo);
            dblist1.add(query2);
            BasicDBObject search = new BasicDBObject();
            search.put("$and", dblist1);
            DBCursor dbCursor2 = collection.find(search);
            List<Map<String, Object>> list3 = dbCursorToList(dbCursor2);
            logger.info("=========list3==============="+list3.size());
            String send_num="0";
            if (list3.size() > 0) {
                for (int i = 0; i < list3.size(); i++) {
                    JSONObject obj_list = new JSONObject();
                    if(list3.get(i).containsKey("send_count")){

                         send_num = list3.get(i).get("send_count").toString();
                    }else{
                        logger.info("===================null============");
                    }

                    String use_num = list3.get(i).get("use_count").toString();
                    String ticket_user_code1 = list3.get(i).get("ticket_user_code").toString();
                    obj_list.put("user_name", list3.get(i).get("ticket_user_name").toString());
                    obj_list.put("user_code", ticket_user_code1);
                    User user = userservicce.selectUserByCode(corp_code, ticket_user_code1, Common.IS_ACTIVE_Y);
                    String store_user = user.getStore_code();
                    store_user = store_user.replace(Common.SPECIAL_HEAD, "");

                    String codes[] = store_user.split(",");

                    if (codes.length > 0) {
                        Store store = storeservice.getStoreByCode(corp_code, codes[0], Common.IS_ACTIVE_Y);
                        if (null != store) {
                            String name = store.getStore_name();
                            obj_list.put("store_name", name);
                        } else {

                            obj_list.put("store_name", "");
                        }
                    }

                    obj_list.put("brand_name", brand_name);
                    obj_list.put("sum", num);
                    obj_list.put("send_num", send_num);
                    obj_list.put("recruit_num", use_num);

                    page1.add(obj_list);
                    page = new PageInfo(page1);

                }


            }else{

                PageInfo list = null;
                list=new PageInfo(page1);
                return list;
            }
        }
        return page;
    }


}
