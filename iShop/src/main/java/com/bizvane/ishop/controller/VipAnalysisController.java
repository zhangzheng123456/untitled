package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/vipAnalysis")
public class VipAnalysisController {
    @Autowired
    StoreService storeService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    VipService vipService;
    @Autowired
    BrandService brandService;

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
            JSONObject jsonObject = jsonObj.getJSONObject("message");
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code = request.getSession().getAttribute("area_code").toString();
            String store_code = request.getSession().getAttribute("store_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            if (role_code.equals(Common.ROLE_SYS)){
                corp_code = jsonObject.get("corp_code").toString();
            }
            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }

            DataBox dataBox = vipGroupService.vipScreenBySolr(new JSONArray(),corp_code,page_num,page_size,role_code,brand_code,area_code,store_code,user_code,sort_key,sort_value);
            logger.info("------AnalysisAllVip-vip列表" +corp_code+ dataBox.status.toString());
            if ( !dataBox.status.toString().equals("SUCCESS")){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("网络异常，请稍后重试");
                return dataBean.getJsonStr();
            }
            String result = dataBox.data.get("message").value;
            String field = "";
            if (jsonObject.containsKey("field")){
                field = jsonObject.getString("field");
            }
            if (field.equals("fsend")){
                result = vipService.vipLastSendTime(corp_code,result);
            }else {
                result = vipService.vipAvatar(corp_code,result);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage("获取数据失败");
        }
        return dataBean.getJsonStr();
    }

    //==============================会员分析==================================

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
//            logger.info("-------AnalysisNewVip:" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
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
//            logger.info("-------AnalysisBirthVip:" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
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

            if (jsonObject.containsKey("time")){
                Data data_time = new Data("time", jsonObject.get("time").toString(), ValueType.PARAM);
                datalist.put(data_time.key, data_time);
            }

            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisSleep", datalist);
            String result = dataBox.data.get("message").value;
