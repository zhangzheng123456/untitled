package com.bizvane.ishop.service.imp;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.sun.app.client.Client;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.Status;
import com.bizvane.sun.v1.common.ValueType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhou on 2016/7/6.
 *
 * @@version
 */

@Service
public class IceInterfaceServiceImpl implements IceInterfaceService {

    String[] arg = new String[]{"--Ice.Config=client.config"};
    Client client = new Client(arg);

    @Autowired
    StoreService storeService;
    @Autowired
    CorpService corpService;
    //
    public DataBox iceInterface(String method ,Map datalist) throws Exception{
        String methods = "com.bizvane.sun.app.method."+method;
        DataBox dataBox1 = new DataBox("1", Status.ONGOING, "", methods, datalist, null, null, System.currentTimeMillis());
        DataBox dataBox = client.put(dataBox1);

        return dataBox;
    }

    //v2接口（数据类）
    public DataBox iceInterfaceV2(String method ,Map datalist) throws Exception{
        String methods = "com.bizvane.sun.app.method.v2."+method;
        DataBox dataBox1 = new DataBox("1", Status.ONGOING, "", methods, datalist, null, null, System.currentTimeMillis());
        DataBox dataBox = client.put(dataBox1);

        return dataBox;
    }

    //v3接口（数据类）
    public DataBox iceInterfaceV3(String method ,Map datalist) throws Exception{
        String methods = "com.bizvane.sun.app.method.v3."+method;
        DataBox dataBox1 = new DataBox("1", Status.ONGOING, "", methods, datalist, null, null, System.currentTimeMillis());
        DataBox dataBox = client.put(dataBox1);

        return dataBox;
    }


