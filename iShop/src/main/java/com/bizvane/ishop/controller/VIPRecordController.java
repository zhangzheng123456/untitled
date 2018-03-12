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
import com.bizvane.ishop.service.VipRecordService;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
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

import static com.bizvane.ishop.utils.MongoUtils.dbCursorToList;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.regex;

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

    @Autowired
    private UserService userService;
    @Autowired
    private StoreService storeService;
    private static final Logger log = Logger.getLogger(VIPRecordController.class);

    String id;

    /***
     * 导出数据
     */
    @RequestMapping(value = "/callback/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExeclCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String user_code = request.getSession(false).getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_back_record);
            DBCursor dbCursor = null;
            int page_num = jsonObject.getInteger("page_num");
            int page_size = jsonObject.getInteger("page_size");

            if (screen.equals("")) {
                String[] column_names = new String[]{"vip_card_no", "vip_name", "user_name"};
                BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
                BasicDBObject queryCondition1 = new BasicDBObject();
                BasicDBList value = new BasicDBList();

                value.add(queryCondition);

                value = vipRecordService.getRoleCondition(request, corp_code, value);

                // 读取数据
                DBCursor dbCursor2;
                if (value.size() > 1) {
                    queryCondition1.put("$and", value);
                    dbCursor2 = cursor.find(queryCondition1);
                } else if (value.size() == 1) {
                    dbCursor2 = cursor.find((BasicDBObject) value.get(0));
                } else {
                    dbCursor2 = cursor.find();
                }
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_num, page_size, "created_date", -1);

            } else {
                JSONArray array = JSONArray.parseArray(screen);
                BasicDBObject queryCondition = vipRecordService.getScreen(array, corp_code);

                BasicDBObject queryCondition1 = new BasicDBObject();
                BasicDBList value = new BasicDBList();
                value.add(queryCondition);
                // 读取数据
                // 读取数据
                DBCursor dbCursor2;
                if (value.size() > 1) {
                    queryCondition1.put("$and", value);
                    dbCursor2 = cursor.find(queryCondition1);
                } else if (value.size() == 1) {
                    dbCursor2 = cursor.find((BasicDBObject) value.get(0));
                } else {
                    dbCursor2 = cursor.find();
                }
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_num, page_size, "created_date", -1);
            }
            System.out.println("==========size" + dbCursor.count());
            if (dbCursor.size() >= 50000) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            JSONArray array1 = vipRecordService.transRecord(dbCursor);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(array1);

            LinkedHashMap<String, String> map2 = WebUtils.Json2ShowName(jsonObject);

            int count = array1.size();
            int start_line = (page_num - 1) * page_size + 1;
            int end_line = page_num * page_size;
            if (count < page_size)
                end_line = (page_num - 1) * page_size + count;

            String pathname = OutExeclHelper.OutExecl(json, array1, map2, response, request, "回访记录(" + start_line + "-" + end_line + ")");
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
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
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
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_back_record);
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonobj = JSONObject.parseObject(jsString);
            String message = jsonobj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String corp_code = request.getSession().getAttribute("corp_code").toString();
            JSONObject result = new JSONObject();
            JSONObject result1 = new JSONObject();

            BasicDBList value = new BasicDBList();

            String[] column_names = new String[]{"vip_card_no", "VIP_CARDNO", "vip_name", "VIP_NAME"};
            BasicDBObject queryCondition = new BasicDBObject();
            if (!search_value.equals("")) {
                queryCondition = MongoUtils.orOperation(column_names, search_value);
                value.add(queryCondition);
            }

            DBCursor dbCursor = null;
            BasicDBObject queryCondition1 = new BasicDBObject();

            value = vipRecordService.getRoleCondition(request, corp_code, value);

            // 读取数据
            DBCursor dbCursor2;
            if (value.size() > 1) {
                queryCondition1.put("$and", value);
                dbCursor2 = cursor.find(queryCondition1);
            } else if (value.size() == 1) {
                dbCursor2 = cursor.find((BasicDBObject) value.get(0));
            } else {
                dbCursor2 = cursor.find();
            }
            int total = dbCursor2.count();
            pages = MongoUtils.getPages(dbCursor2, page_size);
            dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);

            JSONArray array = vipRecordService.transRecord(dbCursor);
            result.put("list", array);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            result.put("total", total);
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            String user_code = request.getSession().getAttribute("user_code").toString();
            JSONObject result = new JSONObject();
            JSONObject result1 = new JSONObject();
            String lists = jsonObject.get("list").toString();
            JSONArray array = JSONArray.parseArray(lists);
