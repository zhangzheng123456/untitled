package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.CRMInterfaceService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.NumberUtil;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by zhou on 2017/1/5.
 */
@Controller
@RequestMapping("/vipCheck")
public class VipCheckController {


    private static final Logger logger = Logger.getLogger(VipCheckController.class);
    String id;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    CRMInterfaceService crmInterfaceService;
    @Autowired
    StoreService storeService;
    @Autowired
    private BaseService baseService;

    /**
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
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_check);

            String[] column_names = new String[]{"billNO","store_name","vip_name","remark"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);

            DBCursor dbCursor1 = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                dbCursor1 = cursor.find(queryCondition);
            } else if (role_code.equals(Common.ROLE_GM)){
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor1 = cursor.find(queryCondition1);
            }else{
                BasicDBList storeList = new BasicDBList();
                if (role_code.equals(Common.ROLE_BM)){
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                    for (int i = 0; i < stores.size(); i++) {
                        storeList.add(stores.get(i).getStore_code());
                    }
                }else if (role_code.equals(Common.ROLE_AM)){
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    String ares_store_code = request.getSession().getAttribute("store_code").toString();
                    ares_store_code = ares_store_code.replace(Common.SPECIAL_HEAD, "");

                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, "", "",ares_store_code);
                    for (int i = 0; i < stores.size(); i++) {
                        storeList.add(stores.get(i).getStore_code());
                    }
                }else if (role_code.equals(Common.ROLE_SM)){
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    String[] stores = store_code.split(",");
                    for (int i = 0; i < stores.length; i++) {
                        storeList.add(stores[i]);
                    }
                }
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("store_code", new BasicDBObject("$in", storeList)));

                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor1 = cursor.find(queryCondition1);
            }
            pages = MongoUtils.getPages(dbCursor1,page_size);
            DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
            if (dbCursor.size()<1){
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number-1,page_size,"modified_date",-1);
            }
            ArrayList list = MongoUtils.dbCursorToList(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("pageNum", page_number);
            result.put("pageSize", page_size);
            result.put("total", dbCursor1.count());

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

    /**
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
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
            String lists = jsonObject.get("list").toString();

            JSONArray array = JSONArray.parseArray(lists);
            BasicDBObject queryCondition = MongoHelperServiceImpl.andSignScreen(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_check);

            DBCursor dbCursor1 = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                dbCursor1 = cursor.find(queryCondition);
            } else if (role_code.equals(Common.ROLE_GM)){
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor1 = cursor.find(queryCondition1);
            }else{
                BasicDBList storeList = new BasicDBList();
                if (role_code.equals(Common.ROLE_BM)){
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                    for (int i = 0; i < stores.size(); i++) {
                        storeList.add(stores.get(i).getStore_code());
                    }
                }else if (role_code.equals(Common.ROLE_AM)){
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    String ares_store_code = request.getSession().getAttribute("store_code").toString();
                    ares_store_code = ares_store_code.replace(Common.SPECIAL_HEAD, "");

                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, "", "",ares_store_code);
                    for (int i = 0; i < stores.size(); i++) {
                        storeList.add(stores.get(i).getStore_code());
                    }
                }else if (role_code.equals(Common.ROLE_SM)){
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    String[] stores = store_code.split(",");
                    for (int i = 0; i < stores.length; i++) {
                        storeList.add(stores[i]);
                    }
                }
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("store_code", new BasicDBObject("$in", storeList)));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor1 = cursor.find(queryCondition1);
            }
            pages = MongoUtils.getPages(dbCursor1,page_size);
            DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
            if (dbCursor.size()<1){
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number-1,page_size,"modified_date",-1);
            }
            ArrayList list = MongoUtils.dbCursorToList(dbCursor);
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
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 根据id查看
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String id = jsonObject.get("id").toString();

            MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_check);
            BasicDBObject dbObject = new BasicDBObject();
            dbObject.put("_id", id);
            DBCursor dbCursor = cursor.find(dbObject);

            if (dbCursor.size() > 0) {
                DBObject obj = dbCursor.next();
                String type = obj.get("type").toString();
                String corp_code = obj.get("corp_code").toString();

                if (obj.containsField("recharge_type")) {
                    String recharge_type = obj.get("recharge_type").toString();
                    if (type.equals("充值")){
                        if (recharge_type.equals("1"))
                            obj.put("recharge_type", "直接充值");
                        if (recharge_type.equals("2"))
                            obj.put("recharge_type", "退换转充值");
                    }
                    if (type.equals("退款")){
                        if (recharge_type.equals("1"))
                            obj.put("recharge_type", "按充值单退款");
                        if (recharge_type.equals("2"))
                            obj.put("recharge_type", "按余额退款");
                    }
                }
                if (obj.containsField("bill_date")){
                    String bill_date = obj.get("bill_date").toString();
                    bill_date = Common.DATETIME_FORMAT_DAY.format(Common.DATETIME_FORMAT_DAY_NO.parse(bill_date));
                    obj.put("bill_date", bill_date);
                }
                if (!obj.containsField("isactive")){
                    obj.put("isactive", "Y");
                }
                if (!obj.containsField("creater")){
                    obj.put("creater", "");
                }
                if (!obj.containsField("modifier")){
                    obj.put("modifier", "");
                }
                JSONObject result = new JSONObject();
                result.put("list", obj);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result.toString());
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("error");
            }

        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 审核单据（改mongodb状态）
     */
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @ResponseBody
    public String changeStatus(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code1 = request.getSession().getAttribute("user_code").toString();
        String user_name1 = request.getSession().getAttribute("user_name").toString();

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_check);
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String id = jsonObject.get("id").toString();
            String type = jsonObject.get("type").toString();

