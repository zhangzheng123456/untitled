package com.bizvane.ishop.service;

/**
 * Created by PC on 2017/1/12.
 */
public interface VipActivityMakeService {
    int addOrUpdateTask(String message,String user_code)throws Exception;

    int addOrUpdateSend(String message,String user_code)throws Exception;

     int addStrategyByTask(String message, String user_code) throws Exception;

    String addStrategyBySend(String message, String user_code) throws Exception ;

    int addOrUpdateVip(String screen_value,String target_vips_count,String corp_code,String activity_vip_code)throws Exception;

}