    //会员列表(搜索)
    public Map vipBasicMethod2(String page_num, String page_size, String corp_code, HttpServletRequest request,String sort_key,String sort_value) throws Exception{
        String user_code = request.getSession().getAttribute("user_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String user_id = "";
        String area_code = "";
        String store_id = "";
        if (role_code.equals(Common.ROLE_SYS) ||role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
            role_code=Common.ROLE_GM;
            store_id = "";
        } else if (role_code.equals(Common.ROLE_AM)){
            role_code = Common.ROLE_SM;
            String brand_code = request.getSession().getAttribute("brand_code").toString(); //品牌编号
            String area_code1 = request.getSession().getAttribute("area_code").toString();  //区域编号
            String area_store_code = request.getSession().getAttribute("store_code").toString(); //店铺编号
            area_code1 = area_code1.replace(Common.SPECIAL_HEAD,"");
            brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,area_code1,brand_code,"",area_store_code);
            for (int i = 0; i < stores.size(); i++) {
                store_id = store_id + stores.get(i).getStore_code() + ",";
            }
        } else if (role_code.equals(Common.ROLE_SM)){
            String store_code = request.getSession().getAttribute("store_code").toString();
            store_id = store_code.replace(Common.SPECIAL_HEAD,"");
        } else if (role_code.equals(Common.ROLE_STAFF)){
            String store_code = request.getSession().getAttribute("store_code").toString();
            store_id = store_code.replace(Common.SPECIAL_HEAD,"");
            user_id = user_code;
        }else if (role_code.equals(Common.ROLE_BM)){
            role_code = Common.ROLE_SM;
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
            String area_code1 = request.getSession().getAttribute("area_code").toString();
            area_code1 = area_code1.replace(Common.SPECIAL_HEAD,"");

            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,area_code1,brand_code,"","");
            for (int i = 0; i < stores.size(); i++) {
                store_id = store_id + stores.get(i).getStore_code() + ",";
            }
        }

        Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
        Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
        Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_sort_key = new Data("sort_key", sort_key, ValueType.PARAM);
        Data data_sort_value = new Data("sort_value", sort_value, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_id.key, data_store_id);
        datalist.put(data_area_code.key, data_area_code);
        datalist.put(data_role_code.key, data_role_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_sort_key.key, data_sort_key);
        datalist.put(data_sort_value.key, data_sort_value);
        return datalist;
    }


    //会员分析
    public Map vipAnalysisBasicMethod(JSONObject jsonObject, HttpServletRequest request) throws Exception{
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String page_num = jsonObject.get("pageNumber").toString();
        String page_size = jsonObject.get("pageSize").toString();

        String store_id = "";
        String user_id = "";
        int flag = 0;
        if (role_code.equals(Common.ROLE_SYS)) {
            corp_code = jsonObject.get("corp_code").toString();
        }
        if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)){
            store_id = jsonObject.get("store_code").toString().trim();
            user_id = jsonObject.get("group_code").toString().trim();
            if (store_id.equals("") && user_id.equals("")) {
                String area_code = jsonObject.get("area_code").toString().trim();
                String brand_code = jsonObject.get("brand_code").toString().trim();
                if (jsonObject.containsKey("query_type") && (jsonObject.getString("query_type").equals("history") ||
                        jsonObject.getString("query_type").equals("0") || jsonObject.getString("query_type").equals("1")
                        || jsonObject.getString("query_type").equals("2") || jsonObject.getString("query_type").equals("3")
                        || jsonObject.getString("query_type").equals("4") || jsonObject.getString("query_type").equals("define"))
                        ||jsonObject.getString("query_type").equals("today") ||jsonObject.getString("query_type").equals("current_month")
                        ||jsonObject.getString("query_type").equals("next_month") ||jsonObject.getString("query_type").equals("daily")
                        ||jsonObject.getString("query_type").equals("weekly") ||jsonObject.getString("query_type").equals("monthly")
                        ||jsonObject.getString("query_type").equals("recent") ||jsonObject.getString("query_type").equals("month")
                        ||jsonObject.getString("query_type").equals("three_month") || jsonObject.getString("query_type").equals("freq")) {
                    //历史排行查solr
                    if (!area_code.equals("") || !brand_code.equals("")) {
                        flag = 1;
                        List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                        for (int i = 0; i < storeList.size(); i++) {
                            store_id = store_id + storeList.get(i).getStore_code() + ",";
                        }
                    }
                } else {
                    if (!area_code.equals("") || !brand_code.equals("")) {
                        flag = 1;
                    }
                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                    for (int i = 0; i < storeList.size(); i++) {
                        store_id = store_id + storeList.get(i).getStore_code() + ",";
                    }
                }
            }else if(!store_id.equals("")){
                flag=1;
            }
        } else if (role_code.equals(Common.ROLE_AM)){
            flag = 1;
            store_id = jsonObject.get("store_code").toString().trim();
            String area_code = jsonObject.get("area_code").toString().trim();
            String brand_code = jsonObject.get("brand_code").toString().trim();
            user_id = jsonObject.get("group_code").toString().trim();

            String area_store_code = "";
            if (store_id.equals("") && user_id.equals("")){
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
            flag = 1;

            store_id = jsonObject.get("store_code").toString().trim();
            user_id = jsonObject.get("group_code").toString().trim();
            String brand_code = jsonObject.get("brand_code").toString().trim();
            String area_code = jsonObject.get("area_code").toString().trim();

            if (store_id.equals("") && user_id.equals("")) {
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
        }else if (role_code.equals(Common.ROLE_SM)){
            flag = 1;

            store_id = jsonObject.get("store_code").toString().trim();
            user_id = jsonObject.get("group_code").toString().trim();
            if (store_id.equals("") && user_id.equals("")) {
                store_id = request.getSession().getAttribute("store_code").toString().replace(Common.SPECIAL_HEAD, "");
            }
        }else if (role_code.equals(Common.ROLE_STAFF)){
            user_id = request.getSession().getAttribute("user_code").toString();
        }

        Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
        Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
//        Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);
//        Data data_brand_code = new Data("brand_code", brand_code1, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_flag = new Data("flag", String.valueOf(flag), ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_id.key, data_store_id);
//        datalist.put(data_area_code.key, data_area_code);
//        datalist.put(data_brand_code.key, data_brand_code);
        datalist.put(data_role_code.key, data_role_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_flag.key, data_flag);

        return datalist;
    }

    //会员筛选
    public DataBox vipScreenMethod(String page_num,String page_size,String corp_code,String area_code,String brand_code,String store_code,String user_code) throws Exception{
        DataBox dataBox = null;
        if (user_code.equals("")) {
            String role_code = Common.ROLE_SM;
            if (store_code.equals("")) {
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                for (int i = 0; i < storeList.size(); i++) {
                    store_code = store_code + storeList.get(i).getStore_code() + ",";
                }
            }
            Data data_user_id = new Data("user_id", user_code, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
            Data data_store_id = new Data("store_id", store_code, ValueType.PARAM);
            Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);
            Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
            Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_id.key, data_store_id);
            datalist.put(data_area_code.key, data_area_code);
            datalist.put(data_role_code.key, data_role_code);
            datalist.put(data_page_num.key, data_page_num);
            datalist.put(data_page_size.key, data_page_size);

            dataBox = iceInterfaceV2("AnalysisAllVip", datalist);
        } else {
            Data data_user_id = new Data("user_id", user_code, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
            Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_page_num.key, data_page_num);
            datalist.put(data_page_size.key, data_page_size);

            dataBox = iceInterfaceV2("AnalysisEmpsVip", datalist);
        }
        return dataBox;
    }

    //会员筛选(Solr)
    public DataBox vipScreenMethod2(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value) throws Exception{
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_sort_key = new Data("sort_key", sort_key, ValueType.PARAM);
        Data data_sort_value = new Data("sort_value", sort_value, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_sort_key.key, data_sort_key);
        datalist.put(data_sort_value.key, data_sort_value);

        DataBox dataBox = iceInterfaceV3("VipSearchForWeb", datalist);
        return dataBox;
    }

    //会员筛选(Solr)
    public DataBox newStyleVipSearchForWeb(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value) throws Exception{
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_sort_key = new Data("sort_key", sort_key, ValueType.PARAM);
        Data data_sort_value = new Data("sort_value", sort_value, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_sort_key.key, data_sort_key);
        datalist.put(data_sort_value.key, data_sort_value);

        DataBox dataBox = iceInterfaceV3("NewStyleVipSearchForWeb", datalist);
        return dataBox;
    }


    public DataBox newStyleVipSearchForWebV2(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value) throws Exception{
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_sort_key = new Data("sort_key", sort_key, ValueType.PARAM);
        Data data_sort_value = new Data("sort_value", sort_value, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_sort_key.key, data_sort_key);
        datalist.put(data_sort_value.key, data_sort_value);

        DataBox dataBox = iceInterfaceV3("NewStyleVipSearchForWebV2", datalist);
        return dataBox;
    }

    //会员筛选导出(Solr)
    public DataBox vipScreen2ExeclMethod(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value,String cust_cols,String columnName,String showName,String user_code) throws Exception{
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_sort_key = new Data("sort_key", sort_key, ValueType.PARAM);
        Data data_sort_value = new Data("sort_value", sort_value, ValueType.PARAM);
        Data data_cust_cols = new Data("cust_cols", cust_cols, ValueType.PARAM);
        Data data_columnName = new Data("columnName", columnName, ValueType.PARAM);
        Data data_showName = new Data("showName", showName, ValueType.PARAM);
        Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_sort_key.key, data_sort_key);
        datalist.put(data_sort_value.key, data_sort_value);
        datalist.put(data_cust_cols.key, data_cust_cols);
        datalist.put(data_columnName.key, data_columnName);
        datalist.put(data_showName.key, data_showName);
        datalist.put(data_user_code.key, data_user_code);

        DataBox dataBox = iceInterfaceV3("VipSearchForOutput", datalist);
        return dataBox;
    }

    //新老vip消费占比
    public DataBox analysisVipCostDetail(String corp_code,String brand_code,String area_code,String store_code,String user_code,String type,String time) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_brand_code = new Data("brand_id", brand_code, ValueType.PARAM);
        Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);
        Data data_store_code = new Data("store_id", store_code, ValueType.PARAM);
        Data data_user_code= new Data("user_id", user_code, ValueType.PARAM);
        Data data_type = new Data("type", type, ValueType.PARAM);
        Data data_time = new Data("date_time", time, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_brand_code.key, data_brand_code);
        datalist.put(data_area_code.key, data_area_code);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_user_code.key, data_user_code);
        datalist.put(data_type.key, data_type);
        datalist.put(data_time.key, data_time);

        DataBox dataBox = iceInterfaceV3("AnalysisVipCostDetail", datalist);
        return dataBox;
    }

    //新增会员
    public DataBox addNewVip(String corp_code,String vip_id,String vip_name,String sex,String birthday,String phone,
                             String vip_card_type,String card_no,String store_code,String user_code,String streets) throws Exception{
        String province = "";
        String city = "";
        String area = "";
        String[] street = streets.split("/");
        if (street.length>=1){
            province = street[0];
        }
        if (street.length>=2){
            city = street[1];
        }
        if (street.length>=3){
            area = street[2];
        }

        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_vip_name = new Data("vip_name", vip_name, ValueType.PARAM);
        Data data_sex = new Data("sex", sex, ValueType.PARAM);
        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_birthday = new Data("birthday", birthday, ValueType.PARAM);
        Data data_vip_card_type = new Data("vip_card_type", vip_card_type, ValueType.PARAM);
        Data data_card_no = new Data("vip_card_no", card_no, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);
        Data data_province = new Data("province", province, ValueType.PARAM);
        Data data_city = new Data("city", city, ValueType.PARAM);
        Data data_area = new Data("area", area, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_vip_name.key, data_vip_name);
        datalist.put(data_sex.key, data_sex);
        datalist.put(data_phone.key, data_phone);
        datalist.put(data_birthday.key, data_birthday);
        datalist.put(data_vip_card_type.key, data_vip_card_type);
        datalist.put(data_card_no.key, data_card_no);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_user_code.key, data_user_code);
        datalist.put(data_province.key, data_province);
        datalist.put(data_city.key, data_city);
        datalist.put(data_area.key, data_area);

        DataBox dataBox = iceInterfaceV3("AddNewVip", datalist);
        return dataBox;
    }

    //保存会员拓展信息
    public DataBox saveVipExtendInfo(String corp_code,String vip_id,String card_no, String custom) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_card_no = new Data("card_no", card_no, ValueType.PARAM);
        Data data_custom = new Data("custom", custom, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_card_no.key, data_card_no);
        datalist.put(data_custom.key, data_custom);

        DataBox dataBox = iceInterfaceV3("AddSearchField", datalist);

        String cust_cols = "";
        JSONArray array = JSON.parseArray(custom);
        for (int i = 0; i < array.size(); i++) {
            cust_cols = cust_cols + array.getJSONObject(i).getString("column") + ",";
        }
