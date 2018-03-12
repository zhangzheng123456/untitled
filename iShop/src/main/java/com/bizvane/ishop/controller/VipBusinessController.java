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
import com.bizvane.sun.v1.common.DataBox;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequestMapping("/VipBusiness")
public class VipBusinessController {

    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    VipService vipService;
    @Autowired
    StoreService storeService;
    @Autowired
    AreaService areaService;
    @Autowired
    VipBusinessService vipBusinessService;
    @Autowired
    UserService userService;
    @Autowired
    CorpParamService corpParamService;
    @Autowired
    CorpService corpService;
    @Autowired
    VipCardTypeService vipCardTypeService;

    private static final Logger logger = Logger.getLogger(VipBusinessController.class);

    String id;

    @RequestMapping(value = "/storeKpiView", method = RequestMethod.POST)
    @ResponseBody
    public String storeKpiView(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        if (role_code.equals(Common.ROLE_SYS))
            corp_code = "C10000";
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String type = jsonObject.get("type").toString();
            String time_type = jsonObject.getString("time_type");
            String time_value = jsonObject.getString("time_value");
            String view=jsonObject.getString("view");
            String screen="";
            String store_group="";
            String store_code="";
            String user_code="";
            String area_code="";
            if(view.equals("Store")){
                if (jsonObject.containsKey("list")){
                    screen=jsonObject.get("list").toString();

                    JSONArray jsonArray = JSON.parseArray(screen);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String screenkey = jsonObject1.getString("screen_key");
                        if (screenkey.equals("store_group")) {
                            store_group = jsonObject1.getString("screen_value");
                        }else if(screenkey.equals("store_code")) {
                            store_code = jsonObject1.getString("screen_value");
                        }
                    }
                    if (store_code.isEmpty() && !store_group.isEmpty()){
                        List<Store> stores = storeService.getStoreByBrandCode(corp_code,store_group,"","",null,"",Common.IS_ACTIVE_Y);
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + stores.get(i).getStore_code() + ",";
                        }
                    }
                }

