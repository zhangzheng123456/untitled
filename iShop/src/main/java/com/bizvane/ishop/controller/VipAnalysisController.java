package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.*;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            if (role_code.equals(Common.ROLE_SYS)){
                corp_code = jsonObject.get("corp_code").toString();
            }
            Map datalist = iceInterfaceService.vipBasicMethod1(page_num,page_size,corp_code,request);
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisAllVip", datalist);
            logger.info("------AnalysisAllVip-vip列表" + dataBox.status.toString());
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
                String freq_type = jsonObject.get("freq_type").toString();
                Data data_type = new Data("type", freq_type, ValueType.PARAM);
                datalist.put(data_type.key, data_type);
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
         //   logger.info("----query_type: "+query_type+"---vipConsume:" + dataBox.data.get("message").value);
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

            if (role_code.equals(Common.ROLE_AM)){
                store_id = jsonObject.get("store_code").toString().trim();
                area_code = jsonObject.get("area_code").toString().trim();
                String brand_code = jsonObject.get("brand_code").toString().trim();
                String area_store_code = "";
                if (store_id.equals("")){
                    if (area_code.equals("")){
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD,"");
                        area_store_code = request.getSession().getAttribute("store_code").toString();
                        area_store_code = area_store_code.replace(Common.SPECIAL_HEAD,"");
                    }
                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "",area_store_code);
                    for (int i = 0; i < storeList.size(); i++) {
                        store_id = store_id + storeList.get(i).getStore_code() + ",";
                    }
                }
            }else {
                if (role_code.equals(Common.ROLE_SYS)){
                    corp_code = jsonObject.get("corp_code").toString();
                }
                store_id = jsonObject.get("store_code").toString().trim();
                if (store_id.equals("")) {
                    area_code = jsonObject.get("area_code").toString().trim();
                    String brand_code = jsonObject.get("brand_code").toString().trim();
                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "","");
                    for (int i = 0; i < storeList.size(); i++) {
                        store_id = store_id + storeList.get(i).getStore_code() + ",";
                    }
                }
            }


            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
            Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
            Data data_query_type=new Data("query_type", type, ValueType.PARAM);
            Data data_date_time=new Data("date_time",date_time, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_id.key, data_store_id);
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

    //生日会员
    @RequestMapping(value = "/vipChart", method = RequestMethod.POST)
    @ResponseBody
    public String vipChart(HttpServletRequest request) {
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

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();

            String query_type = jsonObject.get("type").toString();
            if (query_type.equals("analysis")){
                String brand_code = jsonObject.get("brand_code").toString();
                String area_code = jsonObject.get("area_code").toString();
                String store_code = jsonObject.get("store_code").toString();
                String vip_group_code = jsonObject.get("vip_group_code").toString();
            }else if (query_type.equals("vipInfo")){
                String vip_id = jsonObject.get("vip_id").toString();
            }else if (query_type.equals("vipGroup")){
                String vip_group_code = jsonObject.get("vip_group_code").toString();
            }



            //月份
            JSONObject Month_obj = new JSONObject();
            JSONArray Month_amt_array = new JSONArray();
            JSONArray Month_num_array = new JSONArray();
            String month = "一月,二月,三月,四月,五月,六月,七月,八月,九月,十月,十一月,十二月";
            String[] months = month.split(",");
            for (int i = 0; i < months.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",months[i]);
                obj.put("value",i+30);
                Month_amt_array.add(obj);
            }
            for (int i = 0; i < months.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",months[i]);
                obj.put("value",i+2);
                Month_num_array.add(obj);
            }
            Month_obj.put("trade_amt",Month_amt_array);
            Month_obj.put("trade_num",Month_num_array);

            //周
            JSONObject Week_obj = new JSONObject();
            JSONArray Week_amt_array = new JSONArray();
            JSONArray Week_num_array = new JSONArray();

            String week = "周一,周二,周三,周四,周五,周六,周日";
            String[] weeks = week.split(",");
            for (int i = 0; i < weeks.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",weeks[i]);
                obj.put("value",i+30);
                Week_amt_array.add(obj);
            }
            for (int i = 0; i < weeks.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",weeks[i]);
                obj.put("value",i+2);
                Week_num_array.add(obj);
            }
            Week_obj.put("trade_amt",Week_amt_array);
            Week_obj.put("trade_num",Week_num_array);
            
            
            //时段
            JSONObject Time_obj = new JSONObject();
            JSONArray Time_amt_array = new JSONArray();
            JSONArray Time_num_array = new JSONArray();

            String time = "10,12,14,16,18,20,22";
            String[] times = time.split(",");
            for (int i = 0; i < times.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",times[i]);
                obj.put("value",i+30);
                Time_amt_array.add(obj);
            }
            for (int i = 0; i < times.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",times[i]);
                obj.put("value",i+2);
                Time_num_array.add(obj);
            }
            Time_obj.put("trade_amt",Time_amt_array);
            Time_obj.put("trade_num",Time_num_array);


            //消费类型
            JSONObject type_obj = new JSONObject();
            JSONArray type_amt_array = new JSONArray();
            JSONArray type_num_array = new JSONArray();

            String type = "类型1,类型2,类型3,类型4,类型5,类型6,类型7,其他";
            String[] types = type.split(",");
            for (int i = 0; i < types.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",types[i]);
                obj.put("value",i+30);
                type_amt_array.add(obj);
            }
            for (int i = 0; i < types.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",types[i]);
                obj.put("value",i+2);
                type_num_array.add(obj);
            }
            type_obj.put("trade_amt",type_amt_array);
            type_obj.put("trade_num",type_num_array);


            //系列
            JSONObject series_obj = new JSONObject();
            JSONArray series_amt_array = new JSONArray();
            JSONArray series_num_array = new JSONArray();

            String series = "系列1,系列2,系列3,系列4,系列5,其他";
            String[] seriess = series.split(",");
            for (int i = 0; i < seriess.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",seriess[i]);
                obj.put("value",i+30);
                series_amt_array.add(obj);
            }
            for (int i = 0; i < seriess.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",seriess[i]);
                obj.put("value",i+2);
                series_num_array.add(obj);
            }
            series_obj.put("trade_amt",series_amt_array);
            series_obj.put("trade_num",series_num_array);


            //价格
            JSONObject price_obj = new JSONObject();
            JSONArray price_amt_array = new JSONArray();
            JSONArray price_num_array = new JSONArray();

            String price = "200以下,200-500,500-800,800-1200,1200-2000,2000-4000,4000以上";
            String[] prices = price.split(",");
            for (int i = 0; i < prices.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",prices[i]);
                obj.put("value",i+30);
                price_amt_array.add(obj);
            }
            for (int i = 0; i < prices.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",prices[i]);
                obj.put("value",i+2);
                price_num_array.add(obj);
            }
            price_obj.put("trade_amt",price_amt_array);
            price_obj.put("trade_num",price_num_array);
            
            
            //地域
            JSONObject area_obj = new JSONObject();
            JSONArray area_amt_array = new JSONArray();
            JSONArray area_num_array = new JSONArray();

            String area = "北京,西安,重庆,四川,上海,南京,广州,深圳";
            String[] areas = area.split(",");
            for (int i = 0; i < areas.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",areas[i]);
                obj.put("value",i+30);
                area_amt_array.add(obj);
            }
            for (int i = 0; i < areas.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("name",areas[i]);
                obj.put("value",i+2);
                area_num_array.add(obj);
            }
            area_obj.put("trade_amt",area_amt_array);
            area_obj.put("trade_num",area_num_array);

       


            JSONObject chart_obj = new JSONObject();

            chart_obj.put("Month",Month_obj);
            chart_obj.put("Week",Week_obj);
            chart_obj.put("Time",Time_obj);
            chart_obj.put("Price",price_obj);
            chart_obj.put("Series",series_obj);
            chart_obj.put("Type",type_obj);
            chart_obj.put("Area",area_obj);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(chart_obj.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}