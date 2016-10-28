package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/VIP")
public class VIPRecordController {
    @Autowired
    VipRecordService vipRecordService;
    @Autowired
    CorpService corpService;
    @Autowired
    UserService userService;
    @Autowired
    MongoDBClient mongodbClient;
    private static final Logger log = Logger.getLogger(VIPRecordController.class);

    String id;

    /***
     * 导出数据
     */
    @RequestMapping(value = "/callback/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExeclCallback(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<VipRecord> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.vipRecordService.selectBySearch(1, 30000, "", search_value);
                } else {

                    list = vipRecordService.selectBySearch(1, 30000, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.contains(Common.ROLE_SYS)) {
                    list = vipRecordService.selectAllVipRecordScreen(1, 30000, "", map);
                } else {
                    list = vipRecordService.selectAllVipRecordScreen(1, 30000, corp_code, map);
                }
            }
            List<VipRecord> vipRecords = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(vipRecords);
            if (vipRecords.size() >= 29999) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String,String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json,vipRecords, map, response, request);
            org.json.JSONObject result = new org.json.JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 回访记录管理
     * 列表
     */
    @RequestMapping(value = "/callback/list", method = RequestMethod.GET)
    @ResponseBody
    public String callBackManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));

            JSONObject result = new JSONObject();
            JSONObject result1 = new JSONObject();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_back_record);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find();

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);
            } else {
                Map keyMap = new HashMap();
                keyMap.put("corp_code", corp_code);
                BasicDBObject ref = new BasicDBObject();
                ref.putAll(keyMap);
                DBCursor dbCursor1 = cursor.find(ref);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);
            }
            JSONArray array = new JSONArray();
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                JSONObject object = new JSONObject();
                String corp_code1 = obj.get("corp_code").toString();
                String user_code = obj.get("user_id").toString();
                String vip_id = obj.get("vip_id").toString();
                String vip_name = obj.get("vip_name").toString();
                String created_date = obj.get("created_date").toString();
                String action = obj.get("action").toString();

                object.put("corp_code",corp_code1);
                object.put("user_code",user_code);
                object.put("vip_id",vip_id);
                object.put("vip_name",vip_name);
                object.put("created_date",created_date);
                object.put("action",action);
                if (action.equals("1")){
                    object.put("type_name","电话");
                }else if (action.equals("2")){
                    object.put("type_name","短信");
                }else if (action.equals("3")){
                    object.put("type_name","微信");
                }
                Corp corp = corpService.selectByCorpId(0,corp_code1,Common.IS_ACTIVE_Y);
                String corp_name = "";
                if (corp != null) {
                    corp_name = corp.getCorp_name();
                }
                object.put("corp_name",corp_name);

                List<User> users = userService.userCodeExist(corp_code,user_code,Common.IS_ACTIVE_Y);
                String user_name = "";
                if (users.size()>0){
                    user_name = users.get(0).getUser_name();
                }
                object.put("user_name",user_name);
                array.add(object);
            }
            result.put("list", array);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);

            result1.put("list", result.toJSONString());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result1.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 回访记录管理
     * 编辑前获取数据
     */

    @RequestMapping(value = "/callback/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectCallBack(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String user_id = request.getSession(false).getAttribute("user_id").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            //  int VipRecord_id = Integer.parseInt(jsonObject.getString("id"));
            int VipRecord_id = jsonObject.getInt("id");
            VipRecord VipRecord = this.vipRecordService.getVipRecord(VipRecord_id);
            if (VipRecord != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(JSON.toJSONString(VipRecord));
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("not found VipRecord error");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 回访记录管理
     * 查找
     */
    @RequestMapping(value = "/callback/find", method = RequestMethod.POST)
    @ResponseBody
    public String findCallBack(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        int pages = 0;
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonobj = new org.json.JSONObject(jsString);
            String message = jsonobj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            JSONObject result = new JSONObject();
            JSONObject result1 = new JSONObject();

//            PageInfo<VipRecord> list = null;
//            if (role_code.contains(Common.ROLE_SYS)) {
//                list = vipRecordService.selectBySearch(page_number, page_size, "", search_value);
//            } else {
//                list = vipRecordService.selectBySearch(page_number, page_size, corp_code, search_value);
//            }

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_back_record);

            String[] column_names = new String[]{"vip_id","vip_name","created_date"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);

            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor2,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"time",-1);
            }

            JSONArray array = new JSONArray();
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                JSONObject object = new JSONObject();
                String corp_code1 = obj.get("corp_code").toString();
                String user_code = obj.get("user_id").toString();
                String vip_id = obj.get("vip_id").toString();
                String vip_name = obj.get("vip_name").toString();
                String created_date = obj.get("created_date").toString();
                String action = obj.get("action").toString();

                object.put("corp_code",corp_code1);
                object.put("user_code",user_code);
                object.put("vip_id",vip_id);
                object.put("vip_name",vip_name);
                object.put("created_date",created_date);
                object.put("action",action);
                if (action.equals("1")){
                    object.put("type_name","电话");
                }else if (action.equals("2")){
                    object.put("type_name","短信");
                }else if (action.equals("3")){
                    object.put("type_name","微信");
                }
                Corp corp = corpService.selectByCorpId(0,corp_code1,Common.IS_ACTIVE_Y);
                String corp_name = "";
                if (corp != null) {
                    corp_name = corp.getCorp_name();
                }
                object.put("corp_name",corp_name);

                List<User> users = userService.userCodeExist(corp_code,user_code,Common.IS_ACTIVE_Y);
                String user_name = "";
                if (users.size()>0){
                    user_name = users.get(0).getUser_name();
                }
                object.put("user_name",user_name);
                array.add(object);
            }
            result.put("list", array);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);

            result1.put("list", result.toJSONString());

