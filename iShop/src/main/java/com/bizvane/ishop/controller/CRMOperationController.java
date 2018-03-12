package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

/**
 * Created by PC on 2016/11/17.
 */
@Controller
@RequestMapping("/CRMOperation")
public class CRMOperationController {
    private static final Logger logger = Logger.getLogger(CRMOperationController.class);
    @Autowired
    MongoDBClient mongodbClient;
    String id;

    /**
     *
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_log_user_operation);

            String[] column_names = new String[]{"function","action","code","name","operation_user_code","operation_user_name"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);

            BasicDBObject queryCondition1 = new BasicDBObject();
            BasicDBList value = new BasicDBList();
            Pattern pattern = Pattern.compile("^.*" + "会员档案" + ".*$", Pattern.CASE_INSENSITIVE);
            value.add(new BasicDBObject("function", pattern));
            value.add(queryCondition);
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                queryCondition1.put("$and", value);
            } else {
                value.add(new BasicDBObject("corp_code", corp_code));
                queryCondition1.put("$and", value);
            }
            DBCursor dbCursor2 = cursor.find(queryCondition1);
            pages = MongoUtils.getPages(dbCursor2,page_size);
            DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"operation_time",-1);

            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            result.put("total", dbCursor2.count());

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

    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        DataBean dataBean = new DataBean();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String lists = jsonObject.get("list").toString();

            JSONArray array = JSONArray.parseArray(lists);
            BasicDBObject queryCondition = MongoHelperServiceImpl.andUserOperScreen(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_log_user_operation);

            BasicDBList value = new BasicDBList();
            Pattern pattern = Pattern.compile("^.*" + "会员档案" + ".*$", Pattern.CASE_INSENSITIVE);
            value.add(new BasicDBObject("function", pattern));
            value.add(queryCondition);
            BasicDBObject queryCondition1 = new BasicDBObject();
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {

                queryCondition1.put("$and", value);
            } else {
                value.add(new BasicDBObject("corp_code", corp_code));
                queryCondition1.put("$and", value);
            }
            DBCursor dbCursor1 = cursor.find(queryCondition1);

            pages = MongoUtils.getPages(dbCursor1,page_size);
            DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"operation_time",-1);

            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            result.put("total", dbCursor1.count());

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

    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            ArrayList list = new ArrayList();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_log_user_operation);
            DBObject sort_obj = new BasicDBObject("operation_time", -1);

            DBCursor dbCursor = null;
            BasicDBObject queryCondition = new BasicDBObject();
            if (screen.equals("")) {
                String[] column_names = new String[]{"function","action","corp_name","code","name","operation_user_code","remark","operation_time"};
                queryCondition = MongoUtils.orOperation(column_names,search_value);
            } else {
                JSONArray array = JSONArray.parseArray(screen);
                queryCondition = MongoHelperServiceImpl.andUserOperScreen(array);
            }
            BasicDBList value = new BasicDBList();
            Pattern pattern = Pattern.compile("^.*" + "会员档案" + ".*$", Pattern.CASE_INSENSITIVE);
            value.add(new BasicDBObject("function", pattern));
            value.add(queryCondition);
            BasicDBObject queryCondition1 = new BasicDBObject();
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                queryCondition1.put("$and", value);
            } else {
                value.add(new BasicDBObject("corp_code", corp_code));
                queryCondition1.put("$and", value);
            }
            dbCursor = cursor.find(queryCondition1).sort(sort_obj);

            list = MongoUtils.dbCursorToList_id(dbCursor);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request,"CRM操作日志");
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

    /**
     *
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String callbackDelete(HttpServletRequest request) {
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
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_log_user_operation);

            for (int i = 0; i < ids.length; i++) {
                DBObject deleteRecord = new BasicDBObject();
                deleteRecord.put("_id", new ObjectId(ids[i]));
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
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
