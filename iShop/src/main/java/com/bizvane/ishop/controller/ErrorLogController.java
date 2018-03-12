//package com.bizvane.ishop.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.bizvane.ishop.bean.DataBean;
//import com.bizvane.ishop.constant.Common;
//import com.bizvane.ishop.constant.CommonValue;
//import com.bizvane.ishop.entity.ErrorLog;
//import com.bizvane.ishop.service.BaseService;
//import com.bizvane.ishop.service.ErrorLogService;
//import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
//import com.bizvane.ishop.utils.MongoUtils;
//import com.bizvane.ishop.utils.WebUtils;
//import com.bizvane.sun.common.service.mongodb.MongoDBClient;
//import com.github.pagehelper.PageInfo;
//import com.mongodb.*;
//import org.apache.log4j.Logger;
//import org.bson.types.ObjectId;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by nanji on 2016/8/24.
// */
//@Controller
//@RequestMapping("/errorLog")
//public class ErrorLogController {
//    String id;
//    @Autowired
//    ErrorLogService errorLogService;
//    @Autowired
//    MongoDBClient mongodbClient;
//    @Autowired
//    private BaseService baseService;
//    private static final Logger logger = Logger.getLogger(ErrorLogController.class);
//
////    /**
////     * 错误日志列表
////     *
////     * @param request
////     * @return
////     */
////    @RequestMapping(value = "/list", method = RequestMethod.GET)
////    @ResponseBody
////    public String errorLogManage(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        try {
////            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
////            int page_size = Integer.parseInt(request.getParameter("pageSize"));
////            JSONObject result = new JSONObject();
////            PageInfo<ErrorLog> list = errorLogService.getAllLog(page_number, page_size, "");
////            result.put("list", JSON.toJSONString(list));
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setId("1");
////            dataBean.setMessage(result.toString());
////        } catch (Exception ex) {
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId("1");
////            dataBean.setMessage(ex.getMessage());
////        }
////        return dataBean.getJsonStr();
////    }
//
//
//       /**
//          * 列表展示
//          *
//          * @param request
//          * @return
//          */
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String userActionList(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
//        int pages = 0;
//        try {
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//
//
//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_error_log);
//            System.out.println("======错误日志mongeDB===== "+CommonValue.table_error_log);
//            DBCursor dbCursor = null;
//            // 读取数据
//            if (role_code.equals(Common.ROLE_SYS)) {
//                DBCursor dbCursor1 = cursor.find();
//
//                pages = MongoUtils.getPages(dbCursor1, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
//            } else {
//                Map keyMap = new HashMap();
//                keyMap.put("corp_code", corp_code);
//                BasicDBObject ref = new BasicDBObject();
//                ref.putAll(keyMap);
//                DBCursor dbCursor1 = cursor.find(ref);
//
//                pages = MongoUtils.getPages(dbCursor1, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "created_date", -1);
//            }
//
//            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
//            result.put("list", list);
//            result.put("pages", pages);
//            result.put("page_number", page_number);
//            result.put("page_size", page_size);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//            logger.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
////    /**
////     * 删除错误日志
////     *
////     * @param request
////     * @return
////     */
////    @RequestMapping(value = "/delete", method = RequestMethod.POST)
////    @ResponseBody
////    public String delete(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////
////        try {
////            String jsString = request.getParameter("param");
////            logger.info("json-errorLog-delete-------------" + jsString);
////            JSONObject jsonObj = new JSONObject(jsString);
////            id = jsonObj.get("id").toString();
////            String message = jsonObj.get("message").toString();
////            JSONObject jsonObject = new JSONObject(message);
////            String errorLog_id = jsonObject.get("id").toString();
////            String[] ids = errorLog_id.split(",");
////            for (int i = 0; i < ids.length; i++) {
////                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
////                ErrorLog errorLog = errorLogService.getLogById(Integer.valueOf(ids[i]));
////                if (errorLog != null) {
////                    errorLogService.delete(Integer.valueOf(ids[i]));
////
////                    //----------------行为日志------------------------------------------
////                    /**
////                     * mongodb插入用户操作记录
////                     * @param operation_corp_code 操作者corp_code
////                     * @param operation_user_code 操作者user_code
////                     * @param function 功能
////                     * @param action 动作
////                     * @param corp_code 被操作corp_code
////                     * @param code 被操作code
////                     * @param name 被操作name
////                     * @throws Exception
////                     */
////                    String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
////                    String operation_user_code = request.getSession().getAttribute("user_code").toString();
////                    String function = "系统管理_错误日志";
////                    String action = Common.ACTION_DEL;
////                    String t_corp_code = errorLog.getCorp_code();
////                    String t_code = errorLog.getApp_platform();
////                    String t_name = errorLog.getVersion();
////                    String remark = "";
////                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
////                    //-------------------行为日志结束-----------------------------------------------------------------------------------
////                } else {
////                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////                    dataBean.setId(id);
////                    dataBean.setMessage(message);
////                    return dataBean.getJsonStr();
////                }
////            }
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setId(id);
////            dataBean.setMessage("success");
////        } catch (Exception ex) {
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId(id);
////            dataBean.setMessage(ex.getMessage());
////            return dataBean.getJsonStr();
////        }
////        return dataBean.getJsonStr();
////    }
//
//    /**
//     * 删除
//     */
//    @RequestMapping(value = "/delete", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String callbackDelete(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//             JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//             JSONObject jsonObject = JSONObject.parseObject(message);
//            String[] ids = jsonObject.get("id").toString().split(",");
//
//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_error_log);
//
//            for (int i = 0; i < ids.length; i++) {
//                DBObject deleteRecord = new BasicDBObject();
//                deleteRecord.put("_id", ids[i]);
//
//                DBCursor dbObjects = cursor.find(deleteRecord);
//                String corp_code = "";
//                String version = "";
//                String app_platform = "";
//                while (dbObjects.hasNext()) {
//                    DBObject errorlog = dbObjects.next();
//                    if (errorlog.containsField("corp_code")) {
//                        corp_code = errorlog.get("corp_code").toString();
//                    }
//                    if (errorlog.containsField("version")) {
//                        version = errorlog.get("version").toString();
//                    }
//                    if (errorlog.containsField("app_platform")) {
//                        app_platform = errorlog.get("app_platform").toString();
//                    }
//                }
//                //----------------行为日志------------------------------------------
//                /**
//                 * mongodb插入用户操作记录
//                 * @param operation_corp_code 操作者corp_code
//                 * @param operation_user_code 操作者user_code
//                 * @param function 功能
//                 * @param action 动作
//                 * @param corp_code 被操作corp_code
//                 * @param code 被操作code
//                 * @param name 被操作name
//                 * @throws Exception
//                 */
//                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
//                String operation_user_code = request.getSession().getAttribute("user_code").toString();
//                String function = "系统管理_错误日志";
//                String action = Common.ACTION_DEL;
//                String t_corp_code = corp_code;
//                String t_code = app_platform;
//                String t_name = version;
//                String remark = "";
//                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
//                //-------------------行为日志结束-----------------------------------------------------------------------------------
//                cursor.remove(deleteRecord);
//            }
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage("scuccess");
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            logger.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//
////    /**
////     * 选择一条错误日志记录
////     *
////     * @param request
////     * @return
////     */
////    @RequestMapping(value = "/select", method = RequestMethod.POST)
////    @ResponseBody
////    public String findById(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        String data = null;
////        try {
////            String jsString = request.getParameter("param");
////            logger.info("json-errorLog-------------select-------------" + jsString);
////            JSONObject jsonObj = new JSONObject(jsString);
////            String message = jsonObj.get("message").toString();
////            JSONObject jsonObject = new JSONObject(message);
////            String errorLog_id = jsonObject.get("id").toString();
////            data = JSON.toJSONString(errorLogService.getLogById(Integer.parseInt(errorLog_id)));
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setId("1");
////            dataBean.setMessage(data);
////        } catch (Exception e) {
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId("1");
////            dataBean.setMessage("错误日志信息异常");
////        }
////        return dataBean.getJsonStr();
////    }
//
//
//    /**
//     * 选择一条错误日志记录
//     *
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/select", method = RequestMethod.POST)
//    @ResponseBody
//    public String getfindById(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//
//        try {
//            String jsString = request.getParameter("param");
//            logger.info("json-errorLog-------------select-------------" + jsString);
//            JSONObject jsonObj = new JSONObject(jsString);
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = new JSONObject(message);
//            String errorLog_id = jsonObject.get("id").toString();
//
//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_error_log);
//            DBObject deleteRecord = new BasicDBObject();
//            deleteRecord.put("_id",errorLog_id);
//            DBCursor dbObjects = cursor.find(deleteRecord);
//            DBObject errorlog=null;
//            while (dbObjects.hasNext()) {
//                errorlog  = dbObjects.next();
//            }
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(errorlog.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage("错误日志信息异常");
//        }
//        return dataBean.getJsonStr();
//    }
//
////    /**
////     * 搜索
////     *
////     * @param request
////     * @return
////     */
////    @RequestMapping(value = "/search", method = RequestMethod.POST)
////    @ResponseBody
////    public String search(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        try {
////            String jsString = request.getParameter("param");
////            logger.info("json---errorLog---search---------" + jsString);
////            JSONObject jsonObj = new JSONObject(jsString);
////            id = jsonObj.get("id").toString();
////            String message = jsonObj.get("message").toString();
////            JSONObject jsonObject = new JSONObject(message);
////            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
////            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
////            String search_value = jsonObject.get("searchValue").toString();
////            JSONObject result = new JSONObject();
////            PageInfo<ErrorLog> list = null;
////            list = errorLogService.getAllLog(page_number, page_size, search_value);
////            result.put("list", JSON.toJSONString(list));
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setId(id);
////            dataBean.setMessage(result.toString());
////        } catch (Exception ex) {
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId(id);
////            dataBean.setMessage(ex.getMessage());
////        }
////
////        return dataBean.getJsonStr();
////    }
//
//    /**
//     *
//     * 页面查找
//     */
//    @RequestMapping(value = "/search", method = RequestMethod.POST)
//    @ResponseBody
//    public String getsearch(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
//        int pages = 0;
//        try {
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//            String jsString = request.getParameter("param");
//             JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//             JSONObject jsonObject = JSONObject.parseObject(message);
//            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
//            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
//            String search_value = jsonObject.get("searchValue").toString();
//
//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_error_log);
//
//            String[] column_names = new String[]{"app_platform","corp_code","version"};
//            BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);
//
//            DBCursor dbCursor = null;
//            // 读取数据
//            if (role_code.equals(Common.ROLE_SYS)) {
//                DBCursor dbCursor1 = cursor.find(queryCondition);
//                pages = MongoUtils.getPages(dbCursor1,page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"created_date",-1);
//
//            } else {
//                BasicDBList value = new BasicDBList();
//                value.add(new BasicDBObject("corp_code", corp_code));
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                DBCursor dbCursor2 = cursor.find(queryCondition1);
//
//                pages = MongoUtils.getPages(dbCursor2,page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"created_date",-1);
//            }
//            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
//            result.put("list", list);
//            result.put("pages", pages);
//            result.put("page_number", page_number);
//            result.put("page_size", page_size);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//            logger.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
////    @RequestMapping(value = "/screen", method = RequestMethod.POST)
////    @ResponseBody
////    public String Screen(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        String id = "";
////
////        try {
////            String jsString = request.getParameter("param");
////            logger.info("json-------errorLog--------" + jsString);
////            JSONObject jsonObj = new JSONObject(jsString);
////            id = jsonObj.get("id").toString();
////            String message = jsonObj.get("message").toString();
////            JSONObject jsonObject = new JSONObject(message);
////            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
////            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
////            Map<String, String> map = WebUtils.Json2Map(jsonObject);
////            JSONObject result = new JSONObject();
////            PageInfo<ErrorLog> list;
////            list = errorLogService.selectAllLogScreen(page_number, page_size, map);
////            result.put("list", JSON.toJSONString(list));
////            dataBean.setId(id);
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setMessage(result.toString());
////        } catch (Exception ex) {
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId(id);
////            dataBean.setMessage(ex.getMessage() + ex.toString());
////        }
////
////        return dataBean.getJsonStr();
////    }
//
//    @RequestMapping(value = "/screen", method = RequestMethod.POST)
//    @ResponseBody
//    public String Screen(HttpServletRequest request) {
//        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
//        DataBean dataBean = new DataBean();
//        int pages = 0;
//        try {
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//            String jsString = request.getParameter("param");
//            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
//            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
//            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
//            String lists = jsonObject.get("list").toString();
//
//            JSONArray array = JSONArray.parseArray(lists);
//            BasicDBObject queryCondition = MongoHelperServiceImpl.andUserOperScreen(array);
//
//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_error_log);
//
//            DBCursor dbCursor = null;
//            // 读取数据
//            if (role_code.equals(Common.ROLE_SYS)) {
//                DBCursor dbCursor1 = cursor.find(queryCondition);
//
//                pages = MongoUtils.getPages(dbCursor1,page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"created_date",-1);
//            } else {
//                BasicDBList value = new BasicDBList();
//                value.add(new BasicDBObject("corp_code", corp_code));
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                DBCursor dbCursor1 = cursor.find(queryCondition1);
//
//                pages = MongoUtils.getPages(dbCursor1,page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"created_date",-1);
//            }
//            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
//            result.put("list", list);
//            result.put("pages", pages);
//            result.put("page_number", page_number);
//            result.put("page_size", page_size);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//            logger.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//}
