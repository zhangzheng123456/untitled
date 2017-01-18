package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
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
import org.bson.types.ObjectId;
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
    MongoDBClient mongodbClient;
    @Autowired
    private BaseService baseService;
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
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_message_content);
            DBCursor dbCursor = null;

            if (screen.equals("")) {
                String[] column_names = new String[]{"vip_id", "vip_name", "message_date"};
                BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
                if (role_code.equals(Common.ROLE_SYS)) {
                    DBCursor dbCursor1 = cursor.find(queryCondition);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1, 1, Common.EXPORTEXECLCOUNT, "message_date", -1);

                } else {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2, 1, Common.EXPORTEXECLCOUNT, "message_date", -1);
                }
            } else {

                JSONArray array = JSONArray.parseArray(screen);
                BasicDBObject queryCondition = MongoHelperServiceImpl.andUserOperScreen(array);
                Map<String, String> map = WebUtils.Json2Map(jsonObject);

                // 读取数据
                if (role_code.equals(Common.ROLE_SYS)) {
                    DBCursor dbCursor1 = cursor.find(queryCondition);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1, 1, Common.EXPORTEXECLCOUNT, "message_date", -1);
                } else {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor1 = cursor.find(queryCondition1);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1, 1, Common.EXPORTEXECLCOUNT, "message_date", -1);
                }
            }
            JSONArray array = vipRecordService.transRecord(dbCursor);

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(array);
            if (array.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json, array, map, response, request);
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
//    @RequestMapping(value = "/callback/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String callBackManage(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        int pages = 0;
//        try {
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//
//            JSONObject result = new JSONObject();
//            JSONObject result1 = new JSONObject();
//
//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_message_content);
//
//            DBCursor dbCursor = null;
//            Map keyMap = new HashMap();
//            keyMap.put("message_type", "returnVisit");
//            BasicDBObject ref = new BasicDBObject();
//
//            // 读取数据
//            if (role_code.equals(Common.ROLE_SYS)) {
//                ref.putAll(keyMap);
//                DBCursor dbCursor1 = cursor.find(ref);
//                pages = MongoUtils.getPages(dbCursor1, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "message_date", -1);
//            } else {
//                keyMap.put("corp_code", corp_code);
//                ref.putAll(keyMap);
//                DBCursor dbCursor1 = cursor.find(ref);
//                pages = MongoUtils.getPages(dbCursor1, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "message_date", -1);
//            }
//            JSONArray array = vipRecordService.transRecord(dbCursor);
//
//            result.put("list", array);
//            result.put("pages", pages);
//            result.put("page_number", page_number);
//            result.put("page_size", page_size);
//
//            result1.put("list", result.toJSONString());
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result1.toString());
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

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

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_message_content);

            String[] column_names = new String[]{"vip_id", "vip_name", "user_name", "message_date"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);

            DBCursor dbCursor = null;
            BasicDBObject queryCondition1 = new BasicDBObject();

            BasicDBList value = new BasicDBList();
            value.add(new BasicDBObject("message_type", "returnVisit"));
            value.add(queryCondition);

            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "message_date", -1);

            } else {
                value.add(new BasicDBObject("corp_code", corp_code));
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "message_date", -1);
            }

            JSONArray array = vipRecordService.transRecord(dbCursor);
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
            ex.printStackTrace();
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

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            JSONObject result = new JSONObject();
            JSONObject result1 = new JSONObject();

            String lists = jsonObject.get("list").toString();

            JSONArray array = JSONArray.parseArray(lists);
            BasicDBObject queryCondition = MongoHelperServiceImpl.andUserOperScreen(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_message_content);

            DBCursor dbCursor = null;
            BasicDBObject queryCondition1 = new BasicDBObject();

            BasicDBList value = new BasicDBList();
            value.add(new BasicDBObject("message_type", "returnVisit"));
            value.add(queryCondition);
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "message_date", -1);
            } else {
                value.add(new BasicDBObject("corp_code", corp_code));
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "message_date", -1);
            }

            JSONArray array1 = vipRecordService.transRecord(dbCursor);
            result.put("list", array1);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);

            result1.put("list", result.toJSONString());

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result1.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
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

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_message_content);

            for (int i = 0; i < ids.length; i++) {
                DBObject deleteRecord = new BasicDBObject();
                deleteRecord.put("_id", new ObjectId(ids[i]));
                DBCursor dbObjects = cursor.find(deleteRecord);
                String vip_id="";
                String vip_name="";
                String user_name="";
                String date="";
                String corp_name="";
                while (dbObjects.hasNext()) {
                    DBObject vip = dbObjects.next();
                    if(vip.containsField("vip_id")){
                        vip_id =vip.get("vip_id").toString();
                    }
                    if(vip.containsField("vip_name")){
                        vip_name =vip.get("vip_name").toString();
                    }
                    if(vip.containsField("user_name")){
                        user_name =vip.get("user_name").toString();
                    }
                    if(vip.containsField("date")){
                        date =vip.get("date").toString();
                    }
                    if(vip.containsField("corp_name")){
                        corp_name =vip.get("corp_name").toString();
                    }
                }
                cursor.remove(deleteRecord);
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
                String function = "会员管理_回访记录";
                String action = Common.ACTION_DEL;
                String t_corp_code = corp_name;
                String t_code = vip_id;
                String t_name = vip_name;
                String remark = "回访员工("+user_name+")"+date;
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

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


 /**
  * 编辑前，获取数据
  *
  * @param request
  * @return
  */
    @RequestMapping(value = "/callback/select", method = RequestMethod.POST)
    @ResponseBody
    public String getfindById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String jsString = request.getParameter("param");

            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String errorLog_id = jsonObject.get("id").toString();
     //       System.out.println("----id-----"+errorLog_id);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_message_content);
            DBObject deleteRecord = new BasicDBObject();
            deleteRecord.put("_id",new ObjectId(errorLog_id));
            DBCursor dbObjects = cursor.find(deleteRecord);
            JSONArray array = vipRecordService.transRecord(dbObjects);
//            DBObject record=null;
//            while (dbObjects.hasNext()) {
//                record  = dbObjects.next();
//            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(array.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
        }
        return dataBean.getJsonStr();
    }
}
