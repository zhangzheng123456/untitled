package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.entity.SendCoupons;
import com.bizvane.ishop.entity.SendTicket;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageInfo;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 发券记录
 * Created by jy on 2017/10/11.
 */
@Controller
@RequestMapping("/couponsLog")
public class CouponsLogController {
    @Autowired
    private BaseService baseService;


    @Autowired
    CouponsLogService couponsLogService;
    @Autowired
    SendCouponsService sendCouponsService;
    @Autowired
    private CorpService corpService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    VipRecordService vipRecordService;
    String id;
    @Autowired
    MongoDBClient mongodbClient;

    private static final Logger log = Logger.getLogger(CouponsLogController.class);
    /**
     * 发券记录
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        int pages = 0;
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_coupon_log);
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            String role_code = request.getSession().getAttribute("role_code").toString();
            //String corp_code = request.getSession().getAttribute("corp_code").toString();


            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();


            String corp_code = request.getSession().getAttribute("corp_code").toString();
            JSONObject result = new JSONObject();
            JSONObject result1 = new JSONObject();

            BasicDBList value = new BasicDBList();
            //按【会员卡】 【会员名称】 【优惠券名称】 【券号】搜索

            String[] column_names = new String[]{"params.cardno", "params.vip_name", "params.couponName"};
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
            dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "send_time", -1);

            JSONArray array = couponsLogService.transLog(dbCursor);
            log.info("==================array===================="+array.size()+"=============");

            result.put("list", array);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            result.put("total", total);
            result1.put("list", result.toJSONString());

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result1.toString());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
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
            String lists = jsonObject.getJSONArray("list").toString();
            JSONObject obj_param = new JSONObject();
            BasicDBObject oo = new BasicDBObject();

            JSONArray array = JSONArray.parseArray(lists);
//            BasicDBObject queryCondition = MongoHelperServiceImpl.andUserOperScreen(array);
            BasicDBObject queryCondition = couponsLogService.getScreen(array, corp_code);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_coupon_log);

            DBCursor dbCursor = null;
            BasicDBObject queryCondition1 = new BasicDBObject();

            BasicDBList value = new BasicDBList();
            value.add(queryCondition);
            // 读取数据
          // value = vipRecordService.getRoleCondition(request, corp_code, value);

            // 读取数据
            log.info("=====================value================="+value.size());
            DBCursor dbCursor2;
            if (value.size() > 1) {
                queryCondition1.put("$and", value);
                dbCursor2 = cursor.find(queryCondition1);
            } else if (value.size() == 1) {
                dbCursor2 = cursor.find((BasicDBObject) value.get(0));
            } else {
                dbCursor2 = cursor.find();
            }
            log.info("=================dbCursor2============================"+dbCursor2.count());
            int total = dbCursor2.count();
            pages = MongoUtils.getPages(dbCursor2, page_size);
            dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "send_time", -1);

            JSONArray array1 =couponsLogService.transLog(dbCursor);
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

        }
        return dataBean.getJsonStr();
    }



    /**
     * 发券记录管理
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
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_coupon_log);

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

                    if (vip.containsField("params")) {
                       JSONObject obj=  JSON.parseObject(vip.get("params").toString());
                        if (obj.containsKey("vip_name")) {
                            vip_name = obj.getString("vip_name");
                            vip_id= obj.getString("vip_id");
                        }
                    }

                    if (vip.containsField("send_user")) {
                        user_name = vip.get("send_user").toString();
                    }
                    if (vip.containsField("send_time")) {
                        date = vip.get("send_time").toString();
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
                String function = "会员营销_删除发券记录";
                String action = Common.ACTION_DEL;
                String t_corp_code = operation_corp_code;
                String t_code = vip_id;
                String t_name = vip_name;
                String remark = "发券人(" + user_name + ")" + date;
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
}
