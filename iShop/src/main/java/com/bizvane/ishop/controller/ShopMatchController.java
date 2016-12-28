package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.ErrorLogService;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.service.imp.ShopMatchServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Created by nanji on 2016/8/24.
 */
@Controller
@RequestMapping("/shopMatch")
public class ShopMatchController {
    String id;

    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    private BaseService baseService;
    private static final Logger logger = Logger.getLogger(ShopMatchController.class);
    /**
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String[] d_match_codes = jsonObject.get("d_match_code").toString().split(",");

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_def);

            DBCollection cursor2 = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);
            for (int i = 0; i < d_match_codes.length; i++) {
                DBObject deleteRecord = new BasicDBObject();
                deleteRecord.put("d_match_code", d_match_codes[i]);

                DBCursor dbObjects = cursor.find(deleteRecord);
                String corp_code = "";
                String d_match_title = "";
                String d_match_code = "";
                while (dbObjects.hasNext()) {
                    DBObject errorlog = dbObjects.next();
                    if (errorlog.containsField("corp_code")) {
                        corp_code = errorlog.get("corp_code").toString();
                    }
                    if (errorlog.containsField("d_match_code")) {
                        d_match_code = errorlog.get("d_match_code").toString();
                    }
                    if (errorlog.containsField("d_match_title")) {
                        d_match_title = errorlog.get("d_match_title").toString();
                    }
                }
                //----------------行为日志------------------------------------------
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
                String function = "系统管理_错误日志";
                String action = Common.ACTION_DEL;
                String t_corp_code = corp_code;
                String t_code = d_match_code;
                String t_name = d_match_title;
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
                cursor.remove(deleteRecord);

                cursor2.remove(deleteRecord);
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
    /**
     * 选择一条错误日志记录
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String getfindById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String jsString = request.getParameter("param");
            logger.info("json-errorLog-------------select-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String errorLog_id = jsonObject.get("id").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
            DBObject deleteRecord = new BasicDBObject();
            deleteRecord.put("_id",errorLog_id);
            DBCursor dbObjects = cursor.find(deleteRecord);
            DBObject errorlog=null;
            while (dbObjects.hasNext()) {
                errorlog  = dbObjects.next();
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(errorlog.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
        }
        return dataBean.getJsonStr();
    }


    /**
     *
     * 页面查找
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public String getsearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_def);

            String[] column_names = new String[]{"d_match_title"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"d_match_likeCount",-1);

            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor2,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"d_match_likeCount",-1);
            }
            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
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
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     *
     * 页面查找
     */
    @RequestMapping(value = "/listByUser", method = RequestMethod.POST)
    @ResponseBody
    public String getsearchByUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String user_code = request.getSession(false).getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_def);

            BasicDBObject queryCondition = MongoUtils.andOperation2(corp_code,user_code);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"created_date",-1);

            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor2,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"created_date",-1);
            }
            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
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
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

}