                screen=store_code;
            }else if(view.equals("user")){
                //筛选条件
                if(jsonObject.containsKey("list")){
                    screen=jsonObject.get("list").toString();
                    if(!screen.equals("")) {
                        JSONArray jsonArray = JSON.parseArray(screen);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String screenkey = jsonObject1.getString("screen_key");
                            if(screenkey.equals("user_code")){
                                user_code=jsonObject1.getString("screen_value");
                            }
                            else if(screenkey.equals("store_code")){
                                store_code=jsonObject1.getString("screen_value");
                            }
                        }
                        if(StringUtils.isBlank(user_code)){
                            if(!store_code.equals("")){
                                List<User> list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","Y");
                                for (int i = 0; i <list.size() ; i++) {
                                    user_code+=list.get(i).getUser_code()+",";
                                }
                            }
                        }
                    }
                }
                screen=user_code;
            }else if(view.equals("area")){
                if(jsonObject.containsKey("list")){
                    screen=jsonObject.getString("list");
                    if(!screen.equals("")) {
                        //筛选条件
                        JSONArray jsonArray = JSON.parseArray(screen);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String screenkey = jsonObject1.getString("screen_key");
                            if (screenkey.equals("area_code")) {
                                area_code = jsonObject1.getString("screen_value");
                            }
                        }
                    }
                }
                screen=area_code;
            }

            logger.info("==========storeKpiView==screen"+screen);
            DataBox dataBox = iceInterfaceService.storeKpiView(corp_code,type,time_type,time_value,view,screen);
            if (dataBox.status.toString().equals("SUCCESS")){
                String value = dataBox.data.get("message").value;
                JSONObject obj = JSON.parseObject(value);
               // obj.put("message",obj);
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
    @RequestMapping(value ="/storeViewExportExecl",method = RequestMethod.POST)
    @ResponseBody
    public  String storeViewExportExecl(HttpServletRequest request,HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String type = jsonObject.get("type").toString();
            String time_type = jsonObject.getString("time_type");
            String time_value = jsonObject.getString("time_value");
            String view=jsonObject.getString("view");
            String screen="";
            String store_group="";
            String store_code="";
            String user_code="";
            String area_code="";

            String path_name = "";
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

            if (type.equals("1")){
                map.put("date","日期");
                map.put("ovip_num_sales","老会员件数");
                map.put("ovip_num_trade","老会员笔数");
                map.put("ovip_amt_trade","老会员金额");

                map.put("nvip_num_sales","新会员件数");
                map.put("nvip_num_trade","新会员笔数");
                map.put("nvip_amt_trade","新会员金额");

                map.put("novip_num_sales","非会员件数");
                map.put("novip_num_trade","非会员笔数");
                map.put("novip_amt_trade","非会员金额");

                path_name = "新老顾客";
            }else if (type.equals("2")){
                map.put("date","日期");
                map.put("wechat_vip","新增微会员");
                map.put("wechat_vip_bind","绑卡微会员");
                map.put("new_fans","粉丝关注");

                path_name = "多渠道";
            }else if (type.equals("3")){
                if(view.equals("Store")) {
                    map.put("store_code", "店铺编号");
                }
                if(view.equals("user")) {
                    map.put("user_code", "员工编号");
                }
                if(view.equals("area")) {
                    map.put("area_code", "群组编号");
                }
                map.put("amt_trade","店铺业绩");
                map.put("discount","折扣");
                map.put("vip_count","会员数");
                map.put("active_rate","会员活跃度");

                path_name = "顾客经营";
            }
            if(view.equals("Store")){
                path_name = "店铺vip经营_"+path_name;
                if (jsonObject.containsKey("list")){
                    screen=jsonObject.get("list").toString();
                    if(!screen.equals("")) {
                        JSONArray jsonArray = JSON.parseArray(screen);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String screenkey = jsonObject1.getString("screen_key");
                            if (screenkey.equals("store_group")) {
                                store_group = jsonObject1.getString("screen_value");
                            }else if(screenkey.equals("store_code")) {
                                store_code = jsonObject1.getString("screen_value");
                            }
                        }
                        if (store_code.isEmpty() && !store_group.isEmpty()){
                            List<Store> stores = storeService.getStoreByBrandCode(corp_code,store_group,"","",null,"",Common.IS_ACTIVE_Y);
                            for (int i = 0; i < stores.size(); i++) {
                                store_code = store_code + stores.get(i).getStore_code() + ",";
                            }
                        }
                    }
                }
                screen=store_code;
            }else if(view.equals("user")){
                path_name = "导购vip经营_"+path_name;

                //筛选条件
                if(jsonObject.containsKey("list")){
                    screen=jsonObject.get("list").toString();
                    if(!screen.equals("")) {
                        JSONArray jsonArray = JSON.parseArray(screen);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String screenkey = jsonObject1.getString("screen_key");
                            if(screenkey.equals("user_code")){
                                user_code=jsonObject1.getString("screen_value");
                            }
                            else if(screenkey.equals("store_code")){
                                store_code=jsonObject1.getString("screen_value");
                            }
                        }
                        if(StringUtils.isBlank(user_code)){
                            if(!store_code.equals("")){
                                List<User> list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","Y");
                                for (int i = 0; i <list.size() ; i++) {
                                    user_code+=list.get(i).getUser_code()+",";
                                }
                            }
                        }
                    }
                }
                screen=user_code;
            }else if(view.equals("area")){
                path_name = "店铺群组vip经营_"+path_name;

                if(jsonObject.containsKey("list")){
                    screen=jsonObject.getString("list");
                    if(!screen.equals("")) {
                        //筛选条件
                        JSONArray jsonArray = JSON.parseArray(screen);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String screenkey = jsonObject1.getString("screen_key");
                            if (screenkey.equals("area_code")) {
                                area_code = jsonObject1.getString("screen_value");
                            }
                        }
                    }
                }
                screen=area_code;
            }

            DataBox dataBox = iceInterfaceService.storeKpiView(corp_code,type,time_type,time_value,view,screen);
            String value = dataBox.data.get("message").value;
            JSONObject obj = JSON.parseObject(value);
            JSONArray jsonArray = obj.getJSONArray("view");

            //导出......
            if (jsonArray.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
//            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl_vip2(jsonArray, map, request, path_name);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", "lupload/" + pathname);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(errormessage);
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
            String type = jsonObject.get("type").toString();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            String query_type = "";
            if (jsonObject.containsKey("query_type")){
                query_type = jsonObject.getString("query_type");
            }
            String search_key = "";
            if (jsonObject.containsKey("searchValue")){
                search_key = jsonObject.getString("searchValue");
            }
            String screen = "";
            if (jsonObject.containsKey("list")){
                screen=jsonObject.get("list").toString();
            }
            String store_code="";
            String store_group = "";
            String start_time = "";
            String end_time = "";

            //筛选条件
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if (screenkey.equals("store_group")) {
                        store_group = jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("store_code")){
                        store_code=jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("start_time")){
                        String time=jsonObject1.getString("screen_value");
                        JSONObject object=JSON.parseObject(time);
                        start_time=object.getString("start");
                        end_time=object.getString("end");
                    }
                }
                if (store_code.isEmpty() && !store_group.isEmpty()){
                    List<Store> stores = storeService.getStoreByBrandCode(corp_code,store_group,"","",null,"",Common.IS_ACTIVE_Y);
                    if (stores.size() < 1){
                        JSONObject obj = new JSONObject();
                        obj.put("page_size",page_size);
                        obj.put("page_num",page_number);
                        obj.put("message",new JSONArray());
                        JSONObject sum_object = new JSONObject();
                        sum_object.put("sum_amt_trade",0);
                        sum_object.put("sum_num_sales",0);
                        sum_object.put("sum_num_trade",0);

                        sum_object.put("avg_amt_trade",0);
                        sum_object.put("avg_num_sales",0);
                        sum_object.put("avg_num_trade",0);

                        sum_object.put("avg_trade_price",0);
                        sum_object.put("avg_sales_price",0);
                        sum_object.put("avg_relate_rate",0);
                        obj.put("sum", sum_object);

                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setMessage(obj.toString());
                        return  dataBean.getJsonStr();
                    }
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + stores.get(i).getStore_code() + ",";
                    }
                }
            }
            if (!search_key.isEmpty()){
                List<Store> stores = storeService.selectCorpCanSearch(corp_code,search_key);
                if (stores.size() < 1){
                    JSONObject obj = new JSONObject();
                    obj.put("page_size",page_size);
                    obj.put("page_num",page_number);
                    obj.put("message",new JSONArray());
                    JSONObject sum_object = new JSONObject();
                    sum_object.put("sum_amt_trade",0);
                    sum_object.put("sum_num_sales",0);
                    sum_object.put("sum_num_trade",0);

                    sum_object.put("avg_amt_trade",0);
                    sum_object.put("avg_num_sales",0);
                    sum_object.put("avg_num_trade",0);

                    sum_object.put("avg_trade_price",0);
                    sum_object.put("avg_sales_price",0);
                    sum_object.put("avg_relate_rate",0);
                    obj.put("sum", sum_object);

                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage(obj.toString());
                    return  dataBean.getJsonStr();
                }
                for (int i = 0; i < stores.size(); i++) {
                    store_code = store_code + stores.get(i).getStore_code() + ",";
                }
            }

            DataBox dataBox = iceInterfaceService.getNewStoreKpiReport(corp_code,store_code,start_time,end_time,page_size,page_number,type,query_type);
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

    @RequestMapping(value = "/storeKpiSum", method = RequestMethod.POST)
    @ResponseBody
    public String getStoreKpiSum(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        if (role_code.equals(Common.ROLE_SYS))
            corp_code = "C10000";
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            //筛选条件
            String time_type = jsonObject.getString("time_type");
            String time_value = jsonObject.getString("time_value");
            String screen = "";
            if (jsonObject.containsKey("list")){
                screen=jsonObject.get("list").toString();
            }
            String store_code="";
            String store_group = "";
            //筛选条件
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if (screenkey.equals("store_group")) {
                        store_group = jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("store_code")) {
                        store_code = jsonObject1.getString("screen_value");
                    }
                }
                if (store_code.isEmpty() && !store_group.isEmpty()){
                    List<Store> stores = storeService.getStoreByBrandCode(corp_code,store_group,"","",null,"",Common.IS_ACTIVE_Y);
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + stores.get(i).getStore_code() + ",";
                    }
                }
            }

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";
            DataBox dataBox = iceInterfaceService.StoreKpiSumReport(corp_code,time_type,time_value,store_code,store_group);
            if (dataBox.status.toString().equals("SUCCESS")){
                String value = dataBox.data.get("message").value;
                JSONObject obj = JSON.parseObject(value);
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

   /* //店铺经营报表导出
    @RequestMapping(value ="/storeExportExecl",method = RequestMethod.POST)
    @ResponseBody
    public  String getStoreExportExecl(HttpServletRequest request,HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();

        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String page_number = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String type = jsonObject.get("type").toString();

            String execl_type=jsonObject.get("execl_type").toString();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            String store_group = "";
            String start_time = "";
            String end_time = "";
            String store_code="";

            String search_key=jsonObject.getString("searchValue");
            String screen=jsonObject.get("list").toString();
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if (screenkey.equals("store_group")) {
                        store_group = jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("store_id")){
                        store_code=jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("start_time")){
                        String time=jsonObject1.getString("screen_value");
                        JSONObject object=JSON.parseObject(time);
                        start_time=object.getString("start");
                        end_time=object.getString("end");
                    }
                }
                if (store_code.isEmpty() && !store_group.isEmpty()){
                    List<Store> stores = storeService.getStoreByBrandCode(corp_code,store_group,"","",null,"",Common.IS_ACTIVE_Y);
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + stores.get(i).getStore_code() + ",";
                    }
                    if(store_code.equals("")){
                        store_code="~!@#$%&";
                    }
                }
            }else if(!search_key.equals("")){
                List<Store> stores = storeService.selectCorpCanSearch(corp_code,search_key);
                for (int i = 0; i < stores.size(); i++) {
                    store_code = store_code + stores.get(i).getStore_code() + ",";
                }
            }

            WritableWorkbook book = null;
            String filename ="店铺vip经营报表_" + user_code + "_" + Common.DATETIME_FORMAT_DAY_NUM.format(new Date()) + ".xls";
            //  filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("lupload");
            File file = new File(path, filename);
            book = Workbook.createWorkbook(file);
            System.out.println("路径：" + filename);

            final String corp_code1 = corp_code;
            final String store_code1 = store_code;
            final String start_time1 = start_time;
            final String end_time1 = end_time;
            final WritableWorkbook book1 = book;
            final String page_number1 = page_number;
            final String page_size1 = page_size;

            final JSONArray type_array = JSONArray.parseArray(type);
            for (int i = 0; i < type_array.size(); i++) {
                final JSONObject type_obj = type_array.getJSONObject(i);
                final String type1 = type_obj.getString("type");
//            }
//            String[] types = type.split(",");
//            for (int i = 0; i < types.length; i++) {
//                final String type1 = types[i];
                ExecutorService executorService = Executors.newFixedThreadPool(1);
                executorService.execute(new Runnable() {
                    public void run() {
                        try {
                            logger.info("-----------多线程---");
                            //筛选条件

                            String sheet_name = "";
                            int order = 0;
                            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                            map.put("store_id","店铺编号");
                            map.put("store_name","店铺名称");
                            map.put("store_group","所属群组");
                            map.put("store_area","所属区域");
                            if (type1.equals("store")){
                                sheet_name = "店铺业绩";
                                order = 0;
                                map.put("amt_trade","业绩");
                                map.put("num_sales","件数");
                                map.put("num_trade","笔数");
                                map.put("sale_price","件单价");
                                map.put("trade_price","客单价");
                                map.put("relate_rate","连带率");
                            }
                            if (type1.equals("vip")){
                                sheet_name = "会员消费";
                                order = 1;
                                map.put("vip_amt_trade","会销业绩");
                                map.put("vip_num_sales","会销件数");
                                map.put("vip_num_trade","会销笔数");
                                map.put("sale_price","会销件单价");
                                map.put("trade_price","会销客单价");
                                map.put("relate_rate","会销连带率");
                                map.put("vip_rate","会销占比");

                            }
                            if (type1.equals("vipNum")){
                                sheet_name = "会员数量";
                                order = 2;
                                map.put("vip_amt_trade","总数");
                                map.put("vip_num_sales","新增会员");
                                map.put("vip_num_trade","新客人数");
                                map.put("sale_price","复购人数");
                                map.put("trade_price","已购（总）");
                                map.put("relate_rate","已购（新增）");
                                map.put("vip_rate","活跃人数");
                            }
                            if (type1.equals("newVip")){
                                sheet_name = "新会员";
                                order = 3;

                                map.put("new_vip","新客人数");
                                map.put("new_vip_trade","新客已购");
                                map.put("amt_trade","新客消费");
                                map.put("num_sales","新客件数");
                                map.put("num_trade","新客笔数");
                                map.put("sale_price","新客件单价");
                                map.put("trade_price","新客客单价");
                                map.put("relate_rate","新客连带率");
                                map.put("achv_rate","占比/总业绩");
                                map.put("vip_achv_rate","占比/会销");
                            }
                            if (type1.equals("actVip")){
                                sheet_name = "活跃会员";
                                order = 4;
                                map.put("act_vips","活跃会员人数");
                                map.put("amt_trade","活跃业绩");
                                map.put("num_sales","活跃件数");
                                map.put("num_trade","活跃笔数");
                                map.put("sale_price","活跃件单价");
                                map.put("trade_price","活跃客单价");
                                map.put("relate_rate","活跃连带率");
                                map.put("achv_rate","占比/总业绩");
                                map.put("vip_achv_rate","占比/会销");

                            }
                            if (type1.equals("secVip")){
                                sheet_name = "复购分析";
                                order = 5;
                                map.put("sec_vips","复购会员人数");
                                map.put("amt_trade","复购业绩");
                                map.put("num_sales","复购件数");
                                map.put("num_trade","复购笔数");
                                map.put("sale_price","复购件单价");
                                map.put("trade_price","复购客单价");
                                map.put("relate_rate","复购连带率");
                                map.put("achv_rate","占比/总业绩");
                                map.put("vip_achv_rate","占比/会销");
                            }
                            if (type1.equals("vipSex")){
                                sheet_name = "性别";
                                order = 6;
                                map.put("vip_m","总数(男)");
                                map.put("vip_f","总数(女)");
                                map.put("vip_new_m","新增数量(男)");
                                map.put("vip_new_f","新增数量(女)");
                                map.put("amt_trade_M","销售金额(男)");
                                map.put("amt_trade_F","销售金额(女)");
                                map.put("num_sales_M","销售件数(男)");
                                map.put("num_sales_F","销售件数(女)");
                                map.put("num_trade_M","销售笔数(男)");
                                map.put("num_trade_F","销售笔数(女)");
                                map.put("discount_M","折扣(男)");
                                map.put("discount_F","折扣(女)");
                            }

                            String query_type = "";
                            if (type1.equals("vipAge") || type1.equals("vipSource") || type1.equals("vipCardType")){
                                if (type1.equals("vipAge")){
                                    sheet_name = "年龄";
                                    order = 7;
                                    map.put("age_avg","平均年龄");
                                    query_type = "all,new";
                                    String age_group = "0-10,10-20,20-30,30-40,40-50,50-60";
                                    String param_name  = CommonValue.AGE_GROUP_CONF;
                                    List<CorpParam> corpParams = corpParamService.selectParamByName(corp_code1,param_name);
                                    if (corpParams.size() > 0){
                                        age_group = corpParams.get(0).getParam_value();
                                    }
                                    String[] ages = age_group.split(",");
                                    for (int j = 0; j < age_group.length(); j++) {
                                        map.put(ages[j],ages[j]);
                                    }
                                }
                                if (type1.equals("vipSource")){
                                    sheet_name = "渠道会员";
                                    order = 8;
                                    map.put("wechat_vip_sum","会员数量");
                                    map.put("wechat_vip","新增会员");
                                    map.put("wechat_vip_bind","绑卡会员");
                                    map.put("fans","粉丝总数");
                                    map.put("new_fans","新增粉丝");
                                    map.put("wechat_vip_buy","已购会员");
                                    List<CorpWechat> corps = corpService.getWAuthByCorp(corp_code1);
                                    for (int j = 0; j < corps.size(); j++) {
                                        query_type = query_type + corps.get(j).getApp_id() + ",";
                                    }
                                }
                                if (type1.equals("vipCardType")){
                                    sheet_name = "卡类型";
                                    order = 9;
                                    map.put("vip_num","会员总数");
                                    map.put("vip_num_trade","已购会员");
                                    map.put("amt_trade","会销金额");
                                    map.put("achv_rate","业绩占比");
                                    map.put("num_sales","会销件数");
                                    map.put("num_trade","会销笔数");
                                    map.put("sale_price","会销件单价");
                                    map.put("trade_price","会销客单价");
                                    map.put("relate_rate","会销连带率");
                                    List<VipCardType> vipCards = vipCardTypeService.getVipCardTypes(corp_code1,Common.IS_ACTIVE_Y);
                                    for (int j = 0; j < vipCards.size(); j++) {
                                        query_type = query_type + vipCards.get(j).getVip_card_type_name() + ",";
                                    }
                                }
                                if (type_obj.containsKey("query_type")){
                                    query_type = type_obj.getString("query_type");
                                }
                                String[] query_types = query_type.split(",");
                                for (int j = 0; j < query_types.length; j++) {
                                    sheet_name = sheet_name + "_" + query_type;
                                    order = order + j;

                                    DataBox dataBox = iceInterfaceService.getNewStoreKpiReport(corp_code1,store_code1,start_time1,end_time1,page_size1,page_number1,type1,query_types[j]);

                                    String value=dataBox.data.get("message").value;
                                    JSONObject jsonObject1=JSON.parseObject(value);
                                    String  message1= jsonObject1.getString("message");
                                    JSONArray jsonArray=JSON.parseArray(message1);
                                    jsonArray = vipService.vipAnalysisTime(jsonArray,start_time1,end_time1);

                                    OutExeclHelper.OutExeclSheet(jsonArray,map,book1,sheet_name,order);
                                }

                            }else {
                                DataBox dataBox = iceInterfaceService.getNewStoreKpiReport(corp_code1,store_code1,start_time1,end_time1,page_size1,page_number1,type1,"");

                                String value=dataBox.data.get("message").value;
                                JSONObject jsonObject1=JSON.parseObject(value);
                                String  message1= jsonObject1.getString("message");
                                JSONArray jsonArray=JSON.parseArray(message1);
                                jsonArray = vipService.vipAnalysisTime(jsonArray,start_time1,end_time1);


                                OutExeclHelper.OutExeclSheet(jsonArray,map,book1,sheet_name,order);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        System.out.println(new Date()+"Asynchronous task 导出vip");
                    }

                });
                executorService.shutdown();
            }
            JSONObject result = new JSONObject();
            result.put("path", "lupload/" + filename);
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
*/

    //导购经营报表导出
    @RequestMapping(value ="/storeExportExecl",method = RequestMethod.POST)
    @ResponseBody
    public  String getStoreExportExecl(HttpServletRequest request,HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        String filename ="";
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_code_create = request.getSession().getAttribute("user_code").toString();

        WritableWorkbook book = null;
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String page_number = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            final String type = jsonObject.get("type").toString();

            final String execl_type=jsonObject.getString("execl_type");

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            String store_code="";
            String start_time = "";
            String end_time = "";
            String user_code="";
            String area_code = "";
            String store_group="";

            String search_key=jsonObject.getString("searchValue");
            String screen=jsonObject.get("list").toString();

            /*************************导购***********************************************/
            if(execl_type.equals("user")){
                filename ="导购vip经营报表_" + user_code_create + "_" + Common.DATETIME_FORMAT_DAY_NUM.format(new Date()) + ".xls";
                if(!screen.equals("")) {
                    JSONArray jsonArray = JSON.parseArray(screen);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String screenkey = jsonObject1.getString("screen_key");
                        if(screenkey.equals("user_code")){
                            user_code=jsonObject1.getString("screen_value");
                        }
                        else if(screenkey.equals("store_code")){
                            store_code=jsonObject1.getString("screen_value");
                        }else if(screenkey.equals("start_time")){
                            String time=jsonObject1.getString("screen_value");
                            JSONObject object=JSON.parseObject(time);
                            start_time=object.getString("start");
                            end_time=object.getString("end");
                        }
                    }
                    if(StringUtils.isBlank(user_code)){
                        if(!store_code.equals("")){
                            List<User> list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","Y");
                            for (int i = 0; i <list.size() ; i++) {
                                user_code+=list.get(i).getUser_code()+",";
                            }
                            if(user_code.equals("")){
                                user_code="~!@#$%&";
                            }
                        }
                    }
                }
                //搜索
                if(!search_key.equals("")){
                    user_code="";
                    List<User> list=userService.selectUserCodeByNameOrCode(corp_code,search_key);
                    for (int i = 0; i <list.size() ; i++) {
                        user_code+=list.get(i).getUser_code()+",";
                    }
                }
            }

            /*************************店铺***********************************************/
            if(execl_type.equals("store")){
                filename ="店铺vip经营报表_" + user_code_create + "_" + Common.DATETIME_FORMAT_DAY_NUM.format(new Date()) + ".xls";

                if(!screen.equals("")) {
                    JSONArray jsonArray = JSON.parseArray(screen);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String screenkey = jsonObject1.getString("screen_key");
                        if (screenkey.equals("store_group")) {
                            store_group = jsonObject1.getString("screen_value");
                        }else if(screenkey.equals("store_code")){
                            store_code=jsonObject1.getString("screen_value");
                        }else if(screenkey.equals("start_time")){
                            String time=jsonObject1.getString("screen_value");
                            JSONObject object=JSON.parseObject(time);
                            start_time=object.getString("start");
                            end_time=object.getString("end");
                        }
                    }
                    if (store_code.isEmpty() && !store_group.isEmpty()){
                        List<Store> stores = storeService.getStoreByBrandCode(corp_code,store_group,"","",null,"",Common.IS_ACTIVE_Y);
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + stores.get(i).getStore_code() + ",";
                        }
                        if(store_code.equals("")){
                            store_code="~!@#$%&";
                        }
                    }
                }

                if(!search_key.equals("")){
                    List<Store> stores = storeService.selectCorpCanSearch(corp_code,search_key);
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + stores.get(i).getStore_code() + ",";
                    }
                }
            }

            /*************************店铺群组***********************************************/
            if(execl_type.equals("area")){
                filename ="店铺群组vip经营报表_" + user_code_create + "_" + Common.DATETIME_FORMAT_DAY_NUM.format(new Date()) + ".xls";
                if(!screen.equals("")) {
                    JSONArray jsonArray = JSON.parseArray(screen);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String screenkey = jsonObject1.getString("screen_key");
                        if(screenkey.equals("area_code")){
                            area_code=jsonObject1.getString("screen_value");
                        }
                        else if(screenkey.equals("start_time")){
                            String time=jsonObject1.getString("screen_value");
                            JSONObject object=JSON.parseObject(time);
                            start_time=object.getString("start");
                            end_time=object.getString("end");
                        }
                    }
                }
                if(!search_key.equals("")){
                    area_code="";
                    List<Area> areaList=areaService.getAllAreaByPage(corp_code,search_key);
                    for (int i = 0; i <areaList.size(); i++) {
                        area_code+=areaList.get(i).getArea_code().toString()+",";
                    }
                }
            }

            String path = request.getSession().getServletContext().getRealPath("lupload");
            File file = new File(path, filename);
            book = Workbook.createWorkbook(file);
            System.out.println("路径：" + filename);

            final String corp_code1 = corp_code;
            final String store_code1 = store_code;
            final String start_time1 = start_time;
            final String end_time1 = end_time;
            final WritableWorkbook book1 = book;
            final String page_number1 = page_number;
            final String page_size1 = page_size;

            final JSONArray type_array = JSONArray.parseArray(type);
            for (int i = 0; i < type_array.size(); i++) {
                final JSONObject type_obj = type_array.getJSONObject(i);
                final String type1 = type_obj.getString("type");
                final String finalUser_code = user_code;
                final String finalArea_code = area_code;
//                ExecutorService executorService = Executors.newFixedThreadPool(1);
//                executorService.execute(new Runnable() {
//                    public void run() {
                        try {
                            logger.info("-----------多线程---");
                            //筛选条件
                            String sheet_name = "";
                            int order = 0;
                            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                            if(execl_type.equals("user")){
                                map.put("user_code","导购编号");
                                map.put("user_name","导购名称");
                                map.put("store_name","所属店铺");
                            }
                            if(execl_type.equals("store")){
                                map.put("store_id","店铺编号");
                                map.put("store_name","店铺名称");
                                map.put("store_group","所属群组");
                                map.put("store_area","所属区域");
                            }
                            if(execl_type.equals("area")){
                                map.put("area_code","店铺群组编号");
                                map.put("area_name","店铺群组名称");
                            }

                            if (type1.equals("user")||type1.equals("store")||type1.equals("area")){
                                if(type1.equals("user")){
                                    sheet_name = "导购业绩";
                                }
                                if(type1.equals("store")){
                                    sheet_name = "店铺业绩";
                                }
                                if(type1.equals("area")){
                                    sheet_name = "店铺群组业绩";
                                }
                                order = 0;
                                map.put("amt_trade","业绩");
                                map.put("num_sales","件数");
                                map.put("num_trade","笔数");
                                map.put("sale_price","件单价");
                                map.put("trade_price","客单价");
                                map.put("relate_rate","连带率");
                            }
                            if (type1.equals("vip")){
                                sheet_name = "会员消费";
                                order = 1;
                                map.put("vip_amt_trade","会销业绩");
                                map.put("vip_num_sales","会销件数");
                                map.put("vip_num_trade","会销笔数");
                                map.put("sale_price","会销件单价");
                                map.put("trade_price","会销客单价");
                                map.put("relate_rate","会销连带率");
                                map.put("vip_rate","会销占比");

                            }
                            if (type1.equals("vipNum")){
                                sheet_name = "会员数量";
                                order = 2;
                                map.put("vip_all","总数");
                                map.put("vip_new","新增会员");
                                map.put("new_vip","新客人数");
                                map.put("sec_vip","复购人数");
                                map.put("vip_have_trade_all","已购（总）");
                                map.put("vip_have_trade_new","已购（新增）");
                                map.put("vip_active","活跃人数");
                            }
                            if (type1.equals("newVip")){
                                sheet_name = "新会员";
                                order = 3;

                                map.put("new_vip","新客人数");
                                map.put("new_vip_trade","新客已购");
                                map.put("amt_trade","新客消费");
                                map.put("num_sales","新客件数");
                                map.put("num_trade","新客笔数");
                                map.put("sale_price","新客件单价");
                                map.put("trade_price","新客客单价");
                                map.put("relate_rate","新客连带率");
                                map.put("achv_rate","占比/总业绩");
                                map.put("vip_achv_rate","占比/会销");
                            }
                            if (type1.equals("actVip")){
                                sheet_name = "活跃会员";
                                order = 4;
                                map.put("act_vips","活跃会员人数");
                                map.put("amt_trade","活跃业绩");
                                map.put("num_sales","活跃件数");
                                map.put("num_trade","活跃笔数");
                                map.put("sale_price","活跃件单价");
                                map.put("trade_price","活跃客单价");
                                map.put("relate_rate","活跃连带率");
                                map.put("achv_rate","占比/总业绩");
                                map.put("vip_achv_rate","占比/会销");

                            }
                            if (type1.equals("secVip")){
                                sheet_name = "复购分析";
                                order = 5;
                                map.put("sec_vips","复购会员人数");
                                map.put("amt_trade","复购业绩");
                                map.put("num_sales","复购件数");
                                map.put("num_trade","复购笔数");
                                map.put("sale_price","复购件单价");
                                map.put("trade_price","复购客单价");
                                map.put("relate_rate","复购连带率");
                                map.put("achv_rate","占比/总业绩");
                                map.put("vip_achv_rate","占比/会销");
                            }
                            if (type1.equals("vipSex")){
                                sheet_name = "性别";
                                order = 6;
                                map.put("vip_m","总数(男)");
                                map.put("vip_f","总数(女)");
                                map.put("vip_new_m","新增数量(男)");
                                map.put("vip_new_f","新增数量(女)");
                                map.put("amt_trade_M","销售金额(男)");
                                map.put("amt_trade_F","销售金额(女)");
                                map.put("num_sales_M","销售件数(男)");
                                map.put("num_sales_F","销售件数(女)");
                                map.put("num_trade_M","销售笔数(男)");
                                map.put("num_trade_F","销售笔数(女)");
                                map.put("discount_M","折扣(男)");
                                map.put("discount_F","折扣(女)");
                            }

                            String query_type = "";
                            if (type1.equals("vipAge") || type1.equals("vipSource") || type1.equals("vipCardType")){
                                if (type1.equals("vipAge")){
                                    sheet_name = "年龄";
                                    order = 7;
                                    map.put("age_avg","平均年龄");
                                    query_type = "all,new";
                                    String age_group = "0-10,11-20,21-30,31-40,41-50,51-60";
                                    String param_name  = CommonValue.AGE_GROUP_CONF;
                                    List<CorpParam> corpParams = corpParamService.selectParamByName(corp_code1,param_name);
                                    if (corpParams.size() > 0){
                                        age_group = corpParams.get(0).getParam_value();
                                    }
                                    String[] ages = age_group.split(",");
                                    for (int j = 0; j < ages.length; j++) {
                                        map.put(ages[j],ages[j]);
                                    }
                                }
                                if (type1.equals("vipSource")){
                                    sheet_name = "渠道会员";
                                    order = 8;
                                    map.put("wechat_vip_sum","会员数量");
                                    map.put("wechat_vip","新增会员");
                                    map.put("wechat_vip_bind","绑卡会员");
                                    map.put("fans","粉丝总数");
                                    map.put("new_fans","新增粉丝");
                                    map.put("wechat_vip_buy","已购会员");
                                    List<CorpWechat> corps = corpService.getWAuthByCorp(corp_code1);
                                    for (int j = 0; j < corps.size(); j++) {
                                        query_type = query_type + corps.get(j).getApp_id() + ",";
                                    }
                                }
                                if (type1.equals("vipCardType")){
                                    sheet_name = "卡类型";
                                    order = 9;
                                    map.put("vip_num","会员总数");
                                    map.put("vip_num_trade","已购会员");
                                    map.put("amt_trade","会销金额");
                                    map.put("achv_rate","业绩占比");
                                    map.put("num_sales","会销件数");
                                    map.put("num_trade","会销笔数");
                                    map.put("sale_price","会销件单价");
                                    map.put("trade_price","会销客单价");
                                    map.put("relate_rate","会销连带率");
                                    List<VipCardType> vipCards = vipCardTypeService.getVipCardTypes(corp_code1,Common.IS_ACTIVE_Y,"");
                                    for (int j = 0; j < vipCards.size(); j++) {
                                        query_type = query_type + vipCards.get(j).getVip_card_type_name() + ",";
                                    }
                                }
                                if (type_obj.containsKey("query_type")){
                                    query_type = type_obj.getString("query_type");
                                }
                                query_type = query_type.replace("[","").replace("]","").replace("\"","");
                                String[] query_types = query_type.split(",");
                                for (int j = 0; j < query_types.length; j++) {
                                    String sheet_name1 = sheet_name + "_" + query_types[j];
                                    sheet_name1 = sheet_name1.replace("\"","").replace("new","新增结构").replace("all","会员结构");
                                    order = order + j;
                                    DataBox dataBox=null;
                                    if(execl_type.equals("user")){
                                        dataBox = iceInterfaceService.getNewEmpKpiReport(corp_code1, finalUser_code,start_time1,end_time1,page_size1,page_number1,type1,query_types[j]);
                                    }
                                    if(execl_type.equals("store")){
                                       dataBox = iceInterfaceService.getNewStoreKpiReport(corp_code1,store_code1,start_time1,end_time1,page_size1,page_number1,type1,query_types[j]);
                                    }
                                    if(execl_type.equals("area")){
                                        dataBox = iceInterfaceService.getNewAreaKpiReport(corp_code1, finalArea_code,start_time1,end_time1,page_size1,page_number1,type1,query_types[j]);
                                    }

                                    String value=dataBox.data.get("message").value;
                                    JSONObject jsonObject1=JSON.parseObject(value);
                                    String  message1= jsonObject1.getString("message");
                                    JSONArray jsonArray=JSON.parseArray(message1);
                                    jsonArray = vipService.vipAnalysisTime(jsonArray,start_time1,end_time1);

                                    OutExeclHelper.OutExeclSheet(jsonArray,map,book1,sheet_name1,order);
                                }

                            }else {
                                DataBox dataBox=null;
                                if(execl_type.equals("user")){
                                    dataBox =  iceInterfaceService.getNewEmpKpiReport(corp_code1,finalUser_code,start_time1,end_time1,page_size1,page_number1,type1,"");
                                }
                                if(execl_type.equals("store")){
                                    dataBox =  iceInterfaceService.getNewStoreKpiReport(corp_code1,store_code1,start_time1,end_time1,page_size1,page_number1,type1,"");
                                }
                                if(execl_type.equals("area")){
                                    dataBox =  iceInterfaceService.getNewAreaKpiReport(corp_code1,finalArea_code,start_time1,end_time1,page_size1,page_number1,type1,"");
                                }

                                String value=dataBox.data.get("message").value;
                                JSONObject jsonObject1=JSON.parseObject(value);
                                String  message1= jsonObject1.getString("message");
                                JSONArray jsonArray=JSON.parseArray(message1);
                                jsonArray = vipService.vipAnalysisTime(jsonArray,start_time1,end_time1);

                                OutExeclHelper.OutExeclSheet(jsonArray,map,book1,sheet_name,order);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        System.out.println(new Date()+"Asynchronous task 导出vip");
//                    }

//                });
//                executorService.shutdown();
            }
            //写入文件
            book.write();
            JSONObject result = new JSONObject();
            result.put("path", "lupload/" + filename);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(errormessage);
        }finally {
            if (book != null) {
                try {
                    book.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
            System.gc();
        }
        return  dataBean.getJsonStr();
    }

  /*  //店铺经营报表导出
    @RequestMapping(value ="/areaExportExecl",method = RequestMethod.POST)
    @ResponseBody
    public  String getAreaExportExecl(HttpServletRequest request,HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_code_creater = request.getSession().getAttribute("user_code").toString();

        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String page_number = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String type = jsonObject.get("type").toString();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            String area_code = "";
            String start_time = "";
            String end_time = "";

            String search_key=jsonObject.getString("searchValue");
            String screen=jsonObject.get("list").toString();
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if(screenkey.equals("area_code")){
                        area_code=jsonObject1.getString("screen_value");
                    }
                    else if(screenkey.equals("start_time")){
                        String time=jsonObject1.getString("screen_value");
                        JSONObject object=JSON.parseObject(time);
                        start_time=object.getString("start");
                        end_time=object.getString("end");
                    }
                }
            }
            if(!search_key.equals("")){
                area_code="";
                List<Area> areaList=areaService.getAllAreaByPage(corp_code,search_key);
                for (int i = 0; i <areaList.size(); i++) {
                    area_code+=areaList.get(i).getArea_code().toString()+",";

                    System.out.println("------code----"+area_code);
                }
            }

            WritableWorkbook book = null;
            String filename ="店铺群组vip经营报表_" + user_code_creater + "_" + Common.DATETIME_FORMAT_DAY_NUM.format(new Date()) + ".xls";
            //  filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("lupload");
            File file = new File(path, filename);
            book = Workbook.createWorkbook(file);
            System.out.println("路径：" + filename);

            final String corp_code1 = corp_code;
            final String start_time1 = start_time;
            final String end_time1 = end_time;
            final WritableWorkbook book1 = book;
            final String page_number1 = page_number;
            final String page_size1 = page_size;

            final JSONArray type_array = JSONArray.parseArray(type);
            for (int i = 0; i < type_array.size(); i++) {
                final JSONObject type_obj = type_array.getJSONObject(i);
                final String type1 = type_obj.getString("type");
                ExecutorService executorService = Executors.newFixedThreadPool(1);
                final String finalArea_code = area_code;
                executorService.execute(new Runnable() {
                    public void run() {
                        try {
                            logger.info("-----------多线程---");
                            //筛选条件

                            String sheet_name = "";
                            int order = 0;
                            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                            map.put("area_code","店铺群组编号");
                            map.put("area_name","店铺群组名称");
                            if (type1.equals("store")){
                                sheet_name = "店铺群组业绩";
                                order = 0;
                                map.put("amt_trade","业绩");
                                map.put("num_sales","件数");
                                map.put("num_trade","笔数");
                                map.put("sale_price","件单价");
                                map.put("trade_price","客单价");
                                map.put("relate_rate","连带率");
                            }
                            if (type1.equals("vip")){
                                sheet_name = "会员消费";
                                order = 1;
                                map.put("vip_amt_trade","会销业绩");
                                map.put("vip_num_sales","会销件数");
                                map.put("vip_num_trade","会销笔数");
                                map.put("sale_price","会销件单价");
                                map.put("trade_price","会销客单价");
                                map.put("relate_rate","会销连带率");
                                map.put("vip_rate","会销占比");

                            }
                            if (type1.equals("vipNum")){
                                sheet_name = "会员数量";
                                order = 2;
                                map.put("vip_amt_trade","总数");
                                map.put("vip_num_sales","新增会员");
                                map.put("vip_num_trade","新客人数");
                                map.put("sale_price","复购人数");
                                map.put("trade_price","已购（总）");
                                map.put("relate_rate","已购（新增）");
                                map.put("vip_rate","活跃人数");
                            }
                            if (type1.equals("newVip")){
                                sheet_name = "新会员";
                                order = 3;

                                map.put("new_vip","新客人数");
                                map.put("new_vip_trade","新客已购");
                                map.put("amt_trade","新客消费");
                                map.put("num_sales","新客件数");
                                map.put("num_trade","新客笔数");
                                map.put("sale_price","新客件单价");
                                map.put("trade_price","新客客单价");
                                map.put("relate_rate","新客连带率");
                                map.put("achv_rate","占比/总业绩");
                                map.put("vip_achv_rate","占比/会销");
                            }
                            if (type1.equals("actVip")){
                                sheet_name = "活跃会员";
                                order = 4;
                                map.put("act_vips","活跃会员人数");
                                map.put("amt_trade","活跃业绩");
                                map.put("num_sales","活跃件数");
                                map.put("num_trade","活跃笔数");
                                map.put("sale_price","活跃件单价");
                                map.put("trade_price","活跃客单价");
                                map.put("relate_rate","活跃连带率");
                                map.put("achv_rate","占比/总业绩");
                                map.put("vip_achv_rate","占比/会销");

                            }
                            if (type1.equals("secVip")){
                                sheet_name = "复购分析";
                                order = 5;
                                map.put("sec_vips","复购会员人数");
                                map.put("amt_trade","复购业绩");
                                map.put("num_sales","复购件数");
                                map.put("num_trade","复购笔数");
                                map.put("sale_price","复购件单价");
                                map.put("trade_price","复购客单价");
                                map.put("relate_rate","复购连带率");
                                map.put("achv_rate","占比/总业绩");
                                map.put("vip_achv_rate","占比/会销");
                            }
                            if (type1.equals("vipSex")){
                                sheet_name = "性别";
                                order = 6;
                                map.put("vip_m","总数(男)");
                                map.put("vip_f","总数(女)");
                                map.put("vip_new_m","新增数量(男)");
                                map.put("vip_new_f","新增数量(女)");
                                map.put("amt_trade_M","销售金额(男)");
                                map.put("amt_trade_F","销售金额(女)");
                                map.put("num_sales_M","销售件数(男)");
                                map.put("num_sales_F","销售件数(女)");
                                map.put("num_trade_M","销售笔数(男)");
                                map.put("num_trade_F","销售笔数(女)");
                                map.put("discount_M","折扣(男)");
                                map.put("discount_F","折扣(女)");
                            }

                            String query_type = "";
                            if (type1.equals("vipAge") || type1.equals("vipSource") || type1.equals("vipCardType")){
                                if (type1.equals("vipAge")){
                                    sheet_name = "年龄";
                                    order = 7;
                                    map.put("age_avg","平均年龄");
                                    query_type = "all,new";
                                    String age_group = "0-10,10-20,20-30,30-40,40-50,50-60";
                                    String param_name  = CommonValue.AGE_GROUP_CONF;
                                    List<CorpParam> corpParams = corpParamService.selectParamByName(corp_code1,param_name);
                                    if (corpParams.size() > 0){
                                        age_group = corpParams.get(0).getParam_value();
                                    }
                                    String[] ages = age_group.split(",");
                                    for (int j = 0; j < age_group.length(); j++) {
                                        map.put(ages[j],ages[j]);
                                    }
                                }
                                if (type1.equals("vipSource")){
                                    sheet_name = "渠道会员";
                                    order = 8;
                                    map.put("wechat_vip_sum","会员数量");
                                    map.put("wechat_vip","新增会员");
                                    map.put("wechat_vip_bind","绑卡会员");
                                    map.put("fans","粉丝总数");
                                    map.put("new_fans","新增粉丝");
                                    map.put("wechat_vip_buy","已购会员");
                                    List<CorpWechat> corps = corpService.getWAuthByCorp(corp_code1);
                                    for (int j = 0; j < corps.size(); j++) {
                                        query_type = query_type + corps.get(j).getApp_id() + ",";
                                    }
                                }
                                if (type1.equals("vipCardType")){
                                    sheet_name = "卡类型";
                                    order = 9;
                                    map.put("vip_num","会员总数");
                                    map.put("vip_num_trade","已购会员");
                                    map.put("amt_trade","会销金额");
                                    map.put("achv_rate","业绩占比");
                                    map.put("num_sales","会销件数");
                                    map.put("num_trade","会销笔数");
                                    map.put("sale_price","会销件单价");
                                    map.put("trade_price","会销客单价");
                                    map.put("relate_rate","会销连带率");
                                    List<VipCardType> vipCards = vipCardTypeService.getVipCardTypes(corp_code1,Common.IS_ACTIVE_Y);
                                    for (int j = 0; j < vipCards.size(); j++) {
                                        query_type = query_type + vipCards.get(j).getVip_card_type_name() + ",";
                                    }
                                }
                                if (type_obj.containsKey("query_type")){
                                    query_type = type_obj.getString("query_type");
                                }
                                String[] query_types = query_type.split(",");
                                for (int j = 0; j < query_types.length; j++) {
                                    sheet_name = sheet_name + "_" + query_type;
                                    order = order + j;

                                    DataBox dataBox = iceInterfaceService.getNewAreaKpiReport(corp_code1, finalArea_code,start_time1,end_time1,page_size1,page_number1,type1,query_types[j]);

                                    String value=dataBox.data.get("message").value;
                                    JSONObject jsonObject1=JSON.parseObject(value);
                                    String  message1= jsonObject1.getString("message");
                                    JSONArray jsonArray=JSON.parseArray(message1);
                                    jsonArray = vipService.vipAnalysisTime(jsonArray,start_time1,end_time1);

                                    OutExeclHelper.OutExeclSheet(jsonArray,map,book1,sheet_name,order);
                                }

                            }else {
                                DataBox dataBox = iceInterfaceService.getNewAreaKpiReport(corp_code1,finalArea_code,start_time1,end_time1,page_size1,page_number1,type1,"");

                                String value=dataBox.data.get("message").value;
                                JSONObject jsonObject1=JSON.parseObject(value);
                                String  message1= jsonObject1.getString("message");
                                JSONArray jsonArray=JSON.parseArray(message1);
                                jsonArray = vipService.vipAnalysisTime(jsonArray,start_time1,end_time1);


                                OutExeclHelper.OutExeclSheet(jsonArray,map,book1,sheet_name,order);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        System.out.println(new Date()+"Asynchronous task 导出vip");
                    }

                });
                executorService.shutdown();
            }
            JSONObject result = new JSONObject();
            result.put("path", "lupload/" + filename);
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
    }*/


    @RequestMapping(value = "/empKpi", method = RequestMethod.POST)
    @ResponseBody
    public String getEmpKpiReport(HttpServletRequest request){
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
            String type = jsonObject.get("type").toString();

            String query_type = "";
            if (jsonObject.containsKey("query_type")){
                query_type = jsonObject.getString("query_type");
            }

            String search_key = "";
            String screen = "";
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            if (jsonObject.containsKey("searchValue")){
                search_key = jsonObject.getString("searchValue");
            }
            if (jsonObject.containsKey("list")){
                screen=jsonObject.get("list").toString();
            }
            String area_code = "";
            String brand_code="";
            String store_code="";
            String start_time = "";
            String end_time = "";
            String user_code="";
            //筛选条件
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if(screenkey.equals("user_code")){
                        user_code=jsonObject1.getString("screen_value");
                    }
//                    else if(screenkey.equals("area_code")){
//                        area_code=jsonObject1.getString("screen_value");
//                    }else if(screenkey.equals("brand_code")){
//                        brand_code=jsonObject1.getString("screen_value");
//                    }
                    else if(screenkey.equals("store_code")){
                        store_code=jsonObject1.getString("screen_value");
                    }else if(screenkey.equals("start_time")){
                        String time=jsonObject1.getString("screen_value");
                        JSONObject object=JSON.parseObject(time);
                        start_time=object.getString("start");
                        end_time=object.getString("end");
                    }
                }
                if(StringUtils.isBlank(user_code)){
//                    if (store_code.isEmpty()){
//                        if(!area_code.isEmpty()||!brand_code.isEmpty()){
//                            String[] areas = null;
//                            if (!area_code.equals("")) {
//                                areas = area_code.split(",");
//                            }
//                            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
//                            for (int i = 0; i < stores.size(); i++) {
//                                store_code = store_code + stores.get(i).getStore_code() + ",";
//                            }
//                            List<User> list = userService.selUserByStoreCode(corp_code, "", store_code, areas, "");
//                            for (int i = 0; i <list.size() ; i++) {
//                                user_code+=list.get(i).getUser_code()+",";
//                            }
//                        }
//                    }else
                    if(!store_code.equals("")){
                        List<User> list = userService.selUserByStoreCode(corp_code, "", store_code, null, "","Y");
                        for (int i = 0; i <list.size() ; i++) {
                            user_code+=list.get(i).getUser_code()+",";
                        }

                        if(user_code.equals("")){
                            JSONObject obj = new JSONObject();
                            obj.put("message", new JSONArray());
                            obj.put("sum", vipBusinessService.getSumParam(type));
                            obj.put("page_count", 0);
                            obj.put("count", 0);
                            obj.put("page_size",page_size);
                            obj.put("page_num",page_number);
                            dataBean.setId(id);
                            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                            dataBean.setMessage(obj.toString());
                            return  dataBean.getJsonStr();
                        }

                    }
                }
            }
            if(!search_key.equals("")){//搜索
                user_code="";
                List<User> list=userService.selectUserCodeByNameOrCode(corp_code,search_key);
                for (int i = 0; i <list.size() ; i++) {
                    user_code+=list.get(i).getUser_code()+",";
                }
                if(user_code.equals("")){
                    JSONObject obj = new JSONObject();
                    obj.put("message", new JSONArray());
                    obj.put("sum", vipBusinessService.getSumParam(type));
                    obj.put("page_count", 0);
                    obj.put("count", 0);
                    obj.put("page_size",page_size);
                    obj.put("page_num",page_number);
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage(obj.toString());
                    return  dataBean.getJsonStr();
                }
            }

            System.out.println("======user_code======"+user_code);
            DataBox dataBox = iceInterfaceService.getNewEmpKpiReport(corp_code,user_code,start_time,end_time,page_size,page_number,type,query_type);
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

    @RequestMapping(value = "/empKpiSum", method = RequestMethod.POST)
    @ResponseBody
    public String getEmpKpiSum(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        if (role_code.equals(Common.ROLE_SYS))
            corp_code = "C10000";
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            //筛选条件
            String time_type = jsonObject.getString("time_type");
            String time_value = jsonObject.getString("time_value");

            String store_code="";
            String user_code="";
            //筛选条件
            String screen = "";
            if (jsonObject.containsKey("list")){
                screen=jsonObject.get("list").toString();
            }
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if(screenkey.equals("user_code")){
                        user_code=jsonObject1.getString("screen_value");
                    }
                    else if(screenkey.equals("store_code")){
                        store_code=jsonObject1.getString("screen_value");
                    }
                }
                System.out.println("....user_code..."+user_code);
                System.out.println(".....store_code...."+store_code);
                if(user_code.trim().equals("")){
                    if(!store_code.equals("")){
                        List<User> list =  userService.selUserByStoreCode(corp_code, "", store_code, null, "","Y");
                        for (int i = 0; i <list.size(); i++) {
                            user_code+=list.get(i).getUser_code()+",";
                        }
                    }
                }
            }

            System.out.println("----user_code---"+user_code);
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            DataBox dataBox = iceInterfaceService.EmpKpiSumReport(corp_code,time_type,time_value,user_code,store_code);
            if (dataBox.status.toString().equals("SUCCESS")){
                String value = dataBox.data.get("message").value;
                JSONObject obj = JSON.parseObject(value);
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

    @RequestMapping(value = "/areaKpiSum", method = RequestMethod.POST)
    @ResponseBody
    public String getAreaKpiSum(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        if (role_code.equals(Common.ROLE_SYS))
            corp_code = "C10000";
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            //筛选条件
            String time_type = jsonObject.getString("time_type");
            String time_value = jsonObject.getString("time_value");

            String screen="";
            if (jsonObject.containsKey("list")){
                screen=jsonObject.get("list").toString();
            }
            String area_code = "";
            //筛选条件
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if(screenkey.equals("area_code")){
                        area_code=jsonObject1.getString("screen_value");
                    }
                }
            }

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            DataBox dataBox = iceInterfaceService.AreaKpiSumReport(corp_code,time_type,time_value,area_code);
            if (dataBox.status.toString().equals("SUCCESS")){
                String value = dataBox.data.get("message").value;
                JSONObject obj = JSON.parseObject(value);
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

    @RequestMapping(value = "/areaKpi", method = RequestMethod.POST)
    @ResponseBody
    public String getAreaKpiReport(HttpServletRequest request){
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
            String type = jsonObject.get("type").toString();

            String query_type = "";
            if (jsonObject.containsKey("query_type")){
                query_type = jsonObject.getString("query_type");
            }

            String search_key = "";
            String screen = "";
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            if (jsonObject.containsKey("searchValue")){
                search_key = jsonObject.getString("searchValue");
            }
            if (jsonObject.containsKey("list")){
                screen=jsonObject.get("list").toString();
            }
            String area_code = "";
            String start_time = "";
            String end_time = "";
            //筛选条件
            if(!screen.equals("")) {
                JSONArray jsonArray = JSON.parseArray(screen);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String screenkey = jsonObject1.getString("screen_key");
                    if(screenkey.equals("area_code")){
                        area_code=jsonObject1.getString("screen_value");
                    }
                    else if(screenkey.equals("start_time")){
                        String time=jsonObject1.getString("screen_value");
                        JSONObject object=JSON.parseObject(time);
                        start_time=object.getString("start");
                        end_time=object.getString("end");
                    }
                }
            }
            if(!search_key.isEmpty()){//搜索
                area_code="";
                List<Area> areaList=areaService.getAllAreaByPage(corp_code,search_key);
                for (int i = 0; i <areaList.size(); i++) {
                    area_code+=areaList.get(i).getArea_code().toString()+",";

                    System.out.println("------code----"+area_code);
                }
            }

            DataBox dataBox = iceInterfaceService.getNewAreaKpiReport(corp_code,area_code,start_time,end_time,page_size,page_number,type,query_type);
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
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/ageGroup", method = RequestMethod.POST)
    @ResponseBody
    public String getAgeGroup(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";

            String age_group = "0-10,11-20,21-30,31-40,41-50,51-60";
            String param_name  = CommonValue.AGE_GROUP_CONF;
            List<CorpParam> corpParams = corpParamService.selectParamByName(corp_code,param_name);
            if (corpParams.size() > 0){
                age_group = corpParams.get(0).getParam_value();
            }

            JSONObject age_obj = new JSONObject();
            age_obj.put("age",age_group);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(age_obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
    //===========================优惠券查询===========================

    /**
     * 优惠券查询
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/couponSearch", method = RequestMethod.POST)
    @ResponseBody
    public String couponSearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS) && jsonObject.containsKey("corp_code")){
                corp_code = jsonObject.getString("corp_code");
            }else if (role_code.equals(Common.ROLE_SYS)){
                corp_code = "C10000";
            }
            String page_size = jsonObject.getString("page_size");
            String page_num = jsonObject.getString("page_num");
            JSONObject param = new JSONObject();
            if (jsonObject.containsKey("list")){
                String param1 = jsonObject.getString("list");
                JSONArray array = JSONArray.parseArray(param1);
                for (int i = 0; i < array.size(); i++) {
                    String screen_value = array.getJSONObject(i).getString("screen_value");
                    if (!screen_value.isEmpty())
                        param.put(array.getJSONObject(i).getString("screen_key"),screen_value);
                }
            }
            String searchValue = "";
            if (jsonObject.containsKey("searchValue")){
                searchValue = jsonObject.getString("searchValue");
            }

            DataBox result = iceInterfaceAPIService.couponSearch(corp_code,page_num,page_size,param.toString(),searchValue);

            if (result.status.toString().equals("SUCCESS")) {
                JSONObject coupons = JSONObject.parseObject(result.data.get("message").value);
                int count = Integer.parseInt(coupons.getString("count"));
                int size = Integer.parseInt(page_size);
                int pages = 0;
                if (count % size == 0) {
                    pages = count / size;
                } else {
                    pages = count / size + 1;
                }
                if (pages == Integer.parseInt(page_num) ){
                    coupons.put("lastPage","true");

                }else {
                    coupons.put("lastPage","false");

                }
                coupons.put("page_size",page_size);
                coupons.put("page_num",page_num);
                coupons.put("pages",pages);
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(coupons.toString());
            }else {
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




    //优惠券查询导出
    @RequestMapping(value ="/couponSearchExport",method = RequestMethod.POST)
    @ResponseBody
    public  String couponSearchExport(HttpServletRequest request,HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage="数据异常，导出失败";
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String params = request.getParameter("param");
            logger.info("json---------------" + params);
            JSONObject jsonObj = JSONObject.parseObject(params);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.getString("corp_code");
            String page_size = jsonObject.getString("page_size");
            String page_num = jsonObject.getString("page_num");
            JSONObject param = new JSONObject();
            if (jsonObject.containsKey("list") && !jsonObject.getString("list").isEmpty()){
                String param1 = jsonObject.getString("list");
                JSONArray array = JSONArray.parseArray(param1);
                for (int i = 0; i < array.size(); i++) {
                    String screen_value = array.getJSONObject(i).getString("screen_value");
                    if (!screen_value.isEmpty())
                        param.put(array.getJSONObject(i).getString("screen_key"),screen_value);
                }
            }
            String searchValue = "";
            if (jsonObject.containsKey("searchValue")){
                searchValue = jsonObject.getString("searchValue");
            }

            DataBox result = iceInterfaceAPIService.couponSearch(corp_code,page_num,page_size,param.toString(),searchValue);
            if (result.status.toString().equals("SUCCESS")) {
                JSONObject sku = JSONObject.parseObject(result.data.get("message").value);
                JSONArray sku_list = sku.getJSONArray("coupon_list");

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
                if (map.containsKey("IS_EXPIRED"))
                    map.put("IS_EXPIRED","是否过期(Y/是，N/否)");
                if (map.containsKey("SAME_BV_VIP"))
                    map.put("SAME_BV_VIP","原会员使用(Y/是，N/否)");
                String pathname = OutExeclHelper.OutExecl(json, list, map, response, request,"优惠券查询");
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