package com.bizvane.ishop.controller.v2;//package com.bizvane.ishop.v2;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.bizvane.ishop.bean.DataBean;
//import com.bizvane.ishop.constant.Common;
//import com.bizvane.ishop.constant.CommonValue;
//import com.bizvane.ishop.entity.RelViplabel;
//import com.bizvane.ishop.entity.VipLabel;
//import com.bizvane.ishop.entity.ViplableGroup;
//import com.bizvane.ishop.service.BaseService;
//import com.bizvane.ishop.service.CorpService;
//import com.bizvane.ishop.service.VipLabelService;
//import com.bizvane.ishop.service.ViplableGroupService;
//import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
//import com.bizvane.ishop.utils.MongoUtils;
//import com.bizvane.ishop.utils.OutExeclHelper;
//import com.bizvane.ishop.utils.WebUtils;
//import com.bizvane.sun.common.service.mongodb.MongoDBClient;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.pagehelper.PageInfo;
//import com.mongodb.*;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.*;
//
///**
// * Created by zhouying on 2016-04-20.
// */
//@Controller
//@RequestMapping("/VIP/label")
//public class VIPLabelController {
//
//    @Autowired
//    private VipLabelService vipLabelService;
//    @Autowired
//    private ViplableGroupService viplableGroupService;
//    @Autowired
//    private CorpService corpService;
//    @Autowired
//    MongoDBClient mongodbClient;
//    @Autowired
//    private BaseService baseService;
//    private static final Logger log = Logger.getLogger(VIPLabelController.class);
//
//    String id;
//
//
//    /**
//     * 列表展示
//     *
//     * @param request
//     * @return
//     */
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
//            System.out.println("======会员标签mongeDB===== ");
//
//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//           DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_def);
//
//            DBCursor dbCursor = null;
//            // 读取数据
//            if (role_code.equals(Common.ROLE_SYS)) {
//                DBCursor dbCursor1 = cursor.find();
//
//                pages = MongoUtils.getPages(dbCursor1, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "modified_date", -1);
//            } else {
//                Map keyMap = new HashMap();
//                keyMap.put("corp_code", corp_code);
//                BasicDBObject ref = new BasicDBObject();
//                ref.putAll(keyMap);
//                DBCursor dbCursor1 = cursor.find(ref);
//
//                pages = MongoUtils.getPages(dbCursor1, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "modified_date", -1);
//            }
//
//            ArrayList list = MongoHelperServiceImpl.dbCursorToList_labelType(dbCursor);
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
//            //logger.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//
//
//
//    /**
//     * 编辑标签前，获取数据
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
//
//             JSONObject jsonObj = JSONObject.parseObject(jsString);
//            String message = jsonObj.get("message").toString();
//             JSONObject jsonObject = JSONObject.parseObject(message);
//            String errorLog_id = jsonObject.get("id").toString();
//
//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_def);
//            DBObject deleteRecord = new BasicDBObject();
//            deleteRecord.put("_id",errorLog_id);
//            DBCursor dbObjects = cursor.find(deleteRecord);
//            DBObject vip_label=null;
//            while (dbObjects.hasNext()) {
//                vip_label  = dbObjects.next();
//            }
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(vip_label.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage("信息异常");
//        }
//        return dataBean.getJsonStr();
//    }
//
//
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
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_def);
//
//            for (int i = 0; i < ids.length; i++) {
//                DBObject deleteRecord = new BasicDBObject();
//                deleteRecord.put("_id", ids[i]);
//
//                DBCursor dbObjects = cursor.find(deleteRecord);
//                String corp_code = "";
//
//                String label_name = "";
//                while (dbObjects.hasNext()) {
//                    DBObject errorlog = dbObjects.next();
//                    if (errorlog.containsField("corp_code")) {
//                        corp_code = errorlog.get("corp_code").toString();
//                    }
//                    if (errorlog.containsField("label_name")) {
//                        label_name = errorlog.get("label_name").toString();
//                    }
//                }
//                //----------------行为日志开始------------------------------------------
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
//                String function = "会员管理_会员标签";
//                String action = Common.ACTION_DEL;
//                String t_corp_code = corp_code;
//                String t_code = ids[i];
//                String t_name = label_name;
//                String remark = "";
//                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
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
//
//        }
//        return dataBean.getJsonStr();
//    }
//
//
//
//
//
//    /**
//     * 会员标签管理
//     * 判断企业内标签名称是否唯一
//     */
//    @RequestMapping(value = "/VipLabelNameExist")
//    @ResponseBody
//    public String VipLabelNameExist(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_def);
//
//            String jsString = request.getParameter("param");
//            JSONObject jsonObject1 = JSONObject.parseObject(jsString);
//            String message = jsonObject1.get("message").toString();
//            JSONObject jsonObject2 = JSONObject.parseObject(message);
//            String label_name = jsonObject2.getString("label_name");
//            String corp_code = jsonObject2.getString("corp_code");
//            Map keyMap = new HashMap();
//            keyMap.put("corp_code", corp_code);
//            keyMap.put("label_name", label_name);
//            BasicDBObject ref = new BasicDBObject();
//            ref.putAll(keyMap);
//            DBCursor dbCursor = cursor.find(ref);
//            ArrayList list = MongoHelperServiceImpl.dbCursorToList_labelType(dbCursor);
//            if (list.size() == 0) {
//                dataBean.setId(id);
//                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                dataBean.setMessage("标签名称未被使用");
//            } else {
//                dataBean.setId(id);
//                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setMessage("标签名称已被使用");
//            }
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//    /**
//     *
//     * 页面查找
//     */
//    @RequestMapping(value = "/find", method = RequestMethod.POST)
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
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_def);
//
//            String[] column_names = new String[]{"corp_name","label_name"};
//            BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);
//
//            DBCursor dbCursor = null;
//            // 读取数据
//            if (role_code.equals(Common.ROLE_SYS)) {
//                DBCursor dbCursor1 = cursor.find(queryCondition);
//                pages = MongoUtils.getPages(dbCursor1,page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
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
//                dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"modified_date",-1);
//            }
//            ArrayList list = MongoHelperServiceImpl.dbCursorToList_labelType(dbCursor);
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
//
//        }
//        return dataBean.getJsonStr();
//    }
//
//
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
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_def);
//
//            DBCursor dbCursor = null;
//            // 读取数据
//            if (role_code.equals(Common.ROLE_SYS)) {
//                DBCursor dbCursor1 = cursor.find(queryCondition);
//
//                pages = MongoUtils.getPages(dbCursor1,page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
//            } else {
//                BasicDBList value = new BasicDBList();
//                value.add(new BasicDBObject("corp_code", corp_code));
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                DBCursor dbCursor1 = cursor.find(queryCondition1);
//
//                pages = MongoUtils.getPages(dbCursor1,page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
//            }
//            ArrayList list = MongoHelperServiceImpl.dbCursorToList_labelType(dbCursor);
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
//           // logger.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//
//
//
//}