//        webService.anniverActiRetroative(corp_code,vip_id,cust_cols);

        return dataBox;
    }

    //获取会员信息（vip_id多个逗号分割）
    public DataBox getVipInfo(String corp_code,String vip_id) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_ids", vip_id, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);

        DataBox dataBox = iceInterfaceV2("AnalysisVipInfo", datalist);
        return dataBox;
    }

    //会员详细资料+扩展信息
    public DataBox getVipInfo(String corp_code,String vip_id,String cust_col,String month) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_cust = new Data("cust_col", cust_col, ValueType.PARAM);
        Data data_month = new Data("month", month, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_cust.key, data_cust);
        datalist.put(data_month.key, data_month);

        DataBox dataBox = iceInterfaceV2("AnalysisVipDetail", datalist);
        return dataBox;
    }

    //会员详细资料+扩展信息
    public DataBox getVipInfoByPhone(String corp_code,String phone,String cust_col,String month) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("phone", phone, ValueType.PARAM);
        Data data_cust = new Data("cust_col", cust_col, ValueType.PARAM);
        Data data_month = new Data("month", month, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_cust.key, data_cust);
        datalist.put(data_month.key, data_month);

        DataBox dataBox = iceInterfaceV2("NewAnalysisVipDetail", datalist);
        return dataBox;
    }

    //修改会员卡类型（升降级）
    public DataBox changeVipType(String corp_code,String vip_id,String card_type) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_card_type = new Data("card_type", card_type, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_card_type.key, data_card_type);

        DataBox dataBox = iceInterfaceV3("VipCardTypeUp", datalist);
        return dataBox;
    }


    //更改会员所属导购
    public DataBox vipAssort(String corp_code,String vip_id,String user_code,String store_code,String operator_id) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_user_id = new Data("user_id", user_code, ValueType.PARAM);
        Data data_store_code = new Data("store_id", store_code, ValueType.PARAM);
        Data data_operator_id = new Data("operator_id", operator_id, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_operator_id.key, data_operator_id);

        DataBox dataBox = iceInterfaceV2("VipAssort", datalist);
        return dataBox;
    }

    //获取会员档案，会员分组（智能，固定）图表数据
    public DataBox vipTagSearchForWeb(String corp_code,String vip_id,String vip_group_code,String year,String type_view,String vip_phone) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_group_id = new Data("group_id", vip_group_code, ValueType.PARAM);
        Data data_year = new Data("year", year, ValueType.PARAM);
        Data data_type_view=new Data("type_view",type_view,ValueType.PARAM);
        Data data_vip_phone=new Data("vip_phone",vip_phone,ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_group_id.key, data_group_id);
        datalist.put(data_year.key, data_year);
        datalist.put(data_type_view.key,data_type_view);
        datalist.put(data_vip_phone.key,data_vip_phone);

        DataBox dataBox = iceInterfaceV3("VipTagSearchForWeb", datalist);
        return dataBox;
    }

    //会员分析图表数据
    public DataBox storeSearchForWeb(String corp_code,String store_id,String store_label,String brand_code,String year) throws Exception {
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
        Data data_store_label = new Data("store_label", store_label, ValueType.PARAM);
        Data data_brand_code = new Data("brand_code", brand_code, ValueType.PARAM);
        Data data_year = new Data("year", year, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_id.key, data_store_id);
        datalist.put(data_store_label.key, data_store_label);
        datalist.put(data_brand_code.key, data_brand_code);
        datalist.put(data_year.key, data_year);

        DataBox dataBox = iceInterfaceV3("StoreSearchForWeb", datalist);
        return dataBox;
    }

    //获取会员分组（自定义）图表数据
    public DataBox vipCustomGroup(String corp_code,String screen,String style,String type_view) throws Exception{
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_style = new Data("style", style, ValueType.PARAM);
        Data data_type_view=new Data("type_view",type_view,ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_style.key, data_style);
        datalist.put(data_type_view.key, data_type_view);

        DataBox dataBox = iceInterfaceV3("VipCustomGroup", datalist);
        return dataBox;
    }

    //获取店铺下未分配会员数（store_code多个逗号分割）
    public DataBox getUnAssortVip(String corp_code,String store_code) throws Exception{
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_corp_code.key, data_corp_code);

        DataBox dataBox = iceInterfaceV3("GetUnAssortVip", datalist);
        return dataBox;
    }

    //获取会员收藏夹
    public DataBox favorites(String corp_code,String app_id,String open_id,String vip_card_no) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_app_id = new Data("app_id", app_id, ValueType.PARAM);
        Data data_open_id = new Data("open_id", open_id, ValueType.PARAM);
        Data data_card_no = new Data("vip_card_no", vip_card_no, ValueType.PARAM);
        Data data_type = new Data("type", "1", ValueType.PARAM);
        Data data_row_num = new Data("row_num", "", ValueType.PARAM);
        Data data_query_type = new Data("query_type", "hbase", ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_app_id.key, data_app_id);
        datalist.put(data_open_id.key, data_open_id);
        datalist.put(data_card_no.key, data_card_no);
        datalist.put(data_type.key, data_type);
        datalist.put(data_row_num.key, data_row_num);
        datalist.put(data_query_type.key, data_query_type);

        DataBox dataBox = iceInterfaceV3("Favorites", datalist);
        return dataBox;
    }

    //获取会员购物车
    public DataBox shoppingCart(String corp_code,String vip_id,String page_size,String page_num) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_page_now = new Data("page_now", page_num, ValueType.PARAM);
        Data data_type = new Data("query_type", "hbase", ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_page_now.key, data_page_now);
        datalist.put(data_type.key, data_type);

        DataBox dataBox = iceInterfaceV3("Shopping", datalist);
        return dataBox;
    }

    //获取会员优惠券
    public DataBox coupon(String corp_code,String vip_id,String app_id,String open_id,String phone,String type) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_app_id = new Data("app_id", app_id, ValueType.PARAM);
        Data data_open_id = new Data("open_id", open_id, ValueType.PARAM);
        Data data_card_no = new Data("vip_phone", phone, ValueType.PARAM);
        Data data_type = new Data("query_type", type, ValueType.PARAM);
        Data data_row_num = new Data("row_num", "", ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_app_id.key, data_app_id);
        datalist.put(data_open_id.key, data_open_id);
        datalist.put(data_card_no.key, data_card_no);
        datalist.put(data_type.key, data_type);
        datalist.put(data_row_num.key, data_row_num);
        datalist.put(data_vip_id.key, data_vip_id);

        DataBox dataBox = iceInterfaceV3("Coupon", datalist);
        return dataBox;
    }

    //获取券类型
    public String getCouponInfo(String corp_code,String app_id) throws Exception {
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_app_id = new Data("app_id", app_id, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_app_id.key, data_app_id);

        DataBox dataBox = iceInterfaceV3("GetCoupon", datalist);

        if (dataBox.status.toString().equals("SUCCESS")){
            return dataBox.data.get("message").value;
        }
        return Common.DATABEAN_CODE_ERROR;
    }


    //根据open_id获取会员信息
    public DataBox getVipByOpenId(String corp_code,String open_id,String cust_col) throws Exception {
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_open_id = new Data("open_id", open_id, ValueType.PARAM);
        Data data_cust_col = new Data("cust_col", cust_col, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_open_id.key, data_open_id);
        datalist.put(data_cust_col.key, data_cust_col);

        DataBox dataBox = iceInterfaceV2("GetVipByOpenId", datalist);

        return dataBox;
    }

    //Solr增删改查
    public DataBox operationBySolr(String type,String param) throws Exception {
        Data data_type = new Data("type", type, ValueType.PARAM);
        Data data_param = new Data("param", param, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_type.key, data_type);
        datalist.put(data_param.key, data_param);
        DataBox dataBox = iceInterfaceV3("OperationSolr", datalist);
        return dataBox;
    }


    //分组定义，自定义分组
    public DataBox VipCustomSearchForWeb(String page_num, String page_size,String corp_code,String screen,String screenvalue,String style,String cust_cols) throws Exception {
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_screen = new Data("screen", screenvalue, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_style = new Data("style", style, ValueType.PARAM);
        Data data_cust_cols = new Data("cust_cols", cust_cols, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_screen.key, data_screen);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_style.key, data_style);
        datalist.put(data_cust_cols.key,data_cust_cols);

        DataBox dataBox = iceInterfaceV3("VipCustomSearchForWeb", datalist);
        return dataBox;
    }

    //获取导购下会员的数量
    @Override
    public DataBox getAllVipByUser(String corp_code, String user_id,String store_code) throws Exception {
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_store_code.key,data_store_code);
        DataBox dataBox = iceInterfaceV3("GetAllVipByUser", datalist);
        return dataBox;
    }

    //获取Solr参数
    public  DataBox getSolrParam(String param,String corp_code,String type) throws  Exception{
        Data data_param = new Data("param", param, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_type = new Data("type", type, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_param.key, data_param);
        datalist.put(data_type.key, data_type);
        DataBox dataBox = iceInterfaceV3("GetSolrParam", datalist);
        return dataBox;
    }

    public  DataBox getEmpKpiReport(String corp_code,String store_name,String user_id,
                                    String user_name,String start_time,String end_time,String page_size,String page_num) throws  Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_store_name= new Data("store_name", store_name, ValueType.PARAM);
        Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
        Data data_user_name = new Data("user_name", user_name, ValueType.PARAM);
        Data data_start_time=new Data("start_time",start_time,ValueType.PARAM);
        Data data_end_time=new Data("end_time",end_time,ValueType.PARAM);
        Data data_page_size=new Data("page_size",page_size,ValueType.PARAM);
        Data data_page_num=new Data("page_num",page_num,ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_name.key, data_store_name);
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_user_name.key, data_user_name);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_page_num.key, data_page_num);
        DataBox dataBox = iceInterfaceV3("EmpKpiReport",datalist);
        return  dataBox;
    }

    public DataBox getStoreKpiReport(String corp_code,String store_code,String store_name,String store_group,String sell_area,String start_time,
                                     String end_time,String page_size,String page_num) throws  Exception{
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_store_code= new Data("store_code", store_code, ValueType.PARAM);
        Data data_store_name = new Data("store_name", store_name, ValueType.PARAM);
        Data data_store_group = new Data("store_group", store_group, ValueType.PARAM);
        Data data_sell_area = new Data("store_area", sell_area, ValueType.PARAM);
        Data data_start_time=new Data("start_time",start_time,ValueType.PARAM);
        Data data_end_time=new Data("end_time",end_time,ValueType.PARAM);
        Data data_page_size=new Data("page_size",page_size,ValueType.PARAM);
        Data data_page_num=new Data("page_num",page_num,ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_code.key,data_store_code);
        datalist.put(data_store_name.key,data_store_name);
        datalist.put(data_store_group.key, data_store_group);
        datalist.put(data_sell_area.key, data_sell_area);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_page_num.key, data_page_num);
        DataBox dataBox = iceInterfaceV3("StoreKpiReport",datalist);
        return  dataBox;

    }


    public DataBox getKpiReportSearch(String corp_code,String search_key,String page_size,String page_num,String type) throws  Exception{
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_search_key=new Data("search_key",search_key,ValueType.PARAM);
        Data data_page_size=new Data("page_size",page_size,ValueType.PARAM);
        Data data_page_num=new Data("page_num",page_num,ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_search_key.key, data_search_key);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_page_num.key, data_page_num);
        DataBox dataBox=null;
        if(type.equals("store")) {
            dataBox = iceInterfaceV3("StoreKpiSearch", datalist);
        }else{
            dataBox = iceInterfaceV3("EmpKpiSearchReport", datalist);
        }
        return  dataBox;
    }


    //====================================
    public DataBox getNewStoreKpiReport(String corp_code,String store_code,String start_time, String end_time,String page_size,String page_num,String type,String query_type) throws  Exception{
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_store_code= new Data("store_code", store_code, ValueType.PARAM);
        Data data_start_time=new Data("start_time",start_time,ValueType.PARAM);
        Data data_end_time=new Data("end_time",end_time,ValueType.PARAM);
        Data data_page_size=new Data("page_size",page_size,ValueType.PARAM);
        Data data_page_num=new Data("page_num",page_num,ValueType.PARAM);
        Data data_type=new Data("type",type,ValueType.PARAM);
        Data data_query_type=new Data("query_type",query_type,ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_code.key,data_store_code);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_type.key, data_type);
        datalist.put(data_query_type.key, data_query_type);

        DataBox dataBox = iceInterfaceV3("StoreKpiNewReport",datalist);
        return  dataBox;

    }

    public DataBox getNewEmpKpiReport(String corp_code,String user_code,String start_time, String end_time,String page_size,String page_num,String type,String query_type) throws  Exception{
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_store_code= new Data("user_code", user_code, ValueType.PARAM);
        Data data_start_time=new Data("start_time",start_time,ValueType.PARAM);
        Data data_end_time=new Data("end_time",end_time,ValueType.PARAM);
        Data data_page_size=new Data("page_size",page_size,ValueType.PARAM);
        Data data_page_num=new Data("page_num",page_num,ValueType.PARAM);
        Data data_type=new Data("type",type,ValueType.PARAM);
        Data data_query_type=new Data("query_type",query_type,ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_code.key,data_store_code);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_type.key, data_type);
        datalist.put(data_query_type.key,data_query_type);

        DataBox dataBox = iceInterfaceV3("EmpKpiNewReport",datalist);
        return  dataBox;

    }


    public DataBox StoreKpiSumReport(String corp_code,String time_type,String time_value,String store_id,String store_group) throws  Exception{
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_time_type= new Data("time_type", time_type, ValueType.PARAM);
        Data data_time_value= new Data("time_value", time_value, ValueType.PARAM);
        Data data_store_code= new Data("store_id", store_id, ValueType.PARAM);
        Data data_store_group= new Data("store_group", store_group, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_time_type.key, data_time_type);
        datalist.put(data_time_value.key, data_time_value);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_store_group.key, data_store_group);
        DataBox dataBox = iceInterfaceV3("StoreKpiSum",datalist);
        return  dataBox;

    }

    public DataBox EmpKpiSumReport(String corp_code,String time_type,String time_value,String user_id,String store_code) throws  Exception{
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_time_type= new Data("time_type", time_type, ValueType.PARAM);
        Data data_time_value= new Data("time_value", time_value, ValueType.PARAM);
        Data data_user_code= new Data("user_id", user_id, ValueType.PARAM);
        Data data_store_code= new Data("store_code", store_code, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_time_type.key, data_time_type);
        datalist.put(data_time_value.key, data_time_value);
        datalist.put(data_user_code.key, data_user_code);
        datalist.put(data_store_code.key, data_store_code);
        DataBox dataBox = iceInterfaceV3("EmpKpiSum",datalist);
        return  dataBox;

    }

    public DataBox AreaKpiSumReport(String corp_code,String time_type,String time_value,String area_code) throws  Exception{
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_time_type= new Data("time_type", time_type, ValueType.PARAM);
        Data data_time_value= new Data("time_value", time_value, ValueType.PARAM);
        Data data_area_code= new Data("area_code", area_code, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_time_type.key, data_time_type);
        datalist.put(data_time_value.key, data_time_value);
        datalist.put(data_area_code.key, data_area_code);
        DataBox dataBox = iceInterfaceV3("AreaKpiSum",datalist);
        return  dataBox;

    }

    public DataBox getNewAreaKpiReport(String corp_code,String area_code,String start_time, String end_time,String page_size,String page_num,String type,String query_type) throws  Exception{
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_area_code= new Data("area_code", area_code, ValueType.PARAM);
        Data data_start_time=new Data("start_time",start_time,ValueType.PARAM);
        Data data_end_time=new Data("end_time",end_time,ValueType.PARAM);
        Data data_page_size=new Data("page_size",page_size,ValueType.PARAM);
        Data data_page_num=new Data("page_num",page_num,ValueType.PARAM);
        Data data_type=new Data("type",type,ValueType.PARAM);
        Data data_query_type=new Data("query_type",query_type,ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_area_code.key,data_area_code);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_type.key, data_type);
        datalist.put(data_query_type.key,data_query_type);

        DataBox dataBox = iceInterfaceV3("AreaKpiNewReport",datalist);
        return  dataBox;

    }

    @Override
    public DataBox getAllDataByBoard(String corp_code,String type,String brand_code,String query_time) throws Exception {
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_type= new Data("type", type, ValueType.PARAM);
        Data data_brand_code= new Data("brand_code", brand_code, ValueType.PARAM);
        Data data_query_time= new Data("query_time", query_time, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_type.key, data_type);
        datalist.put(data_brand_code.key, data_brand_code);
        datalist.put(data_query_time.key,data_query_time);
        DataBox dataBox = iceInterfaceV3("BoardData",datalist);
        return  dataBox;
    }

    @Override
    public DataBox getVipAnalyByBoard(String corp_code, String start_time,String end_time,String type,String query_type,String brand_code,String source) throws Exception {
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_start_time= new Data("start_time", start_time, ValueType.PARAM);
        Data data_end_time=new Data("end_time",end_time,ValueType.PARAM);
        Data data_type= new Data("type", type, ValueType.PARAM);
        Data data_query_type= new Data("query_type", query_type, ValueType.PARAM);
        Data data_brand_code= new Data("brand_code", brand_code, ValueType.PARAM);
        Data data_source= new Data("source", source, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);
        datalist.put(data_query_type.key, data_query_type);
        datalist.put(data_brand_code.key, data_brand_code);
        datalist.put(data_source.key, data_source);
        datalist.put(data_type.key, data_type);

        DataBox dataBox = iceInterfaceV3("BoardVipAnaly",datalist);
        return  dataBox;
    }


    public DataBox storeKpiView(String corp_code,String type,String time_type,String time_value,String view,String screen) throws  Exception{
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_type = new Data("type", type, ValueType.PARAM);
        Data data_time_type= new Data("time_type", time_type, ValueType.PARAM);
        Data data_time_value= new Data("time_value", time_value, ValueType.PARAM);
        Data data_view=new Data("view",view,ValueType.PARAM);
        Data data_screen=new Data("screen",screen,ValueType.PARAM);
//        Data data_query_type= new Data("query_type", query_type, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_type.key, data_type);
        datalist.put(data_time_type.key, data_time_type);
        datalist.put(data_time_value.key, data_time_value);
        datalist.put(data_view.key, data_view);
        datalist.put(data_screen.key, data_screen);
//        datalist.put(data_query_type.key, data_query_type);

        DataBox dataBox = iceInterfaceV3("StoreKpiView",datalist);
        return  dataBox;

    }

    //获取会员拓展信息
    public DataBox getVipExtendInfo(String corp_code,String vip_id,String cust_cols) throws Exception {
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_open_id = new Data("vip_ids", vip_id, ValueType.PARAM);
        Data data_cust_cols = new Data("cust_cols", cust_cols, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_open_id.key, data_open_id);
        datalist.put(data_cust_cols.key, data_cust_cols);

        DataBox dataBox = iceInterfaceV3("GetVipExtendInfo", datalist);

        return dataBox;
    }

    public DataBox ACHVBrandDashBoard(String corp_code,String time_type,String time_value,String query_type,String brand_code,String source) throws Exception {
        Data data_corp_code= new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_time_type= new Data("time_type", time_type, ValueType.PARAM);
        Data data_time_value=new Data("time_value",time_value,ValueType.PARAM);
        Data data_query_type= new Data("query_type", query_type, ValueType.PARAM);
        Data data_brand_code= new Data("brand_code", brand_code, ValueType.PARAM);
        Data data_source= new Data("source", source, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_time_type.key, data_time_type);
        datalist.put(data_time_value.key, data_time_value);
        datalist.put(data_query_type.key, data_query_type);
        datalist.put(data_brand_code.key, data_brand_code);
        datalist.put(data_source.key, data_source);

        DataBox dataBox = iceInterface("ACHVBrandDashBoard",datalist);
        return  dataBox;
    }

    public DataBox getAllCardForVip(String corp_code,String vip_phone,String type) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_phone = new Data("vip_phone", vip_phone, ValueType.PARAM);
        Data data_type = new Data("type", type, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_phone.key, data_vip_phone);
        datalist.put(data_type.key, data_type);
        DataBox dataBox = iceInterfaceV3("VipInfoForCard", datalist);
        return dataBox;
    }

    public DataBox manageKpiReport(String corp_code,String brand_code,String vip_source,String achv_source,String start_time,String end_time,String query_type) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_brand_code = new Data("brand_code", brand_code, ValueType.PARAM);
        Data data_vip_source = new Data("vip_source", vip_source, ValueType.PARAM);
        Data data_achv_source = new Data("achv_source", achv_source, ValueType.PARAM);
        Data data_start_time = new Data("start_time", start_time, ValueType.PARAM);
        Data data_end_time = new Data("end_time", end_time, ValueType.PARAM);
        Data data_query_type = new Data("query_type", query_type, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_brand_code.key, data_brand_code);
        datalist.put(data_vip_source.key, data_vip_source);
        datalist.put(data_achv_source.key, data_achv_source);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);
        datalist.put(data_query_type.key, data_query_type);

        DataBox dataBox = iceInterfaceV3("ManageKpiReport", datalist);
        return dataBox;
    }

    @Override
    public void sendCouponNotice(String corp_code, String ticket_code_ishop, String coupon_title, String phone, String users, String user_code) throws Exception {
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_task_code = new Data("ticket_code_ishop", ticket_code_ishop, ValueType.PARAM);
        Data data_task_title = new Data("send_title", coupon_title, ValueType.PARAM);
        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_user = new Data("user", users, ValueType.PARAM);
        Data data_user_id = new Data("user_id", user_code, ValueType.PARAM);


        Map datalist = new HashMap<String, Data>();
        datalist.put(data_phone.key, data_phone);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_task_code.key, data_task_code);
        datalist.put(data_task_title.key, data_task_title);
        datalist.put(data_user.key, data_user);
        datalist.put(data_user_id.key, data_user_id);

        DataBox dataBox = iceInterface("CouponNotice", datalist);
    }

    //会员筛选(Solr)以人为单位
    public DataBox vipScreenForMobile(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value) throws Exception{
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_sort_key = new Data("sort_key", sort_key, ValueType.PARAM);
        Data data_sort_value = new Data("sort_value", sort_value, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_sort_key.key, data_sort_key);
        datalist.put(data_sort_value.key, data_sort_value);
        DataBox dataBox = iceInterfaceV3("NewStyleMobileSearchForWeb", datalist);
        return dataBox;
    }


    //会员档案筛选导出(Solr以人为基础)
    public DataBox MobileScreen2ExeclMethod(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value,String cust_cols) throws Exception{
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_sort_key = new Data("sort_key", sort_key, ValueType.PARAM);
        Data data_sort_value = new Data("sort_value", sort_value, ValueType.PARAM);
        Data data_cust_cols = new Data("cust_cols", cust_cols, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_sort_key.key, data_sort_key);
        datalist.put(data_sort_value.key, data_sort_value);
        datalist.put(data_cust_cols.key, data_cust_cols);

        DataBox dataBox = iceInterfaceV3("MobileSearchForOutput", datalist);
        return dataBox;
    }

    public  DataBox getVipFromCardBySolr(String card_param,String role_param,String corp_code)throws Exception{
        Data data_card_param = new Data("card_param", card_param, ValueType.PARAM);
        Data data_role_param= new Data("role_param", role_param, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_card_param.key, data_card_param);
        datalist.put(data_role_param.key, data_role_param);
        datalist.put(data_corp_code.key, data_corp_code);
        DataBox dataBox = iceInterfaceV3("GetVipFromCardBySolr", datalist);
        return dataBox;
    }


    public  DataBox vipSearchByHbase(String corp_code,String vip_id)throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id= new Data("vip_id", vip_id, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        DataBox dataBox = iceInterfaceV3("VipSearchByHbase", datalist);
        return dataBox;
    }

