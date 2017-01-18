package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.constant.CommonValueCorp;
import com.bizvane.ishop.entity.VipRules;
import com.bizvane.ishop.service.CRMInterfaceService;
import com.bizvane.ishop.service.VipRulesService;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor2,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"modified_date",-1);
            }
            ArrayList list = MongoUtils.dbCursorToList(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("pageNum", page_number);
            result.put("pageSize", page_size);


//            JSONArray array = new JSONArray();
//                JSONObject obj = new JSONObject();
//                obj.put("billNO","201701043423412");
//                obj.put("store_name","商帆演示一店");
//                obj.put("user_name","测试");
//                obj.put("vip_name","李小芳");
//                obj.put("cardno","21152345323");
//                obj.put("price","1000");
//                obj.put("pay_price","900");
//                obj.put("discount","0.9");
//                obj.put("date","2017-01-04");
//                obj.put("pay_type","直接充值");
//                obj.put("check_status","审核通过");
//                obj.put("check_type","充值审核");
//            array.add(obj);
//
//            JSONObject obj1 = new JSONObject();
//            obj1.put("billNO","201701033423412");
//            obj1.put("store_name","商帆演示一店");
//            obj1.put("user_name","测试");
//            obj1.put("vip_name","李小芳");
//            obj1.put("cardno","21152345323");
//            obj1.put("price","1000");
//            obj1.put("pay_price","900");
//            obj1.put("discount","0.9");
//            obj1.put("date","2017-01-04");
//            obj1.put("pay_type","退换转充值");
//            obj1.put("check_status","审核通过");
//            obj1.put("check_type","充值审核");
//            array.add(obj1);
//
//            JSONObject obj2 = new JSONObject();
//            obj2.put("billNO","201701033423412");
//            obj2.put("store_name","商帆演示一店");
//            obj2.put("user_name","测试");
//            obj2.put("vip_name","吴德秀");
//            obj2.put("cardno","3202112974444");
//            obj2.put("price","1000");
//            obj2.put("pay_price","900");
//            obj2.put("discount","0.9");
//            obj2.put("date","2017-01-04");
//            obj2.put("pay_type","退换转充值");
//            obj2.put("check_status","审核通过");
//            obj2.put("check_type","充值审核");
//            array.add(obj2);
//
//            JSONObject obj3 = new JSONObject();
//            obj3.put("billNO","201701033423412");
//            obj3.put("store_name","商帆演示一店");
//            obj3.put("user_name","测试");
//            obj3.put("vip_name","吴德秀");
//            obj3.put("cardno","3202112974444");
//            obj3.put("price","1000");
//            obj3.put("pay_price","900");
//            obj3.put("discount","0.9");
//            obj3.put("date","2017-01-04");
//            obj3.put("pay_type","退换转充值");
//            obj3.put("check_status","待审核");
//            obj3.put("check_type","充值审核");
//            array.add(obj3);
//
//            JSONObject obj4 = new JSONObject();
//            obj4.put("billNO","201701033423412");
//            obj4.put("store_name","商帆演示一店");
//            obj4.put("user_name","测试");
//            obj4.put("vip_name","李小芳");
//            obj4.put("cardno","21152345323");
//            obj4.put("price","1000");
//            obj4.put("pay_price","900");
//            obj4.put("discount","0.9");
//            obj4.put("date","2017-01-04");
//            obj4.put("pay_type","退换转充值");
//            obj4.put("check_status","拒绝");
//            obj4.put("check_type","充值审核");
//            array.add(obj4);
//
//            result.put("pages",1);
//            result.put("pageSize",page_size);
//            result.put("pageNum",page_number);
//            result.put("list", JSON.toJSONString(array));

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
//            BasicDBObject queryCondition = MongoUtils.andOperation(array);
            BasicDBObject queryCondition = MongoHelperServiceImpl.andSignScreen(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_check);

            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
            }
            ArrayList list = MongoUtils.dbCursorToList(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
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
                            obj.put("recharge_type", "退转转充值");
                    }
                    if (type.equals("退款")){
                        if (recharge_type.equals("VM"))
                            obj.put("recharge_type", "按充值单退款");
                        if (recharge_type.equals("BA"))
                            obj.put("recharge_type", "按余额退款");
                    }
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
//            String status = jsonObject.get("status").toString();
            String type = jsonObject.get("type").toString();
//            String billNo = jsonObject.get("billNo").toString();

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
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                cursor.update(updateCondition, updateSetValue);

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage("success");
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

//            JSONArray content = jsonObject.getJSONArray("content");

            String bill_id = id.split("_")[1];
            String corp_code =id.split("_")[0];

            HashMap<String,Object> map = new HashMap<String, Object>();
