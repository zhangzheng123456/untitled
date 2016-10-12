package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.ishop.service.*;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/vipAnalysis")
public class VipAnalysisController {
    @Autowired
    UserService userService;
    @Autowired
    CorpService corpService;
    @Autowired
    StoreService storeService;
    @Autowired
    FeedbackService feedbackService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    VipGroupService vipGroupService;

    private static final Logger logger = Logger.getLogger(VipAnalysisController.class);

    String id;

    //会员列表
    @RequestMapping(value = "/allVip", method = RequestMethod.POST)
    @ResponseBody
    public String allVip(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            Map datalist = iceInterfaceService.vipBasicMethod(jsonObject,request);
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisAllVip", datalist);
            logger.info("-------vip列表" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            JSONObject obj = JSON.parseObject(result);
            String vipLists = obj.get("all_vip_list").toString();
            JSONArray array = JSONArray.parseArray(vipLists);
            JSONArray new_array = vipGroupService.findVipsGroup(array);
            obj.put("all_vip_list",new_array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(obj.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //新入会员
    @RequestMapping(value = "/vipNew", method = RequestMethod.POST)
    @ResponseBody
    public String vipNew(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String query_type = "daily";
            if (jsonObject.containsKey("query_type")){
                query_type = jsonObject.get("query_type").toString();
            }

            Map datalist = iceInterfaceService.vipAnalysisBasicMethod(jsonObject,request);
            Data data_query_type = new Data("query_type", query_type, ValueType.PARAM);
            datalist.put(data_query_type.key, data_query_type);

            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipNew", datalist);
            logger.info("-------AnalysisNewVip:" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //生日会员
    @RequestMapping(value = "/vipBirth", method = RequestMethod.POST)
    @ResponseBody
    public String vipBirth(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String query_type = "today";
            if (jsonObject.containsKey("query_type")){
                query_type = jsonObject.get("query_type").toString();
            }

            Map datalist = iceInterfaceService.vipAnalysisBasicMethod(jsonObject,request);
            Data data_query_type = new Data("query_type", query_type, ValueType.PARAM);
            datalist.put(data_query_type.key, data_query_type);

            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipBirth", datalist);
            logger.info("-------AnalysisBirthVip:" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //活跃会员
    @RequestMapping(value = "/vipSleep", method = RequestMethod.POST)
    @ResponseBody
    public String vipSleep(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String query_type = jsonObject.get("query_type").toString();

            Map datalist = iceInterfaceService.vipAnalysisBasicMethod(jsonObject,request);
            Data data_query_type = new Data("query_type", query_type, ValueType.PARAM);
            datalist.put(data_query_type.key, data_query_type);

            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisSleep", datalist);
            String result = dataBox.data.get("message").value;
            logger.info("----query_type: "+query_type+"---vipConsume:" + dataBox.data.get("message").value);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //消费排行
    @RequestMapping(value = "/vipConsume", method = RequestMethod.POST)
    @ResponseBody
    public String vipConsume(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String query_type = jsonObject.get("query_type").toString();

            DataBox dataBox = null;
            Map datalist = iceInterfaceService.vipAnalysisBasicMethod(jsonObject,request);
            if (query_type.equals("recent")){
                //最近消费
                dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipRecent", datalist);
            }else if (query_type.equals("freq")){
                //消费频率
                dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipFreq", datalist);
            }else if (query_type.equals("month")){
                //本月消费
                Data data_query_type = new Data("query_type", "1", ValueType.PARAM);
                datalist.put(data_query_type.key, data_query_type);
                dataBox = iceInterfaceService.iceInterfaceV2("AnlysisVipAmount", datalist);
            }else if (query_type.equals("three_month")){
                //前三月消费
                Data data_query_type = new Data("query_type", "2", ValueType.PARAM);
                datalist.put(data_query_type.key, data_query_type);
                dataBox = iceInterfaceService.iceInterfaceV2("AnlysisVipAmount", datalist);
            }else if (query_type.equals("history")){
                //历史总额
                Data data_query_type = new Data("query_type", "3", ValueType.PARAM);
                datalist.put(data_query_type.key, data_query_type);
                dataBox = iceInterfaceService.iceInterfaceV2("AnlysisVipAmount", datalist);
            }
            logger.info("----query_type: "+query_type+"---vipConsume:" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //vip消费占比
    @RequestMapping(value = "/vipScale", method = RequestMethod.POST)
    @ResponseBody
    public String vipScale(HttpServletRequest request) {
     DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);


            Date now = new Date();
            //String date_time="";
            String date_time = Common.DATETIME_FORMAT_DAY.format(now);
            if (jsonObject.containsKey("time") && !jsonObject.get("time").toString().equals("")) {
                date_time = jsonObject.get("time").toString();
            }
            String type = jsonObject.get("type").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_id = "";
            String area_code = "";
            String store_id = "";



            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            } else if (role_code.equals(Common.ROLE_GM)){
                if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").toString().trim().equals("")){
                    area_code = jsonObject.get("area_code").toString();
                }
            } else if (role_code.equals(Common.ROLE_AM) ){
                if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").toString().trim().equals("")){
                    area_code = jsonObject.get("area_code").toString();
                }else {
                    area_code = request.getSession().getAttribute("area_code").toString().replace(Common.SPECIAL_HEAD,"");
                    String[] area_codes = area_code.split(",");
                    area_code = area_codes[0];
                }
                if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").toString().trim().equals("")){
                    store_id = jsonObject.get("store_code").toString();
                }
            } else if (role_code.equals(Common.ROLE_SM)){
                if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").toString().trim().equals("")){
                    store_id = jsonObject.get("store_code").toString();
                }else {
                    String store_code = request.getSession().getAttribute("store_code").toString().replace(Common.SPECIAL_HEAD, "");
                    String[] store_codes = store_code.split(",");
                    store_id = store_codes[0];
                }
            } else if (role_code.equals(Common.ROLE_STAFF)){
                user_id = user_code;
                if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").toString().trim().equals("")){
                    store_id = jsonObject.get("store_code").toString();
                }
            }

            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
            Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
            Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);
            Data data_query_type=new Data("query_type", type, ValueType.PARAM);
            Data data_date_time=new Data("date_time",date_time, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_id.key, data_store_id);
            datalist.put(data_area_code.key, data_area_code);
           datalist.put(data_role_code.key, data_role_code);
            datalist.put(data_query_type.key, data_query_type);
            datalist.put(data_date_time.key, data_date_time);
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipCostDetail", datalist);
            logger.info("-------AnalysisNewVip:" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}