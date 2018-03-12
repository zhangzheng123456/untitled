package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.WechatReply;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.ErrorLogService;
import com.bizvane.ishop.service.WeChatReplyService;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by PC on 2017/8/9.
 */
@Controller
@RequestMapping("/reply")
public class WeChAtReplyController {
    String id;
    @Autowired
    ErrorLogService errorLogService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    private BaseService baseService;
    @Autowired
    private WeChatReplyService weChatReplyService;

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
            String[] ids = jsonObject.get("id").toString().split(",");

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_wechat_reply_def);

            for (int i = 0; i < ids.length; i++) {
                DBObject deleteRecord = new BasicDBObject();
                deleteRecord.put("_id", new ObjectId(ids[i]));

                DBCursor dbObjects = cursor.find(deleteRecord);
                String corp_code = "";
                String name = "";
                String code = "";
                while (dbObjects.hasNext()) {
                    DBObject errorlog = dbObjects.next();
                    if (errorlog.containsField("corp_code")) {
                        corp_code = errorlog.get("corp_code").toString();
                    }
                    if (errorlog.containsField("code")) {
                        code = errorlog.get("code").toString();
                    }
                    if (errorlog.containsField("name")) {
                        name = errorlog.get("name").toString();
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
                String function = "客服管理";
                String action = Common.ACTION_DEL;
                String t_corp_code = corp_code;
                String t_code = code;
                String t_name = name;
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
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
            JSONObject jsonObj = new JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String errorLog_id = jsonObject.get("id").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_wechat_reply_def);
            DBObject deleteRecord = new BasicDBObject();
            deleteRecord.put("_id", new ObjectId(errorLog_id));
            DBCursor dbObjects = cursor.find(deleteRecord);
            DBObject errorlog = null;
            while (dbObjects.hasNext()) {
                errorlog = dbObjects.next();
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
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
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
            String searchType = jsonObject.get("searchType").toString();
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_wechat_reply_def);

            String[] column_names = new String[]{"name", "corp_name", "brand_name"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("type", searchType));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor1.count());
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("type", searchType));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor2.count());
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("type", searchType));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor2.count());
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
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        DataBean dataBean = new DataBean();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String searchType = jsonObject.get("searchType").toString();
            String lists = jsonObject.get("list").toString();

            JSONArray array = JSONArray.parseArray(lists);
            BasicDBObject queryCondition = MongoHelperServiceImpl.andUserOperScreen(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_wechat_reply_def);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("type", searchType));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor1.count());

            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("type", searchType));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor2.count());
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("type", searchType));
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor1.count());
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
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/addReply", method = RequestMethod.POST)
    @ResponseBody
    public String addReply(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        try {
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_wechat_reply_def);
            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
            String user_code = request.getSession(false).getAttribute("user_code").toString();
            String corp_code = jsonObject.getString("corp_code");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String R_code = corp_code + "R" + sdf.format(new Date()) + Math.round(Math.random() * 9) + Math.round(Math.random() * 9) + Math.round(Math.random() * 9);

            String code = R_code;
            String name = jsonObject.getString("name");
            String content = jsonObject.getString("content");
            String brand_code = jsonObject.getString("brand_code");
            String type = jsonObject.getString("type");
            String isactive = jsonObject.getString("isactive");
            String brand_name = jsonObject.getString("brand_name");
            String corp_name = jsonObject.getString("corp_name");
            JSONArray reply_list =new JSONArray();
            if(!jsonObject.getString("reply_list").equals("")) {
                reply_list = JSON.parseArray(jsonObject.getString("reply_list"));
            }
            WechatReply reply=new WechatReply();
            reply.setCode(code);
            reply.setName(name);
            reply.setContent(content);
            reply.setBrand_code(brand_code);
            reply.setType(type);
            reply.setIsactive(isactive);
            reply.setBrand_name(brand_name);
            reply.setCorp_name(corp_name);
            reply.setReply_list(reply_list);
            reply.setCreater(user_code);
            reply.setModifier(user_code);
            reply.setCorp_code(corp_code);
            weChatReplyService.insert(reply);

            DBObject deleteRecord = new BasicDBObject();

            deleteRecord.put("code", R_code);
            DBCursor dbObjects = cursor.find(deleteRecord);
            String id="";
            if (dbObjects.hasNext()) {
                DBObject obj = dbObjects.next();
                id  = obj.get("_id").toString();
            }
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("操作失败");
        }

        return dataBean.getJsonStr();
    }



    @RequestMapping(value = "/updReply", method = RequestMethod.POST)
    @ResponseBody
    public String updGoodsByWx(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection_def = mongoTemplate.getCollection(CommonValue.table_wechat_reply_def);
        try {
            Date now = new Date();
            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
            String user_code = request.getSession(false).getAttribute("user_code").toString();
           // String code = jsonObject.getString("code");
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            String content = jsonObject.getString("content");
            String brand_code = jsonObject.getString("brand_code");
            String type = jsonObject.getString("type");
            String isactive = jsonObject.getString("isactive");
            String corp_code = jsonObject.getString("corp_code");
            System.out.println("====updReply======:"+corp_code);
            String brand_name = jsonObject.getString("brand_name");
            String corp_name = jsonObject.getString("corp_name");
            JSONArray reply_list =new JSONArray();
            if(!jsonObject.getString("reply_list").equals("")) {
                 reply_list = JSON.parseArray(jsonObject.getString("reply_list"));
            }
            BasicDBObject queryCondition = new BasicDBObject();
            BasicDBList values = new BasicDBList();
            values.add(new BasicDBObject("_id", new ObjectId(id)));
           // values.add(new BasicDBObject("code", code));
            queryCondition.put("$and", values);

            DBObject updatedValue = new BasicDBObject();
            updatedValue.put("name", name);
            updatedValue.put("content", content);
            updatedValue.put("brand_code", brand_code);
            updatedValue.put("type", type);
            updatedValue.put("brand_name", brand_name);
            updatedValue.put("corp_name", corp_name);
            updatedValue.put("corp_code", corp_code);
            updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(now));
            updatedValue.put("modifier", user_code);
            updatedValue.put("isactive", isactive);
            updatedValue.put("reply_list", reply_list);
            DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
            collection_def.update(queryCondition, updateSetValue);

            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }

        return dataBean.getJsonStr();
    }
}