//            Iterator<String> it = jsonObject.keySet().iterator();
//            while (it.hasNext()) {
//                String key = it.next();
//                if (key == null) {
//                    continue;
//                }
//                Object value = jsonObject.get(key).toString().trim();
//                map.put(key, value);
//            }
            map.put("id",bill_id);

            DBObject updatedValue = new BasicDBObject();

            String result = "";
            if (type.equals("pay")){
                String bill_date = jsonObject.getString("bill_date");
                String store_name = jsonObject.getString("store_name");
                String user_name = jsonObject.getString("user_name");
                String recharge_type = jsonObject.getString("recharge_type");
                if (recharge_type.equals("直接充值"))
                    recharge_type = "1";
                if (recharge_type.equals("退换转充值"))
                    recharge_type = "1";
                String tag_price = jsonObject.getString("tag_price");
                String pay_price = jsonObject.getString("pay_price");
                String active_content = jsonObject.getString("active_content");
                String remark = jsonObject.getString("remark");

                map.put("bill_date",bill_date);
                map.put("store_name",store_name);
                map.put("user_name",user_name);
                map.put("recharge_type",recharge_type);
                map.put("tag_price",tag_price);
                map.put("pay_price",pay_price);
//                map.put("active_content",active_content);
                map.put("remark",remark);

                updatedValue.put("bill_date",bill_date);
                updatedValue.put("store_name",store_name);
                updatedValue.put("user_name",user_name);
                updatedValue.put("recharge_type",recharge_type);
                updatedValue.put("tag_price",tag_price);
                updatedValue.put("pay_price",pay_price);
                updatedValue.put("remark",remark);

                logger.info("-----"+JSON.toJSONString(map));
                result = crmInterfaceService.modPrepaidStatus(corp_code,map);
            }else if (type.equals("refund")){
                String bill_date = jsonObject.getString("bill_date");
                String store_name = jsonObject.getString("store_name");
                String user_name = jsonObject.getString("user_name");
                String recharge_type = jsonObject.getString("recharge_type");
                if (recharge_type.equals("按余额退款"))
                    recharge_type = "BA";
                if (recharge_type.equals("按充值单退款"))
                    recharge_type = "VM";
                String source_no = jsonObject.getString("source_no");
                String remark = jsonObject.getString("remark");

                map.put("bill_date",bill_date);
                map.put("store_name",store_name);
                map.put("user_name",user_name);
                map.put("recharge_type",recharge_type);
                map.put("source_no",source_no);
                map.put("remark",remark);

                updatedValue.put("bill_date",bill_date);
                updatedValue.put("store_name",store_name);
                updatedValue.put("user_name",user_name);
                updatedValue.put("recharge_type",recharge_type);
                updatedValue.put("source_no",source_no);
                updatedValue.put("remark",remark);
                logger.info("-----"+JSON.toJSONString(map));

                result = crmInterfaceService.modRefundStatus(corp_code,map);
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
     * 会员信息页面
     * 获取充值记录
     */
    @RequestMapping(value = "/getPayRecord", method = RequestMethod.POST)
    @ResponseBody
    public String getPayRecord(HttpServletRequest request) {
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
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();

            BasicDBObject dbObject = new BasicDBObject();
            dbObject.put("vip_id", vip_id);
            dbObject.put("corp_code", corp_code);
            dbObject.put("type", "pay");

            DBCursor dbCursor = cursor.find(dbObject);
            ArrayList list = new ArrayList();
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                String recharge_type = obj.get("recharge_type").toString();
                if (recharge_type.equals("1"))
                    obj.put("recharge_type", "直接充值");
                if (recharge_type.equals("2"))
                    obj.put("recharge_type", "退转转充值");
                list.add(obj.toMap());
            }
            JSONObject result = new JSONObject();
            result.put("list", list);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

//    /**
//     * 获取单据详情
//     */
//    @RequestMapping(value = "/showBill", method = RequestMethod.POST)
//    @ResponseBody
//    public String showBill(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String user_id = request.getSession().getAttribute("user_code").toString();
//        try {
//            String jsString = request.getParameter("param");
//            logger.info("json-select-------------" + jsString);
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = JSONObject.parseObject(message);
//            String id = jsonObject.get("id").toString();
//            String billNo = jsonObject.get("billNo").toString();
//            String pay_type = jsonObject.get("pay_type").toString();
//            String store_name = jsonObject.get("store_name").toString();
//            String user_name = jsonObject.get("user_name").toString();
//            String card_no = jsonObject.get("card_no").toString();
//
//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_check);
//            Map keyMap = new HashMap();
//            keyMap.put("_id", id);
//            BasicDBObject queryCondition = new BasicDBObject();
//            queryCondition.putAll(keyMap);
//            DBCursor dbCursor = cursor.find(queryCondition);
//            if (dbCursor.hasNext()){
//
//            }
//
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
}
