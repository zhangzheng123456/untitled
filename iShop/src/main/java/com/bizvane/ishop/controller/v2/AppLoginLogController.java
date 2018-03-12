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
 * Created by yin on 2016/8/24.
 */
@Controller
@RequestMapping("/apploginlog")
public class AppLoginLogController {
    @Autowired
    private UserService userService;
    @Autowired
    private BaseService baseService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    StoreService storeService;
    String id;


    /**
     * 页面查找
     */
    @RequestMapping(value = "/view", method = RequestMethod.POST)
    @ResponseBody
    public String getView(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_login_log);
        try {
            JSONObject loginlog_object = new JSONObject();
            JSONArray loginlog_jsonArray = new JSONArray();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String param = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
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

                if ("W".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    String[] dates = TimeUtils.getWeek2Day(date_value).split(",");
                    for (int i = 0; i < dates.length; i++) {
                        BasicDBObject ref = new BasicDBObject();
                        BasicDBList values = new BasicDBList();
                        if (value != null && value.size() > 0) {
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("store_id", new BasicDBObject("$in", value)));
                        } else {
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("user_id", user_code));
                        }
                        Pattern pattern = Pattern.compile("^.*" + dates[i] + ".*$", Pattern.CASE_INSENSITIVE);
                        values.add(new BasicDBObject("created_date", pattern));

                        ref.put("$and", values);
                        DBCursor dbCursor1 = cursor.find(ref);

                        int login_y = dbCursor1.count();
                        int login_n = userCountByStore - login_y;
                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates[i]);
                        json_data.put("login_y", login_y);
                        json_data.put("login_n", login_n);
                        loginlog_jsonArray.add(json_data);
                    }
                } else if ("M".equals(date_type)) {

                    String date_value = jsonObject.get("date_value").toString();

                    List<String> dates = TimeUtils.getMonthAllDays(date_value);
                    for (int i = 0; i < dates.size(); i++) {
                        BasicDBObject ref = new BasicDBObject();
                        BasicDBList values = new BasicDBList();
                        if (value != null && value.size() > 0) {
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("store_id", new BasicDBObject("$in", value)));
                        } else {
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("user_id", user_code));
                        }
                        Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                        values.add(new BasicDBObject("created_date", pattern));
                        ref.put("$and", values);
                        DBCursor dbCursor1 = cursor.find(ref);

                        int login_y = dbCursor1.count();
                        int login_n = userCountByStore - login_y;
                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates.get(i).substring(5, date_value.length()));
                        json_data.put("login_y", login_y);
                        json_data.put("login_n", login_n);
                        loginlog_jsonArray.add(json_data);
                    }
                } else if ("Y".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    List<String> dates = TimeUtils.getYearAllDays(date_value);
                    for (int i = 0; i < dates.size(); i++) {
                        BasicDBObject ref = new BasicDBObject();
                        BasicDBList values = new BasicDBList();
                        if (value != null && value.size() > 0) {
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("store_id", new BasicDBObject("$in", value)));
                        } else {
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("user_id", user_code));
                        }
                        Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                        values.add(new BasicDBObject("created_date", pattern));
                        ref.put("$and", values);
                        DBCursor dbCursor1 = cursor.find(ref);

                        int login_y = dbCursor1.count();
                        int login_n = userCountByStore - login_y;
                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates.get(i));
                        json_data.put("login_y", login_y);
                        json_data.put("login_n", login_n);
                        loginlog_jsonArray.add(json_data);
                    }

                }
                loginlog_object.put("view_type", "histogram");
            } else {
                String user_code_json = jsonObject.get("user_code").toString();

                if (date_type.equals("W")) {
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(new BasicDBObject("user_code", user_code_json));
                    String date_value = jsonObject.get("date_value").toString();

                    String[] dates = TimeUtils.getWeek2Day(date_value).split(",");
                    for (int i = 0; i < dates.length; i++) {
                        Pattern pattern = Pattern.compile("^.*" + dates[i] + ".*$", Pattern.CASE_INSENSITIVE);
                        value.add(new BasicDBObject("created_date", pattern));
                        queryCondition1.put("$and", value);
                        DBCursor dbCursor2 = cursor.find(queryCondition1);
                        int login_count = 0;
                        while (dbCursor2.hasNext()) {
                            DBObject loginlog = dbCursor2.next();
                            if (loginlog.containsField("count")) {
                                login_count += Integer.parseInt(loginlog.get("count").toString());
                            }
                        }
                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates[i]);
                        json_data.put("login_count", login_count);
                        loginlog_jsonArray.add(json_data);
                    }
                } else if ("M".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    List<String> dates = TimeUtils.getMonthAllDays(date_value);
                    for (int i = 0; i < dates.size(); i++) {
                        BasicDBObject queryCondition1 = new BasicDBObject();
                        BasicDBList value = new BasicDBList();
                        value.add(new BasicDBObject("corp_code", corp_code));
                        value.add(new BasicDBObject("user_code", user_code_json));
                        Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                        value.add(new BasicDBObject("created_date", pattern));
                        queryCondition1.put("$and", value);
                        DBCursor dbCursor2 = cursor.find(queryCondition1);
                        int login_count = 0;
                        while (dbCursor2.hasNext()) {
                            DBObject loginlog = dbCursor2.next();
                            if (loginlog.containsField("count")) {
                                login_count += Integer.parseInt(loginlog.get("count").toString());
                            }
                        }

                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates.get(i).substring(5, date_value.length()));
                        json_data.put("login_count", login_count);
                        loginlog_jsonArray.add(json_data);
                    }
                } else if ("Y".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    List<String> dates = TimeUtils.getYearAllDays(date_value);
                    for (int i = 0; i < dates.size(); i++) {
                        BasicDBObject queryCondition1 = new BasicDBObject();
                        BasicDBList value = new BasicDBList();
                        value.add(new BasicDBObject("corp_code", corp_code));
                        value.add(new BasicDBObject("user_code", user_code_json));
                        Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                        value.add(new BasicDBObject("created_date", pattern));
                        queryCondition1.put("$and", value);
                        DBCursor dbCursor2 = cursor.find(queryCondition1);
                        int login_count = 0;
                        while (dbCursor2.hasNext()) {
                            DBObject loginlog = dbCursor2.next();
                            if (loginlog.containsField("count")) {
                                login_count += Integer.parseInt(loginlog.get("count").toString());
                            }
                        }

                        com.alibaba.fastjson.JSONObject json_data = new com.alibaba.fastjson.JSONObject();
                        json_data.put("date", dates.get(i));
                        json_data.put("login_count", login_count);
                        loginlog_jsonArray.add(json_data);
                    }
                }
                loginlog_object.put("view_type", "line");
            }
            loginlog_object.put("login_logs", loginlog_jsonArray);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(loginlog_object.toJSONString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 页面查找
     */
    @RequestMapping(value = "/newList", method = RequestMethod.POST)
    @ResponseBody
    public String getNewList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_login_log);
        int pages = 0;
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        try {
            JSONArray loginlog_jsonArray = new JSONArray();
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

                    values.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.GTE, dates[0])));

                    values.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.LTE, dates[dates.length - 1])));

                    ref.put("$and", values);
                    DBCursor dbCursor1 = cursor.find(ref);

                    pages = MongoUtils.getPages(dbCursor1, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
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
                    values.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.GTE, dates.get(0))));

                    values.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1))));
                    ref.put("$and", values);
                    DBCursor dbCursor1 = cursor.find(ref);

                    pages = MongoUtils.getPages(dbCursor1, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
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
                    values.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.GTE, dates.get(0) + "-01")));

                    values.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) + "-31")));
                    ref.put("$and", values);
                    DBCursor dbCursor1 = cursor.find(ref);

                    pages = MongoUtils.getPages(dbCursor1, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
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

                    value.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.GTE, dates[0])));

                    value.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.LTE, dates[dates.length - 1])));
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);
                    pages = MongoUtils.getPages(dbCursor2, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                    canloginByCode = userService.getCanloginByCode(corp_code);

                } else if ("M".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    List<String> dates = TimeUtils.getMonthAllDays(date_value);

                    value.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.GTE, dates.get(0))));

                    value.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1))));
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);
                    pages = MongoUtils.getPages(dbCursor2, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                    canloginByCode = userService.getCanloginByCode(corp_code);

                } else if ("Y".equals(date_type)) {
                    String date_value = jsonObject.get("date_value").toString();
                    List<String> dates = TimeUtils.getYearAllDays(date_value);

                    value.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.GTE, dates.get(0) + "-01")));

                    value.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) + "-31")));
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);
                    pages = MongoUtils.getPages(dbCursor2, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
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
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String getSearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String user_code = request.getSession(false).getAttribute("user_code").toString();
            String brand_code = request.getSession(false).getAttribute("brand_code").toString();
            String area_code = request.getSession(false).getAttribute("area_code").toString();
            String store_code = request.getSession(false).getAttribute("store_code").toString();

            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());

            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_login_log);