            String corp_code =id.split("_")[0];
            int bill_id = Integer.parseInt(id.split("_")[1]);
            String result = "";
            if (type.equals("pay")){
                result = crmInterfaceService.submitPrepaidBill(corp_code,bill_id);
            }else if (type.equals("refund")){
                result = crmInterfaceService.submitRefundBill(corp_code,bill_id);
            }
            JSONObject result_obj = JSONObject.parseObject(result);
            String code = result_obj.getString("code");
            if (code.equals("0")){
                //记录存在，更新
                DBObject updateCondition = new BasicDBObject();
                updateCondition.put("_id", id);
                DBObject updatedValue = new BasicDBObject();
                updatedValue.put("status", "1");
                updatedValue.put("modified_date",Common.DATETIME_FORMAT.format(new Date()));
                updatedValue.put("modifier",user_name1+"("+user_code1+")");

                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                cursor.update(updateCondition, updateSetValue);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage("success");

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
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "会员管理_审核管理 ";
                String action = Common.ACTION_UPD;
                String t_corp_code = "";
                String t_code = action_json.get("id").toString();
                String t_name = action_json.get("type").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage(result_obj.getString("message"));
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 编辑单据
     * 直接根据展示的字段全部返回
     */
    @RequestMapping(value = "/editBill", method = RequestMethod.POST)
    @ResponseBody
    public String editBill(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code1 = request.getSession().getAttribute("user_code").toString();
        String user_name1 = request.getSession().getAttribute("user_name").toString();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_check);
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String type = jsonObject.get("type").toString();
            String id = jsonObject.get("id").toString();

            String bill_id = id.split("_")[1];
            String corp_code =id.split("_")[0];

            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("id",bill_id);

            DBObject updatedValue = new BasicDBObject();

            String result = "";
            String bill_date = jsonObject.getString("bill_date");
            String date = bill_date.replace("-","");//单据日期

            String store_code = jsonObject.getString("store_code");
            String store_name = jsonObject.getString("store_name");
            String user_code = jsonObject.getString("user_code");
            String user_name = jsonObject.getString("user_name");
            String recharge_type = jsonObject.getString("recharge_type");
            String remark = jsonObject.getString("remark");
            String isactive = jsonObject.getString("isactive");

            if (!date.equals(""))
                updatedValue.put("bill_date",date);
            updatedValue.put("store_name",store_name);
            updatedValue.put("user_name",user_name);
            updatedValue.put("store_code",store_code);
            updatedValue.put("user_code",user_code);
            updatedValue.put("remark",remark);
            updatedValue.put("isactive",isactive);

            updatedValue.put("modified_date",Common.DATETIME_FORMAT.format(new Date()));
            updatedValue.put("modifier",user_name1+"("+user_code1+")");

            if (type.equals("pay")){
                if (recharge_type.equals("直接充值"))
                    recharge_type = "1";
                if (recharge_type.equals("退换转充值"))
                    recharge_type = "2";
                String tag_price = jsonObject.getString("tag_price");
                String pay_price = jsonObject.getString("pay_price");
                if (jsonObject.containsKey("activity_content")){
                    String activity_content = jsonObject.get("activity_content").toString();//折扣
                    map.put("active_content",activity_content);
                    updatedValue.put("active_content",activity_content);
                }

                map.put("bill_date",date);
                map.put("store_name",store_name);
                map.put("user_name",user_name);
                map.put("recharge_type",recharge_type);
                map.put("tag_price",tag_price);
                map.put("pay_price",pay_price);
                map.put("remark",remark);
                map.put("ISACTIVE",isactive);
                result = crmInterfaceService.modPrepaidStatus(corp_code,map);

                updatedValue.put("recharge_type",recharge_type);
                updatedValue.put("tag_price",tag_price);
                updatedValue.put("pay_price",pay_price);
                updatedValue.put("discount", NumberUtil.keepPrecision(Double.parseDouble(pay_price)/Double.parseDouble(tag_price)));

            }else if (type.equals("refund")){
                if (recharge_type.equals("按余额退款"))
                    recharge_type = "BA";
                if (recharge_type.equals("按充值单退款"))
                    recharge_type = "VM";
                String source_no = jsonObject.getString("source_no");

                map.put("bill_date",date);
                map.put("store_name",store_name);
                map.put("user_name",user_name);
                map.put("recharge_type",recharge_type);
                map.put("source_no",source_no);
                map.put("remark",remark);
                result = crmInterfaceService.modRefundStatus(corp_code,map);

                updatedValue.put("recharge_type",recharge_type);
                updatedValue.put("source_no",source_no);
            }
            JSONObject result_obj = JSONObject.parseObject(result);
            String code = result_obj.getString("code");
            if (code.equals("0")){
                DBObject updateCondition = new BasicDBObject();
                updateCondition.put("_id", id);
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                cursor.update(updateCondition, updateSetValue);

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result_obj.getString("message"));

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
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "会员管理_审核管理";
                String action = Common.ACTION_UPD;
                String t_corp_code = "";
                String t_code = action_json.get("store_code").toString();
                String t_name = action_json.get("store_name").toString();
                String remark1 = action_json.get("remark").toString();
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark1);
                //-------------------行为日志结束-----------------------------------------------------------------------------------


            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage(result_obj.getString("message"));
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 删除单据
     *
     */
    @RequestMapping(value = "/deleteBill", method = RequestMethod.POST)
    @ResponseBody
    public String deleteBill(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_check);
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String id = jsonObject.get("id").toString();

