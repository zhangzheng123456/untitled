package com.bizvane.ishop.controller.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
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
import java.util.*;

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
    String id;


    /**
     * 用户登录日志（mongodb）
     * 列表展示
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String getlist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            System.out.println("======UserOperationController===== ");

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_login_log);
            List<User> canloginByCode = null;
            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find();

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode("");
            } else {
                Map keyMap = new HashMap();
                keyMap.put("corp_code", corp_code);
                BasicDBObject ref = new BasicDBObject();
                ref.putAll(keyMap);
                DBCursor dbCursor1 = cursor.find(ref);

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode(corp_code);
            }

            ArrayList list = MongoHelperServiceImpl.dbCursorToList_canLogin(dbCursor, canloginByCode,"");
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
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_login_log);

            String[] column_names = new String[]{"user_name", "app_platform", "corp_name"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
            List<User> canloginByCode = null;
            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode("");
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode(corp_code);
            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_canLogin(dbCursor, canloginByCode,"");
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
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode("");
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
                canloginByCode = userService.getCanloginByCode(corp_code);
            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_canLogin(dbCursor, canloginByCode,user_can_login);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
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
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            ArrayList list = new ArrayList();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_login_log);
            DBObject sort_obj = new BasicDBObject("time", -1);

            if (screen.equals("")) {
                String[] column_names = new String[]{"user_name", "app_platform", "corp_name"};
                BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
                List<User> canloginByCode = null;
                DBCursor dbCursor = null;
                // 读取数据
                if (role_code.equals(Common.ROLE_SYS)) {
                    dbCursor = cursor.find(queryCondition).sort(sort_obj);
                    canloginByCode = userService.getCanloginByCode("");
                } else {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                    canloginByCode = userService.getCanloginByCode(corp_code);
                }
                list = MongoHelperServiceImpl.dbCursorToList_canLogin(dbCursor, canloginByCode,"");
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
                BasicDBObject queryCondition = MongoHelperServiceImpl.andLoginlogScreen(array);
                DBCursor dbCursor = null;
                List<User> canloginByCode = null;
                // 读取数据
                if (role_code.equals(Common.ROLE_SYS)) {
                    dbCursor = cursor.find(queryCondition).sort(sort_obj);
                    canloginByCode = userService.getCanloginByCode("");
                } else {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                    canloginByCode = userService.getCanloginByCode(corp_code);
                }
                list = MongoHelperServiceImpl.dbCursorToList_canLogin(dbCursor, canloginByCode,user_can_login);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= 50000) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request);
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
