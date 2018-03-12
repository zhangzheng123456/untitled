package com.bizvane.ishop.controller;


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
 * Created by PC on 2017/7/17.
 */
@Controller
@RequestMapping("/smslog")
public class SmsLogController {

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
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sms_log);

            String[] column_names = new String[]{"user_id", "user_name", "store_name","corp_name","vip.name_vip"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
            DBCursor dbCursor = null;
            int total = 0;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("status", "Y"));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                total = dbCursor1.count();
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("status", "Y"));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                total = dbCursor2.count();
                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("status", "Y"));
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
            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_SmsLogApp(dbCursor);
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
            BasicDBObject queryCondition = MongoHelperServiceImpl.andLoginlogScreen(array);
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sms_log);
            DBCursor dbCursor = null;
            int total = 0;
            // 读取数据

            if (role_code.equals(Common.ROLE_SYS)) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("status", "Y"));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                total = dbCursor1.count();

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("status", "Y"));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                total = dbCursor1.count();

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("status", "Y"));
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
            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_SmsLogApp(dbCursor);
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
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sms_log);

            for (int i = 0; i < ids.length; i++) {
                DBObject deleteRecord = new BasicDBObject();
                deleteRecord.put("_id", new ObjectId(ids[i]));

                DBCursor dbObjects = cursor.find(deleteRecord);

                String corp_code = "";
                String user_id = "";
                String user_name = "";
                String phone = "";
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

                    if (loginLog.containsField("phone")) {
                        phone = loginLog.get("phone").toString();
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
                String function = "短信日志";
                String action = Common.ACTION_DEL;
                String t_corp_code = corp_code;
                String t_code = user_id;
                String t_name = user_name;
                String remark = phone;
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
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sms_log);
            DBObject sort_obj = new BasicDBObject("created_date", -1);
            DBCursor dbCursor = null;
            BasicDBObject queryCondition = new BasicDBObject();
            if (screen.equals("")) {
                String[] column_names = new String[]{"user_id", "user_name", "store_name","corp_name"};
                queryCondition = MongoUtils.orOperation(column_names, search_value);
            } else {
                JSONArray array = JSONArray.parseArray(screen);

                queryCondition = MongoHelperServiceImpl.andLoginlogScreen(array);
            }
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("status", "Y"));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor = cursor.find(queryCondition1).sort(sort_obj);
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("status", "Y"));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor = cursor.find(queryCondition1).sort(sort_obj);
            }else {

                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("status", "Y"));
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
                dbCursor = cursor.find(queryCondition1).sort(sort_obj);
            }
            list = MongoHelperServiceImpl.dbCursorToList_SmsLogApp(dbCursor);

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= 50000) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request, "短信日志");
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