//            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result1.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 回访记录管理
     * 筛选
     */
    @RequestMapping(value = "/callback/screen", method = RequestMethod.POST)
    @ResponseBody
    public String findCallBackScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        int pages = 0;
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonobj = new org.json.JSONObject(jsString);
            String message = jsonobj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
//            String screen = jsonObject.get("screen").toString();
//            org.json.JSONObject jsonScreen = new org.json.JSONObject(screen);
//            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            JSONObject result = new JSONObject();
            JSONObject result1 = new JSONObject();

//            PageInfo<VipRecord> list = null;
//            if (role_code.contains(Common.ROLE_SYS)) {
//                list = vipRecordService.selectAllVipRecordScreen(page_number, page_size, "", map);
//            } else {
//                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//                list = vipRecordService.selectAllVipRecordScreen(page_number, page_size, corp_code, map);
//            }

            String lists = jsonObject.get("list").toString();

            JSONArray array = JSONArray.parseArray(lists);
            BasicDBObject queryCondition = MongoUtils.andOperation(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_back_record);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);
            }
            JSONArray array1 = new JSONArray();
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                JSONObject object = new JSONObject();
                String corp_code1 = obj.get("corp_code").toString();
                String user_code = obj.get("user_id").toString();
                String vip_id = obj.get("vip_id").toString();
                String vip_name = obj.get("vip_name").toString();
                String created_date = obj.get("created_date").toString();
                String action = obj.get("action").toString();

                object.put("corp_code",corp_code1);
                object.put("user_code",user_code);
                object.put("vip_id",vip_id);
                object.put("vip_name",vip_name);
                object.put("created_date",created_date);
                object.put("action",action);
                if (action.equals("1")){
                    object.put("type_name","电话");
                }else if (action.equals("2")){
                    object.put("type_name","短信");
                }else if (action.equals("3")){
                    object.put("type_name","微信");
                }
                Corp corp = corpService.selectByCorpId(0,corp_code1,Common.IS_ACTIVE_Y);
                String corp_name = "";
                if (corp != null) {
                    corp_name = corp.getCorp_name();
                }
                object.put("corp_name",corp_name);

                List<User> users = userService.userCodeExist(corp_code,user_code,Common.IS_ACTIVE_Y);
                String user_name = "";
                if (users.size()>0){
                    user_name = users.get(0).getUser_name();
                }
                object.put("user_name",user_name);
                array1.add(object);
            }
            result.put("list", array1);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);

            result1.put("list", result.toJSONString());

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result1.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 回访记录管理
     * 删除
     */
    @RequestMapping(value = "/callback/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String callbackDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String[] ids = jsonObject.get("id").toString().split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                this.vipRecordService.delete(Integer.parseInt(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