//            logger.info("----query_type: "+query_type+"---vipConsume:" + dataBox.data.get("message").value);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //活跃会员占比
    @RequestMapping(value = "/vipSleepRate", method = RequestMethod.POST)
    @ResponseBody
    public String vipSleepRate(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            Map datalist = iceInterfaceService.vipAnalysisBasicMethod(jsonObject,request);

            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisSleepRate", datalist);
            String result = dataBox.data.get("message").value;
//            logger.info("----query_type: "+query_type+"---vipConsume:" + dataBox.data.get("message").value);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
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
                dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipFreqV2", datalist);
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
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //新老vip消费占比
    @RequestMapping(value = "/vipScale", method = RequestMethod.POST)
    @ResponseBody
    public String vipScale(HttpServletRequest request) {
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

            Date now = new Date();
            String date_time = Common.DATETIME_FORMAT_DAY.format(now);
            if (jsonObject.containsKey("time") && !jsonObject.get("time").toString().equals("")) {
                date_time = jsonObject.get("time").toString();
            }
            String type = jsonObject.get("type").toString();

            String user_code = jsonObject.get("user_code").toString().trim();
            String store_code = jsonObject.get("store_code").toString().trim();
            String area_code = jsonObject.get("area_code").toString().trim();
            String brand_code = jsonObject.get("brand_code").toString().trim();

            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)){
                if (store_code.equals("") && user_code.equals("") && !area_code.equals("")) {
                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "","");
                    for (int i = 0; i < storeList.size(); i++) {
                        store_code = store_code + storeList.get(i).getStore_code() + ",";
                    }
                }
            } else if (role_code.equals(Common.ROLE_AM)){
                String area_store_code = "";
                if (store_code.equals("") && user_code.equals("")){
                    if (area_code.equals("")){
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD,"");
                        area_store_code = request.getSession().getAttribute("store_code").toString();
                        area_store_code = area_store_code.replace(Common.SPECIAL_HEAD,"");
                    }
                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "",area_store_code);
                    for (int i = 0; i < storeList.size(); i++) {
                        store_code = store_code + storeList.get(i).getStore_code() + ",";
                    }
                }
            }else if (role_code.equals(Common.ROLE_BM)){
                if (store_code.equals("") && user_code.equals("")) {
                    if (brand_code.equals("")){
                        brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
                    }
                    if (area_code.equals("")){
                        area_code = request.getSession().getAttribute("area_code").toString();
                        area_code = area_code.replace(Common.SPECIAL_HEAD,"");
                    }

                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "","");
                    for (int i = 0; i < storeList.size(); i++) {
                        store_code = store_code + storeList.get(i).getStore_code() + ",";
                    }

                }
            }else if (role_code.equals(Common.ROLE_SM)){
                if (store_code.equals("") && user_code.equals("")) {
                    store_code = request.getSession().getAttribute("store_code").toString().replace(Common.SPECIAL_HEAD, "");
                }
            }else if (role_code.equals(Common.ROLE_STAFF)){
                user_code = request.getSession().getAttribute("user_code").toString();
            }
            if (!brand_code.equals("")){
                Brand brand = brandService.getBrandByCode(corp_code,brand_code,Common.IS_ACTIVE_Y);
                if (brand != null && brand.getBrand_id() != null && !brand.getBrand_id().equals("")){
                    brand_code = brand.getBrand_id();
                }
            }
            DataBox dataBox = iceInterfaceService.analysisVipCostDetail(corp_code,brand_code,area_code,store_code,user_code,type, date_time);
            logger.info("-------analysisVipCostDetail:" + dataBox.status.toString());
            if (dataBox.status.toString().equals("SUCCESS")){
                String result = dataBox.data.get("message").value;
                result = vipService.numberFormat(result);

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("分析失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //会员图表获取，排序
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
            String year = jsonObject.get("year").toString();

            String result="";
            DataBox dataBox = new DataBox();
            String brand_code1 = "";
            if (query_type.equals("analysis")){
                //会员分析
                String store_label = jsonObject.get("store_label").toString();

                String area_code = "";
                String store_id = "";
                if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
                    store_id = jsonObject.get("store_code").toString().trim();
                    area_code = jsonObject.get("area_code").toString().trim();
                    String brand_code = jsonObject.get("brand_code").toString().trim();
                    if (store_id.equals("")) {
                        if (!area_code.equals("") || !brand_code.equals("")){
                            List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "","");
                            for (int i = 0; i < storeList.size(); i++) {
                                store_id = store_id + storeList.get(i).getStore_code() + ",";
                            }
                            if (!brand_code.isEmpty()){
                                brand_code1 = brand_code;
                            }
                        }

                    }
                }else if (role_code.equals(Common.ROLE_AM)){
                    store_id = jsonObject.get("store_code").toString().trim();
                    area_code = jsonObject.get("area_code").toString().trim();
                    String brand_code = jsonObject.get("brand_code").toString().trim();
                    if (!brand_code.isEmpty()){
                        brand_code1 = brand_code;
                    }
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
                }else if (role_code.equals(Common.ROLE_BM)){
                    store_id = jsonObject.get("store_code").toString().trim();
                    area_code = jsonObject.get("area_code").toString().trim();
                    String brand_code = jsonObject.get("brand_code").toString().trim();
                    if (!brand_code.isEmpty()){
                        brand_code1 = brand_code;
                    }
                    if (store_id.equals("")){
                        if (brand_code.equals("")){
                            brand_code = request.getSession().getAttribute("brand_code").toString();
                            brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
                        }
                        if (area_code.equals("")){
                            area_code = request.getSession().getAttribute("area_code").toString();
                            area_code = area_code.replace(Common.SPECIAL_HEAD,"");
                        }
                        List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "","");
                        for (int i = 0; i < storeList.size(); i++) {
                            store_id = store_id + storeList.get(i).getStore_code() + ",";
                        }
                    }
                }else {
                    store_id = jsonObject.get("store_code").toString().trim();
                    if (store_id.equals("") ) {
                        store_id = request.getSession().getAttribute("store_code").toString().replace(Common.SPECIAL_HEAD, "");
                    }
                }
                if (corp_code.equals("C10222") && !store_id.isEmpty()){
                    Store store = storeService.getStoreByCode(corp_code,store_id.split(",")[0],Common.IS_ACTIVE_Y);
                    if (store != null && store.getBrand_code() != null && !store.getBrand_code().isEmpty())
                        brand_code1 = store.getBrand_code().replace(Common.SPECIAL_HEAD,"").split(",")[0];
                }

                dataBox = iceInterfaceService.storeSearchForWeb(corp_code,store_id,store_label,brand_code1,year);
                if (dataBox.status.toString().equals("SUCCESS")) {
                    result = dataBox.data.get("message").value;
                    if (result.equals("{}")){
                        result = "";
                    }else{
                        JSONObject object = JSONObject.parseObject(result);
                        object = vipService.pareseData1(object);
                        result = object.toString();
                    }
                }
            }else if (query_type.equals("vipInfo")){
                //会员档案
                String vip_id = jsonObject.get("vip_id").toString();
                dataBox = iceInterfaceService.vipTagSearchForWeb(corp_code, vip_id, "",year,"","");
                if (dataBox.status.toString().equals("SUCCESS")) {
//                    logger.info("----------vipChart:" + dataBox.data.get("message").value);
                    result = dataBox.data.get("message").value;
                    JSONObject object = JSON.parseObject(result);
                    JSONObject object1 = object.getJSONObject("message");
                    object1 = vipService.pareseData1(object1);

                    object.put("message",object1);
                    result = object.toString();
                }
            }else if(query_type.equals("allCard")){
                //会员档案(所有卡)
                String vip_phone = jsonObject.getString("vip_phone");
                String vip_id=jsonObject.getString("vip_id");
                dataBox = iceInterfaceService.vipTagSearchForWeb(corp_code, vip_id, "",year,"",vip_phone);
                if (dataBox.status.toString().equals("SUCCESS")) {
                    result = dataBox.data.get("message").value;
                    JSONObject object = JSON.parseObject(result);
                    JSONObject object1 = object.getJSONObject("message");
                    object1 = vipService.pareseData1(object1);
                    object.put("message",object1);
                    result = object.toString();
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

//=============================未分配会员===================================

    //未分配会员报表
    @RequestMapping(value = "/vipUnAssort", method = RequestMethod.POST)
    @ResponseBody
    public String vipUnAssort(HttpServletRequest request) {
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

            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";
            JSONObject result = new JSONObject();
            PageInfo<Store> list = new PageInfo<Store>();
            list.setList(new ArrayList<Store>());
            if (!jsonObject.containsKey("list") && jsonObject.containsKey("searchValue")){
                String search_value = jsonObject.get("searchValue").toString();
                if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
                    //系统管理员
                    list = storeService.getAllStore1(request, page_number, page_size, corp_code, search_value, Common.IS_ACTIVE_Y, "","","","","All");
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    if (brand_code == null || brand_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属品牌");
                        return dataBean.getJsonStr();
                    } else {
                        //加上特殊字符，进行查询
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                        String[] brandCodes = brand_code.split(",");
                        for (int i = 0; i < brandCodes.length; i++) {
                            brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
                        }
                        list = storeService.selectByAreaBrand(page_number, page_size, corp_code, null, null, brandCodes, search_value, Common.IS_ACTIVE_Y, "","","","","All");
                    }
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    if (area_code == null || area_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属区域");
                        return dataBean.getJsonStr();
                    } else {
                        //加上特殊字符，进行查询
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        String[] areaCodes = area_code.split(",");
                        String[] storeCodes = null;
                        for (int i = 0; i < areaCodes.length; i++) {
                            areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
                        }
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCodes = store_code.split(",");
                        }
                        if (!storeCodes.equals(""))
                            list = storeService.selectByAreaBrand(page_number, page_size, corp_code, areaCodes, storeCodes, null, search_value, Common.IS_ACTIVE_Y, "","","","","All");
                    }
                } else if (role_code.equals(Common.ROLE_SM)) {
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    if (store_code == null || store_code.equals("")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage("您还没有所属店铺");
                        return dataBean.getJsonStr();
                    } else {
                        list = storeService.selectByUserId(page_number, page_size, store_code, corp_code, search_value,Common.IS_ACTIVE_Y);
                    }
                } else {
                    List<Store> list1 = new ArrayList<Store>();
                    list.setList(list1);
                }
            }else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
                    list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, "", "", "", map, "", Common.IS_ACTIVE_Y);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, area_codes, brand_code, "", map, "", Common.IS_ACTIVE_Y);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, area_codes, "", "", map, store_code, Common.IS_ACTIVE_Y);
                } else {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeService.getAllStoreScreen1(page_number, page_size, corp_code, "", "", store_code, map, "", Common.IS_ACTIVE_Y);
                }
            }

            long total = list.getTotal();
            int pages = list.getPages();
            List<Store> stores = list.getList();
            String store_code = "";
            for (int i = 0; i < stores.size(); i++) {
                store_code = store_code + stores.get(i).getStore_code() + ",";
            }
            DataBox dataBox = iceInterfaceService.getUnAssortVip(corp_code,store_code);
            if (dataBox.status.toString().equals("SUCCESS")){
                JSONObject msg_obj = JSONObject.parseObject(dataBox.data.get("message").value);
                JSONArray array = msg_obj.getJSONArray("message");
                JSONArray store_array = new JSONArray();
                for (int i = 0; i < stores.size(); i++) {
                    Store store = stores.get(i);
                    JSONObject store_obj = WebUtils.bean2JSONObject(store);

                    store_obj.put("vip_count","0");
                    store_obj.put("assort_count","0");
                    store_obj.put("unassort_count","0");
                    store_code = store.getStore_code();
                    for (int j = 0; j < array.size(); j++) {
                        String store_code1 = array.getJSONObject(i).getString("store_code");
                        if (store_code.equals(store_code1)){
                            String vip_count = array.getJSONObject(i).getString("vip_count");
                            String unassort_count = array.getJSONObject(i).getString("unassort_count");
                            String assort_count = array.getJSONObject(i).getString("assort_count");

                            store_obj.put("vip_count",vip_count);
                            store_obj.put("assort_count",assort_count);
                            store_obj.put("unassort_count",unassort_count);
                        }
                    }
                    store_array.add(store_obj);
                }
                JSONObject obj = new JSONObject();
                obj.put("pageSize",page_size);
                obj.put("pageNum",page_number);
                obj.put("total",total);
                obj.put("pages",pages);
                obj.put("list",store_array);

                result.put("list", obj.toString());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result.toString());
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("获取数据失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/exportExecl2View", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        String pathname="";
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if(jsonObject.containsKey("birth_vip_num")||jsonObject.containsKey("sex_vip_num")){
                JSONArray jsonarray=new JSONArray();
                if(jsonObject.containsKey("birth_vip_num")){
                    jsonarray=jsonObject.getJSONArray("birth_vip_num");
                }
                if(jsonObject.containsKey("sex_vip_num")){
                    jsonarray=jsonObject.getJSONArray("sex_vip_num");
                }
                ArrayList list_title = new ArrayList();
                ArrayList list_value_amt = new ArrayList();
                ArrayList list_value_num = new ArrayList();
                ArrayList list_value_discount = new ArrayList();
                list_title.add("类型");
                list_value_amt.add("人数");
                for (int i = 0; i < jsonarray.size(); i++) {
                    JSONObject jsonObject1=jsonarray.getJSONObject(i);
                    String name = jsonObject1.get("name").toString();
                    list_title.add(name);
                    String value = jsonObject1.get("value").toString();
                    list_value_amt.add(value);
                }
                pathname = OutExeclHelper.OutExecl_view(list_title, list_value_amt, list_value_num, list_value_discount, new ArrayList<List<String>>(), request,"图表");
            }else{
                ArrayList list_title = new ArrayList();
                ArrayList list_value_amt = new ArrayList();
                ArrayList list_value_num = new ArrayList();
                ArrayList list_value_discount = new ArrayList();
                ArrayList list_stroke_num=new ArrayList();
                ArrayList list_trade_price=new ArrayList();
                ArrayList list_relate_rate=new ArrayList();
                ArrayList list_size_array=new ArrayList();

                String trade_amt = jsonObject.get("trade_amt").toString();
                JSONArray jsonArray_amt = JSON.parseArray(trade_amt);

                String discount = jsonObject.get("discount").toString();
                JSONArray jsonArray_discount = JSON.parseArray(discount);

                String trade_num = jsonObject.get("trade_num").toString();
                JSONArray jsonArray_num = JSON.parseArray(trade_num);

                JSONArray jsonArray_stroke_num=new JSONArray();
                JSONArray jsonArray_trade_price=new JSONArray();
                JSONArray jsonArray_relate_rate=new JSONArray();
                JSONArray  jsonArray_size_array =new JSONArray();
                if(jsonObject.containsKey("stroke_num")){
                    String stroke_num = jsonObject.get("stroke_num").toString();//消费笔数
                    jsonArray_stroke_num = JSON.parseArray(stroke_num);
                }

                if(jsonObject.containsKey("trade_price")){
                    String trade_price = jsonObject.get("trade_price").toString();//客单价
                     jsonArray_trade_price= JSON.parseArray(trade_price);
                }

                if(jsonObject.containsKey("relate_rate")){
                    String relate_rate = jsonObject.get("relate_rate").toString();//连带率
                    jsonArray_relate_rate = JSON.parseArray(relate_rate);
                }

                if(jsonObject.containsKey("size_array")){
                    String size_array = jsonObject.get("size_array").toString();//数量
                    jsonArray_size_array = JSON.parseArray(size_array);
                }

                list_title.add("类型");
                list_value_amt.add("金额");
                list_value_num.add("件数");
                list_value_discount.add("折扣");

                if(jsonArray_stroke_num.size()>0){
                    list_stroke_num.add("笔数");
                    for (int i = 0; i <jsonArray_stroke_num.size() ; i++) {
                       list_stroke_num.add(jsonArray_stroke_num.getJSONObject(i).getString("value"));
                    }
                }
                if(jsonArray_size_array.size()>0){
                    list_size_array.add("人数");
                    for (int i = 0; i <jsonArray_size_array.size() ; i++) {
                        list_size_array.add(jsonArray_size_array.getJSONObject(i).getString("value"));
                    }
                }
                if(jsonArray_trade_price.size()>0){
                    list_trade_price.add("客单价");
                    for (int i = 0; i <jsonArray_trade_price.size() ; i++) {
                        list_trade_price.add(jsonArray_trade_price.getJSONObject(i).getString("value"));
                    }
                }
                if(jsonArray_relate_rate.size()>0){
                    list_relate_rate.add("连带率");
                    for (int i = 0; i < jsonArray_relate_rate.size(); i++) {
                        list_relate_rate.add(jsonArray_relate_rate.getJSONObject(i).getString("value"));
                    }
                }

                for (int i = 0; i < jsonArray_amt.size(); i++) {
                    JSONObject object_amt = JSON.parseObject(jsonArray_amt.get(i).toString());
                    JSONObject object_num = JSON.parseObject(jsonArray_num.get(i).toString());
                    JSONObject object_discount = JSON.parseObject(jsonArray_discount.get(i).toString());

                    String name = object_amt.get("name").toString();
                    list_title.add(name);
                    String value = object_amt.get("value").toString();
                    list_value_amt.add(value);

                    String value_num = object_num.get("value").toString();
                    list_value_num.add(value_num);

                    String value_dis = object_discount.get("value").toString();
                    list_value_discount.add(value_dis);
                }
                List<List<String>> all_list=new ArrayList<List<String>>();
                if(list_stroke_num.size()>0){
                    all_list.add(list_stroke_num);
                }
                if(list_size_array.size()>0){
                    all_list.add(list_size_array);
                }
                if(list_trade_price.size()>0){
                   all_list.add(list_trade_price);
                }
                if(list_relate_rate.size()>0){
                    all_list.add(list_relate_rate);
                }
                 pathname = OutExeclHelper.OutExecl_view(list_title, list_value_amt, list_value_num, list_value_discount,all_list, request,"图表");
            }

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
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            ex.printStackTrace();

        }
        return dataBean.getJsonStr();
    }


    //================================================================

    @RequestMapping(value ="/empKpi",method = RequestMethod.POST)
    @ResponseBody
    public  String getEmpKpiReport(HttpServletRequest request){
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
            String page_number = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            String store_name = "";
            String user_id = "";
            String user_name = "";
            String start_time = "";
            String end_time = "";

            //筛选条件
            String screen=jsonObject.get("list").toString();
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if (screenkey.equals("store_name")) {
                        store_name = jsonObject1.getString("screen_value");
                    } else if (screenkey.equals("user_code")) {
                        user_id = jsonObject1.getString("screen_value");
                    } else if (screenkey.equals("user_name")) {
                        user_name = jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("start_time")){
                        String time=jsonObject1.getString("screen_value");
                        JSONObject object=JSON.parseObject(time);
                        start_time=object.getString("start");
                        end_time=object.getString("end");
                    }
                }
            }

            DataBox dataBox = iceInterfaceService.getEmpKpiReport(corp_code,store_name,user_id,user_name,start_time,end_time,page_size,page_number);
            if (dataBox.status.toString().equals("SUCCESS")){
                String value = dataBox.data.get("message").value;
                JSONObject obj = JSON.parseObject(value);
                obj.put("page_size",page_size);
                obj.put("page_num",page_number);
                String  message1= obj.getString("user_list");
                JSONArray jsonArray=JSON.parseArray(message1);
                jsonArray = vipService.vipAnalysisTime(jsonArray,start_time,end_time);
                obj.put("user_list",jsonArray);

                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(obj.toString());
            }else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("获取数据失败");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/storeKpi", method = RequestMethod.POST)
    @ResponseBody
    public String getStoreKpiReport(HttpServletRequest request){
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

            String page_number = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            String store_code="";
            String store_name = "";
            String sell_area = "";
            String store_group = "";
            String start_time = "";
            String end_time = "";

            //筛选条件
            String screen=jsonObject.get("list").toString();
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if (screenkey.equals("store_area")) {
                        sell_area = jsonObject1.getString("screen_value");
                    } else if (screenkey.equals("store_group")) {
                        store_group = jsonObject1.getString("screen_value");
                    }else  if(screenkey.equals("store_name")){
                        store_name=jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("store_id")){
                        store_code=jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("start_time")){
                        String time=jsonObject1.getString("screen_value");
                        JSONObject object=JSON.parseObject(time);
                        start_time=object.getString("start");
                        end_time=object.getString("end");
                    }
                }
            }

            System.out.println("sell_area..."+sell_area+"store_group...."+store_group);

            DataBox dataBox = iceInterfaceService.getStoreKpiReport(corp_code,store_code,store_name,store_group,sell_area,start_time,end_time,page_size,page_number);
            if (dataBox.status.toString().equals("SUCCESS")){
                String value = dataBox.data.get("message").value;
                JSONObject obj = JSON.parseObject(value);
                obj.put("page_size",page_size);
                obj.put("page_num",page_number);
                JSONArray message_array = obj.getJSONArray("message");
                message_array = vipService.vipAnalysisTime(message_array,start_time,end_time);
                obj.put("message",message_array);

                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(obj.toString());
            }else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("获取数据失败");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    //搜索接口

    @RequestMapping(value ="/piKSearch",method = RequestMethod.POST)
    @ResponseBody
    public  String getEmpKpiReportSearch(HttpServletRequest request){
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
            String page_number = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String search_key=jsonObject.getString("searchValue");
            String type=jsonObject.getString("type");
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = "C10000";
            }

           DataBox dataBox= iceInterfaceService.getKpiReportSearch(corp_code,search_key,page_size,page_number,type);

            if (dataBox.status.toString().equals("SUCCESS")){
                String value = dataBox.data.get("message").value;
                JSONObject obj = JSON.parseObject(value);
                JSONArray message_array;
                if(type.equals("store")) {
                    message_array = obj.getJSONArray("message");
                }else {
                    message_array = obj.getJSONArray("user_list");
                }
                message_array = vipService.vipAnalysisTime(message_array,"","");
                obj.put("message",message_array);
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(obj.toString());
            }else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("获取数据失败");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    //店铺经营报表导出

    @RequestMapping(value ="/storeExportExecl",method = RequestMethod.POST)
    @ResponseBody
    public  String getStoreExportExecl(HttpServletRequest request,HttpServletResponse response){
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
            JSONObject jsonObject = JSONObject.parseObject(message);

           String page_number = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            String store_name = "";
            String sell_area = "";
            String store_group = "";
            String start_time = "";
            String end_time = "";
            String store_code="";

            DataBox dataBox=null;
            //筛选条件
            String search_key=jsonObject.getString("searchValue");
            String screen=jsonObject.get("list").toString();
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if (screenkey.equals("store_area")) {
                        sell_area = jsonObject1.getString("screen_value");
                    } else if (screenkey.equals("store_group")) {
                        store_group = jsonObject1.getString("screen_value");
                    } else if (screenkey.equals("store_name")) {
                        store_name = jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("store_id")){
                        store_code=jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("start_time")){
                        String time=jsonObject1.getString("screen_value");
                        JSONObject object=JSON.parseObject(time);
                        start_time=object.getString("start");
                        end_time=object.getString("end");
                    }
                }
                dataBox = iceInterfaceService.getStoreKpiReport(corp_code,store_code, store_name, store_group, sell_area, start_time, end_time, page_size, page_number);
            }else if(!search_key.equals("")){
                dataBox=iceInterfaceService.getKpiReportSearch(corp_code,search_key,page_size,page_number,"store");
            }else{
                dataBox = iceInterfaceService.getStoreKpiReport(corp_code, "","", "", "", "", "", page_size, page_number);
            }
            String value=dataBox.data.get("message").value;
            JSONObject jsonObject1=JSON.parseObject(value);
            String  message1= jsonObject1.getString("message");
            JSONArray jsonArray=JSON.parseArray(message1);
            jsonArray = vipService.vipAnalysisTime(jsonArray,start_time,end_time);

            //导出......
            if (jsonArray.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl_vip2(jsonArray, map, request, "店铺VIP经营报表");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(errormessage);
        }
        return  dataBean.getJsonStr();
    }


    //导购经营报表导出
    @RequestMapping(value ="/empExportExecl",method = RequestMethod.POST)
    @ResponseBody
    public  String getEmpExportExecl(HttpServletRequest request,HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage="数据异常，导出失败";
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
           String page_number = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            String store_name = "";
            String user_id = "";
            String user_name = "";
            String start_time = "";
            String end_time = "";

            DataBox dataBox=null;
            //筛选条件
            String search_key=jsonObject.getString("searchValue");
            String screen=jsonObject.get("list").toString();
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if (screenkey.equals("store_name")) {
                        store_name = jsonObject1.getString("screen_value");
                    } else if (screenkey.equals("user_code")) {
                        user_id = jsonObject1.getString("screen_value");
                    } else if (screenkey.equals("user_name")) {
                        user_name = jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("start_time")){
                        String time=jsonObject1.getString("screen_value");
                        JSONObject object=JSON.parseObject(time);
                        start_time=object.getString("start");
                        end_time=object.getString("end");
                    }
                }
                 dataBox = iceInterfaceService.getEmpKpiReport(corp_code, store_name, user_id, user_name, start_time, end_time,page_size,page_number);
            }else if(!search_key.equals("")){
                 dataBox= iceInterfaceService.getKpiReportSearch(corp_code,search_key,page_size,page_number,"user");
            }else{
                dataBox = iceInterfaceService.getEmpKpiReport(corp_code, "", "", "", "", "",page_size,page_number);
            }
            String value=dataBox.data.get("message").value;
            JSONObject jsonObject1=JSON.parseObject(value);
            String  message1= jsonObject1.getString("user_list");
            JSONArray jsonArray=JSON.parseArray(message1);
            jsonArray = vipService.vipAnalysisTime(jsonArray,start_time,end_time);

            //导出......
            if (jsonArray.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl2(message1, jsonArray, map, response, request, "导购VIP经营报表");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(errormessage);
        }
        return  dataBean.getJsonStr();
    }

    /**
     * 商品分析
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/skuAnalysis", method = RequestMethod.POST)
    @ResponseBody
    public String skuAnalysis(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.getString("corp_code");
            String page_size = jsonObject.getString("page_size");
            String page_num = jsonObject.getString("page_num");
            String param = jsonObject.getString("param");
            String store_code = jsonObject.getString("store_code");
            String time_type = jsonObject.getString("time_type");
            String time_value = jsonObject.getString("time_value").replace("-","");

            DataBox result = iceInterfaceAPIService.getSkuAnalysis(corp_code,page_num,page_size,param,store_code,time_type,time_value);
            if (result.status.toString().equals("SUCCESS")) {
                JSONObject sku = JSONObject.parseObject(result.data.get("message").value);
                JSONArray sku_list = sku.getJSONArray("sku_list");
                String image_path = "http://img-oss.bizvane.com/StyleImgs/";
                String image_size = "@200w_200h";
                for (int i = 0; i < sku_list.size(); i++) {
                    JSONObject sku_obj = sku_list.getJSONObject(i);
                    String brand_id = sku_obj.getString("BRAND_ID");

                    if (corp_code.equals("C10016") && (brand_id.equals("481") || brand_id.equals("480") || brand_id.equals("1205") || brand_id.equals("1305"))){
                        brand_id = "101";
                    }
                    if (corp_code.equals("C10016") && brand_id.equals("912")){
                        brand_id = "102";
                    }
                    if (corp_code.equals("C10016") && brand_id.equals("937")){
                        brand_id = "103";
                    }
                    if (corp_code.equals("C10016") && (brand_id.equals("1575") || brand_id.equals("1576"))){
                        brand_id = "104";
                    }
                    if (corp_code.equals("C10016") && brand_id.equals("1600")){
                        brand_id = "105";
                    }
                    String product_code = sku_obj.getString("PRODUCT_CODE");
                    String img = image_path + corp_code + "/" + brand_id + "/" + product_code.replace("*","") + ".jpg" + image_size;
                    if (0 != CheckUtils.isConnect(img)) {
                        img = image_path + corp_code + "/" + brand_id + "/" + product_code.replace("*","") + ".JPG" + image_size;
                    }
                    sku_obj.put("PRODUCT_IMG",img);
                }
                int count = Integer.parseInt(sku.getString("count"));
                int size = Integer.parseInt(page_size);
                int pages = 0;
                if (count % size == 0) {
                    pages = count / size;
                } else {
                    pages = count / size + 1;
                }
                if (pages == Integer.parseInt(page_num) ){
                    sku.put("lastPage","true");

                }else {
                    sku.put("lastPage","false");

                }
                sku.put("page_size",page_size);
                sku.put("page_num",page_num);
                sku.put("pages",pages);
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(sku.toString());
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("获取数据失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 商品购买详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/skuSales", method = RequestMethod.POST)
    @ResponseBody
    public String skuSales(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.getString("corp_code");
            String page_size = jsonObject.getString("page_size");
            String page_num = jsonObject.getString("page_num");
            String time_type = jsonObject.getString("time_type");
            String time_value = jsonObject.getString("time_value").replace("-","");
            String sku_id = jsonObject.getString("sku_id");
            String store_code = jsonObject.getString("store_code");
            String query_type = "";
            String row_key = "";
            if (jsonObject.containsKey("query_type"))
                query_type = jsonObject.getString("query_type");
            if (jsonObject.containsKey("row_key"))
                row_key = jsonObject.getString("row_key");
            DataBox result = iceInterfaceAPIService.getSkuSalesDetail(corp_code,page_num,page_size,time_type,time_value,sku_id,store_code,query_type,row_key);
            if (result.status.toString().equals("SUCCESS")) {
                JSONObject sku = JSONObject.parseObject(result.data.get("message").value);

                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(sku.toString());
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("获取数据失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //商品分析报表导出
    @RequestMapping(value ="/skuAnalysisExport",method = RequestMethod.POST)
    @ResponseBody
    public  String skuAnalysisExport(HttpServletRequest request,HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage="数据异常，导出失败";
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String param1 = request.getParameter("param");
            logger.info("json---------------" + param1);
            JSONObject jsonObj = JSONObject.parseObject(param1);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.getString("corp_code");
            String page_size = jsonObject.getString("page_size");
            String page_num = jsonObject.getString("page_num");
            String param = jsonObject.getString("param");
            String store_code = jsonObject.getString("store_code");
            String time_type = jsonObject.getString("time_type");
            String time_value = jsonObject.getString("time_value").replace("-","");

            DataBox result = iceInterfaceAPIService.getSkuAnalysis(corp_code,page_num,page_size,param,store_code,time_type,time_value);
            if (result.status.toString().equals("SUCCESS")) {
                JSONObject sku = JSONObject.parseObject(result.data.get("message").value);
                JSONArray sku_list = sku.getJSONArray("sku_list");

                //导出......
                if (sku_list.size() >= Common.EXPORTEXECLCOUNT) {
                    errormessage = "导出数据过大";
                    int i = 9 / 0;
                }

                List list = WebUtils.Json2List2(sku_list);
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                String json = mapper.writeValueAsString(list);
                LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
                String pathname = OutExeclHelper.OutExecl(json, list, map, response, request,"商品分析");
                JSONObject result_obj = new JSONObject();
                if (pathname == null || pathname.equals("")) {
                    errormessage = "数据异常，导出失败";
                    int a = 8 / 0;
                }
                result_obj.put("path", JSON.toJSONString("lupload/" + pathname));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result_obj.toString());
            }


        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(errormessage);
        }
        return  dataBean.getJsonStr();
    }

}