            String[] ids = id.split(",");
            for (int i = 0; i < ids.length; i++) {
                BasicDBObject dbObject = new BasicDBObject();
                dbObject.put("_id", ids[i]);
                DBCursor dbCursor = cursor.find(dbObject);

                if (dbCursor.size() > 0) {
                    DBObject obj = dbCursor.next();
                    String type = obj.get("type").toString();
                    String corp_code = obj.get("corp_code").toString();
                    String bill_no = obj.get("bill_no").toString();
                    String store_code= obj.get("store_code").toString();
                    String store_name = obj.get("store_name").toString();
                    String bill_id = ids[i].split("_")[1];
                    String result = "";
                    logger.info("===================delCheck==bill_id"+bill_id);

                    if (type.equals("充值")) {
                        result = crmInterfaceService.delPrepaidBill(corp_code, Integer.parseInt(bill_id));
                    } else {
                        result = crmInterfaceService.delRefundOrder(corp_code, Integer.parseInt(bill_id));
                    }
                    logger.info("===================delCheck=="+result);
                    JSONObject result_obj = JSONObject.parseObject(result);
                    String code = result_obj.getString("code");
                    if (code.equals("0")) {

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
                        String function = "会员管理_审核管理";
                        String action = Common.ACTION_DEL;
                        String t_corp_code = corp_code;
                        String t_code = store_code;
                        String t_name = store_name;
                        String remark = bill_no;
                        baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                        //-------------------行为日志结束-----------------------------------------------------------------------------------

                        DBObject deleteRecord = new BasicDBObject();
                        deleteRecord.put("_id", ids[i]);
                        cursor.remove(deleteRecord);
                        logger.info("===================delCheck=="+ids[i]);

                    }else {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage(bill_no+result_obj.getString("message"));
                        return dataBean.getJsonStr();
                    }
                }
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
        dataBean.setId("1");
        dataBean.setMessage("删除成功");
        return dataBean.getJsonStr();
    }