//    =======================================================================


    //批量发短信
    public DataBox batchSendSMS(String corp_code,String activity_code,String content,String vip_condition,String vip_custom_condition,String send_type,String sms_code,String user_code,String auth_appid,String vip_condition_new) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_content = new Data("content", content, ValueType.PARAM);
        Data data_activity_code = new Data("activity_code", activity_code, ValueType.PARAM);
        Data data_vip_condition = new Data("vip_condition", vip_condition, ValueType.PARAM);
        Data data_vip_custom_condition = new Data("vip_custom_condition", vip_custom_condition, ValueType.PARAM);
        Data data_send_type = new Data("send_type", send_type, ValueType.PARAM);
        Data data_sms_code = new Data("sms_code", sms_code, ValueType.PARAM);
        Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);
        Data data_auth_appid = new Data("auth_appid", auth_appid, ValueType.PARAM);
        Data data_template_id = new Data("vip_condition_new", vip_condition_new, ValueType.PARAM);
        Data data_platform = new Data("platform", "CRM", ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_content.key, data_content);
        datalist.put(data_activity_code.key, data_activity_code);
        datalist.put(data_vip_condition.key, data_vip_condition);
        datalist.put(data_vip_custom_condition.key, data_vip_custom_condition);
        datalist.put(data_send_type.key, data_send_type);
        datalist.put(data_sms_code.key, data_sms_code);
        datalist.put(data_user_code.key, data_user_code);
        datalist.put(data_auth_appid.key, data_auth_appid);
        datalist.put(data_template_id.key, data_template_id);
        datalist.put(data_platform.key, data_platform);

        DataBox dataBox = new DataBox();
        System.out.println("====================SendBatchSMS==================");
        if (corp_code.equals("C20000"))
            dataBox = iceInterfaceV3("SendBatchSMS", datalist);
        return dataBox;
    }

    //企业通道发送短信
    public DataBox sendSmsV2(String corp_code,String text,String phone,String perform_user_code,String remark) throws Exception{
        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_text = new Data("message_content", text, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data record_message = new Data("record_message", "", ValueType.PARAM);
        Data data_user_id = new Data("user_id", perform_user_code, ValueType.PARAM);
        Data data_platform = new Data("platform", "CRM", ValueType.PARAM);
        Data data_remark = new Data("remark", remark, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_phone.key, data_phone);
        datalist.put(data_text.key, data_text);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(record_message.key, record_message);
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_platform.key, data_platform);
        datalist.put(data_remark.key, data_remark);
        DataBox dataBox = new DataBox();
//        if (phone.equals("13260718923") || phone.equals("18652083705"))
        if (corp_code.equals("C20000"))
             dataBox =  iceInterfaceV2("SendSMS", datalist);
        return dataBox;
    }

    //企业通道发送短信
    public DataBox sendSmsV3(String corp_code,String text,String phone,String perform_user_code,String remark,String vip_id) throws Exception{
        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_text = new Data("message_content", text, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data record_message = new Data("record_message", "", ValueType.PARAM);
        Data data_user_id = new Data("user_id", perform_user_code, ValueType.PARAM);
        Data data_platform = new Data("platform", "CRM", ValueType.PARAM);
        Data data_remark = new Data("remark", remark, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_phone.key, data_phone);
        datalist.put(data_text.key, data_text);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(record_message.key, record_message);
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_platform.key, data_platform);
        datalist.put(data_remark.key, data_remark);
        datalist.put(data_vip_id.key, data_vip_id);

        DataBox dataBox = new DataBox();
//        if (phone.equals("13260718923") || phone.equals("18652083705"))
        if (corp_code.equals("C20000"))
            dataBox =  iceInterfaceV3("SendSMSCRM", datalist);
        return dataBox;
    }

    //发送通知（任务/活动）
    public void sendNotice(String corp_code,String task_code,String task_title,String phone,String users,String user_code,String activity_code) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_task_code = new Data("task_code", task_code, ValueType.PARAM);
        Data data_task_title = new Data("coupon_title", task_title, ValueType.PARAM);
        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_user = new Data("user", users, ValueType.PARAM);
        Data data_user_id = new Data("user_id", user_code, ValueType.PARAM);
        Data data_activity_vip_code = new Data("activity_vip_code", activity_code, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_phone.key, data_phone);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_task_code.key, data_task_code);
        datalist.put(data_task_title.key, data_task_title);
        datalist.put(data_user.key, data_user);
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_activity_vip_code.key, data_activity_vip_code);

//        DataBox dataBox = iceInterface("TaskNotice", datalist);
    }



}
