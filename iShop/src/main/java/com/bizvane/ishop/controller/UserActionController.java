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
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryOperators;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
import org.bson.*;
import org.springframework.beans.factory.annotation.Autowired;
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
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            MongoClient client = mongodbClient.getMongoClient();
            MongoDatabase database = client.getDatabase("ishow");
            MongoCollection<Document> collection = database.getCollection("vipActionLogger");
            System.out.println("===========collection vipActionLogger: " + collection);
            // 读取数据
            MongoCursor<Document> cursor;

//            if (page_number == 1) {
//                if (role_code.equals(Common.ROLE_SYS)) {
//                    cursor = collection.find().sort(sort).skip((page_number - 1) * page_size).limit(page_size).iterator();
//                } else {
//                    BsonDocument filter = new BsonDocument();
//                    filter.append("CORP_CODE", new BsonString(corp_code));
//                    cursor = collection.find(filter).sort(sort).skip((page_number - 1) * page_size).limit(page_size).iterator();
//                }
//            }else {
//                String time = "";
//                BasicDBObject queryObject = new BasicDBObject().append("time",new BasicDBObject().append(QueryOperators.GT,time));
//                BsonDocument less = new BsonDocument();
//                less.append("time",new BsonMinKey());
//                if (role_code.equals(Common.ROLE_SYS)) {
//                    cursor = collection.find().sort(sort).skip((page_number - 1) * page_size).limit(page_size).iterator();
//                } else {
//                    BsonDocument filter = new BsonDocument();
//                    filter.append("CORP_CODE", new BsonString(corp_code));
//                    cursor = collection.find(filter).sort(sort).skip((page_number - 1) * page_size).limit(page_size).iterator();
//                }
//            }
            cursor = collection.find().skip(1).limit(3).iterator();
            JSONArray array = new JSONArray();
            while (cursor.hasNext()) {
                System.out.print("------1111--cursor.hasNext()--------");
                Document doc = cursor.next();
                String list = doc.get("MSG").toString();
//                String list = cursor.next().toString();
                array.add(list);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(array));
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
        String id = "";
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<UserAchvGoal> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                //系统管理员
            } else {
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }



    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json-----------" + jsString);
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsString);
            id = jsonObject.getString("id");
            String message = jsonObject.get("message").toString();
            org.json.JSONObject jsonObject1 = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject1.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject1.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject1);
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<UserAchvGoal> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

}
