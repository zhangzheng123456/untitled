package com.bizvane.ishop.service;


import com.alibaba.fastjson.JSONObject;
import com.bizvane.sun.v1.common.DataBox;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by zhou on 2016/7/6.
 *
 * @@version
 */
public interface IceInterfaceService {
    DataBox iceInterface(String method , Map datalist) throws Exception;

    DataBox iceInterfaceV2(String method ,Map datalist) throws Exception;

    DataBox iceInterfaceV3(String method ,Map datalist) throws Exception;

    Map vipBasicMethod2(String page_num, String page_size, String corp_code, HttpServletRequest request,String sort_key,String sort_value) throws Exception;

    Map vipAnalysisBasicMethod(JSONObject jsonObject, HttpServletRequest request) throws Exception;

    DataBox vipScreenMethod(String page_num,String page_size,String corp_code,String area_code,String brand_code,String store_code,String user_code) throws Exception;

    DataBox vipScreenMethod2(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value) throws Exception;

    DataBox newStyleVipSearchForWeb(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value) throws Exception;

    DataBox vipScreen2ExeclMethod(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value,String cust_cols,String columnName,String showName,String user_code) throws Exception;

    DataBox analysisVipCostDetail(String corp_code,String brand_code,String area_code,String store_code,String user_code,String type,String time) throws Exception;

    DataBox addNewVip(String corp_code,String vip_id,String vip_name,String sex,String birthday,String phone,
                      String vip_card_type,String card_no,String store_code,String user_code,String streets) throws Exception;

    DataBox saveVipExtendInfo(String corp_code,String vip_id,String card_no,String custom) throws Exception;

    DataBox getVipInfo(String corp_code,String vip_id) throws Exception;

    DataBox getVipInfo(String corp_code,String vip_id,String cust_col,String month) throws Exception;

    DataBox getVipInfoByPhone(String corp_code,String phone,String cust_col,String month) throws Exception;

    DataBox changeVipType(String corp_code,String vip_id,String card_type) throws Exception;

    DataBox vipAssort(String corp_code,String vip_id,String user_code,String store_code,String operator_id) throws Exception;

    DataBox vipTagSearchForWeb(String corp_code,String vip_id,String vip_group_code,String year,String type_view,String vip_phone) throws Exception;

    DataBox vipCustomGroup(String corp_code,String screen,String style,String type_view) throws Exception;

    DataBox getUnAssortVip(String corp_code,String store_code) throws Exception;

    DataBox favorites(String corp_code,String app_id,String open_id,String vip_card_no) throws Exception;

    DataBox shoppingCart(String corp_code,String vip_id,String page_size,String page_num) throws Exception;

    DataBox coupon(String corp_code,String vip_id,String app_id,String open_id,String phone,String type) throws Exception;

    String getCouponInfo(String corp_code,String app_id) throws Exception;

    DataBox batchSendSMS(String corp_code,String activity_code,String content,String vip_condition,String vip_custom_condition,String send_type,String sms_code,String user_code,String auth_appid,String vip_condition_new) throws Exception;

    DataBox getVipByOpenId(String corp_code,String open_id,String cust_col) throws Exception;

    DataBox storeSearchForWeb(String corp_code,String store_id,String store_label,String brand_code,String year) throws Exception;

    DataBox operationBySolr(String type,String param) throws Exception;

    DataBox sendSmsV2(String corp_code,String text,String phone,String perform_user_code,String remark) throws Exception;

    DataBox sendSmsV3(String corp_code,String text,String phone,String perform_user_code,String remark,String vip_id) throws Exception;

    void sendNotice(String corp_code,String task_code,String task_title,String phone,String users,String user_code,String activity_code) throws Exception;


    void sendCouponNotice(String corp_code,String ticket_code_ishop,String coupon_title,String phone,String users,String user_code) throws Exception;

    DataBox VipCustomSearchForWeb(String page_num, String page_size,String corp_code,String screen,String screenvalue,String style,String cust_cols) throws Exception;

    DataBox getAllVipByUser(String corp_code,String user_id,String store_code)throws  Exception;

    DataBox getSolrParam(String param,String corp_code,String type) throws  Exception;

    DataBox getEmpKpiReport(String corp_code,String store_name,String user_id, String user_name,String start_time,String end_time,String page_size,String page_num) throws  Exception;

    DataBox getStoreKpiReport(String corp_code,String store_code,String store_name,String store_group,String sell_area,String start_time, String end_time,String page_size,String page_num) throws  Exception;

    DataBox getKpiReportSearch(String corp_code,String search_key,String page_size,String page_num,String type) throws  Exception;

    DataBox getNewStoreKpiReport(String corp_code,String store_code,String start_time, String end_time,String page_size,String page_num,String type,String query_type) throws  Exception;

    DataBox StoreKpiSumReport(String corp_code,String time_type,String time_value,String store_id,String store_group) throws  Exception;

    DataBox storeKpiView(String corp_code,String type,String time_type,String time_value,String view,String screen) throws  Exception;

    DataBox getVipExtendInfo(String corp_code,String vip_id,String cust_cols) throws Exception;

    DataBox getNewEmpKpiReport(String corp_code,String user_code,String start_time, String end_time,String page_size,String page_num,String type,String query_type) throws  Exception;

    DataBox EmpKpiSumReport(String corp_code,String time_type,String time_value,String user_id,String store_code) throws  Exception;

    DataBox AreaKpiSumReport(String corp_code,String time_type,String time_value,String area_code) throws  Exception;

    DataBox getNewAreaKpiReport(String corp_code,String area_code,String start_time, String end_time,String page_size,String page_num,String type,String query_type) throws  Exception;

    DataBox  getAllDataByBoard(String corp_code,String type,String brand_code,String query_time) throws Exception;

    DataBox getVipAnalyByBoard(String corp_code,String start_time,String end_time,String type,String query_type,String brand_code,String source) throws Exception;

    DataBox ACHVBrandDashBoard(String corp_code,String time_type,String time_value,String query_type,String brand_code,String source)throws Exception;

    DataBox getAllCardForVip(String corp_code,String vip_phone,String type) throws Exception;

    DataBox manageKpiReport(String corp_code,String brand_code,String vip_source,String achv_source,String start_time,String end_time,String query_type) throws Exception;

    DataBox vipScreenForMobile(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value) throws Exception;

    DataBox MobileScreen2ExeclMethod(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value,String cust_cols) throws Exception;

    DataBox getVipFromCardBySolr(String card_param,String role_param,String corp_code)throws Exception;

    DataBox vipSearchByHbase(String corp_code,String vip_id)throws Exception;

    DataBox newStyleVipSearchForWebV2(String page_num,String page_size,String corp_code,String screen,String sort_key,String sort_value) throws Exception;
}
