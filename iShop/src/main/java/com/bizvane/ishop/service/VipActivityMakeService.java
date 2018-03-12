package com.bizvane.ishop.service;

/**
 * Created by PC on 2017/1/12.
 */
public interface VipActivityMakeService {
    int addOrUpdateTask(String message,String user_code)throws Exception;

    String addOrUpdateSend(String message, String user_code,String group_code,String role_code,String brand_code,String area_code,String store_code)throws Exception;

    String addStrategyByTask(String message, String user_code) throws Exception;

    String addStrategyBySend(String message, String user_code,String group_code,String role_code,String brand_code,String area_code,String store_code) throws Exception ;

    int addOrUpdateVip(String screen_value,String target_vips_count,String corp_code,String activity_vip_code)throws Exception;

}
