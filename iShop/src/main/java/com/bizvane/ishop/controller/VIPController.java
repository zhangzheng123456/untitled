package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/vip")
public class VIPController {

    private static final Logger logger = Logger.getLogger(VIPController.class);

    String id;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    VipLabelService vipLabelService;
    @Autowired
    VipParamService vipParamService;
    @Autowired
    StoreService storeService;
    @Autowired
    ParamConfigureService paramConfigureService;
    @Autowired
    CorpParamService corpParamService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    TableManagerService tableManagerService;
    @Autowired
    VipRulesService vipRulesService;
    @Autowired
    VipGroupService vipGroupService;
    /**
     * 新增会员信息
     */
    @RequestMapping(value = "/addVip", method = RequestMethod.POST)
    @ResponseBody
    public String addVip(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();

            String card_no = "";
            String vip_id = "";
            String phone = jsonObject.get("phone").toString();
            String vip_name = jsonObject.get("vip_name").toString();
            String vip_card_type = jsonObject.get("vip_card_type").toString();
            if (jsonObject.containsKey("card_no"))
                card_no = jsonObject.get("card_no").toString();
            String user_code = jsonObject.get("user_code").toString();
            String store_code = jsonObject.get("store_code").toString();
            String birthday = jsonObject.get("birthday").toString();
            String sex = jsonObject.get("sex").toString();
            JSONObject obj = new JSONObject();
            if (corp_code.equals("C10016")) {
                //调安正新增会员接口，返回会员卡号，vip_id
                obj.put("card_no", "14544423432898");
                obj.put("vip_id", "14544423432898");
            }
            //调毛伟栋新增接口
            iceInterfaceService.addNewVip(corp_code,vip_id,vip_name,sex,birthday,phone,vip_card_type,card_no,store_code,user_code);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(obj.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 更新会员信息
     */
    @RequestMapping(value = "/updateVip", method = RequestMethod.POST)
    @ResponseBody
    public String updateVip(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String phone = jsonObject.get("phone").toString();
            String card_no = jsonObject.get("card_no").toString();

            String vip_name = jsonObject.get("vip_name").toString();
            String birthday = jsonObject.get("birthday").toString();

            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_vip_card_no = new Data("vip_card_no", card_no, ValueType.PARAM);
            Data data_vip_phone = new Data("vip_phone", phone, ValueType.PARAM);
            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
            Data data_name = new Data("name", vip_name, ValueType.PARAM);
            Data data_birthday = new Data("birthday", birthday, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_vip_card_no.key, data_vip_card_no);
            datalist.put(data_vip_phone.key, data_vip_phone);
            datalist.put(data_vip_id.key, data_vip_id);
            datalist.put(data_name.key, data_name);
            datalist.put(data_birthday.key, data_birthday);

            DataBox dataBox = iceInterfaceService.iceInterfaceV3("VipProfileBackup", datalist);
            String status = dataBox.status.toString();
            if (status.equals("SUCCESS")) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("SUCCESS");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("FAILED");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员信息
     * 相册+标签
     */
    @RequestMapping(value = "/vipConsumCount", method = RequestMethod.POST)
    @ResponseBody
    public String vipConsumCount(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject obj = new JSONObject();
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();

//            List<VipAlbum> vipAlbumList = new ArrayList<VipAlbum>();
            List<VipLabel> vipLabelList = new ArrayList<VipLabel>();
            String album = "[]";
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);

            if (jsonObject.containsKey("type")) {
                if (jsonObject.get("type").equals("1")) {
                    //相册
//                    vipAlbumList = vipAlbumService.selectAlbumByVip(corp_code,vip_id);
                    BasicDBObject dbObject = new BasicDBObject();
                    dbObject.put("vip_id", vip_id);
                    dbObject.put("corp_code", corp_code);
                    DBCursor dbCursor = cursor.find(dbObject);

                    while (dbCursor.hasNext()) {
                        DBObject vip = dbCursor.next();
                        if (vip.containsField("album")) {
                            album = obj.get("album").toString();
                        }
                    }
                } else if (jsonObject.get("type").equals("2")) {
                    //标签
                    vipLabelList = vipLabelService.selectLabelByVip(corp_code, vip_id);
                }
            } else {
                //相册
//                vipAlbumList = vipAlbumService.selectAlbumByVip(corp_code, vip_id);
                BasicDBObject dbObject = new BasicDBObject();
                dbObject.put("vip_id", vip_id);
                dbObject.put("corp_code", corp_code);
                DBCursor dbCursor = cursor.find(dbObject);

                while (dbCursor.hasNext()) {
                    DBObject vip = dbCursor.next();
                    if (vip.containsField("album")) {
                        album = vip.get("album").toString();
                    }
                }
                //标签
                vipLabelList = vipLabelService.selectLabelByVip(corp_code, vip_id);
            }
            obj.put("Album", album);
            obj.put("Label", JSON.toJSONString(vipLabelList));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(obj.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员信息
     * 会员详细资料+扩展信息+备注
     */
    @RequestMapping(value = "/vipInfo", method = RequestMethod.POST)
    @ResponseBody
    public String vipInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();

            String extend_info = "";
            String remark = "";
            String avatar = "";


            JSONArray extend = new JSONArray();
            List<VipParam> vipParams = vipParamService.selectParamByCorp(corp_code);
            String cust_col = "";
            for (int i = 0; i < vipParams.size(); i++) {
                cust_col = cust_col + vipParams.get(i).getParam_name() + ",";
                JSONObject extend_obj = new JSONObject();
                extend_obj.put("name", vipParams.get(i).getParam_desc());
                extend_obj.put("key", vipParams.get(i).getParam_name());
                extend_obj.put("type", vipParams.get(i).getParam_type());
                extend_obj.put("values", vipParams.get(i).getParam_values());
                extend_obj.put("is_must", vipParams.get(i).getRequired());
                extend_obj.put("class", vipParams.get(i).getParam_class());
                extend_obj.put("show_order", vipParams.get(i).getShow_order());
                extend.add(extend_obj);
            }

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
            BasicDBObject dbObject = new BasicDBObject();
            dbObject.put("vip_id", vip_id);
            dbObject.put("corp_code", corp_code);
            DBCursor dbCursor = cursor.find(dbObject);

            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                if (obj.containsField("remark"))
                    remark = obj.get("remark").toString();
                if (obj.containsField("avatar"))
                    avatar = obj.get("avatar").toString();
            }

            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
            Data data_cust = new Data("cust_col", cust_col, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_vip_id.key, data_vip_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_cust.key, data_cust);

            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipDetail", datalist);
            String vip_info = dataBox.data.get("message").value;
            JSONObject vip = JSONObject.parseObject(vip_info);
            vip.put("vip_avatar", avatar);

            if (vip.get("custom") != null || !vip.get("custom").toString().equals("null"))
                extend_info = vip.get("custom").toString();
            JSONObject result = new JSONObject();
            result.put("list", vip);
            result.put("extend", extend);
            result.put("extend_info", extend_info);
            result.put("remark", remark);
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

    /**
     * 会员信息
     * 会员积分记录+衣橱
     */
    @RequestMapping(value = "/vipPoints", method = RequestMethod.POST)
    @ResponseBody
    public String vipPoints(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();

            String goods_code = "";
            String goods_name = "";
            String order_id = "";
            String time_start = "";
            String time_end = "";

            if (jsonObject.containsKey("goods_code")) {
                goods_code = jsonObject.get("goods_code").toString();
            }
            if (jsonObject.containsKey("goods_name")) {
                goods_name = jsonObject.get("goods_name").toString();
            }
            if (jsonObject.containsKey("order_id")) {
                order_id = jsonObject.get("order_id").toString();
            }
            if (jsonObject.containsKey("time_start")) {
                time_start = jsonObject.get("time_start").toString();
            }
            if (jsonObject.containsKey("time_end")) {
                time_end = jsonObject.get("time_end").toString();
            }
            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_goods_code = new Data("good_code", goods_code, ValueType.PARAM);
            Data data_goods_name = new Data("good_name", goods_name, ValueType.PARAM);
            Data data_order_id = new Data("order_id", order_id, ValueType.PARAM);
            Data data_time_start = new Data("time_start", time_start, ValueType.PARAM);
            Data data_time_end = new Data("time_end", time_end, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_vip_id.key, data_vip_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_goods_code.key, data_goods_code);
            datalist.put(data_goods_name.key, data_goods_name);
            datalist.put(data_order_id.key, data_order_id);
            datalist.put(data_time_start.key, data_time_start);
            datalist.put(data_time_end.key, data_time_end);

            JSONObject result_points = new JSONObject();
            JSONObject result_wardrobes = new JSONObject();

            if (jsonObject.containsKey("type")) {
                //获取积分列表
                if (jsonObject.get("type").equals("1")) {
//                    DataBox dataBox_points = iceInterfaceService.iceInterfaceV2("VipInfoScoreDetail", datalist);
//                    logger.info("-------VipInfoScoreDetail:" + dataBox_points.data.get("message").value);
//                    String points = dataBox_points.data.get("message").value;
//                    result_points = JSONObject.parseObject(points);
                } else if (jsonObject.get("type").equals("2")) {
                    //获取衣橱列表
                    DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipMoneyRecord", datalist);
                    logger.info("-------AnalysisVipMoneyRecord:" + dataBox.data.get("message").value);
                    String result = dataBox.data.get("message").value;
                    result_wardrobes = JSONObject.parseObject(result);
                }
            } else {
                //获取积分和衣橱信息
//                DataBox dataBox_points = iceInterfaceService.iceInterfaceV2("VipInfoScoreDetail", datalist);
//                logger.info("-------VipInfoScoreDetail:" + dataBox_points.data.get("message").value);
//                String points = dataBox_points.data.get("message").value;
//                result_points = JSONObject.parseObject(points);

                DataBox dataBox_wardrobes = iceInterfaceService.iceInterfaceV2("AnalysisVipMoneyRecord", datalist);
                logger.info("-------AnalysisVipMoneyRecord:" + dataBox_wardrobes.data.get("message").value);
                String wardrobes = dataBox_wardrobes.data.get("message").value;
                result_wardrobes = JSONObject.parseObject(wardrobes);
            }
            JSONArray list_wardrobe = result_wardrobes.getJSONArray("list_wardrobe");
            for (int i = 0; i < list_wardrobe.size(); i++) {
                JSONObject orders = list_wardrobe.getJSONObject(i);
                orders.put("use_points", "180");
                orders.put("get_points", "15");
                orders.put("discount", "满100减50");
            }
            result_wardrobes.put("", "");
            JSONObject result = new JSONObject();
            result.put("result_points", result_points);
            result.put("result_consumn", result_wardrobes);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表（目前只支持姓名，手机号，会员卡号）
     * 搜索
     */
    @RequestMapping(value = "/vipSearch", method = RequestMethod.POST)
    @ResponseBody
    public String vipSearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            String search_value = jsonObject.get("searchValue").toString();
            logger.info("json-----555555555555555555---------corp_code-" + corp_code);

            Map datalist = iceInterfaceService.vipBasicMethod2(page_num, page_size,corp_code, request);
            Data data_search_value = new Data("phone_or_id", search_value, ValueType.PARAM);
            datalist.put(data_search_value.key, data_search_value);
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipSearch3", datalist);
//            logger.info("-------VipSearch:" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表
     * 筛选
     */
    @RequestMapping(value = "/vipScreen", method = RequestMethod.POST)
    @ResponseBody
    public String vipScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            JSONArray screen = jsonObject.getJSONArray("screen");

            DataBox dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,page_num,page_size,request);
            if (dataBox.status.toString().equals("SUCCESS")){
                String result = dataBox.data.get("message").value;
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result);
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("筛选失败");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
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
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String screen_message = jsonObject.get("screen_message").toString();
            String searchValue = jsonObject.get("searchValue").toString().trim();

            String page_num = 1 + "";
            String page_size = 10000 + "";

            org.json.JSONObject jsonObj2 = new org.json.JSONObject(param);
            String output_message = jsonObj2.get("message").toString();
            org.json.JSONObject output_message_object = new org.json.JSONObject(output_message);

            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = "C10000";
            }
            logger.info("json--------------corp_code-" + corp_code);
            DataBox dataBox = null;
            if (!searchValue.equals("")) {

                Map datalist = iceInterfaceService.vipBasicMethod("1","10000",corp_code,request);
                Data data_search_value = new Data("phone_or_id", searchValue, ValueType.PARAM);
                datalist.put(data_search_value.key, data_search_value);
                dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipSearch2", datalist);

//                Data data_search_value = new Data("phone_or_id", searchValue, ValueType.PARAM);
//                datalist.put(data_search_value.key, data_search_value);
//                Data data_output_message = new Data("message", output_message, ValueType.PARAM);
//                datalist.put(data_output_message.key, data_output_message);
//                Data data_output_type = new Data("output_type", "search", ValueType.PARAM);
//                datalist.put(data_output_type.key, data_output_type);
//                dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipExportExecl", datalist);
            }else if(screen_message.equals("") && searchValue.equals("")){
                Map datalist = iceInterfaceService.vipBasicMethod("1","10000",corp_code,request);
                dataBox = iceInterfaceService.iceInterfaceV2("AnalysisAllVip", datalist);

//                Data data_output_message = new Data("message", output_message, ValueType.PARAM);
//                datalist.put(data_output_message.key, data_output_message);
//                Data data_output_type = new Data("output_type", "all", ValueType.PARAM);
//                datalist.put(data_output_type.key, data_output_type);
//                dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipExportExecl", datalist);
            }else if(!screen_message.equals("")){
                JSONObject jsonObject2 = JSONObject.parseObject(screen_message);
                String user_code = jsonObject2.get("user_code").toString();
                String store_code = jsonObject2.get("store_code").toString();
                String brand_code = jsonObject2.get("brand_code").toString();
                String area_code = jsonObject2.get("area_code").toString();
                dataBox = iceInterfaceService.vipScreenMethod(page_num, page_size, corp_code, area_code, brand_code, store_code, user_code);

              //  dataBox = iceInterfaceService.vipScreen2ExeclMethod(page_num, page_size, corp_code, area_code, brand_code, store_code, user_code, output_message);
            }
            //  logger.info("-------VipSearch:" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;
            org.json.JSONObject object = new org.json.JSONObject(result);
            org.json.JSONArray jsonArray = new org.json.JSONArray(object.get("all_vip_list").toString());
            List list = WebUtils.Json2List2(jsonArray);
            if (list.size() >= 10000) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(output_message_object);
            String pathname = OutExeclHelper.OutExecl_vip(jsonArray, list, map, response, request);
            //System.out.println("-----会员导出返回值-----"+object.toString());
            JSONObject result2 = new JSONObject();
//            if (object.toString().startsWith("数据异常")||object.toString().startsWith("导出数据过大") ) {
//                errormessage = object.toString();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result2.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result2.toString());
        }catch (Ice.MemoryLimitException im){
            System.out.println("===============ice异常========================================");
            errormessage = "导出数据过大,请筛选后导出";
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            im.printStackTrace();
        }catch (Exception ex) {
            System.out.println("===============总异常========================================");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员信息(头像，拓展信息，备注，相册)
     * 保存mongodb
     */
    @RequestMapping(value = "/vipSaveInfo", method = RequestMethod.POST)
    @ResponseBody
    public String vipSaveInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        Date now = new Date();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String card_no = jsonObject.get("card_no").toString();
            String phone = jsonObject.get("phone").toString();

            if (jsonObject.containsKey("extend") && !jsonObject.get("extend").toString().equals("")) {
                //扩展信息
                String extend = jsonObject.get("extend").toString();
                JSONObject extend_obj = JSONObject.parseObject(extend);
                Iterator<String> iter = extend_obj.keySet().iterator();
                JSONArray array = new JSONArray();
                while (iter.hasNext()) {
                    JSONObject obj = new JSONObject();
                    String name = iter.next();
                    String value = extend_obj.get(name).toString();
                    obj.put("column",name);
                    obj.put("value",value);
                    array.add(obj);
                }
                iceInterfaceService.saveVipExtendInfo(corp_code,vip_id,JSON.toJSONString(array));
            }else {
                MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
                DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
                Map keyMap = new HashMap();
                keyMap.put("_id", corp_code + vip_id);
                BasicDBObject queryCondition = new BasicDBObject();
                queryCondition.putAll(keyMap);
                DBCursor dbCursor1 = cursor.find(queryCondition);
                if (dbCursor1.size() > 0) {
                    //记录存在，更新
                    DBObject updateCondition = new BasicDBObject();
                    updateCondition.put("_id", corp_code + vip_id);

                    DBObject updatedValue = new BasicDBObject();
                    if (jsonObject.containsKey("remark")) {
                        //备注
                        String remark = jsonObject.get("remark").toString();
                        updatedValue.put("remark", remark);
                    }
                    if (jsonObject.containsKey("avatar") && !jsonObject.get("avatar").toString().equals("")) {
                        //头像
                        String avatar = jsonObject.get("avatar").toString();
                        updatedValue.put("avatar", avatar);
                    }
                    if (jsonObject.containsKey("image_url") && !jsonObject.get("image_url").toString().equals("")) {
                        //相册
                        DBObject obj = dbCursor1.next();
                        JSONArray array = new JSONArray();
                        if (obj.containsField("album")) {
                            String album = obj.get("album").toString();
                            array = JSON.parseArray(album);
                        }
                        String image_url = jsonObject.get("image_url").toString();
                        JSONObject image = new JSONObject();
                        image.put("image_url", image_url);
                        image.put("time", Common.DATETIME_FORMAT.format(now));
                        array.add(image);

                        updatedValue.put("album", array);
                    }
                    DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                    cursor.update(updateCondition, updateSetValue);
                } else {
                    //记录不存在，插入
                    DBObject saveData = new BasicDBObject();
                    saveData.put("_id", corp_code + vip_id);
                    saveData.put("vip_id", vip_id);
                    saveData.put("corp_code", corp_code);
                    saveData.put("card_no", card_no);
                    saveData.put("phone", phone);
                    saveData.put("corp_code", corp_code);
                    String remark = "";
                    String avatar = "";
                    JSONArray array = new JSONArray();
                    if (jsonObject.containsKey("remark")) {
                        remark = jsonObject.get("remark").toString();
                    }
                    if (jsonObject.containsKey("avatar")) {
                        avatar = jsonObject.get("avatar").toString();
                    }
                    if (jsonObject.containsKey("image_url")) {
                        String image_url = jsonObject.get("image_url").toString();
                        JSONObject image = new JSONObject();
                        image.put("image_url", image_url);
                        image.put("time", Common.DATETIME_FORMAT.format(now));
                        array.add(image);
                    }
                    saveData.put("remark", remark);
                    saveData.put("avatar", avatar);
                    saveData.put("album", array);
                    cursor.save(saveData);
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(Common.DATETIME_FORMAT.format(now));
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员列表，批量分配会员
     */
    @RequestMapping(value = "/changeVipsUser", method = RequestMethod.POST)
    @ResponseBody
    public String changeVipsUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String operator_id = request.getSession().getAttribute("user_code").toString();

        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String user_code = jsonObject.get("user_code").toString();
            String store_code = jsonObject.get("store_code").toString();

            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
            Data data_user_id = new Data("user_id", user_code, ValueType.PARAM);
            Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
            Data data_operator_id = new Data("operator_id", operator_id, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_vip_id.key, data_vip_id);
            datalist.put(data_store_code.key, data_store_code);
            datalist.put(data_operator_id.key, data_operator_id);

            DataBox dataBox = iceInterfaceService.iceInterfaceV2("VipAssort", datalist);
            logger.info("-------vip列表" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * MongoDB
     * 会员相册删除
     */
    @RequestMapping(value = "/vipAlbumDelete", method = RequestMethod.POST)
    @ResponseBody
    public String vipAlbumDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String time = jsonObject.get("time").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
            Map keyMap = new HashMap();
            keyMap.put("_id", corp_code + vip_id);
            BasicDBObject queryCondition = new BasicDBObject();
            queryCondition.putAll(keyMap);
            DBCursor dbCursor1 = cursor.find(queryCondition);
            if (dbCursor1.size() > 0) {
                DBObject obj = dbCursor1.next();
                String album = obj.get("album").toString();
                JSONArray array = JSONArray.parseArray(album);
                JSONArray new_array = new JSONArray();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject album_one = array.getJSONObject(i);
                    String time_one = album_one.get("time").toString();
                    if (!time_one.equals(time)) {
                        new_array.add(album_one);
                    }
                }
                DBObject updateCondition = new BasicDBObject();
                updateCondition.put("_id", corp_code + vip_id);
                DBObject updatedValue = new BasicDBObject();
                updatedValue.put("album", new_array);
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                cursor.update(updateCondition, updateSetValue);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表，批量导出会员相册
     */
    @RequestMapping(value = "/exportVipAlbums", method = RequestMethod.POST)
    @ResponseBody
    public String exportVipAlbums(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String vip = jsonObject.get("vip").toString();
            JSONArray vip_array = JSONArray.parseArray(vip);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);

            JSONArray array_album = new JSONArray();
            for (int i = 0; i < vip_array.size(); i++) {
                JSONObject vip_obj = vip_array.getJSONObject(i);
                String vip_id = vip_obj.get("vip_id").toString();
                String vip_name = vip_obj.get("vip_name").toString();
                String corp_code = vip_obj.get("corp_code").toString();
                String card_no = vip_obj.get("card_no").toString();
                String phone = vip_obj.get("phone").toString();

                Map keyMap = new HashMap();
                keyMap.put("_id", corp_code + vip_id);
                BasicDBObject queryCondition = new BasicDBObject();
                queryCondition.putAll(keyMap);
                DBCursor dbCursor1 = cursor.find(queryCondition);
                if (dbCursor1.hasNext()) {
                    DBObject obj = dbCursor1.next();
//                    String phone = obj.get("phone").toString();
//                    String card_no = obj.get("card_no").toString();
                    String album = "";
                    if (obj.containsField("album"))
                        album = obj.get("album").toString();
                    if (!album.equals("")) {
                        JSONArray array1 = JSONArray.parseArray(album);
                        for (int j = 0; j < array1.size(); j++) {
                            String image_url = array1.getJSONObject(j).getString("image_url");
                            String time = array1.getJSONObject(j).getString("time");
                            JSONObject obj_album = new JSONObject();
                            obj_album.put("vip_id", vip_id);
                            obj_album.put("vip_name", vip_name);
                            obj_album.put("card_no", card_no);
                            obj_album.put("phone", phone);
                            obj_album.put("image_url", image_url);
                            obj_album.put("time", time);
                            array_album.add(obj_album);
                        }
                    } else {
                        JSONObject obj_album = new JSONObject();
                        obj_album.put("vip_id", vip_id);
                        obj_album.put("vip_name", vip_name);
                        obj_album.put("card_no", card_no);
                        obj_album.put("phone", phone);
                        obj_album.put("image_url", "");
                        obj_album.put("time", "");
                        array_album.add(obj_album);
                    }
                } else {
                    JSONObject obj_album = new JSONObject();
                    obj_album.put("vip_id", vip_id);
                    obj_album.put("vip_name", vip_name);
                    obj_album.put("card_no", card_no);
                    obj_album.put("phone", phone);
                    obj_album.put("image_url", "");
                    obj_album.put("time", "");
                    array_album.add(obj_album);
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(array_album);
            if (array_album.size() >= 29999) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
//            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("vip_id", "会员编号");
            map.put("vip_name", "会员名称");
            map.put("card_no", "会员卡号");
            map.put("phone", "手机号");
            map.put("image_url", "相册图片地址");
            map.put("time", "图片上传时间");

            String pathname = OutExeclHelper.OutExecl(json, array_album, map, response, request);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /**
     * 充值或退款
     */
    @RequestMapping(value = "/recharge", method = RequestMethod.POST)
    @ResponseBody
    public String recharge(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
//        String user_code = request.getSession().getAttribute("user_code").toString();
//        String store_code = request.getSession().getAttribute("store_code").toString();

        Date now = new Date();
        String errormessage = "数据异常，操作失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String type = jsonObject.get("type").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String vip_id = jsonObject.get("vip_id").toString();
            String card_no = jsonObject.get("card_no").toString();//会员卡号
            String billNO = jsonObject.get("billNO").toString();//单据编号
            String remark = jsonObject.get("remark").toString();

            if (type.equals("pay")) {
                if (corp_code.equals("C10016")) {
                    String date = jsonObject.get("date").toString();//单据日期
                    String pay_type = jsonObject.get("pay_type").toString();//直接充值，退款转充值
                    String store_code = jsonObject.get("store_code").toString();//充值店仓
                    String user_code = jsonObject.get("user_code").toString();//经办人
                    String price = jsonObject.get("price").toString();//吊牌金额
                    String pay_price = jsonObject.get("pay_price").toString();//实付金额
                    String discount = jsonObject.get("discount").toString();//折扣

                }
            } else if (type.equals("refund")) {
                if (corp_code.equals("C10016")) {
                    String store_code = jsonObject.get("store_code").toString();
                    String refund_type = jsonObject.get("refund_type").toString();//充值单退款，余额退款
                    String sourceNo = jsonObject.get("sourceNo").toString();//实付金额

                }
            }

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /**
     * 参数控制
     */
    @RequestMapping(value = "/paramController", method = RequestMethod.GET)
    @ResponseBody
    public String paramController(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
//        String user_code = request.getSession().getAttribute("user_code").toString();
//        String store_code = request.getSession().getAttribute("store_code").toString();

        String errormessage = "数据异常，操作失败";
        try {
            String corp_code = request.getParameter("corp_code").toString();

            JSONObject obj = new JSONObject();
            String is_show_billNo = "Y";
            String is_show_cardNo = "N";

            ParamConfigure param = paramConfigureService.getParamByKey(CommonValue.ADD_VIP_CHECK_BILL, Common.IS_ACTIVE_Y);
            ParamConfigure param1 = paramConfigureService.getParamByKey(CommonValue.ADD_VIP_INPUT_CARDNO, Common.IS_ACTIVE_Y);

            String id = String.valueOf(param.getId());
            String id1 = String.valueOf(param1.getId());

            List<CorpParam> corpParams = corpParamService.selectByCorpParam(corp_code, id, Common.IS_ACTIVE_Y);
            List<CorpParam> corpParams1 = corpParamService.selectByCorpParam(corp_code, id1, Common.IS_ACTIVE_Y);

            if (corpParams.size() > 0 && corpParams.get(0).getParam_value().equals("N"))
                is_show_billNo = "N";

            if (corpParams1.size() > 0 && corpParams1.get(0).getParam_value().equals("Y"))
                is_show_cardNo = "Y";
            obj.put("is_show_billNo", is_show_billNo);
            obj.put("is_show_cardNo", is_show_cardNo);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(obj.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 验证单号
     * 获取余额
     */
    @RequestMapping(value = "/checkBillNo", method = RequestMethod.POST)
    @ResponseBody
    public String checkBillNo(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();

        Date now = new Date();
        String errormessage = "数据异常，操作失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String type = jsonObject.get("type").toString();
            String corp_code = jsonObject.get("corp_code").toString();

            JSONObject obj = new JSONObject();



            if (type.equals("billNo")){
//                if (corp_code.equals("C10016")){
                    String billNo = jsonObject.get("billNo").toString();//单据编号
                    obj.put("can_pass","N");
                    obj.put("price","100");
                    obj.put("pay_price","80");
//                }
            }else if (type.equals("balances")){
//                if (corp_code.equals("C10016")){
                    String vip_id = jsonObject.get("vip_id").toString();
                    obj.put("balance","450");
//                }

            }

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(obj.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员信息，会员升/降级
     */
    @RequestMapping(value = "/changeVipType", method = RequestMethod.POST)
    @ResponseBody
    public String changeVipType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String operator_id = request.getSession().getAttribute("user_code").toString();

        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String type = jsonObject.get("type").toString();
            String vip_card_type = jsonObject.get("vip_card_type").toString();
            String high_vip_type = jsonObject.get("high_vip_type").toString();

            VipRules vipRules = vipRulesService.getVipRulesByType(corp_code,vip_card_type,high_vip_type,Common.IS_ACTIVE_Y);
            if (vipRules != null){
                if (type.equals("upgrade")){
                     high_vip_type = vipRules.getHigh_vip_type();
                    DataBox dataBox = iceInterfaceService.changeVipType(corp_code,vip_id, high_vip_type);
                    if (dataBox.status.toString().equals("SUCCESS")){
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setId("1");
                        dataBean.setMessage("SUCCESS");
                    }else {
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setId("1");
                        dataBean.setMessage("升级失败");
                    }
                }
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("该会员已是最高级别会员");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * MongoDB
     * 会员相册删除
     */
    @RequestMapping(value = "/avorites", method = RequestMethod.POST)
    @ResponseBody
    public String avoritesMethod(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String open_id = jsonObject.get("open_id").toString();
            String vip_card_no = jsonObject.get("vip_card_no").toString();

            Data data_row_num = new Data("row_num","100" , ValueType.PARAM);
            Data data_corp_code = new Data("corp_code",corp_code , ValueType.PARAM);
            Data data_open_id = new Data("open_id", open_id, ValueType.PARAM);
            Data data_vip_card_no = new Data("vip_card_no", vip_card_no, ValueType.PARAM);
            Data data_type= new Data("type", "1", ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_row_num.key, data_row_num);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_open_id.key, data_open_id);
            datalist.put(data_vip_card_no.key, data_vip_card_no);
            datalist.put(data_type.key, data_type);

            DataBox dataBox = iceInterfaceService.iceInterfaceV3("Favorites", datalist);
            String result = dataBox.data.get("message").value;
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
