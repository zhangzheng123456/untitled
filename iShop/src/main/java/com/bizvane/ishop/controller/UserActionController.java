package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
import org.bson.*;
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
import java.lang.System;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouzhou on 2016/9/5.
 *
 * @@version
 */
@Controller
@RequestMapping("/userAction")
public class UserActionController {
    private static final Logger logger = Logger.getLogger(LoginController.class);

    @Autowired
    MongoDBClient mongodbClient;
    String id;

    /**
     * 用户行为日志
     * 列表展示
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String userActionList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        int pages=0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            System.out.println("======vipActionLogger===== ");

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection("log_person_action");

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                int count = Integer.parseInt(String.valueOf(cursor.count()));
                if (count%page_size == 0){
                    pages = count/page_size;
                }else {
                    pages = count/page_size+1;
                }
                System.out.println("======vipActionLogger=====pages : " + pages);
                dbCursor = cursor.find().skip((page_number - 1) * page_size).limit(page_size);
                System.out.println("======sys=====dbCursor : " + dbCursor);
            } else {
                Map keyMap = new HashMap();
                keyMap.put("corp_code",corp_code);
                BasicDBObject ref = new BasicDBObject();
                ref.putAll(keyMap);

                DBCursor dbCursor1 = cursor.find(ref);
                int count = Integer.parseInt(String.valueOf(dbCursor1.count()));
                if (count%page_size == 0){
                    pages = count/page_size;
                }else {
                    pages = count/page_size+1;
                }
                dbCursor = dbCursor1.skip((page_number - 1) * page_size).limit(page_size);
                System.out.println("======other=====dbCursor : " + dbCursor);
            }
            ArrayList list = new ArrayList();
            while(dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                list.add(obj.toMap());
            }
            result.put("list",list);
            result.put("pages",pages);
            result.put("page_number",page_number);
            result.put("page_size",page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 用户行为日志
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String user_id = jsonObject.get("id").toString();
            String[] ids = user_id.split(",");
            for (int i = 0; i < ids.length; i++) {
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 用户行为日志
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        int pages=0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            Pattern pattern = Pattern.compile("^.*" + search_value+ ".*$", Pattern.CASE_INSENSITIVE);
            System.out.println("======vipActionLogger===== ");

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection("log_person_action");

            BasicDBObject queryCondition = new BasicDBObject();
            BasicDBList values = new BasicDBList();
            values.add(new BasicDBObject("emp_id", pattern));
            values.add(new BasicDBObject("url", pattern));
            values.add(new BasicDBObject("corp_name", pattern));
            values.add(new BasicDBObject("time", pattern));
            values.add(new BasicDBObject("vip_id", pattern));
            values.add(new BasicDBObject("action", pattern));
            values.add(new BasicDBObject("emp_name", pattern));
            values.add(new BasicDBObject("corp_code", pattern));
            queryCondition.put("$or", values);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                int count = Integer.parseInt(String.valueOf(dbCursor1.count()));
                if (count%page_size == 0){
                    pages = count/page_size;
                }else {
                    pages = count/page_size+1;
                }
                System.out.println("======vipActionLogger=====pages : " + pages);
                dbCursor = dbCursor1.skip((page_number - 1) * page_size).limit(page_size);
                System.out.println("======sys=====dbCursor : " + dbCursor);
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and",value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                int count = Integer.parseInt(String.valueOf(dbCursor2.count()));
                if (count%page_size == 0){
                    pages = count/page_size;
                }else {
                    pages = count/page_size+1;
                }
                dbCursor = dbCursor2.skip((page_number - 1) * page_size).limit(page_size);
                System.out.println("======other=====dbCursor : " + dbCursor);
            }
            ArrayList list = new ArrayList();
            while(dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                list.add(obj.toMap());
            }
            result.put("list",list);
            result.put("pages",pages);
            result.put("page_number",page_number);
            result.put("page_size",page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
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
        int pages=0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String list = jsonObject.get("list").toString();

            JSONArray array = JSONArray.parseArray(list);
            BasicDBObject queryCondition = new BasicDBObject();
            BasicDBList values = new BasicDBList();
            for (int i = 0; i < array.size(); i++) {
                String info = array.get(i).toString();
                JSONObject json = JSONObject.parseObject(info);
                String screen_key = json.get("screen_key").toString();
                String screen_value = json.get("screen_value").toString();
                Pattern pattern = Pattern.compile("^.*" + screen_value+ ".*$", Pattern.CASE_INSENSITIVE);
                values.add(new BasicDBObject(screen_key, pattern));
            }
            queryCondition.put("$and", values);
            System.out.println("======vipActionLogger===== ");

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection("log_person_action");

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                int count = Integer.parseInt(String.valueOf(dbCursor1.count()));
                if (count%page_size == 0){
                    pages = count/page_size;
                }else {
                    pages = count/page_size+1;
                }
                System.out.println("======vipActionLogger=====pages : " + pages);
                dbCursor = dbCursor1.skip((page_number - 1) * page_size).limit(page_size);
                System.out.println("======sys=====dbCursor : " + dbCursor);
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and",value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                int count = Integer.parseInt(String.valueOf(dbCursor2.count()));
                if (count%page_size == 0){
                    pages = count/page_size;
                }else {
                    pages = count/page_size+1;
                }
                dbCursor = dbCursor2.skip((page_number - 1) * page_size).limit(page_size);
                System.out.println("======other=====dbCursor : " + dbCursor);
            }
            ArrayList lists = new ArrayList();
            while(dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                lists.add(obj.toMap());
            }
            result.put("list",lists);
            result.put("pages",pages);
            result.put("page_number",page_number);
            result.put("page_size",page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

}
