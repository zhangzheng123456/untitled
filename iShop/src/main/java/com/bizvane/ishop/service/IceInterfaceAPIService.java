package com.bizvane.ishop.service;


import com.bizvane.sun.v1.common.DataBox;

import java.util.Map;

/**
 * Created by zhou on 2016/7/6.
 *
 * @@version
 */
public interface IceInterfaceAPIService {

    DataBox iceInterfaceAPI(String method ,Map datalist) throws Exception;

    DataBox getVipPointsList(String corp_code, String vip_id, String vip_phone, String vip_card_no, String page_num, String page_size) throws Exception;

    DataBox vipTransferStore(String corp_code,String vip_id,String store_code, String operator_id) throws Exception;

    DataBox addNewVip(String corp_code,String vip_id,String vip_name,String sex,String birthday,String phone,
                      String vip_card_type,String card_no,String store_code,String user_code,String streets) throws Exception;

    DataBox activityAnalyCoupon(String corp_code,String type_code,String batch_no,String start_time,String end_time,String store_id,String page_num,String page_size,String coupon_list_js,String chart) throws  Exception;

    DataBox activityAnalySalesRate(String type, String store_id, String type_code, String batch_no,String start_time, String end_time,String corp_code,String activity_code,String run_mode, String page_now,String page_size,String screen,String chart) throws Exception;

    DataBox vipProfileBackup(String corp_code,String vip_id,String new_vip_card_no,String phone,String vip_name,String birthday,String gender,String fr_active,
                             String province,String city,String area,String address,String user_code,String store_code,String operator_id,String vip_type_id) throws Exception;

    DataBox vipAssort(String corp_code,String vip_id,String user_code,String store_code,String operator_id) throws Exception;

    DataBox lastAchvRate(String corp_code,String activity_code,String start_time,String end_time,String type,String chart) throws Exception;

    DataBox vipContribution(String corp_code,String activity_code,String chart,String start_time,String end_time) throws Exception;

    DataBox vipPointsClean(String corp_code,String target_vips,String integral_duration,String bill_no,String operate_type,
                           String template_id,String app_id,String clean_time,String clean_date) throws Exception;

    DataBox getSku(String corp_code,String page_num,String page_size,String screen,String type) throws Exception;

    DataBox VipDetail(String corp_code,String vip_id,String vip_card_no, String vip_phone,String start_time,String end_time) throws Exception;

    DataBox sendCoupons(String corp_code,String vip_id,String coupon_code,String coupon_name,String app_id, String open_id,String description,String activity_code,String batch_no,String uid) throws Exception;

    DataBox sendPoints(String corp_code,String vip_id, String points,String uid) throws Exception;

    DataBox getSkuAnalysis(String corp_code,String page_num,String page_size,String screen,String store_code,String time_type,String time_value) throws Exception;

    DataBox getSkuSalesDetail(String corp_code,String page_num,String page_size,String time_type,String time_value,
                              String sku_id,String store_code,String query_type,String row_key) throws Exception;

    DataBox getVipInfoByCard(String corp_code, String vip_card_no,String vip_phone) throws Exception;

    DataBox VipActivityCop(String corp_code,String activity_code, String activity_id,String target_vip,String time_type,String present_point,String coupon_type,String store_id) throws Exception;

    DataBox DisposeActivityData(String corp_code,String vip_id, String activity_code,String run_mode) throws Exception;

    DataBox DisposeTaskData(String corp_code,String vip_id, String task_code) throws Exception;

    DataBox couponSearch(String corp_code,String page_num,String page_size,String screen,String search_value) throws Exception;

    DataBox getVipTaskInfo(String corp_code,String file_path, String app_id,String task_code,String type) throws Exception;

    DataBox adjustVipPoints(String bill_code,String corp_code) throws Exception;

    DataBox batchSendCoupons(String corp_code,String app_id,String activity_code,String vip_condition,String coupon_type,String point) throws Exception;

    DataBox activityAnniversaryAct(String corp_code,String run_time_type) throws Exception;

    DataBox getVipByCardInfo(String card_param,String corp_code) throws Exception;

}
