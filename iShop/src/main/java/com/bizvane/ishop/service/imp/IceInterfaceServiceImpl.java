package com.bizvane.ishop.service.imp;


import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Store;
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

    //会员列表
    public Map vipBasicMethod(JSONObject jsonObject, HttpServletRequest request) throws Exception{
        String user_code = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String page_num = jsonObject.get("pageNumber").toString();
        String page_size = jsonObject.get("pageSize").toString();

        String user_id = "";
        String area_code = "";
        String store_id = "";
        if (role_code.equals(Common.ROLE_SYS)) {
            role_code = Common.ROLE_SM;
            corp_code = jsonObject.get("corp_code").toString();
            List<Store> storeList = storeService.getCorpStore(corp_code);
            for (int i = 0; i < storeList.size(); i++) {
                store_id = store_id + storeList.get(i).getStore_code() + ",";
            }
        } else if (role_code.equals(Common.ROLE_GM)){
            role_code = Common.ROLE_SM;
            List<Store> storeList = storeService.getCorpStore(corp_code);
            for (int i = 0; i < storeList.size(); i++) {
                store_id = store_id + storeList.get(i).getStore_code() + ",";
            }
            System.out.println("-------GM拉店铺--------------"+storeList.size());
        } else if (role_code.equals(Common.ROLE_AM)){
            role_code = Common.ROLE_SM;
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code1 = request.getSession().getAttribute("area_code").toString();
            String area_store_code = request.getSession().getAttribute("store_code").toString();
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
            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,"",brand_code,"","");
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


        Map datalist = new HashMap<String, Data>();
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_id.key, data_store_id);
        datalist.put(data_area_code.key, data_area_code);
        datalist.put(data_role_code.key, data_role_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);

        return datalist;
    }

    //会员列表
    public Map vipBasicMethod1(String page_num,String page_size,String corp_code, HttpServletRequest request) throws Exception{
        String user_code = request.getSession().getAttribute("user_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String user_id = "";
        String area_code = "";
        String store_id = "";
        if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) ){
            role_code = Common.ROLE_SM;
            List<Store> storeList = storeService.getCorpStore(corp_code);
            for (int i = 0; i < storeList.size(); i++) {
                store_id = store_id + storeList.get(i).getStore_code() + ",";
            }
        } else if (role_code.equals(Common.ROLE_AM)){
            role_code = Common.ROLE_SM;
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code1 = request.getSession().getAttribute("area_code").toString();
            String area_store_code = request.getSession().getAttribute("store_code").toString();
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
            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,"",brand_code,"","");
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


        Map datalist = new HashMap<String, Data>();
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_id.key, data_store_id);
        datalist.put(data_area_code.key, data_area_code);
        datalist.put(data_role_code.key, data_role_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);

        return datalist;
    }

    //会员分析
    public Map vipAnalysisBasicMethod(JSONObject jsonObject, HttpServletRequest request) throws Exception{
        String user_code = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String page_num = jsonObject.get("pageNumber").toString();
        String page_size = jsonObject.get("pageSize").toString();
        String vip_group_code = "";
        if (jsonObject.containsKey("vip_group_code")){
            vip_group_code = jsonObject.get("vip_group_code").toString();
        }
        String user_id = user_code;
        String area_code = "";
        String store_id = "";
        if (role_code.equals(Common.ROLE_SYS)) {
            corp_code = jsonObject.get("corp_code").toString();
            store_id = jsonObject.get("store_code").toString().trim();
            if (store_id.equals("")) {
                area_code = jsonObject.get("area_code").toString().trim();
                String brand_code = jsonObject.get("brand_code").toString().trim();
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "","");
                for (int i = 0; i < storeList.size(); i++) {
                    store_id = store_id + storeList.get(i).getStore_code() + ",";
                }
            }
        }else if (role_code.equals(Common.ROLE_AM)){
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
//        } else if (role_code.equals(Common.ROLE_GM)){
//            store_id = jsonObject.get("store_code").toString();
//            if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").toString().trim().equals("")){
//                area_code = jsonObject.get("area_code").toString();
//            }
//        } else if (role_code.equals(Common.ROLE_AM) ){
//            if (jsonObject.containsKey("area_code") && !jsonObject.get("area_code").toString().trim().equals("")){
//                area_code = jsonObject.get("area_code").toString();
//            }else {
//                area_code = request.getSession().getAttribute("area_code").toString().replace(Common.SPECIAL_HEAD,"");
//                String[] area_codes = area_code.split(",");
//                area_code = area_codes[0];
//            }
//            if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").toString().trim().equals("")){
//                store_id = jsonObject.get("store_code").toString();
//            }
//        } else if (role_code.equals(Common.ROLE_SM)){
//            if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").toString().trim().equals("")){
//                store_id = jsonObject.get("store_code").toString();
//            }else {
//                String store_code = request.getSession().getAttribute("store_code").toString().replace(Common.SPECIAL_HEAD, "");
//                String[] store_codes = store_code.split(",");
//                store_id = store_codes[0];
//            }
//        } else if (role_code.equals(Common.ROLE_STAFF)){
//            user_id = user_code;
//            store_id = request.getSession().getAttribute("store_code").toString();
//            store_id = store_id.replace(Common.SPECIAL_HEAD,"");
//            if (jsonObject.containsKey("store_code") && !jsonObject.get("store_code").toString().trim().equals("")){
//                store_id = jsonObject.get("store_code").toString();
//            }
//        }else if (role_code.equals(Common.ROLE_BM)){
//            role_code = Common.ROLE_SM;
//            String brand_code = request.getSession().getAttribute("brand_code").toString();
//            brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
//            List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,"",brand_code,"");
//            for (int i = 0; i < stores.size(); i++) {
//                store_id = store_id + stores.get(i).getStore_code() + ",";
//            }
//        }

        Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
        Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
        Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_vip_group_code = new Data("vip_group_code", vip_group_code, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_id.key, data_store_id);
        datalist.put(data_area_code.key, data_area_code);
        datalist.put(data_role_code.key, data_role_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_vip_group_code.key, data_vip_group_code);

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
    public DataBox vipScreenMethod2(String page_num,String page_size,String corp_code,String screen) throws Exception{
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);

        DataBox dataBox = iceInterfaceV3("VipSearchForWeb", datalist);
        return dataBox;
    }


    //会员筛选
    public DataBox vipScreen2ExeclMethod(String page_num,String page_size,String corp_code,String area_code,String brand_code,String store_code,String user_code,String output_message) throws Exception{
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

            Data data_output_message = new Data("message", output_message, ValueType.PARAM);
            datalist.put(data_output_message.key, data_output_message);
            Data data_output_type = new Data("output_type", "screen", ValueType.PARAM);
            datalist.put(data_output_type.key, data_output_type);
            dataBox = iceInterfaceV2("AnalysisVipExportExecl", datalist);
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

            Data data_output_type = new Data("output_type", "screen", ValueType.PARAM);
            datalist.put(data_output_type.key, data_output_type);
            Data data_output_message = new Data("message", output_message, ValueType.PARAM);
            datalist.put(data_output_message.key, data_output_message);
            dataBox = iceInterfaceV2("AnalysisVipExportExecl", datalist);
        }
        return dataBox;
    }

    //新增会员
    public DataBox addNewVip(String corp_code,String vip_id,String vip_name,String sex,String birthday,String phone,
                             String vip_card_type,String card_no,String store_code,String user_code) throws Exception{

        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_vip_name = new Data("vip_name", vip_name, ValueType.PARAM);
        Data data_sex = new Data("sex", sex, ValueType.PARAM);
        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_birthday = new Data("birthday", birthday, ValueType.PARAM);
        Data data_vip_card_type = new Data("vip_card_type", vip_card_type, ValueType.PARAM);
        Data data_card_no = new Data("card_no", card_no, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);

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

        DataBox dataBox = iceInterfaceV3("AddNewVip", datalist);
        return dataBox;
    }

    public DataBox saveVipExtendInfo(String corp_code,String vip_id,String custom) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_custom = new Data("custom", custom, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_custom.key, data_custom);

        DataBox dataBox = iceInterfaceV3("AddSearchField", datalist);
        return dataBox;
    }

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
}
