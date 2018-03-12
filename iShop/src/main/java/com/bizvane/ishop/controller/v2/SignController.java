package com.bizvane.ishop.controller.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.SignService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by yin on 2016/6/23.
 */
@Controller
@RequestMapping("/sign")
public class SignController {
    @Autowired
    UserService userService;
    @Autowired
    SignService signService;
    @Autowired
    StoreService storeService;
    @Autowired
    BaseService baseService;
    @Autowired
    MongoDBClient mongodbClient;

    String id;
    private static final Logger logger = Logger.getLogger(SignController.class);

    @RequestMapping(value = "/signView", method = RequestMethod.POST)
    @ResponseBody
    public String getSignView(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);
        try {
            com.alibaba.fastjson.JSONObject sign_object = new com.alibaba.fastjson.JSONObject();
            JSONArray sign_jsonArray = new JSONArray();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String param = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
            //系统管理员
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            //筛选条件：时间段
            String date_type = jsonObject.get("date_type").toString();

            if (jsonObject.get("user_code").equals("")) {
                String storeByScreen = baseService.getStoreByScreen(jsonObject, request);
                String[] split_store = storeByScreen.split(",");
                BasicDBList value = new BasicDBList();
                for (int i = 0; i < split_store.length; i++) {
                    value.add(split_store[i]);
                }
                int userCountByStore = 0;

                if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) && storeByScreen.equals("")) {
                    userCountByStore = userService.getUserCountByStore(corp_code, null);
                } else {
                    String[] store2Ts = WebUtils.store2Ts(storeByScreen);
                    userCountByStore = userService.getUserCountByStore(corp_code, store2Ts);
                }
                //周
                if ("W".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    String[] dates = TimeUtils.getWeek2Day(date_value).split(",");
                    for (int i = 0; i < dates.length; i++) {

                        BasicDBList values = new BasicDBList();
                        BasicDBList values1 = new BasicDBList();
                        BasicDBObject dbObject1 = new BasicDBObject();
                        dbObject1.put("corp_code", corp_code);
                        BasicDBObject dbObject2 = new BasicDBObject();
                        dbObject2.put("store_id", new BasicDBObject("$in", value));
                        BasicDBObject dbObject3 = new BasicDBObject();
                        dbObject3.put("user_id", user_code);

                        if (value != null && value.size() > 0) {
                            values.add(dbObject1);
                            values.add(dbObject2);
                            values1.add(dbObject1);
                            values1.add(dbObject2);
                        } else {
                            values.add(dbObject1);
                            values.add(dbObject3);
                            values1.add(dbObject1);
                            values1.add(dbObject3);
                        }
                        Pattern pattern = Pattern.compile("^.*" + dates[i] + ".*$", Pattern.CASE_INSENSITIVE);

                        //签到0   签退-1
                        BasicDBObject dbObject4 = new BasicDBObject();
                        dbObject4.put("sign_time", pattern);
                        dbObject4.put("status", "0");
                        values.add(dbObject4);
                       logger.info("============================="+value.size());
                        BasicDBObject dbObject5 = new BasicDBObject();
                        dbObject5.put("sign_time", pattern);
                        dbObject5.put("status", "-1");
                        values1.add(dbObject5);
                        logger.info("============================="+values1.size());


                        DBCursor dbCursor1 = cursor.find(values);
                        //签到人数（未签退）
                        int sign_in = dbCursor1.count();
                        //签退人数（已签到）
                        DBCursor dbCursor2 = cursor.find(values1);
                        int sign_out = dbCursor2.count();
                        //未签到人数
                        int no_sign = userCountByStore - sign_in;
                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates[i]);
                        json_data.put("sign_in", sign_in);
                        json_data.put("sign_out", sign_out);
                        json_data.put("no_sign", no_sign);
                        sign_jsonArray.add(json_data);
                    }
                    // 月
                } else if ("M".equals(date_type)) {

                    String date_value = jsonObject.get("date_value").toString();

                    List<String> dates = TimeUtils.getMonthAllDays(date_value);
                    for (int i = 0; i < dates.size(); i++) {

                        BasicDBList values = new BasicDBList();
                        BasicDBList values1 = new BasicDBList();
                        BasicDBObject dbObject1 = new BasicDBObject();
                        dbObject1.put("corp_code", corp_code);
                        BasicDBObject dbObject2 = new BasicDBObject();
                        dbObject2.put("store_id", new BasicDBObject("$in", value));
                        BasicDBObject dbObject3 = new BasicDBObject();
                        dbObject3.put("user_id", user_code);

                        if (value != null && value.size() > 0) {
                            values.add(dbObject1);
                            values.add(dbObject2);
                            values1.add(dbObject1);
                            values1.add(dbObject2);
                        } else {
                            values.add(dbObject1);
                            values.add(dbObject3);
                            values1.add(dbObject1);
                            values1.add(dbObject3);
                        }

                        Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                        BasicDBObject dbObject4 = new BasicDBObject();
                        dbObject4.put("sign_time", pattern);
                        dbObject4.put("status", "0");
                        values.add(dbObject4);
                        BasicDBObject dbObject5 = new BasicDBObject();
                        dbObject5.put("sign_time", pattern);
                        dbObject5.put("status", "-1");
                        values1.add(dbObject5);

                        DBCursor dbCursor1 = cursor.find(values);
                        DBCursor dbCursor2 = cursor.find(values1);
                        int sign_in = dbCursor1.count();
                        int sign_out = dbCursor2.count();
                        int no_sign = userCountByStore - sign_in;

                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates.get(i).substring(5, date_value.length()));
                        json_data.put("sign_in", sign_in);
                        json_data.put("sign_out", sign_out);
                        json_data.put("no_sign", no_sign);
                        sign_jsonArray.add(json_data);
                    }

                } else if ("Y".equals(date_type)) {  //年
                    String date_value = jsonObject.get("date_value").toString();
                    List<String> dates = TimeUtils.getYearAllDays(date_value);
                    for (int i = 0; i < dates.size(); i++) {

                        BasicDBList values = new BasicDBList();
                        BasicDBList values1 = new BasicDBList();
                        BasicDBObject dbObject1 = new BasicDBObject();
                        dbObject1.put("corp_code", corp_code);
                        BasicDBObject dbObject2 = new BasicDBObject();
                        dbObject2.put("store_id", new BasicDBObject("$in", value));
                        BasicDBObject dbObject3 = new BasicDBObject();
                        dbObject3.put("user_id", user_code);
                        if (value != null && value.size() > 0) {
                            values.add(dbObject1);
                            values.add(dbObject2);
                            values1.add(dbObject1);
                            values1.add(dbObject2);

                        } else {
                            values.add(dbObject1);
                            values.add(dbObject3);
                            values1.add(dbObject1);
                            values1.add(dbObject3);
                        }
                        Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                        BasicDBObject dbObject4 = new BasicDBObject();
                        dbObject4.put("sign_time", pattern);
                        dbObject4.put("status", "0");
                        values.add(dbObject4);
                        BasicDBObject dbObject5 = new BasicDBObject();
                        dbObject5.put("sign_time", pattern);
                        dbObject5.put("status", "-1");
                        values1.add(dbObject5);

                        DBCursor dbCursor1 = cursor.find(values);
                        DBCursor dbCursor2 = cursor.find(values1);
                        int sign_in = dbCursor1.count();
                        int sign_out = dbCursor2.count();
                        int no_sign = userCountByStore - sign_in;

                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates.get(i));
                        json_data.put("sign_in", sign_in);
                        json_data.put("sign_out", sign_out);
                        json_data.put("no_sign", no_sign);
                        sign_jsonArray.add(json_data);

                    }
                }
                sign_object.put("view_type", "histogram");
                sign_object.put("unit", "number_person");

            } else {

                String user_code_json = jsonObject.get("user_code").toString();
                if (date_type.equals("W")) {
                    BasicDBList value = new BasicDBList();

                    BasicDBObject dbObject1 = new BasicDBObject();
                    dbObject1.put("corp_code", corp_code);
                    BasicDBObject dbObject2 = new BasicDBObject();
                    dbObject2.put("user_code", user_code_json);

                    String date_value = jsonObject.get("date_value").toString();
                    String[] dates = TimeUtils.getWeek2Day(date_value).split(",");
                    for (int i = 0; i < dates.length; i++) {

                        Pattern pattern = Pattern.compile("^.*" + dates[i] + ".*$", Pattern.CASE_INSENSITIVE);
                        BasicDBObject dbObject3 = new BasicDBObject();
                        dbObject3.put("sign_time", pattern);
                        dbObject3.put("status", "0");
                        value.add(dbObject1);
                        value.add(dbObject2);
                        value.add(dbObject3);
                        BasicDBObject dbObject4 = new BasicDBObject();
                        dbObject4.put("sign_time", pattern);
                        dbObject4.put("status", "-1");
                        BasicDBList value1 = new BasicDBList();
                        value1.add(dbObject1);
                        value1.add(dbObject2);
                        value1.add(dbObject4);

                        DBCursor dbCursor1 = cursor.find(value);
                        DBCursor dbCursor2 = cursor.find(value1);
                        int sign_in = 0;
                        int sign_out = 0;
                        int no_sign = 0;
                        while (dbCursor1.hasNext()) {
                            DBObject sign_log = dbCursor1.next();
                            if (sign_log.containsField("count")) {
                                sign_in += Integer.parseInt(sign_log.get("count").toString());
                            }
                        }
                        while (dbCursor2.hasNext()) {
                            DBObject sign_log = dbCursor2.next();
                            if (sign_log.containsField("count")) {
                                sign_out += Integer.parseInt(sign_log.get("count").toString());
                            }
                        }
                        //一天只能签到一次 未签到的次数等于一周的天数减去签到的次数
                        no_sign = dates.length - sign_in;
                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates[i]);
                        json_data.put("sign_in", sign_in);
                        json_data.put("sign_out", sign_out);
                        json_data.put("no_sign", no_sign);
                        sign_jsonArray.add(json_data);
                    }
                } else if ("M".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    List<String> dates = TimeUtils.getMonthAllDays(date_value);
                    for (int i = 0; i < dates.size(); i++) {

                        BasicDBList value = new BasicDBList();
                        BasicDBObject dbObject1 = new BasicDBObject();
                        dbObject1.put("corp_code", corp_code);
                        BasicDBObject dbObject2 = new BasicDBObject();
                        dbObject2.put("user_code", user_code_json);
                        value.add(new BasicDBObject("corp_code", corp_code));
                        value.add(new BasicDBObject("user_code", user_code_json));

                        Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                        BasicDBObject dbObject3 = new BasicDBObject();
                        dbObject3.put("sign_time", pattern);
                        dbObject3.put("status", "0");
                        value.add(dbObject1);
                        value.add(dbObject2);
                        value.add(dbObject3);
                        BasicDBObject dbObject4 = new BasicDBObject();
                        dbObject4.put("sign_time", pattern);
                        dbObject4.put("status", "-1");
                        BasicDBList value1 = new BasicDBList();
                        value1.add(dbObject1);
                        value1.add(dbObject2);
                        value1.add(dbObject4);

                        DBCursor dbCursor1 = cursor.find(value);
                        DBCursor dbCursor2 = cursor.find(value1);
                        int sign_in = 0;
                        int sign_out = 0;
                        int no_sign = 0;
                        while (dbCursor1.hasNext()) {
                            DBObject sign_log = dbCursor1.next();
                            if (sign_log.containsField("count")) {
                                sign_in += Integer.parseInt(sign_log.get("count").toString());
                            }
                        }
                        while (dbCursor2.hasNext()) {
                            DBObject sign_log = dbCursor2.next();
                            if (sign_log.containsField("count")) {
                                sign_out += Integer.parseInt(sign_log.get("count").toString());
                            }
                        }

                        no_sign = dates.size() - sign_in;
                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates.get(i).substring(5, date_value.length()));
                        json_data.put("sign_in", sign_in);
                        json_data.put("sign_out", sign_out);
                        json_data.put("no_sign", no_sign);

                        sign_jsonArray.add(json_data);
                    }
                } else if ("Y".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    List<String> dates = TimeUtils.getYearAllDays(date_value);
                    for (int i = 0; i < dates.size(); i++) {
                        BasicDBList value = new BasicDBList();
                        BasicDBObject dbObject1 = new BasicDBObject();
                        dbObject1.put("corp_code", corp_code);
                        BasicDBObject dbObject2 = new BasicDBObject();
                        dbObject2.put("user_code", user_code_json);
                        Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                        BasicDBObject dbObject3 = new BasicDBObject();
                        dbObject3.put("sign_time", pattern);
                        dbObject3.put("status", "0");
                        value.add(dbObject1);
                        value.add(dbObject2);
                        value.add(dbObject3);
                        BasicDBObject dbObject4 = new BasicDBObject();
                        dbObject4.put("sign_time", pattern);
                        dbObject4.put("status", "-1");
                        BasicDBList value1 = new BasicDBList();
                        value1.add(dbObject1);
                        value1.add(dbObject2);
                        value1.add(dbObject4);

                        DBCursor dbCursor1 = cursor.find(value);
                        DBCursor dbCursor2 = cursor.find(value1);
                        int sign_in = 0;
                        int sign_out = 0;
                        int no_sign = 0;
                        while (dbCursor1.hasNext()) {
                            DBObject sign_log = dbCursor1.next();
                            if (sign_log.containsField("count")) {
                                sign_in += Integer.parseInt(sign_log.get("count").toString());
                            }
                        }
                        while (dbCursor2.hasNext()) {
                            DBObject sign_log = dbCursor2.next();
                            if (sign_log.containsField("count")) {
                                sign_out += Integer.parseInt(sign_log.get("count").toString());
                            }
                        }

                        List<String> date1 = TimeUtils.getMonthAllDays(date_value);
                        no_sign = date1.size() - sign_in;
                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates.get(i));
                        json_data.put("sign_in", sign_in);
                        json_data.put("sign_out", sign_out);
                        json_data.put("no_sign", no_sign);
                        sign_jsonArray.add(json_data);
                    }
                }
                sign_object.put("view_type", "histogram");
                sign_object.put("unit", "number_sign");

            }
            sign_object.put("sign", sign_jsonArray);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(sign_object.toJSONString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 签到记录列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/newSignList", method = RequestMethod.POST)
    @ResponseBody
    public String getNewSignList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);
        int pages = 0;
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        try {

            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String param = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            String date_type = jsonObject.get("date_type").toString();
            List<User> canloginByCode = null;
            DBCursor dbCursor = null;
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            if (jsonObject.get("user_code").equals("")) {
                String storeByScreen = baseService.getStoreByScreen(jsonObject, request);
                String[] split_store = storeByScreen.split(",");

                BasicDBList value = new BasicDBList();
                for (int i = 0; i < split_store.length; i++) {
                    value.add(split_store[i]);
                }
                if ("W".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    String[] dates = TimeUtils.getWeek2Day(date_value).split(",");

                    BasicDBObject ref = new BasicDBObject();
                    BasicDBList values = new BasicDBList();
                    if (value != null && value.size() > 0) {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("store_id", new BasicDBObject("$in", value)));
                    } else {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_id", user_code));
                    }

                    values.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.GTE, dates[0])));

                    values.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.LTE, dates[dates.length-1])));

                    DBCursor dbCursor1 = cursor.find(ref);

                    pages = MongoUtils.getPages(dbCursor1, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "sign_time", -1);
                    canloginByCode = userService.getCanloginByCode(corp_code);


                } else if ("M".equals(date_type)) {

                    String date_value = jsonObject.get("date_value").toString();

                    List<String> dates = TimeUtils.getMonthAllDays(date_value);

                    BasicDBObject ref = new BasicDBObject();
                    BasicDBList values = new BasicDBList();
                    if (value != null && value.size() > 0) {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("store_id", new BasicDBObject("$in", value)));
                    } else {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_id", user_code));
                    }
                    values.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.GTE, dates.get(0))));

                    values.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size()-1))));
                    ref.put("$and", values);
                    DBCursor dbCursor1 = cursor.find(ref);

                    pages = MongoUtils.getPages(dbCursor1, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "sign_time", -1);
                    canloginByCode = userService.getCanloginByCode(corp_code);

                } else if ("Y".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    List<String> dates = TimeUtils.getYearAllDays(date_value);

                    BasicDBObject ref = new BasicDBObject();
                    BasicDBList values = new BasicDBList();
                    if (value != null && value.size() > 0) {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("store_id", new BasicDBObject("$in", value)));
                    } else {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_id", user_code));
                    }
                    values.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.GTE, dates.get(0) + "-01")));

                    values.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size()-1) + "-31")));
                    ref.put("$and", values);
                    DBCursor dbCursor1 = cursor.find(ref);

                    pages = MongoUtils.getPages(dbCursor1, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "sign_time", -1);
                    canloginByCode = userService.getCanloginByCode(corp_code);

                }

            } else {
                String user_code_json = jsonObject.get("user_code").toString();
                BasicDBObject queryCondition1 = new BasicDBObject();
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("user_code", user_code_json));
                if (date_type.equals("W")) {
                    String date_value = jsonObject.get("date_value").toString();

                    String[] dates = TimeUtils.getWeek2Day(date_value).split(",");

                    value.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.GTE, dates[0])));

                    value.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.LTE, dates[dates.length-1])));
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);
                    pages = MongoUtils.getPages(dbCursor2, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "sign_time", -1);
                    canloginByCode = userService.getCanloginByCode(corp_code);

                } else if ("M".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    List<String> dates = TimeUtils.getMonthAllDays(date_value);

                    value.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.GTE, dates.get(0))));

                    value.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size()-1))));
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);
                    pages = MongoUtils.getPages(dbCursor2, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "sign_time", -1);
                    canloginByCode = userService.getCanloginByCode(corp_code);

                } else if ("Y".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    List<String> dates = TimeUtils.getYearAllDays(date_value);

                    value.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.GTE, dates.get(0) + "-01")));
                    value.add(new BasicDBObject("sign_time", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size()-1) + "-31")));
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);
                    pages = MongoUtils.getPages(dbCursor2, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "sign_time", -1);
                    canloginByCode = userService.getCanloginByCode(corp_code);


                }
            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_canLogin(dbCursor, canloginByCode, "");
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * Mongodb条件查询
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        int pages = 0;
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);

            String[] column_names = new String[]{"user_code", "user_name", "corp_name"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
            DBCursor dbCursor = null;
            DBCursor dbCursor1 = null;
            int total = 0;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                dbCursor1 = cursor.find(queryCondition);
            }else if(role_code.equals(Common.ROLE_CM)){
//                BasicDBList value = new BasicDBList();
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>"+manager_corp);
//                String[] split = manager_corp.split(",");
//                BasicDBList manager_corp_arr = new BasicDBList();
//                for (int i = 0; i < split.length; i++) {
//                    manager_corp_arr.add(split[i]);
//                }
//                if(manager_corp_arr.size()>0) {
//                    value.add(new BasicDBObject("corp_code", new BasicDBObject("$in", manager_corp_arr)));
//                }
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                dbCursor1 = cursor.find(queryCondition1);
                //企业管理员
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                 String  corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                //         value.add(new BasicDBObject("role_code", role_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor1 = cursor.find(queryCondition1);
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM)) {
                    //企业管理员
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    //         value.add(new BasicDBObject("role_code", role_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor1 = cursor.find(queryCondition1);

                } else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    if (!area_code.equals("")) {
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    }
                    List<Store> stores = null;

                    stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");

                    //添加.......
                    BasicDBList value = new BasicDBList();
                    //  BasicDBObject ref=new BasicDBObject();
                    String store_code = "";
                    //...
                    for (int i = 0; i < stores.size(); i++) {
                        //       store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                        store_code = stores.get(i).getStore_code().toString();
                        value.add(store_code);
                    }
                    BasicDBObject ba = new BasicDBObject();
                    BasicDBList values = new BasicDBList();
                    if (value != null && value.size() > 0) {
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                        values.add(new BasicDBObject("corp_code", corp_code));
                    } else {

                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    values.add(queryCondition);
                    ba.put("$and", values);
                    dbCursor1 = cursor.find(ba);

                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    BasicDBList values = new BasicDBList();
                    //  value.add(new BasicDBObject("store_code", store_code));

                    String[] stores = null;
                    if (!store_code.equals("")) {
                        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                        stores = store_code.split(",");
                    }
                    BasicDBList value = new BasicDBList();

                    for (int i = 0; i < stores.length; i++) {
                        //       store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                        store_code = stores[i].toString();
                        value.add(store_code);
                    }
                    //   value.add(new BasicDBObject("role_code", role_code));
                    values.add(queryCondition);

                    if (value != null && value.size() > 0) {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                    } else {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", values);
                    dbCursor1 = cursor.find(queryCondition1);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    //.....
                    if (!area_code.equals("")) {
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        String[] areas = area_code.split(",");
                        String[] storeCodes = null;
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCodes = store_code.split(",");
                        }
                        List<Store> store = null;
                        store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, "");
                        BasicDBList value = new BasicDBList();
                        BasicDBObject ba = new BasicDBObject();

                        for (int i = 0; i < store.size(); i++) {
                            store_code = store.get(i).getStore_code().toString();
                            value.add(store_code);
                        }
                        BasicDBList values = new BasicDBList();
                        if (value.size() > 0 && value != null) {
                            values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                            values.add(new BasicDBObject("corp_code", corp_code));
                        } else {
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("user_code", user_code));
                        }
                        values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                        values.add(queryCondition);
                        ba.put("$and", values);
                        dbCursor1 = cursor.find(ba);
                    }

                } else if (role_code.equals(Common.ROLE_STAFF)) {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(new BasicDBObject("user_code", user_code));
                    value.add(queryCondition);
                    value.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor1 = cursor.find(queryCondition1);
                }
            }
            total = dbCursor1.count();
            pages = MongoUtils.getPages(dbCursor1, page_size);
            dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "modified_date", -1);

            ArrayList list = MongoHelperServiceImpl.dbCursorToList_status(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            result.put("total", total);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }


    /**
     * Mongodb删除事务
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String inter_id = jsonObject.get("id").toString();
            String[] ids = inter_id.split(",");

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);

            for (int i = 0; i < ids.length; i++) {
                //logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                //       Sign sign = signService.selSignById(Integer.valueOf(ids[i]));
                //signService.delSignById(Integer.valueOf(ids[i]));

                BasicDBObject deleteRecord = new BasicDBObject();
                deleteRecord.put("_id", new ObjectId(ids[i]));
                //插入到用户操作日志
                DBCursor dbObjects = cursor.find(deleteRecord);
                String t_corp_code = "";
                String t_code = "";
                String t_name = "";
                String modified_date = "";
                String status = "";

                while (dbObjects.hasNext()) {
                    DBObject sign = dbObjects.next();
                    if (sign.containsField("corp_code")) {
                        t_corp_code = sign.get("corp_code").toString();
                    }
                    if (sign.containsField("user_code")) {
                        t_code = sign.get("user_code").toString();
                    }
                    if (sign.containsField("user_name")) {
                        t_name = sign.get("user_name").toString();
                    }
                    if (sign.containsField("modified_date")) {
                        modified_date = sign.get("modified_date").toString();
                    }
                    if (sign.containsField("status")) {
                        status = sign.get("status").toString();
                    }
                }

//----------------行为日志开始------------------------------------------
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
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "员工管理_签到管理";
                String action = Common.ACTION_DEL;
                //   String t_corp_code = sign.getCorp_code();
                //   String t_code = sign.getUser_code();
                //    String t_name = sign.getUser_name();
                String remark = modified_date + "(" + status + ")";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                cursor.remove(deleteRecord);

            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }


    /***
     * Mongodb导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();

            ArrayList list = new ArrayList();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);
            DBObject sort_obj = new BasicDBObject("modified_date", -1);

            if (screen.equals("")) {

                String[] column_names = new String[]{"user_code", "user_name", "corp_name"};
                BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);

                DBCursor dbCursor = null;

                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    dbCursor = cursor.find(queryCondition).sort(sort_obj);
                } else if(role_code.equals(Common.ROLE_CM)){
//                BasicDBList value = new BasicDBList();
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>"+manager_corp);
//                String[] split = manager_corp.split(",");
//                BasicDBList manager_corp_arr = new BasicDBList();
//                for (int i = 0; i < split.length; i++) {
//                    manager_corp_arr.add(split[i]);
//                }
//                if(manager_corp_arr.size()>0) {
//                    value.add(new BasicDBObject("corp_code", new BasicDBObject("$in", manager_corp_arr)));
//                }
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                dbCursor1 = cursor.find(queryCondition1);
                    //企业管理员
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>" + manager_corp);
                      corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>"+corp_code);
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    //         value.add(new BasicDBObject("role_code", role_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                }else if (role_code.equals(Common.ROLE_GM)) {
                    //系统管理员
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                    String store_code = "";
                    BasicDBList value = new BasicDBList();
                    for (int i = 0; i < stores.size(); i++) {

                        store_code = stores.get(i).getStore_code().toString();
                        value.add(store_code);
                    }
                    BasicDBObject ba = new BasicDBObject();
                    BasicDBList values = new BasicDBList();
                    if (value != null && value.size() > 0) {
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                        values.add(new BasicDBObject("corp_code", corp_code));
                    } else {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    values.add(queryCondition);
                    ba.put("$and", values);
                    dbCursor = cursor.find(ba).sort(sort_obj);

                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    BasicDBList values = new BasicDBList();
                    String[] stores = null;
                    if (!store_code.equals("")) {
                        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                        stores = store_code.split(",");
                    }
                    BasicDBList value = new BasicDBList();

                    for (int i = 0; i < stores.length; i++) {

                        store_code = stores[i].toString();
                        value.add(store_code);
                    }

                    values.add(queryCondition);
                    if (value != null && value.size() > 0) {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                    } else {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", values);

                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);

                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();

                    if (!area_code.equals("")) {
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        String[] areas = area_code.split(",");
                        String[] storeCodes = null;
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCodes = store_code.split(",");
                        }

                        BasicDBList value = new BasicDBList();
                        List<Store> store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, "");
                        for (int i = 0; i < store.size(); i++) {
                            store_code = store.get(i).getStore_code().toString();
                            value.add(store_code);
                        }
                        BasicDBList values = new BasicDBList();
                        if (value != null && value.size() > 0) {
                            values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                            values.add(new BasicDBObject("corp_code", corp_code));
                        } else {
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("user_code", user_code));
                        }
                        values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                        values.add(queryCondition);
                        BasicDBObject queryCondition1 = new BasicDBObject();
                        queryCondition1.put("$and", values);
                        dbCursor = cursor.find(queryCondition1).sort(sort_obj);

                    }

                } else if (role_code.equals(Common.ROLE_STAFF)) {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(new BasicDBObject("user_code", user_code));
                    value.add(queryCondition);
                    value.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);

                }
                list = MongoHelperServiceImpl.dbCursorToList_status(dbCursor);

            } else {
                JSONArray array = JSONArray.parseArray(screen);
                BasicDBObject queryCondition = MongoHelperServiceImpl.andSignScreen(array);
                DBCursor dbCursor = null;
                if (role_code.equals(Common.ROLE_SYS)) {
                    dbCursor = cursor.find(queryCondition).sort(sort_obj);
                } else if(role_code.equals(Common.ROLE_CM)){
//                BasicDBList value = new BasicDBList();
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>"+manager_corp);
//                String[] split = manager_corp.split(",");
//                BasicDBList manager_corp_arr = new BasicDBList();
//                for (int i = 0; i < split.length; i++) {
//                    manager_corp_arr.add(split[i]);
//                }
//                if(manager_corp_arr.size()>0) {
//                    value.add(new BasicDBObject("corp_code", new BasicDBObject("$in", manager_corp_arr)));
//                }
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                dbCursor1 = cursor.find(queryCondition1);
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>" + manager_corp);
                    corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>"+corp_code);
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                }else if (role_code.equals(Common.ROLE_GM)) {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                    String store_code = "";
                    BasicDBList value = new BasicDBList();
                    for (int i = 0; i < stores.size(); i++) {
                        //       store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                        store_code = stores.get(i).getStore_code().toString();
                        value.add(store_code);
                    }
                    BasicDBObject ba = new BasicDBObject();
                    BasicDBList values = new BasicDBList();
                    if (value != null && value.size() > 0) {
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                        values.add(new BasicDBObject("corp_code", corp_code));
                    } else {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    values.add(queryCondition);
                    ba.put("$and", values);
                    dbCursor = cursor.find(ba).sort(sort_obj);


                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    //....
                    if (!area_code.equals("")) {
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        String[] areas = area_code.split(",");
                        String[] storeCodes = null;
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCodes = store_code.split(",");
                        }

                        BasicDBList value = new BasicDBList();
                        List<Store> store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, "");
                        //  String a = "";
                        for (int i = 0; i < store.size(); i++) {
                            //      a = a + store.get(i).getStore_code() + ",";
                            store_code = store.get(i).getStore_code().toString();
                            value.add(store_code);
                        }
                        BasicDBList values = new BasicDBList();
                        if (value != null && value.size() > 0) {
                            values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                            values.add(new BasicDBObject("corp_code", corp_code));
                        } else {
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("user_code", user_code));
                        }
                        values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                        values.add(queryCondition);
                        BasicDBObject queryCondition1 = new BasicDBObject();
                        queryCondition1.put("$and", values);
                        dbCursor = cursor.find(queryCondition1).sort(sort_obj);

                    }

                } else if (role_code.equals(Common.ROLE_SM)) {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    BasicDBList values = new BasicDBList();
                    String[] stores = null;
                    if (!store_code.equals("")) {
                        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                        stores = store_code.split(",");
                    }
                    BasicDBList value = new BasicDBList();

                    for (int i = 0; i < stores.length; i++) {

                        store_code = stores[i].toString();
                        value.add(store_code);
                    }

                    values.add(queryCondition);
                    if (value != null && value.size() > 0) {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                    } else {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", values);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);

                } else if (role_code.equals(Common.ROLE_STAFF)) {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(new BasicDBObject("user_code", user_code));
                    value.add(queryCondition);
                    value.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                }
                list = MongoHelperServiceImpl.dbCursorToList_status(dbCursor);
            }

            //导出......
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request, "签到记录");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /**
     * Mongodb签到管理
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String selectAllSignScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        int pages = 0;
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObject1 = JSONObject.parseObject(jsString);
            id = jsonObject1.getString("id");
            String message = jsonObject1.get("message").toString();
            JSONObject jsonObject2 = JSONObject.parseObject(message);
            int page_number = Integer.parseInt(jsonObject2.get("pageNumber").toString());
            int page_size = Integer.parseInt(jsonObject2.get("pageSize").toString());
            String lists = jsonObject2.get("list").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            JSONArray array = JSONArray.parseArray(lists);
            BasicDBObject queryCondition = MongoHelperServiceImpl.andSignScreen(array);
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);

            DBCursor dbCursor = null;
            DBCursor dbCursor1 = null;
            if (role_code.equals(Common.ROLE_SYS)) {

                dbCursor1 = cursor.find(queryCondition);

            } else if(role_code.equals(Common.ROLE_CM)){
//                BasicDBList value = new BasicDBList();
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>"+manager_corp);
//                String[] split = manager_corp.split(",");
//                BasicDBList manager_corp_arr = new BasicDBList();
//                for (int i = 0; i < split.length; i++) {
//                    manager_corp_arr.add(split[i]);
//                }
//                if(manager_corp_arr.size()>0) {
//                    value.add(new BasicDBObject("corp_code", new BasicDBObject("$in", manager_corp_arr)));
//                }
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                dbCursor1 = cursor.find(queryCondition1);
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor1 = cursor.find(queryCondition1);
            }else if (role_code.equals(Common.ROLE_GM)) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor1 = cursor.find(queryCondition1);
            } else if (role_code.equals(Common.ROLE_BM)) {
                //品牌管理员
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");

                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");
                String store_code = "";
                BasicDBList value = new BasicDBList();

                for (int i = 0; i < stores.size(); i++) {

                    store_code = stores.get(i).getStore_code().toString();
                    value.add(store_code);
                }
                BasicDBList values = new BasicDBList();
                if (value.size() > 0 && value != null) {
                    values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                    values.add(new BasicDBObject("corp_code", corp_code));
                } else {
                    values.add(new BasicDBObject("corp_code", corp_code));
                    values.add(new BasicDBObject("user_code", user_code));
                }
                values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));

                values.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", values);
                dbCursor1 = cursor.find(queryCondition1);

            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession(false).getAttribute("area_code").toString();
                String store_code = request.getSession(false).getAttribute("store_code").toString();

                if (!area_code.equals("")) {
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    String[] areas = area_code.split(",");
                    String[] storeCodes = null;
                    if (!store_code.equals("")) {
                        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                        storeCodes = store_code.split(",");
                    }
                    BasicDBList value = new BasicDBList();
                    List<Store> store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, "");

                    for (int i = 0; i < store.size(); i++) {
                        store_code = store.get(i).getStore_code().toString();
                        value.add(store_code);
                    }
                    BasicDBList values = new BasicDBList();
                    if (value != null && value.size() > 0) {
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                        values.add(new BasicDBObject("corp_code", corp_code));
                    } else {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    values.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", values);
                    dbCursor1 = cursor.find(queryCondition1);
                }
            } else if (role_code.equals(Common.ROLE_SM)) {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                //....
                String[] stores = null;
                if (!store_code.equals("")) {
                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    stores = store_code.split(",");
                }
                BasicDBList value = new BasicDBList();
                for (int i = 0; i < stores.length; i++) {

                    store_code = stores[i].toString();
                    value.add(store_code);
                }
                BasicDBList values = new BasicDBList();
                if (value != null && value.size() > 0) {
                    values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                    values.add(new BasicDBObject("corp_code", corp_code));
                } else {
                    values.add(new BasicDBObject("corp_code", corp_code));
                    values.add(new BasicDBObject("user_code", user_code));
                }
                values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                values.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", values);
                dbCursor1 = cursor.find(queryCondition1);

            } else if (role_code.equals(Common.ROLE_STAFF)) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("user_code", user_code));
                value.add(queryCondition);
                value.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor1 = cursor.find(queryCondition1);
                //员工过滤
            }
            int total = dbCursor1.count();
            pages = MongoUtils.getPages(dbCursor1, page_size);
            dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "modified_date", -1);
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_status(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            result.put("total", total);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


}