    /**
     * 会员信息页面
     * 获取充值记录
     */
    @RequestMapping(value = "/getPayRecord", method = RequestMethod.POST)
    @ResponseBody
    public String getPayRecord(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int vip_id = jsonObject.getInteger("vip_id");
            String corp_code = jsonObject.get("corp_code").toString();

            String result1 = crmInterfaceService.getVipRechargeRecord(corp_code,vip_id);
            JSONObject result_obj = JSONObject.parseObject(result1);
            String code = result_obj.getString("code");
            if (code.equals("0")){
                JSONArray result_array = result_obj.getJSONArray("rows");
                JSONArray list = new JSONArray();
                for (int i = 0; i < result_array.size(); i++) {
                    JSONObject obj = result_array.getJSONObject(i);
                    JSONObject list_obj = new JSONObject();
                    list_obj.put("bill_no",obj.getString("DOCNO"));
                    list_obj.put("tag_price",obj.getString("VIP_PAYAMT"));
                    list_obj.put("pay_price",obj.getString("VIP_PAYAMT_ACTUAL"));
                    list_obj.put("modified_date",obj.getString("MODIFIEDDATE"));
                    String remark = obj.getString("DESCRIPTION");
                    if (remark.contains("充值")) {
                        list_obj.put("recharge_type","充值");
                    }else {
                        list_obj.put("recharge_type","退款");
                    }
                    list.add(list_obj);
                }
                JSONObject result = new JSONObject();
                result.put("list", list);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result.toString());
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage(result_obj.getString("message"));
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
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
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String lists = jsonObject.get("list").toString();
            int page_num = Integer.parseInt(jsonObject.get("pageNumber").toString());
            int page_size = Integer.parseInt(jsonObject.get("pageSize").toString());

            JSONArray array = JSONArray.parseArray(lists);
            BasicDBObject queryCondition = MongoHelperServiceImpl.andSignScreen(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_check);
            // 读取数据
            DBCursor dbCursor1 = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                dbCursor1 = cursor.find(queryCondition);
            } else if (role_code.equals(Common.ROLE_GM)){
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor1 = cursor.find(queryCondition1);
            }else{
                BasicDBList storeList = new BasicDBList();
                if (role_code.equals(Common.ROLE_BM)){
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                    for (int i = 0; i < stores.size(); i++) {
                        storeList.add(stores.get(i).getStore_code());
                    }
                }else if (role_code.equals(Common.ROLE_AM)){
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    String ares_store_code = request.getSession().getAttribute("store_code").toString();
                    ares_store_code = ares_store_code.replace(Common.SPECIAL_HEAD, "");

                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, "", "",ares_store_code);
                    for (int i = 0; i < stores.size(); i++) {
                        storeList.add(stores.get(i).getStore_code());
                    }
                }else if (role_code.equals(Common.ROLE_SM)){
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                    String[] stores = store_code.split(",");
                    for (int i = 0; i < stores.length; i++) {
                        storeList.add(stores[i]);
                    }
                }
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("store_code", new BasicDBObject("$in", storeList)));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                dbCursor1 = cursor.find(queryCondition1);
            }
            DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor1,page_num,page_size,"modified_date",-1);
            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);

            int count = list.size();
            int start_line = (page_num-1) * page_size + 1;
            int end_line = page_num*page_size;
            if (count < page_size)
                end_line = (page_num-1) * page_size +count;

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl_vipCheck(json, list, map, response, request,"审核管理("+start_line+"-"+end_line+")");
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