//            BasicDBObject queryCondition = MongoHelperServiceImpl.andUserOperScreen(array);
            BasicDBObject queryCondition = vipRecordService.getScreen(array, corp_code);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_back_record);

            DBCursor dbCursor = null;
            BasicDBObject queryCondition1 = new BasicDBObject();

            BasicDBList value = new BasicDBList();
            value.add(queryCondition);
            // 读取数据
            value = vipRecordService.getRoleCondition(request, corp_code, value);

            // 读取数据
            DBCursor dbCursor2;
            if (value.size() > 1) {
                queryCondition1.put("$and", value);
                dbCursor2 = cursor.find(queryCondition1);
            } else if (value.size() == 1) {
                dbCursor2 = cursor.find((BasicDBObject) value.get(0));
            } else {
                dbCursor2 = cursor.find();
            }
            int total = dbCursor2.count();
            pages = MongoUtils.getPages(dbCursor2, page_size);
            dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);

            JSONArray array1 = vipRecordService.transRecord(dbCursor);
            result.put("list", array1);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            result.put("total", total);

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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String[] ids = jsonObject.get("id").toString().split(",");

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_back_record);

            for (int i = 0; i < ids.length; i++) {
                DBObject deleteRecord = new BasicDBObject();
                if (ids[i].startsWith("etl_")) {
                    deleteRecord.put("_id", ids[i]);
                } else {
                    deleteRecord.put("_id", new ObjectId(ids[i]));
                }
                DBCursor dbObjects = cursor.find(deleteRecord);
                String vip_id = "";
                String vip_name = "";
                String user_name = "";
                String date = "";
                String corp_name = "";
                while (dbObjects.hasNext()) {
                    DBObject vip = dbObjects.next();
                    if (vip.containsField("vip_id")) {
                        vip_id = vip.get("vip_id").toString();
                    }
                    if (vip.containsField("vip_name")) {
                        vip_name = vip.get("vip_name").toString();
                    }
                    if (vip.containsField("user_name")) {
                        user_name = vip.get("user_name").toString();
                    }
                    if (vip.containsField("date")) {
                        date = vip.get("date").toString();
                    }
                    if (vip.containsField("corp_name")) {
                        corp_name = vip.get("corp_name").toString();
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
                String remark = "回访员工(" + user_name + ")" + date;
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
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

            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String errorLog_id = jsonObject.get("id").toString();
            //       System.out.println("----id-----"+errorLog_id);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_back_record);
            DBObject deleteRecord = new BasicDBObject();

            if (errorLog_id.startsWith("etl_")) {
                deleteRecord.put("_id", errorLog_id);
            } else {
                deleteRecord.put("_id", new ObjectId(errorLog_id));
            }
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


    /**
     * 微信聊天记录
     * 查找
     */
    @RequestMapping(value = "/callback/find/message", method = RequestMethod.POST)
    @ResponseBody
    public String findContent(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        int pages = 0;
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_back_record);
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonobj = JSONObject.parseObject(jsString);
            String message = jsonobj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            //String vip_id = jsonObject.get("vip_id").toString();
            //type=爱秀回访    user_id_app   user_name   corp_code
            String user_code = jsonObject.get("user_id_app").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            // open_id
            String open_id = jsonObject.get("open_id").toString();
            String query_time = jsonObject.get("query_time").toString();
            JSONObject message_info = new JSONObject();
            message_info.put("user_avatar","");
            message_info.put("vip_avatar","");
            //会员头像
            String vip_avatar = "";
            //用户的头像
            User user = userService.selectUserByCode(corp_code, user_code, Common.IS_ACTIVE_Y);
            String user_ava = user.getAvatar();
         message_info.put("user_avatar",user_ava);

            JSONArray array = new JSONArray();

            BasicDBList list = new BasicDBList();
            BasicDBList list_ava = new BasicDBList();

            list.add(new BasicDBObject("user_code", user_code));
            list.add(new BasicDBObject("open_id", open_id));
            list.add(new BasicDBObject("corp_code", corp_code));
            list_ava.add(new BasicDBObject("open_id", open_id));
            list_ava.add(new BasicDBObject("corp_code", corp_code));
            //Pattern pattern;//用在模糊查询得时候，对字段进行匹配
            if (StringUtils.isNotBlank(query_time)) {//模糊查询

                Pattern pattern = Pattern.compile("^.*" + query_time + ".*$", Pattern.CASE_INSENSITIVE);

                list.add(new BasicDBObject("message_date", pattern));

              //  log.info("====================pattern==================================================" + pattern);

            }
            //log.info("=========query======================================" + list.toString());
            DBCollection cursor_content = mongoTemplate.getCollection("vip_message_content");
            DBCollection cursor_ava = mongoTemplate.getCollection("vip_emp_relation");

            BasicDBObject query = new BasicDBObject();
            query.put("$and", list);
            BasicDBObject query_ava = new BasicDBObject();
            query_ava.put("$and", list_ava);
            DBCursor dbCursor = cursor_content.find(query);
            DBCursor dbCursor1 = cursor_ava.find(query_ava);
           // log.info("=========================   dbCursor.size()=================================" + dbCursor.size());
            List<Map<String, Object>> list1 = dbCursorToList(dbCursor);
            List<Map<String, Object>> list_vip = dbCursorToList(dbCursor1);


          //  log.info("=========================微信聊天记录==================================" + list1.size());
            if (list_vip.size() > 0) {
                vip_avatar = list_vip.get(0).get("head_img").toString();
                  log.info("=========================微信头像==================================" + vip_avatar);

                message_info.put("vip_avatar",vip_avatar);
            }
            for (int i = 0; i < list1.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("message_content", list1.get(i).get("message_content").toString());
                object.put("message_target", list1.get(i).get("message_target").toString());
                object.put("message_date", list1.get(i).get("message_date").toString());
                object.put("message_content", list1.get(i).get("message_content").toString());
                object.put("message_type", list1.get(i).get("message_type").toString());
                array.add(object);
            }
            message_info.put("content",array.toJSONString());

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(message_info.toJSONString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

}