//            MongoOptions mongoOptions = cursor.getDB().getMongo().getMongoOptions();
//            System.out.println("========SocketTimeOut============="+mongoOptions.getSocketTimeout());
            String[] column_names = new String[]{"user_name", "app_platform", "corp_name"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
            List<User> canloginByCode = null;
            DBCursor dbCursor = null;
            int total = 0;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode("");
                total = dbCursor1.count();
            } else if (role_code.equals(Common.ROLE_CM)) {

//                BasicDBList value = new BasicDBList();
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>" + manager_corp);
//                String[] split = manager_corp.split(",");
//                BasicDBList manager_corp_arr = new BasicDBList();
//                for (int i = 0; i < split.length; i++) {
//                    manager_corp_arr.add(split[i]);
//                }
//                if (manager_corp_arr.size() > 0) {
//                    value.add(new BasicDBObject("corp_code", new BasicDBObject("$in", manager_corp_arr)));
//                }
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                DBCursor dbCursor1 = cursor.find(queryCondition1);
//                pages = MongoUtils.getPages(dbCursor1, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
//                canloginByCode = userService.getCanloginByCode("", manager_corp);
//                total = dbCursor1.count();
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                total = dbCursor2.count();
                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode(corp_code);
            }
            //-------------------------------------
            else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                if (role_code.equals(Common.ROLE_GM)) {
                    queryCondition1.put("$and", value);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                    BasicDBList store = new BasicDBList();
                    for (int i = 0; i < storeList.size(); i++) {
                        String store_code1 = storeList.get(i).getStore_code().toString();
                        store.add(store_code1);
                    }
                    if (store.size() > 0) {
                        value.add(new BasicDBObject("store_id", new BasicDBObject("$in", store)));
                    } else {
                        value.add(new BasicDBObject("user_id", user_code));
                    }
                    queryCondition1.put("$and", value);
                } else if (role_code.equals(Common.ROLE_AM)) {

                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, "", "", store_code);
                    BasicDBList store = new BasicDBList();
                    for (int i = 0; i < storeList.size(); i++) {
                        String store_code1 = storeList.get(i).getStore_code().toString();
                        store.add(store_code1);
                    }
                    if (store.size() > 0) {
                        value.add(new BasicDBObject("store_id", new BasicDBObject("$in", store)));
                    } else {
                        value.add(new BasicDBObject("user_id", user_code));
                    }
                    queryCondition1.put("$and", value);

                } else if (role_code.equals(Common.ROLE_SM)) {
                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    String[] stores = store_code.split(",");

                    BasicDBList store = new BasicDBList();
                    for (int i = 0; i < stores.length; i++) {
                        store.add(stores[i]);
                    }
                    if (store.size() > 0) {
                        value.add(new BasicDBObject("store_id", new BasicDBObject("$in", store)));
                    } else {
                        value.add(new BasicDBObject("user_id", user_code));
                    }
                    queryCondition1.put("$and", value);
                } else {
                    value.add(new BasicDBObject("user_code", user_code));
                    queryCondition1.put("$and", value);
                }
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                total = dbCursor2.count();
                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode(corp_code);
            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_canLogin(dbCursor, canloginByCode, "");
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
            //  logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String getScreen(HttpServletRequest request) {
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        DataBean dataBean = new DataBean();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String user_code = request.getSession(false).getAttribute("user_code").toString();
            String brand_code = request.getSession(false).getAttribute("brand_code").toString();
            String area_code = request.getSession(false).getAttribute("area_code").toString();
            String store_code = request.getSession(false).getAttribute("store_code").toString();
            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String lists = jsonObject.get("list").toString();

            JSONArray array = JSONArray.parseArray(lists);
            String user_can_login = "";
            for (int i = 0; i < array.size(); i++) {
                String info = array.get(i).toString();
                com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(info);
                String screen_key = json.get("screen_key").toString();
                String screen_value = json.get("screen_value").toString();
                if (screen_key.equals("user_can_login")) {
                    user_can_login = screen_value;
                }
            }
            BasicDBObject queryCondition = MongoHelperServiceImpl.andLoginlogScreen(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_login_log);
            List<User> canloginByCode = null;
            DBCursor dbCursor = null;
            int total = 0;
            // 读取数据

            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                total = dbCursor1.count();

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode("");
            } else if (role_code.equals(Common.ROLE_CM)) {
//                BasicDBList value = new BasicDBList();
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>" + manager_corp);
//                String[] split = manager_corp.split(",");
//                BasicDBList manager_corp_arr = new BasicDBList();
//                for (int i = 0; i < split.length; i++) {
//                    manager_corp_arr.add(split[i]);
//                }
//                if (manager_corp_arr.size() > 0) {
//                    value.add(new BasicDBObject("corp_code", new BasicDBObject("$in", manager_corp_arr)));
//                }
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                DBCursor dbCursor1 = cursor.find(queryCondition1);
//                pages = MongoUtils.getPages(dbCursor1, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
//                canloginByCode = userService.getCanloginByCode("", manager_corp);
//                total = dbCursor1.count();
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                total = dbCursor1.count();

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode(corp_code);
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();

                if (role_code.equals(Common.ROLE_GM)) {
                    queryCondition1.put("$and", value);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                    BasicDBList store = new BasicDBList();
                    for (int i = 0; i < storeList.size(); i++) {
                        String store_code1 = storeList.get(i).getStore_code().toString();
                        store.add(store_code1);
                    }
                    if (store.size() > 0) {
                        value.add(new BasicDBObject("store_id", new BasicDBObject("$in", store)));
                    } else {
                        value.add(new BasicDBObject("user_id", user_code));
                    }
                    queryCondition1.put("$and", value);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, "", "", store_code);
                    BasicDBList store = new BasicDBList();
                    for (int i = 0; i < storeList.size(); i++) {
                        String store_code1 = storeList.get(i).getStore_code().toString();
                        store.add(store_code1);
                    }
                    if (store.size() > 0) {
                        value.add(new BasicDBObject("store_id", new BasicDBObject("$in", store)));
                    } else {
                        value.add(new BasicDBObject("user_id", user_code));
                    }
                    queryCondition1.put("$and", value);

                } else if (role_code.equals(Common.ROLE_SM)) {
                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    String[] stores = store_code.split(",");

                    BasicDBList store = new BasicDBList();
                    for (int i = 0; i < stores.length; i++) {
                        store.add(stores[i]);
                    }
                    if (store.size() > 0) {
                        value.add(new BasicDBObject("store_id", new BasicDBObject("$in", store)));
                    } else {
                        value.add(new BasicDBObject("user_id", user_code));
                    }
                    queryCondition1.put("$and", value);
                } else {
                    value.add(new BasicDBObject("user_code", user_code));
                    queryCondition1.put("$and", value);
                }
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                total = dbCursor1.count();

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode(corp_code);
            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_canLogin(dbCursor, canloginByCode, user_can_login);
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

        }
        return dataBean.getJsonStr();
    }


    /**
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String getDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String[] ids = jsonObject.get("id").toString().split(",");

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_login_log);

            for (int i = 0; i < ids.length; i++) {
                DBObject deleteRecord = new BasicDBObject();
                deleteRecord.put("_id", new ObjectId(ids[i]));

                DBCursor dbObjects = cursor.find(deleteRecord);

                String corp_code = "";
                String user_id = "";
                String user_name = "";
                String created_date = "";
                String app_platform = "";
                while (dbObjects.hasNext()) {
                    DBObject loginLog = dbObjects.next();
                    if (loginLog.containsField("corp_code")) {
                        corp_code = loginLog.get("corp_code").toString();
                    }
                    if (loginLog.containsField("user_id")) {
                        user_id = loginLog.get("user_id").toString();
                    }
                    if (loginLog.containsField("user_name")) {
                        user_name = loginLog.get("user_name").toString();
                    }
                    if (loginLog.containsField("created_date")) {
                        created_date = loginLog.get("created_date").toString();
                    }
                    if (loginLog.containsField("app_platform")) {
                        app_platform = loginLog.get("app_platform").toString();
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
                String function = "员工管理_登录日志";
                String action = Common.ACTION_DEL;
                String t_corp_code = corp_code;
                String t_code = user_id;
                String t_name = user_name;
                String remark = created_date + "(" + app_platform + ")";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);

                cursor.remove(deleteRecord);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            // logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String getexportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession(false).getAttribute("role_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();
        String user_code = request.getSession(false).getAttribute("user_code").toString();
        String brand_code = request.getSession(false).getAttribute("brand_code").toString();
        String area_code = request.getSession(false).getAttribute("area_code").toString();
        String store_code = request.getSession(false).getAttribute("store_code").toString();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            ArrayList list = new ArrayList();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_login_log);
            DBObject sort_obj = new BasicDBObject("created_date", -1);
            DBCursor dbCursor = null;
            DBCursor dbCursor1 = null;
            List<User> canloginByCode = null;
            BasicDBObject queryCondition = new BasicDBObject();
            if (screen.equals("")) {
                String[] column_names = new String[]{"user_name", "app_platform", "corp_name"};
                queryCondition = MongoUtils.orOperation(column_names, search_value);
            } else {
                JSONArray array = JSONArray.parseArray(screen);
                String user_can_login = "";
                for (int i = 0; i < array.size(); i++) {
                    String info = array.get(i).toString();
                    com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(info);
                    String screen_key = json.get("screen_key").toString();
                    String screen_value = json.get("screen_value").toString();
                    if (screen_key.equals("user_can_login")) {
                        user_can_login = screen_value;
                    }
                }
                queryCondition = MongoHelperServiceImpl.andLoginlogScreen(array);
            }
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                dbCursor = cursor.find(queryCondition);
                canloginByCode = userService.getCanloginByCode("");
            } else if (role_code.equals(Common.ROLE_CM)) {

//                BasicDBList value = new BasicDBList();
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>" + manager_corp);
//                String[] split = manager_corp.split(",");
//                BasicDBList manager_corp_arr = new BasicDBList();
//                for (int i = 0; i < split.length; i++) {
//                    manager_corp_arr.add(split[i]);
//                }
//                if (manager_corp_arr.size() > 0) {
//                    value.add(new BasicDBObject("corp_code", new BasicDBObject("$in", manager_corp_arr)));
//                }
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                DBCursor dbCursor1 = cursor.find(queryCondition1);
//                pages = MongoUtils.getPages(dbCursor1, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
//                canloginByCode = userService.getCanloginByCode("", manager_corp);
//                total = dbCursor1.count();
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor = cursor.find(queryCondition1);
                canloginByCode = userService.getCanloginByCode(corp_code);
            }else {

                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();

                if (role_code.equals(Common.ROLE_GM)) {
                    queryCondition1.put("$and", value);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                    BasicDBList store = new BasicDBList();
                    for (int i = 0; i < storeList.size(); i++) {
                        String store_code1 = storeList.get(i).getStore_code().toString();
                        store.add(store_code1);
                    }
                    if (store.size() > 0) {
                        value.add(new BasicDBObject("store_id", new BasicDBObject("$in", store)));
                    } else {
                        value.add(new BasicDBObject("user_id", user_code));
                    }
                    queryCondition1.put("$and", value);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, "", "", store_code);
                    BasicDBList store = new BasicDBList();
                    for (int i = 0; i < storeList.size(); i++) {
                        String store_code1 = storeList.get(i).getStore_code().toString();
                        store.add(store_code1);
                    }
                    if (store.size() > 0) {
                        value.add(new BasicDBObject("store_id", new BasicDBObject("$in", store)));
                    } else {
                        value.add(new BasicDBObject("user_id", user_code));
                    }
                    queryCondition1.put("$and", value);

                } else if (role_code.equals(Common.ROLE_SM)) {
                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    String[] stores = store_code.split(",");

                    BasicDBList store = new BasicDBList();
                    for (int i = 0; i < stores.length; i++) {
                        store.add(stores[i]);
                    }
                    if (store.size() > 0) {
                        value.add(new BasicDBObject("store_id", new BasicDBObject("$in", store)));
                    } else {
                        value.add(new BasicDBObject("user_id", user_code));
                    }
                    queryCondition1.put("$and", value);
                } else {
                    value.add(new BasicDBObject("user_code", user_code));
                    queryCondition1.put("$and", value);
                }
                dbCursor = cursor.find(queryCondition1);

                canloginByCode = userService.getCanloginByCode(corp_code);
            }

            if(jsonObject.containsKey("pageNumber") && jsonObject.containsKey("pageSize")) {
                int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
                int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
                dbCursor1 = MongoUtils.sortAndPage(dbCursor, page_number, page_size, "created_date", -1);
            }else{
                dbCursor1 = dbCursor.sort(sort_obj);
            }
            list = MongoHelperServiceImpl.dbCursorToList_canLogin(dbCursor1, canloginByCode, "");

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= 50000) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            if (corp_code.equals("C10016"))
                map.put("flg_tob", "经销商");
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request, "爱秀登录日志");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }
}
